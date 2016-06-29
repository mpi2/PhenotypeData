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

package org.mousephenotype.cda.loads.cdaloader.support;

/**
 * Created by mrelac on 27/06/16.
 */



public class BiologicalModelAggregator {

    private String allelicComposition;
    private String alleleSymbol;
    private int    biologicalModelId;
    private String geneticBackground;
    private String alleleAccessionId;
    private String mpAccessionId;
    private String markerAccessionId;

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

    public String getAlleleAccessionId() {
        return alleleAccessionId;
    }

    public void setAlleleAccessionId(String alleleAccessionId) {
        this.alleleAccessionId = alleleAccessionId;
    }

    public String getMpAccessionId() {
        return mpAccessionId;
    }

    public void setMpAccessionId(String mpAccessionId) {
        this.mpAccessionId = mpAccessionId;
    }

    public String getMarkerAccessionId() {
        return markerAccessionId;
    }

    public void setMarkerAccessionId(String markerAccessionId) {
        this.markerAccessionId = markerAccessionId;
    }

    @Override
    public String toString() {
        return "BiologicalModelAggregator{" +
                "allelicComposition='" + allelicComposition + '\'' +
                ", alleleSymbol='" + alleleSymbol + '\'' +
                ", biologicalModelId=" + biologicalModelId +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", alleleAccessionId='" + alleleAccessionId + '\'' +
                ", mpAccessionId='" + mpAccessionId + '\'' +
                ", markerAccessionId='" + markerAccessionId + '\'' +
                '}';
    }
}
