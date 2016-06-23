package org.mousephenotype.cda.solr.repositories.parameter;


import java.util.List;

public interface ParameterService {

	public List<Parameter> findByStableId(String stableId);
}
