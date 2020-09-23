/*******************************************************************************
 * Copyright © 2020 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.solr.service.dto;

/**
 * DTO for the viability report
 */
public class ViabilityReportDTO {
    private String geneSymbol;
    private String geneAccessionId;
    private String alleleSymbol;
    private String alleleAccessionId;
    private String backgroundStrainName;
    private String backgroundStrainAccessionId;
    private String chromosome;
    private String phenotypingCenter;
    private String colonyId;
    private String metadataGroup;
    private String breedingStrategy;
    private String totalPups;

    private String totalMalePups;
    private String totalMaleWt;
    private String totalMaleHem;
    private String totalMaleHom;
    private String totalMaleHet;

    private String totalFemalePups;
    private String totalFemaleWt;
    private String totalFemaleHom;
    private String totalFemaleHet;
    private String totalFemaleAnz;

    private String percentageHoms;
    private String supportingData;
    private String viabilityPhenotype;
    private String viabilityCallMethod;
    private String comment;
    private String impressInfoLink;

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getGeneAccessionId() {
        return geneAccessionId;
    }

    public void setGeneAccessionId(String geneAccessionId) {
        this.geneAccessionId = geneAccessionId;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public String getAlleleAccessionId() {
        return alleleAccessionId;
    }

    public void setAlleleAccessionId(String alleleAccessionId) {
        this.alleleAccessionId = alleleAccessionId;
    }

    public String getBackgroundStrainName() {
        return backgroundStrainName;
    }

    public void setBackgroundStrainName(String backgroundStrainName) {
        this.backgroundStrainName = backgroundStrainName;
    }

    public String getBackgroundStrainAccessionId() {
        return backgroundStrainAccessionId;
    }

    public void setBackgroundStrainAccessionId(String backgroundStrainAccessionId) {
        this.backgroundStrainAccessionId = backgroundStrainAccessionId;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public String getPhenotypingCenter() {
        return phenotypingCenter;
    }

    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public String getMetadataGroup() {
        return metadataGroup;
    }

    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    public String getBreedingStrategy() {
        return breedingStrategy;
    }

    public void setBreedingStrategy(String breedingStrategy) {
        this.breedingStrategy = breedingStrategy;
    }

    public String getTotalPups() {
        return totalPups;
    }

    public void setTotalPups(String totalPups) {
        this.totalPups = totalPups;
    }

    public String getTotalMalePups() {
        return totalMalePups;
    }

    public void setTotalMalePups(String totalMalePups) {
        this.totalMalePups = totalMalePups;
    }

    public String getTotalMaleWt() {
        return totalMaleWt;
    }

    public void setTotalMaleWt(String totalMaleWt) {
        this.totalMaleWt = totalMaleWt;
    }

    public String getTotalMaleHem() {
        return totalMaleHem;
    }

    public void setTotalMaleHem(String totalMaleHem) {
        this.totalMaleHem = totalMaleHem;
    }

    public String getTotalMaleHom() {
        return totalMaleHom;
    }

    public void setTotalMaleHom(String totalMaleHom) {
        this.totalMaleHom = totalMaleHom;
    }

    public String getTotalMaleHet() {
        return totalMaleHet;
    }

    public void setTotalMaleHet(String totalMaleHet) {
        this.totalMaleHet = totalMaleHet;
    }

    public String getTotalFemalePups() {
        return totalFemalePups;
    }

    public void setTotalFemalePups(String totalFemalePups) {
        this.totalFemalePups = totalFemalePups;
    }

    public String getTotalFemaleWt() {
        return totalFemaleWt;
    }

    public void setTotalFemaleWt(String totalFemaleWt) {
        this.totalFemaleWt = totalFemaleWt;
    }

    public String getTotalFemaleHom() {
        return totalFemaleHom;
    }

    public void setTotalFemaleHom(String totalFemaleHom) {
        this.totalFemaleHom = totalFemaleHom;
    }

    public String getTotalFemaleHet() {
        return totalFemaleHet;
    }

    public void setTotalFemaleHet(String totalFemaleHet) {
        this.totalFemaleHet = totalFemaleHet;
    }

    public String getTotalFemaleAnz() {
        return totalFemaleAnz;
    }

    public void setTotalFemaleAnz(String totalFemaleAnz) {
        this.totalFemaleAnz = totalFemaleAnz;
    }

    public String getPercentageHoms() {
        return percentageHoms;
    }

    public void setPercentageHoms(String percentageHoms) {
        this.percentageHoms = percentageHoms;
    }

    public String getSupportingData() {
        return supportingData;
    }

    public void setSupportingData(String supportingData) {
        this.supportingData = supportingData;
    }

    public String getViabilityPhenotype() {
        return viabilityPhenotype;
    }

    public void setViabilityPhenotype(String viabilityPhenotype) {
        this.viabilityPhenotype = viabilityPhenotype;
    }

    public String getViabilityCallMethod() {
        return viabilityCallMethod;
    }

    public void setViabilityCallMethod(String viabilityCallMethod) {
        this.viabilityCallMethod = viabilityCallMethod;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImpressProcedureLink() {
        return impressInfoLink;
    }

    public void setImpressInfoLink(String impressInfoLink) {
        this.impressInfoLink = impressInfoLink;
    }
}
