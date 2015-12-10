<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ attribute name="displayList" required="true" type="java.util.Set"%>
<%@ attribute name="numberToDisplay" required="true" type="java.lang.Integer"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>

<c:set var="count" value="0" scope="page" />


<c:set var="tooltip" value="<h3>${title}</h3><b>" scope="page" />

<c:forEach var="var" items="${displayList}" varStatus="loop">
	<c:if test="${count < numberToDisplay}">${var}<c:if test="${!loop.last && count < numberToDisplay-1}">, </c:if>
		<c:set var="tooltip" value="${tooltip}${var}, " scope="page" />
	</c:if>
	<c:if test="${count == numberToDisplay}">
		<c:set var="tooltip" value='${tooltip}</b>' scope="page" />
		<c:set var="tooltip" value="${tooltip}${var}, " scope="page" />
	</c:if>
	<c:if test="${count > numberToDisplay}">
		<c:set var="tooltip" value="${tooltip}${var}, " scope="page" />
	</c:if>
	<c:set var="count" value="${count + 1}" scope="page" />
</c:forEach>

<c:if test="${displayList.size() >= numberToDisplay }">
	<a href="#" class="tooltip" title='${tooltip}'>...</a>
</c:if>
