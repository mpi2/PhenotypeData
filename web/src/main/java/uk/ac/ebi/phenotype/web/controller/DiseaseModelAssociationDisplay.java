package uk.ac.ebi.phenotype.web.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.phenodigm2.Disease;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class DiseaseModelAssociationDisplay implements Comparable<DiseaseModelAssociationDisplay> {

    private String diseaseId;
    @EqualsAndHashCode.Exclude private String diseaseTerm;
    @ToString.Exclude @EqualsAndHashCode.Exclude private List<String> matchedPhenotypes;
    @ToString.Exclude @EqualsAndHashCode.Exclude private Double avgNorm;
    @ToString.Exclude @EqualsAndHashCode.Exclude private Double maxNorm;

    public String getFormattedMatchingPhenotypes() {
        List<String> phenotypes = new ArrayList<>();

        if (matchedPhenotypes!= null) {
            for (String matchedPhenotype : matchedPhenotypes) {
                // Each phenotype result comes as a row like:
                // "HP:0005585 Spotty hyperpigmentation"
                // the field before the first space is the ID
                String[] fields = matchedPhenotype.split(" ", 2);
                phenotypes.add(fields[1]);
            }
        }

        return StringUtils.abbreviate(String.join(", ", phenotypes), 100);
    }

    @ToString.Include
    public Double getPhenodigmScore() {
        return (avgNorm + maxNorm) / 2;
    }

    public String getScoreIcon() {
        if (this.getPhenodigmScore() < 0.1 ) {
            return "zero-bars";
        } else if (this.getPhenodigmScore() < 20) {
            return "one-bar";
        } else if (this.getPhenodigmScore() < 40) {
            return "two-bars";
        } else if (this.getPhenodigmScore() < 60) {
            return "three-bars";
        } else if (this.getPhenodigmScore() < 80) {
            return "four-bars";
        } else {
            return "five-bars";
        }
    }

    public String getExternalUrl() {
        return new Disease(diseaseId).getExternalUrl();
    }

    @Override
    public int compareTo(DiseaseModelAssociationDisplay o) {
        if (this.getPhenodigmScore() > o.getPhenodigmScore())
            return -1;
        else if (this.getPhenodigmScore() <= o.getPhenodigmScore())
            return 1;
        return this.diseaseTerm.compareTo(o.diseaseTerm);
    }
}
