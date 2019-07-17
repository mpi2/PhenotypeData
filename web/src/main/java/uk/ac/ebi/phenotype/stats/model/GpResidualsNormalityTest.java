package uk.ac.ebi.phenotype.stats.model;

public class GpResidualsNormalityTest {
//"gp1_residuals_normality_test": {
//    "p_val": 0.185793546039969,
//    "n": 46,
//    "unique_n": 46,
//    "sd": 2.04847715175459,
//    "test": "Shapiro"
//  },
	
	private Double pValue;
	public Double getpValue() {
		return pValue;
	}
	public void setpValue(Double pValue) {
		this.pValue = pValue;
	}
	public Double getN() {
		return n;
	}
	public void setN(Double n) {
		this.n = n;
	}
	public Double getUnique() {
		return unique;
	}
	public void setUnique(Double unique) {
		this.unique = unique;
	}
	public Double getSd() {
		return sd;
	}
	public void setSd(Double sd) {
		this.sd = sd;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	private Double n;
	private Double unique;
	private Double sd;
	private String test;
}
