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


import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PhenotypePipelineDAO extends HibernateDAO , WebStatus{

	/**
	 * Get all pipelines in the system
	 * @return all pipelines
	 */
	List<Pipeline> getAllPhenotypePipelines();

	/**
	 * Find a pipeline by its id.
	 * It will return the latest version of the pipeline
	 * @param id the pipeline id
	 * @return the pipeline
	 */
	Pipeline getPhenotypePipelineById(Integer id);

	/**
	 * Find a pipeline by its stable id.
	 * It will return the latest version of the pipeline
	 * @param stableId the pipeline stable id
	 * @return the pipeline
	 */
	Pipeline getPhenotypePipelineByStableId(String stableId);

	/**
	 * Find a pipeline by its stable id.
	 * It will return the latest version of the pipeline
	 * @param stableId the pipeline stable id
	 * @param majorVersion the major version
	 * @param minorVersion the minor version
	 * @return the pipeline
	 */
	Pipeline getPhenotypePipelineByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);

	/**
	 * Find a procedure by its stable id
	 * It will return the latest version of the procedure
	 * @param stableId the procedure stable id
	 * @return the procedure
	 */
	Procedure getProcedureByStableId(String stableId);

	/**
	 * Find a procedure by its stable id and versions
	 * It will return the latest version of the procedure
	 * @param stableId the pipeline stable id
	 * @param majorVersion the major version
	 * @param minorVersion the minor version
	 * @return the procedure
	 */
	Procedure getProcedureByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);

	/**
	 * Find multiple procedures matching the string passed
	 * It will return a list of procedure matching the string passed
	 * @param pattern the procedure stable id pattern
	 * @return a list of procedures matching the string passed as a parameter
	 */
	List<Procedure> getProcedureByMatchingStableId(String pattern);

	/**
	 * Find a parameter by its stable id and versions
	 * It will return the latest version of the parameter
	 * @param stableId the pipeline stable id
	 * @param majorVersion the major version
	 * @param minorVersion the minor version
	 * @return the parameter
	 */
	Parameter getParameterByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);

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



	List<Parameter> getProcedureMetaDataParametersByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);

	Set<Procedure> getProceduresByOntologyTerm(OntologyTerm term);

	void save(Object object);
	void update(Object object);
	void savePipeline(Pipeline pipeline);
	void saveProcedure(Procedure procedure);
	void saveParameter(Parameter parameter);
	void saveParameterOption(ParameterOption parameterOption);
	void saveParameterIncrement(ParameterIncrement parameterIncrement);
	void saveParameterOntologyAnnotation(ParameterOntologyAnnotation parameterOntologyAnnotation);
	/**
	 * Delete phenotype pipelines from a specific datasource;
	 * Status: experimental
	 * @param datasource
	 */
	void deleteAllPipelinesByDatasource(Datasource datasource);

	List<String> getParameterStableIdsByPhenotypeTerm(String mpTermId);
	Parameter getParameterById(Integer parameterId);
	Set<Parameter> getAllCategoricalParametersForProcessing() throws SQLException;
	Set<Parameter> getAllUnidimensionalParametersForProcessing() throws SQLException;
	List<String> getCategoriesByParameterId(Integer id) throws SQLException;

	String getCategoryDescription (int parameterId, String category) throws SQLException;

	/**
	 * @author tudose
	 * @return <ProcedureStableID, [Set of all MP ids that can be associated]>
	 * @throws SQLException
	 */
	Map<String, Set<String>> getMpsForParameters() throws SQLException;
}
