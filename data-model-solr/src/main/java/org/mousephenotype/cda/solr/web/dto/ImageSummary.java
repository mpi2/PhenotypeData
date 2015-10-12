package org.mousephenotype.cda.solr.web.dto;

public class ImageSummary {
	
	String procedureName;
	String procedureId;
	Long numberOfImages;
	String thumbnailUrl;
	
	public String getProcedureName() {
		return procedureName;
	}
	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}
	public String getProcedureId() {
		return procedureId;
	}
	public void setProcedureId(String procedureid) {
		this.procedureId = procedureid;
	}
	public Long getNumberOfImages() {
		return numberOfImages;
	}
	public void setNumberOfImages(Long numberOfImages) {
		this.numberOfImages = numberOfImages;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnaiUrl) {
		this.thumbnailUrl = thumbnaiUrl;
	}
	
}
