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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.PivotField;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Matt Pearce
 */

@SpringBootApplication
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public abstract class AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(AbstractIndexer.class);

    protected static final int MINIMUM_DOCUMENT_COUNT = 80;

	CommonUtils commonUtils = new CommonUtils();

	// This is used to track the number of documents that were requested to be added by the core.addBeans() call.
    // It is used for later validation by querying the core after the build.
    protected int documentCount = 0;

	@Override
	public void run(String... strings) throws Exception {

		run();
	}

	public abstract RunStatus run() throws IndexerException, IOException, SolrServerException, SQLException;

    public abstract RunStatus validateBuild() throws IndexerException;

    public long getDocumentCount(SolrClient solrClient) throws IndexerException {
        Long numFound = 0L;
        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
        try {
            numFound = solrClient.query(query).getResults().getNumFound();
        } catch (SolrServerException | IOException e) {
            throw new IndexerException(e);
        }
        logger.debug("number found = " + numFound);
        return numFound;
    }


    public long getFacetCountTwoLevelPivot(SolrClient solr, SolrQuery q, String pivot) throws IOException, SolrServerException {

        Long count = new Long(0);
        List<PivotField> facetPivots = solr.query(q).getFacetPivot().get(pivot);

        for( PivotField p : facetPivots){
            List<String> secondLevelFacets = new ArrayList<>();
            count += secondLevelFacets.size();
        }

        return count;
    }


	public void initialise() throws IndexerException {

		printConfiguration();
	}

    /**
     * This is a hook for extended classes to implement to print their
     * configuration - e.g. source and target solr urls, batch values, etc.
     *
     * The intention is to someday make this abstract to insure all implementors
     * provide a printConfiguration method specific to their indexer.
     */
    protected void printConfiguration() {

    }

    /**
     * Common core validator
     *
     * @param solrClient the solr server to be validated
     * @return <code>RunStatus</code> indicating success, failure, or warning.
     * @throws IndexerException
     */
    protected RunStatus validateBuild(SolrClient solrClient) throws IndexerException {
        Long actualSolrDocumentCount = getDocumentCount(solrClient);
        RunStatus runStatus = new RunStatus();

        if (actualSolrDocumentCount <= MINIMUM_DOCUMENT_COUNT) {
            runStatus.addError("Expected at least " + MINIMUM_DOCUMENT_COUNT + " documents. Actual count: " + actualSolrDocumentCount + ".");
        }

        if (actualSolrDocumentCount != documentCount) {
            runStatus.addWarning("SOLR reports " + actualSolrDocumentCount + ". Actual count: " + documentCount);
        }

        return runStatus;
    }

    /**
     * Method for allele2 and product core indexins. Helper methods to parte the tsv file.
     * @param field
     * @param array
     * @param columns
     * @return
     */
    protected String getValueFor (String field, String[] array, Map<String, Integer> columns, RunStatus runStatus){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            } else if (el.equals("\"\"")){
                return "";
            }
            return el;
        } else {
            System.out.println();
            runStatus.addError(" Caught error accessing Allele2 core: " + "Field not found " + field );
            return null;
        }
    }

    // Method for allele2 and product core indexins. Helper methods to parte the tsv file.
    protected Boolean getBooleanValueFor (String field, String[] array, Map<String, Integer> columns, RunStatus runStatus){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return new Boolean(el);
        } else {
            System.out.println("Field not found " + field);
            runStatus.addError(" Caught error accessing Allele2 core: " + "Field not found " + field );
            return null;
        }
    }

    // Method for allele2 and product core indexins. Helper methods to parte the tsv file.
    protected List<String> getListValueFor (String field, String[] array, Map<String, Integer> columns, RunStatus runStatus){

        List<String> list = new ArrayList<>();

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return Arrays.asList(el.split("\\|", -1));
        } else {
            System.out.println("Field not found " + field);
            runStatus.addError(" Caught error accessing Allele2 core: " + "Field not found " + field );
            return null;
        }
    }

    // Method for allele2 and product core indexins. Helper methods to parte the tsv file.
    protected Integer getIntValueFor (String field, String[] array, Map<String, Integer> columns, RunStatus runStatus){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return new Integer(el);
        } else {
            System.out.println("Field not found " + field);
            runStatus.addError(" Caught error accessing Allele2 core: " + "Field not found " + field );
            return null;
        }
    }


}
