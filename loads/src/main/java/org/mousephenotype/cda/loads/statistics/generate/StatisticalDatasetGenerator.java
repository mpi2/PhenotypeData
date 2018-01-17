package org.mousephenotype.cda.loads.statistics.generate;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.BasicService;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generate input files for batch stats processing with PhenStat
 * Read from solr cores and produce files for all parameters to be analyzed
 *
 * @author Andrew Tikhonov
 * @author Jeremy Mason
 *
 */
@Import(value = {StatisticalDatasetGeneratorConfig.class})
public class StatisticalDatasetGenerator extends BasicService implements CommandLineRunner {


    public static final String FILENAME_SEPERATOR = "--";

    final private Logger logger = LoggerFactory.getLogger(getClass());
    final private SolrClient experimentCore;
    final private SolrClient pipelineCore;


    @Inject
    public StatisticalDatasetGenerator(
            @Named("experimentCore") SolrClient experimentCore,
            @Named("pipelineCore") SolrClient pipelineCore) {
        Assert.notNull(experimentCore, "Experiment core cannot be null");
        Assert.notNull(pipelineCore, "Pipeline core cannot be null");

        this.experimentCore = experimentCore;
        this.pipelineCore = pipelineCore;
    }

    private final Set<String> skipProcedures = new HashSet<>(Arrays.asList(
            "SLM_PBI", "SLM_HEM", "IMPC_ALZ", "MGP_ALZ", "IMPC_ELZ", "IMPC_HIS", "IMPC_GEM",
            "IMPC_EMA", "IMPC_GEP", "IMPC_GPP", "IMPC_EVP", "IMPC_MAA", "IMPC_GEO",
            "IMPC_GPO", "IMPC_EMO", "IMPC_GPO", "IMPC_EVO", "IMPC_GPM", "IMPC_EVM",
            "IMPC_GEL", "IMPC_HPL", "IMPC_HEL", "IMPC_EOL", "IMPC_GPL", "IMPC_EVL",
            "IMPC_VIA", "IMPC_FER"));


    private final Set<String> skipParameters = new HashSet<>(Arrays.asList(
            // Do not statistically analyze ANA derived parameter per TM 20171121
            "MGP_ANA_002_001",
            "M-G-P_022_001_001",
            // Skip these parameters (from Natasha)
            "JAX_SLW_001_001" ,"JAX_LDT_007_001" ,"ICS_SHO_004_001" ,"IMPC_EYE_056_001",
            "IMPC_ECG_015_001" ,"IMPC_ECG_003_001" ,"JAX_LDT_006_001" ,"IMPC_ECG_010_001"));


    private final static List<String> PIVOT = Arrays.asList(
            ObservationDTO.DATASOURCE_NAME,
            ObservationDTO.PROJECT_NAME,
            ObservationDTO.PHENOTYPING_CENTER,
            ObservationDTO.PIPELINE_STABLE_ID,
            ObservationDTO.PROCEDURE_GROUP,
            ObservationDTO.STRAIN_ACCESSION_ID);

