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
package org.mousephenotype.cda.solr.service.dto;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.apache.solr.client.solrj.beans.Field;

public class ObservationDTO extends ObservationDTOBase {

	@Field(DATE_OF_EXPERIMENT)
	private Date dateOfExperiment;

    @Field(DATE_OF_BIRTH)
    private Date dateOfBirth;

    @Field(WEIGHT_DATE)
	private Date weightDate;



    /**
     * helper methods
     *
     * @throws SQLException
     */

    public String tabbedToString() throws SQLException {
        String tabbed = pipelineName
                + "\t" + pipelineStableId
                + "\t" + procedureStableId
                + "\t" + procedureName
                + "\t" + parameterStableId
                + "\t" + parameterName
                // + "\t" + pipelineId
                // + "\t" + procedureId
                // + "\t" + parameterId
                + "\t" + strainAccessionId
                + "\t" + strainName
                + "\t" + geneticBackground
                // + "\t" + experimentSourceId
                + "\t" + geneSymbol
                + "\t" + geneAccession
                + "\t" + alleleSymbol
                + "\t" + alleleAccession
                // + "\t" + experimentId
                // + "\t" + organisationId
                // + "\t" + observationType
                + "\t" + phenotypingCenter
                + "\t" + colonyId
                + "\t" + dateOfExperiment
                // + "\t" + dateOfBirth
                // + "\t" + biologicalSampleId
                // + "\t" + biologicalModelId
                + "\t" + zygosity
                + "\t" + sex
                + "\t" + group
                // + "\t" + category
                // + "\t" + dataPoint
                // + "\t" + orderIndex
                // + "\t" + dimension
                // + "\t" + timePoint
                // + "\t" + discretePoint
                + "\t" + externalSampleId
                + "\t\"" + metadata + "\""
                +"\t" + metadataGroup;
        ;

        if (observationType.equalsIgnoreCase("unidimensional")) {
            tabbed += "\t" + dataPoint;
        }
        else if (observationType.equalsIgnoreCase("categorical")) {
            tabbed += "\t" + category;
        }
        else if (observationType.equalsIgnoreCase("time_series")) {
            tabbed += "\t" + dataPoint + "\t" + discretePoint;
        }
        return tabbed;
    }

    public String getTabbedFields() {
        String tabbed = "pipeline name"
                + "\t pipelineStableId"
                + "\t procedureStableId"
                + "\t procedureName"
                + "\t parameterStableId"
                + "\t parameterName"
                // + "\t pipeline id"
                // + "\t procedureId"
                // + "\t parameterId"
                + "\t strainId"
                + "\t strain"
                + "\t backgroundStrain"
                // + "\t experimentSourceId"
                + "\t geneSymbol"
                + "\t geneAccession"
                + "\t alleleSymbol"
                + "\t alleleAccession"
                // + "\t experimentId"
                // + "\t organisationId"
                // + "\t observationType"
                + "\t phenotypingCenter"
                + "\t colonyId"
                + "\t dateOfExperiment"
                // + "\t dateOfBirth"
                // + "\t biologicalSampleId"
                // + "\t biologicalModelId"
                + "\t zygosity"
                + "\t sex"
                + "\t group"
                // + "\t category"
                // + "\t dataPoint"
                // + "\t orderIndex"
                // + "\t dimension"
                // + "\t timePoint"
                // + "\t discretePoint"
                + "\t externalSampleId"
                + "\t metadata"
                + "\t metadataGroup";
        if (observationType.equalsIgnoreCase("unidimensional")) {
            tabbed += "\t" + "dataPoint";
        }
        else if (observationType.equalsIgnoreCase("categorical")) {
            tabbed += "\t" + "category";
        }
        else if (observationType.equalsIgnoreCase("time_series")) {
            tabbed += "\t" + "dataPoint" + "\t" + "discretePoint";
        }
        return tabbed;
    }



