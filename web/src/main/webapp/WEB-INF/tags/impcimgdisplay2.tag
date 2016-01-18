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
<%@ attribute name="pdfThumbnailUrl" required="false" %>
<%@ attribute name="count" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="category" required="false" %>
        <li style="height:275px; max-height:275px; min-height:275px; word-wrap: break-word;width:23%">
         <!-- href specified as arg to tag as in the case of gene page to image picker links -->
         <!-- pdf annotation not image -->
         <!-- defaults to image -->
    <c:choose>

        <c:when test="${not empty href}">
        <!-- href specified as arg to tag as in the case of gene page to image picker links -->
        	<c:if test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
         		<!-- if this image is a pdf on the gene page we want to link to a list view of the pdfs for that gene not the image picker -->
         		 <a href="${href}?mediaType=pdf">
         		<img  src="${pdfThumbnailUrl}/200" style="max-width: 200px; max-height: 200px;"></a>
         	</c:if>
         	<c:if test="${!fn:containsIgnoreCase(img.download_url, 'annotation') }">
         		 <a href="${href}">
         		<img  src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200/" style="max-width: 200px; max-height: 200px;width:auto;height:auto;"></a>
         	</c:if>
         	<div class="caption" style="height:150px; overflow:auto;word-wrap: break-word;">
         </c:when>

         <c:when test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
         <!-- used pdf images on normal image scrolldown pages -->
         		<a href="${img.download_url}" >
         		<img  src="${pdfThumbnailUrl}/200" style="max-height: 200px;"></a>
         		<div class="caption" style="max-width: 200px; max-height: 200px;width:auto;height:auto">
         		 <c:if test="${not empty img.external_sample_id}">sample id: ${img.external_sample_id}<br/></c:if>
                 <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}<br/></c:if>
                 <c:if test="${not empty img.date_of_experiment}">Exp.date: ${img.date_of_experiment}<br/></c:if>
         </c:when>

         <c:otherwise>
         <!-- used for lacz expression pages -->
         		<a href="${impcMediaBaseUrl}/render_image/${img.omero_id}/" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}/" original="${impcMediaBaseUrl}/archived_files/download/${img.omero_id}/">
         		<img  src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200/" style="max-width: 200px; max-height: 200px;width:auto;height:auto"></a>
         		<div class="caption" style="height:150px; overflow:auto;word-wrap: break-word;">
         </c:otherwise>
      </c:choose>
                                                
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
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>

                                                </div>



         </li>
