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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.beans.OntologyTermBean;
import org.mousephenotype.cda.db.dao.EmapOntologyDAO;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.db.dao.OntologyDAO;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 *
 * @author jwarren
 */
public class ImpcImagesIndexer extends AbstractIndexer {
	CommonUtils commonUtils = new CommonUtils();
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("observationReadOnlyIndexing")
	private SolrServer observationCore;

	@Autowired
	@Qualifier("impcImagesIndexing")
	SolrServer impcImagesCore;

	@Autowired
	@Qualifier("alleleReadOnlyIndexing")
	SolrServer alleleIndexing;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	MaOntologyDAO maService;

	@Autowired
	EmapOntologyDAO emapService;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	@Value("classpath:uberonEfoMa_mappings.txt")
	org.springframework.core.io.Resource resource;

	private Map<String, List<AlleleDTO>> alleles;
	private Map<String, ImageBean> imageBeans;
	String excludeProcedureStableId = "";

	private Map<String, String> parameterStableIdToMaTermIdMap;

	private String impcMediaBaseUrl;
	private String impcAnnotationBaseUrl;

	private Map<String, Map<String, List<String>>> maUberonEfoMap = new HashMap(); // key:
																					// MA
																					// id
	private Map<String, String> parameterStableIdToEmapTermIdMap = new HashMap(); // key:
																					// EMAP
																					// id;

	public ImpcImagesIndexer() {
		super();
	}

