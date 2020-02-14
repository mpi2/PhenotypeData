/* Copyright 2015 EMBL - European Bioinformatics Institute
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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.indexers.utils.PhisService;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 *
 * @author jwarren
 * @author ilinca
 */
@EnableAutoConfiguration
public class ImpcImagesIndexer extends AbstractIndexer implements CommandLineRunner {

	@Value("classpath:uberonEfoMa_mappings.txt")
	org.springframework.core.io.Resource anatomogramResource;

	@Value("${impc_media_base_url}")
	private String impcMediaBaseUrl;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private       List<ImageDTO>               secondaryProjectImages            = new ArrayList<>();
	private       PhisService                  phisService                       = new PhisService();
	private       Map<String, List<AlleleDTO>> alleles;
	private       Map<String, ImageBean>       imageBeans;
	private       Map<String, String>          parameterStableIdToMaTermIdMap;
	private       Map<String, String>          parameterStableIdToEmapaTermIdMap = new HashMap<>(); // key: EMAPA id;
	private       Map<String, String>          parameterStableIdToMpTermIdMap;
	private       Map<String, String>          emap2EmapaMap;
	private       Map<String, Set<String>>     primaryGenesProcedures; //  we need to know which genes have images for which procedures so that we don't overwrite them from PhenoImageShare.
	private       String                       impcAnnotationBaseUrl;
	private final String                       FIELD_SEPARATOR                   = "___";

	private OntologyParser        emapaParser;
	private OntologyParser        maParser;
	private OntologyParser        mpParser;
	private OntologyParser        uberonParser;
	private OntologyParserFactory ontologyParserFactory;

	private SolrClient     alleleCore;
	private SolrClient     experimentCore;
	private ImpressService impressService;
	private SolrClient     impcImagesCore;

	protected ImpcImagesIndexer() {

	}

	@Inject
	public ImpcImagesIndexer(
			@NotNull DataSource komp2DataSource,
			@NotNull OntologyTermRepository ontologyTermRepository,
			@NotNull SolrClient alleleCore,
			@NotNull SolrClient experimentCore,
			@NotNull ImpressService impressService,
			@NotNull SolrClient impcImagesCore)
	{
		super(komp2DataSource, ontologyTermRepository);
		this.alleleCore = alleleCore;
		this.experimentCore = experimentCore;
		this.impressService = impressService;
		this.impcImagesCore = impcImagesCore;
	}

