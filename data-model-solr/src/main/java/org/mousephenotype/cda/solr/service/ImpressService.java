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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


/**
 * Wrapper around the pipeline core.
 *
 * @author tudose
 *
 */

@Service
public class ImpressService extends BasicService implements WebStatus {

	@Value("${drupalBaseUrl}")
	public String DRUPAL_BASE_URL;

	@Autowired
	@Qualifier("pipelineCore")
	private HttpSolrClient solr;



	public ImpressService(String solr) {
		super();
		this.solr = new HttpSolrClient(solr);
	}



	public ImpressService() {
		super();
	}



	/**
	 * @since 2015/07/17
	 * @author tudose
	 * @return
	 */

	public List<ProcedureDTO> getProceduresByPipeline(String pipelineStableId) {

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

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return procedures;
	}



	public Set<ImpressDTO> getProceduresByMpTerm(String mpTermId) throws IOException {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.MP_ID + ":\"" + mpTermId +"\"")
				.addField(ImpressDTO.PROCEDURE_ID)
				.addField(ImpressDTO.PROCEDURE_NAME)
				.addField(ImpressDTO.PROCEDURE_STABLE_ID)
				.addField(ImpressDTO.PROCEDURE_STABLE_KEY);
			// Adding another field in here such as pipeline will require some code changes to avoid multiple procedures on mp page.
			// As it is now (only mp and procedure fields) the equal method will ignore the non existing pipeline id field.

			query.setRows(1000000);

			QueryResponse response = solr.query(query);

			HashMap<Integer, ImpressDTO> res = new HashMap<>();
			for (ImpressDTO bean: response.getBeans(ImpressDTO.class)){
				res.put(bean.hashCode(), bean);
			}

