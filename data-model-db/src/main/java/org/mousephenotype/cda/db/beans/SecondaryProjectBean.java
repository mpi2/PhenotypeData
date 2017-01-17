package org.mousephenotype.cda.db.beans;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class SecondaryProjectBean {
	private String accession;
	private String groupLabel;

	public SecondaryProjectBean(String acc, String groupLabel) {
		this.accession = acc;
		this.groupLabel = groupLabel;
	}

	public SecondaryProjectBean(String accession) {
		this.accession = accession;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getGroupLabel() {
		return groupLabel;
	}

	public void setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
	}

	public static Set<String> getAccessionsFromBeans(Set<SecondaryProjectBean> projectBeans) {
		TreeSet<String> accessions = new TreeSet<>();
		for (SecondaryProjectBean bean : projectBeans) {
			accessions.add(bean.getAccession());
			//System.out.println(bean);
		}
		return accessions;
	}
	
	public static HashMap<String, String> getAccessionsToLabelMapFromBeans(Set<SecondaryProjectBean> projectBeans) {
		HashMap<String, String> map = new HashMap<>();
		for (SecondaryProjectBean bean : projectBeans) {
			map.put(bean.getAccession(), bean.getGroupLabel());	
		}
		return map;
	}

	@Override
	public String toString() {
		return "SecondaryProjectBean [accession=" + accession + ", groupLabel=" + groupLabel + "]";
	}

	
}
