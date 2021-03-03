/** *****************************************************************************
 * Copyright 2017 QMUL - Queen Mary University of London
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ****************************************************************************** */
package uk.ac.ebi.phenotype.service.search;

import org.apache.solr.client.solrj.SolrClient;
import org.mousephenotype.cda.solr.SolrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Support for solr search queries for core "gene"
 *
 */
@Service
public class SearchUrlServiceGene extends SearchUrlService {

    @Autowired
    @Qualifier("geneCore")
    protected SolrClient solr;

    @Override
    public String qf() {
        return "geneQf";
    } 

    @Override
    public String bq(String q) {
        return "marker_symbol_lowercase:(" + q + ")^1000"
                + " marker_symbol_bf:(" + q + ")^100"
                + " phenotype_status:\"Phenotyping Complete\" ^200";
    }

    @Override
    public List<String> fieldList() {
        return Arrays.asList(
                "marker_symbol",
                "mgi_accession_id",
                "marker_synonym",
                "marker_name",
                "marker_type",
                "human_gene_symbol",
                "es_cell_status",
                "production_status",
                "phenotype_status",
                "status",
                "es_cell_status",
                "mouse_status",
                "allele_name");
    }

    @Override
    public List<String> facetFields() {
        return Arrays.asList("phenotype_status",
                "status",
                "production_centre",
                "phenotyping_centre",
                "marker_type",
                "embryo_data_available",
                "embryo_modalities",
                "embryo_analysis_view_name"
        );
    }

    @Override
    public String facetSort() {
        return "count";
    }

    @Override
    public String facetMinCount() { return "1"; }

    @Override
    public List<String> gridHeaders() {
        return Arrays.asList("Gene", "Production", "Phenotype", "Register");
    }

    @Override
    public String breadcrumb() {        
        return "Genes";
    }

    @Override
    public String sort() {
        return "marker_symbol asc";
    }

    @Override
    public String solrUrl() {
        return SolrUtils.getBaseURL(solr);
    }
}