			return new HashSet<>(res.values());

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @author tudose
	 * @since 2015/09/25
	 * @return List< [(Procedure, parameter, observationNumber)]>
	 */
	public List<String[]> getProcedureParameterList(){

		List<String[]> result = new ArrayList<>();
		SolrQuery q = new SolrQuery();

    	q.setQuery("*:*");
        q.setFacet(true);
        q.setFacetLimit(-1);
        q.setRows(0);

        String pivotFacet =  ImpressDTO.PROCEDURE_STABLE_ID  + "," + ImpressDTO.PARAMETER_STABLE_ID;
		q.set("facet.pivot", pivotFacet);

        try {
        	QueryResponse res = solr.query(q);

        	for( PivotField pivot : res.getFacetPivot().get(pivotFacet)){
    			for (PivotField parameter : pivot.getPivot()){
    				String[] row = {pivot.getValue().toString(), parameter.getValue().toString()};
    				result.add(row);
    			}
    		}

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return result;
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
					ParameterDTO parameter = new ParameterDTO();
					parameter.setId((Integer)doc.getFirstValue(ImpressDTO.PARAMETER_ID));
					parameter.setStableKey(	Integer.getInteger(doc.getFirstValue(ImpressDTO.PARAMETER_STABLE_KEY).toString()));
					parameter.setStableId(doc.getFirstValue(ImpressDTO.PARAMETER_STABLE_ID).toString());
					parameter.setName(doc.getFirstValue(ImpressDTO.PARAMETER_NAME).toString());
					procedure.addParameter(parameter);
				}
			}

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return procedures;
	}


	/**
	 * @date 2015/07/08
	 * @author tudose
	 * @param pipelineStableId
	 * @return Pipeline in an object of type ImpressBean
	 * @throws SolrServerException, IOException
	 */
	public ImpressBaseDTO getPipeline(String pipelineStableId)
			throws SolrServerException, IOException, IOException {

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
	
	public ProcedureDTO getProcedureByStableId(String procedureStableId) {

		ProcedureDTO procedure = new ProcedureDTO();
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId)
				.setFields(ImpressDTO.PROCEDURE_ID,
						ImpressDTO.PROCEDURE_NAME,
						ImpressDTO.PROCEDURE_STABLE_ID,
						ImpressDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			ImpressDTO imd = response.getBeans(ImpressDTO.class).get(0);

			procedure.setStableId(imd.getProcedureStableId().toString());
			procedure.setName(imd.getProcedureName().toString());
			procedure.setStableKey(imd.getProcedureStableKey());
			return procedure;

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public ProcedureDTO getProcedureByStableKey(String procedureStableKey) {

		ProcedureDTO procedure = new ProcedureDTO();
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PROCEDURE_STABLE_KEY + ":\"" + procedureStableKey + "\"")
				.setFields(ImpressDTO.PROCEDURE_ID,
						ImpressDTO.PROCEDURE_NAME,
						ImpressDTO.PROCEDURE_STABLE_ID,
						ImpressDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			ImpressDTO imd = response.getBeans(ImpressDTO.class).get(0);

			procedure.setStableId(imd.getProcedureStableId().toString());
			procedure.setName(imd.getProcedureName().toString());
			return procedure;

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public Integer getParameterIdByStableKey(String parameterStableKey) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PARAMETER_STABLE_KEY + ":\"" + parameterStableKey + "\"")
				.setFields(ImpressDTO.PARAMETER_ID);

			QueryResponse response = solr.query(query);

			return response.getBeans(ImpressDTO.class).get(0).getParameterId();

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public Integer getProcedureStableKey(String procedureStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":\"" + procedureStableId + "\"")
				.setFields(ImpressDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			return response.getBeans(ImpressDTO.class).get(0).getProcedureStableKey();

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * @author tudose
	 * @since 2015/08/04
	 * @param procedureStableIds
	 * @param observationType
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<ParameterDTO> getParametersByProcedure(List<String> procedureStableIds, String observationType)
	throws SolrServerException, IOException, IOException{

		
		List<ParameterDTO> parameters = new ArrayList<>();
		SolrQuery query = new SolrQuery().setQuery("*:*");

		if (procedureStableIds != null){
			query.setFilterQueries(ImpressDTO.PROCEDURE_STABLE_ID + ":" + StringUtils.join(procedureStableIds, "* OR " + ImpressDTO.PROCEDURE_STABLE_ID + ":") + "*");
		}
		query.setFields(ImpressDTO.PARAMETER_ID, ImpressDTO.PARAMETER_NAME, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.PARAMETER_STABLE_KEY,
				ImpressDTO.UNITX,ImpressDTO.UNITY, ImpressDTO.REQUIRED, ImpressDTO.PROCEDURE_NAME);
		if (observationType != null){
			query.addFilterQuery(ImpressDTO.OBSERVATION_TYPE + ":" + observationType);
		}
		query.setRows(Integer.MAX_VALUE);

		QueryResponse response = solr.query(query);

		for (ImpressDTO doc: response.getBeans(ImpressDTO.class)){
			ParameterDTO param = new ParameterDTO();
			param.setStableId(doc.getParameterStableId());
			param.setStableKey(doc.getParameterStableKey());
			param.setName(doc.getParameterName());
			param.setId(doc.getParameterId());
			param.setUnitX(doc.getUnitX());
			param.setUnitY(doc.getUnitY());
			param.setRequired(doc.isRequired());
			param.addProcedureNames(doc.getProcedureName());
			parameters.add(param);
		}

		return parameters;
	}


	public Integer getPipelineStableKey(String pipelineStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.setFields(ImpressDTO.PIPELINE_STABLE_KEY);

			List<ImpressDTO> response = solr.query(query).getBeans(ImpressDTO.class);

			return response.get(0).getPipelineStableKey();

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
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

	public String getAnchorForProcedure(String procedureName, String procedureStableId) {

		String anchor = procedureName;
		String procKey = getProcedureStableKey(procedureStableId).toString();
		if (procKey != null) {
			anchor = String.format("<a href=\"%s\">%s</a>", getProcedureUrlByKey(procKey), procedureName);
		}

		return anchor;
	}


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
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return idToAbnormalMaId;
	}

	public Map<String,OntologyBean> getParameterStableIdToAbnormalEmapaMap(){

		Map<String,OntologyBean> idToAbnormalEmapaId=new HashMap<>();
		List<ImpressDTO> pipelineDtos=null;
		SolrQuery query = new SolrQuery()
			.setQuery(ImpressDTO.ANATOMY_ID + ":*" )
			.setFields(ImpressDTO.ANATOMY_ID, ImpressDTO.ANATOMY_TERM, ImpressDTO.PARAMETER_STABLE_ID).setRows(1000000);
		QueryResponse response=null;

		try {
			response = solr.query(query);
			pipelineDtos = response.getBeans(ImpressDTO.class);
			for(ImpressDTO pipe:pipelineDtos){
				if(!idToAbnormalEmapaId.containsKey(pipe.getParameterStableId())){
					idToAbnormalEmapaId.put(pipe.getParameterStableId(),new OntologyBean(pipe.getAnatomyId(),pipe.getAnatomyTerm()));
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return idToAbnormalEmapaId;
	}


	/**
	 * @author tudose
	 * @since 2015/08/20
	 * @param stableId
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public ParameterDTO getParameterByStableId(String stableId)
	throws SolrServerException, IOException, IOException {

		ParameterDTO param = new ParameterDTO();
		SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PARAMETER_STABLE_ID + ":" + stableId )
				.setFields(ImpressDTO.PARAMETER_NAME, ImpressDTO.PARAMETER_ID, ImpressDTO.PARAMETER_STABLE_KEY, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.OBSERVATION_TYPE, ImpressDTO.CATEGORIES,
						ImpressDTO.UNITX, ImpressDTO.UNITY)
				.setRows(1);
		QueryResponse response = solr.query(query);
		ImpressDTO dto = response.getBeans(ImpressDTO.class).get(0);
		param.setId(dto.getParameterId());
		param.setStableId(dto.getParameterStableId());
		param.setStableKey(dto.getParameterStableKey());
		param.setUnitX(dto.getUnitX());
		param.setUnitY(dto.getUnitY());
		param.setName(dto.getParameterName());
		param.setObservationType(ObservationType.valueOf(dto.getObservationType()));
		param.setCategories(dto.getCategories());

		return param;
	}


	public Map<String, Set<String>> getMpsForProcedures() throws SolrServerException, IOException, IOException {

		Map<String, Set<String>> mpsByProcedure = new HashMap<>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.setFields(ImpressDTO.PARAMETER_NAME, ImpressDTO.PARAMETER_ID, ImpressDTO.PARAMETER_STABLE_KEY, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.OBSERVATION_TYPE)
			.addFacetPivotField(ImpressDTO.PROCEDURE_STABLE_ID + "," + ImpressDTO.MP_ID)
			.setFacetLimit(-1)
			.setRows(0);

		QueryResponse response = solr.query(query);

		List<Map<String, String>> pivots = getFacetPivotResults(response, true);

		for (Map<String, String> pivot : pivots ) {
			String procedure = pivot.get(ImpressDTO.PROCEDURE_STABLE_ID);
			String mpId = pivot.get(ImpressDTO.MP_ID);

			if ( ! mpsByProcedure.containsKey(procedure)) {
				mpsByProcedure.put(procedure, new TreeSet<>());
			}

			mpsByProcedure.get(procedure).add(mpId);

		}

		return mpsByProcedure;
	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "ImpressService (pipeline core)";
	}

	
	public static String getProcedureUrl(Integer procedureStableKey){
		return "/impress/protocol/" + procedureStableKey;
	}

	public static String getParameterUrl(Integer parameterStableKey){
		return "/impress/parameterontologies/" + parameterStableKey ;
	}
	
}
