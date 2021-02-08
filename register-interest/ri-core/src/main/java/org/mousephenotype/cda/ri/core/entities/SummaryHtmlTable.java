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

package org.mousephenotype.cda.ri.core.entities;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.util.Arrays;
import java.util.List;

public class SummaryHtmlTable {

    private final  Logger                   logger   = LoggerFactory.getLogger(this.getClass());
    private static List<String>             headings = Arrays.asList(new String[] {
            "Gene Symbol", "Gene MGI Accession Id", "Assignment Status", "Null Allele Production", "Conditional Allele Production", "Phenotyping Data Available"
    });


    public static final String style =
            "  table {"+
            "    font-family: arial, sans-serif;"+
            "    border-collapse: collapse;"+
            "    width: 100%;"+
            "}"+
            "td, th {"+
            "    border: 1px solid #dddddd;"+
            "    text-align: left;"+
            "    padding: 8px;"+
            "}"+
            "tr:nth-child(even) {"+
            "    background-color: #dddddd;"+
            "}";

    public static String buildTableContent(String paBaseUrl, Summary summary, boolean inHtml) {

        StringBuilder body = new StringBuilder();

        if (inHtml) {
            body
                    .append("<style>" + style + "</style>")
                    .append("<table id=\"genesTable\">")
                    .append(buildRow("th", headings));
        } else {
            body
                    .append(StringUtils.join(headings, "\t"));
        }

        for (Gene gene : summary.getGenes()) {
            body.append(buildRow(paBaseUrl, gene, inHtml));
        }

        body.append(inHtml ? "</table>" : "\n");

        return body.toString();
    }


    /**
     * Builds an html data row for the specified gene and optional {@link GeneSent} instance. {@code} may be null.
     * @param gene This contact's {@link Gene} instance. Never null.
     * @param inHtml Boolean indicating whether or not the output should be in html
     * @return html tr text, wrapped in tr tag.
     */
    public static String buildRow(String paBaseUrl, Gene gene, boolean inHtml) {

        StringBuilder row = new StringBuilder();
        String anchor;
        String cell;
        String currentValue;
        GeneWithDecoration geneWithDecoration;

        if (gene instanceof  GeneWithDecoration) {
            geneWithDecoration = (GeneWithDecoration) gene;
        } else {
            geneWithDecoration = new GeneWithDecoration(gene, null);
            geneWithDecoration.setAssignmentStatusDecorated(false);
            geneWithDecoration.setConditionalAlleleProductionStatusDecorated(false);
            geneWithDecoration.setNullAlleleProductionStatusDecorated(false);
            geneWithDecoration.setPhenotypingStatusDecorated(false);
            geneWithDecoration.setDecorated(false);
        }

        row.append(inHtml ? "<tr>" : "\n");

        // Gene symbol
        anchor = paBaseUrl + "/genes/" + geneWithDecoration.getMgiAccessionId();
        cell = (inHtml ? buildHtmlCell("td", geneWithDecoration.getSymbol(), anchor) : geneWithDecoration.getSymbol() + "\t");
        row.append(cell);


        // Gene MGI accession id
        anchor = "http://www.informatics.jax.org/marker/" + geneWithDecoration.getMgiAccessionId();
        cell = (inHtml ? buildHtmlCell("td", geneWithDecoration.getMgiAccessionId(), anchor) : geneWithDecoration.getMgiAccessionId() + "\t");
        row.append(cell);


        // Assignment status
        currentValue = geneWithDecoration.getRiAssignmentStatus() == null ? "None" : geneWithDecoration.getRiAssignmentStatus();
        if (geneWithDecoration.isAssignmentStatusDecorated()) {
            currentValue += " *";
        }
        cell = (inHtml ? buildHtmlCell("td", currentValue, null) : currentValue + "\t");
        row.append(cell);


        // Null allele production
        currentValue = geneWithDecoration.getRiNullAlleleProductionStatus() == null ? "None" : geneWithDecoration.getRiNullAlleleProductionStatus();
        if (currentValue.equals(GeneStatus.MOUSE_PRODUCTION_STARTED)){
            anchor = null;
        } else if (currentValue.equals(GeneStatus.MOUSE_PRODUCED)) {
            anchor = paBaseUrl + "/genes/" + geneWithDecoration.getMgiAccessionId() + "/#order";
        } else {
            anchor = null;
        }
        if (geneWithDecoration.isNullAlleleProductionStatusDecorated()) {
            currentValue += " *";
        }
        cell = (inHtml ? buildHtmlCell("td", currentValue, anchor) : currentValue + "\t");
        row.append(cell);


        // Conditional allele production
        currentValue = geneWithDecoration.getRiConditionalAlleleProductionStatus() == null ? "None" : geneWithDecoration.getRiConditionalAlleleProductionStatus();
        if (currentValue.equals(GeneStatus.MOUSE_PRODUCTION_STARTED)) {
            anchor = null;
        } else if (currentValue.equals(GeneStatus.MOUSE_PRODUCED)) {
            anchor = paBaseUrl + "/genes/" + geneWithDecoration.getMgiAccessionId() + "/#order";
        } else {
            anchor = null;
        }
        if (geneWithDecoration.isConditionalAlleleProductionStatusDecorated()) {
            currentValue += " *";
        }
        cell = (inHtml ? buildHtmlCell("td", currentValue, anchor) : currentValue + "\t");
        row.append(cell);


        // Phenotyping data available
        currentValue = geneWithDecoration.getRiPhenotypingStatus() == null ? "No" : geneWithDecoration.getRiPhenotypingStatus();
        if (currentValue.equals(GeneStatus.PHENOTYPING_DATA_AVAILABLE)) {
            currentValue = "Yes";
            anchor = paBaseUrl + "/genes/" + geneWithDecoration.getMgiAccessionId() + "/#order";
        } else {
            currentValue = "No";
            anchor = null;
        }
        if (geneWithDecoration.isPhenotypingStatusDecorated()) {
            currentValue += " *";
        }
        cell = (inHtml ? buildHtmlCell("td", currentValue, anchor) : currentValue + "\t");
        row.append(cell);

        return row.toString();
    }

    public static String buildRow(String tag, List<String> values) {
        StringBuffer row = new StringBuffer();

        row.append("<tr>");
        for (String value : values) {
            String escapedValue = HtmlUtils.htmlEscape(value);
            row.append("<" + tag + ">" + escapedValue + "</" + tag + ">");
        }
        row.append("</tr>");

        return row.toString();
    }

    public static String buildHtmlCell(String tag, String value, String anchor) {

        StringBuilder sb = new StringBuilder();

        String escapedValue = HtmlUtils.htmlEscape(value);
        String escapedAnchor = (anchor == null ? null : HtmlUtils.htmlEscape(anchor));

        sb.append("<" + tag + ">");
        if (escapedAnchor != null) {
            sb.append("<a href=\"" + escapedAnchor + "\" alt=\"" + escapedAnchor + "\">");
        }
        sb.append(escapedValue);
        if (escapedAnchor != null) {
            sb.append("</a>");
        }
        sb.append("</" + tag + ">");

        return sb.toString();
    }
}
