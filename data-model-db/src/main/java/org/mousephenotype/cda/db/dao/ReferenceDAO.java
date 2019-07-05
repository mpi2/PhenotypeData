/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.db.dao;

import org.mousephenotype.cda.db.pojo.ReferenceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;


@Repository
@Transactional
public class ReferenceDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public final String heading =
            "MGI allele symbol"
        + "\tMGI allele id"
        + "\tIMPC gene link"
        + "\tMGI allele name"
        + "\tTitle"
        + "\tjournal"
        + "\tPMID"
        + "\tDate of publication"
        + "\tGrant id"
        + "\tGrant agency"
        + "\tPaper link"
        + "\tMesh term"
        + "\tConsortium paper"
        + "\tabstract"
        + "\tcited_by";

    public ReferenceDAO() {

    }

    /**
     * Given a list of <code>ReferenceDTO</code> and a <code>pubMedId</code>,
     * returns the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     *
     * @param references a list of <code>ReferenceDTO</code>
     *
     * @param pubMedId pub med ID
     *
     * @return the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     *
     * @throws SQLException
     */
    public ReferenceDTO getReferenceByPmid(List<ReferenceDTO> references, Integer pubMedId
    ) throws SQLException {
        for (ReferenceDTO reference : references) {
            if (reference.getPmid() == pubMedId) {
                return reference;
            }
        }

        return null;
    }

    /**
     * Fetch all reference rows.
     *
     * @return all reference rows.
     *
     * @throws SQLException
     */
    public List<ReferenceDTO> getReferenceRows() throws SQLException {
        return getReferenceRows();
    }
}