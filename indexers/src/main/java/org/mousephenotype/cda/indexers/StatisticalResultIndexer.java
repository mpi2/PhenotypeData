/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mousephenotype.cda.indexers;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.MpOntologyDAO;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.indexers.beans.OntologyTermHelper;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Load documents into the statistical-results SOLR core
 */
public class StatisticalResultIndexer extends AbstractIndexer {
	private CommonUtils commonUtils = new CommonUtils();
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	private Connection connection;

	static final String RESOURCE_3I = "3i";
	private final String EMBRYO_PROCEDURES = "IMPC_GPL|IMPC_GEL|IMPC_GPM|IMPC_GEM|IMPC_GPO|IMPC_GEO|IMPC_GPP|IMPC_GEP";

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("ontodbDataSource")
	DataSource ontodbDataSource;

	@Autowired
	@Qualifier("statisticalResultsIndexing")
	SolrServer statResultCore;

	@Autowired
	MpOntologyDAO mpOntologyService;

	private Map<Integer, ImpressBaseDTO> pipelineMap = new HashMap<>();
	private Map<Integer, ImpressBaseDTO> procedureMap = new HashMap<>();
	private Map<Integer, ParameterDTO> parameterMap = new HashMap<>();
	private Map<String, ResourceBean> resourceMap = new HashMap<>();
	private Map<String, List<String>> sexesMap = new HashMap<>();
	private Set<String> alreadyReported = new HashSet<>();

	private Map<Integer, BiologicalDataBean> biologicalDataMap = new HashMap<>();
	private Map<String, Set<String>> parameterMpTermMap = new HashMap<>();
	private Map<String, String> embryoSignificantResults = new HashMap<>();

	public StatisticalResultIndexer() {

	}

