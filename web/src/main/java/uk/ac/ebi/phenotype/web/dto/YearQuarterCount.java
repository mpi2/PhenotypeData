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
public class YearQuarterCount {
    @Field("_id")
    private String year;
    private int q1;
    private int q2;
    private int q3;
    private int q4;
}
