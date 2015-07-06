package org.mousephenotype.cda.db.repositories;

import org.mousephenotype.cda.db.domain.Observation;
import org.springframework.stereotype.Repository;


/**
 * Created by jmason on 23/06/2015.
 */
@Repository
public class JdbcObservationRepository implements ObservationRepository {

	@Override
	public Observation findById(Long id) {

		return null;
	}
}
