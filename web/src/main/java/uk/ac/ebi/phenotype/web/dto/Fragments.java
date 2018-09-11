package uk.ac.ebi.phenotype.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Fragments {

    private ArrayList<String> EUCOMM;
    private ArrayList<String> KOMP;
    private ArrayList<String> IKMC;
    private ArrayList<String> IMPC;

    @Field("International Knockout Mouse Consortium")
    private ArrayList<String> internationalKnockoutMouseConsortium;
    @Field("International Mouse Phenotyping Consortium")
    private ArrayList<String> internationalMousePhenotypingConsortium;
}
