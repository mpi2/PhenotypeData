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
* Phenotyping project data access manager implementation.
*
* @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
* @since February 2012
*/

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.pojo.Project;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ProjectDAOImpl extends HibernateDAOImpl implements ProjectDAO {

	public ProjectDAOImpl() {

	}

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public ProjectDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Project> getAllProjects() {
		return getCurrentSession()
				.createQuery("from Project")
				.list();
	}
}