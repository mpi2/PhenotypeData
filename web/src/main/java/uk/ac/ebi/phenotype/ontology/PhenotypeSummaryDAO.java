/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.ontology;

import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhenotypeSummaryDAO  {

	@Autowired
	private StatisticalResultService srService;
	
	public PhenotypeSummaryDAO() throws MalformedURLException {
	}

	public String getSexesRepresentationForPhenotypesSet(List<StatisticalResultDTO> resp) {
		
		String resume = "";

		// Collect the sexes from the significant documents
		Set<String> sexes = resp.stream()
				.filter(this::isSignificant)
				.map(x -> x.getPhenotypeSex() != null ? x.getPhenotypeSex() : Arrays.asList(x.getSex()))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());

		String sex = null;

		if (sexes.size() == 2) {
			sex = "both sexes";
		} else if (sexes.contains(SexType.female) && !sexes.contains(SexType.male)) {
			sex = SexType.female.toString();
		} else if (sexes.contains(SexType.male) && !sexes.contains(SexType.female)) {
			sex = SexType.male.toString();
		}

		return sex;

	}

	
	public Set<String> getDataSourcesForPhenotypesSet(List<StatisticalResultDTO> resp) {
		
		Set <String> data = new HashSet <> ();
		for (StatisticalResultDTO doc : resp) {
			data.add(doc.getResourceName());
		}
		return data;
		
	}

	
	private long getNumSignificantCalls (List<StatisticalResultDTO> res){
		
		long n = 0; 
		if (res != null && res.size() > 0 && res.get(0) != null){ 
			for (StatisticalResultDTO doc: res){
				if (isSignificant(doc)){
					n += doc.getPhenotypeSex()!=null ? doc.getPhenotypeSex().size() : 2;
				} 
			}
		}

		return n;
	}

	
	private boolean isSignificant (StatisticalResultDTO res){
		return res.getSignificant()!=null ? res.getSignificant() : false;
	}


	public Map<ZygosityType, PhenotypeSummaryBySex> getSummaryObjectsByZygosity(String gene) throws Exception {
		
		Map< ZygosityType, PhenotypeSummaryBySex> res =  new HashMap<>();		
		
		for (ZygosityType zyg : ZygosityType.values()){
			
			PhenotypeSummaryBySex resSummary = new PhenotypeSummaryBySex();
			Map<String, String> mps = srService.getTopLevelMPTerms(gene, zyg);
			Map<String, List<StatisticalResultDTO>> summary = srService.getPhenotypesForTopLevelTerm(gene, zyg);
						
			for (String id: summary.keySet()){
				
				List<StatisticalResultDTO> resp = summary.get(id);
				String sex = getSexesRepresentationForPhenotypesSet(resp);
				Set<String> ds = getDataSourcesForPhenotypesSet(resp);
				String mpName = mps.get(id);
				long n = getNumSignificantCalls(resp);
				boolean significant = (n > 0) ? true : false;
				PhenotypeSummaryType phen = new PhenotypeSummaryType(id, mpName, sex, n, ds, significant);
				resSummary.addPhenotye(phen);
			}
			
			if (resSummary.getTotalPhenotypesNumber() > 0){
				res.put(zyg, resSummary);
			}
		}
		return res;
	}
	
	}
