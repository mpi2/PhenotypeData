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

import java.util.List;

/**
 * Data access for phenodigm2
 *
 */
public interface WebDao {

    /**
     * Returns a Disease object for a given DiseaseIdentifier.
     *
     * @param diseaseId
     * @return
     */
    public Disease getDisease(String diseaseId);

    /**
     * Fetch a list of phenotypes associated to a disease by curation
     * 
     * @param diseaseId
     * @return 
     */
    public List<Phenotype> getDiseasePhenotypes(String diseaseId);
        
    /**
     * Fetch curated associations from disease to genes.
     *
     * @param diseaseId String with a disease identifier.
     *
     * @return
     *
     * A list of objects. Each element will contain information about a human
     * gene associated (via curation) to the disease. The information will also
     * contain links to mouse genes that are orthologous to the human gene.
     */
    public List<Gene> getDiseaseToGeneAssociations(String diseaseId);

    /**
     * Fetch curated associations from genes to diseases.
     *
     * @param geneId String with a gene identifier (either human or mouse)
     *
     * @return
     *
     * A list of objects. Each element will contain information about a gene-
     * disease association (via curation).
     */
    public List<Disease> getGeneToDiseaseAssociations(String geneId);

    
    /**
     * Fetch computed disease-model associations using a disease as a query.
     * This is used on the disease pages.
     *
     * @param diseaseId
     *
     * @return
     * 
     * list of models with scores that have been linked to the disease
     * 
     */
    public List<DiseaseModelAssociation> getDiseaseToModelModelAssociations(String diseaseId);

    /**
     * Fetch computed disease-model associations using a gene as a query. 
     * This is used on the gene pages. 
     *
     * @param geneId     
     * @return
     * 
     * list of objects. Each element will contain 
     */
    public List<DiseaseModelAssociation> getGeneToDiseaseModelAssociations(String geneId);
    
    /**
     * Fetch details explaining how a disease and mouse models are related.
     *
     * @param diseaseId String, disease identifier
     * @param markerId String, marker identifier (e.g. mouse gene Id MGI:xxxx)
     * @return
     *
     * A list of objects; one element per model. The elements will include
     * explanations for the matched phenotypes bewteen a disease and each model.
     *
     */
    public List<DiseaseModelAssociation> getDiseaseModelDetails(String diseaseId, String markerId);

    /**
     * Fetch details of all models that include a certain marker
     *
     * @param markerId String, marker identifier (e.g. mouse gene Id MGI:xxxx)
     * @return
     *
     * A list of objects; one element per model. The elements will include
     * phenotypes associated with the model.
     *
     */
    public List<MouseModel> getGeneModelDetails(String markerId);
}
