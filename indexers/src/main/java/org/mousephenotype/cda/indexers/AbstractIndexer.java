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
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.enumerations.LifeStage;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.LifeStageMapper;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

@SpringBootApplication
public abstract class AbstractIndexer implements CommandLineRunner {

    @Value("${owlpath}")
    protected String owlpath;

    private final   Logger  logger    = LoggerFactory.getLogger(this.getClass());

    protected final int     MINIMUM_DOCUMENT_COUNT = 80;

    protected DataSource             komp2DataSource;
    protected OntologyTermRepository ontologyTermRepository;
    private Map<LifeStage, OntologyTerm> lifeStageOntologyTermMap = new HashMap<>();

    protected AbstractIndexer() {

    }

    @Inject
    public AbstractIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository)
    {
        this.komp2DataSource = komp2DataSource;
        this.ontologyTermRepository = ontologyTermRepository;
    }


    public DataSource getKomp2DataSource() {
        return komp2DataSource;
    }

    public void setKomp2DataSource(DataSource komp2DataSource) {
        this.komp2DataSource = komp2DataSource;
    }

    public OntologyTermRepository getOntologyTermRepository() {
        return ontologyTermRepository;
    }

    public void setOntologyTermRepository(OntologyTermRepository ontologyTermRepository) {
        this.ontologyTermRepository = ontologyTermRepository;
    }

    CommonUtils commonUtils = new CommonUtils();

	// This is used to track the number of documents that were requested to be added by the core.addBeans() call.
    // It is used for later validation by querying the core after the build.
    protected long expectedDocumentCount = 0L;

	@Override
	public void run(String... strings) throws Exception {

		run();
	}

	public abstract RunStatus run() throws IndexerException, IOException, SolrServerException, SQLException, URISyntaxException;

    public abstract RunStatus validateBuild() throws IndexerException;

    public long getDocumentCount(SolrClient solrClient) throws IndexerException {
        long numFound;
        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
        try {
            numFound = solrClient.query(query).getResults().getNumFound();
        } catch (SolrServerException | IOException e) {
            throw new IndexerException(e);
        }
        logger.debug("number found = " + numFound);
        return numFound;
    }

    public long getImitsDocumentCount(SolrClient solrClient) throws IndexerException {
        long numFound = getDocumentCount(solrClient);
        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
        query.setRequestHandler("selectCre");
        try {
            numFound += solrClient.query(query).getResults().getNumFound();
        } catch (SolrServerException | IOException e) {
            throw new IndexerException(e);
        }
        logger.debug("number found = " + numFound);
        return numFound;
    }


    public long getFacetCountTwoLevelPivot(SolrClient solr, SolrQuery q, String pivot) throws IOException, SolrServerException {

        long count = 0L;
        List<PivotField> facetPivots = solr.query(q).getFacetPivot().get(pivot);

        for( PivotField p : facetPivots){
            // TODO: FIXME ?
            List<String> secondLevelFacets = new ArrayList<>();
            count += secondLevelFacets.size();
        }

        return count;
    }


	public void initialise() {

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
     */
    protected RunStatus validateBuild(SolrClient solrClient) throws IndexerException {
        long actualDocumentCount = getDocumentCount(solrClient);
        RunStatus runStatus = new RunStatus();

        if (actualDocumentCount <= MINIMUM_DOCUMENT_COUNT) {
            runStatus.addWarning("SOLR DOCUMENT COUNT VALIDATION: Expected at least " + MINIMUM_DOCUMENT_COUNT + ". Actual:" + actualDocumentCount);
        }

        if (actualDocumentCount != expectedDocumentCount) {
            runStatus.addWarning("SOLR DOCUMENT COUNT VALIDATION: Expected " + expectedDocumentCount + ". Actual:" + actualDocumentCount);
        }

        return runStatus;
    }

    /**
     * Method for allele2 and product core indexins. Helper methods to parse the tsv file.
     * @param field The field name to lookup
     * @param array the values corresponding to the columns
     * @param columns map of columns names to positional indexes
     * @return the value associated with the field passed in
     */
    protected String getValueFor (String field, String[] array, Map<String, Integer> columns, RunStatus runStatus){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()) {
                return null;
            } else if (el.equals("\"\"")){
                return "";
            }
            return el;
        } else {
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
            return Boolean.valueOf(el);
        } else {
            logger.debug("Field not found " + field);
            runStatus.addError(" Caught error accessing Allele2 core: " + "Field not found " + field );
            return null;
        }
    }

    // Method for allele2 and product core indexins. Helper methods to parte the tsv file.
    protected List<String> getListValueFor (String field, String[] array, Map<String, Integer> columns, RunStatus runStatus){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return Arrays.asList(el.split("\\|", -1));
        } else {
            logger.debug("Field not found " + field);
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
            try {
				return new Integer(el);
			} catch (NumberFormatException e) {
                logger.debug("field not string is ="+el);
				e.printStackTrace();
				return null;
			}
        } else {
            logger.debug("Field not found " + field);
            runStatus.addError(" Caught error accessing Allele2 core: " + "Field not found " + field );
            return null;
        }
    }

    protected void doLiveStageLookup() {

        synchronized (this) {

            lifeStageOntologyTermMap.put(LifeStage.E9_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E9_5.getName(), "IMPC"));
            lifeStageOntologyTermMap.put(LifeStage.E12_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E12_5.getName(), "IMPC"));
            lifeStageOntologyTermMap.put(LifeStage.E15_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E15_5.getName(), "IMPC"));
            lifeStageOntologyTermMap.put(LifeStage.E18_5, ontologyTermRepository.getByTermNameAndShortName(LifeStage.E18_5.getName(), "IMPC"));
            lifeStageOntologyTermMap.put(LifeStage.EARLY_ADULT, ontologyTermRepository.getByTermNameAndShortName(LifeStage.EARLY_ADULT.getName(), "IMPC"));
            lifeStageOntologyTermMap.put(LifeStage.MIDDLE_AGED_ADULT, ontologyTermRepository.getByTermNameAndShortName(LifeStage.MIDDLE_AGED_ADULT.getName(), "IMPC"));
            lifeStageOntologyTermMap.put(LifeStage.LATE_ADULT, ontologyTermRepository.getByTermNameAndShortName(LifeStage.LATE_ADULT.getName(), "IMPC"));

        }
    }


    protected OntologyTerm getLifeStage(String parameterStableId) {

        // Populate the live specimen life stage map if it is empty
        if (lifeStageOntologyTermMap == null || lifeStageOntologyTermMap.size() == 0) {
            doLiveStageLookup();
        }

        LifeStage ls = LifeStageMapper.getLifeStage(parameterStableId);
        return lifeStageOntologyTermMap.get(ls);
    }
}