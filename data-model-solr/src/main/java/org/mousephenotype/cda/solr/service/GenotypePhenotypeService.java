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

import org.apache.commons.lang3.StringUtils;
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
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.repositories.GenesSecondaryProjectRepository;
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
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;

public class GenotypePhenotypeService extends BasicService implements WebStatus {

    private final       Logger logger            = LoggerFactory.getLogger(this.getClass());
    public static final double P_VALUE_THRESHOLD = 0.0001;

    protected ImpressService                  impressService;
    protected SolrClient                      genotypePhenotypeCore;
    protected GenesSecondaryProjectRepository genesSecondaryProjectRepository;


    @Inject
    public GenotypePhenotypeService(
            @NotNull ImpressService impressService,
            @NotNull @Qualifier("genotypePhenotypeCore") SolrClient genotypePhenotypeCore,
            @NotNull GenesSecondaryProjectRepository genesSecondaryProjectRepository)
    {
        super();
        this.impressService = impressService;
        this.genotypePhenotypeCore = genotypePhenotypeCore;
        this.genesSecondaryProjectRepository = genesSecondaryProjectRepository;
    }


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
            QueryResponse response = genotypePhenotypeCore.query(query);
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

        return genotypePhenotypeCore.query(query).getResults().getNumFound();
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
    	
