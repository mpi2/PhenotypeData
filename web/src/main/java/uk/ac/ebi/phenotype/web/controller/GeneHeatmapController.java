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
package uk.ac.ebi.phenotype.web.controller;


import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.web.dao.GenesSecondaryProject3IImpl;
import uk.ac.ebi.phenotype.web.dao.GenesSecondaryProjectService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;



@Controller
public class GeneHeatmapController {

    @Autowired
    @Qualifier("idg")
	private GenesSecondaryProjectService idgGenesSecondaryProjectService;

    @Autowired
    @Qualifier("threeI")
	private GenesSecondaryProject3IImpl threeISecondaryProjectDAO;

    @Autowired
    StatisticalResultService srService;


	/**
         *
         * @param project is the external project for example a secondary screen e.g. IDG or 3I
         * @param model
         * @param request
         * @return
	 * @throws SolrServerException, IOException
         */
	@RequestMapping("/geneHeatMap")
	@Cacheable("geneHeatMapCache")
	public String getHeatmapJS(@RequestParam(value = "project") String project,
                Model model,
                HttpServletRequest request) throws SolrServerException, IOException, SQLException {

		if (project.equalsIgnoreCase("idg")) {

			Long                         time                         = System.currentTimeMillis();
			GenesSecondaryProjectService genesSecondaryProjectService = this.getSecondaryProjectDao(project);
			List<GeneRowForHeatMap>      geneRows                     = genesSecondaryProjectService.getGeneRowsForHeatMap(request);
			List<BasicBean>              xAxisBeans                   = genesSecondaryProjectService.getXAxisForHeatMap();
		    model.addAttribute("geneRows", geneRows);
		    model.addAttribute("xAxisBeans", xAxisBeans);
			System.out.println("HeatMap: Getting the data took " + (System.currentTimeMillis() - time) + "ms");
			
		}
        return "geneHeatMap";
	}


	@RequestMapping("/threeIMap")
	@Cacheable("geneHeatMapCache")
	public String getThreeIMap(Model model, HttpServletRequest request)
			throws SolrServerException, IOException, SQLException {

		System.out.println("calling heatmap controller method for 3i");
		String                       project                      ="threeI";
		Long                         time                         = System.currentTimeMillis();
	    List<BasicBean>              xAxisBeans                   = srService.getProceduresForDataSource("3i"); //procedures
		GenesSecondaryProjectService genesSecondaryProjectService = this.getSecondaryProjectDao(project);
		List<GeneRowForHeatMap>      geneRows                     = genesSecondaryProjectService.getGeneRowsForHeatMap(request);
	    model.addAttribute("geneRows", geneRows);
	    model.addAttribute("xAxisBeans", xAxisBeans);
		System.out.println("HeatMap: Getting the data took " + (System.currentTimeMillis() - time) + "ms");
	    return "threeIMap";
	}

	private GenesSecondaryProjectService getSecondaryProjectDao(String project) {
		if(project.equalsIgnoreCase(GenesSecondaryProjectService.SecondaryProjectIds.IDG.name())){
			return idgGenesSecondaryProjectService;
		}
		if(project.equalsIgnoreCase(GenesSecondaryProjectService.SecondaryProjectIds.threeI.name())){
			return threeISecondaryProjectDAO;
		}
		return null;
	}
	



}
