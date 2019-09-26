/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
 * Representation of a strain.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 *
 */

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;


@Entity
@Table(name = "strain")
public class Strain {

	@EmbeddedId
	@AttributeOverrides({@AttributeOverride(name = "accession",
		column = @Column(name = "acc")), @AttributeOverride(name = "databaseId",
		column = @Column(name = "db_id"))})
	DatasourceEntityId id;

	@OneToOne
	@JoinColumns({@JoinColumn(name = "biotype_acc"), @JoinColumn(name = "biotype_db_id"),})
	private OntologyTerm biotype;

	@Column(name = "name")
	private String name;

	// element collections are merged/removed with their parents
	@ElementCollection
	@CollectionTable(name = "synonym",
		joinColumns = {@JoinColumn(name = "acc"), @JoinColumn(name = "db_id"),})
	private List<Synonym> synonyms;


	public Strain() {
		super();
	}

	public String getGeneticBackground() {
		return "involves: " + this.name;
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
		if (synonyms == null) {
			synonyms = new LinkedList<>();
		}
		this.synonyms.add(synonym);
	}

	/**
	 * Returns the synonym from the list of synonyms if it exists; null otherwise.
	 *
	 * @param symbol the desired synonym symbol
	 *
	 * @return the synonym from the list of synonyms if it exists; null otherwise.
     */
	public Synonym getSynonym(String symbol) {
		if (synonyms != null) {
			for (Synonym synonym : synonyms) {
				if (synonym.getSymbol().equals(symbol)) {
					return synonym;
				}
			}
		}

		return null;
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
	 * @return the biotype
	 */
	public OntologyTerm getBiotype() {
		return biotype;
	}


	/**
	 * @param biotype the biotype to set
	 */
	public void setBiotype(OntologyTerm biotype) {
		this.biotype = biotype;
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

	@Override
	public String toString() {

		return "Strain{" +
				"id=" + id +
				", name='" + name + '\'' +
				", biotype=" + (biotype == null ? "null" : biotype) +
				", synonyms=[" + toStringSynonyms() + "]" +
				'}';
	}
	public String toStringSynonyms() {
		String synonymSymbols = "";
		if (synonyms == null) {
			return "";
		}
		for (int i = 0; i < synonyms.size(); i++) {
			if (i > 0)
				synonymSymbols += ", ";
			synonymSymbols += synonyms.get(i).getSymbol();
		}

		return synonymSymbols;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Strain strain = (Strain) o;

		return id.equals(strain.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}