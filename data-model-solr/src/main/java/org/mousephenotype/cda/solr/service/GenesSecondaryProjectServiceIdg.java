/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.EssentialGeneDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * @author jwarren
 */
@Service("idg")
public class GenesSecondaryProjectServiceIdg  {

	public static final String DEVIANCE_COLOR      = "rgb(191, 75, 50)";
	public static final String COULD_NOT_ANALYSE   = "rgb(230, 242, 246)";
	public static final String NO_SIGNIFICANT_CALL = "rgb(247, 157, 70)";
	public static final String NO_DATA             = "rgb(119, 119, 119)";

	private GeneService                     geneService;
	private EssentialGeneService			essentialGeneService;
	private MpService                       mpService;

	@Inject
	public GenesSecondaryProjectServiceIdg(
			@NotNull GeneService geneService,
			@NotNull EssentialGeneService essentialGeneService,
			@NotNull MpService mpService)
	{
		this.geneService = geneService;
		this.essentialGeneService=essentialGeneService;
		this.mpService = mpService;
	}

	public Set<GenesSecondaryProject> getAllBySecondaryProjectId(){
		HashSet<GenesSecondaryProject> infos = new HashSet();
		try {
			List<EssentialGeneDTO> geneList = essentialGeneService.getAllIdgGeneList();

			for(EssentialGeneDTO gene:geneList){
				GenesSecondaryProject info=new GenesSecondaryProject();
				info.setGroupLabel(gene.getIdgFamily());
				info.setMgiGeneAccessionId(gene.getMgiAccession());
				info.setSecondaryProjectId(gene.getIdgIdl());
				info.setHumanGeneSymbol(gene.getHumanGeneSymbol());
				infos.add(info);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return infos;
	}

	public Set<GenesSecondaryProject> getAllBySecondaryProjectIdAndGroupLabel(String groupLabel){
		HashSet<GenesSecondaryProject> infos = new HashSet();
		try {
			List<EssentialGeneDTO> geneList = essentialGeneService.getAllIdgGeneListByGroupLabel(groupLabel);

			for(EssentialGeneDTO gene:geneList){
				GenesSecondaryProject info=new GenesSecondaryProject();
				info.setGroupLabel(gene.getIdgFamily());
				info.setMgiGeneAccessionId(gene.getMgiAccession());
				info.setSecondaryProjectId(gene.getIdgIdl());
				info.setHumanGeneSymbol(gene.getHumanGeneSymbol());
				infos.add(info);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return infos;
	}



	@Cacheable("topLevelPhenotypesGeneRows")
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request) throws SolrServerException, IOException, SQLException {

		List<GeneRowForHeatMap> geneRows = new ArrayList<>();
		List<BasicBean> parameters = this.getXAxisForHeatMap();
		String geneUrl =  request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();

		// get a list of mgi geneAccessionIds for the project - which will be the row headers
		Set<GenesSecondaryProject> projectBeans = this.getAllBySecondaryProjectId();

		Set<String>accessions = projectBeans
				   .stream()
				.map(GenesSecondaryProject::getMgiGeneAccessionId)
				.collect(Collectors.toSet());


		List<GeneDTO> geneToMouseStatus = geneService.getProductionStatusForGeneSet(accessions, null);
		Map<String, GeneRowForHeatMap> rows = geneService.getSecondaryProjectMapForGeneList(geneToMouseStatus, parameters, geneUrl, projectBeans);
		geneRows=rows.values().stream()
				.collect(Collectors.toList());
		Collections.sort(geneRows);
		return geneRows;
	}


	@Cacheable("topLevelPhenotypesXAxis")
	public List<BasicBean> getXAxisForHeatMap() throws IOException, SolrServerException {

		List<BasicBean> mp = new ArrayList<>();
		Set<BasicBean> topLevelPhenotypes = mpService.getAllTopLevelPhenotypesAsBasicBeans();
		mp.addAll(topLevelPhenotypes);

		return mp;
	}


}
