<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="sexes" required="true" type="java.util.List"%>

            <c:set var="containsBoth" value="false"/>
            <c:forEach var="sex" items="${sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
            	<c:if test="${sex=='not_considered'}">
            		<c:set var="containsBoth" value="true"/>
            	</c:if>
            </c:forEach>
            <%-- ${containsBoth} --%>
             <c:choose>
                	<c:when test="${containsBoth}">
                		<span class="bothSexes" style="font-size: 1.8em;"><i class="fal fa-venus"></i>&nbsp;<i class="fal fa-mars"></i></span>
                	</c:when>
                	<c:otherwise>
			                <c:set var="count" value="0" scope="page"/>
			                <c:forEach var="sex" items="${sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
			               			<c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
										<i class="fal fa-venus" style="font-size: 1.8em;"></i></c:if><c:if test="${sex == 'male'}"><c:set
										var="europhenome_gender" value="Male"/><i class="fal fa-mars" style="font-size: 1.8em;"></i>
			                    	</c:if>
			                	 	
			               	</c:forEach>
                	
                	</c:otherwise>
             </c:choose>