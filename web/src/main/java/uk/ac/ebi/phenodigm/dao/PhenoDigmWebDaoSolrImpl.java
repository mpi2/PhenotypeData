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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.phenodigm.model.Disease;
import uk.ac.ebi.phenodigm.model.DiseaseIdentifier;
import uk.ac.ebi.phenodigm.model.DiseaseModelAssociation;
import uk.ac.ebi.phenodigm.model.Gene;
import uk.ac.ebi.phenodigm.model.GeneIdentifier;
import uk.ac.ebi.phenodigm.model.MouseModel;
import uk.ac.ebi.phenodigm.model.PhenotypeTerm;
import uk.ac.ebi.phenodigm.web.AssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseAssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseGeneAssociationDetail;
import uk.ac.ebi.phenodigm.web.GeneAssociationSummary;

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

    @Autowired
    private SolrServer solrServer;

    @Override
    public Disease getDisease(DiseaseIdentifier diseaseId) {

        String query = String.format("disease_id:\"%s\"", diseaseId.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery("type:\"disease\"");
        solrQuery.addField("disease_id");
        solrQuery.addField("disease_term");
        solrQuery.addField("disease_alts");
        solrQuery.addField("disease_locus");
        solrQuery.addField("disease_classes");
        solrQuery.addField("phenotypes");

        SolrDocumentList resultsDocumentList;

        Disease disease = null;

        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();

            if (resultsDocumentList.isEmpty()) {
                logger.info("Uh-oh! Query for disease {} was not found.", diseaseId);
                return disease;
            }
            if (resultsDocumentList.size() > 1) {
                logger.info("Uh-oh! Query for disease {} returned more than one result.", diseaseId);
            }
            SolrDocument solrDocument = resultsDocumentList.get(0);
            disease = new Disease(diseaseId);
            disease.setTerm((String) solrDocument.getFieldValue("disease_term"));
            disease.setAlternativeTerms((List<String>) solrDocument.getFieldValue("disease_alts"));
            disease.setLocus((String) solrDocument.getFieldValue("disease_locus"));
            disease.setClasses((List<String>) solrDocument.getFieldValue("disease_classes"));

            logger.debug("Made {}", disease);
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        }

        return disease;
    }

    @Override
    public List<PhenotypeTerm> getDiseasePhenotypes(DiseaseIdentifier diseaseId) {
        String query = String.format("disease_id:\"%s\"", diseaseId.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(query);
        solrQuery.addFilterQuery("type:\"disease\"");
        solrQuery.addField("phenotypes");
        SolrDocumentList resultsDocumentList;

        List<PhenotypeTerm> phenotypeList = new ArrayList<>();
        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();

            if (resultsDocumentList.isEmpty()) {
                logger.info("Uh-oh! Query for disease {} phenotypes was not found.", diseaseId);
                return phenotypeList;
            }
            if (resultsDocumentList.size() > 1) {
                logger.info("Uh-oh! Query for disease {} phenotypes returned more than one result.", diseaseId);
            }
            SolrDocument solrDocument = resultsDocumentList.get(0);
            List<String> phenotypes = (List<String>) solrDocument.getFieldValue("phenotypes");
            for (String string : phenotypes) {
                PhenotypeTerm phenotype = makePhenotypeTerm(string);
                phenotypeList.add(phenotype);
            }

        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        }

        return phenotypeList;
    }

    @Override
    public Gene getGene(GeneIdentifier geneIdentifier) {

        String query = String.format("marker_accession:\"%s\"", geneIdentifier.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery("type:\"gene\"");
        solrQuery.addField("marker_accession");
        solrQuery.addField("marker_symbol");
        solrQuery.addField("hgnc_id");
        solrQuery.addField("hgnc_gene_symbol");
        solrQuery.addField("hgnc_gene_locus");

        SolrDocumentList resultsDocumentList;

        Gene gene = null;
        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();

            if (resultsDocumentList.isEmpty()) {
                logger.info("Uh-oh! Query for gene {} was not found.", geneIdentifier);
                return gene;
            }
            if (resultsDocumentList.size() > 1) {
                logger.info("Uh-oh! Query for gene {} returned more than one result.", geneIdentifier);
            }
            SolrDocument solrDocument = resultsDocumentList.get(0);

            String modGenId = (String) solrDocument.getFieldValue("marker_accession");
            String modGenSymbol = (String) solrDocument.getFieldValue("marker_symbol");
            String humanGenId = (String) solrDocument.getFieldValue("hgnc_id");
            String humanGenSymbol = (String) solrDocument.getFieldValue("hgnc_gene_symbol");

            GeneIdentifier modelGeneId = new GeneIdentifier(modGenSymbol, modGenId);
            GeneIdentifier humanGeneId = new GeneIdentifier(humanGenSymbol, humanGenId);
            gene = new Gene(modelGeneId, humanGeneId);

            logger.debug("Made {}", gene);
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        }

        return gene;
    }

    @Override
    public List<GeneAssociationSummary> getDiseaseToGeneAssociationSummaries(DiseaseIdentifier diseaseId, double minRawScoreCutoff) {

        String query = String.format("disease_id:\"%s\"", diseaseId.getCompoundIdentifier(), minRawScoreCutoff);

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery("type:\"disease_gene_summary\"");
        //if there is no cutoff then don't put it in the query as it will take a long time (a few seconds) to collect the results
        //rather than a few tens of ms   
        if (minRawScoreCutoff != 0) {
            //we're not doing this anymore as we've pre-processed the data from the database in order to show only the high quality data
            //this makes the query *lot* quicker and also makes for a *much* smaller core so big wins all round.
//            solrQuery.addFilterQuery(String.format("(human_curated:true OR mouse_curated:true OR raw_mod_score:[%s TO *] OR raw_htpc_score:[%s TO *])", minRawScoreCutoff, minRawScoreCutoff));
        }
        
        String maxMgiField = "max_mgi_d2m_score";
        String maxImpcField = "max_impc_d2m_score";
        
        //add fields to return  
        solrQuery.addField("marker_accession");
        solrQuery.addField("marker_symbol");
        solrQuery.addField("hgnc_id");
        solrQuery.addField("hgnc_gene_symbol");
        solrQuery.addField("hgnc_gene_locus");
        solrQuery.addField("in_locus");
        //common fields
        solrQuery.addField("human_curated");
        solrQuery.addField("mouse_curated");
        solrQuery.addField(maxMgiField);
        solrQuery.addField(maxImpcField);

        solrQuery.addSort("human_curated", SolrQuery.ORDER.desc);
        solrQuery.addSort("in_locus", SolrQuery.ORDER.desc);
        solrQuery.addSort(maxMgiField, SolrQuery.ORDER.desc);

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);
        
        SolrDocumentList resultsDocumentList;

        List<GeneAssociationSummary> geneAssociationSummaryList = new ArrayList<>();
        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();
            for (SolrDocument solrDocument : resultsDocumentList) {
//                logger.info("{}", solrDocument.getFieldValuesMap() );
                //make the geneIdentifiers
                String modGenId = (String) solrDocument.getFieldValue("marker_accession");
                String modGenSymbol = (String) solrDocument.getFieldValue("marker_symbol");
                String humanGenId = (String) solrDocument.getFieldValue("hgnc_id");
                String humanGenSymbol = (String) solrDocument.getFieldValue("hgnc_gene_symbol");

                GeneIdentifier modelGeneId = new GeneIdentifier(modGenSymbol, modGenId);
                GeneIdentifier hgncGeneId = new GeneIdentifier(humanGenSymbol, humanGenId);

                AssociationSummary associationSummary = makeAssociationSummary(solrDocument, maxMgiField, maxImpcField);

                GeneAssociationSummary geneAssociationSummary = new GeneAssociationSummary(hgncGeneId, modelGeneId, associationSummary);
//                logger.info("Made {}", geneAssociationSummary );

                geneAssociationSummaryList.add(geneAssociationSummary);
            }
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        }
        return geneAssociationSummaryList;
    }

    @Override
    public List<DiseaseAssociationSummary> getGeneToDiseaseAssociationSummaries(GeneIdentifier geneId, double minRawScoreCutoff) {

        String query = String.format("marker_accession:\"%s\"", geneId.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery("type:\"disease_gene_summary\"");

        String maxMgiField = "max_mgi_m2d_score";
        String maxImpcField = "max_impc_m2d_score";
        
        //add fields to return
        solrQuery.addField("disease_id");
        solrQuery.addField("disease_term");
        //common fields
        solrQuery.addField("in_locus");
        solrQuery.addField("human_curated");
        solrQuery.addField("mouse_curated");
        solrQuery.addField(maxMgiField);
        solrQuery.addField(maxImpcField);

        solrQuery.addSort("human_curated", SolrQuery.ORDER.desc);
        solrQuery.addSort("in_locus", SolrQuery.ORDER.desc);
        solrQuery.addSort(maxMgiField, SolrQuery.ORDER.desc);

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        SolrDocumentList resultsDocumentList;

        List<DiseaseAssociationSummary> diseaseAssociationSummaryList = new ArrayList<>();
        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();
            for (SolrDocument solrDocument : resultsDocumentList) {
//                logger.info("{}", solrDocument.getFieldValuesMap() );
                //make the geneIdentifiers
                String diseaseId = (String) solrDocument.getFieldValue("disease_id");
                String diseaseTerm = (String) solrDocument.getFieldValue("disease_term");

                //make the association summary details
                AssociationSummary associationSummary = makeAssociationSummary(solrDocument, maxMgiField, maxImpcField);

                DiseaseAssociationSummary diseaseAssociationSummary = new DiseaseAssociationSummary(new DiseaseIdentifier(diseaseId), diseaseTerm, associationSummary);
//                logger.info("Made {}", diseaseAssociationSummary );

                diseaseAssociationSummaryList.add(diseaseAssociationSummary);
            }
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        }
        return diseaseAssociationSummaryList;
    }

    @Override
    public DiseaseGeneAssociationDetail getDiseaseGeneAssociationDetail(DiseaseIdentifier diseaseId, GeneIdentifier geneId) {

        String query = String.format("disease_id:\"%s\" AND marker_accession:\"%s\"", diseaseId.getCompoundIdentifier(), geneId.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery("type:\"disease_model_association\"");

        solrQuery.addField("model_id");
        solrQuery.addField("lit_model");
        solrQuery.addField("disease_to_model_score");
        solrQuery.addField("model_to_disease_score");

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        SolrDocumentList resultsDocumentList;

        List<DiseaseModelAssociation> diseaseAssociationSummaryList = new ArrayList<>();

        //get the models needed for this geneIdentifier
        Map<Integer, MouseModel> modelMap = getMouseModels(geneId);

        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();
            for (SolrDocument solrDocument : resultsDocumentList) {
                DiseaseModelAssociation diseaseModelAssociation = new DiseaseModelAssociation();
                diseaseModelAssociation.setDiseaseIdentifier(diseaseId);

                Integer modelId = (Integer) solrDocument.getFieldValue("model_id");
                boolean litEvidence = (boolean) solrDocument.getFieldValue("lit_model");
                double d2m = (double) solrDocument.getFieldValue("disease_to_model_score");
                double m2d = (double) solrDocument.getFieldValue("model_to_disease_score");
                diseaseModelAssociation.setHasLiteratureEvidence(litEvidence);
                diseaseModelAssociation.setDiseaseToModelScore(d2m);
                diseaseModelAssociation.setModelToDiseaseScore(m2d);

                MouseModel model = modelMap.get(modelId);
                diseaseModelAssociation.setMouseModel(model);

                logger.debug("Made {}", diseaseModelAssociation);
                diseaseAssociationSummaryList.add(diseaseModelAssociation);
            }
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
        }

        //build the DiseaseGeneAssociationDetail from the component parts
        return new DiseaseGeneAssociationDetail(diseaseId, getDiseasePhenotypes(diseaseId), getGene(geneId), diseaseAssociationSummaryList);
    }

    /**
     * Creates an AssociationSummary object from a solrDocument. Don't feed this
     * any old document otherwise there will be null pointer exceptions.
     *
     * @param solrDocument
     * @return
     */
    private AssociationSummary makeAssociationSummary(SolrDocument solrDocument, String maxModField, String maxHtpcField) {
        //make the association summary details
        boolean associatedInHuman = (boolean) solrDocument.getFieldValue("human_curated");
        boolean hasLiteratureEvidence = (boolean) solrDocument.getFieldValue("mouse_curated");
        boolean inLocus = (boolean) solrDocument.getFieldValue("in_locus");

        double bestModScore = 0.0;
        if (solrDocument.getFieldValue(maxModField) != null) {
            bestModScore = (double) solrDocument.getFieldValue(maxModField);
        }

        double bestHtpcScore = 0.0;
        if (solrDocument.getFieldValue(maxHtpcField) != null) {
            bestHtpcScore = (double) solrDocument.getFieldValue(maxHtpcField);
        }

        AssociationSummary associationSummary = new AssociationSummary(associatedInHuman, hasLiteratureEvidence, inLocus, bestModScore, bestHtpcScore);
//        System.out.println(associationSummary);
        return associationSummary;
    }

    /**
     * Returns a map of models indexed by model_id.
     *
     * @param geneIdentifier
     * @return
     */
    private Map<Integer, MouseModel> getMouseModels(GeneIdentifier geneIdentifier) {

        String query = String.format("marker_accession:\"%s\"", geneIdentifier.getCompoundIdentifier());

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.addFilterQuery("type:\"mouse_model\"");

        solrQuery.addField("model_id");
        solrQuery.addField("marker_accession");
        solrQuery.addField("marker_symbol");
        solrQuery.addField("source");
        solrQuery.addField("genetic_background");
        solrQuery.addField("allelic_composition");
        solrQuery.addField("allele_ids");
        solrQuery.addField("hom_het");
        solrQuery.addField("phenotypes");

        //there will be more than 10 results for this - we want them all.
        solrQuery.setRows(ROWS);

        SolrDocumentList resultsDocumentList;

        Map<Integer, MouseModel> modelMap = new HashMap<>();

        logger.info("making mouseModels for geneIdentifier: {}", geneIdentifier);

        try {
            resultsDocumentList = solrServer.query(solrQuery).getResults();
            for (SolrDocument solrDocument : resultsDocumentList) {

                Integer modelId = (Integer) solrDocument.getFieldValue("model_id");
                String modelGeneId = (String) solrDocument.getFieldValue("marker_accession");
                String modelGeneSymbol = (String) solrDocument.getFieldValue("marker_symbol");
                String source = (String) solrDocument.getFieldValue("source");
                String geneticBackground = (String) solrDocument.getFieldValue("genetic_background");
                String allelicComposition = (String) solrDocument.getFieldValue("allelic_composition");
                String setAlleleIds = (String) solrDocument.getFieldValue("allele_ids");
                String homHet = (String) solrDocument.getFieldValue("hom_het");
                List<String> phenotypes = (List<String>) solrDocument.getFieldValue("phenotypes");
                //make the phenotype terms
                List<PhenotypeTerm> phenotypeTerms = new ArrayList();

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
        } catch (SolrServerException ex) {
            logger.error(ex.getMessage());
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
