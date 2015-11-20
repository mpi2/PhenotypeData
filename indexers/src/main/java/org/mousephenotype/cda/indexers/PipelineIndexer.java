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
package org.mousephenotype.cda.indexers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.ValidationException;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;



public class PipelineIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory.getLogger(PipelineIndexer.class);
	private Connection komp2DbConnection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("mpIndexing")
	SolrServer mpCore;

	@Autowired
	@Qualifier("pipelineIndexing")
	SolrServer pipelineCore;

	
	private Map<String, ParameterDTO> paramIdToParameter;
	private Map<String, ProcedureDTO> procedureIdToProcedure;
	private Map<String, PipelineDTO> pipelines;
	private Map<String, MpDTO> mpIdToMp;
	private Map<String, ObservationType> parameterToObservationTypeMap;
	protected static final int MINIMUM_DOCUMENT_COUNT = 10;

	
	public PipelineIndexer() {

	}
	

	public static void main(String[] args) 
	throws IndexerException {

		PipelineIndexer indexer = new PipelineIndexer();
		indexer.initialise(args);
		indexer.run();
		indexer.validateBuild();

		logger.info("PipelineIndexer finished.  Exiting.");
	}
	

	@Override
	public void validateBuild() 
	throws IndexerException {
		
		Long numFound = getDocumentCount(pipelineCore);

		if (numFound <= MINIMUM_DOCUMENT_COUNT){
			throw new IndexerException(new ValidationException(
					"Actual pipeline document count is " + numFound + "."));
		}
		if (numFound != documentCount){
			logger.warn("Added " + documentCount
					+ " pipeline documents but SOLR reports " + numFound
					+ " documents.");
		}
		else{
			logger.info("validateBuild(): Indexed " + documentCount
					+ " pipeline documents.");
		}
	}
	

	@Override
	public void initialise(String[] args) 
	throws IndexerException {

		super.initialise(args);

		try {
			this.komp2DbConnection = komp2DataSource.getConnection();
		} catch (SQLException sqle) {
			logger.error("Caught SQL Exception initialising database connections: {}", sqle.getMessage());
			throw new IndexerException(sqle);
		}
	}
	
	

	private void initialiseSupportingBeans()
	throws IndexerException {

		parameterToObservationTypeMap = getObservationTypeMap();
		paramIdToParameter = populateParamIdToParameterMap();
		procedureIdToProcedure = populateProcedureIdToProcedureMap();
		pipelines = populatePipelineList();
		addAbnormalMaOntologyMap();
		mpIdToMp = populateMpIdToMp();
	}
	

	@Override
	public void run() 
	throws IndexerException {

		long startTime = System.currentTimeMillis();

		try {

			logger.info("Starting Pipeline Indexer...");

			initialiseSupportingBeans();
			pipelineCore.deleteByQuery("*:*");
			pipelineCore.commit();

			for (PipelineDTO pipeline : pipelines.values()) {
				
				for (ProcedureDTO procedure : pipeline.getProcedures()){

					List<ParameterDTO> parameters = procedure.getParameters();
	
					for (ParameterDTO param : parameters) {
						
						ImpressDTO doc = new ImpressDTO();
						doc.setParameterId(param.getId());
						doc.setParameterName(param.getName());
						doc.setParameterStableId(param.getStableId());
						doc.setParameterStableKey(param.getStableKey());					
	
						doc.setProcedureId(procedure.getId());
						doc.setProcedureName(procedure.getName());
						doc.setProcedureStableId(procedure.getStableId());
						doc.setProcedureStableKey(procedure.getStableKey());
	
						doc.setPipelineId(pipeline.getId());
						doc.setPipelineName(pipeline.getName());
						doc.setPipelineStableId(pipeline.getStableId());
						doc.setPipelineStableKey(pipeline.getStableKey());
	
						// ididid to be pipe proc param stable id combination that should be unique and is unique in solr
						String ididid = pipeline.getStableId() + "_" + procedure.getStableId() + "_" + param.getStableId();
						doc.setIdIdId(ididid);
	
						doc.setRequired(procedure.isRequired());
						//doc.setDescription(procBean.description); -> maybe we don't need this. If we do, should differentiate from parameter description.
						doc.setObservationType(param.getObservationType().name());
						if (param.getUnit() != null){
							doc.setUnit(param.getUnit());
						}
						doc.setMetadata(param.isMetadata());
						doc.setIncrement(param.isIncrement());
						doc.setHasOptions(param.isOptions());
						doc.setDerived(param.isDerived());
						doc.setMedia(param.isMedia());
						
						if (param.getCategories().size() > 0){
							doc.setCategories(param.getCategories());
						}	
						
						if(param.getMaId() != null){
							doc.setMaId(param.getMaId());
							doc.setMaName(param.getMaName());
						}
	
						if (param.getMpIds().size() > 0){
							for (String mpId : param.getMpIds()){
								doc.addMpId(mpId);
								MpDTO mp = mpIdToMp.get(mpId);
								doc.addMpTerm(mp.getMpTerm());
								if (mp.getIntermediateMpId() != null && mp.getIntermediateMpId().size() > 0){
									doc.addIntermediateMpId(mp.getIntermediateMpId());
									doc.addIntermediateMpTerm(mp.getIntermediateMpTerm());
								}
								if (mp.getTopLevelMpId() != null && mp.getTopLevelMpId().size() > 0){
									doc.addTopLevelMpId(mp.getTopLevelMpId());
									doc.addTopLevelMpTerm(mp.getTopLevelMpTerm());
								}
							}
						}
						
						if (param.getAbnormalMpId() != null){
							doc.setAbnormalMpId(param.getAbnormalMpId());
						}
						if (param.getIncreasedMpId() != null){
							doc.setIncreasedMpId(param.getIncreasedMpId());
						}
						if (param.getDecreasedMpId()!= null){
							doc.setDecreasedMpId(param.getDecreasedMpId());
						}
						
						if (doc.getProcedureId() == null){
							System.out.println(doc.getIdidid() + "  " + doc);
						}
						pipelineCore.addBean(doc);
						documentCount++;
						
						if(documentCount % 10000 == 0){
							logger.info("Commit to Solr. Document count = " + documentCount);
							pipelineCore.commit();
						}
					}
				}

			}
			logger.info("Commit to Solr. Document count = " + documentCount);
			pipelineCore.commit();

		} catch (IOException | SolrServerException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		logger.info("Pipeline indexer completed in " + ( (endTime - startTime) / 1000));

	}
	
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

	
	protected Map<String, ParameterDTO> populateParamIdToParameterMap() {

		logger.info("populating PCS pipeline info");
		Map<String, ParameterDTO> localParamDbIdToParameter = new HashMap<>();
		String queryString = "SELECT * FROM phenotype_parameter";
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ParameterDTO param = new ParameterDTO();
				// store the row in a map of column names to values
				String id = resultSet.getString("stable_id");
				param.setName(resultSet.getString("name"));
				param.setId(resultSet.getInt("id"));
				param.setStableId(resultSet.getString("stable_id"));
				param.setStableKey(resultSet.getInt("stable_key"));
				param.setDataType(resultSet.getString("datatype"));
				param.setParameterType(resultSet.getString("parameter_type"));
				param.setMetadata(resultSet.getBoolean("metadata"));
				param.setUnit(resultSet.getString("unit"));
				param.setDerived(resultSet.getBoolean("derived"));
				param.setRequired(resultSet.getBoolean("required"));
				param.setIncrement(resultSet.getBoolean("increment"));
				param.setOptions(resultSet.getBoolean("options"));
				param.setMedia(resultSet.getBoolean("media"));
				param.setObservationType(assignType(param));
				if (param.getObservationType() == null){
					logger.warn("Obs type is NULL for :" + param.getStableId() + "  " + param.getObservationType());
				}
				localParamDbIdToParameter.put(id, param);
			}
			logger.info("[Check] should be 5704+ phenotype parameter and has "	+ localParamDbIdToParameter.size() + " entries");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		localParamDbIdToParameter = addCategories(localParamDbIdToParameter);
		localParamDbIdToParameter = addMpTerms(localParamDbIdToParameter);
		return localParamDbIdToParameter;

	}

	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param stableIdToParameter
	 * @return
	 */
	protected Map<String, ParameterDTO> addCategories(Map<String, ParameterDTO> stableIdToParameter){

		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);
		String queryString = "SELECT stable_id, o.name AS cat_name, o.description AS cat_description "
				+ " FROM phenotype_parameter p "
				+ " INNER JOIN phenotype_parameter_lnk_option l ON l.parameter_id=p.id "
				+ " INNER JOIN phenotype_parameter_option o ON o.id=l.option_id "
				+ " ORDER BY stable_id ASC;";
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();
			ParameterDTO param = null;
			
			while (resultSet.next()) {
				
				String paramId = resultSet.getString("stable_id");
				if (param == null || !param.getStableId().equalsIgnoreCase(paramId)){
					param = stableIdToParameter.get(paramId);
				}
				
				param.addCategories(getCategory(resultSet));
				localIdToParameter.put(param.getStableId(), param);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return localIdToParameter;
	}
	
	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	protected String getCategory (ResultSet resultSet) 
	throws SQLException{
		
		String name = resultSet.getString("cat_name");
		String description = resultSet.getString("cat_description");
		if (name.matches("[0-9]+")){
			return description;
		}
		
		return name;
		
	}
	
	
	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param stableIdToParameter
	 * @return
	 */
	protected Map<String, ParameterDTO> addMpTerms(Map<String, ParameterDTO> stableIdToParameter){
		
		String queryString = "SELECT stable_id, ontology_acc, event_type FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=pp.id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON l.annotation_id=ppoa.id "
				+ " WHERE ontology_db_id=5 "
				+ " ORDER BY stable_id ASC; ";
		
		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();
			ParameterDTO param = null;
			
			while (resultSet.next()) {
				
				String paramId = resultSet.getString("stable_id");
				if (param == null || !param.getStableId().equalsIgnoreCase(paramId)){
					param = stableIdToParameter.get(paramId);
				}
				
				String type = resultSet.getString("event_type");
				if (type.equalsIgnoreCase("abnormal")){
					param.setAbnormalMpId(resultSet.getString("ontology_acc"));
				} else if(type.equalsIgnoreCase("increased")){
					param.setIncreasedMpId(resultSet.getString("ontology_acc"));
				} else if (type.equalsIgnoreCase("decreased")){
					param.setDecreasedMpId(resultSet.getString("ontology_acc"));
				}
				
				param.addMpIds(resultSet.getString("ontology_acc"));
				localIdToParameter.put(param.getStableId(), param);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return localIdToParameter;
	
	}
	
	
	protected Map<String, Set<String>> populateProcedureToParameterMap() {

		logger.info("Populating procIdToParams");
		Map<String, Set<String>> procIdToParams = new HashMap<>();
		
		String queryString = "SELECT procedure_id, parameter_id, pp.stable_id as parameter_stable_id, pproc.stable_id as procedure_stable_id "
				+ " FROM phenotype_procedure_parameter ppp "
				+ " INNER JOIN phenotype_parameter pp ON pp.id=ppp.parameter_id "
				+ " INNER JOIN phenotype_procedure pproc ON pproc.id=ppp.procedure_id";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Set<String> parameterIds = new HashSet<>();
				String paramId = resultSet.getString("parameter_stable_id");
				String procId = resultSet.getString("procedure_stable_id");

				if (procIdToParams.containsKey(procId)) {
					parameterIds = procIdToParams.get(procId);
				}
				parameterIds.add(paramId);
				procIdToParams.put(procId, parameterIds);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("[Check] procIdToParams should be 5704+  size=" + procIdToParams.size());
		return procIdToParams;
	}

	
	protected Map<String, ProcedureDTO> populateProcedureIdToProcedureMap() {

		logger.info("Populating procedureIdToProcedureMap");
		
		Map<String, Set<String>> procIdToParams = populateProcedureToParameterMap();
		
		Map<String, ProcedureDTO> procedureIdToProcedureMap = new HashMap<>();
		String queryString = "SELECT id as pproc_id, stable_id, name, stable_key, is_mandatory, description, concat(name, '___', stable_id) as proc_name_id "
				+ "FROM phenotype_procedure";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ProcedureDTO proc = new ProcedureDTO();
				proc.setStableId(resultSet.getString("stable_id"));
				proc.setId(resultSet.getInt("pproc_id"));
				proc.setName(resultSet.getString("name"));
				proc.setStableKey(resultSet.getInt("stable_key"));
				proc.setProcNameId(resultSet.getString("proc_name_id"));
				proc.setRequired(resultSet.getBoolean("is_mandatory"));
				proc.setDescription(resultSet.getString("description"));
				for (String parameterId : procIdToParams.get(proc.getStableId())){
					proc.addParameter(paramIdToParameter.get(parameterId));
				}
				procedureIdToProcedureMap.put(resultSet.getString("stable_id"), proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("[Check] should be 190+ procedureIdToProcedureMap size=" + procedureIdToProcedureMap.size());
		
		return procedureIdToProcedureMap;
	}


	protected Map<String, PipelineDTO> populatePipelineList() {

		logger.info("Populating procIdToPipelineMap");
		
		Map<String, PipelineDTO> procIdToPipelineMap = new HashMap<>();
		String queryString = "SELECT pproc.stable_id as procedure_stable_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, "
				+ " ppipe.stable_key AS pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) AS pipe_proc_sid "
				+ " FROM phenotype_procedure pproc INNER JOIN phenotype_pipeline_procedure ppproc ON pproc.id=ppproc.procedure_id "
				+ " INNER JOIN phenotype_pipeline ppipe ON ppproc.pipeline_id=ppipe.id"
				+ " WHERE ppipe.db_id=6 ORDER BY ppipe.id ASC ";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				
				String pipelineStableId = resultSet.getString("pipe_stable_id");
				PipelineDTO pipe = new PipelineDTO();
				
				if (procIdToPipelineMap.containsKey(pipelineStableId)){
					pipe = procIdToPipelineMap.get(pipelineStableId);
				}
				pipe.setId(resultSet.getInt("pipe_id"));
				pipe.setName(resultSet.getString("pipe_name"));
				pipe.setStableKey(resultSet.getInt("pipe_stable_key"));
				pipe.setStableId(resultSet.getString("pipe_stable_id"));
				pipe.addProcedure(procedureIdToProcedure.get(resultSet.getString("procedure_stable_id")));
				procIdToPipelineMap.put(pipelineStableId, pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return procIdToPipelineMap;
		
	}

	
	protected void addAbnormalMaOntologyMap(){
		
		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=8 LIMIT 10000";
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(sqlQuery)) {
			
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("stable_id");
				paramIdToParameter.get(parameterId).setMaId(resultSet.getString("ontology_acc"));
				paramIdToParameter.get(parameterId).setMaName(resultSet.getString("name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	protected Map<String, MpDTO> populateMpIdToMp() 
	throws IndexerException {

		Map<String, MpDTO> map = null;
		try {
			map = SolrUtils.populateMpTermIdToMp(mpCore);
		} catch (SolrServerException e) {
			throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMpTermIdToMp()", e);
		}
		return map;
	}


	/**@since 2015
	 * @author tudose
	 * @param parameter
	 * @param value
	 * @return
	 * @throws SolrServerException 
	 */
	// Method copied from org.mousephenotype.cda.db.impress.Utilities.
	// Adjusted to avoid use of Parameter dao obj.
	// Method should only be used at indexing time. After that query pipeline core to find type.
	protected ObservationType assignType(ParameterDTO parameter) 
	throws SolrServerException {
		return parameterToObservationTypeMap.get(parameter.getStableId());
		
//		Map<String, String> MAPPING = new HashMap<>();
//		MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
//		MAPPING.put("M-G-P_022_001_001", "FLOAT");
//		MAPPING.put("ESLIM_006_001_035", "FLOAT");
//		MAPPING = Collections.unmodifiableMap(MAPPING);
//
//		ObservationType observationType = null;
//		String datatype = parameter.getDataType();
//		
//		if (MAPPING.containsKey(parameter.getStableId())) {
//			datatype = MAPPING.get(parameter.getStableId());
//		}
//
//		if (parameter.isMetadata()) {
//
//			observationType = ObservationType.metadata;
//
//		} else {
//
//			if (parameter.isOptions()) {
//
//				observationType = ObservationType.categorical;
//
//			} else {
//
//				if (datatype.equals("TEXT")) {
//
//					observationType = ObservationType.text;
//
//				} else if (datatype.equals("DATETIME") || datatype.equals("DATE")  || datatype.equals("TIME")) {
//
//					observationType = ObservationType.datetime;
//
//				} else if (datatype.equals("BOOL")) {
//
//					observationType = ObservationType.categorical;
//
//				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {
//
//					if (parameter.isIncrement()) {
//
//						observationType = ObservationType.time_series;
//
//					} else {
//
//						observationType = ObservationType.unidimensional;
//
//					}
//
//				} else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.getName().contains("images"))) {
//
//					observationType = ObservationType.image_record;
//
//				} else if (datatype.equals("") && !parameter.isOptions() && !parameter.getName().contains("images")) {
//
//					/* Look up in observation core. If we have a value the observation type will be correct. 
//					 * If not use the approximation below (categorical will always be missed).
//					 * See declaration of checkType(param, value) in impress utils.
//					 */					
//					ObservationType obs = os.getObservationTypeForParameterStableId(parameter.getStableId());
//					if (obs != null){
//						observationType = obs;
//					} else {			
//						if (parameter.isIncrement()) {
//							observationType = ObservationType.time_series;
//						} else {
//							observationType = ObservationType.unidimensional;
//						}
//					}
//
//				} else {
//					logger.warn("UNKNOWN data type : " + datatype  + " " + parameter.getStableId());
//				}
//			}
//		}

		//return observationType;
	}
	
	private Map<String,ObservationType> getObservationTypeMap(){
		Map<String,ObservationType> paramterToObservationTypeMap=new HashMap<>();
		String query= "select distinct parameter_stable_id, observation_type from observation";
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {
			
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("parameter_stable_id");
				String obsType=resultSet.getString("observation_type");
				
				ObservationType obType;
				try {
					obType = ObservationType.valueOf(obsType);
					paramterToObservationTypeMap.put(parameterId, obType);
				} catch (IllegalArgumentException e) {
					logger.warn("no ObservationType found for parameter:"+parameterId);
					e.printStackTrace();
				}
				
				
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramterToObservationTypeMap;
	}
	
}



