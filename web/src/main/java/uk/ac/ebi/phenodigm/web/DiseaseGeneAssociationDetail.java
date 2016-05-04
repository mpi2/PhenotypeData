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

package uk.ac.ebi.phenodigm.web;

import java.util.List;
import uk.ac.ebi.phenodigm.model.DiseaseModelAssociation;
import uk.ac.ebi.phenodigm.model.DiseaseIdentifier;
import uk.ac.ebi.phenodigm.model.Gene;
import uk.ac.ebi.phenodigm.model.PhenotypeTerm;

/**
 * Holds the mappings for the disease to mouse models for a given association.
 * 
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class DiseaseGeneAssociationDetail {
    
    private final DiseaseIdentifier diseaseId;
    private final Gene gene;
    private final List<PhenotypeTerm> diseasePhenotypes;
    private final List<DiseaseModelAssociation> diseaseAssociations;

    public DiseaseGeneAssociationDetail(DiseaseIdentifier diseaseId, List<PhenotypeTerm> diseasePhenotypes, Gene gene, List<DiseaseModelAssociation> diseaseAssociations) {
        this.diseaseId = diseaseId;
        this.gene = gene;
        this.diseasePhenotypes = diseasePhenotypes;
        this.diseaseAssociations = diseaseAssociations;
    }

    public DiseaseIdentifier getDiseaseId() {
        return diseaseId;
    }

    public Gene getGene() {
        return gene;
    }

    public List<PhenotypeTerm> getDiseasePhenotypes() {
        return diseasePhenotypes;
    }

    public List<DiseaseModelAssociation> getDiseaseAssociations() {
        return diseaseAssociations;
    }
    
}
