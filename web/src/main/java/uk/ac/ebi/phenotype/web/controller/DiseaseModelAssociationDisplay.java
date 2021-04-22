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
        List<String> phens = new ArrayList<>();

//        String urlPattern = "<a href='https://hpo.jax.org/app/browse/term/%s'>%s</a>";
//        if (matchedPhenotypes!= null) {
//            for (String matchedPhenotype : matchedPhenotypes) {
//                String[] fields = matchedPhenotype.split(" ", 2);
//                phens.add(String.format(urlPattern, fields[0], fields[1]));
//            }
//        }
//        return String.join(", ", phens);

        if (matchedPhenotypes!= null) {
            for (String matchedPhenotype : matchedPhenotypes) {
                String[] fields = matchedPhenotype.split(" ", 2);
                phens.add(fields[1]);
            }
        }

        String urlPattern = "<a href='"+getExternalUrl()+"'>%s</a>";
//        return String.format(urlPattern, StringUtils.abbreviate(String.join(", ", phens), 70));
        return StringUtils.abbreviate(String.join(", ", phens), 70);
    }

    @ToString.Include
    public Double getPhenodigmScore() {
        return (avgNorm + maxNorm) / 2;
    }

    public String getScoreIcon() {
        if (this.getPhenodigmScore() < 20) {
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
