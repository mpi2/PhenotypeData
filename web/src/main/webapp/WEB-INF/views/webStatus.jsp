<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
	<header>
	</header>
		<body>
		Status OK:${ok}<br/><br/>
Cores Services Statuses:
				<ol>
					<c:forEach var="status" items="${webStatusModels}">
						<c:choose>
							<c:when test="${status.number!=0}">
								<li>${status.name} : ${status.number}</li>
							</c:when>
							<c:otherwise>
							<li><font color="red">${status.name} : ${status.number}</font></li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ol>				
								
				Imits statuses:
				<ol>
					<c:forEach var="status" items="${imitsWebStatusModels}">
				
						
						<c:choose>
							<c:when test="${status.number!=0}">
								<li>${status.name} : ${status.number}</li>
							</c:when>
							<c:otherwise>
							<li><font color="red">${status.name} : ${status.number}</font></li>
							</c:otherwise>
						</c:choose>
					
					</c:forEach>
				</ol>
				
				
				
				
		</body>
</html>