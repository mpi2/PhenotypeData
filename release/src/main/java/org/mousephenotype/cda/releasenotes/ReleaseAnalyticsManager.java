package org.mousephenotype.cda.releasenotes;


import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.db.pojo.MetaInfo;
import org.mousephenotype.cda.db.repositories.MetaInfoRepository;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Populate meta_info table and associated tables to a new datarelease. Must be run at the end of the release process, after the solr cores are built as well.
 * This is a replacement for the one in AdminTools.
 */
@Component
public class ReleaseAnalyticsManager implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseAnalyticsManager.class);

    public final List<String> INCLUDED_RESOURCE = Arrays.asList("IMPC");
    private Set<MetaInfo> dataReleaseFacts = new HashSet<>();


    @Value("${git.branch}")
    private String gitBranch;

    private ObservationService observationService;
    private GenotypePhenotypeService genotypePhenotypeService;
    private MetaInfoRepository metaInfoRepository;

    @Inject
    public ReleaseAnalyticsManager(
            @NotNull ObservationService observationService,
            @NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
            @NotNull MetaInfoRepository metaInfoRepository,
            @NotNull DataSource komp2DataSource
    ) throws SQLException {
        this.observationService = observationService;
        this.genotypePhenotypeService = genotypePhenotypeService;
        this.metaInfoRepository = metaInfoRepository;
        connection = komp2DataSource.getConnection();
    }

    @Override
    public void run(String... args) throws Exception {

        OptionParser parser = new OptionParser();

        parser.allowsUnrecognizedOptions();
        parser.accepts("dr").withRequiredArg().ofType(String.class).describedAs("Data release version (e.g., \"12.0\")");
        parser.accepts("drdate").withRequiredArg().ofType(String.class).describedAs("Data release date (e.g., \"16 October 2009\")");
        parser.accepts("psversion").withRequiredArg().ofType(String.class).describedAs("Statistical pipeline and version (e.g., \"OpenStats 1.0.2\")");

        OptionSet options = parser.parse(args);

        DATA_RELEASE_VERSION = (String) options.valueOf("dr");
        DATA_RELEASE_DATE = (String) options.valueOf("drdate");
        PHENSTAT_VERSION = (String) options.valueOf("psversion");
        DATA_RELEASE_DESCRIPTION = "Major data release " + DATA_RELEASE_VERSION + ", released on " + DATA_RELEASE_DATE + ", analysed using " + PHENSTAT_VERSION;

        // Add all facts about this release to the dataReleaseFacts set

        updateStaticFacts();

        populateMetaInfo();
        createAnalyticsTables();
        populateMPTerms();
        populateMetaInfoWithMPTerms();

        // Persist all facts to the database
        //metaInfoRepository.saveAll(dataReleaseFacts);
        System.out.println("FACTS\n" + dataReleaseFacts);

    }



    private Connection connection;

    //
    // DATA RELEASE INFORMATION
    //
    private String DATA_RELEASE_VERSION;
    private String DATA_RELEASE_DATE;
    private String PHENSTAT_VERSION;
    private String DATA_RELEASE_DESCRIPTION;

    // Patterns for regular expressions
    public static final String ALLELE_NOMENCLATURE = "^[^<]+<([^\\(]+)\\(([^\\)]+)\\)([^>]+)>";
    public static final String TARGETED_ALLELE_CLASS_1 = "^tm(\\d+)([a-z]{0,1})";


    public static final String[] dataTypes = new String[]{
            Constants.CATEGORICAL_DATATYPE,
            Constants.UNIDIMENSIONAL_DATATYPE,
            Constants.TIME_SERIES_DATATYPE,
            Constants.TEXT_DATATYPE,
            Constants.IMAGE_RECORD_DATATYPE
    };

    /**
     * Facts about the data release.
     */
    public void updateStaticFacts() {

        dataReleaseFacts.add(new MetaInfo("data_release_version", DATA_RELEASE_VERSION, DATA_RELEASE_DESCRIPTION));
        dataReleaseFacts.add(new MetaInfo("data_release_date", DATA_RELEASE_DATE, DATA_RELEASE_DATE));
        dataReleaseFacts.add(new MetaInfo("statistical_packages", "OpenStats", "BioConductor Statistical package used to compute genotype to phenotype significant associations"));
        dataReleaseFacts.add(new MetaInfo("PhenStat_release_version", PHENSTAT_VERSION, "Version of statistical pipeline used to analyse phenotype data"));
        dataReleaseFacts.add(new MetaInfo("PhenStat_repository", "https://github.com/mpi2/OpenStats","GitHub repository for statistical pipeline code"));
        dataReleaseFacts.add(new MetaInfo("code_release_version", gitBranch, "Code used to build the database and to run the portal"));
        dataReleaseFacts.add(new MetaInfo("code_repository", "https://github.com/mpi2/PhenotypeData", "GitHub repository for the mouse phenotype portal"));
        dataReleaseFacts.add(new MetaInfo("ftp_site", "ftp://ftp.ebi.ac.uk/pub/databases/impc/release-" + DATA_RELEASE_VERSION, "Location of latest release data on FTP site"));
        dataReleaseFacts.add(new MetaInfo("genome_assembly_version", "GRCm38", "Genome Reference Consortium GRCm38"));
        dataReleaseFacts.add(new MetaInfo("species", "Mus musculus", "Mus musculus phenotype database"));
        dataReleaseFacts.add(new MetaInfo("statistically_significant_threshold", "1x10-4", "Statistical significance threshold used to define a significant difference from the null hypothesis"));

    }

    /**
     * This will create additional tables for the release that we will use
     * for historical graph plotting of data progression
     */
    public static final String[] analyticsTableCreation = new String[]{

            "drop table if exists analytics_lines_procedures;",
            "create table analytics_lines_procedures select count(distinct ls.colony_id) as nb_mutant_lines, org.name as phenotyping_center, ex.procedure_stable_id, pp.name as procedure_name from biological_sample bs join observation o on o.biological_sample_id = bs.id join live_sample ls on ls.id = bs.id join experiment_observation exo on exo.observation_id = o.id join experiment ex on ex.id = exo.experiment_id join phenotype_procedure pp on ex.procedure_id = pp.id join organisation org on org.id = ex.organisation_id where bs.sample_group = 'experimental' and bs.db_id = 22 group by ex.procedure_stable_id, org.name;",
            "drop table if exists analytics_significant_calls_procedures;",
            "create table analytics_significant_calls_procedures select count(pcs.p_value) as significant_calls, o.name as phenotyping_center, pp.stable_id as procedure_stable_id, pp.name as procedure_name from phenotype_call_summary pcs join phenotype_procedure pp on pp.id = pcs.procedure_id join organisation o on o.id = pcs.organisation_id where pcs.external_db_id = 22 and p_value <= 0.0001 group by o.name, pp.stable_id order by o.name asc;",
            "drop table if exists analytics_pvalue_distribution;",
            "create table analytics_pvalue_distribution (id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, datatype varchar(50) NOT NULL, statistical_method varchar(200) NOT NULL, pvalue_bin float NOT NULL, interval_scale float NOT NULL, pvalue_count int(10) not null, PRIMARY KEY(id)) COLLATE=utf8_general_ci ENGINE=MyISAM;",
            "truncate table analytics_mp_calls;",
            "ALTER TABLE analytics_mp_calls AUTO_INCREMENT = 1;",
            "ALTER TABLE analytics_significant_calls_procedures\n" +
                    "ADD COLUMN `id` INT(10) NOT NULL AUTO_INCREMENT FIRST,\n" +
                    "ADD PRIMARY KEY (`id`);"
    };

    public static final String[] statisticalMethods = new String[]{
            "Fisher's exact test",
            "Wilcoxon rank sum test with continuity correction",
            "Mixed Model framework, generalized least squares, equation withoutWeight",
            "Mixed Model framework, linear mixed-effects model, equation withoutWeight"
    };

    public static final String unidimensionalPValueQuery1 =
            "select count(1), statistical_method from stats_unidimensional_results where status = 'Success' and external_db_id = 22 and statistical_method = :method and null_test_significance is not null and null_test_significance <= :max;";

    public static final String unidimensionalPValueQuery2 =
            "select count(1), statistical_method from stats_unidimensional_results where status = 'Success' and external_db_id = 22 and statistical_method = :method and null_test_significance is not null and null_test_significance > :min and null_test_significance <= :max;";


    public static final String unidimensionalRankSumPValueQuery1 =
            "select count(1), statistical_method from stats_unidimensional_results where status = 'Success' and external_db_id = 22 and statistical_method = :method and gender_male_ko_pvalue is not null and gender_male_ko_pvalue <= :max union " +
                    "select count(1), statistical_method from stats_unidimensional_results where status = 'Success' and external_db_id = 22 and statistical_method = :method and gender_female_ko_pvalue is not null and gender_female_ko_pvalue <= :max;";


    public static final String unidimensionalRankSumPValueQuery2 =
            "select count(1), statistical_method from stats_unidimensional_results where status = 'Success' and external_db_id = 22 and statistical_method = :method and gender_male_ko_pvalue is not null and gender_male_ko_pvalue > :min and gender_male_ko_pvalue <= :max union " +
                    "select count(1), statistical_method from stats_unidimensional_results where status = 'Success' and external_db_id = 22 and statistical_method = :method and gender_female_ko_pvalue is not null and gender_female_ko_pvalue > :min and gender_male_ko_pvalue <= :max ;";

    public static final String categoricalPValueQuery1 =
            "select count(1), statistical_method from stats_categorical_results where p_value <= :max and status = 'Success' and external_db_id = 22 and statistical_method = :method;";

    public static final String categoricalPValueQuery2 =
            "select count(1), statistical_method from stats_categorical_results where p_value > :min and p_value <= :max and status = 'Success' and external_db_id = 22 and statistical_method = :method;";

    public static Map<String, String> statisticalMethodsQuery1Map = new HashMap<>();
    public static Map<String, String> statisticalMethodsQuery2Map = new HashMap<>();

    static {

        statisticalMethodsQuery1Map.put("Fisher's exact test", categoricalPValueQuery1);
        statisticalMethodsQuery2Map.put("Fisher's exact test", categoricalPValueQuery2);
        statisticalMethodsQuery1Map.put("Wilcoxon rank sum test with continuity correction", unidimensionalRankSumPValueQuery1);
        statisticalMethodsQuery2Map.put("Wilcoxon rank sum test with continuity correction", unidimensionalRankSumPValueQuery2);
        statisticalMethodsQuery1Map.put("Mixed Model framework, generalized least squares, equation withoutWeight", unidimensionalPValueQuery1);
        statisticalMethodsQuery2Map.put("Mixed Model framework, generalized least squares, equation withoutWeight", unidimensionalPValueQuery2);
        statisticalMethodsQuery1Map.put("Mixed Model framework, linear mixed-effects model, equation withoutWeight", unidimensionalPValueQuery1);
        statisticalMethodsQuery2Map.put("Mixed Model framework, linear mixed-effects model, equation withoutWeight", unidimensionalPValueQuery2);

    }

    public final String countDataPointsQuery =
            "select count(1), observation_type, missing, SUBSTRING_INDEX( `parameter_status` , ':', 1) AS ps, db_id from observation where db_id = 22 and observation_type in (" + generateCommaSeparatedList(Arrays.asList(dataTypes), true) + ") group by observation_type, db_id, missing, ps order by missing asc;";

    public final String countSpecimenPerCenterQuery =
            "select count(1), bs.sample_group, o.name from biological_sample bs join organisation o on o.id = bs.organisation_id where db_id = 22 group by o.name, bs.sample_group;";

    public final String distinctAlleleByCenterQuery =
            "select distinct a.symbol, gf.acc, gf.symbol, o.name from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join allele a on a.acc = bma.allele_acc left outer join genomic_feature gf on a.gf_acc = gf.acc where bs.sample_group = 'experimental' and bm.db_id = 22 group by bm.id, ls.colony_id order by bm.db_id asc, o.name asc;";


    public final String countMutantLinesPerCenterQuery =
            "select count(distinct ls.colony_id), o.name from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join observation ob on ob.biological_sample_id = ls.id where bs.db_id = 22 and bs.sample_group = 'experimental' group by o.name";


    public final String listPhenotypePipelinesPerCenter =
            "select distinct p.stable_id, o.name from phenotype_call_summary pcs join organisation o on o.id = pcs.organisation_id join phenotype_pipeline p on p.id = pcs.pipeline_id where external_db_id = 22;";

    public final String listStatisticalMethods =
            "select distinct datatype, statistical_method from analytics_pvalue_distribution;";


    Pattern allelePattern = Pattern.compile(ALLELE_NOMENCLATURE);
    Pattern targetedClass1Pattern = Pattern.compile(TARGETED_ALLELE_CLASS_1);

    Map<String, Integer>          alleleTypes      = new HashMap<>();
    Map<String, Integer>          vectorProjects   = new HashMap<>();
    Map<String, Integer>          phenotypedLines  = new HashMap<>();
    Map<String, Integer>          mutantSpecimens  = new HashMap<>();
    Map<String, Integer>          controlSpecimens = new HashMap<>();
    Map<String, List<String>>     centerPipelines  = new HashMap<>();




    private void insertMPTerm(
            Statement insertStatement,
            List<GenotypePhenotypeDTO> dataset,
            String mpTermAccField,
            String mpTermNameField,
            String mpTermLevel) throws SQLException {

//        for (GenotypePhenotypeDTO map : dataset) {
//
//            List<String> mpIds = new ArrayList<>();
//            List<String> mpNames = new ArrayList<>();
//            if (mpTermLevel.equalsIgnoreCase("top")) {
//                mpIds = map.getTopLevelMpTermId();
//                mpNames = map.getTopLevelMpTermName();
//            } else if (mpTermLevel.equalsIgnoreCase("intermediate")) {
//                mpIds = map.getIntermediateMpTermId();
//                mpNames = map.getIntermediateMpTermName();
//            } else {
//                mpIds.add(map.getMpTermId());
//                mpNames.add(map.getMpTermName());
//            }
//
//            if (mpNames == null) {
//                logger.error("Missing term(s) {} from ontodb -- skipping", (map.getMpTermId() != null) ? map.getMpTermId() : "not found");
//                continue;
//            }
//
//            for (int i = 0; i < mpNames.size(); i++) {
//
//                StringBuilder values = new StringBuilder();
//                values.append("'" + map.getPhenotypingCenter() + "',");
//                values.append("'" + map.getMarkerSymbol() + "',");
//                values.append("'" + map.getMarkerAccessionId() + "',");
//                values.append("'" + map.getColonyId() + "',");
//                values.append("'" + mpIds.get(i) + "',");
//                values.append("'" + mpNames.get(i) + "',");
//                values.append("'" + mpTermLevel + "'");
//
//                if (mpNames.get(i) == null || mpNames.get(i).equalsIgnoreCase("null")) {
//                    System.out.println("\n\nNULLL " + map);
//                    return;
//                }
//                System.out.println("\n values: " + values);
//
//                String query = "INSERT INTO analytics_mp_calls(phenotyping_center,marker_symbol,marker_accession_id,colony_id,mp_term_id,mp_term_name,mp_term_level) " +
//                        "VALUES(" + values.toString() + ")";
//                insertStatement.executeUpdate(query);
//            }
//        }
    }

    private void populateMPTerms() throws SQLException, IOException, SolrServerException {

//        List<GenotypePhenotypeDTO> dataset = null;
//        String resource = "IMPC";
//
//        Statement insertStatement = connection.createStatement();
//
//
//        dataset = genotypePhenotypeService.getAllMPByPhenotypingCenterAndColonies(resource);
//
//        System.out.println("TOP LEVEL");
//        String mpTermAccField = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID;
//        String mpTermNameField = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME;
//        insertMPTerm(insertStatement, dataset, mpTermAccField, mpTermNameField, "top");
//
//        System.out.println("INTERMEDIATE LEVEL");
//        mpTermAccField = GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID;
//        mpTermNameField = GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_NAME;
//        insertMPTerm(insertStatement, dataset, mpTermAccField, mpTermNameField, "intermediate");
//
//        System.out.println("DIRECT ANNOTATIONS");
//        mpTermAccField = GenotypePhenotypeDTO.MP_TERM_ID;
//        mpTermNameField = GenotypePhenotypeDTO.MP_TERM_NAME;
//        insertMPTerm(insertStatement, dataset, mpTermAccField, mpTermNameField, "leaf");
//
//
//        insertStatement.close();

    }

    private void populateMetaInfoWithMPTerms() throws SQLException {

//        Statement insertStatement = connection.createStatement();
//
//        // Once it's done, we want to summarize by top level
//        String query = "select distinct phenotyping_center, phenotyping_center_count, mp_term_name, mp_term_count from analytics_mp_calls where mp_term_level = 'top_level';";
//
//        String aggregateCount = "select count(1), mp_term_id from analytics_mp_calls where mp_term_level = 'top' group by mp_term_id;";
//
//        String topLevelsQuery = "select distinct mp_term_id, mp_term_name from analytics_mp_calls where mp_term_level = 'top';";
//        List<String> topLevelsList = new ArrayList<String>();
//
//        PreparedStatement statement = connection.prepareStatement(topLevelsQuery);
//        ResultSet resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//
//            topLevelsList.add(resultSet.getString(1));
//            System.out.println("topLevelsList " + topLevelsList);
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('top_level_" + resultSet.getString(1) + "','" + resultSet.getString(2) + "', 'Mammalian Phenotype top-level category " + resultSet.getString(2) + "')");
//
//        }
//
//        resultSet.close();
//        statement.close();
//
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('top_level_mps','" + generateCommaSeparatedList(topLevelsList.toArray(), false) + "', 'top-level MP terms')");
//
//        statement = connection.prepareStatement(aggregateCount);
//        resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//
//            int sum = resultSet.getInt(1);
//            String topLevel = resultSet.getString(2);
//
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('top_level_" + topLevel + "_calls','" + sum + "', 'Number of associations to top-level MP " + topLevel + "')");
//
//        }
//
//        insertStatement.close();
//
//        resultSet.close();
//        statement.close();

    }

    private void setStatisticalMethods() throws SQLException {

//        PreparedStatement statement = connection.prepareStatement(listStatisticalMethods);
//        ResultSet resultSet = statement.executeQuery();
//
//        Statement insertStatement = connection.createStatement();
//
//        while (resultSet.next()) {
//
//            //insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('statistical_method_"+resultSet.getString(1)+"',\""+resultSet.getString(2)+"\", \"Statistical method used for "+resultSet.getString(1)+" data\")");
//
//        }
//
//        insertStatement.close();
//
//        resultSet.close();
//        statement.close();

    }


    public void createAnalyticsTables() throws SQLException {

//        executeQueriesFromArray(analyticsTableCreation);

    }

    public void executeQueriesFromArray(String[] array) throws SQLException {

//        Statement simpleStatement = connection.createStatement();
//
//        for (int i = 0; i < array.length; i++) {
//            System.out.println("--" + array[i]);
//            simpleStatement.executeUpdate(array[i]);
//        }
//        simpleStatement.close();

    }

    public void populateMetaInfo() throws SQLException, IOException, SolrServerException {

        /*
         * Execute general queries
         */


        // number of significant calls
        final List<GenotypePhenotypeDTO> impcPhenotypeCalls = genotypePhenotypeService.getAllGenotypePhenotypes(INCLUDED_RESOURCE);
        Integer significantCalls = impcPhenotypeCalls.size();
        System.out.println("Significant phenotype calls:\t" + significantCalls);

        // number of genes
        final Set<String> impcGenes = observationService.getAllGeneIdsByResource(INCLUDED_RESOURCE, true);
        int totalGenes = impcGenes.size();
        System.out.println("Total genes:\t" + totalGenes);

        final Set<String> impcLines = observationService.getAllColonyIdsByResource(INCLUDED_RESOURCE, true);
        int totalMutantLines = impcLines.size();
        System.out.println("Total mutant lines:\t" + totalMutantLines);

        // total mutants per center
        final Map<String, Set<String>> coloniesByPhenotypingCenter = observationService.getColoniesByPhenotypingCenter(INCLUDED_RESOURCE, null);
        System.out.println("Total mutants for centers:\t" + Arrays.toString(coloniesByPhenotypingCenter.entrySet().toArray()));
        phenotypedLines = coloniesByPhenotypingCenter.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x->x.getValue().size()));


        // List pipeline per center
        final Map<String, List<String>> pipelineByCenter = observationService.getPipelineByCenter(INCLUDED_RESOURCE);
        System.out.println("Total mutants for centers:\t" + Arrays.toString(pipelineByCenter.entrySet().toArray()));
        centerPipelines = pipelineByCenter.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x->new ArrayList<>(x.getValue())));




