package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*
 * pdsimplify: This class references deprecated PhenodigmDTO
 */
@Service
public class PhenodigmService implements WebStatus {

	private final Logger logger               = LoggerFactory.getLogger(this.getClass());
	private final Double MIN_RAW_SCORE_CUTOFF = 1.97;

	private SolrClient phenodigmCore;


	@Inject
	public PhenodigmService(SolrClient phenodigmCore) {
		this.phenodigmCore = phenodigmCore;
	}


	// Disease sources. When modifying these, please modify getAllDiseases() accordingly.
	public static final class DiseaseField {
		public final static String DISEASE_ID = "disease_id";
		public final static String DISEASE_TERM = "disease_term";
		public final static String DISEASE_SOURCE = "disease_source";
		public final static String DISEASE_SOURCE_DECIPHER = "DECIPHER";
		public final static String DISEASE_SOURCE_OMIM = "OMIM";
		public final static String DISEASE_SOURCE_ORPHANET = "ORPHANET";
	}


	/**
	 * @return all diseases from the disease core.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getAllDiseases() throws SolrServerException, IOException  {
		Set<String> results = new HashSet<String>();

		String[] diseaseSources = { PhenodigmService.DiseaseField.DISEASE_SOURCE_DECIPHER, PhenodigmService.DiseaseField.DISEASE_SOURCE_OMIM, PhenodigmService.DiseaseField.DISEASE_SOURCE_ORPHANET };
		for (String diseaseSource : diseaseSources) {
			results.addAll(getAllDiseasesInDiseaseSource(diseaseSource));
		}

		return results;
	}


	/**
	 * @return all diseases from the disease core.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getDiseaseIdsByGene(String geneId) throws SolrServerException, IOException  {
		Set<String> results = new HashSet<String>();

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("marker_id:\"" + geneId + "\"");
		solrQuery.setFields("disease_id", "disease_term");
		solrQuery.setRows(1000000);
		QueryResponse rsp = phenodigmCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allDiseases = new HashSet<String>();
		for (SolrDocument doc : res) {
			allDiseases.add((String) doc.getFieldValue(PhenodigmService.DiseaseField.DISEASE_ID));
		}
		return allDiseases;
	}


	/**
	 * @return all diseases from the disease core.
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getDiseaseTermsByGene(String geneId) throws SolrServerException, IOException  {
		Set<String> results = new HashSet<String>();

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("marker_id:\"" + geneId + "\"");
		solrQuery.setFields("disease_id", "disease_term");
		solrQuery.setRows(1000000);
		QueryResponse rsp = phenodigmCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allDiseases = new HashSet<String>();
		for (SolrDocument doc : res) {
			allDiseases.add((String) doc.getFieldValue(DiseaseField.DISEASE_TERM));
		}
		return allDiseases;
	}


	/**
	 * @return all diseases in the specified <code>diseaseSource</code> (see
	 * public string definitions) from the disease core.
	 * @param diseaseSource the desired disease source (e.g. DiseaseService.OMIM,
	 * DiseaseSource.ORPHANET, etc.)
	 *
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getAllDiseasesInDiseaseSource(String diseaseSource) throws SolrServerException, IOException  {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("disease_source:\"" + diseaseSource + "\"");
		solrQuery.setFields("disease_id");
		solrQuery.setRows(1000000);
		QueryResponse rsp = phenodigmCore.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allDiseases = new HashSet<String>();
		for (SolrDocument doc : res) {
			allDiseases.add((String) doc.getFieldValue(PhenodigmService.DiseaseField.DISEASE_ID));
		}
		return allDiseases;
	}

	/**
	 * Get the number of unique disease associations from the phenodigm core
	 *
	 * @return a count of the number of disease associations or 0 if there was an error and a log entry will be made
	 */
	public Integer getDiseaseAssociationCount() {

		Integer nGroups = null;

		try {
			SolrQuery q = new SolrQuery();

			q.setQuery("*:*")
					.setRows(0)
					.addFilterQuery(PhenodigmDTO.TYPE + ":disease_model_summary")

					.set("group", true)
					.set("group.ngroups", true)
					.set("group.field", PhenodigmDTO.MODEL_ID);

			nGroups = phenodigmCore.query(q).getGroupResponse().getValues().get(0).getNGroups();
			
		} catch (IOException | SolrServerException | HttpSolrClient.RemoteSolrException e) {
			logger.error("\n\nERROR getDiseaseAssociationCount: Could not get disease association count\n\n", e);
		}

		return nGroups;

	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(phenodigmCore) + "/select?" + query);

