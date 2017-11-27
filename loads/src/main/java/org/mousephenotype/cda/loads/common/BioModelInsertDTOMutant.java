/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.common;

import java.util.HashSet;
import java.util.Set;

public class BioModelInsertDTOMutant extends BioModelInsertDTO {
    
    protected Set<AccDbId> genes   = new HashSet<>();
    protected Set<AccDbId> alleles = new HashSet<>();
    protected Set<AccDbId> strains = new HashSet<>();


    /**
     * Constructs a mutant DTO for inserting biological models and their requisite components into the database
     *
     * @param dbId required
     * @param biologicalSamplePk required
     * @param allelicComposition required
     * @param geneticBackground required
     * @param zygosity required
     * @param gene a single gene that gets inserted into the empty genes set
     * @param allele a single allele that gets inserted into the empty alleles set
     * @param strain a single strain that gets inserted into the empty strains set
     */
    public BioModelInsertDTOMutant(int dbId, int biologicalSamplePk, String allelicComposition, String geneticBackground, String zygosity,
                                   AccDbId gene, AccDbId allele, AccDbId strain) {
        super(dbId, biologicalSamplePk, allelicComposition, geneticBackground, zygosity);
        this.genes.add(gene);
        this.alleles.add(allele);
        this.strains.add(strain);
    }

    public Set<AccDbId> getGenes() {
        return genes;
    }

    public void setGenes(Set<AccDbId> genes) {
        this.genes = genes;
    }

    public Set<AccDbId> getAlleles() {
        return alleles;
    }

    public void setAlleles(Set<AccDbId> alleles) {
        this.alleles = alleles;
    }

    public Set<AccDbId> getStrains() {
        return strains;
    }


    @Override
    public String toString() {
        return "BioModelInsertDTOMutant{" +
                "dbId=" + dbId +
                ", allelicComposition='" + allelicComposition + '\'' +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", zygosity='" + zygosity + '\'' +
                '}';
    }
}
