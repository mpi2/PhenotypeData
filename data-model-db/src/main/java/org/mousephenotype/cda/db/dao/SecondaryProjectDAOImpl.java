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
 * Secondarr project DAO
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.beans.SecondaryProjectBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;


@Repository
@Transactional
public class SecondaryProjectDAOImpl extends HibernateDAOImpl implements SecondaryProjectDAO {

	public SecondaryProjectDAOImpl() {

	}

	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public SecondaryProjectDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	@Override
	public Set<SecondaryProjectBean> getAccessionsBySecondaryProjectId(String projectId)
			throws SQLException {
		return (new LinkedHashSet<>(getCurrentSession()
											.createQuery("from genes_secondary_project where secondary_project_id=?1")
											.setParameter(1, projectId)
											.getResultList()));
	}

	@Transactional(readOnly = true)
	@Override
	public Set<SecondaryProjectBean> getAccessionsBySecondaryProjectId(String projectId, String group_label)
		throws SQLException {
		return (new LinkedHashSet<>(getCurrentSession()
			.createQuery("from genes_secondary_project where secondary_project_id=?1 AND group_label=?2")
			.setParameter(1, projectId)
			.setParameter(2, group_label)
			.getResultList()));
	}
}