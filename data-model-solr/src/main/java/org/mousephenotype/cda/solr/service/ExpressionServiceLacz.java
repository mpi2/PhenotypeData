package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service
public class ExpressionServiceLacz {

    private SolrClient experimentCore;

    @Inject
    public ExpressionServiceLacz(
            SolrClient experimentCore)
    {
        super();
        this.experimentCore = experimentCore;
    }

    /**
     * DO NOT MOVE THIS METHOD TO ExpressionService CLASS. IT WILL BREAK SPRING CACHING
     *
     * @param mgiAccession if mgi accession null assume a request for control data
     */
    @Cacheable("categoricalLaczData")
    public QueryResponse getCategoricalAdultLacZData(String mgiAccession, boolean embryo, String... fields)
            throws SolrServerException, IOException {

        // e.g.
        // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id:%22MGI:1351668%22&facet=true&facet.field=parameter_name&facet.mincount=1&fq=(procedure_name:%22Adult%20LacZ%22)&rows=10000
        SolrQuery solrQuery = new SolrQuery();
        if (mgiAccession != null) {
            solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
        } else {
            // http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=biological_sample_group:control&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)&rows=100000
            solrQuery.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"" + "control" + "\"");
        }
        if (embryo) {
            solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Embryo LacZ\"");
            solrQuery.addFilterQuery("-" + ImageDTO.PARAMETER_NAME + ":\"LacZ images section\"");
            solrQuery.addFilterQuery("-" + ImageDTO.PARAMETER_NAME + ":\"LacZ images wholemount\"");
            solrQuery.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"");
        } else {
            solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"");
            solrQuery.addFilterQuery("-" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\"");
            solrQuery.addFilterQuery("-" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Wholemount\"");
            solrQuery.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"");
        }

        solrQuery.addSort(ImageDTO.ID, SolrQuery.ORDER.asc);
        solrQuery.setFields(fields);
        solrQuery.setRows(Integer.MAX_VALUE);
        solrQuery.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        QueryResponse response = experimentCore.query(solrQuery);
        return response;
    }

}
