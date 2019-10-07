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

package org.mousephenotype.cda.loads.create.extract.impressbase;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.ImpressLoadUtils;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.impress2.ImpressParamMpterm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

/*
 * Impress Version 2
 *
 * Data read from the IMPRESS URL. See: https://api.mousephenotype.org/impress
 *
 */

@ComponentScan
@Component
public class ImpressParser implements CommandLineRunner {

    private DataSource         cdabaseDataSource;
    private CdaSqlUtils        cdabaseSqlUtils;
    private ImpressLoadUtils   impressLoadUtils;
    private ApplicationContext context;
    private Datasource         datasource;
    private Logger             logger         = LoggerFactory.getLogger(this.getClass());
    private Long               mpDbId;
    private Set<String>        normalCategory = new HashSet<>();
    private Set<String>        omitPipelines  = new HashSet<>();

    // This is the map of all ontology terms, updated, indexed by original ontology accession id
    private Map<String, OntologyTerm> updatedOntologyTermsByOriginalOntologyAccessionId;

    private final String IMPRESS_SHORT_NAME = "IMPReSS";

    // RESTful web service classes
    private Map<Long, Schedule>     schedulesById                   = new HashMap<>();
    private Map<Long, Procedure>    proceduresById                  = new HashMap<>();
    private Map<Long, Parameter>    parametersById                  = new HashMap<>();
    private Map<Long, String>       unitsById                       = new HashMap<>();
    private Map<Long, OntologyTerm> updatedOntologyTermsByStableKey = new HashMap<>();

    // Sets for missing terms for display at the end of the run.
    private Set<String> missingOntologyTerms = new HashSet<>();
    private Set<String> missingMpTerms       = new HashSet<>();

    // To support the -c / --component flag, these hold the pipelineId, scheduleId, procedureId, and parameterId.
    // A value of 0 for any component  means process all of those components and their dependencies.
    private long pipelineIdComponent = 0;
    private long scheduleIdComponent = 0;
    private long procedureIdComponent = 0;
    private long parameterIdComponent = 0;

    private int associationCounter = 0;


    private final String[] OPT_HELP = {"h", "help"};
    private final String[] OPT_OMIT_PIPELINES = {"o", "omitPipelines"};
    private final String[] OPT_COMPONENT = {"c", "component"};

    private final String OPT_OMIT_PIPELINES_DESCRIPTION =
            "By default, no pipelines are omitted. Specify this parameter once for every pipeline you want to skip. The" +
            " pipeline value you supply is an approximate 'starts-with' match, so you only need to specify the starting" +
            " characters that uniquely identify the pipeline(s) you want to omit. You may specify this parameter multiple times.";
    private final String OPT_COMPONENT_DESCRIPTION =
            "By default, all pipelines, schedules, procedures, and parameters are processed. Use this option to execute" +
            " a single pipelineId [::scheduleId [::procedureId [::parameterId]]]. This parameter may be specified only once.";
    private boolean help    = false;

    public static final String USAGE = "Usage: [--help/-h] | [--component/-c pipelineId[::scheduleId[::procedureId[::parameterId]]]] | [--omitPipelines/-o p1] [--omitPipelines/-o p2] [...]";


    @Inject
    @Lazy
    public ImpressParser(
            ApplicationContext context,
            CdaSqlUtils cdabaseSqlUtils,
            ImpressLoadUtils impressLoadUtils,
            DataSource cdabaseDataSource
    ) {
        this.context = context;
        this.cdabaseSqlUtils = cdabaseSqlUtils;
        this.impressLoadUtils = impressLoadUtils;
        this.cdabaseDataSource = cdabaseDataSource;
    }

