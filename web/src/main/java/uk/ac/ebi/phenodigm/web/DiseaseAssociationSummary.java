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

import uk.ac.ebi.phenodigm.model.DiseaseIdentifier;


/**
 * Provides a direct-mapping between the data on the disease associations page and the model.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class DiseaseAssociationSummary {
    
    private final DiseaseIdentifier diseaseId;
    private final String diseaseTerm;
    private final AssociationSummary associationSummary;
    
    public DiseaseAssociationSummary(DiseaseIdentifier diseaseId, String diseaseTerm, AssociationSummary associationSummary) {
        this.associationSummary = associationSummary;
        this.diseaseId = diseaseId;
        this.diseaseTerm = diseaseTerm;
    }

    public DiseaseIdentifier getDiseaseIdentifier() {
        return diseaseId;
    }

    public String getDiseaseTerm() {
        return diseaseTerm;
    }

    public AssociationSummary getAssociationSummary() {
        return associationSummary;
    }

    @Override
    public String toString() {
        return String.format("DiseaseAssociationSummary{%s %s}", diseaseId.getCompoundIdentifier(), associationSummary);
    }
 
    
}
