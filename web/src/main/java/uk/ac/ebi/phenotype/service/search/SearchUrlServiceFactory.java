/** *****************************************************************************
 * Copyright 2017 QMUL - Queen Mary University of London
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ****************************************************************************** */
package uk.ac.ebi.phenotype.service.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Factory class for services generating solr search urls.
 *
 * The main purpose is to obtain an instance of a SearchUrlService.
 *
 */
@Service
public class SearchUrlServiceFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());
    
    @Autowired
    private SearchUrlServiceAllele2 searchConfigAllele2;

    @Autowired
    private SearchUrlServiceAnatomy searchConfigAnatomy;

    @Autowired
    private SearchUrlServiceDisease searchConfigDisease;

    @Autowired
    private SearchUrlServiceGene searchConfigGene;

    @Autowired
    private SearchUrlServiceMp searchConfigImpcImage;

    @Autowired
    private SearchUrlServiceMp searchConfigMp;

    @Autowired
    private SearchUrlServicePhenodigm2Disease searchConfigPhenodigm2Disease;

    public SearchUrlService getService(String type) {
        switch (type) {
            case "gene":
                return searchConfigGene;
            case "mp":
                return searchConfigMp;
            case "disease":
                return searchConfigDisease;
            case "phenodigm2disease":
                return searchConfigPhenodigm2Disease;
            case "anatomy":
                return searchConfigAnatomy;
            case "impc_images":
                return searchConfigImpcImage;
            case "allele2":
                return searchConfigAllele2;
            default:
                LOGGER.info("requested service of unknown type: "+type);
                return null;
        }
    }
}
