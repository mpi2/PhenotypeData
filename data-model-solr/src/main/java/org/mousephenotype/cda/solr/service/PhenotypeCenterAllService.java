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
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Created this new class to duplicate functionality of PhenotypeCenterService but with dataSource ALL instead of just
 * IMPC. While it's messy to duplicate the code, it's more of a risk for the ProcedureCompletenessAllReport to accidentally
 * call the IMPC-only methods in PhenotypeCenterService and quietly and incorrectly return only IMPC data. This new
 * class allows us to remove the ProcedureCompletenessAllReport dependency on an IMPC-only service.
 *
 * Hopefully, someday this entire reposts module will be replaced with something more flexible.
 */

@Service
public class PhenotypeCenterAllService {

    private final Logger     logger         = LoggerFactory.getLogger(this.getClass());
    private       SolrClient statisticalResultCore;

	public PhenotypeCenterAllService(
			@Qualifier("statisticalResultCore")
			SolrClient statisticalResultCore)
	{
		this.statisticalResultCore = statisticalResultCore;
	}

	// FIXME FIXME FIXME
	/**
	 * Get a list of all phenotyping Centers we have data for
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=*:*&indent=true&facet=true&facet.field=phenotyping_center&facet.mincount=1&wt=json&rows=0
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<String> getPhenotypeCenters() throws SolrServerException, IOException  {

		List<String> centers=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		.setQuery("*:*")
		.addFacetField(StatisticalResultDTO.PHENOTYPING_CENTER)
		.setFacetMinCount(1)
		.setRows(0);

		QueryResponse response = statisticalResultCore.query(query);
		List<FacetField> fields = response.getFacetFields();
		for(FacetField.Count values: fields.get(0).getValues()){
			centers.add(values.getName());
		}

		return centers;
	}

//	/**
//	 * get the strains with data for a center
//	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=phenotyping_center:%22UC%20Davis%22&wt=json&indent=true&facet=true&facet.field=strain_accession_id&facet.mincount=1&rows=0
//	 * @return
//	 * @throws SolrServerException, IOException
//	 */
//	public List<String> getStrainsForCenter(String center)  throws SolrServerException, IOException  {
//
//		List<String> strains=new ArrayList<>();
//		SolrQuery query = new SolrQuery()
//		.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\"")
//		.addFacetField(ObservationDTO.COLONY_ID)
//		.setFacetMinCount(1)
//		.setFacetLimit(-1)
//		.setRows(0);
//
//		QueryResponse response = statisticalResultCore.query(query);
//		List<FacetField> fields = response.getFacetFields();
//		for(Count values: fields.get(0).getValues()){
//			strains.add(values.getName());
//		}
//		logger.info("getStrainsForCenter ---- " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query);
//		return strains;
//	}

//	public Set<PhenotypeCenterAllServiceBean> getMutantStrainsForCenters() throws IOException, URISyntaxException {
//
//		Set<PhenotypeCenterAllServiceBean> mutantStrainsForCenters = new HashSet<>();
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
//		String    content = proxy.getContent(new URL(SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query));
//
//		// Get the required unique rows.
//		mutantStrainsForCenters = Arrays.stream(content.split("\r"))
//				.skip(1)
//				.map(PhenotypeCenterAllServiceBean::new)
//				.collect(Collectors.toSet());
//
//		// Fill in the remaining PhenotypeCenterAllServiceBean fields: mgiAccessionId, allele, geneSymbol
//
//
//
//
//		return mutantStrainsForCenters;
//	}






//	public List<PhenotypeCenterAllServiceBean> getMutantStrainsForCenter(String center)  throws SolrServerException, IOException  {
//
//		List<PhenotypeCenterAllServiceBean> strains=new ArrayList<>();
//		SolrQuery query = new SolrQuery()
//			.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\" AND " + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
//			.setFields(ObservationDTO.GENE_ACCESSION_ID,ObservationDTO.ALLELE_SYMBOL, ObservationDTO.GENE_SYMBOL, ObservationDTO.ZYGOSITY, ObservationDTO.DEVELOPMENTAL_STAGE_NAME)
//			.setRows(1000000);
//		query.set("group", true);
//		query.set("group.field", ObservationDTO.COLONY_ID);
//		query.set("group.limit", 1);
//
//		QueryResponse response = statisticalResultCore.query(query);
//		GroupResponse groups = response.getGroupResponse();
//		for( Group group : groups.getValues().get(0).getValues()){
//			PhenotypeCenterAllServiceBean strain = new PhenotypeCenterAllServiceBean();
//			String colonyId = group.getGroupValue();
//			if (colonyId != null && !colonyId.equalsIgnoreCase("null")){
//				strain.setColonyId(colonyId);
//				SolrDocument doc = group.getResult().get(0);
//				strain.setAllele((String)doc.get(ObservationDTO.ALLELE_SYMBOL));
//				strain.setGeneSymbol((String)doc.get(ObservationDTO.GENE_SYMBOL));
//				strain.setMgiAccession((String)doc.get(ObservationDTO.GENE_ACCESSION_ID));
//				strain.setZygosity((String)doc.get(ObservationDTO.ZYGOSITY));
//				strain.setLifeStage((String)doc.get(ObservationDTO.DEVELOPMENTAL_STAGE_NAME));
//				strains.add(strain);
//			}
//		}
//		logger.info("getStrainsForCenter -- " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query);
//		return strains;
//	}

//	/**
//	 * get the list of procedures per strain for the center
//	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=strain_accession_id:%22MGI:2164831%22&fq=phenotyping_center:%22UC%20Davis%22&wt=json&indent=true&facet=true&facet.field=procedure_name&facet.mincount=1&rows=0
//	 *@param center
//	 * @param strain
//	 * @return
//	 * @throws SolrServerException, IOException
//	 */
//	public List<ProcedureDTO> getProceduresPerStrainForCenter(String center,String strain) throws SolrServerException, IOException  {
//
//		List<ProcedureDTO> procedures=new ArrayList<>();
//		SolrQuery query = new SolrQuery()
//		 .setQuery(ObservationDTO.COLONY_ID+":\""+strain+"\"")
//		 .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER+":\""+center+"\"")
//		 .addFacetField(ObservationDTO.PROCEDURE_NAME)
//		 .addFacetField(ObservationDTO.PROCEDURE_STABLE_ID)
//		 .setFacetMinCount(1)
//		 .setRows(0);
//
//		QueryResponse response = statisticalResultCore.query(query);
//		List<FacetField> fields = response.getFacetFields();
//		for(int i=0; i< fields.get(0).getValues().size(); i++){
//			ProcedureDTO procedure = new ProcedureDTO();
//			procedure.setName(fields.get(0).getValues().get(i).getName());
//			procedure.setStableId(fields.get(1).getValues().get(i).getName());
//			procedures.add(procedure);
//		}
//
//		return procedures;
//	}
//
//	/**
//	 * @author tudose
//	 * @return
//	 * @throws SolrServerException, IOException
//	 */
//	public Map<String, List<String>> getProceduresPerCenter() throws SolrServerException, IOException {
//
//		SolrQuery query = new SolrQuery()
//		 .setQuery(ObservationDTO.DATASOURCE_NAME + ":IMPC")
//		 .setFacet(true)
//		 .setFacetLimit(-1)
//		 .addFacetPivotField(ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.PROCEDURE_STABLE_ID)
//		 .setFacetMinCount(1)
//		 .setRows(0);
//		QueryResponse response = statisticalResultCore.query(query);
//		Map<String, List<String>> res = new HashMap<>();
//		List<PivotField> fields = response.getFacetPivot().get(ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.PROCEDURE_STABLE_ID);
//
//		for (PivotField facet: fields){
//			if (facet.getPivot() != null){
//				List<String> proceduresList = new ArrayList<>();
//				String center = facet.getValue().toString();
//				List<PivotField> procedures = facet.getPivot();
//				for (PivotField procedure : procedures){
//					proceduresList.add(procedure.getValue().toString());
//				}
//				res.put(center, proceduresList);
//			}
//		}
//		return res;
//	}
//
//
//	public List<String> getDoneProcedureIdsPerStrainAndCenter(String center,String strain) throws SolrServerException, IOException  {
//
//		List<String> procedures=new ArrayList<>();
//		SolrQuery query = new SolrQuery()
//		 .setQuery(ObservationDTO.COLONY_ID+":\""+strain+"\" AND " + ObservationDTO.DATASOURCE_NAME + ":IMPC")
//		 .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER+":\""+center+"\"")
//		 .addFacetField(ObservationDTO.PROCEDURE_STABLE_ID)
//		 .setFacetLimit(-1)
//		 .setFacetMinCount(1)
//		 .setRows(0);
//
//		QueryResponse response = statisticalResultCore.query(query);
//		List<FacetField> fields = response.getFacetFields();
//		for( Count field: fields.get(0).getValues()){
//			procedures.add(field.getName());
//		}
//
//		return procedures;
//	}
//
//	/**
//	 * Uses the methods in this service to get center progress information for each center i.e. procedures we have data for on a per strain basis
//	 * @return
//	 * @throws SolrServerException, IOException
//	 */
//	public Map<String, Map<String, List<ProcedureDTO>>> getCentersProgressInformation() throws SolrServerException, IOException  {
//
//		//map of centers to a map of strain to procedures list
//		Map<String,Map<String, List<ProcedureDTO>>> centerData = new HashMap<>();
//		List<String> centers = this.getPhenotypeCenters();
//
//		for(String center:centers){
//			List<String> strains = this.getStrainsForCenter(center);
//			Map<String,List<ProcedureDTO>> strainsToProcedures = new HashMap<>();
//
//			for(String strain:strains){
//				List<ProcedureDTO> procedures = this.getProceduresPerStrainForCenter(center, strain);
//				strainsToProcedures.put(strain, procedures);
//			}
//			centerData.put(center, strainsToProcedures);
//		}
//		return centerData;
//	}
//
//
//	public Set<String> getMpsForProcedureSet(List<String> procedures, Map<String, Set<String>> mpsPerProcedure){
//
//		HashSet<String> res = new HashSet<>();
//		for (String procedure: procedures){
//			if (mpsPerProcedure.get(procedure) != null){
//				res.addAll(mpsPerProcedure.get(procedure));
//			}
//		}
//		return res;
//	}





