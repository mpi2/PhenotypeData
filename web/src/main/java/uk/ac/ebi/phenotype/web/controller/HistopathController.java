package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.HistopathService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HistopathController {

	private final Logger log = LoggerFactory.getLogger(HistopathController.class);
	
	@Autowired
	HistopathService histopathService;
	
	@Autowired
	GeneService geneService;
	
	
	@RequestMapping("/histopath/{acc}")
	public String histopath(@PathVariable String acc, Model model) throws SolrServerException{
		
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("gene", gene);
		List<HistopathPageTableRow> histopathRows = histopathService.getTableData(acc);
		Set<String> parameterNames=new TreeSet<>();
		for(HistopathPageTableRow row: histopathRows){
			parameterNames.addAll(row.getParameterNames());
		}
		Map<String, List<ObservationDTO>> extSampleIdToObservations = histopathService.getObservations();

		model.addAttribute("histopathRows", histopathRows);
		model.addAttribute("extSampleIdToObservations", extSampleIdToObservations);
		model.addAttribute("parameterNames", parameterNames);
		return "histopath";	
	}
}
