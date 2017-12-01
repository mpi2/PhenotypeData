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

import java.util.ArrayList;
import java.util.List;

/**
 * Object describing a gene.
 * 
 * It is primarily a container class for: gene id, an official symbol, and a 
 * list of withdrawn symbols.
 * 
 */
public class GeneIdentifier implements Comparable<GeneIdentifier> {

    private final String geneId;
    private final String geneSymbol;
    private final List<String> geneSymbolsWithdrawn = new ArrayList<>();

    public GeneIdentifier(String geneId, String geneSymbol) {
        this.geneId = geneId;
        this.geneSymbol = geneSymbol;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public List<String> getGeneSymbolsWithdrawn() {
        return geneSymbolsWithdrawn;
    }

    public void setGeneSymbolsWithdrawn(List<String> symbols) {
        this.geneSymbolsWithdrawn.clear();
        for (String withdrawn : symbols) {
            this.geneSymbolsWithdrawn.add(withdrawn);
        }
    }

    /**
     * Comparator (primarily by gene symbol to enable human-readable sorting)
     * 
     * @param other
     * @return
     */
    @Override
    public int compareTo(GeneIdentifier other) {
        int symbolcompare = this.geneSymbol.compareTo(other.geneSymbol);
        // in abnormal case when symbols match, also check ids
        if (symbolcompare ==0) {
            return this.geneId.compareTo(other.geneId);
        }
        return symbolcompare;        
    }    
   
}
