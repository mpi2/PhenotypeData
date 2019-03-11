<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- <jsp:doBody var="theBody"/>

<%
String allele = (String) jspContext.getAttribute("theBody");
allele = allele.replaceAll("<", "��");
allele = allele.replaceAll(">", "##");

allele = allele.replaceAll("��", "<sup>");
allele = allele.replaceAll("##", "</sup>");
%>

<%= allele %> --%>
<%--<jsp:doBody var="theBody"/>--%>
<%@ attribute name="img" required="true" type="org.mousephenotype.cda.solr.service.dto.ImageDTO"%>
<%@ attribute name="impcMediaBaseUrl" required="true" %>
     
     
<div class="card">

    <div class="card-img-top img-fluid">
        <a href="${impcMediaBaseUrl}/render_image/${img.omeroId}/" target="_blank"><img
                src="${impcMediaBaseUrl}/render_thumbnail/${img.omeroId}/550" style="max-width: 100%"></a>
    </div>

    <div class="card-body">
	    <c:if test="${img.group eq 'control' }">WT</c:if>
	    <c:if test="${img.group eq 'experimental' }">KO</c:if>
	     <c:if test="${not empty img.zygosity}">${img.zygosity}
	     </c:if>
	      <c:if test="${not empty img.sex}">
	      ${img.sex}
	      </c:if>
	      <c:if test="${not empty img.parameterAssociationName}">
	                                                	<c:forEach items="${img.parameterAssociationName}" varStatus="status">
	                                                		<%-- <c:out value="${img.parameterAssociationName[status.index]}"/> --%>
	                                                		value= %<c:out value="${img.parameterAssociationValue[status.index]}"/>
	                                                		
	                                                	</c:forEach>
	      </c:if>
    </div>
</div>