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
 * Representation of a datasource in the database.
 * A datasource is an external data representation from different providers.
 * Not exhaustive list: ontologies, MGI markers, EuroPhenome legacy data, etc.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @version $Revision: 2843 $
 *  @since February 2012
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "external_db")
public class Datasource {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "short_name")
	private String shortName;

	@Column(name = "version")
	private String version;

	@Column(name = "version_date")
	private Date releaseDate;

	public Datasource() {
		super();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the releaseDate
	 */
	public Date getReleaseDate() {
		return releaseDate;
	}

	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String toString() {
		return "Id: " + id + "; Name: " + name + "; version: " + version + "; release date: " + releaseDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Datasource that = (Datasource) o;
		return id.equals(that.id) &&
				Objects.equals(name, that.name) &&
				Objects.equals(shortName, that.shortName) &&
				Objects.equals(version, that.version) &&
				Objects.equals(releaseDate, that.releaseDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, shortName, version, releaseDate);
	}

	// external_db.short_name definitions
	public static final String NCBI_M38 = "NCBI m38";
	public static final String GENOME_FEATURE_TYPE = "Genome Feature Type";
	public static final String MGI = "MGI";
	public static final String X_Y = "X/Y";
	public static final String MP = "MP";
	public static final String IMPRESS = "IMPReSS";
	public static final String PATO = "PATO";
	public static final String MA = "MA";
	public static final String CHEBI = "CHEBI";
	public static final String ENVO = "EnvO";
	public static final String GO = "GO";
	public static final String EUROPHENOME = "EuroPhenome";
	public static final String ECO = "ECO";
	public static final String EMAP = "EMAP";
	public static final String EFO = "EFO";
	public static final String IMSR = "IMSR";
	public static final String VEGA = "VEGA";
	public static final String ENSEMBL = "Ensembl";
	public static final String ENTREZ_GENE = "EntrezGene";
	public static final String MGP = "MGP";
	public static final String CCDS = "cCDS";
	public static final String IMPC = "IMPC";
	public static final String THREE_I = "3I";
	public static final String MPATH = "MPATH";
	public static final String MMUSDV = "MMUSDV";
	public static final String EMAPA = "EMAPA";
}