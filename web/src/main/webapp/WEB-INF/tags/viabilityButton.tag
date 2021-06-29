<%@ tag import="org.mousephenotype.cda.common.Constants" %>
<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="callList" required="true" type="java.util.Set" %>
<%@ attribute name="geneAcc" required="true" type="java.lang.String" %>

<%
    // Build the gene viability link to include all IMPC viability paramFragment
    final List<String> params = new ArrayList<>();
    for (String p : Constants.adultViabilityParameters) {
        params.add(String.format("parameter_stable_id=%s", p));
    }
    jspContext.setAttribute("paramFragment", String.join("&", params));
    jspContext.setAttribute("baseUrl", request.getAttribute("baseUrl").toString());
%>

<c:if test="${callList.size() == 0}">
    No Viability Data
</c:if>
<c:if test="${callList.size() > 0}">
    <a href="${baseUrl}/charts?accession=${geneAcc}&${paramFragment}">Viability Data <i class="far fa-chevron-right fa-xs ml-2"></i></a>
</c:if>


