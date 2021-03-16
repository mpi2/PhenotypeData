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

package org.mousephenotype.cda.ri.entities;

/**
 * Created by mrelac on 12/05/2017.
 *
 * This class extends Gene, adding boolean flags indicating whether or not the status components are to be decorated
 * or not. Decoration indicates that one or more of the gene's status components is now different from the status in the
 * gene_sent table. If there is no record in the gene_sent table (i.e. {@code geneSent} is null), all statuses are
 * considered decorated; otherwise, only those statuses that differ are decorated. If the gene status and geneSent
 * status are identical, {@code isDecorated} is false; otherwise, the caller should interrogate the other booleans
 * to learn which status(es) changed, if any.
 */
public class GeneWithDecoration extends Gene {

    private boolean isAssignmentStatusDecorated;
    private boolean isConditionalAlleleProductionStatusDecorated;
    private boolean isNullAlleleProductionStatusDecorated;
    private boolean isPhenotypingStatusDecorated;
    private boolean isDecorated;


    public GeneWithDecoration(Gene gene, GeneSent geneSent) {
        super(gene);

        if (geneSent == null) {
            isAssignmentStatusDecorated = true;
            isConditionalAlleleProductionStatusDecorated = true;
            isNullAlleleProductionStatusDecorated = true;
            isPhenotypingStatusDecorated = true;
            isDecorated = true;
        } else {

            if (( ! isStatusEqual(gene.getRiAssignmentStatus(), geneSent.getAssignmentStatus())) || geneSent.getSentAt() == null) {
                isDecorated = true;
                isAssignmentStatusDecorated = true;
            }

            if (( ! isStatusEqual(gene.getRiConditionalAlleleProductionStatus(), geneSent.getConditionalAlleleProductionStatus())) || geneSent.getSentAt() == null) {
                isDecorated = true;
                isConditionalAlleleProductionStatusDecorated = true;
            }

            if (( ! isStatusEqual(gene.getRiNullAlleleProductionStatus(), geneSent.getNullAlleleProductionStatus())) || geneSent.getSentAt() == null) {
                isDecorated = true;
                isNullAlleleProductionStatusDecorated = true;
            }

            if (( ! isStatusEqual(gene.getRiPhenotypingStatus(), geneSent.getPhenotypingStatus())) || geneSent.getSentAt() == null) {
                isDecorated = true;
                isPhenotypingStatusDecorated = true;
            }
        }
    }

    public boolean isAssignmentStatusDecorated() {
        return isAssignmentStatusDecorated;
    }

    public void setAssignmentStatusDecorated(boolean assignmentStatusDecorated) {
        isAssignmentStatusDecorated = assignmentStatusDecorated;
    }

    public boolean isConditionalAlleleProductionStatusDecorated() {
        return isConditionalAlleleProductionStatusDecorated;
    }

    public void setConditionalAlleleProductionStatusDecorated(boolean conditionalAlleleProductionStatusDecorated) {
        isConditionalAlleleProductionStatusDecorated = conditionalAlleleProductionStatusDecorated;
    }

    public boolean isNullAlleleProductionStatusDecorated() {
        return isNullAlleleProductionStatusDecorated;
    }

    public void setNullAlleleProductionStatusDecorated(boolean nullAlleleProductionStatusDecorated) {
        isNullAlleleProductionStatusDecorated = nullAlleleProductionStatusDecorated;
    }

    public boolean isPhenotypingStatusDecorated() {
        return isPhenotypingStatusDecorated;
    }

    public void setPhenotypingStatusDecorated(boolean phenotypingStatusDecorated) {
        isPhenotypingStatusDecorated = phenotypingStatusDecorated;
    }

    public boolean isDecorated() {
        return isDecorated;
    }

    public void setDecorated(boolean decorated) {
        isDecorated = decorated;
    }


// PROTECTED METHODS


    protected boolean isStatusEqual(String status1, String status2) {
        if (status1 == status2) {
            return true;
        }
        if (status1 == null || status2 == null) {
            return false;
        }
        return status1.equals(status2);
    }
}