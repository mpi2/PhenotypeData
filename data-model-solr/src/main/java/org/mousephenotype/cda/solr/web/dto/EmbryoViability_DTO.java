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

import org.mousephenotype.cda.enumerations.EmbryoViability;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;

import java.util.*;


//Note: The class name relates to the stage of the embryo

public class EmbryoViability_DTO {


    public final EmbryoViability parameters;

    private String totalChart = "";
    private String deadChart = "";
    private String liveChart = "";
    private String category = "";// should get set to e.g. Homozygous - Viable
    private String proceedureName = "";// should get set to e.g. Viability E9.5 Secondary Screen

    private Map<String, ObservationDTO> paramStableIdToObservation= new HashMap<>();


    public EmbryoViability_DTO(EmbryoViability parameters){

        this.parameters = parameters;
    }







    public Map<String, ObservationDTO> getParamStableIdToObservation() {

        return paramStableIdToObservation;
    }



    public void setParamStableIdToObservation(Map<String, ObservationDTO> paramStableIdToObservation) {

        this.paramStableIdToObservation = paramStableIdToObservation;
    }









    public String getCategory() {

        return category;
    }


    public void setCategory(String category) {

        this.category = category;
    }


    public String getProceedureName() {

        return proceedureName;
    }


    public void setProceedureName(String proceedureName) {

        this.proceedureName = proceedureName;
    }




    public String getTotalChart() {

        return totalChart;
    }


    public void setTotalChart(String totalChart) {

        this.totalChart = totalChart;
    }


    public String getDeadChart() {

        return deadChart;
    }


    public void setDeadChart(String deadChart) {

        this.deadChart = deadChart;
    }


    public String getLiveChart() {

        return liveChart;
    }


    public void setLiveChart(String liveChart) {

        this.liveChart = liveChart;
    }




    public EmbryoViability getParameters() {

        return this.parameters;
    }

}


