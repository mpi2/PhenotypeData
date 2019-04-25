/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.create.extract.cdabase;

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.ImpressUtils;
import org.mousephenotype.impress.GetParameterIncrementsResponse;
import org.mousephenotype.impress.GetParameterMPTermsResponse;
import org.mousephenotype.impress.GetParameterOptionsResponse;
import org.mousephenotype.impress.wsdlclients.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

/*
 *  Classes generated using this command line:
 *
 *  1. Create a temporary directory for the generated sources and targets and create the sources and targets directories:
 *     mkdir ~/impresswsdl
 *  2. cd to ~/impresswsdl
 *  3. mkdir sources
 *  4. mkdir targets
 *  5. Create a wsdlclients xml file describing the service:
 *     A. Paste the url of the web service into a browser:
 *         https://www.mousephenotype.org/impress/soap/server\?wsdlclients
 *     B. Save the page: File -> Save Page As -> ~/impresswsdl/impresswsdl.xml
 *  6. Generate the classes:
 *         wsimport -keep -s `pwd`/sources -d `pwd`/targets -verbose -encoding UTF-8 -extension -Xnocompile -p org.mousephenotype.impress -wsdllocation https://www.mousephenotype.org/impress/soap/server\?wsdlclients "file:/Users/mrelac/impresswsdl/impresswsdl.xml"
 *  7. Create package org.mousephenotype.impress
 *  8. Copy the generated sources to the org.mousephenotype.impress package
 *         cp -r * ~/workspace/PhenotypeData/loads/src/main/java/
 */

@ComponentScan
public class ImpressParser implements CommandLineRunner {

    private DataSource         cdabaseDataSource;
    private CdaSqlUtils        cdabaseSqlUtils;
    private ApplicationContext context;
    private Datasource         datasource;
    private Logger             logger         = LoggerFactory.getLogger(this.getClass());
    private Integer            mpDbId;
    private Set<String>        normalCategory = new HashSet<>();

    private Map<String, Parameter>    parametersByStableIdMap = new HashMap<>();                                        // This is the map of parameters indexed by parameter stableId
    private Map<String, Procedure>    proceduresByStableIdMap = new HashMap<>();                                        // This is the map of procedures indexed by procedure stableId
    private Map<String, OntologyTerm> updatedOntologyTerms;                                                             // This is the map of all ontology terms, updated, indexed by ontology accession id

    private final String IMPRESS_SHORT_NAME = "IMPReSS";

    // SOAP web service classes
    private ParameterMPTermsClient         parameterMPTermsClient;
    private ParameterIncrementsClient      parameterIncrementsClient;
    private ParameterOntologyOptionsClient parameterOntologyOptionsClient;
    private ParameterOptionsClient         parameterOptionsClient;
    private ParametersClient               parametersClient;
    private PipelineClient                 pipelineClient;
    private PipelineKeysClient             pipelineKeysClient;
    private ProcedureClient                procedureClient;
    private ProcedureKeysClient            procedureKeysClient;

    @Inject
    @Lazy
    public ImpressParser(
            ApplicationContext context,
            CdaSqlUtils cdabaseSqlUtils,
            DataSource cdabaseDataSource,
            ParameterIncrementsClient parameterIncrementsClient,
            ParameterMPTermsClient parameterMPTermsClient,
            ParameterOntologyOptionsClient parameterOntologyOptionsClient,
            ParameterOptionsClient parameterOptionsClient,
            ParametersClient parametersClient,
            PipelineClient pipelineClient,
            PipelineKeysClient pipelineKeysClient,
            ProcedureClient procedureClient,
            ProcedureKeysClient procedureKeysClient
    ) {
        this.context = context;
        this.cdabaseSqlUtils = cdabaseSqlUtils;
        this.cdabaseDataSource = cdabaseDataSource;
        this.parameterIncrementsClient = parameterIncrementsClient;
        this.parameterMPTermsClient = parameterMPTermsClient;
        this.parameterOntologyOptionsClient = parameterOntologyOptionsClient;
        this.parameterOptionsClient = parameterOptionsClient;
        this.parametersClient = parametersClient;
        this.pipelineClient = pipelineClient;
        this.pipelineKeysClient = pipelineKeysClient;
        this.procedureClient = procedureClient;
        this.procedureKeysClient = procedureKeysClient;
    }

