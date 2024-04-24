package org.mousephenotype.cda.solr.web.dto;

import org.mousephenotype.cda.solr.service.dto.ObservationDTO;

import java.util.HashMap;
import java.util.Map;

public abstract class ViabilityDTO {

    public abstract String getTotalPups();
    public abstract String getTotalPupsWt();
    public abstract String getTotalPupsHom();
    public abstract String getTotalPupsHet();
    public abstract String getTotalMalePups();
    public abstract String getTotalFemalePups();
    public abstract String getTotalMaleHom();
    public abstract String getTotalFemaleHet();
    public abstract String getTotalMaleHet();
    public abstract String getTotalFemaleWt();
    public abstract String getTotalMaleWt();
    public abstract String getTotalFemaleHom();

    //only in version 2 but subclasses and EL don't seem to play well and we can just return null?
    public abstract String getTotalMaleHem();
    public abstract String getTotalFemaleAnz();

    private Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();
    private String totalChart = "";
    private String maleChart = "";
    private String femaleChart = "";
    private String parameterStableId = "";
    private String category = "";// should get set to e.g. Homozygous - Viable

    private String sequenceId;

    public String getParameterStableId() { return parameterStableId; }

    public void setParameterStableId(String parameterStableId) { this.parameterStableId = parameterStableId; }

    public String getCategory() { return category;}
    public void setCategory(String category) { this.category = category;}
    public String getTotalChart() { return totalChart;}
    public String getMaleChart() { return maleChart;}
    public String getFemaleChart() { return femaleChart;}
    public void setTotalChart(String totalChart) { this.totalChart = totalChart;}
    public void setMaleChart(String maleChart) { this.maleChart = maleChart;}
    public void setFemaleChart(String femaleChart) { this.femaleChart = femaleChart;}
    public Map<String, ObservationDTO> getParamStableIdToObservation() { return paramStableIdToObservation;}
    public void setParamStableIdToObservation(Map<String, ObservationDTO> paramStableIdToObservation) { this.paramStableIdToObservation = paramStableIdToObservation;}

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }
}
