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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * Code mostly refactored from DataTableController
 *
 * TO DO: remove dependence on request object
 *
 */
@Service
public class DataTableServiceMp extends DataTableService {

    /**
     * Most of the code copied from parseJsonforMpDataTable
     *
     * @param json
     * @param settings
     * @return
     */
    @Override
    public String toDataTable(JSONObject json, SearchSettings settings) {

        HttpServletRequest request = settings.getRequest();
        RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);
        String baseUrl = request.getAttribute("baseUrl").toString();

        String qryStr = settings.getQuery();
        
        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);
        j.put("iDisplayStart", request.getAttribute("displayStart"));
        j.put("iDisplayLength", request.getAttribute("displayLength"));

        for (int i = 0; i < docs.size(); i++) {
            List<String> rowData = new ArrayList<String>();

            // array element is an alternate of facetField and facet
            //
            // Count
            JSONObject doc = docs.getJSONObject(i);

            String mpId = doc.getString("mp_id");
            String mpTerm = doc.getString("mp_term");
            String mpLink = "<a href='" + baseUrl + "/phenotypes/" + mpId + "'>" + mpTerm + "</a>";
            String mpCol = null;
            
            if (doc.containsKey("mixSynQf")) {

                mpCol = "<div class='title'>" + mpLink + "</div>";

                JSONArray data = doc.getJSONArray("mixSynQf");
                int counter = 0;
                String synMatch = null;
                String syn = null;

                for (Object d : data) {

                    if (d.toString().startsWith("MP:")) {
                        continue;
                    }
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
                    syn = syn + "<a href='" + baseUrl + "/phenotypes/" + mpId + "'> <span class='moreLess'>(Show more)</span></a>";
                }

                mpCol += "<div class='subinfo'>"
                        + "<span class='label'>synonym</span>: "
                        + syn
                        + "</div>";

                mpCol = "<div class='mpCol'>" + mpCol + "</div>";
                rowData.add(mpCol);
            } else {
                rowData.add(mpLink);
            }

            // some MP do not have definition
            String mpDef = doc.containsKey(MpDTO.MP_DEFINITION) ? doc.getString(MpDTO.MP_DEFINITION) : "No definition data available";

            int defaultLen = 30;
            if (mpDef != null && mpDef.length() > defaultLen) {
                String trimmedDef = mpDef.substring(0, defaultLen);
                // retrim if in the middle of a word
                trimmedDef = trimmedDef.substring(0, Math.min(trimmedDef.length(), trimmedDef.lastIndexOf(" ")));
                String partMpDef = "<div class='partDef'>" + Tools.highlightMatchedStrIfFound(qryStr, trimmedDef, "span", "subMatch") + " ...</div>";
                mpDef = "<div class='fullDef'>" + Tools.highlightMatchedStrIfFound(qryStr, mpDef, "span", "subMatch") + "</div>";
                rowData.add(partMpDef + mpDef + "<div class='moreLess'>Show more</div>");
            } else {
                rowData.add(mpDef);
            }
            
            // link out to ontology browser page
            rowData.add("<a href='" + baseUrl + "/ontologyBrowser?" + "termId=" + mpId + "'><i class=\"fa fa-share-alt-square\"></i> Browse</a>");

            // register of interest
            if (registerInterest.loggedIn()) {
                if (registerInterest.alreadyInterested(mpId)) {
                    String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
                            + "<i class='fa fa-sign-out'></i>"
                            + "<a id='" + mpId + "' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
                            + "</div>";

                    rowData.add(uinterest);
                } else {
                    String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
                            + "<i class='fa fa-sign-in'></i>"
                            + "<a id='" + mpId + "' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
                            + "</div>";

                    rowData.add(rinterest);
                }
            } else {
                // use the login link instead of register link to avoid user clicking on tab which
                // will strip out destination link that we don't want to see happened
                String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
                        + "<i class='fa fa-sign-in'></i>"
                        // + "<a class='regInterest' href='/user/login?destination=data/search#fq=*:*&facet=mp'>&nbsp;Interest</a>"
                        + "<a class='regInterest' href='/user/login?destination=data/search/mp?kw=*&fq=top_level_mp_term:*'>&nbsp;Interest</a>"
                        + "</div>";

                rowData.add(interest);
            }

            j.getJSONArray("aaData").add(rowData);
        }

        JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
        j.put("facet_fields", facetFields);

        return j.toString();
    }

}
