/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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
 ******************************************************************************/

package org.mousephenotype.cda.db.repositories;

import org.mousephenotype.cda.db.pojo.ObservationMissingOntologyTerm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObservationMissingOntologyTermRepository extends CrudRepository<ObservationMissingOntologyTerm, Long> {

    String missingOntologyTermsQuery =
    "SELECT DISTINCT\n" +
            "  o.parameter_status\n" +
            ", ot.acc\n" +
            ", e.organisation_id\n" +
            ", o.observation_type\n" +
            "FROM observation o\n" +
            "LEFT OUTER JOIN ontology_term ot ON ot.acc = o.parameter_status AND ot.db_id = 22\n" +
            "JOIN experiment_observation eo ON eo.observation_id = o.id\n" +
            "JOIN experiment e ON e.id = eo.experiment_id\n" +
            "WHERE (TRIM(IFNULL(parameter_status, '')) != '')\n" +
            "  AND ot.acc IS NULL\n" +
            "ORDER BY o.parameter_status, e.organisation_id, o.observation_type\n";

    /**
     * Fetch list of observation.parameter_status that is not in IMPC ontology_term.acc.
     * @return list of missing ontology_term.acc used by observation.parameter_status
     */
    @Query(value = missingOntologyTermsQuery, nativeQuery = true)
    List<ObservationMissingOntologyTerm> getParameterStatusMissingFromOntologyTerms();
}