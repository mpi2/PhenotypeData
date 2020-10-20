package org.mousephenotype.cda.solr.repositories;

import org.mousephenotype.cda.solr.service.dto.StatisticalRawDataDTO;
import org.springframework.data.solr.repository.SolrCrudRepository;

public interface StatisticalRawDataRepository extends SolrCrudRepository<StatisticalRawDataDTO, String> {

    StatisticalRawDataDTO findStatisticalRawDataDTOByDocId(String docId);
}
