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
import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.mousephenotype.cda.dto.AggregateCountXY;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;

@Service
@Named("genotype-phenotype-service")
public class GenotypePhenotypeService extends BasicService implements WebStatus {

    private final       Logger logger            = LoggerFactory.getLogger(this.getClass());

    protected ImpressService                  impressService;
    protected SolrClient                      genotypePhenotypeCore;
    protected GenesSecondaryProjectServiceIdg genesSecondaryProjectRepository;


    @Inject
    public GenotypePhenotypeService(
            @NotNull ImpressService impressService,
            @NotNull @Qualifier("genotypePhenotypeCore") SolrClient genotypePhenotypeCore,
            @NotNull GenesSecondaryProjectServiceIdg genesSecondaryProjectRepository)
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
            return new TreeMap<>(getFacets(response).get(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME));
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
    
    public List<String[]> getHitsDistributionByProcedure(List<String> resourceName)
    throws SolrServerException, IOException {

        return getHitsDistributionBySomething(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID, resourceName);
    }

    public List<String[]> getHitsDistributionByParameter(List<String> resourceName)
    throws SolrServerException, IOException  {

        return getHitsDistributionBySomething(GenotypePhenotypeDTO.PARAMETER_STABLE_ID, resourceName);
    }


    public Map<String, Long> getHitsDistributionBySomethingNoIds(String fieldToDistributeBy, List<String> resourceName, ZygosityType zygosity, int facetMincount, Double maxPValue)
    throws SolrServerException, IOException  {

        Map<String, Long>  res = new HashMap<>();
        long time = System.currentTimeMillis();
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
        throws SolrServerException, IOException {

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
                    mpTermsByGene.put(mpTerm, new ArrayList<>());
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
        throws SolrServerException, IOException {

        List<String[]>  res = new ArrayList<>();
        long time = System.currentTimeMillis();
        String pivotFacet = "";
        SolrQuery q = new SolrQuery();

        if (field.equals(GenotypePhenotypeDTO.PARAMETER_STABLE_ID)){
            pivotFacet =  GenotypePhenotypeDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.PARAMETER_NAME;
        } else if (field.equals(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID)){
            pivotFacet =  GenotypePhenotypeDTO.PROCEDURE_STABLE_ID + "," + StatisticalResultDTO.PROCEDURE_NAME;
        }
        pivotFacet = pivotFacet + "," + GenotypePhenotypeDTO.MARKER_SYMBOL + "," + StatisticalResultDTO.MARKER_ACCESSION_ID;

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
                String parameterId = pivot.getValue().toString();
                String parameterName = pivot.getPivot().get(0).getValue().toString();
                int count = pivot.getPivot().get(0).getCount();
                List<String> geneSymbols = new ArrayList<>();
                List<String> geneAccs = new ArrayList<>();
                for (int i = 0; i < pivot.getPivot().get(0).getPivot().size(); i++) {
                    geneSymbols.add(pivot.getPivot().get(0).getPivot().get(i).getValue().toString());
                    geneAccs.add(pivot.getPivot().get(0).getPivot().get(i).getPivot().get(0).getValue().toString());
                }
                String genes = StringUtils.join(geneSymbols, "|");
                String accs = StringUtils.join(geneAccs, "|");
                String[] row = {parameterId, parameterName, genes, accs, Integer.toString(count)};
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

        List<String> res = new ArrayList<>();
        SolrQuery query = new SolrQuery().setQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\""
            + phenotype_id + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\") AND ("
            + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsConstants.B6N_STRAINS, "\" OR " + GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\") AND "
            + GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":\"" + parameterStableId + "\"").setRows(100000000);
        query.set("group.field", GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        query.set("group", true);
        List<Group> groups = genotypePhenotypeCore.query(query).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            if ( ! res.contains(gr.getGroupValue())) {
                res.add(gr.getGroupValue());
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
        QueryResponse rsp;
        rsp = genotypePhenotypeCore.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allGenes = new HashSet<>();
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
        HashSet<String> allPhenotypes = new HashSet<>();
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
        HashSet<String> allTopLevelPhenotypes = new HashSet<>();
        for (SolrDocument doc : res) {
            List<String> ids = getListFromCollection(doc.getFieldValues(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID));
            allTopLevelPhenotypes.addAll(ids);
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
        HashSet<String> allIntermediateLevelPhenotypes = new HashSet<>();
        for (SolrDocument doc : res) {
            List<String> ids = getListFromCollection(doc.getFieldValues(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID));
            allIntermediateLevelPhenotypes.addAll(ids);
        }

        return allIntermediateLevelPhenotypes;
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


    
    /**
     * Get map of top level MP terms for supplied gene and zygosity
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


    /**
     * Returns a PhenotypeFacetResult object given a phenotyping center and a
     * pipeline stable id
     *
     * @param phenotypingCenter a short name for a phenotyping center
     * @param pipelineStableId a stable pipeline id
     * @return a PhenotypeFacetResult instance containing a list of PhenotypeCallSummary objects.
     */
    public PhenotypeFacetResult getPhenotypeFacetResultByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId)
        throws IOException, URISyntaxException, JSONException {

        String solrUrl = SolrUtils.getBaseURL(genotypePhenotypeCore);// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";

        solrUrl += "/select/?q=" + GenotypePhenotypeDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"" + "&fq=" + GenotypePhenotypeDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId + "&facet=true" + "&facet.field=" + GenotypePhenotypeDTO.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeDTO.PROCEDURE_NAME + "&facet.field=" + GenotypePhenotypeDTO.MARKER_SYMBOL + "&facet.field=" + GenotypePhenotypeDTO.MP_TERM_NAME + "&sort=p_value%20asc" + "&rows=10000000&version=2.2&start=0&indent=on&wt=json";
        return this.createPhenotypeResultFromSolrResponse(solrUrl);
    }

    public PhenotypeFacetResult getMPByGeneAccessionAndFilter(String accId, List<String> topLevelMpTermName)
    throws IOException, URISyntaxException, JSONException {

        String solrUrl = SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?";
        SolrQuery q = new SolrQuery();
        
        q.setQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + accId + "\"");
        q.setRows(10000000);
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.setFacetLimit(-1);
        q.addFacetField(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
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
        
        return createPhenotypeResultFromSolrResponse(url);

    }
    /**
     * Get a list of gene symbols for this phenotype
     */
    public List<String> getGenesForMpId(String phenotype_id)
            throws IOException, SolrServerException {
        List<String> results = new ArrayList<>();
        SolrQuery q = new SolrQuery();

        q.setQuery("*:*");
        q.addFilterQuery("(" + GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + phenotype_id +
                "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + phenotype_id + "\")");
        q.setRows(10000000);
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.setFacetLimit(-1);
        q.addFacetField(GenotypePhenotypeDTO.MARKER_SYMBOL);
        q.set("wt", "json");
        q.setSort(GenotypePhenotypeDTO.P_VALUE, ORDER.asc);
        q.setFields(GenotypePhenotypeDTO.MARKER_SYMBOL);

        QueryResponse response = genotypePhenotypeCore.query(q);
        for (Count facet : response.getFacetField(GenotypePhenotypeDTO.MARKER_SYMBOL).getValues()) {
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

        if (sex != null && sex.size() > 0) {
            String sexes = sex.stream().map(SexType::getName).collect(Collectors.joining(" OR "));
            if(sexes.equalsIgnoreCase("female OR male") || sexes.equalsIgnoreCase("male OR female")){
                sexes="female OR male OR both OR not_considered";//to account for where entries in solr have used both instead of female and male sexes e.g. threei data has some
            }
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


    public List<GenotypePhenotypeDTO> getGenotypePhenotypeForViability(String markerAccession)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .setRows(Integer.MAX_VALUE);

        if (markerAccession != null) {
            query.addFilterQuery(GenotypePhenotypeDTO.MARKER_ACCESSION_ID + ":\"" + markerAccession + "\"");
        }


            query.addFilterQuery(GenotypePhenotypeDTO.PARAMETER_STABLE_ID + ":IMPC_VIA_*");





        return genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);
    }

    // Returns status of operation in PhenotypeFacetResult.status. Query it for errors and warnings.
    public PhenotypeFacetResult createPhenotypeResultFromSolrResponse(String url)
    throws IOException, URISyntaxException, JSONException {

        PhenotypeFacetResult facetResult = new PhenotypeFacetResult();
        List<PhenotypeCallSummaryDTO> list = new ArrayList<>();
        JSONObject results;
        results = JSONRestUtil.getResults(url);
        JSONArray docs = results.getJSONObject("response").getJSONArray("docs");

        for (int i = 0; i < docs.length(); i++) {
            Object doc = docs.get(i);
            try {
                PhenotypeCallSummaryDTO call = createSummaryCall(doc);
                if ((call.getStatus().hasErrors()) || call.getStatus().hasWarnings()) {
                    facetResult.getStatus().add(call.getStatus());
                }
                list.add(call);
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
     */
    public PhenotypeCallSummaryDTO createSummaryCall(Object doc)
    throws Exception{

        JSONObject              phen = (JSONObject) doc;
        JSONArray               topLevelMpTermNames;
        JSONArray               topLevelMpTermIDs;
        PhenotypeCallSummaryDTO sum;

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

                List<BasicBean> topLevelPhenotypeTerms = new ArrayList<>();
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
                    sum.getStatus().addWarning(gene.getSymbol() + " has no accession id");
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

	try {
        SexType sexType = SexType.getByDisplayName(sex);
        sum.setSex(sexType);

    }catch (Exception e

    ){
	    e.printStackTrace();
        System.out.println("sex is |"+sex+"|");
    };
            if( phen.has(GenotypePhenotypeDTO.LIFE_STAGE_NAME)) {
                String lifeStageName = phen.getString(GenotypePhenotypeDTO.LIFE_STAGE_NAME);
                sum.setLifeStageName(lifeStageName);
            }

            BasicBean datasource = new BasicBean();
            if (phen.has(GenotypePhenotypeDTO.RESOURCE_NAME)) {
                String provider = phen.getString(GenotypePhenotypeDTO.RESOURCE_NAME);
                datasource.setName(provider);
            }
            else {
                sum.getStatus().addWarning(sum.getgId() + " has no resource name");
                datasource.setName("");
            }
            sum.setDatasource(datasource);

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
	        }

            if (phen.has(GenotypePhenotypeDTO.EFFECT_SIZE)) {
                sum.setEffectSize(new Float(phen.getString(GenotypePhenotypeDTO.EFFECT_SIZE)));
            }


	        if (phen.has(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID)) {
	            // There should not ever be more than 1 procedure in the list
                final JSONArray jsonProcedureStableIds = phen.getJSONArray(GenotypePhenotypeDTO.PROCEDURE_STABLE_ID);
                final String jsonProcedureName = phen.getString(GenotypePhenotypeDTO.PROCEDURE_NAME);
                ImpressBaseDTO procedure = new ImpressBaseDTO();
                procedure.setStableId(jsonProcedureStableIds.getString(0));
                procedure.setName(jsonProcedureName);
	            sum.setProcedure(procedure);
	        } else {
                sum.getStatus().addError("procedure_stable_id");
	        }

            if ( phen.has(GenotypePhenotypeDTO.COLONY_ID) ){
                sum.setColonyId(phen.getString(GenotypePhenotypeDTO.COLONY_ID));
            }
        } catch (Exception e){
        	String errorCode;
        	
        		errorCode = "#18";
   
        	Exception exception = new Exception(errorCode);
    		System.out.println(errorCode);
    		e.printStackTrace();
    		throw exception;
        }
    
        return sum;
    }

    public SolrQuery buildQuery(String geneAccession, List<String> procedureName, List<String> alleleSymbol, List<String> phenotypingCenter,
                                List<String> pipelineName, List<String> procedureStableIds, List<String> resource, List<String> mpTermNames, Integer rows, List<String> sex, List<String> zygosities,
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
        if (mpTermNames != null && mpTermNames.size() > 0) {
            ArrayList<String> mpTermNamesSplit = new ArrayList<>();
            for(String mpTermName : mpTermNames) {
                mpTermNamesSplit.addAll(new ArrayList<>(Arrays.asList(mpTermName.split(" or "))));
            }
            query.addFilterQuery(GenotypePhenotypeDTO.MP_TERM_NAME + ":(\"" + StringUtils.join(mpTermNamesSplit, "\" OR \"") + "\") OR "
                    + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + ":(\"" + StringUtils.join(mpTermNamesSplit, "\" OR \"") + "\") OR "
                   // + StatisticalResultDTO.MP_TERM_ID_OPTIONS + ":(\"" + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                    + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_NAME + ":(\"" + StringUtils.join(mpTermNamesSplit, "\" OR \"") + "\")");
                  //  + GenotypePhenotypeDTO.FEMALE_TOP_LEVEL_MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"")
                    //+ "\") OR " + StatisticalResultDTO.FEMALE_MP_TERM_ID + ":(\""
                   // + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
                   // + GenotypePhenotypeDTO.FEMALE_INTERMEDIATE_MP_TERM_ID + ":(\""
                   // + StringUtils.join(mpTermId, "\" OR \"") + "\") OR "
//                    + GenotypePhenotypeDTO.MALE_TOP_LEVEL_MP_TERM_ID + ":(\"" + StringUtils.join(mpTermId, "\" OR \"")
//                    + "\") OR " + GenotypePhenotypeDTO.MALE_INTERMEDIATE_MP_TERM_ID + ":(\""
//                    + StringUtils.join(mpTermId, "\" OR \"") + "\") OR " + GenotypePhenotypeDTO.MALE_MP_TERM_ID + ":(\""
//                    + StringUtils.join(mpTermId, "\" OR \"") + "\")");
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

    public SolrClient getSolrServer() {
        return genotypePhenotypeCore;
    }

    public HttpSolrClient getHttpSolrClient() {
        return SolrUtils.getHttpSolrServer(genotypePhenotypeCore);
    }


    // MOVED FROM OLD PostQcService


    /**
     * Used by chord diagram
     * @param topLevelMpTerms the mp terms are used with AND. If not null returns data for genes that have ALL phenotypes in the passed list.
     * @param idg Option to get data only for IDG. Set to false or null if you want all data.
     * @param idgClass one of [GPCRs, Ion Channels, Ion Channels]
     * @return A JSONObject representing the pietropy matrix
     */
    public JSONObject getPleiotropyMatrix(List<String> topLevelMpTerms, Boolean idg, String idgClass) {
        try {

            Set<GenotypePhenotypeDTO> pleiotropyGenes = getPleiotropyGenes(topLevelMpTerms, idg, idgClass);
//            for(GenotypePhenotypeDTO dto: pleiotropyGenes){
//                System.out.println("pleiotropyGene="+dto.getTopLevelMpTermName());
//                if(dto.getTopLevelMpTermName()==null){
//                    System.out.println(dto);
//                }
//            }



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
                .setQuery(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + ":*")
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
                            genesSecondaryProjectRepository.getAllBySecondaryProjectId() :
                            genesSecondaryProjectRepository.getAllBySecondaryProjectIdAndGroupLabel(idgClass);
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
                    .filter(gene -> gene.getTopLevelMpTermName()!= null)
                    .filter(gene -> gene.getTopLevelMpTermName().containsAll(topLevelMpTerms))
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

    private void addJitter(Double x, Double y, Set<String> existingPoints, JSONObject obj) throws JSONException {

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
            } else {
                x -= 0.05;
                y -= 0.05;
            }
            addJitter(x, y, existingPoints, obj);
        }
    }

    /**
     * @param filterOnAccessions list of marker accessions to restrict the results by e.g. for the hearing page we only want results for the 67 genes for the paper
     */
    public JSONArray getTopLevelPhenotypeIntersection(String mpId, Set<String> filterOnAccessions) throws JSONException {

        final Map<String, Long> documentCountByMgiGeneAccessionId = getDocumentCountByMgiGeneAccessionId();
        SolrQuery query = new SolrQuery()
                .setRows(Integer.MAX_VALUE);
        query.setQuery(mpId == null ? "*:*" : GenotypePhenotypeDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\" OR " + GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\"");

        try {
            final List<GenotypePhenotypeDTO> dtos = genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);

            // Data structure definition
            //   "markerAcc" -> Accession ID of the gene
            //   "markerSymbol" -> Symbol of the gene
            //   "y" -> Number of significant phenotypes for this gene
            //   "x" -> Number of significant phenotypes for this gene for other phenotypes

            final Map<String, Long> countByGene = dtos.stream()
                    .collect(Collectors.groupingBy(
                            GenotypePhenotypeDTO::getMarkerAccessionId,
                            Collectors.counting()));

            Map<String, String> geneAccSymbol = dtos.stream()
                    .collect(Collectors.toMap(
                            GenotypePhenotypeDTO::getMarkerAccessionId,
                            GenotypePhenotypeDTO::getMarkerSymbol,
                            (x, y) -> x));


            Set<String> jitter = new HashSet<>();
            JSONArray array = new JSONArray();
            for (String markerAcc : countByGene.keySet().stream().sorted().collect(Collectors.toList())) {
                JSONObject obj = new JSONObject();
                obj.accumulate("markerAcc", markerAcc);
                obj.accumulate("markerSymbol", geneAccSymbol.get(markerAcc));

                Double y = new Double(countByGene.get(markerAcc));
                Double x = documentCountByMgiGeneAccessionId.get(markerAcc) - y;
                addJitter(x, y, jitter, obj);

                if (filterOnAccessions != null && filterOnAccessions.contains(markerAcc)) {
                    array.put(obj);
                } else if (filterOnAccessions == null) {
                    array.put(obj);
                }
            }

            return array;

        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Map<CombinedObservationKey, ExperimentsDataTableRow> getAllDataRecords(String geneAccession, List<String> procedureName , List<String> alleleSymbol, List<String> phenotypingCenter, List<String> pipelineName, List<String> procedureStableIds, List<String> resource, List<String> mpTermId, String graphBaseUrl)
            throws NumberFormatException, SolrServerException, IOException {

        Map<CombinedObservationKey, ExperimentsDataTableRow> results = new HashMap<>();

        SolrQuery query = buildQuery(geneAccession, procedureName,alleleSymbol, phenotypingCenter, pipelineName, procedureStableIds, resource, mpTermId, null, null, null, null, null, null, null, null);
        List<GenotypePhenotypeDTO> solrResults = genotypePhenotypeCore.query(query).getBeans(GenotypePhenotypeDTO.class);

        for (GenotypePhenotypeDTO dto : solrResults) {
            ExperimentsDataTableRow row = getRowFromDto(dto, graphBaseUrl);
            results.put(row.getCombinedKey(), row);
        }

        return results;

    }

    private ExperimentsDataTableRow getRowFromDto(GenotypePhenotypeDTO dto, String graphBaseUrl)
            throws UnsupportedEncodingException {

        MarkerBean allele = new MarkerBean();
        allele.setAccessionId(dto.getAlleleAccessionId());
        allele.setSymbol(dto.getAlleleSymbol());

        MarkerBean gene = new MarkerBean();
        gene.setAccessionId(dto.getMarkerAccessionId());
        gene.setSymbol(dto.getMarkerSymbol());

        ImpressBaseDTO procedure  = new ImpressBaseDTO(null, Long.parseLong(dto.getProcedureStableKey().get(0)), dto.getProcedureStableId().get(0), dto.getProcedureName());
        ImpressBaseDTO parameter = new ImpressBaseDTO(null, Long.parseLong(dto.getParameterStableKey().get(0)), dto.getParameterStableId(), dto.getParameterName());
        ImpressBaseDTO pipeline = new ImpressBaseDTO(null,Long.parseLong( dto.getPipelineStableKey()), dto.getPipelineStableId(), dto.getPipelineName());

        ZygosityType zygosity = dto.getZygosity() != null ? ZygosityType.valueOf(dto.getZygosity()) : ZygosityType.not_applicable;
        ExperimentsDataTableRow row = new ExperimentsDataTableRow(dto.getPhenotypingCenter(), dto.getStatisticalMethod(),
                "Success", allele, gene, zygosity,
                pipeline, procedure, parameter, graphBaseUrl, dto.getP_value(), null,
                null, dto.getEffectSize(), null);
        row.setLifeStageName(dto.getLifeStageName());
        row.setLifeStageAcc(dto.getLifeStageAcc());
        row.setPhenotypeTerm(new BasicBean(dto.getMpTermId(), dto.getMpTermName()));
        row.setSignificant(Boolean.TRUE);
        return row;
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
