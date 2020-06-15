<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ attribute name="callList" required="true" type="java.util.Set"%>
<%@ attribute name="geneAcc" required="true" type="java.lang.String"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<c:set var="via_href" value="${baseUrl}/charts?accession=${geneAcc}&parameter_stable_id=IMPC_VIA_001_001_001&parameter_stable_id=IMPC_EVP_001_001&parameter_stable_id=IMPC_EVO_001_001&parameter_stable_id=IMPC_EVM_001_001&parameter_stable_id=IMPC_EVL_001_001"></c:set>

<c:if test="${callList.size() == 1}">
	<!--<a class="status done" href="${geneAcc}">	-->
	<c:forEach var="call" items="${callList}" varStatus="loop">
		<%--<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>--%>
		<c:if test="${fn:contains(call, 'Lethal')}">
			<a class="badge badge-danger" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Subviable')}">
			<a class="badge badge-warning" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Viable')}">
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
		<c:if test="${fn:contains(call, 'Lethal')}">
			<a class="badge badge-danger" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Subviable')}">
			<a class="badge badge-warning" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
		<c:if test="${fn:contains(call, 'Viable')}">
			<a class="badge badge-success" style="font-size: 80%;" href="<c:out value='${via_href}'/>">${call}</a>
		</c:if>
	</c:forEach>
	<!--/span-->
</c:if>
