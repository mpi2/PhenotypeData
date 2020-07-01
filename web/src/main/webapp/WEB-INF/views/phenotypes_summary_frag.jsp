<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="row">
    <c:if test="${not empty procedures}">
    <div class="col-12">
        <table id="proceduresTable" class="table dt-responsive">
            <thead>
            <tr>
                <th class="headerSort">Procedure</th>
            </tr>
            </thead>
            <tbody>
            <c:set var="count" value="0" scope="page"/>
            <c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
                <c:set var="count" value="${count+1}"/>
                <c:set var="hrefVar" value="http://www.mousephenotype.org/impress/protocol/${procedure.procedureStableKey}"/>
                <c:if test="${fn:contains(procedure.procedureStableId,'M-G-P')}">
                    <c:set var="hrefVar"
                           value="http://www.mousephenotype.org/impress/parameters/${procedure.procedureStableKey}/4"/>
                </c:if>
                <tr>
                    <td>
                        <a href="${hrefVar}">${procedure.procedureName}</a>
                    </td>
                </tr>
            </c:forEach>

            </tbody>
        </table>
    </div>
    </c:if>
</div>