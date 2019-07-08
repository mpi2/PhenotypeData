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
package org.mousephenotype.cda.db.dao;

/**
 *
 * Phenotype pipeline data access manager interface.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */


import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.pojo.Pipeline;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.mousephenotype.cda.web.WebStatus;

import java.util.List;

public interface PhenotypePipelineDAO extends HibernateDAO , WebStatus{

	/**
	 * Get all pipelines in the system
	 * @return all pipelines
	 */
	List<Pipeline> getAllPhenotypePipelines();

	/**
	 * Find a pipeline by its stable id.
	 * It will return the latest version of the pipeline
	 * @param stableId the pipeline stable id
	 * @return the pipeline
	 */
	Pipeline getPhenotypePipelineByStableId(String stableId);

	/**
	 * Find a procedure by its stable id
	 * It will return the latest version of the procedure
	 * @param stableId the procedure stable id
	 * @return the procedure
	 */
	Procedure getProcedureByStableId(String stableId);

	/**
	 * Find a parameter by stable id only
	 * It will return the latest version of the parameter
	 * @param stableId the pipeline stable id
	 * @return the parameter
	 */
	Parameter getParameterByStableId(String stableId);

	/**
	 * Find a procedure by stable key only
	 * It will return the latest version of the procedure
	 * @param stableKey the procedure stable key
	 * @return the procedure
	 */
	Procedure getProcedureByStableKey(String stableKey);

	void save(Object object);
	void update(Object object);
	Parameter getParameterById(Integer parameterId);
}