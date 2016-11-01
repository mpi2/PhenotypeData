/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
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
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class PresentationsController {

	private static final Logger logger = LoggerFactory.getLogger(PresentationsController.class);

	/**
	 * presentations page
	 *
	 */

    private static final Map<String, List<String>> colsOrder = new HashMap<String, List<String>>() {{
        put("Presentations", new ArrayList<String>() {{
        add("title");
        add("meeting");
        add("date");
        add("location");
        add("type");
        add("file");
        }});
        put("Workshops", new ArrayList<String>() {{
        add("title");
        add("meeting");
        add("date");
        add("location");
        add("type");
        add("file");
        }});
        put("General Slides", new ArrayList<String>() {{
            add("title");
            add("description");
            add("date");
            add("file");
        }});
    }};


	@RequestMapping(value="/presentations", method=RequestMethod.GET)
	public String loadPresentationsPage(
			HttpServletRequest request,
			Model model) throws IOException {

        //Map<String, String>  impcPresentations = composePresentations(request);
        LinkedHashMap<String, String>  impcPresentations = composePresentations(request);
		model.addAttribute("impcPresentations", impcPresentations);

		return "presentations";
	}

	private LinkedHashMap<String, String>  composePresentations(HttpServletRequest request) throws IOException {
        //private Map<String, String>  composePresentations(HttpServletRequest request) throws IOException {
		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		String baseUrl = request.getAttribute("baseUrl").toString();

		//System.out.println("baseurl: " + baseUrl);
        LinkedHashMap<String, String> impcPresentations = new LinkedHashMap<>();
        //Map<String, String> impcPresentations = new HashMap<>();

		BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource("presentations.json").getFile()));
		if (in != null) {
			String json = in.lines().collect(Collectors.joining(" "));

			//System.out.println(json);
			JSONArray sections = new JSONArray(json);

			for (int i = 0; i < sections.length(); i++) {


				JSONObject thisSecObj = sections.getJSONObject(i);
				Iterator<String> keys = thisSecObj.keys();
				if( keys.hasNext() ){
					String secName = (String)keys.next();
					JSONArray valsetList = thisSecObj.getJSONArray(secName);
//					System.out.println(secName + " -- " + valsetList.length());

                    String th = composeTh(secName);

                    String html = "<table>" + th;
                    List<String> trs = new ArrayList<>();
					for( int j=0; j < valsetList.length(); j++) {
                        JSONObject thisValSet = valsetList.getJSONObject(j);
                        trs.add(composeRows(secName, thisValSet));
                    }

                    html += StringUtils.join(trs, "") + "</table>";
                    impcPresentations.put(secName, html);
				}

			}
		}
		return impcPresentations;
	}

    private String composeTh(String secName) {

        List<String> ths = new ArrayList<>();
        for (String key : colsOrder.get(secName)) {
            char first = Character.toUpperCase(key.charAt(0));
            String capKey = first + key.substring(1);
            ths.add("<th>" + capKey + "</th>");
        }

        return "<thead>" + StringUtils.join(ths, "") + "</thead>";
    }

    private String composeRows(String secName, JSONObject thisValSet){

        List<String> tds = new ArrayList<>();
        for(String key : colsOrder.get(secName)){
            String val = thisValSet.getString(key); 
            tds.add("<td>" + val + "</td>");
        }

        return "<tr>" + StringUtils.join(tds, "") + "</tr>";
    }

}
