<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ attribute name="callList" required="true" type="java.util.Set"%>


<c:if test="${callList.size() == 1}">
	<a class="status done">	
		<c:forEach var="call" items="${callList}" varStatus="loop">
			<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>
			<c:if test="${!loop.last}">   </c:if>
		</c:forEach> 
	</a>
</c:if>

<c:if test="${callList.size() > 1}">
	<a class="status done" title="Conflicting calls were made for this gene. For details refer to the associations table on the gene page.">	
		<span class="left"><i class="fa fa-exclamation" ></i></span>
		<c:forEach var="call" items="${callList}" varStatus="loop">
			<span class="left">${call.replaceAll("Homozygous - ","Hom<br/>")}</span>
			<c:if test="${!loop.last}">   </c:if>
		</c:forEach> 
	</a>
</c:if>