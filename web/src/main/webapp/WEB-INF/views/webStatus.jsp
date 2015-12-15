<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
	<header>
	</header>
		<body>
Cores Services Statuses:
				<ol>
					<c:forEach var="status" items="${webStatusModels}">
				
						<li>${status.name} : ${status.number}</li>
					
					</c:forEach>
				</ol>				
								
				Imits statuses:
				
				<ol>
					<c:forEach var="status" items="${imitsWebStatusModels}">
				
						<li>${status.name} : ${status.number}</li>
					
					</c:forEach>
				</ol>
				
				
				
				
		</body>
</html>