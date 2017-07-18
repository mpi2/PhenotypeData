package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckchen on 18/07/2017.
 */
public class AdvancedSearchGeneForm {

    // inputs and checkboxes
    private String chrId;
    private Integer chrStart;
    private Integer chrEnd;

    private List<String> mouseMarkerSymbols = new ArrayList<>();
    private List<String> mouseMgiGeneIds = new ArrayList<>();
    private List<String> humanMarkerSymbols = new ArrayList<>();

    private List<String> genotypes = new ArrayList<>();
    private List<String> alleleTypes = new ArrayList<>();

    //customed output columns
    private Boolean showAlleleSymbol;
    private Boolean showAlleleId;
    private Boolean shwoAlleleDesc;
    private Boolean shwoAlleleType;
    private Boolean shwoAlleleMutationType;

    private Boolean showHgncGeneSymbol;

    private Boolean showMgiGeneSymbol;
    private Boolean showMgiGeneSynonym;
    private Boolean showMgiGeneId;
    private Boolean showMgiGeneType;
    private Boolean showMgiGeneName;

    private Boolean showChrId;
    private Boolean showChrStart;
    private Boolean showChrEnd;
    private Boolean showChrStrand;

    private Boolean showEnsemblGeneId;

    private Boolean shwoEsCellAvailable;
    private Boolean shwoMouseAvailable;
    private Boolean shwoPhenotypingAvailable;

    public String getChrId() {
        return chrId;
    }

    public void setChrId(String chrId) {
        this.chrId = chrId;
    }

    public Integer getChrStart() {
        return chrStart;
    }

    public void setChrStart(Integer chrStart) {
        this.chrStart = chrStart;
    }

    public Integer getChrEnd() {
        return chrEnd;
    }

    public void setChrEnd(Integer chrEnd) {
        this.chrEnd = chrEnd;
    }

    public List<String> getMouseMarkerSymbols() {
        return mouseMarkerSymbols;
    }

    public void setMouseMarkerSymbols(List<String> mouseMarkerSymbols) {
        this.mouseMarkerSymbols = mouseMarkerSymbols;
    }

    public List<String> getMouseMgiGeneIds() {
        return mouseMgiGeneIds;
    }

    public void setMouseMgiGeneIds(List<String> mouseMgiGeneIds) {
        this.mouseMgiGeneIds = mouseMgiGeneIds;
    }

    public List<String> getHumanMarkerSymbols() {
        return humanMarkerSymbols;
    }

    public void setHumanMarkerSymbols(List<String> humanMarkerSymbols) {
        this.humanMarkerSymbols = humanMarkerSymbols;
    }

    public List<String> getGenotypes() {
        return genotypes;
    }

    public void setGenotypes(List<String> genotypes) {
        this.genotypes = genotypes;
    }

    public List<String> getAlleleTypes() {
        return alleleTypes;
    }

    public void setAlleleTypes(List<String> alleleTypes) {
        this.alleleTypes = alleleTypes;
    }

    public Boolean getShowAlleleSymbol() {
        return showAlleleSymbol;
    }

    public void setShowAlleleSymbol(Boolean showAlleleSymbol) {
        this.showAlleleSymbol = showAlleleSymbol;
    }

    public Boolean getShowAlleleId() {
        return showAlleleId;
    }

    public void setShowAlleleId(Boolean showAlleleId) {
        this.showAlleleId = showAlleleId;
    }

    public Boolean getShwoAlleleDesc() {
        return shwoAlleleDesc;
    }

    public void setShwoAlleleDesc(Boolean shwoAlleleDesc) {
        this.shwoAlleleDesc = shwoAlleleDesc;
    }

    public Boolean getShwoAlleleType() {
        return shwoAlleleType;
    }

    public void setShwoAlleleType(Boolean shwoAlleleType) {
        this.shwoAlleleType = shwoAlleleType;
    }

    public Boolean getShwoAlleleMutationType() {
        return shwoAlleleMutationType;
    }

    public void setShwoAlleleMutationType(Boolean shwoAlleleMutationType) {
        this.shwoAlleleMutationType = shwoAlleleMutationType;
    }

    public Boolean getShowHgncGeneSymbol() {
        return showHgncGeneSymbol;
    }

    public void setShowHgncGeneSymbol(Boolean showHgncGeneSymbol) {
        this.showHgncGeneSymbol = showHgncGeneSymbol;
    }

    public Boolean getShowMgiGeneSymbol() {
        return showMgiGeneSymbol;
    }

