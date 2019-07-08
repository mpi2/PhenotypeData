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
 * Parent data access manager implementation.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.SQLException;

/*
* Implementation of the HibernateDAO interface
*/

@Repository
@Transactional
public class HibernateDAOImpl implements HibernateDAO {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	/**
	 * The session factory used to query the database
	 */
	@Autowired
	@Qualifier("sessionFactoryHibernate")
	protected SessionFactory sessionFactory;

	/**
	 * Method to get a jdbc connection.
	 *
	 * @return a jdbc connection.
	 */
	public Connection getConnection() {
		Session session = getSession();

		SessionImplementor sessionImplementor = (SessionImplementor) session;
		Connection connection = null;

		try {
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	/**
	 * Method to get a session from the session factory.
     *
	 * @return a hibernate session.
	 */
	public Session getSession() {
		Session sess = null;
		try {
			sess = sessionFactory.getCurrentSession();
			if ( ! sess.isOpen()) {
				sess = sessionFactory.openSession();
			}
		} catch (org.hibernate.HibernateException he) {
			sess = sessionFactory.openSession();
		}
		return sess;

	}

	/**
	 * @return Returns the sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Returns the session associated with the ongoing reward transaction.
	 * @return the transactional session
	 */
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	protected void finalize() {

		getCurrentSession().flush();
		getCurrentSession().clear();
		getCurrentSession().close();

	}
}