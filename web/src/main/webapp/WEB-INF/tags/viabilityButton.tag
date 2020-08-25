<%@ tag import="org.mousephenotype.cda.common.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>

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
	<!--<a class="status done" href="${geneAcc}">	-->
	<c:forEach var="call" items="${callList}" varStatus="loop">
		<%--<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>--%>
		<c:if test="${fn:contains(call, 'Lethal') || fn:contains(call, 'lethal')}">
			<a class="badge badge-danger" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Subviable')}">
			<a class="badge badge-warning" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Viable') || fn:contains(call, 'no viability phenotype detected')}">
			<a class="badge badge-success" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
	</c:forEach>
	<!--</a> -->
</c:if>

<c:if test="${callList.size() > 1}">
	<!--<a  href="${link}" class="status done" title="Conflicting calls were made for this gene. For details refer to the associations table on the gene page.">-->
	<!-- span  class="status done" title="Conflicting calls were made for this gene. For details refer to the associations table on the gene page."-->
	<c:forEach var="call" items="${callList}" varStatus="loop">
		<%--<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>--%>
		<c:if test="${fn:contains(call, 'Lethal') || fn:contains(call, 'lethal')}">
			<a class="badge badge-danger" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Subviable')}">
			<a class="badge badge-warning" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Viable') || fn:contains(call, 'no viability phenotype detected')}">
			<a class="badge badge-success" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
	</c:forEach>
	<!--/span-->
</c:if>
