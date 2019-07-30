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

import org.mousephenotype.cda.db.pojo.ObservationMissingNotMissingCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObservationMissingNotMissingCountRepository extends CrudRepository<ObservationMissingNotMissingCount, Long> {

    String notMissingCountQuery =
    "SELECT\n" +
            "  o.missing                  AS missing\n" +
            ", COUNT(*)                   AS count\n" +
            ", e.organisation_id          AS organisationId\n" +
            ", o.observation_type         AS observationType\n" +
            ", o.parameter_status         AS parameterStatus\n" +
            ", o.parameter_status_message AS parameterStatusMessage\n" +
            "FROM observation o\n" +
            "JOIN experiment_observation eo ON eo.observation_id = o.id\n" +
            "JOIN experiment e ON e.id = eo.experiment_id\n" +
            "WHERE (missing = 0)  AND (((TRIM(IFNULL(parameter_status, ''))) != '')\n" +
            "   OR (TRIM(IFNULL(parameter_status_message, '')) != ''))\n" +
            "GROUP BY observation_type, e.organisation_id, o.observation_type, o.parameter_status, o.parameter_status_message\n" +
            "ORDER BY e.organisation_id limit 1000000\n";
    /**
     * Fetch count of records NOT missing but with not null/empty parameter_status or parameter_status_message.
     * @return count, interesting fields
     */
    @Query(value = notMissingCountQuery, nativeQuery = true)
    List<ObservationMissingNotMissingCount> getObservationIsNotMissingAndParameterStatusAndParameterStatusMessageAreEmpty();


    String missingCountQuery =
    "SELECT\n" +
            "  o.missing                  AS missing\n" +
            ", COUNT(*)                   AS count\n" +
            ", e.organisation_id          AS organisationId\n" +
            ", o.observation_type         AS observationType\n" +
            ", o.parameter_status         AS parameterStatus\n" +
            ", o.parameter_status_message AS parameterStatusMessage\n" +
            "FROM observation o\n" +
            "JOIN experiment_observation eo ON eo.observation_id = o.id\n" +
            "JOIN experiment e ON e.id = eo.experiment_id\n" +
            "WHERE (missing = 1) AND (TRIM(IFNULL(o.parameter_status, '')) = '')\n" +
            "GROUP BY o.observation_type, e.organisation_id, o.parameter_status, o.parameter_status_message\n" +
            "ORDER BY e.organisation_id limit 1000000\n";
    /**
     * Fetch count of records missing that have a null/empty parameter_status.
     * @return count, interesting fields
     */
    @Query(value = missingCountQuery, nativeQuery = true)
    List<ObservationMissingNotMissingCount> getObservationIsMissingAndParameterStatusIsNotEmpty();
}