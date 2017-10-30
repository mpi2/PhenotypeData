/*
 * Copyright 2017 QMUL - Queen Mary University of London
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenodigm2;

import java.util.ArrayList;
import java.util.List;

/**
 * Object describing an association from a disease to a model.
 *
 * The class defines many fields, but a constructed object is not guaranteed to
 * have values for all of them.
 *
 */
public class DiseaseModelAssociation extends AssociationType implements IdUrl {

    // id here stands for model id
    private String id;
    private String modelSource;
    private String modelGeneticBackground;
    private String modelDescription;
    // data about the disease
    private String diseaseId;
    private String diseaseTerm;
    // markers id and symbol refer to the gene modified in the model
    private String markerId;
    private String markerSymbol;
    private String markerLocus;
    private int markerNumModels;
    private double avgNorm;
    private double avgRaw;
    private double maxNorm;
    private double maxRaw;
    // The lists are denoted as disease and model phenotypes,
    // but solr will use these as matched phenotypes, i.e. only those
    // phenotypes in disease and model that contribute to the scores
    // DEV NOTE: these are no longer used.
    private List<Phenotype> diseasePhenotypes;
    private List<Phenotype> modelPhenotypes;

    public DiseaseModelAssociation(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getModelSource() {
        return modelSource;
    }

    public void setModelSource(String modelSource) {
        this.modelSource = modelSource;
    }

    public String getModelGeneticBackground() {
        return modelGeneticBackground;
    }

    public void setModelGeneticBackground(String modelGeneticBackground) {
        this.modelGeneticBackground = modelGeneticBackground;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getDiseaseTerm() {
        return diseaseTerm;
    }

    public void setDiseaseTerm(String diseaseTerm) {
        this.diseaseTerm = diseaseTerm;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMarkerLocus() {
        return markerLocus;
    }

    public void setMarkerLocus(String markerLocus) {
        this.markerLocus = markerLocus;
    }

    public int getMarkerNumModels() {
        return markerNumModels;
    }

    public void setMarkerNumModels(int markerNumModels) {
        this.markerNumModels = markerNumModels;
    }

    public List<Phenotype> getDiseasePhenotypes() {
        return diseasePhenotypes;
    }

    public void setDiseasePhenotypes(List<Phenotype> diseasePhenotypes) {
        this.diseasePhenotypes = diseasePhenotypes;
    }

    /**
     * Like a constructor, but parse phenotype objects form id+term strings
     *
     * @param diseasePhenotypes
     */
    public void parseDiseasePhenotypes(List<String> diseasePhenotypes) {
        this.diseasePhenotypes = new ArrayList<>();
        for (String phenotype : diseasePhenotypes) {
            this.diseasePhenotypes.add(new Phenotype(phenotype));
        }
    }

    public List<Phenotype> getModelPhenotypes() {
        return modelPhenotypes;
    }

    public void setModelPhenotypes(List<Phenotype> modelPhenotypes) {
        this.modelPhenotypes = modelPhenotypes;
    }

    /**
     * Like a setter, but parses phenotype objects from id+term strings.
     *
     * @param modelPhenotypes
     */
    public void parseModelPhenotypes(List<String> modelPhenotypes) {
        this.modelPhenotypes = new ArrayList<>();
        for (String phenotype : modelPhenotypes) {
            this.modelPhenotypes.add(new Phenotype(phenotype));
        }
    }

    public double getAvgNorm() {
        return avgNorm;
    }

    public void setAvgNorm(double avgNorm) {
        this.avgNorm = avgNorm;
    }

    public double getAvgRaw() {
        return avgRaw;
    }

    public void setAvgRaw(double avgRaw) {
        this.avgRaw = avgRaw;
    }

    public double getMaxNorm() {
        return maxNorm;
    }

    public void setMaxNorm(double maxNorm) {
        this.maxNorm = maxNorm;
    }

    public double getMaxRaw() {
        return maxRaw;
    }

    public void setMaxRaw(double maxRaw) {
        this.maxRaw = maxRaw;
    }

    /**
     * Computes phenodigm score (average of avg and max norm scores)
     *
     * @return
     */
    public double getScore() {
        return (maxNorm + avgNorm) * 0.5;
    }

    @Override
    public String getExternalUrl() {
        return "Todo: " + id;
    }

    @Override
    public String toString() {
        return "ModelAssociation{" + "id=" + id + ", modelSource=" + modelSource + ", modelGeneticBackground=" + modelGeneticBackground + ", modelDescription=" + modelDescription + ", markerId=" + markerId + ", markerSymbol=" + markerSymbol + ", markerLocus=" + markerLocus + ", markerNumModels=" + markerNumModels + ", avgNorm=" + avgNorm + ", avgRaw=" + avgRaw + ", maxNorm=" + maxNorm + ", maxRaw=" + maxRaw + '}';
    }

    /**
     * Create a json representation of the object with focus on models.
     *
     * This renames a few fields with shorter names - saves somes bytes when
     * moving the json around.
     *
     * @return
     */
    public String getModelJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id:\"" + id + "\",");
        sb.append("source:\"" + modelSource + "\",");
        sb.append("description:\"" + modelDescription + "\",");
        sb.append("background:\"" + modelGeneticBackground + "\",");
        sb.append("markerId:\"" + markerId + "\",");
        sb.append("markerSymbol:\"" + markerSymbol + "\",");
        sb.append("markerNumModels:\"" + markerNumModels + "\",");
        sb.append("avgNorm:" + avgNorm + ",");
        sb.append("avgRaw:" + avgRaw + ",");
        sb.append("maxRaw:" + maxRaw + ",");
        sb.append("maxNorm:" + maxNorm);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Create a json representation of the object with focus on diseases.
     *
     * @return
     */
    public String getDiseaseJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id:\"" + id + "\",");
        sb.append("source:\"" + modelSource + "\",");
        sb.append("description:\"" + modelDescription + "\",");
        sb.append("diseaseId:\"" + diseaseId + "\",");
        sb.append("diseaseTerm:\"" + diseaseTerm + "\",");
        sb.append("diseaseUrl:\"" + (new Disease(diseaseId)).getExternalUrl() + "\",");
        sb.append("avgNorm:" + avgNorm + ",");
        sb.append("avgRaw:" + avgRaw + ",");
        sb.append("maxRaw:" + maxRaw + ",");
        sb.append("maxNorm:" + maxNorm);
        sb.append("}");
        return sb.toString();
    }
}
