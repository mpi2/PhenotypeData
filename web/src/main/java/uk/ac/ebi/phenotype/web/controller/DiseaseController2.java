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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.phenodigm2.Disease;
import uk.ac.ebi.phenodigm2.GeneAssociation;
import uk.ac.ebi.phenodigm2.ModelAssociation;
import uk.ac.ebi.phenodigm2.WebDao;

/**
 * Controller for disease pages, uses phenodigm2 core
 *
 *
 * @author Tomasz Konopka <t.konopka@qmul.ac.uk>
 * (based on DiseaseController.java)
 */
@Controller
public class DiseaseController2 {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private WebDao phenoDigm2Dao;

    @RequestMapping(value = {"/disease2"})
    public String allDiseases(Model model) {
        LOGGER.info("Making page for all disease2 - for now abort");
        return "404";
    }

    /**
     * There is confusion about whether ORPHANET IDs should be coded as
     * ORPHA:1234 or ORPHANET:1234. This is a compatibility issue between
     * phenodigm1 vs phenodigm2.
     *
     * @param diseaseId
     * @return
     *
     * returns ids in a normalized format, using ORPHA:1234
     *
     */
    private String normalizeDiseaseId(String diseaseId) {
        String[] tokens = diseaseId.split(":");
        if ("ORPHANET".equals(tokens[0])) {
            diseaseId = "ORPHA:" + tokens[1];
        }
        return diseaseId;
    }

    @RequestMapping(value = {"/disease2/{diseaseId}"})
    public String disease(@PathVariable("diseaseId") String diseaseId, Model model) {

        diseaseId = normalizeDiseaseId(diseaseId);

        LOGGER.info("Making disease2 page for " + diseaseId);

        Disease disease = phenoDigm2Dao.getDisease(diseaseId);
        if (disease == null) {
            LOGGER.info("Disease {} is null - returning fileNotFound page", diseaseId);
            return "fileNotFound";
        }
        LOGGER.info(String.format("Found disease: %s %s", disease.getId(), disease.getTerm()));
        model.addAttribute("disease", disease);

        // fetch associations between the disease and known genes
        LOGGER.info(String.format("%s - getting disease-gene associations", diseaseId));
        List<GeneAssociation> geneAssociations = phenoDigm2Dao.getDiseaseToGeneAssociations(diseaseId);
        LOGGER.info(String.format("%s - recieved %s disease-gene associations", diseaseId, geneAssociations.size()));
        // associated genes due to annotations
        List<GeneAssociation> curatedAssociations = new ArrayList<>();
        List<GeneAssociation> orthologousAssociations = new ArrayList<>();
        HashSet<String> orthologousGenes = new HashSet<>();
        for (GeneAssociation assoc : geneAssociations) {
            if (assoc.isCurated()) {                
                if (assoc.isOrtholog()) {
                    orthologousAssociations.add(assoc);
                    orthologousGenes.add(assoc.getSymbol());
                } else {
                    curatedAssociations.add(assoc);
                }
            }
        }
        model.addAttribute("curatedAssociations", curatedAssociations);
        model.addAttribute("orthologousAssociations", orthologousAssociations);
        model.addAttribute("orthologGenes", orthologousGenes);
        LOGGER.info("ortholog genes are: " + orthologousGenes.toString());

        // create a js object representation of the orthologous genes
        String relevantGenesObj = String.join("\", \"", orthologousGenes);
        if (orthologousGenes.size() > 0) {
            relevantGenesObj = "[\"" + relevantGenesObj + "\"]";
        } else {
            relevantGenesObj = "[]";
        }
        model.addAttribute("relevantMouseGenes", relevantGenesObj);

        // fetch associations between the disease and models
        LOGGER.info("");
        LOGGER.info(String.format("%s - getting disease-model associations", diseaseId));
        List<ModelAssociation> modelAssociations = phenoDigm2Dao.getDiseaseToModelAssociations(diseaseId);
        LOGGER.info(String.format("%s - received %s disease-model associations", diseaseId, modelAssociations.size()));
        model.addAttribute("modelAssociations", modelAssociations);

        // create a js object representation of the models        
        String modelAssociationsObj = "[]";
        boolean hasModelsByOrthology = false;
        if (modelAssociations.size() > 0) {
            List<String> jsons = new ArrayList<>();
            for (ModelAssociation assoc : modelAssociations) {
                jsons.add(assoc.getJson());
                if (orthologousGenes.contains(assoc.getMarkerSymbol())) {
                    hasModelsByOrthology = true;
                }
            }
            modelAssociationsObj = "[" + String.join(", ", jsons) + "]";
        }
        model.addAttribute("modelAssociationsObj", modelAssociationsObj);
        model.addAttribute("hasModelsByOrthology", hasModelsByOrthology);
        LOGGER.info("hasModelsByOrthology: " + hasModelsByOrthology);

        LOGGER.info("Returning disease2 page for " + diseaseId);
        return "disease2";
    }
}
