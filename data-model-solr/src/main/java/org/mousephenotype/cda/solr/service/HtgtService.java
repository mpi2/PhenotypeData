package org.mousephenotype.cda.solr.service;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

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
    public String getDesigns(String designId){
        return htgtDataAccess.getDesigns(designId);
    }
}
