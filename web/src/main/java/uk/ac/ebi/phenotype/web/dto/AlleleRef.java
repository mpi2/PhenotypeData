package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlleleRef {
    @Field("acc")
    private String alleleAccessionId;
    @Field("gacc")
    private String geneAccessionId;
    private String geneSymbol;
    private String project;
    private String alleleName;
    private String alleleSymbol;
}
