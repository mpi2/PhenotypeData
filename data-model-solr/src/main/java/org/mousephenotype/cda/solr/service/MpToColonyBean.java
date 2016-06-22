package org.mousephenotype.cda.solr.service;

public class MpToColonyBean implements Comparable<MpToColonyBean>{
	public OntologyBean getMp() {
		return mp;
	}
	public void setMp(OntologyBean mp) {
		this.mp = mp;
	}
	public String getColonyId() {
		return colonyId;
	}
	public void setColonyId(String colonyId) {
		this.colonyId = colonyId;
	}
	private OntologyBean mp;
	private String colonyId;
	@Override
	public int compareTo(MpToColonyBean o) {
		return this.mp.getName().compareTo(o.mp.name);
		
	}
	@Override
	public String toString() {
		return "MpToColonyBean [mp=" + mp + ", colonyId=" + colonyId + "]";
	}
	
}