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

import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OntologyTermRepository extends CrudRepository<OntologyTerm, DatasourceEntityId> {

    String query = "SELECT ot FROM OntologyTerm ot WHERE ot.id.accession = :acc AND ot.id.databaseId = (SELECT ds.id FROM Datasource ds WHERE ds.shortName = :shortName)";
    @Query(query)
    OntologyTerm getByAccAndShortName(@Param("acc") String acc, @Param("shortName") String shortName);

    String termByNameQuery = "SELECT ot FROM OntologyTerm ot WHERE ot.name = :name AND ot.id.databaseId = (SELECT ds.id FROM Datasource ds WHERE ds.shortName = :shortName)";
    @Query(termByNameQuery)
    OntologyTerm getByTermNameAndShortName(@Param("name") String name, @Param("shortName") String shortName);

}