	@Override
	public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(statResultCore);
	}

	@Override
	public void initialise(String[] args) throws IndexerException {

		super.initialise(args);

		try {

			connection = komp2DataSource.getConnection();

			pipelineMap = IndexerMap.getImpressPipelines(connection);
			procedureMap = IndexerMap.getImpressProcedures(connection);
			parameterMap = IndexerMap.getImpressParameters(connection);

			populateBiologicalDataMap();
			populateResourceDataMap();
			populateSexesMap();
			populateParameterMpTermMap();
			populateEmbryoSignificanceMap();

		} catch (SQLException e) {
			throw new IndexerException(e);
		}

		printConfiguration();
	}

	public static void main(String[] args) throws IndexerException {

		StatisticalResultIndexer main = new StatisticalResultIndexer();
		main.initialise(args);
		main.run();
		main.validateBuild();
	}

	@Override
	public RunStatus run() throws IndexerException {

		long start = System.currentTimeMillis();
		RunStatus runStatus = new RunStatus();

		long count = populateStatisticalResultsSolrCore();

		logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

		return runStatus;
	}

	private int populateStatisticalResultsSolrCore() throws IndexerException {
		int count = 0;

		try {

			statResultCore.deleteByQuery("*:*");

			count += processViabilityResults();
			count += processFertilityResults();
			count += processReferenceRangePlusResults();
			count += processUnidimensionalResults();
			count += processCategoricalResults();
			count += processEmbryoViabilityResults();
			count += processEmbryoResults();

			logger.info(" Added {} statistical result documents", count);


			// Final commit to save the rest of the docs
			// waitflush, waitserver = true
			statResultCore.commit(true, true);

		} catch (SQLException | IOException | SolrServerException e) {
			throw new IndexerException(e);
		}

		return count;
	}

	// Populate embryo results
	public int processEmbryoResults() throws SQLException, IOException, SolrServerException {

		int count = 0;

		String query = "SELECT DISTINCT " +
			"  CONCAT_WS('-', exp.procedure_stable_id, parameter.stable_id, ls.colony_id, bm.zygosity, sex, exp.organisation_id, exp.metadata_group) AS doc_id,  " +
			"  'embryo' AS data_type, 'Success' AS status, " +
			"  exp.metadata_group, exp.pipeline_id, exp.procedure_id, obs.parameter_id, parameter.stable_id AS dependent_variable, " +
			"  bm.id AS biological_model_id, bm.zygosity AS experimental_zygosity, ls.colony_id, sex, " +
			"  NULL AS p_value, NULL AS effect_size, NULL AS mp_acc, NULL AS male_mp_acc, NULL AS female_mp_acc, " +
			"  db.short_name AS resource_name, db.name AS resource_fullname, db.id AS db_id, db.id AS resource_id, db.id AS external_db_id,  " +
			"  proj.name AS project_name, proj.id AS project_id,  " +
			"  org.name AS phenotyping_center, org.id AS phenotyping_center_id  " +
			"FROM observation obs INNER JOIN phenotype_parameter parameter ON parameter.id = obs.parameter_id " +
			"  INNER JOIN live_sample ls ON ls.id = obs.biological_sample_id " +
			"  INNER JOIN biological_sample bs ON bs.id = obs.biological_sample_id " +
			"  INNER JOIN biological_model_sample bms ON bms.biological_sample_id = obs.biological_sample_id " +
			"  INNER JOIN biological_model bm ON bm.id = bms.biological_model_id " +
			"  INNER JOIN experiment_observation eo ON eo.observation_id = obs.id " +
			"  INNER JOIN experiment exp ON exp.id = eo.experiment_id " +
			"  INNER JOIN (SELECT id FROM phenotype_procedure WHERE stable_id REGEXP '" + EMBRYO_PROCEDURES + "') B ON B.id = exp.procedure_id " +
			"  INNER JOIN external_db db ON db.id = obs.db_id " +
			"  INNER JOIN project proj ON proj.id = exp.project_id " +
			"  INNER JOIN organisation org ON org.id = exp.organisation_id " +
			"WHERE bs.sample_group = 'experimental' " ;

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();

			while (r.next()) {

				StatisticalResultDTO doc = parseLineResult(r);

				if (embryoSignificantResults.containsKey(r.getString("doc_id"))) {

					addMpTermData(embryoSignificantResults.get(r.getString("doc_id")), doc);

				}

				documentCount++;
				statResultCore.addBean(doc, 5000);
				count++;

			}

		}

		logger.info(" Added {} embryo parameter documents", count);
		return count;
	}



	// Populate embryo viability results
	public int processEmbryoViabilityResults() throws SQLException, IOException, SolrServerException {
		int count = 0;
		String query = "SELECT co.category, " +
			"  CONCAT(parameter.stable_id, '_', exp.id, '_embryo') as doc_id, " +
			"'embryo' AS data_type, db.id AS db_id, " +
			"zygosity as experimental_zygosity, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, " +
			"parameter.id as parameter_id, exp.colony_id, null as sex, " +
			"parameter.stable_id as dependent_variable, " +
			"'Success' as status, exp.biological_model_id, " +
			"0.0 as p_value, 1.0 AS effect_size, " +
			"ontology_acc AS mp_acc, null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
			"db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
			"proj.name as project_name, proj.id as project_id, " +
			"org.name as phenotyping_center, org.id as phenotyping_center_id " +
			"FROM phenotype_parameter parameter " +
			"  INNER JOIN observation o ON o.parameter_stable_id=parameter.stable_id " +
			"  INNER JOIN categorical_observation co ON co.id=o.id " +
			"  INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
			"  INNER JOIN experiment exp ON eo.experiment_id=exp.id " +
			"  INNER JOIN biological_model bm ON bm.id=exp.biological_model_id " +
			"  INNER JOIN external_db db ON db.id=o.db_id " +
			"  INNER JOIN project proj ON proj.id=exp.project_id " +
			"  INNER JOIN organisation org ON org.id=exp.organisation_id " +
			"  LEFT OUTER JOIN ( " +
			"    SELECT parameter_id, name, ontology_acc FROM phenotype_parameter_lnk_option lnkopt " +
			"    INNER JOIN phenotype_parameter_option opt ON opt.id=lnkopt.option_id " +
			"    INNER JOIN phenotype_parameter_ontology_annotation oa ON oa.option_id=opt.id " +
			"  ) b ON b.parameter_id=parameter.id AND b.name=co.category " +
			"WHERE parameter.stable_id in ('IMPC_EVL_001_001','IMPC_EVM_001_001','IMPC_EVO_001_001','IMPC_EVP_001_001') " ;

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {

				StatisticalResultDTO doc = parseLineResult(r);
				documentCount++;
				statResultCore.addBean(doc, 30000);
				count++;
			}
		}
		logger.info(" Added {} viability parameter documents", count);
		return count;
	}

	public int processFertilityResults() throws SQLException, IOException, SolrServerException {
		int count = 0;
		String query;

		// Populate fertility results
		query = "SELECT CONCAT(parameter.stable_id, '_', exp.id, '_', IF(sex IS NULL,'both',sex)) as doc_id, " +
			"'line' AS data_type, db.id AS db_id, " +
			"zygosity as experimental_zygosity, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, obs.parameter_id, exp.colony_id, sex, " +
			"parameter.stable_id as dependent_variable, " +
			"'Success' as status, exp.biological_model_id, " +
			"p_value as p_value, effect_size AS effect_size, " +
			"mp_acc, null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
			"db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
			"proj.name as project_name, proj.id as project_id, " +
			"org.name as phenotyping_center, org.id as phenotyping_center_id " +
			"FROM phenotype_parameter parameter " +
			"INNER JOIN observation obs ON obs.parameter_stable_id=parameter.stable_id AND obs.parameter_stable_id IN ('IMPC_FER_001_001', 'IMPC_FER_019_001') " +
			"INNER JOIN experiment_observation eo ON eo.observation_id=obs.id " +
			"INNER JOIN experiment exp ON eo.experiment_id=exp.id " +
			"INNER JOIN external_db db ON db.id=obs.db_id " +
			"INNER JOIN project proj ON proj.id=exp.project_id " +
			"INNER JOIN organisation org ON org.id=exp.organisation_id " +
			"LEFT OUTER JOIN phenotype_call_summary sr ON (exp.colony_id=sr.colony_id AND sr.parameter_id=parameter.id) " +
			"WHERE  parameter.stable_id IN ('IMPC_FER_001_001', 'IMPC_FER_019_001') ";

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {

				// Skip processing females for male infertility parameter
				if (r.getString("dependent_variable")!=null && r.getString("dependent_variable").equals("IMPC_FER_001_001") && r.getString("sex")!=null && r.getString("sex").equals("female")) {
					continue;
				}
				// Skip processing males for female infertility parameter
				if (r.getString("dependent_variable")!=null &&r.getString("dependent_variable").equals("IMPC_FER_019_001") && r.getString("sex")!=null && r.getString("sex").equals("male")) {
					continue;
				}

				StatisticalResultDTO doc = parseLineResult(r);
				documentCount++;
				statResultCore.addBean(doc, 30000);
				count++;
			}

		}
		logger.info(" Added {} fertility parameter documents", count);
		return count;
	}

	public int processViabilityResults() throws SQLException, IOException, SolrServerException {
		int count = 0;
		String query;

		// Populate viability results
		query = "SELECT CONCAT(parameter.stable_id, '_', exp.id, '_', sex) as doc_id, co.category, " +
			"'line' AS data_type, db.id AS db_id, " +
			"zygosity as experimental_zygosity, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, obs.parameter_id, exp.colony_id, sex, " +
			"parameter.stable_id as dependent_variable, " +
			"'Success' as status, exp.biological_model_id, " +
			"p_value as p_value, effect_size AS effect_size, " +
			"mp_acc, null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
			"db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
			"proj.name as project_name, proj.id as project_id, " +
			"org.name as phenotyping_center, org.id as phenotyping_center_id, " +
			"0 AS male_controls, " +
			"(SELECT uobs2.data_point " +
			"  FROM observation obs2 " +
			"  INNER JOIN unidimensional_observation uobs2 ON obs2.id=uobs2.id " +
			"  INNER JOIN experiment_observation eo2 ON eo2.observation_id=obs2.id " +
			"  INNER JOIN experiment exp2 ON eo2.experiment_id=exp2.id " +
			"  WHERE exp2.colony_id=exp.colony_id AND obs2.parameter_stable_id='IMPC_VIA_010_001' limit 1) AS male_mutants, " +
			"0 AS female_controls, " +
			"(SELECT uobs2.data_point " +
			"  FROM observation obs2 " +
			"  INNER JOIN unidimensional_observation uobs2 ON obs2.id=uobs2.id " +
			"  INNER JOIN experiment_observation eo2 ON eo2.observation_id=obs2.id " +
			"  INNER JOIN experiment exp2 ON eo2.experiment_id=exp2.id " +
			"  WHERE exp2.colony_id=exp.colony_id AND obs2.parameter_stable_id='IMPC_VIA_014_001' limit 1) AS  female_mutants " +
			"FROM phenotype_parameter parameter " +
			"INNER JOIN observation obs ON obs.parameter_stable_id=parameter.stable_id AND obs.parameter_stable_id = 'IMPC_VIA_001_001' " +
			"INNER JOIN categorical_observation co ON co.id=obs.id " +
			"INNER JOIN experiment_observation eo ON eo.observation_id=obs.id " +
			"INNER JOIN experiment exp ON eo.experiment_id=exp.id " +
			"INNER JOIN external_db db ON db.id=obs.db_id " +
			"INNER JOIN project proj ON proj.id=exp.project_id " +
			"INNER JOIN organisation org ON org.id=exp.organisation_id " +
			"LEFT OUTER JOIN phenotype_call_summary sr ON (exp.colony_id=sr.colony_id AND sr.parameter_id=parameter.id) " +
			"WHERE  parameter.stable_id = 'IMPC_VIA_001_001' " ;

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {

				StatisticalResultDTO doc = parseLineResult(r);
				documentCount++;
				statResultCore.addBean(doc, 30000);
				count++;
			}
		}
		logger.info(" Added {} viability parameter documents", count);
		return count;
	}

	public int processCategoricalResults() throws SQLException, IOException, SolrServerException {
		int count = 0;
		String query;

		// Populate categorical statistic results
		query = "SELECT CONCAT(dependent_variable, '_', sr.id) as doc_id, "
			+ "  'categorical' AS data_type, sr.id AS db_id, control_id, "
			+ "  experimental_id, experimental_sex as sex, experimental_zygosity, "
			+ "  external_db_id, organisation_id, "
			+ "  pipeline_id, procedure_id, parameter_id, colony_id, "
			+ "  dependent_variable, control_selection_strategy, male_controls, "
			+ "  male_mutants, female_controls, female_mutants, "
			+ "  metadata_group, statistical_method, workflow, status, "
			+ "  category_a, category_b, "
			+ "  p_value as categorical_p_value, effect_size AS categorical_effect_size, "
			+ "  mp_acc, null as male_mp_acc, null as female_mp_acc, "
			+ "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
			+ "  proj.name as project_name, proj.id as project_id, "
			+ "  org.name as phenotyping_center, org.id as phenotyping_center_id "
			+ "FROM stats_categorical_results sr "
			+ "INNER JOIN external_db db on db.id=sr.external_db_id "
			+ "INNER JOIN project proj on proj.id=sr.project_id "
			+ "INNER JOIN organisation org on org.id=sr.organisation_id "
			+ "WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%'";

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {
				StatisticalResultDTO doc = parseCategoricalResult(r);
				documentCount++;
				statResultCore.addBean(doc, 30000);
				count++;
			}
		}
		logger.info(" Added {} categorical documents", count);
		return count;
	}


	public int processReferenceRangePlusResults() throws SQLException, IOException, SolrServerException {
		int count = 0;

		// Populate reference range plus statistic results
		String query = "SELECT CONCAT(dependent_variable, '_', sr.id) as doc_id, "
			+ "  'unidimensional' AS data_type, "
			+ "  sr.id AS db_id, control_id, experimental_id, experimental_zygosity, "
			+ "  external_db_id, organisation_id, "
			+ "  pipeline_id, procedure_id, parameter_id, colony_id, "
			+ "  dependent_variable, control_selection_strategy, "
			+ "  male_controls, male_mutants, female_controls, female_mutants, "
			+ "  male_control_mean, male_experimental_mean, female_control_mean, female_experimental_mean, "
			+ "  metadata_group, statistical_method, workflow, status, "
			+ "  genotype_parameter_estimate, genotype_effect_pvalue, "
			+ "  gp1_genotype, gp2_genotype, "
			+ "  gender_female_ko_estimate, gender_female_ko_pvalue, "
			+ "  gender_male_ko_estimate, gender_male_ko_pvalue, "
			+ "  classification_tag, additional_information, "
			+ "  mp_acc, male_mp_acc, female_mp_acc, "
			+ "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
			+ "  proj.name as project_name, proj.id as project_id, "
			+ "  org.name as phenotyping_center, org.id as phenotyping_center_id "
			+ "FROM stats_rrplus_results sr "
			+ "INNER JOIN external_db db on db.id=sr.external_db_id "
			+ "INNER JOIN project proj on proj.id=sr.project_id "
			+ "INNER JOIN organisation org on org.id=sr.organisation_id ";

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {
				StatisticalResultDTO doc = parseReferenceRangeResult(r);
				documentCount++;
				statResultCore.addBean(doc, 30000);
				count++;
			}
		}
		logger.info(" Added {} RR plus documents", count);
		return count;
	}


	public int processUnidimensionalResults() throws SQLException, IOException, SolrServerException {
		int count = 0;

		// Populate unidimensional statistic results
		String query = "SELECT CONCAT(dependent_variable, '_', sr.id) as doc_id, "
			+ "  'unidimensional' AS data_type, "
			+ "  sr.id AS db_id, control_id, experimental_id, experimental_zygosity, "
			+ "  external_db_id, organisation_id, "
			+ "  pipeline_id, procedure_id, parameter_id, colony_id, "
			+ "  dependent_variable, control_selection_strategy, "
			+ "  male_controls, male_mutants, female_controls, female_mutants, "
			+ "  male_control_mean, male_experimental_mean, female_control_mean, female_experimental_mean, "
			+ "  metadata_group, statistical_method, workflow, status, "
			+ "  batch_significance, "
			+ "  variance_significance, null_test_significance, genotype_parameter_estimate, "
			+ "  genotype_percentage_change, "
			+ "  genotype_stderr_estimate, genotype_effect_pvalue, gender_parameter_estimate, "
			+ "  gender_stderr_estimate, gender_effect_pvalue, weight_parameter_estimate, "
			+ "  weight_stderr_estimate, weight_effect_pvalue, gp1_genotype, "
			+ "  gp1_residuals_normality_test, gp2_genotype, gp2_residuals_normality_test, "
			+ "  blups_test, rotated_residuals_normality_test, intercept_estimate, "
			+ "  intercept_stderr_estimate, interaction_significance, interaction_effect_pvalue, "
			+ "  gender_female_ko_estimate, gender_female_ko_stderr_estimate, gender_female_ko_pvalue, "
			+ "  gender_male_ko_estimate, gender_male_ko_stderr_estimate, gender_male_ko_pvalue, "
			+ "  classification_tag, additional_information, "
			+ "  mp_acc, male_mp_acc, female_mp_acc, "
			+ "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
			+ "  proj.name as project_name, proj.id as project_id, "
			+ "  org.name as phenotyping_center, org.id as phenotyping_center_id "
			+ "FROM stats_unidimensional_results sr "
			+ "INNER JOIN external_db db on db.id=sr.external_db_id "
			+ "INNER JOIN project proj on proj.id=sr.project_id "
			+ "INNER JOIN organisation org on org.id=sr.organisation_id "
			+ "WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%'";

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();
			while (r.next()) {
				StatisticalResultDTO doc = parseUnidimensionalResult(r);
				documentCount++;
				statResultCore.addBean(doc, 30000);
				count++;
			}
		}
		logger.info(" Added {} unidimensional documents", count);
		return count;
	}


	private Double nullCheckResult(ResultSet r,  String field) throws SQLException {
		Double v = r.getDouble(field);
		return r.wasNull() ? null : v;
	}

	private StatisticalResultDTO parseReferenceRangeResult(ResultSet r) throws SQLException {

		StatisticalResultDTO doc = parseResultCommonFields(r);
		if (sexesMap.containsKey("rrplus-" + doc.getDbId())) {
			doc.setPhenotypeSex(sexesMap.get("rrplus-" + doc.getDbId()));
		}

		// Index the mean fields
		doc.setMaleControlMean(r.getDouble("male_control_mean"));
		doc.setMaleMutantMean(r.getDouble("male_experimental_mean"));
		doc.setFemaleControlMean(r.getDouble("female_control_mean"));
		doc.setFemaleMutantMean(r.getDouble("female_experimental_mean"));

		doc.setGroup1Genotype(r.getString("gp1_genotype"));
		doc.setGroup2Genotype(r.getString("gp2_genotype"));


		// Set the overall genotype effect fields
		String genotypePvalue = r.getString("genotype_effect_pvalue");
		if (! r.wasNull()) {
			String [] fields = genotypePvalue.split(",");

			// Low vs normal&high genotype pvalue
			Double pvalue = Double.parseDouble(fields[0]);
			doc.setGenotypePvalueLowVsNormalHigh(pvalue);

			// High vs low&normal genotype pvalue
			pvalue = Double.parseDouble(fields[1]);
			doc.setGenotypePvalueLowNormalVsHigh(pvalue);

			doc.setNullTestPValue(Math.min(doc.getGenotypePvalueLowNormalVsHigh(), doc.getGenotypePvalueLowVsNormalHigh()));
			doc.setpValue(doc.getNullTestPValue());

			String genotypeEffectSize = r.getString("genotype_parameter_estimate");
			if (! r.wasNull()) {
				fields = genotypeEffectSize.replaceAll("%", "").split(",");

				// Low vs normal&high genotype effect size
				Double es = Double.parseDouble(fields[0]);
				doc.setGenotypeEffectSizeLowVsNormalHigh(es);

				// High vs low&normal genotype effect size
				es = Double.parseDouble(fields[1]);
				doc.setGenotypeEffectSizeLowNormalVsHigh(es);
			}

		}

		// Set the female female effect fields
		genotypePvalue = r.getString("gender_female_ko_pvalue");
		if (! r.wasNull() && ! genotypePvalue.equals("NA")) {
			String [] fields = genotypePvalue.split(",");

			// Low vs normal&high female pvalue
			Double pvalue = Double.parseDouble(fields[0]);
			doc.setFemalePvalueLowVsNormalHigh(pvalue);

			// High vs low&normal female pvalue
			pvalue = Double.parseDouble(fields[1]);
			doc.setFemalePvalueLowNormalVsHigh(pvalue);

			String genotypeEffectSize = r.getString("gender_female_ko_estimate");
			if (! r.wasNull()) {
				fields = genotypeEffectSize.replaceAll("%", "").split(",");

				// Low vs normal&high female effect size
				Double es = Double.parseDouble(fields[0]);
				doc.setFemaleEffectSizeLowVsNormalHigh(es);

				// High vs low&normal female effect size
				es = Double.parseDouble(fields[1]);
				doc.setFemaleEffectSizeLowNormalVsHigh(es);
			}

		}

		// Set the male effect fields
		genotypePvalue = r.getString("gender_male_ko_pvalue");
		if (! r.wasNull() && ! genotypePvalue.equals("NA")) {
			String [] fields = genotypePvalue.split(",");

			// Low vs normal&high male pvalue
			Double pvalue = Double.parseDouble(fields[0]);
			doc.setMalePvalueLowVsNormalHigh(pvalue);

			// High vs low&normal male pvalue
			pvalue = Double.parseDouble(fields[1]);
			doc.setMalePvalueLowNormalVsHigh(pvalue);

			String genotypeEffectSize = r.getString("gender_male_ko_estimate");
			if (! r.wasNull()) {
				fields = genotypeEffectSize.replaceAll("%", "").split(",");

				// Low vs normal&high male effect size
				Double es = Double.parseDouble(fields[0]);
				doc.setMaleEffectSizeLowVsNormalHigh(es);

				// High vs low&normal male effect size
				es = Double.parseDouble(fields[1]);
				doc.setMaleEffectSizeLowNormalVsHigh(es);
			}

		}


		doc.setClassificationTag(r.getString("classification_tag"));
		doc.setAdditionalInformation(r.getString("additional_information"));
		return doc;

	}

	private StatisticalResultDTO parseUnidimensionalResult(ResultSet r) throws SQLException {

		StatisticalResultDTO doc = parseResultCommonFields(r);
		if (sexesMap.containsKey("unidimensional-" + doc.getDbId())) {
			doc.setPhenotypeSex(sexesMap.get("unidimensional-" + doc.getDbId()));
		}

		// Index the mean fields
		doc.setMaleControlMean(r.getDouble("male_control_mean"));
		doc.setMaleMutantMean(r.getDouble("male_experimental_mean"));
		doc.setFemaleControlMean(r.getDouble("female_control_mean"));
		doc.setFemaleMutantMean(r.getDouble("female_experimental_mean"));

		doc.setNullTestPValue(nullCheckResult(r, "null_test_significance"));

		// If PhenStat did not run, then the result will have a NULL for the null_test_significance field
		// In that case, fall back to Wilcoxon test
		Double pv = doc.getNullTestPValue();
		if (pv==null && doc.getStatus().equals("Success") && doc.getStatisticalMethod() != null && doc.getStatisticalMethod().startsWith("Wilcoxon")) {

			// Wilcoxon test.  Choose the most significant pvalue from the sexes
			pv = 1.0;
			Double fPv = r.getDouble("gender_female_ko_pvalue");
			if (!r.wasNull() && fPv < pv) {
				pv = fPv;
			}

			Double mPv = r.getDouble("gender_male_ko_pvalue");
			if (!r.wasNull() && mPv < pv) {
				pv = mPv;
			}

		}

		doc.setpValue(pv);

		doc.setGroup1Genotype(r.getString("gp1_genotype"));
		doc.setGroup1ResidualsNormalityTest(nullCheckResult(r, "gp1_residuals_normality_test"));
		doc.setGroup2Genotype(r.getString("gp2_genotype"));
		doc.setGroup2ResidualsNormalityTest(nullCheckResult(r, "gp2_residuals_normality_test"));

		doc.setBatchSignificant(r.getBoolean("batch_significance"));
		doc.setVarianceSignificant(r.getBoolean("variance_significance"));
		doc.setInteractionSignificant(r.getBoolean("interaction_significance"));

		doc.setGenotypeEffectParameterEstimate(nullCheckResult(r, "genotype_parameter_estimate"));

		String percentageChange = r.getString("genotype_percentage_change");
		if (!r.wasNull()) {
			Double femalePercentageChange = StatisticalResultService.getFemalePercentageChange(percentageChange);
			if (femalePercentageChange != null) {
				doc.setFemalePercentageChange(femalePercentageChange.toString() + "%");
			}

			Double malePercentageChange = StatisticalResultService.getMalePercentageChange(percentageChange);
			if (malePercentageChange != null) {
				doc.setMalePercentageChange(malePercentageChange.toString() + "%");
			}
		}

		doc.setGenotypeEffectStderrEstimate(nullCheckResult(r, "genotype_stderr_estimate"));
		doc.setGenotypeEffectPValue(nullCheckResult(r, "genotype_effect_pvalue"));

		doc.setSexEffectParameterEstimate(nullCheckResult(r, "gender_parameter_estimate"));
		doc.setSexEffectStderrEstimate(nullCheckResult(r, "gender_stderr_estimate"));
		doc.setSexEffectPValue(nullCheckResult(r, "gender_effect_pvalue"));

		doc.setWeightEffectParameterEstimate(nullCheckResult(r, "weight_parameter_estimate"));
		doc.setWeightEffectStderrEstimate(nullCheckResult(r, "weight_stderr_estimate"));
		doc.setWeightEffectPValue(nullCheckResult(r, "weight_effect_pvalue"));

		doc.setInterceptEstimate(nullCheckResult(r, "intercept_estimate"));
		doc.setInterceptEstimateStderrEstimate(nullCheckResult(r, "intercept_stderr_estimate"));
		doc.setInteractionEffectPValue(nullCheckResult(r, "interaction_effect_pvalue"));

		doc.setFemaleKoParameterEstimate(nullCheckResult(r, "gender_female_ko_estimate"));
		doc.setFemaleKoEffectStderrEstimate(nullCheckResult(r, "gender_female_ko_stderr_estimate"));
		doc.setFemaleKoEffectPValue(nullCheckResult(r, "gender_female_ko_pvalue"));

		doc.setMaleKoParameterEstimate(nullCheckResult(r, "gender_male_ko_estimate"));
		doc.setMaleKoEffectStderrEstimate(nullCheckResult(r, "gender_male_ko_stderr_estimate"));
		doc.setMaleKoEffectPValue(nullCheckResult(r, "gender_male_ko_pvalue"));

		doc.setBlupsTest(nullCheckResult(r, "blups_test"));
		doc.setRotatedResidualsTest(nullCheckResult(r, "rotated_residuals_normality_test"));
		doc.setClassificationTag(r.getString("classification_tag"));
		doc.setAdditionalInformation(r.getString("additional_information"));
		return doc;

	}


	private StatisticalResultDTO parseCategoricalResult(ResultSet r) throws SQLException {

		StatisticalResultDTO doc = parseResultCommonFields(r);
		if (sexesMap.containsKey("categorical-" + doc.getDbId())) {
			doc.setPhenotypeSex(sexesMap.get("categorical-" + doc.getDbId()));
		}

		doc.setSex(r.getString("sex"));
		doc.setpValue(r.getDouble("categorical_p_value"));
		doc.setEffectSize(r.getDouble("categorical_effect_size"));

		Set<String> categories = new HashSet<>();
		if (StringUtils.isNotEmpty(r.getString("category_a"))) {
			categories.addAll(Arrays.asList(r.getString("category_a").split("\\|")));
		}
		if (StringUtils.isNotEmpty(r.getString("category_b"))) {
			categories.addAll(Arrays.asList(r.getString("category_b")
				.split("\\|")));
		}

		doc.setCategories(new ArrayList<>(categories));

		return doc;

	}


	/**
	 * parseLineResult changes a database result set for a line into a solr document
	 *
	 * @param r the result set
	 * @return a solr document
	 * @throws SQLException
	 */
	private StatisticalResultDTO parseLineResult(ResultSet r) throws SQLException {

		StatisticalResultDTO doc = parseLineResultCommonFields(r);

		try {
			String category = r.getString("category");
			if (!r.wasNull() && category.equals("Insufficient numbers to make a call")) {
				doc.setStatus("Failed - " + category);
			}
		} catch (java.sql.SQLException e) {
			// do nothing. Result set did not have "category" in it
		}

		try {
			r.getString("experimental_zygosity");
			if (r.wasNull()) {
				String category = r.getString("category");
				if (!r.wasNull()) {
					String[] fields = category.split("-");

					ZygosityType zygosity;
					switch (fields[0].trim().toLowerCase()) {
						case "heterozygous":
							zygosity = ZygosityType.heterozygote;
							break;
						case "hemizygous":
							zygosity = ZygosityType.hemizygote;
							break;
						case "homozygous":
						default:
							zygosity = ZygosityType.homozygote;
							break;
					}

					doc.setZygosity(zygosity.getName());
				}
			}
		} catch (java.sql.SQLException e) {
			// do nothing. Result set did not have "category" in it
		}

		String sex = r.getString("sex");
		if (!r.wasNull()) {

			doc.setSex(sex);

			// Do not attempt to add to the phenotye_sex fuield if it has been manually set as it is for
			// the viability and fertility parameters listed here
			if ( ! Arrays.asList("IMPC_VIA_001_001", "IMPC_FER_001_001", "IMPC_FER_019_001").contains(doc.getDependentVariable())) {
				// Add the sex to the phenotype_sexes field
				if (doc.getPhenotypeSex() == null) {
					doc.setPhenotypeSex(new ArrayList<>());
				}

				if (!doc.getPhenotypeSex().contains(sex)) {
					doc.getPhenotypeSex().add(sex);
				}
			}

		}

		Double p_value = r.getDouble("p_value");
		if (!r.wasNull() && doc.getMpTermId()!=null) {
			doc.setpValue(p_value);
		}

		Double effect_size = r.getDouble("effect_size");
		if (!r.wasNull() && doc.getMpTermId()!=null) {
			doc.setEffectSize(effect_size);
		}


		return doc;

	}


	private StatisticalResultDTO parseResultCommonFields(ResultSet r) throws SQLException {

		StatisticalResultDTO doc = new StatisticalResultDTO();

		doc.setDocId(r.getString("doc_id"));
		doc.setDataType(r.getString("data_type"));

		// Experiment details
		String procedurePrefix = StringUtils.join(Arrays.asList(parameterMap.get(r.getInt("parameter_id"))
			.getStableId()
			.split("_"))
			.subList(0, 2), "_");
		if (GenotypePhenotypeIndexer.source3iProcedurePrefixes.contains(procedurePrefix)) {
			// Override the resource for the 3i procedures
			doc.setResourceId(resourceMap.get(RESOURCE_3I).id);
			doc.setResourceName(resourceMap.get(RESOURCE_3I).shortName);
			doc.setResourceFullname(resourceMap.get(RESOURCE_3I).name);
		} else {
			doc.setResourceId(r.getInt("resource_id"));
			doc.setResourceName(r.getString("resource_name"));
			doc.setResourceFullname(r.getString("resource_fullname"));
		}

		doc.setProjectId(r.getInt("project_id"));
		doc.setProjectName(r.getString("project_name"));
		doc.setPhenotypingCenter(r.getString("phenotyping_center"));
		doc.setControlBiologicalModelId(r.getInt("control_id"));
		doc.setMutantBiologicalModelId(r.getInt("experimental_id"));
		doc.setZygosity(r.getString("experimental_zygosity"));
		doc.setDependentVariable(r.getString("dependent_variable"));
		doc.setExternalDbId(r.getInt("external_db_id"));
		doc.setDbId(r.getInt("db_id"));
		doc.setOrganisationId(r.getInt("organisation_id"));
		doc.setPhenotypingCenterId(r.getInt("phenotyping_center_id"));

		doc.setControlSelectionMethod(r.getString("control_selection_strategy"));
		doc.setStatisticalMethod(r.getString("statistical_method"));
		doc.setWorkflow(r.getString("workflow"));
		doc.setMaleControlCount(r.getInt("male_controls"));
		doc.setFemaleControlCount(r.getInt("female_controls"));
		doc.setMaleMutantCount(r.getInt("male_mutants"));
		doc.setFemaleMutantCount(r.getInt("female_mutants"));
		doc.setColonyId(r.getString("colony_id"));
		doc.setStatus(r.getString("status"));

		if (doc.getPhenotypeSex() == null) {
			doc.setPhenotypeSex(new ArrayList<>());
		}

		if (doc.getMaleMutantCount()>0) {
			doc.getPhenotypeSex().add(SexType.male.getName());
		}

		if (doc.getFemaleMutantCount()>0) {
			doc.getPhenotypeSex().add(SexType.female.getName());
		}

		// Always set a metadata group here to allow for simpler searching for
		// unique results and to maintain parity with the observation index
		// where "empty string" metadata group means no required metadata.
		if (StringUtils.isNotEmpty(r.getString("metadata_group"))) {
			doc.setMetadataGroup(r.getString("metadata_group"));
		} else {
			doc.setMetadataGroup("");
		}

		addImpressData(r, doc);

		// Biological details
		addBiologicalData(doc, doc.getMutantBiologicalModelId());

		// MP Terms
		addMpTermData(r, doc);

		return doc;
	}


	private StatisticalResultDTO parseLineResultCommonFields(ResultSet r) throws SQLException {

		StatisticalResultDTO doc = new StatisticalResultDTO();

		String docId = r.getString("doc_id");
		if (docId == null) {
			docId = String.valueOf(Math.random());
		}

		doc.setDocId(docId);
		doc.setDataType(r.getString("data_type"));
		doc.setResourceId(r.getInt("resource_id"));
		doc.setResourceName(r.getString("resource_name"));
		doc.setResourceFullname(r.getString("resource_fullname"));
		doc.setProjectId(r.getInt("project_id"));
		doc.setProjectName(r.getString("project_name"));
		doc.setPhenotypingCenter(r.getString("phenotyping_center"));
		doc.setMutantBiologicalModelId(r.getInt("biological_model_id"));
		doc.setZygosity(r.getString("experimental_zygosity"));
		doc.setDependentVariable(r.getString("dependent_variable"));
		doc.setExternalDbId(r.getInt("external_db_id"));
		doc.setDbId(r.getInt("db_id"));
		doc.setPhenotypingCenterId(r.getInt("phenotyping_center_id"));

		doc.setStatisticalMethod("Supplied as data");
//		doc.setMaleControlCount(0);
//		doc.setFemaleControlCount(0);
		doc.setColonyId(r.getString("colony_id"));
		doc.setStatus("Success");


		// Always set a metadata group here to allow for simpler searching for
		// unique results and to maintain parity with the observation index
		// where "empty string" metadata group means no required metadata.
		if (StringUtils.isNotEmpty(r.getString("metadata_group"))) {
			doc.setMetadataGroup(r.getString("metadata_group"));
		} else {
			doc.setMetadataGroup("");
		}

		// Fertility results DO NOT contain the counts of controls/mutants
		if (r.getString("dependent_variable").equals("IMPC_VIA_001_001")) {
			doc.setMaleMutantCount(r.getInt("male_mutants"));
			doc.setFemaleMutantCount(r.getInt("female_mutants"));

			// Viability parameter significant for both sexes
			doc.setPhenotypeSex(Arrays.asList("female", "male"));

		} else if (r.getString("dependent_variable").equals("IMPC_FER_001_001")) {
			// Fertility significant for Males
			doc.setPhenotypeSex(Arrays.asList("male"));

		} else if (r.getString("dependent_variable").equals("IMPC_FER_019_001")) {
			// Fertility significant for females
			doc.setPhenotypeSex(Arrays.asList("female"));

		}

		// Impress pipeline data details
		addImpressData(r, doc);

		// Biological details
		addBiologicalData(doc, doc.getMutantBiologicalModelId());

		// MP Term details
		addMpTermData(r, doc);

		return doc;
	}


	/**
	 * Add the appropriate MP term associations to the document
	 * This is only used for the embryo data for the moment (2016-04-07)
	 *
	 * @param mpTerm the mp term accession id
	 * @param doc the solr document to update
	 * @throws SQLException if the query fields do not exist
	 */
	private void addMpTermData(String mpTerm, StatisticalResultDTO doc) throws SQLException {

		// Add the appropriate fields for the global MP term
		if (mpTerm != null) {

			OntologyTermBean bean = mpOntologyService.getTerm(mpTerm);
			if (bean != null) {
				doc.setMpTermId(bean.getId());
				doc.setMpTermName(bean.getName());

				OntologyTermHelper beanlist = new OntologyTermHelper(mpOntologyService, bean.getId());
				doc.setTopLevelMpTermId(beanlist.getTopLevels().getIds());
				doc.setTopLevelMpTermName(beanlist.getTopLevels().getNames());

				doc.setIntermediateMpTermId(beanlist.getIntermediates().getIds());
				doc.setIntermediateMpTermName(beanlist.getIntermediates().getNames());
			}
		}
	}

	/**
	 * Add the appropriate MP term associations to the document
	 *
	 * @param r   the result set to pull the relevant fields from
	 * @param doc the solr document to update
	 * @throws SQLException if the query fields do not exist
	 */
	private void addMpTermData(ResultSet r, StatisticalResultDTO doc) throws SQLException {

		// Add the appropriate fields for the global MP term
		String mpTerm = r.getString("mp_acc");
		if (!r.wasNull()) {

			addMpTermData(mpTerm, doc);

		}

		// Process the male MP term
		mpTerm = r.getString("male_mp_acc");
		if (!r.wasNull()) {

			OntologyTermBean bean = mpOntologyService.getTerm(mpTerm);
			if (bean != null) {
				doc.setMaleMpTermId(bean.getId());
				doc.setMaleMpTermName(bean.getName());

				OntologyTermHelper beanlist = new OntologyTermHelper(mpOntologyService, bean.getId());
				doc.setMaleTopLevelMpTermId(beanlist.getTopLevels().getIds());
				doc.setMaleTopLevelMpTermName(beanlist.getTopLevels().getNames());

				doc.setMaleIntermediateMpTermId(beanlist.getIntermediates().getIds());
				doc.setMaleIntermediateMpTermName(beanlist.getIntermediates().getNames());
			}
		}

		// Process the female MP term
		mpTerm = r.getString("female_mp_acc");
		if (!r.wasNull()) {

			OntologyTermBean bean = mpOntologyService.getTerm(mpTerm);
			if (bean != null) {
				doc.setFemaleMpTermId(bean.getId());
				doc.setFemaleMpTermName(bean.getName());

				OntologyTermHelper beanlist = new OntologyTermHelper(mpOntologyService, bean.getId());
				doc.setFemaleTopLevelMpTermId(beanlist.getTopLevels().getIds());
				doc.setFemaleTopLevelMpTermName(beanlist.getTopLevels().getNames());

				doc.setFemaleIntermediateMpTermId(beanlist.getIntermediates().getIds());
				doc.setFemaleIntermediateMpTermName(beanlist.getIntermediates().getNames());
			}
		}

	}


	private void addImpressData(ResultSet r, StatisticalResultDTO doc)
	throws SQLException {

		doc.setPipelineId(pipelineMap.get(r.getInt("pipeline_id")).getId());
		doc.setPipelineStableKey(pipelineMap.get(r.getInt("pipeline_id")).getStableKey());
		doc.setPipelineName(pipelineMap.get(r.getInt("pipeline_id")).getName());
		doc.setPipelineStableId(pipelineMap.get(r.getInt("pipeline_id")).getStableId());
		doc.setProcedureId(procedureMap.get(r.getInt("procedure_id")).getId());
		doc.setProcedureStableKey(procedureMap.get(r.getInt("procedure_id")).getStableKey());
		doc.setProcedureName(procedureMap.get(r.getInt("procedure_id")).getName());
		doc.setProcedureStableId(procedureMap.get(r.getInt("procedure_id")).getStableId());
		doc.setParameterId(parameterMap.get(r.getInt("parameter_id")).getId());
		doc.setParameterStableKey(parameterMap.get(r.getInt("parameter_id")).getStableKey());
		doc.setParameterName(parameterMap.get(r.getInt("parameter_id")).getName());
		doc.setParameterStableId(parameterMap.get(r.getInt("parameter_id")).getStableId());

		//		doc.setAnnotate(parameterMap.get(r.getInt("parameter_id")).isAnnotate());

		// Create field that contains all possible MP terms (including intermediate and top level terms)
		// that this parameter can produce
		Set<String> mpIds = parameterMpTermMap.get(doc.getParameterStableId());

		if (mpIds != null) {
			Set<OntologyTermBean> ontoTerms = new HashSet<>();

			mpIds.forEach(mpId -> {

				OntologyTermBean bean = mpOntologyService.getTerm(mpId);

				if (bean != null) {

					ontoTerms.add(bean);

					OntologyTermHelper beanlist = new OntologyTermHelper(mpOntologyService, bean.getId());

					// Add all ancestor terms for this MP ID
					beanlist.getAncestors().getIds().forEach(mp -> {
						OntologyTermBean b = mpOntologyService.getTerm(mp);
						if (b != null) {
							ontoTerms.add(b);
						}
					});

				}

			});

			// Default the term options to empty lists
			doc.setMpTermIdOptions(new ArrayList<>());
			doc.setMpTermNameOptions(new ArrayList<>());

			ontoTerms.forEach(term -> {
				doc.getMpTermIdOptions().add(term.getId());
				doc.getMpTermNameOptions().add(term.getName());
			});


		} else {

			String p = doc.getParameterStableId();
			if ( ! alreadyReported.contains(p)) {
				alreadyReported.add(p);
				logger.debug("Cannot find MP terms for parameter {}", p);
			}

		}

	}


	private void addBiologicalData(StatisticalResultDTO doc, Integer biologicalModelId) {

		BiologicalDataBean b = biologicalDataMap.get(biologicalModelId);

		if (b == null) {
			logger.error("Cannot find genomic information for biological_model_id {}", biologicalModelId);
			return;
		}

		doc.setMarkerAccessionId(b.geneAcc);
		doc.setMarkerSymbol(b.geneSymbol);
		doc.setAlleleAccessionId(b.alleleAccession);
		doc.setAlleleName(b.alleleName);
		doc.setAlleleSymbol(b.alleleSymbol);
		doc.setStrainAccessionId(b.strainAcc);
		doc.setStrainName(b.strainName);

	}


	/**
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological sample
	 *
	 * @throws SQLException when a database exception occurs
	 */
	private void populateBiologicalDataMap() throws SQLException {

		String query = "SELECT bm.id, "
			+ "strain.acc AS strain_acc, strain.name AS strain_name, bm.genetic_background, "
			+ "(SELECT DISTINCT allele_acc FROM biological_model_allele bma WHERE bma.biological_model_id=bm.id) AS allele_accession, "
			+ "(SELECT DISTINCT a.symbol FROM biological_model_allele bma INNER JOIN allele a ON (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) AS allele_symbol, "
			+ "(SELECT DISTINCT a.name FROM biological_model_allele bma INNER JOIN allele a ON (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) AS allele_name, "
			+ "(SELECT DISTINCT gf_acc FROM biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bm.id) AS acc, "
			+ "(SELECT DISTINCT gf.symbol FROM biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id) AS symbol "
			+ "FROM biological_model bm "
			+ "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id "
			+ "INNER JOIN strain strain ON strain.acc=bmstrain.strain_acc "
			+ "WHERE exists(SELECT DISTINCT gf.symbol FROM biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id)";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				BiologicalDataBean b = new BiologicalDataBean();

				b.alleleAccession = resultSet.getString("allele_accession");
				b.alleleSymbol = resultSet.getString("allele_symbol");
				b.alleleName = resultSet.getString("allele_name");
				b.geneAcc = resultSet.getString("acc");
				b.geneSymbol = resultSet.getString("symbol");
				b.strainAcc = resultSet.getString("strain_acc");
				b.strainName = resultSet.getString("strain_name");
				b.geneticBackground = resultSet.getString("genetic_background");

				biologicalDataMap.put(resultSet.getInt("id"), b);
			}
		}
		logger.info(" Mapped {} biological data entries", biologicalDataMap.size());

	}


	/**
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological sample
	 *
	 * @throws SQLException when a database exception occurs
	 */
	private void populateResourceDataMap() throws SQLException {

		String query = "SELECT id, name, short_name FROM external_db";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ResourceBean b = new ResourceBean();
				b.id = resultSet.getInt("id");
				b.name = resultSet.getString("name");
				b.shortName = resultSet.getString("short_name");
				resourceMap.put(resultSet.getString("short_name"), b);
			}
		}
		logger.info(" Mapped {} resource data entries", resourceMap.size());
	}

	/**
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological sample
	 *
	 * @throws SQLException when a database exception occurs
	 */
	private void populateSexesMap() throws SQLException {

		List<String> queries = Arrays.asList(
			"SELECT CONCAT('unidimensional-', s.id) AS id, GROUP_CONCAT(distinct p.sex) as sexes FROM stats_unidimensional_results s INNER JOIN stat_result_phenotype_call_summary r ON r.unidimensional_result_id=s.id INNER JOIN phenotype_call_summary p ON p.id=r.phenotype_call_summary_id GROUP BY s.id",
			"SELECT CONCAT('categorical-', s.id) AS id, GROUP_CONCAT(distinct p.sex) as sexes FROM stats_categorical_results s INNER JOIN stat_result_phenotype_call_summary r ON r.categorical_result_id=s.id INNER JOIN phenotype_call_summary p ON p.id=r.phenotype_call_summary_id GROUP BY s.id",
			"SELECT CONCAT('rrplus-', s.id) AS id, GROUP_CONCAT(distinct p.sex) as sexes FROM stats_rrplus_results s INNER JOIN stat_result_phenotype_call_summary r ON r.rrplus_result_id=s.id INNER JOIN phenotype_call_summary p ON p.id=r.phenotype_call_summary_id GROUP BY s.id"
		);

		for (String query : queries) {
			try (PreparedStatement p = connection.prepareStatement(query)) {

				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {
					List<String> sexes = new ArrayList<>();
					sexes.addAll(Arrays.asList(resultSet.getString("sexes").replaceAll(" ", "").split(",")));

					sexesMap.put(resultSet.getString("id"), sexes);
				}
			}
		}
		logger.info(" Mapped {} sexes data entries", sexesMap.size());
	}

	/**
	 * The embryo significance map keys are document IDs that should match the embryo documents and the key is the MP
	 * acc
	 *
	 * @throws SQLException
	 */
	private void populateEmbryoSignificanceMap() throws SQLException {

		// Populate the significant results map with this query
		String sigResultsQuery = "SELECT CONCAT(parameter.stable_id, '_', pcs.colony_id, pcs.organisation_id) AS doc_id, mp_acc " +
			"FROM phenotype_call_summary pcs " +
			"INNER JOIN phenotype_parameter parameter ON parameter.id = pcs.parameter_id " +
			"WHERE parameter.stable_id REGEXP '" + EMBRYO_PROCEDURES + "' ";

		try (PreparedStatement p = connection.prepareStatement(sigResultsQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			ResultSet r = p.executeQuery();
			while (r.next()) {

				String docId = r.getString("doc_id");
				String mpAcc = r.getString("mp_acc");

				embryoSignificantResults.put(docId, mpAcc);
			}
		}

		logger.info(" Mapped {} embryo significant data entries", embryoSignificantResults.size());

	}

	private void populateParameterMpTermMap() throws SQLException {

		String query = "SELECT stable_id, ontology_acc FROM phenotype_parameter p " +
			"INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=p.id " +
			"INNER JOIN phenotype_parameter_ontology_annotation o ON o.id=l.annotation_id " ;

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String parameter = resultSet.getString("stable_id");
				String ontologyTerm = resultSet.getString("ontology_acc");

				if( ! parameterMpTermMap.containsKey(parameter)) {
					parameterMpTermMap.put(parameter, new HashSet<>());
				}
				parameterMpTermMap.get(parameter).add(ontologyTerm);

			}
		}
		logger.info(" Mapped {} parameterMpTerm data entries", parameterMpTermMap.size());

	}

	protected class ResourceBean {
		public Integer id;
		public String name;
		public String shortName;


		@Override
		public String toString() {

			return "ResourceBean{" + "id=" + id +
				", name='" + name + '\'' +
				", shortName='" + shortName + '\'' +
				'}';
		}
	}

	/**
	 * Internal class to act as Map value DTO for biological data
	 */
	private class BiologicalDataBean {
		private String alleleAccession;
		private String alleleSymbol;
		private String alleleName;
		private String geneAcc;
		private String geneSymbol;
		private String strainAcc;
		private String strainName;
		private String geneticBackground;
	}
}
