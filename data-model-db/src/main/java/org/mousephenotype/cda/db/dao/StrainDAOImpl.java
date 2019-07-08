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
 * Strain data access manager implementation.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.pojo.Strain;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class StrainDAOImpl extends HibernateDAOImpl implements StrainDAO {

	public StrainDAOImpl() {
	}

	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public StrainDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	public Strain getStrainByName(String name) {
		return (Strain) getCurrentSession()
				.createQuery("from Strain as s where s.name= ?1")
				.setParameter(1, name)
				.uniqueResult();
	}
}