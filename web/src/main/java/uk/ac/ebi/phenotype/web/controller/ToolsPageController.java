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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class ToolsPageController {

	private static final Logger logger = LoggerFactory.getLogger(ToolsPageController.class);

	/**
	 * tools page
	 *
	 */

	@Value("classpath:tools.txt")
	Resource toolsResource;


	@RequestMapping(value="/tools", method=RequestMethod.GET)
	public String loadToolsPage(
			HttpServletRequest request,
			Model model) throws IOException {

		String toolsHtml = composeToolBoxes(request);
		model.addAttribute("tools", toolsHtml);

		return "tools";
	}

	private String composeToolBoxes(HttpServletRequest request) throws IOException {

		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		String baseUrl = request.getAttribute("baseUrl").toString();

		//System.out.println("baseurl: " + baseUrl);
		List<String> toolBlocks = new ArrayList<>();

		// reads from /sre/main/resources/impcTools.json and compose the page
		BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource("impcTools.json").getFile()));
		if (in != null) {
			String    json  = in.lines().collect(Collectors.joining(" "));
			JSONArray tools = null;
			try {
				tools = new JSONArray(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < tools.length(); i++) {
				JSONObject jsonObj = null;
				try {
					jsonObj = tools.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String urlPath = null;
				try {
					urlPath = jsonObj.getString("urlPath");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String toolName = null;
				try {
					toolName = jsonObj.getString("toolName");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String imagePath = null;
				try {
					imagePath = jsonObj.getString("imagePath");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String description = null;
				try {
					description = jsonObj.getString("description");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				String site = null;
				try {
					site = jsonObj.getString("site");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				String trs = "";

				String url = "";
				if (site.equals("internal")){
					url = hostName + baseUrl + "/" + urlPath;
				}
				else if (site.equals("external")){
					url = urlPath;
				}
				else if (site.equals("harwell")){
					url = hostName + "/" + urlPath;
				}

				System.out.println("URL: " + url);
				trs += "<tr><td colspan=2 class='toolName'><a href='" + url + "'>" + toolName + "</a></td></tr>";
				trs += "<tr><td><a href='" +  url + "'><img class='toolImg' src='" + baseUrl + imagePath + "'></img></a></td>";
				trs += "<td class='toolDesc'>" + description + "</td></tr>";

//				System.out.println("tr: "+trs);
				toolBlocks.add("<table class='tools'>" + trs + "</table>");

			}
		}

		return StringUtils.join(toolBlocks, "");
	}

}
