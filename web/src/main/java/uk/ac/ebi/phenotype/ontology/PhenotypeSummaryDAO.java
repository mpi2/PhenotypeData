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

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhenotypeSummaryDAO  {

	@Autowired
	private StatisticalResultService srService;
	
	@Autowired
	private MpService mpService;
	
	Map<String, String> topLevelMpIdsToNames;
	
	public PhenotypeSummaryDAO() throws MalformedURLException {
//		try {
//			this.getTopLevelMpIdToNames();
//		} catch (SolrServerException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public String getSexesRepresentationForPhenotypesSet(List<StatisticalResultDTO> resp) {
		
		String resume = "";

		// Collect the sexes from the significant documents
		Set<SexType> sexes = resp.stream()
				.filter(this::isSignificant)
				.map(x -> SexType.valueOf(x.getSex()))
				.collect(Collectors.toSet());

		String sex = null;

		if (sexes.size() == 2 || sexes.contains(SexType.not_considered)) {
			sex = "both sexes";
		} else if (sexes.contains(SexType.female) && !sexes.contains(SexType.male)) {
			sex = SexType.female.toString();
		} else if (sexes.contains(SexType.male) && !sexes.contains(SexType.female)) {
			sex = SexType.male.toString();
		}

		return sex;

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
		return res.getSignificant() != null ? res.getSignificant() : false;
	}


	public Map<ZygosityType, PhenotypeSummaryBySex> getSummaryObjectsByZygosity(String gene) throws Exception {
		
		Map< ZygosityType, PhenotypeSummaryBySex> res =  new HashMap<>();		
		
		for (ZygosityType zyg : ZygosityType.values()){
			
			PhenotypeSummaryBySex resSummary = new PhenotypeSummaryBySex();
			Map<String, List<StatisticalResultDTO>> summary = srService.getPhenotypesForTopLevelTerm(gene, zyg);
						
			for (String id: summary.keySet()){
				List<StatisticalResultDTO> resp = summary.get(id);
				String sex = getSexesRepresentationForPhenotypesSet(resp);
				String mpName = getTopLevelNameFromId(id);
				long n = getNumSignificantCalls(resp);
				boolean significant = n > 0;
				PhenotypeSummaryType phen;
				if (significant) {
					 phen = new PhenotypeSummaryType(id, mpName, sex, n, significant);
				} else {
					// only record info about non significant calls if there is no significant one as that's how the icon map needs this
					phen = new PhenotypeSummaryType(id, mpName, sex, resp.size(), significant);
				}
				resSummary.addPhenotye(phen);
			}
			
			if (resSummary.getTotalPhenotypesNumber() > 0){
				res.put(zyg, resSummary);
			}
		}

		return res;
	}

	private  String getTopLevelNameFromId(String mpId) throws SolrServerException, IOException {
		//if mps empty then fill them otherwise return a cashed version
		String mpName=null;
		loadTopLevelMpIdToNamesMap();
		if(mpId!=null && !mpId.equals("")){
			mpName=this.topLevelMpIdsToNames.get(mpId);
			if(mpName==null){
				System.err.println("mp id not found in top level terms id="+mpId);
				mpName= "obsolete other phenotype";
			}
		}
		return mpName;
	}

	private void loadTopLevelMpIdToNamesMap() throws SolrServerException, IOException {
		
		//only load cache if empty
		if (this.topLevelMpIdsToNames == null || this.topLevelMpIdsToNames.isEmpty()) {
			this.topLevelMpIdsToNames = new HashMap<>();
			System.out.println("cash empty refreshing top level mp cash in PhenotypeSummaryDao");
			Set<BasicBean> mpBeans = mpService.getAllTopLevelPhenotypesAsBasicBeans();
			for (BasicBean bean : mpBeans) {
				this.topLevelMpIdsToNames.put(bean.getId(), bean.getName());
			}

		}
	}
	
	}
