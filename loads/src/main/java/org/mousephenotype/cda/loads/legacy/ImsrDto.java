/*******************************************************************************
 *  Copyright (c) 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.legacy;

import java.util.List;

/**
 * This class encapsulates the code and data necessary to represent an IMSR input file record.
 *
 * Created by mrelac on 26/06/2015.
 */
public class ImsrDto {
    private String nomenclature;
    private String strainId;
    private String strainStock;
    private String repository;
    private String state;
    private List<String> synonyms;
    private String type;
    private String alleleId;
    private String alleleSymbol;
    private String alleleName;
    private String geneId;
    private String geneSymbol;
    private String geneName;

    public ImsrDto() {

    }

    public ImsrDto(String nomenclature, String strainId, String strainStock, String repository, String state, List<String> synonyms, String type, String alleleId, String alleleSymbol, String alleleName, String geneId, String geneSymbol, String geneName) {
        this.nomenclature = nomenclature;
        this.strainId = strainId;
        this.strainStock = strainStock;
        this.repository = repository;
        this.state = state;
        this.synonyms = synonyms;
        this.type = type;
        this.alleleId = alleleId;
        this.alleleSymbol = alleleSymbol;
        this.alleleName = alleleName;
        this.geneId = geneId;
        this.geneSymbol = geneSymbol;
        this.geneName = geneName;
    }

    public String getNomenclature() {
        return nomenclature;
    }

    public void setNomenclature(String nomenclature) {
        this.nomenclature = nomenclature;
    }

    public String getStrainId() {
        return strainId;
    }

    public void setStrainId(String strainId) {
        this.strainId = strainId;
    }

    public String getStrainStock() {
        return strainStock;
    }

    public void setStrainStock(String strainStock) {
        this.strainStock = strainStock;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlleleId() {
        return alleleId;
    }

    public void setAlleleId(String alleleId) {
        this.alleleId = alleleId;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public String getAlleleName() {
        return alleleName;
    }

    public void setAlleleName(String alleleName) {
        this.alleleName = alleleName;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getGeneName() {
        return geneName;
    }

    public void setGeneName(String geneName) {
        this.geneName = geneName;
    }

    @Override
    public String toString() {
        return "ImsrDto{" +
                "nomenclature='" + nomenclature + '\'' +
                ", strainId='" + strainId + '\'' +
                ", strainStock='" + strainStock + '\'' +
                ", repository='" + repository + '\'' +
                ", state='" + state + '\'' +
                ", synonyms=" + synonyms +
                ", type='" + type + '\'' +
                ", alleleId='" + alleleId + '\'' +
                ", alleleSymbol='" + alleleSymbol + '\'' +
                ", alleleName='" + alleleName + '\'' +
                ", geneId='" + geneId + '\'' +
                ", geneSymbol='" + geneSymbol + '\'' +
                ", geneName='" + geneName + '\'' +
                '}';
    }
}
