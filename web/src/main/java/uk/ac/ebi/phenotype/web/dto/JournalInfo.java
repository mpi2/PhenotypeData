package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JournalInfo {
    private String issue;
    private String volume;
    private Integer journalIssueId;
    private String dateOfPublication;
    private Integer monthOfPublication;
    private Integer yearOfPublication;
    private String printPublicationDate;
    private Journal journal;
}
