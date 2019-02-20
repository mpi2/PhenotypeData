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
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class PhenotypeCenterService {
	private static final Logger LOG = LoggerFactory.getLogger(PhenotypeCenterService.class);
	private
	SolrClient solr;
	private final String datasourceName = "IMPC";//pipeline but takes care of things like WTSI MGP select is IMPC!

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ImpressService impressService;


	public PhenotypeCenterService() {
	}


	public PhenotypeCenterService(String baseSolrUrl, ImpressService impressService){
		solr = new HttpSolrClient(baseSolrUrl);
		this.impressService = impressService;

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
		if(SolrUtils.getBaseURL(solr).endsWith("experiment")){
			query.addFilterQuery(ObservationDTO.DATASOURCE_NAME+":"+"\""+datasourceName+"\"");
		}

		QueryResponse response = solr.query(query);
		//String resp = response.getResponse().toString();
		List<FacetField> fields = response.getFacetFields();
		//System.out.println("values="+fields.get(0).getValues());
		for(Count values: fields.get(0).getValues()){
			centers.add(values.getName());
		}
		//System.out.println("resp="+resp);
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
		if(SolrUtils.getBaseURL(solr).endsWith("experiment")){
				query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":" + "\"" + datasourceName + "\"");
		}
		QueryResponse response = solr.query(query);
		List<FacetField> fields = response.getFacetFields();
		for(Count values: fields.get(0).getValues()){
			strains.add(values.getName());
		}
		logger.info("getStrainsForCenter ---- " + SolrUtils.getBaseURL(solr) + "/select?" + query);
		return strains;
	}

	public List<StrainBean> getMutantStrainsForCenter(String center)  throws SolrServerException, IOException  {

		List<StrainBean> strains=new ArrayList<>();
		SolrQuery query = new SolrQuery()
			.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\" AND " + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
			.setFields(ObservationDTO.GENE_ACCESSION_ID,ObservationDTO.ALLELE_SYMBOL, ObservationDTO.GENE_SYMBOL, ObservationDTO.ZYGOSITY, ObservationDTO.DEVELOPMENTAL_STAGE_NAME)
			.setRows(1000000);
		query.set("group", true);
		query.set("group.field", ObservationDTO.COLONY_ID);
		query.set("group.limit", 1);


		if(SolrUtils.getBaseURL(solr).endsWith("experiment")){
				query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":" + "\"" + datasourceName + "\"");
		}
		QueryResponse response = solr.query(query);
		GroupResponse groups = response.getGroupResponse();
		for( Group group : groups.getValues().get(0).getValues()){
			StrainBean strain = new StrainBean();
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
		LOG.info("getStrainsForCenter -- " + SolrUtils.getBaseURL(solr) + "/select?" + query);
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

		if(SolrUtils.getBaseURL(solr).endsWith("experiment")){
			query.addFilterQuery(ObservationDTO.DATASOURCE_NAME+":"+"\""+datasourceName+"\"");
		}

		QueryResponse response = solr.query(query);
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
		QueryResponse response = solr.query(query);
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

		QueryResponse response = solr.query(query);
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


	/**
	 * @author tudose
	 * @return
	 * @throws SolrServerException, IOException
	 * @throws SQLException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException, IOException , SQLException {

		List<String> centers = getPhenotypeCenters();
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
        header.add("Percentage Done");
        header.add("Number of Done Procedures");
        header.add("Done Procedures");
        header.add("MPS Tested For");
        header.add("Number of Missing Procedures");
        header.add("Missing Procedures");
        header.add("MPS Not Tested For");
		results.add(header.toArray(temp));
		Map<String, Set<String>> mpsPerProcedure = impressService.getMpsForProcedures();
		Map<String, List<String>> possibleProceduresPerCenter = getProceduresPerCenter();

		for(String center: centers){
			List<StrainBean> strains = getMutantStrainsForCenter(center);

			for(StrainBean strain: strains){
				List<String> procedures = getDoneProcedureIdsPerStrainAndCenter(center, strain.getColonyId());
				List<String> row = new ArrayList<>();
				row.add(strain.getColonyId());
				row.add(strain.getGeneSymbol());
				row.add(strain.getMgiAccession());
				row.add(strain.getAllele());

				row.add(strain.getZygosity());
				row.add(strain.getLifeStage());
				row.add(center);
				Float percentageDone = (float) ((procedures.size() * 100) / (float)possibleProceduresPerCenter.get(center).size());
				row.add(percentageDone.toString());
				row.add("" + procedures.size()); // #procedures done
				row.add(procedures.toString()); // procedures done
				Set<String> mpsTestedFor = getMpsForProcedureSet(procedures, mpsPerProcedure);
				row.add(mpsTestedFor.toString()); //mpsTestedFor
				row.add("" + (possibleProceduresPerCenter.get(center).size() - procedures.size()));	// #missing procedures
				List<String> missing = new ArrayList<>(possibleProceduresPerCenter.get(center));
				missing.removeAll(procedures); // missing procedures
				row.add(missing.toString());
				Set<String> mpsNotTestedFor = getMpsForProcedureSet(missing, mpsPerProcedure);
				mpsNotTestedFor.removeAll(mpsTestedFor);
				row.add(mpsNotTestedFor.toString());
				results.add(row.toArray(temp));
			}
		}
		return results;
	}

	private Set<String> getMpsForProcedureSet(List<String> procedures, Map<String, Set<String>> mpsPerProcedure){

		HashSet<String> res = new HashSet<>();
		for (String procedure: procedures){
			if (mpsPerProcedure.get(procedure) != null){
				res.addAll(mpsPerProcedure.get(procedure));
			}
		}
		return res;
	}

}

class StrainBean{

	String mgiAccession;
	String allele;
	String geneSymbol;
	String colonyId;
	String zygosity;
	String lifeStage;


	public String getMgiAccession() {

		return mgiAccession;
	}

	public void setMgiAccession(String mgiAccession) {

		this.mgiAccession = mgiAccession;
	}

	public String getAllele() {

		return allele;
	}

	public void setAllele(String allele) {

		this.allele = allele;
	}

	public String getGeneSymbol() {

		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {

		this.geneSymbol = geneSymbol;
	}


	public String getColonyId() {

		return colonyId;
	}


	public void setColonyId(String colonyId) {

		this.colonyId = colonyId;
	}

	public String getZygosity() {
		return zygosity;
	}

	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}

	public String getLifeStage() {
		return lifeStage;
	}

	public void setLifeStage(String lifeStage) {
		this.lifeStage = lifeStage;
	}
}
