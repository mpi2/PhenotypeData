package org.mousephenotype.cda.repositories.solr.parameter;

import java.util.List;


import org.springframework.data.solr.repository.SolrCrudRepository;

interface ParameterRepository extends SolrCrudRepository<Parameter, String> {

	List<Parameter> findByStableId(String stableId);
    
}
