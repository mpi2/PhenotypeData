package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
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

	@RequestMapping(value = "/embryo", method = RequestMethod.GET)
	public String loadMpPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
	throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

		AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
		List<String> resources = new ArrayList<>();
		resources.add("IMPC");
		HashMap<String, Long> viabilityMap = os.getViabilityCategories(resources);
		HashMap<String, Long> viabilityTable = consolidateViabilityTable(viabilityMap);
		
		model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long> (), viabilityMap, "IMPC Viability", "viabilityChart"));
		model.addAttribute("viabilityTable", viabilityTable);
		
		return "embryo";
	}

	HashMap<String, Long> consolidateViabilityTable(HashMap<String, Long> map){
		
		HashMap<String, Long> res = new HashMap<>();
		Long all = Long.getLong("0");
		
		for (String key: map.keySet()){
			all += map.get(key);
			String tableKey = "subviable";
			if (key.toLowerCase().contains(tableKey)){
				if (res.containsKey(tableKey)){
					res.put(tableKey, res.get(tableKey) + map.get(key));
				} else {
					res.put(tableKey, map.get(key));					
				}
			}
			tableKey = "viable";
			if (key.toLowerCase().contains(tableKey) && !key.contains("subviable")){
				if (res.containsKey(tableKey)){
					res.put(tableKey, res.get(tableKey) + map.get(key));
				} else {
					res.put(tableKey, map.get(key));					
				}
			}
			tableKey = "lethal";
			if (key.toLowerCase().contains(tableKey)){
				if (res.containsKey(tableKey)){
					res.put(tableKey, res.get(tableKey) + map.get(key));
				} else {
					res.put(tableKey, map.get(key));					
				}
			}
		}
		
		res.put("all", all);
		
		return res;
	}

}