	@Override
	public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(impcImagesCore);
	}

	public static void main(String[] args) {

		ConfigurableApplicationContext context = new SpringApplicationBuilder(ImpcImagesIndexer.class)
				.web(WebApplicationType.NONE)
				.bannerMode(Banner.Mode.OFF)
				.logStartupInfo(false)
				.run(args);

		context.close();
	}


	@Override
	public RunStatus run() throws IndexerException, SQLException, IOException {

		RunStatus runStatus = new RunStatus();
		long start = System.currentTimeMillis();

		try {
			ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
			mpParser = ontologyParserFactory.getMpParser();
			maParser = ontologyParserFactory.getMaParser();
			uberonParser = ontologyParserFactory.getUberonParser();
			emapaParser = ontologyParserFactory.getEmapaParser();
		} catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
			e.printStackTrace();
		}

		try {
			parameterStableIdToMaTermIdMap = this.populateParameterStableIdToMaIdMap();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			logger.info("  Building parameter to abnormal mp map");
			parameterStableIdToMpTermIdMap = this.populateParameterStableIdToMpIdMap();
			logger.info("  Parameter to abnormal mp map size="+parameterStableIdToMpTermIdMap.size());
			//logger.debug("parameterStableIdToMpTermIdMap"+parameterStableIdToMpTermIdMap);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			logger.info("  Started emap mapping...");
			emap2EmapaMap = ontologyParserFactory.getEmapToEmapaMap();
			logger.info("  Done {} EMAP to EMAPA mappings", emap2EmapaMap.size());
			parameterStableIdToEmapaTermIdMap = this.populateParameterStableIdToEmapaIdMap();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		imageBeans = populateImageUrls();
		logger.info(" Added {} total Image URL beans", imageBeans.size());

		if (imageBeans.size() < 100) {
			runStatus.addError(
					"Didn't get any image entries from the db with omero_ids set so exiting the impc_image Indexer!!");
		}

		this.alleles = populateAlleles();
		logger.info(" Added {} total allele beans", alleles.size());


		// logger.info("  omeroRootUrl=" + impcMediaBaseUrl);
		impcAnnotationBaseUrl = impcMediaBaseUrl.replace("webgateway", "webclient");

		try {
			List<ImageDTO> imageList=new ArrayList<>();
			//populate image DTOs from phis solr dto objects
			logger.info("  Starting indexing.....");
			impcImagesCore.deleteByQuery("*:*");
			SolrQuery query = ImageService.allImageRecordSolrQuery().setRows(Integer.MAX_VALUE);
			List<ImageDTO> imagePrimaryList = experimentCore.query(query).getBeans(ImageDTO.class);
			primaryGenesProcedures = getPrimaryImagesByGeneAndProcedure(imagePrimaryList);

			try {
				this.secondaryProjectImages =populateSecondaryProjectImages(runStatus);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			imageList.addAll(secondaryProjectImages);
			logger.debug("primary imageList size is "+imagePrimaryList.size());
			imageList.addAll(imagePrimaryList);
		
			
			for (ImageDTO imageDTO : imageList) {

				int omeroId=0;

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

					omeroId = iBean.omeroId;
					imageDTO.setOmeroId(omeroId);
				}

				if (omeroId == 0 && imageDTO.getFullResolutionFilePath() == null) {// modified this so phis images should be loaded now
					continue;
				}

				// need to add a full path to image in omero as part of api
				// e.g.
				// https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/4855/
				if (omeroId != 0 && downloadFilePath != null) {
					if (downloadFilePath.endsWith(".pdf")) {
						// http://wwwdev.ebi.ac.uk/mi/media/omero/webclient/annotation/119501/
						imageDTO.setDownloadUrl(impcAnnotationBaseUrl + "/annotation/" + omeroId);
						imageDTO.setJpegUrl(Constants.PDF_THUMBNAIL_RELATIVE_URL);// pdf thumnail
						// placeholder
					} else {
						imageDTO.setDownloadUrl(impcMediaBaseUrl + "/archived_files/download/" + omeroId);
						imageDTO.setJpegUrl(impcMediaBaseUrl + "/render_image/" + omeroId);
						imageDTO.setThumbnailUrl(impcMediaBaseUrl + "/render_birds_eye_view/" + omeroId);
					}
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

							if (imageDTO.getMarkerSynonym() != null) {
								List<String> synSymGene = new ArrayList<>();
								for (String syn : imageDTO.getMarkerSynonym()) {
									synSymGene.add(syn + FIELD_SEPARATOR + symbolGene);
								}
								imageDTO.setMarkerSynonymSymbolGene(synSymGene);
							}
						}
					}
				}
				List<String> paramAssocNameProcName = new ArrayList<>();

				if (imageDTO.getParameterAssociationName() != null) {
					for (String paramAssocName : imageDTO.getParameterAssociationName()) {
						paramAssocNameProcName.add(paramAssocName + FIELD_SEPARATOR + imageDTO.getProcedureName());
					}
					imageDTO.setParameterAssociationNameProcedureName(paramAssocNameProcName);
				}

				addOntologyTerms(imageDTO, parameterStableIdToMaTermIdMap, runStatus);
				addOntologyTerms(imageDTO, parameterStableIdToEmapaTermIdMap, runStatus);
				addOntologyTerms(imageDTO, parameterStableIdToMpTermIdMap, runStatus);

				impcImagesCore.addBean(imageDTO, 30000);
				expectedDocumentCount++;
			}

			impcImagesCore.commit();

		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			throw new IndexerException(e);
		}

		logger.info(" Added {} total beans in {}", expectedDocumentCount,
					commonUtils.msToHms(System.currentTimeMillis() - start));

		return runStatus;
	}


	private Map<String,Set<String>> getPrimaryImagesByGeneAndProcedure(List<ImageDTO> imagePrimaryList) {

		Map<String, Set<String>> res = new HashMap<>();
		for (ImageDTO img : imagePrimaryList){
			if (!res.containsKey(img.getGeneSymbol())){
				res.put(img.getGeneSymbol(), new HashSet<>());
			}
			Set<String> current = res.get(img.getGeneSymbol());
			current.add(img.getProcedureName().toLowerCase().replaceAll("[ -_]", ""));
			res.put(img.getGeneSymbol(), current);
		}
		return res;

	}


	private void addOntologyTerms(ImageDTO imageDTO, Map<String, String> stableIdToTermIdMap, RunStatus runStatus) {

		if (imageDTO.getParameterAssociationStableId() != null
				&& !imageDTO.getParameterAssociationStableId().isEmpty()) {

			for (String paramString : imageDTO.getParameterAssociationStableId()) {
				if (stableIdToTermIdMap.containsKey(paramString)) {
					String thisTermId = stableIdToTermIdMap.get(paramString);
					if (thisTermId.startsWith("MA:")) {
						imageDTO = addAnatomyValues(maParser.getOntologyTerm(thisTermId), imageDTO);
					}
					if (thisTermId.startsWith("EMAPA:")) {
						imageDTO = addAnatomyValues(emapaParser.getOntologyTerm(thisTermId), imageDTO);
					}
					if (thisTermId.startsWith("MP:")) {
						imageDTO = addMpValues(mpParser.getOntologyTerm(thisTermId), imageDTO, thisTermId, runStatus);
					}

				}
			}
		}
	}


	// Since the fields are separate, we have to methods to add ontology info - one for mp and one for anatomy
	private ImageDTO addMpValues(OntologyTermDTO term, ImageDTO imageDTO, String termId, RunStatus runStatus){

		if (term == null) {
			String message = "Cannot find MP ontology term for ID " + termId + ",\n   OMERO ID " + imageDTO.getOmeroId() + ",\n   URL " + imageDTO.getFullResolutionFilePath();
			runStatus.addWarning(message);
			return imageDTO;
		}

		imageDTO.addMpTerm(term.getName(), true);
		imageDTO.addMpId(term.getAccessionId(), true);
		imageDTO.addMpTermSynonym(term.getSynonyms(), true);
		imageDTO.addMpIdTerm(term.getAccessionId() + "_" + term.getName(), true);

		if (term.getIntermediateIds() != null){
			imageDTO.addIntermediateMpId(term.getIntermediateIds());
			imageDTO.addIntermediateMpTerm(term.getIntermediateNames());
		}

		if (term.getTopLevelIds() != null){
			imageDTO.addTopLevelMpId(term.getTopLevelIds(), true);
			imageDTO.addTopLevelMpTerm(term.getTopLevelNames(), true);
		}

		// anatomy terms from mp

		return imageDTO;
	}


	private ImageDTO addAnatomyValues(OntologyTermDTO term, ImageDTO imageDTO) {


		// term
		imageDTO.addAnatomyTerm(term.getName());
		imageDTO.addAnatomyId(term.getAccessionId());
		imageDTO.addAnatomyTermSynonym(term.getSynonyms(), true);
		imageDTO.addAnatomyIdTerm(term.getAccessionId() + "_" + term.getName());
	//	imageDTO.addAnatomyTermSynonymAnatomyIdTerm(termBean.getSynonyms(), FIELD_SEPARATOR + termBean.getTermIdTermName());

		// intermediate terms
		if (term.getIntermediateIds() != null){
			imageDTO.addIntermediateAnatomyId(term.getIntermediateIds());
			imageDTO.addIntermediateAnatomyTerm(term.getIntermediateNames());
			imageDTO.addIntermediateAnatomyTermSynonym(term.getIntermediateSynonyms(), true);
	//		imageDTO.addIntermediateAnatomyIdAnatomyIdTerm(term.getId() + FIELD_SEPARATOR + termBean.getTermIdTermName()); // Do we need these?
	//		imageDTO.addIntermediateAnatomyTermAnatomyIdTerm(term.getName() + FIELD_SEPARATOR + termBean.getTermIdTermName());
	//		imageDTO.addIntermediateAnatomyTermSynonymAnatomyIdTerm(term.getSynonyms(), FIELD_SEPARATOR + termBean.getTermIdTermName());
		}


		if (term.getTopLevelIds() != null){
// logger.debug("adding top level ids="+term.getTopLevelIds());
			imageDTO.addSelectedTopLevelAnatomyId(term.getTopLevelIds(), true);
			imageDTO.addSelectedTopLevelAnatomyTerm(term.getTopLevelNames(), true);
			imageDTO.addSelectedTopLevelAnatomySynonyms(term.getTopLevelSynonyms(), true);
		}

		return imageDTO;
	}

	private List<ImageDTO> populatePhisImages() throws SolrServerException, IOException {
		List<ImageDTO> phisImages = phisService.getPhenoImageShareImageDTOs(primaryGenesProcedures, impressService);
		return phisImages;
	}

	/**
	 * Hopefully all secondary project images will come via PHIS this method should encapsulate all data sources from PHIS and other sources
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	private List<ImageDTO> populateSecondaryProjectImages(RunStatus runStatus) throws SolrServerException, IOException {
		//observation_ids are stored as solr id field and so we need to make sure no conflict
		//need to query the experiment core to make sure we allocate numbers over what we already have
		//this could have other issues if we have assumed id is observation id elsewhere -but I think it's in loading the db and not after indexing??
		int highestSecondaryId=0;

		List<ImageDTO> phisImageDtos = populatePhisImages();
		for(ImageDTO image:phisImageDtos){
			highestSecondaryId++;//do here so one higher than highest obs id
			image.setId("secondary_" + String.valueOf(highestSecondaryId));//add a generated id that we know hasn't been used before
			addMpInfo( image, runStatus);
		}
		return phisImageDtos;
	}

	ImageDTO addMpInfo (ImageDTO image, RunStatus runStatus){

		if ( image.getMpId() != null && !image.getMpId().isEmpty()){

			List<String> mpIds = new ArrayList<>(image.getMpId());
			image.setMpId(new ArrayList<>());
			for (String mpId : mpIds){
				if (mpId.startsWith("MP:")) {
					image = addMpValues(mpParser.getOntologyTerm(mpId), image, mpId, runStatus);
				}
			}
		}
		return image;
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
	    } catch(NumberFormatException | NullPointerException e) {
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
					// logger.info("  Adding status="+allele.getStatus());
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
				//logger.debug("parameterStableId="+parameterStableId+" mpAcc="+mpAcc);
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
					String emapaAcc = emap2EmapaMap.get(acc);
					paramToEmapa.put(parameterStableId, emapaAcc);
				}
				else {
					counter++;
				}
			}
			logger.info("  MISSED: " + counter);
		}
		logger.debug(" paramToMa size = " + paramToEmapa.size());
		return paramToEmapa;
	}
}