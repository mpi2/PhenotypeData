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

import java.util.Arrays;
import java.util.List;
import org.apache.solr.client.solrj.SolrClient;
import org.mousephenotype.cda.solr.SolrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Support for solr search queries for core "phenodigm"
 *
 */
@Service
public class SearchUrlServicePhenodigm2Disease extends SearchUrlService {

    @Autowired
    @Qualifier("phenodigm2Core")
    protected SolrClient solr;

    @Override
    public String qf() {
        return "disease_term";
    }

    @Override
    public String fqStr() {
        return "+type:disease";
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
                "disease_classes");
    }

    @Override
    public List<String> facetFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String facetSort() {
        return "count";
    }

    @Override
    public List<String> gridHeaders() {
        return Arrays.asList("Disease", "Source");
    }

    @Override
    public String breadcrumLabel() {
        return "Diseases";
    }

    @Override
    public String sortingStr() {
        return "&sort=disease_term asc";
    }

    @Override
    public String solrUrl() {
        return SolrUtils.getBaseURL(solr);
    }
}