    	return genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);
    	    	
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

        logger.info("Solr url for getHitsDistributionByParameter " + SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + q);
        QueryResponse response = genotypePhenotypeCore.query(q);

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
        SolrQuery q = new SolrQuery().setFacet(true).setRows(0);
        q.set("facet.limit", -1);

        if (resourceName != null){
            q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }

        if (mpId != null){
            q.addFilterQuery(GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\"" );
        }

        String pivot = GenotypePhenotypeDTO.MP_TERM_ID + "," + GenotypePhenotypeDTO.MP_TERM_NAME+","+GenotypePhenotypeDTO.MARKER_ACCESSION_ID;
        q.add("facet.pivot", pivot);

        logger.info("Solr url for getAssociationsCount " + SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + q);

        QueryResponse response = genotypePhenotypeCore.query(q);
        for (PivotField p : response.getFacetPivot().get(pivot)){
            if (p.getPivot() != null){
                String mpTermId = p.getValue().toString();
                String mpName = p.getPivot().get(0).getValue().toString();
                List<PivotField> pivotFields = p.getPivot().get(0).getPivot();
                Set<String> uniqueAccessions=new HashSet<>();
                if (pivotFields != null){
                    for(PivotField accessionField: pivotFields){
                        String accession=accessionField.getValue().toString();
                        uniqueAccessions.add(accession);
                    }
                }
                int count=uniqueAccessions.size();//we are setting this to the size of the unique set of gene accessions for this MP term and not the count as the count has male and female results and doesn't relate to the number of unique genes which is what we are currently displaying on the interface
                list.add(new CountTableRow(mpName, mpTermId, count));
            }
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

        logger.info("Solr url for getOverviewGenesWithMoreProceduresThan " + SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + q);

        QueryResponse response = genotypePhenotypeCore.query(q);
        for (PivotField pivot : response.getFacetPivot().get(facetPivot)){

            if (pivot.getPivot() != null){
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

        logger.info("Solr url for getHitsDistributionByParameter " + SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + q);
        QueryResponse response = genotypePhenotypeCore.query(q);

        for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
            if (pivot.getPivot() != null){
                String id = pivot.getValue().toString();
                String name = pivot.getPivot().get(0).getValue().toString();
                int count = pivot.getPivot().get(0).getCount();
                String[] row = {id, name, Integer.toString(count)};
                res.add(row);
            }
        }

        logger.info("Done in " + (System.currentTimeMillis() - time));
        return res;

    }

    public List<AggregateCountXY> getAggregateCountXYBean(TreeMap<String, TreeMap<String, Long>> map) {
        List<AggregateCountXY> res = new ArrayList<>();

        for (String category : map.navigableKeySet()) {
            for (String bin : map.get(category).navigableKeySet()) {
                AggregateCountXY bean = new AggregateCountXY(map.get(category).get(bin).intValue(), bin, bin, "xAttribute", category, category, "yAttribute");
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

        QueryResponse response = genotypePhenotypeCore.query(query);
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
        QueryResponse results = genotypePhenotypeCore.query(q);
        
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
        List<Group> groups = genotypePhenotypeCore.query(query).getGroupResponse().getValues().get(0).getValues();
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
        rsp = genotypePhenotypeCore.query(solrQuery);
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
        QueryResponse rsp = genotypePhenotypeCore.query(solrQuery);
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
        QueryResponse rsp = genotypePhenotypeCore.query(solrQuery);
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
        QueryResponse rsp = genotypePhenotypeCore.query(solrQuery);
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
        
        SolrDocumentList result = genotypePhenotypeCore.query(solrQuery).getResults();
               
        return result;
    }

    public SolrDocumentList getPhenotypes(String gene)
    throws SolrServerException, IOException  {

        SolrDocumentList result = runQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
        return result;
    }

    
    public List<GenotypePhenotypeDTO> getAllGenotypePhenotypes(List<String> resourceName) 
    throws SolrServerException, IOException  {

        SolrQuery query = new SolrQuery().setRows(Integer.MAX_VALUE);
       
        if (resourceName != null){
            query.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":(" + StringUtils.join(resourceName, " OR ") + ")");
        }else {
            query.setQuery("*:*");
        }

        return genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);

    }
    
    
    public Set<String> getAllGenotypePhenotypesForGene(String markerAccession) 
    throws SolrServerException, IOException  {

        Set <String> alleles = new HashSet<>();
        SolrQuery query = new SolrQuery().setRows(Integer.MAX_VALUE);
        query.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + markerAccession + "\"");
        query.setFields(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID);
        List<GenotypePhenotypeDTO> results = genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);
        
        for ( GenotypePhenotypeDTO doc : results){
        	alleles.add(doc.getAlleleAccessionId());
        }
        
        return alleles;
    }

    
    public List<GenotypePhenotypeDTO> getPhenotypeDTOs(String gene) 
    throws SolrServerException, IOException  {
        SolrQuery query = new SolrQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"")
            .setRows(Integer.MAX_VALUE);

        return genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);

    }

    private SolrDocumentList runQuery(String q)
        throws SolrServerException, IOException  {

        SolrQuery solrQuery = new SolrQuery().setQuery(q);
        solrQuery.setRows(1000000);
        QueryResponse rsp = null;
        rsp = genotypePhenotypeCore.query(solrQuery);
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
        System.out.println("query for top level mp is "+query);
        List<StatisticalResultDTO> dtos = genotypePhenotypeCore.query(query).getBeans(StatisticalResultDTO.class);

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
        throws IOException, URISyntaxException, JSONException {

        String solrUrl = SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select/?q=" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + paramStableId + "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        if (queryString.startsWith("&")) {
            solrUrl += queryString;
        } else {// add an ampersand parameter splitter if not one as we need
            // one to add to the already present solr query string
            solrUrl += "&" + queryString;
        }
        return createParameter(solrUrl);
    }

    private Parameter createParameter(String url)
        throws IOException, URISyntaxException, JSONException {

        Parameter parameter = new Parameter();
        JSONObject results;
        results = JSONRestUtil.getResults(url);

        JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
        for (int i = 0; i < docs.length(); i++) {
            Object doc = docs.get(i);
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
            if (paramDoc.has(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)) {
                parameter.setStableKey(Integer.parseInt(paramDoc.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_KEY)));
            }

        }
        return parameter;
    }

    /*
     * Methods used by PhenotypeCallSummarySolrImpl
     */
    public List<? extends StatisticalResult> getStatsResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession)
        throws IOException, URISyntaxException, JSONException {

        String solrUrl = SolrUtils.getBaseURL(genotypePhenotypeCore);// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accession + "\"" + "&fq=" + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":" + parameterStableId + "&fq=" + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + strainAccession + "\"" + "&fq=" + GenotypePhenotypeDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        //		System.out.println("solr url for stats results=" + solrUrl);
        List<? extends StatisticalResult> statisticalResult = this.createStatsResultFromSolr(solrUrl, observationType);
        return statisticalResult;
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
        throws IOException, URISyntaxException, SolrServerException, JSONException {

        String solrUrl = SolrUtils.getBaseURL(genotypePhenotypeCore);// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
        //		System.out.println("SOLR URL = " + solrUrl);

        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"" + "&fq=" + GenotypePhenotypeDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        //		System.out.println("SOLR URL = " + solrUrl);
        return this.createPhenotypeResultFromSolrResponse(solrUrl);
    }

    public PhenotypeFacetResult getMPByGeneAccessionAndFilter(String accId, List<String> topLevelMpTermName, List<String> resourceFullname)
    throws IOException, URISyntaxException, JSONException {

        String solrUrl = SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?";
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

        solrUrl += q;
        
        return createPhenotypeResultFromSolrResponse(solrUrl);
    }


    public PhenotypeFacetResult getMPCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName, List<String> markerSymbol, List<String> mpTermName)
            throws IOException, URISyntaxException, JSONException {

        return getMPCallByMPAccessionAndFilter(phenotype_id, procedureName, markerSymbol, mpTermName, null);
    }


    public PhenotypeFacetResult getMPCallByMPAccessionAndFilter(String phenotype_id, List<String> procedureName, List<String> markerSymbol, List<String> mpTermName, Map<String, Synonym> synonyms)
    throws IOException, URISyntaxException, JSONException {

        String url = SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select/?";
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
        
        return createPhenotypeResultFromSolrResponse(url, synonyms);

    }
    /**
     * Get a list of gene symbols for this phenotype
     * @param phenotype_id
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException
     */
    public List<String> getGenesForMpId(String phenotype_id)
    	    throws IOException, URISyntaxException, SolrServerException {
    		List<String>results=new ArrayList<>();
    	        String url = SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select/?";
    	        SolrQuery q = new SolrQuery();
    	        
    	        q.setQuery("*:*");
    	        q.addFilterQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + phenotype_id + 
    	        		"\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\")");
    	        q.setRows(10000000);
    	        q.setFacet(true);
    	        q.setFacetMinCount(1);
    	        q.setFacetLimit(-1);
    	        //q.addFacetField(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
    	        q.addFacetField(GenotypePhenotypeDTO.MARKER_SYMBOL);
    	       // q.addFacetField(GenotypePhenotypeDTO.MP_TERM_NAME );
    	        q.set("wt", "json");
    	        q.setSort(GenotypePhenotypeDTO.P_VALUE, ORDER.asc);
    	        q.setFields(GenotypePhenotypeDTO.MARKER_SYMBOL);
