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
     
        
         <!-- used for lacz expression pages -->
         		<a href="${impcMediaBaseUrl}/render_image/${img.omeroId}/" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omeroId}/" original="${impcMediaBaseUrl}/archived_files/download/${img.omeroId}/">
         		<img  src="${impcMediaBaseUrl}/render_birds_eye_view/${img.omeroId}/" class="thumbnailStyle"></a>
         		<div class="caption" style="height:100px;width:100px; overflow:auto;word-wrap: break-word;">
     
                                                <c:if test="${not empty img.geneSymbol}"><a href="${baseUrl}/genes/${img.geneAccession}">${img.geneSymbol}</a><br/></c:if>
                                                <c:if test="${not empty category}"><a href="${href}">${category}</a><br/></c:if>
                                                <c:if test="${not empty img.imageLink}"><a href="${img.imageLink}" target="_blank">Original Image</a><br/></c:if>
                                                <c:if test="${not empty img.zygosity}">${img.zygosity}<br/></c:if>
                                                <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                <c:if test="${not empty img.parameterAssociationName}">
                                                	<c:forEach items="${img.parameterAssociationName}" varStatus="status">
                                                		<c:out value="${img.parameterAssociationName[status.index]}"/>
                                                		<c:out value="${img.parameterAssociationValue[status.index]}"/>
                                                		<br/>
                                                	</c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.anatomyId}">
                                                	<c:forEach items="${img.anatomyId}" varStatus="status">
                                                		<c:out value="${img.anatomyId[status.index]}"/>
                                                		<c:out value="${img.anatomyId[status.index]}"/>
                                                		<br/>
                                                	</c:forEach>
                                                </c:if>
                                             
                                                <%--  <c:if test="${not empty img.emapId}">
                                                	<c:forEach items="${img.emapId}" varStatus="status">
                                                		<c:out value="${img.emapId[status.index]}"/>
                                                		<c:out value="${img.emapTerm[status.index]}"/>
                                                		<br/>
                                                	</c:forEach>
                                                </c:if> --%>
                                                <c:if test="${not empty img.alleleSymbol}"><t:formatAllele>${img.alleleSymbol}</t:formatAllele><br/></c:if>

                                                </div>

