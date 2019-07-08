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
 * Phenotype pipeline data access manager implementation.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.pojo.Pipeline;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Transactional
public class PhenotypePipelineDAOImpl extends HibernateDAOImpl implements PhenotypePipelineDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates a new Hibernate pipeline data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public PhenotypePipelineDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public PhenotypePipelineDAOImpl() {

	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Pipeline> getAllPhenotypePipelines() {
		List<Pipeline> pipelines = getCurrentSession()
				.createQuery("from Pipeline")
				.list();
		return pipelines;
	}

	@Transactional(readOnly = true)
	public Pipeline getPhenotypePipelineByStableId(String stableId) {
		return (Pipeline) getCurrentSession()
				.createQuery("from Pipeline as p where p.stableId = :stableId")
				.setParameter("stableId", stableId)
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Procedure getProcedureByStableId(String stableId) {
		return (Procedure) getCurrentSession()
				.createQuery("from Procedure as p where p.stableId = :stableId")
				.setParameter("stableId", stableId)
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Procedure getProcedureByStableKey(String stableKey) {
		return (Procedure) getCurrentSession()
				.createQuery("from Procedure as p where p.stableKey = :stableKey")
				.setParameter("stableKey", stableKey)
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	public Parameter getParameterByStableId(String stableId) {
		return (Parameter) getCurrentSession()
				.createQuery("from Parameter as p where p.stableId = :stableId")
				.setParameter("stableId", stableId)
				.uniqueResult();
	}

	@Transactional(readOnly = false)
	public void save(Object object) {
		getCurrentSession().save(object);
	}

	@Transactional(readOnly = false)
	public void update(Object object) {
		getCurrentSession()
				.saveOrUpdate(object);
	}

	/**
	 * Helper method to fetch the actual parameter pojo when provided a
	 * database id.
	 */
	@Transactional(readOnly = true)
	public Parameter getParameterById(Integer parameterId) {
		return (Parameter) getCurrentSession()
				.createQuery("SELECT p FROM Parameter p WHERE p.id=:parameterId")
				.setParameter("parameterId", parameterId)
				.uniqueResult();
	}

	public boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	@Override
	public long getWebStatus() {
		int rows = 0;
		String statusQuery="SELECT count(*) FROM phenotype_procedure";

		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(statusQuery)) {

			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				rows = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}

		 return rows;
	}


	@Override
	public String getServiceName() {
		return "PhenotypePipelineDAO";
	}
}