	@Override
	public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(impcImagesCore);
	}

	public static void main(String[] args) throws IndexerException, SQLException {

		RunStatus runStatus = new RunStatus();
		ImpcImagesIndexer main = new ImpcImagesIndexer();
		main.initialise(args, runStatus);
		main.run();
		main.validateBuild();
	}

	@Override
	public RunStatus run() throws IndexerException {

		RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try {
			parameterStableIdToMaTermIdMap = this.populateParameterStableIdToMaIdMap();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			parameterStableIdToEmapTermIdMap = this.populateParameterStableIdToEmapIdMap();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		imageBeans = populateImageUrls();
		// logger.info(" added {} total Image URL beans", imageBeans.size());

		if (imageBeans.size() < 100) {
			runStatus.addError(
					" Didn't get any image entries from the db with omero_ids set so exiting the impc_image Indexer!!");
		}

		this.alleles = populateAlleles();
		// logger.info(" Added {} total allele beans", alleles.size());

		String impcMediaBaseUrl = config.get("impcMediaBaseUrl");
		String pdfThumbnailUrl = config.get("pdfThumbnailUrl");
		// logger.info(" omeroRootUrl=" + impcMediaBaseUrl);
		impcAnnotationBaseUrl = impcMediaBaseUrl.replace("webgateway", "webclient");

		try {
			maUberonEfoMap = IndexerMap.mapMaToUberronOrEfo(resource);
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
		}

		try {

			impcImagesCore.deleteByQuery("*:*");

			SolrQuery query = ImageService.allImageRecordSolrQuery().setRows(Integer.MAX_VALUE);

			List<ImageDTO> imageList = observationCore.query(query).getBeans(ImageDTO.class);
			for (ImageDTO imageDTO : imageList) {

				String downloadFilePath = imageDTO.getDownloadFilePath();
				if (imageBeans.containsKey(downloadFilePath)) {

					ImageBean iBean = imageBeans.get(downloadFilePath);
					String fullResFilePath = iBean.fullResFilePath;
					if (iBean.image_link != null) {
						imageDTO.setImageLink(iBean.image_link);
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
						System.out.println("skipping omeroId=" + omeroId + "param and center"
								+ imageDTO.getParameterStableId() + imageDTO.getPhenotypingCenter());
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
							}
						}
					}

					addOntology(runStatus, imageDTO, parameterStableIdToMaTermIdMap, maService);
					addOntology(runStatus, imageDTO, parameterStableIdToEmapTermIdMap, emapService);

					impcImagesCore.addBean(imageDTO);
					documentCount++;
				}
			}

			impcImagesCore.commit();

		} catch (SolrServerException | IOException e) {
			throw new IndexerException(e);
		}

		logger.info(" Added {} total beans in {}", documentCount,
				commonUtils.msToHms(System.currentTimeMillis() - start));

		return runStatus;
	}

	private void addOntology(RunStatus runStatus, ImageDTO imageDTO, Map<String, String> stableIdToTermIdMap,
			OntologyDAO ontologyDAO) {
		if (imageDTO.getParameterAssociationStableId() != null
				&& !imageDTO.getParameterAssociationStableId().isEmpty()) {

			ArrayList<String> maIds = new ArrayList<>();
			ArrayList<String> maTerms = new ArrayList<>();
			ArrayList<String> maTermSynonyms = new ArrayList<>();
			ArrayList<String> topLevelMaIds = new ArrayList<>();
			ArrayList<String> topLevelMaTerm = new ArrayList<>();
			ArrayList<String> topLevelMaTermSynonym = new ArrayList<>();

			ArrayList<String> intermediateLevelMaIds = new ArrayList<>();
			ArrayList<String> intermediateLevelMaTerm = new ArrayList<>();
			ArrayList<String> intermediateLevelMaTermSynonym = new ArrayList<>();
			for (String paramString : imageDTO.getParameterAssociationStableId()) {
				if (stableIdToTermIdMap.containsKey(paramString)) {
					String maTermId = stableIdToTermIdMap.get(paramString);
					maIds.add(maTermId);

					OntologyTermBean maTermBean = ontologyDAO.getTerm(maTermId);
					if (maTermBean != null) {
						maTerms.add(maTermBean.getName());
						maTermSynonyms.addAll(maTermBean.getSynonyms());
						List<OntologyTermBean> topLevels = maService.getTopLevel(maTermId);
						for (OntologyTermBean topLevel : topLevels) {
							// System.out.println(topLevel.getName());
							if (!topLevelMaIds.contains(topLevel.getId())) {
								topLevelMaIds.add(topLevel.getId());
								topLevelMaTerm.add(topLevel.getName());
								topLevelMaTermSynonym.addAll(topLevel.getSynonyms());
							}
						}

						List<OntologyTermBean> intermediateLevels = maService.getIntermediates(maTermId);
						for (OntologyTermBean intermediateLevel : intermediateLevels) {
							// System.out.println(topLevel.getName());
							if (!intermediateLevelMaIds.contains(intermediateLevel.getId())) {
								intermediateLevelMaIds.add(intermediateLevel.getId());
								intermediateLevelMaTerm.add(intermediateLevel.getName());
								intermediateLevelMaTermSynonym.addAll(intermediateLevel.getSynonyms());
							}
						}
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

			if (ontologyDAO instanceof EmapOntologyDAO) {
				if (!maIds.isEmpty()) {
					imageDTO.setMaTermId(maIds);

					ArrayList<String> maIdTerms = new ArrayList<>();
					for (int i = 0; i < maIds.size(); i++) {
						String maId = maIds.get(i);

						try {

							// index UBERON/EFO id for MA id
//							if (maUberonEfoMap.containsKey(maId)) {
//
//								if (maUberonEfoMap.get(maId).containsKey("uberon_id")) {
//									for (String id : maUberonEfoMap.get(maId).get("uberon_id")) {
//										imageDTO.addUberonId(id);
//									}
//								}
//								if (maUberonEfoMap.get(maId).containsKey("efo_id")) {
//									for (String id : maUberonEfoMap.get(maId).get("efo_id")) {
//										imageDTO.addEfoId(id);
//									}
//								}
//							}

							String maTerm = maTerms.get(i);
							maIdTerms.add(maId + "_" + maTerm);
						} catch (Exception e) {
							runStatus.addWarning(" Could not find term when indexing EMAP " + maId + ". Exception: "
									+ e.getLocalizedMessage());
						}
					}
					imageDTO.setEmapIdTerm(maIdTerms);
				}
				if (!maTerms.isEmpty()) {
					imageDTO.setEmapTerm(maTerms);
				}

				if (!maTermSynonyms.isEmpty()) {
					imageDTO.setEmapTermSynonym(maTermSynonyms);
				}
				if (!topLevelMaIds.isEmpty()) {
					imageDTO.setTopLevelEmapIds(topLevelMaIds);
				}
				if (!topLevelMaTerm.isEmpty()) {
					imageDTO.setTopLeveEmapTerm(topLevelMaTerm);
				}
				if (!topLevelMaTermSynonym.isEmpty()) {
					imageDTO.setTopLevelEmapTermSynonym(topLevelMaTermSynonym);
				}
				if (!intermediateLevelMaIds.isEmpty()) {
					imageDTO.setIntermediateLevelEmapId(intermediateLevelMaIds);
				}
				if (!intermediateLevelMaTerm.isEmpty()) {
					imageDTO.setIntermediateLevelEmapTerm(intermediateLevelMaTerm);
				}
				if (!intermediateLevelMaTermSynonym.isEmpty()) {
					imageDTO.setIntermediateLevelEmapTermSynonym(intermediateLevelMaTermSynonym);
				}
			}
			
			if (ontologyDAO instanceof MaOntologyDAO) {
				if (!maIds.isEmpty()) {
					imageDTO.setMaTermId(maIds);

					ArrayList<String> maIdTerms = new ArrayList<>();
					for (int i = 0; i < maIds.size(); i++) {
						String maId = maIds.get(i);

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

							String maTerm = maTerms.get(i);
							maIdTerms.add(maId + "_" + maTerm);
						} catch (Exception e) {
							runStatus.addWarning(" Could not find term when indexing MA " + maId + ". Exception: "
									+ e.getLocalizedMessage());
						}
					}
					imageDTO.setMaIdTerm(maIdTerms);
				}
				if (!maTerms.isEmpty()) {
					imageDTO.setMaTerm(maTerms);
				}

				if (!maTermSynonyms.isEmpty()) {
					imageDTO.setMaTermSynonym(maTermSynonyms);
				}
				if (!topLevelMaIds.isEmpty()) {
					imageDTO.setTopLevelMaId(topLevelMaIds);
				}
				if (!topLevelMaTerm.isEmpty()) {
					imageDTO.setTopLevelMaTerm(topLevelMaTerm);
				}
				if (!topLevelMaTermSynonym.isEmpty()) {
					imageDTO.setTopLevelMaTermSynonym(topLevelMaTermSynonym);
				}
				if (!intermediateLevelMaIds.isEmpty()) {
					imageDTO.setIntermediateLevelMaId(intermediateLevelMaIds);
				}
				if (!intermediateLevelMaTerm.isEmpty()) {
					imageDTO.setIntermediateLevelMaTerm(intermediateLevelMaTerm);
				}
				if (!intermediateLevelMaTermSynonym.isEmpty()) {
					imageDTO.setIntermediateLevelMaTermSynonym(intermediateLevelMaTermSynonym);
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
				+ ImageDTO.IMAGE_LINK + ", " + ImageDTO.FULL_RESOLUTION_FILE_PATH
				+ " FROM image_record_observation WHERE omero_id is not null AND omero_id != 0";

		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(getExtraImageInfoSQL)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {

				ImageBean bean = new ImageBean();
				bean.omeroId = resultSet.getInt(ImageDTO.OMERO_ID);
				bean.fullResFilePath = resultSet.getString(ImageDTO.FULL_RESOLUTION_FILE_PATH);
				bean.image_link = resultSet.getString(ImageDTO.IMAGE_LINK);
				imageBeansMap.put(resultSet.getString(ImageDTO.DOWNLOAD_FILE_PATH), bean);

			}

		} catch (Exception e) {
			throw new IndexerException(e);
		}

		return imageBeansMap;
	}

	private class ImageBean {
		int omeroId;
		String fullResFilePath;
		String image_link;
	}

	public Map<String, List<AlleleDTO>> populateAlleles() throws IndexerException {

		return IndexerMap.getGeneToAlleles(alleleIndexing);
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
	// SELECT * FROM phenotype_parameter pp INNER JOIN
	// phenotype_parameter_lnk_ontology_annotation pploa ON
	// pp.id=pploa.parameter_id INNER JOIN
	// phenotype_parameter_ontology_annotation ppoa ON
	// ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=14 LIMIT 100000

	public Map<String, String> populateParameterStableIdToEmapIdMap() throws SQLException {
		Map<String, String> paramToEmap = new HashMap<String, String>();
		String query = "SELECT * FROM phenotype_parameter pp INNER JOIN phenotype_parameter_lnk_ontology_annotation pploa ON pp.id=pploa.parameter_id INNER JOIN phenotype_parameter_ontology_annotation ppoa ON ppoa.id=pploa.annotation_id WHERE ppoa.ontology_db_id=14 LIMIT 1000000";
		try (PreparedStatement statement = komp2DataSource.getConnection().prepareStatement(query)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String parameterStableId = resultSet.getString("stable_id");
				String emapAcc = resultSet.getString("ontology_acc");
				paramToEmap.put(parameterStableId, emapAcc);
			}
		}
		logger.debug(" paramToMa size = " + paramToEmap.size());
		return paramToEmap;
	}

}