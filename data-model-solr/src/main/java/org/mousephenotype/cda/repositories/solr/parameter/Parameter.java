package org.mousephenotype.cda.repositories.solr.parameter;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "pipeline")
public class Parameter implements ParameterDefinition {

	@Indexed(STABLE_ID_FIELD_NAME)
	String stableId;

	public String getStableId() {
		return stableId;
	}

	public void setStableId(String stableId) {
		this.stableId = stableId;
	}

	@Id
	@Indexed
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
