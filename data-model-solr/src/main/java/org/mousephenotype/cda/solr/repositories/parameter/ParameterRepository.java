package org.mousephenotype.cda.solr.repositories.parameter;

import java.util.List;


import org.springframework.data.solr.repository.SolrCrudRepository;

interface ParameterRepository extends SolrCrudRepository<Parameter, String> {

	List<Parameter> findByStableId(String stableId);
    
}
