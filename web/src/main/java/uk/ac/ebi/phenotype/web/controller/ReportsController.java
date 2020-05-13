/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.phenotype.chart.PieChartCreator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;


/**
 * Controller to house the endpoints related to the reports that are generated
 * each data release highlighting / assembling various aspects of the data.
 */

@Controller
public class ReportsController {

	private final GenotypePhenotypeService genotypePhenotypeService;
	private final MpService mpService;

	@Inject
	public ReportsController(
			@NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
			@NotNull MpService mpService) {
		this.genotypePhenotypeService = genotypePhenotypeService;
		this.mpService = mpService;
	}


	@RequestMapping(value = "/reports", method = RequestMethod.GET)
	public String defaultAction() {
		return "reports";
	}


	@RequestMapping(value = "/reports/ebidcc", method = RequestMethod.GET)
	public String ebiDccReport(Model model)
	throws IOException, URISyntaxException, SolrServerException {
		
		int exact = 0;
		int differentPvalue = 0;
		int moreGeneralCallDcc = 0;
		int moreGeneralCallEbi = 0;
		int ignore = 0;
		int differentMps = 0;
		List<CallDTO> ebiList = getCalls(genotypePhenotypeService.getTabbedCallSummary());
		List<CallDTO> noMatch = new ArrayList<>();
		List<String> ignoreList = new ArrayList<>();
		ignoreList.add("IMPC_ALZ");ignoreList.add("IMPC_VIA");ignoreList.add("IMPC_FER");ignoreList.add("IMPC_ELZ");ignoreList.add("IMPC_HIS");ignoreList.add("IMPC_GEM");
		ignoreList.add("IMPC_EMA");ignoreList.add("IMPC_GEP");ignoreList.add("IMPC_GPP");ignoreList.add("IMPC_EVP");ignoreList.add("IMPC_MAA");ignoreList.add("IMPC_GEO");
		ignoreList.add("IMPC_GPO");ignoreList.add("IMPC_EMO");ignoreList.add("IMPC_GPO");ignoreList.add("IMPC_EVO");ignoreList.add("IMPC_GPM");ignoreList.add("IMPC_EVM");
		ignoreList.add("IMPC_GEL");ignoreList.add("IMPC_HPL");ignoreList.add("IMPC_HEL");ignoreList.add("IMPC_EOL");ignoreList.add("IMPC_GPL");ignoreList.add("IMPC_EVL");
		
		Map<String, CallDTO> keyNoPvalNoMp = new HashMap<>();
		Set<String> keyExact = new HashSet<>();
		Map<String, CallDTO> keyNoPval = new HashMap<>();

		
		for (CallDTO ebi: ebiList){
			
			boolean someMatch = false; 
				
			if (keyExact.contains(ebi.getKey())){
					exact ++;
					someMatch = true;
			} else if (keyNoPval.containsKey(ebi.getKeyNoPval())){
					differentPvalue ++;
					someMatch = true;
			} else if (keyNoPvalNoMp.containsKey(ebi.getKeyNoPvalNoMp())
				&& ebi.mpParentIds.contains(keyNoPvalNoMp.get(ebi.getKeyNoPvalNoMp()).mpTermId)){
					moreGeneralCallDcc ++;
					someMatch = true;
			} else if (keyNoPvalNoMp.containsKey(ebi.getKeyNoPvalNoMp()) 
				&& keyNoPvalNoMp.get(ebi.getKeyNoPvalNoMp()).mpParentIds.contains(ebi.mpTermId)){
					moreGeneralCallEbi ++;
					someMatch = true;
			} else if (keyNoPvalNoMp.containsKey(ebi.getKeyNoPvalNoMp())){
					differentMps ++;
					someMatch = true;
			}
		
			if (!someMatch && ignoreList.contains(ebi.parameterStableId.substring(0,8))){
				ignore++;
//				System.out.println("Param not analyzed : " + ebi.getKey());
				someMatch = true;
			} 
			
			if (!someMatch){
				noMatch.add(ebi);
			}
		}
		
		Map<String, Integer> grouped = new HashMap<>();
		for (CallDTO call: noMatch){
			if (grouped.containsKey(call.parameterStableId)){
				grouped.put(call.parameterStableId, grouped.get(call.parameterStableId) + 1);
			} else {
				grouped.put(call.parameterStableId, 1);
				System.out.println("MISSING -- " + call);
			}
		}

		Map<String, Integer> labelToNumber = new HashMap<String, Integer>();
		labelToNumber.put("Exact match", exact);
		labelToNumber.put("Same MP, different p-value", differentPvalue);
		labelToNumber.put("More general call at the DCC", moreGeneralCallDcc);
		labelToNumber.put("More general call at the EBI", moreGeneralCallEbi);
		labelToNumber.put("MPs are different and significant ", differentMps);
		labelToNumber.put("No match but parameter is not analysed statistically ", ignore);
		labelToNumber.put("No match", ebiList.size()-differentPvalue-moreGeneralCallDcc-exact-moreGeneralCallEbi-ignore-differentMps);		
		
		model.addAttribute("chart", PieChartCreator.getPieChart(labelToNumber, "chartDiv", "How do EBI calls match the ones at the DCC?", "", null));
		
		model.addAttribute("exact", exact);
		model.addAttribute("differentPvalue", differentPvalue);
		model.addAttribute("moreGeneralCallDcc", moreGeneralCallDcc);
		model.addAttribute("moreGeneralCallEbi", moreGeneralCallEbi);
		model.addAttribute("ignore", ignore);
		model.addAttribute("differentMps", differentMps);
		model.addAttribute("total", ebiList.size());
		
		System.out.println("Exact " + exact + " differentPvalue "+ differentPvalue + " moreGeneralCallDcc " + moreGeneralCallDcc);
		
		return "ebidccCompare";
	}

