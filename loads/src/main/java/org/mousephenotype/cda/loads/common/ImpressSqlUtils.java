/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.utilities.RunStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

public class ImpressSqlUtils {



    /**
     * This method generically replaces any obsolete/missing ontology terms, identified by {@code ontologyAccessionIds},
     * walking {@code ontologyAccessionIds}, calling {@code }getLatestOntologyTerm()} to replace any obsolete/missing
     * ontology terms.
     *
     * @param ontologyAccessionIds
     * @param jdbc a {@link NamedParameterJdbcTemplate} instance pointing to the database to be updated
     * @param ontologyAccessionIds the list of ontology accession ids to be checked and, if obsolete or missing, replaced
     * @param tableName the name of the table to be updated
     * @param ontologyAccColumnName the name of the ontology accesion id column whose value will be replaced if obsolete
     *                              or missing
     *
     * @return a {@link Set <OntologyTermAnomaly>} a list of the anomalies
     */
    // FIXME Put this in a new class, ImpressSqlUtils, then you can get rid of jdbc, tableName, and ontologyAccColumnName parameters!
    public Set<OntologyTermAnomaly> checkAndUpdateOntologyTerms(NamedParameterJdbcTemplate jdbc, List<String> ontologyAccessionIds, String tableName, String ontologyAccColumnName) {

        String dbName = sqlUtils.getDatabaseName(jdbc);
        String update = "UPDATE " + tableName + "\n" +
                "SET " + ontologyAccColumnName + " = :newOntologyAcc WHERE " + ontologyAccColumnName + " = :originalOntologyAcc;";

        Set<OntologyTermAnomaly> anomalies = new HashSet<>();
        for (String originalAcc : ontologyAccessionIds) {
            OntologyTerm originalTerm = getOntologyTerm(originalAcc);
            if ((originalTerm != null) && ( ! originalTerm.getIsObsolete())) {
                continue;
            }

            RunStatus    status                  = new RunStatus();
            OntologyTerm replacementOntologyTerm = getLatestOntologyTerm(originalAcc, status);
            String       replacementAcc          = null;

            if (replacementOntologyTerm != null) {
                replacementAcc = replacementOntologyTerm.getId().getAccession();
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("originalOntologyAcc", originalAcc);
                parameterMap.put("newOntologyAcc", replacementOntologyTerm.getId().getAccession());
                jdbc.update(update, parameterMap);
            }

            // Log the anomalies
            for (String reason : status.getErrorMessages()) {
                anomalies.add(new OntologyTermAnomaly(dbName, tableName, ontologyAccColumnName, originalAcc, replacementAcc, reason));
            }
            for (String reason : status.getWarningMessages()) {
                anomalies.add(new OntologyTermAnomaly(dbName, tableName, ontologyAccColumnName, originalAcc, replacementAcc, reason));
            }
        }

        for (OntologyTermAnomaly anomaly : anomalies) {

            // Log the anomalies
            anomaly.setDbName(dbName);
            anomaly.setTableName(tableName);
            anomaly.setOntologyAccColumnName(ontologyAccColumnName);

            insertOntologyTermAnomaly(anomaly);

        }

        return anomalies;
    }
}
