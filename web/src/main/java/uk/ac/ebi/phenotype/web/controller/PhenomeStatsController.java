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

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.ColorCodingPalette;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;


@Controller
public class PhenomeStatsController {

	@NotNull @Autowired
	GenotypePhenotypeService genotypePhenotypeService;

	@NotNull @Autowired
	StatisticalResultService srService;

	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

	@RequestMapping(value="/phenome", method=RequestMethod.GET)
	public String getGraph(
		//@PathVariable String phenotype_id,
		@RequestParam(required = true, value = "pipeline_stable_id") String pipelineStableId,
		@RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException , URISyntaxException, JSONException {

		PhenotypeFacetResult results = genotypePhenotypeService.getPhenotypeFacetResultByPhenotypingCenterAndPipeline(phenotypingCenter, pipelineStableId);

		ColorCodingPalette colorCoding = new ColorCodingPalette();

		colorCoding.generatePhenotypeCallSummaryColorsNew(
				results.getPhenotypeCallSummaries(),
				ColorCodingPalette.NB_COLOR_MAX,
				1,
				Constants.SIGNIFICANT_P_VALUE);

		// generate a chart
		String chart = phenomeChartProvider.generatePhenomeChartByPhenotype(
				results.getPhenotypeCallSummaries(),
				phenotypingCenter,
				Constants.SIGNIFICANT_P_VALUE);

		model.addAttribute("phenotypeCalls", results.getPhenotypeCallSummaries());
		model.addAttribute("palette", colorCoding.getPalette());
		model.addAttribute("chart", chart);

		return null;
	}
}