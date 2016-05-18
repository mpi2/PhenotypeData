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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImageComparatorController {

	@Autowired
	ImageService imageService;

	@Autowired
	ExpressionService expressionService;

	@Autowired
	GeneService geneService;

	
	@RequestMapping("/imageComparator/{acc}/{parameter_stable_id}")
	public String imageCompBrowser(@PathVariable String acc,
			@PathVariable String parameter_stable_id, Model model, HttpServletRequest request)
			throws SolrServerException {

		
		// good example url with control and experimental images
		// http://localhost:8080/phenotype-archive/imagePicker/MGI:2669829/IMPC_EYE_050_001
		System.out.println("calling image imageComparator");
				String mediaType=request.getParameter("mediaType");
				 if(mediaType!=null) System.out.println("mediaType= "+mediaType);
		// get experimental images
		// we will also want to call the getControls method and display side by
		// side
		SolrDocumentList experimental = new SolrDocumentList();
		QueryResponse responseExperimental = imageService
				.getImagesForGeneByParameter(acc, parameter_stable_id,
						"experimental", 10000, null, null, null);
		SolrDocument imgDoc =null;
		if (responseExperimental != null && responseExperimental.getResults().size()>0) {
			experimental=responseExperimental.getResults();
			System.out.println("list size=" + experimental.size());
			imgDoc = experimental.get(0);
		}
		
		int numberOfControlsPerSex = 5;
		// int daysEitherSide = 30;// get a month either side
		SolrDocumentList controls = new SolrDocumentList();
		if (imgDoc != null) {
			for (SexType sex : SexType.values()) {

				SolrDocumentList controlsTemp = imageService.getControls(numberOfControlsPerSex, sex, imgDoc, null);
				controls.addAll(controlsTemp);
			}
		}
		
		this.addGeneToPage(acc, model);
		model.addAttribute("mediaType", mediaType);
		System.out.println("experimental size=" + experimental.size());
		model.addAttribute("experimental", experimental);
		System.out.println("controls size=" + controls.size());
		model.addAttribute("controls", controls);
		return "comparator";
	}
	
//	@RequestMapping("/imgDetail/{id}")
//	public String imgDetail(@PathVariable String id, Model model, HttpServletRequest request)
//			throws SolrServerException {
//		System.out.println("calling imgDetail");
//		model.addAttribute("id", id);
//		return "imgDetail";
//	}

	private void addGeneToPage(String acc, Model model)
			throws SolrServerException {
		GeneDTO gene = geneService.getGeneById(acc,GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);//added for breadcrumb so people can go back to the gene page
		System.out.println("gene in picker="+gene);
		model.addAttribute("gene",gene);
	}

	
}