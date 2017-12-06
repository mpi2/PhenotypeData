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
import org.mousephenotype.cda.db.pojo.StatisticalResult;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.web.dto.DataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypePageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Added to cda 2015/07/09
 * @author tudose
 *
 */

@Service
public class PhenotypeCallSummarySolr {

	@Autowired
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;

	//@Autowired
	//@Qualifier("preqcService")
	//PreQcService preqcService;

	@Value("drupalBaseUrl")
	private String drupalBaseUrl;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());


	public PhenotypeFacetResult getPhenotypeCallByGeneAccession(String accId) throws IOException, URISyntaxException, SolrServerException {
		return this.getPhenotypeCallByGeneAccessionAndFilter(accId, null, null);
	}

	
	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName,  List<String> markerSymbol,  List<String> mpTermName) 
	throws IOException, URISyntaxException, SolrServerException {
		return genotypePhenotypeService.getMPCallByMPAccessionAndFilter(phenotype_id,  procedureName, markerSymbol, mpTermName);
	}


	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(String accId, List<String> topLevelMpTermName, List<String> resourceFullname) throws IOException, URISyntaxException, SolrServerException {
		return genotypePhenotypeService.getMPByGeneAccessionAndFilter(accId, topLevelMpTermName, resourceFullname);
	}


//	removing as no longer used any reason to keep this? public PhenotypeFacetResult getPreQcPhenotypeCallByGeneAccessionAndFilter(String accId, List<String> topLevelMpTermName, List<String> resourceFullname) throws IOException, URISyntaxException, SolrServerException {
//		return preqcService.getMPByGeneAccessionAndFilter(accId, topLevelMpTermName, resourceFullname);
//	}


	public List<? extends StatisticalResult> getStatisticalResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession) throws IOException, URISyntaxException {
		return genotypePhenotypeService.getStatsResultFor(accession, parameterStableId, observationType, strainAccession, alleleAccession);
	}

	/**
	 *
	 * @param phenoResult
	 * @param preQcResult
	 * @param baseUrl
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws SolrServerException, IOException
	 */
	//public List<DataTableRow> getPhenotypeRows(PhenotypeFacetResult phenoResult, PhenotypeFacetResult preQcResult, String baseUrl)
	public List<DataTableRow> getPhenotypeRows(PhenotypeFacetResult phenoResult, String baseUrl)
			throws IOException, URISyntaxException, SolrServerException {


		List<PhenotypeCallSummaryDTO> phenotypeList;
		phenotypeList = phenoResult.getPhenotypeCallSummaries();
		//phenotypeList.addAll(preQcResult.getPhenotypeCallSummaries());

		// This is a map because we need to support lookups
		Map<Integer, DataTableRow> phenotypes = new HashMap<Integer, DataTableRow>();

		for (PhenotypeCallSummaryDTO pcs : phenotypeList) {

			// On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
			DataTableRow pr = new PhenotypePageTableRow(pcs, baseUrl, drupalBaseUrl, false);

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
