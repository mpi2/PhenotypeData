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
 * (Not sure if "factory" technically applies here. The getSetter does not
 * generate new object but just returns one of the autowired objects.)
 * 
 */
@Service
public class SearchUrlServiceFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private SearchUrlServiceAllele2 searchUrlServiceAllele2;

    @Autowired
    private SearchUrlServiceAnatomy searchUrlServiceAnatomy;

    @Autowired
    private SearchUrlServiceDisease searchUrlServiceDisease;

    @Autowired
    private SearchUrlServiceGene searchUrlServiceGene;

    @Autowired
    private SearchUrlServiceImpcImage searchUrlServiceImpcImage;

    @Autowired
    private SearchUrlServiceMp searchUrlServiceMp;

    @Autowired
    private SearchUrlServicePhenodigm2Disease searchUrlServicePhenodigm2Disease;

    public SearchUrlService getService(String type) {
        switch (type) {
            case "gene":
                return searchUrlServiceGene;
            case "mp":
                return searchUrlServiceMp;
            case "disease1":
                // Support for disease queries through old disease core
                return searchUrlServiceDisease;
            case "disease":
                // Supports for disease queries through the phenodigm core                
                return searchUrlServicePhenodigm2Disease;
            case "anatomy":
                return searchUrlServiceAnatomy;
            case "impc_images":
                return searchUrlServiceImpcImage;
            case "allele2":
                return searchUrlServiceAllele2;
            default:
                LOGGER.info("requested service of unknown type: " + type);
                return null;
        }
    }
}
