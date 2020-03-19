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
import org.apache.commons.lang3.text.WordUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.repositories.GenomicFeatureRepository;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

@Controller
public class ImagesController {

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	private final Logger log = LoggerFactory.getLogger(ImagesController.class);

	// Initialize the translation map of solr field names -> English names
	private Set<String> doNotShowFields = new HashSet<String>() {

		private static final long serialVersionUID = 1L;
		{
			add("!expName");
		}
	};

	// Initialize the translation map of solr field names -> English names
	private HashMap<String, String> solrFieldToEnglish = new HashMap<String, String>() {

		private static final long serialVersionUID = 1L;
		{
			put("expName", "Procedure");
			put("selected_top_level_ma_term", "Anatomy");
			put("annotatedHigherLevelMpTermName", "Phenotype group");
			put("annotationTermId", "Annotation term");
			put("subtype", "Type");
			put("accession", "Accession");
		}
	};


	private GenomicFeatureRepository genomicFeatureRepository;
	private ImagesSolrDao            imagesSolrDao;
	private OntologyTermRepository   ontologyTermRepository;


	@Inject
	public ImagesController(
			GenomicFeatureRepository genomicFeatureRepository,
			ImagesSolrDao imagesSolrDao,
			OntologyTermRepository ontologyTermRepository)
	{
		this.genomicFeatureRepository = genomicFeatureRepository;
		this.imagesSolrDao = imagesSolrDao;
		this.ontologyTermRepository = ontologyTermRepository;
	}


	@RequestMapping("/images*")
	public String allImages(@RequestParam(required = false, defaultValue = "0", value = "start") int start, 
			@RequestParam(required = false, defaultValue = "24", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn, 
			@RequestParam(required = false, defaultValue = "", value = "phenotype_id") String mpId, 
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId, 
			@RequestParam(required = false, defaultValue = "", value = "fq") String[] filterField, 
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField, 
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf, 
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType, 
			@RequestParam(required = false, defaultValue = "", value = "anatomy_id") String maId, 
			HttpServletRequest request, 
			Model model)
	throws SolrServerException, IOException {

		handleImagesRequest(request, start, length, qIn, mpId, geneId, filterField, qf, defType, maId, model);

		model.addAttribute("breadcrumbText", getBreadcrumbs(request, qIn, mpId, geneId, filterField, maId));

		return "images";
	}

	
	private void sendQueryStringToSolr(HttpServletRequest request, Model model)
	throws IOException, URISyntaxException, JSONException {

		String queryString = request.getQueryString();
		String startString = "0";
		String rowsString = "25";// the number of images passed back for each
									// solr request
		if (request.getParameter("start") != null) {
			startString = request.getParameter("start");
		}
		// if (request.getParameter("rows") != null) {
		// rowsString = request.getParameter("rows");
		// }

		// Map params = request.getParameterMap();
		// Map newParamsMap=new HashMap<>();
		// newParamsMap.putAll(params);
		// newParamsMap.remove("rows");
		// newParamsMap.remove("start");
		String newQueryString = "";
		Enumeration keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			// System.out.println("key=" + key);

			// To retrieve a single value
			String value = request.getParameter(key);
			// System.out.println("value=" + value);
			// only add to our new query string if not rows or length as we want
			// to set those to specific values in the jsp
            if (!key.equals("rows") && !key.equals("start")) {
				newQueryString += "&" + key + "=" + value;
				// If the same key has multiple values (check boxes)
				String[] valueArray = request.getParameterValues(key);

				for (int i = 0; i > valueArray.length; i++) {
					System.out.println("VALUE ARRAY" + valueArray[i]);
				}
			}
		}
		newQueryString += "&start=" + startString + "&rows=" + rowsString;

		System.out.println("queryString from imagesb=" + newQueryString);
		JSONObject imageResults = JSONRestUtil.getResults(config.get("internalSolrUrl") + "/images/select?" + newQueryString);
		JSONArray imageDocs = JSONRestUtil.getDocArray(imageResults);
		if (imageDocs != null) {
			model.addAttribute("images", imageDocs);
			int numberFound = JSONRestUtil.getNumberFoundFromJsonResponse(imageResults);
			// System.out.println("image count=" + numberFound);
			model.addAttribute("imageCount", numberFound);
			model.addAttribute("q", newQueryString);
			// model.addAttribute("filterQueries", filterQueries);
			// model.addAttribute("filterField", filterField);
			// model.addAttribute("qf", qf);//e.g. auto_suggest
			// //model.addAttribute("filterParam", filterParam);
			// model.addAttribute("queryTerms", queryTerms);
			model.addAttribute("start", Integer.valueOf(startString));
			model.addAttribute("length", Integer.valueOf(rowsString));
			// model.addAttribute("defType", defType);
		} else {
			model.addAttribute("solrImagesError", "");
		}
	}

