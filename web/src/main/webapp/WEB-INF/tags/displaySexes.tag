<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="sexes" required="true" type="java.util.List"%>

            <c:set var="containsBoth" value="false"/>
            <c:forEach var="sex" items="${sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
            	<c:if test="${sex=='both'}">
            		<c:set var="containsBoth" value="true"/>
            	</c:if>
            </c:forEach>
            <%-- ${containsBoth} --%>
             <c:choose>
                	<c:when test="${containsBoth}">
                		<span class="bothSexes"> <img alt="Female" src="${baseUrl}/img/female.jpg"/> <img alt="Male" src="${baseUrl}/img/male.jpg"/> </span>
                	</c:when>
                	<c:otherwise>
			                <c:set var="count" value="0" scope="page"/>
			                <c:forEach var="sex" items="${sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
			               			<c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
			                        <img alt="Female" src="${baseUrl}/img/female.jpg"/></c:if><c:if test="${sex == 'male'}"><c:set
			                            var="europhenome_gender" value="Male"/><img alt="Male" src="${baseUrl}/img/male.jpg"/>
			                    	</c:if>
			                	 	
			               	</c:forEach>
                	
                	</c:otherwise>
             </c:choose>