package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Search for genes in the gene core and return minimal information needed for search results
 *
 * @author jwarren
 */

@Service
public class SearchGeneService {

    @Autowired
    @Qualifier("geneCore")
    private SolrClient geneCore;


    @Inject
    public SearchGeneService(SolrClient geneCore) {
        this.geneCore = geneCore;
    }

    public SearchGeneService() {

    }


    /**
     * Return all genes from the gene core filtered by keyword
     *
     * @param rows how many rows to return
     * @param start how much to offset from the beginning
     * @return solr QueryResponse object representing the results of the query
     */
    public QueryResponse searchGenes(String keywords, Integer start, Integer rows) throws SolrServerException, IOException {

        String search = keywords;
        if (search.contains(":")) {
            search = search.replace(":", "\\:");
        }

        //current query used by BZ is just taken from old one which has DisMax and boost in the URL (boosts could be in solr config as defaults??)
        //https://www.ebi.ac.uk/mi/impc/solr/gene/select?facet.field=latest_phenotype_status&facet.field=legacy_phenotype_status&facet.field=status&facet.field=latest_production_centre&facet.field=latest_phenotyping_centre&facet.field=marker_type&facet.field=embryo_data_available&facet.field=embryo_modalities&facet.field=embryo_analysis_view_name&fl=marker_symbol,mgi_accession_id,marker_synonym,marker_name,marker_type,human_gene_symbol,latest_es_cell_status,latest_production_status,latest_phenotype_status,status,es_cell_status,mouse_status,legacy_phenotype_status,allele_name&fq=+*:*&rows=10&bq=marker_symbol_lowercase:(akt*)^1000+marker_symbol_bf:(akt*)^100+latest_phenotype_status:%22Phenotyping+Complete%22+^200&q=akt*&facet.limit=-1&defType=edismax&qf=geneQf&wt=json&indent=true&facet=on&facet.sort=count&start=0

        final SolrQuery query = new SolrQuery(search);
        query.add("defType", "edismax");
        query.setFields(GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_NAME, GeneDTO.MARKER_SYMBOL, GeneDTO.MARKER_SYNONYM, GeneDTO.HUMAN_GENE_SYMBOL, GeneDTO.LATEST_ES_CELL_STATUS, GeneDTO.LATEST_MOUSE_STATUS, GeneDTO.LATEST_PHENOTYPE_STATUS);
        query.add("bq", "latest_phenotype_status:\"Phenotyping Complete\"^200");
        query.add("bq", "marker_symbol_lowercase:(" + search + "*)^1000");
        query.add("bq", "marker_symbol_bf:(" + search + "*)^100");
        query.add("qf", "geneQf");
        query.setStart(start);
        query.setRows(rows);

        return geneCore.query(query);
    }


    public QueryResponse searchSuggestions(String keyword, Integer distance) throws IOException, SolrServerException {

        String search = keyword.isEmpty() ? "*:*" : keyword.replaceAll("\\s", "") + "~" + distance;

        final SolrQuery query = new SolrQuery(search);
        query.add("qf", GeneDTO.MARKER_SYMBOL + " " + GeneDTO.MGI_ACCESSION_ID);
        query.setFields(GeneDTO.MARKER_SYMBOL);
        query.setRows(3);
        query.setSort("score", SolrQuery.ORDER.desc);
        query.add("defType", "edismax");

        return geneCore.query(query);
    }
}