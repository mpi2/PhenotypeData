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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.db.WeightMap;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTOWrite;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Populate the experiment core
 */
@EnableAutoConfiguration
public class ObservationIndexer extends AbstractIndexer implements CommandLineRunner {

	@Value("${experimenterIdMap}")
	String experimenterIdMap;


	private final Logger logger = LoggerFactory.getLogger(ObservationIndexer.class);

	private final List<String> MALE_FERTILITY_PARAMETERS   = Arrays.asList("IMPC_FER_001_001", "IMPC_FER_006_001",
																	   "IMPC_FER_007_001", "IMPC_FER_008_001", "IMPC_FER_009_001");
	private final List<String> FEMALE_FERTILITY_PARAMETERS = Arrays.asList("IMPC_FER_019_001", "IMPC_FER_010_001",
																				 "IMPC_FER_011_001", "IMPC_FER_012_001", "IMPC_FER_013_001");


	private Map<Long, String>                         anatomyMap              = new HashMap<>();
	private Map<String, BiologicalDataBean>           biologicalData          = new HashMap<>();
	private Map<Long, DatasourceBean>                 datasourceMap           = new HashMap<>();
	private Map<String, String>                       emap2emapaIdMap         = new HashMap<>();
	private Map<Long, List<String>>                   experimenterData        = new HashMap<>();
	private Map<String, BiologicalDataBean>           lineBiologicalData      = new HashMap<>();
	private Map<Long, List<OntologyBean>>             ontologyEntityMap;
	private Map<Long, List<ParameterAssociationBean>> parameterAssociationMap = new HashMap<>();
	private Map<Long, ParameterDTO>                   parameterMap            = new HashMap<>();
	private Map<Long, ImpressBaseDTO>                 pipelineMap             = new HashMap<>();
	private Map<Long, ImpressBaseDTO>                 procedureMap            = new HashMap<>();
	private Map<Long, DatasourceBean>                 projectMap              = new HashMap<>();
	private Map<String, Map<String, String>>          translateCategoryNames  = new HashMap<>();
	private WeightMap                                 weightMap;                                   // NOTE: Loading weightMap takes upwards of 8 minutes; therefore, it is not spring-managed. Load it manually and only when needed.

	private OntologyParser        emapaParser;
	private OntologyParser        maParser;
	private OntologyParserFactory ontologyParserFactory;

	private SolrClient experimentCore;


	protected ObservationIndexer() {

	}

	@Inject
	public ObservationIndexer(
			@NotNull DataSource komp2DataSource,
			@NotNull OntologyTermRepository ontologyTermRepository,
			@NotNull SolrClient experimentCore)
	{
		super(komp2DataSource, ontologyTermRepository);
		this.experimentCore = experimentCore;
	}