		QueryResponse response = phenodigmCore.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName() {
		return "Phenodigm Service";
	}


//	public String getGenesWithDiseaseDownload(Set<String> diseaseClasses) throws IOException, SolrServerException, URISyntaxException {
//
//		SolrQuery query = new SolrQuery();
//		query.setQuery(diseaseClasses.stream().collect(Collectors.joining("\" OR \"", PhenodigmDTO.DISEASE_CLASSES + ":(\"", "\")")));
//		query.setFilterQueries(PhenodigmDTO.TYPE + ":disease_gene_summary");
//		query.setRows(Integer.MAX_VALUE);
//		query.setFields(PhenodigmDTO.MARKER_SYMBOL, PhenodigmDTO.MARKER_ACCESSION, PhenodigmDTO.HGNC_GENE_SYMBOL,PhenodigmDTO.DISEASE_TERM, PhenodigmDTO.DISEASE_ID, PhenodigmDTO.IMPC_PREDICTED, PhenodigmDTO.MAX_IMPC_D2M_SCORE,
//			 PhenodigmDTO.MGI_PREDICTED, PhenodigmDTO.MAX_MGI_D2M_SCORE, PhenodigmDTO.HUMAN_CURATED);
//		query.set("wt", "csv");
//
//		HttpProxy proxy = new HttpProxy();
//
//		return proxy.getContent(new URL(SolrUtils.getBaseURL(phenodigmCore) + "/select?" + query));
//
//	}

//	public Map<String, Set<String>> getGenesWithDisease(Set<String> diseaseClasses) throws IOException, SolrServerException {
//
//		Set<String> mgiPredicted = new HashSet<>();
//		Set<String> impcPredicted = new HashSet<>();
//		Set<String> humanCurated = new HashSet<>(); // for predictions by orthology it's possible that neither IMPC nor MGI have good scores
//
//		SolrQuery query = new SolrQuery();
//		query.setQuery(diseaseClasses.stream().collect(Collectors.joining("\" OR \"", PhenodigmDTO.DISEASE_CLASSES + ":(\"", "\")")));
//		query.setFilterQueries(PhenodigmDTO.TYPE + ":disease_gene_summary");
//		query.setRows(Integer.MAX_VALUE);
//		query.setFields(PhenodigmDTO.MARKER_SYMBOL, PhenodigmDTO.IMPC_PREDICTED, PhenodigmDTO.MGI_PREDICTED, PhenodigmDTO.HUMAN_CURATED);
//
//
//		QueryResponse rsp = phenodigmCore.query(query);
//		List<PhenodigmDTO> dtos = rsp.getBeans(PhenodigmDTO.class);
//		for (PhenodigmDTO dto : dtos) {
//			if (dto.getMgiPredicted()) {
//				mgiPredicted.add(dto.getMarkerSymbol());
//			}
//			if (dto.getImpcPredicted()) {
//				impcPredicted.add(dto.getMarkerSymbol());
//			}
//			if (dto.getHumanCurated()){
//				humanCurated.add(dto.getMarkerSymbol());
//			}
//		}
//
//		Map<String, Set<String>> result = new HashMap<>();
//
//		result.put("MGI predicted", mgiPredicted);
//		result.put("IMPC predicted", impcPredicted);
//		if (humanCurated.size() > 0) {
//			result.put("Human curated (orthology)", humanCurated);
//		}
//
//		return result;
//
//	}
}