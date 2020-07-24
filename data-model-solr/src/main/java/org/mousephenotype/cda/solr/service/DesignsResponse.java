package org.mousephenotype.cda.solr.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DesignsResponse {
    private int designId;

    public DesignData getData() {
        return data;
    }

    public void setData(DesignData data) {
        this.data = data;
    }

    @JsonProperty("data")
    private DesignData data;


}
