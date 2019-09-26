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
package org.mousephenotype.cda.db.pojo;

/**
 *
 * Represents an ontology term in the database.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 * @see Synonym
 */

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "ontology_term")
public class OntologyTerm {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name="accession", column=@Column(name="acc")),
		@AttributeOverride(name="databaseId", column=@Column(name="db_id"))
	})
	@NotFound(action= NotFoundAction.IGNORE)
	DatasourceEntityId id;

	@Column(name = "description")
	private String description;

	@Column(name = "name")
	private String name;

	@Column(name = "is_obsolete")
	private Boolean isObsolete;

    @Column(name = "replacement_acc")
	private String replacementAcc;

	@Transient
	private Set<AlternateId> alternateIds = new HashSet<>();

	@Transient
	private Set<ConsiderId> considerIds = new HashSet<>();

	@ElementCollection
	@CollectionTable(
		name="synonym",
		joinColumns= {
			@JoinColumn(name="acc"),
			@JoinColumn(name="db_id"),
		}
	)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Fetch(FetchMode.SELECT)
	private List<Synonym> synonyms = new ArrayList<Synonym>();

	public OntologyTerm() {
		super();
	}

	public OntologyTerm(String accessionId, Long dbId) {
		this.id = new DatasourceEntityId();
		this.id.setAccession(accessionId);
		this.id.setDatabaseId(dbId);
	}

	public Set<ConsiderId> getConsiderIds() {
		return considerIds;
	}

	public void setConsiderIds(Set<ConsiderId> considerIds) {
		this.considerIds = considerIds;
	}

	public Boolean getObsolete() {
		return isObsolete;
	}

	public void setObsolete(Boolean obsolete) {
		isObsolete = obsolete;
	}

	public Set<AlternateId> getAlternateIds() {
		return alternateIds;
	}

	public void setAlternateIds(Set<AlternateId> alternateIds) {
		this.alternateIds = alternateIds;
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

	/**
	 * @return the synonyms
	 */
	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	/**
	 * @param synonyms the synonyms to set
	 */
	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public void addSynonym(Synonym synonym) {
		if (this.synonyms == null) {
			this.synonyms = new LinkedList<Synonym>();
		}
		synonyms.add(synonym);
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
				", considerIds=" + ((considerIds == null) || considerIds.isEmpty() ? "null" : "String [" + StringUtils.join(considerIds, ", ") + "]") +
				", synonyms=" + ((synonyms == null) || synonyms.isEmpty() ? "null" : StringUtils.join(synonyms, ",")) +
				", description='" + description + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OntologyTerm that = (OntologyTerm) o;

		if (!id.equals(that.id)) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		if (!name.equals(that.name)) return false;
		if (!isObsolete.equals(that.isObsolete)) return false;
		if (replacementAcc != null ? !replacementAcc.equals(that.replacementAcc) : that.replacementAcc != null)
			return false;
		if (considerIds != null ? !considerIds.equals(that.considerIds) : that.considerIds != null) return false;
		return synonyms != null ? synonyms.equals(that.synonyms) : that.synonyms == null;

	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + name.hashCode();
		result = 31 * result + isObsolete.hashCode();
		result = 31 * result + (replacementAcc != null ? replacementAcc.hashCode() : 0);
		result = 31 * result + (considerIds != null ? considerIds.hashCode() : 0);
		result = 31 * result + (synonyms != null ? synonyms.hashCode() : 0);
		return result;
	}
}