    @Override
    public void run(String... strings) throws Exception {

        logger.info("Starting statistical dataset generation");

        logger.info("Populating normal category lookup");
        Map<String, String> normalEyeCategory = getNormalEyeCategories();

        List<String> parametersToLoad = null;

        OptionParser parser = new OptionParser();
        parser.accepts("parameters").withRequiredArg();
        OptionSet options = parser.parse( strings );

        if (options.has("parameters")) {
            String paramString = (String) options.valueOf("parameters");
            parametersToLoad = Arrays.asList(paramString.split(","));
        }


        Map<String, SortedSet<String>> parameters = getParameterMap(parametersToLoad);
        logger.info("Prepared " + parameters.keySet().size() + " procedure groups");

        List<Map<String, String>> results = getStatisticalDatasets(parametersToLoad);

        logger.info("Processing {} data sets", results.size());

        // Create directory if not exists
        File d = new File("tsvs");
        if ( ! d.exists()) {
            d.mkdir();
        }

        results
            .stream()
            .filter(x -> parameters.get(x.get(ObservationDTO.PROCEDURE_GROUP)) != null)
            .parallel()
            .forEach(result -> {

                logger.info("Processing {} {} {} {} {}", result.get(ObservationDTO.DATASOURCE_NAME), result.get(ObservationDTO.PHENOTYPING_CENTER), result.get(ObservationDTO.PIPELINE_STABLE_ID), result.get(ObservationDTO.PROCEDURE_GROUP), result.get(ObservationDTO.STRAIN_ACCESSION_ID));

                String fields = "date_of_experiment,external_sample_id,strain_name,allele_accession_id,gene_accession_id,gene_symbol,weight,sex,zygosity,biological_sample_group,colony_id,metadata_group,parameter_stable_id,parameter_name,data_point,category,observation_type";

                SolrQuery q1 = new SolrQuery();
                q1.setQuery("*:*")
                        .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":(" + parameters.get(result.get(ObservationDTO.PROCEDURE_GROUP)).stream().collect(Collectors.joining(" OR ")) + ")")
                        .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + result.get(ObservationDTO.PHENOTYPING_CENTER) + "\"")
                        .addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + result.get(ObservationDTO.PIPELINE_STABLE_ID))
                        .addFilterQuery(ObservationDTO.PROCEDURE_GROUP + ":" + result.get(ObservationDTO.PROCEDURE_GROUP))
                        .addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + result.get(ObservationDTO.STRAIN_ACCESSION_ID) + "\"")

                        .addFilterQuery("observation_type:(categorical OR unidimensional)")
                        .setFields(fields)
                        .setRows(Integer.MAX_VALUE)
                ;

                // Project remapping rules switches EUMODIC to MGP for some legacy colonies, but cannot
                // remap corresponding controls (no imits entry for controls).
                // Ignore project when assembling control group for these remapped colonies

// ********* TEMPORARY TEMPORARY TEMPORARY TEMPORARY TEMPORARY *********
// For now, do not include PROJECT in the splitting
// ********* TEMPORARY TEMPORARY TEMPORARY TEMPORARY TEMPORARY *********

//                if ( ! (
//                        "MGP".equals(result.get(ObservationDTO.PROJECT_NAME)) &&
//                        "WTSI".equals(result.get(ObservationDTO.PHENOTYPING_CENTER)) &&
//                        "M-G-P_001".equals(result.get(ObservationDTO.PIPELINE_STABLE_ID))
//                        )
//                    ) {
//                    q1.addFilterQuery(ObservationDTO.PROJECT_NAME + ":\"" + result.get(ObservationDTO.PROJECT_NAME) + "\"");
//                }

