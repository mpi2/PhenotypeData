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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.chart.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.ChartData;
import uk.ac.ebi.phenotype.chart.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;


@Controller
public class BaselineChartsController {

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

	public BaselineChartsController(){

	}

	@RequestMapping(value="/baselineCharts/{phenotype_id}", method=RequestMethod.GET)
	public String getGraph(
		@PathVariable String phenotype_id,
		@RequestParam(required = true, value = "parameter_id") String parameterId,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException , URISyntaxException, SQLException{
		System.out.println("calling baselineCharts");
			
			return "baselineChart";
	}

	

}
