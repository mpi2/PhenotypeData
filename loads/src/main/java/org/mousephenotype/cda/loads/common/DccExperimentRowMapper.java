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
 * Created by mrelac on 10/11/2016.
 */
public class DccExperimentRowMapper implements RowMapper<DccExperimentDTO> {

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
        public DccExperimentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            DccExperimentDTO experiment = new DccExperimentDTO();

            experiment.setDatasourceShortName(rs.getString("datasourceShortName"));
            experiment.setExperimentId(rs.getString("experimentId"));
            experiment.setSequenceId(rs.getString("sequenceId"));
            experiment.setDateOfExperiment(rs.getDate("dateOfExperiment"));
            experiment.setCenterId(rs.getString("centerId"));
            experiment.setPipeline(rs.getString("pipeline"));
            experiment.setProject(rs.getString("project"));
            experiment.setProcedureId(rs.getString("procedureId"));
            experiment.setColonyId(rs.getString("colonyId"));
            experiment.setLineLevel(rs.getInt("isLineLevel") == 1 ? true : false);

            return experiment;
        }
    }