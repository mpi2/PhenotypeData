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
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.AnatomyPageTableRow;
import org.mousephenotype.cda.solr.web.dto.DataTableRow;
import org.mousephenotype.cda.solr.web.dto.ImageSummary;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ImageService implements WebStatus{

	@Autowired
	@Qualifier("impcImagesCore")
	private SolrClient solr;
	private final Logger logger = LoggerFactory.getLogger(ImageService.class);


	public ImageService() {
	}


	@NotNull
    @Value("${drupalBaseUrl}")
    private String drupalBaseUrl;

    public List<ImageSummary> getImageSummary(String markerAccessionId)
    throws SolrServerException, IOException {

    	SolrQuery q = new SolrQuery();
    	q.setQuery("*:*");
    	q.setFilterQueries(ImageDTO.GENE_ACCESSION_ID + ":\"" + markerAccessionId + "\"");

    	// TM decided only to display some procedures in the Summary
    	q.addFilterQuery("(" + ImageDTO.PROCEDURE_STABLE_ID + ":IMPC_XRY* OR "
    			+ ImageDTO.PROCEDURE_STABLE_ID + ":IMPC_XRY* OR "
    			 + ImageDTO.PROCEDURE_STABLE_ID + ":IMPC_ALZ* OR "
    			  + ImageDTO.PROCEDURE_STABLE_ID + ":IMPC_PAT* OR "
    			   + ImageDTO.PROCEDURE_STABLE_ID + ":IMPC_EYE* OR "
    			    + ImageDTO.PROCEDURE_STABLE_ID + ":IMPC_HIS*" + ")");

    	q.set("group", true);
    	q.set("group.field", ImageDTO.PROCEDURE_NAME);
    	q.set("group.limit", 1);
    	q.set("group.sort" , ImageDTO.DATE_OF_EXPERIMENT + " DESC");

    	List<ImageSummary> res =  new ArrayList<>();

    	for (Group group : solr.query(q).getGroupResponse().getValues().get(0).getValues()){
    		ImageSummary iSummary = new ImageSummary();
    		iSummary.setNumberOfImages(group.getResult().getNumFound());
    		iSummary.setProcedureId(group.getResult().get(0).getFieldValue(ImageDTO.PROCEDURE_STABLE_ID).toString());
    		iSummary.setProcedureName(group.getResult().get(0).getFieldValue(ImageDTO.PROCEDURE_NAME).toString());
    		iSummary.setThumbnailUrl(group.getResult().get(0).getFieldValue(ImageDTO.JPEG_URL).toString().replace("render_image", "render_thumbnail"));
    		res.add(iSummary);
    	}

    	return res;
    }


    /**
     * This method should not be used! This method should use the observation core and get categorical data as well as have image links where applicable!!!
     * @param anatomyId
     * @param anatomyTerms
     * @param phenotypingCenter
     * @param procedure
     * @param paramAssoc
     * @param baseUrl
     * @return
     * @throws SolrServerException, IOException
     */
	public List<AnatomyPageTableRow> getImagesForAnatomy(String anatomyId,
			List<String> anatomyTerms, List<String> phenotypingCenter,
			List<String> procedure, List<String> paramAssoc, String baseUrl)
			throws SolrServerException, IOException {
		Map<String, AnatomyPageTableRow> res = new HashMap<>();
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*")
			.addFilterQuery(
				"(" + ImageDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR "
					+ ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\" OR "
					+ ImageDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\")")
			.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":*LacZ")
			.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"experimental\"")
			.addFilterQuery("("+ImageDTO.PARAMETER_ASSOCIATION_VALUE+ ":\"no expression\" OR "+ImageDTO.PARAMETER_ASSOCIATION_VALUE+":\"expression\""+")") //only have expressed and not expressed ingnore ambiguous and no tissue
			.setRows(Integer.MAX_VALUE)
			.setFields(ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL,
				ImageDTO.ALLELE_ACCESSION_ID, ImageDTO.ZYGOSITY,
				ImageDTO.ANATOMY_ID, ImageDTO.ANATOMY_TERM,
				ImageDTO.PROCEDURE_STABLE_ID, 
				ImageDTO.DATASOURCE_NAME,
				ImageDTO.PARAMETER_ASSOCIATION_VALUE,
				ImageDTO.GENE_SYMBOL, ImageDTO.GENE_ACCESSION_ID,
				ImageDTO.PARAMETER_NAME, ImageDTO.PARAMETER_STABLE_ID, ImageDTO.PROCEDURE_NAME,
				ImageDTO.PHENOTYPING_CENTER,
				ImageDTO.INTERMEDIATE_ANATOMY_ID, ImageDTO.INTERMEDIATE_ANATOMY_TERM,
				ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_ID, ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM
			);

		if (anatomyTerms != null) {
			query.addFilterQuery(ImageDTO.ANATOMY_TERM
					+ ":\""
					+ StringUtils.join(anatomyTerms, "\" OR " + ImageDTO.ANATOMY_TERM
							+ ":\"") + "\"");
		}
		if (phenotypingCenter != null) {
			query.addFilterQuery(ImageDTO.PHENOTYPING_CENTER
					+ ":\""
					+ StringUtils.join(phenotypingCenter, "\" OR "
							+ ImageDTO.PHENOTYPING_CENTER + ":\"") + "\"");
		}
		if (procedure != null) {
			query.addFilterQuery(ImageDTO.PROCEDURE_NAME
					+ ":\""
					+ StringUtils.join(procedure, "\" OR "
							+ ImageDTO.PROCEDURE_NAME + ":\"") + "\"");
		}
		if (paramAssoc != null) {
			query.addFilterQuery(ImageDTO.PARAMETER_ASSOCIATION_VALUE
					+ ":\""
					+ StringUtils.join(paramAssoc, "\" OR "
							+ ImageDTO.PARAMETER_ASSOCIATION_VALUE + ":\"")
					+ "\"");
		}

		List<ImageDTO> response = solr.query(query).getBeans(ImageDTO.class);
		for (ImageDTO image : response) {
			for (String expressionValue : image.getDistinctParameterAssociationsValue()) {
				if (expressionValue.equals("expression") || expressionValue.equals("no expression")) {
					AnatomyPageTableRow row = new AnatomyPageTableRow(image, anatomyId, baseUrl, expressionValue);
					if (res.containsKey(row.getKey())) {
						row = res.get(row.getKey());
						row.addSex(image.getSex());
						row.addIncrementToNumberOfImages();
					}else{
						row.addIncrementToNumberOfImages();
						res.put(row.getKey(), row);
					}
				}
			}
		}

		return new ArrayList<>(res.values());
	}

	public Map<String, Set<String>> getFacets(String anatomyId)
	throws SolrServerException, IOException {

		Map<String, Set<String>> res = new HashMap<>();
		SolrQuery query = new SolrQuery();
		query.setQuery(ImageDTO.PROCEDURE_NAME + ":*LacZ");

		if (anatomyId != null) {
			query.addFilterQuery("(" + ImageDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR " + ImageDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\" OR "
					+ ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\")");
		}
		query.addFilterQuery(ImageDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"experimental\"")
		.addFilterQuery("(" + ImageDTO.PARAMETER_ASSOCIATION_VALUE + ":\"no expression\" OR " + ObservationDTO.PARAMETER_ASSOCIATION_VALUE
				+ ":\"expression\"" + ")"); // only have expressed and
											// not expressed ingnore
											// ambiguous and no tissue
		query.setFacet(true);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);
		query.addFacetField(ImageDTO.ANATOMY_TERM);
		query.addFacetField(ImageDTO.PHENOTYPING_CENTER);
		query.addFacetField(ImageDTO.PROCEDURE_NAME);
		query.addFacetField(ImageDTO.PARAMETER_ASSOCIATION_VALUE);
		QueryResponse response = solr.query(query);

		for (FacetField facetField : response.getFacetFields()) {
			Set<String> filter = new TreeSet<>();
			for (Count facet : facetField.getValues()) {
				if (!facet.getName().equals("tissue not available") && !facet.getName().equals("ambiguous")) {
					filter.add(facet.getName());
				}
			}
				res.put(facetField.getName(), filter);
			
		}

		return res;
	}

	public List<DataTableRow> getImagesForGene(String geneAccession, String baseUrl)
	throws SolrServerException, IOException {

		Map<String, AnatomyPageTableRow> res = new HashMap<>();
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*")
				.addFilterQuery(
						ImageDTO.GENE_ACCESSION_ID + ":\"" + geneAccession
								+ "\"")
				.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":*LacZ")
				.setRows(100000)
				.setFields(ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL,
						ImageDTO.ALLELE_ACCESSION_ID, ImageDTO.ZYGOSITY,
						ImageDTO.ANATOMY_ID, ImageDTO.ANATOMY_TERM,
						ImageDTO.PROCEDURE_STABLE_ID, ImageDTO.DATASOURCE_NAME,
						ImageDTO.PARAMETER_ASSOCIATION_VALUE,
						ImageDTO.GENE_SYMBOL, ImageDTO.GENE_ACCESSION_ID,
						ImageDTO.PARAMETER_NAME, ImageDTO.PROCEDURE_NAME,
						ImageDTO.PHENOTYPING_CENTER);

