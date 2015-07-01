package org.mousephenotype.cda.repositories.solr.parameter;

import java.util.List;

public interface ParameterService {

	public List<Parameter> findByStableId(String stableId);
}
