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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.PipelineDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@EnableAutoConfiguration
public class PipelineIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String, String>          emapToEmapa;
	private Map<String, ObservationType> parameterToObservationTypeMap;
	private Map<String, ParameterDTO>    paramIdToParameter;
	private Map<String, PipelineDTO>     pipelines;
	private Map<String, ProcedureDTO>    procedureIdToProcedure;

	private OntologyParser        emapaParser;
	private OntologyParser        mpParser;
	private OntologyParserFactory ontologyParserFactory;

	private SolrClient pipelineCore;

	protected PipelineIndexer() {

	}

	@Inject
	public PipelineIndexer(
			@NotNull DataSource komp2DataSource,
			@NotNull OntologyTermRepository ontologyTermRepository,
			@NotNull SolrClient pipelineCore)
	{
		super(komp2DataSource, ontologyTermRepository);
		this.pipelineCore = pipelineCore;
	}

	@Override
	public RunStatus validateBuild()	throws IndexerException {
		return super.validateBuild(pipelineCore);
	}

	public static void main(String[] args) {

		ConfigurableApplicationContext context = new SpringApplicationBuilder(PipelineIndexer.class)
				.web(WebApplicationType.NONE)
				.bannerMode(Banner.Mode.OFF)
				.logStartupInfo(false)
				.run(args);

		context.close();
	}


	private void initialiseSupportingBeans(Connection connection, RunStatus runStatus)
			throws IndexerException{

		try {
			ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
			mpParser = ontologyParserFactory.getMpParser();
			emapaParser = ontologyParserFactory.getEmapaParser();
			emapToEmapa = ontologyParserFactory.getEmapToEmapaMap();
			parameterToObservationTypeMap = getObservationTypeMap(connection, runStatus);
			paramIdToParameter = populateParamIdToParameterMap(connection, runStatus);
			addUnits(connection);
			procedureIdToProcedure = populateProcedureIdToProcedureMap(connection, runStatus);
			pipelines = populatePipelineList(connection);
			addAbnormalMaOntology(connection);
			addAbnormalEmapOntology(connection);
		} catch (SQLException | OWLOntologyCreationException | OWLOntologyStorageException | IOException e){
			throw new IndexerException(e);
		}
	}


	@Override
	public RunStatus run() throws IndexerException, SQLException {

		Set<MissingMpId> missingMpIds = new HashSet<>();
		RunStatus        runStatus    = new RunStatus();
		long             start        = System.currentTimeMillis();

		expectedDocumentCount = 0;

		try (Connection connection = komp2DataSource.getConnection()) {

			initialiseSupportingBeans(connection, runStatus);
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
						doc.setStage(procedure.getStage());
						doc.setStageLabel(procedure.getStageLabel());

						//doc.setDescription(procBean.description); -> maybe we don't need this. If we do, should differentiate from parameter description.
						doc.setObservationType(param.getObservationType().name());
						if (param.getUnitX() != null){
							doc.setUnitX(param.getUnitX());
						}
						if (param.getUnitY() != null){
							doc.setUnitY(param.getUnitY());
						}
						doc.setMetadata(param.isMetadata());
						doc.setIncrement(param.isIncrement());
						doc.setHasOptions(param.isOptions());
						doc.setDerived(param.isDerived());
						doc.setMedia(param.isMedia());
						doc.setAnnotate(param.isAnnotate());
						doc.setDataType(param.getDataType());

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
								OntologyTermDTO mp = mpParser.getOntologyTerm(mpId);
								if (mp == null) {
									missingMpIds.add(new MissingMpId(pipeline.getStableId(), procedure.getStableId(), param.getStableId(), mpId));
									continue;
								}

								doc.addMpTerm(mp.getName());
								if (mp.getIntermediateIds() != null && mp.getIntermediateIds().size() > 0){
									doc.addIntermediateMpId(mp.getIntermediateIds());
									doc.addIntermediateMpTerm(mp.getIntermediateNames());
								}
								if (mp.getTopLevelIds() != null && mp.getTopLevelIds().size() > 0){
									doc.addTopLevelMpId(mp.getTopLevelIds());
									doc.addTopLevelMpTerm(mp.getTopLevelNames());
								}
							}
						}

						if (param.getAbnormalMpId() != null){
							doc.setAbnormalMpId(new ArrayList<>(param.getAbnormalMpId()));
							for (String mpId: param.getAbnormalMpId()){
								try {
									doc.addAbnormalMpTerm(mpParser.getOntologyTerm(mpId).getName());
								} catch (NullPointerException e) {
									missingMpIds.add(new MissingMpId(pipeline.getStableId(), procedure.getStableId(), param.getStableId(), mpId));
								}
							}
						}
						if (param.getIncreasedMpId() != null){
							doc.setIncreasedMpId(new ArrayList<>(param.getIncreasedMpId()));
							for(String mpId: param.getIncreasedMpId()){
								try {
									doc.addIncreasedMpTerm(mpParser.getOntologyTerm(mpId).getName());
								} catch (NullPointerException e) {
									missingMpIds.add(new MissingMpId(pipeline.getStableId(), procedure.getStableId(), param.getStableId(), mpId));
								}
							}
						}
						if (param.getDecreasedMpId()!= null){
							doc.setDecreasedMpId(new ArrayList<>(param.getDecreasedMpId()));
							for(String mpId: param.getDecreasedMpId()){
								if (mpParser.getOntologyTerm(mpId) != null) {
									try {
										doc.addDecreasedMpTerm(mpParser.getOntologyTerm(mpId).getName());
									} catch (NullPointerException e) {
										missingMpIds.add(new MissingMpId(pipeline.getStableId(), procedure.getStableId(), param.getStableId(), mpId));
									}
								} else {
									missingMpIds.add(new MissingMpId(pipeline.getStableId(), procedure.getStableId(), param.getStableId(), mpId));
								}
							}
						}

						if (doc.getProcedureId() == null){
							System.out.println(doc.getIdidid() + "  " + doc);
						}

						if(param.getEmapId()!=null){

							String emapId = param.getEmapId();
							doc.setEmapId(emapId);

							if ( emapToEmapa.containsKey(emapId)) {
								try {
									OntologyTermDTO emapaTerm = emapaParser.getOntologyTerm(emapToEmapa.get(emapId));
									doc.setAnatomyId(emapaTerm.getAccessionId());
									doc.setAnatomyTerm(emapaTerm.getName());
								} catch (NullPointerException e) {
									logger.warn(" EMAP Id {} is not mapped to an EMAPA Id, but emapToEmapa.containsKey(emapId) is true", emapId);

								}
							}
							else {
								logger.debug(" EMAP Id {} is not mapped to an EMAPA Id", emapId);
							}
						}
						pipelineCore.addBean(doc);
						expectedDocumentCount++;
					}
				}
			}

			/*
			 * MWT terms started appearing in Impress before DR11.0 They aren't mp terms, they aren't part of any
			 * ontology, and we (the CDA) do not use them, so technically they are not 'missing'. Remove them.
			 * It is OK to add them to the mp terms so that any subsequent references to them don't break our code.
			 */
			missingMpIds = missingMpIds
					.stream()
					.filter(missing -> ! missing.toString().startsWith("MWT:"))
					.collect(Collectors.toSet());

			// Log the missing mp terms
			if ( ! missingMpIds.isEmpty()) {
				System.out.println("Missing " + missingMpIds.size() + " mp terms: (mpTermId::pipelineStableId::procedureStableId::parameterStableId)");
				missingMpIds
						.stream()
						.sorted(Comparator
								.comparing((MissingMpId missingMpId) -> missingMpId.mpId)
								.thenComparing(missingMpId -> missingMpId.pipelineId)
								.thenComparing(missingMpId -> missingMpId.procedureId)
								.thenComparing(missingMpId -> missingMpId.parameterId))
						.map(missingMpID -> {
							System.out.println(missingMpID.toString());
							return missingMpID;
						})
						.collect(Collectors.toSet());
			}

			pipelineCore.commit();

		} catch (IOException | SolrServerException | NullPointerException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}

        logger.info(" Added {} total beans in {}", expectedDocumentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
		return runStatus;
	}

    /**
     * Populate ParamDbIdToParameter
     *
     * @param runStatus instance to which warnings and errors are added
     *
     * @return ParamDbIdToParameter map
     */
	protected Map<String, ParameterDTO> populateParamIdToParameterMap(Connection connection, RunStatus runStatus) {

		Map<String, ParameterDTO> localParamDbIdToParameter = new HashMap<>();
		String queryString = "SELECT * FROM phenotype_parameter";

		try (PreparedStatement p = connection.prepareStatement(queryString)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ParameterDTO param = new ParameterDTO();
				// store the row in a map of column names to values
				String id = resultSet.getString("stable_id");
				param.setName(resultSet.getString("name"));
				param.setId(resultSet.getLong("id"));
				param.setStableId(resultSet.getString("stable_id"));
				param.setStableKey(resultSet.getLong("stable_key"));
				param.setDataType(resultSet.getString("datatype"));
				param.setParameterType(resultSet.getString("parameter_type"));
				param.setMetadata(resultSet.getBoolean("metadata"));
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

            if (localParamDbIdToParameter.size() < 5000) {
                runStatus.addWarning(" localParamDbIdToParameter # records = " + localParamDbIdToParameter.size() + ". Expected at least 5000 records.");
            }

		} catch (Exception e) {
			e.printStackTrace();
		}

		localParamDbIdToParameter = addCategories(connection, localParamDbIdToParameter);
		localParamDbIdToParameter = addMpTerms(connection, localParamDbIdToParameter);
		return localParamDbIdToParameter;

	}

	private void addUnits(Connection connection) {

		String query = "SELECT * FROM phenotype_parameter pp LEFT OUTER JOIN phenotype_parameter_lnk_increment ppli on pp.id = ppli.parameter_id " +
				"LEFT OUTER JOIN phenotype_parameter_increment ppi ON ppli.increment_id = ppi.id ORDER BY pp.stable_id; ";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ParameterDTO param = paramIdToParameter.get(resultSet.getString("stable_id"));
				if (resultSet.getString("increment_unit") != null) {
					param.setUnitX(resultSet.getString("increment_unit"));
					param.setUnitY(resultSet.getString("unit"));
				} else {
					param.setUnitX(resultSet.getString("unit"));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @since 2015/07/27
	 * @author tudose
	 * @param stableIdToParameter
	 * @return
	 */
	protected Map<String, ParameterDTO> addCategories(Connection connection, Map<String, ParameterDTO> stableIdToParameter){

		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);
		String queryString = "SELECT stable_id, o.name AS cat_name, o.description AS cat_description "
				+ " FROM phenotype_parameter p "
				+ " INNER JOIN phenotype_parameter_lnk_option l ON l.parameter_id=p.id "
				+ " INNER JOIN phenotype_parameter_option o ON o.id=l.option_id "
				+ " ORDER BY stable_id ASC;";

		try (PreparedStatement p = connection.prepareStatement(queryString)) {

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
	protected Map<String, ParameterDTO> addMpTerms(Connection connection, Map<String, ParameterDTO> stableIdToParameter){

		String queryString = "SELECT stable_id, ontology_acc, event_type FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=pp.id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON l.annotation_id=ppoa.id "
				+ " WHERE ontology_db_id=5 "
				+ " ORDER BY stable_id ASC; ";

		Map<String, ParameterDTO> localIdToParameter = new HashMap<>(stableIdToParameter);

		try (PreparedStatement p = connection.prepareStatement(queryString)) {

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

	protected Map<String, Set<String>> populateProcedureToParameterMap(Connection connection, RunStatus runStatus) {

		Map<String, Set<String>> procIdToParams = new HashMap<>();

		// This is the minumum number of unique procedures produced by this query on 14-Jan-2016, rounded down (GROUP BY procedure_id)
		// Harwell Ageing Screen parameters removed 2016-08-24
        final int MIN_ROW_COUNT = 150;
		String queryString = "SELECT procedure_id, parameter_id, pp.stable_id as parameter_stable_id, pproc.stable_id as procedure_stable_id "
				+ " FROM phenotype_procedure_parameter ppp "
				+ " INNER JOIN phenotype_parameter pp ON pp.id=ppp.parameter_id "
				+ " INNER JOIN phenotype_procedure pproc ON pproc.id=ppp.procedure_id";

		try (PreparedStatement p = connection.prepareStatement(queryString)) {

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
		    String message = String.format(
		            " procIdToParams # records = %s. Expected at least %s records.",
                    procIdToParams.size(),
                    MIN_ROW_COUNT);
            runStatus.addWarning(message);
        }

		return procIdToParams;
	}

	protected Map<String, ProcedureDTO> populateProcedureIdToProcedureMap(Connection connection, RunStatus runStatus) {

		Map<String, Set<String>> procIdToParams = populateProcedureToParameterMap(connection, runStatus);

		Map<String, ProcedureDTO> procedureIdToProcedureMap = new HashMap<>();
        String q = "SELECT id as pproc_id, stable_id, name, stable_key, " +
                "is_mandatory, level, stage, stage_label, schedule_key, description, " +
                "concat(name, '___', stable_id) as proc_name_id " +
                "FROM phenotype_procedure " ;

		try (PreparedStatement p = connection.prepareStatement(q)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				ProcedureDTO proc = new ProcedureDTO();
				proc.setStableId(resultSet.getString("stable_id"));
				proc.setId(resultSet.getLong("pproc_id"));
				proc.setName(resultSet.getString("name"));
				proc.setStableKey(resultSet.getLong("stable_key"));
				proc.setProcNameId(resultSet.getString("proc_name_id"));
				proc.setRequired(resultSet.getBoolean("is_mandatory"));
				proc.setDescription(resultSet.getString("description"));
				proc.setLevel(resultSet.getString("level"));
				proc.setStage(resultSet.getString("stage"));
				proc.setStageLabel(resultSet.getString("stage_label"));
				proc.setScheduleKey(resultSet.getInt("schedule_key"));

				if (procIdToParams.get(proc.getStableId()) != null) {
					for (String parameterId : procIdToParams.get(proc.getStableId())) {
						proc.addParameter(paramIdToParameter.get(parameterId));
					}
				}

				procedureIdToProcedureMap.put(resultSet.getString("stable_id"), proc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

        if (procedureIdToProcedureMap.size() < 150) {
            runStatus.addWarning(" procedureIdToProcedureMap # records = " + procedureIdToProcedureMap.size() + ". Expected at least 150 records.");
        }

		return procedureIdToProcedureMap;
	}

	protected Map<String, PipelineDTO> populatePipelineList(Connection connection) {

		Map<String, PipelineDTO> procIdToPipelineMap = new HashMap<>();
		String queryString = "SELECT pproc.stable_id as procedure_stable_id, ppipe.name as pipe_name, ppipe.id as pipe_id, ppipe.stable_id as pipe_stable_id, "
				+ " ppipe.stable_key AS pipe_stable_key, concat(ppipe.name, '___', pproc.name, '___', pproc.stable_id) AS pipe_proc_sid "
				+ " FROM phenotype_procedure pproc INNER JOIN phenotype_pipeline_procedure ppproc ON pproc.id=ppproc.procedure_id "
				+ " INNER JOIN phenotype_pipeline ppipe ON ppproc.pipeline_id=ppipe.id"
				+ " WHERE ppipe.db_id=6 ORDER BY ppipe.id ASC ";

		try (PreparedStatement p = connection.prepareStatement(queryString)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String pipelineStableId = resultSet.getString("pipe_stable_id");
				PipelineDTO pipe = new PipelineDTO();

				if (procIdToPipelineMap.containsKey(pipelineStableId)){
					pipe = procIdToPipelineMap.get(pipelineStableId);
				}
				pipe.setId(resultSet.getLong("pipe_id"));
				pipe.setName(resultSet.getString("pipe_name"));
				pipe.setStableKey(resultSet.getLong("pipe_stable_key"));
				pipe.setStableId(resultSet.getString("pipe_stable_id"));
				pipe.addProcedure(procedureIdToProcedure.get(resultSet.getString("procedure_stable_id")));
				procIdToPipelineMap.put(pipelineStableId, pipe);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return procIdToPipelineMap;
	}

	protected void addAbnormalMaOntology(Connection connection){

		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=8 LIMIT 10000";

		try (PreparedStatement p = connection.prepareStatement(sqlQuery)) {

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

	protected void addAbnormalEmapOntology(Connection connection){

		String sqlQuery="SELECT pp.id as id, ot.name as name, stable_id, ontology_acc FROM phenotype_parameter pp "
				+ "	INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id = pploa.parameter_id "
				+ " INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id = pploa.annotation_id "
				+ " INNER JOIN ontology_term ot ON ot.acc = ppoa.ontology_acc "
				+ " WHERE ppoa.ontology_db_id=14";
		//14 db id is emap

		try (PreparedStatement p = connection.prepareStatement(sqlQuery)) {

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

	/**@since 2015
	 * @author tudose
	 * @param parameter
     * @param runStatus a valid <code>RunStatus</code> instance
	 * @return
	 * @throws SolrServerException
	 */
	// Method copied from org.mousephenotype.cda.db.impress.Utilities.
	// Adjusted to avoid use of Parameter entity obj.
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
//					logger.info("  Getting type from observations for parameter {}: Got {}", parameter.getStableId(), parameterToObservationTypeMap.get(parameter.getStableId()));
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

	private Map<String,ObservationType> getObservationTypeMap(Connection connection, RunStatus runStatus){

		Map<String,ObservationType> map = new HashMap<>();
		String query= "select distinct parameter_stable_id, observation_type from observation where observation.missing != 1";

		try (PreparedStatement p = connection.prepareStatement(query)) {

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

	public class MissingMpId {
		public final String pipelineId;
		public final String procedureId;
		public final String parameterId;
		public final String mpId;

		public MissingMpId(String pipelineId, String procedureId, String parameterId, String mpId) {
			this.pipelineId = pipelineId;
			this.procedureId = procedureId;
			this.parameterId = parameterId;
			this.mpId = mpId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			MissingMpId that = (MissingMpId) o;
			return Objects.equals(pipelineId, that.pipelineId) &&
					Objects.equals(procedureId, that.procedureId) &&
					Objects.equals(parameterId, that.parameterId) &&
					Objects.equals(mpId, that.mpId);
		}

		@Override
		public int hashCode() {

			return Objects.hash(pipelineId, procedureId, parameterId, mpId);
		}

		@Override
		public String toString() {
			return mpId + "::" + pipelineId + "::" + procedureId + "::" + parameterId;
		}
	}
}