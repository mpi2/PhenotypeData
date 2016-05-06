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
import org.mousephenotype.cda.solr.service.PreQcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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

	@Autowired
	@Qualifier("preqcService")
	PreQcService preqcService;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());


	public PhenotypeFacetResult getPhenotypeCallByGeneAccession(String accId) throws IOException, URISyntaxException, SolrServerException {
		return this.getPhenotypeCallByGeneAccessionAndFilter(accId, "");
	}

	
	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName,  List<String> markerSymbol,  List<String> mpTermName) 
	throws IOException, URISyntaxException, SolrServerException {
		return genotypePhenotypeService.getMPCallByMPAccessionAndFilter(phenotype_id,  procedureName, markerSymbol, mpTermName);
	}


	public PhenotypeFacetResult getPreQcPhenotypeCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName,  List<String> markerSymbol,  List<String> mpTermName) 
	throws IOException, URISyntaxException, SolrServerException {
		return preqcService.getMPCallByMPAccessionAndFilter(phenotype_id,  procedureName, markerSymbol, mpTermName);
	}


	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(String accId, String filterString) throws IOException, URISyntaxException, SolrServerException {
		return genotypePhenotypeService.getMPByGeneAccessionAndFilter(accId, filterString);
	}


	public PhenotypeFacetResult getPreQcPhenotypeCallByGeneAccessionAndFilter(String accId, String filterString) throws IOException, URISyntaxException, SolrServerException {
		return preqcService.getMPByGeneAccessionAndFilter(accId, filterString);
	}


	public List<? extends StatisticalResult> getStatisticalResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession) throws IOException, URISyntaxException {
		return genotypePhenotypeService.getStatsResultFor(accession, parameterStableId, observationType, strainAccession, alleleAccession);
	}


}
