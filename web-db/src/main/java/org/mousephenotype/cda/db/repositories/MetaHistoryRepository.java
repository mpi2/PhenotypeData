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

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface MetaHistoryRepository extends CrudRepository<MetaHistory, Long> {

    void deleteAllByDataReleaseVersion(String dataReleaseVersion);

    String getAllDataReleaseVersionsCastAscQuery =
            "SELECT DISTINCT new java.lang.String(dataReleaseVersion) FROM org.mousephenotype.cda.db.pojo.MetaHistory ORDER BY CAST(dataReleaseVersion as float) ASC";
    @Query(value = getAllDataReleaseVersionsCastAscQuery)
    <T> List<T> getAllDataReleaseVersionsCastAsc();


    String getAllDataReleaseVersionsCastDescQuery =
            "SELECT DISTINCT new java.lang.String(dataReleaseVersion) FROM org.mousephenotype.cda.db.pojo.MetaHistory ORDER BY CAST(dataReleaseVersion as float) DESC";
    @Query(value = getAllDataReleaseVersionsCastDescQuery)
    <T> List<T> getAllDataReleaseVersionsCastDesc();

    String getAllDataReleaseVersionsLessThanSpecifiedQuery =
            "SELECT DISTINCT new java.lang.String(dataReleaseVersion)"
                    + " FROM org.mousephenotype.cda.db.pojo.MetaHistory"
                    + " WHERE CAST(dataReleaseVersion as float) < CAST(:dataReleaseVersionSpecified as float)"
                    +  "ORDER BY CAST(dataReleaseVersion as float) DESC";
    @Query(value = getAllDataReleaseVersionsLessThanSpecifiedQuery)
    <T> List<T> getAllDataReleaseVersionsBeforeSpecified(@Param("dataReleaseVersionSpecified") String dataReleaseVersionSpecified);


    String getAllByPropertyKeyQueryCastAsc =
            "FROM org.mousephenotype.cda.db.pojo.MetaHistory WHERE propertyKey = :propertyKey ORDER BY CAST(dataReleaseVersion as float) ASC";
    @Query(value = getAllByPropertyKeyQueryCastAsc)
    List<MetaHistory> getAllByPropertyKeyCastAsc(@Param("propertyKey") String propertyKey);
}
