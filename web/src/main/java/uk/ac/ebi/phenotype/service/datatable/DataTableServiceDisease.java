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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class DataTableServiceDisease extends DataTableService {

    /**
     * Most of the code copied from parseJsonforDiseaseDataTable
     *
     * @param json
     * @param settings
     * @return
     */
    @Override
    public String toDataTable(JSONObject json, SearchSettings settings) {

        HttpServletRequest request = settings.getRequest();
        String baseUrl = request.getAttribute("baseUrl") + "/disease/";

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		Map<String, String> srcBaseUrlMap = new HashMap<>();
		srcBaseUrlMap.put("OMIM", "http://omim.org/entry/");
		srcBaseUrlMap.put("ORPHANET", "http://www.orpha.net/consor/cgi-bin/OC_Exp.php?Lng=GB&Expert=");
		srcBaseUrlMap.put("DECIPHER", "http://decipher.sanger.ac.uk/syndrome/");

		for (int i = 0; i < docs.size(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// disease link
			JSONObject doc = docs.getJSONObject(i);			
			String diseaseId = doc.getString("disease_id");
			String diseaseTerm = doc.getString("disease_term");
			String diseaseLink = "<a href='" + baseUrl + diseaseId + "'>" + diseaseTerm + "</a>";
			rowData.add(diseaseLink);

			// disease source
			String src = doc.getString("disease_source");
			String[] IdParts =  diseaseId.split(":");
			String digits = IdParts[1];
			String srcId = src + ":" + digits;
			rowData.add("<a target='_blank' href='" + srcBaseUrlMap.get(src) + digits + "'>" + srcId + "</a>");
			
			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
    }

}
