package uk.ac.ebi.phenotype.web.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RawSummaryStatistics {
	
	@JsonProperty("female_control")
	BasicStats femaleControl;
	public BasicStats getFemaleControl() {
		return femaleControl;
	}
	public void setFemaleControl(BasicStats femaleControl) {
		this.femaleControl = femaleControl;
	}
	public BasicStats getMaleControl() {
		return maleControl;
	}
	public void setMaleControl(BasicStats maleControl) {
		this.maleControl = maleControl;
	}
	public BasicStats getFemaleExperimental() {
		return femaleExperimental;
	}
	public void setFemaleExperimental(BasicStats femaleExperimental) {
		this.femaleExperimental = femaleExperimental;
	}
	public BasicStats getMaleExpreimental() {
		return maleExpreimental;
	}
	public void setMaleExpreimental(BasicStats maleExpreimental) {
		this.maleExpreimental = maleExpreimental;
	}
	@JsonProperty("male_control")
	BasicStats maleControl;
	@JsonProperty("female_experimental")
	BasicStats femaleExperimental;
	@JsonProperty("male_experimental")
	BasicStats maleExpreimental;
	@Override
	public String toString() {
		return "RawSummaryStatistics [femaleControl=" + femaleControl + ", maleControl=" + maleControl
				+ ", femaleExperimental=" + femaleExperimental + ", maleExpreimental=" + maleExpreimental + "]";
	}
	
	
	
//	"female_control": {
//    "count": 445,
//    "mean": 0.0723820224719101,
//    "sd": 0.186794167300111
//},
//"male_control": {
//    "count": 446,
//    "mean": 0.11432735426009,
//    "sd": 0.310302310753645
//},
//"female_experimental": {
//    "count": 7,
//    "mean": 0.02,
//    "sd": 0.0529150262212918
//},
//"male_experimental": {
//    "count": 7,
//    "mean": 0.04,
//    "sd": 0.0378593889720018
//}
}
