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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.exception.JDBCConnectionException;
import org.mousephenotype.cda.db.dao.AlleleDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.mousephenotype.cda.solr.bean.ImpressBean;
import org.mousephenotype.cda.solr.bean.StatisticalResultBean;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.web.dto.AllelePageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.ColorCodingPalette;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class ExperimentsController {

	private final Logger log = LoggerFactory.getLogger(ExperimentsController.class);

	@Autowired
	private AlleleDAO alleleDao;

	@Autowired
	private PhenotypePipelineDAO pipelineDao;

	@Autowired
	SolrIndex solrIndex;

	@Autowired
	private ImpressService impressService;

	@Autowired
	private StatisticalResultService srService;

	@Autowired
	private ObservationService observationService;

	private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/experimentsFrag")
	public String getAlleles(
			@RequestParam(required = true, value = "geneAccession") String geneAccession,
			@RequestParam(required = false, value = "alleleAccession") String alleleAccession,
			@RequestParam(required = false, value = "phenotypingCenter") String phenotypingCenter,
			@RequestParam(required = false, value = "pipelineStableId") String pipelineStableId,
			@RequestParam(required = false, value = "procedureStableId") List<String> procedureStableId,
			@RequestParam(required = false, value = "resource") ArrayList<String> resource,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes)
	throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {

		System.out.println("geneAccession :: " + geneAccession);
		Long time = System.currentTimeMillis();
		AllelePageDTO allelePageDTO = observationService.getAllelesInfo(geneAccession);
		Map<String, List<StatisticalResultDTO>> pvaluesMap = new HashMap<>();
		int rows = 0;

		pvaluesMap.putAll(srService.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(geneAccession, alleleAccession, phenotypingCenter, pipelineStableId, procedureStableId, resource));
		for ( List<StatisticalResultDTO> list : pvaluesMap.values()){
			rows += list.size();
		}
		String chart = phenomeChartProvider.generatePvaluesOverviewChart(geneAccession, alleleAccession, pvaluesMap, Constants.SIGNIFICANT_P_VALUE, allelePageDTO.getParametersByProcedure(), phenotypingCenter);

		model.addAttribute("chart", chart);
		model.addAttribute("rows", rows);
		model.addAttribute("pvaluesMap", pvaluesMap);
		model.addAttribute("allelePageDTO", allelePageDTO);

		return "experimentsFrag";
	}
	
	@RequestMapping("/experiments")
	public String getBasicInfo(
			@RequestParam(required = true, value = "geneAccession") String geneAccession,
			@RequestParam(required = false, value = "alleleAccession") String alleleAccession,
			@RequestParam(required = false, value = "phenotypingCenter") String phenotypingCenter,
			@RequestParam(required = false, value = "pipelineStableId") String pipelineStableId,
			@RequestParam(required = false, value = "procedureStableId") List<String> procedureStableId,
			@RequestParam(required = false, value = "resource") ArrayList<String> resource,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes)
	throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {

		AllelePageDTO allelePageDTO = observationService.getAllelesInfo(geneAccession);
		Map<String, List<StatisticalResultDTO>> pvaluesMap = new HashMap<>();
		int rows = 0;

		pvaluesMap.putAll(srService.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(geneAccession, alleleAccession, phenotypingCenter, pipelineStableId, procedureStableId, resource));
		for ( List<StatisticalResultDTO> list : pvaluesMap.values()){
			rows += list.size();
		}

//		ColorCodingPalette colorCoding = new ColorCodingPalette();
//		colorCoding.generateColors(	pvaluesMap,	ColorCodingPalette.NB_COLOR_MAX, 1,	Constants.SIGNIFICANT_P_VALUE);

		String chart = phenomeChartProvider.generatePvaluesOverviewChart(geneAccession, alleleAccession, pvaluesMap, Constants.SIGNIFICANT_P_VALUE, allelePageDTO.getParametersByProcedure(), phenotypingCenter);

//		model.addAttribute("palette", colorCoding.getPalette());
		model.addAttribute("chart", chart);
		model.addAttribute("rows", rows);
		model.addAttribute("pvaluesMap", pvaluesMap);
		model.addAttribute("allelePageDTO", allelePageDTO);

		return "experiments";
	}
	
	/**
	 * Error handler for gene not found
	 *
	 * @param exception
	 * @return redirect to error page
	 *
	 */
	@ExceptionHandler(GenomicFeatureNotFoundException.class)
	public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage",exception.getMessage());
        mv.addObject("acc",exception.getAcc());
        mv.addObject("type","MGI gene");
        mv.addObject("exampleURI", "/experiments/alleles/MGI:4436678?phenotyping_center=HMGU&pipeline_stable_id=ESLIM_001");
        return mv;
    }

	@ExceptionHandler(JDBCConnectionException.class)
	public ModelAndView handleJDBCConnectionException(JDBCConnectionException exception) {
        ModelAndView mv = new ModelAndView("uncaughtException");
        System.out.println(ExceptionUtils.getFullStackTrace(exception));
        mv.addObject("errorMessage", "An error occurred connecting to the database");
        return mv;
    }

	@ExceptionHandler(Exception.class)
	public ModelAndView handleGeneralException(Exception exception) {
        ModelAndView mv = new ModelAndView("uncaughtException");
        System.out.println(ExceptionUtils.getFullStackTrace(exception));
        return mv;
    }


}
