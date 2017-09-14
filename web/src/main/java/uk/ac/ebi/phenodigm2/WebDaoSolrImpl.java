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
import java.util.List;
import org.mousephenotype.cda.solr.service.dto.Phenodigm2DTO;

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
    @Qualifier("phenodigm2Core")
    private HttpSolrClient phenodigm2Core;

    @Override
    public Disease getDisease(String diseaseId) {

        String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(Phenodigm2DTO.TYPE + ":disease")
                .addField(Phenodigm2DTO.DISEASE_ID)
                .addField(Phenodigm2DTO.DISEASE_TERM)
                .addField(Phenodigm2DTO.DISEASE_ALTS)
                .addField(Phenodigm2DTO.DISEASE_LOCUS)
                .addField(Phenodigm2DTO.DISEASE_CLASSES)
                .addField(Phenodigm2DTO.DISEASE_PHENOTYPES)
                .setRows(ROWLIMIT);

        Disease disease = null;
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);
            if (results.isEmpty()) {
                LOGGER.info("Query for disease {} returned empty.", diseaseId);
                return null;
            }

            Phenodigm2DTO phenodigm = results.get(0);
            disease = new Disease(diseaseId);
            disease.setTerm(phenodigm.getDiseaseTerm());
            disease.setAlts(phenodigm.getDiseaseAlts());
            disease.setLocus(phenodigm.getDiseaseLocus());
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
                .addFilterQuery(Phenodigm2DTO.TYPE + ":disease")
                .addField(Phenodigm2DTO.DISEASE_PHENOTYPES)
                .setRows(ROWLIMIT);

        List<Phenotype> phenotypeList = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);

            if (results.isEmpty()) {
                LOGGER.info("Query for disease {} phenotypes was not found.", diseaseId);
                return phenotypeList;
            }
            Phenodigm2DTO phenodigm = results.get(0);
            for (String phenotype : phenodigm.getDiseasePhenotypes()) {
                phenotypeList.add(new Phenotype(phenotype));
            }

        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        return phenotypeList;
    }

    @Override
    public List<Gene> getDiseaseToGeneAssociations(String diseaseId) {

        String query = String.format("%s:\"%s\"", Phenodigm2DTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addField(Phenodigm2DTO.MARKER_SYMBOLS_WITHDRAWN)
                .addField(Phenodigm2DTO.HGNC_GENE_SYMBOLS_WITHDRAWN)
                .addField(Phenodigm2DTO.HGNC_GENE_LOCUS);
        completeDiseaseGeneQuery(solrQuery);

        List<Gene> genes = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);
            for (Phenodigm2DTO phenodigm : results) {
                // set mouse genes (orthologs to human genes)
                String markerId = phenodigm.getMarkerId();
                String markerSymbol = phenodigm.getMarkerSymbol();
                if (markerId != null) {
                    Gene assoc = new Gene(markerId, markerSymbol);
                    assoc.setSymbolsWithdrawn(phenodigm.getMarkerSymbolsWithdrawn());
                    assoc.setCurated(phenodigm.getAssociationCurated());
                    assoc.setOrtholog(true);
                    //LOGGER.info("Found an association: " + assoc.toString());
                    genes.add(assoc);
                }

                // set human genes (human annotations)
                String humanId = phenodigm.getHgncGeneId();
                String humanSymbol = phenodigm.getHgncGeneSymbol();
                if (humanId != null) {
                    Gene assoc = new Gene(humanId, humanSymbol);
                    assoc.setSymbolsWithdrawn(phenodigm.getHgncGeneSymbolsWithdrawn());
                    assoc.setCurated(phenodigm.getAssociationCurated());
                    assoc.setOrtholog(false);
                    //LOGGER.info("Found an association: " + assoc.toString());
                    genes.add(assoc);
                }

            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return genes;
    }

    @Override
    public List<Disease> getGeneToDiseaseAssociations(String geneId) {

        String query = String.format("%s:\"%s\" OR %s:\"%s\"",
                Phenodigm2DTO.MARKER_ID, geneId,
                Phenodigm2DTO.HGNC_GENE_ID, geneId);
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addField(Phenodigm2DTO.DISEASE_ID)
                .addField(Phenodigm2DTO.DISEASE_TERM);
        completeDiseaseGeneQuery(solrQuery);

        List<Disease> diseases = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);

            for (Phenodigm2DTO phenodigm : results) {
                Disease assoc = new Disease(phenodigm.getDiseaseId());
                assoc.setTerm(phenodigm.getDiseaseTerm());

                // set mouse genes (orthologs to human genes)
                String markerId = phenodigm.getMarkerId();
                if (markerId != null && geneId.equals(markerId)) {
                    assoc.setCurated(phenodigm.getAssociationCurated());
                    assoc.setOrtholog(true);
                    //LOGGER.info("Found an association: " + assoc.toString());
                    diseases.add(assoc);
                }

                // set human genes (human annotations)
                String humanId = phenodigm.getHgncGeneId();
                if (humanId != null && geneId.equals(humanId)) {
                    assoc.setCurated(phenodigm.getAssociationCurated());
                    assoc.setOrtholog(false);
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
        solrQuery.addFilterQuery(Phenodigm2DTO.TYPE + ":disease_gene_summary")
                .addField(Phenodigm2DTO.MARKER_ID)
                .addField(Phenodigm2DTO.MARKER_SYMBOL)
                .addField(Phenodigm2DTO.HGNC_GENE_ID)
                .addField(Phenodigm2DTO.HGNC_GENE_SYMBOL)
                .addField(Phenodigm2DTO.ASSOCIATION_CURATED)
                .setRows(ROWLIMIT);
    }

    @Override
    public List<DiseaseModelAssociation> getDiseaseToModelModelAssociations(String diseaseId) {

        String query = String.format("%s:\"%s\"", Phenodigm2DTO.DISEASE_ID, diseaseId);
        SolrQuery solrQuery = new SolrQuery(query);
        completeDiseaseModelQuery(solrQuery);
        // add more fields about the gene involved
        solrQuery.addField(Phenodigm2DTO.MARKER_ID)
                .addField(Phenodigm2DTO.MARKER_SYMBOL)
                .addField(Phenodigm2DTO.MARKER_LOCUS)
                .addField(Phenodigm2DTO.MARKER_NUM_MODELS);

        List<DiseaseModelAssociation> associations = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);
            for (Phenodigm2DTO phenodigm : results) {
                DiseaseModelAssociation assoc = createBasicDMA(phenodigm);
                assoc.setMarkerId(phenodigm.getMarkerId());
                assoc.setMarkerSymbol(phenodigm.getMarkerSymbol());
                assoc.setMarkerLocus(phenodigm.getMarkerLocus());
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

        String query = String.format("%s:\"%s\"", Phenodigm2DTO.MARKER_ID, markerId);
        SolrQuery solrQuery = new SolrQuery(query);
        completeDiseaseModelQuery(solrQuery);
        // add fields for details about the disease        
        solrQuery.addField(Phenodigm2DTO.DISEASE_ID)
                .addField(Phenodigm2DTO.DISEASE_TERM);

        List<DiseaseModelAssociation> associations = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);
            for (Phenodigm2DTO phenodigm : results) {
                DiseaseModelAssociation assoc = createBasicDMA(phenodigm);
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
        solrQuery.addFilterQuery(Phenodigm2DTO.TYPE + ":disease_model_summary")
                .addField(Phenodigm2DTO.MODEL_ID)
                .addField(Phenodigm2DTO.MODEL_SOURCE)
                .addField(Phenodigm2DTO.MODEL_DESCRIPTION)
                .addField(Phenodigm2DTO.DISEASE_MODEL_AVG_RAW)
                .addField(Phenodigm2DTO.DISEASE_MODEL_AVG_NORM)
                .addField(Phenodigm2DTO.DISEASE_MODEL_MAX_RAW)
                .addField(Phenodigm2DTO.DISEASE_MODEL_MAX_NORM)
                .addSort(Phenodigm2DTO.DISEASE_MODEL_MAX_NORM, SolrQuery.ORDER.desc)
                .setRows(ROWLIMIT);
    }

    /**
     * Create a basic DMA object from the fields defined in the
     * completeDiseaseToModelQuery
     *
     * @param phenodigm
     * @return
     */
    private DiseaseModelAssociation createBasicDMA(Phenodigm2DTO phenodigm) {
        DiseaseModelAssociation assoc = new DiseaseModelAssociation(phenodigm.getModelId());
        assoc.setModelSource(phenodigm.getModelSource());
        assoc.setModelDescription(phenodigm.getModelDescription());
        assoc.setAvgNorm(phenodigm.getDiseaseModelAvgNorm());
        assoc.setAvgRaw(phenodigm.getDiseaseModelAvgRaw());
        assoc.setMaxNorm(phenodigm.getDiseaseModelMaxNorm());
        assoc.setMaxRaw(phenodigm.getDiseaseModelMaxRaw());
        return assoc;
    }

    @Override
    public List<DiseaseModelAssociation> getDiseaseModelDetails(String diseaseId, String markerId) {

        LOGGER.info("inside getDiseaseModelDetails " + diseaseId + " " + markerId);
        String query = String.format("%s:\"%s\" AND %s:\"%s\"",
                Phenodigm2DTO.DISEASE_ID, diseaseId,
                Phenodigm2DTO.MARKER_ID, markerId);
        LOGGER.info("looking for query: " + query);

        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(Phenodigm2DTO.TYPE + ":disease_model_summary")
                .addField(Phenodigm2DTO.MODEL_ID)
                .addField(Phenodigm2DTO.MODEL_SOURCE)
                .addField(Phenodigm2DTO.MODEL_DESCRIPTION)
                .addField(Phenodigm2DTO.MODEL_GENETIC_BACKGROUND)
                .addField(Phenodigm2DTO.MARKER_SYMBOL)
                .addField(Phenodigm2DTO.DISEASE_MODEL_AVG_NORM)
                .addField(Phenodigm2DTO.DISEASE_MODEL_MAX_NORM)
                .addField(Phenodigm2DTO.DISEASE_MATCHED_PHENOTYPES)
                .addField(Phenodigm2DTO.MODEL_MATCHED_PHENOTYPES)
                .addSort(Phenodigm2DTO.DISEASE_MODEL_MAX_NORM, SolrQuery.ORDER.desc)
                .setRows(ROWLIMIT);

        List<DiseaseModelAssociation> modelAssociations = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);
            for (Phenodigm2DTO phenodigm : results) {
                DiseaseModelAssociation assoc = new DiseaseModelAssociation(phenodigm.getModelId());
                assoc.setModelSource(phenodigm.getModelSource());
                assoc.setModelDescription(phenodigm.getModelDescription());
                assoc.setModelGeneticBackground(phenodigm.getModelGeneticBackground());
                assoc.setMarkerId(markerId);
                assoc.setMarkerSymbol(phenodigm.getMarkerSymbol());
                // assign the normalized scores (necessary to compute the overall model score)
                assoc.setAvgNorm(phenodigm.getDiseaseModelAvgNorm());
                assoc.setMaxNorm(phenodigm.getDiseaseModelMaxNorm());
                assoc.parseModelPhenotypes(phenodigm.getModelMatchedPhenotypes());
                assoc.parseDiseasePhenotypes(phenodigm.getDiseaseMatchedPhenotypes());
                modelAssociations.add(assoc);
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("from getDiseaseModelDetails: found " + modelAssociations.size());
        return modelAssociations;
    }

    /**
     * Get details for all models that capture one gene
     *
     * @param markerId
     * @return
     */
    @Override
    public List<Model> getGeneModelDetails(String markerId) {

        LOGGER.info("inside getGeneModelDetails " + markerId);
        String query = String.format("%s:\"%s\"", Phenodigm2DTO.MARKER_ID, markerId);
        LOGGER.info("looking for query: " + query);

        SolrQuery solrQuery = new SolrQuery(query)
                .addFilterQuery(Phenodigm2DTO.TYPE + ":mouse_model")
                .addField(Phenodigm2DTO.MODEL_ID)
                .addField(Phenodigm2DTO.MODEL_SOURCE)
                .addField(Phenodigm2DTO.MODEL_DESCRIPTION)
                .addField(Phenodigm2DTO.MODEL_GENETIC_BACKGROUND)
                .addField(Phenodigm2DTO.MARKER_SYMBOL)
                .addField(Phenodigm2DTO.MODEL_PHENOTYPES)
                .setRows(ROWLIMIT);

        List<Model> result = new ArrayList<>();
        try {
            List<Phenodigm2DTO> results = phenodigm2Core.query(solrQuery).getBeans(Phenodigm2DTO.class);
            for (Phenodigm2DTO phenodigm : results) {
                Model model = new Model(phenodigm.getModelId());
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

        LOGGER.info("from getGeneModelDetails: found " + result.size());
        return result;
    }

}
