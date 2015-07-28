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
package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * Wrapper around the pipeline core.
 *
 * @author tudose
 *
 */

@Service
public class ImpressService {

	@Value("${drupalBaseUrl}")
	public String DRUPAL_BASE_URL;

	@Autowired
	@Qualifier("pipelineCore")
	private HttpSolrServer solr;

	
	
	/**
	 * @since 2015/07/17
	 * @author tudose
	 * @param procedureStableIdRegex
	 * @return 
	 */
	
	public List<ProcedureDTO> getProceduresByPipeline(String pipelineStableId){
		
		List<ProcedureDTO> procedures = new ArrayList<>();
		
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
				.addField(ImpressDTO.PROCEDURE_ID)
				.addField(ImpressDTO.PROCEDURE_NAME)
				.addField(ImpressDTO.PROCEDURE_STABLE_ID)
				.addField(ImpressDTO.PROCEDURE_STABLE_KEY);
			query.set("group", true);
			query.set("group.field", ImpressDTO.PROCEDURE_STABLE_ID);
			query.setRows(10000);
			query.set("group.limit", 1);

			System.out.println("URL for getProceduresByStableIdRegex " + solr.getBaseURL() + "/select?" + query);
			
			QueryResponse response = solr.query(query);
			
			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){
				ProcedureDTO procedure = new ProcedureDTO(Integer.getInteger(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_ID).toString()),
						Integer.getInteger(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_KEY).toString()), 
						group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_ID).toString(), 
						group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_NAME).toString());
				procedures.add(procedure);
			}

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return procedures;
	}
	

	/**
	 * @date 2015/07/08
	 * @author tudose
	 * @return List of procedures in a pipeline
	 */
	public List<ProcedureDTO> getProcedures(String pipelineStableId){
		
		List<ProcedureDTO> procedures = new ArrayList<>();
		
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.addField(ImpressDTO.PROCEDURE_ID)
				.addField(ImpressDTO.PROCEDURE_NAME)
				.addField(ImpressDTO.PROCEDURE_STABLE_ID)
				.addField(ImpressDTO.PROCEDURE_STABLE_KEY)
				.addField(ImpressDTO.PARAMETER_ID)
				.addField(ImpressDTO.PARAMETER_NAME)
				.addField(ImpressDTO.PARAMETER_STABLE_ID)
				.addField(ImpressDTO.PARAMETER_STABLE_KEY);
			query.set("group", true);
			query.set("group.field", ImpressDTO.PROCEDURE_STABLE_ID);
			query.setRows(10000);
			query.set("group.limit", 10000);

			QueryResponse response = solr.query(query);
			
			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){
				
				ProcedureDTO procedure = new ProcedureDTO();
				procedure.setId(Integer.getInteger(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_ID).toString()));
				procedure.setName(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_NAME).toString());
				procedure.setStableId(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_ID).toString());
				procedure.setStableKey(	Integer.getInteger(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_KEY).toString()));
				
				for (SolrDocument doc : group.getResult()){
					ImpressBaseDTO parameter = new ImpressBaseDTO((Integer)doc.getFirstValue(ImpressDTO.PARAMETER_ID), 
							Integer.getInteger(doc.getFirstValue(ImpressDTO.PARAMETER_STABLE_KEY).toString()),
							doc.getFirstValue(ImpressDTO.PARAMETER_STABLE_ID).toString(), 
							doc.getFirstValue(ImpressDTO.PARAMETER_NAME).toString());
					procedure.addParameter(parameter);
				}
			}

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return procedures;
	}
	
	
	/**
	 * @date 2015/07/08
	 * @author tudose
	 * @param pipelineStableId
	 * @return Pipeline in an object of type ImpressBean
	 * @throws SolrServerException
	 */	
	public ImpressBaseDTO getPipeline(String pipelineStableId) 
	throws SolrServerException{
		
		SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.addField(ImpressDTO.PIPELINE_STABLE_ID)
				.addField(ImpressDTO.PIPELINE_STABLE_KEY)
				.addField(ImpressDTO.PIPELINE_NAME)
				.addField(ImpressDTO.PIPELINE_ID)
				.setRows(1);
		SolrDocument doc = solr.query(query).getResults().get(0);
		
		return new ImpressBaseDTO((Integer)doc.getFirstValue(ImpressDTO.PIPELINE_ID), 
				Integer.getInteger(doc.getFirstValue(ImpressDTO.PIPELINE_STABLE_KEY).toString()), 
				doc.getFirstValue(ImpressDTO.PIPELINE_STABLE_ID).toString(), 
				doc.getFirstValue(ImpressDTO.PIPELINE_NAME).toString());
	
	}
	


	public Integer getProcedureStableKey(String procedureStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":\"" + procedureStableId + "\"")
				.setFields(ImpressDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			return response.getBeans(ImpressDTO.class).get(0).getProcedureStableKey();

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public Integer getPipelineStableKey(String pipelineStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.setFields(ImpressDTO.PIPELINE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			return response.getBeans(ImpressDTO.class).get(0).getPipelineStableKey();

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public String getProcedureUrlByKey(String procedureStableKey) {

		return DRUPAL_BASE_URL + "/impress/impress/displaySOP/" + procedureStableKey;
	}


	/**
	 * Return a string that either contains the name of the procedure if the
	 * procedure key cannot be found, or a string that has an HTML anchor tag
	 * ready to be used in a chart.
	 *
	 * @param procedureName
	 *            the name of the procedure
	 * @param procedureStableId
	 *            the IMPReSS stable ID of the procedure
	 * @return a string that either has the name of the procedure or and HTML
	 *         anchor tag to be used by the chart
	 */
	/* Temp comment out as pipeline core does not contian all procedures.
	public String getAnchorForProcedure(String procedureName, String procedureStableId) {

		String anchor = procedureName;
		String procKey = getProcedureStableKey(procedureStableId).toString();
		if (procKey != null) {
			anchor = String.format("<a href=\"%s\">%s</a>", getProcedureUrlByKey(procKey), procedureName);
		}

		return anchor;
	}

*/
	public String getPipelineUrlByStableId(String stableId){
		Integer pipelineKey = getPipelineStableKey(stableId);
		if (pipelineKey != null ){
			return DRUPAL_BASE_URL + "/impress/procedures/" + pipelineKey;
		}
		else return "#";
	}


	public Map<String,OntologyBean> getParameterStableIdToAbnormalMaMap(){
	
		Map<String,OntologyBean> idToAbnormalMaId=new HashMap<>();
		List<ImpressDTO> pipelineDtos=null;
		SolrQuery query = new SolrQuery()
			.setQuery(ImpressDTO.MA_ID + ":*" )
			.setFields(ImpressDTO.MA_ID, ImpressDTO.MA_TERM, ImpressDTO.PARAMETER_STABLE_ID).setRows(1000000);
		QueryResponse response=null;
		
		try {
			response = solr.query(query);
			pipelineDtos = response.getBeans(ImpressDTO.class);
			for(ImpressDTO pipe:pipelineDtos){
				if(!idToAbnormalMaId.containsKey(pipe.getParameterStableId())){
					idToAbnormalMaId.put(pipe.getParameterStableId(),new OntologyBean(pipe.getMaTermId(),pipe.getMaName()));
				}
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	
		return idToAbnormalMaId;
	}
	

	public class OntologyBean{

		public OntologyBean(String id, String name){
			this.maId=id;
			this.name=name;
		}

		String maId;
		public String getMaId() {
			return maId;
		}
		public void setMaId(String maId) {
			this.maId = maId;
		}
		String name;
		public String getName() {
			return name;
		}
		public void setName(String maName) {
			this.name = maName;
		}
	}
}
