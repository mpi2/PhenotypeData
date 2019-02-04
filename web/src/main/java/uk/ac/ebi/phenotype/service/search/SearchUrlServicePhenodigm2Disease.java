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
 * Support for solr search queries for core "phenodigm"
 *
 */
@Service
public class SearchUrlServicePhenodigm2Disease extends SearchUrlService {

    @Autowired
    @Qualifier("phenodigmCore")
    protected SolrClient solr;

    @Override
    public String qf() {
        return "search_qf";
    }

    @Override
    public String fq() {
        return "+type:disease_search";
    }

    @Override
    public String bq(String q) {
        return "disease_term:(" + q + ")^1000"
                + " disease_alts:(" + q + ")^700"
                + " disease_source:(" + q + ")^200";
    }

    @Override
    public List<String> fieldList() {
        return Arrays.asList("disease_id",
                "disease_term",
                "disease_source",
                "disease_alts"
                );
    }

    @Override
    public List<String> facetFields() {
        return Arrays.asList("disease_source",
                "disease_classes",
                "human_curated_gene",         
                "impc_model_with_curated_gene",
                "impc_model_with_computed_association",
                "mgi_model_with_curated_gene",
                "mgi_model_with_computed_association");                
    }

    @Override
    public String facetSort() {
        return "count";
    }

    @Override
    public String facetMinCount() { return "0"; }

    @Override
    public List<String> gridHeaders() {
        return Arrays.asList("Disease", "Source");
    }

    @Override
    public String breadcrumb() {
        return "Diseases";
    }

    @Override
    public String sort() {
        return "disease_term asc";
    }

    @Override
    public String solrUrl() {
        return SolrUtils.getBaseURL(solr);
    }
}
