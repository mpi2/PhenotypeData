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
    private GenomicFeature gene;
    private String colonyName;
    private String es_cell_name;
    private String backgroundStrain;
    private Organisation productionCentre;
    private Project productionConsortium;
    private Organisation phenotypingCentre;
    private Project phenotypingConsortium;
    private Organisation CohortProductionCentre;
    private String alleleSymbol;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GenomicFeature getGene() {
        return gene;
    }

    public void setGene(GenomicFeature gene) {
        this.gene = gene;
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

    public String getBackgroundStrainName() {
        return backgroundStrain;
    }

    public void setBackgroundStrainName(String backgroundStrain) {
        this.backgroundStrain = backgroundStrain;
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

    public Organisation getCohortProductionCentre() {
        return CohortProductionCentre;
    }

    public void setCohortProductionCentre(Organisation cohortProductionCentre) {
        CohortProductionCentre = cohortProductionCentre;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    @Override
    public String toString() {
        return "PhenotypedColony{" +
                "id=" + id +
                ", gene=" + gene +
                ", colonyName='" + colonyName + '\'' +
                ", es_cell_name='" + (es_cell_name == null ? "null" : es_cell_name) + '\'' +
                ", backgroundStrain=" + backgroundStrain +
                ", productionCentre=" + productionCentre +
                ", productionConsortium=" + productionConsortium +
                ", phenotypingCentre=" + phenotypingCentre +
                ", phenotypingConsortium=" + phenotypingConsortium +
                ", CohortProductionCentre=" + CohortProductionCentre +
                ", alleleSymbol=" + alleleSymbol +
                '}';
    }
}
