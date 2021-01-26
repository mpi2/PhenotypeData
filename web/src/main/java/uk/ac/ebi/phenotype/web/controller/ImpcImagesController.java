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
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestParam;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {

	@Autowired
	ImageService imageService;

	@Autowired
	ExpressionService expressionService;

	@Autowired
	GeneService geneService;

	@RequestMapping("/impcImages/laczimages/{acc}/{topLevelMa}")
	public String laczImages(@PathVariable String acc, @PathVariable String topLevelMa, Model model)
			throws SolrServerException, IOException , URISyntaxException {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:2387599%22&facet=true&facet.field=selected_top_level_ma_term&fq=parameter_name:%22LacZ%20Images%20Section%22&group=true&group.field=selected_top_level_ma_term

		System.out.println("calling laczImages web page");
		addGeneToPage(acc, model);
		boolean overview=false;
		expressionService.getLacImageDataForGene(acc, topLevelMa,null, overview);
		return "laczImages";
	}

	@RequestMapping("/impcImages/laczimages/{acc}")
	public String laczImages(@PathVariable String acc, Model model)
			throws SolrServerException, IOException , URISyntaxException {
		addGeneToPage(acc, model);
		boolean overview=false;
		expressionService.getLacImageDataForGene(acc, null, null, overview);
		return "laczImages";
	}
	
	@RequestMapping("/impcImages/embryolaczimages/{acc}/{topLevelEmap}")
	public String embryoLaczImages(@PathVariable String acc, @PathVariable String topLevelEmap, Model model)
			throws SolrServerException, IOException , URISyntaxException {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:2387599%22&facet=true&facet.field=selected_top_level_ma_term&fq=parameter_name:%22LacZ%20Images%20Section%22&group=true&group.field=selected_top_level_ma_term

		System.out.println("calling embryolaczImages web page with specific term="+topLevelEmap);
		addGeneToPage(acc, model);
		boolean overview=false;
		expressionService.getLacImageDataForGene(acc, topLevelEmap,null, overview);

		return "laczImages";
	}

	@RequestMapping("/impcImages/embryolaczimages/{acc}")
	public String embryoLaczImages(@PathVariable String acc, Model model)
			throws SolrServerException, IOException , URISyntaxException {
		System.out.println("calling embryolaczImages web page");
		addGeneToPage(acc, model);
		boolean overview=false;
		expressionService.getLacImageDataForGene(acc, null, null, overview);

		return "laczImages";
	}

	private void addGeneToPage(String acc, Model model)
			throws SolrServerException, IOException {
		GeneDTO gene = geneService.getGeneById(acc,GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);//added for breadcrumb so people can go back to the gene page
		//System.out.println("gene in picker="+gene);
		model.addAttribute("gene",gene);
	}


	@RequestMapping("/imageNavigator")
	public String imageControlNavigator(HttpServletRequest request, Model model) {

		String page = "imageNavigator";
		System.out.println("calling imageNavigator");
		return page;
	}


	@RequestMapping("/impcImages/images*")
	public String allImages(HttpServletRequest request, Model model)
			throws SolrServerException, IOException , URISyntaxException {

		// http://localhost:8080/phenotype-archive/impcImages?q=observation_type:image_record&rows=100
		System.out.println("calling impcImages web page");

		this.sendQueryStringToSolr(request, model);
		return "impcImages";
	}

	@RequestMapping("/impcImages/download")
	public String fileDownload(@RequestParam(required = true, value = "parameter_stable_id") String parameterStableId,
							   @RequestParam(required = true, value = "acc") String geneAcc, Model model) throws IOException, SolrServerException {
		QueryResponse response = this.imageService.getImages(geneAcc, parameterStableId, null, 1000, null, null, null, null, null, null, null, null);
		SolrDocumentList solrDocumentList = response.getResults();
		solrDocumentList.forEach(d -> d.replace("sex", Arrays.asList(d.get("sex"))));
		model.addAttribute("parameterName", solrDocumentList.get(0).get("parameter_name"));
		model.addAttribute("procedureName", solrDocumentList.get(0).get("procedure_name"));
		model.addAttribute("geneSymbol", solrDocumentList.get(0).get("gene_symbol"));
		model.addAttribute("geneAcc", geneAcc);
		model.addAttribute("files", solrDocumentList);
		return "impcImagesFileDownload";

	}

	private void sendQueryStringToSolr(HttpServletRequest request, Model model)
			throws IOException, URISyntaxException, SolrServerException {
		String titleString = "";
		String startString = "0";
		String rowsString = "25";// the number of images passed back for each
									// solr request

		if (request.getParameter("start") != null) {
			startString = request.getParameter("start");
		}
		String newQueryString = "";
		String qStr = null;
		String fqStr = null;
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			// System.out.println("key=" + key);

			// To retrieve a single value
			String value = request.getParameter(key);
			 
			// only add to our new query string if not rows or length as we want
			// to set those to specific values in the jsp
			if (!key.equals("rows") && !key.equals("start")) {
				
//				if(StringUtils.countMatches(value, ":")>=2){
//					//query has colon in it more than field seperator e.g. q value=mp_id:MP:0012466
//					String fieldQuery=value.substring(value.indexOf(":")+1, value.length());
//					System.out.println("fieldQuery="+fieldQuery);
//					String fieldKey=value.substring(0,value.indexOf(":")+1);
//					if (value.contains(":")) {
//						value = fieldKey+"\""+fieldQuery+"\"";// for mgi ids for
//																// example encode
//																// the :
//					}
//				}
				newQueryString += "&" + key + "=" + value;
				
			}
			if (key.equals("q")) {

				qStr = value;
				// get rid of wierd solr comments etc so more human readable
				titleString = qStr;
				titleString = titleString.replace(
						"observation_type:image_record AND", " ");
				titleString = titleString.replace(
						" AND observation_type:image_record", " ");

				// also check what is in fq
				if (request.getParameterValues("fq") != null) {

					String[] fqStrings = request.getParameterValues("fq");
					fqStr = fqStrings[0];
					if (titleString.equals("*:*") && fqStr.equals("*:*")) {
						titleString = "IMPC image dataset";
					} else if (titleString.equals("*:*")
							&& !fqStr.equals("*:*")) {
						titleString = fqStr;
					} else {
						titleString += " AND " + fqStr;
					}

					titleString = titleString.replace("\"", " ");
					titleString = titleString.replace("(", " ");
					titleString = titleString.replace(")", " ");
					titleString = titleString.replace("_", " ");
					titleString = titleString.replace("*", " ");
					titleString = titleString.replace(":", " ");
				}
			}
		}
		String qBaseStr = newQueryString;
		newQueryString += "&start=" + startString + "&rows=" + rowsString;
		newQueryString+="&sort=parameter_name asc";
		newQueryString = URLDecoder.decode(newQueryString, "UTF-8");  // before it gets passed to SOLR
		
		System.out.println("new query str: " + newQueryString);
		QueryResponse imageResponse = imageService
				.getResponseForSolrQuery(newQueryString);
		if (imageResponse.getResults() != null) {
			model.addAttribute("images", imageResponse.getResults());
			Long totalNumberFound = imageResponse.getResults().getNumFound();
			// System.out.println("image count=" + numberFound);
			model.addAttribute("imageCount", totalNumberFound);
			// model.addAttribute("q", newQueryString);
			model.addAttribute("q", qStr);
			model.addAttribute("qBaseStr", qBaseStr);

			if (request.getParameter("title") != null) {// if title is provided
														// as a parameter use
														// that for the title
				titleString = request.getParameter("title");
			}

			model.addAttribute("titleString", titleString);

			// model.addAttribute("filterQueries", filterQueries);
			// model.addAttribute("filterField", filterField);
			// model.addAttribute("qf", qf);//e.g. auto_suggest
			// //model.addAttribute("filterParam", filterParam);
			// model.addAttribute("queryTerms", queryTerms);
			model.addAttribute("start", Integer.valueOf(startString));
			model.addAttribute("length", Integer.valueOf(rowsString));
			// model.addAttribute("defType", defType);
		}
	}

}