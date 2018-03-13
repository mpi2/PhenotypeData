package org.mousephenotype.cda.loads.statistics.load.threei;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Colony.Prefixes",
        "Parameter.Name",
        "Gene",
        "Genotype",
        "Procedure.Name",
        "Procedure.Id",
        "Parameter.Id",
        "Call.Type",
        "Annotation.Calls",
        "Combine.Gender.Call",
        "samples",
        "Construct",
        "Gender"
})
public class ThreeIFileResult {

    @JsonProperty("Colony.Prefixes")
    String colony_Prefixes;

    @JsonProperty("Parameter.Name")
    String parameter_Name;

    @JsonProperty("Gene")
    String gene;

    @JsonProperty("Genotype")
    String genotype;

    @JsonProperty("Procedure.Name")
    String procedure_Name;

    @JsonProperty("Procedure.Id")
    String procedure_Id;

    @JsonProperty("Parameter.Id")
    String parameter_Id;

    @JsonProperty("Call.Type")
    String call_Type;

    @JsonProperty("Annotation.Calls")
    String annotation_Calls;

    @JsonProperty("Combine.Gender.Call")
    String combine_Gender_Call;

    @JsonProperty("samples")
    String samples;

    @JsonProperty("Construct")
    String construct;

    @JsonProperty("Gender")
    String gender;

    public String getColony_Prefixes() {
        return colony_Prefixes;
    }

    public void setColony_Prefixes(String colony_Prefixes) {
        this.colony_Prefixes = colony_Prefixes;
    }

    public String getParameter_Name() {
        return parameter_Name;
    }

    public void setParameter_Name(String parameter_Name) {
        this.parameter_Name = parameter_Name;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getGenotype() {
        return genotype;
    }

    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }

    public String getProcedure_Name() {
        return procedure_Name;
    }

    public void setProcedure_Name(String procedure_Name) {
        this.procedure_Name = procedure_Name;
    }

    public String getProcedure_Id() {
        return procedure_Id;
    }

    public void setProcedure_Id(String procedure_Id) {
        this.procedure_Id = procedure_Id;
    }

    public String getParameter_Id() {
        return parameter_Id;
    }

    public void setParameter_Id(String parameter_Id) {
        this.parameter_Id = parameter_Id;
    }

    public String getCall_Type() {
        return call_Type;
    }

    public void setCall_Type(String call_Type) {
        this.call_Type = call_Type;
    }

    public String getAnnotation_Calls() {
        return annotation_Calls;
    }

    public void setAnnotation_Calls(String annotation_Calls) {
        this.annotation_Calls = annotation_Calls;
    }

    public String getCombine_Gender_Call() {
        return combine_Gender_Call;
    }

    public void setCombine_Gender_Call(String combine_Gender_Call) {
        this.combine_Gender_Call = combine_Gender_Call;
    }

    public String getSamples() {
        return samples;
    }

    public void setSamples(String samples) {
        this.samples = samples;
    }

    public String getConstruct() {
        return construct;
    }

    public void setConstruct(String construct) {
        this.construct = construct;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ThreeIFileResult{" +
                "colony_Prefixes='" + colony_Prefixes + '\'' +
                ", parameter_Name='" + parameter_Name + '\'' +
                ", gene='" + gene + '\'' +
                ", genotype='" + genotype + '\'' +
                ", procedure_Name='" + procedure_Name + '\'' +
                ", procedure_Id='" + procedure_Id + '\'' +
                ", parameter_Id='" + parameter_Id + '\'' +
                ", call_Type='" + call_Type + '\'' +
                ", annotation_Calls='" + annotation_Calls + '\'' +
                ", combine_Gender_Call='" + combine_Gender_Call + '\'' +
                ", samples='" + samples + '\'' +
                ", construct='" + construct + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
