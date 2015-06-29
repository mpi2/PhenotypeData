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
package org.mousephenotype.cda.dao;

import org.mousephenotype.cda.enumerations.SignificantType;
import org.mousephenotype.cda.pojo.BiologicalModel;
import org.mousephenotype.cda.pojo.CategoricalResult;
import org.mousephenotype.cda.pojo.Parameter;
import org.mousephenotype.cda.pojo.UnidimensionalResult;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


/**
 *
 * The statistical result data access object is a wrapper to get access to
 * the results stored in the databased once the statistical pipeline has been
 * run on the categorical, unidimensional and derived parameters.
 *
 * @author Jonathan Warren <jwarren@ebi.ac.uk>
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2014
 */

public interface StatisticalResultDAO {

	/**
	 * Given a procedure parameter and a biological model, returns a list of
	 * unidimensional results for this parameter and model.
	 * @param parameter
	 * @param controlBiologicalModel
	 * @param biologicalModel
	 * @return
	 */
	public List<UnidimensionalResult> getUnidimensionalResultByParameterAndBiologicalModel(Parameter parameter, BiologicalModel controlBiologicalModel, BiologicalModel biologicalModel);

	public List<UnidimensionalResult> getUnidimensionalResultByParameterIdAndBiologicalModelIds(Integer parameter, Integer controlBiologicalId, Integer biologicalId);


	public UnidimensionalResult getUnidimensionalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException;

    public CategoricalResult getCategoricalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException;

    public HashMap<SignificantType, Integer> getSexualDimorphismSummary() throws SQLException;

}
