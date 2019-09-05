package org.mousephenotype.cda.solr.service.embryoviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "procedureId",
        "procedureKey",
        "minFemales",
        "minMales",
        "minAnimals",
        "isVisible",
        "isMandatory",
        "isInternal",
        "name",
        "type",
        "level",
        "majorVersion",
        "minorVersion",
        "description",
        "oldProcedureKey",
        "parameterCollection",
        "scheduleId"
})
public class Procedure {

    @JsonProperty("procedureId")
    public Integer procedureId;
    @JsonProperty("procedureKey")
    public String procedureKey;
    @JsonProperty("minFemales")
    public Integer minFemales;
    @JsonProperty("minMales")
    public Integer minMales;
    @JsonProperty("minAnimals")
    public Integer minAnimals;
    @JsonProperty("isVisible")
    public Boolean isVisible;
    @JsonProperty("isMandatory")
    public Boolean isMandatory;
    @JsonProperty("isInternal")
    public Boolean isInternal;
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public Integer type;
    @JsonProperty("level")
    public String level;
    @JsonProperty("majorVersion")
    public Integer majorVersion;
    @JsonProperty("minorVersion")
    public Integer minorVersion;
    @JsonProperty("description")
    public String description;
    @JsonProperty("oldProcedureKey")
    public Object oldProcedureKey;
    @JsonProperty("parameterCollection")
    public List<Integer> parameterCollection = null;
    @JsonProperty("scheduleId")
    public Integer scheduleId;

}
