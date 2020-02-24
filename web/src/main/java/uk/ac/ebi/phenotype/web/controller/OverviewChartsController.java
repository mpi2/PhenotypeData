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
import org.mousephenotype.cda.constants.OverviewChartsConstants;
import org.mousephenotype.cda.db.pojo.DiscreteTimePoint;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.utilities.ImpressUtils;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.mousephenotype.cda.solr.web.dto.StackedBarsData;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.ChartData;
import uk.ac.ebi.phenotype.chart.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class OverviewChartsController {


	private GenotypePhenotypeService genotypePhenotypeService;
	private ImpressUtils             impressUtils;
	private ObservationService       observationService;
	private ParameterRepository      parameterRepository;
	private StatisticalResultService statisticalResultService;

	@Inject
	public OverviewChartsController(
            GenotypePhenotypeService genotypePhenotypeService,
            ImpressUtils impressUtils,
            ObservationService observationService,
            ParameterRepository parameterRepository,
            StatisticalResultService statisticalResultService)
	{
		this.genotypePhenotypeService = genotypePhenotypeService;
		this.impressUtils = impressUtils;
		this.observationService = observationService;
		this.parameterRepository = parameterRepository;
		this.statisticalResultService = statisticalResultService;
	}


	@RequestMapping(value="/chordDiagram", method=RequestMethod.GET)
	public String getGraph(
			@RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes){

		model.addAttribute("phenotypeName", (phenotypeName != null) ? new JSONArray(phenotypeName.stream().distinct().collect(Collectors.toList())) : null);
		return "chordDiagram";

	}


	@ResponseBody
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping(value="/chordDiagram.json", method=RequestMethod.GET)
	public String getMatrix(
			@RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
			@RequestParam(required = false, value = "idg") Boolean idg,
			@RequestParam(required = false, value = "idgClass") String idgClass) {

			return genotypePhenotypeService.getPleiotropyMatrix(phenotypeName, idg, idgClass).toString();

	}


	@ResponseBody
	@RequestMapping(value="/chordDiagram.csv", method=RequestMethod.GET)
	public String getChordDiagramDownload(
			@RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
			@RequestParam(required = false, value = "idg") Boolean idg,
			@RequestParam(required = false, value = "idgClass") String idgClass,
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) {

		try {
			return genotypePhenotypeService.getPleiotropyDownload(phenotypeName, idg, idgClass);
		} catch (IOException | SolrServerException e) {
			e.printStackTrace();
		}
		return "";
	}


	@RequestMapping(value="/overviewCharts/{phenotype_id}", method=RequestMethod.GET)
	public String getGraph(
		@PathVariable String phenotype_id,
		@RequestParam(required = true, value = "parameter_id") String parameterId,
		@RequestParam(required = false, value = "center") String center,
		@RequestParam(required = false, value = "sex") String sex,
		@RequestParam(required = false, value = "all_centers") String allCenters,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException , URISyntaxException, SQLException{

			String[] centerArray = (center != null) ? center.split(",") : null;
			String[] sexArray = (sex != null) ? sex.split(",") : null;
			String[] allCentersArray = (allCenters != null) ? allCenters.split(",") : null;
			String[] centers = (centerArray != null) ? centerArray : allCentersArray;

			model.addAttribute("chart", getDataOverviewChart(phenotype_id, model, parameterId, centers, sexArray));

			return "overviewChart";

	}


	public ChartData getDataOverviewChart(String mpId, Model model, String parameter, String[] center, String[] sex)
	throws SolrServerException, IOException , URISyntaxException, SQLException{

		CategoricalChartAndTableProvider cctp = new CategoricalChartAndTableProvider();
		TimeSeriesChartAndTableProvider tstp = new TimeSeriesChartAndTableProvider();
		UnidimensionalChartAndTableProvider uctp = new UnidimensionalChartAndTableProvider();
		Parameter p = parameterRepository.getFirstByStableId(parameter);
		ChartData chartRes = null;
		List<String> genes = null;
		String[] centerToFilter = center;

		// Assuming that different versions of a procedure will keep the same name.
		// Hibernate.initialize(p.getProcedures().iterator().next().getName());
		String procedureName = p.getProcedures().iterator().next().getName();

		if (p != null){

			genes = genotypePhenotypeService.getGenesAssocByParamAndMp(parameter, mpId);

			if (centerToFilter == null) { // first time we load the page.
				// We need to know centers for the controls, otherwise we show all controls
				Set <String> tempCenters = observationService.getCenters(p, genes, OverviewChartsConstants.B6N_STRAINS, "experimental");
				centerToFilter = tempCenters.toArray(new String[0]);
			}

			if( impressUtils.checkType(p).equals(ObservationType.categorical) ){
				CategoricalSet controlSet = observationService.getCategories(p, null , "control", OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex);
				controlSet.setName("Control");
				CategoricalSet mutantSet = observationService.getCategories(p, null, "experimental", OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex);
				mutantSet.setName("Mutant");
				List<ChartData> chart = cctp.doCategoricalDataOverview(controlSet, mutantSet, model, p, procedureName);
				if (chart.size() > 0){
					chartRes = chart.get(0);
				}
			}

			else if ( impressUtils.checkType(p).equals(ObservationType.time_series) ){
				Map<String, List<DiscreteTimePoint>> data = new HashMap<String, List<DiscreteTimePoint>>();
				data.put("Control", observationService.getTimeSeriesControlData(parameter, OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex));
				data.putAll(observationService.getTimeSeriesMutantData(parameter, genes, OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex));
				ChartData chart = tstp.doTimeSeriesOverviewData(data, p);
				chart.setId(parameter);
				chartRes = chart;
			}

			else if ( impressUtils.checkType(p).equals(ObservationType.unidimensional) ){
				StackedBarsData data = statisticalResultService.getUnidimensionalData(p, genes, OverviewChartsConstants.B6N_STRAINS, "experimental", centerToFilter, sex);
				chartRes = uctp.getStackedHistogram(data, p, procedureName);
			}

			if (chartRes != null && center == null && sex == null){ // we don't do a filtering
				// we want to offer all filter values, not to eliminate males if we filtered on males
				// plus we don't want to do another SolR call each time to get the same data
				Set<String> centerFitlers =	observationService.getCenters(p, genes, OverviewChartsConstants.B6N_STRAINS, "experimental");
				model.addAttribute("centerFilters", centerFitlers);
			}
		}

		return chartRes;
	}
}