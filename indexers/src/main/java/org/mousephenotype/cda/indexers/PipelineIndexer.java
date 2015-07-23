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

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.ValidationException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.SangerProcedureMapper;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the MA core
 */
public class PipelineIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory
			.getLogger(PipelineIndexer.class);
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
	MaOntologyDAO maOntologyService;
	
	@Autowired
	

	private Map<Integer, Map<String, String>> paramDbIdToParameter = null;
	private Map<Integer, Set<Integer>> procedureIdToParams = null;
	private Map<Integer, ProcedureDTO> procedureIdToProcedure = null;
	private List<PipelineBean> pipelines;
	private Map<String, List<GfMpBean>> pppidsToGfMpBeans;
	private Map<String, List<AlleleDTO>> mgiToAlleleMap;
	private Map<String, MpDTO> mpIdToMp;
	private Map<String, String> parameterStableIdToAbnormalMaMap;
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
		procedureIdToParams = populateParamIdToProcedureIdListMap();
		procedureIdToProcedure = populateProcedureIdToProcedureMap();
		pipelines = populateProcedureIdToPipelineMap();
		parameterStableIdToAbnormalMaMap=populateParameterStableIdToAbnormalOntologyMap();
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

				Set<Integer> parameterIds = procedureIdToParams
						.get(pipeline.pipelineId);

				for (int paramDbId : parameterIds) {
					PipelineDTO pipe = new PipelineDTO();// new pipe object for
															// each param
					Map<String, String> row = paramDbIdToParameter.get(paramDbId);
					pipe.setParameterId(paramDbId);
					pipe.setParameterName(row.get(ObservationDTO.PARAMETER_NAME));
					String paramStableId = row.get(ObservationDTO.PARAMETER_STABLE_ID);
					String paramStableName = row.get(ObservationDTO.PARAMETER_NAME);
					pipe.setParameterStableId(paramStableId);
					if(parameterStableIdToAbnormalMaMap.containsKey(paramStableId)){
						pipe.setAbnormalMaTermId(parameterStableIdToAbnormalMaMap.get(paramStableId));
						OntologyTermBean term = maOntologyService.getTerm(parameterStableIdToAbnormalMaMap.get(paramStableId));
						pipe.setAbnormalMaName(term.getName());
					}

					pipe.setParameterStableKey(row.get("stable_key"));
					ProcedureDTO procBean = procedureIdToProcedure.get(pipeline.procedureId);
					pipe.setProcedureId(pipeline.procedureId);
					pipe.setProcedureName(procBean.procedureName);
					pipe.setProcedureStableId(procBean.procedureStableId);
					pipe.setProcedureStableKey(procBean.procedureStableKey);
					pipe.addProcedureNameId(procBean.procNameId);
					pipe.addMappedProcedureName(SangerProcedureMapper.getImpcProcedureFromSanger(procBean.procedureName));

					String procParamStableId = procBean.procedureStableId + "___" + paramStableId;
					String procParamName = procBean.procedureName + "___" + paramStableName;
					pipe.addProcParamStableId(procParamStableId);
					pipe.addProcParamName(procParamName);
					// add the pipeline info here
					pipe.setPipelineId(pipeline.pipelineId);
					pipe.setPipelineName(pipeline.pipelineName);
					pipe.setPipelineStableId(pipeline.pipelineStableId);
					pipe.setPipelineStableKey(pipeline.pipelineStableKey);
					pipe.addPipeProcId(pipeline.pipeProcSid);

					//changed the ididid to be pipe proc param stable id combination that should be unique and is unique in solr
					String ididid = pipeline.pipelineStableId + "_" + procBean.procedureStableId + "_" + paramStableId;
					String idididKey = paramDbId + "_" + pipeline.pipeProcSid + "_" + pipe.getPipelineId();
					pipe.setIdIdId(ididid);
					if (pppidsToGfMpBeans.containsKey(idididKey)) {
						List<GfMpBean> gfMpBeanList = pppidsToGfMpBeans.get(idididKey);
						for (GfMpBean gfMpBean : gfMpBeanList) {
							String mgiAccession = gfMpBean.gfAcc;
							pipe.addMgiAccession(mgiAccession);
							if (mgiToAlleleMap.containsKey(mgiAccession)) {
								List<AlleleDTO> alleles = mgiToAlleleMap.get(mgiAccession);
								for (AlleleDTO allele : alleles) {
									if (allele.getMarkerSymbol() != null) {
										pipe.addMarkerType(allele.getMarkerType());
										pipe.addMarkerSymbol(allele.getMarkerSymbol());
										if (allele.getMarkerSynonym() != null) {
											pipe.addMarkerSynonym(allele.getMarkerSynonym());
										}
									}

									pipe.addMarkerName(allele.getMarkerName());
									if (allele.getHumanGeneSymbol() != null) {
										pipe.addHumanGeneSymbol(allele.getHumanGeneSymbol());
									}
									// /> <!-- status name from Bill
									// Skarnes and used at EBI -->
									pipe.addStatus(allele.getStatus());
									pipe.addImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
									pipe.addImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
									pipe.addImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
									if (allele.getLatestProductionCentre() != null) {
										pipe.addLatestProductionCentre(allele.getLatestProductionCentre());
									}
									if (allele.getLatestPhenotypingCentre() != null) {
										pipe.addLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
									}
									pipe.addLatestPhenotypingCentre(allele.getLatestPhenotypeStatus());
									pipe.addLegacyPhenotypingStatus(allele.getLatestPhenotypeStatus());
									pipe.addAlleleName(allele.getAlleleName());
								}
							}
							// mps for parameter

							String mpTermId = gfMpBean.mpAcc;
							MpDTO mp = mpIdToMp.get(mpTermId);
							pipe.addMpId(mpTermId);

							if (mp != null) {

								pipe.addMpTerm(mp.getMpTerm());

								if (mp.getMpTermSynonym() != null) {
									pipe.addMpTermSynonym(mp.getMpTermSynonym());
								}

								if (mp.getOntologySubset() != null) {
									pipe.addOntologySubset(mp.getOntologySubset());
								}
								if (mp.getTopLevelMpTermId() != null) {
									pipe.addTopLevelMpId(mp.getTopLevelMpTermId());
								} else {
									logger.warn("topLevelMpTermId for mpTerm " + mpTermId + " is null!");
								}
								if (mp.getTopLevelMpTerm() != null) {
									pipe.addTopLevelMpTerm(mp.getTopLevelMpTerm());
								} else {
									logger.warn("topLevelMpTerm for mpTerm "
											+ mpTermId + " is null!");
								}
								if (mp.getTopLevelMpTermSynonym() != null) {
									pipe.addTopLevelMpTermSynonym(mp.getTopLevelMpTermSynonym());
								}
								if (mp.getIntermediateMpId() != null) {
									pipe.addIntermediateMpId(mp.getIntermediateMpId());
								}
								if (mp.getIntermediateMpTerm() != null) {
									pipe.addIntermediateMpTerm(mp.getIntermediateMpTerm());
								}
								if (mp.getIntermediateMpTermSynonym() != null) {
									pipe.addIntermediateMpTermSynonym(mp.getIntermediateMpTermSynonym());
								}
								if (mp.getChildMpId() != null) {
									pipe.addChildMpId(mp.getChildMpId());
									pipe.addChildMpTerm(mp.getChildMpTerm());
								}
								if (mp.getChildMpTermSynonym() != null) {
									pipe.addChildMpTermSynonym(mp.getChildMpTermSynonym());
								}
								if (mp.getHpId() != null) {
									pipe.addHpId(mp.getHpId());
								}

								if (mp.getHpTerm() != null) {
									pipe.addHpTerm(mp.getHpTerm());
								}
								if (mp.getInferredMaId() != null) {
									pipe.addInferredMaId(mp.getInferredMaId());
									pipe.addInferredMaTerm(mp.getInferredMaTerm());
									if (mp.getInferredMaTermSynonym() != null) {
										pipe.addInferredMaTermSynonym(mp.getInferredMaTermSynonym());
									}
								}
								if (mp.getInferredSelectedTopLevelMaId() != null) {
									pipe.addInferredSelectedTopLevelMaId(mp.getInferredSelectedTopLevelMaId());
									if (mp.getInferredSelectedTopLevelMaTerm() != null) {
										pipe.addInferredSelectedTopLevelMaTerm(mp.getInferredSelectedTopLevelMaTerm());
									}
									if (mp.getInferredSelectedTopLevelMaTermSynonym() != null) {
										pipe.addInferredSelectedToLevelMaTermSynonym(mp.getInferredSelectedTopLevelMaTermSynonym());
									}

								}
								if (mp.getInferredChildMaId() != null) {
									pipe.addInferredChildMaId(mp.getInferredChildMaId());
									pipe.addInferredChildMaTerm(mp.getInferredChildMaTerm());
									if (mp.getInferredChildMaTermSynonym() != null) {
										pipe.addInferredChildMaTermSynonyms(mp.getInferredChildMaTermSynonym());
									}
								}
							}

						}
					}
					documentCount++;
					pipelineCore.addBean(pipe);
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

	
	private Map<Integer, Map<String, String>> populateParamDbIdToParametersMap() {

		logger.info("populating PCS pipeline info");
		Map<Integer, Map<String, String>> localParamDbIdToParameter = new HashMap<>();
		String queryString = "select id, stable_id, name, stable_key from phenotype_parameter";
		//SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=8 LIMIT 100;
		
		try (PreparedStatement p = komp2DbConnection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Map<String, String> rowMap = new HashMap<>();
				// store the row in a map of column names to values
				int id = resultSet.getInt("id");
				rowMap.put(ObservationDTO.PARAMETER_NAME, resultSet.getString("name"));
				rowMap.put(ObservationDTO.PARAMETER_STABLE_ID, resultSet.getString("stable_id"));
				rowMap.put("stable_key", resultSet.getString("stable_key"));

				localParamDbIdToParameter.put(id, rowMap);
			}
			System.out.println("phenotype parameter should have 5704+ entries and has "	+ localParamDbIdToParameter.size() + " entries");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return localParamDbIdToParameter;

	}

	private Map<Integer, Set<Integer>> populateParamIdToProcedureIdListMap() {

		logger.info("populating param To ProcedureId info");
		Map<Integer, Set<Integer>> procIdToParams = new HashMap<>();
		String queryString = "select procedure_id, parameter_id from phenotype_procedure_parameter";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				Set<Integer> parameterIds = new HashSet<>();// store the row in
				// a map of column
				// names to values
				int paramId = resultSet.getInt("parameter_id");
				int procId = resultSet.getInt("procedure_id");
				if (procIdToParams.containsKey(procId)) {
					parameterIds = procIdToParams.get(procId);
				} else {
					parameterIds = new HashSet<>();// store the row in a map of
					// column names to values
				}
				parameterIds.add(paramId);
				procIdToParams.put(procId, parameterIds);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("should be 5704+ entries " + procIdToParams.size());
		return procIdToParams;
	}

	private Map<Integer, ProcedureDTO> populateProcedureIdToProcedureMap() {

		logger.info("populating procedureId to Procedure Map info");
		Map<Integer, ProcedureDTO> procedureIdToProcedureMap = new HashMap<>();
		String queryString = "select id as pproc_id, stable_id, name, stable_key, is_mandatory, desciption, concat(name, '___', stable_id) as proc_name_id from phenotype_procedure";

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
				proc.description = resultSet.getString("desciption");
				procedureIdToProcedureMap.put(resultSet.getInt("pproc_id"), proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("187+ the procedureIdToProcedureMap size="
				+ procedureIdToProcedureMap.size());
		return procedureIdToProcedureMap;
	}


	// select pproc.id as pproc_id, ppipe.name as pipe_name, ppipe.id as
	// pipe_id, ppipe.stable_id as pipe_stable_id, ppipe.stable_key as
	// pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___',
	// pproc.stable_id) as pipe_proc_sid from phenotype_procedure pproc inner
	// join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id
	// inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id where
	// ppipe.db_id=6
	private List<PipelineBean> populateProcedureIdToPipelineMap() {

		logger.info("populating procedureId to  pipeline Map info");
		List<PipelineBean> procIdToPipelineMap = new ArrayList<>();
		String queryString = "select pproc.id as pproc_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, ppipe.stable_key as pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) as pipe_proc_sid from phenotype_procedure pproc inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id where ppipe.db_id=6";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				PipelineBean pipe = new PipelineBean();

				int procedureId = resultSet.getInt("pproc_id");
				String pipeName = resultSet.getString("pipe_name");
				int pipeId = resultSet.getInt("pipe_id");
				String pipeStableId = resultSet.getString("pipe_stable_id");
				int pipeStableKey = resultSet.getInt("pipe_stable_key");
				String pipeProcSid = resultSet.getString("pipe_proc_sid");
				pipe.pipelineId = pipeId;
				pipe.pipelineName = pipeName;
				pipe.pipelineStableKey = pipeStableKey;
				pipe.pipelineStableId = pipeStableId;
				pipe.pipeProcSid = pipeProcSid;
				pipe.procedureId = procedureId;
				procIdToPipelineMap.add(pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return procIdToPipelineMap;
		
	}

	
	private Map<String,String> populateParameterStableIdToAbnormalOntologyMap(){
		Map<String,String> parameterStableIdToOntology=new HashMap<>();
		String sqlQuery="SELECT stable_id, ontology_acc FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=8 LIMIT 10000";
		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(sqlQuery)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String parameterId = resultSet.getString("stable_id");
				String maId = resultSet.getString("ontology_acc");
				parameterStableIdToOntology.put(parameterId,maId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("547+ should be and is in procIdToPipelineMap " + parameterStableIdToOntology.size());


		return parameterStableIdToOntology;
	}


	private Map<String, List<GfMpBean>> populateGfAccAndMp() {
		logger.info("populating GfAcc and Mp info - started");
		Map<String, List<GfMpBean>> gfMpBeansMap = new HashMap<>();
		String queryString = "select distinct concat(s.parameter_id,'_',s.procedure_id,'_',s.pipeline_id) as pppIds, s.gf_acc, s.mp_acc, s.parameter_id as pp_parameter_id, s.procedure_id as pproc_procedure_id, s.pipeline_id as ppipe_pipeline_id, s.allele_acc, s.strain_acc from phenotype_parameter pp INNER JOIN phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id INNER JOIN phenotype_procedure pproc on ppp.procedure_id=pproc.id INNER JOIN phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id INNER JOIN phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id inner join phenotype_call_summary s on ppipe.id=s.pipeline_id and pproc.id=s.procedure_id and pp.id=s.parameter_id";

		try (PreparedStatement p = komp2DbConnection
				.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				GfMpBean gfMpBean = new GfMpBean();

				String pppids = resultSet.getString("pppids");
				String gfAcc = resultSet.getString("gf_acc");
				String mpAcc = resultSet.getString("mp_acc");
				// String alleleAcc=resultSet.getString("allele_acc");//doesn't
				// look like these are needed?
				// String strainAcc=resultSet.getString("strain_acc");
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

	private Map<String, MpDTO> populateMpIdToMp() throws IndexerException {

		Map<String, MpDTO> map = null;
		try {
			map = SolrUtils.populateMpTermIdToMp(mpCore);
		} catch (SolrServerException e) {
			throw new IndexerException("Unable to query phenodigm_core in SolrUtils.populateMpTermIdToMp()", e);
		}
		return map;
	}

	public static void main(String[] args) throws IndexerException {

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
	 */
	// Method copied from org.mousephenotype.cda.db.impress.Utilities.
	// Adjusted to avoid use of Parameter dao obj.
	public ObservationType checkType(ParameterDTO parameter, String value) {

		Map<String, String> MAPPING = new HashMap<>();
		MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
		MAPPING.put("M-G-P_022_001_001", "FLOAT");
		MAPPING.put("ESLIM_006_001_035", "FLOAT");
		MAPPING = Collections.unmodifiableMap(MAPPING);

		ObservationType observationType = null;

		Float valueToInsert = 0.0f;

		String datatype = parameter.dataType;
		if (MAPPING.containsKey(parameter.parameterStableKey)) {
			datatype = MAPPING.get(parameter.parameterStableId);
		}

		if (parameter.isMetadata) {

			observationType = ObservationType.metadata;

		} else {

			if (parameter.isOptions) {

				observationType = ObservationType.categorical;

			} else {

				if (datatype.equals("TEXT")) {

					observationType = ObservationType.text;

				} else if (datatype.equals("DATETIME")) {

					observationType = ObservationType.datetime;

				} else if (datatype.equals("BOOL")) {

					observationType = ObservationType.categorical;

				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

					if (parameter.isIncrement) {

						observationType = ObservationType.time_series;

					} else {

						observationType = ObservationType.unidimensional;

					}

					try {
						if (value != null) {
							valueToInsert = Float.valueOf(value);
						}
					} catch (NumberFormatException ex) {
						logger.debug("Invalid float value: " + value);
						//TODO probably should throw an exception!
					}

				} else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.parameterName.contains("images"))) {

					observationType = ObservationType.image_record;

				} else if (datatype.equals("") && !parameter.isOptions && !parameter.parameterName.contains("images")) {

					// is that a number or a category?
					try {
						// check whether it's null
						if (value != null && !value.equals("null")) {

							valueToInsert = Float.valueOf(value);
						}
						if (parameter.isIncrement) {
							observationType = ObservationType.time_series;
						} else {
							observationType = ObservationType.unidimensional;
						}

					} catch (NumberFormatException ex) {
						observationType = ObservationType.categorical;
					}
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
		int procedureId;
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
		String observationType;
		boolean isIncrement;
		boolean isMetadata;
		boolean isOptions;
		
	}
}
