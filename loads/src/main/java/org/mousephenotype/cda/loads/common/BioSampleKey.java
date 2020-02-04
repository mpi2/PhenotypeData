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

import java.util.Objects;

/**
 * This class is meant to encapsulate a key that uniquely identifies a sample.
 */
public class BioSampleKey {

    private String specimenId;
    private Long phenotypingCenterPk;

    public BioSampleKey(String specimenId, Long phenotypingCenterPk) {
        this.specimenId = specimenId;
        this.phenotypingCenterPk = phenotypingCenterPk;
    }

    /**
     * Create and return a key that uniquely identifies a biological sample
     *
     * @param specimenId the specimen id (also known as external_id or stableId). Not necessarily unique in itself amongst all centers.
     * @param phenotypingCenterPk the phenotyping center primary key (also known as the sample organisation key)
     *
     * @return a key that uniquely identifies a sample
     */
    public static BioSampleKey make(String specimenId, Long phenotypingCenterPk) {
        return new BioSampleKey(specimenId, phenotypingCenterPk);
    }

    public String getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(String specimenId) {
        this.specimenId = specimenId;
    }

    public Long getPhenotypingCenterPk() {
        return phenotypingCenterPk;
    }

    public void setPhenotypingCenterPk(Long phenotypingCenterPk) {
        this.phenotypingCenterPk = phenotypingCenterPk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BioSampleKey)) return false;
        BioSampleKey that = (BioSampleKey) o;
        return specimenId.equals(that.specimenId) &&
                phenotypingCenterPk.equals(that.phenotypingCenterPk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specimenId, phenotypingCenterPk);
    }

    @Override
    public String toString() {
        return specimenId + "_" + phenotypingCenterPk;
    }
}
