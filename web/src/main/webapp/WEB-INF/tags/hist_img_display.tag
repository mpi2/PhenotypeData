<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<%@ attribute name="img" required="true" type="java.util.Map" %>
<%@ attribute name="impcMediaBaseUrl" required="true" %>
<%@ attribute name="pdfThumbnailUrl" required="false" %>
<%@ attribute name="count" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="category" required="false" %>
<%@ attribute name="parameterName" required="false" %>

    <!-- href specified as arg to tag as in the case of gene page to image picker links -->
    <!-- pdf annotation not image -->
    <!-- defaults to image -->
   

   <a href="${img.jpeg_url}" class="fancybox" fullRes="${img.jpeg_url}" original="${img.download_url}">
         		<img  src="${img.thumbnail_url}" class="thumbnailStyle"></a>
         		<div class="caption" style="height:100px;width:100px; overflow:auto;word-wrap: break-word;">
         		
         		 	<c:if test="${not empty img.marker_symbol}"><a href="${baseUrl}/genes/${img.gene_accession_id}">${img.marker_symbol}</a><br/></c:if>
         		 	<c:if test="${not empty img.zygosity}">${img.zygosity}<br/></c:if>
                        <c:if test="${not empty count}">${count} Images<br/></c:if>
                        <c:if test="${not empty img.parameter_association_name}">
                            <c:forEach items="${img.parameter_association_name}" varStatus="status">
                                <c:out value="${img.parameter_association_name[status.index]}"/>
                                <c:out value="${img.parameter_association_value[status.index]}"/>
                                <br/>
                            </c:forEach>
                        </c:if>
                        <c:if test="${not empty img.anatomy_id}">
                            <c:forEach items="${img.anatomy_id}" varStatus="status">
                                <c:out value="${img.anatomy_id[status.index]}"/>
                                <c:out value="${img.anatomy_term[status.index]}"/>
                                <br/>
                            </c:forEach>
                        </c:if>

                        <c:if test="${not empty img.mp_id}">
                            <c:forEach items="${img.mp_id}" varStatus="status">
                                <c:out value="${img.mp_id[status.index]}"/>
                                <c:out value="${img.mp_term[status.index]}"/>
                                <br/>
                            </c:forEach>
                        </c:if>

                        <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
                        <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group }</c:if>
                                               
                </div>

         		
            



