/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousephenotype.cda.solr.repositories.image;


import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
@SolrDocument(solrCoreName = "impc_images")
public class Image {
	
	public static final String DOWNLOAD_URL = "download_url";
    public static final String MA_ID = "ma_id";
    public static final String MARKER_ACCESSION_ID="gene_accession_id";
    @Indexed(DOWNLOAD_URL)
    private String downloadUrl;
    @Indexed(MA_ID)
    private List<String> maId;
    @Indexed(MARKER_ACCESSION_ID)
    private String markerAccession;
	@Id @Indexed
    private Integer id;
	
    public String getMarkerAccession() {
        return markerAccession;
    }

    public void setMarkerAccession(String markerAccession) {
        this.markerAccession = markerAccession;
    }
        

    public List<String> getMaId() {
        return maId;
    }

    public void setMaId(List<String> maId) {
        this.maId = maId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
	

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Image{" + "downloadUrl=" + downloadUrl + ", maId=" + maId + ", markerAccession=" + markerAccession + ", id=" + id + '}';
    }
	
}