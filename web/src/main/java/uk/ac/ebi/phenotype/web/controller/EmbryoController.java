package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
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
		Map<String, Long> viabilityMap = getViabilityCategories(viabilityRes);
		List<EmbryoTableRow> viabilityTable = consolidateZygosities(viabilityRes);
		List<GeneDTO> genesWithEmbryoViewer = gs.getGenesWithEmbryoViewer();
		
		model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long> (), viabilityMap, "", "viabilityChart"));
		model.addAttribute("viabilityTable", viabilityTable);
		model.addAttribute("genesWithEmbryoViewer", genesWithEmbryoViewer);
		
		System.out.println("VIA map " + viabilityMap);
		
		return "embryo";
	}

	
	/**
	 * @author ilinca
	 * @since 2016/01/21
	 * @param map <viability category, number of genes in category>
	 * @return 
	 */
	List<EmbryoTableRow> consolidateZygosities(Map<String, Set<String>> map){
		
		Map<String, Set<String>> res = new LinkedHashMap<>();
		List<EmbryoTableRow> result = new ArrayList<>();
		
		// Consolidate by zygosities so that we show "subviable" in the table, not "hom-subviable" and "het-subviable"
		for (String key: map.keySet()){
			
			String tableKey = "subviable";
			if (key.toLowerCase().contains(tableKey)){
				if (res.containsKey(tableKey)){
					res.get(tableKey).addAll(map.get(key));
				} else {
					res.put(tableKey, new HashSet<String>(map.get(key)));					
				}
			} else {
				tableKey = "viable";
				if (key.toLowerCase().contains(tableKey) && !key.contains("subviable")){
					if (res.containsKey(tableKey)){
						res.get(tableKey).addAll(map.get(key));
					} else {
						res.put(tableKey, new HashSet<String>(map.get(key)));					
					}
				} else {
						tableKey = "lethal";
					if (key.toLowerCase().contains(tableKey)){
						if (res.containsKey(tableKey)){
							res.get(tableKey).addAll(map.get(key));
						} else {
							res.put(tableKey, new HashSet<String>(map.get(key)));					
						}
					}
				}
			}
		}
		
		// Fill list of EmbryoTableRows so that it's easiest to access from jsp.
		for (String key: res.keySet()){
			EmbryoTableRow row = new EmbryoTableRow();
			row.setCategory(key);
			row.setCount( new Long(res.get(key).size()));
			if (key.equalsIgnoreCase("Lethal")){
				row.setMpId("MP:0011100");
			} else  if (key.equalsIgnoreCase("Subviable")){
				row.setMpId("MP:0011110");
			} else {
				row.setMpId(null);
			}
			result.add(row);
			System.out.println("Added:: " + row);
		}
		return result;
		
	}
	
	public static Comparator<String> getComparatorForViabilityChart()	{   
		Comparator<String> comp = new Comparator<String>(){
		    @Override
		    public int compare(String param1, String param2)
		    {
		    	if (param1.contains("- Viable") && !param2.contains("- Viable")){
					return -1;
				}
				if (param2.contains("- Viable") && !param1.contains("- Viable")){
					return 1;
				}
				if (param2.contains("- Lethal") && !param1.contains("- Lethal")){
					return 1;
				}
				if (param2.contains("- Lethal") && !param1.contains("- Lethal")){
					return 1;
				}
				return param1.compareTo(param2);
		    }
		};
		return comp;
	}
	
	/**
	 * @author ilinca
	 * @since 2016/01/28
	 * @param facets
	 * @return 
	 * @throws SolrServerException
	 */
	public Map<String, Long> getViabilityCategories(Map<String, Set<String>>facets){

		Map<String, Long> res = new TreeMap<>(getComparatorForViabilityChart());
		for (String category : facets.keySet()){
			Long geneCount = new Long(facets.get(category).size());
			res.put(category, geneCount);
		}

		return res;
	}

	public class EmbryoTableRow{
		
		String category;
		String mpId;
		Long count;
		
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getMpId() {
			return mpId;
		}
		public void setMpId(String mpId) {
			this.mpId = mpId;
		}
		public Long getCount() {
			return count;
		}
		public void setCount(Long geneNo) {
			this.count = geneNo;
		}
		@Override
		public String toString() {
			return "EmbryoTableRow [category=" + category + ", mpId=" + mpId + ", count=" + count + "]";
		}
		
		
	}
	
}
