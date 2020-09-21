package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

public class EssentialGeneDTO {
    public static final String MARKER_SYMBOL="mg_symbol";
    public static final String MGI_ACCESSION="mg_mgi_gene_acc_id";

    @Field(MARKER_SYMBOL)
    private String markerSymbol;

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMgiAccession() {
        return mgiAccession;
    }

    public void setMgiAccessionId(String mgiAccession) {
        this.mgiAccession = mgiAccession;
    }

    @Field(MGI_ACCESSION)
    String mgiAccession;

    public String getMarkerSymbol() {
        return markerSymbol;
    }

//"idg_id":252,
//        "idg_human_gene_id":378,
//        "idg_name":"Disintegrin and metalloproteinase domain-containing protein 7",
//        "idg_tdl":"Tdark",
//        "idg_family":"Enzyme",
//        "idg_symbol":"ADAM7",
//        "idg_uniprot_acc_id":"Q9H2U9",
//        "idg_chr":"8p21.2",

    public static final String IDG_NAME="idg_name";
    @Field(IDG_NAME)
    String idgName;
    public static final String IDG_IDL= "idg_tdl";
    @Field(IDG_IDL)
    String idgIdl;
    public static final String IDG_FAMILY="idg_family";
    @Field(IDG_FAMILY)
    String idgFamily;

    public static final String IDG_SYMBOL="idg_symbol";
    @Field(IDG_SYMBOL)
    String idgSymbol;
    public static final String IDG_UNIPROT_ACC="idg_uniprot_acc_id";
    @Field(IDG_UNIPROT_ACC)
    String idgUniprotAcc;
    public static final String IDG_CHR="idg_chr";
    @Field(IDG_CHR)
    String idgChr;

    public String getIdgName() {
        return idgName;
    }

    public void setIdgName(String idgName) {
        this.idgName = idgName;
    }

    public String getIdgIdl() {
        return idgIdl;
    }

    public void setIdgIdl(String idgIdl) {
        this.idgIdl = idgIdl;
    }

    public String getIdgFamily() {
        return idgFamily;
    }

    public void setIdgFamily(String idgFamily) {
        this.idgFamily = idgFamily;
    }

    public String getIdgSymbol() {
        return idgSymbol;
    }

    public void setIdgSymbol(String idgSymbol) {
        this.idgSymbol = idgSymbol;
    }

    public String getIdgUniprotAcc() {
        return idgUniprotAcc;
    }

    public void setIdgUniprotAcc(String idgUniprotAcc) {
        this.idgUniprotAcc = idgUniprotAcc;
    }

    public String getIdgChr() {
        return idgChr;
    }

    public void setIdgChr(String idgChr) {
        this.idgChr = idgChr;
    }

    public void setMgiAccession(String mgiAccession) {
        this.mgiAccession = mgiAccession;
    }
}
