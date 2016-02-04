<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<html>
<body>
<br/>
<br/>
<br/>
Male Colors
<table>
<c:forEach var="maleColor" items="${maleColors}">
<tr><td>${maleColor}<td/><td style="background-color: rgb(${maleColor})">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><tr/>
</c:forEach>
</table>
<br/>
<br/>
<br/>

Female Colors
<table>
<c:forEach var="femaleColor" items="${femaleColors}">
<tr><td>${femaleColor}<td/><td style="background-color: rgb(${femaleColor})">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><tr/>
</c:forEach>
</table>
<br/>
<br/>
<br/>
High Difference Colors
<table>
<c:forEach var="highDifferenceColor" items="${highDifferenceColors}">
<tr><td>${highDifferenceColor}<td/><td style="background-color: rgb(${highDifferenceColor})">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><tr/>
</c:forEach>
</table>
</body>
</html>