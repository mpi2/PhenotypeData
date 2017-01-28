package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhenodigmService implements WebStatus {

	private final Logger logger = LoggerFactory.getLogger(PhenodigmService.class);
	private final Double MIN_RAW_SCORE_CUTOFF = 1.97;

	@Autowired
	@Qualifier("phenodigmCore")
	private SolrClient solr;

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
					.addFilterQuery(PhenodigmDTO.TYPE + ":disease_model_association")

					.set("group", true)
					.set("group.ngroups", true)
					.set("group.field", PhenodigmDTO.MODEL_ID);

			nGroups = solr.query(q).getGroupResponse().getValues().get(0).getNGroups();
		} catch (IOException | SolrServerException | HttpSolrClient.RemoteSolrException e) {
			logger.error("\n\nERROR getDiseaseAssociationCount: Could not get disease association count\n\n", e);
		}

		return nGroups;

	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName() {
		return "Phenodigm Service";
	}


	public String getGenesWithDiseaseDownload(Set<String> diseaseClasses) throws IOException, SolrServerException, URISyntaxException {

		SolrQuery query = new SolrQuery();
		query.setQuery(diseaseClasses.stream().collect(Collectors.joining("\" OR \"", PhenodigmDTO.DISEASE_CLASSES + ":(\"", "\")")));
		query.setFilterQueries(PhenodigmDTO.TYPE + ":disease_gene_summary");
		query.setRows(Integer.MAX_VALUE);
		query.setFields(PhenodigmDTO.MARKER_SYMBOL, PhenodigmDTO.MARKER_ACCESSION, PhenodigmDTO.DISEASE_TERM, PhenodigmDTO.DISEASE_ID, PhenodigmDTO.IMPC_PREDICTED, PhenodigmDTO.MAX_IMPC_D2M_SCORE,
			 PhenodigmDTO.MGI_PREDICTED, PhenodigmDTO.MAX_MGI_D2M_SCORE, PhenodigmDTO.HUMAN_CURATED);
		query.set("wt", "csv");

		HttpProxy proxy = new HttpProxy();

		return proxy.getContent(new URL(SolrUtils.getBaseURL(solr) + "/select?" + query));

	}

	public Map<String, Set<String>> getGenesWithDisease(Set<String> diseaseClasses) throws IOException, SolrServerException {

		Set<String> mgiPredicted = new HashSet<>();
		Set<String> impcPredicted = new HashSet<>();
		Set<String> humanCurated = new HashSet<>(); // for predictions by orthology it's possible that neither IMPC nor MGI have good scores

		SolrQuery query = new SolrQuery();
		query.setQuery(diseaseClasses.stream().collect(Collectors.joining("\" OR \"", PhenodigmDTO.DISEASE_CLASSES + ":(\"", "\")")));
		query.setFilterQueries(PhenodigmDTO.TYPE + ":disease_gene_summary");
		query.setRows(Integer.MAX_VALUE);
		query.setFields(PhenodigmDTO.MARKER_SYMBOL, PhenodigmDTO.IMPC_PREDICTED, PhenodigmDTO.MGI_PREDICTED, PhenodigmDTO.HUMAN_CURATED);


		QueryResponse rsp = solr.query(query);
		List<PhenodigmDTO> dtos = rsp.getBeans(PhenodigmDTO.class);
		for (PhenodigmDTO dto : dtos) {
			if (dto.getMgiPredicted()) {
				mgiPredicted.add(dto.getMarkerSymbol());
			}
			if (dto.getImpcPredicted()) {
				impcPredicted.add(dto.getMarkerSymbol());
			}
			if (dto.getHumanCurated()){
				humanCurated.add(dto.getMarkerSymbol());
			}
		}

		Map<String, Set<String>> result = new HashMap<>();

		result.put("MGI predicted", mgiPredicted);
		result.put("IMPC predicted", impcPredicted);
		if (humanCurated.size() > 0) {
			result.put("Human curated (orthology)", humanCurated);
		}

		return result;

	}
}
