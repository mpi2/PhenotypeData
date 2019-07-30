/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.common;

public class PhenotypeParameterOntologyAnnotation {

    private long    id;
    private String  eventType;
    private Integer optionId;
    private String  ontologyAcc;
    private Long    ontologyDbId;
    private String  sex;

    public PhenotypeParameterOntologyAnnotation() {
    }

    public PhenotypeParameterOntologyAnnotation(PhenotypeParameterOntologyAnnotation ppoa) {

        this.id = ppoa.id;
        this.eventType = ppoa.eventType;
        this.optionId = ppoa.optionId;
        this.ontologyAcc = ppoa.ontologyAcc;
        this.ontologyDbId = ppoa.ontologyDbId;
        this.sex = ppoa.sex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public String getOntologyAcc() {
        return ontologyAcc;
    }

    public void setOntologyAcc(String ontologyAcc) {
        this.ontologyAcc = ontologyAcc;
    }

    public Long getOntologyDbId() {
        return ontologyDbId;
    }

    public void setOntologyDbId(Long ontologyDbId) {
        this.ontologyDbId = ontologyDbId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}