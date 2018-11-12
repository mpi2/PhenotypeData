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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.ImpressUtils;
import org.mousephenotype.impress.wsdlclients.ParameterMPTermsClient;
import org.mousephenotype.impress.wsdlclients.ParameterOntologyOptionsClient;
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
public class ImpressParserV2 implements CommandLineRunner {

    private DataSource         cdabaseDataSource;
    private CdaSqlUtils        cdabaseSqlUtils;
    private ImpressUtils       impressUtils;
    private ApplicationContext context;
    private Datasource         datasource;
    private Logger             logger         = LoggerFactory.getLogger(this.getClass());
    private Integer            mpDbId;
    private Set<String>        normalCategory = new HashSet<>();
    private Set<String>        omitPipelines  = new HashSet<>();

    private Map<String, OntologyTerm> updatedOntologyTerms;                                                             // This is the map of all ontology terms, updated, indexed by ontology accession id

    private final String IMPRESS_SHORT_NAME = "IMPReSS";

//    // SOAP web service classes
    private ParameterMPTermsClient         parameterMPTermsClient;
    private ParameterOntologyOptionsClient parameterOntologyOptionsClient;

    // RESTful web service classes
    private Map<Integer, Schedule>  schedulesById  = new HashMap<>();
    private Map<Integer, Procedure> proceduresById = new HashMap<>();
    private Map<Integer, Parameter> parametersById = new HashMap<>();
    private Map<Integer, String>    unitsById      = new HashMap<>();

    // Sets for missing terms for display at the end of the run.
    private Set<String> missingOntologyTerms = new HashSet<>();
    private Set<String> missingMpTerms       = new HashSet<>();


    private final String[] OPT_HELP = {"h", "help"};
    private final String[] OPT_OMIT_PIPELINES = {"o", "omitPipelines"};

    private final String OPT_OMIT_PIPELINES_DESCRIPTION =
            "By default, no pipelines are omitted. Specify this parameter once for every pipeline you want to skip. The" +
            " pipeline value you supply is an approximate 'starts-with' match, so you only need to specify the starting" +
            " characters that uniquely identify the pipeline(s) you want to omit. You may specify this parameter multiple times.";
    private boolean help    = false;

    public static final String USAGE = "Usage: [--help/-h] | [--omitPipelines/-o p1] [--omitPipelines/-o p2] [...]";


    @Inject
    @Lazy
    public ImpressParserV2(
            ApplicationContext context,
            CdaSqlUtils cdabaseSqlUtils,
            ImpressUtils impressUtils,
            DataSource cdabaseDataSource,
            ParameterMPTermsClient parameterMPTermsClient,
            ParameterOntologyOptionsClient parameterOntologyOptionsClient
    ) {
        this.context = context;
        this.cdabaseSqlUtils = cdabaseSqlUtils;
        this.impressUtils = impressUtils;
        this.cdabaseDataSource = cdabaseDataSource;
        this.parameterMPTermsClient = parameterMPTermsClient;
        this.parameterOntologyOptionsClient = parameterOntologyOptionsClient;
    }

