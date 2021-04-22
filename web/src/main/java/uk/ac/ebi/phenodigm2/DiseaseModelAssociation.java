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

import lombok.ToString;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.util.List;

/**
 * Object describing an association from a disease to a model.
 *
 */
@ToString
public class DiseaseModelAssociation extends MouseModel implements ByOrthology {

    // information about the model is inherited from MouseModel
    // fields here identify the disease
    private String diseaseId;
    private String diseaseTerm;
    // details of the association
    private int markerNumModels;
    private double avgNorm;
    private double avgRaw;
    private double maxNorm;
    private double maxRaw;
    private List<String> diseaseMatchedPhenotypes;
    private List<String> modelMatchedPhenotypes;

    // is the association via an byOrthology relation?
    private boolean byOrthology;    

    public DiseaseModelAssociation(String modelId, String diseaseId) {
        super(modelId);
        this.diseaseId = diseaseId;
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

    public int getMarkerNumModels() {
        return markerNumModels;
    }

    public void setMarkerNumModels(int markerNumModels) {
        this.markerNumModels = markerNumModels;
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

    @Override
    public boolean isByOrthology() {
        return byOrthology;
    }

    public void setByOrthology(boolean byOrthology) {
        this.byOrthology = byOrthology;
    }

    public List<String> getDiseaseMatchedPhenotypes() {
        return diseaseMatchedPhenotypes;
    }

    public void setDiseaseMatchedPhenotypes(List<String> diseaseMatchedPhenotypes) {
        this.diseaseMatchedPhenotypes = diseaseMatchedPhenotypes;
    }

    public List<String> getModelMatchedPhenotypes() {
        return modelMatchedPhenotypes;
    }

    public void setModelMatchedPhenotypes(List<String> modelMatchedPhenotypes) {
        this.modelMatchedPhenotypes = modelMatchedPhenotypes;
    }

    /**
     * Computes phenodigm score (average of avg and max norm scores)
     *
     * @return
     */
    public double getScore() {
        return (maxNorm + avgNorm) * 0.5;
    }

    /**
     * Returns a URL representing the external canonical endpoint of this disease
     */
    public String getExternalId () {
        Disease disease = new Disease(diseaseId);
        return disease.getExternalUrl();
    }

    /**
     * Create a json representation of the object with focus on model.
     *
     * This renames a few fields with shorter names - saves somes bytes when
     * moving the json around.
     *
     * @return
     */
    public String makeModelJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id:\"" + this.getId() + "\",");
        sb.append("source:\"" + this.getSource() + "\",");
        sb.append("description:\"" + this.getDescription() + "\",");
        sb.append("background:\"" + this.getGeneticBackground() + "\",");
        sb.append("markerId:\"" + this.getMarkerId() + "\",");
        sb.append("markerSymbol:\"" + this.getMarkerSymbol() + "\",");
        sb.append("diseaseMatchedPhenotypes:\"" + new JSONArray(diseaseMatchedPhenotypes) + "\",");
        sb.append("modelMatchedPhenotypes:\"" + new JSONArray(modelMatchedPhenotypes) + "\",");
        sb.append("markerNumModels:\"" + markerNumModels + "\",");
        sb.append("avgNorm:" + avgNorm + ",");
        sb.append("avgRaw:" + avgRaw + ",");
        sb.append("maxRaw:" + maxRaw + ",");
        sb.append("maxNorm:" + maxNorm);
        sb.append("}");
        return sb.toString();
    }

    /**
     * Create a json representation of the object with focus on disease.
     *
     * @return
     */
    public String makeDiseaseJson() {
        Disease disease = new Disease(diseaseId);
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("id:\"" + this.getId() + "\",");
        sb.append("source:\"" + this.getSource() + "\",");
        sb.append("description:\"" + this.getDescription() + "\",");
        sb.append("diseaseId:\"" + diseaseId + "\",");
        sb.append("diseaseTerm:\"" + diseaseTerm + "\",");
        sb.append("diseaseUrl:\"" + disease.getExternalUrl() + "\",");
        sb.append("diseaseMatchedPhenotypes:" + new JSONArray(diseaseMatchedPhenotypes).toString().replaceAll("\"", "'") + ",");
        sb.append("modelMatchedPhenotypes:" + new JSONArray(modelMatchedPhenotypes).toString().replaceAll("\"", "'") + ",");
        sb.append("avgNorm:" + avgNorm + ",");
        sb.append("avgRaw:" + avgRaw + ",");
        sb.append("maxRaw:" + maxRaw + ",");
        sb.append("maxNorm:" + maxNorm);
        sb.append("}");
        return sb.toString();
    }

}
