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
package uk.ac.ebi.phenotype.service;

import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * An abstraction for classes that create url strings for searching solr cores.
 * Classes that extend this abstract class will provide functions that will
 * build search strings that are specific for a solr core or specific to a type
 * of query.
 *
 */
public abstract class SearchUrlService {

    /**
     * @return
     *
     * Expression suitable for &defType= part of a solr query.
     *
     */
    public String defType() {
        return "edismax";
    }

    /**
     * The "query field" argument for a solr query.
     *
     * @return
     *
     * Expression suitable for the &qf= part of a solr query.
     */
    public abstract String qf();

    /**
     * The "filter query" argument.
     *
     * To create a filter query with a user-specific request, use the related
     * function fq(String)
     *
     * @return
     *
     * Expression suitable for the &fq= part of a solr query. This expression is
     * a must-have filter components for the class instance.
     */
    public String fq() {
        return "+*:*";
    }

    /**
     * Create a composite filter query string using the default filter query
     * (form the class' fq() function) and an additional filter string.
     *
     * @param customFilter
     *
     * user-specified filter string
     *
     * @return
     *
     * An expression suitable for the &fq= part of a solr query.
     */
    public String fq(String customFilter) {
        String fq = fq();
        if (StringUtils.isEmpty(customFilter)) {
            return fq;
        } else {
            return fq + " AND (" + customFilter + ")";
        }
    }

    /**
     * The "boost query" argument for adjusting hit scoring. Use this, for
     * example, to score a hit in field A higher than a hit in field B.
     *
     * @param q
     *
     * a query string
     *
     * @return
     *
     * Expression suitable for the &bq= part of a solr query.
     */
    public abstract String bq(String q);

    /**
     * @return
     *
     * List of fields that should be returned in the the search.
     */
    public abstract List<String> fieldList();

    /**
     * Plain list of facet field names; Consider using facetFieldsSolrStr to
     * obtain a complete solr query part that turns faceting on and includes all
     * the facet fields.
     *
     * @return
     *
     * List of fields in documents to facet on.
     */
    public abstract List<String> facetFields();

    /**
     * Helper to create a part of a solr query.
     *
     * @return
     *
     * A part of a solr query that turns on faceting, facet sorting, and sets
     * the facet fields using facetFields()
     */
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

    /**
     * Not really part of solr query - remove?
     *
     * @return
     */
    public abstract String breadcrumb();

    /**
     * Produce a an instruction for how to sort solr query hits.
     *
     * Note: the function has a somewhat confusing name. It does not sort
     * anything, it merely produces a string without any side effects.
     *
     * Note: the sorting does not appear to be used. Remove?
     *
     * @return
     *
     * Expression suitable for the &sort= part of solr query
     */
    public abstract String sort();

    public abstract String solrUrl();

    /**
     * Produce a suffix to a solr search query.
     *
     * The default is to add no suffix. Individual cores can override this
     * function to add core-specific twists.
     *
     * @return
     *
     * Part of a solr query; can be added to the end of a valid solr query
     * string to produce another valid solr query string.
     */
    public String querySuffix() {
        return "";
    }

    /**
     * Produce a solr query string with all the options. The build query will
     * include all the selected result and facets, etc.
     *
     * original had extra logic for dealing with mp and anatomy cores That has
     * been removed. See old version of code for details.
     *
     * @param query
     * @param customFq
     * @param start
     * @param length
     * @param facet
     *
     * @return
     */
    public String getGridQueryStr(String query, String customFq, int start, int length,
            boolean facet) {

        StringBuilder sb = new StringBuilder();
        sb.append("wt=json")
                .append("&q=").append(query)
                .append("&fq=").append(fq(customFq))
                .append("&qf=").append(qf())
                .append("&defType=").append(defType())
                .append("&fl=").append(StringUtils.join(fieldList(), ","))
                .append("&bq=").append(bq(query));
        if (facet) {
            sb.append(facetFieldsSolrStr());
        }
        sb.append("&start=").append(start)
                .append("&rows=").append(length);

        return sb.toString();
    }

    /**
     * Similar to getGridQueryStr, but here result starts with full solr url.
     *
     * @param query
     * @param customFq
     * @param start
     * @param length
     * @param facet
     * @return
     */
    public String getGridQueryUrl(String query, String customFq, int start, int length, boolean facet) {
        return solrUrl() + "/select?" + getGridQueryStr(query, customFq, start, length, facet);
    }

    /**
     * Create a simple solr query tring. This applies the default settings from
     * the SearchConfig class and the user's query and custom filter.
     *
     * The query is simple in that it returns everything; use getGridQueryStr to
     * introduce faceting and field lists.
     *
     * @param query
     *
     * primary search string
     *
     * @param customFq
     *
     * a custom solr filter
     *
     * @param rows
     *
     * number of rows to return
     *
     * @return
     *
     * a solr query string
     */
    public String getSimpleQueryStr(String query, String customFq, int rows) {
        return "q=" + query
                + "&fq=" + fq(customFq)
                + "&qf=" + qf()
                + "&defType=" + defType()
                + "&rows=" + rows
                + "&wt=json";
    }

    /**
     * Similar to getSimpleQueryStr, but contains solr core url.
     *
     * @param query
     * @param customFq
     * @param rows
     * @return
     */
    public String getSimpleQueryUrl(String query, String customFq, int rows) {
        return solrUrl() + "/select?" + getSimpleQueryStr(query, customFq, rows);
    }

    /**
     * Similar to getSimpleQueryStr, but here forces result to have zero rows.
     * This is suitable for only obtaining an idea of the size of the result.
     *
     * @param query
     * @param customFq
     * @return
     */
    public String getCountQuerySolrStr(String query, String customFq) {
        return getSimpleQueryStr(query, customFq, 0);
    }

    /**
     * Similar to getCountQuerySolrStr, but contains solr core url
     *
     * @param query
     * @param customFq
     * @return
     */
    public String getCountQuerySolrUrl(String query, String customFq) {
        return getSimpleQueryUrl(query, customFq, 0);
    }

}
