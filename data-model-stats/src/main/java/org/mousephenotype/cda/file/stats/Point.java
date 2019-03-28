package org.mousephenotype.cda.file.stats;

public class Point {
	
	private String sex;
	private String value;
	private String sampleType;
	private Float bodyWeight;
	
	public Point() {
		
	}
	
	public Point(String value, String sex, String sampleType, Float bodyWeight) {
		this.value=value;
		this.sex=sex;
		this.sampleType=sampleType;
		this.bodyWeight=bodyWeight;
	}
	
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getSampleType() {
		return sampleType;
	}
	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}
	public Float getBodyWeight() {
		return bodyWeight;
	}
	public void setBodyWeight(Float bodyWeight) {
		this.bodyWeight = bodyWeight;
	}

}
