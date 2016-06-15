package org.mousephenotype.cda.solr.web.dto;

public class EvidenceLink {
    	
    	String alt;
    	String url;
    	IconType iconType;
    	Boolean display;
    	
		public String getAlt() {
			return alt;
		}
		public void setAlt(String alt) {
			this.alt = alt;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public IconType getIconType() {
			return iconType;
		}
		public void setIconType(IconType icon) {
			this.iconType = icon;
		}
		public Boolean getDisplay() {
			return display;
		}
		public void setDisplay(Boolean display) {
			this.display = display;
		}	

    
    public enum IconType{
    	IMAGE, GRAPH, TABLE;
    }
}