    /**
     * This class is intended to be a command-line callable java main program that creates and populates the impress
     * tables using the impress 2 rest web service.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplicationBuilder(ImpressParser.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .logStartupInfo(false).build();
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {

        initialise(args);

        // LOAD UNITS
        unitsById = impressLoadUtils.getUnits();

        // LOAD ONTOLOGY TERM MAP FROM WEB SERVICE
        Map<String, Long> ontologyTermStableKeysByAccFromWs = impressLoadUtils.getOntologyTermStableKeysByAccFromWs();

        // LOAD ontologyTermsByStableKey MAP WITH UPDATED TERMS
        for (Map.Entry<String, Long> entry : ontologyTermStableKeysByAccFromWs.entrySet()) {
            String acc = entry.getKey();
            Long stableKey = entry.getValue();
            OntologyTerm updatedTerm = updatedOntologyTermsByOriginalOntologyAccessionId.get(acc);
            updatedOntologyTermsByStableKey.put(stableKey, updatedTerm);
        }

        // LOAD PIPELINES and their child elements
        List<Pipeline> pipelines = impressLoadUtils.getPipelines(datasource);

        for (Pipeline pipeline : pipelines) {

            // Skip any pipelines starting with the -o/--omitPipelines flag value(s) (multiple -o flags permitted)
            if (shouldSkip(pipeline)) {
                logger.info("Skipping omitted pipelineId {} ({})", pipeline.getStableKey(), pipeline.getStableId());
                continue;
            }

            loadPipeline(pipeline);
        }

        if ( ! missingOntologyTerms.isEmpty()) {
            logger.info("Missing ontology terms:");
            Collections.sort(new ArrayList<>(missingOntologyTerms));
            for (String term : missingOntologyTerms) {
                logger.warn(term);
            }
        }

        if ( ! missingMpTerms.isEmpty()) {
            logger.info("Missing MP ontology terms:");
            Collections.sort(new ArrayList<>(missingMpTerms));
            for (String term : missingMpTerms) {
                logger.warn(term);
            }
        }

        Set<String> exceptions = impressLoadUtils.getExceptions();
        if ( ! exceptions.isEmpty()) {
            System.out.println(" ");
            logger.info("exceptions:");
            for (String exception : exceptions) {
                logger.warn(exception);
            }
        }
    }


    // PRIVATE METHODS


    @Transactional
    public void loadPipeline(Pipeline pipeline) {

        if ((pipelineIdComponent > 0) && (pipeline.getStableKey() != pipelineIdComponent)) {
            return;
        }

        System.out.println(" ");
        logger.info("***** Loading pipelineId {} ({}) *****", pipeline.getStableKey(), pipeline.getStableId());

        if (cdabaseSqlUtils.insertPhenotypePipeline(pipeline) == null) {
            logger.warn("INSERT OF pipeline {} ({}) FAILED. PIPELINE SKIPPED...", pipeline.getStableKey(), pipeline.getStableId());
            return;
        }


        // LOAD SCHEDULES
        for (Long scheduleId : pipeline.getScheduleCollection()) {

            if ((scheduleIdComponent > 0) && (scheduleId != scheduleIdComponent)) {
                continue;
            }

            Schedule schedule = schedulesById.get(scheduleId);

            if (schedule == null) {
                schedule = impressLoadUtils.getSchedule(pipeline.getStableKey(), scheduleId);
                schedulesById.put(scheduleId, schedule);

                logger.debug("      Loading pipelineId::scheduleId {}::{}", pipeline.getStableKey(), scheduleId);
            }


            // LOAD PROCEDURES
            for (Long procedureId : schedule.getProcedureCollection()) {

                if ((procedureIdComponent > 0) && (procedureId != procedureIdComponent)) {
                    continue;
                }

                Procedure procedure = proceduresById.get(procedureId);

                if (procedure == null) {

                    procedure = impressLoadUtils.getProcedure(pipeline.getStableKey(), scheduleId, procedureId, datasource);
                    if (procedure == null) {
                        logger.warn("Unable to get procedureId {}. Skipping...", procedureId);
                        continue;
                    }

                    // Add SCHEDULE components to PROCEDURE
                    procedure.setStageLabel(schedule.getTimeLabel());
                    procedure.setStage(schedule.getStage());

                    proceduresById.put(procedureId, procedure);

                    logger.info("    Loading pipelineId::scheduleId::procedureId::procedureKey {}::{}::{}::{}", pipeline.getStableKey(), scheduleId, procedureId, procedure.getStableId());

                    if (cdabaseSqlUtils.insertPhenotypeProcedure(pipeline.getId(), procedure) == null) {
                        logger.warn("INSERT OF procedureId {} ({}) FAILED. PROCEDURE SKIPPED...", procedure.getStableKey(), procedure.getStableId());
                        continue;
                    }
                }


                // LOAD PARAMETERS
                for (Long parameterId : procedure.getParameterCollection()) {

                    if ((parameterIdComponent > 0) && (parameterId != parameterIdComponent)) {
                        continue;
                    }

                    logger.debug("      Loading pipelineId::scheduleId::procedureId::parameterId   {}::{}::{}::{}", pipeline.getStableKey(), schedule.getScheduleId(), procedure.getStableKey(), parameterId);
                    Parameter parameter = parametersById.get(parameterId);

                    if (parameter == null) {

                        parameter = impressLoadUtils.getParameter(pipeline.getStableKey(), scheduleId, procedureId, parameterId, datasource, unitsById);
                        if (parameter == null) {
                            logger.warn("Unable to get parameterId {}. Skipping...", parameterId);
                            continue;
                        }
                        parametersById.put(parameterId, parameter);

                        logger.debug("INSERTing parameterId {} ({})", parameter.getStableKey(), parameter.getStableId());

                        parameter = insertParameter(pipeline, parameter, procedure, pipeline.getStableId());
                        if (parameter == null) {
                            logger.warn("INSERT OF parameterId {} ({}) FAILED. PARAMETER SKIPPED...", parameter.getStableKey(), parameter.getStableId());
                            continue;
                        }
                    }

                    // INSERT into the phenotype_procedure_parameter lookup table
                    cdabaseSqlUtils.insertPhenotypeProcedureParameter(procedure.getId(), parameter.getId());
                }

                // INSERT into the phenotype_pipeline_procedure lookup table
                cdabaseSqlUtils.insertPhenotypePipelineProcedure(pipeline.getId(), procedure.getId());
            }
        }
    }


    private void initialise(String[] args) throws IOException, SQLException {

        // Parse and load command-line parameters
        OptionParser parser  = new OptionParser();
        OptionSet    options = parseOptions(parser, args);

        logger.info("Program Arguments: " + (args.length > 0 ? StringUtils.join(args, ", ") : "<none>"));

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

        logger.info("recreating IMPReSS tables from : " + impressSchemaLocation);
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
        updatedOntologyTermsByOriginalOntologyAccessionId = cdabaseSqlUtils.getUpdatedOntologyTermMap(originalTerms, null, null);     // We're trying to update all terms. Ignore infos and warnings, as most don't apply to IMPReSS.

        logger.info("impress.service.url = {}", impressLoadUtils.getImpressServiceUrl());
    }


    protected OptionSet parseOptions(OptionParser parser, String[] args) {

        OptionSet options = null;
        OptionSpec<String> omitSpec;
        OptionSpec<String> componentSpec;

        parser.allowsUnrecognizedOptions();

        parser.acceptsAll(Arrays.asList(OPT_HELP), "Display help/usage information\t" + USAGE)
                .forHelp();

        omitSpec = parser.acceptsAll(Arrays.asList(OPT_OMIT_PIPELINES), OPT_OMIT_PIPELINES_DESCRIPTION)
                .withRequiredArg()
                .withValuesSeparatedBy(",")
                .forHelp();

        componentSpec = parser.acceptsAll(Arrays.asList(OPT_COMPONENT), OPT_COMPONENT_DESCRIPTION)
                .withRequiredArg()
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

        if (options.has("c")) {
            String components = options.valueOf(componentSpec);
            parseComponents(components);
        }

        return options;
    }

    private void parseComponents(String components) {

        String[] parts = components.split("::");
        if (parts.length > 4) {
            System.out.println(usage());
            System.exit(1);
        }

        Integer i;
        switch (parts.length) {
            case 4:
                i = CommonUtils.tryParseInt(parts[3]);
                if (i == null) {
                    System.out.println(usage());
                    System.exit(1);
                }
                parameterIdComponent = i;

            case 3:
                i = CommonUtils.tryParseInt(parts[2]);
                if (i == null) {
                    System.out.println(usage());
                    System.exit(1);
                }
                procedureIdComponent = i;

            case 2:
                i = CommonUtils.tryParseInt(parts[1]);
                if (i == null) {
                    System.out.println(usage());
                    System.exit(1);
                }
                scheduleIdComponent = i;

            case 1:
                i = CommonUtils.tryParseInt(parts[0]);
                if (i == null) {
                    System.out.println(usage());
                    System.exit(1);
                }
                pipelineIdComponent = i;

        }
    }

    private String usage() {
        return USAGE;
    }

    private List<ParameterOntologyAnnotationWithSex> getPhenotypeParameterOntologyAssociations(Pipeline pipeline, Procedure procedure, Parameter parameter) {

        List<ParameterOntologyAnnotationWithSex> annotations = new ArrayList<>();

        // Get this parameter's ontology annotations.
        // NOTE: if ontologyTermsFromWs is null, continue on to the mp ontology terms.
        Map<String, String> ontologyTermsFromWs = impressLoadUtils.getOntologyTermsFromWs(pipeline.getStableKey(), procedure.getScheduleKey(), procedure.getStableKey(), parameter);
        if (ontologyTermsFromWs != null) {

            for (Map.Entry<String, String> entry : ontologyTermsFromWs.entrySet()) {

                ParameterOntologyAnnotationWithSex ontologyAnnotation = new ParameterOntologyAnnotationWithSex();

                String originalOntologyAcc  = entry.getKey();                                                           // Get the original accession id
                String originalOntologyName = ImpressLoadUtils.newlineToSpace(entry.getValue());                            // Get the original ontology term

                OntologyTerm updatedOntologyTerm = getUpdatedOntologyTerm(originalOntologyAcc, originalOntologyName, pipeline, procedure, parameter);
                if (updatedOntologyTerm == null) {
                    continue;
                }

                ontologyAnnotation.setOntologyTerm(updatedOntologyTerm);
                parameter.addAnnotation(ontologyAnnotation);
                annotations.add(ontologyAnnotation);
            }
        }

        // Get this parameter's MP ontology annotations.
        Map<String, String> mpOntologyTermsFromWs = impressLoadUtils.getMpOntologyTermsFromWs(pipeline.getStableKey(), procedure.getScheduleKey(), procedure.getStableKey(), parameter);
        if (mpOntologyTermsFromWs != null) {

            // Get the outcome and sex for each of this parameter's MP terms
            Map<String, List<ImpressParamMpterm>> impressParamMpTermsByOntologyTermAccessionId = impressLoadUtils.getParamMpTermsByOntologyTermAccessionId(
                    pipeline.getStableKey(), procedure.getScheduleKey(), procedure.getStableKey(), parameter, updatedOntologyTermsByStableKey);

            for (Map.Entry<String, String> entry : mpOntologyTermsFromWs.entrySet()) {

                String ontologyAcc  = entry.getKey();                                                                   // Get the accession id
                String ontologyName = ImpressLoadUtils.newlineToSpace(entry.getValue());                                    // Get the ontology term

                OntologyTerm ontologyTerm = getUpdatedOntologyTerm(ontologyAcc, ontologyName, pipeline, procedure, parameter); // If the ontology term can't be found/created, continue to the next.
                if (ontologyTerm == null) {
                    continue;
                }

                // Remap original ontology acc and name to be updated.
                ontologyAcc = ontologyTerm.getId().getAccession();
                ontologyName = ontologyTerm.getName();

                List<ImpressParamMpterm> paramMpterms = impressParamMpTermsByOntologyTermAccessionId.get(ontologyAcc);
                if (paramMpterms == null) {
                    logger.warn("No paramMpTerms for ontologyAcc {} for pipelineKey::scheduleId::procedureKey::parameterKey {}::{}::{}::{}",
                                ontologyAcc, pipeline.getStableId(), procedure.getScheduleKey(), procedure.getStableId(), parameter.getStableId());
                    continue;
                }
                for (ImpressParamMpterm paramMpterm : paramMpterms) {

                    ParameterOntologyAnnotationWithSex mpOntologyAnnotation = new ParameterOntologyAnnotationWithSex();

                    // OUTCOME
                    String outcome = paramMpterm.getSelectionOutcome();
                    PhenotypeAnnotationType phenotypeAnnotationType = ((outcome == null) || (outcome.trim().isEmpty()) ? null : PhenotypeAnnotationType.find(outcome));
                    mpOntologyAnnotation.setType(phenotypeAnnotationType);

                    // SEX
                    String sex = paramMpterm.getSex();
                    if ((sex != null) && (!sex.trim().isEmpty())) {                                                       // Get SexType
                        SexType sexType = getSexType(sex, parameter.getStableId());
                        mpOntologyAnnotation.setSex             (sexType);
                    }

                    // OPTIONS - If this ontology term has options, wire them up.
                    if (parameter.isOptionsFlag()) {
                        String optionName = paramMpterm.getOptionText();

                        // Look up the option in this parameter's options list so we can extract the phenotype_parameter_option primary key.
                        for (ParameterOption parameterOption : parameter.getOptions()) {
                            if (parameterOption.getName().equals(optionName)) {
                                parameterOption.setNormalCategory(true);
                                mpOntologyAnnotation.setOption(parameterOption);

                                // log every 1000th association for spot checking.
                                if ((++associationCounter % 200) == 0) {
                                    logger.info("Associate outcome {} to option {} for parameterKey {} ({}) with MP Ontology term {}", outcome, parameterOption.getName(), parameter.getStableId(), parameter.getStableKey(), ontologyTerm.getId().getAccession());
                                }
                                break;
                            }
                        }
                    }

                    mpOntologyAnnotation.setOntologyTerm(ontologyTerm);
                    parameter.addAnnotation(mpOntologyAnnotation);
                    annotations.add(mpOntologyAnnotation);
                }
            }
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


    private OntologyTerm getUpdatedOntologyTerm(String ontologyAcc, String ontologyName, Pipeline pipeline, Procedure procedure, Parameter parameter) {
        OntologyTerm ontologyTerm = updatedOntologyTermsByOriginalOntologyAccessionId.get(ontologyAcc);                                          // Get the updated ontology term if it exists.
        if (ontologyTerm == null) {

            if ((ontologyAcc == null) || (ontologyAcc.trim().isEmpty())) {
                logger.error("Empty/null ontologyAcc for {}({})::{}({})::{}({}). Skipping ...",
                             pipeline.getStableKey(), pipeline.getStableId(),
                             procedure.getStableKey(), procedure.getStableId(),
                             parameter.getStableKey(), parameter.getStableId());
                return null;
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
            updatedOntologyTermsByOriginalOntologyAccessionId.put(ontologyTerm.getId().getAccession(), ontologyTerm);

            logger.warn("Created ontology term {}, name '{}' for {}({})::{}({})::{}({})",
                        ontologyTerm.getId(), ontologyTerm.getName(),
                        pipeline.getStableKey(), pipeline.getStableId(),
                        procedure.getStableKey(), procedure.getStableId(),
                        parameter.getStableKey(), parameter.getStableId());
        }

        return ontologyTerm;
    }


    /**
     * Inserts the specified {@link Parameter} and any related increments, options, and ontology annotations
     * @param parameter the {@link Parameter} instance to be inserted
     * @return the same {@link Parameter} instance, with primary keys set, if successful; null otherwise
     */
    private Parameter insertParameter(Pipeline pipeline, Parameter parameter, Procedure procedure, String pipelineKey) {
        Long phenotypeParameterPk = cdabaseSqlUtils.insertPhenotypeParameter(parameter);
        if (phenotypeParameterPk == null) {
            logger.warn("INSERT OF pipeline::procedure::parameter " + pipelineKey + "::" + procedure.getStableId() + "::" + phenotypeParameterPk + " failed. Parameter skipped...");
            return null;
        }

        // INCREMENTS
        if (parameter.isIncrementFlag()) {

            List<ParameterIncrement> increments = impressLoadUtils.getIncrements(pipeline.getStableKey(), procedure.getScheduleKey(), procedure.getStableKey(), parameter.getStableKey());
            cdabaseSqlUtils.insertPhenotypeParameterIncrements(parameter.getId(), increments);
        }

        // OPTIONS
        if (parameter.isOptionsFlag()) {

            List<ParameterOption> options = impressLoadUtils.getOptions(pipeline.getStableKey(), procedure.getScheduleKey(), procedure.getStableKey(), parameter, normalCategory);
            cdabaseSqlUtils.insertPhenotypeParameterOptions(parameter.getId(), options);
            parameter.setOptions(options);                                                                              // Set the list of options (with their primary keys) for use by the next step.
        }

        // ONTOLOGY ANNOTATIONS
        List<ParameterOntologyAnnotationWithSex> annotations =
                getPhenotypeParameterOntologyAssociations(pipeline, procedure, parameter);
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