<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: ilinca
  Date: 22/11/2016
  Time: 12:34
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h4>Procedures that can lead to relevant phenotype associations</h4>
	
<div>
    <c:if test="${not empty procedures}">

        <h6>Young Adult:</h6>
        <ul>
            <c:set var="count" value="0" scope="page"/>
            <c:set var="lastProcedure" value="" scope="page"/>

            <c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
                <c:if test="${lastProcedure.equalsIgnoreCase(procedure.getProcedureName())}">
                    <c:if test="${!lastProcedure.contains('Embryo') && !lastProcedure.contains('Gross Morphology')}">
                        <a href="${drupalBaseUrl}/impress/protocol/${procedure.procedureStableKey}">
                                , ${procedure.procedureStableId.split("_")[0]} v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())}
                        </a>
                    </c:if>
                </c:if>
                <c:if test="${!lastProcedure.equalsIgnoreCase(procedure.getProcedureName())}">
                    <c:if test="${! procedure.procedureName.contains('Embryo') && ! procedure.procedureName.contains('Gross Morphology')}">
                        </li>
                        <li>
                            ${procedure.procedureName}
                            <a href="${drupalBaseUrl}/impress/protocol/${procedure.procedureStableKey}">
                                 ${procedure.procedureStableId.split("_")[0]} v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())}
                            </a>
                    </c:if>
                </c:if>
                <c:set var="lastProcedure" value="${procedure.getProcedureName()}" scope="page"/>

            </c:forEach>
            
        </ul>
     
        <p></p>

        <c:if test="${! adultOnly}">
        <h6>Embryo:</h6>
        <ul>
            <c:set var="count" value="0" scope="page"/>
            <c:set var="lastProcedure" value="" scope="page"/>

            <c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
                <c:if test="${lastProcedure.equalsIgnoreCase(procedure.getProcedureName())}">
                    <c:if test="${lastProcedure.contains('Embryo') || lastProcedure.contains('Gross Morphology')}">
                        <a href="${drupalBaseUrl}/impress/protocol/${procedure.procedureStableKey}">
                            , ${procedure.procedureStableId.split("_")[0]} v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())}
                        </a>
                    </c:if>
                </c:if>
                <c:if test="${!lastProcedure.equalsIgnoreCase(procedure.getProcedureName())}">
                    <c:if test="${procedure.procedureName.contains('Embryo') || procedure.procedureName.contains('Gross Morphology')}">
                    </li>
                    <li>
                            ${procedure.procedureName}
                        <a href="${drupalBaseUrl}/impress/protocol/${procedure.procedureStableKey}">
                                ${procedure.procedureStableId.split("_")[0]} v${procedure.procedureStableId.substring(procedure.procedureStableId.length()-1, procedure.procedureStableId.length())}
                        </a>
                    </c:if>
                </c:if>
                <c:set var="lastProcedure" value="${procedure.getProcedureName()}" scope="page"/>

            </c:forEach>
        </ul>
        </c:if>


    </c:if>

</div>
