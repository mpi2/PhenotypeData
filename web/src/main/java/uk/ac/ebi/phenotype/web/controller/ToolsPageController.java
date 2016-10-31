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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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

		BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource("impcTools.json").getFile()));
		if (in != null) {
			String json = in.lines().collect(Collectors.joining(" "));
			JSONArray tools = new JSONArray(json);
			for (int i = 0; i < tools.length(); i++) {
				JSONObject jsonObj = tools.getJSONObject(i);
				String urlPath = jsonObj.getString("urlPath");
				String toolName = jsonObj.getString("toolName");
				String imagePath = jsonObj.getString("imagePath");
				String description = jsonObj.getString("description");
				String site = jsonObj.getString("site");

				String trs = "";
				String link = site.equals("internal") ? hostName + baseUrl + "/" + urlPath : urlPath;
				trs += "<tr><td colspan=2 class='toolName'><a href='" + link + "'>" + toolName + "</a></td></tr>";
				trs += "<tr><td><a href='" +  link + "'><img class='toolImg' src='" + baseUrl + imagePath + "'></img></a></td>";
				trs += "<td class='toolDesc'>" + description + "</td></tr>";

//				System.out.println("tr: "+trs);
				toolBlocks.add("<table class='tools'>" + trs + "</table>");

			}
		}

		return StringUtils.join(toolBlocks, "");
	}

}
