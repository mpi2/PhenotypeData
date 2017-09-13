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

    public List<Phenotype> getDiseasePhenotypes(String diseaseId);

    //public Gene getGene(String geneId);
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
    public List<GeneAssociation> getDiseaseToGeneAssociations(String diseaseId);

    /**
     * Fetch computed disease-model associations.
     *
     * @param diseaseId
     *
     * @return
     */
    public List<ModelAssociation> getDiseaseToModelAssociations(String diseaseId);

    /**
     * The gene page view. Use the minRawScoreCutoff to set the lower-level
     * limit for the phenodigm score of a gene-disease association. High
     * confidence scores are generally above 2.0
     *
     * @param geneId
     * @param minRawScoreCutoff
     * @return
     */
    //public List<DiseaseAssociationSummary> getGeneToDiseaseAssociationSummaries(GeneIdentifier geneId, double minRawScoreCutoff);
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
    public List<ModelAssociation> getDiseaseModelDetails(String diseaseId, String markerId);

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
    public List<Model> getGeneModelDetails(String markerId);
}
