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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.*;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.PhisService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 *
 * @author jwarren
 */
@EnableAutoConfiguration
public class ImpcImagesIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(ImpcImagesIndexer.class);


	@NotNull
	@Value("${impcMediaBaseUrl}")
	private String impcMediaBaseUrl;

	@NotNull
	@Value("${pdfThumbnailUrl}")
	private String pdfThumbnailUrl;

	@Autowired
	@Qualifier("experimentCore")
	SolrClient experimentCore;

	@Autowired
	@Qualifier("impcImagesIndexing")
	SolrClient impcImagesIndexing;

	@Autowired
	@Qualifier("alleleCore")
	SolrClient alleleCore;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("ontodbDataSource")
	DataSource ontodbDataSource;

	@Autowired
    MaOntologyDAO maOntologyService;

	@Autowired
	EmapaOntologyDAO emapaService;

	@Autowired
	MpOntologyDAO mpService;
	
	PhisService phisService=new PhisService();

	@Value("classpath:uberonEfoMa_mappings.txt")
	org.springframework.core.io.Resource anatomogramResource;

	private Map<String, List<AlleleDTO>> alleles;
	private Map<String, ImageBean> imageBeans;
	String excludeProcedureStableId = "";

	private Map<String, String> parameterStableIdToMaTermIdMap;
	private Map<String, String> parameterStableIdToEmapaTermIdMap = new HashMap<>(); // key: EMAPA id;
	private Map<String, String> parameterStableIdToMpTermIdMap;
	private Map<String, EmapaOntologyDAO.Emapa> emap2EmapaMap;

	private String impcAnnotationBaseUrl;

	private Map<String, Map<String, List<String>>> maUberonEfoMap = new HashMap<>(); // key: MA id

	private final String fieldSeparator = "___";

	public ImpcImagesIndexer() {
		super();
	}

	@Override
	public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(impcImagesIndexing);
	}

	public static void main(String[] args) throws IndexerException, SQLException {
		SpringApplication.run(ImpcImagesIndexer.class, args);
	}



	@Override
	public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {

		RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();
		
		try {
			parameterStableIdToMaTermIdMap = this.populateParameterStableIdToMaIdMap();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			logger.info("Building parameter to abnormal mp map");
			parameterStableIdToMpTermIdMap = this.populateParameterStableIdToMpIdMap();
			logger.info("Parameter to abnormal mp map size="+parameterStableIdToMpTermIdMap.size());
			//System.out.println("parameterStableIdToMpTermIdMap"+parameterStableIdToMpTermIdMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			logger.info("Started emap mapping...");
			emap2EmapaMap = emapaService.populateEmap2EmapaMap();
			logger.info("Done {} EMAP to EMAPA mappings", emap2EmapaMap.size());
			parameterStableIdToEmapaTermIdMap = this.populateParameterStableIdToEmapaIdMap();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		imageBeans = populateImageUrls();
		logger.info("Added {} total Image URL beans", imageBeans.size());

		if (imageBeans.size() < 100) {
			runStatus.addError(
					"Didn't get any image entries from the db with omero_ids set so exiting the impc_image Indexer!!");
		}

		this.alleles = populateAlleles();
		logger.info("Added {} total allele beans", alleles.size());


		// logger.info(" omeroRootUrl=" + impcMediaBaseUrl);
		impcAnnotationBaseUrl = impcMediaBaseUrl.replace("webgateway", "webclient");

		try {
			maUberonEfoMap = IndexerMap.mapMaToUberronOrEfoForAnatomogram(anatomogramResource);
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
		}

		try {
			
			//populate image DTOs from phis solr dto objects
			List<ImageDTO> phisImages=phisService.getPhenoImageShareImageDTOs();
			
			logger.info("Starting indexing.....");
			impcImagesIndexing.deleteByQuery("*:*");

			SolrQuery query = ImageService.allImageRecordSolrQuery().setRows(Integer.MAX_VALUE);

			List<ImageDTO> imageList = experimentCore.query(query).getBeans(ImageDTO.class);
			imageList.addAll(phisImages);
			for (ImageDTO imageDTO : imageList) {
				
				if(imageDTO.getDateOfBirth()!=null && imageDTO.getDateOfExperiment() !=null ){
				Date dateOfExperiment=imageDTO.getDateOfExperiment();
				Date dateOfBirth=imageDTO.getDateOfBirth();
				Instant dob=dateOfBirth.toInstant();
				Instant expDate=dateOfExperiment.toInstant();
				long ageInDays = Duration.between(dob, expDate).toDays();
				imageDTO.setAgeInDays(ageInDays);
				}
				
				String downloadFilePath = imageDTO.getDownloadFilePath();
				if (imageBeans.containsKey(downloadFilePath)) {

					ImageBean iBean = imageBeans.get(downloadFilePath);
					String fullResFilePath = iBean.fullResFilePath;
					if (iBean.image_link != null) {
						imageDTO.setImageLink(iBean.image_link);
					}
					if(iBean.increment!=null){
						imageDTO.setIncrement(iBean.increment);
					}
					imageDTO.setFullResolutionFilePath(fullResFilePath);

					int omeroId = iBean.omeroId;
					imageDTO.setOmeroId(omeroId);

					if (omeroId == 0 || imageDTO.getProcedureStableId().equals(excludeProcedureStableId)) {// ||
																											// downloadFilePath.endsWith(".pdf")
																											// ){//if(downloadFilePath.endsWith(".pdf")){//||
																											// (imageDTO.getParameterStableId().equals("IMPC_ALZ_075_001")
																											// &&
																											// imageDTO.getPhenotypingCenter().equals("JAX")))
																											// {
						// Skip records that do not have an omero_id
						//System.out.println("skipping omeroId=" + omeroId + "param and center"
								//+ imageDTO.getParameterStableId() + imageDTO.getPhenotypingCenter());
						// runStatus.addWarning(" Skipping record for image
						// record " + fullResFilePath + " -- missing omero_id or
						// excluded procedure");
						continue;
					}

					// need to add a full path to image in omero as part of api
					// e.g.
					// https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/4855/
					if (omeroId != 0 && downloadFilePath != null) {
						// logger.info(" Setting
						// downloadurl="+impcMediaBaseUrl+"/render_image/"+omeroId);
						// /webgateway/archived_files/download/
						if (downloadFilePath.endsWith(".pdf")) {
							// http://wwwdev.ebi.ac.uk/mi/media/omero/webclient/annotation/119501/
							imageDTO.setDownloadUrl(impcAnnotationBaseUrl + "/annotation/" + omeroId);
							imageDTO.setJpegUrl(pdfThumbnailUrl);// pdf thumnail
																	// placeholder
						} else {
							imageDTO.setDownloadUrl(impcMediaBaseUrl + "/archived_files/download/" + omeroId);
							imageDTO.setJpegUrl(impcMediaBaseUrl + "/render_image/" + omeroId);
						}
					} else {
						runStatus.addWarning(" omero id is null for " + downloadFilePath);
					}

					// add the extra stuf we need for the searching and faceting
					// here
					if (imageDTO.getGeneAccession() != null && !imageDTO.getGeneAccession().equals("")) {

						String geneAccession = imageDTO.getGeneAccession();
						if (alleles.containsKey(geneAccession)) {
							populateImageDtoStatuses(imageDTO, geneAccession);

							if (imageDTO.getSymbol() != null) {
								String symbolGene = imageDTO.getSymbol() + "_" + imageDTO.getGeneAccession();
								imageDTO.setSymbolGene(symbolGene);

								if (imageDTO.getMarkerSynonym() != null){
									List<String> synSymGene = new ArrayList<>();
									for (String syn : imageDTO.getMarkerSynonym()) {
										synSymGene.add(syn + fieldSeparator + symbolGene);
									}
									imageDTO.setMarkerSynonymSymbolGene(synSymGene);
								}
							}
						}
					}

					addOntology(runStatus, imageDTO, parameterStableIdToMaTermIdMap, maOntologyService);
					addOntology(runStatus, imageDTO, parameterStableIdToEmapaTermIdMap, emapaService);
					addOntology(runStatus, imageDTO, parameterStableIdToMpTermIdMap, mpService);

					impcImagesIndexing.addBean(imageDTO);

					documentCount++;
				}
			}

			impcImagesIndexing.commit();

		} catch (SolrServerException | IOException e) {
			throw new IndexerException(e);
		}

		logger.info("Added {} total beans in {}", documentCount,
				commonUtils.msToHms(System.currentTimeMillis() - start));

		return runStatus;
	}

	private void addOntology(RunStatus runStatus, ImageDTO imageDTO, Map<String, String> stableIdToTermIdMap,
			OntologyDAO ontologyDAO) {

		if (imageDTO.getParameterAssociationStableId() != null
				&& !imageDTO.getParameterAssociationStableId().isEmpty()) {


			List<String> paramAssocNameProcName = new ArrayList<>();
			for(String paramAssocName : imageDTO.getParameterAssociationName()){
				paramAssocNameProcName.add(paramAssocName + fieldSeparator + imageDTO.getProcedureName());
			}
			imageDTO.setParameterAssociationNameProcedureName(paramAssocNameProcName);

			ArrayList<String> termIds = new ArrayList<>();
			ArrayList<String> terms = new ArrayList<>();
			ArrayList<String> termSynonyms = new ArrayList<>();

			ArrayList<String> topLevelIds = new ArrayList<>();
			ArrayList<String> topLevelTerms = new ArrayList<>();
			ArrayList<String> topLevelTermSynonyms = new ArrayList<>();

			ArrayList<String> selectedTopLevelIds = new ArrayList<>();
			ArrayList<String> selectedTopLevelTerms = new ArrayList<>();
			ArrayList<String> selectedTopLevelTermSynonyms = new ArrayList<>();

			ArrayList<String> intermediateLevelIds = new ArrayList<>();
			ArrayList<String> intermediateLevelTerms = new ArrayList<>();
			ArrayList<String> intermediateLevelTermSynonyms = new ArrayList<>();
			for (String paramString : imageDTO.getParameterAssociationStableId()) {
				//System.out.println("paramString"+paramString);
				if (stableIdToTermIdMap.containsKey(paramString)) {
					String thisTermId = stableIdToTermIdMap.get(paramString);
					//System.out.println("thisTermId="+thisTermId);

					int level = 0; // use to determine top level and selected top levels: differs from ontology to ontology
					if ( thisTermId.startsWith("MA:") || thisTermId.startsWith("EMAPA:") ){
						level = 2;
					}
					if (thisTermId.startsWith("MP:")){
						level = 1;
//						System.out.println("starts with MP="+thisTermId);
					}

					termIds.add(thisTermId);


					OntologyTermBean termBean = ontologyDAO.getTerm(thisTermId);
					//System.out.println("termbean="+termBean);
					if (termBean != null) {

						terms.add(termBean.getName());

						if ( termBean.getSynonyms() != null) {
							termSynonyms.addAll(termBean.getSynonyms());

							List<String> termSynIdTerm = new ArrayList<>();
							for(String termSyn : termBean.getSynonyms()){
								termSynIdTerm.add(termSyn + fieldSeparator + termBean.getTermIdTermName());
							}
							imageDTO.setAnatomyTermSynonymAnatomyIdTerm(termSynIdTerm);
						}


						List<OntologyTermBean> topLevels = ontologyDAO.getTopLevel(thisTermId, level);

						for (OntologyTermBean topLevel : topLevels) {
							if (!topLevelIds.contains(topLevel.getId())) {
								topLevelIds.add(topLevel.getId());
								topLevelTerms.add(topLevel.getName());
								topLevelTermSynonyms.addAll(topLevel.getSynonyms());
							}
						}

						OntologyDetail selectedTopLevels = ontologyDAO.getSelectedTopLevelDetails(thisTermId);

						List<String> selectedTopLevelIdIdTerm = new ArrayList<>();
						List<String> selectedTopLevelTermIdTerm = new ArrayList<>();
						List<String> selectedTopLevelTermSynIdTerm = new ArrayList<>();


						if (selectedTopLevels.getIds() != null) {
							for (String id : selectedTopLevels.getIds()) {
								if (! selectedTopLevelIds.contains(id)) {
									selectedTopLevelIds.add(id);
									selectedTopLevelIdIdTerm.add(id + fieldSeparator + termBean.getTermIdTermName());
								}
							}
						}
						if (selectedTopLevels.getNames() != null) {
							for (String name : selectedTopLevels.getNames()) {
								if (! selectedTopLevelTerms.contains(name)) {
									selectedTopLevelTerms.add(name);
									selectedTopLevelTermIdTerm.add(name + fieldSeparator + termBean.getTermIdTermName());
								}
							}
						}
						if (selectedTopLevels.getSynonyms() != null) {
							for (String syn : selectedTopLevels.getSynonyms()) {
								if (! selectedTopLevelTermSynonyms.contains(syn)) {
									selectedTopLevelTermSynonyms.add(syn);
									selectedTopLevelTermSynIdTerm.add(syn + fieldSeparator + termBean.getTermIdTermName());
								}
							}
						}

						imageDTO.setSelectedTopLevelAnatomyIdAnatomyIdTerm(selectedTopLevelIdIdTerm);
						imageDTO.setSelectedTopLevelAnatomyTermAnatomyIdTerm(selectedTopLevelTermIdTerm);
						imageDTO.setSelectedTopLevelAnatomyTermSynonymAnatomyIdTerm(selectedTopLevelTermSynIdTerm);


						List<OntologyTermBean> intermediateLevels = ontologyDAO.getIntermediates(thisTermId);

						List<String> intermediateIdIdTerm = new ArrayList<>();
						List<String> intermediateTermIdTerm = new ArrayList<>();
						List<String> intermediateTermSynIdTerm = new ArrayList<>();

						for (OntologyTermBean intermediateLevel : intermediateLevels) {
							String id = intermediateLevel.getId();
							if (!intermediateLevelIds.contains(id)) {
								intermediateLevelIds.add(id);
								intermediateIdIdTerm.add(id + fieldSeparator + termBean.getTermIdTermName());
							}

							String name = intermediateLevel.getName();
							if (!intermediateLevelTerms.contains(name)) {
								intermediateLevelTerms.add(name);
								intermediateTermIdTerm.add(name + fieldSeparator + termBean.getTermIdTermName());
							}

							if (intermediateLevel.getSynonyms() != null) {
								for (String syn : intermediateLevel.getSynonyms()) {
									intermediateLevelTermSynonyms.add(syn);
									intermediateTermSynIdTerm.add(syn + fieldSeparator + termBean.getTermIdTermName());
								}
							}
						}

						imageDTO.setIntermediateAnatomyIdAnatomyIdTerm(intermediateIdIdTerm);
						imageDTO.setIntermediateAnatomyTermAnatomyIdTerm(intermediateTermIdTerm);
						imageDTO.setIntermediateAnatomyTermSynonymAnatomyIdTerm(intermediateTermSynIdTerm);



//						for (OntologyTermBean intermediateLevel : intermediateLevels) {
//							// System.out.println(topLevel.getName());
//							if (!intermediateLevelIds.contains(intermediateLevel.getId())) {
//								intermediateLevelIds.add(intermediateLevel.getId());
//								intermediateLevelTerms.add(intermediateLevel.getName());
//								intermediateLevelTermSynonyms.addAll(intermediateLevel.getSynonyms());
//							}
//						}
					}
					// <field name="selected_top_level_ma_id" type="string"
					// indexed="true" stored="true" required="false"
					// multiValued="true" />
					// <field name="selected_top_level_ma_term" type="string"
					// indexed="true" stored="true" required="false"
					// multiValued="true" />
					// <field name="selected_top_level_ma_term_synonym"
					// type="string" indexed="true" stored="true"
					// required="false" multiValued="true" />

					// selected_top_level_ma_term
					// String selectedTopLevelMaTerm=
				}
				// IndexerMap.get
			}

			if (ontologyDAO instanceof EmapaOntologyDAO) {
				if (! termIds.isEmpty()) {
					imageDTO.setAnatomyId(termIds);

					ArrayList<String> emapaIdTerms = new ArrayList<>();
					for (int i = 0; i < termIds.size(); i++) {
						String emapaId = termIds.get(i);

						imageDTO.setStage("embryo");
						try {
							String emapaTerm = terms.get(i);
							emapaIdTerms.add(emapaId + "_" + emapaTerm);
						} catch (Exception e) {
							runStatus.addWarning(" Could not find term when indexing EMAPA " + emapaId + ". Exception: "
									+ e.getLocalizedMessage());
						}
					}
					imageDTO.setAnatomyIdTerm(emapaIdTerms);
				}
				if (!terms.isEmpty()) {
					imageDTO.setAnatomyTerm(terms);
				}
				if (!termSynonyms.isEmpty()) {
					imageDTO.setAnatomyTermSynonym(termSynonyms);
				}
				if (!topLevelIds.isEmpty()) {
					imageDTO.setTopLevelAnatomyId(topLevelIds);
				}
				if (!topLevelTerms.isEmpty()) {
					imageDTO.setTopLevelAnatomyTerm(topLevelTerms);
				}
				if (!topLevelTermSynonyms.isEmpty()) {
					imageDTO.setTopLevelAnatomyTermSynonym(topLevelTermSynonyms);
				}
				if (!selectedTopLevelIds.isEmpty()) {
					imageDTO.setSelectedTopLevelAnatomyId(selectedTopLevelIds);
				}
				if (!selectedTopLevelTerms.isEmpty()) {
					imageDTO.setSelectedTopLevelAnatomyTerm(selectedTopLevelTerms);
				}
				if (!selectedTopLevelTermSynonyms.isEmpty()) {
					imageDTO.setSelectedTopLevelAnatomyTermSynonym(selectedTopLevelTermSynonyms);
				}
				if (!intermediateLevelIds.isEmpty()) {
					imageDTO.setIntermediateAnatomyId(intermediateLevelIds);
				}
				if (!intermediateLevelTerms.isEmpty()) {
					imageDTO.setIntermediateAnatomyTerm(intermediateLevelTerms);
				}
				if (!intermediateLevelTermSynonyms.isEmpty()) {
					imageDTO.setIntermediateAnatomyTermSynonym(intermediateLevelTermSynonyms);
				}
			}

			if (ontologyDAO instanceof MaOntologyDAO) {

				if (!termIds.isEmpty()) {
					imageDTO.setAnatomyId(termIds);

					ArrayList<String> maIdTerms = new ArrayList<>();
					for (int i = 0; i < termIds.size(); i++) {
						String maId = termIds.get(i);

						imageDTO.setStage("adult");

						try {

							// index UBERON/EFO id for MA id
							if (maUberonEfoMap.containsKey(maId)) {

								if (maUberonEfoMap.get(maId).containsKey("uberon_id")) {
									for (String id : maUberonEfoMap.get(maId).get("uberon_id")) {
										imageDTO.addUberonId(id);
									}
								}
								if (maUberonEfoMap.get(maId).containsKey("efo_id")) {
									for (String id : maUberonEfoMap.get(maId).get("efo_id")) {
										imageDTO.addEfoId(id);
									}
								}
							}

							String maTerm = terms.get(i);
							maIdTerms.add(maId + "_" + maTerm);
						} catch (Exception e) {
							runStatus.addWarning(" Could not find term when indexing MA " + maId + ". i = " + i + ". termIds = " + StringUtils.join(termIds, ", ") + ". LocalizedMessage: "
									+ e.getLocalizedMessage());
						}
					}
					imageDTO.setAnatomyIdTerm(maIdTerms);
				}

				if (!terms.isEmpty()) {
					imageDTO.setAnatomyTerm(terms);
				}
				if (!termSynonyms.isEmpty()) {
					imageDTO.setAnatomyTermSynonym(termSynonyms);
				}
				if (!topLevelIds.isEmpty()) {
					imageDTO.setTopLevelAnatomyId(topLevelIds);
				}
				if (!topLevelTerms.isEmpty()) {
					imageDTO.setTopLevelAnatomyTerm(topLevelTerms);
				}
				if (!topLevelTermSynonyms.isEmpty()) {
					imageDTO.setTopLevelAnatomyTermSynonym(topLevelTermSynonyms);
				}
				if (!selectedTopLevelIds.isEmpty()) {
					imageDTO.setSelectedTopLevelAnatomyId(selectedTopLevelIds);
				}
				if (!selectedTopLevelTerms.isEmpty()) {
					imageDTO.setSelectedTopLevelAnatomyTerm(selectedTopLevelTerms);
				}
				if (!selectedTopLevelTermSynonyms.isEmpty()) {
					imageDTO.setSelectedTopLevelAnatomyTermSynonym(selectedTopLevelTermSynonyms);
				}
				if (!intermediateLevelIds.isEmpty()) {
					imageDTO.setIntermediateAnatomyId(intermediateLevelIds);
				}
				if (!intermediateLevelTerms.isEmpty()) {
					imageDTO.setIntermediateAnatomyTerm(intermediateLevelTerms);
				}
				if (!intermediateLevelTermSynonyms.isEmpty()) {
					imageDTO.setIntermediateAnatomyTermSynonym(intermediateLevelTermSynonyms);
				}
			}

			if (ontologyDAO instanceof MpOntologyDAO) {
				//System.out.println("instance of mp ontology DAO");
				if (!termIds.isEmpty()) {
//					System.out.println("setting mp term ids="+termIds);
					imageDTO.setMpTermId(termIds);

					ArrayList<String> mpIdTerms = new ArrayList<>();
					for (int i = 0; i < termIds.size(); i++) {
						String mpId = termIds.get(i);

						try {
							String mpTerm = terms.get(i);
							mpIdTerms.add(mpId + "_" + mpTerm);
						} catch (Exception e) {
							runStatus.addWarning(" Could not find term when indexing MP " + mpId + ". Exception: "
									+ e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
					imageDTO.setMpIdTerm(mpIdTerms);
				}
				if (!terms.isEmpty()) {
					imageDTO.setMpTerm(terms);
				}

				if (!termSynonyms.isEmpty()) {
					imageDTO.setMpTermSynonym(termSynonyms);
				}

			}
		}
	}

	/**
	 * Get the image urls from the db using the download_path from Harwell for
	 * images that have an omero_id that should have already bean set by the
	 * python scripts that update the impc_images dev.
	 *
	 * @throws IndexerException
	 */
	private Map<String, ImageBean> populateImageUrls() throws IndexerException {

		Map<String, ImageBean> imageBeansMap = new HashMap<>();
		final String getExtraImageInfoSQL = "SELECT " + ImageDTO.OMERO_ID + ", " + ImageDTO.DOWNLOAD_FILE_PATH + ", "
				+ ImageDTO.IMAGE_LINK
				+ ", "
				+ ImageDTO.FULL_RESOLUTION_FILE_PATH
				+","
				+ ImageDTO.INCREMENT_VALUE
				+ " FROM image_record_observation WHERE omero_id is not null AND omero_id != 0";

		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(getExtraImageInfoSQL)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				ImageBean bean = new ImageBean();
				bean.omeroId = resultSet.getInt(ImageDTO.OMERO_ID);
				bean.fullResFilePath = resultSet.getString(ImageDTO.FULL_RESOLUTION_FILE_PATH);
				bean.image_link = resultSet.getString(ImageDTO.IMAGE_LINK);
				String inc=resultSet.getString(ImageDTO.INCREMENT_VALUE);
				if(inc!=null && !inc.equals("")){
					if(inc.equals("one")){//over 3304 entries are one not 1, but no twos etc!
						bean.increment=1;
					}else{
						if(isInteger(inc)){
							bean.increment=Integer.parseInt(inc);
						}
					}
				}
				imageBeansMap.put(resultSet.getString(ImageDTO.DOWNLOAD_FILE_PATH), bean);

			}

		} catch (Exception e) {
			throw new IndexerException(e);
		}

		return imageBeansMap;
	}

	public static boolean isInteger(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch(NumberFormatException e) {
	    	e.printStackTrace();
	        return false;
	    } catch(NullPointerException e) {
	    	e.printStackTrace();
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}



	private class ImageBean {
		Integer increment=0;
		int omeroId;
		String fullResFilePath;
		String image_link;
	}

	public Map<String, List<AlleleDTO>> populateAlleles() throws IndexerException {

		return IndexerMap.getGeneToAlleles(alleleCore);
	}

	private void populateImageDtoStatuses(ImageDTO img, String geneAccession) {

		if (alleles.containsKey(geneAccession)) {
			List<AlleleDTO> localAlleles = alleles.get(geneAccession);
			for (AlleleDTO allele : localAlleles) {

				// so some of the fields below a that we have multiples for for
				// SangerIMages we only have one for ObservationDTOs??????
				// <field column="marker_symbol"
				// xpath="/response/result/doc/str[@name='marker_symbol']" />
				// <field column="marker_name"
				if (allele.getMarkerSymbol() != null) {
					img.addSymbol(allele.getMarkerSymbol());
				}
				if (allele.getMarkerName() != null) {
					img.addMarkerName(allele.getMarkerName());
				}

				// // xpath="/response/result/doc/str[@name='marker_name']" />
				// // <field column="marker_synonym"
				if (allele.getMarkerSynonym() != null) {
					img.addMarkerSynonym(allele.getMarkerSynonym());
				}

				// //
				// xpath="/response/result/doc/arr[@name='marker_synonym']/str"
				// // />
				// // <field column="marker_type"
				if (allele.getMarkerType() != null) {
					img.addMarkerType(allele.getMarkerType());

				}

				// xpath="/response/result/doc/str[@name='marker_type']" />
				if (allele.getHumanGeneSymbol() != null) {
					img.addHumanGeneSymbol(allele.getHumanGeneSymbol());
				}
				// <field column="human_gene_symbol"
				// xpath="/response/result/doc/arr[@name='human_gene_symbol']/str"
				// />
				//
				if (allele.getStatus() != null) {
					// logger.info(" Adding status="+allele.getStatus());
					img.addStatus(allele.getStatus());
				}
				// <!-- latest project status (ES cells/mice production status)
				// -->
				// <field column="status"
				// xpath="/response/result/doc/str[@name='status']"
				// />
				//
				if (allele.getImitsPhenotypeStarted() != null) {
					img.addImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
				}
				// <!-- latest mice phenotyping status for faceting -->
				// <field column="imits_phenotype_started"
				// xpath="/response/result/doc/str[@name='imits_phenotype_started']"
				// />
				// <field column="imits_phenotype_complete"
				if (allele.getImitsPhenotypeComplete() != null) {
					img.addImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
				}
				// xpath="/response/result/doc/str[@name='imits_phenotype_complete']"
				// />
				// <field column="imits_phenotype_status"
				if (allele.getImitsPhenotypeStatus() != null) {
					img.addImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
				}
				// xpath="/response/result/doc/str[@name='imits_phenotype_status']"
				// />
				//
				// <!-- phenotyping status -->
				// <field column="latest_phenotype_status"
				// xpath="/response/result/doc/str[@name='latest_phenotype_status']"
				// />
				if (allele.getLegacyPhenotypeStatus() != null) {
					img.setLegacyPhenotypeStatus(allele.getLegacyPhenotypeStatus());
				}
				// <field column="legacy_phenotype_status"
				// xpath="/response/result/doc/int[@name='legacy_phenotype_status']"
				// />
				//
				// <!-- production/phenotyping centers -->
				img.setLatestProductionCentre(allele.getLatestProductionCentre());
				// <field column="latest_production_centre"
				// xpath="/response/result/doc/arr[@name='latest_production_centre']/str"
				// />
				// <field column="latest_phenotyping_centre"
				img.setLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
				// xpath="/response/result/doc/arr[@name='latest_phenotyping_centre']/str"
				// />
				//
				// <!-- alleles of a gene -->
				img.setAlleleName(allele.getAlleleName());
				// <field column="allele_name"
				// xpath="/response/result/doc/arr[@name='allele_name']/str" />
				//
				// </entity>
				img.setSubtype(allele.getMarkerType());
				img.addLatestPhenotypeStatus(allele.getLatestPhenotypeStatus());
				if (img.getLegacyPhenotypeStatus() != null) {
					img.setLegacyPhenotypeStatus(allele.getLegacyPhenotypeStatus());
				}
			}
		}
	}

	public Map<String, String> populateParameterStableIdToMaIdMap() throws SQLException {
		Map<String, String> paramToMa = new HashMap<String, String>();
		String query = "SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=8 LIMIT 1000000";
		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String parameterStableId = resultSet.getString("stable_id");
				String maAcc = resultSet.getString("ontology_acc");
				paramToMa.put(parameterStableId, maAcc);
			}
		}
		logger.debug(" paramToMa size = " + paramToMa.size());
		return paramToMa;
	}

	public Map<String, String> populateParameterStableIdToMpIdMap() throws SQLException {
		Map<String, String> paramToMp = new HashMap<String, String>();
		String query = "SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=5 and ppoa.event_type='abnormal' LIMIT 1000000";
		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String parameterStableId = resultSet.getString("stable_id");
				String mpAcc = resultSet.getString("ontology_acc");
				//System.out.println("parameterStableId="+parameterStableId+" mpAcc="+mpAcc);
				paramToMp.put(parameterStableId, mpAcc);
			}
		}
		logger.debug(" paramToMp size = " + paramToMp.size());
		return paramToMp;
	}

	// SELECT * FROM phenotype_parameter pp INNER JOIN
	// phenotype_parameter_lnk_ontology_annotation pploa ON
	// pp.id=pploa.parameter_id INNER JOIN
	// phenotype_parameter_ontology_annotation ppoa ON
	// ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=14 LIMIT 100000

	// we then map EMAP to EMAPA using the mapping file from PURL
	public Map<String, String> populateParameterStableIdToEmapaIdMap() throws SQLException {
		Map<String, String> paramToEmapa = new HashMap<String, String>();
		String query = "SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=14 LIMIT 1000000";
		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();

			int counter = 0;
			while (resultSet.next()) {

				String parameterStableId = resultSet.getString("stable_id");
				String acc = resultSet.getString("ontology_acc");
				if (emap2EmapaMap.get(acc) != null) {
					String emapaAcc = emap2EmapaMap.get(acc).getEmapaId();
					//System.out.println(acc + " mapped to " + emapaAcc);
					paramToEmapa.put(parameterStableId, emapaAcc);
				}
				else {
					counter++;
				}
			}
			logger.info("MISSED: " + counter);
		}
		logger.debug(" paramToMa size = " + paramToEmapa.size());
		return paramToEmapa;
	}

}
