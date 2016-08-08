package org.mousephenotype.cda.solr.web.dto;

/**
 * hold information for button links from the order section within the Gene targeting details column and the product ordering columns
 * @author jwarren
 *
 */
public class LinkDetails {
	private String label;
	private String link;
	private String contact;
	private String genbankLink;
	
	
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	
	public String getGenbankLink() {
		return genbankLink;
	}
	public void setGenbankLink(String genbankLink) {
		this.genbankLink = genbankLink;
	}
	
	@Override
	public String toString() {
		return "GeneTargetDetail [label=" + label + ", link=" + link + "]";
	}
	
	
}