	/**
	 * Returns an HTML string representation of the "last mile" breadcrumb
	 *
	 *
	 * @param request
	 *            the request object
	 * @param qIn
	 *            query term passed in to the controller
	 * @param mpId
	 *            MP id passed in to the controller
	 * @param geneId
	 *            Gene (usually MGI) ID passed in to the controller
	 * @param filterField
	 *            Array of filter query parameters passed in to the controller
	 *
	 * @return a raw HTML string containing the pieces of the query assembled
	 *         into a breadcrumb fragment
	 */
	private String getBreadcrumbs(HttpServletRequest request, String qIn, String mpId, String geneId, String[] filterField, String maId) {

		String baseUrl = (String) request.getAttribute("baseUrl");

		ArrayList<String> breadcrumbs = new ArrayList<String>();

		if (!qIn.equals("") && !qIn.contains(":") && !qIn.equals("*")) {
			breadcrumbs.add("Search term: " + qIn);
		}

		if (!geneId.equals("")) {
			// MGI
			GenomicFeature gf = genomicFeatureRepository.getById_AccessionAndExternalDbShortName(geneId, "MGI");
			String value = gf.getSymbol();
			// String geneBc = "<a href='" + baseUrl + "/genes/" + geneId + "'>"
			// + gf.getSymbol() + "</a>";
			// breadcrumbs.add("gene: \"" + geneBc + "\"");
			breadcrumbs.add("gene: \"" + geneId + "\"");
		}

		if (!mpId.equals("")) {
			//  Mammalian Phenotype
			OntologyTerm mpTerm = ontologyTermRepository.getByAccAndShortName(mpId, Datasource.MP);
			String value = mpTerm.getName();
			String mpBc = "<a href='" + baseUrl + "/phenotypes/" + mpId + "'>" + value + "</a>";
			breadcrumbs.add("phenotype: \"" + mpBc + "\"");
		}

		if (!maId.equals("")) {
			// Mouse Adult Gross Anatomy
			OntologyTerm maTerm = ontologyTermRepository.getByAccAndShortName(maId, Datasource.MA);
			String value = maTerm.getName();
			String mpBc = "<a href='" + baseUrl + "/phenotypes/" + maId + "'>" + value + "</a>";
			breadcrumbs.add("anatomy: \"" + mpBc + "\"");
		}

		if (!qIn.equals("") && !qIn.equals("*:*") && !qIn.equals("*") && qIn.contains(":")) {
			String[] parts = qIn.split(":");
			String key = solrFieldToEnglish.get(parts[0]);
			String value = parts[1].replaceAll("%20", " ").replaceAll("\"", "");
			breadcrumbs.add(key + ": " + value);
		}

		if (!filterField.equals("")) {
			for (String field : filterField) {

				String formatted = field.replaceAll("%20", " ").replaceAll("\"", "");

				ArrayList<String> orFields = new ArrayList<String>();

				for (String f : formatted.split(" OR ")) {
					String[] parts = f.split(":");
					if (!doNotShowFields.contains(parts[0])) {
						String key = solrFieldToEnglish.get(parts[0]);
						if (key == null) {
							log.error("Cannot find " + parts[0] + " in translation map. Add the mapping in ImagesController static constructor");
							key = parts[0]; // default the key to the solr field
											// name (ugly!)
						}

						String value = parts[1];

						if (key.equals("Anatomy")) {
							value = "<a href='" + baseUrl + "/search#q=*&core=images&fq=annotated_or_inferred_higherLevelMaTermName:\"" + value + "\"'>" + value + "</a>";
							orFields.add(key.toLowerCase() + ": \"" + value + "\"");
						} else {
							orFields.add(key.toLowerCase() + ": \"" + value + "\"");
						}
					}
				}
				String bCrumb = StringUtils.join(orFields, " OR ");

				// Surround the clauses joined with OR by parens
				if (orFields.size() > 1) {
					bCrumb = "(" + bCrumb + ")";
				}

				if (!bCrumb.trim().equals("")) {
					breadcrumbs.add(bCrumb);
				}
			}
		}

		return StringUtils.join(breadcrumbs, " AND ");
	}


