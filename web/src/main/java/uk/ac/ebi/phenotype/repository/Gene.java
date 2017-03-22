package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Gene {

    @GraphId
    Long id;

    private String mgiAccessionId;
    private String markerType;
    private String markerSymbol;
    private String markerName;
    private String chrId;
    private String chrStart;
    private String chrEnd;
    private String chrStrand;

    @Relationship(type = "ENSEMBL_GENE_ID", direction = Relationship.OUTGOING)
    private Set<EnsemblGeneId> ensemblGeneIds;

    @Relationship(type = "MARKER_SYNONYM", direction = Relationship.OUTGOING)
    private Set<MarkerSynonym> markerSynonyms;

    @Relationship(type = "HUMAN_GENE_SYMBOL", direction = Relationship.OUTGOING)
    private Set<HumanGeneSymbol> humanGeneSymbols;

    @Relationship(type = "ALLELE", direction = Relationship.OUTGOING)
    private Set<Allele> alleles;

    @Relationship(type = "DISEASE", direction = Relationship.OUTGOING)
    private Set<Disease> diseases;

    @Relationship(type = "PHENOTYPE", direction = Relationship.OUTGOING)
    private Set<Phenotype> phenotypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getChrId() {
        return chrId;
    }

    public void setChrId(String chrId) {
        this.chrId = chrId;
    }

    public String getChrStart() {
        return chrStart;
    }

    public void setChrStart(String chrStart) {
        this.chrStart = chrStart;
    }

    public String getChrEnd() {
        return chrEnd;
    }

    public void setChrEnd(String chrEnd) {
        this.chrEnd = chrEnd;
    }

    public String getChrStrand() {
        return chrStrand;
    }

    public void setChrStrand(String chrStrand) {
        this.chrStrand = chrStrand;
    }

    public Set<EnsemblGeneId> getEnsemblGeneIds() {
        return ensemblGeneIds;
    }

    public void setEnsemblGeneIds(Set<EnsemblGeneId> ensemblGeneIds) {
        this.ensemblGeneIds = ensemblGeneIds;
    }

    public Set<MarkerSynonym> getMarkerSynonyms() {
        return markerSynonyms;
    }

    public void setMarkerSynonyms(Set<MarkerSynonym> markerSynonyms) {
        this.markerSynonyms = markerSynonyms;
    }

    public Set<HumanGeneSymbol> getHumanGeneSymbols() {
        return humanGeneSymbols;
    }

    public void setHumanGeneSymbols(Set<HumanGeneSymbol> humanGeneSymbols) {
        this.humanGeneSymbols = humanGeneSymbols;
    }

    public Set<Allele> getAlleles() {
        return alleles;
    }

    public void setAlleles(Set<Allele> alleles) {
        this.alleles = alleles;
    }

    public Set<Disease> getDiseases() {
        return diseases;
    }

    public void setDiseases(Set<Disease> diseases) {
        this.diseases = diseases;
    }

    public Set<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public void setPhenotypes(Set<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "id=" + id +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", markerType='" + markerType + '\'' +
                ", markerSymbol='" + markerSymbol + '\'' +
                ", markerName='" + markerName + '\'' +
                ", chrId='" + chrId + '\'' +
                ", chrStart='" + chrStart + '\'' +
                ", chrEnd='" + chrEnd + '\'' +
                ", chrStrand='" + chrStrand + '\'' +
                ", ensemblGeneIds=" + ensemblGeneIds +
                ", markerSynonyms=" + markerSynonyms +
                ", humanGeneSymbols=" + humanGeneSymbols +
                ", alleles=" + alleles +
                ", diseases=" + diseases +
                ", phenotypes=" + phenotypes +
                '}';
    }
}