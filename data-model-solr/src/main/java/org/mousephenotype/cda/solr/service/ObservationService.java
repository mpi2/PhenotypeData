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

import com.google.common.collect.Iterators;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.db.pojo.DiscreteTimePoint;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.enumerations.*;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.web.dto.CategoricalDataObject;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ObservationService extends BasicService implements WebStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected SolrClient experimentCore;

    private static int ROWLIMIT=100000;

    @Inject
    public ObservationService(HttpSolrClient experimentCore) {
        super();
        this.experimentCore = experimentCore;
    }

    public ObservationService() {
        super();
    }


    /**
     * Return true if the marker has bodyweight data
     *
     * @param markerAcccesionId
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public Boolean hasBodyWeight(String markerAcccesionId) throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + markerAcccesionId + "\"")
                .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":IMPC_BWT_008_001")
                .setRows(1);

        QueryResponse response = experimentCore.query(query);
        return response.getBeans(ExperimentDTO.class).size() > 0;
    }

    /**
     * Reinstated this method to show charts when no Statistical Result associated
     * @param mgiAccession
     * @param parameterStableId
     * @param pipelineStableId
     * @param phenotypingCenterParams
     * @param strainParams
     * @param metaDataGroups
     * @param alleleAccessions
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public Map<String, List<String>> getExperimentKeys(String mgiAccession, String parameterStableId, List<String> pipelineStableId, List<String> phenotypingCenterParams, List<String> strainParams, List<String> metaDataGroups, List<String> alleleAccessions)
            throws SolrServerException, IOException {

        // Example of key
        // String experimentKey = observation.getPhenotypingCenter()
        // + observation.getStrain()
        // + observation.getParameterStableId()
        // + observation.getGeneAccession()
        // + observation.getMetadataGroup();
        Map<String, List<String>> map = new LinkedHashMap<>();

        SolrQuery query = new SolrQuery();

        query.setQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId).addFacetField(ObservationDTO.PHENOTYPING_CENTER).addFacetField(ObservationDTO.STRAIN_ACCESSION_ID).addFacetField(ObservationDTO.METADATA_GROUP).addFacetField(ObservationDTO.PIPELINE_STABLE_ID).addFacetField(ObservationDTO.ALLELE_ACCESSION_ID).setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).setFacetSort(FacetParams.FACET_SORT_COUNT);

        if (phenotypingCenterParams != null && !phenotypingCenterParams.isEmpty()) {
            List<String> spaceSafeStringsList = new ArrayList<>();
            for (String pCenter : phenotypingCenterParams) {
                if (!pCenter.endsWith("\"") && !pCenter.startsWith("\"")) {
                    spaceSafeStringsList.add("\"" + pCenter + "\"");
                }
            }
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":(" + StringUtils.join(spaceSafeStringsList, " OR ") + ")");
        }

        if (strainParams != null && !strainParams.isEmpty()) {
            query.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":(" + StringUtils.join(strainParams, " OR ").replace(":", "\\:") + ")");
        }

        if (metaDataGroups != null && !metaDataGroups.isEmpty()) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":(" + StringUtils.join(metaDataGroups, " OR ") + ")");
        }

        if (pipelineStableId != null && !pipelineStableId.isEmpty()) {
            query.addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":(" + StringUtils.join(pipelineStableId, " OR ") + ")");
        }

        if (alleleAccessions != null && !alleleAccessions.isEmpty()) {
            String alleleFilter = ObservationDTO.ALLELE_ACCESSION_ID + ":(" + StringUtils.join(alleleAccessions, " OR ").replace(":", "\\:") + ")";
            logger.debug("alleleFilter=" + alleleFilter);
            query.addFilterQuery(alleleFilter);

        }

        QueryResponse response = experimentCore.query(query);
        System.out.println("experiment key query=" + query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            // if (ff.getValues() == null) {
            // continue;
            // }
            for (Count count : ff.getValues()) {
                if (map.containsKey(ff.getName())) {
                    map.get(ff.getName()).add(count.getName());
                } else {
                    List<String> newList = new ArrayList<>();
                    newList.add(count.getName());
                    map.put(ff.getName(), newList);
                }

            }
        }

        logger.info("experimentKeys=" + map);
        return map;
    }


    public List<Group> getDatapointsByColony(List<String> resourceName, String parameterStableId, String biologicalSampleGroup)
            throws SolrServerException, IOException {

        SolrQuery q = new SolrQuery();
        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        if (parameterStableId != null) {
            q.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
        }

        q.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSampleGroup);

        q.set("group", true);
        q.set("group.field", ObservationDTO.COLONY_ID);
        q.set("group.limit", 10000);
        q.set("group.sort", ObservationDTO.DATE_OF_EXPERIMENT + " ASC");

        q.setFields(ObservationDTO.DATA_POINT, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.DATE_OF_EXPERIMENT,
                ObservationDTO.ALLELE_SYMBOL, ObservationDTO.GENE_SYMBOL, ObservationDTO.COLONY_ID, ObservationDTO.ALLELE_ACCESSION_ID,
                ObservationDTO.PIPELINE_STABLE_ID, ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.STRAIN_ACCESSION_ID,
                ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.PHENOTYPING_CENTER_ID);
        q.setRows(10000);

        logger.info("Solr url for getOverviewGenesWithMoreProceduresThan " + SolrUtils.getBaseURL(experimentCore) + "/select?" + q);
        return experimentCore.query(q).getGroupResponse().getValues().get(0).getValues();

    }


    public List<String> getGenesWithMoreProcedures(int minProcedureCount, List<String> resourceName)
            throws SolrServerException, IOException {

        Set<String> genes = new HashSet<>();

        Set<String> geneAccessionIds = getAllGeneIdsByResource(resourceName, true);
        Iterators.partition(geneAccessionIds.iterator(), 50).forEachRemaining(geneAccessions ->
        {

            SolrQuery q = new SolrQuery()
                    .setQuery(geneAccessions.stream().collect(Collectors.joining("\" OR \"", ObservationDTO.GENE_ACCESSION_ID + ":(\"", "\")")))
                    .addFacetQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"))
                    .setRows(1)
                    .setFacetMinCount(1)
                    .setFacet(true)
                    .setFacetLimit(-1);

            String geneProcedurePivot = ObservationDTO.GENE_SYMBOL + "," + ObservationDTO.PROCEDURE_NAME;
            q.add("facet.pivot", geneProcedurePivot);

            logger.info("Solr url for getOverviewGenesWithMoreProceduresThan " + SolrUtils.getBaseURL(experimentCore) + "/select?" + q);
            QueryResponse response;
            try {
                response = experimentCore.query(q);
            } catch (SolrServerException | IOException e) {
                e.printStackTrace();
                return;
            }

            for (PivotField pivot : response.getFacetPivot().get(geneProcedurePivot)) {
                if (pivot.getPivot() != null){
                    if (pivot.getPivot().size() >= minProcedureCount) {
                        genes.add(pivot.getValue().toString());
                    }
                }
            }

        });

        return new ArrayList<>(genes);
    }

    public List<ObservationDTO> getObservationsByParameterStableId(String parameterStableId) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.setQuery(String.format("%s:\"%s\"", ObservationDTO.PARAMETER_STABLE_ID, parameterStableId));
        query.setRows(Integer.MAX_VALUE);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        logger.info("getObservationsByParameterStableId Url: " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return experimentCore.query(query).getBeans(ObservationDTO.class);
    }


    public long getNumberOfDocuments(List<String> resourceName, boolean experimentalOnly)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.setRows(0);
        if (resourceName != null) {
            query.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            query.setQuery("*:*");
        }
        if (experimentalOnly) {
            query.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        }

        return experimentCore.query(query).getResults().getNumFound();
    }


    public Set<String> getViabilityForGene(String acc)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":IMPC_VIA_001_001");
        query.setFilterQueries(ObservationDTO.GENE_ACCESSION_ID + ":\"" + acc + "\"");
        query.addField(ObservationDTO.GENE_SYMBOL);
        query.addField(ObservationDTO.GENE_ACCESSION_ID);
        query.addField(ObservationDTO.CATEGORY);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        query.setRows(100000);

        logger.info("getViabilityForGene Url" + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        HashSet<String> viabilityCategories = new HashSet<>();

        for (SolrDocument doc : experimentCore.query(query).getResults()) {
            viabilityCategories.add(doc.getFieldValue(ObservationDTO.CATEGORY).toString());
        }

        return viabilityCategories;
    }

    public Map<String, Set<String>> getViabilityCategories(List<String> resources, Boolean adultOnly) throws SolrServerException, IOException {

        ViabilityData data = new ViabilityData(resources, adultOnly, null);

        return data.getViabilityCategories();
    }

    public ViabilityData getViabilityData(List<String> resources, Boolean adultOnly, Integer maxRows) {

        ViabilityData data = new ViabilityData(resources, adultOnly, 1000000);

        return data;
    }
    public Set<ExperimentsDataTableRow> getAllPhenotypesFromObservationsByGeneAccession(String acc) throws IOException, SolrServerException {

        Long start = System.currentTimeMillis();
        List<ExperimentsDataTableRow> alleleZygParameterStableIdToRows = new ArrayList<>();
        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + acc + "\"")
                .setFields(ObservationDTO.ALLELE_SYMBOL,
                        ObservationDTO.ALLELE_ACCESSION_ID,
                        ObservationDTO.GENE_SYMBOL,
                        ObservationDTO.GENE_ACCESSION_ID,
                        ObservationDTO.PARAMETER_STABLE_ID,
                        ObservationDTO.PARAMETER_NAME,
                        ObservationDTO.PROCEDURE_STABLE_ID,
                        ObservationDTO.PROCEDURE_NAME,
                        ObservationDTO.PIPELINE_STABLE_ID,
                        ObservationDTO.PIPELINE_NAME,
                        ObservationDTO.ZYGOSITY,
                        ObservationDTO.PHENOTYPING_CENTER,
                        ObservationDTO.DEVELOPMENTAL_STAGE_NAME,
                        ObservationDTO.SEX
                )
                .setRows(100000);

        logger.info("get All Phenotypes for gene " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);
        logger.info("  Timing: Starting solr query: " + (System.currentTimeMillis() - start));
        final List<ObservationDTO> beans = experimentCore.query(query).getBeans(ObservationDTO.class);
        logger.info("  Timing: Ending solr query: " + (System.currentTimeMillis() - start));

        logger.info("  Timing: Starting collection: " + (System.currentTimeMillis() - start));
        // Key -> Sex -> List<ObservationDTO>
        final Map<ObservationDTO.CombinedObservationKey, Map<String, List<ObservationDTO>>> groups = beans.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(ObservationDTO::getCombinedKey, Collectors.groupingBy(ObservationDTO::getSex)));
        logger.info("  Timing: Ending collection: " + (System.currentTimeMillis() - start));

        logger.info("  Timing: Starting generate rows: " + (System.currentTimeMillis() - start));
        for (ObservationDTO.CombinedObservationKey key : groups.keySet()) {
            // Translate the key into a ExperimentDataTableRow
            ExperimentsDataTableRow row = new ExperimentsDataTableRow(key);
            Integer femaleCount = (groups.get(key).get(SexType.female.getName())!= null) ?groups.get(key).get(SexType.female.getName()).size() : 0;
            Integer maleCount = (groups.get(key).get(SexType.male.getName())!= null) ?groups.get(key).get(SexType.male.getName()).size() : 0;
            row.setFemaleMutantCount(femaleCount);
            row.setMaleMutantCount(maleCount);
            alleleZygParameterStableIdToRows.add(row);
        }
        logger.info("  Timing: Ending generate rows: " + (System.currentTimeMillis() - start));

        return new HashSet<>(alleleZygParameterStableIdToRows);
    }


    public class ViabilityData {

        private SolrQuery          query;
        private QueryResponse      response;


        public ViabilityData(List<String> resources, Boolean adultOnly, Integer maxRows) {
            query = buildViabilityQuery(resources, adultOnly, maxRows);

            logger.info("ViabilityData Url: " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);
        }

        public List<ObservationDTO> getData() {

            List<ObservationDTO> list = new ArrayList<>();

            try {
                if (response == null) {
                    response = experimentCore.query(query);
                }

                SolrDocumentList docs = response.getResults();
                for (SolrDocument doc : docs) {

                    String geneSymbol = (doc.getFieldValue(ObservationDTO.GENE_SYMBOL) == null ? "" : doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString());
                    String geneAccessionId = (doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID) == null ? "" : doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString());
                    String colonyId = (doc.getFieldValue(ObservationDTO.COLONY_ID) == null ? "" : doc.getFieldValue(ObservationDTO.COLONY_ID).toString());
                    String phenotypingCenter = (doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER) == null ? "" : doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER).toString());
                    String sex = (doc.getFieldValue(ObservationDTO.SEX) == null ? "" : doc.getFieldValue(ObservationDTO.SEX).toString());
                    String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
                    String[] parts = category.split("-");
                    String zygosity = (((parts != null) && (parts.length > 0)) ? parts[0] : "");
                    String phenotype = (((parts != null) && (parts.length > 1)) ? parts[1] : "");
                    String alleleSymbol = (doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL) == null ? "" : doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL).toString());
                    Object alleleAccessionValue = doc.getFieldValue(ObservationDTO.ALLELE_ACCESSION_ID);
                    String alleleAccession = ((alleleAccessionValue != null) && (alleleAccessionValue.toString().trim().toUpperCase().startsWith("MGI:")) ? alleleAccessionValue.toString().trim() : "-");

                    ObservationDTO observationDTO = new ObservationDTO();
                    observationDTO.setGeneSymbol(geneSymbol);
                    observationDTO.setGeneAccession(geneAccessionId);
                    observationDTO.setPhenotypingCenter(phenotypingCenter);
                    observationDTO.setColonyId(colonyId);
                    observationDTO.setSex(sex);
                    observationDTO.setZygosity(zygosity);
                    observationDTO.setCategory(phenotype);
                    observationDTO.setAlleleSymbol(alleleSymbol);
                    observationDTO.setAlleleAccession(alleleAccession);

                    list.add(observationDTO);
                }

            } catch (SolrServerException | IOException e) {
                e.printStackTrace();
            }

            return list;
        }

        public Map<String, Set<String>> getViabilityCategories() throws SolrServerException, IOException {

            Map<String, Set<String>> results = new HashMap<>();

            try {

                String pivot = ObservationDTO.CATEGORY + "," + ObservationDTO.GENE_SYMBOL;
                if (response == null) {
                    response = experimentCore.query(query);
                }
                Map<String, List<String>> facets = getFacetPivotResults(response, pivot);

                for (String category : facets.keySet()) {
                    results.put(category, new HashSet<>(facets.get(category).subList(0, facets.get(category).size())));
                }

            } catch (SolrServerException | IOException e) {
                e.printStackTrace();
            }

            return results;
        }


        // PRIVATE METHODS


        private SolrQuery buildViabilityQuery(List<String> resources, boolean adultOnly, Integer maxRows) {

            SolrQuery query = new SolrQuery();
            HashMap<String, Set<String>> res = new HashMap<>();
            String pivot = ObservationDTO.CATEGORY + "," + ObservationDTO.GENE_SYMBOL;

            if (resources != null) {
                query.setFilterQueries(ObservationDTO.DATASOURCE_NAME + ":"
                        + StringUtils.join(resources, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
            }
            if (adultOnly){
                query.setQuery(Constants.adultViabilityParameters.stream().collect(Collectors.joining(" OR ", ObservationDTO.PARAMETER_STABLE_ID + ":(", ")")));
            } else {
                query.setQuery(Constants.viabilityParameters.stream().collect(Collectors.joining(" OR ", ObservationDTO.PARAMETER_STABLE_ID + ":(", ")")));
            }

            query.setRows((maxRows != null) && (maxRows > 0) ? maxRows : 0);
            query.setFacet(true);
            query.setFacetMinCount(1);
            query.setFacetLimit(-1);
            query.set("facet.pivot", pivot);

            if ((maxRows != null) && (maxRows > 0)){
                query.addField(ObservationDTO.GENE_SYMBOL);
                query.addField(ObservationDTO.GENE_ACCESSION_ID);
                query.addField(ObservationDTO.PHENOTYPING_CENTER);
                query.addField(ObservationDTO.COLONY_ID);
                query.addField(ObservationDTO.CATEGORY);
                query.addField(ObservationDTO.SEX);
                query.addField(ObservationDTO.ZYGOSITY);
                query.addField(ObservationDTO.ALLELE_SYMBOL);
                query.addField(ObservationDTO.ALLELE_ACCESSION_ID);
                query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
            }

            return query;
        }
    }

    /**
     * Returns a map of categories, faceted by the given pivot, indexed by category, comprising # Genes and Gene Symbols
     *
     * @param resources
     * @param parameterStableIds A list of parameter_stable_id values (e.g. IMPC_VIA_001_001)
     * @param pivot              A comma-separated string of solr fields to pivot the facet by (e.g. category,gene_symbol)
     * @return a map of categories, faceted by the given pivot, indexed by category, comprising # Genes and Gene Symbols
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getCategories(List<String> resources, List<String> parameterStableIds, String pivot) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();

        if ((resources != null) && (!resources.isEmpty())) {
            query.setFilterQueries(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resources, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        }
        if ((parameterStableIds != null) && (!parameterStableIds.isEmpty())) {
            query.setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + StringUtils.join(parameterStableIds, " OR " + ObservationDTO.PARAMETER_STABLE_ID + ":"));
        }
        query.setRows(0);
        query.setFacet(true);
        query.setFacetMinCount(1);
        query.setFacetLimit(-1);
        query.set("facet.pivot", pivot);

        logger.info("getCategories Url: " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return getFacetPivotResults(experimentCore.query(query), false);
    }

    /**
     * Returns a <code>QueryResponse</code> of data found using the given resources, parameter stable ids, and category
     * comprising geneSymbol, geneAccessionId, colonyId, and category.
     *
     * @param resources
     * @param parameterStableIds A list of parameter stable ids that is "or'd" together to produce the result (e.g. IMPC_VIA_001_001)
     * @param categories         A list of categories that is "or'd" together to produce the result (e.g. Viable, Lethal, Male, Fertile)
     * @return a <code>QueryResponse</code> of data found using the given resources, parameter stable ids, and category,
     * comprising geneSymbol, geneAccessionId, colonyId, and category.
     * @throws SolrServerException, IOException
     */
    public QueryResponse getData(List<String> resources, List<String> parameterStableIds, List<String> categories) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery();
        if ((resources != null) && (!resources.isEmpty())) {
            query.setFilterQueries(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resources, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        }
        if ((categories != null) && (!categories.isEmpty())) {
            query.setFilterQueries(ObservationDTO.CATEGORY + ":" + StringUtils.join(categories, " OR " + ObservationDTO.CATEGORY + ":"));
        }
        if ((parameterStableIds != null) && (!parameterStableIds.isEmpty())) {
            query.setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + StringUtils.join(parameterStableIds, " OR " + ObservationDTO.PARAMETER_STABLE_ID + ":"));
        }

        query.addField(ObservationDTO.GENE_SYMBOL);
        query.addField(ObservationDTO.GENE_ACCESSION_ID);
        query.addField(ObservationDTO.ALLELE_SYMBOL);
        query.addField(ObservationDTO.ALLELE_ACCESSION_ID);
        query.addField(ObservationDTO.PHENOTYPING_CENTER);
        query.addField(ObservationDTO.COLONY_ID);
        query.addField(ObservationDTO.CATEGORY);
        query.addField(ObservationDTO.SEX);
        query.addField(ObservationDTO.ZYGOSITY);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        query.setRows(1000000);

        logger.info("getData Url: " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return experimentCore.query(query);
    }

    public Map<String, Set<String>> getColoniesByPhenotypingCenter(List<String> resourceName, ZygosityType zygosity)
            throws SolrServerException, IOException, InterruptedException {

        Map<String, Set<String>> res = new HashMap<>();
        SolrQuery q = new SolrQuery();
        String pivotFacet = ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.COLONY_ID;
        NamedList<List<PivotField>> response;

        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        if (zygosity != null) {
            q.addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosity.name());
        }

        q.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        q.addFacetPivotField(pivotFacet);
        q.setFacet(true);
        q.setFacetLimit(-1);
        q.setFacetMinCount(1);
        q.setRows(0);

        try {
            response = experimentCore.query(q).getFacetPivot();
            for (PivotField genePivot : response.get(pivotFacet)) {
                if (genePivot.getPivot() != null){
                    String center = genePivot.getValue().toString();
                    HashSet<String> colonies = new HashSet<>();
                    for (PivotField f : genePivot.getPivot()) {
                        colonies.add(f.getValue().toString());
                    }
                    res.put(center, colonies);
                }
            }
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * for testing - not for users
     *
     * @param start
     * @param length
     * @param type
     * @param parameterIds
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws SQLException
     */
    public List<Map<String, String>> getLinksListForStats(Integer start, Integer length, ObservationType type, List<String> parameterIds)
            throws IOException, URISyntaxException, JSONException {

        if (start == null) {
            start = 0;
        }
        if (length == null) {
            length = 100;
        }

        String url = SolrUtils.getBaseURL(experimentCore) + "/select?" + "q=" + ObservationDTO.OBSERVATION_TYPE + ":" + type + " AND "
        + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental" + "&wt=json&indent=true&start=" + start + "&rows=" + length;

        JSONObject result       = JSONRestUtil.getResults(url);
        JSONArray  resultsArray = JSONRestUtil.getDocArray(result);

        List<Map<String, String>> listWithStableId = new ArrayList<>();
        for (int i = 0; i < resultsArray.length(); i++) {
            Map<String, String> map = new HashMap<>();
            JSONObject exp = resultsArray.getJSONObject(i);
            String statbleParamId = exp.getString(ObservationDTO.PARAMETER_STABLE_ID);
            String accession = exp.getString(ObservationDTO.GENE_ACCESSION_ID);
            map.put("paramStableId", statbleParamId);
            map.put("accession", accession);
            listWithStableId.add(map);
        }
        return listWithStableId;
    }

    /**
     * construct a query to get all observations for a given combination of
     * pipeline, parameter, gene, zygosity, organisation and strain
     *
     * @param parameterId
     * @param geneAcc
     * @param zygosity
     * @param organisationId
     * @param strain
     * @param sex
     * @return
     * @throws SolrServerException, IOException
     */
    public SolrQuery getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain, String sex)
            throws SolrServerException, IOException {

        return new SolrQuery().setQuery("((" + ObservationDTO.GENE_ACCESSION_ID + ":" + geneAcc.replace(":", "\\:") + " AND " + ObservationDTO.ZYGOSITY + ":" + zygosity + ") OR " +
        ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control) ").addFilterQuery(ObservationDTO.PARAMETER_ID +
        ":" + parameterId).addFilterQuery(ObservationDTO.PHENOTYPING_CENTER_ID + ":" + organisationId).addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:"))
        .addFilterQuery(ObservationDTO.SEX + ":" + sex).setStart(0).setRows(10000)
        .setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
    }

    /**
     * Return a list of a all data candidates for deletion prior to statistical
     * analysis
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctOrganisaionPipelineParameter()
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID);

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);
    }


    /**
     * Return a list of a all data candidates for deletion prior to statistical
     * analysis
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctStatisticalCandidates(List<String> phenotypingCenter, List<String> pipelineStableId, List<String> procedureStub, List<String> parameterStableId, List<String> alleleAccessionId)
            throws SolrServerException, IOException {

        String pivotFields = ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PROCEDURE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.OBSERVATION_TYPE;

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField(pivotFields);

        if (phenotypingCenter != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : phenotypingCenter) {
                toJoin.add(ObservationDTO.PHENOTYPING_CENTER + ":" + c);
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (pipelineStableId != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : pipelineStableId) {
                toJoin.add(ObservationDTO.PIPELINE_STABLE_ID + ":" + c);
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (procedureStub != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : procedureStub) {
                toJoin.add(ObservationDTO.PROCEDURE_STABLE_ID + ":" + c + "*");
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (parameterStableId != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : parameterStableId) {
                toJoin.add(ObservationDTO.PARAMETER_STABLE_ID + ":" + c);
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (alleleAccessionId != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : alleleAccessionId) {
                toJoin.add(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + c + "\"");
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);
    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for a specific procedure
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByProcedure(String procedureStableId)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery().setQuery(ObservationDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);

    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for all specified procedures
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByProcedure(List<String> procedureStableIds)
            throws SolrServerException, IOException {

        // Build the SOLR query string
        String field = ObservationDTO.PROCEDURE_STABLE_ID;
        String q = (procedureStableIds.size() > 1) ? "(" + field + ":\"" + StringUtils.join(procedureStableIds.toArray(), "\" OR " + field + ":\"") + "\")" : field + ":\"" + procedureStableIds.get(0) + "\"";

        SolrQuery query = new SolrQuery().setQuery(q).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);

    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for all specified parameter
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByParameter(List<String> parameterStableIds)
            throws SolrServerException, IOException {

        // Build the SOLR query string
        String field = ObservationDTO.PARAMETER_STABLE_ID;
        String q = (parameterStableIds.size() > 1) ? "(" + field + ":\"" + StringUtils.join(parameterStableIds.toArray(), "\" OR " + field + ":\"") + "\")" : field + ":\"" + parameterStableIds.get(0) + "\"";

        SolrQuery query = new SolrQuery().setQuery(q).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);

    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for a specific parameter
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByParameter(String parameterStableId)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery().setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at least 2 fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);

    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata()
            throws SolrServerException, IOException {

        List<Map<String, String>> candidates = new ArrayList<>();

        SolrQuery centersQuery = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetField(ObservationDTO.PROCEDURE_GROUP);

        logger.info(SolrUtils.getBaseURL(experimentCore) + "/select?" + centersQuery);

        QueryResponse centerResponse = experimentCore.query(centersQuery);
        List<FacetField> candidateSubsets = centerResponse.getFacetFields();

        for (FacetField ff : candidateSubsets) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                String candidateSubset = c.getName();
                SolrQuery query = new SolrQuery()
                        .setQuery("*:*")
                        .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                        .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional")
                        .addFilterQuery(ObservationDTO.PROCEDURE_GROUP + ":" + candidateSubset)
                        .setRows(0)
                        .setFacet(true)
                        .setFacetMinCount(1)
                        .setFacetLimit(-1)
                        .addFacetPivotField(StringUtils.join(Arrays.asList(ObservationDTO.PIPELINE_ID, ObservationDTO.PARAMETER_ID, ObservationDTO.STRAIN_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.METADATA_GROUP, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.GENE_ACCESSION_ID), ","));

                logger.info(SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

                QueryResponse response = experimentCore.query(query);

                List<Map<String, String>> centerCandidates = getFacetPivotResults(response, false);
                for (Map<String, String> centerCandidate : centerCandidates) {
                    centerCandidate.put(ObservationDTO.PROCEDURE_GROUP, candidateSubset);
                }

                candidates.addAll(centerCandidates);
            }
        }


        return candidates;

    }


    /**
     * Return a list of a all data candidates for statistical analysis
     *
     * @return list of maps of results
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata()
            throws SolrServerException, IOException {

        List<String> pivotFields = Arrays.asList(ObservationDTO.PHENOTYPING_CENTER_ID, ObservationDTO.PIPELINE_ID, ObservationDTO.PROCEDURE_GROUP, ObservationDTO.PARAMETER_ID, ObservationDTO.STRAIN_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.METADATA_GROUP, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.GENE_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":categorical")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(StringUtils.join(pivotFields, ","));

        QueryResponse response = experimentCore.query(query);
        logger.debug(" getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata: Solr query - {}", query.toString());
        logger.debug(" getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata: Num Solr documents - {}", response.getResults().getNumFound());

        return getFacetPivotResults(response, false);

    }


    public List<Map<String, String>> getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadataByParameter(String parameterStableId)
            throws SolrServerException, IOException {

        List<String> pivotFields = Arrays.asList(ObservationDTO.PHENOTYPING_CENTER_ID, ObservationDTO.PIPELINE_ID, ObservationDTO.PARAMETER_ID, ObservationDTO.STRAIN_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.METADATA_GROUP, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.GENE_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":categorical")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(StringUtils.join(pivotFields, ","));

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, false);

    }

    /**
     * buildQuery constructs the solr query to get the experiment back
     *
     *   At least gene and parameterStableId must not be null, everything else is optional
     *
     * @param parameterStableId NOT NULL
     * @param pipelineStableId null optional
     * @param gene NOT NULL
     * @param zygosities null optional
     * @param phenotypingCenter null optional
     * @param strain null optional
     * @param sex null optional
     * @param metaDataGroup null optional
     * @param alleleAccession null optional
     * @return
     */
    public SolrQuery buildQuery(String parameterStableId, String pipelineStableId, String gene, List<String> zygosities, String phenotypingCenter, String strain, SexType sex, String metaDataGroup, String alleleAccession){

        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.GENE_ACCESSION_ID + ":" + gene.replace(":", "\\:"))
                .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .setStart(0)
                .setRows(Integer.MAX_VALUE)
                .setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        if (pipelineStableId != null) {
            query.addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId);
        }

        if (zygosities != null && zygosities.size() > 0 && zygosities.size() != 3) {
            if (zygosities.size() == 2) {
                query.addFilterQuery(ObservationDTO.ZYGOSITY + ":(" + zygosities.get(0) + " OR " + zygosities.get(1) + ")");
            } else {
                if ( ! zygosities.get(0).equalsIgnoreCase("null")) {
                    query.addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosities.get(0));
                }
            }
        }
        if (strain != null) {
            query.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:"));
        }
        if (phenotypingCenter != null) {
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
        }
        if (sex != null) {
            query.addFilterQuery(ObservationDTO.SEX + ":" + sex);
        }
        if (metaDataGroup != null) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"" + metaDataGroup + "\"");
        }
        if (alleleAccession != null) {
            query.addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":" + alleleAccession.replace(":", "\\:"));
        }

        return query;

    }

    public List<ObservationDTO> getExperimentObservationsBy(String parameterStableId, String pipelineStableId, String gene, List<String> zygosities, String phenotypingCenter, String strain, SexType sex, String metaDataGroup, String alleleAccession)
    throws SolrServerException, IOException  {

        List<ObservationDTO> resultsDTO;
        SolrQuery query = buildQuery(parameterStableId, pipelineStableId, gene, zygosities, phenotypingCenter, strain, sex, metaDataGroup, alleleAccession);
        QueryResponse response = experimentCore.query(query);
        resultsDTO = response.getBeans(ObservationDTO.class);

        return resultsDTO;

    }


    public List<ObservationDTO> getViabilityData(String parameterStableId, String pipelineStableId, String gene, List<String> zygosities, String phenotypingCenter, String strain, SexType sex, String metaDataGroup, String alleleAccession)
    throws SolrServerException, IOException  {

        List<ObservationDTO> resultsDTO = null;
        SolrQuery query = buildQuery(parameterStableId, pipelineStableId, gene, zygosities, phenotypingCenter, strain, sex, metaDataGroup, alleleAccession);
        QueryResponse response = experimentCore.query(query);
        // Avoid calling this method if there are no results
        for(int i=0; i < response.getResults().getNumFound(); i++){
            resultsDTO = response.getBeans(ObservationDTO.class);
        }
        return resultsDTO;

    }


    /**
     * Return a list of a triplets of pipeline stable id, phenotyping center and
     * allele accession
     *
     *
     * @param genomicFeatureAcc a gene accession
     * @return list of triplets
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctPipelineAlleleCenterListByGeneAccession(String genomicFeatureAcc)
    throws SolrServerException, IOException  {

        List<Map<String, String>> results = new LinkedList<>();
        List<String> facetFields = Arrays.asList(ObservationDTO.PIPELINE_STABLE_ID, ObservationDTO.PIPELINE_NAME, ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.ALLELE_SYMBOL);

        SolrQuery query = new SolrQuery().setQuery("*:*")
                .addFilterQuery(ObservationDTO.GENE_ACCESSION_ID + ":" + "\"" + genomicFeatureAcc + "\"")
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(StringUtils.join(facetFields, ","));

        logger.debug("query for postQcdata="+query);
        QueryResponse response = experimentCore.query(query);

        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        if (facetPivot != null && facetPivot.size() > 0) {
            for (int i = 0; i < facetPivot.size(); i ++) {

                String name = facetPivot.getName(i); // in this case only one of
                // them
                logger.debug("facetPivot name" + name);
                List<PivotField> pivotResult = facetPivot.get(name);

                // iterate on results
                for (int j = 0; j < pivotResult.size(); j ++) {

					// create a HashMap to store a new triplet of data
                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, false);
                    results.addAll(lmap);
                }
            }
        }

        return results;
    }


    /**
     * Return a list of parameters measured for a particular pipeline, allele
     * and center combination. A list of filters (meaning restriction to some
     * specific procedures is passed).
     *
     * @param alleleAccession an allele accession
     * @return list of triplets
     * @throws SolrServerException, IOException
     */
    public List<Map<String, String>> getDistinctParameterListByPipelineAlleleCenter(String pipelineStableId, String alleleAccession, String phenotypingCenter, List<String> procedureFilters, List<String> resource)
    throws SolrServerException, IOException  {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
                .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
                .addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"");
        if (resource != null) {
            query.addFilterQuery("(" + ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resource, " OR " + ObservationDTO.DATASOURCE_NAME + ":") + ")");
        }

        int index = 0;
        if (procedureFilters != null && procedureFilters.size() > 0) {
            StringBuilder queryBuilder = new StringBuilder(ObservationDTO.PROCEDURE_STABLE_ID + ":(");

            for (String procedureFilter : procedureFilters) {
                if (index == 0) {
                    queryBuilder.append(procedureFilter);
                } else {
                    queryBuilder.append(" OR " + procedureFilter);
                }
                index ++;
            }
            queryBuilder.append(")");
            query.addFilterQuery(queryBuilder.toString());
        }

        query.setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField(ObservationDTO.PROCEDURE_STABLE_ID
                + "," + ObservationDTO.PROCEDURE_NAME + "," + ObservationDTO.PARAMETER_STABLE_ID + "," + ObservationDTO.PARAMETER_NAME
                + "," + ObservationDTO.OBSERVATION_TYPE + "," + ObservationDTO.ZYGOSITY);

        logger.info(SolrUtils.getBaseURL(experimentCore) + "/select?" + query.toString());
        QueryResponse response = experimentCore.query(query);
        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();
        List<Map<String, String>> results = new LinkedList<Map<String, String>>();

        if (facetPivot != null && facetPivot.size() > 0) {

            for (int i = 0; i < facetPivot.size(); i ++) {

                String name = facetPivot.getName(i);
                List<PivotField> pivotResult = facetPivot.get(name);

                for (int j = 0; j < pivotResult.size(); j ++) {

                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, false);
                    results.addAll(lmap);
                }

            }
        }

        return results;
    }


    /**
     * Return a list of procedures effectively performed given pipeline stable
     * id, phenotyping center and allele accession
     *
     * @param alleleAccession an allele accession
     * @return list of integer db keys of the parameter rows
     * @throws SolrServerException, IOException
     */
    public List<String> getDistinctProcedureListByPipelineAlleleCenter(String pipelineStableId, String alleleAccession, String phenotypingCenter)
    throws SolrServerException, IOException  {

        List<String> results = new LinkedList<String>();

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId).addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":" + phenotypingCenter).addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetField(ObservationDTO.PROCEDURE_STABLE_ID);

        QueryResponse response = experimentCore.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

			// If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                results.add(c.getName());
            }
        }

        return results;
    }


    // gets categorical data for graphs on phenotype page
    public Map<String, List<DiscreteTimePoint>> getTimeSeriesMutantData(String parameter, List<String> genes, List<String> strains, String[] center, String[] sex)
    throws SolrServerException, IOException  {

        Map<String, List<DiscreteTimePoint>> finalRes = new HashMap<String, List<DiscreteTimePoint>>(); // <allele_accession,
        // timeSeriesData>

        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter);

        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";

        if (genes != null && genes.size() > 0) {
            q += " AND (";
            q += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            q += ")";
        }

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null && sex.length == 1) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex[0] + "\"";
        }

        query.setQuery(q);
        query.set("group.field", ObservationDTO.GENE_SYMBOL);
        query.set("group", true);
        query.set("fl", ObservationDTO.DATA_POINT + "," + ObservationDTO.DISCRETE_POINT);
        query.set("group.limit", 100000); // number of documents to be returned
        // per group
        query.set("group.sort", ObservationDTO.DISCRETE_POINT + " asc");
        query.setRows(10000);

		// logger.info("+_+_+ " + SolrUtils.getBaseURL(experimentCore) + "/select?" +
        // query);
        List<Group> groups = experimentCore.query(query).getGroupResponse().getValues().get(0).getValues();
		// for mutants it doesn't seem we need binning
        // groups are the alleles
        for (Group gr : groups) {
            SolrDocumentList resDocs = gr.getResult();
            DescriptiveStatistics stats = new DescriptiveStatistics();
            float discreteTime = (float) resDocs.get(0).getFieldValue(ObservationDTO.DISCRETE_POINT);
            List<DiscreteTimePoint> res = new ArrayList<DiscreteTimePoint>();
            for (int i = 0; i < resDocs.getNumFound(); i ++) {
                SolrDocument doc = resDocs.get(i);
                stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                if (discreteTime != (float) doc.getFieldValue(ObservationDTO.DISCRETE_POINT) || i == resDocs.getNumFound() - 1) { // we
                    // are
                    // at
                    // the
                    // end
                    // of
                    // the
                    // document
                    // list
                    // add to list
                    float discreteDataPoint = (float) stats.getMean();
                    DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime, discreteDataPoint, new Float(stats.getStandardDeviation()));
                    List<Float> errorPair = new ArrayList<>();
                    Float lower = new Float(discreteDataPoint);
                    Float higher = new Float(discreteDataPoint);
                    errorPair.add(lower);
                    errorPair.add(higher);
                    dp.setErrorPair(errorPair);
                    res.add(dp);
                    // update discrete point
                    discreteTime = Float.valueOf(doc.getFieldValue(ObservationDTO.DISCRETE_POINT).toString());
                    // update stats
                    stats = new DescriptiveStatistics();
                }
            }
            // add list
            finalRes.put(gr.getGroupValue(), res);
        }
        return finalRes;
    }


    // gets categorical data for graphs on phenotype page
    public List<DiscreteTimePoint> getTimeSeriesControlData(String parameter, List<String> strains, String[] center, String[] sex)
    throws SolrServerException, IOException  {

        List<DiscreteTimePoint> res = new ArrayList<DiscreteTimePoint>();
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter);
        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null && sex.length == 1) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex[0] + "\"";
        }

        query.setQuery(q);
        query.set("group.field", ObservationDTO.DISCRETE_POINT);
        query.set("group", true);
        query.set("fl", ObservationDTO.DATA_POINT + "," + ObservationDTO.DISCRETE_POINT);
        query.set("group.limit", 100000); // number of documents to be returned
        // per group
        query.set("sort", ObservationDTO.DISCRETE_POINT + " asc");
        query.setRows(10000);

		// logger.info("+_+_+ " + SolrUtils.getBaseURL(solr) + "/select?" +
        // query);
        List<Group> groups = experimentCore.query(query).getGroupResponse().getValues().get(0).getValues();
        boolean rounding = false;
		// decide if binning is needed i.e. is the increment points are too
        // scattered, as for calorimetry
        if (groups.size() > 30) { // arbitrary value, just piced it because it
            // seems reasonable for the size of our
            // graphs
            if (Float.valueOf(groups.get(groups.size() - 1).getGroupValue()) - Float.valueOf(groups.get(0).getGroupValue()) <= 30) { // then
                // rounding
                // will
                // be
                // enough
                rounding = true;
            }
        }
        if (rounding) {
            int bin = Math.round(Float.valueOf(groups.get(0).getGroupValue()));
            for (Group gr : groups) {
                int discreteTime = Math.round(Float.valueOf(gr.getGroupValue()));
                // for calormetry ignore what's before -5 and after 16
                if (parameter.startsWith("IMPC_CAL") || parameter.startsWith("ESLIM_003_001") || parameter.startsWith("M-G-P_003_001")) {
                    if (discreteTime < -5) {
                        continue;
                    } else if (discreteTime > 16) {
                        break;
                    }
                }
                float sum = 0;
                SolrDocumentList resDocs = gr.getResult();
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (SolrDocument doc : resDocs) {
                    sum += (float) doc.getFieldValue(ObservationDTO.DATA_POINT);
                    stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                }
                if (bin < discreteTime || groups.indexOf(gr) == groups.size() - 1) { // finished
                    // the
                    // groups
                    // of
                    // filled
                    // the
                    // bin
                    float discreteDataPoint = sum / resDocs.getNumFound();
                    DiscreteTimePoint dp = new DiscreteTimePoint((float) discreteTime, discreteDataPoint, new Float(stats.getStandardDeviation()));
                    List<Float> errorPair = new ArrayList<>();
                    double std = stats.getStandardDeviation();
                    Float lower = new Float(discreteDataPoint - std);
                    Float higher = new Float(discreteDataPoint + std);
                    errorPair.add(lower);
                    errorPair.add(higher);
                    dp.setErrorPair(errorPair);
                    res.add(dp);
                    bin = discreteTime;
                }
            }
        } else {
            for (Group gr : groups) {
                Float discreteTime = Float.valueOf(gr.getGroupValue());
                float sum = 0;
                SolrDocumentList resDocs = gr.getResult();
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (SolrDocument doc : resDocs) {
                    sum += (float) doc.getFieldValue(ObservationDTO.DATA_POINT);
                    stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                }
                float discreteDataPoint = sum / resDocs.getNumFound();
                DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime, discreteDataPoint, new Float(stats.getStandardDeviation()));
                List<Float> errorPair = new ArrayList<>();
                double std = stats.getStandardDeviation();
                Float lower = new Float(discreteDataPoint - std);
                Float higher = new Float(discreteDataPoint + std);
                errorPair.add(lower);
                errorPair.add(higher);
                dp.setErrorPair(errorPair);
                res.add(dp);
            }
        }
        return res;
    }



    /**
     *
     * @param p
     * @param genes
     * @param strains
     * @param biologicalSample
     * @return list of centers and sexes for the given parameters
     * @throws SolrServerException, IOException
     */
    public Set<String> getCenters(Parameter p, List<String> genes, List<String> strains, String biologicalSample)
    throws SolrServerException, IOException  {

        Set<String> centers = new HashSet<String>();
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSample).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + p.getStableId());
        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";
        String fq = "";
        if (genes != null && genes.size() > 0) {
            fq += " (";
            fq += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            fq += ")";
        }
        query.addFilterQuery(fq);
        query.setQuery(q);
        query.setRows(100000000);
        query.setFields(ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.DATA_POINT);
        query.set("group", true);
        query.set("group.field", ObservationDTO.PHENOTYPING_CENTER);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        List<Group> groups = experimentCore.query(query, METHOD.POST).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            centers.add((String) gr.getGroupValue());
        }

        return centers;
    }

    // gets categorical data for graphs on phenotype page
    public CategoricalSet getCategories(Parameter parameter, List<String> genes, String biologicalSampleGroup, List<String> strains, String[] center, String[] sex)
    throws SolrServerException, IOException , SQLException {

        CategoricalSet resSet = new CategoricalSet();
        resSet.setName(biologicalSampleGroup);
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSampleGroup).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter.getStableId());

        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";

        if (genes != null && genes.size() > 0) {
            q += " AND (";
            q += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            q += ")";
        }

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null && sex.length == 1) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex[0] + "\"";
        }

        query.setQuery(q);
        query.set("group.field", ObservationDTO.CATEGORY);
        query.set("group", true);
        query.setRows(100);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        logger.info("URL in getCategories " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        QueryResponse res = experimentCore.query(query, METHOD.POST);

        List<Group> groups = res.getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            CategoricalDataObject catObj = new CategoricalDataObject();
            catObj.setCount((long) gr.getResult().getNumFound());
	        String catLabel = gr.getGroupValue();
            catObj.setCategory(catLabel);
            resSet.add(catObj);
        }
        return resSet;
    }


    /**
     * Get all controls for a specified set of center, strain, parameter,
     * (optional) sex, and metadata group.
     *
     * @param strain
     * @param experimentDate date of experiment
     * @param sex if null, both sexes are returned
     * @param metadataGroup when metadataGroup is empty string, force solr to
     * search for metadata_group:""
     * @return list of control observationDTOs that conform to the search
     * criteria
     * @throws SolrServerException, IOException
     */
    public List<ObservationDTO> getAllControlsBySex(String parameterStableId, String strain, String phenotypingCenter, Date experimentDate, String sex, String metadataGroup)
    throws SolrServerException, IOException  {

        List<ObservationDTO> results;

        QueryResponse response = new QueryResponse();

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control")
            .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId).addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:")).setStart(0).setRows(20000);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        if (phenotypingCenter != null) {
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
        }

        if (metadataGroup == null) {
            // Do nothing
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"\"");
        } else if ( ! parameterStableId.contains("BWT_008_001") ){
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":" + metadataGroup);
        }

        if (sex != null) {
            query.addFilterQuery(ObservationDTO.SEX + ":" + sex);
        }

		// Filter starting at 2000-01-01 and going through the end
        // of day on the experiment date
        if (experimentDate != null) {

			// Set time range to the last possible time on the day for SOLR
            // range query to include all observations on the same day
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.addDays(experimentDate, 1));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date maxDate = cal.getTime();

            Date beginning = new Date(946684800000L); // Start date (Jan 1 2000)
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String dateFilter = df.format(beginning) + "Z TO " + df.format(maxDate) + "Z";
            query.addFilterQuery(ObservationDTO.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]");
        }
        response = experimentCore.query(query);
        results = response.getBeans(ObservationDTO.class);
        logger.debug("getAllControlsBySex " + query);
        return results;
    }

    /**
     * Get all controls for a specified set of center, strain, parameter,
     * (optional) sex, and metadata group that occur on the same day as passed
     * in (or in WTSI case, the same week).
     *
     * @param strain
     * @param experimentDate the date of interest
     * @param sex if null, both sexes are returned
     * @param metadataGroup when metadataGroup is empty string, force solr to
     * search for metadata_group:""
     * @return list of control observationDTOs that conform to the search
     * criteria
     * @throws SolrServerException, IOException
     */
    public List<ObservationDTO> getConcurrentControlsBySex(String parameterStableId, String strain, String phenotypingCenter, Date experimentDate, String sex, String metadataGroup)
    throws SolrServerException, IOException  {

        List<ObservationDTO> results;

		// Use any control mouse ON THE SAME DATE as concurrent control
        // Set min and max time ranges to encompass the whole day
        Calendar cal = Calendar.getInstance();
        cal.setTime(experimentDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date minDate = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date maxDate = cal.getTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateFilter = df.format(minDate) + "Z TO " + df.format(maxDate) + "Z";

        QueryResponse response = new QueryResponse();

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control")
        		.addFilterQuery(ObservationDTO.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]")
        		.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
        		.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
        		.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:"))
        		.addFilterQuery(ObservationDTO.SEX + ":" + sex).setStart(0).setRows(5000).setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        if (metadataGroup == null) {
            // don't add a metadata group filter
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"\"");
        } else {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":" + metadataGroup);
        }

        response = experimentCore.query(query);
        results = response.getBeans(ObservationDTO.class);

        return results;
    }


    /**
     * Returns a collection of biological sample ids for all mice matching the PROCEDURE_STABLE_ID.
     *
     * @param procedureStableId the procedure stable id (e.g. "IMPC_CAL_*" or "IMPC_IPG_*")
     *
     * @return a collection of biological sample ids for all mice matching the PROCEDURE_STABLE_ID
     *
     * @throws SolrServerException, IOException
     */
    public Collection<String> getMetabolismReportBiologicalSampleIds(String procedureStableId)  throws SolrServerException, IOException  {
        SolrQuery query = new SolrQuery();

        query.setQuery(String.format("%s:%s", ObservationDTO.PROCEDURE_STABLE_ID, procedureStableId));
        query.setRows(0);
        query.setFacetMinCount(1);
        query.setFacetLimit(100000);
        query.addFacetField(ObservationDTO.BIOLOGICAL_SAMPLE_ID);

        logger.info(SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return getFacets(experimentCore.query(query)).get(ObservationDTO.BIOLOGICAL_SAMPLE_ID).keySet();
    }


    /**
     * Returns a list of <code>ObservationDTO</code> observations for the specified procedureStableId and biologicalSampleId.
          *
     * @param procedureStableId the procedure stable id (e.g. "IMPC_CAL_*" or "IMPC_IPP_*")
     * @param biologicalSampleId the biological sample id (mouse id) of the desired mouse
     *
     * @return a list of <code>ObservationDTO</code> calorimetry results for the specified mouse.
     *
     * @throws SolrServerException, IOException
     */
    public List<ObservationDTO> getMetabolismReportBiologicalSampleId(String procedureStableId, Integer biologicalSampleId) throws SolrServerException, IOException  {
        SolrQuery query = new SolrQuery();

        query.setFields(
                ObservationDTO.ALLELE_ACCESSION_ID,
                ObservationDTO.ALLELE_SYMBOL,
                ObservationDTO.BIOLOGICAL_SAMPLE_GROUP,
                ObservationDTO.BIOLOGICAL_SAMPLE_ID,
                ObservationDTO.COLONY_ID,
                ObservationDTO.DATA_POINT,
                ObservationDTO.DATE_OF_EXPERIMENT,
                ObservationDTO.DISCRETE_POINT,
                ObservationDTO.EXTERNAL_SAMPLE_ID,
                ObservationDTO.GENE_ACCESSION_ID,
                ObservationDTO.GENE_SYMBOL,
                ObservationDTO.METADATA,
                ObservationDTO.METADATA_GROUP,
                ObservationDTO.OBSERVATION_TYPE,
                ObservationDTO.PARAMETER_STABLE_ID,
                ObservationDTO.PHENOTYPING_CENTER,
                ObservationDTO.PROCEDURE_STABLE_ID,
                ObservationDTO.SEX,
                ObservationDTO.TIME_POINT,
                ObservationDTO.WEIGHT,
                ObservationDTO.ZYGOSITY);
        query.setRows(5000);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        query.setFilterQueries(ObservationDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId);
        query.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_ID + ":" + biologicalSampleId);

        return experimentCore.query(query).getBeans(ObservationDTO.class);
    }


    public Set<String> getAllGeneIdsByResource(List<String> resources, boolean experimentalOnly) throws IOException, SolrServerException {

        Set<String> geneAccessionIds = new HashSet<>();

        for( String resource : resources) {

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + resource);

            if (experimentalOnly) {
                solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + BiologicalSampleType.experimental);
            }

            solrQuery.addField(ObservationDTO.GENE_ACCESSION_ID);
            solrQuery.setRows(Integer.MAX_VALUE);
            solrQuery.add("group", "true")
                    .add("group.field", ObservationDTO.GENE_ACCESSION_ID)
                    .add("group.limit", Integer.toString(1))
            .add("group.main", "true");

            logger.info("associated gene accession id solr query: " + solrQuery);
            geneAccessionIds.addAll(experimentCore.query(solrQuery).getBeans(ObservationDTO.class).stream().map(ObservationDTOBase::getGeneAccession).collect(Collectors.toSet()));

        }

        return geneAccessionIds;
    }


    public Set<String> getAllColonyIdsByResource(List<String> resources, boolean experimentalOnly) throws IOException, SolrServerException {

        Set<String> colonyIds = new HashSet<>();

        for( String resource : resources) {

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + resource);

            if (experimentalOnly) {
                solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + BiologicalSampleType.experimental);
            }

            solrQuery.addField(ObservationDTO.COLONY_ID);
            solrQuery.setRows(Integer.MAX_VALUE);
            solrQuery.add("group", "true")
                    .add("group.field", ObservationDTO.COLONY_ID)
                    .add("group.limit", Integer.toString(1))
                    .add("group.main", "true");

            logger.info("associated colony id solr query: " + solrQuery);
            colonyIds.addAll(experimentCore.query(solrQuery).getBeans(ObservationDTO.class).stream().map(ObservationDTOBase::getColonyId).collect(Collectors.toSet()));

        }

        return colonyIds;
    }


    public DataBatchesBySex getExperimentalBatches(String phenotypingCenter, String pipelineStableId, String parameterStableId, String strainAccessionId, String zygosity, String metadataGroup, String alleleAccessionId)
    throws SolrServerException, IOException  {

        SolrQuery q = new SolrQuery()
                .setQuery("*:*")
                .setRows(10000)
                .setSort(ObservationDTO.ID, SolrQuery.ORDER.asc)
                .setFields(ObservationDTO.SEX, ObservationDTO.DATE_OF_EXPERIMENT)
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
                .addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
                .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strainAccessionId + "\"")
                .addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosity)
                .addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccessionId + "\"")
                .addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"" + metadataGroup + "\"");

        return new DataBatchesBySex(experimentCore.query(q).getBeans(ObservationDTO.class));
    }


    /**
     * Returns a list of <code>count</code> parameter stable ids matching <code>observationType</code>.
     *
     * @param observationType desired observation type
     * @param count the number of parameter stable ids to return
     *
     * @return a list of <code>count</code> parameter stable ids matching <code>observationType</code>.
     * @throws SolrServerException, IOException
     */
    public List<String> getParameterStableIdsByObservationType(ObservationType observationType, int count)
    throws SolrServerException, IOException  {

    	List<String> retVal = new ArrayList<String>();

        if (count < 1)
            return retVal;

        SolrQuery query = new SolrQuery();
        query.setQuery( ObservationDTO.OBSERVATION_TYPE + ":" + observationType.name())
            .addFacetField(ObservationDTO.PARAMETER_STABLE_ID)
            .setFacetMinCount(1)
            .setFacet(true)
            .setRows(count)
            .setSort(ObservationDTO.ID, SolrQuery.ORDER.asc)
            .set("facet.limit", count);

        QueryResponse response = experimentCore.query(query);
        for (Count facet: response.getFacetField(ObservationDTO.PARAMETER_STABLE_ID).getValues()) {
            retVal.add(facet.getName());
        }

        return retVal;
    }

    public class DataBatchesBySex {




    	private Set<String> maleBatches = new HashSet<>();
    	private Set<String> femaleBatches = new HashSet<>();

    	public DataBatchesBySex(List<ObservationDTO> observations) {
    		for (ObservationDTO obs : observations) {

    			if (obs.getSex().equals(SexType.male.toString())) {
    				maleBatches.add(obs.getDateOfExperimentString());
    			}

    			if (obs.getSex().equals(SexType.female.toString())) {
    				femaleBatches.add(obs.getDateOfExperimentString());
    			}

    		}
    	}

    	/*
    	    if male_batches == 0 or female_batches == 0:
    	        batch = "One sex only"
    	    elif both_batches == 1:
    	        batch = "One batch"
    	    elif both_batches <= 3:
    	        batch = "Low batch"
    	    elif male_batches >= 3 and female_batches >= 2:
    	        batch = "Multi batch"
    	    elif female_batches >= 3 and male_batches >= 2:
    	        batch = "Multi batch"
    	    else:
    	        batch = "Low batch"
    	 */
    	public BatchClassification getBatchClassification() {

    		logger.debug("Male batches by sex: " + StringUtils.join(maleBatches, ", "));
    		logger.debug("Femle batches by sex: " + StringUtils.join(femaleBatches, ", "));
    		logger.debug("Both batches by sex: " + StringUtils.join(CollectionUtils.union(maleBatches, femaleBatches), ", "));

    		if ((maleBatches.size()==0 && femaleBatches.size()>0) ||
    			(femaleBatches.size()==0 && maleBatches.size()>0) ) {
    			return BatchClassification.one_sex_only;

    		} else if ( CollectionUtils.union(maleBatches, femaleBatches).size() == 1 ) {
    			return BatchClassification.one_batch;

    		} else if ( CollectionUtils.union(maleBatches, femaleBatches).size() <= 3 ) {
    			return BatchClassification.low_batch;

    		} else if ( maleBatches.size() >=3 && femaleBatches.size() >= 2 ||
    			femaleBatches.size() >=3 && maleBatches.size() >= 2 ) {
    			return BatchClassification.multi_batch;
    		}

    		return BatchClassification.low_batch;
    	}

    }


    /**
     * @author tudose
     * @date 2015/07/08
     * @param alleleAccession
     * @param phenotypingCenter
     * @param resource
     * @return List of pipelines with data for the given parameters.
     * @throws SolrServerException, IOException
     */
    public List<ImpressBaseDTO> getPipelines(String alleleAccession, String phenotypingCenter, List<String> resource)
    throws SolrServerException, IOException {

    	List<ImpressBaseDTO> pipelines = new ArrayList<>();

    	SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"")
			.addField(ObservationDTO.PIPELINE_ID)
			.addField(ObservationDTO.PIPELINE_NAME)
			.addField(ObservationDTO.PIPELINE_STABLE_ID);
    	if (phenotypingCenter != null){
			query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
    	}
    	if ( resource != null){
    		query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":\"" + StringUtils.join(resource, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"");
    	}

		query.set("group", true);
		query.set("group.field", ObservationDTO.PIPELINE_STABLE_ID);
		query.setRows(10000);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
		query.set("group.limit", 1);

        logger.info("SOLR URL getPipelines " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        QueryResponse response = experimentCore.query(query);

		for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){

			SolrDocument doc = group.getResult().get(0);
			ImpressBaseDTO pipeline = new ImpressBaseDTO();
			pipeline.setId(Long.getLong(doc.getFirstValue(ObservationDTO.PIPELINE_ID).toString()));
			pipeline.setStableId(doc.getFirstValue(ObservationDTO.PIPELINE_STABLE_ID).toString());
			pipeline.setName(doc.getFirstValue(ObservationDTO.PIPELINE_NAME).toString());
			pipelines.add(pipeline);

		}

		return pipelines;
    }

    @Override
	public long getWebStatus() throws SolrServerException, IOException  {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

		QueryResponse response = experimentCore.query(query);
		return response.getResults().getNumFound();
	}



	@Override
	public String getServiceName(){
		return "Observation Service (experiment core)";
	}



	/**
	 * @author ilinca
	 * @since 2016/01/21
	 * @param map <viability category, number of genes in category>
	 * @return
	 */
	public List<CountTableRow> consolidateZygosities(Map<String, Set<String>> map){

		Map<String, Set<String>> res = new LinkedHashMap<>();
		List<CountTableRow> result = new ArrayList<>();

		// Consolidate by zygosities so that we show "subviable" in the table, not "hom-subviable" and "het-subviable"
		for (String key: map.keySet()){

			String tableKey = "subviable";
			if (key.toLowerCase().contains(tableKey)){
				if (res.containsKey(tableKey)){
					res.get(tableKey).addAll(map.get(key));
				} else {
					res.put(tableKey, new HashSet<String>(map.get(key)));
				}
			} else {
				tableKey = "viable";
				if (key.toLowerCase().contains(tableKey) && !key.contains("subviable")){
					if (res.containsKey(tableKey)){
						res.get(tableKey).addAll(map.get(key));
					} else {
						res.put(tableKey, new HashSet<String>(map.get(key)));
					}
				} else {
						tableKey = "lethal";
					if (key.toLowerCase().contains(tableKey)){
						if (res.containsKey(tableKey)){
							res.get(tableKey).addAll(map.get(key));
						} else {
							res.put(tableKey, new HashSet<String>(map.get(key)));
						}
					}
				}
			}
		}

		// Fill list of EmbryoTableRows so that it's easiest to access from jsp.
		for (String key: res.keySet()){
			CountTableRow row = new CountTableRow();
			row.setCategory(key);
			row.setCount(res.get(key).size());
			if (key.equalsIgnoreCase("lethal")){
				row.setMpId("MP:0011100");
			} else  if (key.equalsIgnoreCase("subviable")){
				row.setMpId("MP:0011110");
			} else {
				row.setMpId(null);
			}
			result.add(row);
		}
		return result;

	}

	public static Comparator<String> getComparatorForViabilityChart()	{
		Comparator<String> comp = new Comparator<String>(){
		    @Override
		    public int compare(String param1, String param2)
		    {
		    	if (param1.contains("- Viable") && !param2.contains("- Viable")){
					return -1;
				}
				if (param2.contains("- Viable") && !param1.contains("- Viable")){
					return 1;
				}
				if (param2.contains("- Lethal") && !param1.contains("- Lethal")){
					return 1;
				}
				if (param2.contains("- Lethal") && !param1.contains("- Lethal")){
					return 1;
				}
				return param1.compareTo(param2);
		    }
		};
		return comp;
	}

	/**
	 * @author ilinca
	 * @since 2016/01/28
	 * @param facets
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public Map<String, Long> getViabilityCategories(Map<String, Set<String>>facets) {

		Map<String, Long> res = new TreeMap<>(getComparatorForViabilityChart());
		for (String category : facets.keySet()){
			Long geneCount = new Long(facets.get(category).size());
			res.put(category, geneCount);
		}

		return res;
	}


	public List<ObservationDTO> getObservationsByProcedureNameAndGene(String procedureName, String geneAccession) throws SolrServerException, IOException  {
		SolrQuery q = new SolrQuery()
                .setQuery("*:*")
                .setRows(ROWLIMIT)
                .setSort(ObservationDTO.ID, SolrQuery.ORDER.asc)
                .addFilterQuery(ObservationDTO.PROCEDURE_NAME +":\""+ procedureName+"\"");
                if(geneAccession!=null) {
				q.addFilterQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + geneAccession + "\"");
                }

		logger.info("solr query in getObservationByProcedureNameAndGene="+q);
        return experimentCore.query(q).getBeans(ObservationDTO.class);

	}

    public NamedList<List<PivotField>> getHistopathGeneParameterNameCategoryPivots() throws SolrServerException, IOException  {

	    //http://ves-hx-d1.ebi.ac.uk:8986/solr/experiment/select?q=*:*&rows=0&sort=id+asc&fq=parameter_stable_id:*HIS*&facet=true&facet.pivot=gene_symbol,category&facet.limit=-1
        //we need the significance score only can't filter based on a parameter ids as all different for each anatomy but can do search for "Significance score " in parameter_name string
        SolrQuery q = new SolrQuery()
                .setQuery("*:*")
                .setRows(0)//we don't care about the observations themselfs so don't return them only which anatomy has significant Histopath data on which genes.
                .setSort(ObservationDTO.ID, SolrQuery.ORDER.asc)
                //.setFields(fields)
                .addFilterQuery("parameter_stable_id:*HIS*")
        .addFilterQuery("parameter_name:*Significance*");//.addFilterQuery("gene_symbol:Prkab1");
                //.addFilterQuery("category:Significant");
                //.addFilterQuery("category:Significant");//otherwise query takes too long
        q.setFacet(true);
        String pivotFacet = ObservationDTO.GENE_SYMBOL + "," +ObservationDTO.PARAMETER_NAME + "," + ObservationDTO.CATEGORY;

        q.add("facet.pivot", pivotFacet );
        q.setFacetLimit(-1);
        System.out.println("solr query in getObservationByProcedureNameAndGene="+q);
        return experimentCore.query(q).getFacetPivot();
    }
	
	/**
	 * Get stats for the baseline graphs on the phenotype pages for each parameter/center
	 * if phenotypingCenter is null just return all stats for the center otherwise filter on that center
	 */
    public List<FieldStatsInfo> getStatisticsForParameterFromCenter(String parameterStableId, String phenotypingCenter) throws SolrServerException, IOException {
        //http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=*:*&stats=true&stats.field=data_point&stats.facet=parameter_stable_id&rows=0&indent=true&fq=phenotyping_center:HMGU&fq=parameter_stable_id:IMPC_CBC_010_001
        //http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=*:*&stats=true&stats.field=data_point&stats.facet=phenotyping_center&rows=0&indent=true&fq=parameter_stable_id:IMPC_CBC_010_001
        logger.debug("calling getStats for baseline");
        SolrQuery query = new SolrQuery()
                .setQuery("*:*");
        query.setGetFieldStatistics(true);
        query.setGetFieldStatistics(ObservationDTO.DATA_POINT);
        query.setParam("stats.facet", ObservationDTO.PHENOTYPING_CENTER);
        query.setFacetLimit(-1);
        query.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control");
        if (parameterStableId != null) {
            query.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
        }

        if (phenotypingCenter != null) {
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
        }

        query.setRows(0);

        logger.debug("SOLR URL getPipelines " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        QueryResponse response = experimentCore.query(query);
        FieldStatsInfo statsInfo = response.getFieldStatsInfo().get(ObservationDTO.DATA_POINT);
        Map<String, List<FieldStatsInfo>> facetToStatsMap = statsInfo.getFacets();

        List<FieldStatsInfo> centerStatsList = null;
        //just get the first result as we only expect 1
        for (String facet : facetToStatsMap.keySet()) {
            centerStatsList = facetToStatsMap.get(facet);
        }

        return centerStatsList;

    }

    public Set<String> getChartPivots(String baseUrl, String acc, ParameterDTO parameter, List<String> pipelineStableIds, List<String> zyList, List<String> phenotypingCentersList,
			  List<String> strainsParams, List<String> metaDataGroup, List<String> alleleAccession) throws IOException, SolrServerException, URISyntaxException {

			SolrQuery query = new SolrQuery();
			query.setQuery("*:*");
			if (acc != null){
			query.addFilterQuery("gene_accession_id"+ ":\"" + acc + "\"");
			}
			if (parameter != null){
			query.addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameter.getStableId() );
			}
			if (pipelineStableIds != null && pipelineStableIds.size() > 0){
			query.addFilterQuery(pipelineStableIds.stream().collect(Collectors.joining(" OR ",  StatisticalResultDTO.PIPELINE_STABLE_ID + ":(", ")")));
			}
			if (zyList != null && zyList.size() > 0){
			query.addFilterQuery(zyList.stream().collect(Collectors.joining(" OR ",  StatisticalResultDTO.ZYGOSITY + ":(", ")")));
			}
			if (phenotypingCentersList != null && phenotypingCentersList.size() > 0){
			query.addFilterQuery(phenotypingCentersList.stream().collect(Collectors.joining("\" OR \"",  StatisticalResultDTO.PHENOTYPING_CENTER + ":(\"", "\")")));
			}
			if (strainsParams != null && strainsParams.size() > 0){
			query.addFilterQuery(strainsParams.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.STRAIN_ACCESSION_ID + ":(\"", "\")")));
			}
			if (metaDataGroup != null && metaDataGroup.size() > 0){
			query.addFilterQuery(metaDataGroup.stream().collect(Collectors.joining("\" OR \"",  StatisticalResultDTO.METADATA_GROUP + ":(\"", "\")")));
			}
			if (alleleAccession != null && alleleAccession.size() > 0){
			query.addFilterQuery(alleleAccession.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.ALLELE_ACCESSION_ID + ":(\"", "\")")));
			}
			query.setFacet(true);
			
			// If you add/change order of pivots, make sure you do the same in the for loops below
			String pivotFacet = StatisticalResultDTO.PIPELINE_STABLE_ID + "," +
				StatisticalResultDTO.ZYGOSITY + "," +
				StatisticalResultDTO.PHENOTYPING_CENTER + "," +
				StatisticalResultDTO.STRAIN_ACCESSION_ID + "," +
				StatisticalResultDTO.ALLELE_ACCESSION_ID;
			if (metaDataGroup != null  && metaDataGroup.size() > 0){
			pivotFacet += "," + StatisticalResultDTO.METADATA_GROUP;
			
			}
			query.add("facet.pivot", pivotFacet );
			
			query.setFacetLimit(-1);
			
			Set<String> resultParametersForCharts = new HashSet<>();
			NamedList<List<PivotField>> facetPivot = experimentCore.query(query).getFacetPivot();
			for( PivotField pivot : facetPivot.get(pivotFacet)){
			getParametersForChartFromPivot(pivot, baseUrl, resultParametersForCharts);
			}
			return resultParametersForCharts;
}
	
	private Set<String> getParametersForChartFromPivot(PivotField pivot, String urlParams, Set<String> set){

		if ( pivot != null){
			urlParams += pivot.getField() + "=" + pivot.getValue().toString() + "&";
			if (pivot.getPivot() != null) {
				for (PivotField p : pivot.getPivot()) {
					getParametersForChartFromPivot(p, urlParams, set);
				}
			} else {
				set.add(urlParams);
			}
		}

		return set;
	}
}
