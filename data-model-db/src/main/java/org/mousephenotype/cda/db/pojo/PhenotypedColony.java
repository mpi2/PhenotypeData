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

package org.mousephenotype.cda.db.pojo;

/**
 * Created by mrelac on 13/07/16.
 */
public class PhenotypedColony {
    private int id;
    private String colonyName;
    private String es_cell_name;
    private GenomicFeature gene;
    private String alleleSymbol;
    private String backgroundStrain;
    private String backgroundStrainAcc;
    private Organisation phenotypingCentre;
    private Project phenotypingConsortium;
    private Organisation productionCentre;
    private Project productionConsortium;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColonyName() {
        return colonyName;
    }

    public void setColonyName(String colonyName) {
        this.colonyName = colonyName;
    }

    public String getEs_cell_name() {
        return es_cell_name;
    }

    public void setEs_cell_name(String es_cell_name) {
        this.es_cell_name = es_cell_name;
    }

    public GenomicFeature getGene() {
        return gene;
    }

    public void setGene(GenomicFeature gene) {
        this.gene = gene;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public String getBackgroundStrain() {
        return backgroundStrain;
    }

    public String getBackgroundStrainAcc() {
        return backgroundStrainAcc;
    }

    public void setBackgroundStrain(String backgroundStrain) {
        this.backgroundStrain = backgroundStrain;
    }

    public void setBackgroundStrainAcc(String backgroundStrainAcc) {
        this.backgroundStrainAcc = backgroundStrainAcc;
    }

    public Organisation getPhenotypingCentre() {
        return phenotypingCentre;
    }

    public void setPhenotypingCentre(Organisation phenotypingCentre) {
        this.phenotypingCentre = phenotypingCentre;
    }

    public Project getPhenotypingConsortium() {
        return phenotypingConsortium;
    }

    public void setPhenotypingConsortium(Project phenotypingConsortium) {
        this.phenotypingConsortium = phenotypingConsortium;
    }

    public Organisation getProductionCentre() {
        return productionCentre;
    }

    public void setProductionCentre(Organisation productionCentre) {
        this.productionCentre = productionCentre;
    }

    public Project getProductionConsortium() {
        return productionConsortium;
    }

    public void setProductionConsortium(Project productionConsortium) {
        this.productionConsortium = productionConsortium;
    }

    @Override
    public String toString() {
        return "PhenotypedColony{" +
                "id=" + id +
                ", colonyName='" + colonyName + '\'' +
                ", es_cell_name='" + (es_cell_name == null ? "null" : es_cell_name) + '\'' +
                ", gene=" + gene +
                ", alleleSymbol=" + alleleSymbol +
                ", backgroundStrain=" + backgroundStrain +
                ", backgroundStrainAcc=" + backgroundStrainAcc +
                ", phenotypingCentre=" + phenotypingCentre +
                ", phenotypingConsortium=" + phenotypingConsortium +
                ", productionCentre=" + productionCentre +
                ", productionConsortium=" + productionConsortium +
                '}';
    }
}
