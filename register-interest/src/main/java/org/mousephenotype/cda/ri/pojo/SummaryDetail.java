/*******************************************************************************
 * Copyright © 2021 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.pojo;

import org.mousephenotype.cda.solr.service.dto.GeneDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/*
 * This class encapsulates the code and data necessary to represent a single summary
 * detail line as used in the summary web page and the send e-mail facility.
 */
public class SummaryDetail {
    private String  geneAccessionId;
    private String  symbol;
    private String  assignmentStatus;
    private String  conditionalAlleleProductionStatus;
    private String  crisprAlleleProductionStatus;
    private String  nullAlleleProductionStatus;
    private boolean isPhenotypingDataAvailable;

    public SummaryDetail(GeneDTO geneDTO) {
        this.geneAccessionId = geneDTO.getMgiAccessionId();
        this.symbol = geneDTO.getMarkerSymbol();
        this.assignmentStatus = geneDTO.getAssignmentStatus();
        this.conditionalAlleleProductionStatus = geneDTO.getConditionalAlleleProductionStatus();
        this.crisprAlleleProductionStatus = geneDTO.getCrisprAlleleProductionStatus();
        this.nullAlleleProductionStatus = geneDTO.getNullAlleleProductionStatus();
        this.isPhenotypingDataAvailable = geneDTO.isPhenotypingDataAvailable();
    }

    public SummaryDetail() { }

    public String getGeneAccessionId() {
        return geneAccessionId;
    }

