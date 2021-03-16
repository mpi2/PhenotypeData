/*******************************************************************************
 *  Copyright © 2018 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.rowmappers;

import org.mousephenotype.cda.ri.entities.GeneSentSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mrelac on 14/02/2018.
 */
public class GeneSentSummaryRowMapper implements RowMapper<GeneSentSummary> {

    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    @Override
    public GeneSentSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        GeneSentSummary geneSentSummary = new GeneSentSummary();

        geneSentSummary.setPk(rs.getInt("pk"));

        geneSentSummary.setSubject((rs.getString("subject")));
        geneSentSummary.setBody((rs.getString("body")));

        geneSentSummary.setContactPk(rs.getInt("contact_pk"));

        geneSentSummary.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));

        Timestamp ts = rs.getTimestamp("sent_at");
        geneSentSummary.setSentAt(ts == null ? null : new Date(ts.getTime()));

        geneSentSummary.setUpdatedAt(new Date(rs.getTimestamp("updated_at").getTime()));

        return geneSentSummary;
    }
}