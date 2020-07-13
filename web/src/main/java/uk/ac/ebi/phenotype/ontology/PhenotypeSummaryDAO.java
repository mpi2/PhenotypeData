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
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.springframework.stereotype.Service;

import javax.inject.Named;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhenotypeSummaryDAO  {

	Map<String, String> topLevelMpIdsToNames;

	private final StatisticalResultService srService;
	private final MpService mpService;

	public PhenotypeSummaryDAO(
			@Named("statistical-result-service") StatisticalResultService srService,
			MpService mpService) {
		this.srService = srService;
		this.mpService = mpService;
	}

	public String getSexesRepresentationForPhenotypesSet(List<StatisticalResultDTO> resp) {
		
		// Collect the sexes from the significant documents
		Set<String> sexes = resp.stream()
				.filter(this::isSignificant)
				.map(x -> x.getPhenotypeSex() != null ? x.getPhenotypeSex() : Collections.singletonList(x.getSex()))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());

		String sex = null;

		if (sexes.size() == 2) {
			sex = "both sexes";
		} else if (sexes.contains(SexType.female.getName()) && !sexes.contains(SexType.male.getName())) {
			sex = SexType.female.toString();
		} else if (sexes.contains(SexType.male.getName()) && !sexes.contains(SexType.female.getName())) {
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
		boolean significant =  res.getSignificant() != null ? res.getSignificant() : false;

		// For Categorical data
		// If either of the sex specific (one sex only) tests are significant, but the combined sexes test
		// is not significant, we need to check each sex independently.  If either of the sexes are significant by
		// themselves, a phenotype call will have been made for that sex, even though the overall (combined) result
		// is not significant.
		if (res.getDataType().equals(Constants.CATEGORICAL_DATATYPE) && !significant) {
			if (
					(res.getFemaleKoEffectPValue()!=null && res.getFemaleKoEffectPValue() < Constants.P_VALUE_THRESHOLD) ||
					(res.getMaleKoEffectPValue()!=null && res.getMaleKoEffectPValue() < Constants.P_VALUE_THRESHOLD)) {
				significant = true;
			}
		}

		return significant;
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
