package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SummaryStatistics {

@JsonProperty("Female_control")
private SummaryStatistic femaleControl;//: {
//  "count": 252,
//  "mean": 0.038452380952381,
//  "sd": 0.0545059822942607
//},
@JsonProperty("Male_control")
private SummaryStatistic maleControl;
//"Male_control": {
//  "count": 231,
//  "mean": 0.0572294372294372,
//  "sd": 0.0688674479747252
//},
@JsonProperty("Female_experimental")
private SummaryStatistic femaleExperimental;
//"Female_experimental": {
//  "count": 7,
//  "mean": 0.02,
//  "sd": 0.0529150262212918
//},
@JsonProperty("Male_experimental")
private SummaryStatistic maleExperimental;
//"Male_experimental": {
//  "count": 7,
//  "mean": 0.04,
//  "sd": 0.0378593889720018
//}
}
