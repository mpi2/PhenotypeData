
package org.mousephenotype.cda.solr.service.embryoviewer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "analysis_download_url",
        "analysis_view_url",
    "centre",
    "colony_id",
    "has_automated_analysis",
    "mgi",
    "url",
    "procedures_parameters"
})
public class Colony {

    @JsonProperty("centre")
    public String centre;
    @JsonProperty("colony_id")
    public String colonyId;
    @JsonProperty("has_automated_analysis")
    public Boolean hasAutomatedAnalysis;
    @JsonProperty("analysis_download_url")
    public String analysisDownloadUrl;
    @JsonProperty("analysis_view_url")
    public String analysisViewUrl;
    @JsonProperty("mgi")
    public String mgi;
    @JsonProperty("url")
    public String url;
    @JsonProperty("procedures_parameters")
    public List<ProceduresParameter> proceduresParameters = null;

}
