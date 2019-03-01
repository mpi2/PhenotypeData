/*******************************************************************************
 * Copyright 2019 EMBL - European Bioinformatics Institute
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

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PhenotypeCenterProcedureCompletenessService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ImpressService impressService;
	private PhenotypeCenterService phenotypeCenterService;

	public PhenotypeCenterProcedureCompletenessService(
			PhenotypeCenterService phenotypeCenterService,
			ImpressService impressService)
	{
		this.phenotypeCenterService = phenotypeCenterService;
		this.impressService = impressService;
	}


	/**
	 * @author tudose, mrelac
	 * @return a {@List} of {@String[]} of each center's progress by Strain, suitable for display in a report
	 * @throws SolrServerException, IOException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException, IOException {

		List<String> centers = phenotypeCenterService.getPhenotypeCenters();
        List<String[]> results = new ArrayList<>();
        String[] temp = new String[1];
        List<String> header = new ArrayList<>();
        header.add("Colony Id");
        header.add("Gene Symbol");
        header.add("MGI Gene Id");
        header.add("Allele Symbol");
        header.add("Phenotyping Center");
        header.add("Percentage Done");
        header.add("Number of Done Procedures");
        header.add("Done Procedures");
        header.add("MPS Tested For");
        header.add("Number of Missing Procedures");
        header.add("Missing Procedures");
        header.add("MPS Not Tested For");
		results.add(header.toArray(temp));
		Map<String, Set<String>> mpsPerProcedure = impressService.getMpsForProcedures();
		Map<String, List<String>> possibleProceduresPerCenter = phenotypeCenterService.getProceduresPerCenter();

		for(String center: centers){
			List<PhenotypeCenterServiceBean> strains = phenotypeCenterService.getMutantStrainsForCenter(center);

			for(PhenotypeCenterServiceBean strain: strains){
				List<String> procedures = phenotypeCenterService.getDoneProcedureIdsPerStrainAndCenter(center, strain.getColonyId());
				List<String> row = new ArrayList<>();
				row.add(strain.getColonyId());
				row.add(strain.getGeneSymbol());
				row.add(strain.getMgiAccession());
				row.add(strain.getAllele());

				row.add(center);
				Float percentageDone = (float) ((procedures.size() * 100) / (float)possibleProceduresPerCenter.get(center).size());
				row.add(percentageDone.toString());
				row.add("" + procedures.size()); // #procedures done
				row.add(procedures.toString()); // procedures done
				Set<String> mpsTestedFor = phenotypeCenterService.getMpsForProcedureSet(procedures, mpsPerProcedure);
				row.add(mpsTestedFor.toString()); //mpsTestedFor
				row.add("" + (possibleProceduresPerCenter.get(center).size() - procedures.size()));	// #missing procedures
				List<String> missing = new ArrayList<>(possibleProceduresPerCenter.get(center));
				missing.removeAll(procedures); // missing procedures
				row.add(missing.toString());
				Set<String> mpsNotTestedFor = phenotypeCenterService.getMpsForProcedureSet(missing, mpsPerProcedure);
				mpsNotTestedFor.removeAll(mpsTestedFor);
				row.add(mpsNotTestedFor.toString());
				results.add(row.toArray(temp));
			}
		}

		return results;
	}


	public PhenotypeCenterService getPhenotypeCenterService() {
		return phenotypeCenterService;
	}
}