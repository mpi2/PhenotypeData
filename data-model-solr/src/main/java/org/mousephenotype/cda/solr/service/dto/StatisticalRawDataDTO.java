package org.mousephenotype.cda.solr.service.dto;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Data
@SolrDocument(solrCoreName = "statistical-raw-data")
public class StatisticalRawDataDTO {
    public final static String DOCUMENT_ID = "doc_id";
    public final static String RAW_DATA = "raw_data";

    @Id
    @Field(DOCUMENT_ID)
    private String docId;

    @Field(RAW_DATA)
    private String rawData;
}