    public void setShowMgiGeneSymbol(Boolean showMgiGeneSymbol) {
        this.showMgiGeneSymbol = showMgiGeneSymbol;
    }

    public Boolean getShowMgiGeneSynonym() {
        return showMgiGeneSynonym;
    }

    public void setShowMgiGeneSynonym(Boolean showMgiGeneSynonym) {
        this.showMgiGeneSynonym = showMgiGeneSynonym;
    }

    public Boolean getShowMgiGeneId() {
        return showMgiGeneId;
    }

    public void setShowMgiGeneId(Boolean showMgiGeneId) {
        this.showMgiGeneId = showMgiGeneId;
    }

    public Boolean getShowMgiGeneType() {
        return showMgiGeneType;
    }

    public void setShowMgiGeneType(Boolean showMgiGeneType) {
        this.showMgiGeneType = showMgiGeneType;
    }

    public Boolean getShowMgiGeneName() {
        return showMgiGeneName;
    }

    public void setShowMgiGeneName(Boolean showMgiGeneName) {
        this.showMgiGeneName = showMgiGeneName;
    }

    public Boolean getShowChrId() {
        return showChrId;
    }

    public void setShowChrId(Boolean showChrId) {
        this.showChrId = showChrId;
    }

    public Boolean getShowChrStart() {
        return showChrStart;
    }

    public void setShowChrStart(Boolean showChrStart) {
        this.showChrStart = showChrStart;
    }

    public Boolean getShowChrEnd() {
        return showChrEnd;
    }

    public void setShowChrEnd(Boolean showChrEnd) {
        this.showChrEnd = showChrEnd;
    }

    public Boolean getShowChrStrand() {
        return showChrStrand;
    }

    public void setShowChrStrand(Boolean showChrStrand) {
        this.showChrStrand = showChrStrand;
    }

    public Boolean getShowEnsemblGeneId() {
        return showEnsemblGeneId;
    }

    public void setShowEnsemblGeneId(Boolean showEnsemblGeneId) {
        this.showEnsemblGeneId = showEnsemblGeneId;
    }

    public Boolean getShwoEsCellAvailable() {
        return shwoEsCellAvailable;
    }

    public void setShwoEsCellAvailable(Boolean shwoEsCellAvailable) {
        this.shwoEsCellAvailable = shwoEsCellAvailable;
    }

    public Boolean getShwoMouseAvailable() {
        return shwoMouseAvailable;
    }

    public void setShwoMouseAvailable(Boolean shwoMouseAvailable) {
        this.shwoMouseAvailable = shwoMouseAvailable;
    }

    public Boolean getShwoPhenotypingAvailable() {
        return shwoPhenotypingAvailable;
    }

    public void setShwoPhenotypingAvailable(Boolean shwoPhenotypingAvailable) {
        this.shwoPhenotypingAvailable = shwoPhenotypingAvailable;
    }

    @Override
    public String toString() {
        return "AdvancedSearchGeneForm{" +
                "chrId='" + chrId + '\'' +
                ", chrStart=" + chrStart +
                ", chrEnd=" + chrEnd +
                ", mouseMarkerSymbols=" + mouseMarkerSymbols +
                ", mouseMgiGeneIds=" + mouseMgiGeneIds +
                ", humanMarkerSymbols=" + humanMarkerSymbols +
                ", genotypes=" + genotypes +
                ", alleleTypes=" + alleleTypes +
                ", showAlleleSymbol=" + showAlleleSymbol +
                ", showAlleleId=" + showAlleleId +
                ", shwoAlleleDesc=" + shwoAlleleDesc +
                ", shwoAlleleType=" + shwoAlleleType +
                ", shwoAlleleMutationType=" + shwoAlleleMutationType +
                ", showHgncGeneSymbol=" + showHgncGeneSymbol +
                ", showMgiGeneSymbol=" + showMgiGeneSymbol +
                ", showMgiGeneSynonym=" + showMgiGeneSynonym +
                ", showMgiGeneId=" + showMgiGeneId +
                ", showMgiGeneType=" + showMgiGeneType +
                ", showMgiGeneName=" + showMgiGeneName +
                ", showChrId=" + showChrId +
                ", showChrStart=" + showChrStart +
                ", showChrEnd=" + showChrEnd +
                ", showChrStrand=" + showChrStrand +
                ", showEnsemblGeneId=" + showEnsemblGeneId +
                ", shwoEsCellAvailable=" + shwoEsCellAvailable +
                ", shwoMouseAvailable=" + shwoMouseAvailable +
                ", shwoPhenotypingAvailable=" + shwoPhenotypingAvailable +
                '}';
    }
}
