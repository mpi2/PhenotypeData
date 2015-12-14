package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;

public interface WebStatus {

	public long getWebStatus() throws SolrServerException;
	public String getServiceName();
}
