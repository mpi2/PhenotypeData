package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CitingPaper {
    private String title;
    private String url;
    private Date publicationDate;

}
