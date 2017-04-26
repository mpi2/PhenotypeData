package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class StatisticalResult {

    @GraphId
    Long id;

    private Integer dbId;
    private List<String> phenotypeSex; // male/female or both
    private String phenotypingCenter;
    private String colonyId;
    private String zygosity;
    private Double pValue;
    private Double effectSize;
    private boolean significant;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    @Relationship(type="ALLELE", direction=Relationship.OUTGOING)
    private Allele allele;

    @Relationship(type="MP", direction=Relationship.OUTGOING)
    private Set<Mp> mps;

    @Relationship(type="PROCEDURE", direction=Relationship.OUTGOING)
    private Procedure procedure;

    @Relationship(type="PARAMETER", direction=Relationship.OUTGOING)
    private Parameter parameter;

    public Long getId() {
        return id;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPhenotypeSex() {
        return phenotypeSex;
    }

    public void setPhenotypeSex(List<String> phenotypeSex) {
        this.phenotypeSex = phenotypeSex;
    }

    public String getPhenotypingCenter() {
        return phenotypingCenter;
    }

    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public Double getpValue() {
        return pValue;
    }

    public void setpValue(Double pValue) {
        this.pValue = pValue;
    }

    public Double getEffectSize() {
        return effectSize;
    }

    public void setEffectSize(Double effectSize) {
        this.effectSize = effectSize;
    }

    public boolean isSignificant() {
        return significant;
    }

    public void setSignificant(boolean significant) {
        this.significant = significant;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Allele getAllele() {
        return allele;
    }

    public void setAllele(Allele allele) {
        this.allele = allele;
    }

    public Set<Mp> getMps() {
        return mps;
    }

    public void setMps(Set<Mp> mps) {
        this.mps = mps;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "StatisticalResult{" +
                "id=" + id +
                ", dbId=" + dbId +
                ", phenotypeSex=" + phenotypeSex +
                ", phenotypingCenter='" + phenotypingCenter + '\'' +
                ", colonyId='" + colonyId + '\'' +
                ", zygosity='" + zygosity + '\'' +
                ", pValue=" + pValue +
                ", effectSize=" + effectSize +
                ", significant=" + significant +
                ", gene=" + gene +
                ", allele=" + allele +
                ", mps=" + mps +
                ", procedure=" + procedure +
                ", parameter=" + parameter +
                '}';
    }
}
