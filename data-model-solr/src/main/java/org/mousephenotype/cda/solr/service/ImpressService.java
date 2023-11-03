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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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

	@Value("${cms_base_url}")
	public String cmsBaseUrl;

	private SolrClient pipelineCore;


	@Inject
	public ImpressService(@Qualifier("pipelineCore") SolrClient pipelineCore) {
		super();
		this.pipelineCore = pipelineCore;
	}

	public ImpressService() {
		super();
	}


	/**
	 * @since 2015/07/17
	 * @author tudose
	 * @return
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

			System.out.println("URL for getProceduresByStableIdRegex " + SolrUtils.getBaseURL(pipelineCore) + "/select?" + query);

			QueryResponse response = pipelineCore.query(query);

			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){
				ProcedureDTO procedure = new ProcedureDTO(Long.getLong(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_ID).toString()),
														  Long.getLong(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_KEY).toString()),
						group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_ID).toString(),
						group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_NAME).toString());
				procedures.add(procedure);
			}

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return procedures;
	}


	public Set<ImpressDTO> getProceduresByMpTerm(String mpTermId, boolean intermediateMpTermsToo) throws IOException {

		try {
			String q = intermediateMpTermsToo ? ImpressDTO.MP_ID + ":\"" + mpTermId +"\" OR " + ImpressDTO.TOP_LEVEL_MP_ID + ":\"" + mpTermId +"\" OR " + ImpressDTO.INTERMEDIATE_MP_ID + ":\"" + mpTermId +"\"": ImpressDTO.MP_ID + ":\"" + mpTermId +"\"";
			SolrQuery query = new SolrQuery()
				.setQuery(q)
				.addField(ImpressDTO.PROCEDURE_ID)
				.addField(ImpressDTO.PROCEDURE_NAME)
				.addField(ImpressDTO.PROCEDURE_STABLE_ID)
				.addField(ImpressDTO.PROCEDURE_STABLE_KEY);
			// Adding another field in here such as pipeline will require some code changes to avoid multiple procedures on mp page.
			// As it is now (only mp and procedure fields) the equal method will ignore the non existing pipeline id field.

			query.setRows(1000000);

			QueryResponse response = pipelineCore.query(query);

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
        	QueryResponse res = pipelineCore.query(q);

        	for( PivotField pivot : res.getFacetPivot().get(pivotFacet)){
    			if (pivot.getPivot() != null){
    				for (PivotField parameter : pivot.getPivot()){
						String[] row = {pivot.getValue().toString(), parameter.getValue().toString()};
						result.add(row);
					}
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

			QueryResponse response = pipelineCore.query(query);

			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){

				ProcedureDTO procedure = new ProcedureDTO();
				procedure.setId(Long.getLong(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_ID).toString()));
				procedure.setName(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_NAME).toString());
				procedure.setStableId(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_ID).toString());
				procedure.setStableKey(	Long.getLong(group.getResult().get(0).getFirstValue(ImpressDTO.PROCEDURE_STABLE_KEY).toString()));

				for (SolrDocument doc : group.getResult()){
					ParameterDTO parameter = new ParameterDTO();
					parameter.setId((Long)doc.getFirstValue(ImpressDTO.PARAMETER_ID));
					parameter.setStableKey(	Long.getLong(doc.getFirstValue(ImpressDTO.PARAMETER_STABLE_KEY).toString()));
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
			throws SolrServerException, IOException  {

		SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.addField(ImpressDTO.PIPELINE_STABLE_ID)
				.addField(ImpressDTO.PIPELINE_STABLE_KEY)
				.addField(ImpressDTO.PIPELINE_NAME)
				.addField(ImpressDTO.PIPELINE_ID)
				.setRows(1);
		SolrDocument doc = pipelineCore.query(query).getResults().get(0);

		return new ImpressBaseDTO((Long)doc.getFirstValue(ImpressDTO.PIPELINE_ID),
				(Long) doc.getFirstValue(ImpressDTO.PIPELINE_STABLE_KEY),
				doc.getFirstValue(ImpressDTO.PIPELINE_STABLE_ID).toString(),
				doc.getFirstValue(ImpressDTO.PIPELINE_NAME).toString());

	}

	public ProcedureDTO getProcedureByStableId(String pipelineStableId, String procedureStableId) {

		ProcedureDTO procedure = new ProcedureDTO();
		try {
			SolrQuery query = new SolrQuery()
					.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId + " AND " + ImpressDTO.PIPELINE_STABLE_ID +":"+pipelineStableId)
					.setFields(ImpressDTO.PROCEDURE_ID,
							ImpressDTO.PROCEDURE_NAME,
							ImpressDTO.PROCEDURE_STABLE_ID,
							ImpressDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = pipelineCore.query(query);

			ImpressDTO imd = response.getBeans(ImpressDTO.class).get(0);

			procedure.setStableId(imd.getProcedureStableId());
			procedure.setName(imd.getProcedureName());
			procedure.setStableKey(imd.getProcedureStableKey());
			return procedure;

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	@Cacheable("procedureByStableId")
	public ProcedureDTO getProcedureByStableId(String procedureStableId) {

		ProcedureDTO procedure = new ProcedureDTO();
		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId)
				.setRows(1)
				.setFields(ImpressDTO.PROCEDURE_ID,
						ImpressDTO.PROCEDURE_NAME,
						ImpressDTO.PROCEDURE_STABLE_ID,
						ImpressDTO.PROCEDURE_STABLE_KEY);

			ImpressDTO imd = pipelineCore.query(query).getBeans(ImpressDTO.class).get(0);

			procedure.setStableId(imd.getProcedureStableId());
			procedure.setName(imd.getProcedureName());
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

			QueryResponse response = pipelineCore.query(query);

			ImpressDTO imd = response.getBeans(ImpressDTO.class).get(0);

			procedure.setStableId(imd.getProcedureStableId().toString());
			procedure.setName(imd.getProcedureName().toString());
			return procedure;

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public Long getParameterIdByStableKey(String parameterStableKey) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PARAMETER_STABLE_KEY + ":\"" + parameterStableKey + "\"")
				.setFields(ImpressDTO.PARAMETER_ID);

			QueryResponse response = pipelineCore.query(query);

			return response.getBeans(ImpressDTO.class).get(0).getParameterId();

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public Long getProcedureStableKey(String procedureStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PROCEDURE_STABLE_ID + ":\"" + procedureStableId + "\"")
				.setFields(ImpressDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = pipelineCore.query(query);

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
	public List<ParameterDTO> getParameters(List<String> procedureStableIds, String observationType, String toplevelMpId)
	throws SolrServerException, IOException {

		
		List<ParameterDTO> parameters = new ArrayList<>();
		SolrQuery query = new SolrQuery().setQuery("*:*");

		if (procedureStableIds != null){
			query.setFilterQueries(ImpressDTO.PROCEDURE_STABLE_ID + ":" + StringUtils.join(procedureStableIds, "* OR " + ImpressDTO.PROCEDURE_STABLE_ID + ":") + "*");
		}
		if (observationType != null){
			query.addFilterQuery(ImpressDTO.OBSERVATION_TYPE + ":" + observationType);
		}
		if (toplevelMpId != null){
			query.addFilterQuery(ImpressDTO.TOP_LEVEL_MP_ID + ":\"" + toplevelMpId + "\"");
		}
		query.setFields(ImpressDTO.PARAMETER_ID, ImpressDTO.PARAMETER_NAME, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.PARAMETER_STABLE_KEY,
				ImpressDTO.UNITX,ImpressDTO.UNITY, ImpressDTO.REQUIRED, ImpressDTO.PROCEDURE_NAME);

		query.setRows(Integer.MAX_VALUE);

		QueryResponse response = pipelineCore.query(query);

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


	public Long getPipelineStableKey(String pipelineStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.setFields(ImpressDTO.PIPELINE_STABLE_KEY);

			List<ImpressDTO> response = pipelineCore.query(query).getBeans(ImpressDTO.class);

			return response.get(0).getPipelineStableKey();

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getProcedureUrlByStableKeyAndPipelineStableKey(Long procedureStableKey, Long pipelineStableKey) {

		return String.format("%s/impress/ProcedureInfo?action=list&procID=%s&pipeID=%s",
				cmsBaseUrl,
				procedureStableKey,
				pipelineStableKey);
	}
	public String getProcedureUrlByKey(String procedureStableKey) {

		return cmsBaseUrl + "/impress/ProcedureInfo?action=list&procID=" + procedureStableKey;
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


	public String getPipelineUrlByStableKey(Long stableKey){
		return cmsBaseUrl + "/impress/PipelineInfo?id=" + stableKey;
	}


	public Map<String,OntologyBean> getParameterStableIdToAbnormalMaMap(){

		Map<String,OntologyBean> idToAbnormalMaId=new HashMap<>();
		List<ImpressDTO> pipelineDtos=null;
		SolrQuery query = new SolrQuery()
			.setQuery(ImpressDTO.MOUSE_ANATOMY_ID + ":*" )
			.setFields(ImpressDTO.MOUSE_ANATOMY_ID, ImpressDTO.MOUSE_ANATOMY_TERM, ImpressDTO.PARAMETER_STABLE_ID).setRows(1000000);
		QueryResponse response=null;

		try {
			response = pipelineCore.query(query);
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
			.setQuery(ImpressDTO.EMBRYO_ANATOMY_ID + ":*" )
			.setFields(ImpressDTO.EMBRYO_ANATOMY_ID, ImpressDTO.EMBRYO_ANATOMY_TERM, ImpressDTO.PARAMETER_STABLE_ID).setRows(1000000);
		QueryResponse response=null;

		try {
			response = pipelineCore.query(query);
			pipelineDtos = response.getBeans(ImpressDTO.class);
			for(ImpressDTO pipe:pipelineDtos){
				if(!idToAbnormalEmapaId.containsKey(pipe.getParameterStableId())){
					idToAbnormalEmapaId.put(pipe.getParameterStableId(),new OntologyBean(pipe.getEmapId(),pipe.getEmapTerm()));
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return idToAbnormalEmapaId;
	}


	/**
	 * Return an ImpressDTO object representing the IMPReSS parameter requested with the stableId parameter
	 * The ImpressDTO object with bew populated with only those attributes specified in the "fields" parameter, or
	 * if "fields" is null, the whold object will be populated
	 *
	 * see https://www.mousephenotype.org/impress
	 *
	 * @param stableId the IMPReSS ID of the parameter to return
	 *                 @param fields List of ImpressDTO
	 *
	 * @return parameterDTO object populated with the attributes indicated by the "fields" parameter, or all attributes if "fields" is null
	 *
	 * @throws SolrServerException, IOException
	 *
	 */
	public ImpressDTO getParameterByStableIdSpecifyFields(String stableId, List<String> fields)
			throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PARAMETER_STABLE_ID + ":" + stableId)
				.setRows(1);

		// Specify fields if set
		if (fields != null) {
			query.setFields(fields.toArray(new String[]{}));
		}

		List<ImpressDTO> parameters = pipelineCore.query(query).getBeans(ImpressDTO.class);
		return (parameters.size() > 0) ? parameters.get(0) : null;
	}


	/**
	 * @since 2015/08/20
	 * @param parameterStableId stable ID of the parameter in question
	 */
	public ParameterDTO getParameterByPipelineProcedureParameterStableKey(Long pipelineStableKey, Long procedureStableKey, String parameterStableId)
			throws SolrServerException, IOException  {

		ParameterDTO param = new ParameterDTO();
		SolrQuery query = new SolrQuery()
				.setQuery(String.format("%s:%s AND %s:%S AND %s:%s",
						ImpressDTO.PIPELINE_STABLE_KEY, pipelineStableKey,
						ImpressDTO.PROCEDURE_STABLE_KEY, procedureStableKey,
						ImpressDTO.PARAMETER_STABLE_ID, parameterStableId))
				.setFields(ImpressDTO.PARAMETER_NAME,
						ImpressDTO.PARAMETER_ID,
						ImpressDTO.PARAMETER_STABLE_KEY,
						ImpressDTO.PARAMETER_STABLE_ID,
						ImpressDTO.OBSERVATION_TYPE,
						ImpressDTO.CATEGORIES,
						ImpressDTO.UNITX,
						ImpressDTO.UNITY,
						ImpressDTO.PROCEDURE_NAME)
				.setRows(1);
		QueryResponse response = pipelineCore.query(query);

		List<ImpressDTO> dtoList = response.getBeans(ImpressDTO.class);
		if ((dtoList == null) || (dtoList.isEmpty())) {
			return null;
		}

		ImpressDTO dto = response.getBeans(ImpressDTO.class).get(0);
		param.setId(dto.getParameterId());
		param.setStableId(dto.getParameterStableId());
		param.setStableKey(dto.getParameterStableKey());
		param.setUnitX(dto.getUnitX());
		param.setUnitY(dto.getUnitY());
		param.setName(dto.getParameterName());
		param.setObservationType(ObservationType.valueOf(dto.getObservationType()));
		param.setCategories(dto.getCategories());
		List<String> procedures = new ArrayList<String>();
		procedures.add(dto.getProcedureName());
		param.setProcedureNames(procedures);

		return param;
	}


	/**
	 * @since 2015/08/20
	 * @param stableId
	 */
	@Cacheable("parameterByStableId")
	public ParameterDTO getParameterByStableId(String stableId)
	throws SolrServerException, IOException  {

		ParameterDTO param = new ParameterDTO();
		SolrQuery query = new SolrQuery()
				.setQuery(ImpressDTO.PARAMETER_STABLE_ID + ":" + stableId + " " + ImpressDTO.OBSERVATION_TYPE + ":*")
				.setFields(ImpressDTO.PARAMETER_NAME,
						ImpressDTO.PARAMETER_ID,
						ImpressDTO.PARAMETER_STABLE_KEY,
						ImpressDTO.PARAMETER_STABLE_ID,
						ImpressDTO.OBSERVATION_TYPE,
						ImpressDTO.CATEGORIES,
						ImpressDTO.UNITX,
						ImpressDTO.UNITY,
						ImpressDTO.PROCEDURE_NAME)
				.setRows(1);
		QueryResponse response = pipelineCore.query(query);

		List<ImpressDTO> dtoList = response.getBeans(ImpressDTO.class);
		if ((dtoList == null) || (dtoList.isEmpty())) {
			return null;
		}

		ImpressDTO dto = dtoList.get(0);
		param.setId(dto.getParameterId());
		param.setStableId(dto.getParameterStableId());
		param.setStableKey(dto.getParameterStableKey());
		param.setUnitX(dto.getUnitX());
		param.setUnitY(dto.getUnitY());
		param.setName(dto.getParameterName());
		param.setObservationType(dto.getObservationType() != null ? ObservationType.valueOf(dto.getObservationType()) : null);
		param.setCategories(dto.getCategories());
		List<String> procedures = new ArrayList<>();
		procedures.add(dto.getProcedureName());
		param.setProcedureNames(procedures);

		return param;
	}


	public Map<String, Set<String>> getMpsForProcedures() throws SolrServerException, IOException  {

		Map<String, Set<String>> mpsByProcedure = new HashMap<>();

		SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.setFields(ImpressDTO.PARAMETER_NAME, ImpressDTO.PARAMETER_ID, ImpressDTO.PARAMETER_STABLE_KEY, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.OBSERVATION_TYPE)
			.addFacetPivotField(ImpressDTO.PROCEDURE_STABLE_ID + "," + ImpressDTO.MP_ID)
			.setFacetLimit(-1)
			.setRows(0);

		QueryResponse response = pipelineCore.query(query);

		List<Map<String, String>> pivots = getFacetPivotResults(response, true);

		for (Map<String, String> pivot : pivots ) {
			String procedure = pivot.get(ImpressDTO.PROCEDURE_STABLE_ID);
			String mpId = pivot.get(ImpressDTO.MP_ID);

			if ( ! mpsByProcedure.containsKey(procedure)) {
				mpsByProcedure.put(procedure, new TreeSet<>());
			}

			try {
				mpsByProcedure.get(procedure).add(mpId);
			} catch(NullPointerException e) {
				e.printStackTrace();
				e.getLocalizedMessage();
				if (mpsByProcedure != null) {
					System.out.println("Missing procedure in mpsByProcedure from ImpressService.");
					System.out.println(procedure);
				}
			}
		}

		return mpsByProcedure;
	}


	@Override
	public long getWebStatus() throws SolrServerException, IOException  {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);
		QueryResponse response = pipelineCore.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "ImpressService (pipeline core)";
	}

	
	public static String getProcedureUrl(Long procedureStableKey){
		return "/impress/protocol/" + procedureStableKey;
	}

	public static String getParameterUrl(Long parameterStableKey){
		return "/impress/parameterontologies/" + parameterStableKey ;
	}


	public String getParameterUrlByProcedureAndParameterKey(Long procedureKey,Long parameterKey) {
		
		return cmsBaseUrl + "/impress/OntologyInfo?action=list&procID="+procedureKey+"#"+parameterKey;
	}
}