    public Map<String, String> getKeyValuePairs(String pivotFacet) {

        Map<String, String> map  = new HashMap<>();

        SolrQuery query = new SolrQuery();

        query
                .setQuery("*:*")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .add("facet.pivot", pivotFacet);

        try {
            QueryResponse    response = statisticalResultCore.query(query);

            for( PivotField pivot : response.getFacetPivot().get(pivotFacet)) {
                if (pivot.getPivot() != null){
                    for (PivotField parameter : pivot.getPivot()){
                        String[] row = {pivot.getValue().toString(), parameter.getValue().toString()};
                        map.put(row[0], row[1]);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return map;
    }


    public Map<String, String> getProcedureNamesById() {
        final
        String pivotFacet =  StatisticalResultDTO.PROCEDURE_STABLE_ID + "," + StatisticalResultDTO.PROCEDURE_NAME;
        return getKeyValuePairs(pivotFacet);
    }


    public Map<String, String> getParameterNamesById() {
        final
        String pivotFacet =  StatisticalResultDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.PARAMETER_NAME;
        return getKeyValuePairs(pivotFacet);
    }


    public Map<String, String> getTopLevelMpNamesById() {
        final
        String pivotFacet =  StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID + "," + StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME;
        return getKeyValuePairs(pivotFacet);
    }


    public Map<String, String> getMpNamesById() {
        final
        String pivotFacet =  StatisticalResultDTO.MP_TERM_ID + "," + StatisticalResultDTO.MP_TERM_NAME;
        return getKeyValuePairs(pivotFacet);
    }


	public Set<PhenotypeCenterAllServiceBean> getDataByCenter(String center) throws SolrServerException, IOException {

		final String[] fields = {

				StatisticalResultDTO.PHENOTYPING_CENTER,
				StatisticalResultDTO.COLONY_ID,
				StatisticalResultDTO.MARKER_ACCESSION_ID,
				StatisticalResultDTO.MARKER_SYMBOL,
				StatisticalResultDTO.ALLELE_ACCESSION_ID,
				StatisticalResultDTO.ALLELE_SYMBOL,
				StatisticalResultDTO.PROCEDURE_STABLE_ID,
				StatisticalResultDTO.PROCEDURE_NAME,
				StatisticalResultDTO.PARAMETER_STABLE_ID,
				StatisticalResultDTO.PARAMETER_NAME,
				StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID,
				StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME,
				StatisticalResultDTO.MP_TERM_ID,
				StatisticalResultDTO.MP_TERM_NAME,
				StatisticalResultDTO.ZYGOSITY,
				StatisticalResultDTO.LIFE_STAGE_NAME,
				StatisticalResultDTO.STATUS

		};
				;
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*");
		query.setFilterQueries(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + center + "\"");
		query.setFields(fields);
		query.setRows(1000000);
        QueryResponse response = statisticalResultCore.query(query);

		List<StatisticalResultDTO> dtos = response.getBeans(StatisticalResultDTO.class);

		HashSet<PhenotypeCenterAllServiceBean>  data = new HashSet<>();
		for (StatisticalResultDTO dto : dtos) {

				// Add a separate row for every topLevelMpTermId
				PhenotypeCenterAllServiceBean bean = new PhenotypeCenterAllServiceBean();

				bean.setColonyId(dto.getColonyId());
				bean.setZygosity(dto.getZygosity());
				bean.setGeneAccessionId(dto.getMarkerAccessionId());
				bean.setGeneSymbol(dto.getMarkerSymbol());
				bean.setAlleleAccessionId(dto.getAlleleAccessionId());
				bean.setAlleleSymbol(dto.getAlleleSymbol());
				bean.setProcedureStableId(dto.getProcedureStableId());
				bean.setParameterStableId(dto.getParameterStableId());
				bean.setTopLevelMpTermId(dto.getTopLevelMpTermId());
				bean.setMpTermId(dto.getMpTermId());
				bean.setLifeStageName(dto.getLifeStageName());
				bean.setStatus(dto.getStatus());

				data.add(bean);
		}

		return data;
	}
}