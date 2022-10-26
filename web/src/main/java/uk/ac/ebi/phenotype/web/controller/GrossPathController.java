package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GrossPathService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.OntologyBean;
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
	
	@Autowired
	MpService mpService;
	
	
	@RequestMapping("/grosspath/{acc}/{parameterStableId}")
	public String grossPath(@PathVariable String acc, @PathVariable String parameterStableId, Model model) throws SolrServerException, IOException {
		//exmple Lpin2 MGI:1891341
		//best example is MGI:2148793
		System.out.println("calling gross path controller");
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("gene", gene);
		
		List<ObservationDTO> allObservations = grossPathService.getObservationsForGrossPathForGene(acc, parameterStableId.contains("ALT"));
		int sampleSize=grossPathService.getSampleToObservationMap(allObservations).size();
		System.out.println("sample size="+sampleSize);
		List<SolrDocument> images = grossPathService.getGrossPathImagesForGene(acc);
		//abnormal observations informs us of which Anatomy terms we need stats for both normal and abnormal numbers
		List<ObservationDTO> abnormalObservations = grossPathService.getAbnormalObservations(allObservations);
		//grossPathService.processForAbnormalAnatomies(allObservations, abnormalObservations);
		List<GrossPathPageTableRow> grossPathRows = grossPathService.getSummaryTableData(allObservations, images, abnormalObservations, false, parameterStableId);
		for (GrossPathPageTableRow row : grossPathRows) {
            System.out.println("row="+row);
        }
		model.addAttribute("sampleSize",sampleSize);
		model.addAttribute("pathRows", grossPathRows);
		model.addAttribute("extSampleIdToObservations", allObservations);
		model.addAttribute("images", images);
		return "grosspath";	
	}
	
	
	@RequestMapping("/grosspathdetails/{acc}")
	public String grossPathDetails(@PathVariable String acc, Model model) throws SolrServerException, IOException {
		//exmple Lpin2 MGI:1891341
		//best example is MGI:2148793
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("gene", gene);
		
		List<ObservationDTO> allObservations = grossPathService.getObservationsForGrossPathForGene(acc, false);
		int sampleSize=grossPathService.getSampleToObservationMap(allObservations).size();
		System.out.println("sample size="+sampleSize);
		List<SolrDocument> images = grossPathService.getGrossPathImagesForGene(acc);
		//abnormal observations informs us of which Anatomy terms we need stats for both normal and abnormal numbers
		List<ObservationDTO> abnormalObservations = grossPathService.getAbnormalObservations(allObservations);
		//grossPathService.processForAbnormalAnatomies(allObservations, abnormalObservations);
		List<GrossPathPageTableRow> grossPathRows = grossPathService.getTableData(allObservations, images);
		model.addAttribute("sampleSize",sampleSize);
		model.addAttribute("pathRows", grossPathRows);
		model.addAttribute("extSampleIdToObservations", allObservations);
		model.addAttribute("images", images);
		return "grosspath";	
	}
}

//other examples:
//<int name="MGI:2148793">34</int>
//<int name="MGI:1929872">11</int>
//<int name="MGI:1923517">9</int>
//<int name="MGI:95707">9</int>
//<int name="MGI:108031">6</int>
//<int name="MGI:109320">6</int>
//<int name="MGI:109497">6</int>
//<int name="MGI:1914346">6</int>
//<int name="MGI:1932970">6</int>
//<int name="MGI:2145316">6</int>
//<int name="MGI:2679446">6</int>
//<int name="MGI:3045249">6</int>
//<int name="MGI:104556">5</int>
//<int name="MGI:104725">5</int>
//<int name="MGI:108034">5</int>
//<int name="MGI:1913955">5</int>
//<int name="MGI:2183426">5</int>
//<int name="MGI:95568">5</int>
//<int name="MGI:98852">5</int>
//<int name="MGI:106604">4</int>
//<int name="MGI:1261433">4</int>
//<int name="MGI:1336167">4</int>
//<int name="MGI:1336993">4</int>
//<int name="MGI:1914805">4</int>
//<int name="MGI:1916320">4</int>
//<int name="MGI:1918573">4</int>
//<int name="MGI:1919398">4</int>
//<int name="MGI:1919598">4</int>
//<int name="MGI:1932094">4</int>
//<int name="MGI:2159342">4</int>
//<int name="MGI:2387642">4</int>
//<int name="MGI:88456">4</int>
//<int name="MGI:94203">4</int>
//<int name="MGI:99161">4</int>
