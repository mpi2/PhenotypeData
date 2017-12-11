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
package uk.ac.ebi.phenodigm.model;

import java.util.Objects;

/**
 * Class for holding allele data.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 * 
 * @deprecated pdsimplify
 */
public class Allele {

    private final String source;
    private final String geneId;
    private final String alleleSymbol;
    private final String alleleId;

    public Allele(String source, String geneId, String alleleSymbol, String alleleId) {
        this.source = source;
        this.geneId = geneId;
        this.alleleSymbol = alleleSymbol;
        this.alleleId = alleleId;
    }

    public String getSource() {
        return source;
    }

    public String getGeneId() {
        return geneId;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public String getAlleleId() {
        return alleleId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.source);
        hash = 67 * hash + Objects.hashCode(this.geneId);
        hash = 67 * hash + Objects.hashCode(this.alleleSymbol);
        hash = 67 * hash + Objects.hashCode(this.alleleId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Allele other = (Allele) obj;
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.geneId, other.geneId)) {
            return false;
        }
        if (!Objects.equals(this.alleleSymbol, other.alleleSymbol)) {
            return false;
        }
        return Objects.equals(this.alleleId, other.alleleId);
    }

    @Override
    public String toString() {
        return "Allele{" + "source=" + source + ", geneId=" + geneId + ", alleleId=" + alleleId + ", alleleSymbol=" + alleleSymbol + '}';
    }
}
