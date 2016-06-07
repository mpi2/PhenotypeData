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
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@EnableAutoConfiguration
public class PipelineIndexer extends AbstractIndexer implements CommandLineRunner {
    private CommonUtils commonUtils = new CommonUtils();

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
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


	public static void main(String[] args) throws IndexerException {
		SpringApplication.run(PipelineIndexer.class, args);
	}


	@Override
	public RunStatus validateBuild()	throws IndexerException {
		return super.validateBuild(pipelineCore);
	}

	@Override
	public void initialise(String[] args)
	throws IndexerException {

		super.initialise(args);

		try {
			this.komp2DbConnection = komp2DataSource.getConnection();
		} catch (SQLException sqle) {
			throw new IndexerException(sqle);
		}
	}

	private void initialiseSupportingBeans(RunStatus runStatus)
	throws IndexerException {

		parameterToObservationTypeMap = getObservationTypeMap(runStatus);
		paramIdToParameter = populateParamIdToParameterMap(runStatus);
		procedureIdToProcedure = populateProcedureIdToProcedureMap(runStatus);
		pipelines = populatePipelineList();
		addAbnormalMaOntology();
		addAbnormalEmapOntology();
		mpIdToMp = populateMpIdToMp();
	}

	@Override
	public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
		try {
			run("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void run(String... strings) throws Exception {
        documentCount = 0;
        Set<String> noTermSet = new HashSet<>();
        RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try {
			initialiseSupportingBeans(runStatus);
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
						doc.setAnnotate(param.isAnnotate());

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
								if (mp == null) {
									noTermSet.add(pipeline.getName() + ":" + procedure.getName() + ":" + mpId);
									continue;
								}

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
							doc.setAbnormalMpId(new ArrayList<String>(param.getAbnormalMpId()));
							for (String mpId: param.getAbnormalMpId()){
								doc.addAbnormalMpTerm(mpIdToMp.get(mpId).getMpTerm());
							}
						}
						if (param.getIncreasedMpId() != null){
							doc.setIncreasedMpId(new ArrayList<String>(param.getIncreasedMpId()));
							for(String mpId: param.getIncreasedMpId()){
								doc.addIncreasedMpTerm(mpIdToMp.get(mpId).getMpTerm());
							}
						}
						if (param.getDecreasedMpId()!= null){
							doc.setDecreasedMpId(new ArrayList<String>(param.getDecreasedMpId()));
							for(String mpId: param.getDecreasedMpId()){
								if (mpIdToMp.get(mpId)!=null) {
									doc.addDecreasedMpTerm(mpIdToMp.get(mpId).getMpTerm());
								} else {
									logger.warn("Cannot find MP term for MP ID {}", mpId);
								}
							}
						}

						if (doc.getProcedureId() == null){
							System.out.println(doc.getIdidid() + "  " + doc);
						}
						if(param.getEmapId()!=null){
							doc.setEmapId(param.getEmapId());
							if(param.getEmapName()!=null){
								doc.setEmapTerm(param.getEmapName());
							}
						}
						pipelineCore.addBean(doc);
						documentCount++;
					}
				}
			}

			List<String> noTermList = new ArrayList<>(noTermSet);
			Collections.sort(noTermList);
			for (String mpId : noTermList) {
                runStatus.addWarning( "No mp term for " + mpId);
			}

			pipelineCore.commit();

		} catch (IOException | SolrServerException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

        if (runStatus.hasWarnings()) {
            runStatus.addWarning("No mp term COUNT: " + noTermSet.size());
        }

        logger.info(" Added {} total beans in {}", documentCount, commonUtils.msToHms(System.currentTimeMillis() - start));

	}

    /**
     * Populate ParamDbIdToParameter
     *
     * @param runStatus instance to which warnings and errors are added
     *
     * @return ParamDbIdToParameter map
     */
	protected Map<String, ParameterDTO> populateParamIdToParameterMap(RunStatus runStatus) {

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
				param.setAnnotate(resultSet.getBoolean("annotate"));
				param.setObservationType(assignType(param, runStatus));
				if (param.getObservationType() == null){
                    runStatus.addWarning(" Observation type is NULL for :" + param.getStableId() + "  " + param.getObservationType());
				}
				localParamDbIdToParameter.put(id, param);
			}

            if (localParamDbIdToParameter.size() < 5704) {
                runStatus.addWarning(" localParamDbIdToParameter # records = " + localParamDbIdToParameter.size() + ". Expected at least 5704 records.");
            }

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
				if (param == null || !param.getStableId().equalsIgnoreCase(paramId)) {
					param = stableIdToParameter.get(paramId);
				}

				String type = resultSet.getString("event_type");
				String mpId = resultSet.getString("ontology_acc");
				if (type == null) {
					param.addMpIds(mpId);
				} else if (type.equalsIgnoreCase("abnormal") && (param.getAbnormalMpId() == null || !param.getAbnormalMpId().contains(mpId))){
					param.addAbnormalMpId(mpId);
				} else if(type.equalsIgnoreCase("increased") && (param.getIncreasedMpId() == null ||!param.getIncreasedMpId().contains(mpId))){
					param.addIncreasedMpId(mpId);
				} else if (type.equalsIgnoreCase("decreased") && (param.getDecreasedMpId() == null || !param.getDecreasedMpId().contains(mpId))){
					param.addDecreasedMpId(mpId);
				}

				param.addMpIds(resultSet.getString("ontology_acc"));
				localIdToParameter.put(param.getStableId(), param);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return localIdToParameter;

	}


	protected Map<String, Set<String>> populateProcedureToParameterMap(RunStatus runStatus) {

		Map<String, Set<String>> procIdToParams = new HashMap<>();

        final int MIN_ROW_COUNT = 200;     // This is the minumum number of unique procedures produced by this query on 14-Jan-2016, rounded down (GROUP BY procedure_id)
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

        if (procIdToParams.size() < MIN_ROW_COUNT) {
            runStatus.addWarning(" procIdToParams # records = " + procIdToParams.size() + ". Expected at least " + MIN_ROW_COUNT + " records.");
        }

		return procIdToParams;
	}


	protected Map<String, ProcedureDTO> populateProcedureIdToProcedureMap(RunStatus runStatus) {

		Map<String, Set<String>> procIdToParams = populateProcedureToParameterMap(runStatus);

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

        if (procedureIdToProcedureMap.size() < 190) {
            runStatus.addWarning(" procedureIdToProcedureMap # records = " + procedureIdToProcedureMap.size() + ". Expected at least 190 records.");
        }

		return procedureIdToProcedureMap;
	}


	protected Map<String, PipelineDTO> populatePipelineList() {

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


	protected void addAbnormalMaOntology(){

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

protected void addAbnormalEmapOntology(){

		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=14";
		//14 db id is emap
		try (PreparedStatement p = komp2DbConnection.prepareStatement(sqlQuery)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("stable_id");
				paramIdToParameter.get(parameterId).setEmapId(resultSet.getString("ontology_acc"));
				paramIdToParameter.get(parameterId).setEmapName(resultSet.getString("name"));
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
     * @param runStatus a valid <code>RunStatus</code> instance
	 * @return
	 * @throws SolrServerException
	 */
	// Method copied from org.mousephenotype.cda.db.impress.Utilities.
	// Adjusted to avoid use of Parameter dao obj.
	// Method should only be used at indexing time. After that query pipeline core to find type.
	protected ObservationType assignType(ParameterDTO parameter, RunStatus runStatus)
	throws SolrServerException {

		Map<String, String> MAPPING = new HashMap<>();
		MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
		MAPPING.put("M-G-P_022_001_001", "FLOAT");
		MAPPING.put("ESLIM_006_001_035", "FLOAT");
		MAPPING = Collections.unmodifiableMap(MAPPING);

		ObservationType observationType = null;
		String datatype = parameter.getDataType();

		if (MAPPING.containsKey(parameter.getStableId())) {
			datatype = MAPPING.get(parameter.getStableId());
		}

		if (parameter.isMetadata()) {

			observationType = ObservationType.metadata;

		} else {

			if (parameter.isOptions()) {

				observationType = ObservationType.categorical;

			} else {

				if (datatype.equals("TEXT")) {

					observationType = ObservationType.text;

				} else if (datatype.equals("DATETIME") || datatype.equals("DATE")  || datatype.equals("TIME")) {

					observationType = ObservationType.datetime;

				} else if (datatype.equals("BOOL")) {

					observationType = ObservationType.categorical;

				} else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

					if (parameter.isIncrement()) {

						observationType = ObservationType.time_series;

					} else {

						observationType = ObservationType.unidimensional;

					}

				} else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.getName().contains("images"))) {

					observationType = ObservationType.image_record;

				} else if (datatype.equals("") && !parameter.isOptions() && !parameter.getName().contains("images")) {

					/* Look up in observation core. If we have a value the observation type will be correct.
					 * If not use the approximation below (categorical will always be missed).
					 * See declaration of checkType(param, value) in impress utils.
					 */
					// Do not use the Service here because it has likely not been loaded yet (or is pointing to the wrong context
					// ObservationType obs = os.getObservationTypeForParameterStableId(parameter.getStableId());
//					logger.info("Getting type from observations for parameter {}: Got {}", parameter.getStableId(), parameterToObservationTypeMap.get(parameter.getStableId()));
					ObservationType obs = parameterToObservationTypeMap.get(parameter.getStableId());
					if (obs != null){
						observationType = obs;
					} else {
						if (parameter.isIncrement()) {
							observationType = ObservationType.time_series;
						} else {
							observationType = ObservationType.unidimensional;
						}
					}

				} else {
					runStatus.addWarning(" Unknown data type : " + datatype  + " " + parameter.getStableId());
				}
			}
		}

		return observationType;
	}

	private Map<String,ObservationType> getObservationTypeMap(RunStatus runStatus){
		Map<String,ObservationType> map = new HashMap<>();
		String query= "select distinct parameter_stable_id, observation_type from observation";

		try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				String parameterId = resultSet.getString("parameter_stable_id");
				String obsType=resultSet.getString("observation_type");

				ObservationType obType;
				try {
					obType = ObservationType.valueOf(obsType);
					map.put(parameterId, obType);
				} catch (IllegalArgumentException e) {
					runStatus.addWarning(" No ObservationType found for parameter: " + parameterId);
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}
}
