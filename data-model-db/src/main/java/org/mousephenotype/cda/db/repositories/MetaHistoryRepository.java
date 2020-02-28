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

import org.mousephenotype.cda.db.pojo.MetaHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MetaHistoryRepository extends CrudRepository<MetaHistory, Long> {

    String getAllDataReleaseVersionQuery =
            "SELECT DISTINCT new java.lang.String(dataReleaseVersion) FROM MetaHistory ORDER BY CAST(dataReleaseVersion as float) ASC";
    @Query(value = getAllDataReleaseVersionQuery)
    <T> List<T> getAllDataReleaseVersionsCastAsc();


    String getAllDataReleaseVersionsLessThanSpecifiedQuery =
            "SELECT DISTINCT new java.lang.String(dataReleaseVersion)"
                    + " FROM MetaHistory"
                    + " WHERE CAST(dataReleaseVersion as float) < CAST(:dataReleaseVersionSpecified as float)"
                    +  "ORDER BY CAST(dataReleaseVersion as float) DESC";
    @Query(value = getAllDataReleaseVersionsLessThanSpecifiedQuery)
    <T> List<T> getAllDataReleaseVersionsBeforeSpecified(@Param("dataReleaseVersionSpecified") String dataReleaseVersionSpecified);


    String getAllByPropertyKeyQueryCastAsc =
            "FROM MetaHistory WHERE propertyKey = :propertyKey ORDER BY CAST(dataReleaseVersion as float) ASC";
    @Query(value = getAllByPropertyKeyQueryCastAsc)
    List<MetaHistory> getAllByPropertyKeyCastAsc(@Param("propertyKey") String propertyKey);
}
