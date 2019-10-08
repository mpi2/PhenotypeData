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

public class BioModelInsertDTOMGI extends BioModelInsertDTO {

    protected Set<AccDbId> genes      = new HashSet<>();
    protected Set<AccDbId> alleles    = new HashSet<>();
    protected Set<AccDbId> phenotypes = new HashSet<>();


    public BioModelInsertDTOMGI(long dbId, String allelicComposition, String geneticBackground, String zygosity, AccDbId gene, AccDbId alleleSymbol, AccDbId phenotype) {
        super(dbId, null, allelicComposition, geneticBackground, zygosity);
        this.genes.add(gene);
        this.alleles.add( alleleSymbol);
        this.phenotypes.add(phenotype);
    }


    public BioModelInsertDTOMGI(long dbId, String allelicComposition, String geneticBackground, String zygosity) {
        super(dbId, null, allelicComposition, geneticBackground, zygosity);
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

    public Set<AccDbId> getPhenotypes() {
        return phenotypes;
    }

    public void setPhenotypes(Set<AccDbId> phenotypes) {
        this.phenotypes = phenotypes;
    }

    @Override
    public String toString() {
        return "BioModelInsertDTOMGI{" +
                "dbId=" + dbId +
                ", allelicComposition='" + allelicComposition + '\'' +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", zygosity='" + zygosity + '\'' +
                '}';
    }
}
