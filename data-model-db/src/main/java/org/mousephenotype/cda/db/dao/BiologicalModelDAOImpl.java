/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
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
 * Biological model manager implementation.
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.mousephenotype.cda.db.pojo.LiveSample;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public class BiologicalModelDAOImpl extends HibernateDAOImpl implements BiologicalModelDAO {

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public BiologicalModelDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	public BiologicalModelDAOImpl() {
	}


	@Transactional(readOnly = true)
	public BiologicalModel findByDbidAndAllelicCompositionAndGeneticBackgroundAndZygosity(Integer id, String allelicComposition, String geneticBackground, String zygosity) {
		return (BiologicalModel) getCurrentSession()
				.createQuery("from BiologicalModel m where m.datasource.id=?1 and m.allelicComposition=?2 and m.geneticBackground=?3 and m.zygosity=?4")
				.setParameter(1, id)
				.setParameter(2, allelicComposition)
				.setParameter(3, geneticBackground)
				.setParameter(4, zygosity)
				.uniqueResult();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<LiveSample> getAllLiveSamples() {
		return getCurrentSession()
				.createQuery("SELECT live FROM LiveSample AS live")
				.getResultList();
	}

	@Transactional(readOnly = true)
	public BiologicalModel getBiologicalModelById(int modelId) {
		return (BiologicalModel) getCurrentSession()
				.createQuery("from BiologicalModel as m where m.id = ?1")
				.setParameter(1, modelId)
				.uniqueResult();
	}
}