//		System.out.println("SOLR URL WAS " +  SolrUtils.getBaseURL(solr) + "/select?"
//				+ query);
		List<ImageDTO> response = solr.query(query).getBeans(ImageDTO.class);

		for (ImageDTO image : response) {
			for (String maId : image.getAnatomyId()) {
				AnatomyPageTableRow row = new AnatomyPageTableRow(image, maId,
						baseUrl, "expression");
				if (res.containsKey(row.getKey())) {
					row = res.get(row.getKey());
					row.addSex(image.getSex());
					row.addIncrementToNumberOfImages();
				}
				res.put(row.getKey(), row);
			}
		}

		System.out.println("# rows added : " + res.size());

		return new ArrayList<>(res.values());

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

		return solr.query(query).getResults().getNumFound();
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
				// System.out.println("param=" + key + " value=" + value);
				solrQuery.setParam(key, value);
			}

		}
		QueryResponse response = solr.query(solrQuery);

		return response;
	}

	public static SolrQuery allImageRecordSolrQuery()
			throws SolrServerException, IOException {

		return new SolrQuery().setQuery(ImageDTO.OBSERVATION_TYPE + ":image_record")
				.addFilterQuery(
						"(" + ObservationDTO.DOWNLOAD_FILE_PATH + ":"
								+ "*mousephenotype.org*)");
	}

	public QueryResponse getProcedureFacetsForGeneByProcedure(
			String mgiAccession, String experimentOrControl)
			throws SolrServerException, IOException {

		// Map<String, ResponseWrapper<ImageDTO>> map=new HashMap<String,
		// ResponseWrapper<ImageDTO>>();
		// String queryString = "q=gene_accession_id:\"" + mgiAccession +
		// "\"&fq=" + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" +
		// experimentOrControl+"&facet=true&facet.field=procedure_name&facet.mincount=1";
		// log.debug("queryString in ImageService getFacets=" + queryString);

		// make a facet request first to get the procedures and then reuturn
		// make requests for each procedure
		// http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:2384986%22&&fq=biological_sample_group:experimental&facet=true&facet.field=procedure_name
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ experimentOrControl);
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("procedure_name");
		// solrQuery.setRows(0);
		QueryResponse response = solr.query(solrQuery);
		return response;
	}

	public QueryResponse getImagesForGeneByProcedure(String mgiAccession,
			String procedure_name, String parameterStableId,
			String experimentOrControl, int numberOfImagesToRetrieve,
			SexType sex, String metadataGroup, String strain)
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ experimentOrControl);
		if (metadataGroup != null) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":"
					+ metadataGroup);
		}
		if (strain != null) {
			solrQuery.addFilterQuery(ObservationDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery(ImageDTO.SEX + ":" + sex.name());
		}
		if (parameterStableId != null) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":"
					+ parameterStableId);
		}

		solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\""
				+ procedure_name + "\"");
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = solr.query(solrQuery);
		return response;
	}

	public QueryResponse getImagesForGeneByParameter(String mgiAccession, String parameterStableId,
			String experimentOrControl, int numberOfImagesToRetrieve, SexType sex,
			String metadataGroup, String strain, String anatomyId,
			String parameterAssociationValue, String mpId, String colonyId) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery().setQuery("*:*");
		//gene accession will take precedence if both acc and symbol supplied
		if(StringUtils.isNotEmpty(mgiAccession)){
			solrQuery.addFilterQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		}
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ experimentOrControl);
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
					+ parameterAssociationValue);
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
		
		solrQuery.setRows(numberOfImagesToRetrieve);
		System.out.println("solr Query in image service"+solrQuery);
		QueryResponse response = solr.query(solrQuery);
		return response;
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
        query.setRows(1000000);
        query.addField(ImageDTO.GENE_SYMBOL);
		query.addField(ImageDTO.GENE_ACCESSION_ID);
		query.addField(ImageDTO.ANATOMY_ID);
		query.addField(ImageDTO.ANATOMY_TERM);

		return solr.query(query).getBeans(ImageDTO.class);
	}


	public List<String[]> getLaczExpressionSpreadsheet() {
        SolrQuery query = new SolrQuery();
        ArrayList<String[]> res = new ArrayList<>();
        String[] aux = new String[0];

        query.setQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\" AND "
                + ImageDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        query.setRows(1000000);
        query.addField(ImageDTO.GENE_SYMBOL);
		query.addField(ImageDTO.GENE_ACCESSION_ID);
        query.addField(ImageDTO.ALLELE_SYMBOL);
        query.addField(ImageDTO.COLONY_ID);
        query.addField(ImageDTO.BIOLOGICAL_SAMPLE_ID);
        query.addField(ImageDTO.ZYGOSITY);
        query.addField(ImageDTO.SEX);
        query.addField(ImageDTO.PARAMETER_ASSOCIATION_NAME);
        query.addField(ImageDTO.PARAMETER_STABLE_ID);
        query.addField(ImageDTO.PARAMETER_ASSOCIATION_VALUE);
        query.addField(ImageDTO.GENE_ACCESSION_ID);
        query.addField(ImageDTO.PHENOTYPING_CENTER);
        query.setFacet(true);
        query.setFacetLimit(100);
        query.addFacetField(ImageDTO.PARAMETER_ASSOCIATION_NAME);
        query.set("group", true);
        query.set("group.limit", 100000);
        query.set("group.field", ImageDTO.BIOLOGICAL_SAMPLE_ID);

        try {
            QueryResponse solrResult = solr.query(query);
            ArrayList<String> allParameters = new ArrayList<>();
            List<String> header = new ArrayList<>();
            header.add("Gene Symbol");
			header.add("MGI Gene Id");
            header.add("Allele Symbol");
            header.add("Colony Id");
            header.add("Biological Sample Id");
            header.add("Zygosity");
            header.add("Sex");
            header.add("Phenotyping Centre");

            System.out.println(SolrUtils.getBaseURL(solr) + "/select?" + query);

            // Get facets as we need to turn them into columns
            for (Count facet : solrResult.getFacetField(
                    ImageDTO.PARAMETER_ASSOCIATION_NAME).getValues()) {
                allParameters.add(facet.getName());
                header.add(facet.getName());
            }
            header.add("image_collection_link");
            res.add(header.toArray(aux));
            for (Group group : solrResult.getGroupResponse().getValues().get(0)
                    .getValues()) {

                List<String> row = new ArrayList<>();
                ArrayList<String> params = new ArrayList<>();
                ArrayList<String> paramValues = new ArrayList<>();
                String urlToImagePicker = drupalBaseUrl
                        + "/data/imageComparator/";

                for (SolrDocument doc : group.getResult()) {
                    if (row.size() == 0) {
                        row.add(doc.getFieldValues(ImageDTO.GENE_SYMBOL)
                                .iterator().next().toString());
                        row.add(doc.getFieldValues(ImageDTO.GENE_ACCESSION_ID)
                                .iterator().next().toString());
                        urlToImagePicker += doc
                                .getFieldValue(ImageDTO.GENE_ACCESSION_ID)
                                + "/";
                        urlToImagePicker += doc
                                .getFieldValue(ImageDTO.PARAMETER_STABLE_ID);
                        if (doc.getFieldValue(ImageDTO.ALLELE_SYMBOL) != null) {
                            row.add(doc.getFieldValue(ImageDTO.ALLELE_SYMBOL)
                                    .toString());
                        }
                        row.add(doc.getFieldValue(ImageDTO.COLONY_ID)
                                .toString());
                        row.add(doc
                                .getFieldValue(ImageDTO.BIOLOGICAL_SAMPLE_ID)
                                .toString());
                        if (doc.getFieldValue(ImageDTO.ZYGOSITY) != null) {
                            row.add(doc.getFieldValue(ImageDTO.ZYGOSITY)
                                    .toString());
                        }
                        row.add(doc.getFieldValue(ImageDTO.SEX).toString());
                        row.add(doc.getFieldValue(ImageDTO.PHENOTYPING_CENTER)
                                .toString());
                    }

                    if (doc.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_NAME) != null) {
                        for (int i = 0; i < doc.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_NAME).size(); i++) {
                            params.add(doc.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_NAME).toArray(new Object[0])[i].toString());

                            if (doc.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_VALUE) != null) {
                                paramValues.add(doc.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_VALUE).toArray(new Object[0])[i].toString());
                            } else {
                                paramValues.add(SolrIndex.IMG_NOT_FOUND);
                            }
                        }
                    }
                }

                for (String tissue : allParameters) {
                    if (params.contains(tissue)) {
                        row.add(paramValues.get(params.indexOf(tissue)));
                    } else {
                        row.add("");
                    }
                }
                row.add(urlToImagePicker);
                res.add(row.toArray(aux));
            }

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
	 *
	 * @param metadataGroup
	 * @param center
	 * @param strain
	 * @param procedure_name
	 * @param parameter
	 * @param date
	 * @param numberOfImagesToRetrieve
	 * @param sex
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse getControlImagesForProcedure(String metadataGroup,
			String center, String strain, String procedure_name,
			String parameter, Date date, int numberOfImagesToRetrieve,
			SexType sex) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();

		solrQuery.setQuery("*:*");

		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP
				+ ":control", ObservationDTO.PHENOTYPING_CENTER + ":\""
				+ center + "\"",
				ObservationDTO.STRAIN_NAME + ":" + strain,
				ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter,
				ObservationDTO.PROCEDURE_NAME + ":\"" + procedure_name + "\"");


			solrQuery.setSort("abs(ms(date_of_experiment,"
					+ org.apache.solr.common.util.DateUtil
							.getThreadLocalDateFormat().format(date) + "))",
					SolrQuery.ORDER.asc);


		solrQuery.setRows(numberOfImagesToRetrieve);

		if (StringUtils.isNotEmpty(metadataGroup)) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":"
					+ metadataGroup);
		}
		if (sex != null) {
			solrQuery.addFilterQuery(ObservationDTO.SEX + ":" + sex.name());
		}

		QueryResponse response = solr.query(solrQuery);

		return response;
	}

	/**
	 *
	 * @param numberOfImagesToRetrieve
	 * @param anatomy if this is specified then filter by parameter_association_name and don't filter on date
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse getControlImagesForExpressionData(int numberOfImagesToRetrieve, String anatomy) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();

		solrQuery.setQuery("*:*");

		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP
				+ ":control");
		if (StringUtils.isNotEmpty(anatomy)) {
			solrQuery.addFilterQuery(ImageDTO.PARAMETER_ASSOCIATION_NAME
					+ ":\"" + anatomy + "\"");
		}
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = solr.query(solrQuery);

		return response;
	}


	/**
	 * Get the first control and then experimental images if available for the
	 * all procedures for a gene and then the first parameter for the procedure
	 * that we come across
	 *
	 * @param acc
	 *            the gene to get the images for
	 * @param model
	 *            the model to add the images to
	 * @param numberOfControls
	 *            TODO
	 * @param numberOfExperimental
	 *            TODO
	 * @param getForAllParameters
	 *            TODO
	 * @throws SolrServerException, IOException
	 */
	public void getImpcImagesForGenePage(String acc, Model model,
			int numberOfControls, int numberOfExperimental,
			boolean getForAllParameters) throws SolrServerException, IOException {
		String excludeProcedureName = null;// "Adult LacZ";// exclude adult lacz from
													// the images section as
													// this will now be in the
													// expression section on the
													// gene page
		QueryResponse solrR = this.getProcedureFacetsForGeneByProcedure(acc,
				"experimental");
		if (solrR == null) {
			logger.error("no response from solr data source for acc=" + acc);
			return;
		}

		List<FacetField> procedures = solrR.getFacetFields();
		if (procedures == null) {
			logger.error("no facets from solr data source for acc=" + acc);
			return;
		}

		List<Count> filteredCounts = new ArrayList<>();
		Map<String, List<ImageDTO>> facetToDocs = new HashMap<>();

		for (FacetField procedureFacet : procedures) {

			if (procedureFacet.getValueCount() != 0) {

				// for (FacetField procedureFacet : procedures) {
				// System.out.println("proc facet name="+procedureFacet.getName());
				// this.getControlAndExperimentalImpcImages(acc, model,
				// procedureFacet.getCount().getName(), null, 1, 1,
				// "Adult LacZ");
				// }

				// get rid of wholemount expression/Adult LacZ facet as this is
				// displayed seperately in the using the other method
				// need to put the section in genes.jsp!!!
				for (Count count : procedures.get(0).getValues()) {
					if (!count.getName().equals(excludeProcedureName)) {
						filteredCounts.add(count);
					}
				}

				for (Count procedure : procedureFacet.getValues()) {
					if (!procedure.getName().equals(excludeProcedureName)) {
						this.getControlAndExperimentalImpcImages(acc, model,
								procedure.getName(), null, 0, 1,
								excludeProcedureName, filteredCounts,
								facetToDocs, null);

					}
				}
			}

		}
		model.addAttribute("impcImageFacets", filteredCounts);
		model.addAttribute("impcFacetToDocs", facetToDocs);

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
	 * @param anatomy
	 *            TODO
	 * @return solr document list, now updated to include all appropriate
	 *         control images
	 * @throws SolrServerException, IOException
	 */
	public List<ImageDTO> getControls(int numberOfControls, SexType sex,
			ImageDTO imgDoc, String anatomy) throws SolrServerException, IOException {
		List<ImageDTO> list = new ArrayList<>();
		final String metadataGroup = imgDoc.getMetadataGroup();
				
		final String center = imgDoc.getPhenotypingCenter();
		final String strain = imgDoc.getStrainName();
		final String procedureName = imgDoc.getProcedureName();
		final String parameter = imgDoc.getParameterStableId();
		final Date date = (Date) imgDoc.getDateOfExperiment();


		QueryResponse responseControl =null;
		if(StringUtils.isNotEmpty(anatomy)){
			responseControl=this.getControlImagesForExpressionData(numberOfControls, anatomy);
		}else{
			responseControl=this.getControlImagesForProcedure(metadataGroup, center, strain, procedureName, parameter, date, numberOfControls, sex);
		}

		list.addAll(responseControl.getBeans(ImageDTO.class));

		return list;
	}

	/**
	 *
	 * @param acc
	 *            gene accession mandatory
	 * @param model
	 *            mvc model
	 * @param procedureName
	 *            mandatory
	 * @param parameterStableId
	 *            optional if we want to restrict to a parameter make not null
	 * @param numberOfControls
	 *            can be 0 or any other number
	 * @param numberOfExperimental
	 *            can be 0 or any other int
	 * @param excludedProcedureName
	 *            for example if we don't want "Adult Lac Z" returned
	 * @param filteredCounts
	 * @param facetToDocs
	 * @param anatomyId TODO
	 * @throws SolrServerException, IOException
	 */
	public void getControlAndExperimentalImpcImages(String acc, Model model,
			String procedureName, String parameterStableId,
			int numberOfControls, int numberOfExperimental,
			String excludedProcedureName, List<Count> filteredCounts,
			Map<String, List<ImageDTO>> facetToDocs, String anatomyId)
			throws SolrServerException, IOException {

		model.addAttribute("acc", acc);// forward the gene id along to the new
										// page for links
		QueryResponse solrR = this.getParameterFacetsForGeneByProcedure(acc,
				procedureName, "experimental");
		if (solrR == null) {
			logger.error("no response from solr data source for acc=" + acc);
			return;
		}

		List<FacetField> facets = solrR.getFacetFields();
		if (facets == null) {
			logger.error("no facets from solr data source for acc=" + acc);
			return;
		}

		// get rid of wholemount expression/Adult LacZ facet as this is
		// displayed seperately in the using the other method
		// need to put the section in genes.jsp!!!
		for (Count count : facets.get(0).getValues()) {
			if (!count.getName().equals(excludedProcedureName)) {
				filteredCounts.add(count);
			}
		}
		for (FacetField facet : facets) {
			if (facet.getValueCount() != 0) {
				for (Count count : facet.getValues()) {
					List<ImageDTO> list = null;// list of
													// image
													// docs to
													// return to
													// the
													// procedure
													// section
													// of the
													// gene page
					if (!count.getName().equals(excludedProcedureName)) {
						System.out.println("balh");
						QueryResponse responseExperimental = this
								.getImagesForGeneByParameter(acc,count.getName(),
										"experimental", 1,null, null,
										null, anatomyId, null, null, null);
						if (responseExperimental.getResults().size() > 0) {

							ImageDTO imgDoc = responseExperimental
									.getBeans(ImageDTO.class).get(0);
							QueryResponse responseExperimental2 = this
									.getImagesForGeneByParameter(
											acc, imgDoc
													.getParameterStableId(),
											"experimental",
											numberOfExperimental,
											null,
											imgDoc
													.getMetadataGroup(),
											imgDoc
													.getStrainName(),
											anatomyId,
											null, null, null);

							list = getControls(numberOfControls, null, imgDoc,
									null);

							if (responseExperimental2 != null) {
								list.addAll(responseExperimental2.getBeans(ImageDTO.class));
							}

						}

						facetToDocs.put(count.getName(), list);

					}
				}

			}
		}

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
		QueryResponse response = solr.query(solrQuery);
		List<GroupCommand> groupResponse = response.getGroupResponse().getValues();
        for (GroupCommand groupCommand : groupResponse) {
            List<Group> localGroups = groupCommand.getValues();
            groups.addAll(localGroups);
            
        }
		return groups;
		
	}

	public QueryResponse getParameterFacetsForGeneByProcedure(String acc,
			String procedureName, String controlOrExperimental)
			throws SolrServerException, IOException {

		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/query?q=gene_accession_id:%22MGI:2384986%22&fq=biological_sample_group:experimental&fq=procedure_name:X-ray&facet=true&facet.field=parameter_stable_id
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + acc + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ controlOrExperimental);
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\""
				+ procedureName + "\"");
		solrQuery.addFacetField(ObservationDTO.PARAMETER_STABLE_ID);
		// solrQuery.setRows(0);
		QueryResponse response = solr.query(solrQuery);
		return response;

	}

	public QueryResponse getImagesAnnotationsDetailsByOmeroId(
			List<String> omeroIds) throws SolrServerException, IOException {

		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/query?q=omero_id:(5815
		// 5814)
		SolrQuery solrQuery = new SolrQuery();
		String omeroIdString = "omero_id:(";
		String result = StringUtils.join(omeroIds, " OR ");
		omeroIdString += result + ")";
		solrQuery.setQuery(omeroIdString);
		// System.out.println(omeroIdString);
		// solrQuery.setRows(0);
		QueryResponse response = solr.query(solrQuery);
		return response;

	}

	public Boolean hasImages(String geneAccessionId, String procedureName, String colonyId) throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*")
				.addFilterQuery(
						"(" + ImageDTO.GENE_ACCESSION_ID + ":\"" + geneAccessionId + "\" AND "
								+ ImageDTO.PROCEDURE_NAME + ":\"" + procedureName + "\" AND "
								+ ImageDTO.COLONY_ID + ":\"" + colonyId + "\")")
				.setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		if ( response.getResults().getNumFound() == 0 ){
			return false;
		}
		return true;

	}

	public Boolean hasImagesWithMP(String geneAccessionId, String procedureName, String colonyId, String mpId) throws SolrServerException, IOException {
		//System.out.println("looking for mp term="+mpTerm +"  colony Id="+colonyId);
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*")
				.addFilterQuery(
						"(" + ImageDTO.GENE_ACCESSION_ID + ":\"" + geneAccessionId + "\" AND "
								+ ImageDTO.PROCEDURE_NAME + ":\"" + procedureName + "\" AND "
								+ ImageDTO.COLONY_ID + ":\"" + colonyId + "\" AND "
								+ MpDTO.MP_ID + ":\"" + mpId + "\")")
				.setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		if ( response.getResults().getNumFound() == 0 ){
			return false;
		}
		//System.out.println("returning true");
		return true;
	}

	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}

	public String getServiceName(){
		return "impc_images";
	}


	public SolrDocument getImageByDownloadFilePath(String downloadFilePath) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery(ImageDTO.DOWNLOAD_FILE_PATH+":\""+downloadFilePath+"\"").setRows(1);
		//query.addField(ImageDTO.OMERO_ID);
		//query.addField(ImageDTO.INCREMENT_VALUE);
		//query.addField(ImageDTO.DOWNLOAD_URL);
		//query.addField(ImageDTO.EXTERNAL_SAMPLE_ID);
		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		SolrDocument img = response.getResults().get(0);
		//ImageDTO image = response.get(0);
		//System.out.println("image omero_id"+image.getOmeroId()+" increment_id="+image.getIncrement());
		return img;

	}


	/**
	 *
	 * @param acc
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
		//System.out.println("solr query for images properties for mp="+query);
		QueryResponse response = solr.query(query);
		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			//System.out.println("pivot="+pivot.getValue());
			String mpIdAndName=pivot.getValue().toString();
			//System.out.println("mpIdAndName" +mpIdAndName);
			String mpId="";
			Set<String> colonIds=new TreeSet<>();
			if(mpIdAndName.contains("_")){
				mpId=(mpIdAndName.split("_")[0]);
			}
			for (PivotField mp : pivot.getPivot()){

				//System.out.println("adding mp="+pivot.getValue()+" adding value="+mp.getValue());
				String colonyId=mp.getValue().toString();
				colonIds.add(colonyId);
			}
			mpToColony.put(mpId, colonIds);

		}
		return mpToColony;


	}


}
