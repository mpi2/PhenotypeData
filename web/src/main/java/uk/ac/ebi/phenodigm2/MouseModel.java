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
 * MouseModel bean for phenodigm2 solr documents. Holds data describing one mouse
 * model.
 *
 */
public class MouseModel implements Comparable<MouseModel>, IdUrl {

    private String id;
    private String source;
    private String description;
    private String geneticBackground;
    private String markerId;
    private String markerSymbol;    
    private List<Phenotype> phenotypes;

    private static String baseUrlMGI = "http://www.informatics.jax.org/accession/MGI:";
        
    public MouseModel() {
    }

    /**
     * Basic constructor for a model with an id.
     * A complete instance of this class should also set source, description, 
     * and genetic background
     *
     * @param id
     */
    public MouseModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
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

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public void setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
    }
   
    /**
     * Like a setter, but parses phenotype objects from id+term strings.
     *
     * @param phenotypes
     */
    public void parsePhenotypes(List<String> phenotypes) {
        this.phenotypes = new ArrayList<>();
        for (String phenotype : phenotypes) {
            this.phenotypes.add(new Phenotype(phenotype));
        }
    }
    
    @Override
    public String getExternalUrl() {
        if (id.endsWith("hom") || id.endsWith("het")) {
            return "IMPC model "+id;
        }
        String[] tokens = id.split(":");
        switch (tokens[0]) {
            case "MGI":
                return baseUrlMGI + tokens[1];
            default:
                return "";
        }
    }
    
    @Override
    public int compareTo(MouseModel t) {
        return this.id.compareTo(t.id);
    }

    //@Override
    //public String toString() {
    //    return "MouseModel{" + "id=" + id + ", source=" + source + ", description=" + description + ", geneticBackground=" + geneticBackground + ", markerId=" + markerId + ", markerSymbol=" + markerSymbol + ", phenotypes=" + phenotypes + '}';
    //}
                  
}
