package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

/**
 * @author ilinca
 * @since 2016/01/?
 */
@Controller
public class EmbryoController {
	
	@Autowired
	private ObservationService os;

	@Autowired
	private GeneService gs;
	
	@RequestMapping(value = "/embryo", method = RequestMethod.GET)
	public String loadPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

		AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
		List<String> resources = new ArrayList<>();
		resources.add("IMPC");
		Map<String, Set<String>> viabilityRes = os.getViabilityCategories(resources);

		Map<String, Long> viabilityMap = os.getViabilityCategories(viabilityRes);
		List<ObservationService.EmbryoTableRow> viabilityTable = os.consolidateZygosities(viabilityRes);
		
		model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long> (), viabilityMap, "", "viabilityChart"));
		model.addAttribute("viabilityTable", viabilityTable);
		
		
		System.out.println("VIA map " + viabilityMap);
		System.out.println("viabilityTable="+viabilityTable);
		return "embryo";
	}
	
	@RequestMapping(value = "/embryo/vignettes", method = RequestMethod.GET)
	public String loadVignettes(Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

		
		return "embryoVignettes";
	}

	
}
