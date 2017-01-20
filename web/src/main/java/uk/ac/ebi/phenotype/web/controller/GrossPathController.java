package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GrossPathService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.GrossPathPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class GrossPathController {

	private final Logger log = LoggerFactory.getLogger(GrossPathController.class);
	
	@Autowired
	GrossPathService grossPathService;
	
	@Autowired
	GeneService geneService;
	
	
	@RequestMapping("/grosspath/{acc}")
	public String grossPath(@PathVariable String acc, Model model) throws SolrServerException, IOException {
		//exmple Lpin2 MGI:1891341
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("gene", gene);
		
		List<ObservationDTO> allObservations = grossPathService.getObservationsForGrossPathForGene(acc);
		List<ImageDTO> images = grossPathService.getGrossPathImagesForGene(acc);
		List<GrossPathPageTableRow> grossPathRows = grossPathService.getTableData(allObservations);
		model.addAttribute("histopathRows", grossPathRows);
		model.addAttribute("extSampleIdToObservations", allObservations);
		model.addAttribute("images", images);
		return "grosspath";	
	}
}
