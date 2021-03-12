package org.mousephenotype.cda.solr.service.dto;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

@Data
public class EssentialGeneDTO {
    public static final String MARKER_SYMBOL="mg_symbol";
    public static final String MGI_ACCESSION="mg_mgi_gene_acc_id";
    public static final String HUMAN_GENE_SYMBOL="hg_symbol";
    public static final String IDG_FAMILY="idg_family";

    @Field(MARKER_SYMBOL)
    private String markerSymbol;

    @Field(HUMAN_GENE_SYMBOL)
    private String humanGeneSymbol;

    @Field(MGI_ACCESSION)
    String mgiAccession;

    @Field(IDG_FAMILY)
    String idgFamily;

}
