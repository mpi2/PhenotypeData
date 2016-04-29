/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.db.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates an ontology termId, name, termId & name concatenated, and a list
 of synonyms.
 * 
 * @author mrelac
 */
public class OntologyTermBean {
    private String id;
    private String name;
    private String definition;
    private List<String> synonyms = new ArrayList<>();
    private String topLevelTermId;
    private List<String> altMaIds = new ArrayList<>();
    private List<Integer>  maNodeIds = new ArrayList<>();

    public List<Integer> getMaNodeIds() {
        return maNodeIds;
    }

    public void setMaNodeIds(List<Integer> maNodeIds) {
        this.maNodeIds = maNodeIds;
    }

    public List<String> getAltMaIds() {
        return altMaIds;
    }

    public void setAltMaIds(List<String> altMaIds) {
        this.altMaIds = altMaIds;
    }
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public OntologyTermBean() {
        
    }

	/**
	 * @return the topLevelMPTermId
	 */
	public String getTopLevelTermId() {
		return topLevelTermId;
	}

	/**
	 * @param topLevelTermId the topLevelTermId to set
	 */
	public void setTopLevelTermId(String topLevelTermId) {
		this.topLevelTermId = topLevelTermId;
	}
    
    public String getId() {
        return id;
    }

    public void setId(String termId) {
        this.id = termId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public List<String> getAltIds() {
        return altMaIds;
    }

    public void setAltIds(List<String> altMaIds) {
        this.altMaIds = altMaIds;
    }

    // AUXILIARY METHODS

    
    /**
     * Returns a concatenation of term id and term name.
     * @return a concatenation of term id and term name, separated by a pair of
     * underscores. If either id or name is null, that null component is replaced
     * by an empty string.
     */
    public String getTermIdTermName() {
        String value = "";
        if (id != null)
            value += id;
        value += "__";
        if (name != null)
            value += name;
        
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + Objects.hashCode(this.definition);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OntologyTermBean other = (OntologyTermBean) obj;
        if ( ! Objects.equals(this.id, other.id)) {
            return false;
        }
        if ( ! Objects.equals(this.name, other.name)) {
            return false;
        }
        if ( ! Objects.equals(this.definition, other.definition)) {
            return false;
        }
        return true;
    }
}
