package org.mousephenotype.cda.solr.service;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service to get High Throughput Gene Targeting Designs
 */
@Service
public class HtgtService {

    private final HtgtDataAccess htgtDataAccess;

    @Inject
    public HtgtService(HtgtDataAccess htgtDataAccess){
        this.htgtDataAccess=htgtDataAccess;
    }
    public List<Design> getDesigns(int designId){
        DesignsResponse designsResponse = htgtDataAccess.getDesigns(designId);
        System.out.println("oligos size="+designsResponse.getData().getOligos());
        return designsResponse.getData().getOligos();

    }
}