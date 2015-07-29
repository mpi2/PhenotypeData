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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ParallelCoordinatesController {

	@Autowired
	ObservationService os;

	@Autowired
	StatisticalResultService srs;
	

	@Autowired
	ImpressService impressService;

	@Autowired
	PhenotypePipelineDAO pp;

	@RequestMapping(value="/parallel", method=RequestMethod.GET)
	public String getData(	Model model,	HttpServletRequest request,	RedirectAttributes attributes)
	throws SolrServerException{

		List<ImpressBaseDTO> procedures = os.getProceduresByPipeline(null, "unidimensional", "IMPC", 2);
		model.addAttribute("procedures", procedures);
		
		return "parallel2";
		
	}

	
	@RequestMapping(value="/parallelFrag", method=RequestMethod.GET)
	public String getGraph(	@RequestParam(required = false, value = "procedure_id") List<String> procedureIds, Model model,	HttpServletRequest request,	RedirectAttributes attributes)
	throws SolrServerException{

		if (procedureIds == null){
			model.addAttribute("procedure", "");			
		}
		else {
			String data = srs.getGenotypeEffectFor(procedureIds , false);
			model.addAttribute("dataJs", data + ";");
			String title = "";
			for (int i = 0;  i < procedureIds.size()-1; i++){
				String p = procedureIds.get(i);
				title += pp.getProcedureByMatchingStableId(p + "%").get(0).getName() + ", ";
			}
			title += pp.getProcedureByMatchingStableId(procedureIds.get(procedureIds.size()-1) + "%").get(0).getName();
			
			model.addAttribute("procedure", title);
		}

		return "parallelFrag";
	}
}
