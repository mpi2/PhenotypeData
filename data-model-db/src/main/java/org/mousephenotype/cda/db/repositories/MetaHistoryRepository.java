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

import java.util.List;

public interface MetaHistoryRepository extends CrudRepository<MetaHistory, Long> {

    // This query is MYSQL-specific.
    String getAllDataReleaseVersionQuery = "SELECT DISTINCT data_release_version, null AS property_key, null AS property_value FROM meta_history ORDER BY CAST(data_release_version as unsigned) ASC";
    @Query(value = getAllDataReleaseVersionQuery, nativeQuery = true)
    List<MetaHistory> getAllDataReleaseVersionsCastAsc();

    // This query is MYSQL-specific.
    String getAllDataReleaseVersionsExcludingOneCastAscQuery = "SELECT DISTINCT data_release_version FROM meta_history WHERE data_release_version <> '1.0' ORDER BY CAST(data_release_version as unsigned) ASC";
    @Query(value = getAllDataReleaseVersionsExcludingOneCastAscQuery, nativeQuery = true)
    List<MetaHistory> getAllDataReleaseVersionsExcludingOneCastAsc(String dataReleaseVersionToExclude);

    // This query is MYSQL-specific
    String getAllByPropertyKeyQueryCastAsc = "SELECT * FROM meta_history WHERE property_key = 'phenotyped_genes' ORDER BY CAST(data_release_version AS unsigned) ASC";
    @Query(value = getAllByPropertyKeyQueryCastAsc, nativeQuery = true)
    List<MetaHistory> getAllByPropertyKeyCastAsc(String propertyKey);
}