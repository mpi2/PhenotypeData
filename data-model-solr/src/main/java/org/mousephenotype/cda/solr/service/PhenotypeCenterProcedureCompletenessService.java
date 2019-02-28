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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhenotypeCenterProcedureCompletenessService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ImpressService impressService;
	private PhenotypeCenterService phenotypeCenterService;
	private SolrClient statisticalResultCore;

	public PhenotypeCenterProcedureCompletenessService(
			PhenotypeCenterService phenotypeCenterService,
			ImpressService impressService,
			@Qualifier("statisticalResultCore")
			SolrClient statisticalResultCore)
	{
		this.phenotypeCenterService = phenotypeCenterService;
		this.impressService = impressService;
		this.statisticalResultCore = statisticalResultCore;
	}


	/**
	 * @author tudose, mrelac
	 * @return a {@List} of {@String[]} of each center's progress by Strain, suitable for display in a report
	 * @throws SolrServerException, IOException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException, IOException, URISyntaxException {


		final Set<ProcedureStatusDTO> procedureParameterStatuses = getProcedureParameterStatuses();


		List<String> centers = phenotypeCenterService.getPhenotypeCenters();
        List<String[]> results = new ArrayList<>();
        String[] temp = new String[1];
        List<String> header = new ArrayList<>();
        header.add("Colony Id");
        header.add("Gene Symbol");
        header.add("MGI Gene Id");
        header.add("Allele Symbol");
		header.add("Zygosity");
		header.add("Life Stage");
        header.add("Phenotyping Center");


//        header.add("Procedures With Parameter Status Success");
//		header.add("Parameter Status Success Procedure Count");
//		header.add("Parameter Status Success Count");
//        header.add("Procedures With Parameter Status Fail");
//		header.add("Parameter Status Fail Procedure Count");
//		header.add("Parameter Status Fail Count");
//		header.add("Procedures With Parameter Status Other");
//		header.add("Parameter Status Other Procedure Count");
//		header.add("Parameter Status Other Count");




		header.add("Procedure Ids with Parameter Status Success");
		header.add("Procedure Names with Parameter Status Success");
		header.add("Parameter Ids with Status Success");
		header.add("Parameter Names with Status Success");
		header.add("Top Level MP Ids with Parameter Status Success");
		header.add("Top Level MP Names with Parameter Status Success");
		header.add("MP Ids with Parameter Status Success");
		header.add("MP Names with Parameter Status Success");
		header.add("Procedure Ids with Parameter Status Success Count");
		header.add("Parameter Ids with Status Success Count");



		header.add("Procedure Ids with Parameter Status Fail");
		header.add("Procedure Ids with Parameter Status Fail Count");
		header.add("Parameter Ids with Status Fail Count");



		header.add("Procedure Ids with Parameter Status Other");
		header.add("Procedure Ids with Parameter Status Other Count");
		header.add("Parameter Ids with Status Other Count");







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
//			List<PhenotypeCenterServiceBean> strains = phenotypeCenterService.getMutantStrainsForCenter(center);
			Set<PhenotypeCenterServiceBean> strains = phenotypeCenterService.getMutantStrainsForCenters();

			for(PhenotypeCenterServiceBean strain: strains){
				List<String> procedures = phenotypeCenterService.getDoneProcedureIdsPerStrainAndCenter(center, strain.getColonyId());
				List<String> row = new ArrayList<>();
				row.add(strain.getColonyId());
				row.add(strain.getGeneSymbol());
				row.add(strain.getMgiAccession());
				row.add(strain.getAllele());

				row.add(strain.getZygosity());
				row.add(strain.getLifeStage());
				row.add(center);


				// Get the procedures and statuses for the given colony id and center
				final Set<ProcedureStatusDTO> statuses = procedureParameterStatuses
						.stream()
						.filter(x -> x.getColonyId().equalsIgnoreCase(strain.getColonyId()))
						.filter(x -> x.getCenter().equalsIgnoreCase(center))
						.filter(x -> procedures.contains(x.getProcedureStableId()))
						.collect(Collectors.toSet());

				Set<ProcedureStatusDTO> rowsByProcedure = new HashSet<>();
				Set<ProcedureStatusDTO> successful      = new HashSet<>();
				Set<ProcedureStatusDTO> failed          = new HashSet<>();
				Set<ProcedureStatusDTO> other           = new HashSet<>();

				// Iterate the list of procedures and calucluate the success / failure
				for (String procedure : procedures) {
					rowsByProcedure.addAll(statuses.stream().filter(x -> x.getProcedureStableId().equals(procedure)).collect(Collectors.toSet()));
					successful.addAll(rowsByProcedure.stream().filter(x -> x.getStatus().equals(STATUSES.SUCCESS)).collect(Collectors.toSet()));
					failed.addAll(rowsByProcedure.stream().filter(x -> x.getStatus().equals(STATUSES.FAILED)).collect(Collectors.toSet()));
					other.addAll(rowsByProcedure.stream().filter(x -> x.getStatus().equals(STATUSES.OTHER)).collect(Collectors.toSet()));
				}

				row.add(successful.stream().map(ProcedureStatusDTO::getProcedureStableId).collect(Collectors.joining(", ")));
				row.add(Integer.toString(successful.size()));
				row.add(successful.stream().map(ProcedureStatusDTO::getCount).reduce(0, Integer::sum).toString());


				row.add(failed.stream().map(ProcedureStatusDTO::getProcedureStableId).collect(Collectors.joining(", ")));
				row.add(Integer.toString(failed.size()));
				row.add(failed.stream().map(ProcedureStatusDTO::getCount).reduce(0, Integer::sum).toString());

				row.add(other.stream().map(ProcedureStatusDTO::getProcedureStableId).collect(Collectors.joining(", ")));
				row.add(Integer.toString(other.size()));
				row.add(other.stream().map(ProcedureStatusDTO::getCount).reduce(0, Integer::sum).toString());

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


    /**
     *  colony_id + phenotyping_center : "procedure_stable_id", "parameter_stable_id", "status"
     * Return a map of xxx, keyed by colony_id

     * @return
     * @throws SolrServerException, IOException
     */
    public Set<ProcedureStatusDTO> getProcedureParameterStatuses() throws IOException, URISyntaxException  {

		String facetPivotFields =
				    StatisticalResultDTO.COLONY_ID
//			+ "," + StatisticalResultDTO.PHENOTYPING_CENTER
////			+ "," + StatisticalResultDTO.ZYGOSITY
////			+ "," + StatisticalResultDTO.LIFE_STAGE_NAME
//			+ "," + StatisticalResultDTO.PROCEDURE_STABLE_ID
//			+ "," + StatisticalResultDTO.STATUS
		;

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .setRows(0)
				.setFacet(true)
				.setFacetMinCount(1)
				.setFacetLimit(-1)
				.addFacetPivotField(facetPivotFields);

		query
				.set("wt", "xslt")
				.set("tr", "pivot.xsl");

		HttpProxy proxy = new HttpProxy();

		URL url = new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query);


		String content = proxy.getContent(url);

