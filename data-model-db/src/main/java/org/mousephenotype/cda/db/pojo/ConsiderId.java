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
 * Representation of a consider id in the database.
 *
 * @author Mike Relac
 *
 */

import javax.persistence.Embeddable;


@Embeddable
public class ConsiderId {

	private String ontologyTermAccessionId;
	private String considerAccessionId;

	public ConsiderId() {

	}

	/**
	 * Create a new {@code ConsiderId} instance
	 *
	 * @param ontologyTermAccessionId the ontology term accession id (foreign key to the ontology_term table)
	 * @param @param considerAccessionId the consider accession id
     */
	public ConsiderId(String ontologyTermAccessionId, String considerAccessionId) {
		this.ontologyTermAccessionId = ontologyTermAccessionId;
		this.considerAccessionId = considerAccessionId;
	}

	/**
	 * Return the consider accession id
	 *
	 * @return the consider accession id
	 */
	public String getConsiderAccessionId() {
		return considerAccessionId;
	}

	/**
	 * Set the consider accession id
	 *
	 * @param considerAccessionId the consider accession id to set
	 */
	public void setConsiderAccessionId(String considerAccessionId) {
		this.considerAccessionId = considerAccessionId;
	}

	/**
	 * Return the ontology term accession id (foreign key to the ontology_term table)
	 *
	 * @return the ontology term accession id (foreign key to the ontology_term table)
	 */
	public String getOntologyTermAccessionId() {
		return ontologyTermAccessionId;
	}

	/**
	 * Set the ontology term accession id (foreign key to the ontology_term table)
	 *
	 * @param ontologyTermAccessionId the ontology term accession id (foreign key to the ontology_term table) to set
	 */
	public void setOntologyTermAccessionId(String ontologyTermAccessionId) {
		this.ontologyTermAccessionId = ontologyTermAccessionId;
	}

	@Override
	public String toString() {
		return "ConsiderId{" +
				"ontologyTermAccessionId='" + ontologyTermAccessionId + '\'' +
				", considerAccessionId='" + considerAccessionId + '\'' +
				'}';
	}
}