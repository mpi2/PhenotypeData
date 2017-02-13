package org.mousephenotype.cda.indexers.beans;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilinca on 30/01/2017.
 */
public class PChannelDTO {

    public final static String ID = "id";
    public final static String ASSOCIATED_ROI = "associated_roi";
    public final static String ASSOCIATED_IMAGE = "associated_image";
    public final static String GENE_ID = "gene_id";
    public final static String GENE_SYMBOL = "gene_symbol";
    public final static String GENETIC_FEATURE_ID = "genetic_feature_id";
    public final static String GENETIC_FEATURE_SYMBOL = "genetic_feature_symbol";
    public final static String GENETIC_FEATURE_ENSEML_ID = "genetic_feature_ensembl_id";
    public final static String MUTATION_TYPE = "mutation_type";
    public final static String CHROMOSOME = "chromosome";
    public final static String START_POS = "start_pos";
    public final static String END_POS = "end_pos";
    public final static String STRAND = "strand";
    public final static String ZYGOSITY = "zygosity";
    public final static String MARKER = "marker";
    public final static String VISUALISATION_METHOD_LABEL = "visualisation_method_label";
    public final static String VISUALISATION_METHOD_ID = "visualisation_method_id";
    public final static String VISUALISATION_METHOD_SYNONYMS = "visualisation_method_synonyms";
    public final static String VISUALISATION_METHOD_FREETEXT = "visualisation_method_freetext";

    @Field(ID)
    String id;

    @Field(ASSOCIATED_IMAGE)
    String associatedImage;
    @Field(ASSOCIATED_ROI)
    List<String> associatedRoi;
    @Field(CHROMOSOME)
    String chromosome;
    @Field(END_POS)
    long endPos;
    @Field(GENE_ID)
    String geneId;
    @Field(GENE_SYMBOL)
    String geneSymbol;
    @Field(GENETIC_FEATURE_ENSEML_ID)
    String geneticFeatureEnsemlId;
    @Field(GENETIC_FEATURE_ID)
    String geneticFeatureId;
    @Field(GENETIC_FEATURE_SYMBOL)
    String geneticFeatureSymbol;
    @Field(MUTATION_TYPE)
    String mutationType;
    @Field(MARKER)
    String marker;
    @Field(START_POS)
    long startPos;
    @Field(STRAND)
    String strand;
    @Field(ZYGOSITY)
    String zygosity;
    @Field(VISUALISATION_METHOD_ID)
    ArrayList<String> visualisationMethodId;
    @Field(VISUALISATION_METHOD_LABEL)
    ArrayList<String> visualisationMethodLabel;
    @Field(VISUALISATION_METHOD_SYNONYMS)
    ArrayList<String> visualisationMethodSynonyms;
    @Field(VISUALISATION_METHOD_FREETEXT)
    ArrayList<String> visualisationMethodFreetext;

