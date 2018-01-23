/*
 * Copyright 2017 QMUL - Queen Mary University of London
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

/**
 * An extension of Disease. It links a gene identifier to information about a disease.
 *  
 */
public class GeneDiseaseAssociation extends Disease implements ByOrthology {
        
    // the query gene
    private String geneId;
    // is the association via an ortholog relation?
    private boolean byOrthology;    

    /**
     * Constructor of association between a disease and a gene
     * 
     * @param diseaseId
     * @param geneId 
     */
    public GeneDiseaseAssociation(String diseaseId, String geneId) {
        super(diseaseId);
        this.geneId = geneId;
    }
    
    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    @Override
    public boolean isByOrthology() {
        return byOrthology;
    }

    @Override
    public void setByOrthology(boolean byOrthology) {
        this.byOrthology = byOrthology;
    }           
}
