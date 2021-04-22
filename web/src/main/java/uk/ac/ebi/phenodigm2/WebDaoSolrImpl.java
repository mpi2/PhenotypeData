/*
 * Copyright © 2017 QMUL - Queen Mary University of London 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenodigm2;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Implementation of Phenodigm2 WebDao using Solr as the data source. See WebDao
 * for description of the functions.
 *
 */
@Repository
public class WebDaoSolrImpl implements WebDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDaoSolrImpl.class);

    // large row number as a proxy for retrieving all matching rows
    private static final int ROWLIMIT = 100000;

    // This autowired qualifier connects this object to the solr core
    @Autowired
    @Qualifier("phenodigmCore")
    private HttpSolrClient phenodigmCore;

    @Override
    public Disease getDisease(String diseaseId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(PhenodigmDTO.TYPE + ":disease")
                .addField(PhenodigmDTO.DISEASE_ID)
                .addField(PhenodigmDTO.DISEASE_TERM)
                .addField(PhenodigmDTO.DISEASE_ALTS)
                .addField(PhenodigmDTO.DISEASE_CLASSES)
                .addField(PhenodigmDTO.DISEASE_PHENOTYPES)
                .setRows(ROWLIMIT);

        Disease disease = null;
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            if (results.isEmpty()) {
                LOGGER.info("Query for disease {} returned empty.", diseaseId);
                return null;
            }

            PhenodigmDTO phenodigm = results.get(0);
            disease = new Disease(diseaseId);
            disease.setTerm(phenodigm.getDiseaseTerm());
            disease.setAlts(phenodigm.getDiseaseAlts());
            disease.setClasses(phenodigm.getDiseaseClasses());
            disease.parsePhenotypes(phenodigm.getDiseasePhenotypes());

        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return disease;
    }

    @Override
    public List<Phenotype> getDiseasePhenotypes(String diseaseId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(PhenodigmDTO.TYPE + ":disease")
                .addField(PhenodigmDTO.DISEASE_PHENOTYPES)
                .setRows(ROWLIMIT);

        List<Phenotype> phenotypeList = new ArrayList<>();
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);

            if (results.isEmpty()) {
                LOGGER.info("Query for disease {} phenotypes was not found.", diseaseId);
                return phenotypeList;
            }
            PhenodigmDTO phenodigm = results.get(0);
            for (String phenotype : phenodigm.getDiseasePhenotypes()) {
                phenotypeList.add(new Phenotype(phenotype));
            }

        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return phenotypeList;
    }

    @Override
    public List<DiseaseGeneAssociation> getDiseaseToGeneAssociations(String diseaseId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addField(PhenodigmDTO.MARKER_SYMBOLS_WITHDRAWN)
                .addField(PhenodigmDTO.HGNC_GENE_SYMBOLS_WITHDRAWN)
                .addField(PhenodigmDTO.HGNC_GENE_LOCUS);
        completeDiseaseGeneQuery(solrQuery);

        List<DiseaseGeneAssociation> genes = new ArrayList<>();
        try {
            // to avoid transfering the same ids more than once, keep a hashset
            // duplicate ids can occur with many-to-one human-mouse gene mappings
            HashSet<String> seenIds = new HashSet();

            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            for (PhenodigmDTO phenodigm : results) {
                // set mouse genes (orthologs to human genes)
                String markerId = phenodigm.getMarkerId();
                String markerSymbol = phenodigm.getMarkerSymbol();
                if (markerId != null && !seenIds.contains(markerId)) {
                    DiseaseGeneAssociation assoc = new DiseaseGeneAssociation(markerId, markerSymbol, diseaseId);
                    assoc.setSymbolsWithdrawn(phenodigm.getMarkerSymbolsWithdrawn());
                    assoc.setByOrthology(true);
                    genes.add(assoc);
                    seenIds.add(markerId);
                }

                // set human genes (human annotations)
                String humanId = phenodigm.getHgncGeneId();
                String humanSymbol = phenodigm.getHgncGeneSymbol();
                if (humanId != null && !seenIds.contains(humanId)) {
                    DiseaseGeneAssociation assoc = new DiseaseGeneAssociation(humanId, humanSymbol, diseaseId);
                    assoc.setSymbolsWithdrawn(phenodigm.getHgncGeneSymbolsWithdrawn());
                    assoc.setLocus(phenodigm.getHgncGeneLocus());
                    assoc.setByOrthology(false);
                    genes.add(assoc);
                    seenIds.add(humanId);
                }

            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return genes;
    }

    @Override
    public List<GeneDiseaseAssociation> getGeneToDiseaseAssociations(String geneId) {

        String query = String.format("%s:\"%s\" OR %s:\"%s\"",
                PhenodigmDTO.MARKER_ID, geneId,
                PhenodigmDTO.HGNC_GENE_ID, geneId);
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addField(PhenodigmDTO.DISEASE_ID)
                .addField(PhenodigmDTO.DISEASE_TERM);
        completeDiseaseGeneQuery(solrQuery);

        List<GeneDiseaseAssociation> diseases = new ArrayList<>();
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);

            for (PhenodigmDTO phenodigm : results) {
                GeneDiseaseAssociation assoc = new GeneDiseaseAssociation(phenodigm.getDiseaseId(), geneId);
                assoc.setTerm(phenodigm.getDiseaseTerm());

                // set mouse genes (orthologs to human genes)
                String markerId = phenodigm.getMarkerId();
                if (markerId != null && geneId.equals(markerId)) {
                    assoc.setByOrthology(true);
                    //LOGGER.info("Found an association: " + assoc.toString());
                    diseases.add(assoc);
                }

                // set human genes (human annotations)
                String humanId = phenodigm.getHgncGeneId();
                if (humanId != null && geneId.equals(humanId)) {
                    assoc.setByOrthology(false);
                    //LOGGER.info("Found an association: " + assoc.toString());
                    diseases.add(assoc);
                }

            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return diseases;

    }

    /**
     * Augment a solrQuery with fields to fetch disease-model associations.
     *
     * @param solrQuery
     */
    private void completeDiseaseGeneQuery(SolrQuery solrQuery) {
        solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease_gene_summary")
                .addField(PhenodigmDTO.MARKER_ID)
                .addField(PhenodigmDTO.MARKER_SYMBOL)
                .addField(PhenodigmDTO.HGNC_GENE_ID)
                .addField(PhenodigmDTO.HGNC_GENE_SYMBOL)
                .addField(PhenodigmDTO.HGNC_GENE_LOCUS)
                .setRows(ROWLIMIT);
    }

    @Override
    public List<DiseaseModelAssociation> getDiseaseToModelModelAssociations(String diseaseId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query);
        completeDiseaseModelQuery(solrQuery);
        // add more fields about the gene involved
        solrQuery.addField(PhenodigmDTO.MARKER_SYMBOL)
                .addField(PhenodigmDTO.MARKER_NUM_MODELS);

        List<DiseaseModelAssociation> associations = new ArrayList<>();
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            for (PhenodigmDTO phenodigm : results) {
                DiseaseModelAssociation assoc = createBasicDMA(phenodigm);
                assoc.setMarkerId(phenodigm.getMarkerId());
                assoc.setMarkerSymbol(phenodigm.getMarkerSymbol());
                assoc.setMarkerNumModels(phenodigm.getMarkerNumModels());
                associations.add(assoc);
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return associations;
    }

    @Override
    public List<DiseaseModelAssociation> getGeneToDiseaseModelAssociations(String markerId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.MARKER_ID, markerId);
        SolrQuery solrQuery = new SolrQuery(query);
        completeDiseaseModelQuery(solrQuery);
        // add fields for details about the disease        
        solrQuery.addField(PhenodigmDTO.DISEASE_TERM);

        List<DiseaseModelAssociation> associations = new ArrayList<>();
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            for (PhenodigmDTO phenodigm : results) {
                DiseaseModelAssociation assoc = createBasicDMA(phenodigm);
                assoc.setMarkerId(phenodigm.getMarkerId());
                assoc.setDiseaseId(phenodigm.getDiseaseId());
                assoc.setDiseaseTerm(phenodigm.getDiseaseTerm());
                associations.add(assoc);
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return associations;
    }

    /**
     * Augment a solrQuery with fields to fetch disease-model associations.
     *
     * @param solrQuery
     */
    private void completeDiseaseModelQuery(SolrQuery solrQuery) {
        solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease_model_summary")                
                .addField(PhenodigmDTO.DISEASE_ID)
                .addField(PhenodigmDTO.MARKER_ID)
                .addField(PhenodigmDTO.MODEL_ID)
                .addField(PhenodigmDTO.MODEL_SOURCE)
                .addField(PhenodigmDTO.MODEL_DESCRIPTION)
                .addField(PhenodigmDTO.MODEL_GENETIC_BACKGROUND)                
                .addField(PhenodigmDTO.DISEASE_MODEL_AVG_RAW)
                .addField(PhenodigmDTO.DISEASE_MODEL_AVG_NORM)
                .addField(PhenodigmDTO.DISEASE_MODEL_MAX_RAW)
                .addField(PhenodigmDTO.DISEASE_MODEL_MAX_NORM)
                .addField(PhenodigmDTO.DISEASE_MATCHED_PHENOTYPES)
                .addField(PhenodigmDTO.MODEL_MATCHED_PHENOTYPES)
                .addSort(PhenodigmDTO.DISEASE_MODEL_MAX_NORM, SolrQuery.ORDER.desc)
                .setRows(ROWLIMIT);
    }

    /**
     * Create a basic DMA object from the fields defined in the
     * completeDiseaseToModelQuery
     *
     * @param phenodigm
     * @return
     */
    private DiseaseModelAssociation createBasicDMA(PhenodigmDTO phenodigm) {        
        DiseaseModelAssociation assoc = new DiseaseModelAssociation(phenodigm.getModelId(), phenodigm.getDiseaseId());
        assoc.setSource(phenodigm.getModelSource());
        assoc.setDescription(phenodigm.getModelDescription());
        assoc.setGeneticBackground(phenodigm.getModelGeneticBackground());
        assoc.setAvgNorm(phenodigm.getDiseaseModelAvgNorm());
        assoc.setAvgRaw(phenodigm.getDiseaseModelAvgRaw());
        assoc.setMaxNorm(phenodigm.getDiseaseModelMaxNorm());
        assoc.setMaxRaw(phenodigm.getDiseaseModelMaxRaw());
        assoc.setDiseaseMatchedPhenotypes(phenodigm.getDiseaseMatchedPhenotypes());
        assoc.setModelMatchedPhenotypes(phenodigm.getModelMatchedPhenotypes());
        return assoc;
    }

    @Override
    public List<DiseaseModelAssociation> getDiseaseModelDetails(String diseaseId, String markerId) {

        String query = String.format("%s:\"%s\" AND %s:\"%s\"",
                PhenodigmDTO.DISEASE_ID, diseaseId,
                PhenodigmDTO.MARKER_ID, markerId);
        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(PhenodigmDTO.TYPE + ":disease_model_summary")
                .addField(PhenodigmDTO.MODEL_ID)
                .addField(PhenodigmDTO.MODEL_SOURCE)
                .addField(PhenodigmDTO.MODEL_DESCRIPTION)
                .addField(PhenodigmDTO.MODEL_GENETIC_BACKGROUND)
                .addField(PhenodigmDTO.MARKER_SYMBOL)
                .addField(PhenodigmDTO.DISEASE_MODEL_AVG_NORM)
                .addField(PhenodigmDTO.DISEASE_MODEL_MAX_NORM)
                .addSort(PhenodigmDTO.DISEASE_MODEL_MAX_NORM, SolrQuery.ORDER.desc)
                .setRows(ROWLIMIT);

        List<DiseaseModelAssociation> modelAssociations = new ArrayList<>();
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            for (PhenodigmDTO phenodigm : results) {
                DiseaseModelAssociation assoc = new DiseaseModelAssociation(phenodigm.getModelId(), diseaseId);
                assoc.setSource(phenodigm.getModelSource());
                assoc.setDescription(phenodigm.getModelDescription());
                assoc.setGeneticBackground(phenodigm.getModelGeneticBackground());
                assoc.setMarkerId(markerId);
                assoc.setMarkerSymbol(phenodigm.getMarkerSymbol());
                // assign the normalized scores (necessary to compute the overall model score)
                assoc.setAvgNorm(phenodigm.getDiseaseModelAvgNorm());
                assoc.setMaxNorm(phenodigm.getDiseaseModelMaxNorm());
                modelAssociations.add(assoc);
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return modelAssociations;
    }

    /**
     * Get details for all models that capture one gene
     *
     * @param markerId
     * @return
     */
    @Override
    public List<MouseModel> getGeneModelDetails(String markerId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.MARKER_ID, markerId);
        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(PhenodigmDTO.TYPE + ":mouse_model")
                .addField(PhenodigmDTO.MODEL_ID)
                .addField(PhenodigmDTO.MODEL_SOURCE)
                .addField(PhenodigmDTO.MODEL_DESCRIPTION)
                .addField(PhenodigmDTO.MODEL_GENETIC_BACKGROUND)
                .addField(PhenodigmDTO.MARKER_SYMBOL)
                .addField(PhenodigmDTO.MODEL_PHENOTYPES)
                .setRows(ROWLIMIT);

        List<MouseModel> result = new ArrayList<>();
        try {
            List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            for (PhenodigmDTO phenodigm : results) {
                MouseModel model = new MouseModel(phenodigm.getModelId());
                model.setSource(phenodigm.getModelSource());
                model.setDescription(phenodigm.getModelDescription());
                model.setGeneticBackground(phenodigm.getModelGeneticBackground());
                model.setMarkerId(markerId);
                model.setMarkerSymbol(phenodigm.getMarkerSymbol());
                model.parsePhenotypes(phenodigm.getModelPhenotypes());
                result.add(model);
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return result;
    }

}
