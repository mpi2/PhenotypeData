<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ attribute name="callList" required="true" type="java.util.Set"%>
<%@ attribute name="link" required="true" type="java.lang.String"%>


<c:if test="${callList.size() == 1}">
	<!--<a class="status done" href="${link}">	-->
		<c:forEach var="call" items="${callList}" varStatus="loop">
			<%--<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>--%>
			<span class="left">${call}</span>
			<c:if test="${!loop.last}">   </c:if>
		</c:forEach> 
	<!--</a> -->
</c:if>

<c:if test="${callList.size() > 1}">
	<!--<a  href="${link}" class="status done" title="Conflicting calls were made for this gene. For details refer to the associations table on the gene page.">-->
	<!-- span  class="status done" title="Conflicting calls were made for this gene. For details refer to the associations table on the gene page."-->
		<span class="left"><i class="fa fa-exclamation" ></i></span>
		<c:forEach var="call" items="${callList}" varStatus="loop">
			<%--<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>--%>
			<span class="left">${call}</span>
			<c:if test="${!loop.last}">&nbsp;&nbsp;&nbsp;</c:if>
		</c:forEach> 
	<!--/span-->
</c:if>