    /**
     * This class is intended to be a command-line callable java main program that creates and populates the impress
     * tables using the impress 2 rest web service.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ImpressParserV2.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        initialise(args);

        // LOAD UNITS
        unitsById = impressUtils.getUnits();


        // LOAD PIPELINES
        List<Pipeline> pipelines = impressUtils.getPipelines(datasource);

        for (Pipeline pipeline : pipelines) {

            // Skip any pipelines starting with the -o/--omitPipelines flag value(s) (multiple -o flags permitted)
            if (shouldSkip(pipeline)) {
                logger.info("Skipping omitted pipelineId {} ({})", pipeline.getStableKey(), pipeline.getStableId());
                continue;
            }

            logger.info("INSERTing pipelineId {} ({})", pipeline.getStableKey(), pipeline.getStableId());

            if (cdabaseSqlUtils.insertPhenotypePipeline(pipeline) == null) {
                logger.warn("INSERT OF pipeline {} ({}) FAILED. PIPELINE SKIPPED...", pipeline.getStableKey(), pipeline.getStableId());
                continue;
            }


            // LOAD SCHEDULES
            for (Integer scheduleId : pipeline.getScheduleCollection()) {

                Schedule schedule = schedulesById.get(scheduleId);

                if (schedule == null) {
                    schedule = impressUtils.getSchedule(scheduleId, datasource);
                    schedulesById.put(scheduleId, schedule);

                    logger.info("  INSERTing scheduleId {}", scheduleId);
                }


                // LOAD PROCEDURES
                for (Integer procedureId : schedule.getProcedureCollection()) {

                    Procedure procedure = proceduresById.get(procedureId);

                    if (procedure == null) {

                        procedure = impressUtils.getProcedure(procedureId, datasource);
                        if (procedure == null) {
                            logger.warn("Unable to get procedureId {}. Skipping...", procedureId);
                            continue;
                        }

                        // Add SCHEDULE components to PROCEDURE
                        procedure.setStageLabel(schedule.getTimeLabel());
                        procedure.setStage(schedule.getStage());

                        proceduresById.put(procedureId, procedure);

                        logger.info("    Loading procedureId {}", procedureId);

                        if (cdabaseSqlUtils.insertPhenotypeProcedure(pipeline.getId(), procedure) == null) {
                            logger.warn("INSERT OF procedureId {} ({}) FAILED. PROCEDURE SKIPPED...", procedure.getStableKey(), procedure.getStableId());
                            continue;
                        }
                    }


                    // LOAD PARAMETERS
                    for (Integer parameterId : procedure.getParameterCollection()) {

                        logger.debug("      Loading pipelineId::scheduleId::procedureId::parameterId   {}::{}::{}::{}", pipeline.getStableKey(), schedule.getScheduleId(), procedure.getStableKey(), parameterId);
                        Parameter parameter = parametersById.get(parameterId);

                        if (parameter == null) {

                            parameter = impressUtils.getParameter(parameterId, datasource, unitsById);
                            if (parameter == null) {
                                logger.warn("Unable to get parameterId {}. Skipping...", parameterId);
                                continue;
                            }
                            parametersById.put(parameterId, parameter);

                            logger.debug("INSERTing parameterId {} ({})", parameter.getStableKey(), parameter.getStableId());

                            parameter = insertParameter(parameter, procedure, pipeline.getStableId());
                            if (parameter == null) {
                                logger.warn("INSERT OF parameterId {} ({}) FAILED. PARAMETER SKIPPED...", parameter.getStableKey(), parameter.getStableId());
                                continue;
                            }
                        }

                        cdabaseSqlUtils.insertPhenotypeProcedureParameter(procedure.getId(), parameter.getId());        // INSERT into the phenotype_procedure_parameter lookup table
                    }

                    cdabaseSqlUtils.insertPhenotypePipelineProcedure(pipeline.getId(), procedure.getId());              // INSERT into the phenotype_pipeline_procedure lookup table
                }
            }
        }

        // Print out missing sets
        Collections.sort(new ArrayList<>(missingOntologyTerms));
        for (String term : missingOntologyTerms) {
            logger.warn(term);
        }

        System.out.println();

        Collections.sort(new ArrayList<>(missingMpTerms));
        for (String term : missingOntologyTerms) {
            logger.warn(term);
        }

    }


    // PRIVATE METHODS


    private void initialise(String[] args) throws IOException, SQLException {

        // Parse and load command-line parameters
        OptionParser parser  = new OptionParser();
        OptionSet    options = parseOptions(parser, args);

        logger.info("Program Arguments: " + StringUtils.join(args, ", "));

        if (help) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }

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


    protected OptionSet parseOptions(OptionParser parser, String[] args) {

        OptionSet options = null;
        OptionSpec<String> omitSpec = null;

        parser.allowsUnrecognizedOptions();

        parser.acceptsAll(Arrays.asList(OPT_HELP), "Display help/usage information\t" + USAGE)
                .forHelp();

        omitSpec = parser.acceptsAll(Arrays.asList(OPT_OMIT_PIPELINES), OPT_OMIT_PIPELINES_DESCRIPTION)
                .withRequiredArg()
                .withValuesSeparatedBy(",")
                .forHelp();

        try {

            options = parser.parse(args);

        } catch (Exception e) {

            System.out.println(e.getLocalizedMessage());
            System.out.println(usage());
            System.exit(1);
        }

        help = (options.has("help"));
        if (options.has("o")) {
            omitPipelines.addAll(options.valuesOf(omitSpec));
        }

        return options;
    }

    private String usage() {
        return USAGE;
    }

    private List<ParameterOntologyAnnotationWithSex> getPhenotypeParameterOntologyAssociations(String pipelineKey, String procedureKey, Parameter parameter) {

        List<ParameterOntologyAnnotationWithSex> annotations = new ArrayList<>();

        // Get the map of ontology terms from the IMPReSS web service. A null return value indicates an error.
        List<Map<String, String>> ontologyTermsFromWs = impressUtils.getOntologyTermsFromWs(parameterOntologyOptionsClient, parameter);
        if (ontologyTermsFromWs == null) {
            missingOntologyTerms.add("parameterOntologyOptionsClient(): Missing ontology term for pipelineKey::procedureKey::parameterKey(parameterId) " + pipelineKey + "::" + procedureKey + "::" + parameter.getStableId() + "::" + parameter.getStableKey());
            return annotations;
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
            OntologyTerm ontologyTerm = updatedOntologyTerms.get(ontologyAcc);                                          // Get the updated ontology term if it exists.
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

        // Create the map of MP ontology terms from the IMPReSS web service.
        List<Map<String, String>> mpOntologyTermsFromWs = impressUtils.getMpOntologyTermsFromWs(parameterMPTermsClient, parameter);
        if (mpOntologyTermsFromWs == null) {
            missingMpTerms.add("parameterMPTermsClient(): Missing MP ontology term for pipelineKey::procedureKey::parameterKey(parameterId) " + pipelineKey + "::" + procedureKey + "::" + parameter.getStableId() + "::" + parameter.getStableKey());
            return annotations;
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

            List<ParameterIncrement> increments = impressUtils.getIncrements(parameter.getStableKey());
            cdabaseSqlUtils.insertPhenotypeParameterIncrements(parameter.getId(), increments);
        }

        // OPTIONS
        if (parameter.isOptionsFlag()) {

            List<ParameterOption> options = impressUtils.getOptions(parameter, normalCategory);
            cdabaseSqlUtils.insertPhenotypeParameterOptions(parameter.getId(), options);
            parameter.setOptions(options);                                                                              // Set the list of options (with their primary keys) for use by the next step.
        }

        // ONTOLOGY ANNOTATIONS
        List<ParameterOntologyAnnotationWithSex> annotations =

                getPhenotypeParameterOntologyAssociations(pipelineKey, procedure.getStableId(), parameter);
        cdabaseSqlUtils.insertPhenotypeParameterOntologyAnnotations(parameter.getId(), annotations);

        return parameter;
    }


    private boolean shouldSkip(Pipeline pipeline) {
        String pipelineStableId = pipeline.getStableId();
        for (String omitPipeline : omitPipelines) {
            if (pipelineStableId.startsWith(omitPipeline)) {

                return true;
            }
        }

        return false;
    }
}