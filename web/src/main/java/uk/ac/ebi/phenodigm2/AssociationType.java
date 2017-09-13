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
 * Object describing a type of association - is it curated or computed etc.
 * 
 * 
 */
public class AssociationType {
    
    // Fields answer questions like "Is the association ... ?"            
    // assigned by a human person
    private boolean curated;
    // assigned by orthology
    private boolean ortholog;
    // determined by a genomic location, e.g. gene is in a genomic locus
    private boolean genomic;
    // general description
    private String description;

    /**
     * Constructor does nothing; set all association types using setters.
     */
    public AssociationType() {
    }

    public boolean isCurated() {
        return curated;
    }

    public void setCurated(boolean curated) {
        this.curated = curated;
    }

    public boolean isOrtholog() {
        return ortholog;
    }

    public void setOrtholog(boolean ortholog) {
        this.ortholog = ortholog;
    }

    public boolean isGenomic() {
        return genomic;
    }

    public void setGenomic(boolean genomic) {
        this.genomic = genomic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AssociationType{" + "curated=" + curated + ", genomic=" + genomic + ", description=" + description + '}';
    }
    
}
