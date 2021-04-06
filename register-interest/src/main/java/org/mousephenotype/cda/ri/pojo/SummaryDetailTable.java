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

package org.mousephenotype.cda.ri.pojo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

public class SummaryDetailTable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String style =
        "  table {" +
            "    font-family: arial, sans-serif;" +
            "    border-collapse: collapse;" +
            "    width: 100%;" +
            "}" +
            "td, th {" +
            "    border: 1px solid #dddddd;" +
            "    text-align: left;" +
            "    padding: 8px;" +
            "}" +
            "tr:nth-child(even) {" +
            "    background-color: #dddddd;" +
            "}";

    public static String build(List<SummaryDetail> currentSds, boolean inHtml) {
        StringBuilder body = new StringBuilder();
        body
            .append(buildHeading(inHtml))
            .append(String.join("", buildRows(currentSds, inHtml)))
            .append(inHtml ? "</table>" : "\n");
        return body.toString();
    }

    private static String[] buildRows(List<SummaryDetail> currentSds, boolean inHtml) {
        return currentSds
            .stream()
            .map((SummaryDetail sd) -> sd.toStringDecorated(inHtml) + (inHtml ? "</tr>" : "\n"))
            .toArray(String[]::new);
    }

    private static String buildHeading(boolean inHtml) {
        StringBuilder sb = new StringBuilder();
        if (inHtml) {
            sb
                .append("<style>" + style + "</style>")
                .append("<table id=\"genesTable\"><tr>")
                .append(SummaryDetail.toStringHeading(inHtml))
                .append("</tr>");
        } else {
            sb.append(SummaryDetail.toStringHeading(inHtml));
        }
        return sb.toString();
    }

    public static String buildHeadingRow(String tag, List<String> values) {
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
        StringBuilder sb            = new StringBuilder();
        String        escapedValue  = HtmlUtils.htmlEscape(value);
        String        escapedAnchor = (anchor == null ? null : HtmlUtils.htmlEscape(anchor));

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
