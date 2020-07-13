package org.mousephenotype.cda.solr.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DesignData {
    public List<Design> getOligos() {
        return oligos;
    }

    public void setOligos(List<Design> oligos) {
        this.oligos = oligos;
    }

    @JsonProperty("oligos")
    private List<Design> oligos;

}
