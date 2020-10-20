package uk.ac.ebi.phenotype.web.dto;

public class BasicStats {
	int count;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Float getMean() {
		return mean;
	}
	public void setMean(Float mean) {
		this.mean = mean;
	}
	public Float getSd() {
		return sd;
	}
	public void setSd(Float sd) {
		this.sd = sd;
	}
	Float mean;
	Float sd;
	@Override
	public String toString() {
		return "BasicStats [count=" + count + ", mean=" + mean + ", sd=" + sd + "]";
	}
	
	
	
}