    /**
     * This class is intended to be a command-line callable java main program that creates and populates the impress
     * tables using the impress SOAP web service.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ImpressParser.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {

        initialise();

        // LOAD PIPELINES
        List<String> pipelineKeys = pipelineKeysClient.getPipelineKeys().getGetPipelineKeysResult().getItem();

        for (String pipelineKey : pipelineKeys) {

            if (pipelineKey.startsWith("HAS_")) {
                // Do not load the Harwell Ageing Screen pipeline (per Terry 2016-08-18)
                logger.info("Skipping pipeline {} (the Harwell Ageing Screen pipeline)", pipelineKey);
                continue;
            }

            logger.info("Loading pipeline {}", pipelineKey);

            Pipeline pipeline = null;

            try {
                pipeline = getPipeline(pipelineKey);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("INSERT OF pipeline {} FAILED:", pipelineKey);
                System.exit(1);
            }
            if (cdabaseSqlUtils.insertPhenotypePipeline(pipeline) == null) {
                logger.warn("INSERT OF pipeline " + pipelineKey + " failed. Pipeline skipped...");
                continue;
            }

            // LOAD PROCEDURES

            /*
             * If procedure DOES NOT EXIST in procedure map
             *    INSERT procedure from web service
             *    add to procedure map
             *    If parameter DOES NOT EXIST in parameter map
             *        INSERT parameter from web service
             *        add to parameter map
             *    INSERT phenotype_procedure_parameter
             *
             * INSERT phenotype_pipeline_procedure
             */

            List<String> procedureKeys = procedureKeysClient.getProcedureKeys(pipeline.getStableId()).getGetProcedureKeysResult().getItem();

