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
package uk.ac.ebi.phenotype.web.dao;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author tudose
 *
 */

@Service("threeI")
public class GenesSecondaryProject3IImpl implements GenesSecondaryProjectService {

	private MpService                mpService;
	private StatisticalResultService statisticalResultService;


	@Inject
	public GenesSecondaryProject3IImpl(
			@NotNull MpService mpService,
			@NotNull StatisticalResultService statisticalResultService)
	{
		this.mpService = mpService;
		this.statisticalResultService = statisticalResultService;
	}


	public Set<GenesSecondaryProject> getAccessionsBySecondaryProjectId(String projectId) {
		Set<GenesSecondaryProject> secondaryProjectBeans=new TreeSet<>();
		if (projectId.equalsIgnoreCase(GenesSecondaryProjectService.SecondaryProjectIds.threeI.name())){
			Set<String> accessions = statisticalResultService.getAccessionsByResourceName("3i");
			for(String accession: accessions){
				GenesSecondaryProject bean = new GenesSecondaryProject(accession);
				secondaryProjectBeans.add(bean);
			}
		}

		return secondaryProjectBeans;
	}

	@Override
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request) {

		return  statisticalResultService.getSecondaryProjectMapForResource("3i");
	}

	@Override
	public List<BasicBean> getXAxisForHeatMap() {

		List<BasicBean> mp = new ArrayList<>();
		try {
			Set<BasicBean> topLevelPhenotypes = mpService.getAllTopLevelPhenotypesAsBasicBeans();
			mp.addAll(topLevelPhenotypes);
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return mp;
	}
}