    /**
     * Format date of experiment string into day/month/year format
     *
     * @return string representation of the date the experiment was performed
     */
    public String getDateOfExperimentString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dateOfExperiment);
    }

    public String getDateOfBirthString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dateOfBirth);

    }

    public boolean isControl() {
        return this.group.equals("control");
    }

    public boolean isMutant() {
        return this.group.equals("experimental");
    }

    /**
     *
     * @return key uniquely identifying the group in which the ObservationDTO
     *         object is analysed. A concatenation of phenotyping center,
     *         strainAccessionId, allele, parameter, pipeline, zygosity, sex, metadata
     */

    public String getKey() {
        return "[allele: " + this.getAlleleAccession()
                + " , strainAccessionId :" + this.getStrain()
                + " , phenotyping center :" + this.getPhenotypingCenter()
                + " , parameter :" + this.getParameterStableId()
                + " , pipeline :" + this.getPipelineStableId()
                + " , zygosity :" + this.getZygosity()
                + " , metadata :" + this.getMetadataGroup()
                + " ]";
    }

    /**
     * end helper methods
     */

    @Override
    public String toString() {
        return super.toString()+" id=" + id
	        + ", procedure=" + procedureGroup
                + ", parameterId=" + parameterId
                + ", phenotypingCenterId=" + phenotypingCenterId
                + ", biologicalModelId=" + biologicalModelId
                + ", zygosity=" + zygosity
                + ", sex=" + sex
                + ", group=" + group
                + ", colonyId=" + colonyId
                + ", metadataGroup=" + metadataGroup
                + ", dataPoint=" + dataPoint
                + ", category=" + category
                + ", dateOfExperiment=" + dateOfExperiment
                + ", orderIndex=" + orderIndex
                + ", dimension=" + dimension
                + ", timePoint=" + timePoint
                + ", discretePoint=" + discretePoint
                + ", externalSampleId=" + externalSampleId;
    }


    /**
     * @return the dateOfExperiment
     */
    public Date getDateOfExperiment() {
	    //        return dateOfExperiment;

	    ZonedDateTime zdt = ZonedDateTime.ofInstant(dateOfExperiment.toInstant(), ZoneId.of("UTC"));
	    if(TimeZone.getDefault().inDaylightTime(dateOfExperiment)) {
		    zdt = dateOfExperiment.toInstant().atZone(ZoneId.of(TimeZone.getDefault().getID()));
	    }
	    return Date.from(zdt.toLocalDateTime().toInstant(ZoneOffset.ofHours(0)));
    }

    /**
     * @param dateOfExperiment
     *            the dateOfExperiment to set
     */
    public void setDateOfExperiment(Date dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    /**
     * @return the dateOfBirth
     */
    public Date getDateOfBirth() {
//        return dateOfBirth;
	    ZonedDateTime zdt = ZonedDateTime.ofInstant(dateOfBirth.toInstant(), ZoneId.of("UTC"));
	    if(TimeZone.getDefault().inDaylightTime(dateOfBirth)) {
		    zdt = dateOfBirth.toInstant().atZone(ZoneId.of(TimeZone.getDefault().getID()));

	    }
	    return Date.from(zdt.toLocalDateTime().toInstant(ZoneOffset.ofHours(0)));

    }

    /**
     * @param dateOfBirth
     *            the dateOfBirth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;

    }


    public Date getWeightDate() {
	    ZonedDateTime zdt = ZonedDateTime.ofInstant(weightDate.toInstant(), ZoneId.of("UTC"));
	    if(TimeZone.getDefault().inDaylightTime(weightDate)) {
		    zdt = weightDate.toInstant().atZone(ZoneId.of(TimeZone.getDefault().getID()));
	    }
	    return Date.from(zdt.toLocalDateTime().toInstant(ZoneOffset.ofHours(0)));

//        return weightDate;
    }


    public void setWeightDate(Date weightDate) {

        this.weightDate = weightDate;
    }


}
