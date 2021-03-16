/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
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

import org.mousephenotype.cda.ri.entities.Gene;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class GeneRowMapper implements RowMapper<Gene> {

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
    public Gene mapRow(ResultSet rs, int rowNum) throws SQLException {
        Gene gene = new Gene();

        gene.setPk(rs.getInt("pk"));

        gene.setMgiAccessionId(rs.getString("mgi_accession_id"));
        gene.setSymbol(rs.getString("symbol"));
        gene.setAssignedTo(rs.getString("assigned_to"));
        gene.setAssignmentStatus(rs.getString("assignment_status"));
        Timestamp ts = rs.getTimestamp("assignment_status_date");
        gene.setAssignmentStatusDate(ts == null ? null : new Date(ts.getTime()));
        gene.setRiAssignmentStatus(rs.getString("ri_assignment_status"));   // Store null ri status values if they are are null (i.e. don't remap them to "")

        gene.setConditionalAlleleProductionCentre(rs.getString("conditional_allele_production_centre"));
        gene.setConditionalAlleleProductionStatus(rs.getString("conditional_allele_production_status"));
        gene.setRiConditionalAlleleProductionStatus(rs.getString("ri_conditional_allele_production_status"));   // Store null ri status values if they are are null (i.e. don't remap them to "")
        ts = rs.getTimestamp("conditional_allele_production_status_date");
        gene.setConditionalAlleleProductionStatusDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("conditional_allele_production_start_date");
        gene.setConditionalAlleleProductionStartDate(ts == null ? null : new Date(ts.getTime()));

        gene.setNullAlleleProductionCentre(rs.getString("null_allele_production_centre"));
        gene.setNullAlleleProductionStatus(rs.getString("null_allele_production_status"));
        gene.setRiNullAlleleProductionStatus(rs.getString("ri_null_allele_production_status"));   // Store null ri status values if they are are null (i.e. don't remap them to "")
        ts = rs.getTimestamp("null_allele_production_status_date");
        gene.setNullAlleleProductionStatusDate(ts == null ? null : new Date(ts.getTime()));
        ts = rs.getTimestamp("null_allele_production_start_date");
        gene.setNullAlleleProductionStartDate(ts == null ? null : new Date(ts.getTime()));

        gene.setPhenotypingCentre(rs.getString("phenotyping_centre"));
        gene.setPhenotypingStatus(rs.getString("phenotyping_status"));
        ts = rs.getTimestamp("phenotyping_status_date");
        gene.setPhenotypingStatusDate(ts == null ? null : new Date(ts.getTime()));
        gene.setRiPhenotypingStatus(rs.getString("ri_phenotyping_status"));   // Store null ri status values if they are are null (i.e. don't remap them to "")
        gene.setNumberOfSignificantPhenotypes(rs.getInt("number_of_significant_phenotypes"));

        gene.setCreatedAt(new Date(rs.getDate("created_at").getTime()));
        gene.setUpdatedAt(new Date(rs.getDate("updated_at").getTime()));

        return gene;
    }
}