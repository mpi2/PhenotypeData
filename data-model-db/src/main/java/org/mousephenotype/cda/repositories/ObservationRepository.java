package org.mousephenotype.cda.repositories;

import org.mousephenotype.cda.domain.Observation;


/**
 * Created by jmason on 23/06/2015.
 */
public interface ObservationRepository  {

	Observation findById(Long id);

}
