<%--
  Created by IntelliJ IDEA.
  User: ilinca
  Date: 22/11/2016
  Time: 12:34
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="twocolumns">

    <c:if test="${not empty procedures}">
        <h4>Procedures that can lead to relevant phenotype associations</h4>
        <ul>
            <c:set var="count" value="0" scope="page"/>
            <c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
                <li><a href="${drupalBaseUrl}/impress/impress/displaySOP/${procedure.procedureStableKey}">
                        ${procedure.procedureName} (${procedure.procedureStableId.split("_")[0]},
                    v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())})
                </a></li>
            </c:forEach>
        </ul>
    </c:if>

</div>
