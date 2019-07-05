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

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

@Controller
public class ExternalAnnotsController {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	@Autowired
	private SolrIndex solrIndex;

	@RequestMapping(value = "/publications", method = RequestMethod.GET)
	public String dataTableJsonAlleleRef(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) {

		return "alleleref";
	}

	@RequestMapping(value = "/testbq", method = RequestMethod.GET)
	public String testbq(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException  {
		// model.addAttribute("q", q);

		return "testbq";
	}

	@ExceptionHandler(Exception.class)
	private ResponseEntity<String> getSolrErrorResponse(Exception e) {
		e.printStackTrace();
		String bootstrap="<div class=\"alert\"><strong>Warning!</strong>  Error: Search functionality is currently unavailable</div>";
		String errorJSON="{'aaData':[[' "+bootstrap+"','  ', ' ']], 'iTotalRecords':1,'iTotalDisplayRecords':1}";
		JSONObject errorJson = (JSONObject) JSONSerializer.toJSON(errorJSON);
		return new ResponseEntity<String>(errorJson.toString(), createResponseHeaders(), HttpStatus.CREATED);
	}

	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}
}