package org.mousephenotype.cda.loads.annotations;

import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;

public class GenotypePhenotypeAssociationDTO {

    public static final String saveStatement = "INSERT INTO phenotype_call_summary(external_db_id, project_id, gf_acc, gf_db_id, strain_acc, strain_db_id, allele_acc, allele_db_id, sex, zygosity, parameter_id, procedure_id, pipeline_id, mp_acc, mp_db_id, p_value, effect_size, organisation_id, colony_id, life_stage, life_stage_acc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String saveCategoricalAssociationStatement = "INSERT INTO stat_result_phenotype_call_summary(categorical_result_id, phenotype_call_summary_id) VALUES (?, ?)";
    public static final String saveUnidimensionalAssociationStatement = "INSERT INTO stat_result_phenotype_call_summary(unidimensional_result_id, phenotype_call_summary_id) VALUES (?, ?)";
    public static final String saveRRPlusAssociationStatement = "INSERT INTO stat_result_phenotype_call_summary(rrplus_result_id, phenotype_call_summary_id) VALUES (?, ?)";

    private StatisticalResultDataType dataType;

    private Integer external_db_id;
    private Integer project_id;
    private String gf_acc;
    private Integer gf_db_id;
    private String strain_acc;
    private Integer strain_db_id;
    private String allele_acc;
    private Integer allele_db_id;
    private SexType sex;
    private ZygosityType zygosity;
    private String parameterStableId;
    private String procedureStableId;
    private String pipelineStableId;
    private String mp_acc;
    private Integer mp_db_id;
    private Float p_value;
    private String effect_size;
    private String phenotypingCenter; // Organisation
    private String colony_id;
    private String lifeStageName;
    private String lifeStageAcc;

    public String getSaveStatement() {
        return saveStatement;
    }

    public String getSaveAssociationStatement() {

        String saveStatement;

        switch (dataType) {
            case categorical:
                saveStatement = saveCategoricalAssociationStatement;
                break;
            case unidimensional:
                saveStatement = saveUnidimensionalAssociationStatement;
                break;
            case rr_plus:
                saveStatement = saveRRPlusAssociationStatement;
                break;
            default:
                saveStatement = null;
                break;
        }

        return saveStatement;
    }

    public StatisticalResultDataType getDataType() {
        return dataType;
    }

    public void setDataType(StatisticalResultDataType dataType) {
        this.dataType = dataType;
    }

    public Integer getExternal_db_id() {
        return external_db_id;
    }

    public void setExternal_db_id(Integer external_db_id) {
        this.external_db_id = external_db_id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getGf_acc() {
        return gf_acc;
    }

    public void setGf_acc(String gf_acc) {
        this.gf_acc = gf_acc;
    }

    public Integer getGf_db_id() {
        return gf_db_id;
    }

    public void setGf_db_id(Integer gf_db_id) {
        this.gf_db_id = gf_db_id;
    }

    public String getStrain_acc() {
        return strain_acc;
    }

    public void setStrain_acc(String strain_acc) {
        this.strain_acc = strain_acc;
    }

    public Integer getStrain_db_id() {
        return strain_db_id;
    }

    public void setStrain_db_id(Integer strain_db_id) {
        this.strain_db_id = strain_db_id;
    }

    public String getAllele_acc() {
        return allele_acc;
    }

    public void setAllele_acc(String allele_acc) {
        this.allele_acc = allele_acc;
    }

    public Integer getAllele_db_id() {
        return allele_db_id;
    }

    public void setAllele_db_id(Integer allele_db_id) {
        this.allele_db_id = allele_db_id;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public ZygosityType getZygosity() {
        return zygosity;
    }

    public void setZygosity(ZygosityType zygosity) {
        this.zygosity = zygosity;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public String getProcedureStableId() {
        return procedureStableId;
    }

    public void setProcedureStableId(String procedureStableId) {
        this.procedureStableId = procedureStableId;
    }

    public String getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public String getMp_acc() {
        return mp_acc;
    }

    public void setMp_acc(String mp_acc) {
        this.mp_acc = mp_acc;
    }

    public Integer getMp_db_id() {
        return mp_db_id;
    }

    public void setMp_db_id(Integer mp_db_id) {
        this.mp_db_id = mp_db_id;
    }

    public Float getP_value() {
        return p_value;
    }

    public void setP_value(Float p_value) {
        this.p_value = p_value;
    }

    public String getEffect_size() {
        return effect_size;
    }

    public void setEffect_size(String effect_size) {
        this.effect_size = effect_size;
    }

    public String getPhenotypingCenter() {
        return phenotypingCenter;
    }

    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }

    public String getColony_id() {
        return colony_id;
    }

    public void setColony_id(String colony_id) {
        this.colony_id = colony_id;
    }

    public String getLifeStageName() {
        return lifeStageName;
    }

    public void setLifeStageName(String lifeStageName) {
        this.lifeStageName = lifeStageName;
    }

    public String getLifeStageAcc() {
        return lifeStageAcc;
    }

    public void setLifeStageAcc(String lifeStageAcc) {
        this.lifeStageAcc = lifeStageAcc;
    }
}