	private List<CallDTO> getCalls(String content) 
	throws SolrServerException, IOException{

		String[] list = content.split("\\r\\n|\\n|\\r");
		List<CallDTO> res = new ArrayList<>();
		// Start from 1 to skip header column
		for (int i = 1; i< list.length; i++){
			res.add(new CallDTO(list[i].replace("\"", "")));
		}
		return res;
		
	}
	
	
	
	private class CallDTO{
		
		String mpTermId;
		String markerSymbol;
		String colonyId;
		String phenotypingCenter;
		String sex;
		String zygosity;
		String parameterStableId;
		Double pValue;
		List<String> mpParentIds; 
		
		public CallDTO(String line) 
		throws SolrServerException, IOException{

			String[] split = line.split(",");
			mpTermId= split[0];
			markerSymbol = split[1];
			colonyId = split[2];
			phenotypingCenter = split[3];
			sex = split[4];
			zygosity = split[5];
			parameterStableId = split[6];
			pValue = new Double(split[7]);
			mpParentIds = new ArrayList<>();
			
			for (OntologyBean parent: mpService.getParents(mpTermId)){
				mpParentIds.add(parent.getId());
			}
			
		}
		
		public String getKey(){
			return mpTermId +  markerSymbol + colonyId + phenotypingCenter + sex + zygosity + parameterStableId + pValue;
		}
		
		public String getKeyNoPval(){
			return mpTermId +  markerSymbol + colonyId + phenotypingCenter + sex + zygosity + parameterStableId;
		}
		
		public String getKeyNoPvalNoMp(){
			return markerSymbol + colonyId + phenotypingCenter + sex + zygosity + parameterStableId;
		}

		@Override
		public String toString() {
			return "CallDTO [mpTermId=" + mpTermId + ", markerSymbol=" + markerSymbol + ", colonyId=" + colonyId
					+ ", phenotypingCenter=" + phenotypingCenter + ", sex=" + sex + ", zygosity=" + zygosity
					+ ", parameterStableId=" + parameterStableId + ", pValue=" + pValue + ", mpParentIds=" + mpParentIds
					+ "]";
		}
			
	}
	
}