// ********* TEMPORARY TEMPORARY TEMPORARY TEMPORARY TEMPORARY *********
// For now, do not include PROJECT in the splitting
// ********* TEMPORARY TEMPORARY TEMPORARY TEMPORARY TEMPORARY *********

                logger.debug(SolrUtils.getBaseURL(experimentCore) + "/select" + q1.toQueryString());

                try {
                    List<ObservationDTO> observationDTOs = experimentCore.query(q1).getBeans(ObservationDTO.class);
                    Map<String, Map<String, String>> specimenParameterMap = new HashMap<>();

                    for (ObservationDTO observationDTO : observationDTOs) {

                        String colonyId = observationDTO.getGroup().equals(BiologicalSampleType.control.getName()) ? "+/+" : observationDTO.getColonyId();
                        String zygosity = observationDTO.getGroup().equals(BiologicalSampleType.control.getName()) ? "" : observationDTO.getZygosity();
                        String alleleAccessionId = observationDTO.getGroup().equals(BiologicalSampleType.control.getName()) ? "" : observationDTO.getAlleleAccession();
                        String geneAccessionId = observationDTO.getGroup().equals(BiologicalSampleType.control.getName()) ? "" : observationDTO.getGeneAccession();
                        String specimenId = observationDTO.getExternalSampleId();
                        String dateOfExperiment = observationDTO.getDateOfExperimentString();
                        String weight = observationDTO.getWeight() != null ? observationDTO.getWeight().toString() : "";
                        String sex = observationDTO.getSex();
                        String biologicalSampleGroup = observationDTO.getGroup();
                        String strainName = observationDTO.getStrainName();
                        String metadataGroup = observationDTO.getMetadataGroup();

                        String key = Stream.of(specimenId, biologicalSampleGroup, strainName, colonyId, geneAccessionId, alleleAccessionId, dateOfExperiment, metadataGroup, zygosity, weight, sex).collect(Collectors.joining("\t"));

                        if (!specimenParameterMap.containsKey(key)) {
                            specimenParameterMap.put(key, new HashMap<>());
                        }

                        String dataValue;

                        switch (ObservationType.valueOf(observationDTO.getObservationType())) {
                            case categorical:
                                dataValue = observationDTO.getCategory();
                                break;
                            case unidimensional:
                                dataValue = observationDTO.getDataPoint().toString();
                                break;
                            default:
                                // No value
                                continue;
                        }

                        specimenParameterMap.get(key).put(observationDTO.getParameterStableId(), dataValue);


                        // Add a column for the MAPPED category for EYE parameters
                        if (ObservationType.valueOf(observationDTO.getObservationType()) == ObservationType.categorical &&
                                (
                                        observationDTO.getParameterStableId().toUpperCase().contains("_EYE_") ||
                                                observationDTO.getParameterStableId().toUpperCase().contains("M-G-P_014") ||
                                                observationDTO.getParameterStableId().toUpperCase().contains("ESLIM_014")
                                )
                                ) {

                            // Get mapped data category
                            String mappedDataValue = observationDTO.getCategory();
                            switch (observationDTO.getCategory()) {

                                case "imageOnly":
                                case "no data":
                                case "no data for both eyes":
                                case "No data":
                                case "not defined":
                                case "unobservable":
                                    mappedDataValue = "";
                                    break;

                                // Per MPI2 F2F 20170922
                                // Do not remap the "one eye normal" categories to "normal"
//                            case "no data left eye":
//                            case "no data right eye":
//                                // Map to normal category
//                                mappedDataValue = normalEyeCategory.get(observationDTO.getParameterStableId());
//                                break;

                                case "no data left eye, present right eye":
                                    mappedDataValue = "present right eye";
                                    break;

                                case "no data right eye, present left eye":
                                    mappedDataValue = "present left eye";
                                    break;

                                case "no data left eye, right eye abnormal":
                                    mappedDataValue = "right eye abnormal";
                                    break;

                                case "no data right eye, left eye abnormal":
                                    mappedDataValue = "left eye abnormal";
                                    break;

                                default:
                                    break;
                            }

                            String mappedCategory = observationDTO.getParameterStableId() + "_MAPPED";
                            specimenParameterMap.get(key).put(mappedCategory, mappedDataValue);

                        }


                    }

                    logger.info("  Has {} specimens with {} parameters", specimenParameterMap.size(), specimenParameterMap.values().stream().mapToInt(value -> value.keySet().size()).sum());

                    // Allow low N if ABR procedure
                    if (specimenParameterMap.size() < 5 && ! (result.get(ObservationDTO.PROCEDURE_GROUP).equals("IMPC_ABR") || result.get(ObservationDTO.DATASOURCE_NAME).equals("3i") ) ) {
                        logger.info("  Not processing due to low N {} {} {} {} {}",
                                result.get(ObservationDTO.PROJECT_NAME),
                                result.get(ObservationDTO.PHENOTYPING_CENTER),
                                result.get(ObservationDTO.PIPELINE_STABLE_ID),
                                result.get(ObservationDTO.PROCEDURE_GROUP),
                                result.get(ObservationDTO.STRAIN_ACCESSION_ID)
                        );
                        return;
                    }

                    List<String> headers = new ArrayList<>(Arrays.asList("specimen_id", "group", "background_strain_name", "colony_id", "marker_accession_id", "allele_accession_id", "batch", "metadata_group", "zygosity", "weight", "sex", "::"));

                    final SortedSet<String> sortedParameters = parameters.get(result.get(ObservationDTO.PROCEDURE_GROUP));
                    headers.addAll(sortedParameters);

                    List<List<String>> lines = new ArrayList<>();
                    lines.add(headers);

                    for (String key : specimenParameterMap.keySet()) {

                        // If the specimen doesn't have any parameters associated, skip it
                        if (specimenParameterMap.get(key).values().size() < 1) {
                            continue;
                        }

                        Map<String, String> data = specimenParameterMap.get(key);

                        List<String> line = new ArrayList<>();
                        line.addAll(Arrays.asList(key.split("\t")));
                        line.add("::"); // Separator column

                        for (String parameter : sortedParameters) {
                            line.add(data.getOrDefault(parameter, ""));
                        }

                        lines.add(line);

                    }

                    StringBuilder sb = new StringBuilder();
                    for (List<String> line : lines) {
                        sb.append(line.stream().collect(Collectors.joining("\t")));
                        sb.append("\n");
                    }

                    String filename = "tsvs/" + Stream.of(
                            result.get(ObservationDTO.DATASOURCE_NAME).replace(" ", "_"),
                            result.get(ObservationDTO.PROJECT_NAME).replace(" ", "_"),
                            result.get(ObservationDTO.PHENOTYPING_CENTER).replace(" ", "_"),
                            result.get(ObservationDTO.PIPELINE_STABLE_ID),
                            result.get(ObservationDTO.PROCEDURE_GROUP),
                            result.get(ObservationDTO.STRAIN_ACCESSION_ID).replace(":", "")).collect(Collectors.joining(FILENAME_SEPERATOR)) + ".tsv";

                    Path p = new File(filename).toPath();
                    logger.info("Writing file {} ({})", filename, p);
                    Files.write(p, sb.toString().getBytes());

                } catch (Exception e) {
                    logger.warn("Error occurred geting data for query: " + q1.toQueryString(), e);
                }


            });

    }

    public List<Map<String, String>> getStatisticalDatasets(List<String> parameters) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*")

                // Filter out line level parameters
                .addFilterQuery("-procedure_group:(" + StringUtils.join(skipProcedures, " OR ") + ")")

                // Only processing categorical and unidimensional parameters
                .addFilterQuery("observation_type:(categorical OR unidimensional)")

                // Filter out incorrect M-G-P pipeline bodyweight
                .addFilterQuery("-parameter_stable_id:(" + StringUtils.join(skipParameters, " OR ") + ")")

                // Filter out IMM results until we have normalised parameters in IMPRESS
                .addFilterQuery("-parameter_stable_id:*_IMM_*")

                // Include only parameters for which we have experimental data
                .addFilterQuery("biological_sample_group:experimental")

                .setRows(0)
                .setFacet(true)
                .setFacetLimit(-1)
                .setFacetMinCount(1)
                .addFacetPivotField(PIVOT.stream().collect(Collectors.joining(",")));

        if (parameters!=null) {
            query.addFilterQuery("parameter_stable_id:(" + StringUtils.join(parameters, " OR ") + ")");
        }

        logger.info(SolrUtils.getBaseURL(experimentCore) + "/select" + query.toQueryString());
        return getFacetPivotResults(experimentCore.query(query), false);
    }

    /**
     * Get list of "normal" category
     * @return map of categories
     */
    private Map<String,String> getNormalEyeCategories() {

    Map<String,String> map = new HashMap<>();

        map.put("ESLIM_014_001_001", "normal");
        map.put("ESLIM_014_001_003", "normal");
        map.put("ESLIM_014_001_004", "absent");
        map.put("ESLIM_014_001_005", "normal");
        map.put("ESLIM_014_001_006", "normal");
        map.put("ESLIM_014_001_007", "normal");
        map.put("ESLIM_014_001_008", "absent");
        map.put("ESLIM_014_001_009", "normal");
        map.put("ESLIM_014_001_010", "normal");
        map.put("ESLIM_014_001_011", "normal");
        map.put("ESLIM_014_001_012", "normal");
        map.put("ESLIM_014_001_013", "normal");
        map.put("ESLIM_014_001_014", "normal");
        map.put("ESLIM_014_001_015", "normal");
        map.put("IMPC_EYE_001_001",  "present");
        map.put("IMPC_EYE_002_001",  "absent");
        map.put("IMPC_EYE_003_001",  "absent");
        map.put("IMPC_EYE_004_001",  "normal");
        map.put("IMPC_EYE_005_001",  "normal");
        map.put("IMPC_EYE_006_001",  "normal");
        map.put("IMPC_EYE_007_001",  "normal");
        map.put("IMPC_EYE_008_001",  "absent");
        map.put("IMPC_EYE_009_001",  "absent");
        map.put("IMPC_EYE_010_001",  "normal");
        map.put("IMPC_EYE_011_001",  "normal");
        map.put("IMPC_EYE_012_001",  "normal");
        map.put("IMPC_EYE_013_001",  "normal");
        map.put("IMPC_EYE_014_001",  "normal");
        map.put("IMPC_EYE_015_001",  "normal");
        map.put("IMPC_EYE_016_001",  "normal");
        map.put("IMPC_EYE_017_001",  "absent");
        map.put("IMPC_EYE_018_001",  "absent");
        map.put("IMPC_EYE_019_001",  "absent");
        map.put("IMPC_EYE_020_001",  "normal");
        map.put("IMPC_EYE_021_001",  "normal");
        map.put("IMPC_EYE_022_001",  "normal");
        map.put("IMPC_EYE_023_001",  "normal");
        map.put("IMPC_EYE_024_001",  "normal");
        map.put("IMPC_EYE_025_001",  "normal");
        map.put("IMPC_EYE_026_001",  "normal");
        map.put("IMPC_EYE_027_001",  "absent");
        map.put("IMPC_EYE_080_001",  "absent");
        map.put("IMPC_EYE_081_001",  "absent");
        map.put("IMPC_EYE_082_001",  "normal");
        map.put("IMPC_EYE_083_001",  "normal");
        map.put("IMPC_EYE_084_001",  "absent");
        map.put("IMPC_EYE_085_001",  "absent");
        map.put("IMPC_EYE_086_001",  "absent");
        map.put("M-G-P_014_001_001", "normal");
        map.put("M-G-P_014_001_003", "normal");
        map.put("M-G-P_014_001_004", "absent");
        map.put("M-G-P_014_001_005", "normal");
        map.put("M-G-P_014_001_006", "normal");
        map.put("M-G-P_014_001_007", "normal");
        map.put("M-G-P_014_001_008", "absent");
        map.put("M-G-P_014_001_009", "normal");
        map.put("M-G-P_014_001_010", "normal");
        map.put("M-G-P_014_001_011", "normal");
        map.put("M-G-P_014_001_012", "normal");
        map.put("M-G-P_014_001_013", "normal");
        map.put("M-G-P_014_001_014", "normal");
        map.put("M-G-P_014_001_015", "normal");

        return map;
    }

    /**
     * Gets a map of procedure group -> SortedSet(paramter stable IDs) for each procedure group
     * Parameters that have no associated ontology term are not to be annotated (subsequently, not analysed) are not
     * included in the SortedSet
     *
     * @return Map of procedure group key to a Set of parameter stable IDs from IMPReSS
     */
    private Map<String, SortedSet<String>> getParameterMap(List<String> parametersToLoad) throws SolrServerException, IOException {

        Map<String, SortedSet<String>> parameters = new HashMap<>();

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
                .addFilterQuery("annotate:true")
                .addFilterQuery("observation_type:(categorical OR unidimensional) OR parameter_stable_id:IMPC_EYE_092_001")
            .setFields(ImpressDTO.PROCEDURE_STABLE_ID, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.HAS_OPTIONS)
            .setRows(Integer.MAX_VALUE);

        if (parametersToLoad!= null) {
            query.addFilterQuery("parameter_stable_id:("+StringUtils.join(parametersToLoad, " OR ")+")");
        }

        pipelineCore
            .query(query)
            .getBeans(ImpressDTO.class)
            .forEach(x -> {

                final String procedureStableId = x.getProcedureStableId();
                final String procedureGroup = procedureStableId.substring(0, procedureStableId.lastIndexOf("_"));

                if ( ! parameters.containsKey(procedureGroup)) {
                    // Use an ordered set (TreeSet) to keep the parameters sorted low to high
                    parameters.put(procedureGroup, new TreeSet<>());
                }

                parameters.get(procedureGroup).add(x.getParameterStableId());

                // Add another column to the EYE procedures to store the mapped categories (or slit lamp for legacy procedures)
                // as agreed at the 20170824 Dev call
                if (
                        (
                                x.getParameterStableId().toUpperCase().contains("IMPC_EYE") ||
                                procedureGroup.toUpperCase().contains("M-G-P_013") ||
                                procedureGroup.toUpperCase().contains("M-G-P_014") ||
                                procedureGroup.toUpperCase().contains("ESLIM_013") ||
                                procedureGroup.toUpperCase().contains("ESLIM_014")
                        ) &&
                        x.isHasOptions()
                    ) {
                    String mappedParameter = x.getParameterStableId() + "_MAPPED";
                    parameters.get(procedureGroup).add(mappedParameter);

                }


            });

        return parameters;
    }

    public static void main(String[] args) {
        SpringApplication.run(StatisticalDatasetGenerator.class, args);
    }


}
