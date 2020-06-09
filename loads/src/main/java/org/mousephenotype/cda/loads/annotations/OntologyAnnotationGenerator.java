package org.mousephenotype.cda.loads.annotations;

import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.statistics.ResultDTO;
import org.mousephenotype.cda.dto.LifeStage;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.utilities.LifeStageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Import(value={OntologyAnnotationGeneratorConfig.class})
public class OntologyAnnotationGenerator implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OntologyAnnotationGenerator.class);

    public static Boolean SAVE_RESULTS = Boolean.TRUE;

    private Map<LifeStage, OntologyTerm> lifeStageOntologyTermMap = new HashMap<>();

    // Concurrent hash map is threadsafe
    private static final Set<String> alreadyReported       = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private              Set<String> sexSpecificParameters = null;
    public               Set<Long>   skipProcedures        = new HashSet<>();

    // Default significance threshold
    public static float SIGNIFICANCE_THRESHOLD = 0.0001f;

    // 0.05 threshold per West, Welch and Galecki (see PhenStat documentation)
    private static float BASE_SIGNIFICANCE_THRESHOLD = 0.05f;
    public static  Float RRPLUS_SIGNIFICANCE         = 0.0001f;

    private DataSource             komp2DataSource;
    private MpTermService          mpTermService;
    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository    parameterRepository;


    private static PreparedStatement insertPhenotypeCallSummaryStatement;
    private static PreparedStatement insertCategoricalStatResultPhenotypeCallSummaryStatement;
    private static PreparedStatement insertUnidimensionalStatResultPhenotypeCallSummaryStatement;
	private static PreparedStatement insertRRPlusStatResultPhenotypeCallSummaryStatement;

	@Inject
	public OntologyAnnotationGenerator(
            @NotNull DataSource komp2DataSource,
	        @NotNull MpTermService mpTermService,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull ParameterRepository parameterRepository)
    {
        this.komp2DataSource = komp2DataSource;
        this.mpTermService = mpTermService;
        this.ontologyTermRepository = ontologyTermRepository;
        this.parameterRepository = parameterRepository;

        lifeStageOntologyTermMap.put(LifeStage.E9_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E9_5.getName(), "IMPC"));
        lifeStageOntologyTermMap.put(LifeStage.E12_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E12_5.getName(), "IMPC"));
        lifeStageOntologyTermMap.put(LifeStage.E15_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E15_5.getName(), "IMPC"));
        lifeStageOntologyTermMap.put(LifeStage.E18_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E18_5.getName(), "IMPC"));
        lifeStageOntologyTermMap.put(LifeStage.EARLY_ADULT, ontologyTermRepository.getByTermNameAndShortName(LifeStage.EARLY_ADULT.getName(), "IMPC"));
        lifeStageOntologyTermMap.put(LifeStage.MIDDLE_AGED_ADULT, ontologyTermRepository.getByTermNameAndShortName(LifeStage.MIDDLE_AGED_ADULT.getName(), "IMPC"));
        lifeStageOntologyTermMap.put(LifeStage.LATE_ADULT, ontologyTermRepository.getByTermNameAndShortName(LifeStage.LATE_ADULT.getName(), "IMPC"));

    }

    void initializeInsertRRPlusStatResultPhenotypeCallSummaryStatement(Connection connection) throws SQLException {
        insertRRPlusStatResultPhenotypeCallSummaryStatement = connection.prepareStatement("INSERT INTO stat_result_phenotype_call_summary(rrplus_result_id, phenotype_call_summary_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
    }

    void initializeInsertUnidimensionalStatResultPhenotypeCallSummaryStatement(Connection connection) throws SQLException {
        insertUnidimensionalStatResultPhenotypeCallSummaryStatement = connection.prepareStatement("INSERT INTO stat_result_phenotype_call_summary(unidimensional_result_id, phenotype_call_summary_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
    }

    void initializeInsertCategoricalStatResultPhenotypeCallSummaryStatement(Connection connection) throws SQLException {
        insertCategoricalStatResultPhenotypeCallSummaryStatement = connection.prepareStatement("INSERT INTO stat_result_phenotype_call_summary(categorical_result_id, phenotype_call_summary_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
    }

    void initializeInsertPhenotypeCallSummaryStatement(Connection connection) throws SQLException {
        insertPhenotypeCallSummaryStatement = connection.prepareStatement("INSERT INTO phenotype_call_summary(external_db_id, project_id, gf_acc, gf_db_id, strain_acc, strain_db_id, allele_acc, allele_db_id, sex, zygosity, parameter_id, procedure_id, pipeline_id, mp_acc, mp_db_id, p_value, effect_size, organisation_id, colony_id, life_stage, life_stage_acc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * When making a phenotype call some vocabulary can get confusing here are
     * some definitions:
     * <p/>
     *
     * <pre>
     * datasource - Who made the call.  The call is always being made by us here, so the data source is always "IMPC"
     * project id - What project produced the data.  Could be EuroPhenome, MGP, IMPC, etc.
     * organisation_id - What center produced the data. Could be WTSI, JAX, etc.
     * strain - background strain of the biological model
     * gene - gene with which this biological model is associated
     * allele - allele with which this biological model is associated
     * sex - the sex of the mutant biological model for which this associate applies
     * zygosity - the zygosity of the mutant biological model for which this associate applies
     * parameter_id - the internal database identifier of the parameter that was measured
     * procedure_id - the internal database identifier of the procedure to which the parameter belongs
     * pipeline_id - the internal database identifier of the pipeline to which the procedure belongs
     * mp term accession - the actual MP term associated to this model because of this mutation
     * p_value - what is the likelihood that this MP term would have been observed due to chance
     * effect_size - strength of the effect over the population tested
     * </pre>
     */
    @Override
    public void run(String... strings) throws SQLException {

        Connection connection = komp2DataSource.getConnection();

        initializeInsertPhenotypeCallSummaryStatement(connection);
        initializeInsertCategoricalStatResultPhenotypeCallSummaryStatement(connection);
        initializeInsertUnidimensionalStatResultPhenotypeCallSummaryStatement(connection);
        initializeInsertRRPlusStatResultPhenotypeCallSummaryStatement(connection);

		deleteData(connection);
        initializeSexSpecificMap(connection);
		generateSkipProceduresSet(connection);

        logger.info("processThreeIStatsResults");
        processThreeIStatsResults(connection, mpTermService);
        logger.info("Done processThreeIStatsResults");

        logger.info("processLineParameters");
		processLineParameters(getLineResults(connection));
        logger.info("Done processLineParameters");

        logger.info("processLineOntologyParameters");
		processLineOntologyParameters(connection);
        logger.info("Done processLineOntologyParameters");

        logger.info("processUnidimensionalParameters");
		processUnidimensionalParameters(connection);
        logger.info("Done processUnidimensionalParameters");

        logger.info("processCategoricalParameters");
		processCategoricalParameters(connection);
        logger.info("Done processCategoricalParameters");

        logger.info("processRRPlusParameters");
		processRRPlusParameters(connection);
        logger.info("Done processRRPlusParameters");

        logger.info("processEmbryonicParameters");
		processEmbryonicParameters(connection);
        logger.info("Done processEmbryonicParameters");

        logger.info("processEmbryonicLineParameters");
		processEmbryonicLineParameters(connection);
        logger.info("Done processEmbryonicLineParameters");

        logger.info("Done");

    }

	/**
	 * Generate the set of integers corresponding to the procedures that we are
	 * to skip while processing
	 *
	 * @throws SQLException
	 */
	private void generateSkipProceduresSet(Connection connection) throws SQLException {

		logger.info("Generate set for skipping procedures");

		// Do not create genotype-phenotype associations for these procedures
		// IMPC_ELZ - Embryonic lacz procedure
		// IMPC_EOL - Embryonic OPT E9.5 procedure
		// IMPC_EMO - Embryonic MicroCT E14.5-E15.5 procedure
		// IMPC_MAA - Embryonic MicroCT E14.5-E15.5 Analysis procedure
		// IMPC_EMA - Embryonic MicroCT E18.5 procedure
		List<String> procedures = Arrays.asList("IMPC_ELZ", "IMPC_EOL", "IMPC_EMO", "IMPC_MAA", "IMPC_EMA");

		String query = "SELECT id FROM phenotype_procedure WHERE stable_id LIKE ?";

		for (String procedure : procedures) {

			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, "%" + procedure + "%");
				ResultSet resultSet = statement.executeQuery();

				while (resultSet.next()) {
					skipProcedures.add(resultSet.getLong("id"));
				}
			}
		}
	}

    // Delete the existing stat results
    private static void deleteData(Connection connection) throws SQLException {

	    logger.info("Deleting data from tables: phenotype_call_summary, stat_result_phenotype_call_summary");
        String query;

        query = "TRUNCATE TABLE phenotype_call_summary";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (SAVE_RESULTS) statement.executeUpdate();
        }

        query = "TRUNCATE TABLE stat_result_phenotype_call_summary";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (SAVE_RESULTS) statement.executeUpdate();
        }

    }

    public void processLineOntologyParameters(Connection connection) throws SQLException {

        for (ResultDTO res : getLineOntologyResults(connection, "mpath")) {

            // Get the MPATH term

            insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
            insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
            insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
            insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
            insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
            insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
            insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
            insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
            insertPhenotypeCallSummaryStatement.setString(9, res.getSex().toString());
            insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().getName());
            insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
            insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
            insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
            insertPhenotypeCallSummaryStatement.setString(14, res.getMpTerm()); // is actually MPATH acc here
            insertPhenotypeCallSummaryStatement.setLong(15, 24); // hard-coded MPATH db id
            insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
            insertPhenotypeCallSummaryStatement.setNull(17, Types.DOUBLE);
            insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
            insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());


            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

        } // end for (ResultDTO res : getLineOntologyResults(connection, "mpath")) {

        for (ResultDTO res : getLineOntologyResults(connection, "emap")) {

            // Get the EMAP term

            insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
            insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
            insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
            insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
            insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
            insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
            insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
            insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());

            insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().getName());

            insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
            insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
            insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
            insertPhenotypeCallSummaryStatement.setString(14, res.getMpTerm()); // is actually EMAP acc here
            insertPhenotypeCallSummaryStatement.setLong(15, 14); // hard-coded EMAP db id

            insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
            insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            insertPhenotypeCallSummaryStatement.setString(9, res.getSex().toString());

            insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
            insertPhenotypeCallSummaryStatement.setNull(17, Types.DOUBLE);

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

        } // end for (ResultDTO res : getLineOntologyResults(connection, "emap")) {

	    for (ResultDTO res : getLineOntologyResults(connection, "mp")) {

		    // Get the Gross Pathology abnormal MP term

		    insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
		    insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
		    insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
		    insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
		    insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
		    insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
		    insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
		    insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());

		    insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().getName());

		    insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
		    insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
		    insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
		    insertPhenotypeCallSummaryStatement.setString(14, res.getMpTerm()); // is actually MP acc here
		    insertPhenotypeCallSummaryStatement.setLong(15, 5); // hard-coded MP db id

		    insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
		    insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

		    insertPhenotypeCallSummaryStatement.setString(9, res.getSex().toString());

		    insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
		    insertPhenotypeCallSummaryStatement.setNull(17, Types.DOUBLE);

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

	    } // end for (ResultDTO res : getLineOntologyResults(connection, "mp")) {


    }

	public void processLineParameters(List<ResultDTO> lineResults) throws SQLException {

		for (ResultDTO res : lineResults) {

			// Get the MP term
			OntologyTerm term = mpTermService.getMPTerm(res.getMpTerm());

            if (term==null) {
                logger.warn("No MP term found for result {}", res);
                continue;
            }

			insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
			insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
			insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
			insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
			insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
			insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
			insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
			insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());

			if(res.getZygosity() != null) {
                logger.debug("For colony ID {}, Viability parameter USING zygosity {}", res.getColonyId(), res.getZygosity());
				insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().getName());
			} else {
                logger.debug("For colony ID {}, Viability parameter would have used zygosity {}, instead using {}", res.getColonyId(), res.getZygosity(), ZygosityType.homozygote);
				insertPhenotypeCallSummaryStatement.setString(10, ZygosityType.homozygote.getName());
			}

            insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
			insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
			insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
			insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
			insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());

			insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
			insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());


            insertPhenotypeCallSummaryStatement.setString(9, "female");
            if (res.getFemalePvalue()!=null) {
                insertPhenotypeCallSummaryStatement.setDouble(16, res.getFemalePvalue());
                insertPhenotypeCallSummaryStatement.setDouble(17, res.getFemaleEffectSize());
	            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();
            }

            insertPhenotypeCallSummaryStatement.setString(9, "male");
            if (res.getMalePvalue() != null) {
                insertPhenotypeCallSummaryStatement.setDouble(16, res.getMalePvalue());
                insertPhenotypeCallSummaryStatement.setDouble(17, res.getMaleEffectSize());
	            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();
            }

		} // end for (ResultDTO res : getLineResults(connection)) {
	}

    private void processThreeIStatsResults(Connection connection, MpTermService mpTermService) throws SQLException {

        PreparedStatement insertStatResultPhenotypeCallSummaryStatement = insertUnidimensionalStatResultPhenotypeCallSummaryStatement;

        for (ResultDTO res : getThreeIResults(connection)) {

            Parameter parameter = parameterRepository.findById(res.getParameterId()).get();

            // Get the MP term directly from the stats result object for 3I calls
            OntologyTerm term = ontologyTermRepository.getByAccAndShortName(res.getMpTerm(), Datasource.MP);

            if (term == null) {
                String msg = "No term could be found to associate for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")";
                if (!alreadyReported.contains(msg)) {
                    alreadyReported.add(msg);
                    logger.warn(msg);
                }
                continue;
            }

            insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
            insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
            insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
            insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
            insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
            insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
            insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
            insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
            insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().name());
            insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
            insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
            insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
            insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
            insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());

            if (res.getNullTestPvalue() != null) {
                // This is a PhenStat result
                insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
            } else {
                // This is NOT a PhenStat result
                insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
            }

            insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
            insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (res.getGenotypeEffectPvalue() == null) {

                Float significanceThreshold = BASE_SIGNIFICANCE_THRESHOLD;

                if (res.getNullTestPvalue() == null) {
                    // This is a wilcoxon result
                    significanceThreshold = SIGNIFICANCE_THRESHOLD;
                }

                // Effects sexes differently. Need MP term for each sex

                // Default is more generic abnormal term, try to get
                // increased/decreased term, but it's ok if it can't find it
                // Simply fall back to the default

                ResultSet rs;
                OntologyTerm female = null;
                OntologyTerm male = null;

                if (res.getFemaleEffectSize() != null && res.getFemalePvalue() < significanceThreshold && (res.getFemaleControls() > 1 && res.getFemaleMutants() > 1)) {
                    female = term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);
                    if (term != null) {
                        insertPhenotypeCallSummaryStatement.setString(9, "female");
                        insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());

                        // If wilcoxon result, null_test_pvalue will be null, so use the sex pvalue
                        // as the global pvalue.  If PhenStat result, then the null_test_pvalue
                        // will not be null and already be set up above.
                        if (res.getNullTestPvalue() == null) {
                            // wilcoxon
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getFemalePvalue());
                        } else {
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                        }

                        insertPhenotypeCallSummaryStatement.setDouble(17, res.getFemaleEffectSize());

                        if (SAVE_RESULTS)
                            insertPhenotypeCallSummaryStatement.executeUpdate();

                        rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                        saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                    }

                }

                if (res.getMaleEffectSize() != null && res.getMalePvalue() < significanceThreshold && (res.getMaleControls() > 1 && res.getMaleMutants() > 1)) {
                    male = term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);
                    if (term != null) {
                        insertPhenotypeCallSummaryStatement.setString(9, "male");
                        insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());

                        // If wilcoxon result, null_test_pvalue will be null, so use the sex pvalue
                        // as the global pvalue.  If PhenStat result, then the null_test_pvalue
                        // will not be null and already be set up above.
                        if (res.getNullTestPvalue() == null) {
                            // wilcoxon
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getMalePvalue());
                        } else {
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                        }

                        insertPhenotypeCallSummaryStatement.setDouble(17, res.getMaleEffectSize());
                        if (SAVE_RESULTS)
                            insertPhenotypeCallSummaryStatement.executeUpdate();

                        rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                        saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                    }
                }


                // In the case where there was a sex effect detected, but there is not enough power
                // to determine direction per sex, we need to fall back to abnormal MP term (if available)
                if (res.getNullTestPvalue() != null && male == null && female == null) {

                    // Cannot classify effect, revert to the abnormal term

                    if (res.getFemaleControls() > 1 && res.getFemaleMutants() > 1) {
                        // set female abnormal term (always return associated value by passing in a 1.0f)
                        term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.female, connection, 1.0f, true);

                        if (term != null) {
                            insertPhenotypeCallSummaryStatement.setString(9, "female");
                            insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                            insertPhenotypeCallSummaryStatement.setDouble(17, res.getFemaleEffectSize());

                            if (SAVE_RESULTS)
                                insertPhenotypeCallSummaryStatement.executeUpdate();

                            rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                            saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                        }
                    }

                    if (res.getMaleControls() > 1 && res.getMaleMutants() > 1) {
                        // set male abnormal term (always return associated value by passing in a 1.0f)
                        term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.male, connection, 1.0f, true);
                        if (term != null) {
                            insertPhenotypeCallSummaryStatement.setString(9, "male");
                            insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                            insertPhenotypeCallSummaryStatement.setDouble(17, res.getMaleEffectSize());

                            if (SAVE_RESULTS)
                                insertPhenotypeCallSummaryStatement.executeUpdate();

                            rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                            saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                        }
                    }
                }

            } else {

                // Sexes differ (either in direction or not enough data for one
                insertPhenotypeCallSummaryStatement.setDouble(17, res.getGenotypeEffectSize());

                // Per Gautier 2013-09024
                // Insert rows for male and female in the phenotype call summary
                // table if male and female are both effected equally AND
                // there is enough data to support the phenotype call (i.e.
                // there is at least 1 data point for both control/mutant
                // for this sex. Only 1 data point is required because PhenStat
                // would not have calculated a significant result with fewer
                // data points

                List<String> sexList = new ArrayList<>();

                // From PhenStat
                // ## There are combinations with less than two data points
                // ## If you have data in one genotype for both sexes
                // ## but not in the other then you have to revert to a
                // ## one sex analysis.

                if (res.getFemaleControls() > 1 && res.getFemaleMutants() > 1) {
                    sexList.add("female");
                }
                if (res.getMaleControls() > 1 && res.getMaleMutants() > 1) {
                    sexList.add("male");
                }

                for (String sex : sexList) {

                    insertPhenotypeCallSummaryStatement.setString(9, sex);
                    if (SAVE_RESULTS)
                        insertPhenotypeCallSummaryStatement.executeUpdate();

                    ResultSet rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                    saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                }
            }
        }
    }


    private void processUnidimensionalParameters(Connection connection) throws SQLException {

        PreparedStatement insertStatResultPhenotypeCallSummaryStatement = insertUnidimensionalStatResultPhenotypeCallSummaryStatement;

        for (ResultDTO res : getUnidimensionalResults(connection)) {

            Parameter parameter = parameterRepository.findById(res.getParameterId()).get();

            // Get the MP term (by sex if no term found based on genotype result
            // effect size)
            OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), res, null, connection, SIGNIFICANCE_THRESHOLD, true);
            if (term == null) {
                term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);
                if (term == null) {
                    term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);
                }
            }

            if (term == null) {
                String msg = "No term could be found to associate for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")";
                if (!alreadyReported.contains(msg)) {
                    alreadyReported.add(msg);
                    logger.warn(msg);
                }
                continue;
            }

            insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
            insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
            insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
            insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
            insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
            insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
            insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
            insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
            insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().name());
            insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
            insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
            insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
            insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
            insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());

            if (res.getNullTestPvalue() != null) {
                // This is a PhenStat result
                insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
            } else {
                // This is NOT a PhenStat result
                insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
            }

            insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
            insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (res.getGenotypeEffectPvalue() == null) {

                Float significanceThreshold = BASE_SIGNIFICANCE_THRESHOLD;

                if (res.getNullTestPvalue() == null) {
                    // This is a wilcoxon result
                    significanceThreshold = SIGNIFICANCE_THRESHOLD;
                }

                // Effects sexes differently. Need MP term for each sex

                // Default is more generic abnormal term, try to get
                // increased/decreased term, but it's ok if it can't find it
                // Simply fall back to the default

                ResultSet rs;
                OntologyTerm female = null;
                OntologyTerm male = null;

                if (res.getFemaleEffectSize() != null && res.getFemalePvalue() < significanceThreshold && (res.getFemaleControls() > 1 && res.getFemaleMutants() > 1)) {
                    female = term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);
                    if (term != null) {
                        insertPhenotypeCallSummaryStatement.setString(9, "female");
                        insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());

                        // If wilcoxon result, null_test_pvalue will be null, so use the sex pvalue
                        // as the global pvalue.  If PhenStat result, then the null_test_pvalue
                        // will not be null and already be set up above.
                        if (res.getNullTestPvalue() == null) {
                            // wilcoxon
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getFemalePvalue());
                        } else {
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                        }

                        insertPhenotypeCallSummaryStatement.setDouble(17, res.getFemaleEffectSize());

                        if (SAVE_RESULTS)
                            insertPhenotypeCallSummaryStatement.executeUpdate();

                        rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                        saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                    }

                }

                if (res.getMaleEffectSize() != null && res.getMalePvalue() < significanceThreshold && (res.getMaleControls() > 1 && res.getMaleMutants() > 1)) {
                    male = term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);
                    if (term != null) {
                        insertPhenotypeCallSummaryStatement.setString(9, "male");
                        insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());

                        // If wilcoxon result, null_test_pvalue will be null, so use the sex pvalue
                        // as the global pvalue.  If PhenStat result, then the null_test_pvalue
                        // will not be null and already be set up above.
                        if (res.getNullTestPvalue() == null) {
                            // wilcoxon
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getMalePvalue());
                        } else {
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                        }

                        insertPhenotypeCallSummaryStatement.setDouble(17, res.getMaleEffectSize());
                        if (SAVE_RESULTS)
                            insertPhenotypeCallSummaryStatement.executeUpdate();

                        rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                        saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                    }
                }


                // In the case where there was a sex effect detected, but there is not enough power
                // to determine direction per sex, we need to fall back to abnormal MP term (if available)
                if (res.getNullTestPvalue() != null && male == null && female == null) {

                    // Cannot classify effect, revert to the abnormal term

                    if (res.getFemaleControls() > 1 && res.getFemaleMutants() > 1) {
                        // set female abnormal term (always return associated value by passing in a 1.0f)
                        term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.female, connection, 1.0f, true);

                        if (term != null) {
                            insertPhenotypeCallSummaryStatement.setString(9, "female");
                            insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                            insertPhenotypeCallSummaryStatement.setDouble(17, res.getFemaleEffectSize());

                            if (SAVE_RESULTS)
                                insertPhenotypeCallSummaryStatement.executeUpdate();

                            rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                            saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                        }
                    }

                    if (res.getMaleControls() > 1 && res.getMaleMutants() > 1) {
                        // set male abnormal term (always return associated value by passing in a 1.0f)
                        term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.male, connection, 1.0f, true);
                        if (term != null) {
                            insertPhenotypeCallSummaryStatement.setString(9, "male");
                            insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                            insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                            insertPhenotypeCallSummaryStatement.setDouble(17, res.getMaleEffectSize());

                            if (SAVE_RESULTS)
                                insertPhenotypeCallSummaryStatement.executeUpdate();

                            rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                            saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
                        }
                    }
                }


            } else {

                // Sexes differ (either in direction or not enough data for one
                insertPhenotypeCallSummaryStatement.setDouble(17, res.getGenotypeEffectSize());

                // Per Gautier 2013-09024
                // Insert rows for male and female in the phenotype call summary
                // table if male and female are both effected equally AND
                // there is enough data to support the phenotype call (i.e.
                // there is at least 1 data point for both control/mutant
                // for this sex. Only 1 data point is required because PhenStat
                // would not have calculated a significant result with fewer
                // data points

                List<String> sexList = new ArrayList<>();

                // From PhenStat
                // ## There are combinations with less than two data points
                // ## If you have data in one genotype for both sexes
                // ## but not in the other then you have to revert to a
                // ## one sex analysis.

                if (res.getFemaleControls() > 1 && res.getFemaleMutants() > 1) {
                    sexList.add("female");
                }
                if (res.getMaleControls() > 1 && res.getMaleMutants() > 1) {
                    sexList.add("male");
                }

                for (String sex : sexList) {

                    insertPhenotypeCallSummaryStatement.setString(9, sex);
                    if (SAVE_RESULTS)
                        insertPhenotypeCallSummaryStatement.executeUpdate();

                    ResultSet rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                    saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);

                }
            }
        }
    }

    private void processCategoricalParameters(Connection connection) throws SQLException {

        for (ResultDTO res : getCategoricalResults(connection)) {
            saveCategoricalResult(connection, res);
        }
    }

    protected void saveCategoricalResult(Connection connection, ResultDTO res) throws SQLException {

        PreparedStatement insertStatResultPhenotypeCallSummaryStatement = insertCategoricalStatResultPhenotypeCallSummaryStatement;

        // Only save the phenotype call if significant
        if (res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD ||
                res.getMalePvalue()!=null && res.getMalePvalue()<=SIGNIFICANCE_THRESHOLD ||
                res.getFemalePvalue()!=null && res.getFemalePvalue()<=SIGNIFICANCE_THRESHOLD
        ) {

            // Effect is significant, find out which term to associate

            Parameter parameter = parameterRepository.findById(res.getParameterId()).get();

            // Get the abnormal term

            insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
            insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
            insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
            insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
            insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
            insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
            insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
            insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
            insertPhenotypeCallSummaryStatement.setString(9, res.getSex().name());
            insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().name());
            insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
            insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
            insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
            insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
            insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            // Set the id of the stats result which is producing the phenotype call
            insertStatResultPhenotypeCallSummaryStatement.setLong(1, res.getResultId());
            try {

                if (
                        ((res.getFemalePvalue() == null || res.getFemalePvalue() >= SIGNIFICANCE_THRESHOLD) &&
                                (res.getMalePvalue() == null || res.getMalePvalue() >= SIGNIFICANCE_THRESHOLD) &&
                                res.getNullTestPvalue() <= SIGNIFICANCE_THRESHOLD) &&
                                ! sexSpecificParameters.contains(res.getParameterStableId())
                ) {
                    OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), res, null, connection, SIGNIFICANCE_THRESHOLD, true);
                    if (term == null) {
                        String msg = "No term could be found to associate for category " + res.getCategoryA() + " for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")";
                        if (!alreadyReported.contains(msg)) {
                            alreadyReported.add(msg);
                            logger.warn(msg);
                        }
                        return;
                    }

                    logger.debug("Assigning term " + term.getId().getAccession() + " for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")");


                    // Individually, not significant, but combined is
                    insertPhenotypeCallSummaryStatement.setString(9, SexType.both.getName());
                    insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                    insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());
                    insertPhenotypeCallSummaryStatement.setDouble(16, res.getNullTestPvalue());
                    insertPhenotypeCallSummaryStatement.setDouble(17, res.getGenotypeEffectSize());
                    if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

                    ResultSet rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                    while (rs.next()) {
                        insertStatResultPhenotypeCallSummaryStatement.setLong(2, rs.getLong(1));
                        if (SAVE_RESULTS) insertStatResultPhenotypeCallSummaryStatement.executeUpdate();
                    }

                } else {

                    // If female p_value is significant, add a female phenotype call
                    if (res.getFemalePvalue() != null && res.getFemalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        res.setSex(SexType.female);
                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.female, connection, SIGNIFICANCE_THRESHOLD, true);
                        if (term == null) {
                            String msg = "No term could be found to associate for category " + res.getCategoryA() + " for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")";
                            if (!alreadyReported.contains(msg)) {
                                alreadyReported.add(msg);
                                logger.warn(msg);
                            }
                            return;
                        }

                        logger.debug("Assigning female specific term " + term.getId().getAccession() + " for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")");

                        insertPhenotypeCallSummaryStatement.setString(9, SexType.female.getName());
                        insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                        insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());
                        insertPhenotypeCallSummaryStatement.setDouble(16, res.getFemalePvalue());
                        insertPhenotypeCallSummaryStatement.setDouble(17, res.getFemaleEffectSize());
                        if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

                        ResultSet rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                        while (rs.next()) {
                            insertStatResultPhenotypeCallSummaryStatement.setLong(2, rs.getLong(1));
                            if (SAVE_RESULTS) insertStatResultPhenotypeCallSummaryStatement.executeUpdate();
                        }
                    }

                    // If male p_value is significant, add a female phenotype call
                    if (res.getMalePvalue() != null && res.getMalePvalue() <= SIGNIFICANCE_THRESHOLD) {

                        res.setSex(SexType.male);
                        OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), res, SexType.male, connection, SIGNIFICANCE_THRESHOLD, true);
                        if (term == null) {
                            String msg = "No term could be found to associate for category " + res.getCategoryA() + " for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")";
                            if (!alreadyReported.contains(msg)) {
                                alreadyReported.add(msg);
                                logger.warn(msg);
                            }
                            return;
                        }

                        logger.debug("Assigning female specific term " + term.getId().getAccession() + " for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")");

                        insertPhenotypeCallSummaryStatement.setString(9, SexType.male.getName());
                        insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
                        insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());
                        insertPhenotypeCallSummaryStatement.setDouble(16, res.getMalePvalue());
                        insertPhenotypeCallSummaryStatement.setDouble(17, res.getMaleEffectSize());
                        if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

                        ResultSet rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
                        while (rs.next()) {
                            insertStatResultPhenotypeCallSummaryStatement.setLong(2, rs.getLong(1));
                            if (SAVE_RESULTS) insertStatResultPhenotypeCallSummaryStatement.executeUpdate();
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("Error processing result " + res, e);
            }
        }
    }

	public void processRRPlusParameters(Connection connection) throws SQLException {

		PreparedStatement insertStatResultPhenotypeCallSummaryStatement = insertRRPlusStatResultPhenotypeCallSummaryStatement;

		for (ResultDTO res : getRRPlusResults(connection)) {

			Parameter parameter = parameterRepository.findById(res.getParameterId()).get();

			// Get the MP term (by sex if no term found based on genotype result
			// effect size)
			OntologyTerm term = mpTermService.getMPTerm(parameter.getStableId(), res, res.getSex(), connection, RRPLUS_SIGNIFICANCE, true);

			if (term == null) {
				String msg = "No term could be found to associate for parameter: " + parameter.getStableId() + " (" + res.getParameterId() + ")";
				if (!alreadyReported.contains(msg)) {
					alreadyReported.add(msg);
					logger.warn(msg);
				}
				continue;
			}

			logger.debug("Adding term {} to RR+ result for parameter {} for gene {} for sex {}", term.getId().getAccession(), parameter.getStableId(), res.getGeneAcc(), res.getSex());

			insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
			insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
			insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
			insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
			insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
			insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
			insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
			insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
			insertPhenotypeCallSummaryStatement.setString(9, res.getSex().name());
			insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().name());
			insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
			insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
			insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
			insertPhenotypeCallSummaryStatement.setString(14, term.getId().getAccession());
			insertPhenotypeCallSummaryStatement.setLong(15, term.getId().getDatabaseId());
			insertPhenotypeCallSummaryStatement.setDouble(16, res.getGenotypeEffectPvalue());
			insertPhenotypeCallSummaryStatement.setDouble(17, res.getGenotypeEffectSize());
			insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
			insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

			ResultSet rs = insertPhenotypeCallSummaryStatement.getGeneratedKeys();
            saveOntologyResultAssociation(insertStatResultPhenotypeCallSummaryStatement, res, rs);
        }
	}

    private void saveOntologyResultAssociation(PreparedStatement insertStatResultPhenotypeCallSummaryStatement, ResultDTO res, ResultSet rs) throws SQLException {
        while (rs.next()) {
            insertStatResultPhenotypeCallSummaryStatement.setLong(1, res.getResultId());
            insertStatResultPhenotypeCallSummaryStatement.setLong(2, rs.getLong(1));
            if (SAVE_RESULTS) insertStatResultPhenotypeCallSummaryStatement.executeUpdate();
        }
    }


    public void processEmbryonicParameters(Connection connection) throws SQLException {

		for (ResultDTO res : getEmbryonicResults(connection)) {

			insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
			insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
			insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
			insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
			insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
			insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
			insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
			insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
			insertPhenotypeCallSummaryStatement.setString(9, res.getSex().getName());
			insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().name());
			insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
			insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
			insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
			insertPhenotypeCallSummaryStatement.setString(14, res.getMpTerm());
			insertPhenotypeCallSummaryStatement.setLong(15, res.getOntologyDbId());

			// This is NOT a statistical result
			insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
			insertPhenotypeCallSummaryStatement.setNull(17, Types.DOUBLE);

			insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
			insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();

		}
	}

	public void processEmbryonicLineParameters(Connection connection) throws SQLException {

		for (ResultDTO res : getEmbryonicLineResults(connection)) {

			insertPhenotypeCallSummaryStatement.setLong(1, res.getDataSourceId());
			insertPhenotypeCallSummaryStatement.setLong(2, res.getProjectId());
			insertPhenotypeCallSummaryStatement.setString(3, res.getGeneAcc());
			insertPhenotypeCallSummaryStatement.setLong(4, res.getGeneDbId());
			insertPhenotypeCallSummaryStatement.setString(5, res.getStrainAcc());
			insertPhenotypeCallSummaryStatement.setLong(6, res.getStrainDbId());
			insertPhenotypeCallSummaryStatement.setString(7, res.getAlleleAcc());
			insertPhenotypeCallSummaryStatement.setLong(8, res.getAlleleDbId());
			insertPhenotypeCallSummaryStatement.setString(9, res.getSex().getName());
			insertPhenotypeCallSummaryStatement.setString(10, res.getZygosity().name());
			insertPhenotypeCallSummaryStatement.setLong(11, res.getParameterId());
			insertPhenotypeCallSummaryStatement.setLong(12, res.getProcedureId());
			insertPhenotypeCallSummaryStatement.setLong(13, res.getPipelineId());
			insertPhenotypeCallSummaryStatement.setString(14, res.getMpTerm());
			insertPhenotypeCallSummaryStatement.setLong(15, res.getOntologyDbId());

			// This is NOT a statistical result
			insertPhenotypeCallSummaryStatement.setNull(16, Types.DOUBLE);
			insertPhenotypeCallSummaryStatement.setNull(17, Types.DOUBLE);

			insertPhenotypeCallSummaryStatement.setLong(18, res.getCenterId());
			insertPhenotypeCallSummaryStatement.setString(19, res.getColonyId());

            LifeStage lifeStage = LifeStageMapper.getLifeStage(res.getParameterStableId());
            insertPhenotypeCallSummaryStatement.setString(20, lifeStageOntologyTermMap.get(lifeStage).getName());
            insertPhenotypeCallSummaryStatement.setString(21, lifeStageOntologyTermMap.get(lifeStage).getId().getAccession());

            if (SAVE_RESULTS) insertPhenotypeCallSummaryStatement.executeUpdate();
		}
	}

	/**
     * THIS IS TO DEAL WITH ONTOLOGY TERMS OTHER THAN MP FOR LOADING INTO phenotype_call_summary table
     *
     * Get the line level parameters with
     * the proper fields required for insertion into the phenotype_call_summary
     * table
     *
     * @param connection
     *            the database connection
     * @return list of DTOs representing a phenotype_call_summary entry
     * @throws SQLException
     */
    public List<ResultDTO> getLineOntologyResults(Connection connection, String ontologyName) throws SQLException {
        List<ResultDTO> statResults = new ArrayList<>();

        String query = "";
        if ( ontologyName.toUpperCase().equals("MPATH") ) {

            //
            // Only add the HISTOPATH MPATH entry if the centre has identified the result as "significant"
            // This is determined by checking the <tissue>_significance parameter for the term being loaded and
            // if the significance term is equal to 0 (false) then skip loading the record
            //

            query = "SELECT distinct e.id AS 'experiment_id', e.db_id, ls.colony_id, e.project_id, e.organisation_id as center_id, ls.sex, " +
                    "  o.parameter_stable_id, o.parameter_id as parameter_id, e.procedure_id as procedure_id, e.pipeline_id as pipeline_id, " +
                    "  bm.zygosity, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bmst.strain_acc, bmst.strain_db_id, " +
                    "  oe.term as ontology_acc, p.name, term_value, bs.external_id, a.stable_id, a.name, a.significant " +
                    "FROM observation o " +
                    "  INNER JOIN experiment_observation eo ON eo.observation_id = o.id " +
                    "  INNER JOIN experiment e ON e.id = eo.experiment_id " +
                    "  INNER JOIN ontology_observation oo ON oo.id = o.id " +
                    "  INNER JOIN ontology_entity oe ON oe.ontology_observation_id = oo.id " +
                    "  INNER JOIN live_sample ls ON ls.id = o.biological_sample_id " +
                    "  INNER JOIN biological_model_sample bms ON bms.biological_sample_id = o.biological_sample_id " +
                    "  INNER JOIN biological_model bm ON bm.id = bms.biological_model_id " +
                    "  INNER JOIN biological_sample bs ON bs.id = bms.biological_model_id " +
                    "  INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id = bms.biological_model_id " +
                    "  INNER JOIN biological_model_strain bmst ON bmst.biological_model_id = bms.biological_model_id " +
                    "  INNER JOIN biological_model_allele bma ON bma.biological_model_id = bms.biological_model_id " +
                    "  INNER JOIN phenotype_parameter p ON p.id = o.parameter_id " +
                    "  INNER JOIN (SELECT distinct pp2.stable_id, pp2.name, o2.sequence_id, co2.category AS significant, e2.id AS experiment_id " +
                    "              FROM observation o2 " +
                    "                INNER JOIN categorical_observation co2 ON co2.id=o2.id " +
                    "                INNER JOIN experiment_observation eo2 ON eo2.observation_id = o2.id " +
                    "                INNER JOIN experiment e2 ON e2.id = eo2.experiment_id " +
                    "                INNER JOIN phenotype_parameter pp2 ON pp2.id=o2.parameter_id " +
                    "              WHERE pp2.name like '%Significance score' AND co2.category = 1) a ON (a.sequence_id = o.sequence_id OR a.sequence_id is null) AND a.experiment_id = e.id AND a.name = CONCAT_WS(' - ', SUBSTRING_INDEX(p.name, ' - ', 1), 'Significance score') " +
                    "WHERE term_value != 'normal' AND term LIKE 'MPATH:%' " ;
        }
        else if ( ontologyName.toUpperCase().equals("EMAP") ){
            query = "SELECT e.id AS result_id, e.db_id, ls.colony_id, e.project_id, e.organisation_id AS center_id, "
                   + "ls.sex, o.parameter_stable_id, o.parameter_id AS parameter_id, e.procedure_id AS procedure_id, "
                   + "e.pipeline_id AS pipeline_id, bm.zygosity, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, "
                   + "bma.allele_db_id, bmst.strain_acc, bmst.strain_db_id, a.ontology_acc "
                   + "FROM observation o INNER JOIN categorical_observation co ON co.id = o.id "
                   + "INNER JOIN biological_model_sample bms ON bms.biological_sample_id = o.biological_sample_id "
                   + "INNER JOIN biological_model bm ON bms.biological_model_id = bm.id "
                   + "INNER JOIN biological_sample bs ON bs.id = bms.biological_model_id "
                   + "INNER JOIN biological_model_genomic_feature bmgf ON bms.biological_model_id = bmgf.biological_model_id "
                   + "INNER JOIN biological_model_strain bmst ON bms.biological_model_id = bmst.biological_model_id "
                   + "INNER JOIN biological_model_allele bma ON bms.biological_model_id = bma.biological_model_id "
                   + "INNER JOIN live_sample ls ON bms.biological_sample_id = ls.id "
                   + "INNER JOIN experiment_observation eo ON eo.observation_id = o.id "
                   + "INNER JOIN experiment e ON e.id = eo.experiment_id "
                   + "INNER JOIN phenotype_parameter_lnk_ontology_annotation lnk ON lnk.parameter_id = o.parameter_id "
                   + "INNER JOIN phenotype_parameter_ontology_annotation a ON a.id = lnk.annotation_id "
                   + "WHERE co.category = 'expression' AND o.parameter_stable_id LIKE 'IMPC_ELZ%' AND a.event_type = 'abnormal' "
                   + "AND a.ontology_acc IS NOT null";
        }
        else if ( ontologyName.toUpperCase().equals("MP") ) {
	        query = "SELECT DISTINCT e.db_id, ls.colony_id, e.project_id, e.organisation_id AS center_id, " +
		        "ls.sex, o.parameter_stable_id, o.parameter_id AS parameter_id, e.procedure_id AS procedure_id, " +
		        "e.pipeline_id AS pipeline_id, bm.zygosity, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, " +
		        "bma.allele_db_id, bmst.strain_acc, bmst.strain_db_id, oe.term AS ontology_acc " +
		        "FROM observation o " +
		        "INNER JOIN ontology_entity oe on oe.ontology_observation_id=o.id " +
		        "INNER JOIN biological_model_sample bms ON bms.biological_sample_id = o.biological_sample_id " +
		        "INNER JOIN biological_model bm ON bms.biological_model_id = bm.id " +
		        "INNER JOIN biological_sample bs ON bs.id = bms.biological_model_id " +
		        "INNER JOIN biological_model_genomic_feature bmgf ON bms.biological_model_id = bmgf.biological_model_id " +
		        "INNER JOIN biological_model_strain bmst ON bms.biological_model_id = bmst.biological_model_id " +
		        "INNER JOIN biological_model_allele bma ON bms.biological_model_id = bma.biological_model_id " +
		        "INNER JOIN live_sample ls ON bms.biological_sample_id = ls.id " +
		        "INNER JOIN experiment_observation eo ON eo.observation_id = o.id " +
		        "INNER JOIN experiment e ON e.id = eo.experiment_id " +
		        "WHERE o.parameter_stable_id like '%PAT%' and term_value not in ( 'normal', 'no abnormal phenotype detected' ) and term like 'MP%' " ;
        }

	    logger.debug("query: "+ query);
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

	            ResultDTO result = new ResultDTO();

                result.setDataSourceId(resultSet.getLong("db_id"));
                result.setColonyId(resultSet.getString("colony_id"));
                result.setProjectId(resultSet.getLong("project_id"));
                result.setCenterId(resultSet.getLong("center_id"));
                result.setPipelineId(resultSet.getLong("pipeline_id"));
                result.setProcedureId(resultSet.getLong("procedure_id"));
                result.setParameterId(resultSet.getLong("parameter_id"));
                result.setParameterStableId(resultSet.getString("parameter_stable_id"));
                result.setGeneAcc(resultSet.getString("gf_acc"));
                result.setGeneDbId(resultSet.getLong("gf_db_id"));
                result.setAlleleAcc(resultSet.getString("allele_acc"));
                result.setAlleleDbId(resultSet.getLong("allele_db_id"));
                result.setStrainAcc(resultSet.getString("strain_acc"));
                result.setStrainDbId(resultSet.getLong("strain_db_id"));
                result.setMpTerm(resultSet.getString("ontology_acc"));
                result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
                result.setSex(SexType.valueOf(resultSet.getString("sex")));

                statResults.add(result);

            }
        }


        logger.info("  Processing " + statResults.size() + " line level results for " + ontologyName.toUpperCase());


        return statResults;
    }


	/**
	 * Get the line level parameters with
	 * the proper fields required for insertion into the phenotype_call_summary
	 * table
	 *
	 * @param connection
	 *            the database connection
	 * @return list of DTOs representing a phenotype_call_summary entry
	 * @throws SQLException
	 */
	public List<ResultDTO> getLineResults(Connection connection) throws SQLException {
		List<ResultDTO> statResults = new ArrayList<>();

		String query = "SELECT e.id as result_id, e.db_id, e.colony_id, e.project_id, e.organisation_id as center_id, o.parameter_stable_id as parameter_stable_id, o.parameter_id as parameter_id, e.procedure_id as procedure_id, e.pipeline_id as pipeline_id, bmm.zygosity, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bms.strain_acc, bms.strain_db_id, co.category, ppoa.ontology_acc " +
            "FROM experiment e " +
            "INNER JOIN experiment_observation eo ON e.id=eo.experiment_id " +
            "INNER JOIN observation o ON eo.observation_id=o.id " +
            "INNER JOIN categorical_observation co ON eo.observation_id=co.id " +
            "INNER JOIN biological_model bmm ON bmm.id=e.biological_model_id " +
			"INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=e.biological_model_id " +
			"INNER JOIN biological_model_strain bms ON bms.biological_model_id=e.biological_model_id " +
			"INNER JOIN biological_model_allele bma ON bma.biological_model_id=e.biological_model_id " +
			"INNER JOIN phenotype_procedure pproc ON pproc.id=e.procedure_id " +
            "INNER JOIN phenotype_parameter pparam ON o.parameter_id=pparam.id " +
            "INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON o.parameter_id=pploa.parameter_id " +
            "INNER JOIN phenotype_parameter_ontology_annotation ppoa ON (pploa.annotation_id=ppoa.id) " +
            "INNER JOIN phenotype_parameter_option ppoption ON ppoa.option_id=ppoption.id " +
            "WHERE e.colony_id IS NOT NULL AND co.category=ppoption.name AND pparam.annotate=1";

		try (PreparedStatement statement = connection.prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				ResultDTO result = new ResultDTO();

				result.setResultId(resultSet.getLong("result_id"));
				result.setDataSourceId(resultSet.getLong("db_id"));
				result.setColonyId(resultSet.getString("colony_id"));
				result.setProjectId(resultSet.getLong("project_id"));
				result.setCenterId(resultSet.getLong("center_id"));
				result.setPipelineId(resultSet.getLong("pipeline_id"));
				result.setProcedureId(resultSet.getLong("procedure_id"));
				result.setParameterId(resultSet.getLong("parameter_id"));
				result.setGeneAcc(resultSet.getString("gf_acc"));
				result.setGeneDbId(resultSet.getLong("gf_db_id"));
				result.setAlleleAcc(resultSet.getString("allele_acc"));
				result.setAlleleDbId(resultSet.getLong("allele_db_id"));
				result.setStrainAcc(resultSet.getString("strain_acc"));
				result.setStrainDbId(resultSet.getLong("strain_db_id"));
				result.setMpTerm(resultSet.getString("ontology_acc"));
                result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));

				final String parameterStableId = resultSet.getString("parameter_stable_id");

				if (parameterStableId.contains("VIA")) {

					Double pValue = 0.0;

					// For the "Outcome" parameter, use the cetner supplied p value
					// if available
					if (parameterStableId.equals("IMPC_VIA_001_001")) {

						String pValueQuery = "SELECT uo.data_point AS data_point " +
							"FROM experiment e " +
							"  INNER JOIN experiment_observation eo ON e.id=eo.experiment_id " +
							"  INNER JOIN observation o ON o.id=eo.observation_id " +
							"  INNER JOIN unidimensional_observation uo ON o.id=uo.id " +
							"WHERE o.parameter_stable_id='IMPC_VIA_032_001' AND e.colony_id=?";

						try (PreparedStatement s = connection.prepareStatement(pValueQuery)) {
							s.setString(1, resultSet.getString("colony_id"));
							ResultSet rs = s.executeQuery();
							while (rs.next()) {
								pValue = rs.getDouble("data_point");
								break;
							}
						}
					}
					result.setMalePvalue(pValue);
					result.setFemalePvalue(pValue);

					result.setMaleEffectSize(1.0);
					result.setFemaleEffectSize(1.0);

				} else if (parameterStableId.contains("FER")) {

					switch (parameterStableId) {

						// males infertile
						case "IMPC_FER_001_001":
							result.setMalePvalue(0.0);
							result.setMaleEffectSize(1.0);
							result.setFemalePvalue(null);
							result.setFemaleEffectSize(null);
							break;

						// females infertile
						case "IMPC_FER_019_001":
							result.setFemalePvalue(0.0);
							result.setFemaleEffectSize(1.0);
							result.setMalePvalue(null);
							result.setMaleEffectSize(null);
							break;
					}

				}
                result.setParameterStableId(parameterStableId);

				statResults.add(result);

			}
		}

		logger.info("  Processing " + statResults.size() + " line level MP ontological term results");

		return statResults;
	}


    /**
     * Get the results of processing the data on unidimensional parameters with
     * the proper fields required for insertion into the phenotype_call_summary
     * table
     *
     * @param connection
     *            the database connection
     * @return list of DTOs representing a phenotype_call_summary entry
     * @throws SQLException
     */
    public List<ResultDTO> getThreeIResults(Connection connection) throws SQLException {
        List<ResultDTO> statResults = new ArrayList<>();

        String query = "SELECT s.id as result_id, s.mp_acc, s.external_db_id, s.colony_id, s.project_id, s.experimental_zygosity as zygosity, s.female_controls, s.female_mutants, s.male_controls, s.male_mutants, s.organisation_id as center_id, s.null_test_significance, s.genotype_effect_pvalue, s.genotype_parameter_estimate, s.gender_female_ko_estimate, s.gender_male_ko_estimate, s.gender_female_ko_pvalue, s.gender_male_ko_pvalue, s.parameter_id as parameter_id, pparam.stable_id as parameter_stable_id, pproc.id as procedure_id, s.pipeline_id as pipeline_id, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bms.strain_acc, bms.strain_db_id"
                + " FROM stats_unidimensional_results s"
                + " INNER JOIN biological_model bmm ON bmm.id=s.experimental_id"
                + " INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=s.experimental_id"
                + " INNER JOIN biological_model_strain bms ON bms.biological_model_id=s.experimental_id"
                + " INNER JOIN biological_model_allele bma ON bma.biological_model_id=s.experimental_id"
                + " INNER JOIN phenotype_procedure pproc ON pproc.id=s.procedure_id"
                + " INNER JOIN phenotype_parameter pparam ON pparam.id=s.parameter_id"
                + " WHERE s.statistical_method = 'Manual'" // These are provided by the 3I consortium
                + " AND s.null_test_significance <= ? " ;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setFloat(1, SIGNIFICANCE_THRESHOLD);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ResultDTO result = new ResultDTO();

                result.setResultId(resultSet.getLong("result_id"));
                result.setDataSourceId(resultSet.getLong("external_db_id"));
                result.setColonyId(resultSet.getString("colony_id"));
                result.setMpTerm(resultSet.getString("mp_acc"));
                result.setProjectId(resultSet.getLong("project_id"));
                result.setCenterId(resultSet.getLong("center_id"));
                result.setPipelineId(resultSet.getLong("pipeline_id"));
                result.setProcedureId(resultSet.getLong("procedure_id"));
                result.setParameterId(resultSet.getLong("parameter_id"));
                result.setParameterStableId(resultSet.getString("parameter_stable_id"));
                result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
                result.setStrainAcc(resultSet.getString("strain_acc"));
                result.setStrainDbId(resultSet.getLong("strain_db_id"));
                result.setGeneAcc(resultSet.getString("gf_acc"));
                result.setGeneDbId(resultSet.getLong("gf_db_id"));
                result.setAlleleAcc(resultSet.getString("allele_acc"));
                result.setAlleleDbId(resultSet.getLong("allele_db_id"));
                result.setMaleControls(resultSet.getInt("male_controls"));
                result.setMaleMutants(resultSet.getInt("male_mutants"));
                result.setFemaleControls(resultSet.getInt("female_controls"));
                result.setFemaleMutants(resultSet.getInt("female_mutants"));

                // The wasNull() check is required because by default, ResultSet
                // will coerce the value to 0 if it's SQL counterpart is null
                // http://stackoverflow.com/questions/2920364/checking-for-a-null-int-value-from-a-java-resultset
                result.setNullTestPvalue(resultSet.getDouble("null_test_significance"));
                if (resultSet.wasNull()) {
                    result.setNullTestPvalue(null);
                }

                result.setGenotypeEffectPvalue(resultSet.getDouble("genotype_effect_pvalue"));
                if (resultSet.wasNull()) {
                    result.setGenotypeEffectPvalue(null);
                }

                result.setGenotypeEffectSize(resultSet.getDouble("genotype_parameter_estimate"));
                if (resultSet.wasNull()) {
                    result.setGenotypeEffectSize(null);
                }

                result.setFemaleEffectSize(resultSet.getDouble("gender_female_ko_estimate"));
                if (resultSet.wasNull()) {
                    result.setFemaleEffectSize(null);
                }

                result.setFemalePvalue(resultSet.getDouble("gender_female_ko_pvalue"));
                if (resultSet.wasNull()) {
                    result.setFemalePvalue(null);
                }

                result.setMaleEffectSize(resultSet.getDouble("gender_male_ko_estimate"));
                if (resultSet.wasNull()) {
                    result.setMaleEffectSize(null);
                }

                result.setMalePvalue(resultSet.getDouble("gender_male_ko_pvalue"));
                if (resultSet.wasNull()) {
                    result.setMalePvalue(null);
                }

                statResults.add(result);
            }
        }

        logger.info("  Processing " + statResults.size() + " Three I results");

        return statResults;
    }



    /**
     * Get the results of processing the data on unidimensional parameters with
     * the proper fields required for insertion into the phenotype_call_summary
     * table
     *
     * @param connection
     *            the database connection
     * @return list of DTOs representing a phenotype_call_summary entry
     * @throws SQLException
     */
    public List<ResultDTO> getUnidimensionalResults(Connection connection) throws SQLException {
        List<ResultDTO> statResults = new ArrayList<>();

        String query = "SELECT s.id as result_id, s.external_db_id, s.colony_id, s.project_id, s.experimental_zygosity as zygosity, s.female_controls, s.female_mutants, s.male_controls, s.male_mutants, s.organisation_id as center_id, s.null_test_significance, s.genotype_effect_pvalue, s.genotype_parameter_estimate, s.gender_female_ko_estimate, s.gender_male_ko_estimate, s.gender_female_ko_pvalue, s.gender_male_ko_pvalue, s.parameter_id as parameter_id, pparam.stable_id as parameter_stable_id, pproc.id as procedure_id, s.pipeline_id as pipeline_id, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bms.strain_acc, bms.strain_db_id"
            + " FROM stats_unidimensional_results s"
            + " INNER JOIN biological_model bmm ON bmm.id=s.experimental_id"
            + " INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model_strain bms ON bms.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model_allele bma ON bma.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model bmc ON bmc.id=s.control_id"
            + " INNER JOIN phenotype_procedure pproc ON pproc.id=s.procedure_id"
            + " INNER JOIN phenotype_parameter pparam ON pparam.id=s.parameter_id"
            + " WHERE s.statistical_method != 'Reference Ranges Plus framework'" // These are handled seperately
	        + " AND EXISTS (SELECT * from phenotype_parameter_lnk_ontology_annotation pont WHERE parameter_id = s.parameter_id)" // Filter in results that can generate MP terms
            + " AND pparam.annotate=1 AND (s.null_test_significance <= ? "
            + " OR (s.null_test_significance is null AND (gender_female_ko_pvalue <= ? OR gender_male_ko_pvalue <= ?)))"; // pickup the wilcox terms as well

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setFloat(1, SIGNIFICANCE_THRESHOLD);
            statement.setFloat(2, SIGNIFICANCE_THRESHOLD);
            statement.setFloat(3, SIGNIFICANCE_THRESHOLD);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ResultDTO result = new ResultDTO();

                result.setResultId(resultSet.getLong("result_id"));
                result.setDataSourceId(resultSet.getLong("external_db_id"));
                result.setColonyId(resultSet.getString("colony_id"));
                result.setProjectId(resultSet.getLong("project_id"));
                result.setCenterId(resultSet.getLong("center_id"));
                result.setPipelineId(resultSet.getLong("pipeline_id"));
                result.setProcedureId(resultSet.getLong("procedure_id"));
                result.setParameterId(resultSet.getLong("parameter_id"));
                result.setParameterStableId(resultSet.getString("parameter_stable_id"));
                result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
                result.setStrainAcc(resultSet.getString("strain_acc"));
                result.setStrainDbId(resultSet.getLong("strain_db_id"));
                result.setGeneAcc(resultSet.getString("gf_acc"));
                result.setGeneDbId(resultSet.getLong("gf_db_id"));
                result.setAlleleAcc(resultSet.getString("allele_acc"));
                result.setAlleleDbId(resultSet.getLong("allele_db_id"));
                result.setMaleControls(resultSet.getInt("male_controls"));
                result.setMaleMutants(resultSet.getInt("male_mutants"));
                result.setFemaleControls(resultSet.getInt("female_controls"));
                result.setFemaleMutants(resultSet.getInt("female_mutants"));

                // The wasNull() check is required because by default, ResultSet
                // will coerce the value to 0 if it's SQL counterpart is null
                // http://stackoverflow.com/questions/2920364/checking-for-a-null-int-value-from-a-java-resultset
                result.setNullTestPvalue(resultSet.getDouble("null_test_significance"));
                if (resultSet.wasNull()) {
                    result.setNullTestPvalue(null);
                }

                result.setGenotypeEffectPvalue(resultSet.getDouble("genotype_effect_pvalue"));
                if (resultSet.wasNull()) {
                    result.setGenotypeEffectPvalue(null);
                }

                result.setGenotypeEffectSize(resultSet.getDouble("genotype_parameter_estimate"));
                if (resultSet.wasNull()) {
                    result.setGenotypeEffectSize(null);
                }

                result.setFemaleEffectSize(resultSet.getDouble("gender_female_ko_estimate"));
                if (resultSet.wasNull()) {
                    result.setFemaleEffectSize(null);
                }

                result.setFemalePvalue(resultSet.getDouble("gender_female_ko_pvalue"));
                if (resultSet.wasNull()) {
                    result.setFemalePvalue(null);
                }

                result.setMaleEffectSize(resultSet.getDouble("gender_male_ko_estimate"));
                if (resultSet.wasNull()) {
                    result.setMaleEffectSize(null);
                }

                result.setMalePvalue(resultSet.getDouble("gender_male_ko_pvalue"));
                if (resultSet.wasNull()) {
                    result.setMalePvalue(null);
                }

                statResults.add(result);
            }
        }

        logger.info("  Processing " + statResults.size() + " unidimensional results");

        return statResults;
    }

    /**
     * Get the results of processing the data on categorical parameters with the
     * proper fields required for insertion into the phenotype_call_summary
     * table
     *
     * @param connection
     *            the database connection
     * @return list of DTOs representing a phenotype_call_summary entry
     * @throws SQLException
     */
    public List<ResultDTO> getCategoricalResults(Connection connection) throws SQLException {
        List<ResultDTO> statResults = new ArrayList<>();

        String query = "SELECT s.id as result_id, s.external_db_id, s.colony_id, s.project_id, s.category_a, s.category_b, s.experimental_zygosity as zygosity, s.female_controls, s.female_mutants, s.male_controls, s.male_mutants, s.organisation_id as center_id, s.experimental_sex as sex, s.p_value, s.effect_size, s.male_p_value, s.male_effect_size, s.female_p_value, s.female_effect_size, s.parameter_id as parameter_id, pparam.stable_id as parameter_stable_id, pproc.id as procedure_id, s.pipeline_id as pipeline_id, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bms.strain_acc, bms.strain_db_id"
            + " FROM stats_categorical_results s"
            + " INNER JOIN biological_model bmm ON bmm.id=s.experimental_id"
            + " INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model_strain bms ON bms.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model_allele bma ON bma.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model bmc ON bmc.id=s.control_id"
            + " INNER JOIN phenotype_procedure pproc ON pproc.id=s.procedure_id"
            + " INNER JOIN phenotype_parameter pparam ON pparam.id=s.parameter_id"
            + " WHERE EXISTS (SELECT * from phenotype_parameter_lnk_ontology_annotation pont WHERE parameter_id = s.parameter_id)" // Filter in results that can generate MP terms
            + " AND (s.p_value <= ? OR s.female_p_value <= ? OR s.male_p_value <= ?) AND pparam.annotate=1";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setFloat(1, SIGNIFICANCE_THRESHOLD);
            statement.setFloat(2, SIGNIFICANCE_THRESHOLD);
            statement.setFloat(3, SIGNIFICANCE_THRESHOLD);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ResultDTO result = new ResultDTO();

                result.setResultId(resultSet.getLong("result_id"));
                result.setDataSourceId(resultSet.getLong("external_db_id"));
                result.setColonyId(resultSet.getString("colony_id"));
                result.setProjectId(resultSet.getLong("project_id"));
                result.setCenterId(resultSet.getLong("center_id"));
                result.setPipelineId(resultSet.getLong("pipeline_id"));
                result.setProcedureId(resultSet.getLong("procedure_id"));
                result.setParameterId(resultSet.getLong("parameter_id"));
                result.setParameterStableId(resultSet.getString("parameter_stable_id"));
                result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
                result.setStrainAcc(resultSet.getString("strain_acc"));
                result.setStrainDbId(resultSet.getLong("strain_db_id"));
                result.setGeneAcc(resultSet.getString("gf_acc"));
                result.setGeneDbId(resultSet.getLong("gf_db_id"));
                result.setAlleleAcc(resultSet.getString("allele_acc"));
                result.setAlleleDbId(resultSet.getLong("allele_db_id"));
                result.setSex(SexType.valueOf(resultSet.getString("sex")));

                result.setNullTestPvalue(resultSet.getDouble("p_value"));
                result.setGenotypeEffectSize(resultSet.getDouble("effect_size"));

                result.setMalePvalue(resultSet.getDouble("male_p_value"));
                result.setMaleEffectSize(resultSet.getDouble("male_effect_size"));

                result.setFemalePvalue(resultSet.getDouble("female_p_value"));
                result.setFemaleEffectSize(resultSet.getDouble("female_effect_size"));

                result.setCategoryA(resultSet.getString("category_a"));
                result.setCategoryB(resultSet.getString("category_b"));
                result.setMaleControls(resultSet.getInt("male_controls"));
                result.setMaleMutants(resultSet.getInt("male_mutants"));
                result.setFemaleControls(resultSet.getInt("female_controls"));
                result.setFemaleMutants(resultSet.getInt("female_mutants"));

                if (result.getCategoryA() == null) {
                    result.setCategoryA("normal");
                }

                statResults.add(result);
            }
        }

        logger.info("  Processing " + statResults.size() + " categorical results");

        return statResults;
    }

	/**
	 * Get the results of processing the data on unidimensional parameters with the reference range plus method
	 * the proper fields required for insertion into the phenotype_call_summary
	 * table
	 *
	 * @param connection the database connection
	 * @return list of DTOs representing a phenotype_call_summary entry
	 * @throws SQLException
	 */
	public List<ResultDTO> getRRPlusResults(Connection connection) throws SQLException {

		List<ResultDTO> statResults = new ArrayList<>();

		String query = "SELECT * FROM (SELECT s.id AS result_id, s.external_db_id, s.colony_id, s.project_id, s.experimental_zygosity AS zygosity, " +
			" gf.name AS marker_symbol, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bms.strain_acc, bms.strain_db_id, " +
			" s.female_controls, s.female_mutants, s.male_controls, s.male_mutants, s.classification_tag, " +
			" s.organisation_id AS center_id, pparam.name, pparam.stable_id as parameter_stable_id, s.parameter_id AS parameter_id, pproc.id AS procedure_id, s.pipeline_id AS pipeline_id, " +
			" CASE WHEN s.genotype_effect_pvalue = 'NA' THEN NULL WHEN s.genotype_effect_pvalue != 'NA'       THEN CAST(                       SUBSTRING_INDEX(s.genotype_effect_pvalue, ',', 1)      AS DECIMAL(8,7))     END AS decreased_genotype_pvalue, " +
			" CASE WHEN s.genotype_effect_pvalue = 'NA' THEN NULL WHEN s.genotype_effect_pvalue != 'NA'       THEN CAST(                       SUBSTRING_INDEX(s.genotype_effect_pvalue, ',', -1)     AS DECIMAL(8,7))     END AS increased_genotype_pvalue, " +
			" CASE WHEN s.gender_female_ko_pvalue = 'NA' THEN NULL WHEN s.gender_female_ko_pvalue != 'NA'     THEN CAST(                       SUBSTRING_INDEX(s.gender_female_ko_pvalue, ',', 1)     AS DECIMAL(8,7))     END AS decreased_female_genotype_pvalue, " +
			" CASE WHEN s.gender_female_ko_pvalue = 'NA' THEN NULL WHEN s.gender_female_ko_pvalue != 'NA'     THEN CAST(                       SUBSTRING_INDEX(s.gender_female_ko_pvalue, ',', -1)    AS DECIMAL(8,7))     END AS increased_female_genotype_pvalue, " +
			" CASE WHEN s.gender_male_ko_pvalue = 'NA' THEN NULL WHEN s.gender_male_ko_pvalue != 'NA'         THEN CAST(                       SUBSTRING_INDEX(s.gender_male_ko_pvalue, ',', 1)       AS DECIMAL(8,7))     END AS decreased_male_genotype_pvalue, " +
			" CASE WHEN s.gender_male_ko_pvalue = 'NA' THEN NULL WHEN s.gender_male_ko_pvalue != 'NA'         THEN CAST(                       SUBSTRING_INDEX(s.gender_male_ko_pvalue, ',', -1)      AS DECIMAL(8,7))     END AS increased_male_genotype_pvalue, " +
			" CASE WHEN s.genotype_parameter_estimate = 'NA' THEN NULL WHEN s.genotype_parameter_estimate != 'NA' THEN CAST(TRIM(TRAILING '%' FROM SUBSTRING_INDEX(s.genotype_parameter_estimate, ',', 1))  AS DECIMAL(8,7))/100 END AS decreased_genotype_estimate, " +
			" CASE WHEN s.genotype_parameter_estimate = 'NA' THEN NULL WHEN s.genotype_parameter_estimate != 'NA' THEN CAST(TRIM(TRAILING '%' FROM SUBSTRING_INDEX(s.genotype_parameter_estimate, ',', -1)) AS DECIMAL(8,7))/100 END AS increased_genotype_estimate, " +
			" CASE WHEN s.gender_female_ko_estimate = 'NA' THEN NULL WHEN s.gender_female_ko_estimate != 'NA' THEN CAST(TRIM(TRAILING '%' FROM SUBSTRING_INDEX(s.gender_female_ko_estimate, ',', 1))  AS DECIMAL(8,7))/100 END AS decreased_female_genotype_estimate, " +
			" CASE WHEN s.gender_female_ko_estimate = 'NA' THEN NULL WHEN s.gender_female_ko_estimate != 'NA' THEN CAST(TRIM(TRAILING '%' FROM SUBSTRING_INDEX(s.gender_female_ko_estimate, ',', -1)) AS DECIMAL(8,7))/100 END AS increased_female_genotype_estimate, " +
			" CASE WHEN s.gender_male_ko_estimate = 'NA' THEN NULL WHEN s.gender_male_ko_estimate != 'NA'     THEN CAST(TRIM(TRAILING '%' FROM SUBSTRING_INDEX(s.gender_male_ko_estimate, ',', 1))    AS DECIMAL(8,7))/100 END AS decreased_male_genotype_estimate, " +
			" CASE WHEN s.gender_male_ko_estimate = 'NA' THEN NULL WHEN s.gender_male_ko_estimate != 'NA'     THEN CAST(TRIM(TRAILING '%' FROM SUBSTRING_INDEX(s.gender_male_ko_estimate, ',', -1))   AS DECIMAL(8,7))/100 END AS increased_male_genotype_estimate " +
			"FROM stats_rrplus_results s " +
			"INNER JOIN biological_model bmm ON bmm.id=s.experimental_id " +
			"INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=s.experimental_id " +
			"INNER JOIN biological_model_strain bms ON bms.biological_model_id=s.experimental_id " +
			"INNER JOIN biological_model_allele bma ON bma.biological_model_id=s.experimental_id " +
			"INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc " +
			"INNER JOIN biological_model bmc ON bmc.id=s.control_id " +
			"INNER JOIN phenotype_procedure pproc ON pproc.id=s.procedure_id " +
			"INNER JOIN phenotype_parameter pparam ON pparam.id=s.parameter_id) a " +
			"WHERE decreased_genotype_pvalue < ? OR " +
			"      increased_genotype_pvalue < ? OR " +
			"      decreased_female_genotype_pvalue < ? OR " +
			"      increased_female_genotype_pvalue < ? OR " +
			"      decreased_male_genotype_pvalue < ? OR " +
			"      increased_male_genotype_pvalue < ? " ;

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setFloat(1, RRPLUS_SIGNIFICANCE);
			statement.setFloat(2, RRPLUS_SIGNIFICANCE);
			statement.setFloat(3, RRPLUS_SIGNIFICANCE);
			statement.setFloat(4, RRPLUS_SIGNIFICANCE);
			statement.setFloat(5, RRPLUS_SIGNIFICANCE);
			statement.setFloat(6, RRPLUS_SIGNIFICANCE);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				Boolean perSexResults = Boolean.FALSE;

				// Skip parameters we are not interested in
				if (skipProcedures.contains(resultSet.getLong("procedure_id"))) {
					continue;
				}

				if (!perSexResults) {
					for (String combo : Arrays.asList("decreased_", "increased_")) {

						Integer sign = (combo.equals("decreased_")) ? -1 : 1;

						ResultDTO result = new ResultDTO();

						result.setResultId(resultSet.getLong("result_id"));
						result.setDataSourceId(resultSet.getLong("external_db_id"));
						result.setColonyId(resultSet.getString("colony_id"));
						result.setProjectId(resultSet.getLong("project_id"));
						result.setCenterId(resultSet.getLong("center_id"));
						result.setPipelineId(resultSet.getLong("pipeline_id"));
						result.setProcedureId(resultSet.getLong("procedure_id"));
						result.setParameterId(resultSet.getLong("parameter_id"));
						result.setParameterStableId(resultSet.getString("parameter_stable_id"));
						result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
						result.setStrainAcc(resultSet.getString("strain_acc"));
						result.setStrainDbId(resultSet.getLong("strain_db_id"));
						result.setGeneAcc(resultSet.getString("gf_acc"));
						result.setGeneDbId(resultSet.getLong("gf_db_id"));
						result.setAlleleAcc(resultSet.getString("allele_acc"));
						result.setAlleleDbId(resultSet.getLong("allele_db_id"));
						result.setMaleControls(resultSet.getInt("male_controls"));
						result.setMaleMutants(resultSet.getInt("male_mutants"));
						result.setFemaleControls(resultSet.getInt("female_controls"));
						result.setFemaleMutants(resultSet.getInt("female_mutants"));

						Double pvalue = resultSet.getDouble(combo + "genotype_pvalue");
						if (resultSet.wasNull()) {
							pvalue = null;
						}
						if (pvalue!=null && pvalue < RRPLUS_SIGNIFICANCE) {

							result.setGenotypeEffectSize(resultSet.getDouble(combo + "genotype_estimate") * sign);
							if (resultSet.wasNull()) {
								result.setGenotypeEffectSize(null);
							}

							result.setGenotypeEffectPvalue(resultSet.getDouble(combo + "genotype_pvalue"));
							if (resultSet.wasNull()) {
								result.setGenotypeEffectPvalue(null);
							}

							result.setSex(SexType.both);
							statResults.add(result);
						}

					}

				}
			}
		}

		logger.info("  Would add " + statResults.size() + " Reference Range Plus results at significance threshold "+RRPLUS_SIGNIFICANCE);

		return statResults;
	}




    /**
	 * Get the results of the embryonic data with the proper fields required for insertion into the
	 * phenotype_call_summary table
	 *
	 * @param connection the database connection
	 * @return list of DTOs representing a phenotype_call_summary entry
	 * @throws SQLException when db error occurs
	 */
	public List<ResultDTO> getEmbryonicResults(Connection connection) throws SQLException {

		List<ResultDTO> statResults = new ArrayList<>();

		//
		// 2016-02-25 Per Henrik W. @ DCC
		// Make associations when there are 2 or more abnormals in the group of embryos tested
		//
		String query = "SELECT DISTINCT b.cn, e.db_id, ls.colony_id, ls.sex, e.project_id, e.organisation_id AS center_id, " +
			"  o.parameter_stable_id, o.parameter_id AS parameter_id, e.procedure_id AS procedure_id, e.pipeline_id AS pipeline_id, " +
			"  bm.zygosity, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bmstrain.strain_acc, bmstrain.strain_db_id, " +
			"  oa.ontology_acc, oa.ontology_db_id " +
			"FROM observation o " +
			"  INNER JOIN categorical_observation co ON co.id = o.id " +
			"  INNER JOIN experiment_observation eo ON eo.observation_id = o.id " +
			"  INNER JOIN experiment e ON eo.experiment_id = e.id " +
			"  INNER JOIN live_sample ls ON ls.id = o.biological_sample_id " +
			"  INNER JOIN biological_model_sample bms ON bms.biological_sample_id = o.biological_sample_id " +
			"  INNER JOIN biological_model bm ON bm.id = bms.biological_model_id " +
			"  INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id = bms.biological_model_id " +
			"  INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id = bms.biological_model_id " +
			"  INNER JOIN biological_model_allele bma ON bma.biological_model_id = bms.biological_model_id " +
			"  INNER JOIN genomic_feature gf ON gf.acc = bmgf.gf_acc " +
			"  INNER JOIN phenotype_procedure pproc ON pproc.id = e.procedure_id " +
			"  INNER JOIN phenotype_parameter pparam ON pparam.id = o.parameter_id " +
			"  INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON pparam.id=l.parameter_id " +
			"  INNER JOIN phenotype_parameter_ontology_annotation oa ON l.annotation_id=oa.id " +
			"  INNER JOIN (SELECT COUNT(co.category) AS cn, ls.zygosity, ls.colony_id, o.parameter_stable_id FROM observation o " +
			"    INNER JOIN categorical_observation co ON co.id=o.id " +
			"    INNER JOIN live_sample ls ON (ls.id=o.biological_sample_id AND ls.zygosity IS NOT NULL) " +
			"  WHERE category IN ('yes', 'abnormal') " +
			"        AND o.parameter_stable_id REGEXP 'IMPC_GPL*|IMPC_GEL*|IMPC_GPM*|IMPC_GEM*|IMPC_GPO*|IMPC_GEO*|IMPC_GPP*|IMPC_GEP*' " +
			"  GROUP BY ls.zygosity, ls.colony_id, o.parameter_stable_id " +
			"  HAVING COUNT(co.category) > 1) AS b ON (b.colony_id=ls.colony_id AND b.parameter_stable_id=o.parameter_stable_id AND b.zygosity=ls.zygosity) " +
			"WHERE co.category IN ('yes', 'abnormal') AND pparam.annotate = 1 AND " +
			"  e.procedure_id IN (SELECT id FROM phenotype_procedure WHERE stable_id REGEXP 'IMPC_GPL|IMPC_GEL|IMPC_GPM|IMPC_GEM|IMPC_GPO|IMPC_GEO|IMPC_GPP|IMPC_GEP') ";

		try (PreparedStatement statement = connection.prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				// Skip parameters we are not interested in
				if (skipProcedures.contains(resultSet.getLong("procedure_id"))) {
					continue;
				}

				if ((resultSet.getString("parameter_stable_id").startsWith("IMPC_GEP_004") || resultSet.getString("parameter_stable_id").startsWith("IMPC_GEP_005")) && resultSet.getLong("center_id")==6) {
					// Skipping JAX parameters Spontaneous movement IMPC_GEP_004_002 and Responsive to tactile stimuli IMPC_GEP_005_002 for now
					continue;
				}


				ResultDTO result = new ResultDTO();

				result.setDataSourceId(resultSet.getLong("db_id"));
				result.setColonyId(resultSet.getString("colony_id"));
				result.setSex(SexType.valueOf(resultSet.getString("sex")));
				result.setProjectId(resultSet.getLong("project_id"));
				result.setCenterId(resultSet.getLong("center_id"));
				result.setPipelineId(resultSet.getLong("pipeline_id"));
				result.setProcedureId(resultSet.getLong("procedure_id"));
				result.setParameterId(resultSet.getLong("parameter_id"));
				result.setParameterStableId(resultSet.getString("parameter_stable_id"));
				result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
				result.setStrainAcc(resultSet.getString("strain_acc"));
				result.setStrainDbId(resultSet.getLong("strain_db_id"));
				result.setGeneAcc(resultSet.getString("gf_acc"));
				result.setGeneDbId(resultSet.getLong("gf_db_id"));
				result.setAlleleAcc(resultSet.getString("allele_acc"));
				result.setAlleleDbId(resultSet.getLong("allele_db_id"));
				result.setMpTerm(resultSet.getString("ontology_acc"));
				result.setOntologyDbId(resultSet.getLong("ontology_db_id"));

				statResults.add(result);

			}
		}

		logger.info("  Adding " + statResults.size() + " Embryonic MP results");

		return statResults;
	}

	/**
	 * Get the results of the embryonic line level data with the proper fields required for insertion into the
	 * phenotype_call_summary table
	 *
	 * @param connection the database connection
	 * @return list of DTOs representing a phenotype_call_summary entry
	 * @throws SQLException when db error occurs
	 */
	public List<ResultDTO> getEmbryonicLineResults(Connection connection) throws SQLException {

		List<ResultDTO> statResults = new ArrayList<>();

		String query = "SELECT e.id AS result_id, e.db_id, e.colony_id, 'no data', e.project_id, e.organisation_id AS center_id, " +
			"  o.parameter_stable_id, o.parameter_id AS parameter_id, e.procedure_id AS procedure_id, e.pipeline_id AS pipeline_id, " +
			"  bm.zygosity, bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bmstrain.strain_acc, bmstrain.strain_db_id, " +
			"  oa.ontology_acc, oa.ontology_db_id, co.category " +
			"FROM observation o " +
			"  INNER JOIN categorical_observation co ON co.id = o.id " +
			"  INNER JOIN experiment_observation eo ON eo.observation_id = o.id " +
			"  INNER JOIN experiment e ON eo.experiment_id = e.id " +
			"  INNER JOIN biological_model bm ON bm.id = e.biological_model_id " +
			"  INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id = e.biological_model_id " +
			"  INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id = e.biological_model_id " +
			"  INNER JOIN biological_model_allele bma ON bma.biological_model_id = e.biological_model_id " +
			"  INNER JOIN genomic_feature gf ON gf.acc = bmgf.gf_acc " +
			"  INNER JOIN phenotype_procedure pproc ON pproc.id = e.procedure_id " +
			"  INNER JOIN phenotype_parameter pparam ON pparam.id = o.parameter_id " +
			"  INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON pparam.id = l.parameter_id " +
			"  INNER JOIN phenotype_parameter_ontology_annotation oa ON l.annotation_id = oa.id " +
			"WHERE o.parameter_stable_id IN ('IMPC_EVL_001_001', 'IMPC_EVM_001_001', 'IMPC_EVO_001_001', 'IMPC_EVP_001_001') " +
			"      AND co.category IN ('Homozygous - Lethal', 'Homozygous - Subviable', 'Hemizygous - Lethal') " ;

		try (PreparedStatement statement = connection.prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				// Skip parameters we are not interested in
				if (skipProcedures.contains(resultSet.getLong("procedure_id"))) {
					continue;
				}

				ResultDTO result = new ResultDTO();

				result.setResultId(resultSet.getLong("result_id"));
				result.setDataSourceId(resultSet.getLong("db_id"));
				result.setColonyId(resultSet.getString("colony_id"));
				result.setSex(SexType.both);
				result.setProjectId(resultSet.getLong("project_id"));
				result.setCenterId(resultSet.getLong("center_id"));
				result.setPipelineId(resultSet.getLong("pipeline_id"));
				result.setProcedureId(resultSet.getLong("procedure_id"));
				result.setParameterId(resultSet.getLong("parameter_id"));
                result.setParameterStableId(resultSet.getString("parameter_stable_id"));
				result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
				result.setStrainAcc(resultSet.getString("strain_acc"));
				result.setStrainDbId(resultSet.getLong("strain_db_id"));
				result.setGeneAcc(resultSet.getString("gf_acc"));
				result.setGeneDbId(resultSet.getLong("gf_db_id"));
				result.setAlleleAcc(resultSet.getString("allele_acc"));
				result.setAlleleDbId(resultSet.getLong("allele_db_id"));
				result.setMpTerm(resultSet.getString("ontology_acc"));
				result.setOntologyDbId(resultSet.getLong("ontology_db_id"));

				statResults.add(result);

			}
		}

		logger.info("  Adding " + statResults.size() + " Line Level Embryonic MP results");

		return statResults;
	}



    public void initializeSexSpecificMap(Connection connection) throws SQLException {

        // Don't re-initialize
        if (sexSpecificParameters != null) {
            return;
        }

        sexSpecificParameters = new HashSet<>();

        String query = "SELECT stable_id " +
                "FROM phenotype_parameter p " +
                "INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id = p.id " +
                "INNER JOIN phenotype_parameter_ontology_annotation o ON l.annotation_id = o.id " +
                "WHERE o.sex != '' " ;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                sexSpecificParameters.add(resultSet.getString("stable_id"));
            }
        }

    }


    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(OntologyAnnotationGenerator.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}