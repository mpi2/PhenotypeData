package org.mousephenotype.cda.solr.bean;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocumentList;

public class ExpressionImagesBean {

	private List<Count> filteredTopLevelAnatomyTerms;
	private Map<String, Boolean> haveImpcImages;
	private Map<String, SolrDocumentList> expFacetToDocs;
	
	
	public ExpressionImagesBean(List<Count> filteredTopLevelAnatomyTerms, Map<String, Boolean> haveImpcImages,
			Map<String, SolrDocumentList> expFacetToDocs) {
		this.filteredTopLevelAnatomyTerms=filteredTopLevelAnatomyTerms;
		this.haveImpcImages=haveImpcImages;
		this.expFacetToDocs=expFacetToDocs;
	}


	public List<Count> getFilteredTopLevelAnatomyTerms() {
		return filteredTopLevelAnatomyTerms;
	}


	public void setFilteredTopLevelAnatomyTerms(List<Count> filteredTopLevelAnatomyTerms) {
		this.filteredTopLevelAnatomyTerms = filteredTopLevelAnatomyTerms;
	}


	public Map<String, Boolean> getHaveImpcImages() {
		return haveImpcImages;
	}


	public void setHaveImpcImages(Map<String, Boolean> haveImpcImages) {
		this.haveImpcImages = haveImpcImages;
	}


	public Map<String, SolrDocumentList> getExpFacetToDocs() {
		return expFacetToDocs;
	}


	public void setExpFacetToDocs(Map<String, SolrDocumentList> expFacetToDocs) {
		this.expFacetToDocs = expFacetToDocs;
	}

}
