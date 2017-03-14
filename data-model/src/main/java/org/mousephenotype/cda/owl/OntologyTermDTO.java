package org.mousephenotype.cda.owl;

import org.semanticweb.owlapi.model.OWLClass;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ilinca on 10/08/2016.
 */
public class OntologyTermDTO {

    String               accessionId;
    String               name;
    Set<String>          synonyms;
    Set<String>          narrowSynonyms;
    Set<OntologyTermDTO> equivalentClasses; // from equivalent classes
    Set<String>          childIds;
    Set<String>          childNames;
    Set<String>          parentIds;
    Set<String>          parentNames;
    Set<String>          alternateIds;
    Set<String>          considerIds;
    Set<String>          broadSynonyms;
    Set<String>          intermediateIds;
    Set<String>          intermediateNames;
    Set<String>          topLevelNames;
    Set<String>          toplevelIds;
    String               replacementAccessionId;
    String               definition;
    boolean              isObsolete;
    OWLClass             cls;

    public Set<OntologyTermDTO> getEquivalentClasses() {
        return equivalentClasses;
    }

    public void setEquivalentClasses(Set<OntologyTermDTO> equivalentClasses) { this.equivalentClasses = equivalentClasses; }

    public OWLClass getCls() {
        return cls;
    }

    public void setCls(OWLClass cls) {
        this.cls = cls;
    }

    public String getReplacementAccessionId() {
        return replacementAccessionId;
    }

    public void setReplacementAccessionId(String replacementAccessionId) {   this.replacementAccessionId = replacementAccessionId; }

    public boolean isObsolete() {
        return isObsolete;
    }

    public void setObsolete(boolean obsolete) {
        isObsolete = obsolete;
    }

    public Set<String> getIntermediateIds() {
        return intermediateIds;
    }

    public void setIntermediateIds(Set<String> intermediateIds) {
        this.intermediateIds = intermediateIds;
    }

    public Set<String> getIntermediateNames() {
        return intermediateNames;
    }

    public void setIntermediateNames(Set<String> intermediateNames) {
        this.intermediateNames = intermediateNames;
    }

    public Set<String> getTopLevelNames() {
        return topLevelNames;
    }

    public void setTopLevelNames(Set<String> topLevelNames) {
        this.topLevelNames = topLevelNames;
    }

    public Set<String> getToplevelIds() {
        return toplevelIds;
    }

    public void setToplevelIds(Set<String> toplevelIds) {
        this.toplevelIds = toplevelIds;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessonId) {
        this.accessionId = accessonId;
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


    public Set<String> getChildNames() {  return childNames;  }

    public void setChildNames(Set<String> childNames) {   this.childNames = childNames;  }

    public Set<String> getParentIds() {  return parentIds;}

    public void setParentIds(Set<String> parentIds) { this.parentIds = parentIds; }

    public void addParentId(String parentId) {
        if (this.parentIds == null){
            this.parentIds = new HashSet<>();
        }
        this.parentIds.add(parentId);
    }

    public void addParentName(String parentName) {
        if (this.parentNames == null){
            this.parentNames = new HashSet<>();
        }
        this.parentNames.add(parentName);
    }

    public void addChildName(String childName) {
        if (this.childNames == null){
            this.childNames = new HashSet<>();
        }
        this.childNames.add(childName);
    }

    public void addChildId(String childId) {
        if (this.childIds == null){
            this.childIds = new HashSet<>();
        }
        this.childIds.add(childId);
    }

    public Set<String> getParentNames() { return parentNames; }

    public void setParentNames(Set<String> parentNames) {  this.parentNames = parentNames; }

    public Set<String> getChildIds() {
        return childIds;
    }

    public void setChildIds(Set<String> childClasses) {
        this.childIds = childClasses;
    }

    @Override
    public String toString() {
        return "OntologyTermDTO{" +
                "accessonId='" + accessionId + '\'' +
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
