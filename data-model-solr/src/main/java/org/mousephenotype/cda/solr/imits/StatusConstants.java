/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
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
 *******************************************************************************/
package org.mousephenotype.cda.solr.imits;



public class StatusConstants {

//	public static final String IMITS_ES_CELL_STATUS_PRODUCTION_NONE = "No ES Cell Production";
//	public static final String IMITS_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS = "ES Cell Production in Progress";
//	public static final String IMITS_ES_CELL_STATUS_TARGETING_CONFIRMED = "ES Cell Targeting Confirmed";
//
//	public static final String IMITS_MOUSE_STATUS_CHIMERA_OBTAINED = "Chimeras obtained";
//	public static final String IMITS_MOUSE_STATUS_MICRO_INJECTION_IN_PROGRESS = "Micro-injection in progress";
//	public static final String IMITS_MOUSE_STATUS_CRE_EXCISION_STARTED = "Cre Excision Started";
//	public static final String IMITS_MOUSE_STATUS_CRE_EXCISION_COMPLETE = "Cre Excision Complete";
//	public static final String IMITS_MOUSE_STATUS_REDERIVATION_STARTED =  "Rederivation Started";
//	public static final String IMITS_MOUSE_STATUS_REDERIVATION_COMPLETE = "Rederivation Complete";
//	public static final String IMITS_MOUSE_STATUS_GENOTYPE_CONFIRMED = "Genotype confirmed";

	public static final String IMPC_ES_CELL_STATUS_PRODUCTION_NONE = "Not Assigned for ES Cell Production";
	public static final String IMPC_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS = "Assigned for ES Cell Production";
	public static final String IMPC_ES_CELL_STATUS_PRODUCTION_DONE = "ES Cells Produced";
	
	public static final String WEB_ES_CELL_STATUS_PRODUCTION_NONE = "No ES Cell produced";
	public static final String WEB_ES_CELL_STATUS_PRODUCTION_IN_PROGRESS = "ES cells production in progress";
	public static final String WEB_ES_CELL_STATUS_PRODUCTION_DONE = "ES Cells produced";

	public static final String IMPC_MOUSE_STATUS_PRODUCTION_IN_PROGRESS = "Assigned for Mouse Production and Phenotyping";
	public static final String IMPC_MOUSE_STATUS_PRODUCTION_DONE = "Mice Produced";
	
	public static final String WEB_MOUSE_STATUS_PRODUCTION_IN_PROGRESS = "Mice production in progress";
	public static final String WEB_MOUSE_STATUS_PRODUCTION_DONE = "Mice Produced";
	
	public static final String PHENOTYPE_ATTEMPT_REGISTERED = "Phenotype attempt registered";
	public static final String PHENOTYPING_STARTED = "Phenotyping started";
	public static final String PHENOTYPING_COMPLETE = "Phenotyping complete";

	public static final String PHENOTYPING_DATA_AVAILABLE = "Phenotyping data available";


}
