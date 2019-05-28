package uk.ac.ebi.phenotype.web.controller;

import org.mousephenotype.cda.solr.service.dto.MpDTO;

public class SearchResultMpDTO extends MpDTO {

    private Integer geneCount = 0;

    public Integer getGeneCount() {
        return geneCount;
    }

    public void setGeneCount(Integer geneCount) {
        this.geneCount = geneCount;
    }


}
