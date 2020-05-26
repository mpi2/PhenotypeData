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
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ImageService extends BasicService implements WebStatus {

	private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

	private SolrClient impcImagesCore;

	@Inject
	public ImageService(@Qualifier("impcImagesCore") SolrClient impcImagesCore) {
		Assert.notNull(impcImagesCore, "ImageService impc_images core cannot be null");
		this.impcImagesCore = impcImagesCore;

		if (impcImagesCore instanceof HttpSolrClient) {
			logger.info("Image Service starting with impc_images core: " + ((HttpSolrClient) impcImagesCore).getBaseURL());
		}
	}

	public ImageService() {

	}

	public long getNumberOfDocuments(List<String> resourceName,
			boolean experimentalOnly) throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		query.setRows(0);

		if (resourceName != null) {
			query.setQuery(ImageDTO.DATASOURCE_NAME
					+ ":"
					+ StringUtils.join(resourceName, " OR "
							+ ImageDTO.DATASOURCE_NAME + ":"));
		} else {
			query.setQuery("*:*");
		}

		if (experimentalOnly) {
			query.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP
					+ ":experimental");
		}

		return impcImagesCore.query(query).getResults().getNumFound();
	}


	/**
	 *
	 * @param query
	 *            the url from the page name onwards e.g
	 *            q=observation_type:image_record
	 * @return query response
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse getResponseForSolrQuery(String query)
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		String[] paramsKeyValues = query.split("&");
		for (String paramKV : paramsKeyValues) {
			logger.debug("paramKV=" + paramKV);
			String[] keyValue = paramKV.split("=");
			if (keyValue.length > 1) {
				String key = keyValue[0];
				String value = keyValue[1];
				// logger.info("param=" + key + " value=" + value);
				solrQuery.setParam(key, value);
			}

		}

		return impcImagesCore.query(solrQuery);
	}

	/**
	 *  Return a query string formatted for getting all the image records from the experiment core
	 *
	 *  The query string should come out looking something like this:
	 *  q=observation_type:image_record AND downloadFilePath:(*filter* OR *filter2* ...)
	 *
	 * @return a SolrQuery representing the query to get all appropriate image documents
	 */
	public static SolrQuery allImageRecordSolrQuery() {

		// Double escape of the colon is required for regex matching, then string matching
		String filter = Constants.INCLUDE_IMAGE_PATHS
				.stream()
				.map(x -> "*" + x.replaceAll(":", "\\\\:") + "*")
				.collect(Collectors.joining(" OR "));

		SolrQuery query = new SolrQuery()
				.setQuery(ImageDTO.OBSERVATION_TYPE + ":image_record AND " + ObservationDTO.DOWNLOAD_FILE_PATH + ":" + "(" + filter + ")");

		logger.info("Solr query to get images: {}", query.toString());

		return query;
	}


	/**
	 * get images and filter by many properties - main method used by comparison viewer
	 * @param parameterAssociationStableId TODO
	 */
	public QueryResponse getImages(String mgiAccession, String parameterStableId,
			String experimentOrControl, int numberOfImagesToRetrieve, SexType sex,
			String metadataGroup, String strain, String anatomyId,
			String parameterAssociationValue, String mpId, String colonyId, String parameterAssociationStableId) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery().setQuery("*:*");
		//gene accession will take precedence if both acc and symbol supplied
		if(StringUtils.isNotEmpty(mgiAccession)){
			solrQuery.addFilterQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		}
		
		if(StringUtils.isNotEmpty(experimentOrControl)) {
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ experimentOrControl);
		}
		if (StringUtils.isNotEmpty(metadataGroup)) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":"
					+ metadataGroup);
		}
		if (StringUtils.isNotEmpty(strain)) {
			solrQuery.addFilterQuery(ObservationDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery("sex:" + sex.name());
		}
		if (StringUtils.isNotEmpty(parameterStableId)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":"
					+ parameterStableId);
		}
		if (StringUtils.isNotEmpty(parameterAssociationValue)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_ASSOCIATION_VALUE + ":"
					+ "\""+parameterAssociationValue+"\"");//put in quotes for "no expression" query
		}
		if(StringUtils.isNotEmpty(anatomyId)){
			solrQuery.addFilterQuery(ObservationDTO.ANATOMY_ID + ":\""
					+ anatomyId+"\" OR "+ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\""
					+ anatomyId+"\" OR "+ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\""
					+ anatomyId+"\"");
		}
		if (StringUtils.isNotEmpty(mpId)) {
//			solrQuery.addFilterQuery(ImageDTO.MP_ID + ":\""
//					+ mpId+"\"");
			solrQuery.addFilterQuery(ImageDTO.MP_ID + ":\"" + mpId  + "\" OR "+ ImageDTO.INTERMEDIATE_MP_ID + ":\"" + mpId + "\" OR " +ImageDTO.INTERMEDIATE_MP_TERM + ":\"" + mpId + "\" OR " +ImageDTO.TOP_LEVEL_MP_ID + ":\"" + mpId + "\"");

		}
		if (StringUtils.isNotEmpty(colonyId)) {
			solrQuery.addFilterQuery(ObservationDTO.COLONY_ID + ":\""
					+ colonyId+"\"");
		}
		if (StringUtils.isNotEmpty(parameterAssociationStableId)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_ASSOCIATION_STABLE_ID + ":"
					+ "\""+parameterAssociationStableId+"\"");//put in quotes for "no expression" query
		}
		
		solrQuery.setRows(numberOfImagesToRetrieve);
        logger.info("solr Query in image service " + solrQuery);
		return impcImagesCore.query(solrQuery);
	}
	

	public QueryResponse getHeadlineImages(String mgiAccession, String parameterStableId, int numberOfImagesToRetrieve, SexType sex,
			String parameterAssociationValue, String parameterAssociationStableId) throws SolrServerException, IOException {

		//need equivalent to this in order to get both control and experimental images filtered by gene if experimental image
		//https://wwwdev.ebi.ac.uk/mi/impc/dev/solr/impc_images/select?q=*:*&fq=(gene_accession_id:%22MGI:2446296%22%20OR%20biological_sample_group:%22control%22)&fq=parameter_association_stable_id:%22MGP_IMM_086_001%22&rows=1000
		SolrQuery solrQuery = new SolrQuery().setQuery("*:*");
		//gene accession will take precedence if both acc and symbol supplied
		if(StringUtils.isNotEmpty(mgiAccession)){
		solrQuery.addFilterQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\" OR "+ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control");
		}
		if (sex != null) {
			solrQuery.addFilterQuery("sex:" + sex.name());
		}
		if (StringUtils.isNotEmpty(parameterStableId)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":"
					+ parameterStableId);
		}
		if (StringUtils.isNotEmpty(parameterAssociationValue)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_ASSOCIATION_VALUE + ":"
					+ "\""+parameterAssociationValue+"\"");//put in quotes for "no expression" query
		}
		
		if (StringUtils.isNotEmpty(parameterAssociationStableId)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_ASSOCIATION_STABLE_ID + ":"
					+ "\""+parameterAssociationStableId+"\"");//put in quotes for "no expression" query
		}
		
		solrQuery.setRows(numberOfImagesToRetrieve);
		//group controls and experimental together
		solrQuery.addSort(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP , SolrQuery.ORDER.desc);
        logger.info("solr Query in image service " + solrQuery);
		return impcImagesCore.query(solrQuery);
	}


	/**
	 *
	 * @return list of image DTOs with laczData. Selected fields only.
	 * @throws SolrServerException, IOException
	 */
	public List<ImageDTO> getImagesForLacZ()
	throws SolrServerException, IOException{

		SolrQuery query = new SolrQuery();
		query.setQuery(ImageDTO.PROCEDURE_NAME + ":*LacZ*");
		query.setFilterQueries(ImageDTO.ANATOMY_ID + ":*");
		query.addFilterQuery(ImageDTO.GENE_ACCESSION_ID + ":*");
        query.setRows(Integer.MAX_VALUE);
        query.addField(ImageDTO.GENE_SYMBOL);
		query.addField(ImageDTO.GENE_ACCESSION_ID);
		query.addField(ImageDTO.ANATOMY_ID);
		query.addField(ImageDTO.ANATOMY_TERM);

		return impcImagesCore.query(query).getBeans(ImageDTO.class);
	}

	public Map<String, Set<String>> getLaczImagesAvailable() throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery()
				.setQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\" AND "
						+ ImageDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
				.setRows(Integer.MAX_VALUE)
				.setFields(
						ImageDTO.GENE_SYMBOL,
						ImageDTO.PARAMETER_STABLE_ID
						);
		final List<ImageDTO> imageDTOs = impcImagesCore.query(query).getBeans(ImageDTO.class);
		return imageDTOs
				.stream()
				.collect(Collectors.groupingBy(
						ImageDTO::getGeneSymbol,
						Collectors.mapping(ImageDTO::getParameterStableId, Collectors.toSet())));
	}

    /**
	 *
	 */
	public QueryResponse getControlImagesForProcedure(String metadataGroup,
													  String center, String strain, String procedure_name,
													  String parameter, int numberOfImagesToRetrieve,
													  SexType sex) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();

		solrQuery.setQuery("*:*");

		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP
				+ ":control", ObservationDTO.PHENOTYPING_CENTER + ":\""
				+ center + "\"",
				ObservationDTO.STRAIN_NAME + ":" + strain,
				ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter,
				ObservationDTO.PROCEDURE_NAME + ":\"" + procedure_name + "\"");

		solrQuery.setSort(ObservationDTO.DATE_OF_EXPERIMENT, SolrQuery.ORDER.asc);
		solrQuery.setRows(numberOfImagesToRetrieve);

		if (StringUtils.isNotEmpty(metadataGroup)) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":"
					+ metadataGroup);
		}
		if (sex != null) {
			solrQuery.addFilterQuery(ObservationDTO.SEX + ":" + sex.name());
		}

		return impcImagesCore.query(solrQuery);
	}

	/**
	 *
	 * @param numberOfImagesToRetrieve how many images to return
	 * @param parameterStableId the parameter to query
	 * @return a solr query configured with the passed in values
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse getControlImagesForExpressionData(
			int numberOfImagesToRetrieve,
			String parameterStableId,
			String anatomyId,
			String parameterAssociationValue) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();

		solrQuery.setQuery("*:*");

		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP
				+ ":control");
		if(parameterAssociationValue!=null){
		solrQuery.addFilterQuery(ObservationDTO.PARAMETER_ASSOCIATION_VALUE
				+ ":\""+parameterAssociationValue+"\"");
		}
		if (StringUtils.isNotEmpty(anatomyId)) {
			//note - parameter_associations are already handled by the addOntology method in the ImpcImagesIndexer and are anatomy_id associated already.
			solrQuery.addFilterQuery(ObservationDTO.ANATOMY_ID + ":\""
					+ anatomyId+"\" OR "+ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\""
					+ anatomyId+"\" OR "+ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\""
					+ anatomyId+"\"");
		}
		if(StringUtils.isNotEmpty(parameterStableId)){
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":\""
					+ parameterStableId+"\"");
		}
		solrQuery.setRows(numberOfImagesToRetrieve);
        logger.info("solr query for control expression images="+solrQuery);

		return impcImagesCore.query(solrQuery);
	}


	/**
	 * Gets numberOfControls images which are "nearest in time" to the date of
	 * experiment defined in the imgDoc parameter for the specified sex.
	 *
	 * @param numberOfControls
	 *            how many control images to collect
	 * @param sex
	 *            the sex of the specimen in the images
	 * @param imgDoc
	 *            the solr document representing the image record
	 * @param parameterStableId TODO
	 * @param anatomy the anatomy term
	 * @return solr document list, now updated to include all appropriate
	 *         control images
	 * @throws SolrServerException, IOException
	 */
	public List<ImageDTO> getControls(int numberOfControls, SexType sex,
			ImageDTO imgDoc, String parameterStableId, String anatomy, String parameterAssociationValue) throws SolrServerException, IOException {
		List<ImageDTO> list = new ArrayList<>();
		


		QueryResponse responseControl =null;
		if(StringUtils.isNotEmpty(anatomy)){
			responseControl=this.getControlImagesForExpressionData(numberOfControls, parameterStableId, anatomy, parameterAssociationValue);
		}else{
			if(imgDoc!=null){
			final String metadataGroup = imgDoc.getMetadataGroup();
			
			final String center = imgDoc.getPhenotypingCenter();
			final String strain = imgDoc.getStrainName();
			final String procedureName = imgDoc.getProcedureName();
			final String parameter = imgDoc.getParameterStableId();
			responseControl=this.getControlImagesForProcedure(metadataGroup, center, strain, procedureName, parameter, numberOfControls, sex);
			}
		}

		if(responseControl!=null && responseControl.getResults().size()>0){
			list.addAll(responseControl.getBeans(ImageDTO.class));
		}

		return list;
	}

	public List<Group> getPhenotypeAssociatedImages(String geneAcc, String mpId, List<String> anatomyIds, boolean experimentalOnly, int count)
	throws SolrServerException, IOException {

		List<Group> groups = new ArrayList<>();
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		String fq = "";
		if (geneAcc != null) {
			solrQuery.addFilterQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + geneAcc + "\"");
		}
		if (mpId != null){
			fq = ImageDTO.MP_ID + ":\"" + mpId  + "\" OR "+ ImageDTO.INTERMEDIATE_MP_ID + ":\"" + mpId + "\" OR " +ImageDTO.TOP_LEVEL_MP_ID + ":\"" + mpId +"\"";
		}
		if(experimentalOnly){
			solrQuery.addFilterQuery(ImageDTO.BIOLOGICAL_SAMPLE_GROUP+":"+BiologicalSampleType.experimental);
		}
		if (anatomyIds != null && !anatomyIds.isEmpty()){
			fq += (fq.isEmpty()) ? "" : " OR ";
			fq += anatomyIds.stream().collect(Collectors.joining("\" OR " + ImageDTO.ANATOMY_ID + ":\"", ImageDTO.ANATOMY_ID + ":\"", "\""));
			fq += " OR " + anatomyIds.stream().collect(Collectors.joining("\" OR " + ImageDTO.INTERMEDIATE_ANATOMY_ID + ":\"", ImageDTO.INTERMEDIATE_ANATOMY_ID + ":\"", "\""));
		}
		solrQuery.addFilterQuery(fq);
		solrQuery.add("group", "true")
        	.add("group.field", ImageDTO.PARAMETER_STABLE_ID)
        	.add("group.limit", Integer.toString(count));
		logger.info("associated images solr query: " + solrQuery);
		QueryResponse response = impcImagesCore.query(solrQuery);
		List<GroupCommand> groupResponse = response.getGroupResponse().getValues();
        for (GroupCommand groupCommand : groupResponse) {
            List<Group> localGroups = groupCommand.getValues();
            groups.addAll(localGroups);
            
        }
		return groups;
		
	}

	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);
		QueryResponse response = impcImagesCore.query(query);
		return response.getResults().getNumFound();
	}

	public String getServiceName(){
		return "impc_images";
	}


	public SolrDocument getImageByDownloadFilePath(String downloadFilePath) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();
		SolrDocument img=null;
		query.setQuery(ImageDTO.DOWNLOAD_FILE_PATH+":\""+downloadFilePath+"\"").setRows(1);

		QueryResponse response = impcImagesCore.query(query);
		if(response.getResults().getNumFound()>0){
			img = response.getResults().get(0);
		}
		return img;

	}


	/**
	 *
	 * @return a map containing the mp and colony_id combinations so that if we have these then we show an image link on the phenotype table on the gene page. Each row in table could have a different colony_id as well as mp id
	 * @throws SolrServerException, IOException
	 */

	public Map<String, Set<String>> getImagePropertiesThatHaveMp(String acc) throws SolrServerException, IOException {
		//http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1913955%22&fq=mp_id:*&facet=true&facet.mincount=1&facet.limit=-1&facet.field=colony_id&facet.field=mp_id&facet.field=mp_term&rows=0
		Map<String, Set<String>> mpToColony = new HashMap<>();
		SolrQuery query = new SolrQuery();

		query.setQuery(ImageDTO.GENE_ACCESSION_ID+":\""+acc+"\"").setRows(100000000);
		query.addFilterQuery(ImageDTO.MP_ID_TERM+":*");
		query.setFacet(true);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);

		String pivotFacet=ImageDTO.MP_ID_TERM + "," + ImageDTO.COLONY_ID;
		query.set("facet.pivot", pivotFacet);
		query.addFacetField(ObservationDTO.COLONY_ID);
		logger.debug("solr query for images properties for mp = " + query);
		QueryResponse response = impcImagesCore.query(query);
		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			if (pivot.getPivot() != null) {
				//logger.info("pivot="+pivot.getValue());
				String mpIdAndName=pivot.getValue().toString();
				//logger.info("mpIdAndName" +mpIdAndName);
				String mpId="";
				Set<String> colonIds=new TreeSet<>();
				if(mpIdAndName.contains("_")){
					mpId=(mpIdAndName.split("_")[0]);
				}
				for (PivotField mp : pivot.getPivot()){

					//logger.info("adding mp="+pivot.getValue()+" adding value="+mp.getValue());
					String colonyId=mp.getValue().toString();
					colonIds.add(colonyId);
				}
				mpToColony.put(mpId, colonIds);
			}

		}
		return mpToColony;


	}


	public SolrDocumentList getImagesForGrossPathForGene(String accession) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + accession + "\"").setRows(Integer.MAX_VALUE)
				.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\"" + "Gross Pathology and Tissue Collection" + "\"");

		return impcImagesCore.query(query).getResults();
	}

	
	public List<ImageDTO> getMutantImagesForComparisonViewer(String acc, String parameterStableId, String parameterAssociationValue,
															 String anatomyId, String zygosity, String colonyId, String mpId, SexType sexType)
			throws SolrServerException, IOException {
		List<ImageDTO> mutants=new ArrayList<>();
	
		QueryResponse responseExperimental = this.getImages(acc, parameterStableId,"experimental", Integer.MAX_VALUE, 
						null, null, null, anatomyId, parameterAssociationValue, mpId, colonyId, null);
		
		if (responseExperimental != null && responseExperimental.getResults().size()>0) {
			mutants=responseExperimental.getBeans(ImageDTO.class);
		}

		List<ImageDTO> filteredMutants = filterMutantsBySex(mutants, sexType);

		List<ZygosityType> zygosityTypes;
		if(zygosity!=null && !zygosity.equals("all")){
			zygosityTypes=getZygosityTypesForFilter(zygosity);
			//only filter mutants by zygosity as all controls are homs.
			filteredMutants=filterImagesByZygosity(filteredMutants, zygosityTypes);
		}
		return filteredMutants;
	}
	
	private List<ImageDTO> filterImagesByZygosity(List<ImageDTO> filteredMutants, List<ZygosityType> zygosityTypes) {
		List<ImageDTO> filteredImages=new ArrayList<>();
		if(zygosityTypes==null || (zygosityTypes.get(0).getName().equals("not_applicable"))){//just return don't filter if not applicable default is found
			return filteredMutants;
		}
		for(ZygosityType zygosityType:zygosityTypes){
			for(ImageDTO image:filteredMutants){
				if(image.getZygosity().equals(zygosityType.getName())){
					filteredImages.add(image);
				}
			}
		}
		return filteredImages;
	}

	private List<ImageDTO> filterMutantsBySex(List<ImageDTO> mutants, SexType sexType) {
		if(sexType==null){
			return mutants;
		}
		
		List<ImageDTO> filteredMutants = new ArrayList<>();
		
		
			
				for(ImageDTO mutant:mutants){
					if(mutant.getSex().equals(sexType.getName())){
						filteredMutants.add(mutant);
					}
				}
				
			
		
		return filteredMutants;
	}

	public List<ImageDTO> getControlsBySexAndOthersForComparisonViewer(ImageDTO imgDoc, int numberOfControlsPerSex,
			SexType sex, String parameterStableId, String anatomyId, String parameterAssociationValue)
			throws SolrServerException, IOException {

		List<ImageDTO> controlsTemp = this.getControls(numberOfControlsPerSex, sex, imgDoc, parameterStableId,
				anatomyId, parameterAssociationValue);

		// add a unique set only so we don't have duplicate omero ids!!!
		return new ArrayList<>(new HashSet<>(controlsTemp));
	}

	public SexType getSexTypesForFilter(String gender) {
		for(SexType sex:SexType.values()){
			if(sex.name().equals(gender)){
				return sex;
			}
		}
		return null;
	}
	
	private List<ZygosityType> getZygosityTypesForFilter(String zygosity) {
		List<ZygosityType> zygosityTypes=new ArrayList<>();

		switch (zygosity) {
			case "homozygote":
				zygosityTypes.add(ZygosityType.homozygote);
				break;
			case "heterozygote":
				zygosityTypes.add(ZygosityType.heterozygote);
				break;
			case "hemizygote":
				zygosityTypes.add(ZygosityType.hemizygote);
				break;
			case "not_applicable":
			case "notapplicable":
				zygosityTypes.addAll(Arrays.asList(ZygosityType.values()));
				break;
			case "all":
				zygosityTypes.addAll(Arrays.asList(ZygosityType.values()));
				break;
		}

		return zygosityTypes;
	}
}