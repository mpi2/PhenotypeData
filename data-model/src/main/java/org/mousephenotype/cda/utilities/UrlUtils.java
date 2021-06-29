/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.utilities;

import org.mousephenotype.cda.enumerations.ZygosityType;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

/**
 * This class encapsulates the code and data necessary to manage the composition of url strings.
 *
 * NOTE: Please do not add any methods here that require being wired in to Spring. Keep this file spring-free, as it
 *       is used in places that are not spring-dependent.
 *
 * Created by mrelac on 02/07/2015.
 */
public class UrlUtils {

    public String getChartPageUrlPostQc(String baseUrl, String geneAcc, String alleleAcc, ZygosityType zygosity, String parameterStableId, String pipelineStableId, String phenotypingCenter) {
        String url = baseUrl;
        url += "/charts?accession=" + geneAcc;
        url += "&allele_accession_id=" + alleleAcc;
        if (zygosity != null) {
            url += "&zygosity=" + zygosity.name();
        }
        if (parameterStableId != null) {
            url += "&parameter_stable_id=" + parameterStableId;
        }
        if (pipelineStableId != null) {
            url += "&pipeline_stable_id=" + pipelineStableId;
        }
        if (phenotypingCenter != null) {
            url += "&phenotyping_center=" + phenotypingCenter;
        }
        return url;
    }

    /**
     * Decodes <code>url</code>, into UTF-8, making it suitable to use as a link.
     * Invalid url strings are ignored and the original string is returned.
     * @param url the url to decode
     * @return the decoded url
     */
    public String urlDecode(String url) {
        String retVal = url;
        try {
            String decodedValue = URLDecoder.decode(url, "UTF-8");
            retVal = decodedValue;
        } catch (Exception e) {
            System.out.println("Decoding of value '" + (url == null ? "<null>" : url) + "' failed. URL ignored. Reason: " + e.getLocalizedMessage());
        }

        return retVal;
    }

    /**
     * Decodes the data in each input row for column <code>columnIndex</code> into UTF-8.
     *
     * @param data the data to decode
     * @param columnIndex the index of the column whose data is to be decoded
     *
     * @return A copy of the input data, with column <code>columnIndex</code> url-decoded
     */
    public String[][] urlDecodeColumn(String[][] data, int columnIndex) {
        if (data == null)
            return data;

        int rowIndex = 0;
        int colIndex = 0;
        String cell = "";
        String[][] retVal = new String[data.length][data[0].length];
        try {
            for (rowIndex = 0; rowIndex < data.length; rowIndex++) {
                String[] row = data[rowIndex];
                for (colIndex = 0; colIndex < row.length; colIndex++) {
                    if (data[rowIndex][colIndex] == null) {
                        retVal[rowIndex][colIndex] = null;
                    } else {
                        cell = data[rowIndex][colIndex];
                        retVal[rowIndex][colIndex] = (colIndex == columnIndex ? urlDecode(cell) : cell);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Decoding of value [" + rowIndex + "][" + colIndex + "] (" + (cell == null ? "<null>" : cell) + ") failed. URL ignored. Reason: " + e.getLocalizedMessage());
        }

        return retVal;
    }

    /**
     * Encode only the following characters:
     *   ' ' (space)        %20
     *   '"' (double quote) %22
     *   ':' (colon - but only in the query part of the string; don't encode http: or the port indicator wp-np3-83:8080
     * @param url
     * @return
     */
    public String urlEncode(String url) {
        String retVal = url;

        try {
            url = url.replaceAll(" ", "%20").replaceAll("\"", "%22");
            int qmarkOffset = url.indexOf("?");
            if (qmarkOffset >= 0) {
                String firstPart = url.substring(0, qmarkOffset + 1);
                String queryPart = url.substring(qmarkOffset + 1).replaceAll(":", "%3A");
                url = firstPart + queryPart;
            }

            retVal = url;

        } catch (Exception e) { }

        return retVal;
    }

    /**
     * Encodes the data in each input row for column <code>columnIndex</code> into UTF-8, making it suitable to use as a link.
     * Invalid url string values are ignored.
     * @param data the data to encode
     * @param columnIndex the index of the column whose data is to be encoded
     *
     * @return A copy of the input data, with column <code>columnIndex</code> url-encoded
     */
    public String[][] urlEncodeColumn(String[][] data, int columnIndex) {
        if (data == null)
            return data;

        int rowIndex = 0;
        int colIndex = 0;
        String cell = "";
        String[][] retVal = new String[data.length][data[0].length];
        try {
            for (rowIndex = 0; rowIndex < data.length; rowIndex++) {
                String[] row = data[rowIndex];
                for (colIndex = 0; colIndex < row.length; colIndex++) {
                    if (data[rowIndex][colIndex] == null) {
                        retVal[rowIndex][colIndex] = null;
                    } else {
                        cell = data[rowIndex][colIndex];
                        retVal[rowIndex][colIndex] = (colIndex == columnIndex ? urlEncode(cell) : cell);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Encoding of value [" + rowIndex + "][" + colIndex + "] (" + (cell == null ? "<null>" : cell) + ") failed. URL ignored. Reason: " + e.getLocalizedMessage());
        }

        return retVal;
    }

    /**
     * This method returns a string that is the redirected url, if any. It has a cost, as it has to open a connection
     * to get the redirected url. If the URL is not redirected or is invalid, the original source string is returned.
     *
     * @param source the url source string for redirection
     * @return the redirected url source string, if redirected; the original source string otherwise
     */
    public static String getRedirectedUrl(String source) {
        String result;

        URLConnection urlConnection;
        String        newSource;

        try {
            urlConnection = (new URL(source).openConnection());
            urlConnection.connect();
            newSource = urlConnection.getHeaderField("Location");
            result = ((newSource != null) && ( ! newSource.isEmpty()) ? newSource : source);
        } catch (IOException e) {
            return source;
        }

        return result;
    }
}