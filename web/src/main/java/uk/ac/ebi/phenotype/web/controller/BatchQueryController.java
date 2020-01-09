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

import org.mousephenotype.cda.solr.generic.util.Tools;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BatchQueryController {

	@RequestMapping(value="/batchQuery", method=RequestMethod.GET)
	public String loadBatchQueryPage(
			@RequestParam(value = "core", required = false) String core,
			@RequestParam(value = "fllist", required = false) String fllist,
			@RequestParam(value = "idlist", required = false) String idlist,
			Model model) {

		String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
		model.addAttribute("outputFields", outputFieldsHtml);

		if ( idlist != null) {
			model.addAttribute("core", core);
			model.addAttribute("fllist", fllist);
			model.addAttribute("idlist", idlist);
		}

		return "batchQuery";
	}

	@RequestMapping(value="/batchquery2", method=RequestMethod.GET)
	public @ResponseBody
	String fetchDataFields(
			@RequestParam(value = "core", required = false) String core,
			HttpServletRequest request,
			Model model) {

		return Tools.fetchOutputFieldsCheckBoxesHtml(core);

	}

}
