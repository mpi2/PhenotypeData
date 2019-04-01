package uk.ac.ebi.phenotype.web.controller;

import static org.springframework.web.bind.annotation.ValueConstants.DEFAULT_NONE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.json.JSONException;
import org.mousephenotype.cda.db.pojo.CategoricalResult;
import org.mousephenotype.cda.enumerations.EmbryoViability;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.solr.web.dto.EmbryoViability_DTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTO;
import org.mousephenotype.cda.web.ChartType;
import org.mousephenotype.cda.web.TimeSeriesConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.chart.CategoricalResultAndCharts;
import uk.ac.ebi.phenotype.chart.ChartData;
import uk.ac.ebi.phenotype.chart.FileUnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.ScatterChartAndData;
import uk.ac.ebi.phenotype.chart.UnidimensionalDataSet;
import uk.ac.ebi.phenotype.chart.UnidimensionalStatsObject;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;
import uk.ac.ebi.phenotype.web.dao.Stats;
import uk.ac.ebi.phenotype.web.dao.StatsService;

@Controller
public class FileChartController {

	 @Resource(name = "globalConfiguration")
	    private Map<String, String> config;
	 
	//private FileUnidimensionalChartAndTableProvider fileProvider;
	 @Autowired
	private StatsService statsProvider;

	@Inject
    public FileChartController(StatsService statsProvider) {
		this.statsProvider=statsProvider;
	}
    		
    		
	
}
