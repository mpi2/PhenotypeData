/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * This class is used for overriding the default Solr date stamp behaviour.
 * Solrj 7.7.1 expects dates to be in iso8601 format (e.g. yyyy-MM-ddTHH:mm:ssZ) and it queries all the annotated
 * @Field variables for these dates. If they are not in the expected format, the addBean call fails.
 *
 */
public class ObservationDTOWrite extends ObservationDTOBase {

	@Field(DATE_OF_EXPERIMENT)
	private String dateOfExperiment;

	@Field(DATE_OF_BIRTH)
	private String dateOfBirth;

	@Field(WEIGHT_DATE)
	private String weightDate;


	// dateOfExperiment
	public void setDateOfExperiment(Date dateOfExperiment) {

		if (dateOfExperiment != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			this.dateOfExperiment = formatter.format(dateOfExperiment);
		}
	}

	public void setDateOfExperiment(ZonedDateTime dateOfExperiment) {

		if (dateOfExperiment != null) {
			this.dateOfExperiment = dateOfExperiment.format(DateTimeFormatter.ISO_INSTANT);
		}
	}

	public Date getDateOfExperimentAsDate()  throws Exception{

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		return (dateOfExperiment == null ? null : formatter.parse(dateOfExperiment));
	}

	public ZonedDateTime getDateOfExperimentAsZonedDateTime() {
		return (dateOfExperiment == null ? null : ZonedDateTime.parse(dateOfExperiment));
	}


	// dateOfBirth
	public void setDateOfBirth(Date dateOfBirth) {

		if (dateOfBirth != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			this.dateOfBirth = formatter.format(dateOfBirth);
		}
	}

	public void setDateOfBirth(ZonedDateTime dateOfBirth) {

		if (dateOfBirth != null) {
			this.dateOfBirth = dateOfBirth.format(DateTimeFormatter.ISO_INSTANT);
		}
	}

	public Date getDateOfBirthAsDate()  throws Exception{

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		return (dateOfBirth == null ? null : formatter.parse(dateOfBirth));
	}

	public ZonedDateTime getDateOfBirthAsZonedDateTime() {
		return (dateOfBirth == null ? null : ZonedDateTime.parse(dateOfBirth));
	}


	// weightDate
	public void setWeightDate(Date weightDate) {

		if (weightDate != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			this.weightDate = formatter.format(weightDate);
		}
	}

	public void setWeightDate(ZonedDateTime weightDate) {

		if (weightDate != null) {
			this.weightDate = weightDate.format(DateTimeFormatter.ISO_INSTANT);
		}
	}

	public Date getWeightDateAsDate()  throws Exception{

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		return (weightDate == null ? null : formatter.parse(weightDate));
	}

	public ZonedDateTime getWeightDateAsZonedDateTime() {
		return (weightDate == null ? null : ZonedDateTime.parse(weightDate));
	}
}