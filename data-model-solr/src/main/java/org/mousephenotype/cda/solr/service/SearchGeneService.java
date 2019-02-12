package org.mousephenotype.cda.solr.service;

import java.io.IOException;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Search for genes in the gene core and return minimal information needed for search results
 * @author jwarren
 *
 */
		
@Service
public class SearchGeneService {

	@Autowired
	@Qualifier("geneCore")
	private SolrClient solr;

    @NotNull
    @Value("${base_url}")
    private String baseUrl;
    
    /**
	 * Return all genes from the gene core.
     * @param rows 
     * @param start 
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse searchGenes(String keywords, Integer start, Integer rows) throws SolrServerException, IOException {
		//current query used by BZ is just taken from old one which has DisMax and boost in the URL (boosts could be in solr config as defaults??)
//https://www.ebi.ac.uk/mi/impc/solr/gene/select?facet.field=latest_phenotype_status&facet.field=legacy_phenotype_status&facet.field=status&facet.field=latest_production_centre&facet.field=latest_phenotyping_centre&facet.field=marker_type&facet.field=embryo_data_available&facet.field=embryo_modalities&facet.field=embryo_analysis_view_name&fl=marker_symbol,mgi_accession_id,marker_synonym,marker_name,marker_type,human_gene_symbol,latest_es_cell_status,latest_production_status,latest_phenotype_status,status,es_cell_status,mouse_status,legacy_phenotype_status,allele_name&fq=+*:*&rows=10&bq=marker_symbol_lowercase:(akt*)^1000+marker_symbol_bf:(akt*)^100+latest_phenotype_status:%22Phenotyping+Complete%22+^200&q=akt*&facet.limit=-1&defType=edismax&qf=geneQf&wt=json&indent=true&facet=on&facet.sort=count&start=0
		final SolrQuery query = new SolrQuery(keywords);
		query.add("defType", "edismax");
		//query.addField(GeneDTO.MGI_ACCESSION_ID);
		query.setFields(GeneDTO.MGI_ACCESSION_ID,GeneDTO.MARKER_NAME, GeneDTO.MARKER_SYMBOL, GeneDTO.MARKER_SYNONYM, GeneDTO.HUMAN_GENE_SYMBOL, GeneDTO.LATEST_ES_CELL_STATUS, GeneDTO.LATEST_MOUSE_STATUS, GeneDTO.LATEST_PHENOTYPE_STATUS);
		//bq=marker_symbol_lowercase:(akt*)^1000+marker_symbol_bf:(akt*)^100+latest_phenotype_status:%22Phenotyping+Complete%22+^200&
		query.add("bq", "latest_phenotype_status:\"Phenotyping Complete\"^200");
		query.add("bq", "marker_symbol_lowercase:("+keywords+"*)^1000" );
		query.add("bq", "marker_symbol_bf:("+keywords+"*)^100");
		query.add("qf", "geneQf");
		query.setStart(start);
		query.setRows(rows);
System.out.println("gene search query="+query);
		final QueryResponse response = solr.query( query);
		return response;
		
	}
    
    
}
