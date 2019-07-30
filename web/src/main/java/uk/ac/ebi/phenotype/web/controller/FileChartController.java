package uk.ac.ebi.phenotype.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.phenotype.web.dao.StatisticsService;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.Map;

@Controller
public class FileChartController {

	 @Resource(name = "globalConfiguration")
	    private Map<String, String> config;
	 
	//private FileUnidimensionalChartAndTableProvider fileProvider;
	 @Autowired
	private StatisticsService statsProvider;

	@Inject
    public FileChartController(StatisticsService statsProvider) {
		this.statsProvider=statsProvider;
	}
}
