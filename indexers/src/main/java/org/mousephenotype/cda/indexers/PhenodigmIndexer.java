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

	Set<String> mgiAccessionIdSet = new HashSet();
	Map<String, Set<String>> diseasePhenotypeMap = new HashMap<>();


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
		Set<String> noTermSet = new HashSet<>();
		RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try {

			diseasePhenotypeMap = getDiseasePhenotypeMap();
			logger.info("Populated disease phenotype map");

			phenodigmIndexing.deleteByQuery("*:*");

			populateDiseaseTerms();

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


	private void populateDiseaseTerms() throws SolrServerException, IOException, SQLException {

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
				documentCount++;

			}
		}
	}

}
