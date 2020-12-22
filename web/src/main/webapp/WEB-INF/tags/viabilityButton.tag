<%@ tag import="org.mousephenotype.cda.common.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>
<%@ tag import="org.apache.commons.text.WordUtils" %>

<%@ attribute name="callList" required="true" type="java.util.Set"%>
<%@ attribute name="geneAcc" required="true" type="java.lang.String"%>


<%
	// Build the gene viability link to include all IMPC viabilty parmeters
	final List<String> params = new ArrayList<>();
	for (String p : Constants.adultViabilityParameters) {
		params.add(String.format("parameter_stable_id=%s", p));
	}
	final String VIABILITY_LINK = String.join("&", params);
	jspContext.setAttribute("VIABILITY_LINK", VIABILITY_LINK);
%>

<c:set var="via_href" value="${baseUrl}/charts?accession=${geneAcc}&${VIABILITY_LINK}" />

<c:if test="${callList.size() == 1}">
	<c:forEach var="call" items="${callList}" varStatus="loop">
		<c:if test="${fn:contains(fn:toLowerCase(call), 'lethal')}">
			<a class="badge badge-danger" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${WordUtils.capitalizeFully(call)}</a>
		</c:if>
		<c:if test="${fn:contains(fn:toLowerCase(call), 'subviable')}">
			<a class="badge badge-warning" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${WordUtils.capitalizeFully(call)}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Viable')}">
			<a class="badge badge-success" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${WordUtils.capitalizeFully(call)}</a>
		</c:if>
	</c:forEach>
	<!--</a> -->
</c:if>

<c:if test="${callList.size() > 1}">
	<c:forEach var="call" items="${callList}" varStatus="loop">
		<c:if test="${fn:contains(fn:toLowerCase(call), 'lethal')}">
			<a class="badge badge-danger" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${WordUtils.capitalizeFully(call)}</a>
		</c:if>
		<c:if test="${fn:contains(fn:toLowerCase(call), 'subviable')}">
			<a class="badge badge-warning" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${WordUtils.capitalizeFully(call)}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Viable')}">
			<a class="badge badge-success" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${WordUtils.capitalizeFully(call)}</a>
		</c:if>
	</c:forEach>
	<!--/span-->
</c:if>
