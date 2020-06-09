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

import java.io.Serializable;

public class DatasourceEntityId implements Serializable {
	private String accession;
	private long databaseId;

	public DatasourceEntityId() {
		super();
	}

	public DatasourceEntityId(String accession, long databaseId){
		this.accession = accession;
		this.databaseId = databaseId;
	}


	/**
	 * @return the accession
	 */
	public String getAccession() {
		return accession;
	}



	/**
	 * @param accession the accession to set
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}



	/**
	 * @return the databaseId
	 */
	public long getDatabaseId() {
		return databaseId;
	}



	/**
	 * @param databaseId the databaseId to set
	 */
	public void setDatabaseId(long databaseId) {
		this.databaseId = databaseId;
	}



	@Override
	public boolean equals(Object arg0) {
		if(arg0 == null) return false;
		if(!(arg0 instanceof DatasourceEntityId)) return false;
		DatasourceEntityId arg1 = (DatasourceEntityId) arg0;
		return (this.databaseId == arg1.getDatabaseId()) && (this.accession.equals(arg1.getAccession()));

	}
	@Override
	public int hashCode() {
		int hsCode;
		hsCode = Long.valueOf(databaseId).hashCode();
		hsCode = 19 * hsCode+ accession.hashCode();
		return hsCode;
	}

	@Override
	public String toString() {
		return "DatasourceEntityId [accession=" + accession + ", databaseId="
				+ databaseId + "]";
	}
}