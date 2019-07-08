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
 * External data source access manager implementation.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class GenomicFeatureDAOImpl extends HibernateDAOImpl implements
		GenomicFeatureDAO {

	public GenomicFeatureDAOImpl() {

	}
	/**
	 * Creates a new Hibernate GenomicFeature data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public GenomicFeatureDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureByAccession(String accession) {
		return (GenomicFeature) getCurrentSession()
				.createQuery("from GenomicFeature as g where g.id.accession= ?1")
				.setParameter(1, accession)
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	public GenomicFeature getGenomicFeatureByAccessionAndDbId(String accession,int dbId) {
		return (GenomicFeature) getCurrentSession()
				.createQuery("from GenomicFeature as g where g.id.accession= ?1 and g.id.databaseId= ?2")
				.setParameter(1, accession)
				.setParameter(2, dbId).uniqueResult();
	}
}