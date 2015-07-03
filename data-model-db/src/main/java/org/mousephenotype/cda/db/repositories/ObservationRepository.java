package org.mousephenotype.cda.db.repositories;

import org.mousephenotype.cda.db.domain.Observation;


/**
 * Created by jmason on 23/06/2015.
 */
public interface ObservationRepository  {

	Observation findById(Long id);

}
