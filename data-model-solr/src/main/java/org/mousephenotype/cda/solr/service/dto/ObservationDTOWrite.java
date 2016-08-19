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
import org.apache.solr.common.util.JavaBinCodec;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * This class is used for overriding the default Solr date stamp behaviour
 *
 */
public class ObservationDTOWrite extends ObservationDTOBase {


	@Field(DATE_OF_EXPERIMENT)
	private Iso8601ZonedDateTime solrDateOfExperiment;
	private ZonedDateTime dateOfExperiment;

	@Field(DATE_OF_BIRTH)
	private Iso8601ZonedDateTime solrDateOfBirth;
	private ZonedDateTime dateOfBirth;

	@Field(WEIGHT_DATE)
	private Iso8601ZonedDateTime solrWeightDate;
	private ZonedDateTime weightDate;


	public Iso8601ZonedDateTime getSolrWeightDate() {
		return new Iso8601ZonedDateTime(this.weightDate);
	}

	public Iso8601ZonedDateTime getSolrDateOfExperiment() {
		return new Iso8601ZonedDateTime(this.dateOfExperiment);
	}

	public Iso8601ZonedDateTime getSolrDateOfBirth() {
		return new Iso8601ZonedDateTime(this.dateOfBirth);
	}

	/**
	 * ======================================================================
	 * Setters that will set the dateOfExperiment field as well as the solr field
	 */

	public void setSolrDateOfExperiment(Date solrDateOfExperimentBase) {
		Iso8601ZonedDateTime solrDateOfExperiment = new Iso8601ZonedDateTime(ZonedDateTime.ofInstant(solrDateOfExperimentBase.toInstant(), ZoneId.of("UTC")));
		this.solrDateOfExperiment = solrDateOfExperiment;
		this.dateOfExperiment = solrDateOfExperiment.inner;
	}

	public void setSolrDateOfBirth(Date solrDateOfBirthBase) {
		Iso8601ZonedDateTime solrDateOfBirth = new Iso8601ZonedDateTime(ZonedDateTime.ofInstant(solrDateOfBirthBase.toInstant(), ZoneId.of("UTC")));
		this.solrDateOfBirth = solrDateOfBirth;
		this.dateOfBirth = solrDateOfBirth.inner;
	}

	public void setSolrWeightDate(Date solrWeightDateBase) {
		Iso8601ZonedDateTime solrWeightDate = new Iso8601ZonedDateTime(ZonedDateTime.ofInstant(solrWeightDateBase.toInstant(), ZoneId.of("UTC")));
		this.solrWeightDate = solrWeightDate;
		this.weightDate = solrWeightDate.inner;
	}


	public ZonedDateTime getDateOfExperiment() {
		return dateOfExperiment;
	}

	public void setDateOfExperiment(ZonedDateTime dateOfExperiment) {
		this.dateOfExperiment = dateOfExperiment;
		this.solrDateOfExperiment = (dateOfExperiment != null) ? new Iso8601ZonedDateTime(dateOfExperiment) : null;
	}

	public ZonedDateTime getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(ZonedDateTime dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		this.solrDateOfBirth = (dateOfBirth != null) ? new Iso8601ZonedDateTime(dateOfBirth) : null;

	}

	public ZonedDateTime getWeightDate() {
		return weightDate;
	}

	public void setWeightDate(ZonedDateTime weightDate) {
		this.weightDate = weightDate;
		this.solrWeightDate = (weightDate != null) ? new Iso8601ZonedDateTime(weightDate) : null;
	}


	public class Iso8601ZonedDateTime implements JavaBinCodec.ObjectResolver {
		ZonedDateTime inner;

		public Iso8601ZonedDateTime(ZonedDateTime zdt) {
			inner = zdt;
		}

		public String toString() {

			if (inner == null) {
				return null;
			}

			return inner.format(DateTimeFormatter.ISO_INSTANT);
		}

		// Need an ObjectResolver to turn this Iso8601ZonedDateTime object into a string for Solr indexing
		@Override
		public Object resolve(Object o, JavaBinCodec codec) throws IOException {
			if (o instanceof Iso8601ZonedDateTime)
			{
				return this.toString();
			}
			return o;
		}
	}




}
