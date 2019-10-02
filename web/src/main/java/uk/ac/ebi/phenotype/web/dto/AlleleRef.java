package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlleleRef {
    private String alleleAccessionId;
    private String geneAccessionId;
    private String geneSymbol;
    private String project;
    private String alleleName;
    private String alleleSymbol;
}
