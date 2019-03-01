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

/**
 * Created this new class to duplicate functionality of PhenotypeCenterProcedureCompletenessService but with dataSource
 * ALL instead of just IMPC. While it's messy to duplicate the code, it's more of a risk for the ProcedureCompletenessAllReport
 * to accidentally call the IMPC-only methods in PhenotypeCenterService and quietly and incorrectly return only IMPC data.
 * This new class, in conjunction with PhenotypeCenterAllService, allows us to remove the ProcedureCompletenessAllReport
 * dependency on an IMPC-only service.
 *
 * Hopefully, someday this entire reposts module will be replaced with something more flexible.
 */

@Service
public class PhenotypeCenterProcedureCompletenessAllService {

	private final Logger                    logger = LoggerFactory.getLogger(this.getClass());
	private       PhenotypeCenterAllService phenotypeCenterAllService;
	private       SolrClient                statisticalResultCore;

	public PhenotypeCenterProcedureCompletenessAllService(
			PhenotypeCenterAllService phenotypeCenterAllService,
			@Qualifier("statisticalResultCore")
			SolrClient statisticalResultCore)
	{
		this.phenotypeCenterAllService = phenotypeCenterAllService;
		this.statisticalResultCore = statisticalResultCore;
	}


	/**
	 * @author mrelac
	 * @return a {@List} of {@String[]} of each center's progress by Strain, suitable for display in a report
	 * @throws SolrServerException, IOException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException, IOException, URISyntaxException {

//		List<String> centers = phenotypeCenterAllService.getPhenotypeCenters();



		List<String[]> results = new ArrayList<>();
		String[] temp = new String[1];
		List<String> header = new ArrayList<>();

		// Report rows are unique amongst the following fields:
		header.add("Phenotyping Center");
		header.add("Colony Id");
		header.add("Gene Symbol");
		header.add("MGI Gene Id");
		header.add("Allele Symbol");
		header.add("Zygosity");
		header.add("Life Stage");

		// All remaining fields are either collapsed, comma-separated collections or a single count.
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

		results.add(header.toArray(temp));

		final Set<ProcedureCompletenessDTO> procedureCompletenessDTOs = getProcedureCompletenessDTOs();
		for (ProcedureCompletenessDTO procedureCompletenessDTO : procedureCompletenessDTOs) {

			Set<PhenotypeCenterAllServiceBean> data = phenotypeCenterAllService.getDataByCenter(procedureCompletenessDTO.getCenter());

			System.out.println();
















// FIXME FIXME FIXME
//		for(String center: centers){
//			List<PhenotypeCenterAllServiceBean> strains = phenotypeCenterAllService.getMutantStrainsForCenter(center);
//			Set<PhenotypeCenterAllServiceBean> strains = phenotypeCenterAllService.getMutantStrainsForCenters();

//			for(PhenotypeCenterAllServiceBean strain: strains){
//				List<String> procedures = phenotypeCenterAllService.getDoneProcedureIdsPerStrainAndCenter(center, strain.getColonyId());
				List<String> row = new ArrayList<>();
//				row.add(center);
//				row.add(strain.getColonyId());
//				row.add(strain.getGeneSymbol());
//				row.add(strain.getMgiAccession());
//				row.add(strain.getAllele());
//				row.add(strain.getZygosity());
//				row.add(strain.getLifeStage());


//				// Get the procedures and statuses for the given colony id and center
//				final Set<ProcedureStatusDTO> statuses = procedureParameterStatuses
//						.stream()
//						.filter(x -> x.getColonyId().equalsIgnoreCase(strain.getColonyId()))
//						.filter(x -> x.getCenter().equalsIgnoreCase(center))
//						.filter(x -> procedures.contains(x.getProcedureStableId()))
//						.collect(Collectors.toSet());
//
//				Set<ProcedureStatusDTO> rowsByProcedure = new HashSet<>();
//				Set<ProcedureStatusDTO> successful      = new HashSet<>();
//				Set<ProcedureStatusDTO> failed          = new HashSet<>();
//				Set<ProcedureStatusDTO> other           = new HashSet<>();
//
//				// Iterate the list of procedures and calucluate the success / failure
//				for (String procedure : procedures) {
//					rowsByProcedure.addAll(statuses.stream().filter(x -> x.getProcedureStableId().equals(procedure)).collect(Collectors.toSet()));
//					successful.addAll(rowsByProcedure.stream().filter(x -> x.getStatus().equals(STATUSES.SUCCESS)).collect(Collectors.toSet()));
//					failed.addAll(rowsByProcedure.stream().filter(x -> x.getStatus().equals(STATUSES.FAILED)).collect(Collectors.toSet()));
//					other.addAll(rowsByProcedure.stream().filter(x -> x.getStatus().equals(STATUSES.OTHER)).collect(Collectors.toSet()));
//				}
//
//				row.add(successful.stream().map(ProcedureStatusDTO::getProcedureStableId).collect(Collectors.joining(", ")));
//				row.add(Integer.toString(successful.size()));
//				row.add(successful.stream().map(ProcedureStatusDTO::getCount).reduce(0, Integer::sum).toString());
//
//
//				row.add(failed.stream().map(ProcedureStatusDTO::getProcedureStableId).collect(Collectors.joining(", ")));
//				row.add(Integer.toString(failed.size()));
//				row.add(failed.stream().map(ProcedureStatusDTO::getCount).reduce(0, Integer::sum).toString());
//
//				row.add(other.stream().map(ProcedureStatusDTO::getProcedureStableId).collect(Collectors.joining(", ")));
//				row.add(Integer.toString(other.size()));
//				row.add(other.stream().map(ProcedureStatusDTO::getCount).reduce(0, Integer::sum).toString());

				results.add(row.toArray(temp));
//			}
		}

		return results;
	}


	/**
	 *  colony_id + phenotyping_center : "procedure_stable_id", "parameter_stable_id", "status"
	 * Return the set of rows to be returned in the procedureCompletenessAll report.

	 * @return
	 * @throws SolrServerException, IOException
	 */
	public Set<ProcedureCompletenessDTO> getProcedureCompletenessDTOs() throws IOException, URISyntaxException {

		// The order of these filters is critical to solr performance: put collections with fewer elements at the
		// beginning, and collections with more elements at the end.
		String facetPivotFields =
				    StatisticalResultDTO.PHENOTYPING_CENTER
			+ "," + StatisticalResultDTO.LIFE_STAGE_NAME
			+ "," + StatisticalResultDTO.ZYGOSITY
			+ "," + StatisticalResultDTO.STATUS
			+ "," + StatisticalResultDTO.COLONY_ID
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

		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		// FIXME FIXME FIXME FIXME FIXME
		URL url = new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query);
//url = new URL("http://ves-ebi-d0:8986/solr/statistical-result/select?q=*:*&rows=0&facet=true&facet.mincount=1&facet.limit=-1&facet.pivot=phenotyping_center,life_stage_name,zygosity,status,colony_id&wt=xslt&tr=pivot.xsl");
		String content = proxy.getContent(url);

//		String content = proxy.getContent(new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query));

//		return Arrays.stream(content.split("\r"))
//				.skip(1)
//				.map(ProcedureStatusDTO::new)
//				.collect(Collectors.toSet());

