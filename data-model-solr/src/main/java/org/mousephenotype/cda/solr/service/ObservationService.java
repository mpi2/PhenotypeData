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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.dto.DiscreteTimePoint;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ObservationService extends BasicService implements WebStatus {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected SolrClient experimentCore;

    private static int ROWLIMIT = 100000;

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

    public Map<String, Map<String, Set<ObservationDTO>>> getDataPointsByColony(List<String> resourceName, String parameterStableId)
            throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        if (resourceName != null) {
            query.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            query.setQuery("*:*");
        }

        if (parameterStableId != null) {
            query.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
        }

        query.setFields(ObservationDTO.DATA_POINT, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.DATE_OF_EXPERIMENT,
                    ObservationDTO.ALLELE_SYMBOL, ObservationDTO.GENE_SYMBOL, ObservationDTO.COLONY_ID, ObservationDTO.ALLELE_ACCESSION_ID,
                    ObservationDTO.PIPELINE_STABLE_ID, ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.STRAIN_ACCESSION_ID,
                    ObservationDTO.STRAIN_NAME, ObservationDTO.PARAMETER_NAME, ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.PHENOTYPING_CENTER_ID,
                    ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
        query.setRows(Integer.MAX_VALUE);

        logger.info("Query: getDataPointsByColony() " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        List<ObservationDTO> dtoList =  experimentCore.query(query).getBeans(ObservationDTO.class);

        Map<String, Map<String, Set<ObservationDTO>>> dtoMap =
            dtoList
            .stream()
            .collect(
                Collectors.groupingBy(
                    ObservationDTO::getColonyId,
                    Collectors.groupingBy(
                        ObservationDTO::getGroup,
                        Collectors.mapping(
                            Function.identity(), Collectors.toSet()))));

        return dtoMap;
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
        query.setFields(
            ObservationDTO.DATASOURCE_NAME,
            ObservationDTO.GENE_SYMBOL,
            ObservationDTO.GENE_ACCESSION_ID,
            ObservationDTO.ZYGOSITY,
            ObservationDTO.PHENOTYPING_CENTER,
            ObservationDTO.CATEGORY);
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
        query.setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":(" + String.join(" OR ", Constants.adultViabilityParameters) + ")");
        query.setFilterQueries(ObservationDTO.GENE_ACCESSION_ID + ":\"" + acc + "\"");
        query.addField(ObservationDTO.GENE_SYMBOL);
        query.addField(ObservationDTO.GENE_ACCESSION_ID);
        query.addField(ObservationDTO.CATEGORY);
        query.addField(ObservationDTO.TEXT_VALUE);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        query.setRows(100000);

        logger.info("getViabilityForGene Url" + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        HashSet<String> viabilityCategories = new HashSet<>();

        final List<ObservationDTO> viabilityObservations = experimentCore.query(query).getBeans(ObservationDTO.class);
        for (ObservationDTO observation : viabilityObservations) {
            if (observation.getCategory() != null) {
                viabilityCategories.add(observation.getCategory());
            } else if (observation.getTextValue() != null) {
                String viaText = null;
                try {
                    viaText = new JSONObject(observation.getTextValue()).getString("outcome");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viabilityCategories.add(viaText);
            }
        }

        return viabilityCategories;
    }

    public Map<String, Set<String>> getViabilityCategories(List<String> resources, Boolean adultOnly) {

        ViabilityData data = new ViabilityData(resources, adultOnly, null);

        return data.getViabilityCategories();
    }

    public ViabilityData getViabilityData(List<String> resources, Boolean adultOnly) {
        return new ViabilityData(resources, adultOnly, 1000000);
    }

    public Set<ExperimentsDataTableRow> getAllPhenotypesFromObservationsByGeneAccession(String acc) throws IOException, SolrServerException {

        long start = System.currentTimeMillis();
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
                        ObservationDTO.SEX,
                        ObservationDTO.EXTERNAL_SAMPLE_ID
                )
                .setRows(100000);

        logger.info("get All Phenotypes for gene " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);
        logger.info("  Timing: Starting solr query: " + (System.currentTimeMillis() - start));
        final List<ObservationDTO> beans = experimentCore.query(query).getBeans(ObservationDTO.class);
        logger.info("  Timing: Ending solr query: " + (System.currentTimeMillis() - start));

        logger.info("  Timing: Starting collection: " + (System.currentTimeMillis() - start));
        // Key -> Sex -> List<ObservationDTO>
        final Map<CombinedObservationKey, Map<String, List<ObservationDTO>>> groups = beans.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        ObservationDTO::getCombinedKey,
                        Collectors.groupingBy(ObservationDTO::getSex)));
        logger.info("  Timing: Ending collection: " + (System.currentTimeMillis() - start));

        logger.info("  Timing: Starting generate rows: " + (System.currentTimeMillis() - start));
        for (CombinedObservationKey key : groups.keySet()) {
            // Translate the key into a ExperimentDataTableRow
            ExperimentsDataTableRow row = new ExperimentsDataTableRow(key);
            Integer femaleCount = (groups.get(key).get(SexType.female.getName())!= null) ?groups.get(key).get(SexType.female.getName()).stream().map(ObservationDTOBase::getExternalSampleId).collect(Collectors.toSet()).size() : 0;
            Integer maleCount = (groups.get(key).get(SexType.male.getName())!= null) ?groups.get(key).get(SexType.male.getName()).stream().map(ObservationDTOBase::getExternalSampleId).collect(Collectors.toSet()).size() : 0;
            row.setFemaleMutantCount(femaleCount);
            row.setMaleMutantCount(maleCount);
            alleleZygParameterStableIdToRows.add(row);
        }
        logger.info("  Timing: Ending generate rows: " + (System.currentTimeMillis() - start));

        return new HashSet<>(alleleZygParameterStableIdToRows);
    }


    public Integer getAllDataCount(String acc) throws IOException, SolrServerException {

        long start = System.currentTimeMillis();
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

        Set<CombinedObservationKey> rows = beans.stream().map(ObservationDTO::getCombinedKey).collect(Collectors.toSet());

        return rows.size();
    }

    public String getGeneAccFromAlleleAcc(String acc) throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + acc + "\"")
                .setFields(ObservationDTO.GENE_ACCESSION_ID)
                .setRows(1);
        List<ObservationDTO> beans = experimentCore.query(query).getBeans(ObservationDTO.class);
        if (beans.size() > 0) {
            return beans.get(0).getGeneAccession();
        }
        return null;
    }
    public String getStrainNameFromStrainAcc(String acc) throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + acc + "\"")
                .setFields(ObservationDTO.STRAIN_NAME)
                .setRows(1);
        List<ObservationDTO> beans = experimentCore.query(query).getBeans(ObservationDTO.class);
        if (beans.size() > 0) {
            return beans.get(0).getStrainName();
        }
        return null;
    }


    public class ViabilityData {

        private SolrQuery          query;
        private QueryResponse      response;


        public ViabilityData(List<String> resources, Boolean adultOnly, Integer maxRows) {
            query = buildViabilityQuery(resources, adultOnly, maxRows);

            logger.info("ViabilityData Url: " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);
        }

        public List<ObservationDTO> getViabilityReportData() {
            List<ObservationDTO> list = new ArrayList<>();
            try {
                if (response == null) {
                    response = experimentCore.query(query);
                }

                SolrDocumentList docs = response.getResults();
                for (SolrDocument doc : docs) {

                    String geneSymbol = (doc.getFieldValue(ObservationDTO.GENE_SYMBOL) == null ? "" : doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString());
                    String geneAccessionId = (doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID) == null ? "" : doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString());
                    String alleleSymbol = (doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL) == null ? "" : doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL).toString());
                    Object alleleAccessionValue = doc.getFieldValue(ObservationDTO.ALLELE_ACCESSION_ID);
                    String alleleAccession = ((alleleAccessionValue != null) && (alleleAccessionValue.toString().trim().toUpperCase().startsWith("MGI:")) ? alleleAccessionValue.toString().trim() : "-");
                    String strainName = (doc.getFieldValue(ObservationDTO.STRAIN_NAME) == null ? "" : doc.getFieldValue(ObservationDTO.STRAIN_NAME).toString());
                    String strainAccessionId = (doc.getFieldValue(ObservationDTO.STRAIN_ACCESSION_ID) == null ? "" : doc.getFieldValue(ObservationDTO.STRAIN_ACCESSION_ID).toString());
                    String colonyId = (doc.getFieldValue(ObservationDTO.COLONY_ID) == null ? "" : doc.getFieldValue(ObservationDTO.COLONY_ID).toString());
                    String phenotypingCenter = (doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER) == null ? "" : doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER).toString());
                    Object o = doc.getFieldValue(ObservationDTO.CATEGORY);
                    String category = (o == null ? "MISSING" : o.toString());
                    String[] parts = category.split("-");
                    String zygosity = parts.length > 0 ? parts[0] : "";
                    String sex = (doc.getFieldValue(ObservationDTO.SEX) == null ? "" : doc.getFieldValue(ObservationDTO.SEX).toString());
                    String phenotype = parts.length > 1 ? parts[1] : "";

                    ObservationDTO observationDTO = new ObservationDTO();
                    observationDTO.setGeneSymbol(geneSymbol);
                    observationDTO.setGeneAccession(geneAccessionId);
                    observationDTO.setAlleleSymbol(alleleSymbol);
                    observationDTO.setAlleleAccession(alleleAccession);
                    observationDTO.setStrainName(strainName);
                    observationDTO.setStrainAccessionId(strainAccessionId);
                    observationDTO.setColonyId(colonyId);
                    observationDTO.setPhenotypingCenter(phenotypingCenter);
                    observationDTO.setZygosity(zygosity);
                    observationDTO.setSex(sex);
                    observationDTO.setCategory(phenotype);

                    list.add(observationDTO);
                }

            } catch (SolrServerException | IOException e) {
                e.printStackTrace();
            }

            return list;
        }

        public Map<String, Set<String>> getViabilityCategories()  {

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
                query.addField(ObservationDTO.ALLELE_SYMBOL);
                query.addField(ObservationDTO.ALLELE_ACCESSION_ID);
                query.addField(ObservationDTO.STRAIN_NAME);
                query.addField(ObservationDTO.STRAIN_ACCESSION_ID);
                query.addField(ObservationDTO.COLONY_ID);
                query.addField(ObservationDTO.PHENOTYPING_CENTER);
                query.addField(ObservationDTO.ZYGOSITY);
                query.addField(ObservationDTO.SEX);
                query.addField(ObservationDTO.CATEGORY);
                query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
            }

            return query;
        }
    }

    /**
     * Returns a map of categories, faceted by the given pivot, indexed by category, comprising # Genes and Gene Symbols
     *
     * @param parameterStableIds A list of parameter_stable_id values (e.g. IMPC_VIA_001_001)
     * @param pivot              A comma-separated string of solr fields to pivot the facet by (e.g. category,gene_symbol)
     * @return a map of categories, faceted by the given pivot, indexed by category, comprising # Genes and Gene Symbols
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
        query.addField(ObservationDTO.STRAIN_NAME);
        query.addField(ObservationDTO.STRAIN_ACCESSION_ID);
        query.addField(ObservationDTO.PHENOTYPING_CENTER);
        query.addField(ObservationDTO.COLONY_ID);
        query.addField(ObservationDTO.CATEGORY);
        query.addField(ObservationDTO.SEX);
        query.addField(ObservationDTO.ZYGOSITY);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        query.setRows(Integer.MAX_VALUE);

        logger.info("getViabilityReportData Url: " + SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return experimentCore.query(query);
    }

    public Map<String, Map<String, Integer>> getDatapointsByPhenotypingCenterAndSampleGroup(List<String> resourceName) throws IOException, SolrServerException {

        SolrQuery q = new SolrQuery();
        String pivotFacet = ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP;

        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        q.addFacetPivotField(pivotFacet);
        q.setFacet(true);
        q.setFacetLimit(-1);
        q.setFacetMinCount(1);
        q.setRows(0);

        return getFacetPivotResultsKeepCount(experimentCore.query(q), pivotFacet);
    }

    public Map<String, Set<String>> getColoniesByPhenotypingCenter(List<String> resourceName, ZygosityType zygosity) {

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
     */
    public List<Map<String, String>> getLinksListForStats(Integer start, Integer length, ObservationType type)
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
     * buildQuery constructs the solr query to get the experiment back
     *
     *   At least gene and parameterStableId must not be null, everything else is optional
     *
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
     * Return a map of center to pipeline stable ids
     */
    public Map<String, List<String>> getPipelineByCenter(List<String> resourceName)
            throws SolrServerException, IOException  {

        final String pivot = StringUtils.join(Arrays.asList(ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.PIPELINE_STABLE_ID), ",");

        SolrQuery query = new SolrQuery()
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(pivot);

        if (resourceName != null){
            query.setQuery(ObservationDTO.DATASOURCE_NAME + ":(" + StringUtils.join(resourceName, " OR ") + ")");
        }else {
            query.setQuery("*:*");
        }

        QueryResponse response = experimentCore.query(query);

        return getFacetPivotResults(response, pivot);

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
                for (PivotField pivotLevel : pivotResult) {

                    // create a HashMap to store a new triplet of data
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, false);
                    results.addAll(lmap);
                }
            }
        }

        return results;
    }

    public Set<String> getCenters(String parameterStableId, List<String> genes, List<String> strains, String biologicalSample)
            throws SolrServerException, IOException  {

        Set<String> centers = new HashSet<>();
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSample).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";
        String fq = "";
        if (genes != null && genes.size() > 0) {
            fq += " (";
            fq += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            fq += ")";
        }
        query.addFilterQuery(fq);
        query.setQuery(q);
        query.setRows(Integer.MAX_VALUE);
        query.setFields(ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.DATA_POINT);
        query.set("group", true);
        query.set("group.field", ObservationDTO.PHENOTYPING_CENTER);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        List<Group> groups = experimentCore.query(query, SolrRequest.METHOD.POST).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            centers.add(gr.getGroupValue());
        }

        return centers;
    }

    // gets categorical data for graphs on phenotype page
    public CategoricalSet getCategories(String parameterStableId, List<String> genes, String biologicalSampleGroup, List<String> strains, String[] center, String[] sex) throws IOException, SolrServerException {

        CategoricalSet resSet = new CategoricalSet();
        resSet.setName(biologicalSampleGroup);
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSampleGroup).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);

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

        QueryResponse res = experimentCore.query(query, SolrRequest.METHOD.POST);

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


    // gets categorical data for graphs on phenotype page
    public Map<String, List<DiscreteTimePoint>> getTimeSeriesMutantData(String parameterStableId, List<String> genes, List<String> strains, String[] center, String[] sex)
            throws SolrServerException, IOException  {

        Map<String, List<DiscreteTimePoint>> finalRes = new HashMap<>(); // <allele_accession, timeSeriesData>

        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);

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
        query.set("group.sort", ObservationDTO.DISCRETE_POINT + " asc");
        query.setRows(10000);

        List<Group> groups = experimentCore.query(query).getGroupResponse().getValues().get(0).getValues();

        for (Group gr : groups) {
            SolrDocumentList resDocs = gr.getResult();
            DescriptiveStatistics stats = new DescriptiveStatistics();
            float discreteTime = (float) resDocs.get(0).getFieldValue(ObservationDTO.DISCRETE_POINT);
            List<DiscreteTimePoint> res = new ArrayList<>();
            for (int i = 0; i < resDocs.getNumFound(); i ++) {
                SolrDocument doc = resDocs.get(i);
                stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                if (discreteTime != (float) doc.getFieldValue(ObservationDTO.DISCRETE_POINT) || i == resDocs.getNumFound() - 1) { // we
                    float discreteDataPoint = (float) stats.getMean();
                    DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime, discreteDataPoint, (float) stats.getStandardDeviation());
                    List<Float> errorPair = new ArrayList<>();
                    Float lower = discreteDataPoint;
                    Float higher = discreteDataPoint;
                    errorPair.add(lower);
                    errorPair.add(higher);
                    dp.setErrorPair(errorPair);
                    res.add(dp);
                    // update discrete point
                    discreteTime = Float.parseFloat(doc.getFieldValue(ObservationDTO.DISCRETE_POINT).toString());
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

        List<DiscreteTimePoint> res = new ArrayList<>();
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

        List<Group> groups = experimentCore.query(query).getGroupResponse().getValues().get(0).getValues();
        boolean rounding = false;
        // decide if binning is needed i.e. is the increment points are too
        // scattered, as for calorimetry
        if (groups.size() > 30) { // arbitrary value, just piced it because it
            // seems reasonable for the size of our
            // graphs
            if (Float.parseFloat(groups.get(groups.size() - 1).getGroupValue()) - Float.parseFloat(groups.get(0).getGroupValue()) <= 30) { // then
                // rounding
                // will
                // be
                // enough
                rounding = true;
            }
        }
        if (rounding) {
            int bin = Math.round(Float.parseFloat(groups.get(0).getGroupValue()));
            for (Group gr : groups) {
                int discreteTime = Math.round(Float.parseFloat(gr.getGroupValue()));
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
                    float discreteDataPoint = sum / resDocs.getNumFound();
                    DiscreteTimePoint dp = new DiscreteTimePoint((float) discreteTime, discreteDataPoint, (float) stats.getStandardDeviation());
                    List<Float> errorPair = new ArrayList<>();
                    double std = stats.getStandardDeviation();
                    Float lower = (float) (discreteDataPoint - std);
                    Float higher = (float) (discreteDataPoint + std);
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
                DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime, discreteDataPoint, (float) stats.getStandardDeviation());
                List<Float> errorPair = new ArrayList<>();
                double std = stats.getStandardDeviation();
                Float lower = (float) (discreteDataPoint - std);
                Float higher = (float) (discreteDataPoint + std);
                errorPair.add(lower);
                errorPair.add(higher);
                dp.setErrorPair(errorPair);
                res.add(dp);
            }
        }
        return res;
    }

    /**
     * Get all controls for a specified set of center, strain, parameter,
     * (optional) sex, and metadata group.
     *
     * @param strain background strain
     * @param experimentDate date of experiment
     * @param sex if null, both sexes are returned
     * @param metadataGroup when metadataGroup is empty string, force solr to
     * search for metadata_group:""
     * @return list of control observationDTOs that conform to the search
     * criteria
     */
    public List<ObservationDTO> getAllControlsBySex(String parameterStableId, String strain, String phenotypingCenter, Date experimentDate, String sex, String metadataGroup)
    throws SolrServerException, IOException  {

        List<ObservationDTO> results;

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

        QueryResponse response = experimentCore.query(query);
        results = response.getBeans(ObservationDTO.class);
        logger.debug("getAllControlsBySex " + query);
        return results;
    }

    /**
     * Get all controls for a specified set of center, strain, parameter,
     * (optional) sex, and metadata group that occur on the same day as passed
     * in (or in WTSI case, the same week).
     *
     * @param strain background strain
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

        QueryResponse response = experimentCore.query(query);
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
        query.addFacetField(ObservationDTO.SPECIMEN_ID);

        logger.info(SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return getFacets(experimentCore.query(query)).get(ObservationDTO.SPECIMEN_ID).keySet();
    }

    /**
     * Returns a Map that represents facets counts by observation_type:
     * observation_type =>
     *      CATEGORICAL => xxx
     *      UNIDIMENSIONAL => yyy
     *      ...
     */
    public Map<String, Long> getDataPointCountByType(List<String> resourceName) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery()
        .setQuery("*:*")
        .setRows(0)
        .setFacetMinCount(1)
        .setFacetLimit(Integer.MAX_VALUE)
        .addFacetField(ObservationDTO.OBSERVATION_TYPE);

        if (resourceName != null){
            query.setQuery(ObservationDTO.DATASOURCE_NAME + ":(" + StringUtils.join(resourceName, " OR ") + ")");
        }

        logger.debug(SolrUtils.getBaseURL(experimentCore) + "/select?" + query);

        return getFacets(experimentCore.query(query)).get(ObservationDTO.OBSERVATION_TYPE);
    }


    /**
     * Returns a list of <code>ObservationDTO</code> observations for the specified procedureStableId and biologicalSampleId.
          *
     * @param procedureStableId the procedure stable id (e.g. "IMPC_CAL_*" or "IMPC_IPP_*")
     * @param sampleId the biological sample id (mouse id) of the desired mouse
     *
     * @return a list of <code>ObservationDTO</code> calorimetry results for the specified mouse.
     *
     * @throws SolrServerException, IOException
     */
    public List<ObservationDTO> getMetabolismReportBiologicalSampleId(String procedureStableId, String sampleId) throws SolrServerException, IOException  {
        SolrQuery query = new SolrQuery();

        query.setFields(
                ObservationDTO.ALLELE_ACCESSION_ID,
                ObservationDTO.ALLELE_SYMBOL,
                ObservationDTO.BIOLOGICAL_SAMPLE_GROUP,
                ObservationDTO.SPECIMEN_ID,
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
                ObservationDTO.STRAIN_NAME,
                ObservationDTO.STRAIN_ACCESSION_ID,
                ObservationDTO.TIME_POINT,
                ObservationDTO.WEIGHT,
                ObservationDTO.ZYGOSITY);
        query.setRows(5000);
        query.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
        query.setFilterQueries(ObservationDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId);
        query.setQuery(ObservationDTO.SPECIMEN_ID + ":" + sampleId);

        return experimentCore.query(query).getBeans(ObservationDTO.class);
    }


    public Set<String> getAllGeneIdsByResource(List<String> resources, boolean experimentalOnly) throws IOException, SolrServerException {

        Set<String> geneAccessionIds = new HashSet<>();

        if (resources != null) {
            for (String resource : resources) {

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


    public Set<String> getAllAlleleSymbolsByResource(List<String> resources, boolean experimentalOnly) throws IOException, SolrServerException {

        Set<String> alleles = new HashSet<>();

        for( String resource : resources) {

            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + resource);

            if (experimentalOnly) {
                solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + BiologicalSampleType.experimental);
            }

            solrQuery.addField(ObservationDTO.ALLELE_SYMBOL);
            solrQuery.setRows(Integer.MAX_VALUE);
            solrQuery.add("group", "true")
                    .add("group.field", ObservationDTO.ALLELE_SYMBOL)
                    .add("group.limit", Integer.toString(1))
                    .add("group.main", "true");

            logger.info("associated colony id solr query: " + solrQuery);
            alleles.addAll(experimentCore.query(solrQuery).getBeans(ObservationDTO.class).stream().map(ObservationDTOBase::getAlleleSymbol).collect(Collectors.toSet()));

        }

        return alleles;
    }


    @Override
	public long getWebStatus() throws SolrServerException, IOException  {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		QueryResponse response = experimentCore.query(query);
		return response.getResults().getNumFound();
	}



	@Override
	public String getServiceName(){
		return "Observation Service (experiment core)";
	}



	/**
	 * @param map <viability category, number of genes in category>
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
					res.put(tableKey, new HashSet<>(map.get(key)));
				}
			} else {
				tableKey = "viable";
				if (key.toLowerCase().contains(tableKey) && !key.contains("subviable")){
					if (res.containsKey(tableKey)){
						res.get(tableKey).addAll(map.get(key));
					} else {
						res.put(tableKey, new HashSet<>(map.get(key)));
					}
				} else {
						tableKey = "lethal";
					if (key.toLowerCase().contains(tableKey)){
						if (res.containsKey(tableKey)){
							res.get(tableKey).addAll(map.get(key));
						} else {
							res.put(tableKey, new HashSet<>(map.get(key)));
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

	public static Comparator<String> getComparatorForViabilityChart() {
        return (param1, param2) -> {
            if (param1.contains("- Viable") && !param2.contains("- Viable")) {
                return -1;
            }
            if (param2.contains("- Viable") && !param1.contains("- Viable")) {
                return 1;
            }
            if (param2.contains("- Lethal") && !param1.contains("- Lethal")) {
                return 1;
            }
            return param1.compareTo(param2);
        };
    }

	public Map<String, Long> getViabilityCategories(Map<String, Set<String>>facets) {

		Map<String, Long> res = new TreeMap<>(getComparatorForViabilityChart());
		for (String category : facets.keySet()){
			Long geneCount = (long) facets.get(category).size();
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

    public Set<String> getChartPivots(String baseUrl, String acc, String parameter, List<String> pipelineStableIds, List<String> zyList, List<String> phenotypingCentersList,
			  List<String> strainsParams, List<String> metaDataGroup, List<String> alleleAccession, List<String> procedureStableIds) throws IOException, SolrServerException {

        String pivotFacet = StatisticalResultDTO.PIPELINE_STABLE_ID + "," +
                StatisticalResultDTO.ZYGOSITY + "," +
                StatisticalResultDTO.PHENOTYPING_CENTER + "," +
                StatisticalResultDTO.STRAIN_ACCESSION_ID + "," +
                StatisticalResultDTO.ALLELE_ACCESSION_ID + "," +
                StatisticalResultDTO.PROCEDURE_STABLE_ID;
        if (metaDataGroup != null && metaDataGroup.size() > 0) {
            pivotFacet += "," + StatisticalResultDTO.METADATA_GROUP;
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFacet(true);
        query.add("facet.pivot", pivotFacet);
        query.setFacetLimit(-1);

        if (acc != null) {
            query.addFilterQuery("gene_accession_id" + ":\"" + acc + "\"");
        }
        if (parameter != null) {
            query.addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameter);
        }
        if (pipelineStableIds != null && pipelineStableIds.size() > 0) {
            query.addFilterQuery(pipelineStableIds.stream().collect(Collectors.joining(" OR ", StatisticalResultDTO.PIPELINE_STABLE_ID + ":(", ")")));
        }
        if (zyList != null && zyList.size() > 0) {
            query.addFilterQuery(zyList.stream().collect(Collectors.joining(" OR ", StatisticalResultDTO.ZYGOSITY + ":(", ")")));
        }
        if (phenotypingCentersList != null && phenotypingCentersList.size() > 0) {
            query.addFilterQuery(phenotypingCentersList.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.PHENOTYPING_CENTER + ":(\"", "\")")));
        }
        if (strainsParams != null && strainsParams.size() > 0) {
            query.addFilterQuery(strainsParams.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.STRAIN_ACCESSION_ID + ":(\"", "\")")));
        }
        if (metaDataGroup != null && metaDataGroup.size() > 0) {
            query.addFilterQuery(metaDataGroup.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.METADATA_GROUP + ":(\"", "\")")));
        }
        if (alleleAccession != null && alleleAccession.size() > 0) {
            query.addFilterQuery(alleleAccession.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.ALLELE_ACCESSION_ID + ":(\"", "\")")));
        }
        //now need unique procedure stable ids since these are not lumped together anymore by stats pipeline? - JW
        if (procedureStableIds != null && procedureStableIds.size() > 0) {
            query.addFilterQuery(procedureStableIds.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.PROCEDURE_STABLE_ID + ":(\"", "\")")));
        }

        Set<String> resultParametersForCharts = new HashSet<>();
        NamedList<List<PivotField>> facetPivot = experimentCore.query(query).getFacetPivot();
        for (PivotField pivot : facetPivot.get(pivotFacet)) {
            getParametersForChartFromPivot(pivot, baseUrl, resultParametersForCharts);
        }
        return resultParametersForCharts;
    }
	
	private void getParametersForChartFromPivot(PivotField pivot, String urlParams, Set<String> set){

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

    }
}
