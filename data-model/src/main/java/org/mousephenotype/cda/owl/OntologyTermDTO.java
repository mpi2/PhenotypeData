package org.mousephenotype.cda.owl;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.Set;

/**
 * Created by ilinca on 10/08/2016.
 */
public class OntologyTermDTO {

    String accessonId;
    String name;
    Set<String> synonyms;
    Set<String> narrowSynonyms;
    Set<OntologyTermDTO> equivalentClasses; // from equivalent classes, return the
    Set<String> considerId;
    Set<String> broadSynonyms;
    String replacementAccessionIds;
    String definition;
    boolean isObsolete;
    OWLClass cls;

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

    public String getReplacementAccessionIds() {
        return replacementAccessionIds;
    }

    public void setReplacementAccessionIds(String replacementAccessionIds) {
        this.replacementAccessionIds = replacementAccessionIds;
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

    public Set<String> getConsiderId() {
        return considerId;
    }

    public void setConsiderId(Set<String> considerId) {
        this.considerId = considerId;
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

}
