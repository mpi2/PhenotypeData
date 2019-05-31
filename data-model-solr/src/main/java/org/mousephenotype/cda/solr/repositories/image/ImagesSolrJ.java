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
package org.mousephenotype.cda.solr.repositories.image;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Class that gets image data from the solr images index
 * @author jwarren
 */
@Service
public class ImagesSolrJ implements ImagesSolrDao,  WebStatus{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private long numberFound;

	@Autowired
	@Qualifier("sangerImagesCore")
	public SolrClient server;

//	public ImagesSolrJ(String solrBaseUrl) throws MalformedURLException {
//
//		// Use system proxy if set
//		if (System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyPort") != null) {
//
//			String PROXY_HOST = System.getProperty("http.proxyHost");
//			Integer PROXY_PORT = Integer.parseInt(System.getProperty("http.proxyPort"));
//			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http");
//			DefaultHttpClient client = new DefaultHttpClient();
//			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//			server = new HttpSolrClient(solrBaseUrl, client);
//
//			log.debug("Proxy Settings: " + System.getProperty("http.proxyHost") + " on port: " + System.getProperty("http.proxyPort"));
//		} else {
//			server = new HttpSolrClient(solrBaseUrl);
//		}
//	}


	public long getNumberFound() {
		return numberFound;
	}

	private void setNumberFound(long numberFound) {
		this.numberFound = numberFound;
	}


	public List<String> getIdsForKeywordsSearch(String query, int start, int length) throws SolrServerException, IOException {
		return this.getIds(query, start, length);
	}

