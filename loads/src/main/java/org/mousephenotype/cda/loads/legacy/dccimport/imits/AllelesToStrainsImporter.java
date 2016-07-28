/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

//import org.apache.solr.client.solrj.response.PivotField;

/**
 *
 * Retrieve allele/strain information from imits to update EMMA
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since June 2013
 */

public class AllelesToStrainsImporter {

	AllelesToStrainsImporter() {
		SolrServer server = getSolrServer();
	}

	public static SolrServer getSolrServer() {
		String url = "http://ves-ebi-d0.ebi.ac.uk:8090/mi/solr/allele";
		SolrServer server = new HttpSolrServer( url );
		return server;
	}

	public static void main(String[] args) {

		AllelesToStrainsImporter importer = new AllelesToStrainsImporter();

		try {
				importer.queryAttempts();
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public void queryAttempts() throws KeyManagementException, NoSuchAlgorithmException {

		ImitsRestClient client = new ImitsRestClient();
		List<MicroInjectionAttemptBean> miAttempts = client.getMicroInjectionAttempts("WTSI");

	}

	public void queryAlleles() throws SolrServerException {


		SolrServer server = getSolrServer();

		SolrQuery query = new SolrQuery();
	    query.setQuery( "*:*" );
	    QueryResponse response = server.query( query );

	    SolrDocumentList results = response.getResults();
	    long nbResults = results.getNumFound();
	    System.out.println(results.getNumFound() + "documents found");
	    for (int i=0; i<10; i++) {
	    	SolrDocument doc = results.get(i);
	    	Integer alleleId = (Integer) doc.getFieldValue("allele_id");
	    	if (alleleId != null) {
	    		String alleleName = (String) doc.getFieldValue("allele_name");
	    		System.out.println(alleleId + " " + alleleName);
	    		if (alleleName != null && alleleName.contains("EUCOMM") || alleleName.contains("KOMP")) {
	    			// connect to REST interface.
	    		}
	    	}
	    }




	}
}
