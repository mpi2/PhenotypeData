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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.solr.web.dto.ParallelCoordinatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ParallelCoordinatesController {

	@Autowired
	ObservationService os;

	@Autowired
	StatisticalResultService srs;
	

	@Autowired
	ImpressService impressService;
	
	@RequestMapping(value="/parallel", method=RequestMethod.GET)
	public String getData(	Model model,	HttpServletRequest request,	RedirectAttributes attributes)
	throws SolrServerException{

		TreeSet<ImpressBaseDTO> procedures = new TreeSet<>(ImpressBaseDTO.getComparatorByName());
		procedures.addAll(srs.getProcedures(null, "unidimensional", "IMPC", 2, ParallelCoordinatesDTO.procedureNoDisplay, "Success"));
		

		TreeSet<String> centers = new TreeSet<>();
		centers.addAll(srs.getCenters(null, "unidimensional", "IMPC", "Success"));

		model.addAttribute("procedures", procedures);
		model.addAttribute("centers", centers);
		
		return "parallel2";
		
	}

	
	@RequestMapping(value="/parallelFrag", method=RequestMethod.GET)
	public String getGraph(	@RequestParam(required = false, value = "procedure_id") List<String> procedureIds, 	@RequestParam(required = false, value = "phenotyping_center") List<String> phenotypingCenter, Model model,	HttpServletRequest request,	RedirectAttributes attributes)
	throws SolrServerException{

		long time = System.currentTimeMillis();
		if (procedureIds == null){
			
			model.addAttribute("procedure", "");
			model.addAttribute("dataJs", getJsonForParallelCoordinates(null, null) + ";");	
			
		} else {
			
			String mappedHostname = (String)request.getAttribute("mappedHostname") + (String)request.getAttribute("baseUrl");
			List<ParameterDTO> parameters = impressService.getParametersByProcedure(procedureIds, "unidimensional");
			String data = getJsonForParallelCoordinates(srs.getGenotypeEffectFor(procedureIds, phenotypingCenter, false, mappedHostname), parameters);
			String procedures = "{" ;
			for (int i = 0;  i < procedureIds.size(); i++){
				String p = procedureIds.get(i);
				ProcedureDTO proc = impressService.getProcedureByStableId(p + "*");
				procedures += (i != 0) ? "," :"";
				procedures += "\"" + proc.getName() + "\":\"" + ImpressService.getProcedureUrl(proc.getStableKey())  + "\"";
			}
			procedures += "}";
			
			model.addAttribute("dataJs", data + ";");
			model.addAttribute("selectedProcedures", procedures);
			model.addAttribute("phenotypingCenter", StringUtils.join(phenotypingCenter, ", "));
			
		}
		
		System.out.println("Generating data for parallel coordinates took " + (System.currentTimeMillis() - time) + " ms.");

		return "parallelFrag";
	}
	

	/**
	 * @author tudose
	 * @since 2015/08/04
	 * @param rows
	 * @return Parsed rows into the json format needed for the parallel coordinates
	 */
	protected String getJsonForParallelCoordinates(Map<String, ParallelCoordinatesDTO> rows, List<ParameterDTO> parameters){
		
		String data = "[";
		String defaultMeans = "";
		String res = "var foods = []; \nvar defaults = {};" ;
		
		if (rows != null){
			int i = 0;
	    	for (String key: rows.keySet()){
	    		ParallelCoordinatesDTO bean = rows.get(key);
	    		if (key == null || !key.equalsIgnoreCase(ParallelCoordinatesDTO.DEFAULT)){
		    		i++;
		    		String currentRow = bean.toString(false);
		    		if (!currentRow.equals("")){
			    		data += "{" + currentRow + "}";
			    		if (i < rows.values().size()){
			    			data += ", ";
			    		}
		    		}
	    		}
	    		else {
	    			String currentRow = bean.toString(false);
	    			defaultMeans += "{" + currentRow + "}\n";
	    			data += "{" + currentRow + "}";
		    		if (i < rows.values().size()){
		    			data += ", ";
		    		}
	    		}
	    	}
	    	data +=  "]";

	    	res = "var foods = " + data.toString() + "; \n\n var defaults = " + defaultMeans +";";
	    	
	    	if (parameters != null){
	    		res += "var links = {";
	    		String groups = "var groups = {";
	    		Set<String> parameterNames =  new HashSet<>();
	    		for (ParameterDTO p : parameters){
	    			if (!parameterNames.contains(p.getName())){
	    				parameterNames.add(p.getName());
	    				res += "\"" + p.getName() + "\":\"" + ImpressService.getParameterUrl(p.getStableKey()) + "\", ";
	    				for (String procedure : p.getProcedureNames()){
	    					groups += "\"" + p.getName() + "\":\"" + procedure  + "\", ";
	    				}
	    			}
	    		}
	    		groups += "};";
	    		res += "};";
	    		res += groups;
	    	}
	    	
		} 
		return res;
	}
}
