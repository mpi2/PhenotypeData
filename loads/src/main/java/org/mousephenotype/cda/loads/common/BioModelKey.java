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

public class BioModelKey {

    private String specimenId;
    private int    phenotypingCenterPk;
    private String datasourceShortName;
    private String zygosity;


    public BioModelKey(String specimenId, int phenotypingCenterPk, String datasourceShortName, String zygosity) {
        this.specimenId = specimenId;
        this.phenotypingCenterPk = phenotypingCenterPk;
        this.datasourceShortName = datasourceShortName;
        this.zygosity = zygosity;
    }

    /**
     * Create and return a key that uniquely identifies a biological model
     *
     * @param specimenId the specimen id (also known as external_id or stableId). Not necessarily unique in itself amongst all centers.
     * @param phenotypingCenterPk the phenotyping center primary key (also known as the sample organisation key)
     * @param datasourceShortName the data source (e.g. EuroPhenome, WTSI, etc)
     * @param zygosity the specimen's zygosity
     *
     * @return a key that uniquely identifies a model
     */
    public static BioModelKey make(String specimenId, Integer phenotypingCenterPk, String datasourceShortName, String zygosity) {
        return new BioModelKey(specimenId, phenotypingCenterPk, datasourceShortName, zygosity);
    }

    public String getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(String specimenId) {
        this.specimenId = specimenId;
    }

    public int getPhenotypingCenterPk() {
        return phenotypingCenterPk;
    }

    public void setPhenotypingCenterPk(int phenotypingCenterPk) {
        this.phenotypingCenterPk = phenotypingCenterPk;
    }

    public String getDatasourceShortName() {
        return datasourceShortName;
    }

    public void setDatasourceShortName(String datasourceShortName) {
        this.datasourceShortName = datasourceShortName;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    @Override
    public boolean equals(Object o) {

        return (o.toString().equals(this.toString()));
    }

    @Override
    public int hashCode() {
        int result = this.toString().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return specimenId + "_" + phenotypingCenterPk + "_" + datasourceShortName + "_" + zygosity;
    }
}