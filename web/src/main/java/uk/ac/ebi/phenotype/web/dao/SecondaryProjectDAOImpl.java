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
import org.mousephenotype.cda.db.dao.GenomicFeatureDAO;
import org.mousephenotype.cda.db.dao.SecondaryProjectDAO;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.web.controller.GeneHeatmapController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jwarren
 */
@Service("idg")
class SecondaryProjectServiceIdg implements SecondaryProjectService {

	@Autowired
	private GeneService geneService;

	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	@Qualifier("postqcService")
	private PostQcService genotypePhenotypeService;

	@Autowired
	private MpService mpService;

	@Autowired
	private SecondaryProjectDAO secondaryProjectDAO;


	@Override
	public Set<String> getAccessionsBySecondaryProjectId(String projectId) throws SQLException {

		return secondaryProjectDAO.getAccessionsBySecondaryProjectId(projectId);
	}


	@Override
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request) throws SolrServerException {
		List<GeneRowForHeatMap> geneRows = new ArrayList<>();
		List<BasicBean> parameters = this.getXAxisForHeatMap();

		try {
			System.out.println("getGeneHeatMap called");
			// get a list of genes for the project - which will be the row
			// headers
			Set<String> accessions = secondaryProjectDAO.getAccessionsBySecondaryProjectId("idg");
			String url = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl");
			Map<String, String> geneToMouseStatusMap = geneService.getProductionStatusForGeneSet(accessions, url);
			Map<String, List<String>> geneToTopLevelMpMap = geneService.getTopLevelMpForGeneSet(accessions);
			// for(String key: geneToMouseStatusMap.keySet()){
			// System.out.println("key="+key+"  value="+geneToMouseStatusMap.get(key));
			// }
			for (String accession : accessions) {
				// System.out.println("accession="+accession);
				GenomicFeature gene = genesDao.getGenomicFeatureByAccession(accession);
				// get a data structure with the gene accession,with parameter
				// associated with a Value or status ie. not phenotyped, not
				// significant
				GeneRowForHeatMap row = genotypePhenotypeService.getResultsForGeneHeatMap(accession, gene, parameters, geneToTopLevelMpMap);
				if (geneToMouseStatusMap.containsKey(accession)) {
					row.setMiceProduced(geneToMouseStatusMap.get(accession));
					//System.out.println("Mice produced=|"+row.getMiceProduced()+"|");
					if (row.getMiceProduced().equals("Neither production nor phenotyping status available ")) {//note the space on the end - why we should have enums
						for (HeatMapCell cell : row.getXAxisToCellMap().values()) {
							// set all the cells to No Data Available
							cell.setStatus("No Data Available");
						}
					}
				} else {
					row.setMiceProduced("No");// if not contained in map just
					// set no to mice produced
				}
				geneRows.add(row);
			}
			// model.addAttribute("heatmapCode", fillHeatmap(hdto));

		} catch (SQLException | IOException ex) {
			Logger.getLogger(GeneHeatmapController.class.getName()).log(Level.SEVERE, null, ex);
		}
		Collections.sort(geneRows);
		return geneRows;
	}


	@Override
	public List<BasicBean> getXAxisForHeatMap() {
		List<BasicBean> mp = new ArrayList<>();
		try {
			Set<BasicBean> topLevelPhenotypes = mpService.getAllTopLevelPhenotypesAsBasicBeans();
			mp.addAll(topLevelPhenotypes);
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mp;
	}
}
