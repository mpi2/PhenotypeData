<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
<head>
</head>
<body>
Status OK:${ok}<br/>

<p><b>Cache</b> hits: ${hits}, misses: ${misses}, ratio: ${ratio} <a href="clearCache">clear</a></p>

<p>Cores Services Statuses:</p>
<ol>
    <c:forEach var="status" items="${webStatusModels}">
        <li<c:if test="${status.number==0}"> style="color:red;"</c:if>>${status.name} : ${status.number}</li>
    </c:forEach>
</ol>

<p>Non Essential Statuses:</p>
<ol>
    <c:forEach var="status" items="${nonEssentialWebStatusModels}">
        <li<c:if test="${status.number==0}"> style="color:red;"</c:if>>${status.name} : ${status.number}</li>
    </c:forEach>
</ol>


</body>
</html>