		Set<ProcedureCompletenessDTO> procedureParameterStatuses;
		procedureParameterStatuses = Arrays.stream(content.split("\r"))
				.skip(1)
				.map(ProcedureCompletenessDTO::new)
				.collect(Collectors.toSet());

		return procedureParameterStatuses;
	}


	public class ProcedureCompletenessDTO {
		private String   center;
		private String   lifeStageName;
		private String   zygosity;
		private STATUSES status;
		private String   colonyId;

		public ProcedureCompletenessDTO(String data) {
			List<String> fields = Arrays.asList((data.split("\","))).stream().map(x -> x.replaceAll("\"", "")).collect(Collectors.toList());

			this.center = fields.get(0);
			this.lifeStageName = fields.get(1);
			this.zygosity = fields.get(2);
			this.status = fields.get(3).equalsIgnoreCase("Success") ? STATUSES.SUCCESS :
					fields.get(3).toLowerCase().startsWith("failed") ? STATUSES.FAILED :
							STATUSES.OTHER;
			this.colonyId = fields.get(4);
		}

		public String getCenter() {
			return center;
		}

		public String getLifeStageName() {
			return lifeStageName;
		}

		public String getZygosity() {
			return zygosity;
		}

		public STATUSES getStatus() {
			return status;
		}

		public String getColonyId() {
			return colonyId;
		}
	}

	enum STATUSES {
		SUCCESS,
		FAILED,
		OTHER
	}
}