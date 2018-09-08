package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Journal {
    private String title;
    private String medlineAbbreviation;
    private String essn;
    private String issn;
    private String isoabbreviation;
    private String nlmid;
}