	private void handleImagesRequest(HttpServletRequest request, int start, int length, String q, String mpId, String geneId, String[] filterField, String qf, String defType, String maId, Model model)
	throws SolrServerException, IOException {

		System.out.println("query string=" + request.getQueryString());
		String queryTerms = ""; // used for a human readable String of the query
								// for display on the results page
		QueryResponse imageDocs = null;
		String filterQueries = "";
		for (String field : filterField) {
			// System.out.println("filterField in controller=" + field);
			filterQueries += "&fq=" + field;
		}
		List<String> filterList = Arrays.asList(filterField);

		if (!geneId.equals("")) {
			queryTerms = geneId;
			q = "accession:" + geneId.replace("MGI:", "MGI\\:");
			//genomicFeatureRepository.getById_Accession(geneId).getSymbol();
		}

		if (!mpId.equals("")) {
			queryTerms = mpId;
			q = "annotationTermId:" + mpId.replace("MP:", "MP\\:");
			queryTerms = ontologyTermRepository.getByAccAndShortName(mpId, Datasource.MP).getName();
		}

		if (!maId.equals("")) {
			q = "annotationTermId:" + maId.replace("MA:", "MA\\:");
			queryTerms = ontologyTermRepository.getByAccAndShortName(maId, Datasource.MA).getName();
		}

		if (mpId.equals("") && geneId.equals("") && maId.equals("")) {
			queryTerms = "";
		}

		if (!q.equals("*:*")) {
			queryTerms += " " + q.replaceAll("expName:", "").replaceAll("\"", "");
			// q = q + " AND " + qIn;
		}

		if (filterField.length > 0) {

			queryTerms = humanizeStrings(filterField, queryTerms);

		}

		// System.out.println("q=" + q);

		imageDocs = imagesSolrDao.getFilteredDocsForQuery(q, filterList, qf, defType, start, length);
		if (imageDocs != null) {
			model.addAttribute("images", imageDocs.getResults());
			// System.out.println("image count="
			// + imageDocs.getResults().getNumFound());
			model.addAttribute("imageCount", imageDocs.getResults().getNumFound());
			model.addAttribute("q", URLEncoder.encode(q, "UTF-8"));
			model.addAttribute("filterQueries", filterQueries);
			model.addAttribute("filterField", filterField);
			model.addAttribute("qf", qf);// e.g. auto_suggest
			// model.addAttribute("filterParam", filterParam);
			model.addAttribute("queryTerms", queryTerms);
			model.addAttribute("start", start);
			model.addAttribute("length", length);
			model.addAttribute("defType", defType);
		} else {
			model.addAttribute("solrImagesError", "");
		}
	}

	private String humanizeStrings(String[] filterField, String queryTerms) {

		List<String> terms = new ArrayList<String>();
		for (String filter : filterField) {
			// System.out.println("filterField="+filter);
			if (!filter.equals("annotationTermId:M*")) {// dont add M* to human
														// readable form
				terms.add(WordUtils.capitalize(filter.replaceAll(".*:", "").replaceAll("\"", "")));
			}
		}
		queryTerms += ": " + StringUtils.join(terms, ", ");
		return queryTerms;
	}
}
