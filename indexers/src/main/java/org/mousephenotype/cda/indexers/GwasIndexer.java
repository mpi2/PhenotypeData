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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.db.dao.GwasDTO;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.ValidationException;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mousephenotype.cda.db.dao.OntologyDAO.BATCH_SIZE;

/**
 * Populate the GWAS core
 */
@EnableAutoConfiguration
public class GwasIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(GwasIndexer.class);

	@Autowired
	@Qualifier("admintoolsDataSource")
	private DataSource admintoolsDataSource;

    @Autowired
	private GwasDAO gwasDao;

    @Autowired
    @Qualifier("gwasCore")
    SolrClient gwasCore;

    private List<GwasDTO> gwasMappings = new ArrayList<>();

    public GwasIndexer() {

    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        Integer numFound;
        RunStatus runStatus = new RunStatus();

		try {
			numFound = getDocCount();
			if (numFound <= MINIMUM_DOCUMENT_COUNT)
	            throw new IndexerException(new ValidationException("Actual gwas document count is " + numFound + "."));

	        if (numFound != documentCount)
	            runStatus.addWarning(" WARNING: Added " + documentCount + " gwas documents but SOLR reports " + numFound + " documents.");

		} catch (SQLException e) {
			e.printStackTrace();
            throw new IndexerException(e);
		}

        return runStatus;
    }

    public Integer getDocCount() throws SQLException {

        // rows of impc to GWAS mapping
    	List<GwasDTO> gwasMappings = gwasDao.getGwasMappingRows();
    	int rows = gwasMappings.size();
    	return rows;
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        try {
            gwasCore.deleteByQuery("*:*");
            gwasCore.commit();

            //initialiseSupportingBeans();

            List<GwasDTO> gwasBatch = new ArrayList(BATCH_SIZE);

            // Add all ma terms to the index.
            List<GwasDTO> gwasMappings = gwasDao.getGwasMappingRows();

            String mpIdOwlBaseUrl = "";
            for (GwasDTO gw : gwasMappings) {

                count ++;

                gwasBatch.add(gw);
                if (gwasBatch.size() == BATCH_SIZE) {
                    // Update the batch, clear the list
                    documentCount += gwasBatch.size();
                    gwasCore.addBeans(gwasBatch, 60000);
                    gwasBatch.clear();
                }
            }

            // Make sure the last batch is indexed
            if (gwasBatch.size() > 0) {
                documentCount += gwasBatch.size();
                gwasCore.addBeans(gwasBatch, 60000);
                count += gwasBatch.size();
            }

            // Send a final commit
            gwasCore.commit();

        } catch (SQLException | SolrServerException| IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }


    // PROTECTED METHODS


    @Override
    protected void printConfiguration() {
        if (logger.isDebugEnabled()) {
            logger.debug(" WRITING Gwas     CORE TO: " + SolrUtils.getBaseURL(gwasCore));
        }
    }


    // PRIVATE METHODS


    private final Integer MAX_ITERATIONS = 2;                                   // Set to non-null value > 0 to limit max_iterations.

    public static void main(String[] args) throws IndexerException, SQLException {
        SpringApplication.run(AlleleIndexer.class, args);
    }
}
