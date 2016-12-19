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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.constants.OverviewChartsConstants;
import org.mousephenotype.cda.db.beans.AggregateCountXYBean;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.web.dto.GeneRowForHeatMap;
import org.mousephenotype.cda.solr.web.dto.HeatMapCell;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypeTableRowAnatomyPage;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class AbstractGenotypePhenotypeService extends BasicService {

    
    @Autowired
    ImpressService is;

    protected SolrClient solr;

    protected Boolean isPreQc;

    public static final double P_VALUE_THRESHOLD = 0.0001;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @param zygosity - optional (pass null if not needed)
     * @return Map <String, Long> : <top_level_mp_name, number_of_annotations>
     * @author tudose
     */
    public TreeMap<String, Long> getDistributionOfAnnotationsByMPTopLevel(ZygosityType zygosity, String resourceName) {

        SolrQuery query = new SolrQuery();

        if (zygosity != null) {
            query.setQuery(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.getName());
        } else if (resourceName != null){
            query.setFilterQueries(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + resourceName);
        }else {
            query.setQuery("*:*");
        }

        query.setFacet(true);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);
        query.setRows(0);
        query.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);

        try {
            QueryResponse response = solr.query(query);
            TreeMap<String, Long> res = new TreeMap<>();
            res.putAll(getFacets(response).get(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME));
            return res;
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getNumberOfDocuments(List<String> resourceName )
        throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.setRows(0);
        if (resourceName != null){
            query.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            query.setQuery("*:*");
        }

        return solr.query(query).getResults().getNumFound();
    }

    /**
     * @since 2016/07/04
     * @author ilinca
     * @param anatomyId
     * @return
     * @throws SolrServerException, IOException
     */
    public List<GenotypePhenotypeDTO> getPhenotypesForAnatomy(String anatomyId) 
    throws SolrServerException, IOException {
    	
    	SolrQuery query = new SolrQuery();
    	query.setRows(Integer.MAX_VALUE);
    	query.setQuery(GenotypePhenotypeDTO.ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " + 
    				GenotypePhenotypeDTO.INTERMEDIATE_ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " +  
    				GenotypePhenotypeDTO.TOP_LEVEL_ANATOMY_TERM_ID + ":\"" + anatomyId + "\"");
    	
    	query.setFields(GenotypePhenotypeDTO.MP_TERM_ID, GenotypePhenotypeDTO.MP_TERM_NAME, GenotypePhenotypeDTO.P_VALUE,
    			GenotypePhenotypeDTO.MARKER_ACCESSION_ID, GenotypePhenotypeDTO.MARKER_SYMBOL, GenotypePhenotypeDTO.SEX,
    			GenotypePhenotypeDTO.ZYGOSITY);
    	
    	return solr.query(query).getBeans(GenotypePhenotypeDTO.class);
    	    	
    }
    
    public Collection<PhenotypeTableRowAnatomyPage> getCollapsedPhenotypesForAnatomy(String anatomyId, String baseUrl) 
    throws SolrServerException, IOException {
    	
    	Map<String, PhenotypeTableRowAnatomyPage> res = new HashMap<>();
    	List<GenotypePhenotypeDTO> phenotypes = getPhenotypesForAnatomy(anatomyId);
    	
    	for (GenotypePhenotypeDTO call : phenotypes){
    		
    		PhenotypeTableRowAnatomyPage row = new PhenotypeTableRowAnatomyPage();
    		if (res.containsKey(call.getMpTermId())){
    			row  = res.get(call.getMpTermId());
    		} else {
    			row.setPhenotypeTerm(new BasicBean(call.getMpTermId(), call.getMpTermName()));
    		}
    		MarkerBean gene = new MarkerBean();
    		gene.setAccessionId(call.getMarkerAccessionId());
    		gene.setSymbol(call.getMarkerSymbol());
    		//System.out.println("call allename="+call.getAlleleName());
    		//gene.setName(call.getAlleleName());
    		row.addGenes(gene);
    		res.put(call.getMpTermId(), row);
    		
    	}
    	return res.values();    	
    }
    
    
    public List<String[]> getHitsDistributionByProcedure(List<String> resourceName)
    throws SolrServerException, IOException , InterruptedException, ExecutionException {

        return getHitsDistributionBySomething(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID, resourceName);
    }

    public List<String[]> getHitsDistributionByParameter(List<String> resourceName)
    throws SolrServerException, IOException , InterruptedException, ExecutionException {

        return getHitsDistributionBySomething(GenotypePhenotypeDTO.PARAMETER_STABLE_ID, resourceName);
    }


    public Map<String, Long> getHitsDistributionBySomethingNoIds(String fieldToDistributeBy, List<String> resourceName, ZygosityType zygosity, int facetMincount, Double maxPValue)
    throws SolrServerException, IOException , InterruptedException, ExecutionException {

        Map<String, Long>  res = new HashMap<>();
        Long time = System.currentTimeMillis();
        SolrQuery q = new SolrQuery();

        if (resourceName != null){
            q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }

        if (zygosity != null){
            q.addFilterQuery(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.name());
        }

        if (maxPValue != null){
            q.addFilterQuery(GenotypePhenotypeDTO.P_VALUE+ ":[0 TO " + maxPValue + "]");
        }

        q.addFacetField(fieldToDistributeBy);
        q.setFacetMinCount(facetMincount);
        q.setFacet(true);
        q.setRows(1);
        q.set("facet.limit", -1);

        logger.info("Solr url for getHitsDistributionByParameter " + SolrUtils.getBaseURL(solr) + "/select?" + q);
        QueryResponse response = solr.query(q);

        for( Count facet : response.getFacetField(fieldToDistributeBy).getValues()){
            String value = facet.getName();
            long count = facet.getCount();
            res.put(value,count);
        }

        logger.info("Done in " + (System.currentTimeMillis() - time));
        return res;

    }

    public TreeSet<CountTableRow> getAssociationsCount(String mpId, List<String> resourceName) throws IOException, SolrServerException {

        TreeSet<CountTableRow> list = new TreeSet<>(CountTableRow.getComparatorByCount());
        SolrQuery q = new SolrQuery().setFacet(true).setRows(1);
        q.set("facet.limit", -1);

        if (resourceName != null){
            q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }

        if (mpId != null){
            q.addFilterQuery(GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\"" );
        }

        String pivot = GenotypePhenotypeDTO.MP_TERM_ID + "," + GenotypePhenotypeDTO.MP_TERM_NAME;
        q.add("facet.pivot", pivot);


        logger.info("Solr url for getAssociationsCount " + SolrUtils.getBaseURL(solr) + "/select?" + q);

        QueryResponse response = solr.query(q);
        for (PivotField p : response.getFacetPivot().get(pivot)){
            String mpTermId = p.getValue().toString();
            String mpName = p.getPivot().get(0).getValue().toString();
            int count = p.getPivot().get(0).getCount();
            list.add(new CountTableRow(mpName, mpId, count));
        }

        return list;
    }


    public Map<String, List<String>> getMpTermByGeneMap(List<String> geneSymbols, String facetPivot, List<String> resourceName)
        throws SolrServerException, IOException , InterruptedException, ExecutionException {

        Map<String, List<String>> mpTermsByGene = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        SolrQuery q = new SolrQuery().setFacet(true).setRows(1);
        q.set("facet.limit", -1);

        if (resourceName != null){
            q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }

        if (facetPivot != null) {
            q.add("facet.pivot", facetPivot);
        }

        logger.info("Solr url for getOverviewGenesWithMoreProceduresThan " + SolrUtils.getBaseURL(solr) + "/select?" + q);

        QueryResponse response = solr.query(q);
        for (PivotField pivot : response.getFacetPivot().get(facetPivot)){

            String mpTerm = pivot.getValue().toString();

            if( ! mpTermsByGene.containsKey(mpTerm)) {
                mpTermsByGene.put(mpTerm, new ArrayList<String>());
            }

            for (PivotField genePivot : pivot.getPivot()){
                String gene = genePivot.getValue().toString();
                if (geneSymbols.contains(gene)){
                    mpTermsByGene.get(mpTerm).add(gene);
                }
            }
        }

        return mpTermsByGene;
    }

    private List<String[]> getHitsDistributionBySomething(String field, List<String> resourceName)
        throws SolrServerException, IOException , InterruptedException, ExecutionException {

        List<String[]>  res = new ArrayList<>();
        Long time = System.currentTimeMillis();
        String pivotFacet = "";
        SolrQuery q = new SolrQuery();

        if (field.equals(GenotypePhenotypeDTO.PARAMETER_STABLE_ID)){
            pivotFacet =  GenotypePhenotypeDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.PARAMETER_NAME;
        } else if (field.equals(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID)){
            pivotFacet =  GenotypePhenotypeDTO.PROCEDURE_STABLE_ID + "," + StatisticalResultDTO.PROCEDURE_NAME;
        }

        if (resourceName != null){
            q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }
        q.set("facet.pivot", pivotFacet);
        q.setFacet(true);
        q.setRows(1);
        q.set("facet.limit", -1);

        logger.info("Solr url for getHitsDistributionByParameter " + SolrUtils.getBaseURL(solr) + "/select?" + q);
        QueryResponse response = solr.query(q);

        for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
            String id = pivot.getValue().toString();
            String name = pivot.getPivot().get(0).getValue().toString();
            int count = pivot.getPivot().get(0).getCount();
            String[] row = {id, name, Integer.toString(count)};
            res.add(row);
        }

        logger.info("Done in " + (System.currentTimeMillis() - time));
        return res;

    }

    public List<AggregateCountXYBean> getAggregateCountXYBean(TreeMap<String, TreeMap<String, Long>> map) {
        List<AggregateCountXYBean> res = new ArrayList<>();

        for (String category : map.navigableKeySet()) {
            for (String bin : map.get(category).navigableKeySet()) {
                AggregateCountXYBean bean = new AggregateCountXYBean(map.get(category).get(bin).intValue(), bin, bin, "xAttribute", category, category, "yAttribute");
                res.add(bean);
            }
        }
        return res;
    }

    /**
     * Returns a list of a all colonies
     *
     * @param phenotypeResourceName
     * @return
     * @throws SolrServerException, IOException
     */
    public List<GenotypePhenotypeDTO> getAllMPByPhenotypingCenterAndColonies(String phenotypeResourceName)
        throws SolrServerException, IOException  {

        List<String> fields = Arrays.asList(GenotypePhenotypeDTO.PHENOTYPING_CENTER, GenotypePhenotypeDTO.MP_TERM_ID,
            GenotypePhenotypeDTO.MP_TERM_NAME, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME,
            GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID, GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_NAME,
            GenotypePhenotypeDTO.COLONY_ID, GenotypePhenotypeDTO.MARKER_SYMBOL, GenotypePhenotypeDTO.MARKER_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .addFilterQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + phenotypeResourceName)
            .setRows(MAX_NB_DOCS)
            .setFields(StringUtils.join(fields, ","));

        QueryResponse response = solr.query(query);
        return response.getBeans(GenotypePhenotypeDTO.class);

    }

  

    public List<Group> getGenesBy(String mpId, String sex, boolean onlyB6N)
        throws SolrServerException, IOException  {

        // males only
        SolrQuery q = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " +
        			GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " +
        			GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\")");
        if (onlyB6N){
            q.setFilterQueries("(" + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsConstants.B6N_STRAINS, "\" OR " +
                GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\")");
        }
        q.setRows(10000000);
        q.set("group.field", "" + GenotypePhenotypeDTO.MARKER_SYMBOL);
        q.set("group", true);
        q.set("group.limit", 0);
        if (sex != null) {
            q.addFilterQuery(GenotypePhenotypeDTO.SEX + ":" + sex);
        }
        QueryResponse results = solr.query(q);
        
        return results.getGroupResponse().getValues().get(0).getValues();
    }

    public List<String> getGenesAssocByParamAndMp(String parameterStableId, String phenotype_id)
        throws SolrServerException, IOException  {

        List<String> res = new ArrayList<String>();
        SolrQuery query = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\""
            + phenotype_id + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\") AND ("
            + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsConstants.B6N_STRAINS, "\" OR " + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\") AND "
            + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + parameterStableId + "\"").setRows(100000000);
        query.set("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.set("group", true);
        List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            if ( ! res.contains((String) gr.getGroupValue())) {
                res.add((String) gr.getGroupValue());
            }
        }
        return res;
    }

    /**
     * Returns a set of MARKER_ACCESSION_ID strings of all genes that have
     * phenotype associations.
     *
     * @return a set of MARKER_ACCESSION_ID strings of all genes that have
     * phenotype associations.
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllGenesWithPhenotypeAssociations()
        throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":*");
        solrQuery.setRows(1000000);
        solrQuery.setFields(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        QueryResponse rsp = null;
        rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allGenes = new HashSet<String>();
        for (SolrDocument doc : res) {
            allGenes.add((String) doc.getFieldValue(GenotypePhenotypeDTO.MARKER_ACCESSION_ID));
        }
        return allGenes;
    }

    /**
     * Returns a set of MP_TERM_ID strings of all phenotypes that have gene
     * associations.
     *
     * @return a set of MP_TERM_ID strings of all phenotypes that have gene
     * associations.
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllPhenotypesWithGeneAssociations()
        throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(GenotypePhenotypeDTO.MP_TERM_ID + ":*");
        solrQuery.setRows(1000000);
        solrQuery.setFields(GenotypePhenotypeDTO.MP_TERM_ID);
        QueryResponse rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allPhenotypes = new HashSet<String>();
        for (SolrDocument doc : res) {
            allPhenotypes.add((String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_ID));
        }

        return allPhenotypes;
    }

    /**
     * Returns a set of MP_TERM_ID strings of all top-level phenotypes.
     *
     * @return a set of MP_TERM_ID strings of all top-level phenotypes.
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllTopLevelPhenotypes()
        throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":*");
        solrQuery.setRows(1000000);
        solrQuery.setFields(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
        QueryResponse rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allTopLevelPhenotypes = new HashSet<String>();
        for (SolrDocument doc : res) {
            List<String> ids = getListFromCollection(doc.getFieldValues(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID));
            for (String id : ids) {
                allTopLevelPhenotypes.add(id);
            }
        }

        return allTopLevelPhenotypes;
    }

    /**
     * Returns a set of MP_TERM_ID strings of all intermediate-level phenotypes.
     *
     * @return a set of MP_TERM_ID strings of all intermediate-level phenotypes.
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllIntermediateLevelPhenotypes()
        throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":*");
        solrQuery.setRows(1000000);
        solrQuery.setFields(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID);
        QueryResponse rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allIntermediateLevelPhenotypes = new HashSet<String>();
        for (SolrDocument doc : res) {
            List<String> ids = getListFromCollection(doc.getFieldValues(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID));
            for (String id : ids) {
                allIntermediateLevelPhenotypes.add(id);
            }
        }

        return allIntermediateLevelPhenotypes;
    }


    /*
     * Methods used by PhenotypeSummaryDAO
     */
    public SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID, ZygosityType zygosity)
        throws SolrServerException, IOException  {

        String query;
        if (gene.equalsIgnoreCase("*")) {
            query = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":" + gene + " AND ";
        } else {
            query = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\" AND ";

        }

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query + "(" + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpID + "\" OR " + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpID + "\")");
        solrQuery.setRows(1000000);
        solrQuery.setSort(StatisticalResultDTO.P_VALUE, ORDER.asc);
        solrQuery.setFields(GenotypePhenotypeDTO.P_VALUE, GenotypePhenotypeDTO.SEX, GenotypePhenotypeDTO.ZYGOSITY, GenotypePhenotypeDTO.MARKER_ACCESSION_ID, GenotypePhenotypeDTO.MARKER_SYMBOL,
        		GenotypePhenotypeDTO.MP_TERM_ID, GenotypePhenotypeDTO.MP_TERM_NAME, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
        

        if (zygosity != null) {
            solrQuery.setFilterQueries(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.getName());
        }
        
        SolrDocumentList result = solr.query(solrQuery).getResults();
               
        return result;
    }

    public SolrDocumentList getPhenotypes(String gene)
    throws SolrServerException, IOException  {

        SolrDocumentList result = runQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
        return result;
    }

    
    public List<GenotypePhenotypeDTO> getAllGenotypePhenotypes(List<String> resourceName) 
    throws SolrServerException, IOException  {

        SolrQuery query = new SolrQuery().setRows(1000000);
       
        if (resourceName != null){
            query.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":(" + StringUtils.join(resourceName, " OR ") + ")");
        }else {
            query.setQuery("*:*");
        }

        return solr.query(query).getBeans(GenotypePhenotypeDTO.class);

    }
    
    
    public Set<String> getAllGenotypePhenotypesForGene(String markerAccession) 
    throws SolrServerException, IOException  {

        Set <String> alleles = new HashSet<>();
        SolrQuery query = new SolrQuery().setRows(Integer.MAX_VALUE);
        query.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + markerAccession + "\"");
        query.setFields(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID);
        List<GenotypePhenotypeDTO> results = solr.query(query).getBeans(GenotypePhenotypeDTO.class);
        
        for ( GenotypePhenotypeDTO doc : results){
        	alleles.add(doc.getAlleleAccessionId());
        }
        
        return alleles;
    }

    
    public List<GenotypePhenotypeDTO> getPhenotypeDTOs(String gene) 
    throws SolrServerException, IOException  {
        SolrQuery query = new SolrQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"")
            .setRows(Integer.MAX_VALUE);

        return solr.query(query).getBeans(GenotypePhenotypeDTO.class);

    }

    private SolrDocumentList runQuery(String q)
        throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery().setQuery(q);
        solrQuery.setRows(1000000);
        QueryResponse rsp = null;
        rsp = solr.query(solrQuery);
        return rsp.getResults();
    }

    
    /**
     * 
     * @param gene
     * @param zyg
     * @return HashMap topLevelTerms <mp_id, mp_name>
     * @throws SolrServerException, IOException
     */
    public Map<String, String> getTopLevelMPTerms(String gene, ZygosityType zyg)
        throws SolrServerException, IOException  {

        Map<String, String> tl = new HashMap<>();

        SolrQuery query = new SolrQuery();
        if (gene.equalsIgnoreCase("*")) {
            query.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":" + gene);
        } else {
            query.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
        }
        query.setRows(Integer.MAX_VALUE);
        if (zyg != null) {
            query.setFilterQueries(GenotypePhenotypeDTO.ZYGOSITY + ":" + zyg.getName());
        }
        query.setFields(
                GenotypePhenotypeDTO.MP_TERM_ID, GenotypePhenotypeDTO.MP_TERM_NAME,
                GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME,
                StatisticalResultDTO.FEMALE_TOP_LEVEL_MP_TERM_ID, StatisticalResultDTO.FEMALE_TOP_LEVEL_MP_TERM_NAME,
                StatisticalResultDTO.MALE_TOP_LEVEL_MP_TERM_ID, StatisticalResultDTO.MALE_TOP_LEVEL_MP_TERM_NAME);

        List<StatisticalResultDTO> dtos = solr.query(query).getBeans(StatisticalResultDTO.class);

        for (StatisticalResultDTO dto : dtos) {
            if (dto.getTopLevelMpTermId()!=null || dto.getFemaleTopLevelMpTermId()!=null || dto.getMaleTopLevelMpTermId()!=null) {

                if (dto.getTopLevelMpTermId() != null) {
                    for (int i = 0; i < dto.getTopLevelMpTermId().size(); i++) {
                        tl.put(dto.getTopLevelMpTermId().get(i), dto.getTopLevelMpTermName().get(i));
                    }
                }

                if (dto.getFemaleTopLevelMpTermId() != null) {
                    for (int i = 0; i < dto.getFemaleTopLevelMpTermId().size(); i++) {
                        tl.put(dto.getFemaleTopLevelMpTermId().get(i), dto.getFemaleTopLevelMpTermName().get(i));
                    }
                }
                if (dto.getMaleTopLevelMpTermId() != null) {
                    for (int i = 0; i < dto.getMaleTopLevelMpTermId().size(); i++) {
                        tl.put(dto.getMaleTopLevelMpTermId().get(i), dto.getMaleTopLevelMpTermName().get(i));
                    }
                }
            } else {
                tl.put(dto.getMpTermId(), dto.getMpTermName());
            }
        }

        return tl;
    }


    /*
     * End of Methods for PhenotypeSummaryDAO
     */

    /*
     * Methods for PipelineSolrImpl
     */
    public Parameter getParameterByStableId(String paramStableId, String queryString)
        throws IOException, URISyntaxException {

        String solrUrl = SolrUtils.getBaseURL(solr) + "/select/?q=" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + paramStableId + "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        if (queryString.startsWith("&")) {
            solrUrl += queryString;
        } else {// add an ampersand parameter splitter if not one as we need
            // one to add to the already present solr query string
            solrUrl += "&" + queryString;
        }
        return createParameter(solrUrl);
    }

    private Parameter createParameter(String url)
        throws IOException, URISyntaxException {

        Parameter parameter = new Parameter();
        JSONObject results = null;
        results = JSONRestUtil.getResults(url);

        JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
        for (Object doc : docs) {
            JSONObject paramDoc = (JSONObject) doc;
            String isDerivedInt = paramDoc.getString("parameter_derived");
            boolean derived = false;
            if (isDerivedInt.equals("true")) {
                derived = true;
            }
            parameter.setDerivedFlag(derived);
            parameter.setName(paramDoc.getString(GenotypePhenotypeDTO.PARAMETER_NAME));
            // we need to set is derived in the solr core!
            // pipeline core parameter_derived field
            parameter.setStableId(paramDoc.getString("" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ""));
            if (paramDoc.containsKey(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)) {
                parameter.setStableKey(Integer.parseInt(paramDoc.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)));
            }

        }
        return parameter;
    }


    /*
     * End of method for PipelineSolrImpl
     */

    /*
     * Methods used by PhenotypeCallSummarySolrImpl
     */
    public List<? extends StatisticalResult> getStatsResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession)
        throws IOException, URISyntaxException {

        String solrUrl = SolrUtils.getBaseURL(solr);// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accession + "\"" + "&fq=" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":" + parameterStableId + "&fq=" + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + strainAccession + "\"" + "&fq=" + GenotypePhenotypeDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        //		System.out.println("solr url for stats results=" + solrUrl);
        List<? extends StatisticalResult> statisticalResult = this.createStatsResultFromSolr(solrUrl, observationType);
        return statisticalResult;
    }

    /**
     * Returns a PhenotypeFacetResult object given a list of genes
     *
     * @param genomicFeatures list of marker accession
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException, IOException
     */
    public PhenotypeFacetResult getPhenotypeFacetResultByGenomicFeatures(Set<String> genomicFeatures)
        throws IOException, URISyntaxException, SolrServerException {

        String solrUrl = SolrUtils.getBaseURL(solr);
        // build OR query from a list of genes (assuming they have MGI ids
        StringBuilder geneClause = new StringBuilder(genomicFeatures.size() * 15);
        boolean start = true;
        for (String genomicFeatureAcc : genomicFeatures) {
            geneClause.append((start) ? genomicFeatureAcc : "\" OR \"" + genomicFeatureAcc);
            start = false;
        }

        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":(\"" + geneClause.toString() + "\")" + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        //		System.out.println("\n\n\n SOLR URL = " + solrUrl);
        return this.createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);
    }

    /**
     * Returns a PhenotypeFacetResult object given a phenotyping center and a
     * pipeline stable id
     *
     * @param phenotypingCenter a short name for a phenotyping center
     * @param pipelineStableId a stable pipeline id
     * @return a PhenotypeFacetResult instance containing a list of
     * PhenotypeCallSummary objects.
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException, IOException
     */
    public PhenotypeFacetResult getPhenotypeFacetResultByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId)
        throws IOException, URISyntaxException, SolrServerException {

        String solrUrl = SolrUtils.getBaseURL(solr);// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
        //		System.out.println("SOLR URL = " + solrUrl);

        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"" + "&fq=" + GenotypePhenotypeDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        //		System.out.println("SOLR URL = " + solrUrl);
        return this.createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);
    }

    public PhenotypeFacetResult getMPByGeneAccessionAndFilter(String accId, List<String> topLevelMpTermName, List<String> resourceFullname)
    throws IOException, URISyntaxException, SolrServerException {

        String solrUrl = SolrUtils.getBaseURL(solr) + "/select?";
        SolrQuery q = new SolrQuery();
        
        q.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accId + "\"");
        q.setRows(10000000);
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.setFacetLimit(-1);
        q.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
        //q.addFacetField(GenotypePhenotypeDTO.RESOURCE_FULLNAME);
        q.set("wt", "json");
        q.setSort(GenotypePhenotypeDTO.P_VALUE, ORDER.asc);
                 
        if (topLevelMpTermName != null){
           	q.addFilterQuery(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + ":(\"" + StringUtils.join(topLevelMpTermName, "\" OR \"") + "\")");
        }
