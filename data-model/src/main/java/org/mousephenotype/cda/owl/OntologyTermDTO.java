package org.mousephenotype.cda.owl;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.Set;

/**
 * Created by ilinca on 10/08/2016.
 */
public class OntologyTermDTO {

    String               accessonId;
    String               name;
    Set<String>          synonyms;
    Set<String>          narrowSynonyms;
    Set<OntologyTermDTO> equivalentClasses; // from equivalent classes, return the
    Set<String>          alternateIds;
    Set<String>          considerIds;
    Set<String>          broadSynonyms;
    String               replacementAccessionId;
    String               definition;
    boolean              isObsolete;
    OWLClass             cls;

    public Set<OntologyTermDTO> getEquivalentClasses() {
        return equivalentClasses;
    }

    public void setEquivalentClasses(Set<OntologyTermDTO> equivalentClasses) {
        this.equivalentClasses = equivalentClasses;
    }

    public OWLClass getCls() {
        return cls;
    }

    public void setCls(OWLClass cls) {
        this.cls = cls;
    }

    public String getReplacementAccessionId() {
        return replacementAccessionId;
    }

    public void setReplacementAccessionId(String replacementAccessionId) {
        this.replacementAccessionId = replacementAccessionId;
    }

    public boolean isObsolete() {
        return isObsolete;
    }

    public void setObsolete(boolean obsolete) {
        isObsolete = obsolete;
    }

    public String getAccessonId() {
        return accessonId;
    }

    public void setAccessonId(String accessonId) {
        this.accessonId = accessonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public void addSynonym(String synonym){

    }

    public Set<String> getConsiderIds() {
        return considerIds;
    }

    public Set<String> getNarrowSynonyms() {
        return narrowSynonyms;
    }

    public void setNarrowSynonyms(Set<String> narrowSynonyms) {
        this.narrowSynonyms = narrowSynonyms;
    }

    public Set<String> getAlternateIds() {
        return alternateIds;
    }

    public void setAlternateIds(Set<String> alternateIds) {
        this.alternateIds = alternateIds;
    }

    public void setConsiderIds(Set<String> considerIds) {
        this.considerIds = considerIds;
    }

    public Set<String> getBroadSynonyms() {
        return broadSynonyms;
    }

    public void setBroadSynonyms(Set<String> broadSynonyms) {
        this.broadSynonyms = broadSynonyms;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return "OntologyTermDTO{" +
                "accessonId='" + accessonId + '\'' +
                ", name='" + name + '\'' +
                ", synonyms=" + synonyms +
                ", narrowSynonyms=" + narrowSynonyms +
                ", equivalentClasses=" + equivalentClasses +
                ", alternateIds=" + alternateIds +
                ", considerIds=" + considerIds +
                ", broadSynonyms=" + broadSynonyms +
                ", replacementAccessionId='" + replacementAccessionId + '\'' +
                ", definition='" + definition + '\'' +
                ", isObsolete=" + isObsolete +
                ", cls=" + cls +
                '}';
    }

}
