<%@ tag import="org.mousephenotype.cda.common.Constants" %>
<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>

<%@ attribute name="callList" required="true" type="java.util.Set" %>
<%@ attribute name="geneAcc" required="true" type="java.lang.String" %>

<%
    // Build the gene viability link to include all IMPC viability paramFragment
    final List<String> params = new ArrayList<>();
    for (String p : Constants.adultViabilityParameters) {
        params.add(String.format("parameter_stable_id=%s", p));
    }
    final String paramFragment = String.join("&", params);
    final String baseUrl = request.getAttribute("baseUrl").toString();
    final String hrefSnippet = String.format("href='%s/charts?accession=%s&%s'", baseUrl, geneAcc, paramFragment);

    if (callList.isEmpty()) {
        // No viability data, no link and danger-badge
        jspContext.setAttribute("HREF",  "");
        jspContext.setAttribute("TEXT",  "No data available");
        jspContext.setAttribute("BADGE", "badge-danger");
        jspContext.setAttribute("STYLE", "color: white; font-size: 100%; padding: 5px;");
    } else {
        // Turn badge green and link to viability page
        jspContext.setAttribute("HREF",  hrefSnippet);
        jspContext.setAttribute("TEXT",  "Data available");
        jspContext.setAttribute("BADGE", "badge-success");
        jspContext.setAttribute("STYLE", "font-size: 100%; padding: 5px;");
    }
%>

<a class="badge ${BADGE}" style="${STYLE}" ${HREF}>${TEXT}</a>