    /**
     * @return the id
     */
    public String getId() {

        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {

        this.id = id;
    }


    public String getMutationType() {

        return mutationType;
    }


    public void setMutationType(String mutationType) {

        this.mutationType = mutationType;
    }

    /**
     * @return the associatedImage
     */
    public String getAssociatedImage() {

        return associatedImage;
    }

    /**
     * @param associatedImage the associatedImage to set
     */
    public void setAssociatedImage(String associatedImage) {

        this.associatedImage = associatedImage;
    }

    /**
     * @return the associatedRoi
     */
    public List<String> getAssociatedRoi() {

        return associatedRoi;
    }

    public void addAssociatedRoi(String roiId) {

        if (associatedRoi == null) {
            associatedRoi = new ArrayList<>();
        }
        associatedRoi.add(roiId);
    }

    /**
     * @param associatedRoi the associatedRoi to set
     */
    public void setAssociatedRoi(List<String> associatedRoi) {

        this.associatedRoi = associatedRoi;
    }

    /**
     * @return the chromosome
     */
    public String getChromosome() {

        return chromosome;
    }

    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(String chromosome) {

        this.chromosome = chromosome;
    }

    /**
     * @return the endPos
     */
    public long getEndPos() {

        return endPos;
    }

    /**
     * @param endPos the endPos to set
     */
    public void setEndPos(long endPos) {

        this.endPos = endPos;
    }

    /**
     * @return the geneId
     */
    public String getGeneId() {

        return geneId;
    }

    /**
     * @param geneId the geneId to set
     */
    public void setGeneId(String geneId) {

        this.geneId = geneId;
    }

    /**
     * @return the geneSymbol
     */
    public String getGeneSymbol() {

        return geneSymbol;
    }

    /**
     * @param geneSymbol the geneSymbol to set
     */
    public void setGeneSymbol(String geneSymbol) {

        this.geneSymbol = geneSymbol;
    }

    /**
     * @return the geneticFeatureEnsemlId
     */
    public String getGeneticFeatureEnsemlId() {

        return geneticFeatureEnsemlId;
    }

    /**
     * @param geneticFeatureEnsemlId the geneticFeatureEnsemlId to set
     */
    public void setGeneticFeatureEnsemlId(String geneticFeatureEnsemlId) {

        this.geneticFeatureEnsemlId = geneticFeatureEnsemlId;
    }

    /**
     * @return the geneticFeatureId
     */
    public String getGeneticFeatureId() {

        return geneticFeatureId;
    }

    /**
     * @param geneticFeatureId the geneticFeatureId to set
     */
    public void setGeneticFeatureId(String geneticFeatureId) {

        this.geneticFeatureId = geneticFeatureId;
    }

    /**
     * @return the geneticFeatureSymbol
     */
    public String getGeneticFeatureSymbol() {

        return geneticFeatureSymbol;
    }

    /**
     * @param geneticFeatureSymbol the geneticFeatureSymbol to set
     */
    public void setGeneticFeatureSymbol(String geneticFeatureSymbol) {

        this.geneticFeatureSymbol = geneticFeatureSymbol;
    }

    /**
     * @return the marker
     */
    public String getMarker() {

        return marker;
    }

    /**
     * @param marker the marker to set
     */
    public void setMarker(String marker) {

        this.marker = marker;
    }

    /**
     * @return the startPos
     */
    public long getStartPos() {

        return startPos;
    }

    /**
     * @param startPos the startPos to set
     */
    public void setStartPos(long startPos) {

        this.startPos = startPos;
    }

    /**
     * @return the strand
     */
    public String getStrand() {

        return strand;
    }

    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {

        this.strand = strand;
    }

    /**
     * @return the zygosity
     */
    public String getZygosity() {

        return zygosity;
    }

    /**
     * @param zygosity the zygosity to set
     */
    public void setZygosity(String zygosity) {

        this.zygosity = zygosity;
    }

    public ArrayList<String> getVisualisationMethodSynonyms() {
        return visualisationMethodSynonyms;
    }

    public void setVisualisationMethodSynonyms(ArrayList<String> visualisationMethodSynonyms) {
        this.visualisationMethodSynonyms = visualisationMethodSynonyms;
    }

    public void addVisualisationMethodSynonyms(ArrayList<String> visualisationMethodSynonyms) {
        if (this.visualisationMethodSynonyms == null) {
            this.visualisationMethodSynonyms = new ArrayList<>();
        }
        this.visualisationMethodSynonyms.addAll(visualisationMethodSynonyms);
    }

    public ArrayList<String> getVisualisationMethodId() {
        return visualisationMethodId;
    }

    public void setVisualisationMethodId(ArrayList<String> visualisationMethodId) {
        this.visualisationMethodId = visualisationMethodId;
    }

    public void addVisualisationMethodId(String visualisationMethodId) {
        if (this.visualisationMethodId == null) {
            this.visualisationMethodId = new ArrayList<>();
        }
        this.visualisationMethodId.add(visualisationMethodId);
    }

    public ArrayList<String> getVisualisationMethodLabel() {
        return visualisationMethodLabel;
    }

    public void setVisualisationMethodLabel(ArrayList<String> visualisationMethodLabel) {
        this.visualisationMethodLabel = visualisationMethodLabel;
    }

    public void addVisualisationMethodLabel(String visualisationMethodLabel) {
        if (this.visualisationMethodLabel == null) {
            this.visualisationMethodLabel = new ArrayList<>();
        }
        this.visualisationMethodLabel.add(visualisationMethodLabel);
    }

    public ArrayList<String> getVisualisationMethodFreetext() {
        return visualisationMethodFreetext;
    }

    public void setVisualisationMethodFreetext(ArrayList<String> visualisationMethodFreetext) {
        this.visualisationMethodFreetext = visualisationMethodFreetext;
    }

    public void addVisualisationMethodFreetext(String visualisationMethodFreetext) {
        if (this.visualisationMethodFreetext == null) {
            this.visualisationMethodFreetext = new ArrayList<>();
        }
        this.visualisationMethodFreetext.add(visualisationMethodFreetext);
    }

    @Override
    public String toString() {
        return "PChannelDTO{" +
                "id='" + id + '\'' +
                ", associatedImage='" + associatedImage + '\'' +
                ", associatedRoi=" + associatedRoi +
                ", chromosome='" + chromosome + '\'' +
                ", endPos=" + endPos +
                ", geneId='" + geneId + '\'' +
                ", geneSymbol='" + geneSymbol + '\'' +
                ", geneticFeatureEnsemlId='" + geneticFeatureEnsemlId + '\'' +
                ", geneticFeatureId='" + geneticFeatureId + '\'' +
                ", geneticFeatureSymbol='" + geneticFeatureSymbol + '\'' +
                ", mutationType='" + mutationType + '\'' +
                ", marker='" + marker + '\'' +
                ", startPos=" + startPos +
                ", strand='" + strand + '\'' +
                ", zygosity='" + zygosity + '\'' +
                ", visualisationMethodId=" + visualisationMethodId +
                ", visualisationMethodLabel=" + visualisationMethodLabel +
                ", visualisationMethodSynonyms=" + visualisationMethodSynonyms +
                ", visualisationMethodFreetext=" + visualisationMethodFreetext +
                '}';
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (associatedImage != null ? associatedImage.hashCode() : 0);
        result = 31 * result + (associatedRoi != null ? associatedRoi.hashCode() : 0);
        result = 31 * result + (chromosome != null ? chromosome.hashCode() : 0);
        result = 31 * result + (int) (endPos ^ (endPos >>> 32));
        result = 31 * result + (geneId != null ? geneId.hashCode() : 0);
        result = 31 * result + (geneSymbol != null ? geneSymbol.hashCode() : 0);
        result = 31 * result + (geneticFeatureEnsemlId != null ? geneticFeatureEnsemlId.hashCode() : 0);
        result = 31 * result + (geneticFeatureId != null ? geneticFeatureId.hashCode() : 0);
        result = 31 * result + (geneticFeatureSymbol != null ? geneticFeatureSymbol.hashCode() : 0);
        result = 31 * result + (mutationType != null ? mutationType.hashCode() : 0);
        result = 31 * result + (marker != null ? marker.hashCode() : 0);
        result = 31 * result + (int) (startPos ^ (startPos >>> 32));
        result = 31 * result + (strand != null ? strand.hashCode() : 0);
        result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
        result = 31 * result + (visualisationMethodId != null ? visualisationMethodId.hashCode() : 0);
        result = 31 * result + (visualisationMethodLabel != null ? visualisationMethodLabel.hashCode() : 0);
        result = 31 * result + (visualisationMethodSynonyms != null ? visualisationMethodSynonyms.hashCode() : 0);
        result = 31 * result + (visualisationMethodFreetext != null ? visualisationMethodFreetext.hashCode() : 0);
        return result;
    }
}
