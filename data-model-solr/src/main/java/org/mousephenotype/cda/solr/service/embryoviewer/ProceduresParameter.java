
package org.mousephenotype.cda.solr.service.embryoviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "modality",
    "procedure_id",
    "parameter_id"
})
public class ProceduresParameter {

    @JsonProperty("modality")
    public String modality;
    @JsonProperty("procedure_id")
    public String procedureId;
    @JsonProperty("parameter_id")
    public String parameterId;

}
