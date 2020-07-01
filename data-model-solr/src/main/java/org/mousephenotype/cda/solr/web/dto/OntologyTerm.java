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
package org.mousephenotype.cda.solr.web.dto;


import java.util.Objects;


public class OntologyTerm {

    DatasourceEntityId id;
    private String description;
    private String name;
    private Boolean isObsolete;
    private String replacementAcc;


    public OntologyTerm() {
        super();
    }

    public OntologyTerm(String accessionId, Long dbId) {
        this.id = new DatasourceEntityId();
        this.id.setAccession(accessionId);
        this.id.setDatabaseId(dbId);
    }

    /**
     * @return the id
     */
    public DatasourceEntityId getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(DatasourceEntityId id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    public String getReplacementAcc() {
        return replacementAcc;
    }

    public void setReplacementAcc(String replacementAcc) {
        this.replacementAcc = replacementAcc;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    public Boolean getIsObsolete() {
        return isObsolete;
    }


    public void setIsObsolete(Boolean isObsolete) {
        this.isObsolete = isObsolete;
    }

    @Override
    public String toString() {
        return "OntologyTerm{" +
                "id={" + (id == null ? "null" : id.getAccession() + "," + id.getDatabaseId()) + "}" +
                ", name='" + name + '\'' +
                ", isObsolete=" + isObsolete +
                ", replacementAcc=" + replacementAcc +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OntologyTerm that = (OntologyTerm) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(name, that.name) &&
                Objects.equals(isObsolete, that.isObsolete) &&
                Objects.equals(replacementAcc, that.replacementAcc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, name, isObsolete, replacementAcc);
    }
}
