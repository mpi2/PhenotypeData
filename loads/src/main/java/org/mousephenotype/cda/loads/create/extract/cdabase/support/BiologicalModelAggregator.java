/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.create.extract.cdabase.support;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mrelac on 27/06/16.
 */
public class BiologicalModelAggregator {

    private String      allelicComposition;
    private String      alleleSymbol;
    private int         biologicalModelId;
    private String      geneticBackground;
    private Set<String> alleleAccessionIds = new HashSet<>();
    private Set<String> mpAccessionIds     = new HashSet<>();
    private Set<String> markerAccessionIds = new HashSet<>();

    public BiologicalModelAggregator() {

    }

    /**
     * Creates a new bioModel instance from the given one.
     * @param bioModel source bio model
     */
    public BiologicalModelAggregator(BiologicalModelAggregator bioModel) {
        clone(bioModel);
    }

    public BiologicalModelAggregator clone(BiologicalModelAggregator bioModel) {
        this.allelicComposition = bioModel.allelicComposition;
        this.alleleSymbol = bioModel.alleleSymbol;
        this.biologicalModelId = bioModel.biologicalModelId;
        this.geneticBackground = bioModel.geneticBackground;
        this.alleleAccessionIds = new HashSet<>();
        this.alleleAccessionIds.addAll(bioModel.getAlleleAccessionIds());
        this.mpAccessionIds = new HashSet<>();
        this.mpAccessionIds.addAll(bioModel.getMpAccessionIds());
        this.markerAccessionIds = new HashSet<>();
        this.markerAccessionIds.addAll(bioModel.getMarkerAccessionIds());

        return this;
    }

    public String getAllelicComposition() {
        return allelicComposition;
    }

    public void setAllelicComposition(String allelicComposition) {
        this.allelicComposition = allelicComposition;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public int getBiologicalModelId() {
        return biologicalModelId;
    }

    public void setBiologicalModelId(int biologicalModelId) {
        this.biologicalModelId = biologicalModelId;
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public Set<String> getAlleleAccessionIds() {
        return alleleAccessionIds;
    }

    public void setAlleleAccessionIds(Set<String> alleleAccessionIds) {
        this.alleleAccessionIds = alleleAccessionIds;
    }

    public Set<String> getMpAccessionIds() {
        return mpAccessionIds;
    }

    public void setMpAccessionIds(Set<String> mpAccessionIds) {
        this.mpAccessionIds = mpAccessionIds;
    }

    public Set<String> getMarkerAccessionIds() {
        return markerAccessionIds;
    }

    public void setMarkerAccessionIds(Set<String> markerAccessionIds) {
        this.markerAccessionIds = markerAccessionIds;
    }

    @Override
    public String toString() {
        return "BiologicalModelAggregator{" +
                "allelicComposition='" + allelicComposition + '\'' +
                ", alleleSymbol='" + alleleSymbol + '\'' +
                ", biologicalModelId=" + biologicalModelId +
                ", geneticBackground='" + geneticBackground + '\'' +
                '}';
    }
}
