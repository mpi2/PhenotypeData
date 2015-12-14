<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<html>
	<header>
	</header>
		<body>
Staus information should be here!
				<ol>
					<c:forEach var="status" items="${webStatusModels}">
				
						<li>${status.name} : ${status.number}</li>
					
					</c:forEach>
				</ol>
		</body>
</html>