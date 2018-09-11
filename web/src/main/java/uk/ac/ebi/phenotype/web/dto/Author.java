package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {
    private String fullName;
    private String firstName;
    private String lastName;
    private String initials;
    private String affiliation;
}
