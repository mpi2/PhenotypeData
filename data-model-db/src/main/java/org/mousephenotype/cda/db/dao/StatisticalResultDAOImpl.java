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

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.mousephenotype.cda.db.pojo.CategoricalResult;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.pojo.UnidimensionalResult;
import org.mousephenotype.cda.enumerations.SignificantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;


/**
 *
 * The statistical result data access object is a wrapper to get access to
 * the results stored in the database once the statistical pipeline has been
 * run on the categorical, unidimensional and derived parameters.
 *
 * @author Jonathan Warren <jwarren@ebi.ac.uk>
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2014
 */
@Repository
@Transactional
public class StatisticalResultDAOImpl extends HibernateDAOImpl implements StatisticalResultDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public StatisticalResultDAOImpl() {

	}

	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public StatisticalResultDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public List<UnidimensionalResult>
	getUnidimensionalResultByParameterAndBiologicalModel(Parameter parameter,
			BiologicalModel controlBiologicalModel,
			BiologicalModel mutantBiologicalModel) {
		return (List<UnidimensionalResult>) getCurrentSession().createQuery("from UnidimensionalResult u  WHERE parameter=? and control_id=? and experimental_id=?")
				.setInteger(0, parameter.getId())
				.setInteger(1, controlBiologicalModel.getId())
				.setInteger(2, mutantBiologicalModel.getId())
				.list();

		//select p from AnalysisPolicy p where exists elements(p.nodeIds)
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<UnidimensionalResult>
	getUnidimensionalResultByParameterIdAndBiologicalModelIds(
			Integer parameterId, Integer controlBiologicalId,
			Integer biologicalId) {
		return (List<UnidimensionalResult>) getCurrentSession().createQuery("from UnidimensionalResult u  WHERE parameter=? and control_id=? and experimental_id=?")
				.setInteger(0, parameterId)
				.setInteger(1, controlBiologicalId)
				.setInteger(2, biologicalId)
				.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public UnidimensionalResult getUnidimensionalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException {
		//get the id we need from the join table
		int resultId=this.getUnidimensionalResultIdFromStatsResultPhenotypeCallSummary(phenotypeCallSummaryId);
		//use the join table id to get the actual result
		return (UnidimensionalResult) getCurrentSession().get(UnidimensionalResult.class,  resultId);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public CategoricalResult getCategoricalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException {
		//get the id we need from the join table
		int resultId=this.getCategoricalResultIdFromStatsResultPhenotypeCallSummary(phenotypeCallSummaryId);
		//use the join table id to get the actual result
		return (CategoricalResult) getCurrentSession().get(CategoricalResult.class,  resultId);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private int getUnidimensionalResultIdFromStatsResultPhenotypeCallSummary(int id) throws SQLException {

		int result=-1;
		String query = "SELECT unidimensional_result_id FROM stat_result_phenotype_call_summary where phenotype_call_summary_id= '"
				+ id + "'";

		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result=resultSet.getInt(1);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	private int getCategoricalResultIdFromStatsResultPhenotypeCallSummary(int id)throws SQLException {
		//get the id we need from the join table
		int result=-1;
		String query = "SELECT categorical_result_id FROM stat_result_phenotype_call_summary where phenotype_call_summary_id= '"
				+ id + "'";

		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result=resultSet.getInt(1);
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	/**
	 * IMPC only
	 */
	public HashMap<SignificantType, Integer> getSexualDimorphismSummary()
	throws SQLException {

		HashMap<SignificantType, Integer> res = new HashMap<>();
		String query = "SELECT COUNT(*), classification_tag FROM stat_result_phenotype_call_summary srpc "+
			"INNER JOIN phenotype_call_summary pcs on pcs.id = srpc.phenotype_call_summary_id "+
			"INNER JOIN stats_unidimensional_results sur ON srpc.unidimensional_result_id=sur.id "+
			"WHERE pcs.p_value < 0.0001 AND pcs.external_db_id=22 " + // IMPC only
			"GROUP BY classification_tag;";

		try (Connection connection = getConnection()) {
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				res.put(SignificantType.getValue(resultSet.getString("classification_tag")), resultSet.getInt("COUNT(*)"));
			}
		}
		return res;
	}

}