//        /*
//         * Targeted allele type
//         * check tm1a, tm1b, etc...
//         */
//
//        statement = connection.prepareStatement(distinctAlleleByCenterQuery);
//        resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//
//            String allele = resultSet.getString(1);
//            System.out.println(allele);
//            getAlleleType(allele);
//
//        }
//
//        resultSet.close();
//        statement.close();
//
//        /*
//         * This part calculates the number of control and mutant mice per center
//         */
//        statement = connection.prepareStatement(countSpecimenPerCenterQuery);
//        resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//            if (resultSet.getString(2).equals("control")) {
//                controlSpecimens.put(resultSet.getString(3), resultSet.getInt(1));
//            } else {
//                mutantSpecimens.put(resultSet.getString(3), resultSet.getInt(1));
//            }
//        }
//
//        resultSet.close();
//        statement.close();

//        /*
//         * insert meta data
//         */
//
//        Statement insertStatement = connection.createStatement();
//
//        for (String alleleType : alleleTypes.keySet()) {
//            System.out.println(alleleType + "\t" + alleleTypes.get(alleleType));
//        }
//
//        for (String vectorProject : vectorProjects.keySet()) {
//            System.out.println(vectorProject + "\t" + vectorProjects.get(vectorProject));
//        }
//
//        // now write the results to the back-end
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('statistically_significant_calls'," + significantCalls + ",'Number of statistically significant calls')");
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_genes'," + totalGenes + ",'Number of distinct genes with complete phenotype data')");
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_lines'," + totalMutantLines + ",'Number of distinct lines with complete phenotype data')");
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_lines_centers','" + generateCommaSeparatedList(new ArrayList<>(phenotypedLines.keySet()), false) + "', 'Comma separated list of centers having submitted complete phenotype data')");
//        // center by center
//        for (String center : phenotypedLines.keySet()) {
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_lines_" + center + "','" + phenotypedLines.get(center) + "', 'Number of lines with complete phenotype data from " + center + "')");
//        }
//
//        /*
//         * Insert nb of specimen, center by center
//         */
//
//        for (String center : phenotypedLines.keySet()) {
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('control_specimens_" + center + "','" + controlSpecimens.get(center) + "', 'Number of control specimens with complete phenotype data from " + center + "')");
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('mutant_specimens_" + center + "','" + mutantSpecimens.get(center) + "', 'Number of mutant specimens with complete phenotype data from " + center + "')");
//
//        }
//
//        // mouse knockout programs
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('mouse_knockout_programs','" + generateCommaSeparatedList(new ArrayList<>(vectorProjects.keySet()), false) + "', 'Comma separated list of mouse knockout programs having contributed to IMPC')");
//        // program by program
//        for (String program : vectorProjects.keySet()) {
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('alleles_" + program + "','" + vectorProjects.get(program) + "', 'Number of " + program + " alleles')");
//        }
//
//        // allele type
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('targeted_allele_types','" + generateCommaSeparatedList(new ArrayList<>(alleleTypes.keySet()), false) + "', 'Comma separated list of mouse knockout allele types')");
//        for (String alleleType : alleleTypes.keySet()) {
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('targeted_allele_type_" + alleleType + "','" + alleleTypes.get(alleleType) + "', 'Number of targeted mutation " + alleleType + " alleles')");
//        }
//
//        // pipeline per center
//        for (String center : centerPipelines.keySet()) {
//            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotype_pipelines_" + center + "','" + generateCommaSeparatedList(centerPipelines.get(center), false) + "', 'Comma separated list of phenotype pipeline for center " + center + "')");
//        }
//
//        /*
//         * This parts compute the number of datapoints
//         * and insert results directly
//         * countDataPointsQuery
//         */
//
//        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('datapoint_types','" + generateCommaSeparatedList(Arrays.asList(dataTypes), false) + "', 'Types for measured data')");
//
//        statement = connection.prepareStatement(countDataPointsQuery);
//        resultSet = statement.executeQuery();
//
//        while (resultSet.next()) {
//
//            int count = resultSet.getInt(1);
//            String observationType = resultSet.getString(2);
//            boolean missing = (resultSet.getInt(3) == 1);
//            String parameterStatus = resultSet.getString(4);
//
//            // we insert directly
//            if (missing) {
//                if (parameterStatus == null) {
////					parameterStatus = "IMPC_PARAMSC_010";
//                    insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_QC_failed_no_status_code','" + count + "', 'Total number of " + observationType + " datapoints that failed QC')");
//                } else if (parameterStatus.equals("IMPC_PARAMSC_010")) {
//                    insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_QC_failed','" + count + "', 'Total number of " + observationType + " datapoints that failed QC')");
//                } else if (parameterStatus.equals("IMPC_PARAMSC_005")) {
//                    insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_issues','" + count + "', 'Total number of missing " + observationType + " datapoints due to issues')");
//                }
//            } else {
//                insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_QC_passed','" + count + "', 'Total number of " + observationType + " datapoints that passed QC')");
//            }
//        }
//
//        resultSet.close();
//        statement.close();
//
//
//        insertStatement.close();
    }


    protected void getAlleleType(String allele) {

        String[] entry = null;

        Matcher matcher = allelePattern.matcher(allele);
        while (matcher.find()) {
            //System.out.print("Start index: " + matcher.start());
            //	System.out.print(" End index: " + matcher.end() + " ("+matcher.group()+") Count:" + matcher.groupCount() + "\n");
            //for (int i=0; i<=matcher.groupCount(); i++) {
            //	String groupStr = matcher.group(i);
            //	logger.debug("DEF_GROUP " + i + " =>" + groupStr);
            //return
            //}
            if (matcher.groupCount() == 3) {
                // it means it's a synonym
                //System.out.println("Allele type\t" + matcher.group(1));
                //System.out.println("Targeting Vector\t" + matcher.group(2));
                String vectorProject = matcher.group(2);

                // Required to fix bad data entry in the iMits system
                // The allele superscript is defined for gene Ctps2 as lowercase "impc" instead of the proper "IMPC"
                if (vectorProject.contains("impc")) {
                    vectorProject = vectorProject.toUpperCase();
                }
                if (!vectorProjects.containsKey(vectorProject)) {
                    vectorProjects.put(vectorProject, 0);
                }
                vectorProjects.put(vectorProject, vectorProjects.get(vectorProject) + 1);

                //System.out.println("Ilar code\t" + matcher.group(3));

                Matcher match_targeted = targetedClass1Pattern.matcher(matcher.group(1));

                if (match_targeted.find()) {

                    String alleleType = null;

                    if (match_targeted.group(2).isEmpty()) {
                        alleleType = match_targeted.group(1);
                    } else {
                        alleleType = match_targeted.group(2);
                    }
                    //System.out.println("Allele class:" + alleleType);

                    if (!alleleTypes.containsKey(alleleType)) {
                        alleleTypes.put(alleleType, 0);
                    }
                    alleleTypes.put(alleleType, alleleTypes.get(alleleType) + 1);
                } else {
                    System.err.println("CANT MATCH!!!!" + allele);

                }
            }
        }

    }


    String generateCommaSeparatedList(List<String> array, boolean withSingleQuotes) {
        return (withSingleQuotes) ? String.join("'", array) :  String.join("\"", array);
    }


}