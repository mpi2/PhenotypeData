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
import org.mousephenotype.cda.solr.bean.ImpressBean;
import org.mousephenotype.cda.solr.bean.ProcedureBean;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
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
	
	public List<ProcedureBean> getProceduresByStableIdRegex(String procedureStableIdRegex){
		
		List<ProcedureBean> procedures = new ArrayList<>();
		
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PROCEDURE_STABLE_ID + ":" + procedureStableIdRegex)
				.addField(PipelineDTO.PROCEDURE_ID)
				.addField(PipelineDTO.PROCEDURE_NAME)
				.addField(PipelineDTO.PROCEDURE_STABLE_ID)
				.addField(PipelineDTO.PROCEDURE_STABLE_KEY);
			query.set("group", true);
			query.set("group.field", PipelineDTO.PROCEDURE_STABLE_ID);
			query.setRows(10000);
			query.set("group.limit", 1);

			System.out.println("URL for getProceduresByStableIdRegex " + solr.getBaseURL() + "/select?" + query);
			
			QueryResponse response = solr.query(query);
			
			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){
				ProcedureBean procedure = new ProcedureBean(group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_ID).toString(), 
															group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_NAME).toString(),
															group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_STABLE_ID).toString(),
															group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_STABLE_KEY).toString());
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
	public List<ProcedureBean> getProcedures(String pipelineStableId){
		
		List<ProcedureBean> procedures = new ArrayList<>();
		
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.addField(PipelineDTO.PROCEDURE_ID)
				.addField(PipelineDTO.PROCEDURE_NAME)
				.addField(PipelineDTO.PROCEDURE_STABLE_ID)
				.addField(PipelineDTO.PROCEDURE_STABLE_KEY)
				.addField(PipelineDTO.PARAMETER_ID)
				.addField(PipelineDTO.PARAMETER_NAME)
				.addField(PipelineDTO.PARAMETER_STABLE_ID)
				.addField(PipelineDTO.PARAMETER_STABLE_KEY);
			query.set("group", true);
			query.set("group.field", PipelineDTO.PROCEDURE_STABLE_ID);
			query.setRows(10000);
			query.set("group.limit", 10000);

			QueryResponse response = solr.query(query);
			
			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){
				ProcedureBean procedure = new ProcedureBean(group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_ID).toString(), 
															group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_NAME).toString(),
															group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_STABLE_ID).toString(),
															group.getResult().get(0).getFirstValue(PipelineDTO.PROCEDURE_STABLE_KEY).toString());
				for (SolrDocument doc : group.getResult()){
					ImpressBean parameter = new ImpressBean((Integer)doc.getFirstValue(PipelineDTO.PARAMETER_ID), doc.getFirstValue(PipelineDTO.PARAMETER_STABLE_KEY).toString(),
															doc.getFirstValue(PipelineDTO.PARAMETER_STABLE_ID).toString(), doc.getFirstValue(PipelineDTO.PARAMETER_NAME).toString());
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
	public ImpressBean getPipeline(String pipelineStableId) 
	throws SolrServerException{
		
		SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.addField(PipelineDTO.PIPELINE_STABLE_ID)
				.addField(PipelineDTO.PIPELINE_STABLE_KEY)
				.addField(PipelineDTO.PIPELINE_NAME)
				.addField(PipelineDTO.PIPELINE_ID)
				.setRows(1);
		SolrDocument doc = solr.query(query).getResults().get(0);
		
		return new ImpressBean((Integer)doc.getFirstValue(PipelineDTO.PIPELINE_ID), doc.getFirstValue(PipelineDTO.PIPELINE_STABLE_KEY).toString(), doc.getFirstValue(PipelineDTO.PIPELINE_STABLE_ID).toString(), doc.getFirstValue(PipelineDTO.PIPELINE_NAME).toString());
	
	}
	


	public Integer getProcedureStableKey(String procedureStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PROCEDURE_STABLE_ID + ":\"" + procedureStableId + "\"")
				.setFields(PipelineDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = solr.query(query);
//System.out.println("impress in getprocedureStablekey response ="+response);
			return response.getBeans(PipelineDTO.class).get(0).getProcedureStableKey();

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public Integer getPipelineStableKey(String pipelineStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.setFields(PipelineDTO.PIPELINE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			return response.getBeans(PipelineDTO.class).get(0).getPipelineStableKey();

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
		List<PipelineDTO> pipelineDtos=null;
		SolrQuery query = new SolrQuery()
			.setQuery(PipelineDTO.ABNORMAL_MA_ID + ":*" )
			.setFields(PipelineDTO.ABNORMAL_MA_ID, PipelineDTO.ABNORMAL_MA_NAME, PipelineDTO.PARAMETER_STABLE_ID).setRows(1000000);
		QueryResponse response=null;
		
		try {
			response = solr.query(query);
			pipelineDtos = response.getBeans(PipelineDTO.class);
			for(PipelineDTO pipe:pipelineDtos){
				if(!idToAbnormalMaId.containsKey(pipe.getParameterStableId())){
					idToAbnormalMaId.put(pipe.getParameterStableId(),new OntologyBean(pipe.getAbnormalMaTermId(),pipe.getAbnormalMaName()));
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
