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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.solr.web.dto.ParallelCoordinatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class ParallelCoordinatesController {

	@Autowired
	ObservationService os;

	@Autowired
	StatisticalResultService srs;

	@Autowired
	ImpressService impressService;

	private static final Integer MAX_ENTRIES = 50;

	@SuppressWarnings("unchecked") // synchronized map so it's thread safe
	Map<String, String> cache = (Map<String, String>) Collections.synchronizedMap(new LinkedHashMap<String, String>(MAX_ENTRIES+1, .75F, true) {

        private static final long serialVersionUID = 1L;

		// This method is called just after a new entry has been added
		public boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_ENTRIES;
		}
	});


	@RequestMapping(value = "/parallel", method = RequestMethod.GET)
	public String getData(Model model, HttpServletRequest request, RedirectAttributes attributes) {
        System.out.println("Parallel");

        try {
            TreeSet<ImpressBaseDTO> procedures = new TreeSet<>(ImpressBaseDTO.getComparatorByName());
            procedures.addAll(srs.getProcedures(null, "unidimensional", "IMPC", 2, ParallelCoordinatesDTO.procedureNoDisplay, "Successful", false));

            TreeSet<String> centers = new TreeSet<>();
            centers.addAll(srs.getCenters(null, "unidimensional", "IMPC", "Successful"));

            model.addAttribute("procedures", procedures);
            model.addAttribute("centers", centers);
        } catch (Exception e) {
            e.printStackTrace();
        }

		return "parallel";

	}


	@RequestMapping(value = "/parallelFrag", method = RequestMethod.GET)
	@CrossOrigin(origins = "*", maxAge = 3600)
	public String getGraph(	@RequestParam(required = false, value = "procedure_id") List<String> procedureIds,
							@RequestParam(required = false, value = "phenotyping_center") List<String> phenotypingCenter,
							@RequestParam(required = false, value = "gene_acc_list") String genes,
							@RequestParam(required = false, value = "top_level_mp_id") String topLevelMpId,
						   	Model model, HttpServletRequest request) {

		try { // keep try-catch instead of throws as Spring hides [some] pf the exceptions.
			List<String> geneList = (genes != null && !genes.contains(".")) ? Arrays.stream(genes.split("[ ,\t\n]+")).map(item -> {return item.trim();}).collect(Collectors.toList()) : null;
			long totalTime = System.currentTimeMillis();
			if (procedureIds == null && topLevelMpId == null) {
				model.addAttribute("procedure", "");
				model.addAttribute("dataJs", getJsonForParallelCoordinates(null, null) + ";");
			} else {

				String procedures;
				if (procedureIds == null) {
					procedures = "{}";
				} else {
					procedures = "{";
					for (int i = 0; i < procedureIds.size(); i++) {
						String p = procedureIds.get(i);
						ProcedureDTO proc = impressService.getProcedureByStableId(p + "*");
						procedures += (i != 0) ? "," : "";
						procedures += "\"" + proc.getName() + "\":\"" + ImpressService.getProcedureUrl(proc.getStableKey()) + "\"";
					}
					procedures += "}";
				}
				model.addAttribute("dataJs", getData(procedureIds, phenotypingCenter, geneList, topLevelMpId, request) + ";");
				model.addAttribute("selectedProcedures", procedures);
				model.addAttribute("phenotypingCenter", StringUtils.join(phenotypingCenter, ", "));
			}

			System.out.println("Generating data for parallel coordinates took " + (System.currentTimeMillis() - totalTime) + " ms.");

		} catch ( Exception e){
			e.printStackTrace();
		}

		return "parallelFrag";
	}



	@RequestMapping(value = "/parallel/cache", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> clearCache(
			@RequestParam(value = "clearCache", required = false) Boolean clearCache) throws JSONException {

		JSONObject jsonResponse = new JSONObject();

		if (clearCache != null && clearCache == true) {
			jsonResponse.put("Details", cache.keySet().size() + " cleared from cache");
			cache.clear();
		} else {
			jsonResponse.put("Details", cache.keySet().size() + " entries in cache");
			jsonResponse.put("Cached Keys", cache.keySet());
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<JSONObject>(jsonResponse,responseHeaders, HttpStatus.CREATED);
	}


	private String getData(List<String> procedureIds, List<String> phenotypingCenter, List<String> genes, String topLevelMpId,
	HttpServletRequest request) throws IOException, SolrServerException, URISyntaxException {

		String key =  procedureIds != null ? procedureIds.toString() : "" ;
		key += phenotypingCenter != null ? phenotypingCenter.toString() : "";
		key += genes != null ? genes.hashCode() : "";
		key += topLevelMpId != null ? topLevelMpId : "";
		if (!cache.containsKey(key)){
			String mappedHostname = (String) request.getAttribute("mappedHostname") + (String) request.getAttribute("baseUrl");
			List<ParameterDTO> parameters = impressService.getParameters(procedureIds, "unidimensional", topLevelMpId);
			String data = getJsonForParallelCoordinates(srs.getGenotypeEffectFor(procedureIds, phenotypingCenter, false, genes, topLevelMpId), parameters);
			cache.put(key, data);

		}
		return cache.get(key);
	}



	/**
	 * @author tudose
	 * @since 2015/08/04
	 * @param rows
	 * @return Parsed rows into the json format needed for the parallel coordinates
	 */
	protected String getJsonForParallelCoordinates(Map<String, ParallelCoordinatesDTO> rows, List<ParameterDTO> parameters){

		StringBuffer data = new StringBuffer();
		data.append("[");
		String defaultMeans = "";
		StringBuffer res = new StringBuffer();
		res.append("var foods = []; \nvar defaults = {};");
		if (rows != null){
			int i = 0;
			for (String key: rows.keySet()){
	    		ParallelCoordinatesDTO bean = rows.get(key);
	    		if (key == null || !key.equalsIgnoreCase(ParallelCoordinatesDTO.DEFAULT)){
		    		i++;
		    		String currentRow = bean.toString(false);
		    		if (!currentRow.equals("")){
		    			data.append("{").append(currentRow).append("}");
			    		if (i < rows.values().size()){
			    			data.append(", ");
			    		}
		    		}
	    		}
	    		else {
	    			String currentRow = bean.toString(false);
	    			defaultMeans += "{" + currentRow + "}\n";
	    			data.append("{").append(currentRow).append("}");
		    		if (i < rows.values().size()){
		    			data.append(", ");
		    		}
	    		}
	    	}
	    	data.append("]");

	    	res.append("var foods = ").append(data).append("; \n\n var defaults = ").append(defaultMeans).append(";");

	    	if (parameters != null){
	    		res.append("var links = {");
	    		String groups = "var groups = {";
	    		Set<String> parameterNames =  new HashSet<>();
	    		for (ParameterDTO p : parameters){
					if (!parameterNames.contains(p.getName())){
	    				parameterNames.add(p.getName());
	    				res.append("\"").append(p.getName()).append("\":\"").append(ImpressService.getParameterUrl(p.getStableKey())).append("\", ");
	    				for (String procedure : p.getProcedureNames()){
	    					if (! procedure.equals("No effect")) {
								groups += "\"" + p.getName() + "\":\"" + procedure + "\", ";
							}
	    				}
	    			}
	    		}
	    		groups += "};";
	    		res.append("};");
	    		res.append(groups);
	    	}

		}

		return res.toString();
	}
}