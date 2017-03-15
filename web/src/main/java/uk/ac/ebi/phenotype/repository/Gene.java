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
    private List<String> markerSynonym;
    private List<String> humanGeneSymbol;
    private String markerName;
    private String latestPhenotypeStatus;
    private String latestEsCellStatus;
    private String latestMouseStatus;
    private String latestPhenotypingCentre;
    private String latestProductionCentre;
    private String seqRegionId;
    private String seqRegionStart;
    private String seqRegionEnd;
    private String embryoDataAvailable;

    @Relationship(type="IS_ENSEMBL_GENE_ID", direction=Relationship.OUTGOING)
    private Set<EnsemblGeneId> ensemblGeneIds;

    @Relationship(type="HAS_MARKER_SYNONYM", direction=Relationship.OUTGOING)
    private Set<MarkerSynonym> markerSynonyms;

    @Relationship(type="HUMAN_GENE_SYMBOL", direction=Relationship.OUTGOING)
    private Set<HumanGeneSymbol> humanGeneSymbols;

    @Relationship(type="HAS_ALLELE", direction=Relationship.OUTGOING)
    private Set<Allele> alleles;

    @Relationship(type="HAS_DISEASE", direction=Relationship.OUTGOING)
    private Set<DiseaseModelAssociation> diseases;

    @Relationship(type="HAS_PHENOTYPE", direction=Relationship.OUTGOING)
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

    public List<String> getMarkerSynonym() {
        return markerSynonym;
    }

    public void setMarkerSynonym(List<String> markerSynonym) {
        this.markerSynonym = markerSynonym;
    }

    public List<String> getHumanGeneSymbol() {
        return humanGeneSymbol;
    }

    public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
        this.humanGeneSymbol = humanGeneSymbol;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getLatestPhenotypeStatus() {
        return latestPhenotypeStatus;
    }

    public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {
        this.latestPhenotypeStatus = latestPhenotypeStatus;
    }

    public String getLatestEsCellStatus() {
        return latestEsCellStatus;
    }

    public void setLatestEsCellStatus(String latestEsCellStatus) {
        this.latestEsCellStatus = latestEsCellStatus;
    }

    public String getLatestMouseStatus() {
        return latestMouseStatus;
    }

    public void setLatestMouseStatus(String latestMouseStatus) {
        this.latestMouseStatus = latestMouseStatus;
    }

    public String getLatestPhenotypingCentre() {
        return latestPhenotypingCentre;
    }

    public void setLatestPhenotypingCentre(String latestPhenotypingCentre) {
        this.latestPhenotypingCentre = latestPhenotypingCentre;
    }

    public String getLatestProductionCentre() {
        return latestProductionCentre;
    }

    public void setLatestProductionCentre(String latestProductionCentre) {
        this.latestProductionCentre = latestProductionCentre;
    }

    public String getSeqRegionId() {
        return seqRegionId;
    }

    public void setSeqRegionId(String seqRegionId) {
        this.seqRegionId = seqRegionId;
    }

    public String getSeqRegionStart() {
        return seqRegionStart;
    }

    public void setSeqRegionStart(String seqRegionStart) {
        this.seqRegionStart = seqRegionStart;
    }

    public String getSeqRegionEnd() {
        return seqRegionEnd;
    }

    public void setSeqRegionEnd(String seqRegionEnd) {
        this.seqRegionEnd = seqRegionEnd;
    }

    public String getEmbryoDataAvailable() {
        return embryoDataAvailable;
    }

    public void setEmbryoDataAvailable(String embryoDataAvailable) {
        this.embryoDataAvailable = embryoDataAvailable;
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

    public Set<DiseaseModelAssociation> getDiseases() {
        return diseases;
    }

    public void setDiseases(Set<DiseaseModelAssociation> diseases) {
        this.diseases = diseases;
    }

    public Set<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public void setPhenotypes(Set<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
    }
}