    public void setGeneAccessionId(String geneAccessionId) {
        this.geneAccessionId = geneAccessionId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getConditionalAlleleProductionStatus() {
        return conditionalAlleleProductionStatus;
    }

    public void setConditionalAlleleProductionStatus(String conditionalAlleleProductionStatus) {
        this.conditionalAlleleProductionStatus = conditionalAlleleProductionStatus;
    }

    public String getCrisprAlleleProductionStatus() {
        return crisprAlleleProductionStatus;
    }

    public void setCrisprAlleleProductionStatus(String crisprAlleleProductionStatus) {
        this.crisprAlleleProductionStatus = crisprAlleleProductionStatus;
    }

    public String getNullAlleleProductionStatus() {
        return nullAlleleProductionStatus;
    }

    public void setNullAlleleProductionStatus(String nullAlleleProductionStatus) {
        this.nullAlleleProductionStatus = nullAlleleProductionStatus;
    }

    public boolean isPhenotypingDataAvailable() {
        return isPhenotypingDataAvailable;
    }

    public void setPhenotypingDataAvailable(boolean phenotypingDataAvailable) {
        isPhenotypingDataAvailable = phenotypingDataAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryDetail that = (SummaryDetail) o;
        return isPhenotypingDataAvailable == that.isPhenotypingDataAvailable &&
            geneAccessionId.equals(that.geneAccessionId) &&
            Objects.equals(assignmentStatus, that.assignmentStatus) &&
            Objects.equals(conditionalAlleleProductionStatus, that.conditionalAlleleProductionStatus) &&
            Objects.equals(crisprAlleleProductionStatus, that.crisprAlleleProductionStatus) &&
            Objects.equals(nullAlleleProductionStatus, that.nullAlleleProductionStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geneAccessionId, assignmentStatus, conditionalAlleleProductionStatus, crisprAlleleProductionStatus, nullAlleleProductionStatus, isPhenotypingDataAvailable);
    }

    public static final  List<String> h             = Arrays.asList(
        "Gene Symbol",
        "Gene Accession Id",
        "Assignment Status",
        "Conditional Allele Production Status",
        "Null Allele Production Status",
        "Crispr Allele Production Status",
        "Phenotyping Data Available"
    );
    private static final String       FORMAT_QUOTED =
        "\"%-" + h.get(0).length() + "." + h.get(0).length() + "s" + "\"" +
            "  \"%-" + h.get(1).length() + "." + h.get(1).length() + "s" + "\"" +
            "  \"%-" + (h.get(2).length() + 10) + "." + (h.get(2).length() + 10) + "s" + "\"" +  // Data is wider than heading
            "  \"%-" + h.get(3).length() + "." + h.get(3).length() + "s" + "\"" +
            "  \"%-" + h.get(4).length() + "." + h.get(4).length() + "s" + "\"" +
            "  \"%-" + h.get(5).length() + "." + h.get(5).length() + "s" + "\"" +
            "  \"%-" + h.get(6).length() + "." + h.get(6).length() + "s" + "\"";

    private static final String       FORMAT_UNQUOTED =
        " %-" + h.get(0).length() + "." + h.get(0).length() + "s" + " " +
            "   %-" + h.get(1).length() + "." + h.get(1).length() + "s" + " " +
            "   %-" + (h.get(2).length() + 10) + "." + (h.get(2).length() + 10) + "s" + " " + // Data is wider than heading
            "   %-" + h.get(3).length() + "." + h.get(3).length() + "s" + " " +
            "   %-" + h.get(4).length() + "." + h.get(4).length() + "s" + " " +
            "   %-" + h.get(5).length() + "." + h.get(5).length() + "s" + " " +
            "   %-" + h.get(6).length() + "." + h.get(6).length() + "s";

    public static String toStringHeading(boolean inHtml) {
        return (inHtml
            ? h.stream().map(s -> "<th>" + s + "</th>").reduce("", String::concat)
            : String.format(FORMAT_QUOTED + "\n", h.get(0), h.get(1), h.get(2), h.get(3), h.get(4), h.get(5), h.get(6)));
    }

    // lastSd may be null. If so, handle it as unchanged (i.e. no asterisk)
    public final String toStringDecorated(SummaryDetail lastSd, boolean inHtml) {
        List<String> d = Arrays.asList(
            symbol,
            geneAccessionId,
            _assignmentStatusToString(lastSd),
            _conditionalAlleleProductionStatusToString(lastSd),
            _nullAlleleProductionStatusToString(lastSd),
            _crisprAlleleProductionStatusToString(lastSd),
            _isPhenotypingDataAvailableToString(lastSd));
        return (inHtml
            ? d.stream().map(s -> "<td>" + s + "</td>").reduce("", String::concat)
            : String.format(FORMAT_UNQUOTED, d.get(0), d.get(1), d.get(2), d.get(3), d.get(4), d.get(5), d.get(6)));
    }

    @Override
    public String toString() {
        return "SummaryDetail{" +
            "geneAccessionId='" + geneAccessionId + '\'' +
            ", symbol='" + symbol + '\'' +
            ", assignmentStatus='" + assignmentStatus + '\'' +
            ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
            ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
            ", crisprAlleleProductionStatus='" + crisprAlleleProductionStatus + '\'' +
            ", isPhenotypingDataAvailable=" + isPhenotypingDataAvailable +
            '}';
    }


    // PRIVATE METHODS


    // These *toString() methods return a a printable string of each declared status.
    // If this status differs from 'other' status, the returned string has " *" appended to it.
    private final String _assignmentStatusToString(SummaryDetail other) {
        String s = assignmentStatus == null ? "None" : assignmentStatus;
        return s + decoration(assignmentStatus, other == null ? null : other.assignmentStatus);
    }

    private final String _nullAlleleProductionStatusToString(SummaryDetail other) {
        String s = nullAlleleProductionStatus == null ? "None" : nullAlleleProductionStatus;
        return s + decoration(nullAlleleProductionStatus, other == null ? null : other.nullAlleleProductionStatus);
    }

    private final String _conditionalAlleleProductionStatusToString(SummaryDetail other) {
        String s = conditionalAlleleProductionStatus == null ? "None" : conditionalAlleleProductionStatus;
        return s + decoration(conditionalAlleleProductionStatus, other == null ? null : other.conditionalAlleleProductionStatus);
    }

    private final String _crisprAlleleProductionStatusToString(SummaryDetail other) {
        String s = crisprAlleleProductionStatus == null ? "None" : crisprAlleleProductionStatus;
        return s + decoration(crisprAlleleProductionStatus, other == null ? null : other.crisprAlleleProductionStatus);
    }

    private final String _isPhenotypingDataAvailableToString(SummaryDetail other) {
        String s = isPhenotypingDataAvailable ? "Yes" : "No";
        return s + decoration(isPhenotypingDataAvailable, other == null ? null : other.isPhenotypingDataAvailable);
    }

    // Returns either "" (if s1 equals s2) or " *" (if s1 does not equal s2).
    // Either s1 or s2 or both can be null.
    private String decoration(String s1, String s2) {
        if ((s1 == null) && (s2 == null)) {
            return "";
        }
        if ((s1 == null) || (s2 == null)) {
            return " *";
        }
        return s1.equalsIgnoreCase(s2) ? "" : " *";
    }

    // Returns either "" (if s1 equals s2) or " *" (if s1 does not equal s2).
    // Either s1 or s2 or both can be null.
    private String decoration(Boolean b1, Boolean b2) {
        if (b1 == null)
            b1 = false;
        if (b2 == null)
            b2 = false;
        return b1 == b2 ? "" : " *";
    }
}
