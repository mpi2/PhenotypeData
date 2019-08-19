package uk.ac.ebi.phenotype.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mousephenotype.cda.solr.service.embryoviewer.EmbryoViewerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EmbryoViewerApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String CACHE_NAME = "embryodata";
    private final EmbryoViewerService embryoViewerService;
    private final ObjectMapper mapper = new ObjectMapper();

    public EmbryoViewerApiController(EmbryoViewerService embryoViewerService) {
        this.embryoViewerService = embryoViewerService;
    }

    // Refresh cache every day
    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(cacheNames = CACHE_NAME)
    public void resetEmbryoList() {
        logger.info("Refreshing embryo cache");
    }

    @RequestMapping(value = "/api/embryostatus", method = RequestMethod.GET, produces = "application/json")
    @Cacheable(CACHE_NAME)
    public String getGeneTable() throws IOException {
        return mapper.writeValueAsString(embryoViewerService.getGenesEmbryoStatus());
    }

}
