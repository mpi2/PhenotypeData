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
 * An extension of Gene. It links a disease identifier to information about a gene.
 * 
 */
public class DiseaseGeneAssociation extends Gene implements ByOrthology {
    
    private String diseaseId;
    private boolean byOrthology;
    
    /**
     * 
     * @param id
     * 
     * gene identifier
     * 
     * @param symbol
     * 
     * gene symbol
     * 
     * @param diseaseId 
     * 
     * disease identifier
     * 
     */
    public DiseaseGeneAssociation(String id, String symbol, String diseaseId) {
        super(id, symbol);
        this.diseaseId = diseaseId;        
    }

    @Override
    public boolean isByOrthology() {
        return byOrthology;
    }

    @Override
    public void setByOrthology(boolean byOrthology) {
        this.byOrthology = byOrthology;
    }   

    public String getDiseaseId() {
        return diseaseId;
    }    

}
