/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mousephenotype.cda.repositories.solr.image;


import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
@SolrDocument(solrCoreName = "impc_images")
public class Image {
	
	public static final String DOWNLOAD_URL = "download_url";
        public static final String MA_ID = "ma_id";
	@Indexed(DOWNLOAD_URL)
        private String downloadUrl;
        @Indexed(MA_ID)
        private String maId;

    public String getMaId() {
        return maId;
    }

    public void setMaId(String maId) {
        this.maId = maId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
	
	@Id @Indexed
        private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
	
}