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

import java.util.Map;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * An abstraction of a service that format data from solr into data tables. 
 * 
 */
public abstract class DataTableService {
        
    // a single logger definition applies to all the classes that extend the abstract
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());
   
    // This configuration object is required in at least one extension classes
    // but is this really relevant here? Think about removing?
    @Resource(name = "globalConfiguration")
	protected Map<String, String> config;
    
    public abstract String toDataTable(JSONObject json, SearchSettings settings);
    
}
