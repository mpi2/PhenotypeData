package org.mousephenotype.cda.loads.create.releasenotes;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.loads.common.CommandLineUtils;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Populate meta_info table and associated tables to a new datarelease. Must be run at the end of the release process, after the solr cores are built as well.
 * This is a replacement for the one in AdminTools.
 */
@SpringBootApplication
@Import(value = {ReleaseAnalyticsManagerConfig.class})
public class ReleaseAnalyticsManager implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseAnalyticsManager.class);

    private Connection connection;

    //
    // DATA RELEASE CONSTANTS
    //
    private static String DATA_RELEASE_VERSION = "5.0";
    private static String DATA_RELEASE_DATE = "02 August 2016";
    private static String PHENSTAT_VERSION = "2.7.1";
    private static String DATA_RELEASE_DESCRIPTION = "Major data release " + DATA_RELEASE_VERSION + ", released on " + DATA_RELEASE_DATE + ", analysed using PhenStat version " + PHENSTAT_VERSION;

    // Patterns for regular expressions
    public static final String ALLELE_NOMENCLATURE = "^[^<]+<([^\\(]+)\\(([^\\)]+)\\)([^>]+)>";
    public static final String TARGETED_ALLELE_CLASS_1 = "^tm(\\d+)([a-z]{0,1})";

    // Data type contants
    public static final String UNIDIMENSIONAL_DATATYPE = "unidimensional";
    public static final String TIME_SERIES_DATATYPE = "time_series";
    public static final String TEXT_DATATYPE = "text";
    public static final String CATEGORICAL_DATATYPE = "categorical";
    public static final String IMAGE_RECORD_DATATYPE = "image_record";

    public static final String[] dataTypes = new String[]{

            CATEGORICAL_DATATYPE,
            UNIDIMENSIONAL_DATATYPE,
            TIME_SERIES_DATATYPE,
            TEXT_DATATYPE,
            IMAGE_RECORD_DATATYPE

    };

    /**
     * Add all your static facts below. This will be accepted for every release.
     */
    public static String[] metaInfoQueries = new String[]{};

    public static void updateStaticFacts() {

        metaInfoQueries = new String[]{
                "truncate table meta_info;",
                "insert into meta_info(property_key, property_value, description) values('data_release_version', '" + DATA_RELEASE_VERSION + "', '" + DATA_RELEASE_DESCRIPTION + "');",
                "insert into meta_info(property_key, property_value, description) values('data_release_date', '" + DATA_RELEASE_DATE + "', '" + DATA_RELEASE_DATE + "');",
                "insert into meta_info(property_key, property_value, description) values('statistical_packages', 'PhenStat', 'BioConductor Statistical package used to compute genotype to phenotype significant associations. Implements linear mixed model');",
                "insert into meta_info(property_key, property_value, description) values('PhenStat_release_version', '" + PHENSTAT_VERSION + "', 'Version of PhenStat used to analyse phenotype data');",
                "insert into meta_info(property_key, property_value, description) values('PhenStat_repository', 'https://github.com/mpi2/stats_working_group/tree/master/PhenStat', 'GitHub repository for PhenStats code');",
                "insert into meta_info(property_key, property_value, description) values('code_release_version', '1.2.9', 'Code used to build the database and to run the portal');",
                "insert into meta_info(property_key, property_value, description) values('code_repository', 'https://github.com/mpi2/PhenotypeData', 'GitHub repository for the mouse phenotype warehouse and the code to run the portal');",
                "insert into meta_info(property_key, property_value, description) values('ftp_site', 'ftp://ftp.ebi.ac.uk/pub/databases/impc/release-" + DATA_RELEASE_VERSION + "', 'Location of latest release data on FTP site');",
                "insert into meta_info(property_key, property_value, description) values('genome_assembly_version', 'GRCm38', 'Genome Reference Consortium GRCm38');",
                "insert into meta_info(property_key, property_value, description) values('species', 'Mus musculus', 'Mus musculus phenotype database');",
                "insert into meta_info(property_key, property_value, description) values('statistically_significant_threshold', '1x10-4', 'Statistical significance threshold used to define a significant difference from the null hypothesis');",

        };
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

    public static Map<String, String> statisticalMethodsQuery1Map = new HashMap<String, String>();
    public static Map<String, String> statisticalMethodsQuery2Map = new HashMap<String, String>();

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
            "select count(1), observation_type, missing, SUBSTRING_INDEX( `parameter_status` , ':', 1) AS ps, db_id from observation where db_id = 22 and observation_type in (" + generateCommaSeparatedList(dataTypes, true) + ") group by observation_type, db_id, missing, ps order by missing asc;";

    public final String countSpecimenPerCenterQuery =
            "select count(1), bs.sample_group, o.name from biological_sample bs join organisation o on o.id = bs.organisation_id where db_id = 22 group by o.name, bs.sample_group;";

    public final String distinctAlleleByCenterQuery =
            "select distinct a.symbol, gf.acc, gf.symbol, o.name from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join allele a on a.acc = bma.allele_acc left outer join genomic_feature gf on a.gf_acc = gf.acc where bs.sample_group = 'experimental' and bm.db_id = 22 group by bm.id, ls.colony_id order by bm.db_id asc, o.name asc;";

    public final String aggregateLinesQuery =
            "select count(ls.id), o.name, ls.colony_id, bm.zygosity, bm.db_id, bs.sample_group, ls.sex, bm.genetic_background, a.symbol, gf.acc, gf.symbol from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join allele a on a.acc = bma.allele_acc left outer join genomic_feature gf on a.gf_acc = gf.acc where bs.sample_group = 'experimental' and bm.db_id = 22 group by bm.id, ls.colony_id order by bm.db_id asc, o.name asc;";

    public final String countMutantLinesPerCenterQuery =
            "select count(distinct ls.colony_id), o.name from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join observation ob on ob.biological_sample_id = ls.id where bs.db_id = 22 and bs.sample_group = 'experimental' group by o.name";

    public final String countTotalMutantLinesQuery =
            "select count(distinct ls.colony_id) from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join observation ob on ob.biological_sample_id = ls.id where bs.db_id = 22 and bs.sample_group = 'experimental'";

    public final String countTotalGenesQuery =
            "select count(distinct gf.acc) from biological_model bm join biological_model_sample bms on bm.id = bms.biological_model_id join biological_sample bs on bms.biological_sample_id = bs.id join organisation o on bs.organisation_id = o.id join live_sample ls on ls.id = bs.id left outer join biological_model_allele bma on bma.biological_model_id = bm.id join allele a on a.acc = bma.allele_acc left outer join genomic_feature gf on a.gf_acc = gf.acc where bs.sample_group = 'experimental' and bs.db_id = 22";

    public final String countTotalSignificantCallsQuery =
            "select count(1) from phenotype_call_summary where external_db_id = 22;";

    public final String listObservationTypeQuery =
            "select distinct(observation_type) from observation where db_id = 22;";

    public final String listPhenotypePipelinesPerCenter =
            "select distinct p.stable_id, o.name from phenotype_call_summary pcs join organisation o on o.id = pcs.organisation_id join phenotype_pipeline p on p.id = pcs.pipeline_id where external_db_id = 22;";

    public final String listStatisticalMethods =
            "select distinct datatype, statistical_method from analytics_pvalue_distribution;";


    Pattern allelePattern = Pattern.compile(ALLELE_NOMENCLATURE);
    Pattern targetedClass1Pattern = Pattern.compile(TARGETED_ALLELE_CLASS_1);

    HashMap<String, List<String>> alleles          = new HashMap<>();
    Map<String, Integer>          alleleTypes      = new HashMap<>();
    Map<String, Integer>          vectorProjects   = new HashMap<>();
    Map<String, Integer>          phenotypedLines  = new HashMap<>();
    Map<String, Integer>          mutantSpecimens  = new HashMap<>();
    Map<String, Integer>          controlSpecimens = new HashMap<>();
    Map<String, List<String>>     centerPipelines  = new HashMap<>();

    List<String> alleleTypeList        = new ArrayList<>();
    List<String> vectorProjectList     = new ArrayList<>();
    List<String> phenotypingCenterList = new ArrayList<>();


    private GenotypePhenotypeService genotypePhenotypeService;


    @Inject
    public ReleaseAnalyticsManager(
            @NotNull GenotypePhenotypeService genotypePhenotypeService,
            @NotNull DataSource komp2DataSource) throws SQLException
    {
        this.genotypePhenotypeService = genotypePhenotypeService;
        connection = komp2DataSource.getConnection();
    }

    public static void help() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ReleaseAnalyticsManager usage:\n\n");
        buffer.append("\tReleaseAnalyticsManager --dir <filename>\n");
        buffer.append("\t--dr\tData release version\n");
        buffer.append("\t--drdate\tData release date\n");
        System.out.println(buffer);
        System.exit(1);
    }

    @Override
    public void run(String... args) throws Exception {

        OptionParser parser = CommandLineUtils.getOptionParser();

        parser.accepts("dr").withOptionalArg();
        parser.accepts("drdate").withOptionalArg();
        parser.accepts("psversion").withOptionalArg();

        OptionSet options = parser.parse(args);

        boolean redoDataDesc = false;
        if (options.has("dr")) {
            DATA_RELEASE_VERSION = (String) options.valueOf("dr");
            redoDataDesc = true;
        }
        if (options.has("drdate")) {
            DATA_RELEASE_DATE = (String) options.valueOf("drdate");
            redoDataDesc = true;
        }
        if (options.has("psversion")) {
            PHENSTAT_VERSION = (String) options.valueOf("psversion");
            redoDataDesc = true;
        }
        if (redoDataDesc) {
            DATA_RELEASE_DESCRIPTION = "Major data release " + DATA_RELEASE_VERSION + ", released on " + DATA_RELEASE_DATE + ", analysed using PhenStat version " + PHENSTAT_VERSION;
        }

        // Update the facts about this release
        updateStaticFacts();
        populateMetaInfo();
        createAnalyticsTables();
        pValuesDistribution();
        populateMPTerms();
        populateMetaInfoWithMPTerms();
    }



    private void insertMPTerm(
            Statement insertStatement,
            List<GenotypePhenotypeDTO> dataset,
            String mpTermAccField,
            String mpTermNameField,
            String mpTermLevel) throws SQLException {

        for (GenotypePhenotypeDTO map : dataset) {

            List<String> mpIds = new ArrayList<>();
            List<String> mpNames = new ArrayList<>();
            if (mpTermLevel.equalsIgnoreCase("top")) {
                mpIds = map.getTopLevelMpTermId();
                mpNames = map.getTopLevelMpTermName();
            } else if (mpTermLevel.equalsIgnoreCase("intermediate")) {
                mpIds = map.getIntermediateMpTermId();
                mpNames = map.getIntermediateMpTermName();
            } else {
                mpIds.add(map.getMpTermId());
                mpNames.add(map.getMpTermName());
            }

            if (mpNames == null) {
                logger.error("Missing term(s) {} from ontodb -- skipping", (map.getMpTermId() != null) ? map.getMpTermId() : "not found");
                continue;
            }

            for (int i = 0; i < mpNames.size(); i++) {

                StringBuilder values = new StringBuilder();
                values.append("'" + map.getPhenotypingCenter() + "',");
                values.append("'" + map.getMarkerSymbol() + "',");
                values.append("'" + map.getMarkerAccessionId() + "',");
                values.append("'" + map.getColonyId() + "',");
                values.append("'" + mpIds.get(i) + "',");
                values.append("'" + mpNames.get(i) + "',");
                values.append("'" + mpTermLevel + "'");

                if (mpNames.get(i) == null || mpNames.get(i).equalsIgnoreCase("null")) {
                    System.out.println("\n\nNULLL " + map);
                    return;
                }
                System.out.println("\n values: " + values);

                String query = "INSERT INTO analytics_mp_calls(phenotyping_center,marker_symbol,marker_accession_id,colony_id,mp_term_id,mp_term_name,mp_term_level) " +
                        "VALUES(" + values.toString() + ")";
                insertStatement.executeUpdate(query);
            }
        }
    }

    private void populateMPTerms() throws SQLException, IOException, SolrServerException {

        List<GenotypePhenotypeDTO> dataset = null;
        String resource = "IMPC";

        Statement insertStatement = connection.createStatement();


        dataset = genotypePhenotypeService.getAllMPByPhenotypingCenterAndColonies(resource);

        System.out.println("TOP LEVEL");
        String mpTermAccField = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID;
        String mpTermNameField = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME;
        insertMPTerm(insertStatement, dataset, mpTermAccField, mpTermNameField, "top");

        System.out.println("INTERMEDIATE LEVEL");
        mpTermAccField = GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID;
        mpTermNameField = GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_NAME;
        insertMPTerm(insertStatement, dataset, mpTermAccField, mpTermNameField, "intermediate");

        System.out.println("DIRECT ANNOTATIONS");
        mpTermAccField = GenotypePhenotypeDTO.MP_TERM_ID;
        mpTermNameField = GenotypePhenotypeDTO.MP_TERM_NAME;
        insertMPTerm(insertStatement, dataset, mpTermAccField, mpTermNameField, "leaf");


        insertStatement.close();

    }

    private void populateMetaInfoWithMPTerms() throws SQLException {

        Statement insertStatement = connection.createStatement();

        // Once it's done, we want to summarize by top level
        String query = "select distinct phenotyping_center, phenotyping_center_count, mp_term_name, mp_term_count from analytics_mp_calls where mp_term_level = 'top_level';";

        String aggregateCount = "select count(1), mp_term_id from analytics_mp_calls where mp_term_level = 'top' group by mp_term_id;";

        String topLevelsQuery = "select distinct mp_term_id, mp_term_name from analytics_mp_calls where mp_term_level = 'top';";
        List<String> topLevelsList = new ArrayList<String>();

        PreparedStatement statement = connection.prepareStatement(topLevelsQuery);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {

            topLevelsList.add(resultSet.getString(1));
            System.out.println("topLevelsList " + topLevelsList);
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('top_level_" + resultSet.getString(1) + "','" + resultSet.getString(2) + "', 'Mammalian Phenotype top-level category " + resultSet.getString(2) + "')");

        }

        resultSet.close();
        statement.close();

        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('top_level_mps','" + generateCommaSeparatedList(topLevelsList.toArray(), false) + "', 'top-level MP terms')");

        statement = connection.prepareStatement(aggregateCount);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {

            int sum = resultSet.getInt(1);
            String topLevel = resultSet.getString(2);

            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('top_level_" + topLevel + "_calls','" + sum + "', 'Number of associations to top-level MP " + topLevel + "')");

        }

        insertStatement.close();

        resultSet.close();
        statement.close();

    }

    private void setStatisticalMethods() throws SQLException {

        PreparedStatement statement = connection.prepareStatement(listStatisticalMethods);
        ResultSet resultSet = statement.executeQuery();

        Statement insertStatement = connection.createStatement();

        while (resultSet.next()) {

            //insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('statistical_method_"+resultSet.getString(1)+"',\""+resultSet.getString(2)+"\", \"Statistical method used for "+resultSet.getString(1)+" data\")");

        }

        insertStatement.close();

        resultSet.close();
        statement.close();

    }


    public void createAnalyticsTables() throws SQLException {

        executeQueriesFromArray(analyticsTableCreation);

    }

    public void executeQueriesFromArray(String[] array) throws SQLException {

        Statement simpleStatement = connection.createStatement();

        for (int i = 0; i < array.length; i++) {
            System.out.println("--" + array[i]);
            simpleStatement.executeUpdate(array[i]);
        }
        simpleStatement.close();

    }

    public void populateMetaInfo() throws SQLException {

		/*
         * Execute general queries
		 */

        executeQueriesFromArray(metaInfoQueries);


        // number of significant calls
        int significantCalls = executeCountQuery(countTotalSignificantCallsQuery);
        System.out.println("significantCalls:\t" + significantCalls);

        // number of genes
        int totalGenes = executeCountQuery(countTotalGenesQuery);
        System.out.println("Total genes:\t" + totalGenes);

        int totalMutantLines = executeCountQuery(countTotalMutantLinesQuery);
        System.out.println("Total mutants:\t" + totalMutantLines);

        // total mutants per center
        PreparedStatement statement = connection.prepareStatement(countMutantLinesPerCenterQuery);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {

            int count = resultSet.getInt(1);
            String phenotypingCenter = resultSet.getString(2);
            System.out.println("Total mutants for center " + phenotypingCenter + ":\t" + count);
            if (!phenotypedLines.containsKey(phenotypingCenter)) {
                phenotypedLines.put(phenotypingCenter, count);
            }

        }

        resultSet.close();
        statement.close();

		/*
		 * List pipeline per center
		 */

        statement = connection.prepareStatement(listPhenotypePipelinesPerCenter);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {

            String pipeline = resultSet.getString(1);
            String center = resultSet.getString(2);
            List<String> pipelines = null;
            if (!centerPipelines.containsKey(center)) {
                pipelines = new ArrayList<String>();
                centerPipelines.put(center, pipelines);
            } else {
                pipelines = centerPipelines.get(center);
            }
            pipelines.add(pipeline);

        }

        resultSet.close();
        statement.close();

		/*
		 * Targeted allele type
		 * check tm1a, tm1b, etc...
		*/

        statement = connection.prepareStatement(distinctAlleleByCenterQuery);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {

            String allele = resultSet.getString(1);
            System.out.println(allele);
            getAlleleType(allele);

        }

        resultSet.close();
        statement.close();

		/*
		 * This part calculates the number of control and mutant mice per center
		 */
        statement = connection.prepareStatement(countSpecimenPerCenterQuery);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {
            if (resultSet.getString(2).equals("control")) {
                controlSpecimens.put(resultSet.getString(3), resultSet.getInt(1));
            } else {
                mutantSpecimens.put(resultSet.getString(3), resultSet.getInt(1));
            }
        }

        resultSet.close();
        statement.close();

		/*
		 * insert meta data
		 */

        Statement insertStatement = connection.createStatement();

        for (String alleleType : alleleTypes.keySet()) {
            System.out.println(alleleType + "\t" + alleleTypes.get(alleleType));
        }

        for (String vectorProject : vectorProjects.keySet()) {
            System.out.println(vectorProject + "\t" + vectorProjects.get(vectorProject));
        }

        // now write the results to the back-end
        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('statistically_significant_calls'," + significantCalls + ",'Number of statistically significant calls')");
        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_genes'," + totalGenes + ",'Number of distinct genes with complete phenotype data')");
        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_lines'," + totalMutantLines + ",'Number of distinct lines with complete phenotype data')");
        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_lines_centers','" + generateCommaSeparatedList(phenotypedLines.keySet().toArray(), false) + "', 'Comma separated list of centers having submitted complete phenotype data')");
        // center by center
        for (String center : phenotypedLines.keySet()) {
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotyped_lines_" + center + "','" + phenotypedLines.get(center) + "', 'Number of lines with complete phenotype data from " + center + "')");
        }

		/*
		 * Insert nb of specimen, center by center
		 */

        for (String center : phenotypedLines.keySet()) {
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('control_specimens_" + center + "','" + controlSpecimens.get(center) + "', 'Number of control specimens with complete phenotype data from " + center + "')");
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('mutant_specimens_" + center + "','" + mutantSpecimens.get(center) + "', 'Number of mutant specimens with complete phenotype data from " + center + "')");

        }

        // mouse knockout programs
        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('mouse_knockout_programs','" + generateCommaSeparatedList(vectorProjects.keySet().toArray(), false) + "', 'Comma separated list of mouse knockout programs having contributed to IMPC')");
        // program by program
        for (String program : vectorProjects.keySet()) {
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('alleles_" + program + "','" + vectorProjects.get(program) + "', 'Number of " + program + " alleles')");
        }

        // allele type
        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('targeted_allele_types','" + generateCommaSeparatedList(alleleTypes.keySet().toArray(), false) + "', 'Comma separated list of mouse knockout allele types')");
        for (String alleleType : alleleTypes.keySet()) {
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('targeted_allele_type_" + alleleType + "','" + alleleTypes.get(alleleType) + "', 'Number of targeted mutation " + alleleType + " alleles')");
        }

        // pipeline per center
        for (String center : centerPipelines.keySet()) {
            insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('phenotype_pipelines_" + center + "','" + generateCommaSeparatedList(centerPipelines.get(center).toArray(), false) + "', 'Comma separated list of phenotype pipeline for center " + center + "')");
        }

		/*
		 * This parts compute the number of datapoints
		 * and insert results directly
		 * countDataPointsQuery
		 */

        insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('datapoint_types','" + generateCommaSeparatedList(dataTypes, false) + "', 'Types for measured data')");

        statement = connection.prepareStatement(countDataPointsQuery);
        resultSet = statement.executeQuery();

        while (resultSet.next()) {

            int count = resultSet.getInt(1);
            String observationType = resultSet.getString(2);
            boolean missing = (resultSet.getInt(3) == 1);
            String parameterStatus = resultSet.getString(4);

            // we insert directly
            if (missing) {
                if (parameterStatus == null) {
//					parameterStatus = "IMPC_PARAMSC_010";
                    insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_QC_failed_no_status_code','" + count + "', 'Total number of " + observationType + " datapoints that failed QC')");
                } else if (parameterStatus.equals("IMPC_PARAMSC_010")) {
                    insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_QC_failed','" + count + "', 'Total number of " + observationType + " datapoints that failed QC')");
                } else if (parameterStatus.equals("IMPC_PARAMSC_005")) {
                    insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_issues','" + count + "', 'Total number of missing " + observationType + " datapoints due to issues')");
                }
            } else {
                insertStatement.executeUpdate("INSERT INTO meta_info(property_key, property_value, description) VALUES('" + observationType + "_datapoints_QC_passed','" + count + "', 'Total number of " + observationType + " datapoints that passed QC')");
            }
        }

        resultSet.close();
        statement.close();


        insertStatement.close();
    }


    public void pValuesDistribution() throws SQLException {

        Statement insertStatement = connection.createStatement();


        for (int i = 0; i < statisticalMethods.length; i++) {

            String datatype = (statisticalMethods[i].equals("Fisher's exact test")) ? "categorical" : "unidimensional";
            // NamedParameterStatement p = new NamedParameterStatement(connection, query);
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            // works with int if you don't to loose precision (or BigDecimal)
            int min = 0;
            int max = 1;
            double last = min;
            double interval = min;
            double count = 0;
            double bins = 50.0;

            while (interval < max) {

                count++;
                interval = count / bins;

                if (last == 0) {

                    String query = statisticalMethodsQuery1Map.get(statisticalMethods[i]);
                    query = query.replaceAll(":max", interval + "");
                    query = query.replaceAll(":method", "\"" + statisticalMethods[i] + "\"");
                    statement = connection.prepareStatement(query);

                } else {

                    String query = statisticalMethodsQuery2Map.get(statisticalMethods[i]);
                    query = query.replaceAll(":min", last + "");
                    query = query.replaceAll(":max", interval + "");
                    query = query.replaceAll(":method", "\"" + statisticalMethods[i] + "\"");
                    statement = connection.prepareStatement(query);

                }

                resultSet = statement.executeQuery();

                int countPValues = 0;

                while (resultSet.next()) {

                    countPValues += resultSet.getInt(1);

                }

                System.out.println(count + ".\t" + last + "\t" + interval + "\t" + countPValues);
                String query =
                        "insert into analytics_pvalue_distribution (datatype, statistical_method, pvalue_bin, interval_scale, pvalue_count) values ('" + datatype + "', \"" + statisticalMethods[i] + "\"," + interval + "," + (1 / bins) + "," + countPValues + ")";
                System.out.println(query);
                insertStatement.executeUpdate(query);

                resultSet.close();
                statement.close();


                last = interval;

            }
        }
        insertStatement.close();

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

    protected void populateTopLevel() {

        //PhenotypeSummaryBySex getSummaryObjects(String gene);

    }

    String generateCommaSeparatedList(Object[] array, boolean withSingleQuotes) {
        String result = "";
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            String value = (String) ((withSingleQuotes) ? ("'" + array[i] + "'") : array[i]);
            result += (index == 0) ? value : ("," + value);
            index++;
        }
        return result;
    }

    protected int executeCountQuery(String query) throws SQLException {

        int result = 0;

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            result = resultSet.getInt(1);
        }

        statement.close();

        return result;
    }


    public static void main(String args[]) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ReleaseAnalyticsManager.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}