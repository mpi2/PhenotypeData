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

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.List;

/**
 * Interface for images data access from solr (probably not need an interface to
 * this as it's solr specific...)
 * 
 * @author jwarren
 * 
 */
public interface ImagesSolrDao {

	public abstract long getNumberFound();

	public abstract List<String> getIdsForKeywordsSearch(
			String query,
			int start, 
			int length) throws SolrServerException, IOException;

	public abstract QueryResponse getExperimentalFacetForGeneAccession(
			String geneId)
			throws SolrServerException, IOException;

	public QueryResponse getExpressionFacetForGeneAccession(
			String geneId)
			throws SolrServerException, IOException;

	public QueryResponse getDocsForGeneWithFacetField(
			String query,
			String facetName, 
			String facetValue, 
			String filterQuery, 
			int start,
			int length) 
			throws SolrServerException, IOException;

	public QueryResponse getFilteredDocsForQuery(
			String query,
			List<String> filterField, 
			String qf, 
			String defType, 
			int start,
			int length) 
			throws SolrServerException, IOException;

	public QueryResponse getDocsForMpTerm(
			String mpId, 
			int start, 
			int length)
			throws SolrServerException, IOException;

	public SolrDocumentList getImagesForLacZ()
			throws SolrServerException, IOException;
}