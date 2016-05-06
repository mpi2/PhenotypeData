package org.mousephenotype.cda.solr.web.dto;

/**
 * Class extends normal histopath row so we add the extra features only to the summary object and don't confuse methods of basic table
 * @author jwarren
 *
 */
public class HistopathSumPageTableRow extends HistopathPageTableRow {

	
	private Integer significantCount=new Integer(0);
	private Integer nonSignificantCount=new Integer(0);
	
	private boolean hasImages=false;
	
	//System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue()+"ontologyTermValue="+obs.getTermValue());
	
	public boolean isHasImages() {
		return hasImages;
	}
	public void setHasImages(boolean hasImages) {
		this.hasImages = hasImages;
	}
	public Integer getSignificantCount() {
		return significantCount;
	}
	public void setSignificantCount(Integer significanceCount) {
		this.significantCount = significanceCount;
	}
	public Integer getNonSignificantCount() {
		return nonSignificantCount;
	}
	public void setNonSignificantCount(Integer nonSignificantCount) {
		this.nonSignificantCount = nonSignificantCount;
	}
	
}