	private List<String> getIds(String query, int start, int length) throws SolrServerException, IOException {

		SolrDocumentList result = runQuery(query, start, length);

		log.debug("number found=" + result.getNumFound());
		this.setNumberFound(result.getNumFound());
		if (result.size() > 0) {
			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < result.size(); i++) {
				SolrDocument doc = result.get(i);
				ids.add((String) doc.getFieldValue("id"));
			}
			return ids;

		}
		return Collections.emptyList();

	}

	private SolrDocumentList runQuery(String query, int start, int length) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();

		log.debug("solr query=" + query);
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		solrQuery.setFields("id");

		QueryResponse rsp = null;
		rsp = server.query(solrQuery);

		return rsp.getResults();
	}

	private QueryResponse runFacetQuery(String query, String facetField, int start, int length, String filterQuery) 
	throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		solrQuery.setFacet(true);
		solrQuery.setFacetMinCount(1);
		solrQuery.addFacetField(facetField);
		if (filterQuery != "") {
			solrQuery.addFilterQuery(filterQuery);
		}
	
		return server.query(solrQuery);

	}

	@Override
	public QueryResponse getExperimentalFacetForGeneAccession(String geneId) 
	throws SolrServerException, IOException {
	
		String processedGeneId = processQuery(geneId);
		QueryResponse solrResp = this.runFacetQuery(SangerImageDTO.MGI_ACCESSION_ID + ":" + processedGeneId, "expName", 0, 1, "");
		
		return solrResp;
		
	}

	@Override
	public QueryResponse getExpressionFacetForGeneAccession(String geneId) 
	throws SolrServerException, IOException {
		
		String processedGeneId = processQuery(geneId);
		log.debug("eventually gene id will be here and we'll need an extra filter");
		//changed facet field from annotated_or_inferred_higherLevelMaTermName to as old field not there anymore higherLevelMaTermName
		QueryResponse solrResp = this.runFacetQuery("expName:"+"\"Wholemount Expression\"","selected_top_level_ma_term", 0,5, "accession:"+processedGeneId);
	
		return solrResp;
		
	}

	//TODO cleanup this method
	public QueryResponse getDocsForGeneWithFacetField(String geneId, String facetName, String facetValue, String filterQuery, int start, int length) 
	throws SolrServerException, IOException{

		SolrQuery solrQuery = new SolrQuery();

		String processedGeneId = processQuery(geneId);
		String query="accession:"+processedGeneId;

		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);

		// need to add quotes around so that spaces are allowed
		String facetQuery = facetName + ":" + processValueForSolr(facetValue);
		solrQuery.addFilterQuery(facetQuery);

		log.debug("facet name and val===="+facetQuery);

		if (filterQuery != "") {
			solrQuery.addFilterQuery(filterQuery);
		}

		return server.query(solrQuery);
	}


	/**
	 * Put quotes around any query that contains spaces with the following
	 * restrictions
	 *
	 * 1) Not the wildcard character
	 * 2) Not already quoted
	 *
	 * @param value the string to be quoted if necessary
	 * @return the processed string
	 */
	public String processValueForSolr(String value) {

		// If the string contains only the wildcard search, return it
		if(value.equals("*")) {
			return value;
		}

		// If the string is already quoted, return it
		if ((value.contains(" ") && // Contains space and
			value.startsWith("\"") &&
			value.endsWith("\"") // is already quoted
			)) {
			return value;
		}

		// If the string contains spaces, and it's not already quoted,
		// quote the string, escape all the double quotes, and return it
		if (value.contains(" ") && // Contains space and
			value.contains("\"") && // Contains quote and is
			! value.contains("\\\"") // not already escaped
			) {

			// Escape all already existing double quotes if they are not
			// at the beginning or end of the string
			return "\"" + value.replaceAll("\"", "\\\\\"") + "\"";
		}

		// If we're here, all special cases should be dealt with already
		// If the value contains a space, quote and return it
		if (value.contains(" ")) {
			return "\"" + value + "\"";
		}

		return value;
	}
	
	/**
	 * @since 2016/02/25
	 * @author ilinca
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public SolrDocumentList getImagesForLacZ()
	throws SolrServerException, IOException{
		
		SolrQuery query = new SolrQuery();
		query.setQuery("procedure_name:*LacZ*");
		query.addFilterQuery("ma_id:*");
		query.addFilterQuery("accession:*");
		query.addField("ma_id");
		query.addField("accession");
		query.setRows(Integer.MAX_VALUE);
		return server.query(query).getResults();
	}
	

	/**
	 * Get all SOLR documents for a mammalian phenotype ontology term
	 *
	 * @param mpId the phenotype term to query for
	 * @param start offset from zero
	 * @param length how many docs to return
	 */
	public QueryResponse getDocsForMpTerm(String mpId, int start, int length) throws SolrServerException, IOException {

		String id = processQuery(mpId);
		String query = "mp_id:" + id;

		log.debug("solr query=" + query);

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);
		
		solrQuery.setFields(ImageDTO.FULL_RESOLUTION_FILE_PATH, "smallThumbnailFilePath", "largeThumbnailFilePath",
				"institute", "gender", "genotype", "maTerm", "annotationTermName", "genotypeString");
		
		return server.query(solrQuery);
	}


	/**
	 * Returns documents from Solr Index filtered by the passed in query
	 * filterField e.g. annotationTerm:large ear
	 * @throws SolrServerException, IOException
	 *
	 *
	 */
	public QueryResponse getFilteredDocsForQuery(String query, List<String> filterFields, String qf, String defType, int start, int length) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();

		//if query field is set (such as auto_suggest), add this to the search
		if( ! qf.equals("")){
			solrQuery.set("qf", qf);
			log.debug("added qf=" + qf);
		}

		//if defType is set (such as edismax), add this to the search
		if( ! defType.equals("")){
			solrQuery.set("defType", defType);
			log.debug("set defType=" + defType);
		}

		solrQuery.setQuery(query);
		solrQuery.setStart(start);
		solrQuery.setRows(length);

		for(String fieldNValue : filterFields){
			String [] colonStrings = fieldNValue.split(":");
			String filterField = colonStrings[0];
			String filterParam = fieldNValue.substring(
				fieldNValue.indexOf(":")+1,
				fieldNValue.length());

			filterParam = processValueForSolr(filterParam);
			String fq = filterField + ":" + filterParam;

			log.debug("adding filter fieldNValue=" + fq);

			solrQuery.addFilterQuery(fq);
		}

		log.debug("Query is: "+solrQuery.toString());

		return server.query(solrQuery);
	}

	public static String processQuery(String id) {

		String processedId = id;

		// Quote the ID if it hasn't been already
		if (processedId.contains(":") && !processedId.contains("\\")) {
			processedId = "\"" + processedId + "\"";
		}

		// put quotes around any query that contains spaces
		if (processedId.contains(" ")) {
			processedId = "\"" + processedId + "\"";
		}

		return processedId;
	}
	
	@Override
	public long getWebStatus() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = server.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName(){
		return "Sanger Image Service";
	}
}