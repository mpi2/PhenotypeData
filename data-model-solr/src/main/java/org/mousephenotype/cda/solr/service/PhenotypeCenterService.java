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
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class PhenotypeCenterService {

    private final Logger     logger         = LoggerFactory.getLogger(this.getClass());
    private       SolrClient experimentCore;
    private final String     datasourceName = "IMPC";//pipeline but takes care of things like WTSI MGP select is IMPC!

	public PhenotypeCenterService(
			@Qualifier("experimentCore")
			SolrClient solrClientExperiment)
	{
		this.experimentCore = solrClientExperiment;
	}

	/**
	 * Get a list of phenotyping Centers we have data for e.g. query like below
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=*:*&indent=true&facet=true&facet.field=phenotyping_center&facet.mincount=1&wt=json&rows=0
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<String> getPhenotypeCenters() throws SolrServerException, IOException  {

		List<String> centers=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		.setQuery("*:*")
		.addFacetField(ObservationDTO.PHENOTYPING_CENTER)
		.setFacetMinCount(1)
		.setRows(0);
		if(SolrUtils.getBaseURL(experimentCore).endsWith("experiment")){
			query.addFilterQuery(ObservationDTO.DATASOURCE_NAME+":"+"\""+datasourceName+"\"");
		}

		QueryResponse response = experimentCore.query(query);
		//String resp = response.getResponse().toString();
		List<FacetField> fields = response.getFacetFields();
		//System.out.println("values="+fields.get(0).getValues());
		for(Count values: fields.get(0).getValues()){
			centers.add(values.getName());
		}

		return centers;
	}

	/**
	 * get the strains with data for a center
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=phenotyping_center:%22UC%20Davis%22&wt=json&indent=true&facet=true&facet.field=strain_accession_id&facet.mincount=1&rows=0
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<String> getStrainsForCenter(String center)  throws SolrServerException, IOException  {

		List<String> strains=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\"")
		.addFacetField(ObservationDTO.COLONY_ID)
		.setFacetMinCount(1)
		.setFacetLimit(-1)
		.setRows(0);
		if(SolrUtils.getBaseURL(experimentCore).endsWith("experiment")){
				query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":" + "\"" + datasourceName + "\"");
		}
		QueryResponse response = experimentCore.query(query);
		List<FacetField> fields = response.getFacetFields();
		for(Count values: fields.get(0).getValues()){
			strains.add(values.getName());
		}
		logger.info("getStrainsForCenter ---- " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);
		return strains;
	}

//	public Set<PhenotypeCenterServiceBean> getMutantStrainsForCenters() throws IOException, URISyntaxException {
//
//		Set<PhenotypeCenterServiceBean> mutantStrainsForCenters = new HashSet<>();
//
//		String queryString = ObservationDTO.BIOLOGICAL_SAMPLE_GROUP    + ":experimental";
//
//		String facetPivotFields =
//				    ObservationDTO.PHENOTYPING_CENTER
//			+ "," + ObservationDTO.COLONY_ID
//			+ "," + ObservationDTO.ZYGOSITY
//			+ "," + ObservationDTO.DEVELOPMENTAL_STAGE_NAME;
//
//		SolrQuery query = new SolrQuery()
//				.setQuery(queryString)
//				.setRows(0)
//				.setFacet(true)
//				.setFacetMinCount(1)
//				.setFacetLimit(-1)
//				.addFacetPivotField(facetPivotFields);
//
//		query
//				.set("wt", "xslt")
//				.set("tr", "pivot.xsl");
//		HttpProxy proxy   = new HttpProxy();
//		String    content = proxy.getContent(new URL(SolrUtils.getBaseURL(experimentCore) + "/select?" + query));
//
//		// Get the required unique rows.
//		mutantStrainsForCenters = Arrays.stream(content.split("\r"))
//				.skip(1)
//				.map(PhenotypeCenterServiceBean::new)
//				.collect(Collectors.toSet());
//
//		// Fill in the remaining PhenotypeCenterServiceBean fields: mgiAccessionId, allele, geneSymbol
//
//
//
//
//		return mutantStrainsForCenters;
//	}






	public List<PhenotypeCenterServiceBean> getMutantStrainsForCenter(String center)  throws SolrServerException, IOException  {

		List<PhenotypeCenterServiceBean> strains=new ArrayList<>();
		SolrQuery query = new SolrQuery()
			.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\" AND " + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
			.setFields(ObservationDTO.GENE_ACCESSION_ID,ObservationDTO.ALLELE_SYMBOL, ObservationDTO.GENE_SYMBOL, ObservationDTO.ZYGOSITY, ObservationDTO.DEVELOPMENTAL_STAGE_NAME)
			.setRows(1000000);
		query.set("group", true);
		query.set("group.field", ObservationDTO.COLONY_ID);
		query.set("group.limit", 1);


		if(SolrUtils.getBaseURL(experimentCore).endsWith("experiment")){
				query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":" + "\"" + datasourceName + "\"");
		}
		QueryResponse response = experimentCore.query(query);
		GroupResponse groups = response.getGroupResponse();
		for( Group group : groups.getValues().get(0).getValues()){
			PhenotypeCenterServiceBean strain = new PhenotypeCenterServiceBean();
			String colonyId = group.getGroupValue();
			if (colonyId != null && !colonyId.equalsIgnoreCase("null")){
				strain.setColonyId(colonyId);
				SolrDocument doc = group.getResult().get(0);
				strain.setAllele((String)doc.get(ObservationDTO.ALLELE_SYMBOL));
				strain.setGeneSymbol((String)doc.get(ObservationDTO.GENE_SYMBOL));
				strain.setMgiAccession((String)doc.get(ObservationDTO.GENE_ACCESSION_ID));
				strain.setZygosity((String)doc.get(ObservationDTO.ZYGOSITY));
				strain.setLifeStage((String)doc.get(ObservationDTO.DEVELOPMENTAL_STAGE_NAME));
				strains.add(strain);
			}
		}
		logger.info("getStrainsForCenter -- " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);
		return strains;
	}

	/**
	 * get the list of procedures per strain for the center
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=strain_accession_id:%22MGI:2164831%22&fq=phenotyping_center:%22UC%20Davis%22&wt=json&indent=true&facet=true&facet.field=procedure_name&facet.mincount=1&rows=0
	 *@param center
	 * @param strain
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<ProcedureDTO> getProceduresPerStrainForCenter(String center,String strain) throws SolrServerException, IOException  {

		List<ProcedureDTO> procedures=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		 .setQuery(ObservationDTO.COLONY_ID+":\""+strain+"\"")
		 .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER+":\""+center+"\"")
		 .addFacetField(ObservationDTO.PROCEDURE_NAME)
		 .addFacetField(ObservationDTO.PROCEDURE_STABLE_ID)
		 .setFacetMinCount(1)
		 .setRows(0);

		if(SolrUtils.getBaseURL(experimentCore).endsWith("experiment")){
			query.addFilterQuery(ObservationDTO.DATASOURCE_NAME+":"+"\""+datasourceName+"\"");
		}

		QueryResponse response = experimentCore.query(query);
		List<FacetField> fields = response.getFacetFields();
		for(int i=0; i< fields.get(0).getValues().size(); i++){
			ProcedureDTO procedure = new ProcedureDTO();
			procedure.setName(fields.get(0).getValues().get(i).getName());
			procedure.setStableId(fields.get(1).getValues().get(i).getName());
			procedures.add(procedure);
		}

		return procedures;
	}

	/**
	 * @author tudose
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public Map<String, List<String>> getProceduresPerCenter() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery()
		 .setQuery(ObservationDTO.DATASOURCE_NAME + ":IMPC")
		 .setFacet(true)
		 .setFacetLimit(-1)
		 .addFacetPivotField(ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.PROCEDURE_STABLE_ID)
		 .setFacetMinCount(1)
		 .setRows(0);
		QueryResponse response = experimentCore.query(query);
		Map<String, List<String>> res = new HashMap<>();
		List<PivotField> fields = response.getFacetPivot().get(ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.PROCEDURE_STABLE_ID);

		for (PivotField facet: fields){
			if (facet.getPivot() != null){
				List<String> proceduresList = new ArrayList<>();
				String center = facet.getValue().toString();
				List<PivotField> procedures = facet.getPivot();
				for (PivotField procedure : procedures){
					proceduresList.add(procedure.getValue().toString());
				}
				res.put(center, proceduresList);
			}
		}
		return res;
	}


	public List<String> getDoneProcedureIdsPerStrainAndCenter(String center,String strain) throws SolrServerException, IOException  {

		List<String> procedures=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		 .setQuery(ObservationDTO.COLONY_ID+":\""+strain+"\" AND " + ObservationDTO.DATASOURCE_NAME + ":IMPC")
		 .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER+":\""+center+"\"")
		 .addFacetField(ObservationDTO.PROCEDURE_STABLE_ID)
		 .setFacetLimit(-1)
		 .setFacetMinCount(1)
		 .setRows(0);

		QueryResponse response = experimentCore.query(query);
		List<FacetField> fields = response.getFacetFields();
		for( Count field: fields.get(0).getValues()){
			procedures.add(field.getName());
		}

		return procedures;
	}

	/**
	 * Uses the methods in this service to get center progress information for each center i.e. procedures we have data for on a per strain basis
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public Map<String, Map<String, List<ProcedureDTO>>> getCentersProgressInformation() throws SolrServerException, IOException  {

		//map of centers to a map of strain to procedures list
		Map<String,Map<String, List<ProcedureDTO>>> centerData = new HashMap<>();
		List<String> centers = this.getPhenotypeCenters();

		for(String center:centers){
			List<String> strains = this.getStrainsForCenter(center);
			Map<String,List<ProcedureDTO>> strainsToProcedures = new HashMap<>();

			for(String strain:strains){
				List<ProcedureDTO> procedures = this.getProceduresPerStrainForCenter(center, strain);
				strainsToProcedures.put(strain, procedures);
			}
			centerData.put(center, strainsToProcedures);
		}
		return centerData;
	}


	public Set<String> getMpsForProcedureSet(List<String> procedures, Map<String, Set<String>> mpsPerProcedure){

		HashSet<String> res = new HashSet<>();
		for (String procedure: procedures){
			if (mpsPerProcedure.get(procedure) != null){
				res.addAll(mpsPerProcedure.get(procedure));
			}
		}
		return res;
	}
}