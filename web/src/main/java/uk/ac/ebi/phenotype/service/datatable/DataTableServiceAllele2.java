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
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * Code mostly refactored from DataTableController
 *
 * Seems to work, but needs more testing.
 * 
 * TO DO: remove dependence on request object
 *
 */
@Service
public class DataTableServiceAllele2 extends DataTableService {

    /**
     * Most of the code copied from parseJsonforProductDataTable
     * 
     * @param json
     * @param settings
     * @return 
     */
    @Override
    public String toDataTable(JSONObject json, SearchSettings settings) {
                
        HttpServletRequest request = settings.getRequest();
        String baseUrl = request.getAttribute("baseUrl").toString();

        JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        int totalDocs = json.getJSONObject("response").getInt("numFound");

        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);
        j.put("iDisplayStart", settings.getiDisplayStart());
        j.put("iDisplayLength", settings.getiDisplayLength());

        for (int i = 0; i < docs.size(); i++) {
            List<String> rowData = new ArrayList<String>();

            // array element is an alternate of facetField and facetCount
            JSONObject doc = docs.getJSONObject(i);

            //String alleleName = "<span class='allelename'>"+ URLEncoder.encode(doc.getString("allele_name"), "UTF-8")+"</span>";
            if (!doc.containsKey(Allele2DTO.ALLELE_NAME)) {
                // no point to show an allele that has no name
                continue;
            }

            String markerAcc = doc.getString(Allele2DTO.MGI_ACCESSION_ID);
            String alleleName = doc.getString(Allele2DTO.ALLELE_NAME);
//			String alleleUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/alleles/" + markerAcc + "/" + alleleName;
//			String markerSymbol = doc.getString(Allele2DTO.MARKER_SYMBOL);
            String alleleLink = "";//"<a href='" + alleleUrl + "'>" + markerSymbol + "<sup>" + alleleName + "</sup></a>";

            String mutationType = "";
            String mt = doc.containsKey(Allele2DTO.MUTATION_TYPE) ? doc.getString(Allele2DTO.MUTATION_TYPE) : "";
            String desc = doc.containsKey(Allele2DTO.ALLELE_DESCRIPTION) ? doc.getString(Allele2DTO.ALLELE_DESCRIPTION) : "";
            List<String> mtDesc = new ArrayList<>();
            mtDesc.add(mt);
            mtDesc.add(desc);

            if (!mt.isEmpty() && !desc.isEmpty()) {
                mutationType = StringUtils.join(mtDesc, "; ");
            }

            List<String> order = new ArrayList<>();
            String dataUrl = baseUrl + "/order?acc=" + markerAcc + "&allele=" + alleleName + "&bare=true";

            if (doc.containsKey(Allele2DTO.TARGETING_VECTOR_AVAILABLE) && doc.getBoolean(Allele2DTO.TARGETING_VECTOR_AVAILABLE)) {
                order.add("<tr>");
                order.add("<td><a class='iFrameFancy' data-url='" + dataUrl + "&type=targeting_vector'><i class='fa fa-shopping-cart'><span class='orderFont'> Targeting vector</span></i></a></td>");

                if (doc.containsKey(Allele2DTO.VECTOR_ALLELE_IMAGE)) {
                    order.add("<td><a class='iFrameVector' data-url='" + doc.getString(Allele2DTO.VECTOR_ALLELE_IMAGE) + "' title='map for vector'><i class='fa fa-th-list'></i></a></td>");
                } else {
                    order.add("<td></td>");
                }

                if (doc.containsKey(Allele2DTO.VECTOR_GENBANK_LINK)) {
                    order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.VECTOR_GENBANK_LINK) + "' title='genbank file for vector'><i class='fa fa-file-text'></i></a></td>");
                } else {
                    order.add("<td></td>");
                }
                order.add("</tr>");
            }
            if (doc.containsKey(Allele2DTO.ES_CELL_AVAILABLE) && doc.getBoolean(Allele2DTO.ES_CELL_AVAILABLE)) {
                order.add("<tr>");
                order.add("<td><a class='iFrameFancy' data-url='" + dataUrl + "&type=es_cell'><i class='fa fa-shopping-cart'><span class='orderFont'> ES cell</span></i></a></td>");

                if (doc.containsKey(Allele2DTO.ALLELE_SIMPLE_IMAGE)) {
                    order.add("<td><a class='iFrameVector' data-url='" + doc.getString(Allele2DTO.ALLELE_SIMPLE_IMAGE) + "' title='map for allele'><i class='fa fa-th-list'></i></a></td>");
                } else {
                    order.add("<td></td>");
                }

                if (doc.containsKey(Allele2DTO.GENBANK_FILE)) {
                    order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.GENBANK_FILE) + "' title='genbank file for allele'><i class='fa fa-file-text'></i></a></td>");
                } else {
                    order.add("<td></td>");
                }
                order.add("</tr>");
            }

            if (doc.containsKey(Allele2DTO.MOUSE_AVAILABLE) && doc.getBoolean(Allele2DTO.MOUSE_AVAILABLE)) {
                order.add("<tr>");
                order.add("<td><a class='iFrameFancy' data-url='" + dataUrl + "&type=mouse'><i class='fa fa-shopping-cart'><span class='orderFont'> Mouse</span></i></a></td>");

                if (doc.containsKey(Allele2DTO.ALLELE_SIMPLE_IMAGE)) {
                    order.add("<td><a class='iFrameVector' data-url='" + doc.getString(Allele2DTO.ALLELE_SIMPLE_IMAGE) + "' title='map for allele'><i class='fa fa-th-list'></i></a></td>");
                } else {
                    order.add("<td></td>");
                }

                if (doc.containsKey(Allele2DTO.GENBANK_FILE)) {
                    order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.GENBANK_FILE) + "' title='genbank file for allele'><i class='fa fa-file-text'></i></a></td>");
                } else {
                    order.add("<td></td>");
                }
                order.add("</tr>");
            }

            // populate the cells
            rowData.add(concateAlleleNameInfo(doc, request, settings.getQuery()));
            rowData.add(mutationType);
            rowData.add("<table><tbody>" + StringUtils.join(order, "") + "</tbody></table>");

            j.getJSONArray("aaData").add(rowData);
        }

        JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
        j.put("facet_fields", facetFields);
                
        return j.toString();
    }
    
    /**
     * Copied verbatim from DataTableController
     * 
     * TO DO: cleanup
     * 
     * @param doc
     * @param request
     * @param qryStr
     * @return 
     */
    private String concateAlleleNameInfo(JSONObject doc, HttpServletRequest request, String qryStr) {

		List<String> alleleNameInfo = new ArrayList<String>();

		String markerAcc = doc.getString(Allele2DTO.MGI_ACCESSION_ID);
		String alleleName = doc.getString(Allele2DTO.ALLELE_NAME);
		String alleleUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/alleles/" + markerAcc + "/" + alleleName;
		String markerSymbol = doc.getString(Allele2DTO.MARKER_SYMBOL);
		String alleleLink = "<a href='" + alleleUrl + "'>" + markerSymbol + "<sup>" + alleleName + "</sup></a>";

		String[] fields = {"marker_name", "marker_synonym"};
		for (int i = 0; i < fields.length; i ++) {
			try {
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},

				//System.out.println(qryStr);
				String field = fields[i];
				List<String> info = new ArrayList<String>();

				if (field.equals("marker_name")) {
					System.out.println("checking marker_name");
					info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getString(field), "span", "subMatch"));
				}
				else if (field.equals("marker_synonym")) {
					System.out.println("checking marker_synonym");
					JSONArray data = doc.getJSONArray(field);
					int counter = 0;
					String synMatch = null;
					String syn = null;

					for (Object d : data) {
						counter++;
						String targetStr = qryStr.toLowerCase().replaceAll("\"", "");
						if ( d.toString().toLowerCase().contains(targetStr) ) {
							if ( synMatch == null ) {
								synMatch = Tools.highlightMatchedStrIfFound(targetStr, d.toString(), "span", "subMatch");
							}
						}
						else {
							if  (counter == 1) {
								syn = d.toString();
							}
						}
					}

					if ( synMatch != null ){
						syn = synMatch;
					}

					if ( counter > 0 ){
						info.add(syn);
					}
				}

				// field string shown to the users
				if ( field.equals("marker_name" ) ){
					field = "gene name";
				}
				else if ( field.equals("marker_synonym") ){
					field = "gene synonym";
				}
				String ulClass = "synonym";

				//geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
				if (info.size() > 1) {
					String fieldDisplay = "<ul class='" + ulClass + "'><li>" + StringUtils.join(info, "</li><li>") + "</li></ul>";
					alleleNameInfo.add("<span class='label'>" + field + "</span>: " + fieldDisplay);
				} else {
					alleleNameInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		//return "<div class='geneCol'>" + markerSymbolLink + StringUtils.join(geneInfo, "<br>") + "</div>";
		return "<div class='alleleNameCol'><div class='title'>"
				+ alleleLink
				+ "</div>"
				+ "<div class='subinfo'>"
				+ StringUtils.join(alleleNameInfo, "<br>")
				+ "</div>";

	}
}
