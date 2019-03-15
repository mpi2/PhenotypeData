package uk.ac.ebi.phenotype.web.controller;

import static org.springframework.web.bind.annotation.ValueConstants.DEFAULT_NONE;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.file.stats.FileExperimentDao;
import org.mousephenotype.cda.file.stats.Result;
import org.mousephenotype.cda.file.stats.Stats;
import org.mousephenotype.cda.file.stats.StatsJson;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.web.ChartType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.chart.FileUnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;

@Controller
public class FileChartController {

	 @Resource(name = "globalConfiguration")
	    private Map<String, String> config;
	 
	//private FileUnidimensionalChartAndTableProvider fileProvider;
	private FileExperimentDao statsProvider;

	@Inject
    public FileChartController(FileExperimentDao statsProvider) {
		this.statsProvider=statsProvider;
	}
    		
    		
	@RequestMapping("/fileChart")
    public String chart(@RequestParam(required = true, value = "experimentNumber", defaultValue = "1") String experimentNumber,
                        @RequestParam(required = false, value = "accession") String[] accession,
                        @RequestParam(required = false, value = "strain_accession_id") String strain,
                        @RequestParam(required = false, value = "allele_accession_id") String alleleAccession,
                        @RequestParam(required = false, value = "metadata_group", defaultValue = DEFAULT_NONE) String metadataGroup,
                        @RequestParam(required = false, value = "parameter_stable_id") String parameterStableId,
                        @RequestParam(required = false, value = "gender") String[] gender,
                        @RequestParam(required = false, value = "zygosity") String[] zygosity,
                        @RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
                        @RequestParam(required = false, value = "strategy") String[] strategies,
                        @RequestParam(required = false, value = "pipeline_stable_id") String pipelineStableId,
                        @RequestParam(required = false, value = "chart_type") ChartType chartType,
                        @RequestParam(required = false, value = "chart_only", defaultValue = "false") boolean chartOnly,
                        @RequestParam(required = false, value = "standAlone") boolean standAlone, Model model)
            throws ParameterNotFoundException, IOException, URISyntaxException, SolrServerException, SpecificExperimentException {
System.out.println("got to file chart controller");
		List<String> filePaths = statsProvider.getParameterOptionsForRequest(phenotypingCenter, parameterStableId, metadataGroup);
		System.out.println("filePaths.size="+filePaths.size());
        Stats result = statsProvider.getStatsSummary(phenotypingCenter, "", parameterStableId, "", zygosity[0], metadataGroup);
		return "fileChart";
	}
}