	@Override
	public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(experimentCore);
	}

	public static void main(String[] args) throws IndexerException {
		SpringApplication.run(ObservationIndexer.class, args);
	}


	@Override
	public RunStatus run() throws IndexerException, SQLException {
		weightMap = new WeightMap(komp2DataSource);
		long count = 0;
		RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try (Connection connection = komp2DataSource.getConnection()) {

			ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
			emapaParser = ontologyParserFactory.getEmapaParser();
			maParser = ontologyParserFactory.getMaParser();
			logger.info("  populating supporting maps");
			pipelineMap = IndexerMap.getImpressPipelines(connection);
			procedureMap = IndexerMap.getImpressProcedures(connection);
			parameterMap = IndexerMap.getImpressParameters(connection);
			logger.debug(" IMPReSS maps\n  Pipeline: {}, Procedure: {}, Parameter: {} " + pipelineMap.size(), procedureMap.size(), parameterMap.size());

			logger.debug("  populating ontology entity map");
			ontologyEntityMap = IndexerMap.getOntologyParameterSubTerms(connection);
			logger.debug(" ontology entity map size: " + ontologyEntityMap.size());

			logger.debug("  populating datasource map");
			populateDatasourceDataMap(connection);

			logger.debug("  populating experimenter map");
			populateExperimenterDataMap(connection);
			logger.debug("  map size: " + experimenterData.size());

			logger.debug("  populating categorynames map");
			populateCategoryNamesDataMap(connection);
			logger.debug("  map size: " + translateCategoryNames.size());

			logger.debug("  populating biological data map");
			populateBiologicalDataMap(connection);
			logger.debug("  map size: " + biologicalData.size());

			logger.debug("  populating line data map");
			populateLineBiologicalDataMap(connection);
			logger.debug("  map size: " + lineBiologicalData.size());

			logger.debug("  populating parameter association map");
			populateParameterAssociationMap(connection);
			logger.debug("  map size: " + parameterAssociationMap.size());

			logger.debug("  populating emap to emapa map");
			populateEmap2EmapaMap();
			logger.debug(" map size: "+ emap2emapaIdMap.size());

			logger.debug("  populating anatomy map");
			populateAnatomyMap(connection);
			logger.debug("  map size: " + anatomyMap.size());

			logger.info("  maps populated");


			count = populateObservationSolrCore(connection, runStatus);

		} catch (SolrServerException | SQLException | IOException |OWLOntologyCreationException | OWLOntologyStorageException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}

		logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
		return runStatus;
	}

	public long populateObservationSolrCore(Connection connection, RunStatus runStatus) throws IOException, SolrServerException {

		int count = 0;
		final int MAX_MISSING_BIOLOGICAL_DATA_ERROR_COUNT_DISPLAYED = 100;
		int missingBiologicalDataErrorCount = 0;
		experimentCore.deleteByQuery("*:*");

		String query = "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, "
				+ "o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, "
				+ "o.biological_sample_id, "
				+ "e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, "
				+ "e.date_of_experiment, e.external_id, e.id as experiment_id, "
				+ "e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, "
				+ "co.category as raw_category, uo.data_point as unidimensional_data_point, "
				+ "mo.data_point as multidimensional_data_point, tso.data_point as time_series_data_point, "
				+ "tro.text as text_value, mo.order_index, mo.dimension, tso.time_point, "
				+ "tso.discrete_point, iro.file_type, iro.download_file_path, o.sequence_id as sequence_id "
				+ "FROM observation o "
				+ "LEFT OUTER JOIN categorical_observation co ON o.id=co.id "
				+ "LEFT OUTER JOIN unidimensional_observation uo ON o.id=uo.id "
				+ "LEFT OUTER JOIN multidimensional_observation mo ON o.id=mo.id "
				+ "LEFT OUTER JOIN time_series_observation tso ON o.id=tso.id "
				+ "LEFT OUTER JOIN image_record_observation iro ON o.id=iro.id "
				+ "LEFT OUTER JOIN text_observation tro ON o.id=tro.id "
				+ "INNER JOIN experiment_observation eo ON eo.observation_id=o.id "
				+ "INNER JOIN experiment e on eo.experiment_id=e.id " + "WHERE o.missing=0";

		try (PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = p.executeQuery();

			while (r.next()) {

				ObservationDTOWrite o = new ObservationDTOWrite();
				o.setId(r.getString("id"));
				o.setParameterId(r.getLong("parameter_id"));
				o.setExperimentId(r.getLong("experiment_id"));
				o.setExperimentSourceId(r.getString("external_id"));

				if (StringUtils.isNotEmpty(r.getString("sequence_id"))) {
					if (isInteger(r.getString("sequence_id"))) {
						Integer seqId = Integer.parseInt(r.getString("sequence_id"));
						o.setSequenceId(seqId);
					}
				}

				ZonedDateTime dateOfExperiment = null;
				try {
					dateOfExperiment = ZonedDateTime.parse(r.getString("date_of_experiment"), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS).withZone(ZoneId.of("UTC")));
					o.setDateOfExperiment(dateOfExperiment);
				} catch (NullPointerException e) {
					logger.debug("  No date of experiment set for experiment external ID: {}", r.getString("external_id"));
					o.setDateOfExperiment(null);
				}

				o.setParameterId(parameterMap.get(r.getLong("parameter_id")).getId());
				o.setParameterName(parameterMap.get(r.getLong("parameter_id")).getName());
				o.setParameterStableId(parameterMap.get(r.getLong("parameter_id")).getStableId());
				o.setDataType(parameterMap.get(r.getInt("parameter_id")).getDatatype());

				o.setProcedureId(procedureMap.get(r.getLong("procedure_id")).getId());
				o.setProcedureName(procedureMap.get(r.getLong("procedure_id")).getName());
				String procedureStableId = procedureMap.get(r.getLong("procedure_id")).getStableId();
				o.setProcedureStableId(procedureStableId);
				o.setProcedureGroup(procedureStableId.substring(0, procedureStableId.lastIndexOf("_")));

				o.setPipelineId(pipelineMap.get(r.getLong("pipeline_id")).getId());
				o.setPipelineName(pipelineMap.get(r.getLong("pipeline_id")).getName());
				o.setPipelineStableId(pipelineMap.get(r.getLong("pipeline_id")).getStableId());

				if (anatomyMap.containsKey(r.getLong("parameter_id"))) {

					String anatomyTermId = anatomyMap.get(r.getLong("parameter_id"));

					if (anatomyTermId != null) {

						if (o.getAnatomyId() == null) {
							// Initialize all the collections of anatomy terms
							o.setAnatomyId(new ArrayList<>());
							o.setAnatomyTerm(new ArrayList<>());
							o.setAnatomyTermSynonym(new ArrayList<>());
							o.setIntermediateAnatomyId(new ArrayList<>());
							o.setIntermediateAnatomyTerm(new ArrayList<>());
							o.setIntermediateAnatomyTermSynonym(new ArrayList<>());
							o.setSelectedTopLevelAnatomyId(new ArrayList<>());
							o.setSelectedTopLevelAnatomyTerm(new ArrayList<>());
							o.setSelectedTopLevelAnatomyTermSynonym(new ArrayList<>());
						}

						if (anatomyTermId.startsWith("MA:")) {
							addAnatomyInfo (maParser.getOntologyTerm(anatomyTermId), o);
						} else if (anatomyTermId.startsWith("EMAPA:")) {
							addAnatomyInfo (emapaParser.getOntologyTerm(anatomyTermId), o);
						}
					}
				}

				o.setDataSourceId(datasourceMap.get(r.getLong("datasource_id")).id);
				o.setDataSourceName(datasourceMap.get(r.getInt("datasource_id")).name);

				o.setProjectId(projectMap.get(r.getLong("project_id")).id);
				o.setProjectName(projectMap.get(r.getInt("project_id")).name);

				o.setMetadataGroup(r.getString("metadata_group"));
				if (r.wasNull()) {
					o.setMetadataGroup("");
					o.setMetadata(new ArrayList<>());
				}

				String metadataCombined = r.getString("metadata_combined");
				if (!r.wasNull()) {
					o.setMetadata(new ArrayList<>(Arrays.asList(metadataCombined.split("::"))));
				}

				// Add experimenter ID(s) to the metadata
				if (experimenterData.containsKey(o.getExperimentId())) {
					if (o.getMetadata()==null) {
						o.setMetadata(new ArrayList<>(experimenterData.get(o.getExperimentId())));
					} else {
						o.getMetadata().addAll(experimenterData.get(o.getExperimentId()));
					}
				}

				// Add the Biological data
				String bioSampleId = r.getString("biological_sample_id");
				if (r.wasNull()) {
					// Line level data

					BiologicalDataBean b = lineBiologicalData.get(r.getString("experiment_id"));
					if (b == null) {
						runStatus.addError(
								" Cannot find biological data for line level experiment " + r.getString("experiment_id"));
						continue;
					}
					o.setBiologicalModelId(b.biologicalModelId);
					o.setGeneAccession(b.geneAcc);
					o.setGeneSymbol(b.geneSymbol);
					o.setAlleleAccession(b.alleleAccession);
					o.setAlleleSymbol(b.alleleSymbol);
					o.setStrainAccessionId(b.strainAcc);
					o.setStrainName(b.strainName);
					o.setGeneticBackground(b.geneticBackground);
					o.setAllelicComposition(b.allelicComposition);
					o.setPhenotypingCenter(b.phenotypingCenterName);
					o.setPhenotypingCenterId(b.phenotypingCenterId);
					o.setColonyId(b.colonyId);

					// Viability applies to both sexes
					if (o.getParameterStableId().contains("_VIA_")) {
						o.setSex(SexType.both.getName());
					} else {
						// Fertility applies to the sex tested, separate
						// parameters per male//female
						if (MALE_FERTILITY_PARAMETERS.contains(o.getParameterStableId())) {
							o.setSex(SexType.male.getName());
						} else if (FEMALE_FERTILITY_PARAMETERS.contains(o.getParameterStableId())) {
							o.setSex(SexType.female.getName());
						}
						if (o.getSex() == null) {
							o.setSex(SexType.both.getName());
						}
					}

					if (b.zygosity != null) {
						o.setZygosity(b.zygosity);
					} else {
						// Default to hom
						o.setZygosity(ZygosityType.homozygote.getName());
					}

					// All line level parameters are sample group "experimental"
					// due to the nature of the
					// procedures (i.e. no control mice will go through VIA or
					// FER procedures.)
					o.setGroup(BiologicalSampleType.experimental.getName());

				} else {
					// Specimen level data


					BiologicalDataBean b = biologicalData.get(bioSampleId);

					if (b == null) {

						if (missingBiologicalDataErrorCount++ < MAX_MISSING_BIOLOGICAL_DATA_ERROR_COUNT_DISPLAYED) {
							runStatus.addError(
									" Cannot find biological data for specimen id: " + bioSampleId + ", experiment id: " + r.getString("experiment_id"));
						}

						continue;
					}

					o.setBiologicalModelId(b.biologicalModelId);
					o.setGeneAccession(b.geneAcc);
					o.setGeneSymbol(b.geneSymbol);
					o.setAlleleAccession(b.alleleAccession);
					o.setAlleleSymbol(b.alleleSymbol);
					o.setStrainAccessionId(b.strainAcc);
					o.setStrainName(b.strainName);
					o.setGeneticBackground(b.geneticBackground);
					o.setAllelicComposition(b.allelicComposition);
					o.setPhenotypingCenter(b.phenotypingCenterName);
					o.setPhenotypingCenterId(b.phenotypingCenterId);

					o.setColonyId(b.colonyId);
					o.setZygosity(b.zygosity);
					o.setDateOfBirth(b.dateOfBirth);
					if(b.dateOfBirth!=null && dateOfExperiment !=null ){

						Instant dob=b.dateOfBirth.toInstant();
						Instant expDate=dateOfExperiment.toInstant();
						int ageInDays = (int) Duration.between(dob, expDate).toDays();
						int daysInWeek = 7;
						int ageInWeeks = ageInDays / daysInWeek;
						o.setAgeInDays(ageInDays);
						o.setAgeInWeeks(ageInWeeks);
					}
					o.setSex(b.sex);
					o.setGroup(b.sampleGroup);
					o.setBiologicalSampleId(b.biologicalSampleId);
					o.setExternalSampleId(b.externalSampleId);

					if (b.productionCenterName != null) {
						o.setProductionCenter(b.productionCenterName);
					}
					if (b.productionCenterId != null) {
						o.setProductionCenterId(b.productionCenterId);
					}
					if (b.litterId != null) {
						o.setLitterId(b.litterId);
					}
				}

				//
				// NOTE
				// Developmental stage must be set after the colony ID, pipeline ID and procedure ID fields are set
				//
				BasicBean developmentalStage = getDevelopmentalStage(o.getPipelineStableId(), o.getProcedureStableId(), o.getColonyId());
				if (developmentalStage != null) {
					o.setDevelopmentStageAcc(developmentalStage.getId());
					o.setDevelopmentStageName(developmentalStage.getName());
				}

				o.setObservationType(r.getString("observation_type"));

				String cat = r.getString("raw_category");
				if (!r.wasNull()) {

					String param = r.getString("parameter_stable_id");
					if (translateCategoryNames.containsKey(param)) {

						String transCat = translateCategoryNames.get(param).get(cat);
						// System.out.println("param with cat is="+param+"
						// cat="+cat);
						// System.out.println("transCat="+transCat);
						if (transCat != null && !transCat.equals("")) {
							o.setCategory(transCat);
						} else {
							o.setCategory(cat);
						}

					} else {
						o.setCategory(cat);
					}
				}

				// Add the correct "data point" for the type
				switch (r.getString("observation_type")) {
				case "unidimensional":
					o.setDataPoint(r.getFloat("unidimensional_data_point"));
					break;
				case "multidimensional":
					o.setDataPoint(r.getFloat("multidimensional_data_point"));
					break;
				case "time_series":
					o.setDataPoint(r.getFloat("time_series_data_point"));
					break;
				}

				Integer order_index = r.getInt("order_index");
				if (!r.wasNull()) {
					o.setOrderIndex(order_index);
				}

				String dimension = r.getString("dimension");
				if (!r.wasNull()) {
					o.setDimension(dimension);
				}

				String time_point = r.getString("time_point");
				if (!r.wasNull()) {
					o.setTimePoint(time_point);
				}

				Float discrete_point = r.getFloat("discrete_point");
				if (!r.wasNull()) {
					o.setDiscretePoint(discrete_point);
				}

				String text_value = r.getString("text_value");
				if (!r.wasNull()) {
					o.setTextValue(text_value);
				}

				if (ontologyEntityMap.containsKey(Integer.parseInt(o.getId()))) {

					List<OntologyBean> subOntBeans = ontologyEntityMap.get(Integer.parseInt(o.getId()));
					for (OntologyBean bean : subOntBeans) {
						o.addSubTermId(bean.getId());
						o.addSubTermName(bean.getName());
						o.addSubTermDescription(bean.getDescription());
					}
				}
				String file_type = r.getString("file_type");
				if (!r.wasNull()) {
					o.setFileType(file_type);
				}

				String download_file_path = r.getString("download_file_path");
				if (!r.wasNull()) {
					o.setDownloadFilePath(download_file_path);
				}

				if (parameterAssociationMap.containsKey(r.getInt("id"))) {
					for (ParameterAssociationBean pb : parameterAssociationMap.get(r.getInt("id"))) {

						// Will never be null, we hope
						o.addParameterAssociationStableId(pb.parameterStableId);
						o.addParameterAssociationName(pb.parameterAssociationName);
						if (StringUtils.isNotEmpty(pb.parameterAssociationValue)) {
							o.addParameterAssociationValue(pb.parameterAssociationValue);
						}
						if (StringUtils.isNotEmpty(pb.sequenceId)) {
							o.addParameterAssociationSequenceId(pb.sequenceId);
						}

						if (StringUtils.isNotEmpty(pb.dimId)) {
							o.addParameterAssociationDimId(pb.dimId);
						}

					}
				}

				// Add weight parameters only if this observation isn't itself a
				// weight parameter
				if ( ! WeightMap.isWeightParameter(o.getParameterStableId()) )
				{
					WeightMap.BodyWeight b = weightMap.getNearestWeight(o.getBiologicalSampleId(), o.getParameterStableId(), dateOfExperiment);

					if (o.getProcedureGroup().contains("_IPG")) {
						b = weightMap.getNearestIpgttWeight(o.getBiologicalSampleId());
					}

					if (b != null) {
						o.setWeight(b.getWeight());
						o.setWeightDate(b.getDate());
						o.setWeightDaysOld(b.getDaysOld());
						o.setWeightParameterStableId(b.getParameterStableId());
					}
				}

				// 60 seconds between commits
				documentCount++;
				experimentCore.addBean(o, 60000);

				count++;

				if (count % 2000000 == 0) {
					logger.info(" Added " + count + " beans");
				}
			}

			// Final commit to save the rest of the docs
			experimentCore.commit();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" Big error :" + e.getMessage());
		}

		if (missingBiologicalDataErrorCount > 0) {
			logger.error("'Cannot find biological data for specimen id...' occurred " + missingBiologicalDataErrorCount + " times.");
		}

		return count;
	}


	private void addAnatomyInfo(OntologyTermDTO term, ObservationDTOWrite doc) {

		if (term != null) {
			doc.getAnatomyId().add(term.getAccessionId());
			doc.getAnatomyTerm().add(term.getName());
			doc.getAnatomyTermSynonym().addAll(term.getSynonyms());
			if (term.getTopLevelIds() != null) {
				doc.setSelectedTopLevelAnatomyId(new ArrayList<>(term.getTopLevelIds()));
				doc.setSelectedTopLevelAnatomyTerm(new ArrayList<>(term.getTopLevelNames()));
			}
			if (term.getIntermediateIds() != null) {
				doc.setIntermediateAnatomyId(new ArrayList<>(term.getIntermediateIds()));
				doc.setIntermediateAnatomyTerm(new ArrayList<>(term.getIntermediateNames()));
			}
		}
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NullPointerException | NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	/**
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological sample
	 *
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	void populateBiologicalDataMap(Connection connection) throws SQLException {

		String query = "SELECT CAST(bs.id AS CHAR) as biological_sample_id, bs.organisation_id as phenotyping_center_id, "
				+ "org.name as phenotyping_center_name, bs.sample_group, bs.external_id as external_sample_id, "
				+ "ls.date_of_birth, ls.colony_id, ls.sex as sex, ls.zygosity, ls.developmental_stage_acc, ot.name AS developmental_stage_name, ot.acc as developmental_stage_acc,"
				+ "bms.biological_model_id, "
				+ "strain.acc as strain_acc, strain.name as strain_name, bm.genetic_background, bm.allelic_composition, "
				+ "bs.production_center_id, prod_org.name as production_center_name, ls.litter_id, "
				+ "(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=bms.biological_model_id) as allele_accession, "
				+ "(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bms.biological_model_id)  as allele_symbol, "
				+ "(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bms.biological_model_id) as acc, "
				+ "(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bms.biological_model_id)  as symbol "
				+ "FROM biological_sample bs "
				+ "INNER JOIN organisation org ON bs.organisation_id=org.id "
				+ "INNER JOIN live_sample ls ON bs.id=ls.id "
				+ "INNER JOIN biological_model_sample bms ON bs.id=bms.biological_sample_id "
				+ "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id "
				+ "INNER JOIN strain strain ON strain.acc=bmstrain.strain_acc "
				+ "INNER JOIN biological_model bm ON bm.id = bms.biological_model_id "
				+ "INNER JOIN ontology_term ot ON ot.acc=ls.developmental_stage_acc "
		        + "INNER JOIN organisation prod_org ON bs.organisation_id=prod_org.id ";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				BiologicalDataBean b = new BiologicalDataBean();

				b.alleleAccession = resultSet.getString("allele_accession");
				b.alleleSymbol = resultSet.getString("allele_symbol");
				b.biologicalModelId = resultSet.getLong("biological_model_id");
				b.biologicalSampleId = resultSet.getLong("biological_sample_id");
				b.colonyId = resultSet.getString("colony_id");

				String rawDOB = null;

				try {
					rawDOB = resultSet.getString("date_of_birth");

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS);
					b.dateOfBirth = ZonedDateTime.parse(rawDOB, formatter.withZone(ZoneId.of("UTC")));

				} catch (NullPointerException e) {

					b.dateOfBirth = null;
					logger.debug("  No date of birth set for specimen external ID: {}",
								 resultSet.getString("external_sample_id"));

				} catch (DateTimeParseException e) {

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS);
					b.dateOfBirth = ZonedDateTime.parse(rawDOB, formatter.withZone(ZoneId.of("UTC")));
				}

				b.externalSampleId = resultSet.getString("external_sample_id");
				b.geneAcc = resultSet.getString("acc");
				b.geneSymbol = resultSet.getString("symbol");
				b.phenotypingCenterId = resultSet.getLong("phenotyping_center_id");
				b.phenotypingCenterName = resultSet.getString("phenotyping_center_name");
				b.sampleGroup = resultSet.getString("sample_group");
				b.sex = resultSet.getString("sex");
				b.strainAcc = resultSet.getString("strain_acc");
				b.strainName = resultSet.getString("strain_name");
				b.geneticBackground = resultSet.getString("genetic_background");
				b.allelicComposition = resultSet.getString("allelic_composition");
				b.zygosity = resultSet.getString("zygosity");
				b.productionCenterId = resultSet.getLong("production_center_id");
				b.productionCenterName = resultSet.getString("production_center_name");
				b.litterId = resultSet.getString("litter_id");


				biologicalData.put(resultSet.getString("biological_sample_id"), b);
			}
		}
	}

	/**
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological model (really an experiment)
	 *
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	void populateLineBiologicalDataMap(Connection connection) throws SQLException {

		String query = "SELECT e.id as experiment_id, e.colony_id, e.biological_model_id, "
				+ "e.organisation_id as phenotyping_center_id, org.name as phenotyping_center_name, "
				+ "strain.acc as strain_acc, strain.name as strain_name, bm.genetic_background, bm.allelic_composition, "
				+ "(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=e.biological_model_id) as allele_accession, "
				+ "(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=e.biological_model_id)  as allele_symbol, "
				+ "(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=e.biological_model_id) as acc, "
				+ "(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=e.biological_model_id)  as symbol "
				+ "FROM experiment e "
				+ "INNER JOIN organisation org ON e.organisation_id=org.id "
				+ "INNER JOIN biological_model_strain bm_strain ON bm_strain.biological_model_id=e.biological_model_id "
				+ "INNER JOIN strain strain ON strain.acc=bm_strain.strain_acc "
				+ "INNER JOIN biological_model bm ON bm.id = e.biological_model_id";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				BiologicalDataBean b = new BiologicalDataBean();

				b.colonyId = resultSet.getString("colony_id");
				b.phenotypingCenterId = resultSet.getLong("phenotyping_center_id");
				b.phenotypingCenterName = resultSet.getString("phenotyping_center_name");
				b.strainAcc = resultSet.getString("strain_acc");
				b.strainName = resultSet.getString("strain_name");
				b.geneticBackground = resultSet.getString("genetic_background");
				b.allelicComposition = resultSet.getString("allelic_composition");
				b.alleleAccession = resultSet.getString("allele_accession");
				b.alleleSymbol = resultSet.getString("allele_symbol");
				b.biologicalModelId = resultSet.getLong("biological_model_id");
				b.geneAcc = resultSet.getString("acc");
				b.geneSymbol = resultSet.getString("symbol");

				if (b.alleleAccession == null && b.colonyId != null) {
					// Override the biological model with one that has the
					// correct gene/allele/strain
					String query2 = "SELECT DISTINCT bm.id as biological_model_id, "
							+ " (select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=bm.id) as allele_accession, "
							+ " (select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) as allele_symbol, "
							+ " (select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bm.id) as acc, "
							+ " (select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id)  as symbol, "
							+ " strain.acc as strain_acc, strain.name as strain_name, bm.genetic_background, bm.allelic_composition "
							+ " FROM live_sample ls "
							+ " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=ls.id "
							+ " INNER JOIN biological_model bm ON bm.id=bms.biological_model_id "
							+ " INNER JOIN biological_model_strain bm_strain ON bm_strain.biological_model_id=bm.id "
							+ " INNER JOIN strain strain ON strain.acc=bm_strain.strain_acc "
							+ " WHERE bm.allelic_composition !='' AND ls.colony_id = ? LIMIT 1 ";
					try (PreparedStatement p2 = connection.prepareStatement(query2)) {
						p2.setString(1, resultSet.getString("colony_id"));
						ResultSet resultSet2 = p2.executeQuery();
						resultSet2.next();
						b.strainAcc = resultSet2.getString("strain_acc");
						b.strainName = resultSet2.getString("strain_name");
						b.geneticBackground = resultSet2.getString("genetic_background");
						b.allelicComposition = resultSet.getString("allelic_composition");
						b.alleleAccession = resultSet2.getString("allele_accession");
						b.alleleSymbol = resultSet2.getString("allele_symbol");
						b.biologicalModelId = resultSet2.getLong("biological_model_id");
						b.geneAcc = resultSet2.getString("acc");
						b.geneSymbol = resultSet2.getString("symbol");
					}
				}

				lineBiologicalData.put(resultSet.getString("experiment_id"), b);
			}
		}
	}

	/**
	 * Add all the relevant data required for translating the category names in
	 * the cases where the category names are numerals, but the actual name is
	 * in the description field
	 *
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	void populateCategoryNamesDataMap(Connection connection) throws SQLException {

		String query = "SELECT pp.stable_id, ppo.name, ppo.description FROM phenotype_parameter pp "
				+ "INNER JOIN phenotype_parameter_lnk_option pplo ON pp.id=pplo.parameter_id "
				+ "INNER JOIN phenotype_parameter_option ppo ON ppo.id=pplo.option_id "
				+ "WHERE ppo.name NOT REGEXP '^[a-zA-Z]' AND ppo.description!='' ";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String stableId = resultSet.getString("stable_id");
				logger.debug("  parameter_stable_id for numeric category: {}", stableId);

				if (!translateCategoryNames.containsKey(stableId)) {
					translateCategoryNames.put(stableId, new HashMap<>());
				}

				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				if (name.matches("[0-9]+")) {

					translateCategoryNames.get(stableId).put(name, description);

					// also add .0 onto string as sometimes this is what our numerical
					// categories look like in the database!!!!
					name += ".0";
					translateCategoryNames.get(stableId).put(name, description);
				} else {
					logger.debug("  Not translating non alphabetical category for parameter: " + stableId + ", name: "
							+ name + ", desc:" + description);
				}

			}
		}
	}

	void populateParameterAssociationMap(Connection connection) throws SQLException {

		Map<String, String> stableIdToNameMap = this.getAllParameters(connection);
		String query = "SELECT id, observation_id, parameter_id, sequence_id, dim_id, parameter_association_value FROM parameter_association  where parameter_association_value is not  null";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				Long observationId = resultSet.getLong("observation_id");

				ParameterAssociationBean pb = new ParameterAssociationBean();
				pb.observationId = observationId;
				pb.parameterStableId = resultSet.getString("parameter_id");
				pb.parameterAssociationValue = resultSet.getString("parameter_association_value");
				if (stableIdToNameMap.get(pb.parameterStableId) != null) {
					pb.parameterAssociationName = stableIdToNameMap.get(pb.parameterStableId);
				}
				pb.sequenceId = resultSet.getString("sequence_id");
				pb.dimId = resultSet.getString("dim_id");

				if (!parameterAssociationMap.containsKey(observationId)) {
					parameterAssociationMap.put(observationId, new ArrayList<>());
				}

				parameterAssociationMap.get(observationId).add(pb);
			}
		}
	}

	/**
	 * Return all parameter stable ids and names
	 *
	 * @throws SQLException
	 *             When a database error occurrs
	 */
	Map<String, String> getAllParameters(Connection connection) throws SQLException {
		Map<String, String> parameters = new HashMap<>();

		String query = "SELECT stable_id, name FROM phenotype_parameter";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				parameters.put(resultSet.getString("stable_id"), resultSet.getString("name"));
			}
		}

		return parameters;
	}

	void populateExperimenterDataMap(Connection connection) throws SQLException, IOException {

		Map<String, String> nameMap = new HashMap<>();
		List<String> lines = Files.readAllLines(Paths.get(experimenterIdMap));
		for (String line : lines) {
			String [] fields = line.split("\t");
			nameMap.put(fields[0], fields[1]);
		}

		String query = "SELECT DISTINCT experiment_id, value, parameter_id, p.name " +
			"FROM procedure_meta_data m " +
			"INNER JOIN phenotype_parameter p ON p.stable_id=m.parameter_id " +
			"WHERE name LIKE '%experimenter%' AND value IS NOT NULL ";

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {

				if ( ! experimenterData.containsKey(resultSet.getLong("experiment_id"))) {
					experimenterData.put(resultSet.getLong("experiment_id"), new ArrayList<>());
				}

				String ids = resultSet.getString("value");
				String parameterName = resultSet.getString("name");

				for (String id : ids.split(",")) {

					String loadId = id;

					//Translate the experimenter ID if needed
					if (nameMap.containsKey(id)) {
						loadId = nameMap.get(id);
					}

					//Hash the ID
					loadId = DigestUtils.md5Hex(loadId).substring(0,5).toUpperCase();

					experimenterData.get(resultSet.getLong("experiment_id")).add(parameterName + " = " + loadId);
				}
			}
		}
	}

	void populateDatasourceDataMap(Connection connection) throws SQLException {

		List<String> queries = new ArrayList<>();
		queries.add("SELECT id, short_name as name, 'DATASOURCE' as datasource_type FROM external_db");
		queries.add("SELECT id, name, 'PROJECT' as datasource_type FROM project");

		for (String query : queries) {

			try (PreparedStatement p = connection.prepareStatement(query)) {

				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {

					DatasourceBean b = new DatasourceBean();

					b.id = resultSet.getLong("id");
					b.name = resultSet.getString("name");

					switch (resultSet.getString("datasource_type")) {
					case "DATASOURCE":
						datasourceMap.put(resultSet.getLong("id"), b);
						break;
					case "PROJECT":
						projectMap.put(resultSet.getLong("id"), b);
						break;
					}
				}
			}
		}
	}



	/**
	 * Return map of EMAP => EMAPA
	 *
	 * @exception IOException
	 *                When a database error occurrs
	 */
	void populateEmap2EmapaMap() throws IOException {
		emap2emapaIdMap = ontologyParserFactory.getEmapToEmapaMap();
	}

	/**
	 * Return map of specimen ID => weight for
	 *
	 * @exception SQLException
	 *                When a database error occurrs
	 */
	void populateAnatomyMap(Connection connection) throws SQLException {

		String query = "SELECT DISTINCT p.id, p.stable_id, o.ontology_acc " +
			"FROM phenotype_parameter p " +
			"INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=p.id " +
			"INNER JOIN phenotype_parameter_ontology_annotation o ON o.id=l.annotation_id " +
			"WHERE p.stable_id like '%_ALZ_%' OR p.stable_id like '%_ELZ_%' " ;

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String ontoAcc = resultSet.getString("ontology_acc");
				if (ontoAcc != null) {
					anatomyMap.put(resultSet.getLong("id"),
							ontoAcc.startsWith("EMAP:") ? emap2emapaIdMap.get(ontoAcc) : ontoAcc);
				}
				else {
					logger.warn(" Parameter {} missing ontology association.", resultSet.getString("stable_id"));
				}
			}
		}

	}

	Map<String, Map<String, String>> getTranslateCategoryNames() {
		return translateCategoryNames;
	}

	Map<String, BiologicalDataBean> getLineBiologicalData() {
		return lineBiologicalData;
	}

	Map<String, BiologicalDataBean> getBiologicalData() {
		return biologicalData;
	}

	Map<Long, DatasourceBean> getDatasourceMap() {
		return datasourceMap;
	}

	Map<Long, DatasourceBean> getProjectMap() {
		return projectMap;
	}

	/**
	 * Internal class to act as Map value DTO for biological data
	 */
	class BiologicalDataBean {

		public String        alleleAccession;
		public String        alleleSymbol;
		public Long          biologicalModelId;
		public Long          biologicalSampleId;
		public String        colonyId;
		public ZonedDateTime dateOfBirth;
		public String        externalSampleId;
		public String        geneAcc;
		public String        geneSymbol;
		public String        phenotypingCenterName;
		public Long          phenotypingCenterId;
		public String        sampleGroup;
		public String        sex;
		public String        strainAcc;
		public String        strainName;
		public String        geneticBackground;
		public String        allelicComposition;
		public String        zygosity;
		public String        productionCenterName;
		public Long          productionCenterId;
		public String        litterId;

	}


	/**
	 * Internal class to act as Map value DTO for datasource data
	 */
	class DatasourceBean {

		public Long   id;
		public String name;
	}

	/**
	 * Internal class to act as Map value DTO for datasource data
	 */
	class ParameterAssociationBean {

		public String parameterAssociationName;
		public String parameterAssociationValue;
		public Long   id;
		public Long   observationId;
		public String parameterStableId;
		public String sequenceId;
		public String dimId;
	}
}