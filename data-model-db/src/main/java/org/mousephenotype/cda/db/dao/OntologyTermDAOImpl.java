/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
 * Ontology term and controlled vocabulary data access manager implementation.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.beans.AggregateCountXYBean;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Repository
@Transactional
public class OntologyTermDAOImpl extends HibernateDAOImpl implements OntologyTermDAO{

	public OntologyTermDAOImpl() {
	}


	/**
	 * Creates a new Hibernate ontology term data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public OntologyTermDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<OntologyTerm> getAllOntologyTerms() {
		return getCurrentSession().createQuery("from OntologyTerm")
		                          .list();
	}


	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByName(String name) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as o where o.name = :name")
		                                         .setString("name", name)
		                                         .uniqueResult();
	}


	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByAccession(String accession) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as ot where ot.id.accession = :accession")
		                                         .setString("accession", accession)
		                                         .uniqueResult();
	}


	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByAccessionAndDatabaseId(String accession, int databaseId) {
		return (OntologyTerm) getCurrentSession().createQuery("from OntologyTerm as ot where ot.id.accession = :accession and ot.id.databaseId = :databaseId")
		                                         .setString("accession", accession)
		                                         .setInteger("databaseId", databaseId)
		                                         .uniqueResult();
	}



	@Transactional(readOnly = false)
	public int deleteAllTerms(String shortName) {

		Session session = getSession();

		// get the database id
		Datasource d = (Datasource) session.createQuery("from Datasource as d where d.shortName = :shortName")
		                                   .setString("shortName", shortName)
		                                   .uniqueResult();

		// execute the delete query
		String hqlDelete = "delete OntologyTerm as ot where ot.id.databaseId = :dbId";
		int deletedEntities = session.createQuery(hqlDelete)
		                             .setInteger("dbId", d.getId())
		                             .executeUpdate();
		return deletedEntities;
	}


	@Transactional(readOnly = false)
	public int batchInsertion(Collection<OntologyTerm> ontologyTerms) {
		int c = 0;

		Session session = getSession();

		for (OntologyTerm term : ontologyTerms) {

			session.save(term);

			c++;
		}

		return c;
	}

	@Override
	public OntologyTerm getOntologyTermByNameAndDatabaseId(String name, int databaseId) {
		return (OntologyTerm)this.getCurrentSession().createQuery("from OntologyTerm as o where o.name= :name and o.id.databaseId = :databaseId")
			.setString("name", name)
			.setInteger("databaseId", databaseId)
			.uniqueResult();
	}


	@Override
	public long getWebStatus() throws Exception {
		int rows = 0;
		String statusQuery="SELECT count(*) FROM ontodb_komp2.ma_node2term";

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<AggregateCountXYBean> results = new ArrayList<AggregateCountXYBean>();

		try (Connection connection = getConnection()) {

			statement = connection.prepareStatement(statusQuery);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {


			rows=resultSet.getInt(1);

			}
			statement.close();

		}catch (SQLException e) {
			e.printStackTrace();

		}



		 return rows;
	}


	@Override
	public String getServiceName() {
		return "Ontology Dao";
	}

}
