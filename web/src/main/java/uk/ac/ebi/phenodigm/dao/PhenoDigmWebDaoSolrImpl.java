/*
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * and Genome Research Limited
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
package uk.ac.ebi.phenodigm.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.phenodigm.model.*;
import uk.ac.ebi.phenodigm.web.AssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseAssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseGeneAssociationDetail;
import uk.ac.ebi.phenodigm.web.GeneAssociationSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of PhenoDigmWebDao which uses Solr as the datasource. This
 * enables both fast free-text searching of PhenoDigm and also means the data
 * can be accessed using Solr as a web-service.
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@Repository
public class PhenoDigmWebDaoSolrImpl implements PhenoDigmWebDao {

    private static final Logger logger = LoggerFactory.getLogger(PhenoDigmWebDaoSolrImpl.class);
    private static final int ROWS = 10000;

    @Autowired @Qualifier("phenodigmCore")
    private HttpSolrClient phenodigmCore;

    @Override
    public Disease getDisease(DiseaseIdentifier diseaseId) {

	    String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId.getCompoundIdentifier());

	    SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease");
        solrQuery.addField(PhenodigmDTO.DISEASE_ID);
        solrQuery.addField(PhenodigmDTO.DISEASE_TERM);
        solrQuery.addField(PhenodigmDTO.DISEASE_ALTS);
        solrQuery.addField(PhenodigmDTO.DISEASE_LOCUS);
        solrQuery.addField(PhenodigmDTO.DISEASE_CLASSES);
        solrQuery.addField(PhenodigmDTO.PHENOTYPES);

        Disease disease = null;

        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);

            if (results.isEmpty()) {
                logger.info("Uh-oh! Query for disease {} was not found.", diseaseId);
                return null;
            }
            if (results.size() > 1) {
                logger.info("Uh-oh! Query for disease {} returned more than one result.", diseaseId);
            }

	        PhenodigmDTO phenodigm = results.get(0);
            disease = new Disease(diseaseId);
            disease.setTerm(phenodigm.getDiseaseTerm());
            disease.setAlternativeTerms(phenodigm.getDiseaseAlts());
            disease.setLocus(phenodigm.getDiseaseLocus());
            disease.setClasses(phenodigm.getDiseaseClasses());

            logger.debug("Made {}", disease);
        } catch (SolrServerException | IOException e) {
            logger.error(e.getMessage());
        }

        return disease;
    }

    @Override
    public List<PhenotypeTerm> getDiseasePhenotypes(DiseaseIdentifier diseaseId) {

	    String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId.getCompoundIdentifier());

	    SolrQuery solrQuery = new SolrQuery(query);
	    solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease");
	    solrQuery.addField(PhenodigmDTO.PHENOTYPES);

        List<PhenotypeTerm> phenotypeList = new ArrayList<>();
        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);

            if (results.isEmpty()) {
                logger.info("Uh-oh! Query for disease {} phenotypes was not found.", diseaseId);
                return phenotypeList;
            }
            if (results.size() > 1) {
                logger.info("Uh-oh! Query for disease {} phenotypes returned more than one result.", diseaseId);
            }
	        PhenodigmDTO phenodigm = results.get(0);

            for (String string : phenodigm.getPhenotypes()) {
                PhenotypeTerm phenotype = makePhenotypeTerm(string);
                phenotypeList.add(phenotype);
            }

        } catch (SolrServerException | IOException e) {
            logger.error(e.getMessage());
        }

        return phenotypeList;
    }

    @Override
    public Gene getGene(GeneIdentifier geneIdentifier) {

	    String query = String.format("%s:\"%s\"", PhenodigmDTO.MARKER_ACCESSION, geneIdentifier.getCompoundIdentifier());

	    SolrQuery solrQuery = new SolrQuery(query);
	    solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":gene");
        solrQuery.addField(PhenodigmDTO.MARKER_ACCESSION);
        solrQuery.addField(PhenodigmDTO.MARKER_SYMBOL);
        solrQuery.addField(PhenodigmDTO.HGNC_GENE_ID);
        solrQuery.addField(PhenodigmDTO.HGNC_GENE_SYMBOL);
        solrQuery.addField(PhenodigmDTO.HGNC_GENE_LOCUS);

        Gene gene = null;
        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);

            if (results.isEmpty()) {
                logger.info("Uh-oh! Query for gene {} was not found.", geneIdentifier);
                return null;
            }
            if (results.size() > 1) {
                logger.info("Uh-oh! Query for gene {} returned more than one result.", geneIdentifier);
            }
	        PhenodigmDTO phenodigm = results.get(0);

            String modGenId = phenodigm.getMarkerAccession();
            String modGenSymbol = phenodigm.getMarkerSymbol();
            String humanGenId = phenodigm.getHgncGeneID();
            String humanGenSymbol = phenodigm.getHgncGeneSymbol();

            GeneIdentifier modelGeneId = new GeneIdentifier(modGenSymbol, modGenId);
            GeneIdentifier humanGeneId = new GeneIdentifier(humanGenSymbol, humanGenId);
            gene = new Gene(modelGeneId, humanGeneId);

            logger.debug("Made {}", gene);
        } catch (SolrServerException | IOException e) {
            logger.error(e.getMessage());
        }

        return gene;
    }

    @Override
    public List<GeneAssociationSummary> getDiseaseToGeneAssociationSummaries(DiseaseIdentifier diseaseId, double minRawScoreCutoff) {

	    String query = String.format("%s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId.getCompoundIdentifier());

	    SolrQuery solrQuery = new SolrQuery(query);
	    solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease_gene_summary");

        //if there is no cutoff then don't put it in the query as it will take a long time (a few seconds) to collect the results
        //rather than a few tens of ms
//        if (minRawScoreCutoff != 0) {
            //we're not doing this anymore as we've pre-processed the data from the database in order to show only the high quality data
            //this makes the query *lot* quicker and also makes for a *much* smaller core so big wins all round.
//            solrQuery.addFilterQuery(String.format("(human_curated:true OR mouse_curated:true OR raw_mod_score:[%s TO *] OR raw_htpc_score:[%s TO *])", minRawScoreCutoff, minRawScoreCutoff));
//        }

        //add fields to return
	    solrQuery.addField(PhenodigmDTO.MARKER_ACCESSION);
	    solrQuery.addField(PhenodigmDTO.MARKER_SYMBOL);
	    solrQuery.addField(PhenodigmDTO.HGNC_GENE_ID);
	    solrQuery.addField(PhenodigmDTO.HGNC_GENE_SYMBOL);
	    solrQuery.addField(PhenodigmDTO.HGNC_GENE_LOCUS);
        solrQuery.addField(PhenodigmDTO.IN_LOCUS);
        //common fields
        solrQuery.addField(PhenodigmDTO.HUMAN_CURATED);
        solrQuery.addField(PhenodigmDTO.MOUSE_CURATED);
        solrQuery.addField(PhenodigmDTO.MAX_MGI_D2M_SCORE);
        solrQuery.addField(PhenodigmDTO.MAX_IMPC_D2M_SCORE);

        solrQuery.addSort(PhenodigmDTO.HUMAN_CURATED, SolrQuery.ORDER.desc);
        solrQuery.addSort(PhenodigmDTO.IN_LOCUS, SolrQuery.ORDER.desc);
        solrQuery.addSort(PhenodigmDTO.MAX_MGI_D2M_SCORE, SolrQuery.ORDER.desc);

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        List<GeneAssociationSummary> geneAssociationSummaryList = new ArrayList<>();
        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);

            for (PhenodigmDTO phenodigm : results) {

                //make the geneIdentifiers
                String modGenId = phenodigm.getMarkerAccession();
                String modGenSymbol = phenodigm.getMarkerSymbol();
                String humanGenId = phenodigm.getHgncGeneID();
                String humanGenSymbol = phenodigm.getHgncGeneSymbol();

                GeneIdentifier modelGeneId = new GeneIdentifier(modGenSymbol, modGenId);
                GeneIdentifier hgncGeneId = new GeneIdentifier(humanGenSymbol, humanGenId);

	            boolean associatedInHuman = phenodigm.getHumanCurated();
	            boolean hasLiteratureEvidence = phenodigm.getMouseCurated();
	            boolean inLocus = phenodigm.getInLocus();
	            double bestMgiScore = phenodigm.getMaxMgiD2mScore()!=null ? phenodigm.getMaxMgiD2mScore() : 0.0;
	            double bestImpcScore = phenodigm.getMaxImpcD2mScore()!=null ? phenodigm.getMaxImpcD2mScore() : 0.0;

	            AssociationSummary associationSummary = new AssociationSummary(associatedInHuman, hasLiteratureEvidence, inLocus, bestMgiScore, bestImpcScore);
	            GeneAssociationSummary geneAssociationSummary = new GeneAssociationSummary(hgncGeneId, modelGeneId, associationSummary);
//                logger.info("Made {}", geneAssociationSummary );

                geneAssociationSummaryList.add(geneAssociationSummary);
            }
        } catch (SolrServerException | IOException e) {
            logger.error(e.getMessage());
        }
        return geneAssociationSummaryList;
    }

    @Override
    public List<DiseaseAssociationSummary> getGeneToDiseaseAssociationSummaries(GeneIdentifier geneIdentifier, double minRawScoreCutoff) {

	    String query = String.format("%s:\"%s\"", PhenodigmDTO.MARKER_ACCESSION, geneIdentifier.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
	    solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease_gene_summary");

        //add fields to return
        solrQuery.addField(PhenodigmDTO.DISEASE_ID);
        solrQuery.addField(PhenodigmDTO.DISEASE_TERM);
        //common fields
        solrQuery.addField(PhenodigmDTO.IN_LOCUS);
        solrQuery.addField(PhenodigmDTO.HUMAN_CURATED);
        solrQuery.addField(PhenodigmDTO.MOUSE_CURATED);
        solrQuery.addField(PhenodigmDTO.MAX_MGI_M2D_SCORE);
        solrQuery.addField(PhenodigmDTO.MAX_IMPC_M2D_SCORE);

        solrQuery.addSort(PhenodigmDTO.HUMAN_CURATED, SolrQuery.ORDER.desc);
        solrQuery.addSort(PhenodigmDTO.IN_LOCUS, SolrQuery.ORDER.desc);
        solrQuery.addSort(PhenodigmDTO.MAX_MGI_M2D_SCORE, SolrQuery.ORDER.desc);

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        List<DiseaseAssociationSummary> diseaseAssociationSummaryList = new ArrayList<>();
        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
            for (PhenodigmDTO phenodigm : results) {

                String diseaseId = phenodigm.getDiseaseID();
                String diseaseTerm = phenodigm.getDiseaseTerm();

                //make the association summary details
	            boolean associatedInHuman = phenodigm.getHumanCurated();
	            boolean hasLiteratureEvidence = phenodigm.getMouseCurated();
	            boolean inLocus = phenodigm.getInLocus();
	            double bestMgiScore = phenodigm.getMaxMgiM2dScore()!=null ? phenodigm.getMaxMgiM2dScore() : 0.0;
	            double bestImpcScore = phenodigm.getMaxImpcM2dScore()!=null ? phenodigm.getMaxImpcM2dScore() : 0.0;

	            AssociationSummary associationSummary = new AssociationSummary(associatedInHuman, hasLiteratureEvidence, inLocus, bestMgiScore, bestImpcScore);
	            DiseaseAssociationSummary diseaseAssociationSummary = new DiseaseAssociationSummary(new DiseaseIdentifier(diseaseId), diseaseTerm, associationSummary);
	            //                logger.info("Made {}", diseaseAssociationSummary );

                diseaseAssociationSummaryList.add(diseaseAssociationSummary);
            }
        } catch (SolrServerException | IOException e) {
            logger.error(e.getMessage());
        }
        return diseaseAssociationSummaryList;
    }

    @Override
    public DiseaseGeneAssociationDetail getDiseaseGeneAssociationDetail(DiseaseIdentifier diseaseId, GeneIdentifier geneId) {

	    String query = String.format("%s:\"%s\" AND %s:\"%s\"", PhenodigmDTO.DISEASE_ID, diseaseId.getCompoundIdentifier(), PhenodigmDTO.MARKER_ACCESSION, geneId.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":disease_model_association");

        solrQuery.addField(PhenodigmDTO.MODEL_ID);
        solrQuery.addField(PhenodigmDTO.LIT_MODEL);
        solrQuery.addField(PhenodigmDTO.DISEASE_TO_MODEL_SCORE);
        solrQuery.addField(PhenodigmDTO.MODEL_TO_DISEASE_SCORE);

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        List<DiseaseModelAssociation> diseaseAssociationSummaryList = new ArrayList<>();

        //get the models needed for this geneIdentifier
        Map<Integer, MouseModel> modelMap = getMouseModels(geneId);

        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
	        for (PhenodigmDTO phenodigm : results) {
                DiseaseModelAssociation diseaseModelAssociation = new DiseaseModelAssociation();
                diseaseModelAssociation.setDiseaseIdentifier(diseaseId);

                Integer modelId = phenodigm.getModelID();
                boolean litEvidence = phenodigm.getLitModel();
                double d2m = phenodigm.getDiseaseToModelScore();
                double m2d = phenodigm.getModelToDiseaseScore();
                diseaseModelAssociation.setHasLiteratureEvidence(litEvidence);
                diseaseModelAssociation.setDiseaseToModelScore(d2m);
                diseaseModelAssociation.setModelToDiseaseScore(m2d);

                MouseModel model = modelMap.get(modelId);
                diseaseModelAssociation.setMouseModel(model);

                logger.debug("Made {}", diseaseModelAssociation);
                diseaseAssociationSummaryList.add(diseaseModelAssociation);
            }
        } catch (SolrServerException | IOException e)  {
            logger.error(e.getMessage());
        }

        //build the DiseaseGeneAssociationDetail from the component parts
        return new DiseaseGeneAssociationDetail(diseaseId, getDiseasePhenotypes(diseaseId), getGene(geneId), diseaseAssociationSummaryList);
    }

    /**
     * Returns a map of models indexed by model_id.
     *
     * @param geneIdentifier the gene to be queried
     * @return a map of mgi model ids to model objects
     */
    private Map<Integer, MouseModel> getMouseModels(GeneIdentifier geneIdentifier) {

	    String query = String.format("%s:\"%s\"", PhenodigmDTO.MARKER_ACCESSION, geneIdentifier.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery(PhenodigmDTO.TYPE + ":mouse_model");

        solrQuery.addField(PhenodigmDTO.MODEL_ID);
        solrQuery.addField(PhenodigmDTO.MARKER_ACCESSION);
        solrQuery.addField(PhenodigmDTO.SOURCE);
        solrQuery.addField(PhenodigmDTO.GENETIC_BACKGROUND);
        solrQuery.addField(PhenodigmDTO.ALLELIC_COMPOSITION);
        solrQuery.addField(PhenodigmDTO.ALLELE_IDS);
        solrQuery.addField(PhenodigmDTO.PHENOTYPES);

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        Map<Integer, MouseModel> modelMap = new HashMap<>();

        logger.info("making mouseModels for geneIdentifier: {}", geneIdentifier);

        try {
	        List<PhenodigmDTO> results = phenodigmCore.query(solrQuery).getBeans(PhenodigmDTO.class);
	        for (PhenodigmDTO phenodigm : results) {

                Integer modelId = phenodigm.getModelID();
                String modelGeneId = phenodigm.getMarkerAccession();
                String source = phenodigm.getSource();
                String geneticBackground = phenodigm.getGeneticBackground();
                String allelicComposition = phenodigm.getAllelicComposition();
                String setAlleleIds = phenodigm.getAlleleIds();
                List<String> phenotypes = phenodigm.getPhenotypes();
                //make the phenotype terms
                List<PhenotypeTerm> phenotypeTerms = new ArrayList<>();

                if (phenotypes != null) {
                    for (String string : phenotypes) {
                        PhenotypeTerm phenotype = makePhenotypeTerm(string);
                        phenotypeTerms.add(phenotype);
                        logger.debug("Made {}", phenotype);
                    }
                }
                //make the model
                MouseModel model = new MouseModel();
                model.setMgiModelId(modelId);
                model.setMgiGeneId(modelGeneId);
                model.setSource(source);
                model.setGeneticBackground(geneticBackground);

                model.setAllelicComposition(allelicComposition);
                model.setAlleleIds(setAlleleIds);
                model.setAllelicCompositionLink(ExternalLinkBuilder.buildLink(model));

                model.setPhenotypeTerms(phenotypeTerms);

                logger.debug("Made {}", model);
                modelMap.put(model.getMgiModelId(), model);
            }
        } catch (SolrServerException | IOException e) {
            logger.error(e.getMessage());
        }

        return modelMap;
    }

    private PhenotypeTerm makePhenotypeTerm(String string) {
        String[] splitString = string.split("_");
        String id = "";
        String term = "";
        if (splitString.length == 2) {
            id = splitString[0];
            term = splitString[1];
        } else {
            logger.warn("makePhenotypeTerm Parsing Error: '{}' should be of format MP:1234_Term", string);
        }
        return new PhenotypeTerm(id, term);
    }
}
