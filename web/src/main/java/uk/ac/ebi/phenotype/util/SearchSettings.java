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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

/**
 * A bean class that holds details of a search request, including query string
 * and filters.
 *
 * The bean allows passing around all those settings around functions in just
 * one object.
 *
 */
public class SearchSettings {

    // default view for the search
    private String dataType = "gene";
    // query is the string to search for
    private String query = "*:*";
    // fqStr is a filter
    private String fqStr = null;
    // oriQuery, oriFqStr are the original string for query,fq
    private String oriQuery = "*:*";
    private String oriFqStr = null;
    // chrQuery is a derived string for genomic coordinate search
    private String chrQuery = null;
    // imgView is used during display
    private boolean imgView = false;
    // iDisplay determine what hits are requested
    private int iDisplayStart = 0;
    private int iDisplayLength = 10;

    // The next two items record information about the http request    
    HttpServletRequest request = null;
    private String baseUrl = "";
    private String hostName = "";

    // for identifying queries by genomic coordinate
    private final String chrPattern = "(?i:^\"(chr)(.*)\\\\:(\\d+)\\\\-(\\d+)\"$)";

    /**
     * Constructor stores arguments into a class instance.
     *
     * The class will have default values for all settings, but these can be
     * overwritten using the setters.
     *
     * Consider also using setFromRequest to record basic information about an
     * http source.
     *
     * @param dataType
     * @param query
     * @param fqStr
     * @param request
     *
     */
    public SearchSettings(String dataType, String query, String fqStr, HttpServletRequest request) {
        // set data type
        if (!StringUtils.isEmpty(dataType)) {
            this.dataType = dataType;
        }
        // set query string
        if (!query.equals("*")) {
            this.query = normSpaces(query);
        }
        this.oriQuery = this.query;
        // set the filters
        this.fqStr = fqStr;
        this.oriFqStr = fqStr;

        // compute the chrQuery string
        setChrQuery();

        // copy information from the request object
        this.request = request;
        this.baseUrl = request.getAttribute("baseUrl").toString();
        this.hostName = request.getAttribute("mappedHostname").toString();
    }

    /**
     * Normalise a query string for search, i.e. replace spaces by codes
     *
     * @return
     */
    private String normSpaces(String s) {
        return s.replaceAll(" ", "%20");        
    }

    /**
     * for queries that ask for genomic coordinates, parse the query string into
     * chromosome, start-end positions TK: does this work? Search for
     * chr1:1000000-4000000 (chr1:1M-4M) returns Dst, but Dst is
     * Chr1:33908225-34308661 (coordinates off by 10)
     *
     */
    private void setChrQuery() {
        if (query.matches(chrPattern)) {
            Pattern r = Pattern.compile(chrPattern);
            Matcher m = r.matcher(query);
            if (m.find()) {
                String chrName = m.group(2).toUpperCase();
                String range = "[" + m.group(3).toUpperCase() + " TO " + m.group(4) + "]";
                String rangeQry = "(chr_name:" + chrName + ") AND (seq_region_start:" + range + ") AND (seq_region_end:" + range + ")";
                chrQuery = fqStr == null ? rangeQry : fqStr + " AND " + rangeQry;
            }
            if (dataType.equals("gene")) {
                query = "*:*";
            } else {
                fqStr = oriFqStr;
            }
        }        
    }

    /**
     * Change settings for search result (starting index, number of hits)
     *
     * @param iDisplayStart
     * @param iDisplayLength
     */
    public void setDisplay(Integer iDisplayStart, Integer iDisplayLength) {
        if (iDisplayStart != null) {
            this.iDisplayStart = iDisplayStart;
        }
        if (iDisplayLength != null) {
            this.iDisplayLength = iDisplayLength;
        }
    }

    /**
     * Change display type for images
     *
     * @param imgView
     */
    public void setImgView(boolean imgView) {
        this.imgView = imgView;
    }

    public String getDataType() {
        return dataType;
    }

    public String getQuery() {
        return query;
    }

    public String getFqStr() {
        return fqStr;
    }

    public String getOriQuery() {
        return oriQuery;
    }

    public String getOriFqStr() {
        return oriFqStr;
    }

    public String getChrQuery() {
        return chrQuery;
    }

    public boolean isImgView() {
        return imgView;
    }

    public int getiDisplayStart() {
        return iDisplayStart;
    }

    public int getiDisplayLength() {
        return iDisplayLength;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getHostName() {
        return hostName;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public String toString() {
        return "SearchSettings{"
                + "dataType=" + dataType
                + ", query=" + query
                + ", fqStr=" + fqStr
                + ", oriQuery=" + oriQuery
                + ", oriFqStr=" + oriFqStr
                + ", chrQuery=" + chrQuery
                + ", imgView=" + imgView
                + ", iDisplayStart=" + iDisplayStart
                + ", iDisplayLength=" + iDisplayLength
                + ", baseUrl=" + baseUrl
                + ", hostName=" + hostName
                + '}';
    }

}
