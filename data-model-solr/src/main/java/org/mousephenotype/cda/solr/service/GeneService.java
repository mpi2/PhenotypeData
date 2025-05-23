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

import com.google.common.collect.Iterators;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.dto.LifeStage;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.imits.StatusConstants;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class GeneService extends BasicService implements WebStatus {

	private static final Logger logger = LoggerFactory.getLogger(GeneService.class);
	public static final int PARTITION_SIZE = 50;

	private SolrClient geneCore;
	private ImpressService impressService;


	@Inject
	public GeneService(SolrClient geneCore,
					   ImpressService impressService) {
		super();
		this.geneCore = geneCore;
		this.impressService = impressService;
	}

	public GeneService() {
		super();
	}


	public static final class GeneFieldValue {
		// on 05-Apr-2019 the WTSI tests started breaking because the DCC renamed WTSI to WSI. Change to WSI here.
		public final static String CENTRE_WTSI = "WSI";
		public final static String PHENOTYPE_STATUS_COMPLETE = StatusConstants.PHENOTYPING_DATA_AVAILABLE;
		public final static String PHENOTYPE_STATUS_STARTED = StatusConstants.PHENOTYPING_STARTED;
	}

	public Integer getAllDataCount(String geneAccessionId) throws IOException, SolrServerException {

		// Default count of datasets associated with this gene is 0
		Integer count = 0;

		SolrQuery query = new SolrQuery();
		query.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + geneAccessionId + "\"");
		final List<GeneDTO> geneDTOs = geneCore.query(query).getBeans(GeneDTO.class);

		if (geneDTOs.size() != 1) throw new RuntimeException("Multiple gene documents found for " + geneAccessionId);

		// Get the raw data from the result
		String output;
		String b64 = geneDTOs.get(0).getDatasetsRawData();

		if (b64 != null) {
			try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(Base64.decodeBase64(b64)))) {
				output = IOUtils.toString(gzis, "UTF-8");
			} catch (IOException e) {
				logger.error("Failed to unzip content for result", geneDTOs.get(0), e);
				return count;
			}

			// Each element has the structure:
			// {
			//   "allele_symbol": String
			//   "allele_accession_id": String
			//   "gene_symbol": String
			//   "gene_accession_id": String
			//   "parameter_stable_id": String
			//   "parameter_name": String
			//   "procedure_stable_id": String
			//   "procedure_name": String
			//   "pipeline_stable_id": String
			//   "pipeline_name": String
			//   "zygosity": String
			//   "phenotyping_center": String
			//   "life_stage_name": String
			// }
			List<GeneService.MinimalGeneDataset> datasetsJson = Arrays.asList(
					new Gson().fromJson(output, GeneService.MinimalGeneDataset[].class));

			List<CombinedObservationKey> datasets = datasetsJson.stream().map(x -> new CombinedObservationKey(
					x.getAlleleSymbol(),
					x.getAlleleAccessionID(),
					x.getGeneSymbol(),
					x.getGeneAccessionID(),
					x.getParameterStableID(),
					x.getParameterName(),
					x.getProcedureStableID(),
					x.getProcedureName(),
					x.getPipelineStableID(),
					x.getPipelineName(),
					ZygosityType.getByDisplayName(x.getZygosity()),
					x.getPhenotypingCenter(),
					LifeStage.getByDisplayName(x.getLifeStageName())
			)).collect(Collectors.toList());

			count = datasets.size();

		}


		return count;
	}

	@Data
	public class MinimalGeneDataset {

		//[
		// {
		//   "allele_symbol":
		//   "allele_accession_id":
		//   "gene_symbol":
		//   "gene_accession_id":
		//   "parameter_stable_id":
		//   "parameter_name":
		//   "procedure_stable_id":
		//   "procedure_name":
		//   "pipeline_stable_id":
		//   "pipeline_name":
		//   "zygosity":
		//   "phenotyping_center":
		//   "life_stage_name":
		//   "significance":
		//   "p_value":
		//   "phenotype_term_id":
		//   "phenotype_term_name":
		// },
		// { ... },
		//]

		@SerializedName("allele_symbol")
		String alleleSymbol;
		@SerializedName("allele_accession_id")
		String alleleAccessionID;
		@SerializedName("gene_symbol")
		String geneSymbol;
		@SerializedName("gene_accession_id")
		String geneAccessionID;
		@SerializedName("parameter_stable_id")
		String parameterStableID;
		@SerializedName("parameter_name")
		String parameterName;
		@SerializedName("procedure_stable_id")
		String procedureStableID;
		@SerializedName("procedure_name")
		String procedureName;
		@SerializedName("pipeline_stable_id")
		String pipelineStableID;
		@SerializedName("pipeline_name")
		String pipelineName;
		@SerializedName("zygosity")
		String zygosity;
		@SerializedName("phenotyping_center")
		String phenotypingCenter;
		@SerializedName("life_stage_name")
		String lifeStageName;
		@SerializedName("significance")
		String significance;
		@SerializedName("p_value")
		Double pValue;
		@SerializedName("phenotype_term_id")
		String phenotypeTermId;
		@SerializedName("phenotype_term_name")
		String phenotypeTermName;
		@SerializedName("top_level_phenotype_term_name")
		Set<String> topLevelPhenotypeTermName;
		@SerializedName("top_level_phenotype_term_id")
		Set<String> topLevelPhenotypeTermId;

	}

	public GeneTopLevelMpTerms getTopLevelMpTerms(GeneDTO gene) {

		return new GeneTopLevelMpTerms(gene.getSignificantTopLevelMpTerms(), gene.getNotSignificantTopLevelMpTerms());
	}


	public GeneTopLevelMpTerms getTopLevelMpTerms(String geneAccessionId) throws IOException, SolrServerException {

		SolrQuery query = new SolrQuery()
				.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + geneAccessionId + "\"")
				.setFields(GeneDTO.SIGNIFICANT_TOP_LEVEL_MP_TERMS, GeneDTO.NOT_SIGNIFICANT_TOP_LEVEL_MP_TERMS);
		final List<GeneDTO> geneDTOs = geneCore.query(query).getBeans(GeneDTO.class);

		if (geneDTOs.size() != 1) throw new RuntimeException("Multiple gene documents found for " + geneAccessionId);

		GeneDTO gene = geneDTOs.get(0);

		return new GeneTopLevelMpTerms(gene.getSignificantTopLevelMpTerms(), gene.getNotSignificantTopLevelMpTerms());
	}

	public Set<ExperimentsDataTableRow> getAllData(String geneAccessionId) throws IOException, SolrServerException {

		SolrQuery query = new SolrQuery()
				.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + geneAccessionId + "\"")
				.setFields(GeneDTO.DATASETS_RAW_DATA);
		final List<GeneDTO> geneDTOs = geneCore.query(query).getBeans(GeneDTO.class);

		if (geneDTOs.size() != 1) throw new RuntimeException("Multiple gene documents found for " + geneAccessionId);

		// Get the raw data from the result
		String b64 = geneDTOs.get(0).getDatasetsRawData();

		if (b64 != null) {

			List<GeneService.MinimalGeneDataset> datasetsJson = unwrapGeneMinimalDataset(b64);

			return datasetsJson.stream().map(x -> {
				MarkerBean allele = new MarkerBean(x.getAlleleAccessionID(), x.getAlleleSymbol());
				MarkerBean gene = new MarkerBean(x.getGeneAccessionID(), x.getGeneSymbol());
				ImpressBaseDTO pipeline = new ImpressBaseDTO(null, null, x.getPipelineStableID(), x.getPipelineName());

				// TODO: Remove after the procedure name is added to the gene solr core dataset_raw_data
				final String procedureName = (x.getProcedureName() != null) ? x.getProcedureName() : impressService.getProcedureByStableId(x.getProcedureStableID()).getName();

				ImpressBaseDTO procedure = new ImpressBaseDTO(null, null, x.getProcedureStableID(), procedureName);
				ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, x.getParameterStableID(), x.getParameterName());
				Double pValue = x.getPValue();
				BasicBean phenotypeTerm = new BasicBean(x.getPhenotypeTermId(), x.getPhenotypeTermName());
				Set<String> topLevelPhenotypeTerms = x.getTopLevelPhenotypeTermId();
				ExperimentsDataTableRow row = new ExperimentsDataTableRow(
						x.getPhenotypingCenter(),
						x.getSignificance(), // Statistical method
						null, // Status
						allele,
						gene,
						ZygosityType.valueOf(x.getZygosity()),
						pipeline,
						procedure,
						parameter,
						null,
						pValue,
						null, // Female mutant count
						null, // Male mutant count
						null, // Effect size
						null // Metadata group
				);
				row.setTopLevelPhenotypes(topLevelPhenotypeTerms);
				row.setPhenotypeTerm(phenotypeTerm);
				row.setLifeStageName(x.getLifeStageName());
				return row;
			}).collect(Collectors.toSet());

		}

		return new HashSet<>();
	}

	// Each element has the structure:
	// {
	//   "allele_symbol": String
	//   "allele_accession_id": String
	//   "gene_symbol": String
	//   "gene_accession_id": String
	//   "parameter_stable_id": String
	//   "parameter_name": String
	//   "procedure_stable_id": String
	//   "procedure_name": String
	//   "pipeline_stable_id": String
	//   "pipeline_name": String
	//   "zygosity": String
	//   "phenotyping_center": String
	//   "life_stage_name": String
	//   "significance": String
	//   "p_value": String
	//   "phenotype_term_id": String
	//   "phenotype_term_name": String
	//	 "top_level_phenotype_term_name": String
	// }
	public static List<GeneService.MinimalGeneDataset> unwrapGeneMinimalDataset(String data) {

		List<GeneService.MinimalGeneDataset> genes = new ArrayList<>();
		if (data != null) {
			try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(Base64.decodeBase64(data)))) {
				String output = IOUtils.toString(gzis, "UTF-8");
				genes = Arrays.asList(new Gson().fromJson(output, GeneService.MinimalGeneDataset[].class));
			} catch (IOException e) {
				logger.error("Failed to gunzip content", e);
			}
		}

		return genes;
	}


	/**
	 * Return all genes in the gene core matching latestPhenotypeStatus and latestProductionCentre.
	 *
	 * @param latestPhenotypeStatus latest phenotype status (i.e. most advanced along the pipeline)
	 * @param latestProductionCentre latest production centre (i.e. most advanced along the pipeline)
	 * @return all genes in the gene core matching phenotypeStatus and productionCentre.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getGenesByLatestPhenotypeStatusAndProductionCentre(
			String latestPhenotypeStatus,
			String latestProductionCentre) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery();
		String queryString = "(" + GeneDTO.PHENOTYPE_STATUS + ":\""
				+ latestPhenotypeStatus + "\") AND ("
				+ GeneDTO.PRODUCTION_CENTRE + ":\""
				+ latestProductionCentre + "\")";
		solrQuery.setQuery(queryString);
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneDTO.MGI_ACCESSION_ID);
		QueryResponse rsp;
		rsp = geneCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		Set<String> allGenes = new HashSet<>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneDTO.MGI_ACCESSION_ID));
		}

		logger.debug("getGenesByLatestPhenotypeStatusAndProductionCentre: solrQuery = "
				+ queryString);
		return allGenes;
	}

	/**
	 * Return all genes in the gene core matching latestPhenotypeStatus and latestPhenotypeCentre.
	 *
	 * @param latestPhenotypeStatus latest phenotype status (i.e. most advanced along the pipeline)
	 * @param latestPhenotypeCentre latest phenotype centre (i.e. most advanced along the pipeline)
	 * @return all genes in the gene core matching phenotypeStatus and productionCentre.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getGenesByLatestPhenotypeStatusAndPhenotypeCentre(
			String latestPhenotypeStatus,
			String latestPhenotypeCentre)
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		String queryString = "(" + GeneDTO.PHENOTYPE_STATUS + ":\""
				+ latestPhenotypeStatus + "\") AND ("
				+ GeneDTO.PHENOTYPING_CENTRE + ":\""
				+ latestPhenotypeCentre + "\")";
		solrQuery.setQuery(queryString);
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneDTO.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = geneCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneDTO.MGI_ACCESSION_ID));
		}

		logger.debug("getGenesByLatestPhenotypeStatusAndPhenotypeCentre: solrQuery = "
				+ queryString);
		return allGenes;
	}

	/**
	 * Return all gene MGI IDs from the gene core.
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getAllGenes() throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GeneDTO.MGI_ACCESSION_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneDTO.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = geneCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneDTO.MGI_ACCESSION_ID));
		}
		return allGenes;
	}


	/**
	 * Return all genes from the gene core.
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException, IOException
	 */
	public List<GeneDTO> getAllGeneDTOs() throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setRows(Integer.MAX_VALUE);
		return geneCore.query(solrQuery).getBeans(GeneDTO.class);
	}


	/**
	 * Return all genes from the gene core whose MGI_ACCESSION_ID does not start with 'MGI'.
	 *
	 * @return all genes from the gene core whose MGI_ACCESSION_ID does not start with 'MGI'.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getAllNonConformingGenes()
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("-" + GeneDTO.MGI_ACCESSION_ID + ":MGI*");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneDTO.MGI_ACCESSION_ID);
		QueryResponse rsp = null;
		rsp = geneCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();

		for (SolrDocument doc : res) {
			allGenes.add((String) doc.getFieldValue(GeneDTO.MGI_ACCESSION_ID));
		}

		return allGenes;
	}


	public List<GeneDTO> getGenesWithEmbryoViewer()
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(GeneDTO.EMBRYO_DATA_AVAILABLE + ":true");
		solrQuery.setRows(1000000);
		solrQuery.setFields(GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);

		return geneCore.query(solrQuery).getBeans(GeneDTO.class);

	}


	/**
	 * Get the latest phenotyping status for a document.
	 *
	 * @return the latest status (Complete or Started or Phenotype Attempt Registered) as appropriate for this gene
	 */
	public static String getPhenotypingStatus(String statusField, String genePageUrl) {

		String phenotypeStatusHTMLRepresentation = "";

		if (statusField != null && !statusField.isEmpty()) {

			if (statusField.equals(StatusConstants.PHENOTYPING_STARTED) ||
					statusField.equals(StatusConstants.PHENOTYPING_DATA_AVAILABLE) ||
					statusField.equals(StatusConstants.PHENOTYPING_COMPLETE)
			) {
				phenotypeStatusHTMLRepresentation += "<a class=' badge badge-success mr-1' href='" + genePageUrl + "#section-associations'><span>Phenotype data</span></a>";
			}
		}

		return phenotypeStatusHTMLRepresentation;
	}

	/**
	 * Get the latest production status of ES cells for a document. Modified 2015/08/03 by @author tudose
	 *
	 * @return the latest status at the gene level for ES cells as a string
	 */
	private static String getEsCellStatus(String latestEsCellStatus, String genePageUrl, boolean toExport) {

		String esCellStatus = "";
		String exportEsCellStatus = "";

		try {
			if (latestEsCellStatus != null) {
				if (latestEsCellStatus.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE)) {
					esCellStatus = "<a class='badge badge-success mr-1' href='" + genePageUrl + "#order2" + "' title='" + StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_DONE + "'>"
							+ " <span>ES Cells</span>"
							+ "</a>";
					exportEsCellStatus += StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_DONE;
				} else if (esCellStatus.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS)) {
					esCellStatus = "<span class='status inprogress' title='" + StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS + "'>"
							+ "	<span>ES Cells</span>"
							+ "</span>";
					exportEsCellStatus += StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS;
				} else {
					esCellStatus = "";
					exportEsCellStatus = StatusConstants.WEB_ES_CELL_STATUS_PRODUCTION_NONE;
				}
			}
		} catch (Exception e) {
			logger.error("Error getting ES cell");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		if (toExport) {
			return exportEsCellStatus;
		}

		return esCellStatus;
	}


	/**
	 * Modified 2015/08/03 by @author tudose
	 *
	 * @param mouseStatus
	 * @param alleleNames
	 * @param geneLink
	 * @return
	 */
	public static String getMiceProductionStatusButton(List<String> mouseStatus, List<String> alleleNames, String geneLink) {

		String miceStatus = "";
		final List<String> exportMiceStatus = new ArrayList<>();
		Map<String, String> statusMap = new HashMap<>();

		try {
			if (mouseStatus != null) {

				for (int i = 0; i < mouseStatus.size(); i++) {
					String mouseStatusStr = mouseStatus.get(i);

					if (!mouseStatusStr.equals("")) {

						statusMap.put(mouseStatusStr, "yes");
					}
				}

				// if no mice status found but there is already allele produced, mark it as "mice produced planned"
				if (alleleNames != null) {

					for (int j = 0; j < alleleNames.size(); j++) {

						String alleleName = alleleNames.get(j);
						if (!alleleName.equals("") && !alleleName.equals("None") && !alleleName.contains("tm1e") && mouseStatus.get(j).equals("")) {
							statusMap.put("mice production planned", "yes");
						}
					}
				}

				if (statusMap.containsKey("Mice Produced")) {

					miceStatus = "<a class='badge badge-success mr-1' oldtitle='Mice Produced' title='' href='" + geneLink + "#order2'>"
							+ "<span>Mice</span>"
							+ "</a>";

					exportMiceStatus.add("mice produced");
				} else if (statusMap.containsKey("Assigned for Mouse Production and Phenotyping")) {
					miceStatus = "<a class='status inprogress' oldtitle='Mice production in progress' title=''>"
							+ "<span>Mice</span>"
							+ "</a>";
					exportMiceStatus.add("mice production in progress");
				} else if (statusMap.containsKey("mice production planned")) {
					miceStatus = "<a class='status none' oldtitle='Mice production planned' title=''>"
							+ "<span>Mice</span>"
							+ "</a>";
					exportMiceStatus.add("mice production in progress");
				}
			}
		} catch (Exception e) {
			logger.error("getMiceProductionStatusButton Error getting ES cell/Mice status");
			logger.error(e.getLocalizedMessage());

		}

		return miceStatus;

	}


	/**
	 * Get the simplified production status of ES cells/mice for a document.
	 *
	 * @param doc represents a gene with imits status fields
	 * @return the latest status at the gene level for both ES cells and alleles
	 */
	public String getLatestProductionStatuses(JSONObject doc, boolean toExport, String geneLink) throws JSONException {


		String esCellStatus = doc.has(GeneDTO.ES_CELL_PRODUCTION_STATUS) && !GeneDTO.MOUSE_PRODUCTION_STATUS.equals("") ? getEsCellStatus(doc.getString(GeneDTO.ES_CELL_PRODUCTION_STATUS), geneLink, toExport) : "";

		List<String> mouseStatus = doc.has(GeneDTO.MOUSE_PRODUCTION_STATUS) && !GeneDTO.MOUSE_PRODUCTION_STATUS.equals("") ? getListFromJson(doc.getJSONArray(GeneDTO.MOUSE_PRODUCTION_STATUS)) : null;

		List<String> alleleNames = doc.has(GeneDTO.ALLELE_NAME) && !GeneDTO.ALLELE_NAME.equals("") ? getListFromJson(doc.getJSONArray(GeneDTO.ALLELE_NAME)) : null;

		String miceStatus = getMiceProductionStatusButton(mouseStatus, alleleNames, geneLink);

		return esCellStatus + miceStatus;

	}

	/**
	 * Generates a map of buttons for ES Cell and Mice status
	 *
	 * @param doc a SOLR Document
	 * @return
	 */
	public static Map<String, String> getStatusFromDTO(GeneDTO doc, String url) {

		String miceStatus = "";
		String esCellStatusHTMLRepresentation = "";
		String miceStatusHTMLRepresentation = "";
		String phenotypingStatusHTMLRepresentation = "";
		Boolean order = false;

		try {

			// Get the HTML representation of the ES Cell status
			String esStatus = (doc.getEsCellProductionStatus() != null) ? doc.getEsCellProductionStatus() : null;

			// Get the HTML representation of the Mouse Production status
			miceStatus = doc.getMouseProductionStatus() == null ? "" : doc.getMouseProductionStatus();

			// Get the HTML representation of the phenotyping status
			String statusField = (doc.getPhenotypeStatus() != null) ? doc.getPhenotypeStatus() : null;

			esCellStatusHTMLRepresentation = getEsCellStatus(esStatus, url, false);
			miceStatusHTMLRepresentation = getMiceProductionStatusButton(Collections.singletonList(miceStatus), null, url);
			phenotypingStatusHTMLRepresentation = getPhenotypingStatus(statusField, url);

			// Order flag is separated from HTML generation code
			order = checkOrderProducts(doc);

		} catch (Exception e) {
			logger.error("getStatusFromDTO Error getting ES cell/Mice status");
			e.printStackTrace();
		}

		HashMap<String, String> res = new HashMap<>();
		res.put("productionIcons", esCellStatusHTMLRepresentation + miceStatusHTMLRepresentation);
		res.put("phenotypingIcons", phenotypingStatusHTMLRepresentation);
		res.put("orderPossible", order.toString());

		return res;
	}


	public static boolean checkOrderProducts(SolrDocument doc) {

		return checkOrderMice(doc) || checkOrderESCells(doc);
	}

	public static boolean checkOrderProducts(GeneDTO doc) {

		return checkOrderMice(doc) || checkOrderESCells(doc);
	}

	public static boolean checkOrderESCells(GeneDTO doc) {

		String status = null;
		boolean order = false;

		try {
			final String field = GeneDTO.ES_CELL_PRODUCTION_STATUS;
			if (doc.getEsCellProductionStatus() != null) {

				status = doc.getEsCellProductionStatus();

				if (status.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE)) {
					order = true;
				}
			}
		} catch (Exception e) {
			logger.error("checkOrderESCells (DTO) Error getting ES cell/Mice status");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		return order;
	}

	public static boolean checkOrderMice(GeneDTO doc) {

		boolean order = false;

		if (doc.getMouseProductionStatus() != null &&
				doc.getMouseProductionStatus().equals(StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_DONE)) {
			order = true;
		}

		return order;

	}

	public static boolean checkOrderESCells(SolrDocument doc) {

		String status = null;
		boolean order = false;

		try {
			final String field = GeneDTO.ES_CELL_PRODUCTION_STATUS;
			if (doc.containsKey(field)) {

				status = doc.getFirstValue(field).toString();

				if (status.equals(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE)) {
					order = true;
				}
			}
		} catch (Exception e) {
			logger.error("checkOrderESCells (solr doc) Error getting ES cell/Mice status");
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

		return order;
	}


	public static boolean checkOrderMice(SolrDocument doc) {

		boolean order = false;

		if (doc.containsKey(GeneDTO.MOUSE_PRODUCTION_STATUS)) {

			List<String> mouseStatus = getListFromCollection(doc.getFieldValues(GeneDTO.MOUSE_PRODUCTION_STATUS));
			for (int i = 0; i < mouseStatus.size(); i++) {

				String mouseStatusStr = mouseStatus.get(i).toString();
				if (mouseStatusStr.equals(StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_DONE)) {

					order = true;
					break;
				}
			}
		}

		return order;

	}


	public Boolean checkAttemptRegistered(String geneAcc) throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		query.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + geneAcc + "\"");
		QueryResponse response = geneCore.query(query);

		if (response.getResults().size() > 0) {

			SolrDocument doc = response.getResults().get(0);
			if (doc.containsKey(GeneDTO.PHENOTYPE_STATUS)) {

				return true;
			}
		}

		return false;
	}


	public Boolean checkPhenotypeStarted(String geneAcc)
			throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		query.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + geneAcc + "\"");
		QueryResponse response = geneCore.query(query);

		if (response.getResults().size() > 0) {
			// check we have results before we try and access them

			SolrDocument doc = response.getResults().get(0);
			if (doc.containsKey(GeneDTO.PHENOTYPE_STATUS)) {

				List<String> statuses = getListFromCollection(doc.getFieldValues(GeneDTO.PHENOTYPE_STATUS));
				for (String status : statuses) {

					if (status.equalsIgnoreCase(StatusConstants.PHENOTYPING_DATA_AVAILABLE) || status.equalsIgnoreCase(StatusConstants.PHENOTYPING_DATA_AVAILABLE)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Get the production status of ES cells/mice for a document.
	 *
	 * @param doc represents a gene with imits status fields
	 * @return the latest status at the gene level for ES cells and all statuses at the allele level for mice as a comma
	 * separated string
	 */
	public String getProductionStatusForEsCellAndMice(JSONObject doc, String url, boolean toExport) throws JSONException {

		String esCellStatus = doc.has(GeneDTO.ES_CELL_PRODUCTION_STATUS) ? getEsCellStatus(doc.getString(GeneDTO.ES_CELL_PRODUCTION_STATUS), url, toExport) : "";
		String miceStatus = "";
		final List<String> exportMiceStatus = new ArrayList<String>();

		String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
		Pattern pattern = Pattern.compile(patternStr);

		try {

			// mice production status

			// Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs)
			if (doc.has("mouse_status")) {

				JSONArray alleleNames = doc.getJSONArray("allele_name");

				JSONArray mouseStatus = doc.getJSONArray("mouse_status");

				for (int i = 0; i < mouseStatus.length(); i++) {
					String mouseStatusStr = mouseStatus.get(i).toString();

					if (mouseStatusStr.equals("Mice Produced")) {

						String alleleName = alleleNames.getString(i).toString();

						Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());

						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<a class='badge badge-success mr-1' oldtitle='" + mouseStatusStr + "' title='' href='#order2'>"
									+ "<span><i class='icon icon-species icon-mouse'></i> " + alleleType + "</span>"
									+ "</a>";

							exportMiceStatus.add(alleleType + " mice produced");
						}
					} else if (mouseStatusStr.equals("Assigned for Mouse Production and Phenotyping")) {
						String alleleName = alleleNames.getString(i).toString();
						Matcher matcher = pattern.matcher(alleleName);
						if (matcher.find()) {
							String alleleType = matcher.group(1);
							miceStatus += "<span class='status inprogress' oldtitle='Mice production in progress' title=''>"
									+ "<span><i class='icon icon-species icon-mouse'></i> " + alleleType + "</span>"
									+ "</span>";
							exportMiceStatus.add(alleleType + " mice production in progress");
						}
					}
				}
				// if no mice status found but there is already allele produced, mark it as "mice produced planned"
				if (alleleNames != null) {
					for (int j = 0; j < alleleNames.length(); j++) {
						String alleleName = alleleNames.get(j).toString();
						if (!alleleName.equals("") && !alleleName.equals("None") && mouseStatus.get(j).toString().equals("")) {
							Matcher matcher = pattern.matcher(alleleName);
							if (matcher.find()) {
								String alleleType = matcher.group(1);
								miceStatus += "<span class='status none' oldtitle='Mice production planned' title=''>"
										+ "<span><i class='icon icon-species icon-mouse'></i> " + alleleType + "</span>"
										+ "</span>";

								exportMiceStatus.add(alleleType + " mice production planned");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("getProductionStatusForEsCellAndMice Error getting ES cell/Mice status");
			logger.error(e.getLocalizedMessage());
		}

		if (toExport) {
			exportMiceStatus.add(0, esCellStatus); // want to keep this at front
			return StringUtils.join(exportMiceStatus, ", ");
		}
		return esCellStatus + miceStatus;

	}


	/**
	 * Get the mouse production status for gene for geneHeatMap implementation for IDG
	 *
	 * @param geneIds
	 * @return Returns GeneDTOs populated with the information required to display the production statuses
	 * @throws SolrServerException, IOException
	 */
	public List<GeneDTO> getProductionStatusForGeneSet(Set<String> geneIds)
			throws SolrServerException, IOException {

		List<GeneDTO> genes = new ArrayList<>();

		SolrQuery solrQuery = new SolrQuery()
				.setQuery("*:*").setRows(Integer.MAX_VALUE)
				.setSort(GeneDTO.MARKER_SYMBOL, SolrQuery.ORDER.asc)
				.setFields(GeneDTO.MGI_ACCESSION_ID,
						GeneDTO.HUMAN_GENE_SYMBOL,
						GeneDTO.MARKER_SYMBOL,
						GeneDTO.ALLELE_NAME,
						GeneDTO.PHENOTYPE_STATUS,
						GeneDTO.ES_CELL_PRODUCTION_STATUS,
						GeneDTO.PHENOTYPE_STATUS,
						GeneDTO.PHENOTYPE_STATUS,
						GeneDTO.HAS_QC,
						GeneDTO.TOP_LEVEL_MP_TERM,
						GeneDTO.SIGNIFICANT_TOP_LEVEL_MP_TERMS,
						GeneDTO.NOT_SIGNIFICANT_TOP_LEVEL_MP_TERMS,
						GeneDTO.MARKER_SYMBOL,
						GeneDTO.ES_CELL_PRODUCTION_STATUS,
						GeneDTO.MOUSE_PRODUCTION_STATUS,
						GeneDTO.PHENOTYPE_STATUS);

		if (geneIds != null) {

			// Partition the set of genes into groups of PARTITION_SIZE so as not to overwhelm solr with OR fields

			Iterators.partition(geneIds.iterator(), PARTITION_SIZE).forEachRemaining(geneAccessionBatch ->
			{
				String geneQuery = GeneDTO.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneAccessionBatch, " OR ").replace(":", "\\:") + ")";
				solrQuery.setQuery(geneQuery);

				try {
					genes.addAll(geneCore.query(solrQuery).getBeans(GeneDTO.class));
				} catch (SolrServerException | IOException e) {
					logger.warn("Exception getting gene solr facet query " + solrQuery.toQueryString() + "\n response for getStatusCount \n", e);
				}
			});

		}

		return genes;

	}


	/**
	 * Get the Register Interest gene detail fields for all genes in the core
	 *
	 * @return the Register Interest gene detail fields for all genes in the core
	 * @throws SolrServerException, IOException
	 */
	public List<GeneDTO> getRegisterInterestGeneDetails()
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery("*:*").setRows(Integer.MAX_VALUE)
				.setFields(GeneDTO.MGI_ACCESSION_ID,
						GeneDTO.MARKER_SYMBOL,
						GeneDTO.ASSIGNMENT_STATUS,
						GeneDTO.CONDITIONAL_ALLELE_PRODUCTION_STATUS,
						GeneDTO.CRISPR_ALLELE_PRODUCTION_STATUS,
						GeneDTO.NULL_ALLELE_PRODUCTION_STATUS,
						GeneDTO.PHENOTYPING_DATA_AVAILABLE);

		return geneCore.query(solrQuery).getBeans(GeneDTO.class);
	}


	public GeneDTO getGeneById(String mgiId, String... fields) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(GeneDTO.MGI_ACCESSION_ID + ":\"" + mgiId + "\"").setRows(1);
		if (fields != null) {
			solrQuery.setFields(fields);
		}

		QueryResponse rsp = geneCore.query(solrQuery);
		if (rsp.getResults().getNumFound() > 0) {
			return rsp.getBeans(GeneDTO.class).get(0);
		}
		return null;
	}

	/**
	 * Return a list of gene DTOs cooresponding to the
	 *
	 * @param mgiIds
	 * @param fields (Optional) list of fields
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public List<GeneDTO> getGenesByMgiIds(List<String> mgiIds, String... fields) throws SolrServerException, IOException {

		Set<GeneDTO> genes = new HashSet<>();

		if (mgiIds == null) return new ArrayList<>();
		List<String> quotedMgiIds = mgiIds.stream()
				.map(x -> String.format("\"%s\"", x))
				.collect(Collectors.toList());

		SolrQuery solrQuery = new SolrQuery()
				.setRows(PARTITION_SIZE * 2);

		if (fields != null) {
			solrQuery.setFields(fields);
		}

		// Partition the set of gene symbold into groups of PARTITION_SIZE so as not to overwhelm solr with OR fields
		Iterators.partition(quotedMgiIds.iterator(), PARTITION_SIZE).forEachRemaining(batch ->
		{
			solrQuery.setQuery(GeneDTO.MGI_ACCESSION_ID + ":(" + StringUtils.join(batch, ",") + ")");

			List<GeneDTO> geneDTOS;
			try {
				geneDTOS = geneCore.query(solrQuery, METHOD.POST).getBeans(GeneDTO.class);
			} catch (SolrServerException | IOException e) {
				logger.error("Error getting results for subset of genes symbols", e);
				return;
			}

			genes.addAll(geneDTOS);

		});

		return new ArrayList<>(genes);
	}

	// supports multiple symbols or synonyms
	public List<GeneDTO> getGeneByGeneSymbolsOrGeneSynonyms(List<String> symbols) throws SolrServerException, IOException {
		Set<GeneDTO> genes = new HashSet<>();

		SolrQuery solrQuery = new SolrQuery()
				.setRows(PARTITION_SIZE * 2);

		// Partition the set of gene symbold into groups of PARTITION_SIZE so as not to overwhelm solr with OR fields
		Iterators.partition(symbols.iterator(), PARTITION_SIZE).forEachRemaining(symbolBatch ->
		{
			String symbolsStr = StringUtils.join(symbolBatch, "\",\"");  // ["bla1","bla2"]
			String geneQuery = GeneDTO.MARKER_SYMBOL_LOWERCASE + ":(" + symbolsStr + ") OR " + GeneDTO.MARKER_SYNONYM_LOWERCASE + ":(" + symbolsStr + ")";
			solrQuery.setQuery(geneQuery);

			List<GeneDTO> geneDTOS;
			try {
				geneDTOS = geneCore.query(solrQuery, METHOD.POST).getBeans(GeneDTO.class);
			} catch (SolrServerException | IOException e) {
				logger.error("Error getting results for subset of genes symbols", e);
				return;
			}

			genes.addAll(geneDTOS);

		});

		return new ArrayList<>(genes);
	}


	public GeneDTO getGeneByGeneSymbolWithLimitedFields(String symbol) throws SolrServerException, IOException {
		SolrQuery solrQuery = new SolrQuery()
				.setQuery(GeneDTO.MARKER_SYMBOL_LOWERCASE + ":\"" + symbol + "\"")
				.setRows(1)
				.setFields(GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL, GeneDTO.MARKER_NAME);

		QueryResponse rsp = geneCore.query(solrQuery);
		if (rsp.getResults().getNumFound() > 0) {
			return rsp.getBeans(GeneDTO.class).get(0);
		}
		return null;
	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		QueryResponse response = geneCore.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName() {
		return "Gene Service";
	}

	/**
	 * @param geneIds the input set of gene ids (e.g. idg genes)
	 * @param statusField the status field
	 * @return Number of genes (from the provided list) in each status of interest.
	 */
	public Map<String, Long> getStatusCount(Set<String> geneIds, String statusField) {

		Map<String, AtomicLong> res = new HashMap<>();

		// Base solr query setup configured to Facet on statusField
		SolrQuery solrQuery = new SolrQuery()
				.addFacetField(statusField)
				.setRows(1)
				.setFacet(true)
				.setFacetLimit(-1);


		if (geneIds == null) {
			solrQuery.setQuery("*:*");
			try {
				QueryResponse solrResponse = geneCore.query(solrQuery);
				for (Count c : solrResponse.getFacetField(statusField).getValues()) {
					res.put(c.getName(), new AtomicLong(c.getCount()));
				}
			} catch (SolrServerException | IOException e) {
				logger.warn("Exception getting gene solr facet query " + solrQuery.toQueryString() + "\n response for getStatusCount \n", e);
			}
		} else {

			// Partition the set of genes into groups of PARTITION_SIZE so as not to overwhelm solr with OR fields

			Iterators.partition(geneIds.iterator(), PARTITION_SIZE).forEachRemaining(geneAccessionBatch ->
			{
				String geneQuery = GeneDTO.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneAccessionBatch, " OR ").replace(":", "\\:") + ")";
				solrQuery.setQuery(geneQuery);

				try {
					QueryResponse solrResponse = geneCore.query(solrQuery);
					for (Count c : solrResponse.getFacetField(statusField).getValues()) {
						res.putIfAbsent(c.getName(), new AtomicLong(0));
						res.get(c.getName()).addAndGet(c.getCount());
					}
				} catch (SolrServerException | IOException e) {
					logger.warn("Exception getting gene solr facet query " + solrQuery.toQueryString() + "\n response for getStatusCount \n", e);
				}
			});
		}

		return res.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue()));
	}

	/**
	 * Get the mouse project status for a set of genes (not alleles) for table implementation for cmg
	 *
	 * @param geneId the MGI ID of the gene of interest
	 * @return latest mouse production status for the supplied gene
	 */
	public String getLatestProjectStatusForGeneSet(String geneId) throws SolrServerException, IOException {
		String latestProjectStatus = "";
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setFilterQueries(GeneDTO.MGI_ACCESSION_ID + ":(" + geneId.replace(":", "\\:") + ")");
		solrQuery.setRows(100000);
		solrQuery.setFields(GeneDTO.MGI_ACCESSION_ID, GeneDTO.ASSIGNMENT_STATUS);
		logger.info("server query is: {}", solrQuery.toString());
		QueryResponse rsp = geneCore.query(solrQuery);
		List<GeneDTO> genes = rsp.getBeans(GeneDTO.class);
		for (GeneDTO gene : genes) {
			latestProjectStatus = gene.getAssignmentStatus();
		}

		return latestProjectStatus;
	}

}
