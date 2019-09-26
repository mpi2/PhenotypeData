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

/**
 * 2017-10-04 Created by mrelac . This class encapsulates the code and data necessary to insert into the
 * biological_model table and its related tables: biological_model_genomic_feature, biological_model_allele,
 * biological_model_strain, biological_model_phenotype, and biological_model_sample.
 *
 * Biological model inserts require different parameters depending on the type of model that is being inserted:
 * <ul>
 *     <li>MGI models</li>
 *     <li>line-level with mutant specimen</li>
 *     <li>line-level with control specimen</li>
 *     <li>experiment-level with mutant specimens</li>
 *     <li>experiment-level with control specimens</li>
 * </ul>
 *
 * This class is the parent of the subclasses listed above.
 */
package org.mousephenotype.cda.loads.common;

public abstract class BioModelInsertDTO {
    protected Long   dbId;
    protected String allelicComposition;
    protected Long   biologicalSamplePk;
    protected String geneticBackground;
    protected String zygosity;


    public BioModelInsertDTO(Long dbId, Long biologicalSamplePk, String allelicComposition, String geneticBackground, String zygosity) {
        this.dbId = dbId;
        this.biologicalSamplePk = biologicalSamplePk;
        this.allelicComposition = allelicComposition;
        this.geneticBackground = geneticBackground;
        this.zygosity = zygosity;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getAllelicComposition() {
        return allelicComposition;
    }

    public void setAllelicComposition(String allelicComposition) {
        this.allelicComposition = allelicComposition;
    }

    public Long getBiologicalSamplePk() {
        return biologicalSamplePk;
    }

    public void setBiologicalSamplePk(Long biologicalSamplePk) {
        this.biologicalSamplePk = biologicalSamplePk;
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public String getCompositeKey() {
        return getCompositeKey(dbId, allelicComposition,  geneticBackground, zygosity);
    }
    public static String getCompositeKey(long dbId, String allelicComposition, String geneticBackground, String zygosity) {
        return dbId + "_" + allelicComposition + "_" + geneticBackground + "_" + (zygosity == null ? "" : zygosity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BioModelInsertDTO that = (BioModelInsertDTO) o;

        if (!dbId.equals(that.dbId)) return false;
        if (!allelicComposition.equals(that.allelicComposition)) return false;
        if (!geneticBackground.equals(that.geneticBackground)) return false;
        return zygosity != null ? zygosity.equals(that.zygosity) : that.zygosity == null;
    }

    @Override
    public int hashCode() {
        int result = dbId.hashCode();
        result = 31 * result + allelicComposition.hashCode();
        result = 31 * result + geneticBackground.hashCode();
        result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BioModelInsertDTO{" +
                "dbId=" + dbId +
                ", allelicComposition='" + allelicComposition + '\'' +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", zygosity='" + zygosity + '\'' +
                '}';
    }
}
