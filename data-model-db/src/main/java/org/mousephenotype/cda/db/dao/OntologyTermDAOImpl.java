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
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		return getCurrentSession()
				.createQuery("from OntologyTerm")
				.list();
	}


	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByName(String name) {
		return (OntologyTerm) getCurrentSession()
				.createQuery("from OntologyTerm as o where o.name = :name")
		        .setParameter("name", name)
		        .uniqueResult();
	}


	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByAccession(String accession) {
		return (OntologyTerm) getCurrentSession()
				.createQuery("from OntologyTerm as ot where ot.id.accession = :accession")
		        .setParameter("accession", accession)
		        .uniqueResult();
	}


	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermByAccessionAndDatabaseId(String accession, int databaseId) {
		return (OntologyTerm) getCurrentSession()
				.createQuery("from OntologyTerm as ot where ot.id.accession = :accession and ot.id.databaseId = :databaseId")
				.setParameter("accession", accession)
				.setParameter("databaseId", databaseId)
				.uniqueResult();
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
	public long getWebStatus() {
		int rows = 0;
		String statusQuery="SELECT count(*) FROM ontology_term";

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
		return "Ontology Dao";
	}
}