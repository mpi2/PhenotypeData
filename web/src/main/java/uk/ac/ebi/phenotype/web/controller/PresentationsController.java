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


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Controller
public class PresentationsController {

	private static final Logger logger = LoggerFactory.getLogger(PresentationsController.class);

	/**
	 * presentations page
	 *
	 */

    private static final Map<String, List<String>> colsOrder = new HashMap<String, List<String>>() {{
        put("Talks-posters", new ArrayList<String>() {{
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

    private String ftpPath = "ftp://ftp.ebi.ac.uk/pub/databases/impc/presentations/";

	@RequestMapping(value="/presentations", method=RequestMethod.GET)
	public String loadPresentationsPage(
			HttpServletRequest request,
			Model model) throws IOException {

        LinkedHashMap<String, String>  impcPresentations = composePresentations(request);
		model.addAttribute("impcPresentations", impcPresentations);

		return "presentations";
	}

	private LinkedHashMap<String, String>  composePresentations(HttpServletRequest request) throws IOException {

		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		String baseUrl = request.getAttribute("baseUrl").toString();

		//System.out.println("baseurl: " + baseUrl);
        LinkedHashMap<String, String> impcPresentations = new LinkedHashMap<>();

       // BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource("presentations.json").getFile())); // local test

        logger.info("Connecting to FTP server...");

        try {
            URL url = new URL(ftpPath + "/presentations.json");
            URLConnection con = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            logger.info("Reading file start.");

            String inputLine;
            String json = "";
            while ((inputLine = in.readLine()) != null) {
               // System.out.println(inputLine);
                json += inputLine;
            }
            in.close();

           // System.out.println("json: " + json);

            JSONArray sections = new JSONArray(json);

			for (int i = 0; i < sections.length(); i++) {

				JSONObject       thisSecObj = sections.getJSONObject(i);
				Iterator<String> keys       = thisSecObj.keys();
				if( keys.hasNext() ){
					String secName = (String)keys.next();
					JSONArray valsetList = thisSecObj.getJSONArray(secName);
                //	System.out.println(secName + " -- " + valsetList.length());

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
        catch (FileNotFoundException e) {
            logger.error("File not find on server.");
            System.exit(0);
        }catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Read FTP File Complete.");

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
            String val = null;
            try {
                val = thisValSet.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (key.equals("file")) {
                try {
                    val = "<a href='" + ftpPath + secName + "/" + thisValSet.getString(key) + "'>" + val + "</a>";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            tds.add("<td>" + val + "</td>");
        }

        return "<tr>" + StringUtils.join(tds, "") + "</tr>";
    }

}
