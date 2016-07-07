package org.mousephenotype.cda.solr.web.dto;

import java.util.HashSet;
import java.util.Set;

import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.MarkerBean;

public class PhenotypeTableRowAnatomyPage extends DataTableRow{

	Set<MarkerBean> genes;
	BasicBean anatomyTerm;
	
	public Set<MarkerBean> getGenes() {
		return genes;
	}

	public void setGenes(Set<MarkerBean> genes) {
		this.genes = genes;
	}

	public void addGenes(MarkerBean gene){
		
		if (genes == null){
			genes = new HashSet<>();
		}
		
		genes.add(gene);
	}
	
	public BasicBean getAnatomyTerm() {
		return anatomyTerm;
	}

	public void setAnatomyTerm(BasicBean anatomyTerm) {
		this.anatomyTerm = anatomyTerm;
	}

	@Override
	public int compareTo(DataTableRow o) {
		return 0;
	}
	
	
}
