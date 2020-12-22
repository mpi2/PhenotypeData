package org.mousephenotype.cda.solr.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Design {
    @JsonProperty("id")
    private int id;
    @JsonProperty("design_id")
    private int designId;

    private String assembly;
    @JsonProperty("feature_type")
    private String featureType;
    @JsonProperty("oligo_sequence")
    private String oligoSequence;
    @JsonProperty("oligo_start")
    private int oligoStart;
    @JsonProperty("oligo_stop")
    private int oligoStop;

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getOligoSequence() {
        return oligoSequence;
    }

    public void setOligoSequence(String oligoSequence) {
        this.oligoSequence = oligoSequence;
    }

    public int getOligoStart() {
        return oligoStart;
    }

    public void setOligoStart(int oligoStart) {
        this.oligoStart = oligoStart;
    }

    public int getOligoStop() {
        return oligoStop;
    }

    public void setOligoStop(int oligoStop) {
        this.oligoStop = oligoStop;
    }

    private String chr;

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    private String strand;

    public int getDesignId() {
        return designId;
    }

    public void setDesignId(int designId) {
        this.designId = designId;
    }

    @Override
    public String toString() {
        return "Design{" +
                "id="+id+
                "designId=" + designId +
                ", assembly='" + assembly + '\'' +
                ", featureType='" + featureType + '\'' +
                ", oligoSequence='" + oligoSequence + '\'' +
                ", oligoStart=" + oligoStart +
                ", oligoStop=" + oligoStop +
                ", chr='" + chr + '\'' +
                ", strand='" + strand + '\'' +
                '}';
    }
}
