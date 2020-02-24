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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.mousephenotype.cda.enumerations.ObservationType;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ObservationDTO extends ObservationDTOBase {

    private static final String EXPORT_DATE_PATTERN = "yyyy-MM-dd";

    @Field(DATE_OF_EXPERIMENT)
	private Date dateOfExperiment;

    @Field(DATE_OF_BIRTH)
    private Date dateOfBirth;

    @Field(WEIGHT_DATE)
	private Date weightDate;




    /**
     * tabbedToString method used to get this object in a representation for exporting
     *
     * @return string of fields separated by TAB characters
     */
    public String tabbedToString() {

        List<String> fields = new ArrayList<>(Arrays.asList(
                pipelineName,
                pipelineStableId,
                procedureName,
                procedureStableId,
                parameterName,
                parameterStableId,
                strainAccessionId,
                strainName,
                geneticBackground,
                geneSymbol,
                geneAccession,
                alleleSymbol,
                alleleAccession,
                phenotypingCenter,
                colonyId,
                getDateOfExperimentString(),
                getDateOfBirthString(),
                getAgeInWeeks() != null ? getAgeInWeeks().toString() : "-",
                developmentalStageName,
                zygosity,
                sex,
                group,
                externalSampleId,
                "\"" + metadata.toString() + "\"",
                metadataGroup,
                weight!=null ? weight.toString() : "-",
                	productionCenter
        ));

        switch (ObservationType.valueOf(observationType)) {
            case unidimensional:
                fields.add(dataPoint.toString());
                break;
            case categorical:
                fields.add(category);
                break;
            case time_series:
                fields.add(dataPoint.toString());
                fields.add(discretePoint.toString());
                break;
            default:
                break;
        }

        return fields
                .parallelStream()
                .map(x -> StringUtils.isBlank(x) ? "-" : x)
                .collect(Collectors.joining("\t"));
    }

    /**
     * getTabbedFields method used to get the report headers for exporting
     *
     * @return string of field headers separated by TAB characters
     */
    public String getTabbedFields() {

        List<String> fields = new ArrayList<>(Arrays.asList(
                PIPELINE_NAME,
                PIPELINE_STABLE_ID,
                PROCEDURE_NAME,
                PROCEDURE_STABLE_ID,
                PARAMETER_NAME,
                PARAMETER_STABLE_ID,
                STRAIN_ACCESSION_ID,
                STRAIN_NAME,
                GENETIC_BACKGROUND,
                GENE_SYMBOL,
                GENE_ACCESSION_ID,
                ALLELE_SYMBOL,
                ALLELE_ACCESSION_ID,
                PHENOTYPING_CENTER,
                COLONY_ID,
                DATE_OF_EXPERIMENT,
                DATE_OF_BIRTH,
                AGE_IN_WEEKS,
                DEVELOPMENTAL_STAGE_NAME,
                ZYGOSITY,
                SEX,
                BIOLOGICAL_SAMPLE_GROUP,
                EXTERNAL_SAMPLE_ID,
                METADATA,
                METADATA_GROUP,
                WEIGHT,
                PRODUCTION_CENTER
        ));

        switch (ObservationType.valueOf(observationType)) {
            case unidimensional:
                fields.add(DATA_POINT);
                break;
            case categorical:
                fields.add(CATEGORY);
                break;
            case time_series:
                fields.add(DATA_POINT);
                fields.add(DISCRETE_POINT);
                break;
            default:
                break;
        }

        return fields.stream().collect(Collectors.joining("\t"));
    }



    /**
     * Format date of experiment string into day/month/year format
     *
     * @return string representation of the date the experiment was performed
     */
    public String getDateOfExperimentString() {
        return dateOfExperiment != null ? new SimpleDateFormat(EXPORT_DATE_PATTERN).format(dateOfExperiment) : "-";
    }

    public String getDateOfBirthString() {
        return dateOfBirth != null ? new SimpleDateFormat(EXPORT_DATE_PATTERN).format(dateOfBirth) : "-";

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
                + " , productionCenter :" + this.getProductionCenter()
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
                + ", externalSampleId=" + externalSampleId
                + ", productionCenter :" + productionCenter;
    }


    /**
     * @return the dateOfExperiment
     */
    public Date getDateOfExperiment() {

    	if(dateOfExperiment==null){
    		return null;
    	}
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

    	if(dateOfBirth==null){
    		return null;
    	}
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
    }


    public void setWeightDate(Date weightDate) {

        this.weightDate = weightDate;
    }
}
