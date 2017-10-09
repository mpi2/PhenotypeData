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
package uk.ac.ebi.phenotype.service.datatable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.generic.util.JSONImageUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * Code mostly refactored from DataTableController
 *
 * TO DO: remove dependence on request object
 *
 */
@Service
public class DataTableServiceAnatomy extends DataTableService {

    @Autowired
    ExpressionService expressionService;

    /**
     * Most of the code copied from parseJsonforAnatomyDataTable
     *
     * @param json
     * @param settings
     * @return
     */
    @Override
    public String toDataTable(JSONObject json, SearchSettings settings) {

        String baseUrl = settings.getBaseUrl();
        String qryStr = settings.getQuery();

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);
        j.put("iDisplayStart", settings.getiDisplayStart());
        j.put("iDisplayLength", settings.getiDisplayLength());

        for (int i = 0; i < docs.size(); i++) {
            List<String> rowData = new ArrayList<String>();

            // array element is an alternate of facetField and facetCount
            JSONObject doc = docs.getJSONObject(i);
            String anatomyId = doc.getString(AnatomyDTO.ANATOMY_ID);
            String anatomyTerm = doc.getString(AnatomyDTO.ANATOMY_TERM);
            String anatomylink = "<a href='" + baseUrl + "/anatomy/" + anatomyId + "'>" + anatomyTerm + "</a>";

            // check has expression data
            //Anatomy ma = JSONMAUtils.getMA(anatomyId, config);
            String anatomyCol = "<div class='title'>" + anatomylink + "</div>";

            if (doc.containsKey(AnatomyDTO.ANATOMY_TERM_SYNONYM)) {

                JSONArray data = doc.getJSONArray(AnatomyDTO.ANATOMY_TERM_SYNONYM);
                int counter = 0;
                String synMatch = null;
                String syn = null;

                for (Object d : data) {

                    String targetStr = qryStr.toLowerCase().replaceAll("\"", "");
                    if (d.toString().toLowerCase().contains(targetStr)) {
                        if (synMatch == null) {
                            synMatch = Tools.highlightMatchedStrIfFound(targetStr, d.toString(), "span", "subMatch");
                        }
                    } else {
                        counter++;
                        if (counter == 1) {
                            syn = d.toString();
                        }
                    }
                }

                if (synMatch != null) {
                    syn = synMatch;
                }

                if (counter > 1) {
                    syn = syn + "<a href='" + baseUrl + "/anatomy/" + anatomyId + "'> <span class='moreLess'>(Show more)</span></a>";
                }

                anatomyCol += "<div class='subinfo'>"
                        + "<span class='label'>synonym</span>: "
                        + syn
                        + "</div>";

                anatomyCol = "<div class='mpCol'>" + anatomyCol + "</div>";
                rowData.add(anatomyCol);
            } else {
                rowData.add(anatomyCol);
            }

            // developmental stage
            rowData.add(doc.getString("stage"));

            //display yes or no in anatomy search results in the LacZ Expression Data column                         
            boolean expressionDataAvailable = hasExpressionData(anatomyId);

            rowData.add(expressionDataAvailable ? "<a href='" + baseUrl + "/anatomy/" + anatomyId + "#maHasExp" + "'>Yes</a>" : "No");

            // link out to ontology browser page
            rowData.add("<a href='" + baseUrl + "/ontologyBrowser?" + "termId=" + anatomyId + "'><i class=\"fa fa-share-alt-square\"></i> Browse</a>");

            j.getJSONArray("aaData").add(rowData);
        }

        JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
        j.put("facet_fields", facetFields);

        return j.toString();

    }

    /**
     * Copied with some modifications from DataTableController
     *
     * @param anatomyId
     * @return
     */
    private boolean hasExpressionData(String anatomyId) {

        // default behavior is to report "no expression data"        
        boolean expressionDataAvailable = false;        

        //check legacy Sanger images for any images
        try {
            JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomyId, config, 1);
            JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
            if (expressionImageDocs.size() > 0) {
                expressionDataAvailable = true;
            }
        } catch (IOException | URISyntaxException ex) {
            LOGGER.info("Exception while checking legacy Sanger images: "+ex.getLocalizedMessage());            
        }

        //check experiment core for expression categorical data and impc images for parameter associated expression data
        try {
            if (expressionService.expressionDataAvailable(anatomyId)) {
                expressionDataAvailable = true;
            }
        } catch (SolrServerException | IOException ex) {
            LOGGER.info("Exception while checking for expression images: "+ex.getLocalizedMessage());
        }

        return expressionDataAvailable;
    }

}