//		String content = proxy.getContent(new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query));

//		return Arrays.stream(content.split("\r"))
//				.skip(1)
//				.map(ProcedureStatusDTO::new)
//				.collect(Collectors.toSet());

		Set<ProcedureStatusDTO> procedureParameterStatuses;
		procedureParameterStatuses = Arrays.stream(content.split("\r"))
				.skip(1)
				.map(ProcedureStatusDTO::new)
				.collect(Collectors.toSet());

		return procedureParameterStatuses;
    }


	public class ProcedureStatusDTO {
		private String   colonyId;
		private String   center;
		private String   procedureStableId;
		private STATUSES status;
		private Integer  count;

		public ProcedureStatusDTO(String data) {
			List<String> fields = Arrays.asList((data.split("\","))).stream().map(x -> x.replaceAll("\"", "")).collect(Collectors.toList());

			this.colonyId = fields.get(0);
			this.center = fields.get(1);
			this.procedureStableId = fields.get(2);
			this.status = fields.get(3).equalsIgnoreCase("Success") ? STATUSES.SUCCESS :
					fields.get(3).toLowerCase().startsWith("failed") ? STATUSES.FAILED :
					STATUSES.OTHER;
			this.count = Integer.parseInt(fields.get(4));
		}

		public String getColonyId() {
			return colonyId;
		}

		public String getCenter() {
			return center;
		}

		public String getProcedureStableId() {
			return procedureStableId;
		}

		public STATUSES getStatus() {
			return status;
		}

		public Integer getCount() {
			return count;
		}
	}

	enum STATUSES {
    	SUCCESS,
		FAILED,
		OTHER;
	}
}