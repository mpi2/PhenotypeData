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
 * Inherits from a biological sample. Usually an animal.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * @see BiologicalSample
 * @see OntologyTerm
 */

import javax.persistence.*;
import java.util.Date;


@Entity
@PrimaryKeyJoinColumn(name="id")
@Table(name = "live_sample")
public class LiveSample extends BiologicalSample {

	@OneToOne
	@JoinColumns({
	@JoinColumn(name = "developmental_stage_acc"),
	@JoinColumn(name = "developmental_stage_db_id"),
	})
	private OntologyTerm developmentalStage;

	@Column(name = "sex")
	private String sex;

	@Column(name = "zygosity")
	private String zygosity;

	@Column(name = "colony_id")
	private String colonyID;

	@Column(name = "date_of_birth")
	private Date dateOfBirth;

	@OneToOne
	@JoinColumn(
		name = "production_center_id"
	)
	private Organisation productionCenter;

	@Column(
		name = "litter_id"
	)
	private String litterId;


	public Organisation getProductionCenter() {
		return productionCenter;
	}

	public void setProductionCenter(Organisation productionCenter) {
		this.productionCenter = productionCenter;
	}

	public String getLitterId() {
		return litterId;
	}

	public void setLitterId(String litterId) {
		this.litterId = litterId;
	}
	/**
	 * @return the developmentalStage
	 */
	public OntologyTerm getDevelopmentalStage() {
		return developmentalStage;
	}

	/**
	 * @param developmentalStage the developmentalStage to set
	 */
	public void setDevelopmentalStage(OntologyTerm developmentalStage) {
		this.developmentalStage = developmentalStage;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the zygosity
	 */
	public String getZygosity() {
		return zygosity;
	}

	/**
	 * @param zygosity the zygosity to set
	 */
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the colonyID
	 */
	public String getColonyID() {
		return colonyID;
	}

	/**
	 * @param colonyID the colonyID to set
	 */
	public void setColonyID(String colonyID) {
		this.colonyID = colonyID;
	}

	@Override
	public String toString() {
		return "LiveSample{" +
				"developmentalStage=" + developmentalStage +
				", sex='" + sex + '\'' +
				", zygosity='" + zygosity + '\'' +
				", colonyID='" + colonyID + '\'' +
				", dateOfBirth=" + dateOfBirth +
				", litterId='" + litterId + '\'' +
				", id=" + id +
				", datasource=" + datasource +
				", group='" + group + '\'' +
				", type=" + type +
				", organisation=" + organisation +
				", productionCenter=" + productionCenter +
				", project=" + project +
				", biologicalModel=" + biologicalModel +
				'}';
	}
}