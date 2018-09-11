/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.common;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by mrelac on 01/02/2018.
 */
public class PhenotypeParameterOntologyAnnotationRowMapper implements RowMapper<PhenotypeParameterOntologyAnnotation> {

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
    public PhenotypeParameterOntologyAnnotation mapRow(ResultSet rs, int rowNum) throws SQLException {
        PhenotypeParameterOntologyAnnotation row = new PhenotypeParameterOntologyAnnotation();

        row.setId(rs.getInt("id"));
        row.setEventType(rs.getString("event_type"));
        row.setOptionId(rs.getInt("option_id"));
        row.setOntologyAcc(rs.getString("ontology_acc"));
        row.setOntologyDbId(rs.getInt("ontology_db_id"));
        row.setSex(rs.getString("sex"));

        return row;
    }
}