//        if (resourceFullname != null){
//          	q.addFilterQuery(GenotypePhenotypeDTO.RESOURCE_FULLNAME + ":(\"" + StringUtils.join(resourceFullname, "\" OR \"") + "\")");
//        } 
        
        solrUrl += q;
        
        return createPhenotypeResultFromSolrResponse(solrUrl, isPreQc);
    }

    public PhenotypeFacetResult getMPCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName, List<String> markerSymbol, List<String> mpTermName)
    throws IOException, URISyntaxException, SolrServerException {

        String url = SolrUtils.getBaseURL(solr) + "/select/?";
        SolrQuery q = new SolrQuery();
        
        q.setQuery("*:*");
        q.addFilterQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + phenotype_id + 
        		"\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\")");
        q.setRows(10000000);
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.setFacetLimit(-1);
        q.addFacetField(GenotypePhenotypeDTO.PROCEDURE_NAME);
        q.addFacetField(GenotypePhenotypeDTO.MARKER_SYMBOL);
        q.addFacetField(GenotypePhenotypeDTO.MP_TERM_NAME );
        q.set("wt", "json");
        q.setSort(GenotypePhenotypeDTO.P_VALUE, ORDER.asc);
               
        q.setFields(GenotypePhenotypeDTO.MP_TERM_NAME, GenotypePhenotypeDTO.MP_TERM_ID, GenotypePhenotypeDTO.MPATH_TERM_NAME, GenotypePhenotypeDTO.MPATH_TERM_ID, GenotypePhenotypeDTO.EXTERNAL_ID,
        		GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME, GenotypePhenotypeDTO.ALLELE_SYMBOL, 
        		GenotypePhenotypeDTO.PHENOTYPING_CENTER, GenotypePhenotypeDTO.ALLELE_ACCESSION_ID, GenotypePhenotypeDTO.MARKER_SYMBOL,
        		GenotypePhenotypeDTO.MARKER_ACCESSION_ID, GenotypePhenotypeDTO.PHENOTYPING_CENTER, GenotypePhenotypeDTO.ZYGOSITY, 
        		GenotypePhenotypeDTO.SEX, GenotypePhenotypeDTO.LIFE_STAGE_NAME, GenotypePhenotypeDTO.RESOURCE_NAME, GenotypePhenotypeDTO.PARAMETER_STABLE_ID, GenotypePhenotypeDTO.PARAMETER_NAME,
        		GenotypePhenotypeDTO.PIPELINE_STABLE_ID, GenotypePhenotypeDTO.PROJECT_NAME, GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID,
        		GenotypePhenotypeDTO.P_VALUE, GenotypePhenotypeDTO.EFFECT_SIZE, GenotypePhenotypeDTO.PROCEDURE_STABLE_ID,
        		GenotypePhenotypeDTO.PROCEDURE_NAME, GenotypePhenotypeDTO.PIPELINE_NAME);
       
        if (procedureName != null){
           	q.addFilterQuery(GenotypePhenotypeDTO.PROCEDURE_NAME + ":(\"" + StringUtils.join(procedureName, "\" OR \"") + "\")");
        }
        if (markerSymbol != null){
          	q.addFilterQuery(GenotypePhenotypeDTO.MARKER_SYMBOL + ":(\"" + StringUtils.join(markerSymbol, "\" OR \"") + "\")");
        }            
        if (mpTermName != null){
          	q.addFilterQuery(GenotypePhenotypeDTO.MP_TERM_NAME + ":(\"" + StringUtils.join(mpTermName, "\" OR \"") + "\")");
        }
        
        url += q;
        
        return createPhenotypeResultFromSolrResponse(url, isPreQc);

    }

    private List<? extends StatisticalResult> createStatsResultFromSolr(String url, ObservationType observationType)
        throws IOException, URISyntaxException {

        // need some way of determining what type of data and therefor what type
        // of stats result object to create default to unidimensional for now
        List<StatisticalResult> results = new ArrayList<>();

        JSONObject resultsj = null;
        resultsj = JSONRestUtil.getResults(url);
        JSONArray docs = resultsj.getJSONObject("response").getJSONArray("docs");

        if (observationType == ObservationType.unidimensional) {
            for (Object doc : docs) {
                UnidimensionalResult unidimensionalResult = new UnidimensionalResult();
                JSONObject phen = (JSONObject) doc;
                String pValue = phen.getString(GenotypePhenotypeDTO.P_VALUE);
                String sex = phen.getString(GenotypePhenotypeDTO.SEX);
                String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
                String effectSize = phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE);
                String phenoCallSummaryId = phen.getString(GenotypePhenotypeDTO.ID);

                if (pValue != null) {
                    unidimensionalResult.setId(Integer.parseInt(phenoCallSummaryId));
                    // one id for each document and for each sex
                    unidimensionalResult.setpValue(Double.valueOf(pValue));
                    unidimensionalResult.setZygosityType(ZygosityType.valueOf(zygosity));
                    unidimensionalResult.setEffectSize(new Double(effectSize));
                    unidimensionalResult.setSexType(SexType.valueOf(sex));
                }
                results.add(unidimensionalResult);
            }
            return results;
        }

        if (observationType == ObservationType.categorical) {

            for (Object doc : docs) {
                CategoricalResult catResult = new CategoricalResult();
                JSONObject phen = (JSONObject) doc;
                String pValue = phen.getString(GenotypePhenotypeDTO.P_VALUE);
                String sex = phen.getString(GenotypePhenotypeDTO.SEX);
                String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
                String effectSize = phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE);
                String phenoCallSummaryId = phen.getString(GenotypePhenotypeDTO.ID);

                catResult.setId(Integer.parseInt(phenoCallSummaryId));
                // one id for each document and for each sex
                catResult.setpValue(Double.valueOf(pValue));
                catResult.setZygosityType(ZygosityType.valueOf(zygosity));
                catResult.setEffectSize(new Double(Double.valueOf(effectSize)));
                catResult.setSexType(SexType.valueOf(sex));
                results.add(catResult);
            }
            return results;
        }
        return results;
    }

    
    public PhenotypeFacetResult createPhenotypeResultFromSolrResponse(String url, Boolean isPreQc)
    throws IOException, URISyntaxException, SolrServerException {

        PhenotypeFacetResult facetResult = new PhenotypeFacetResult();
        List<PhenotypeCallSummaryDTO> list = new ArrayList<PhenotypeCallSummaryDTO>();
        JSONObject results = new JSONObject();
        results = JSONRestUtil.getResults(url);
        JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
                
        for (Object doc : docs) {
            try{
        		PhenotypeCallSummaryDTO call = createSummaryCall(doc, isPreQc);
        		if (call != null){
        			list.add(call);
        		}
        	}catch(Exception e){
           		// Catch errors so that at least the data without issues displays
           		facetResult.addErrorCode(e.getMessage());
           	}
        }

        // get the facet information that we can use to create the buttons, dropdowns, checkboxes
        JSONObject facets = results.getJSONObject("facet_counts").getJSONObject("facet_fields");

        Iterator<String> iterator = facets.keys();
        Map<String, Map<String, Integer>> dropdowns = new HashMap<String, Map<String, Integer>>();
        while (iterator.hasNext()) {
            Map<String, Integer> map = new HashMap<String, Integer>();
            String key = (String) iterator.next();
            JSONArray facetArray = (JSONArray) facets.get(key);
            int i = 0;
            while (i + 1 < facetArray.size()) {
                String facetString = facetArray.get(i).toString();
                int number = facetArray.getInt(i + 1);
                if (number != 0) {
                	// only add if some counts to filter on!
                    map.put(facetString, number);
                }
                i += 2;
            }
            dropdowns.put(key, map);
        }
        
        facetResult.setFacetResults(dropdowns);
        facetResult.setPhenotypeCallSummaries(list);
        
        return facetResult;
    }

    
    public PhenotypeCallSummaryDTO createSummaryCall(Object doc, Boolean preQc)
    throws Exception{
        
    	JSONObject phen = (JSONObject) doc;
        JSONArray topLevelMpTermNames;
        JSONArray topLevelMpTermIDs;
        PhenotypeCallSummaryDTO sum = null;
        try{

            sum = new PhenotypeCallSummaryDTO();


            BasicBean phenotypeTerm = new BasicBean();

            // distinguishes between MP and MPATH

            if( phen.containsKey(GenotypePhenotypeDTO.MP_TERM_ID) ){

                String mpId = phen.getString(GenotypePhenotypeDTO.MP_TERM_ID);
                phenotypeTerm.setId(mpId);

                if ( phen.containsKey(GenotypePhenotypeDTO.MP_TERM_NAME)){
                    String mpTerm = phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME);
                    phenotypeTerm.setName(mpTerm);
                }
                else {
                    logger.warn(mpId + " has no term name");
                }

                // check the top level categories
                if (phen.containsKey(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID)) {
                    topLevelMpTermNames = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
                    topLevelMpTermIDs = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
                } else {
                    // a top level term is directly associated
                    topLevelMpTermNames = new JSONArray();
                    topLevelMpTermNames.add(phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME));
                    topLevelMpTermIDs = new JSONArray();
                    topLevelMpTermIDs.add(phen.getString(GenotypePhenotypeDTO.MP_TERM_ID));
                }

                List<BasicBean> topLevelPhenotypeTerms = new ArrayList<BasicBean>();
                for (int i = 0; i < topLevelMpTermNames.size(); i ++) {
                    BasicBean toplevelTerm = new BasicBean();
                    toplevelTerm.setName(topLevelMpTermNames.getString(i));
                    toplevelTerm.setDescription(topLevelMpTermNames.getString(i));
                    toplevelTerm.setId(topLevelMpTermIDs.getString(i));
                    topLevelPhenotypeTerms.add(toplevelTerm);
                }

                sum.setTopLevelPhenotypeTerms(topLevelPhenotypeTerms);
            }
            else if ( phen.containsKey(GenotypePhenotypeDTO.MPATH_TERM_ID ) ){

                String mpathTerm = phen.getString(GenotypePhenotypeDTO.MPATH_TERM_NAME);
                String mpathId = phen.getString(GenotypePhenotypeDTO.MPATH_TERM_ID);
                phenotypeTerm.setId(mpathId);
                phenotypeTerm.setName(mpathTerm);

            }
            sum.setPhenotypeTerm(phenotypeTerm);


	        // Gid is required for linking to phenoview
	        if (phen.containsKey(GenotypePhenotypeDTO.EXTERNAL_ID)) {
	            sum.setgId(phen.getString(GenotypePhenotypeDTO.EXTERNAL_ID));
	        }
	
	        sum.setPreQC(preQc);

	        if (phen.containsKey(GenotypePhenotypeDTO.PHENOTYPING_CENTER)) {
                sum.setPhenotypingCenter(phen.getString(GenotypePhenotypeDTO.PHENOTYPING_CENTER));
            }
            else {
	            logger.warn(sum.getgId() + " has no phenotyping center");
            }
	
	        if (phen.containsKey(GenotypePhenotypeDTO.ALLELE_SYMBOL)) {
	        	String alleleSymbol=phen.getString(GenotypePhenotypeDTO.ALLELE_SYMBOL);
	        	MarkerBean allele = new MarkerBean();
	            allele.setSymbol(alleleSymbol);
	            allele.setAccessionId(phen.getString(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID));
	            if(alleleSymbol.contains("<")){
		            String superscript=alleleSymbol.substring(alleleSymbol.indexOf("<")+1, alleleSymbol.indexOf(">"));
		            allele.setSuperScript(superscript);
	            }
	        
	            sum.setAllele(allele);            
	        }
	        
	        if (phen.containsKey(GenotypePhenotypeDTO.MARKER_SYMBOL)) {
                MarkerBean gene = new MarkerBean();
                gene.setSymbol(phen.getString(GenotypePhenotypeDTO.MARKER_SYMBOL));

	            if ( phen.containsKey(GenotypePhenotypeDTO.MARKER_ACCESSION_ID)){
                    gene.setAccessionId(phen.getString(GenotypePhenotypeDTO.MARKER_ACCESSION_ID));
                }
                else {
	                logger.warn(gene.getSymbol() + " has no accession id");
                }

	            sum.setGene(gene);
	        }
	        
	        if (phen.containsKey(GenotypePhenotypeDTO.PHENOTYPING_CENTER)) {
	            sum.setPhenotypingCenter(phen.getString(GenotypePhenotypeDTO.PHENOTYPING_CENTER));
	        }

	        if (phen.containsKey(GenotypePhenotypeDTO.ZYGOSITY)) {
                String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
                ZygosityType zyg = ZygosityType.valueOf(zygosity);
                sum.setZygosity(zyg);
            }
            else {
	            logger.warn(sum.getgId() + " has no zygosity");
            }


	        String sex = phen.getString(GenotypePhenotypeDTO.SEX);
	
	        SexType sexType = SexType.valueOf(sex);
	        sum.setSex(sexType);


            if( ! preQc &&  phen.containsKey(GenotypePhenotypeDTO.LIFE_STAGE_NAME)) {
                String lifeStageName = phen.getString(GenotypePhenotypeDTO.LIFE_STAGE_NAME);
                sum.setLifeStageName(lifeStageName);
            }

            BasicBean datasource = new BasicBean();
            if (phen.containsKey(GenotypePhenotypeDTO.RESOURCE_NAME)) {
                String provider = phen.getString(GenotypePhenotypeDTO.RESOURCE_NAME);
                datasource.setName(provider);
                sum.setDatasource(datasource);
            }
            else {
                logger.warn(sum.getgId() + " has no resource name");
                datasource.setName("");
                sum.setDatasource(datasource);
            }
	
	        ImpressBaseDTO parameter = new ParameterDTO();
	        if (phen.containsKey(GenotypePhenotypeDTO.PARAMETER_STABLE_ID)) {
	            parameter.setStableId(phen.getString(GenotypePhenotypeDTO.PARAMETER_STABLE_ID));
	            parameter.setName(phen.getString((GenotypePhenotypeDTO.PARAMETER_NAME)));
	        } else {
	            System.err.println("parameter_stable_id missing");
	        }
	        sum.setParameter(parameter);
	
	        ImpressBaseDTO pipeline = new ImpressBaseDTO();
	        if (phen.containsKey(GenotypePhenotypeDTO.PIPELINE_STABLE_ID)) {
	            pipeline.setStableId(phen.getString(GenotypePhenotypeDTO.PIPELINE_STABLE_ID));
	            pipeline.setName(phen.getString((GenotypePhenotypeDTO.PIPELINE_NAME)));
	        } else {
	            System.err.println("pipeline stable_id missing");
	        }
	        sum.setPipeline(pipeline);
	
	        BasicBean project = new BasicBean();
	        if (phen.containsKey(GenotypePhenotypeDTO.PROJECT_NAME)) {
                project.setName(phen.getString(GenotypePhenotypeDTO.PROJECT_NAME));
                if (phen.containsKey(GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID)) {
                    project.setId(phen.getString(GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID));
                }
                sum.setProject(project);
            }
            else {
	            logger.warn(sum.getgId() + " has no project name");
            }
	
	        if (phen.containsKey(GenotypePhenotypeDTO.P_VALUE)) {
	            sum.setpValue(new Float(phen.getString(GenotypePhenotypeDTO.P_VALUE)));
	            sum.setEffectSize(new Float(phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE)));
	        }
	        
	        ImpressBaseDTO procedure = new ImpressBaseDTO();
	        if (phen.containsKey(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID)) {
	            procedure.setStableId(phen.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID));
	            procedure.setName(phen.getString(GenotypePhenotypeDTO.PROCEDURE_NAME));
	            sum.setProcedure(procedure);
	        } else {
	            System.err.println("procedure_stable_id");
	        }

            if ( phen.containsKey(GenotypePhenotypeDTO.COLONY_ID) ){
                sum.setColonyId(phen.getString(GenotypePhenotypeDTO.COLONY_ID));
            }
        } catch (Exception e){
        	String errorCode = "";
        	if (preQc){
        		errorCode = "#17";
        	} else {
        		errorCode = "#18";
        	}
        	Exception exception = new Exception(errorCode);
    		System.out.println(errorCode);
    		e.printStackTrace();
    		throw exception;
        }
    
        return sum;
    }

    
    /**
     * @param mpTermName
     * @param resource
     * @return map <colony_id, occurences>
     */
    public HashMap<String, Long> getAssociationsDistribution(String mpTermName, String resource) {

        String query = GenotypePhenotypeDTO.MP_TERM_NAME + ":\"" + mpTermName + "\"";
        if (resource != null) {
            query += " AND " + GenotypePhenotypeDTO.RESOURCE_NAME + ":" + resource;
        }

        SolrQuery q = new SolrQuery();
        q.setQuery(query);
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.addFacetField(GenotypePhenotypeDTO.COLONY_ID);

        try {
            return (getFacets(solr.query(q))).get(GenotypePhenotypeDTO.COLONY_ID);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    
    public Set<String> getFertilityAssociatedMps() {

        SolrQuery q = new SolrQuery();
        q.setQuery(GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":*_FER_*");
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.addFacetField(GenotypePhenotypeDTO.MP_TERM_NAME);

        try {
            return (getFacets(solr.query(q))).get(GenotypePhenotypeDTO.MP_TERM_NAME).keySet();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public SolrQuery buildQuery(String geneAccession, List<String> procedureName, List<String> alleleSymbol, List<String> phenotypingCenter,
                                List<String> pipelineName, List<String> procedureStableIds, List<String> resource, List<String> mpTermId, Integer rows, List<String> sex, List<String> zygosities,
                                String strain, String parameterStableId, String pipelineStableId, String metadataGroup, String alleleAccessionId){

        SolrQuery query = new SolrQuery();

        query.setQuery("*:*");
        query.setRows(rows != null? rows : Integer.MAX_VALUE);
        query.set("sort", StatisticalResultDTO.P_VALUE + " asc");

        if (geneAccession != null) {
            query.addFilterQuery(StatisticalResultDTO.MARKER_ACCESSION_ID + ":\"" + geneAccession + "\"");
        }
        if (phenotypingCenter != null) {
            query.addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":(\""
                    + StringUtils.join(phenotypingCenter, "\" OR \"") + "\")");
        }
        if (mpTermId != null) {
            query.addFilterQuery(StatisticalResultDTO.MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + StatisticalResultDTO.MP_TERM_ID_OPTIONS + ":(\"" + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + StatisticalResultDTO.INTERMEDIATE_MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + StatisticalResultDTO.FEMALE_TOP_LEVEL_MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"")
                    + "\") OR " + StatisticalResultDTO.FEMALE_MP_TERM_ID + ":(\""
                    + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + StatisticalResultDTO.FEMALE_INTERMEDIATE_MP_TERM_ID + ":(\""
                    + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + StatisticalResultDTO.MALE_TOP_LEVEL_MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"")
                    + "\") OR " + StatisticalResultDTO.MALE_INTERMEDIATE_MP_TERM_ID + ":(\""
                    + StringUtils.join(mpTermId, "\" OR \"") + "\") OR " + StatisticalResultDTO.MALE_MP_TERM_ID + ":(\""
                    + StringUtils.join(mpTermId, "\" OR \"") + "\")");
        }
        if (pipelineName != null) {
            query.addFilterQuery(
                    StatisticalResultDTO.PIPELINE_NAME + ":(\"" + StringUtils.join(pipelineName, "\" OR \"") + "\")");
        }
        if (metadataGroup != null) {
            query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":" + metadataGroup );
        }
        if (alleleAccessionId != null) {
            query.addFilterQuery(StatisticalResultDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccessionId + "\"" );
        }
        if (alleleSymbol != null) {
            query.addFilterQuery(
                    StatisticalResultDTO.ALLELE_SYMBOL + ":(\"" + StringUtils.join(alleleSymbol, "\" OR \"") + "\")");
        }
        if (procedureStableIds != null) {
            query.addFilterQuery(StatisticalResultDTO.PROCEDURE_STABLE_ID + ":("
                    + StringUtils.join(procedureStableIds, " OR ") + ")");
        }
        if (procedureName != null) {
            query.addFilterQuery( StatisticalResultDTO.PROCEDURE_NAME + ":(\"" + StringUtils.join(procedureName, "\" OR \"") + "\")");
        }
        if (resource != null) {
            query.addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":(" + StringUtils.join(resource, " OR ") + ")");
        }
        if (pipelineStableId != null) {
            query.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId);
        }
        if (parameterStableId != null) {
            query.addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
        }
        if (zygosities != null && zygosities.size() > 0 && zygosities.size() != 3) {
            if (zygosities.size() == 2) {
                query.addFilterQuery(StatisticalResultDTO.ZYGOSITY + ":(" + zygosities.get(0) + " OR " + zygosities.get(1) + ")");
            } else {
                if ( ! zygosities.get(0).equalsIgnoreCase("null")) {
                    query.addFilterQuery(StatisticalResultDTO.ZYGOSITY + ":" + zygosities.get(0));
                }
            }
        }

        if (sex != null && sex.size() > 0 && sex.size() != 3) {
            if (sex.size() == 2) {
                query.addFilterQuery(StatisticalResultDTO.SEX + ":(" + sex.get(0) + " OR " + sex.get(1) + ")");
            } else {
                if ( ! sex.get(0).equalsIgnoreCase("null")) {
                    query.addFilterQuery(StatisticalResultDTO.SEX + ":" + sex.get(0));
                }
            }
        }

        if (strain != null) {
            query.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:"));
        }

        return query;

    }

    /*
     * End of method for PhenotypeCallSummarySolrImpl
     */
    public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<BasicBean> xAxisBeans, Map<String, List<String>> geneToTopLevelMpMap) {

        GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
        if (gene != null) {
            row.setSymbol(gene.getSymbol());
        } else {
            System.err.println("error no symbol for gene " + accession);
        }

        Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();
        for (BasicBean xAxisBean : xAxisBeans) {
            HeatMapCell cell = new HeatMapCell();
            if (geneToTopLevelMpMap.containsKey(accession)) {

                List<String> mps = geneToTopLevelMpMap.get(accession);
                // cell.setLabel("No Phenotype Detected");
                if (mps != null &&  ! mps.isEmpty()) {
                    if (mps.contains(xAxisBean.getId())) {
                        cell.setxAxisKey(xAxisBean.getId());
                        cell.setLabel("Data Available");
                        cell.setStatus("Data Available");
                    } else {
                        cell.setStatus("No MP");
                    }
                } else {
                    // System.err.println("mps are null or empty");
                    cell.setStatus("No MP");
                }
            } else {
                // if no doc found for the gene then no data available
                cell.setStatus("No Data Available");
            }
            xAxisToCellMap.put(xAxisBean.getId(), cell);
        }
        row.setXAxisToCellMap(xAxisToCellMap);

        return row;
    }

    public SolrClient getSolrServer() {
        return solr;
    }

    public HttpSolrClient getHttpSolrClient() {
        return SolrUtils.getHttpSolrServer(solr);
    }

    
    /**
     * @author ilinca
     * @since 2016/07/07
     * @return CSV string of facet values (mp,gene,colony,phenCenter,sex,zyg,param,pVal). At the moment of writing used to compare calls from EBI to DCC
     * @throws SolrServerException, IOException
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws MalformedURLException 
     */
    public String getTabbedCallSummary() 
    throws MalformedURLException, IOException, URISyntaxException{
    //	http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/genotype-phenotype/select?q=resource_name:IMPC&facet=true&facet.mincount=1&facet.pivot=mp_term_name,marker_symbol,colony_id,phenotyping_center,sex,zygosity,parameter_stable_id,p_value&facet.limit=-1&facet.mincount=1&wt=xslt&tr=pivot.xsl
    		
    	SolrQuery q = new SolrQuery();
    	q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":IMPC");
    	q.addFilterQuery("-" + GenotypePhenotypeDTO.ASSERTION_TYPE + ":manual");
    	q.setFacet(true);
    	q.setFacetLimit(-1)
    		.setFacetMinCount(1)
    		.addFacetPivotField(GenotypePhenotypeDTO.MP_TERM_ID + "," + GenotypePhenotypeDTO.MARKER_SYMBOL + "," + 
    			GenotypePhenotypeDTO.COLONY_ID + "," + GenotypePhenotypeDTO.PHENOTYPING_CENTER + "," + GenotypePhenotypeDTO.SEX + "," + 
    			GenotypePhenotypeDTO.ZYGOSITY + "," + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + "," + GenotypePhenotypeDTO.P_VALUE);
    	q.set("wt", "xslt").set("tr", "pivot.xsl");
    	
    	HttpProxy proxy = new HttpProxy();
		System.out.println("Solr ULR for getTabbedCallSummary " + SolrUtils.getBaseURL(solr) + "/select?" + q);

		String content = proxy.getContent(new URL(SolrUtils.getBaseURL(solr) + "/select?" + q));

        return content;
    }
    
}
