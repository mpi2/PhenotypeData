package org.mousephenotype.cda.solr.service.embryoviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "colonies"
})
public class Embryo {

    @JsonProperty("colonies")
    public List<Colony> colonies = null;

}
