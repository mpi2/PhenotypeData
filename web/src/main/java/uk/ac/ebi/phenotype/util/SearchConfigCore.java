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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * Functionality for creating solr search queries for a particular solr core.
 *
 */
public abstract class SearchConfigCore {

    public String defTypeSolrStr() {
        return "&defType=edismax";
    }
    
    public abstract String qf();

    public String qfSolrStr() {
        return "&qf=" + qf();
    }

    public abstract String fqStr();

    public abstract String bqStr(String q);

    public abstract List<String> fieldList();

    public String fieldListSolrStr() {
        return "&fl=" + StringUtils.join(fieldList(), ",");
    }

    public abstract List<String> facetFields();

    public String facetFieldsSolrStr() {
        String solrStr = "";
        for (String facetField : facetFields()) {
            solrStr += "&facet.field=" + facetField;
        }
        return "&facet=on&facet.limit=-1&facet.sort=" + facetSort() + solrStr;
    }
    
    public abstract String facetSort();

    public abstract List<String> gridHeaders();

    public String gridHeadersStr() {
        return StringUtils.join(gridHeaders(), ",");
    }
    
    public abstract String breadcrumLabel();

    public abstract String sortingStr();
}
