package org.mousephenotype.cda.solr.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GeneTopLevelMpTerms {

    private List<String> significantTopLevelMpTerms;
    private List<String> notSignificantTopLevelMpTerms;

}
