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

/**
 * 2017-10-09 (mrelac)
 *
 * This class encapsulates the components of a biological model lookup key.
 */

public abstract class BioModelKey {

    private Integer dbId;
    private String allelicComposition;
    private String geneticBackground;
    private String zygosity;
    private int phenotypingCenterPk;


    public BioModelKey(Integer dbId, String allelicComposition, String geneticBackground, String zygosity, int phenotypingCenterPk) {
        this.dbId = dbId;
        this.allelicComposition = allelicComposition;
        this.geneticBackground = geneticBackground;
        this.zygosity = zygosity;
        this.phenotypingCenterPk = phenotypingCenterPk;
    }


    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getAllelicComposition() {
        return allelicComposition;
    }

    public void setAllelicComposition(String allelicComposition) {
        this.allelicComposition = allelicComposition;
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

    public int getPhenotypingCenterPk() {
        return phenotypingCenterPk;
    }

    public void setPhenotypingCenterPk(int phenotypingCenterPk) {
        this.phenotypingCenterPk = phenotypingCenterPk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BioModelKey that = (BioModelKey) o;

        if (dbId != null ? !dbId.equals(that.dbId) : that.dbId != null) return false;
        if (allelicComposition != null ? !allelicComposition.equals(that.allelicComposition) : that.allelicComposition != null)
            return false;
        if (geneticBackground != null ? !geneticBackground.equals(that.geneticBackground) : that.geneticBackground != null)
            return false;
        return zygosity != null ? zygosity.equals(that.zygosity) : that.zygosity == null;
    }

    @Override
    public int hashCode() {
        int result = dbId != null ? dbId.hashCode() : 0;
        result = 31 * result + (allelicComposition != null ? allelicComposition.hashCode() : 0);
        result = 31 * result + (geneticBackground != null ? geneticBackground.hashCode() : 0);
        result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return dbId + "_" + allelicComposition + "_" + geneticBackground + "_" + (zygosity == null ? "" : zygosity);
    }
}