//    	        q.setFields(GenotypePhenotypeDTO.MP_TERM_NAME, GenotypePhenotypeDTO.MP_TERM_ID, GenotypePhenotypeDTO.MPATH_TERM_NAME, GenotypePhenotypeDTO.MPATH_TERM_ID, GenotypePhenotypeDTO.EXTERNAL_ID,
//    	        		GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID, GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME, GenotypePhenotypeDTO.ALLELE_SYMBOL, 
//    	        		GenotypePhenotypeDTO.PHENOTYPING_CENTER, GenotypePhenotypeDTO.ALLELE_ACCESSION_ID, GenotypePhenotypeDTO.MARKER_SYMBOL,
//    	        		GenotypePhenotypeDTO.MARKER_ACCESSION_ID, GenotypePhenotypeDTO.PHENOTYPING_CENTER, GenotypePhenotypeDTO.ZYGOSITY, 
//    	        		GenotypePhenotypeDTO.SEX, GenotypePhenotypeDTO.LIFE_STAGE_NAME, GenotypePhenotypeDTO.RESOURCE_NAME, GenotypePhenotypeDTO.PARAMETER_STABLE_ID, GenotypePhenotypeDTO.PARAMETER_NAME,
//    	        		GenotypePhenotypeDTO.PIPELINE_STABLE_ID, GenotypePhenotypeDTO.PROJECT_NAME, GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID,
//    	        		GenotypePhenotypeDTO.P_VALUE, GenotypePhenotypeDTO.EFFECT_SIZE, GenotypePhenotypeDTO.PROCEDURE_STABLE_ID,
//    	        		GenotypePhenotypeDTO.PROCEDURE_NAME, GenotypePhenotypeDTO.PIPELINE_NAME);
    	       
    	        
    	        
    	        QueryResponse response = genotypePhenotypeCore.query(q);
    	        for( Count facet : response.getFacetField(GenotypePhenotypeDTO.MARKER_SYMBOL).getValues()){
    	        	//System.out.println("facet="+facet.getName());
    	        	results.add(facet.getName());
    	        }
    	        return results;
    	    }

    public List<GenotypePhenotypeDTO> getGenotypePhenotypeFor(String markerAccession, String parameterStableId, String strainAccession, String alleleAccession, Set<ZygosityType> zygosity, String phenotypingCenter, Set<SexType> sex)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .setRows(Integer.MAX_VALUE);

        if (markerAccession != null) {
            query.addFilterQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + markerAccession + "\"");
        }

        if (alleleAccession != null) {
            query.addFilterQuery(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"");
        }

        if (strainAccession != null) {
            query.addFilterQuery(GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + strainAccession + "\"");
        }

        if (parameterStableId != null) {
            query.addFilterQuery(GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + parameterStableId + "\"");
        }

        if (sex != null) {
            String sexes = sex.stream().map(SexType::getName).collect(Collectors.joining(" OR "));
            query.addFilterQuery(GenotypePhenotypeDTO.SEX + ":(" + sexes + ")");
        }

        if (zygosity != null) {
            String zygosities = zygosity.stream().map(ZygosityType::getName).collect(Collectors.joining(" OR "));
            query.addFilterQuery(GenotypePhenotypeDTO.ZYGOSITY + ":(" + zygosities + ")");
        }

        if (phenotypingCenter != null) {
            query.addFilterQuery(GenotypePhenotypeDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
        }

        return genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);
    }


    private List<? extends StatisticalResult> createStatsResultFromSolr(String url, ObservationType observationType)
        throws IOException, URISyntaxException, JSONException {

        // need some way of determining what type of data and therefor what type
        // of stats result object to create default to unidimensional for now
        List<StatisticalResult> results = new ArrayList<>();

        JSONObject resultsj;
        resultsj = JSONRestUtil.getResults(url);
        JSONArray docs = resultsj.getJSONObject("response").getJSONArray("docs");

        if (observationType == ObservationType.unidimensional) {
            for (int i = 0; i < docs.length(); i++) {
                Object doc = docs.get(i);
                UnidimensionalResult unidimensionalResult = new UnidimensionalResult();
                JSONObject phen = (JSONObject) doc;
                String pValue = phen.getString(GenotypePhenotypeDTO.P_VALUE);
                String sex = phen.getString(GenotypePhenotypeDTO.SEX);
                String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
                String effectSize = phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE);
                String phenoCallSummaryId = phen.getString(GenotypePhenotypeDTO.ID);

                if (pValue != null) {
                    unidimensionalResult.setId(Long.parseLong(phenoCallSummaryId));
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

            for (int i = 0; i < docs.length(); i++) {
                Object doc = docs.get(i);
                CategoricalResult catResult = new CategoricalResult();
                JSONObject phen = (JSONObject) doc;
                String pValue = phen.getString(GenotypePhenotypeDTO.P_VALUE);
                String sex = phen.getString(GenotypePhenotypeDTO.SEX);
                String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
                String effectSize = phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE);
                String phenoCallSummaryId = phen.getString(GenotypePhenotypeDTO.ID);

                catResult.setId(Long.parseLong(phenoCallSummaryId));
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


    public PhenotypeFacetResult createPhenotypeResultFromSolrResponse(String url)
            throws IOException, URISyntaxException, JSONException {
        return createPhenotypeResultFromSolrResponse(url, null);
    }


    // Returns status of operation in PhenotypeFacetResult.status. Query it for errors and warnings.
    public PhenotypeFacetResult createPhenotypeResultFromSolrResponse(String url, Map<String, Synonym> synonyms)
    throws IOException, URISyntaxException, JSONException {

        PhenotypeFacetResult facetResult = new PhenotypeFacetResult();
        List<PhenotypeCallSummaryDTO> list = new ArrayList<>();
        JSONObject results;
        results = JSONRestUtil.getResults(url);
        JSONArray docs = results.getJSONObject("response").getJSONArray("docs");

        for (int i = 0; i < docs.length(); i++) {
            Object doc = docs.get(i);
            try {
                PhenotypeCallSummaryDTO call = createSummaryCall(doc, synonyms);
                if ((call.getStatus().hasErrors()) || call.getStatus().hasWarnings()) {
                    facetResult.getStatus().add(call.getStatus());
                }
                if (call != null) {
                    list.add(call);
                }
            } catch (Exception e) {
                // Catch errors so that at least the data without issues displays
                facetResult.addErrorCode(e.getMessage());
            }
        }

        // get the facet information that we can use to create the buttons, dropdowns, checkboxes
        JSONObject facets = results.getJSONObject("facet_counts").getJSONObject("facet_fields");

        Iterator<String> iterator = facets.keys();
        Map<String, Map<String, Integer>> dropdowns = new HashMap<>();
        while (iterator.hasNext()) {
            Map<String, Integer> map = new HashMap<>();
            String key = iterator.next();
            JSONArray facetArray = (JSONArray) facets.get(key);
            int i = 0;
            while (i + 1 < facetArray.length()) {
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

    // synonyms may be null. If it is not, this method will check synonyms if the gene symbol is not found

    /**
     * Returns a {@link PhenotypeCallSummaryDTO}from the given inputs. If no mgiAccessionId is found for the gene marker
     * symbol AND {@code summary} is not null, the synonyms are searched for a mgiAccessionId and, if found, used. If
     * {@code synonyms} is null or the gene marker symbol is not found in the synonyms, a warning is returned and an
     * empty {@link PhenotypeCallSummaryDTO} is returned
     *
     * @param doc
     * @param synonyms if not null, contains a full list of {@link Synonym} instances to search for mgiAccessionId.
     * @return
     * @throws Exception
     */
    public PhenotypeCallSummaryDTO createSummaryCall(Object doc, Map<String, Synonym> synonyms)
    throws Exception{

        JSONObject              phen = (JSONObject) doc;
        JSONArray               topLevelMpTermNames;
        JSONArray               topLevelMpTermIDs;
        PhenotypeCallSummaryDTO sum;
        if (synonyms == null) {
            synonyms = new HashMap<>();             // If synonyms is null, create an empty one to avoid NPE.
        }

        try {
            sum = new PhenotypeCallSummaryDTO();

            BasicBean phenotypeTerm = new BasicBean();

            // distinguishes between MP and MPATH

            if( phen.has(GenotypePhenotypeDTO.MP_TERM_ID) ){

                String mpId = phen.getString(GenotypePhenotypeDTO.MP_TERM_ID);
                phenotypeTerm.setId(mpId);

                if ( phen.has(GenotypePhenotypeDTO.MP_TERM_NAME)){
                    String mpTerm = phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME);
                    phenotypeTerm.setName(mpTerm);
                }
                else {
                    sum.getStatus().addWarning(mpId + " has no term name");
                }

                // check the top level categories
                if (phen.has(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID)) {
                    topLevelMpTermNames = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
                    topLevelMpTermIDs = phen.getJSONArray(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID);
                } else {
                    // a top level term is directly associated
                    topLevelMpTermNames = new JSONArray();
                    topLevelMpTermNames.put(phen.getString(GenotypePhenotypeDTO.MP_TERM_NAME));
                    topLevelMpTermIDs = new JSONArray();
                    topLevelMpTermIDs.put(phen.getString(GenotypePhenotypeDTO.MP_TERM_ID));
                }

                List<BasicBean> topLevelPhenotypeTerms = new ArrayList<BasicBean>();
                for (int i = 0; i < topLevelMpTermNames.length(); i ++) {
                    BasicBean toplevelTerm = new BasicBean();
                    toplevelTerm.setName(topLevelMpTermNames.getString(i));
                    toplevelTerm.setDescription(topLevelMpTermNames.getString(i));
                    toplevelTerm.setId(topLevelMpTermIDs.getString(i));
                    topLevelPhenotypeTerms.add(toplevelTerm);
                }

                sum.setTopLevelPhenotypeTerms(topLevelPhenotypeTerms);
            }
            else if ( phen.has(GenotypePhenotypeDTO.MPATH_TERM_ID ) ){

                String mpathTerm = phen.getString(GenotypePhenotypeDTO.MPATH_TERM_NAME);
                String mpathId = phen.getString(GenotypePhenotypeDTO.MPATH_TERM_ID);
                phenotypeTerm.setId(mpathId);
                phenotypeTerm.setName(mpathTerm);

            }
            sum.setPhenotypeTerm(phenotypeTerm);


	        // Gid is required for linking to phenoview
	        if (phen.has(GenotypePhenotypeDTO.EXTERNAL_ID)) {
	            sum.setgId(phen.getString(GenotypePhenotypeDTO.EXTERNAL_ID));
	        }

	        if (phen.has(GenotypePhenotypeDTO.PHENOTYPING_CENTER)) {
                sum.setPhenotypingCenter(phen.getString(GenotypePhenotypeDTO.PHENOTYPING_CENTER));
            }
            else {
                sum.getStatus().addWarning(sum.getgId() + " has no phenotyping center");
            }
	
	        if (phen.has(GenotypePhenotypeDTO.ALLELE_SYMBOL)) {
	        	String alleleSymbol=phen.getString(GenotypePhenotypeDTO.ALLELE_SYMBOL);
	        	MarkerBean allele = new MarkerBean();
	            allele.setSymbol(alleleSymbol);
	            if (phen.has(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID)) {
                    allele.setAccessionId(phen.getString(GenotypePhenotypeDTO.ALLELE_ACCESSION_ID));
                }
                if(alleleSymbol.contains("<")){
		            String superscript=alleleSymbol.substring(alleleSymbol.indexOf("<")+1, alleleSymbol.indexOf(">"));
		            allele.setSuperScript(superscript);
	            }
	        
	            sum.setAllele(allele);            
	        }
	        
	        if (phen.has(GenotypePhenotypeDTO.MARKER_SYMBOL)) {
                MarkerBean gene = new MarkerBean();
                gene.setSymbol(phen.getString(GenotypePhenotypeDTO.MARKER_SYMBOL));

	            if ( phen.has(GenotypePhenotypeDTO.MARKER_ACCESSION_ID)){
                    gene.setAccessionId(phen.getString(GenotypePhenotypeDTO.MARKER_ACCESSION_ID));
                }
                else {
                    Synonym syn = synonyms.get(gene.getSymbol());
                    if (syn != null) {
                        gene.setAccessionId(syn.getAccessionId());
                    } else {
                        sum.getStatus().addWarning(gene.getSymbol() + " has no accession id");
                    }
                }

	            sum.setGene(gene);
	        }
	        
	        if (phen.has(GenotypePhenotypeDTO.PHENOTYPING_CENTER)) {
	            sum.setPhenotypingCenter(phen.getString(GenotypePhenotypeDTO.PHENOTYPING_CENTER));
	        }

	        if (phen.has(GenotypePhenotypeDTO.ZYGOSITY)) {
                String zygosity = phen.getString(GenotypePhenotypeDTO.ZYGOSITY);
                ZygosityType zyg = ZygosityType.valueOf(zygosity);
                sum.setZygosity(zyg);
            }
            else {
                sum.getStatus().addWarning(sum.getgId() + " has no zygosity");
            }


	        String sex = phen.getString(GenotypePhenotypeDTO.SEX);
	
	        SexType sexType = SexType.valueOf(sex);
	        sum.setSex(sexType);


            if( phen.has(GenotypePhenotypeDTO.LIFE_STAGE_NAME)) {
                String lifeStageName = phen.getString(GenotypePhenotypeDTO.LIFE_STAGE_NAME);
                sum.setLifeStageName(lifeStageName);
            }

            BasicBean datasource = new BasicBean();
            if (phen.has(GenotypePhenotypeDTO.RESOURCE_NAME)) {
                String provider = phen.getString(GenotypePhenotypeDTO.RESOURCE_NAME);
                datasource.setName(provider);
                sum.setDatasource(datasource);
            }
            else {
                sum.getStatus().addWarning(sum.getgId() + " has no resource name");
                datasource.setName("");
                sum.setDatasource(datasource);
            }
	
	        ImpressBaseDTO parameter = new ParameterDTO();
	        if (phen.has(GenotypePhenotypeDTO.PARAMETER_STABLE_ID)) {
	            parameter.setStableId(phen.getString(GenotypePhenotypeDTO.PARAMETER_STABLE_ID));
	            parameter.setName(phen.getString((GenotypePhenotypeDTO.PARAMETER_NAME)));
	        } else {
                sum.getStatus().addError("parameter_stable_id missing");
	        }
	        sum.setParameter(parameter);
	
	        ImpressBaseDTO pipeline = new ImpressBaseDTO();
	        if (phen.has(GenotypePhenotypeDTO.PIPELINE_STABLE_ID)) {
	            pipeline.setStableId(phen.getString(GenotypePhenotypeDTO.PIPELINE_STABLE_ID));
	            pipeline.setName(phen.getString((GenotypePhenotypeDTO.PIPELINE_NAME)));
	        } else {
                sum.getStatus().addError("pipeline stable_id missing");
	        }
	        sum.setPipeline(pipeline);
	
	        BasicBean project = new BasicBean();
	        if (phen.has(GenotypePhenotypeDTO.PROJECT_NAME)) {
                project.setName(phen.getString(GenotypePhenotypeDTO.PROJECT_NAME));
                if (phen.has(GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID)) {
                    project.setId(phen.getString(GenotypePhenotypeDTO.PROJECT_EXTERNAL_ID));
                }
                sum.setProject(project);
            }
            else {
                sum.getStatus().addWarning(sum.getgId() + " has no project name");
            }
	
	        if (phen.has(GenotypePhenotypeDTO.P_VALUE)) {
	            sum.setpValue(new Float(phen.getString(GenotypePhenotypeDTO.P_VALUE)));
	            sum.setEffectSize(new Float(phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE)));
	        }
	        
	        ImpressBaseDTO procedure = new ImpressBaseDTO();
	        if (phen.has(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID)) {
	            procedure.setStableId(phen.getString(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID));
	            procedure.setName(phen.getString(GenotypePhenotypeDTO.PROCEDURE_NAME));
	            sum.setProcedure(procedure);
	        } else {
                sum.getStatus().addError("procedure_stable_id");
	        }

            if ( phen.has(GenotypePhenotypeDTO.COLONY_ID) ){
                sum.setColonyId(phen.getString(GenotypePhenotypeDTO.COLONY_ID));
            }
        } catch (Exception e){
        	String errorCode = "";
        	
        		errorCode = "#18";
   
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
    public Map<String, Long> getAssociationsDistribution(String mpTermName, String resource) {

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
            return (getFacets(genotypePhenotypeCore.query(q))).get(GenotypePhenotypeDTO.COLONY_ID);
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
            return (getFacets(genotypePhenotypeCore.query(q))).get(GenotypePhenotypeDTO.MP_TERM_NAME).keySet();
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
        query.set("wt", "xml");


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
                        cell.addStatus("Data Available");
                    } else {
                        cell.addStatus("No MP");
                    }
                } else {
                    // System.err.println("mps are null or empty");
                    cell.addStatus("No MP");
                }
            } else {
                // if no doc found for the gene then no data available
                cell.addStatus("No Data Available");
            }
            xAxisToCellMap.put(xAxisBean.getId(), cell);
        }
        row.setXAxisToCellMap(xAxisToCellMap);

        System.out.println("---ROW " + row);

        return row;
    }

    public SolrClient getSolrServer() {
        return genotypePhenotypeCore;
    }

    public HttpSolrClient getHttpSolrClient() {
        return SolrUtils.getHttpSolrServer(genotypePhenotypeCore);
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
		System.out.println("Solr ULR for getTabbedCallSummary " + SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + q);

		String content = proxy.getContent(new URL(SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + q));

        return content;
    }


    // MOVED FROM OLD PostQcService


    /**
     * Used by chord diagram
     * @param topLevelMpTerms the mp terms are used with AND. If not null returns data for genes that have ALL phenotypes in the passed list.
     * @param idg Option to get data only for IDG. Set to false or null if you want all data.
     * @param idgClass one of [GPCRs, Ion Channels, Ion Channels]
     * @return
     */
    public JSONObject getPleiotropyMatrix(List<String> topLevelMpTerms, Boolean idg, String idgClass) {


        try {

            Set<GenotypePhenotypeDTO> pleiotropyGenes = getPleiotropyGenes(topLevelMpTerms, idg, idgClass);
            Set<String> topLevelMpTermNames = pleiotropyGenes
                    .stream()
                    .map(GenotypePhenotypeDTO::getTopLevelMpTermName)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            Map<String, Set<String>> genesByTopLevelMp = new HashMap<>(); // <mp, [genes]>
            Integer[][] matrix = new Integer[topLevelMpTermNames.size()][topLevelMpTermNames.size()];
            // initialize labels -> needed to keep track of order for the matrix cells
            List<String>  matrixLabels = new ArrayList<>(topLevelMpTermNames);

            // Fill matrix with 0s
            for (int i = 0; i < matrixLabels.size(); i++) {
                for (int j = 0; j< matrixLabels.size(); j++) {
                    matrix[i][j] = 0;
                }
                genesByTopLevelMp.put(matrixLabels.get(i), new HashSet<>());
            }

            Map<String, Set<String>> facetPivotResults = new HashMap<>(); // <gene, <top_level_mps>>
            for (GenotypePhenotypeDTO dto : pleiotropyGenes) {
                facetPivotResults.putIfAbsent(dto.getMarkerAccessionId(), new HashSet<>());
                facetPivotResults.get(dto.getMarkerAccessionId()).addAll(dto.getTopLevelMpTermName());
            }

            // Count genes associated to each pair of top-level mps. Gene count not g-p doc count, nor allele.
            for (String gene : facetPivotResults.keySet()) {
                Set<String> mpTerms = facetPivotResults.get(gene);
                for (int i = 0; i < matrixLabels.size(); i++){
                    String mpI = matrixLabels.get(i);
                    if (mpTerms.contains(mpI)) {
                        Set<String> temp = genesByTopLevelMp.get(mpI);
                        temp.add(gene);
                        genesByTopLevelMp.put(mpI, temp);
                        if (mpTerms.size() > 1) { // Other phenotypes too
                            for (int j = 0; j < matrixLabels.size(); j++) {
                                String mpJ = matrixLabels.get(j);
                                if (mpTerms.contains(mpJ)) {
                                    if (!mpI.equalsIgnoreCase(mpJ)) {
                                        matrix[i][j]++;
                                    }
                                }
                            }
                        } else { // Only one top level mp for this gene; count as self, will display as arch to self
                            matrix[i][i]++;
                        }
                    }
                }
            }

            List<JSONObject> labelList = genesByTopLevelMp.entrySet().stream().map(entry -> {
                try {
                    return new JSONObject().put("name", entry.getKey()).put("geneCount", entry.getValue().size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            JSONObject result = new JSONObject();
            result.put("matrix", new org.springframework.boot.configurationprocessor.json.JSONArray(matrix));
            result.put("labels", labelList);
            result.put("geneCount", facetPivotResults.keySet().size());

            return result;

        } catch (SolrServerException | IOException | JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    public String getPleiotropyDownload(List<String> topLevelMpTerms, Boolean idg, String idgClass) throws IOException, SolrServerException {

        Set<GenotypePhenotypeDTO> pleiotropyGenes = getPleiotropyGenes(topLevelMpTerms, idg, idgClass);
        Map<PleiotropyExportRow, AtomicInteger> data = new HashMap<>();

        for (GenotypePhenotypeDTO dto : pleiotropyGenes) {
            for (String term : dto.getTopLevelMpTermName()) {
                PleiotropyExportRow newRow = new PleiotropyExportRow(dto.getMarkerAccessionId(), dto.getMarkerSymbol(), term);
                data.putIfAbsent(newRow, new AtomicInteger(0));
                data.get(newRow).incrementAndGet();
            }
        }

        // Return list of genes, unique entries only. The splitting is based on the order of pivot facets.
        return data.entrySet().stream().sorted(comparingByKey()).map(x -> String.join(",", Arrays.asList(
                x.getKey().accessionId,
                x.getKey().symbol,
                x.getKey().topLevelMp,
                x.getValue().toString())))
                .collect(Collectors.joining("\n"));
    }

    static class PleiotropyExportRow implements Comparable<PleiotropyExportRow> {
        String accessionId;
        String symbol;
        String topLevelMp;

        public PleiotropyExportRow(String accessionId, String symbol, String topLevelMp) {
            this.accessionId = accessionId;
            this.symbol = symbol;
            this.topLevelMp = topLevelMp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PleiotropyExportRow that = (PleiotropyExportRow) o;
            return accessionId.equals(that.accessionId) &&
                    symbol.equals(that.symbol) &&
                    topLevelMp.equals(that.topLevelMp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accessionId, symbol, topLevelMp);
        }


        @Override
        public int compareTo(PleiotropyExportRow o) {
            return this.symbol.compareTo(o.symbol);
        }
    }

    /**
     * The set of associations differs depending on topLevelMpTerms. If at least one term is passed, we need to
     * filter the list of genes that have the required phenotypes. We can't use the list of MP terms themselves
     * as it will filter out the other phenotype associations for the genes we're interested in.
     *
     * @param topLevelMpTerms The list of terms to filter for, or null for all terms
     * @param idg True indicating filter in IDG genes only
     * @param idgClass The class of IDG genes, e.g., Kinase, Ion channel, etc.
     * @return Set of genotype to phenotype associations for genes that have all top level phenotypes
     *         passed in topLevelMpTerms.
     */
    @Cacheable("pleiotropy")
    public Set<GenotypePhenotypeDTO> getPleiotropyGenes(List<String> topLevelMpTerms, Boolean idg, String idgClass) throws IOException, SolrServerException {

        final List<String> fieldList = Arrays.asList(
                GenotypePhenotypeDTO.MARKER_ACCESSION_ID,
                GenotypePhenotypeDTO.MARKER_SYMBOL,
                GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);

        SolrQuery query = new SolrQuery()
                .setQuery(GenotypePhenotypeDTO.MP_TERM_ID + ":*")
                .setRows(Integer.MAX_VALUE)
                .setFields(String.join(",", fieldList));

        Set<GenotypePhenotypeDTO> genotypePhenotypeDTOS = new HashSet<>(
                genotypePhenotypeCore
                .query(query)
                .getBeans(GenotypePhenotypeDTO.class));

        // Filter for IDG genes if idg is true
        if ( idg != null && idg ){

            // If the idgClass has not been set, get all genes for the idg project, else filter for the class specified
            Set<GenesSecondaryProject> idgGenes =
                    idgClass == null ?
                            genesSecondaryProjectRepository.getAllBySecondaryProjectId("idg") :
                            genesSecondaryProjectRepository.getAllBySecondaryProjectIdAndGroupLabel("idg", idgClass);
            Set<String> idgGeneIds = idgGenes
                    .stream()
                    .map(GenesSecondaryProject::getMgiGeneAccessionId)
                    .collect(Collectors.toSet());
            genotypePhenotypeDTOS = genotypePhenotypeDTOS
                    .stream()
                    .filter(x -> idgGeneIds.contains(x.getMarkerAccessionId()))
                    .collect(Collectors.toSet());
        }

        // Deal with top level MP terms if set
        if (topLevelMpTerms != null) {
            genotypePhenotypeDTOS = genotypePhenotypeDTOS
                    .stream()
                    .filter(gene -> gene.getTopLevelMpTermId().containsAll(topLevelMpTerms))
                    .collect(Collectors.toSet());
        }

        return genotypePhenotypeDTOS;
    }


    Map<String,Long> getDocumentCountByMgiGeneAccessionId() {

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setRows(0);
        query.setFacet(true);
        query.addFacetField(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);

        try {
            return getFacets(genotypePhenotypeCore.query(query)).get(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return new HashMap<>();

    }

    private JSONObject addJitter(Double x, Double y, Set<String> existingPoints, JSONObject obj) throws JSONException {

        String s = x + "_" + y;
        if (!existingPoints.contains(s)){
            obj.accumulate("y", y);
            obj.accumulate("x", x);
            existingPoints.add(s);
        } else {
            if (existingPoints.size()%6 == 0) {
                y += 0.05;
            } else if (existingPoints.size()%6 == 1) {
                x += 0.05;
            } else if (existingPoints.size()%6 == 2) {
                x += 0.05;
                y += 0.05;
            } else if (existingPoints.size()%6 == 3) {
                x -= 0.05;
            } else if (existingPoints.size()%6 == 4) {
                y -= 0.05;
            } else if (existingPoints.size()%6 == 5) {
                x -= 0.05;
                y -= 0.05;
            }
            addJitter(x, y, existingPoints, obj);
        }
        return obj;
    }

    /**
     * @author ilinca
     * @since 2016/07/05
     * @param anatomyId
     * @return Number of genes in g-p core for anatomy term given.
     * @throws SolrServerException, IOException
     */
    public Integer getGenesByAnatomy(String anatomyId)
            throws SolrServerException, IOException, JSONException {

        SolrQuery query = new SolrQuery();
        query.setQuery("(" + GenotypePhenotypeDTO.ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " +
                               GenotypePhenotypeDTO.INTERMEDIATE_ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " +
                               GenotypePhenotypeDTO.TOP_LEVEL_ANATOMY_TERM_ID + ":\"" + anatomyId + "\")")
                .setRows(0)
                .add("group", "true")
                .add("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID)
                .add("group.ngroups", "true")
                .add("wt","json");

        JSONObject groups = new JSONObject(genotypePhenotypeCore.query(query).getResponse().get("grouped").toString().replaceAll("=", ":"));

        return groups.getJSONObject(GenotypePhenotypeDTO.MARKER_ACCESSION_ID).getInt("ngroups");
    }

    /**
     *
     * @param mpId
     * @param filterOnAccessions list of marker accessions to restrict the results by e.g. for the hearing page we only want results for the 67 genes for the paper
     * @return
     * @throws JSONException
     */
    public JSONArray getTopLevelPhenotypeIntersection(String mpId, Set<String> filterOnAccessions) throws JSONException {

        String pivot = GenotypePhenotypeDTO.MARKER_ACCESSION_ID + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
        SolrQuery query = new SolrQuery();
        query.setQuery(mpId == null ? "*:*" : GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\"");
        query.setRows(0);
        query.setFacet(true);
        query.addFacetField(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);
        query.add("facet.pivot", pivot);

        try {
            QueryResponse response = genotypePhenotypeCore.query(query);
            Map<String,Long> countByGene = getFacets(response).get(GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
            Map<String,List<String>>  geneAccSymbol = getFacetPivotResults(response,pivot);

            Set<String> jitter = new HashSet<>();
            JSONArray array = new JSONArray();
            for (String markerAcc: countByGene.keySet()){
                JSONObject obj = new JSONObject();
                Double y = new Double(countByGene.get(markerAcc));
                Double x = (getDocumentCountByMgiGeneAccessionId().get(markerAcc) - y);
                obj = addJitter(x, y, jitter, obj);
                obj.accumulate("markerAcc", markerAcc);
                obj.accumulate("markerSymbol", geneAccSymbol.get(markerAcc).get(0));
                if(filterOnAccessions!=null && filterOnAccessions.contains(markerAcc)){
                    array.put(obj);
                }else if(filterOnAccessions==null){
                    array.put(obj);
                }
            }

            return array;

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // IMPLEMENTATIONS


    @Override
    public long getWebStatus() throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();

        query.setQuery("*:*").setRows(0);
        QueryResponse response = genotypePhenotypeCore.query(query);
        return response.getResults().getNumFound();
    }

    @Override
    public String getServiceName() {

        return "genotypePhenotype (genotype-phenotype core)";
    }
}