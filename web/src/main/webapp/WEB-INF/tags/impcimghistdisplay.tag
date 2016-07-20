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
<%@ attribute name="img" required="true" type="java.util.Map"%>
<%@ attribute name="impcMediaBaseUrl" required="true" %>
     
        
         <!-- used for lacz expression pages -->
         		<a href="${impcMediaBaseUrl}/render_image/${img.omero_id}/" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}/" original="${impcMediaBaseUrl}/archived_files/download/${img.omero_id}/">
         		<img  src="${impcMediaBaseUrl}/render_birds_eye_view/${img.omero_id}/" class="thumbnailStyle"></a>
         		<div class="caption" style="height:100px;width:100px; overflow:auto;word-wrap: break-word;">
     
                                                <c:if test="${not empty img.gene_symbol}"><a href="${baseUrl}/genes/${img.gene_accession_id}">${img.gene_symbol}</a><br/></c:if>
                                                <c:if test="${not empty category}"><a href="${href}">${category}</a><br/></c:if>
                                                <c:if test="${not empty img.image_link}"><a href="${img.image_link}" target="_blank">Original Image</a><br/></c:if>
                                                <c:if test="${not empty img.zygosity}">${img.zygosity}<br/></c:if>
                                                <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach items="${img.parameter_association_name}" varStatus="status">
                                                		<c:out value="${img.parameter_association_name[status.index]}"/>
                                                		<c:out value="${img.parameter_association_value[status.index]}"/>
                                                		<br/>
                                                	</c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.ma_id}">
                                                	<c:forEach items="${img.ma_id}" varStatus="status">
                                                		<c:out value="${img.ma_id[status.index]}"/>
                                                		<c:out value="${img.ma_term[status.index]}"/>
                                                		<br/>
                                                	</c:forEach>
                                                </c:if>
                                             
                                                 <c:if test="${not empty img.emap_id}">
                                                	<c:forEach items="${img.emap_id}" varStatus="status">
                                                		<c:out value="${img.emap_id[status.index]}"/>
                                                		<c:out value="${img.emap_term[status.index]}"/>
                                                		<br/>
                                                	</c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>

                                                </div>

