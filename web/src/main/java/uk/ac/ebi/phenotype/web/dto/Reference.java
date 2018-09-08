package uk.ac.ebi.phenotype.web.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Reference {
	private String id;
    private ArrayList<String> alleleSymbols;
    private ArrayList<String> geneAccessionIds;
    private String journal;
    private String title;
    private String author;
    private String pmid;
    private String abstractTxt;
    private ArrayList<String> grantAgencies;
    private Date dateOfPublication;
    private ArrayList<String> paperUrls;
    private ArrayList<String> citationTitles;
    private ArrayList<String> citationUrls;
    private ArrayList<String> citationDates;
}
