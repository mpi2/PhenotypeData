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
 * Provides support for solr search queries for core "allele2"
 *
 */
@Service
public class SearchConfigAllele2 extends SearchConfigCore {

    @Override
    public String qf() {
        return "auto_suggest";
    }

    @Override
    public String fqStr() {
        return "type:Allele";
    }

    @Override
    public String bqStr(String q) {
        return "&bq=allele_name:(" + q + ")^500";
    }

    @Override
    public List<String> fieldList() {
        return Arrays.asList("marker_symbol",
                "mgi_accession_id",
                "allele_name",
                "marker_name",
                "marker_synonym",
                "allele_description",
                "allele_simple_image", // es cell and mouse vector (gene map)
                "vector_allele_image", // targeting vector map
                "genbank_file", // for es cells / mouse
                "vector_genbank_file", // for vector
                "mutation_type",
                "es_cell_available",
                "mouse_available",
                "targeting_vector_available",
                "allele_category"
        );
    }

    @Override
    public List<String> facetFields() {
        return Arrays.asList("mutation_type_str",
                "es_cell_available",
                "mouse_available",
                "targeting_vector_available",
                "allele_category_str",
                "allele_features_str");
    }   
    
    @Override
    public String facetSort() {
        return "count";
    }
    
    @Override
    public List<String> gridHeaders() {
        return Arrays.asList("Allele Name", "Mutation", "<span id='porder'>Order</span><span id='pmap'>Map</span><span id='pseq'>Seq</span>");
    }

    @Override
    public String breadcrumLabel() {
        return "Products";
    }

    @Override
    public String sortingStr() {
        return "&sort=allele_name asc";
    }

}
