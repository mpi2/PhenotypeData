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
public class SearchUrlServiceAnatomy extends SearchUrlService {

    @Autowired
    @Qualifier("anatomyCore")
    protected SolrClient solr;

    @Override
    public String qf() {
        return "anatomyQf";
    }  

    @Override
    public String bq(String q) {
        return "anatomy_term:(" + q + ")^1000"
                + " anatomy_term_synonym:(" + q + ")^500";
    }

    @Override
    public List<String> fieldList() {
        return Arrays.asList("anatomy_id",
                "anatomy_term",
                "anatomy_term_synonym",
                "stage",
                "selected_top_level_anatomy_term",
                "selected_top_level_anatomy_id");
    }

    @Override
    public List<String> facetFields() {
        return Arrays.asList("selected_top_level_anatomy_term", "stage");
    }

    @Override
    public String facetSort() {
        return "index";
    }

    @Override
    public List<String> gridHeaders() {
        return Arrays.asList("Anatomy", "Stage", "LacZ Expression Data", "Ontology<br/>Tree");
    }

    @Override
    public String breadcrumb() {
        return "Anatomy";
    }

    @Override
    public String sort() {
        return "term asc";
    }

    @Override
    public String solrUrl() {
        return SolrUtils.getBaseURL(solr);
    }
    
}
