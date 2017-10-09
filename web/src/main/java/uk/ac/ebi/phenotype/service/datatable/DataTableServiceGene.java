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
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * Code mostly refactored from DataTableController
 * 
 *
 */
@Service
public class DataTableServiceGene extends DataTableService {

    @Autowired
    private GeneService geneService;

    /**
     * Most of the code copied from parseJsonforGeneDataTable
     *
     * @param json
     * @param settings
     * @return
     */
    @Override
    public String toDataTable(JSONObject json, SearchSettings settings) {

        HttpServletRequest request = settings.getRequest();
        RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        LOGGER.debug("TOTAL GENEs: " + totalDocs);

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);

        // TK: original had a snippet on useProteinCodingGeneCount
        // removed because unused elsewhere		
        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);
        j.put("iDisplayStart", settings.getiDisplayStart());
        j.put("iDisplayLength", settings.getiDisplayLength());

        String baseUrl = settings.getBaseUrl();
        String hostName = settings.getHostName();

        for (int i = 0; i < docs.size(); i++) {
            List<String> rowData = new ArrayList<>();

            JSONObject doc = docs.getJSONObject(i);
            String geneInfo = concateGeneInfo(doc, json, settings.getQuery(), request);
            rowData.add(geneInfo);

            // phenotyping status
            String mgiId = doc.getString(GeneDTO.MGI_ACCESSION_ID);
            String geneSymbol = doc.getString(GeneDTO.MARKER_SYMBOL);
            String productLink = hostName + baseUrl + "/search/allele2?kw=\"" + geneSymbol + "\"";
            String geneLink = hostName + baseUrl + "/genes/" + mgiId;

            // ES cell/mice production status
            boolean toExport = false;

            String prodStatus = geneService.getLatestProductionStatuses(doc, toExport, productLink);
            rowData.add(prodStatus);

            String statusField = (doc.containsKey(GeneDTO.LATEST_PHENOTYPE_STATUS)) ? doc.getString(GeneDTO.LATEST_PHENOTYPE_STATUS) : null;

            // made this as null by default: don't want to show this for now
            //Integer legacyPhenotypeStatus = null;
            Integer legacyPhenotypeStatus = (doc.containsKey(GeneDTO.LEGACY_PHENOTYPE_STATUS)) ? doc.getInt(GeneDTO.LEGACY_PHENOTYPE_STATUS) : null;

            Integer hasQc = (doc.containsKey(GeneDTO.HAS_QC)) ? doc.getInt(GeneDTO.HAS_QC) : null;

            // TK: legacy manually set to false
            String phenotypeStatusHTMLRepresentation = GeneService.getPhenotypingStatus(statusField, hasQc, legacyPhenotypeStatus, geneLink, toExport, false);
            rowData.add(phenotypeStatusHTMLRepresentation);

            // register of interest
            if (registerInterest.loggedIn()) {
                if (registerInterest.alreadyInterested(mgiId)) {
                    String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
                            + "<i class='fa fa-sign-out'></i>"
                            + "<a id='" + doc.getString("mgi_accession_id") + "' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
                            + "</div>";

                    rowData.add(uinterest);
                } else {
                    String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
                            + "<i class='fa fa-sign-in'></i>"
                            + "<a id='" + doc.getString("mgi_accession_id") + "' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
                            + "</div>";

                    rowData.add(rinterest);
                }
            } else {
                // use the login link instead of register link to avoid user clicking on tab which
                // will strip out destination link that we don't want to see happened
                String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
                        + "<i class='fa fa-sign-in'></i>"
                        // + "<a class='regInterest' href='/user/login?destination=data/search#fq=*:*&facet=gene'>&nbsp;Interest</a>"
                        + "<a class='regInterest' href='/user/login?destination=data/search/gene?kw=*&fq=*:*'>&nbsp;Interest</a>"
                        + "</div>";

                rowData.add(interest);
            }

            j.getJSONArray("aaData").add(rowData);
        }

        JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

        //facetFields.
        j.put("facet_fields", facetFields);

        return j.toString();
    }

    /**
     * Copied verbatim from DataTableController.java
     *
     * @param doc
     * @param json
     * @param qryStr
     * @param request
     * @return
     */
    private String concateGeneInfo(JSONObject doc, JSONObject json, String qryStr, HttpServletRequest request) {

        List<String> geneInfo = new ArrayList<>();

        String markerSymbol = "<span class='gSymbol'>" + doc.getString("marker_symbol") + "</span>";
        String mgiId = doc.getString("mgi_accession_id");        
        String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;        
        String markerSymbolLink = "<a href='" + geneUrl + "'>" + markerSymbol + "</a>";

        String[] fields = {"marker_name", "human_gene_symbol", "marker_synonym"};
        for (int i = 0; i < fields.length; i++) {
            try {                
                
                String field = fields[i];
                List<String> info = new ArrayList<String>();

                if (field.equals("marker_name")) {
                    info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getString(field), "span", "subMatch"));
                } else if (field.equals("human_gene_symbol")) {
                    JSONArray data = doc.getJSONArray(field);
                    for (Object h : data) {
                        info.add(Tools.highlightMatchedStrIfFound(qryStr, h.toString(), "span", "subMatch"));
                    }
                } else if (field.equals("marker_synonym")) {
                    JSONArray data = doc.getJSONArray(field);
                    int counter = 0;
                    String synMatch = null;
                    String syn = null;

                    for (Object d : data) {
                        counter++;
                        String targetStr = qryStr.toLowerCase().replaceAll("\"", "");
                        if (d.toString().toLowerCase().contains(targetStr)) {
                            if (synMatch == null) {
                                synMatch = Tools.highlightMatchedStrIfFound(targetStr, d.toString(), "span", "subMatch");
                            }
                        } else {
                            if (counter == 1) {
                                syn = d.toString();
                            }
                        }
                    }

                    if (synMatch != null) {
                        syn = synMatch;
                    }

                    if (counter == 1) {
                        info.add(syn);
                    } else if (counter > 1) {
                        info.add(syn + "<a href='" + geneUrl + "'> <span class='moreLess'>(see more)</span></a>");
                    }
                }

                // field string shown to the users
                if (field.equals("human_gene_symbol")) {
                    field = "human ortholog";
                } else if (field.equals("marker_name")) {
                    field = "name";
                } else if (field.equals("marker_synonym")) {
                    field = "synonym";
                }
                String ulClass = field == "human ortholog" ? "ortholog" : "synonym";

                //geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
                if (info.size() > 1) {
                    String fieldDisplay = "<ul class='" + ulClass + "'><li>" + StringUtils.join(info, "</li><li>") + "</li></ul>";
                    geneInfo.add("<span class='label'>" + field + "</span>: " + fieldDisplay);
                } else {
                    geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
       
        return "<div class='geneCol'><div class='title'>"
                + markerSymbolLink
                + "</div>"
                + "<div class='subinfo'>"
                + StringUtils.join(geneInfo, "<br>")
                + "</div>";

    }
}
