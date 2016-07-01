/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by jmason on 02/06/2016.
 */
@EnableAutoConfiguration
public class PhenodigmIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(PhenodigmIndexer.class);

	@Autowired
	@Qualifier("phenodigmIndexing")
	private SolrServer phenodigmIndexing;

	@Autowired
	@Qualifier("phenodigmDataSource")
	@NotNull
	private DataSource phenodigmDataSource;

	public static final long MIN_EXPECTED_ROWS = 218000;

	Map<String, Set<String>> diseasePhenotypeMap = new HashMap<>();
	Map<Integer, Set<String>> mousePhenotypeMap = new HashMap<>();
	Map<String, Set<String>> humanSynonymMap = new HashMap<>();

	public PhenodigmIndexer() {}

	public PhenodigmIndexer(SolrServer phenodigmIndexing, DataSource phenodigmDataSource) {
		this.phenodigmIndexing = phenodigmIndexing;
		this.phenodigmDataSource = phenodigmDataSource;
	}

	public static void main(String[] args) throws IndexerException {
		SpringApplication.run(PhenodigmIndexer.class, args);
	}

	@Override
	public RunStatus validateBuild()	throws IndexerException {
		return super.validateBuild(phenodigmIndexing);
	}


	@Override
	public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
		documentCount = 0;

		Integer count = 0;
		RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try {

			diseasePhenotypeMap = getDiseasePhenotypeMap();
			logger.info("Populated disease phenotype map");

			mousePhenotypeMap = getMousePhenotypeMap();
			logger.info("Populated mouse phenotype map");

			humanSynonymMap = getHumanSynonymMap();
			logger.info("Populated human synonym map");

			phenodigmIndexing.deleteByQuery("*:*");

			count = populateDiseaseTerms();
			logger.info("  Added {} disease term documents", count);
			documentCount += count;

			count = populateGeneSummary();
			logger.info("  Added {} gene summary documents", count);
			documentCount += count;

			count = populateMouseModel();
			logger.info("  Added {} mouse model documents", count);
			documentCount += count;

			count = populateHumanPhenotype();
			logger.info("  Added {} human phenotype documents", count);
			documentCount += count;

			count = populateMousePhenotype();
			logger.info("  Added {} mouse phenotype documents", count);
			documentCount += count;

			count = populateDiseaseGeneSummary();
			logger.info("  Added {} disease summary documents", count);
			documentCount += count;

			count = populateDiseaseModelAssociation();
			logger.info("  Added {} disease model association documents", count);
			documentCount += count;

			count = populateBestHpMpMappings();
			logger.info("  Added {} best HP-MP mappings documents", count);
			documentCount += count;

			count = populateBestMpHpMappings();
			logger.info("  Added {} best MP-HP mappings documents", count);
			documentCount += count;

			// Final commit
			phenodigmIndexing.commit();

		} catch (SQLException | SolrServerException | IOException e) {
			throw new IndexerException(e);
		}

		logger.info(" Added {} total beans in {}", documentCount, commonUtils.msToHms(System.currentTimeMillis() - start));

		return runStatus;
	}

	public Map<String, Set<String>> getDiseasePhenotypeMap() throws SQLException {

		Map<String, Set<String>> diseasePhenotypeMap = new HashMap<>();

		String query = "SELECT DISTINCT dh.disease_id, CONCAT(hp.hp_id, '_', hp.term) AS phenotype FROM disease_hp dh INNER JOIN hp ON hp.hp_id = dh.hp_id";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String diseaseId = r.getString("disease_id");
				if (!diseasePhenotypeMap.containsKey(diseaseId)) {
					diseasePhenotypeMap.put(diseaseId, new HashSet<>());
				}
				diseasePhenotypeMap.get(diseaseId).add(r.getString("phenotype"));
			}

		}

		return diseasePhenotypeMap;
	}

	public Map<Integer, Set<String>> getMousePhenotypeMap() throws SQLException {

		Map<Integer, Set<String>> map = new HashMap<>();

		String query = "SELECT CONCAT(mp.mp_id, '_', mp.term) AS phenotype, mmmp.model_id FROM mp JOIN mouse_model_mp mmmp ON mmmp.mp_id = mp.mp_id";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			ResultSet r = p.executeQuery();
			while (r.next()) {

				Integer id = r.getInt("model_id");
				if (!map.containsKey(id)) {
					map.put(id, new HashSet<>());
				}
				map.get(id).add(r.getString("phenotype"));
			}

		}

		return map;
	}

	public Map<String, Set<String>> getHumanSynonymMap() throws SQLException {

		Map<String, Set<String>> map = new HashMap<>();

		String query = "select synonym, hp_id from hp_synonym";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String id = r.getString("hp_id");
				if (!map.containsKey(id)) {
					map.put(id, new HashSet<>());
				}
				map.get(id).add(r.getString("synonym"));
			}

		}

		return map;
	}


	private Integer populateDiseaseTerms() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT 'disease'     AS type, " +
			"  d.disease_id                  AS disease_id, " +
			"  disease_term, " +
			"  disease_alts, " +
			"  disease_locus, " +
			"  disease_classes               AS disease_classes, " +
			"  human_curated, " +
			"  mod_curated                   AS mod_curated, " +
			"  mod_predicted                 AS mod_predicted, " +
			"  htpc_predicted, " +
			"  mod_predicted_in_locus, " +
			"  htpc_predicted_in_locus, " +
			"  mod_predicted_known_gene      AS mod_predicted_known_gene, " +
			"  novel_mod_predicted_in_locus  AS novel_mod_predicted_in_locus, " +
			"  htpc_predicted_known_gene     AS htpc_predicted_known_gene, " +
			"  novel_htpc_predicted_in_locus AS novel_htpc_predicted_in_locus " +
			"FROM disease d " +
			"  JOIN mouse_disease_summary mds ON mds.disease_id = d.disease_id; ";


		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String diseaseId = r.getString("disease_id");

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setDiseaseID(diseaseId);

				doc.setType(r.getString("type"));

				// Disease source is the beginning of the ID
				doc.setDiseaseSource(r.getString("disease_id").split(":")[0]);

				doc.setDiseaseTerm(r.getString("disease_term"));
				doc.setDiseaseAlts(Arrays.asList(r.getString("disease_alts").split("\\|")));
				doc.setDiseaseLocus(r.getString("disease_locus"));
				doc.setDiseaseClasses(Arrays.asList(r.getString("disease_classes").split(",")));

				doc.setHumanCurated(r.getBoolean("human_curated"));
				doc.setMouseCurated(r.getBoolean("mod_curated"));
				doc.setMgiPredicted(r.getBoolean("mod_predicted"));
				doc.setImpcPredicted(r.getBoolean("htpc_predicted"));

				doc.setMgiPredictedKnownGene(r.getBoolean("mod_predicted_known_gene"));
				doc.setImpcPredictedKnownGene(r.getBoolean("htpc_predicted_known_gene"));

				doc.setMgiNovelPredictedInLocus(r.getBoolean("novel_mod_predicted_in_locus"));
				doc.setImpcNovelPredictedInLocus(r.getBoolean("novel_htpc_predicted_in_locus"));

				// Disease Phenotype associations
				doc.setPhenotypes(new ArrayList<>(diseasePhenotypeMap.get(diseaseId)));



				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateGeneSummary() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT" +
			"  'gene'                  AS type, " +
			"  mgo.model_gene_id       AS model_gene_id, " +
			"  model_gene_symbol       AS model_gene_symbol, " +
			"  hgnc_id AS hgnc_gene_id, " +
			"  hgnc_gene_symbol, " +
			"  hgnc_gene_locus         AS hgnc_gene_locus, " +
			"  human_curated, " +
			"  mod_curated             AS mod_curated, " +
			"  mod_predicted           AS mod_predicted, " +
			"  htpc_predicted          AS htpc_predicted, " +
			"  mod_predicted_in_locus  AS mod_predicted_in_locus, " +
			"  htpc_predicted_in_locus AS htpc_predicted_in_locus, " +
			"  mod_model, " +
			"  htpc_model, " +
			"  htpc_phenotype " +
			"FROM mouse_gene_summary mgs " +
			"  JOIN mouse_gene_ortholog mgo ON mgo.model_gene_id = mgs.model_gene_id ";


		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setMarkerAccession(r.getString("model_gene_id"));
				doc.setMarkerSymbol(r.getString("model_gene_symbol"));
				doc.setHgncGeneID(r.getString("hgnc_gene_id"));
				doc.setHgncGeneSymbol(r.getString("hgnc_gene_symbol"));
				doc.setHgncGeneLocus(r.getString("hgnc_gene_locus"));
				doc.setHumanCurated(r.getBoolean("human_curated"));
				doc.setMouseCurated(r.getBoolean("mod_curated"));
				doc.setMgiPredicted(r.getBoolean("mod_predicted"));
				doc.setImpcPredicted(r.getBoolean("htpc_predicted"));
				doc.setModModel(r.getBoolean("mod_model"));
				doc.setHtpcModel(r.getBoolean("htpc_model"));
				doc.setHtpcPhenotype(r.getBoolean("htpc_phenotype"));

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateMouseModel() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT " +
			"  'mouse_model'         AS type, " +
			"  mm.model_id           AS model_id, " +
			"  mgo.model_gene_id     AS model_gene_id, " +
			"  mgo.model_gene_symbol AS model_gene_symbol, " +
			"  mm.source, " +
			"  mm.allelic_composition, " +
			"  mm.genetic_background, " +
			"  mm.allele_ids, " +
			"  mm.hom_het " +
			"FROM mouse_model mm " +
			"  JOIN mouse_model_gene_ortholog mmgo ON mmgo.model_id = mm.model_id " +
			"  JOIN mouse_gene_ortholog mgo ON mgo.model_gene_id = mmgo.model_gene_id ";


		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				Integer mouseId = r.getInt("model_id");

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setModelID(mouseId);
				doc.setSource(r.getString("source"));
				doc.setMarkerAccession(r.getString("model_gene_id"));
				doc.setMarkerSymbol(r.getString("model_gene_symbol"));
				doc.setAllelicComposition(r.getString("allelic_composition"));
				doc.setGeneticBackground(r.getString("genetic_background"));
				doc.setAlleleIds(r.getString("allele_ids"));
				doc.setHomHet(r.getString("hom_het"));

				// Disease Phenotype associations
				if (mousePhenotypeMap.containsKey(mouseId)) {
					doc.setPhenotypes(new ArrayList<>(mousePhenotypeMap.get(mouseId)));
				}

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateHumanPhenotype() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT DISTINCT 'hp' AS type, hp_id, term FROM hp ";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String hpId = r.getString("hp_id");

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setHpID(hpId);
				doc.setHpTerm(r.getString("term"));

				// Disease Phenotype associations
				if(humanSynonymMap.containsKey(hpId))
				doc.setHpSynonym(new ArrayList<>(humanSynonymMap.get(hpId)));

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateMousePhenotype() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT DISTINCT 'mp' AS type, mp_id, term FROM mp ";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String mpId = r.getString("mp_id");

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setMpID(mpId);
				doc.setMpTerm(r.getString("term"));

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateDiseaseGeneSummary() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT" +
			"  'disease_gene_summary'               AS type, " +
			"  d.disease_id, " +
			"  disease_term, " +
			"  disease_alts, " +
			"  disease_locus, " +
			"  disease_classes                      AS disease_classes, " +
			"  mgo.model_gene_id                    AS model_gene_id, " +
			"  mgo.model_gene_symbol                AS model_gene_symbol, " +
			"  hgnc_id, " +
			"  hgnc_gene_symbol, " +
			"  human_curated, " +
			"  mod_curated                          AS mod_curated, " +
			"  in_locus, " +
			"  max_mod_disease_to_model_perc_score  AS max_mod_disease_to_model_perc_score, " +
			"  max_mod_model_to_disease_perc_score  AS max_mod_model_to_disease_perc_score, " +
			"  max_htpc_disease_to_model_perc_score AS max_htpc_disease_to_model_perc_score, " +
			"  max_htpc_model_to_disease_perc_score AS max_htpc_model_to_disease_perc_score, " +
			"  mod_raw_score                        AS raw_mod_score, " +
			"  htpc_raw_score                       AS raw_htpc_score, " +
			"  mod_predicted                        AS mod_predicted, " +
			"  mod_predicted_known_gene             AS mod_predicted_known_gene, " +
			"  novel_mod_predicted_in_locus         AS novel_mod_predicted_in_locus, " +
			"  htpc_predicted                       AS htpc_predicted, " +
			"  htpc_predicted_known_gene            AS htpc_predicted_known_gene, " +
			"  novel_htpc_predicted_in_locus        AS novel_htpc_predicted_in_locus " +
			"FROM " +
			"  mouse_disease_gene_summary_high_quality mdgshq " +
			"  LEFT JOIN disease d ON d.disease_id = mdgshq.disease_id" +
			"  JOIN mouse_gene_ortholog mgo ON mgo.model_gene_id = mdgshq.model_gene_id " ;


		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String diseaseId = r.getString("disease_id");

				if (diseaseId == null) {
					continue;
				}

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setDiseaseID(diseaseId);

				// Disease source is the beginning of the ID
				doc.setDiseaseSource(diseaseId.split(":")[0]);

				doc.setDiseaseTerm(r.getString("disease_term"));
				doc.setDiseaseAlts(Arrays.asList(r.getString("disease_alts").split("\\|")));
				doc.setDiseaseLocus(r.getString("disease_locus"));
				doc.setDiseaseClasses(Arrays.asList(r.getString("disease_classes").split(",")));

				// Disease Phenotype associations
				doc.setPhenotypes(new ArrayList<>(diseasePhenotypeMap.get(diseaseId)));

				doc.setMarkerAccession(r.getString("model_gene_id"));
				doc.setMarkerSymbol(r.getString("model_gene_symbol"));
				doc.setHgncGeneID(r.getString("hgnc_gene_id"));
				doc.setHgncGeneSymbol(r.getString("hgnc_gene_symbol"));

				doc.setHumanCurated(r.getBoolean("human_curated"));
				doc.setMouseCurated(r.getBoolean("mod_curated"));
				doc.setInLocus(r.getBoolean("in_locus"));

				// <!--model organism database (MGI) scores-->
				doc.setMaxMgiD2mScore(r.getDouble("max_mod_disease_to_model_perc_score"));
				doc.setMaxMgiM2dScore(r.getDouble("max_mod_model_to_disease_perc_score"));

				// <!--IMPC scores-->
				doc.setMaxImpcD2mScore(r.getDouble("max_htpc_disease_to_model_perc_score"));
				doc.setMaxImpcM2dScore(r.getDouble("max_htpc_model_to_disease_perc_score"));

				// <!--raw scores-->
				doc.setRawModScore(r.getDouble("raw_mod_score"));
				doc.setRawHtpcScore(r.getDouble("raw_htpc_score"));

				// <!--summary fields for faceting-->
				doc.setMgiPredicted(r.getBoolean("mod_predicted"));
				doc.setImpcPredicted(r.getBoolean("htpc_predicted"));
				doc.setMgiPredictedKnownGene(r.getBoolean("mod_predicted_known_gene"));
				doc.setImpcPredictedKnownGene(r.getBoolean("htpc_predicted_known_gene"));
				doc.setMgiNovelPredictedInLocus(r.getBoolean("novel_mod_predicted_in_locus"));
				doc.setImpcNovelPredictedInLocus(r.getBoolean("novel_htpc_predicted_in_locus"));



				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateDiseaseModelAssociation() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT " +
			"  'disease_model_association'      AS type, " +
			"  mdgshq.disease_id, " +
			"  mmgo.model_gene_id               AS model_gene_id, " +
			"  mmgo.model_id, " +
			"  mdma.lit_model, " +
			"  mdma.disease_to_model_perc_score AS disease_to_model_perc_score, " +
			"  mdma.model_to_disease_perc_score AS model_to_disease_perc_score, " +
			"  mdma.raw_score, " +
			"  mdma.hp_matched_terms, " +
			"  mdma.mp_matched_terms " +
			"FROM mouse_disease_gene_summary_high_quality mdgshq " +
			"  JOIN mouse_model_gene_ortholog mmgo ON mdgshq.model_gene_id = mmgo.model_gene_id " +
			"  JOIN mouse_disease_model_association mdma ON mdgshq.disease_id = mdma.disease_id AND mmgo.model_id = mdma.model_id ";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String diseaseId = r.getString("disease_id");

				if (diseaseId == null) {
					continue;
				}

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setDiseaseID(diseaseId);
				doc.setMarkerAccession(r.getString("model_gene_id"));
				doc.setModelID(r.getInt("model_id"));
				doc.setLitModel(r.getBoolean("lit_model"));
				doc.setDiseaseToModelScore(r.getDouble("disease_to_model_perc_score"));
				doc.setModelToDiseaseScore(r.getDouble("model_to_disease_perc_score"));
				doc.setRawScore(r.getDouble("raw_score"));
				doc.setHpMatchedTerms(Arrays.asList(r.getString("hp_matched_terms").split(",")));
				doc.setMpMatchedTerms(Arrays.asList(r.getString("mp_matched_terms").split(",")));

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateBestHpMpMappings() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT 'hp_mp' AS type, hp_id, hp_term, mp_id, mp_term FROM best_impc_hp_mp_mapping ";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				String hpId = r.getString("hp_id");

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setHpID(hpId);
				doc.setHpTerm(r.getString("hp_term"));
				doc.setMpID(r.getString("mp_id"));
				doc.setMpTerm(r.getString("mp_term"));

				// Human phenotype synonyms
				if (humanSynonymMap.containsKey(hpId)) {
					doc.setHpSynonym(new ArrayList<>(humanSynonymMap.get(hpId)));
				}

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}

	private Integer populateBestMpHpMappings() throws SolrServerException, IOException, SQLException {

		Integer count = 0;
		String query = "SELECT 'mp_hp' AS type, hp_id, hp_term, mp_id, mp_term FROM best_impc_mp_hp_mapping ";

		try (Connection connection = phenodigmDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				PhenodigmDTO doc = new PhenodigmDTO();

				doc.setType(r.getString("type"));
				doc.setHpID(r.getString("hp_id"));
				doc.setHpTerm(r.getString("hp_term"));
				doc.setMpID(r.getString("mp_id"));
				doc.setMpTerm(r.getString("mp_term"));

				phenodigmIndexing.addBean(doc);
				count++;

			}
		}

		return count;
	}



}
