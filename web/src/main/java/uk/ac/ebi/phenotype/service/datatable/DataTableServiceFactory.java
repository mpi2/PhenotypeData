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
package uk.ac.ebi.phenotype.service.datatable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Factory class for services html and datatable strings from solr results
 *
 */
@Service
public class DataTableServiceFactory {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private DataTableServiceGene dtsGene;

    @Autowired
    private DataTableServiceAllele2 dtsAllele;

    @Autowired
    private DataTableServiceMp dtsMp;

    @Autowired
    private DataTableServiceDisease dtsDisease;
    
    public DataTableService getService(String type) {
        switch (type) {
            case "gene":
                return dtsGene;
            case "allele2":
                return dtsAllele;
            case "disease":
                return dtsDisease;            
            case "mp":
                return dtsMp;                
            default:
                LOGGER.info("requested service of unknown type: " + type);
                return null;
        }
    }

}
