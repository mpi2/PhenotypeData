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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.phenodigm2.Disease;
import uk.ac.ebi.phenodigm2.DiseaseGeneAssociation;
import uk.ac.ebi.phenodigm2.DiseaseModelAssociation;
import uk.ac.ebi.phenodigm2.WebDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Controller for disease pages, uses phenodigm2 core
 *
 */
@Controller
public class DiseaseController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private WebDao phenoDigm2Dao;

    @RequestMapping(value = {"/disease"})
    public String allDiseases(Model model) {
        LOGGER.info("Making page for all disease2 - for now abort");
        return "404";
    }

    /**
     * There is confusion about whether ORPHANET IDs should be coded as
     * ORPHA:1234 or ORPHANET:1234. 
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

    @RequestMapping(value = {"/disease/{diseaseId}"})
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
        List<DiseaseGeneAssociation> geneAssociations = phenoDigm2Dao.getDiseaseToGeneAssociations(diseaseId);
        // split the genes into curated/ortholog, i.e. human/mouse 
        List<DiseaseGeneAssociation> curatedAssociations = new ArrayList<>();
        List<DiseaseGeneAssociation> orthologousAssociations = new ArrayList<>();
        HashSet<String> orthologousGenes = new HashSet<>();
        for (DiseaseGeneAssociation assoc : geneAssociations) {
            if (assoc.isByOrthology()) {
                orthologousAssociations.add(assoc);
                orthologousGenes.add(assoc.getSymbol());
            } else {
                curatedAssociations.add(assoc);
            }
        }
        // add details about curated genes and orthologous genes (for disease page header)
        model.addAttribute("curatedAssociations", curatedAssociations);
        model.addAttribute("orthologousAssociations", orthologousAssociations);
        // stringify the ortholgoous genes into a js array
        String curatedJsArray = String.join("\", \"", orthologousGenes);
        if (orthologousGenes.size() > 0) {
            curatedJsArray = "[\"" + curatedJsArray + "\"]";
        } else {
            curatedJsArray = "[]";
        }
        model.addAttribute("curatedMouseGenes", curatedJsArray);

        // fetch associations between the disease and models        
        List<DiseaseModelAssociation> modelAssociations = phenoDigm2Dao.getDiseaseToModelModelAssociations(diseaseId);

        // create a js object representation of the models        
        String modelAssocsJsArray = "[]";
        boolean hasModelsByOrthology = false;
        if (modelAssociations.size() > 0) {
            List<String> jsons = new ArrayList<>();
            for (DiseaseModelAssociation assoc : modelAssociations) {
                jsons.add(assoc.makeModelJson());
                if (orthologousGenes.contains(assoc.getMarkerSymbol())) {
                    hasModelsByOrthology = true;
                }
            }
            modelAssocsJsArray = "[" + String.join(", ", jsons) + "]";
        }
        model.addAttribute("modelAssociations", modelAssocsJsArray);
        model.addAttribute("hasModelsByOrthology", hasModelsByOrthology);
        model.addAttribute("hasModelAssociations", modelAssociations.size() > 0);

        return "disease";
    }
}
