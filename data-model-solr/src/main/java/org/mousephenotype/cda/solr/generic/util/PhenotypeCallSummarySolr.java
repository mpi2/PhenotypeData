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
package org.mousephenotype.cda.solr.generic.util;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.web.dto.DataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypePageTableRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * PhenotypeCallSummarySolr collects all interactions with the genotype-phenotype
 * datasource.
 *
 */

@Service
public class PhenotypeCallSummarySolr {

	@Value("cmsBaseUrl")
	private String cmsBaseUrl;


	private GenotypePhenotypeService genotypePhenotypeService;


	@Inject
	public PhenotypeCallSummarySolr(@Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService) {
		this.genotypePhenotypeService = genotypePhenotypeService;
	}

	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName, List<String> markerSymbol, List<String> mpTermName)
			throws IOException, JSONException, URISyntaxException {
		return genotypePhenotypeService.getMPCallByMPAccessionAndFilter(phenotype_id, procedureName, markerSymbol, mpTermName);
	}

	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(String accId, List<String> topLevelMpTermName, List<String> resourceFullname)
			throws IOException, JSONException, URISyntaxException {
		return genotypePhenotypeService.getMPByGeneAccessionAndFilter(accId, topLevelMpTermName, resourceFullname);
	}

	/**
	 *
	 * @param phenoResult
	 * @param baseUrl
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws IOException, SolrServerException
	 */
	
	public List<DataTableRow> getPhenotypeRows(PhenotypeFacetResult phenoResult, String baseUrl) throws IOException, SolrServerException {

		List<PhenotypeCallSummaryDTO> phenotypeList;
		phenotypeList = phenoResult.getPhenotypeCallSummaries();
		// This is a map because we need to support lookups
		Map<Integer, DataTableRow> phenotypes = new HashMap<Integer, DataTableRow>();

		for (PhenotypeCallSummaryDTO pcs : phenotypeList) {

			// On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
			DataTableRow pr = new PhenotypePageTableRow(pcs, baseUrl, cmsBaseUrl, false);

			// Collapse rows on sex
			if (phenotypes.containsKey(pr.hashCode())) {

				pr = phenotypes.get(pr.hashCode());
				// Use a tree set to maintain an alphabetical order (Female, Male)
				TreeSet<String> sexes = new TreeSet<String>();
				for (String s : pr.getSexes()) {
					sexes.add(s);
				}
				sexes.add(pcs.getSex().toString());

				pr.setSexes(new ArrayList<String>(sexes));
			}

			if (pr.getParameter() != null && pr.getProcedure() != null) {
				phenotypes.put(pr.hashCode(), pr);
			}
		}

		List<DataTableRow> list = new ArrayList<DataTableRow>(phenotypes.values());
		Collections.sort(list);

		return list;
	}
}