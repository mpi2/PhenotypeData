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
package org.mousephenotype.cda.solr.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class DiseaseService implements WebStatus{

    @Autowired
    @Qualifier("diseaseCore")
    private HttpSolrClient solr;

    // Disease sources. When modifying these, please modify getAllDiseases() accordingly.
    public static final class DiseaseField {
        public final static String DISEASE_ID = "disease_id";
        public final static String DISEASE_SOURCE = "disease_source";
        public final static String DISEASE_SOURCE_DECIPHER = "DECIPHER";
        public final static String DISEASE_SOURCE_OMIM = "OMIM";
        public final static String DISEASE_SOURCE_ORPHANET = "ORPHANET";
    }


    public DiseaseService() {
    }


    public DiseaseService(String solrUrl) {
        solr = new HttpSolrClient(solrUrl);
    }

    /**
     * @return all diseases from the disease core.
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllDiseases() throws SolrServerException, IOException  {
        Set<String> results = new HashSet<String>();

        String[] diseaseSources = { DiseaseField.DISEASE_SOURCE_DECIPHER, DiseaseField.DISEASE_SOURCE_OMIM, DiseaseField.DISEASE_SOURCE_ORPHANET };
        for (String diseaseSource : diseaseSources) {
            results.addAll(getAllDiseasesInDiseaseSource(diseaseSource));
        }

        return results;
    }

    /**
     * @return all diseases in the specified <code>diseaseSource</code> (see
     * public string definitions) from the disease core.
     * @param diseaseSource the desired disease source (e.g. DiseaseService.OMIM,
     * DiseaseSource.ORPHANET, etc.)
     *
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllDiseasesInDiseaseSource(String diseaseSource) throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("disease_source:\"" + diseaseSource + "\"");
        solrQuery.setFields("disease_id");
        solrQuery.setRows(1000000);
        QueryResponse rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allDiseases = new HashSet<String>();
        for (SolrDocument doc : res) {
            allDiseases.add((String) doc.getFieldValue(DiseaseField.DISEASE_ID));
        }
        return allDiseases;
    }
    
    @Override
	public long getWebStatus() throws SolrServerException, IOException  {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "Disease Service";
	}

}
