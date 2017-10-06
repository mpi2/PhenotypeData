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
package uk.ac.ebi.phenotype.util;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Provides support for solr search queries for core "gene"
 *
 */
@Service
public class SearchConfigGene extends SearchConfigCore {

    @Override
    public String qf() {
        return "geneQf";
    }

    @Override
    public String fqStr() {
        return "";
    }

    @Override
    public String bqStr(String q) {
        return "&bq=" + "marker_symbol_lowercase:(" + q + ")^1000"
                + " marker_symbol_bf:(" + q + ")^100"
                + " latest_phenotype_status:\"Phenotyping Complete\" ^200";
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
                "latest_es_cell_status",
                "latest_production_status",
                "latest_phenotype_status",
                "status",
                "es_cell_status",
                "mouse_status",
                "legacy_phenotype_status",
                "allele_name");
    }

    @Override
    public List<String> facetFields() {
        return Arrays.asList("latest_phenotype_status",
                "legacy_phenotype_status",
                "status",
                "latest_production_centre",
                "latest_phenotyping_centre",
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
    public List<String> gridHeaders() {
        return Arrays.asList("Gene", "Production", "Phenotype", "Register");
    }

    @Override
    public String breadcrumLabel() {
        return "Genes";
    }

    @Override
    public String sortingStr() {
        return "&sort=marker_symbol asc";
    }

}
