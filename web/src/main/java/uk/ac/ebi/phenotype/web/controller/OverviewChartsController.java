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
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.impress.Utilities;
import org.mousephenotype.cda.db.pojo.DiscreteTimePoint;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.mousephenotype.cda.solr.web.dto.StackedBarsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.ChartData;
import uk.ac.ebi.phenotype.chart.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
public class OverviewChartsController {

	@Autowired
	private PhenotypePipelineDAO pipelineDao;

	@Autowired
	ObservationService os;

	@Autowired
	PostQcService gpService;


	@Autowired
	StatisticalResultService srs;

	@Autowired
	Utilities impressUtilities;

	public OverviewChartsController(){

	}


	@RequestMapping(value="/chordDiagram", method=RequestMethod.GET)
	public String getGraph(
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes){
				return "chordDiagram";

			}

	@ResponseBody
	@RequestMapping(value="/chordDiagram/matrix.json", method=RequestMethod.GET)
	public String getMatrix(
			Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) {

		return gpService.getPleiotropyMatrix();


	}



	@RequestMapping(value = "/chordDiagram/cities.csv", method = RequestMethod.GET)
	public void getCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv; charset=utf-8");
		response.getWriter().print("name,latitude,longitude,population,color\nExcelsior,37.7244,-122.421,0.083884,#E41A1C\nCrocker Amazon,37.7107,-122.4372,0.058439,#FFFF33\nMarina,37.8021,-122.4369,0.40364,#FF7F00\nNob Hill,37.793,-122.416,0.33259,#FF7F00\nNorth Beach,37.8045,-122.4076,0.28784,#FF7F00\nPacific Heights,37.7924,-122.4352,0.33382,#FF7F00\nPresidio Heights,37.7868,-122.4538,0.19965,#FF7F00\nRussian Hill,37.8014,-122.4182,0.34165,#FF7F00\nVisitacion Valley,37.7144,-122.4113,0.064774,#999999\nDiamond Heights,37.7423,-122.4423,0.074148,#984EA3\nGlen Park,37.7378,-122.4316,0.1226,#984EA3\nInner Sunset,37.7584,-122.4654,0.18995,#984EA3\nLakeshore,37.7225,-122.4885,0.092602,#984EA3\nOuter Mission,37.7239,-122.4439,0.10242,#984EA3\nWest Of Twin Peaks,37.7373,-122.4589,0.12473,#984EA3\nBayview,37.73,-122.3855,0.10309,#377EB8\nBernal Heights,37.7399,-122.4169,0.20043,#377EB8\nCastro,37.7624,-122.4348,0.34092,#377EB8\nHaight,37.7692,-122.4463,0.29559,#377EB8\nMission,37.7589,-122.4153,0.42583,#377EB8\nNoe,37.7493,-122.433,0.25158,#377EB8\nParkside,37.7411,-122.4892,0.092271,#377EB8\nPotrero,37.7583,-122.393,0.26353,#377EB8\nSouth Of Market,37.7764,-122.3994,0.55316,#377EB8\nTwin Peaks,37.752,-122.45,0.13105,#377EB8\nWestern Addition,37.7804,-122.4332,0.41192,#377EB8\nInner Richmond,37.7802,-122.4652,0.21194,#4DAF4A\nOuter Richmond,37.778,-122.4928,0.18916,#4DAF4A\nOuter Sunset,37.7553,-122.4938,0.12363,#4DAF4A\nSeacliff,37.7841,-122.5009,0.091937,#4DAF4A\nGolden Gate Park,37.7689,-122.4828,0.15941,#4DAF4A\nChinatown,37.7941,-122.407,0.24273,#F781BF\nDowntown,37.7835,-122.4158,0.44235,#F781BF\nFinancial District,37.7915,-122.3988,0.49915,#F781BF\nOcean View,37.7178,-122.4622,0.076638,#A65628");
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
		Parameter p = pipelineDao.getParameterByStableId(parameter);
		ChartData chartRes = null;
		List<String> genes = null;
		String[] centerToFilter = center;


		// Assuming that different versions of a procedure will keep the same name.
		String procedureName = p.getProcedures().iterator().next().getName();

		if (p != null){

			genes = gpService.getGenesAssocByParamAndMp(parameter, mpId);

			if (centerToFilter == null) { // first time we load the page.
				// We need to know centers for the controls, otherwise we show all controls
				Set <String> tempCenters = os.getCenters(p, genes, OverviewChartsConstants.B6N_STRAINS, "experimental");
				centerToFilter = tempCenters.toArray(new String[0]);
			}

			if( impressUtilities.checkType(p).equals(ObservationType.categorical) ){
				CategoricalSet controlSet = os.getCategories(p, null , "control", OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex);
				controlSet.setName("Control");
				CategoricalSet mutantSet = os.getCategories(p, null, "experimental", OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex);
				mutantSet.setName("Mutant");
				List<ChartData> chart = cctp.doCategoricalDataOverview(controlSet, mutantSet, model, p, procedureName);
				if (chart.size() > 0){
					chartRes = chart.get(0);
				}
			}

			else if ( impressUtilities.checkType(p).equals(ObservationType.time_series) ){
				Map<String, List<DiscreteTimePoint>> data = new HashMap<String, List<DiscreteTimePoint>>();
				data.put("Control", os.getTimeSeriesControlData(parameter, OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex));
				data.putAll(os.getTimeSeriesMutantData(parameter, genes, OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex));
				ChartData chart = tstp.doTimeSeriesOverviewData(data, p);
				chart.setId(parameter);
				chartRes = chart;
			}

			else if ( impressUtilities.checkType(p).equals(ObservationType.unidimensional) ){
				StackedBarsData data = srs.getUnidimensionalData(p, genes, OverviewChartsConstants.B6N_STRAINS, "experimental", centerToFilter, sex);
				chartRes = uctp.getStackedHistogram(data, p, procedureName);
			}

			if (chartRes != null && center == null && sex == null){ // we don't do a filtering
				// we want to offer all filter values, not to eliminate males if we filtered on males
				// plus we don't want to do another SolR call each time to get the same data
				Set<String> centerFitlers =	os.getCenters(p, genes, OverviewChartsConstants.B6N_STRAINS, "experimental");
				model.addAttribute("centerFilters", centerFitlers);
			}
		}

		return chartRes;
	}

}
