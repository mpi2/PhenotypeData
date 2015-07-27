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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.maven.doxia.logging.Log;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.ValidationException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
import org.neo4j.cypher.internal.compiler.v2_1.planner.logical.steps.uniqueIndexSeekLeafPlanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Populate the MA core
 */
public class PipelineIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory.getLogger(PipelineIndexer.class);
	private Connection komp2DbConnection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("alleleIndexing")
	SolrServer alleleCore;

	@Autowired
	@Qualifier("mpIndexing")
	SolrServer mpCore;

	@Autowired
	@Qualifier("pipelineIndexing")
	SolrServer pipelineCore;

	@Autowired
	ObservationService os;
	
	private Map<String, ParameterDTO> paramDbIdToParameter;
	private Map<String, Set<String>> procedureIdToParams;
	private Map<String, ProcedureDTO> procedureIdToProcedure;
	private List<PipelineBean> pipelines;
	private Map<String, List<GfMpBean>> pppidsToGfMpBeans;
	private Map<String, List<AlleleDTO>> mgiToAlleleMap;
	private Map<String, MpDTO> mpIdToMp;
	protected static final int MINIMUM_DOCUMENT_COUNT = 10;

	public PipelineIndexer() {

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
			logger.warn("WARNING: Added " + documentCount
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

		paramDbIdToParameter = populateParamDbIdToParametersMap();
		procedureIdToParams = populateProcedureToParameterMap();
		procedureIdToProcedure = populateProcedureIdToProcedureMap();
		pipelines = populateProcedureIdToPipelineMap();
		addAbnormalMaOntologyMap();
		pppidsToGfMpBeans = populateGfAccAndMp();
		mgiToAlleleMap = IndexerMap.getGeneToAlleles(alleleCore);
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

			for (PipelineBean pipeline : pipelines) {

				Set<String> parameterIds = procedureIdToParams.get(pipeline.procedureStableId);

				for (String paramId : parameterIds) {
					
					PipelineDTO doc = new PipelineDTO();
					ParameterDTO param = paramDbIdToParameter.get(paramId);
					ProcedureDTO procBean = procedureIdToProcedure.get(pipeline.procedureStableId);
					
					doc.setParameterId(param.parameterId);
					doc.setParameterName(param.parameterName);
					doc.setParameterStableId(param.parameterStableId);
					doc.setParameterStableKey(param.parameterStableKey);					

					doc.setProcedureId(procBean.procedureId);
					doc.setProcedureName(procBean.procedureName);
					doc.setProcedureStableId(procBean.procedureStableId);
					doc.setProcedureStableKey(procBean.procedureStableKey);

					doc.setPipelineId(pipeline.pipelineId);
					doc.setPipelineName(pipeline.pipelineName);
					doc.setPipelineStableId(pipeline.pipelineStableId);
					doc.setPipelineStableKey(pipeline.pipelineStableKey);

					// ididid to be pipe proc param stable id combination that should be unique and is unique in solr
					String ididid = pipeline.pipelineStableId + "_" + procBean.procedureStableId + "_" + param.parameterStableId;
					doc.setIdIdId(ididid);

					doc.setRequired(procBean.required);
					doc.setDescription(procBean.description);
					doc.setObservationType(param.observationType.toString());
					doc.setUnit(param.unit);
					doc.setMetadata(param.metadata);
					doc.setIncrement(param.increment);
					doc.setHasOptions(param.options);
					doc.setDerived(param.derived);
					doc.setMedia(param.media);
					
					if (param.categories.size() > 0){
						doc.setCategories(param.categories);
					}					
					if(param.abnormalMaId != null){
						doc.setMaId(param.abnormalMaId);
						doc.setMaName(param.abnormalMaName);
					}
					documentCount++;
					pipelineCore.addBean(doc);
					if(documentCount % 10000 == 0){
						System.out.println("documentCount=" + documentCount);
						pipelineCore.commit();
					}
				}

			}

			logger.info("commiting to Pipeline core for last time!");
			logger.info("Pipeline commit started.");
			pipelineCore.commit();
			logger.info("Pipeline commit finished.");

		} catch (IOException | SolrServerException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		logger.info("time was " + (endTime - startTime) / 1000);
		logger.info("Pipeline Indexer complete!");
	}
	

	// PROTECTED METHODS
	@Override
	protected Logger getLogger() {
		return logger;
	}

	
	private Map<String, ParameterDTO> populateParamDbIdToParametersMap() {

		logger.info("populating PCS pipeline info");
		Map<String, ParameterDTO> localParamDbIdToParameter = new HashMap<>();
		String queryString = "select * from phenotype_parameter";
		//SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=8 LIMIT 100;
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ParameterDTO param = new ParameterDTO();
				// store the row in a map of column names to values
				String id = resultSet.getString("stable_id");
				param.parameterName = resultSet.getString("name");
				param.parameterStableId = resultSet.getString("stable_id");
				param.parameterStableKey = resultSet.getInt("stable_key");
				param.dataType = resultSet.getString("datatype");
				param.parameterType = resultSet.getString("parameter_type");
				param.metadata = resultSet.getBoolean("metadata");
				param.unit = resultSet.getString("unit");
				param.derived = resultSet.getBoolean("derived");
				param.required = resultSet.getBoolean("required");
				param.increment = resultSet.getBoolean("increment");
				param.options = resultSet.getBoolean("options");
				param.media = resultSet.getBoolean("media");
				param.observationType = assignType(param);
				if (param.observationType == null){
					System.out.println("Obs type : " + param.parameterStableId + "  " + param.observationType);
				}
				// TODO mp_terms 
				localParamDbIdToParameter.put(id, param);
			}
			System.out.println("[Check] should be 5704+ phenotype parameter and has "	+ localParamDbIdToParameter.size() + " entries");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		localParamDbIdToParameter = addCategories(localParamDbIdToParameter);
		return localParamDbIdToParameter;

	}

	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param stableIdToParameter
	 * @return
	 */
	private Map<String, ParameterDTO> addCategories(Map<String, ParameterDTO> stableIdToParameter){
		
		String queryString = "SELECT * FROM phenotype_parameter p INNER JOIN phenotype_parameter_lnk_option l ON l.parameter_id=p.id "
				+ " INNER JOIN phenotype_parameter_option o ON o.id=l.option_id order by stable_id ASC limit 10;";
		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();
			ParameterDTO param = null;
			
			while (resultSet.next()) {
				
				String paramId = resultSet.getString("parameter_stable_id");
				if (param == null || !param.parameterStableId.equalsIgnoreCase(paramId)){
					param = stableIdToParameter.get(paramId);
				}
				
				param.categories.add(getCategory(resultSet));
				localIdToParameter.put(param.parameterStableId, param);
				
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
	private String getCategory (ResultSet resultSet) 
	throws SQLException{
		
		String name = resultSet.getString("name");
		String description = resultSet.getString("description");
		if (name.matches("[0-9]+")){
			return description;
		}
		
		return name;
		
	}
	
	
	private Map<String, Set<String>> populateProcedureToParameterMap() {

		logger.info("Populating param To ProcedureId info");
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
		System.out.println("[Check] procIdToParams should be 5704+  size=" + procIdToParams.size());
		return procIdToParams;
	}

	private Map<String, ProcedureDTO> populateProcedureIdToProcedureMap() {

		logger.info("populating procedureId to Procedure Map info");
		Map<String, ProcedureDTO> procedureIdToProcedureMap = new HashMap<>();
		String queryString = "SELECT id as pproc_id, stable_id, name, stable_key, is_mandatory, description, concat(name, '___', stable_id) as proc_name_id "
				+ "FROM phenotype_procedure";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ProcedureDTO proc = new ProcedureDTO();
				proc.procedureStableId = resultSet.getString("stable_id");
				proc.procedureName = resultSet.getString("name");
				proc.procedureStableKey = resultSet.getInt("stable_key");
				proc.procNameId = resultSet.getString("proc_name_id");
				proc.required = new Boolean(resultSet.getString("is_mandatory"));
				proc.description = resultSet.getString("description");
				procedureIdToProcedureMap.put(resultSet.getString("stable_id"), proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("[Check] should be 187+ procedureIdToProcedureMap size="
				+ procedureIdToProcedureMap.size());
		return procedureIdToProcedureMap;
	}


	private List<PipelineBean> populateProcedureIdToPipelineMap() {

		logger.info("populating procedureId to  pipeline Map info");
		List<PipelineBean> procIdToPipelineMap = new ArrayList<>();
		String queryString = "SELECT pproc.stable_id as procedure_stable_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, "
				+ " ppipe.stable_key AS pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) AS pipe_proc_sid "
				+ " FROM phenotype_procedure pproc INNER JOIN phenotype_pipeline_procedure ppproc ON pproc.id=ppproc.procedure_id "
				+ " INNER JOIN phenotype_pipeline ppipe ON ppproc.pipeline_id=ppipe.id "
				+ " WHERE ppipe.db_id=6";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				
				PipelineBean pipe = new PipelineBean();
				pipe.pipelineId =  resultSet.getInt("pipe_id");
				pipe.pipelineName = resultSet.getString("pipe_name");
				pipe.pipelineStableKey = resultSet.getInt("pipe_stable_key");
				pipe.pipelineStableId = resultSet.getString("pipe_stable_id");
				pipe.pipeProcSid = resultSet.getString("pipe_proc_sid");
				pipe.procedureStableId = resultSet.getString("procedure_stable_id");
				procIdToPipelineMap.add(pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return procIdToPipelineMap;
		
	}

	
	private void addAbnormalMaOntologyMap(){
		
		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=8 LIMIT 10000";
		try (PreparedStatement p = komp2DbConnection.prepareStatement(sqlQuery)) {
			
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("stable_id");
				paramDbIdToParameter.get(parameterId).abnormalMaId = resultSet.getString("ontology_acc");
				paramDbIdToParameter.get(parameterId).abnormalMaName = resultSet.getString("name");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	private Map<String, List<GfMpBean>> populateGfAccAndMp() {
		
		logger.info("populating GfAcc and Mp info - started");
		Map<String, List<GfMpBean>> gfMpBeansMap = new HashMap<>();
		String queryString = "select distinct concat(s.parameter_id,'_',s.procedure_id,'_',s.pipeline_id) as pppIds, s.gf_acc, s.mp_acc, s.parameter_id as pp_parameter_id, s.procedure_id as pproc_procedure_id, s.pipeline_id as ppipe_pipeline_id, s.allele_acc, s.strain_acc from phenotype_parameter pp INNER JOIN phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id INNER JOIN phenotype_procedure pproc on ppp.procedure_id=pproc.id INNER JOIN phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id INNER JOIN phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id inner join phenotype_call_summary s on ppipe.id=s.pipeline_id and pproc.id=s.procedure_id and pp.id=s.parameter_id";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				GfMpBean gfMpBean = new GfMpBean();

				String pppids = resultSet.getString("pppids");
				String gfAcc = resultSet.getString("gf_acc");
				String mpAcc = resultSet.getString("mp_acc");
				gfMpBean.gfAcc = gfAcc;
				gfMpBean.mpAcc = mpAcc;
				List<GfMpBean> beanList = new ArrayList<>();
				if (gfMpBeansMap.containsKey(pppids)) {
					beanList = gfMpBeansMap.get(pppids);
				}
				beanList.add(gfMpBean);
				gfMpBeansMap.put(pppids, beanList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("populating GfAcc and Mp info - finished");

		return gfMpBeansMap;
	}

	class GfMpBean {

		String gfAcc;
		String mpAcc;

	}

	private Map<String, MpDTO> populateMpIdToMp() 
	throws IndexerException {

		Map<String, MpDTO> map = null;
		try {
			map = SolrUtils.populateMpTermIdToMp(mpCore);
		} catch (SolrServerException e) {
			throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMpTermIdToMp()", e);
		}
		return map;
	}

	public static void main(String[] args) 
	throws IndexerException {

		PipelineIndexer indexer = new PipelineIndexer();
		indexer.initialise(args);
		indexer.run();
		indexer.validateBuild();

		logger.info("Process finished.  Exiting.");
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
		
		Map<String, String> MAPPING = new HashMap<>();
		MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
		MAPPING.put("M-G-P_022_001_001", "FLOAT");
		MAPPING.put("ESLIM_006_001_035", "FLOAT");
		MAPPING = Collections.unmodifiableMap(MAPPING);

		ObservationType observationType = null;
		String datatype = parameter.dataType;
		
		if (MAPPING.containsKey(parameter.parameterStableKey)) {
			datatype = MAPPING.get(parameter.parameterStableId);
		}

		if (parameter.metadata) {

			observationType = ObservationType.metadata;

		} else {

			if (parameter.options) {

				observationType = ObservationType.categorical;

			} else {

				if (datatype.equals("TEXT")) {

					observationType = ObservationType.text;

				} else if (datatype.equals("DATETIME") || datatype.equals("DATE")  || datatype.equals("TIME")) {

					observationType = ObservationType.datetime;

				} else if (datatype.equals("BOOL")) {

					observationType = ObservationType.categorical;

				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

					if (parameter.increment) {

						observationType = ObservationType.time_series;

					} else {

						observationType = ObservationType.unidimensional;

					}

				} else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.parameterName.contains("images"))) {

					observationType = ObservationType.image_record;

				} else if (datatype.equals("") && !parameter.options && !parameter.parameterName.contains("images")) {

					/* Look up in observation core. If we have a value the observation type will be correct. 
					 * If not use the approximation below (categorical will always be missed).
					 * See declaration of checkType(param, value) in impress utils.
					 */					
					ObservationType obs = os.getObservationTypeForParameterStableId(parameter.parameterStableId);
					if (obs != null){
						observationType = obs;
					} else {			
						if (parameter.increment) {
							observationType = ObservationType.time_series;
						} else {
							observationType = ObservationType.unidimensional;
						}
					}

				} else {
					logger.warn("UNKNOWN data type : " + datatype  + " " + parameter.parameterStableId);
				}
			}
		}

		return observationType;
	}

	public class ProcedureDTO {

		boolean required;
		int procedureId;
		int procedureStableKey;
		String procedureStableId;
		String procNameId;
		String procedureName;
		String observationType;	
		String description;

	}

	public class PipelineBean {

		int pipelineStableKey;
		int pipelineId;
		String procedureStableId;
		String pipelineName;
		String pipelineStableId;
		String pipeProcSid;
		
	}
	
	public class ParameterDTO {

		int parameterStableKey;
		int parameterId;
		String parameterName;
		String parameterStableId;
		String dataType;
		String parameterType;
		ObservationType observationType;
		String abnormalMaId;
		String abnormalMaName;
		String unit;
		boolean increment;
		boolean metadata;
		boolean options;
		boolean derived;
		boolean required;
		boolean media;		
		
		List<String> procedureStableIds;
		List<String> categories = new ArrayList<>();
		
	}
}



