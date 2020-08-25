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

package uk.ac.ebi.phenotype.web.dao;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.db.repositories.GenesSecondaryProjectRepository;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
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
public class GenesSecondaryProjectServiceIdg implements GenesSecondaryProjectService {

	public static final String DEVIANCE_COLOR      = "rgb(191, 75, 50)";
	public static final String COULD_NOT_ANALYSE   = "rgb(230, 242, 246)";
	public static final String NO_SIGNIFICANT_CALL = "rgb(247, 157, 70)";
	public static final String NO_DATA             = "rgb(119, 119, 119)";


	private GenesSecondaryProjectRepository genesSecondaryProjectRepository;
	private GeneService                     geneService;
	private MpService                       mpService;
	private StatisticalResultService        statisticalResultService;

	@Inject
	public GenesSecondaryProjectServiceIdg(
			@NotNull GenesSecondaryProjectRepository genesSecondaryProjectRepository,
			@NotNull GeneService geneService,
			@NotNull MpService mpService,
			@NotNull StatisticalResultService statisticalResultService)
	{
		this.genesSecondaryProjectRepository = genesSecondaryProjectRepository;
		this.geneService = geneService;
		this.mpService = mpService;
		this.statisticalResultService = statisticalResultService;
	}

	@Override
	public Set<GenesSecondaryProject> getAccessionsBySecondaryProjectId(String projectId) throws SQLException {

		return genesSecondaryProjectRepository.getAllBySecondaryProjectId(projectId);
	}


	@Override
	@Cacheable("topLevelPhenotypesGeneRows")
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request) throws SolrServerException, IOException, SQLException {

		List<GeneRowForHeatMap> geneRows = new ArrayList<>();
		List<BasicBean> parameters = this.getXAxisForHeatMap();
		String geneUrl =  request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();

		// get a list of mgi geneAccessionIds for the project - which will be the row headers
		Set<GenesSecondaryProject> projectBeans = genesSecondaryProjectRepository.getAllBySecondaryProjectId("idg");

		Set<String>accessions = projectBeans
				   .stream()
				.map(GenesSecondaryProject::getMgiGeneAccessionId)
				.collect(Collectors.toSet());


		Map<String, String> accessionToGroupLabelMap = projectBeans
				.stream()
				.collect(Collectors.toMap(GenesSecondaryProject::getMgiGeneAccessionId, GenesSecondaryProject::getGroupLabel));

		List<SolrDocument> geneToMouseStatus = geneService.getProductionStatusForGeneSet(accessions, null);
		Map<String, GeneRowForHeatMap> rows = statisticalResultService.getSecondaryProjectMapForGeneList(accessions, parameters);

		for (SolrDocument doc : geneToMouseStatus) {

			// get a data structure with the gene accession, parameter associated with a value or status ie. not phenotyped, not significant
			String accession = doc.get(GeneDTO.MGI_ACCESSION_ID).toString();
			GeneRowForHeatMap row = rows.containsKey(accession) ? rows.get(accession) : new GeneRowForHeatMap(accession, doc.get(GeneDTO.MARKER_SYMBOL).toString() , parameters);
			row.setHumanSymbol((ArrayList<String>)doc.get(GeneDTO.HUMAN_GENE_SYMBOL));
			// Mouse production status
			Map<String, String> prod =  GeneService.getStatusFromDoc(doc, geneUrl);
			String prodStatusIcons = prod.get("productionIcons") + prod.get("phenotypingIcons");
			prodStatusIcons = prodStatusIcons.equals("") ? "No" : prodStatusIcons;
			row.setMiceProduced(prodStatusIcons);
			if (row.getMiceProduced().equals("Neither production nor phenotyping status available ")) {//note the space on the end - why we should have enums
				for (HeatMapCell cell : row.getXAxisToCellMap().values()) {
					cell.addStatus(HeatMapCell.THREE_I_NO_DATA); // set all the cells to No Data Available
				}
			}
			if(accessionToGroupLabelMap.containsKey(accession)){
				row.setGroupLabel(accessionToGroupLabelMap.get(accession));
			}
			geneRows.add(row);
		}

		Collections.sort(geneRows);

		return geneRows;
	}

	@Override
	public List<BasicBean> getXAxisForHeatMap() throws IOException, SolrServerException {

		List<BasicBean> mp = new ArrayList<>();
		Set<BasicBean> topLevelPhenotypes = mpService.getAllTopLevelPhenotypesAsBasicBeans();
		mp.addAll(topLevelPhenotypes);

		return mp;
	}
}