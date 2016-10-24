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
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ToolsController {

	private static final Logger logger = LoggerFactory.getLogger(ToolsController.class);

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
		String baseUrl = request.getAttribute("baseUrl") .toString();

		System.out.println("baseurl: "+baseUrl);
		List<String> toolBlocks = new ArrayList<>();
		InputStreamReader in = new InputStreamReader(toolsResource.getInputStream());

        int lineCount = 0;

		try (BufferedReader bin = new BufferedReader(in)) {

			String line;

			while ((line = bin.readLine()) != null) {
				// cols: urlpath    label   imagepath   description

				if (line.startsWith("urlPath")){
					continue;
				}
				String[] kv = line.split("\\t");
                lineCount++;

				String urlPath = kv[0];
				String toolName = kv[1];
				String imagePath = kv[2];
				String toolDesc = kv[3];
//				System.out.println("urlpath: " + urlPath);
//				System.out.println("name: "+ toolName);
//				System.out.println("imgpath: " + imagePath);
//				System.out.println("desc: "+ toolDesc);

				String trs = "";
				trs += "<tr><td colspan=2 class='toolName'><a href='"+ hostName + baseUrl + "/" + urlPath+"'>"+toolName+"</a></td></tr>";
				trs += "<tr><td><img class='toolImg' src='" + baseUrl + imagePath + "'></img></td>";
				trs += "<td class='toolDesc'>" + toolDesc + "</td></tr>";

//				System.out.println("tr: "+trs);
				toolBlocks.add("<table class='tools'>" + trs + "</table>");

			}
		}

		logger.info("Number of tools fetched: " + toolBlocks.size() + ", expected: " + lineCount);

		return StringUtils.join(toolBlocks, "");

	}


}
