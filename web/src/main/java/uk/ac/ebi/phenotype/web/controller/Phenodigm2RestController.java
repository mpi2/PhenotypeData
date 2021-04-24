/** *****************************************************************************
 * Copyright 2017 QMUL - Queen Mary University of London
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
 ****************************************************************************** */
package uk.ac.ebi.phenotype.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.phenodigm2.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller that responds to ajax requests for phenodigm data.
 * 
 */
@RestController
public class Phenodigm2RestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Phenodigm2RestController.class);

    @Autowired
    private WebDao phenoDigm2Dao;

    /**
     * Provides details (including phenotypes) for mouse models with a gene KO.
     *
     * @param geneId
     *
     * gene identifier, can be comma-separated list
     * 
     * @return
     */
    @RequestMapping(value = "/phenodigm2/mousemodels", method = RequestMethod.GET)
    public List<MouseModel> getMouseModels(@RequestParam String geneId) {
        LOGGER.info(String.format("AJAX call for phenodigm mouse models with gene %s", geneId));
        List<MouseModel> modelDetails = new ArrayList<>();
        for (String id : geneId.split(",")) {
            modelDetails.addAll(phenoDigm2Dao.getGeneModelDetails(id));
        }
        return modelDetails;
    }

    /**
     * Provides details (including phenotypes) for mouse models associated with
     * a disease
     *
     * @param diseaseId
     * 
     * disease identifier, can be comma-separated
     * 
     * @return
     */
    @RequestMapping(value = "/phenodigm2/diseasescores", method = RequestMethod.GET)
    public List<DiseaseModelAssociation> getDiseaseScores(@RequestParam String diseaseId) {        
        LOGGER.info(String.format("AJAX call for phenodigm2 associations for disease %s", diseaseId));
        List<DiseaseModelAssociation> models = new ArrayList<>();
        for (String id : diseaseId.split(",")) {
            models.addAll(phenoDigm2Dao.getDiseaseToModelModelAssociations(id));
        }        
        return models;        
    }

    @RequestMapping(value = "/phenodigm2/genescores", method = RequestMethod.GET)
    public List<DiseaseModelAssociation> getGeneScores(@RequestParam String geneId) {        
        LOGGER.info(String.format("AJAX call for phenodigm2 associations for gene %s", geneId));
        List<DiseaseModelAssociation> models = new ArrayList<>();
        for (String id : geneId.split(",")) {
            models.addAll(phenoDigm2Dao.getGeneToDiseaseModelAssociations(id));
        }        
        return models;        
    }
    
    
    /**
     * Provides details for a disease
     *
     * @param diseaseId
     *
     * disease identifier, can be comma-separated
     *
     * @return
     */
    @RequestMapping(value = "/phenodigm2/disease", method = RequestMethod.GET)
    public List<Disease> getDisease(@RequestParam String diseaseId) {
        LOGGER.info(String.format("AJAX call for phenodigm2 data for disease %s", diseaseId));
        List<Disease> diseases = new ArrayList<>();
        for (String id : diseaseId.split(",")) {
            diseases.add(phenoDigm2Dao.getDisease(id));
        }
        return diseases;
    }

    /**
     * Provides details for the genes that are associated with a disease
     * (with a curation or orhtology)
     *
     * @param diseaseId
     *
     * disease identifier, can be comma-separated
     *
     * @return
     */
    @RequestMapping(value = "/phenodigm2/diseasegenes", method = RequestMethod.GET)
    public List<DiseaseGeneAssociation> getDiseaseGenes(@RequestParam String diseaseId) {
        LOGGER.info(String.format("AJAX call for phenodigm2 data for genes associated with disease %s", diseaseId));
        List<DiseaseGeneAssociation> genes = new ArrayList<>();
        for (String id : diseaseId.split(",")) {
            genes.addAll(phenoDigm2Dao.getDiseaseToGeneAssociations(id));
        }
        return genes;
    }

    /**
     * Provides a partial skeleton for a phenogrid object.
     *
     * @param pageType
     *
     * use "disease" or "gene"
     *
     * @param diseaseId
     * @param geneId
     */
    @RequestMapping(value = "/phenodigm2/phenogrid", method = RequestMethod.GET)
    public PhenoGrid getPhenoGrid(@RequestParam String pageType,
                                  @RequestParam String diseaseId,
                                  @RequestParam String geneId) {
        LOGGER.info(String.format("Making phenogigm2/phenogrid for %s %s from %s page", diseaseId, geneId, pageType));
        List<Phenotype> diseasePhenotypes = phenoDigm2Dao.getDiseasePhenotypes(diseaseId);
        List<MouseModel> modelDetails = phenoDigm2Dao.getGeneModelDetails(geneId);
        List<PhenoGridGroup> xAxisGroups = makePhenoGridGroups(modelDetails);

        //
        // calculate the phenodigm score for each model and replace the corresponding entry in the xAxisGroup
        //
        final List<DiseaseModelAssociation> geneToDiseaseModelAssociations = phenoDigm2Dao
                .getDiseaseToModelModelAssociations(diseaseId)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        // Collapse all the returned models into a map keyed on model ID => list (model IDs)
        // There are often multiple models for the same combination of Gene:Zygosity:Lifestage (e.g. different allele)
        Map<String, List<DiseaseModelAssociation>> modelAssociationsById = new HashMap<>();
        for (DiseaseModelAssociation d : geneToDiseaseModelAssociations) {
            if ( ! modelAssociationsById.containsKey(d.getId())) {
                modelAssociationsById.put(d.getId(), new ArrayList<>());
            }
            modelAssociationsById.get(d.getId()).add(d);
        }
        for (String key : modelAssociationsById.keySet()) {
            Collections.sort(modelAssociationsById.get(key)) ;
        }

        // Populate the x axis group score field with the highest score associated to the particular ID
        for (PhenoGridGroup pgg : xAxisGroups) {

            // Every entity in the x axis group represents a potential model to display on the phenogrid, but
            // the diseaseModelAssociations do not get populated with their corresponding score (why?  I dunno).
            // But the phenogrid widget will display those scores, so we need to populate it here by merging with the
            // disease models by gene.
            for (PhenoGridEntity entity : pgg.getEntities()) {
                if (modelAssociationsById.containsKey(entity.getId())) {
                    final PhenoGridScore x = entity.getScore();
                    final List<DiseaseModelAssociation> modelAssociationsSorted = modelAssociationsById.get(entity.getId());
                    entity.setScore(new PhenoGridScore(x.getMetric(), modelAssociationsSorted.get(0).getScore(), x.getRank()));
                }
            }

            // Sort entities by score desc
            pgg.entities.sort(Comparator.comparing(x -> ((PhenoGridEntity)x).getScore().getScore()).reversed());

            // Re-rank the entities to reflect the changed order with the highest score first in the list
            for (int i = 0; i < pgg.getEntities().size(); i++) {
                PhenoGridEntity entity = pgg.getEntities().get(i);
                final PhenoGridScore score = entity.getScore();
                entity.setScore(new PhenoGridScore(score.getMetric(), score.getScore(), i));
            }
        }

        // Use a space instead of null or empty string to prevent the phenogrid from displaying an unwanted default title
        String title = " ";
        return new PhenoGrid(title, xAxisGroups, diseasePhenotypes);
    }

    private List<PhenoGridGroup> makePhenoGridGroups(List<MouseModel> models) {
        List<PhenoGridGroup> xAxisGroups = new ArrayList<>();
        xAxisGroups.add(new PhenoGridGroup("pheno", "Phenotype Associated", makeGridEntities(models)));
        return xAxisGroups;
    }

    private List<PhenoGridEntity> makeGridEntities(List<MouseModel> models) {
        List<PhenoGridEntity> result = new ArrayList<>(models.size());
        for (MouseModel model : models) {
            result.add(makeGridEntity(model, result.size()));
        }
        return result;
    }

    private PhenoGridEntity makeGridEntity(MouseModel model, int rank) {

        // collect information from the association oject
        String id = model.getId();
        String label = model.getDescription();
        List<Phenotype> phenotypes = model.getPhenotypes();
        PhenoGridScore score = new PhenoGridScore("Phenodigm score", 0, rank);
        List<EntityInfo> info = makeModelInfo(model);

        return new PhenoGridEntity(id, label, phenotypes, score, info);
    }

    /**
     * Convert a list of phenotypes into a new list that contains only ids (not
     * terms) This is an optimization that avoid sending phenotype terms in the
     * Rest response.
     *
     * To display phenotype details in a table, the terms are actually required.
     * So this function comes into and out of use during development.
     *
     * @param phenotypes
     * @return
     */
    private List<Phenotype> makeIdOnlyPhenotypes(List<Phenotype> phenotypes) {
        List<Phenotype> result = new ArrayList<>(phenotypes.size());
        for (Phenotype p : phenotypes) {
            result.add(new Phenotype(p.getId(), ""));
        }
        return result;
    }

    /**
     *
     */
    private List<EntityInfo> makeModelInfo(MouseModel model) {

        List<EntityInfo> result = new ArrayList<>();
        result.add(new EntityInfo("Source: ", model.getSource(), null));
        result.add(new EntityInfo("Genotype: ", model.getDescription(), null));
        result.add(new EntityInfo("Background: ", model.getGeneticBackground(), null));
        result.add(new EntityInfo("Gene ID: ", model.getMarkerId(), null));
        result.add(new EntityInfo("Gene symbol: ", model.getMarkerSymbol(), null));
        result.add(new EntityInfo("Observed phenotypes: ", "", null));

        // copy the list of phenotypes
        for (Phenotype phenotype : model.getPhenotypes()) {
            result.add(new EntityInfo("", phenotype.getTerm(), null));
        }

        return result;
    }

    /**
     * Object defining a phenogrid skeleton.
     *
     * Holds a title, definitions of the x and y axes. See the Phenogrid docs on
     * github for details.
     */
    private class PhenoGrid {

        private final String title;
        private final List<PhenoGridGroup> xAxis;
        private final List<Phenotype> yAxis;

        public PhenoGrid(String title, List<PhenoGridGroup> xAxis, List<Phenotype> yAxis) {
            this.title = title;
            this.xAxis = xAxis;
            this.yAxis = yAxis;
        }

        public String getTitle() {
            return title;
        }

        public List<PhenoGridGroup> getxAxis() {
            return xAxis;
        }

        public List<Phenotype> getyAxis() {
            return yAxis;
        }
    }

    private class PhenoGridGroup {

        private final String groupId;
        private final String groupName;
        private final List<PhenoGridEntity> entities;

        public PhenoGridGroup(String groupId, String groupName, List<PhenoGridEntity> entities) {
            this.groupId = groupId;
            this.groupName = groupName;
            this.entities = entities;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public List<PhenoGridEntity> getEntities() {
            return entities;
        }
    }

    private class PhenoGridEntity {

        private String id; //not sure if this is required or not
        private String label;
        private List<Phenotype> phenotypes;
        private PhenoGridScore score;
        private List<EntityInfo> info;

        public PhenoGridEntity(String id, String label, List<Phenotype> phenotypes, PhenoGridScore score, List<EntityInfo> info) {
            this.id = id;
            this.label = label;
            this.phenotypes = phenotypes;
            this.score = score;
            this.info = info;
        }

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public List<Phenotype> getPhenotypes() {
            return phenotypes;
        }

        public PhenoGridScore getScore() {
            return score;
        }

        public void setScore(PhenoGridScore score) {
            this.score = score;
        }


        public List<EntityInfo> getInfo() {
            return info;
        }
    }

    private class PhenoGridScore {

        private final String metric;
        private final double score;
        private final int rank;

        public PhenoGridScore(String metric, double score, int rank) {
            this.metric = metric;
            this.score = score;
            this.rank = rank;
        }

        public String getMetric() {
            return metric;
        }

        public double getScore() {
            return score;
        }

        public int getRank() {
            return rank;
        }
    }

    private class EntityInfo {

        private final String id;
        private final String value;
        private final String href;

        public EntityInfo(String id, String value, String href) {
            this.id = id;
            this.value = value;
            this.href = href;
        }

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }

        public String getHref() {
            return href;
        }
    }
}
