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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.service.search.SearchUrlServiceImpcImage;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * Code mostly refactored from DataTableController
 *
 * TO DO: cleanup! 
 *
 */
@Service
public class DataTableServiceImpcImage extends DataTableService {

    @Autowired
    private SearchUrlServiceImpcImage urlService;

    @Autowired
    private SolrIndex solrIndex;

    private final String IMG_NOT_FOUND = "Image coming soon<br>";

    /**
     * Most of the code copied from parseJsonforImpcImageDataTable
     *
     * @param json
     * @param settings
     * @return
     */
    @Override
    public String toDataTable(JSONObject json, SearchSettings settings) {

        // extract items from the settings
        int start = settings.getiDisplayStart();
        int length = settings.getiDisplayLength();
        HttpServletRequest request = settings.getRequest();
        String baseUrl = settings.getBaseUrl();
        String solrParams = urlService.getGridQueryStr(settings.getQuery(), settings.getFqStr(), start, length, false);
        String query = settings.getQuery();
        String fqOri = settings.getOriFqStr();
        if (fqOri == null) {
            fqOri = "*:*";
        }

        String mediaBaseUrl = baseUrl + "/impcImages/images?";

        if (settings.isImgView()) {
            LOGGER.info("using imgview");

            // image view: one image per row
            JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
            int totalDocs = json.getJSONObject("response").getInt("numFound");

            JSONObject j = new JSONObject();
            j.put("aaData", new Object[0]);
            // original had utf-8 encoding, but that requires exception handling
            // j.put("imgHref", mediaBaseUrl + URLEncoder.encode(solrParams, "UTF-8"));
            j.put("imgHref", mediaBaseUrl + solrParams);
            j.put("imgCount", totalDocs);
            j.put("iTotalRecords", totalDocs);
            j.put("iTotalDisplayRecords", totalDocs);
            j.put("iDisplayStart", settings.getiDisplayStart());
            j.put("iDisplayLength", settings.getiDisplayLength());

            for (int i = 0; i < docs.size(); i++) {

                List<String> rowData = new ArrayList<>();
                JSONObject doc = docs.getJSONObject(i);
                String annots = "";

                String imgLink = null;

                if (doc.containsKey("jpeg_url")) {

                    String fullSizePath = doc.getString("jpeg_url");
                    String thumbnailPath = doc.getString("thumbnail_url");
                    String largeThumbNailPath = doc.getString("jpeg_url");
                    String img = "<img class='thumbnailStyle' src='" + thumbnailPath + "'/>";
                    if (doc.getString("download_url").contains("annotation")) {
                        imgLink = "<a rel='nofollow' href='" + doc.getString("download_url") + "'>" + img + "</a>";
                    } else {
                        imgLink = "<a rel='nofollow' class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath + "'>" + img + "</a>";
                    }
                } else {
                    imgLink = IMG_NOT_FOUND;
                }

                try {
                    ArrayList<String> ma = new ArrayList<>();
                    ArrayList<String> procedures = new ArrayList<>();

                    int counter = 0;

                    if (doc.has("anatomy_id")) {
                        JSONArray termIds = doc.getJSONArray("anatomy_id");
                        JSONArray termNames = doc.getJSONArray("anatomy_term");
                        for (Object s : termIds) {
                            //LOGGER.info(i + " - anatomy: " + termNames.get(counter).toString());
                            //LOGGER.debug(i + " - anatomy: " + termNames.get(counter).toString());
                            String name = termNames.get(counter).toString();
                            String maid = termIds.get(counter).toString();
                            String url = request.getAttribute("baseUrl") + "/anatomy/" + maid;
                            ma.add("<a href='" + url + "'>" + name + "</a>");
                            counter++;
                        }
                    }

                    if (doc.has("procedure_name")) {
                        String procedureName = doc.getString("procedure_name");
                        procedures.add(procedureName);
                    }

                    if (ma.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + StringUtils.join(ma, ", ") + "</span>";
                    } else if (ma.size() > 1) {
                        String list = "<ul class='imgMa'><li>" + StringUtils.join(ma, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + list + "</span>";
                    }
                    if (procedures.size() == 1) {
                        annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + StringUtils.join(procedures, ", ") + "</span>";
                    } else if (procedures.size() > 1) {
                        String list = "<ul class='imgProcedure'><li>" + StringUtils.join(procedures, "</li><li>") + "</li></ul>";
                        annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + list + "</span>";
                    }

                    // gene link
                    if (doc.has("gene_symbol")) {
                        String geneSymbol = doc.getString("gene_symbol");
                        String geneAccessionId = doc.getString("gene_accession_id");
                        String url = baseUrl + "/genes/" + geneAccessionId;
                        String geneLink = "<a href='" + url + "'>" + geneSymbol + "</a>";

                        annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + geneLink + "</span>";
                    }

                    rowData.add(annots);
                    rowData.add(imgLink);

                    j.getJSONArray("aaData").add(rowData);
                } catch (Exception e) {
                    // some images have no annotations
                    rowData.add("No information available");
                    rowData.add(imgLink);
                    j.getJSONArray("aaData").add(rowData);
                }
            }

            JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
            j.put("facet_fields", facetFields);

            return j.toString();

        } else {
            LOGGER.info("using imgview NOT");

            // annotation view: images group by annotationTerm per row
            String defaultQStr = "observation_type:image_record&qf=imgQf&defType=edismax";

            if (!"".equals(query)) {
                defaultQStr = "q=" + query + " AND " + defaultQStr;
            } else {
                defaultQStr = "q=" + defaultQStr;
            }

            List<SolrIndex.AnnotNameValCount> annots = solrIndex.mergeImpcFacets(query, json, baseUrl);
            int numAnnots = annots.size();

            JSONObject j = new JSONObject();
            j.put("aaData", new Object[0]);
            // as above, utf-8 encoding requires exception handling
            //j.put("imgHref", mediaBaseUrl + URLEncoder.encode(solrParams, "UTF-8"));
            j.put("imgHref", mediaBaseUrl + solrParams);
            j.put("imgCount", json.getJSONObject("response").getInt("numFound"));
            j.put("iTotalRecords", numAnnots);
            j.put("iTotalDisplayRecords", numAnnots);
            j.put("iDisplayStart", settings.getiDisplayStart());
            j.put("iDisplayLength", settings.getiDisplayLength());

            int end = start + length > numAnnots ? numAnnots : start + length;
            for (int i = start; i < end; i = i + 1) {

                List<String> rowData = new ArrayList<>();

                SolrIndex.AnnotNameValCount annot = annots.get(i);
                String displayAnnotName = annot.getName();
                String annotVal = annot.getVal();
                String qryStr = query.replaceAll("\"", "");
                String displayLabel = annotVal;
                if (annot.getRelatedSynonym() != null) {
                    displayLabel = annotVal + " (Related synonym: " + Tools.highlightMatchedStrIfFound(qryStr, annot.getRelatedSynonym(), "span", "subMatch") + ")";
                } else if (annot.getMarkerSynonym() != null) {
                    displayLabel = annotVal + " (Synonym: " + Tools.highlightMatchedStrIfFound(qryStr, annot.getMarkerSynonym(), "span", "subMatch") + ")";
                } else if (annot.getParamAssociationName() != null) {
                    displayLabel = annotVal + " (Parameter association: " + Tools.highlightMatchedStrIfFound(qryStr, annot.getParamAssociationName(), "span", "subMatch") + ")";
                }

                String annotId = annot.getId();
                String valLink = annot.getLink() != null ? "<a href='" + annot.getLink() + "'>" + displayLabel + "</a>" : displayLabel;

                String fqVal = annot.id != null ? annot.getId() : annot.getVal();
                String thisFqStr = null;
                String annotFilter = annot.getFq() + ":\"" + fqVal + "\"";
                String fqOri2 = fqOri.replaceAll("\\(|\\)", "");
                if (fqOri2.equals(annotFilter)) {
                    thisFqStr = fqOri;
                } else {
                    thisFqStr = fqOri + " AND " + annotFilter;
                }
                String imgSubSetLink = null;
                String thisImgUrl = null;

                // create a dummy List
                List pathAndImgCount = new ArrayList<>();
                pathAndImgCount.add("");
                pathAndImgCount.add(0);
                try {
                    pathAndImgCount = solrIndex.fetchImpcImagePathByAnnotName(query, thisFqStr);
                } catch (IOException | URISyntaxException ex) {
                    Logger.getLogger(DataTableServiceImpcImage.class.getName()).log(Level.SEVERE, null, ex);
                }

                int imgCount = (int) pathAndImgCount.get(1);

                String unit = imgCount > 1 ? "images" : "image";

                if (imgCount > 0) {
                    String currFqStr = null;
                    String subFqStr = null;

                    if (displayAnnotName.equals("Gene")) {
                        subFqStr = "gene_symbol:\"" + annotVal + "\"";
                    } else if (displayAnnotName.equals("Procedure")) {
                        subFqStr = "procedure_name:\"" + annotVal + "\"";
                    } else if (displayAnnotName.equals("Anatomy")) {
                        subFqStr = "anatomy_id:\"" + annotId + "\"";
                    }

                    if (fqOri.equals("*:*")) {
                        currFqStr = subFqStr;
                    } else {
                        if (!fqOri.equals("(" + subFqStr + ")")) {
                            currFqStr = fqOri + " AND " + subFqStr;
                        } else {
                            currFqStr = fqOri;
                        }
                    }

                    thisImgUrl = mediaBaseUrl + defaultQStr + "&fq=" + currFqStr;

                    imgSubSetLink = "<a rel='nofollow' href='" + thisImgUrl + "'>" + imgCount + " " + unit + "</a>";

                    rowData.add("<span class='annotType'>" + displayAnnotName + "</span>: " + valLink + " (" + imgSubSetLink + ")");

                    rowData.add("<a rel='nofollow' href='" + thisImgUrl + "'>" + pathAndImgCount.get(0) + "</a>");
                    j.getJSONArray("aaData").add(rowData);
                }

            }

            JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
            j.put("facet_fields", facetFields);

            return j.toString();
        }
    }
}
