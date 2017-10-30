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
 * Disease bean for phenodigm2 solr documents. Holds data describing one
 * disease.
 *
 */
public class Disease extends AssociationType implements Comparable<Disease>, IdUrl {

    private String id;
    private String term;
    private List<String> alts;    
    private List<String> classes;
    private List<Phenotype> phenotypes;

    // used internally to construct URLs
    private static String baseUrlOmim = "http://omim.org/entry/";
    private static String baseUrlOrphanet = "http://www.orpha.net/consor/cgi-bin/OC_Exp.php?lng=en&Expert=";
    private static String baseUrlDecipher = "https://decipher.sanger.ac.uk/syndrome/";
   
    public Disease() {
    }

    /**
     * Basic constructor for a disease with an id.
     *
     * A complete instance of this class should also set term (plain language
     * description of the disease).
     *
     * @param id
     */
    public Disease(String id) {
        this.id = id;
    }

    /**
     * Convenience constructor for a disease with an id and description
     *
     * @param id
     * @param term
     */
    public Disease(String id, String term) {
        this.id = id;
        this.term = term;
    }

    public String getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<String> getAlts() {
        return alts;
    }

    public void setAlts(List<String> alts) {
        this.alts = alts;
    }
   
    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
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
        if (phenotypes==null) {            
            return;
        }        
        for (String phenotype : phenotypes) {
            this.phenotypes.add(new Phenotype(phenotype));
        }        
    }

    @Override
    public int compareTo(Disease t) {
        return this.id.compareTo(t.id);
    }

    @Override
    public String toString() {
        return "Disease{" + "id=" + id + ", term=" + term + ", alts=" + alts + ", classes=" + classes + ", phenotypes=" + phenotypes + '}';
    }

    /**
     *
     * @return
     *
     * string with a url to a disease definition page
     *
     */
    @Override
    public String getExternalUrl() {
        String[] tokens = id.split(":");
        switch (tokens[0]) {
            case "DECIPHER":
                return baseUrlDecipher + tokens[1];
            case "OMIM":
                return baseUrlOmim + tokens[1];
            case "ORPHANET":
            case "ORPHA":
                // both ORPHANET and ORPHA prefix lead to orphanet link
                return baseUrlOrphanet + tokens[1];
            default:
                return "[unknown]";
        }
    }

}