            for (String procedureKey : procedureKeys) {

                logger.debug("  Loading procedure: {}", procedureKey);

                Procedure procedure = proceduresByStableIdMap.get(procedureKey);
                if (procedure == null) {
                    procedure = getProcedure(procedureKey, pipeline);                                                   // Get the procedure details from the web service
                    cdabaseSqlUtils.insertPhenotypeProcedure(pipeline.getId(), procedure);                              // INSERT the procedure
                    try {
                        proceduresByStableIdMap.put(procedure.getStableId(), procedure);                                    // Add the procedure to the map
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("INSERT OF pipeline::procedure {}::{} FAILED", pipelineKey, procedureKey);
                        System.exit(1);
                    }

                    // LOAD PARAMETERS
                    NodeList parameterNodesMap = ((Element) parametersClient.getParameters(procedureKey).getGetParametersResult()).getChildNodes();
                    for (int i = 0; i < parameterNodesMap.getLength(); i++) {
                        NodeList  parameterNodes = parameterNodesMap.item(i).getChildNodes();
                        Parameter parameter      = getParameter(parameterNodes, procedure);

                        logger.debug("    Loading parameter: {}", parameter.getStableId());

                        if ( ! parametersByStableIdMap.containsKey(parameter.getStableId())) {
                            try {
                                parameter = insertParameter(parameter, procedure, pipelineKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error("INSERT OF pipeline::procedure::parameter {}::{}::{} FAILED", pipelineKey, procedureKey, parameter.getStableId());
                                System.exit(1);
                            }
                            if (parameter == null) {
                                continue;                                                                               // If the INSERT failed, continue on to the next parameter
                            }
                            parametersByStableIdMap.put(parameter.getStableId(), parameter);
                        }

                        cdabaseSqlUtils.insertPhenotypeProcedureParameter(procedure.getId(), parameter.getId());        // INSERT into the phenotype_procedure_parameter lookup table
                    }
                }

                cdabaseSqlUtils.insertPhenotypePipelineProcedure(pipeline.getId(), procedure.getId());                  // INSERT into the phenotype_pipeline_procedure lookup table
            }
        }
    }


    // PRIVATE METHODS


    private void initialise() throws IOException, SQLException {

        datasource = new Datasource();
        datasource.setShortName(IMPRESS_SHORT_NAME);
        datasource.setId(cdabaseSqlUtils.getDbIdByShortName(IMPRESS_SHORT_NAME));

        mpDbId = cdabaseSqlUtils.getDbIdByShortName("MP");

        // Create impress tables
        String impressSchemaLocation = "scripts/impress_schema.sql";

        logger.info("[re]creating IMPReSS tables from : " + impressSchemaLocation);
        Resource r = context.getResource(impressSchemaLocation);
        ScriptUtils.executeSqlScript(cdabaseDataSource.getConnection(), r);

        // Load CategoryRemapping map
        Resource resource = context.getResource("impress/CategoryRemapping.tsv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] pieces = line.split("\t");
            if (pieces[2].equals("0")) {

                // Create sets of parameterStableId_NormalCategoryName
                normalCategory.add(pieces[0] + "_" + pieces[1]);
            }
        }

        // Load updated ontology terms
        List<OntologyTerm> originalTerms = cdabaseSqlUtils.getOntologyTerms();
        updatedOntologyTerms = cdabaseSqlUtils.getUpdatedOntologyTermMap(originalTerms, null, null);     // We're trying to update all terms. Ignore infos and warnings, as most don't apply to IMPReSS.
    }

    private List<ParameterIncrement> getIncrements(String parameterKey) {

        List<ParameterIncrement> parameterIncrements = new ArrayList<>();

        List<Map<String, String>> incrementsList = new ArrayList<>();


        // Create a map of increments from the IMPReSS web service.
        GetParameterIncrementsResponse response              = parameterIncrementsClient.getParameterIncrements(parameterKey);
        NodeList                       incrementNodeList = ((Element) response.getGetParameterIncrementsResult()).getChildNodes();

        // Parse out the keys and values for this parameter increment
        for (int i = 0; i < incrementNodeList.getLength(); i++) {
            NodeList incrementNodes = incrementNodeList.item(i).getChildNodes();

            Map<String, String> incrementChildNodeListMap = new HashMap<>();

            // Parse out the keys and values for this parameter
            for (int j = 0; j < incrementNodes.getLength(); j++) {
                NodeList incrementChildNodeList = incrementNodes.item(j).getChildNodes();
                incrementChildNodeListMap.put(incrementChildNodeList.item(0).getTextContent(), incrementChildNodeList.item(1).getTextContent());
            }

            incrementsList.add(incrementChildNodeListMap);
        }

        for (Map<String, String> map : incrementsList) {
            ParameterIncrement increment = new ParameterIncrement();
            increment.setDataType(map.get("type").toUpperCase());
            increment.setMinimum(map.get("min"));
            increment.setUnit(map.get("unit"));
            increment.setValue(map.get("string"));

            parameterIncrements.add(increment);
        }

        return parameterIncrements;
    }

    private List<ParameterOption> getOptions(String parameterKey) {

        List<ParameterOption> parameterOptions = new ArrayList<>();

        List<Map<String, String>> optionsList = new ArrayList<>();

        GetParameterOptionsResponse response = parameterOptionsClient.getParameterOptions(parameterKey);
        NodeList optionNodeList = ((Element) response.getGetParameterOptionsResult()).getChildNodes();

        for (int i = 0; i < optionNodeList.getLength(); i++) {
            NodeList optionNodes = optionNodeList.item(i).getChildNodes();

            Map<String, String> optionChildNodeListMap = new HashMap<>();

            // Parse out the keys and values for this parameter
            for (int j = 0; j < optionNodes.getLength(); j++) {
                NodeList optionChildNodeList = optionNodes.item(j).getChildNodes();
                optionChildNodeListMap.put(optionChildNodeList.item(0).getTextContent(), optionChildNodeList.item(1).getTextContent());
            }

            optionsList.add(optionChildNodeListMap);
        }

        for (Map<String, String> map : optionsList) {
            ParameterOption option = new ParameterOption();
            option.setName(map.get("name"));
            option.setDescription(map.get("description"));

            // This is the same format as the populate method, parameterStableId_NormalOption
            String candidate = parameterKey + "_" + option.getName();
            option.setNormalCategory(normalCategory.contains(candidate));

            parameterOptions.add(option);
        }

        return parameterOptions;
    }

    private Parameter getParameter(NodeList parameterNodes, Procedure procedure) {

        Parameter parameter;
        Map<String, String> map = new HashMap<>();

        // Parse out the keys and values for this parameter
        for (int j = 0; j < parameterNodes.getLength(); j++) {
            NodeList m = parameterNodes.item(j).getChildNodes();
            map.put(m.item(0).getTextContent(), m.item(1).getTextContent());
        }

        String parameterStableId = map.get("parameter_key");

        parameter = parametersByStableIdMap.get(parameterStableId);
        if (parameter == null) {
            parameter = new Parameter();
            parameter.setStableId(map.get("parameter_key"));
            parameter.setStableKey(Integer.parseInt(map.get("parameter_id")));
            parameter.setDatasource(datasource);
            parameter.setName(ImpressUtils.newlineToSpace(map.get("parameter_name")));
            parameter.setDescription(map.get("description"));
            parameter.setMajorVersion(Integer.parseInt(map.get("major_version")));
            parameter.setMinorVersion(Integer.parseInt(map.get("minor_version")));
            parameter.setType(map.get("type"));
            parameter.setRequiredFlag(Boolean.parseBoolean(map.get("is_required")));
            parameter.setImportantFlag(Boolean.parseBoolean(map.get("important")));
            parameter.setDataAnalysisNotes(map.get("data_analysis_notes"));
            parameter.setOptionsFlag(Boolean.parseBoolean(map.get("is_option")));
            parameter.setMetaDataFlag(map.get("type").equals("procedureMetadata"));
            parameter.setMediaFlag(Boolean.parseBoolean(map.get("is_media")));
            parameter.setIncrementFlag(Boolean.parseBoolean(map.get("is_increment")));
            parameter.setDerivedFlag(Boolean.parseBoolean(map.get("is_derived")));
            parameter.setAnnotateFlag(Boolean.parseBoolean(map.get("is_annotation")));
            if (parameter.getDerivedFlag()) {
                parameter.setFormula(map.get("derivation"));
            }

            parameter.setUnit(map.get("unit"));

            parameter.setDatatype(map.get("value_type"));
        }

        procedure.addParameter(parameter);

        return parameter;
    }

    private List<ParameterOntologyAnnotationWithSex> getPhenotypeParameterOntologyAssociations(String pipelineKey, String procedureKey, Parameter parameter) {

        List<ParameterOntologyAnnotationWithSex> annotations          = new ArrayList<>();

        // Get the map of ontology terms from the IMPReSS web service.
        List<Map<String, String>> ontologyTermsFromWs = new ArrayList<>();
        NodeList ontologyTermMap = ((Element) parameterOntologyOptionsClient.getParameterOntologyOptions(parameter.getStableId()).getGetParameterOntologyOptionsResult()).getChildNodes();

        for (int i = 0; i < ontologyTermMap.getLength(); i++) {
            NodeList ontologyTermNodes = ontologyTermMap.item(i).getChildNodes();

            Map<String, String> map = new HashMap<>();

            // Parse out the keys and values for this parameter
            for (int j = 0; j < ontologyTermNodes.getLength(); j++) {
                NodeList m = ontologyTermNodes.item(j).getChildNodes();
                map.put(m.item(0).getTextContent(), m.item(1).getTextContent());
            }

            ontologyTermsFromWs.add(map);
        }

        /*
         * Loop through this parameter's ontology terms, creating an ontologyAnnotation for each term. Add each
         * ontologyAnnotation to this parameter's annotations list.
         */
        for (Map<String, String> ontologyTermFromWs : ontologyTermsFromWs) {

            ParameterOntologyAnnotationWithSex ontologyAnnotation = new ParameterOntologyAnnotationWithSex();

            if (ontologyTermFromWs.get("sex") != null && ! ontologyTermFromWs.get("sex").isEmpty()) {                   // Get SexType
                SexType sexType = getSexType(ontologyTermFromWs.get("sex"), parameter.getStableId());
                ontologyAnnotation.setSex(sexType);
            }

            String       ontologyAcc  = ontologyTermFromWs.get("ontology_id");                                          // Extract the ontology term accession id from the web service
            String       ontologyName = ontologyTermFromWs.get("ontology_term");                                        // Extract the term name from the web service in case we need to create one.
            OntologyTerm ontologyTerm = updatedOntologyTerms.get(ontologyAcc);                                          // Get the updated ontology term
            if (ontologyTerm == null) {

                if ((ontologyAcc == null) || (ontologyAcc.trim().isEmpty())) {
                    logger.error("NO ONTOLOGY TERM WAS FOUND FOR pipeline::procedure::parameter {}::{}::{}, ontologyAcc '{}', ontologyName '{}'. Skipping parameter...",
                                 pipelineKey, procedureKey, parameter.getStableId(), ontologyAcc, ontologyName);
                    continue;
                }

                DatasourceEntityId dsId = new DatasourceEntityId();
                dsId.setAccession(ontologyAcc);
                dsId.setDatabaseId(mpDbId);

                ontologyTerm = new OntologyTerm();
                ontologyTerm.setId(dsId);
                ontologyTerm.setName(ontologyName == null || ontologyName.trim().isEmpty() ? ontologyAcc : ontologyName.trim());
                ontologyTerm.setDescription(ontologyTerm.getName());
                ontologyTerm.setIsObsolete(false);

                cdabaseSqlUtils.insertOntologyTerm(ontologyTerm);
                updatedOntologyTerms.put(ontologyTerm.getId().getAccession(), ontologyTerm);

                logger.warn("Created ontology term {}, name '{}' for pipeline::procedure::parameter {}::{}::{}", ontologyTerm.getId(), ontologyTerm.getName(), pipelineKey, procedureKey, parameter.getStableId());
            }

            ontologyAnnotation.setOntologyTerm(ontologyTerm);
            parameter.addAnnotation(ontologyAnnotation);
            annotations.add(ontologyAnnotation);
        }

        // Create a map of MP terms from the IMPReSS web service.
        GetParameterMPTermsResponse response = parameterMPTermsClient.getParameterMPTerms(parameter.getStableId());
        NodeList mpOntologyTermMap = ((Element) response.getGetParameterMPTermsResult()).getChildNodes();

        List<Map<String, String>> mpOntologyTermsFromWs = new ArrayList<>();

        for (int i = 0; i < mpOntologyTermMap.getLength(); i++) {
            NodeList mpOntologyTermNodes = mpOntologyTermMap.item(i).getChildNodes();

            Map<String, String> map = new HashMap<>();

            // Parse out the keys and values for this parameter
            for (int j = 0; j < mpOntologyTermNodes.getLength(); j++) {
                NodeList m = mpOntologyTermNodes.item(j).getChildNodes();
                map.put(m.item(0).getTextContent(), m.item(1).getTextContent());
            }

            mpOntologyTermsFromWs.add(map);
        }

        /*
         * Loop through this parameter's mp ontology terms, creating an mpOntologyAnnotation for each mp term. Add each
         * mpOntologyAnnotation to this parameter's annotations list. If no ontology term exists, create one using the
         * ontologyAcc and ontologyName.
         */
        for (Map<String, String> mpOntologyTermFromWs : mpOntologyTermsFromWs) {

            ParameterOntologyAnnotationWithSex mpOntologyAnnotation = new ParameterOntologyAnnotationWithSex();

            String outcome = mpOntologyTermFromWs.get("selection_outcome");
            PhenotypeAnnotationType phenotypeAnnotationType = ((outcome == null) || (outcome.trim().isEmpty()) ? null : PhenotypeAnnotationType.find(outcome));
            mpOntologyAnnotation.setType(phenotypeAnnotationType);

            if (mpOntologyTermFromWs.get("sex") != null && ! mpOntologyTermFromWs.get("sex").isEmpty()) {               // Get SexType
                SexType sexType = getSexType(mpOntologyTermFromWs.get("sex"), parameter.getStableId());
                mpOntologyAnnotation.setSex(sexType);
            }

            String       mpOntologyAcc  = mpOntologyTermFromWs.get("mp_id");                                            // Extract the mp term accession id from the web service
            String       mpOntologyName = mpOntologyTermFromWs.get("mp_term");                                          // Extract the mp term name from the web service in case we need to create one.
            OntologyTerm mpOntologyTerm = updatedOntologyTerms.get(mpOntologyAcc);                                      // Try to get the updated ontology term. Create and add it to the database and to the updatedOntologyTerms list if it is missing.
            if (mpOntologyTerm == null) {

                if ((mpOntologyAcc == null) || (mpOntologyAcc.trim().isEmpty())) {
                    logger.error("NO MP ONTOLOGY TERM WAS FOUND FOR pipeline::procedure::parameter {}::{}::{}, mpOntologyAcc '{}', mpOntologyName '{}'. Skipping parameter...",
                                 pipelineKey, procedureKey, parameter.getStableId(), mpOntologyAcc, mpOntologyName);
                    continue;
                }

                DatasourceEntityId dsId = new DatasourceEntityId();
                dsId.setAccession(mpOntologyAcc);
                dsId.setDatabaseId(mpDbId);

                mpOntologyTerm = new OntologyTerm();
                mpOntologyTerm.setId(dsId);
                mpOntologyTerm.setName(mpOntologyName == null || mpOntologyName.trim().isEmpty() ? mpOntologyAcc : mpOntologyName.trim());
                mpOntologyTerm.setDescription(mpOntologyTerm.getName());
                mpOntologyTerm.setIsObsolete(false);

                cdabaseSqlUtils.insertOntologyTerm(mpOntologyTerm);
                updatedOntologyTerms.put(mpOntologyTerm.getId().getAccession(), mpOntologyTerm);

                logger.warn("Created MP ontology term {}, name '{}' for pipeline::procedure::parameter {}::{}::{}", mpOntologyTerm.getId(), mpOntologyTerm.getName(), pipelineKey, procedureKey, parameter.getStableId());
            }

            if (parameter.isOptionsFlag()) {                                                                            // Add mp options as specified by the web service
                if (mpOntologyTermFromWs.get("option").length() > 0) {
                    String optionName = mpOntologyTermFromWs.get("option");

                    // Look up the option in this parameter's options list so we can extract the phenotype_parameter_option primary key.
                    for (ParameterOption parameterOption : parameter.getOptions()) {
                        if (parameterOption.getName().equals(optionName)) {
                            parameterOption.setNormalCategory(true);
                            mpOntologyAnnotation.setOption(parameterOption);
                            logger.debug("Associate " + outcome + " to option " + parameterOption.getName() + " to parameter " + parameter.getStableId() + " with MP Ontology term '" + mpOntologyTermFromWs.get("mp_term") + "'. ");
                            break;
                        }
                    }
                } else {
                    logger.debug("Associate " + outcome + " to parameter " + parameter.getStableId() + " with MP Ontology term '" + mpOntologyTermFromWs.get("mp_term") + "'.");
                }
            }

            mpOntologyAnnotation.setOntologyTerm(mpOntologyTerm);
            parameter.addAnnotation(mpOntologyAnnotation);
            annotations.add(mpOntologyAnnotation);
        }

        return annotations;
    }

    private Pipeline getPipeline(String pipelineKey) {

        NodeList pipelineNodes = ((Element) pipelineClient.getPipeline(pipelineKey).getGetPipelineResult()).getChildNodes();

        Map<String, String> map = new HashMap<>();
        // Parse out the keys and values for this pipeline
        for (int j = 0; j < pipelineNodes.getLength(); j++) {
            NodeList m = pipelineNodes.item(j).getChildNodes();
            map.put(m.item(0).getTextContent(), m.item(1).getTextContent());
        }

        logger.debug("Parsed pipeline map: {}", map);

        Pipeline pipeline = new Pipeline();
        pipeline.setStableId(map.get("pipeline_key"));
        pipeline.setStableKey(Integer.valueOf(map.get("pipeline_id")));
        pipeline.setDatasource(datasource);
        pipeline.setName(ImpressUtils.newlineToSpace(map.get("pipeline_name")));
        pipeline.setDescription(map.get("description"));
        pipeline.setMajorVersion(Integer.parseInt(map.get("major_version")));
        pipeline.setMinorVersion(Integer.parseInt(map.get("minor_version")));

        return pipeline;
    }

    private Procedure getProcedure(String procedureKey, Pipeline pipeline) {

        Procedure procedure;
        Map<String, String> map = new HashMap<>();

        NodeList procedureNodes = ((Element) procedureClient.getProcedure(procedureKey, pipeline.getStableId()).getGetProcedureResult()).getChildNodes();

        // Parse out the keys and values for this procedure
        for (int j = 0; j < procedureNodes.getLength(); j++) {
            NodeList m = procedureNodes.item(j).getChildNodes();
            map.put(m.item(0).getTextContent(), m.item(1).getTextContent());
        }

        String procedureStableId = map.get("procedure_key");

        procedure = proceduresByStableIdMap.get(procedureStableId);
        if (procedure == null) {
            procedure = new Procedure();
            procedure.setStableId(procedureStableId);
            procedure.setStableKey(Integer.parseInt(map.get("procedure_id")));
            procedure.setDatasource(datasource);
            procedure.setName(ImpressUtils.newlineToSpace(map.get("procedure_name")));
            procedure.setDescription(map.get("description"));
            procedure.setStage(map.get("stage"));
            procedure.setStageLabel(map.get("stage_label"));
            procedure.setLevel(map.get("level"));
            procedure.setMajorVersion(Integer.parseInt(map.get("major_version")));
            procedure.setMinorVersion(Integer.parseInt(map.get("minor_version")));
            procedure.setMandatory(Boolean.parseBoolean(map.get("is_mandatory")));

            procedure.addPipeline(pipeline);
        }

        pipeline.addProcedure(procedure);

        return procedure;
    }

    private SexType getSexType(String sex, String parameterKey) {

        // Default value for sexType is null
        SexType sexType = null;

        if (sex.equals("M")) {
            sexType = SexType.male;
        } else if (sex.equals("F")) {
            sexType = SexType.female;
        }

        logger.debug("Got sex field {} for parameter {}. Converted to {}", sex, parameterKey, sexType.getName());

        return sexType;
    }

    /**
     * Inserts the specified {@link Parameter} and any related increments, options, and ontology annotations
     * @param parameter the {@link Parameter} instance to be inserted
     * @return the same {@link Parameter} instance, with primary keys set, if successful; null otherwise
     */
    private Parameter insertParameter(Parameter parameter, Procedure procedure, String pipelineKey) {
        Integer phenotypeParameterPk = cdabaseSqlUtils.insertPhenotypeParameter(parameter);
        if (phenotypeParameterPk == null) {
            logger.warn("INSERT OF pipeline::procedure::parameter " + pipelineKey + "::" + procedure.getStableId() + "::" + phenotypeParameterPk + " failed. Parameter skipped...");
            return null;
        }

        // INCREMENTS
        if (parameter.isIncrementFlag()) {
            List<ParameterIncrement> increments = getIncrements(parameter.getStableId());
            cdabaseSqlUtils.insertPhenotypeParameterIncrements(parameter.getId(), increments);
        }

        // OPTIONS
        if (parameter.isOptionsFlag()) {
            List<ParameterOption> options = getOptions(parameter.getStableId());
            cdabaseSqlUtils.insertPhenotypeParameterOptions(parameter.getId(), options);
            parameter.setOptions(options);                                                                              // Set the list of options (with their primary keys) for use by the next step.
        }

        // ONTOLOGY ANNOTATIONS
        List<ParameterOntologyAnnotationWithSex> annotations = getPhenotypeParameterOntologyAssociations(pipelineKey, procedure.getStableId(), parameter);
        cdabaseSqlUtils.insertPhenotypeParameterOntologyAnnotations(parameter.getId(), annotations);

        return parameter;
    }
}