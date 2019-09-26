package org.mousephenotype.cda.solr.repositories;

import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Support accessing the solr index using spring data
 */
public interface StatisticalResultRepository extends SolrCrudRepository<StatisticalResultDTO, String> {

	List<StatisticalResultDTO> findByParameterStableId(String stableId);
	List<StatisticalResultDTO> findByMarkerAccessionIdAndParameterStableIdAndProcedureStableIdAndPhenotypeSexInAndMpTermIdOptionsIn(String markerAccessionId, String parameterStableId, String procedureStableId, String Sex, String mpTermId);
	List<StatisticalResultDTO> findByMarkerAccessionIdAndParameterStableIdAndProcedureStableIdAndMpTermId(String markerAccessionId, String parameterStableId, String procedureStableId, String mpTermId);
}