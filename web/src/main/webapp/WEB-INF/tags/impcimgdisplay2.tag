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
<div class="col-sm-6 col-md-3">
    <!-- href specified as arg to tag as in the case of gene page to image picker links -->
    <!-- pdf annotation not image -->
    <!-- defaults to image -->
    <c:set var="imgStyle" scope="page" value="max-width: 200px; max-height: 200px;width:auto;height:auto;"/>

    <c:choose>

        <c:when test="${not empty href}">
            <!-- href specified as arg to tag as in the case of gene page to image picker links -->
            <c:if test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
                <!-- if this image is a pdf on the gene page we want to link to a list view of the pdfs for that gene not the image picker -->
    <div class="thumbnail">
        <a href="${href}&mediaType=pdf">
            <img src="../${pdfThumbnailUrl}">
        </a>
    </div>
            </c:if>

            <c:if test="${!fn:containsIgnoreCase(img.download_url, 'annotation') }"> <!-- if has no annotation in string then not a pdf -->
    <div class="thumbnail">
                <a href="${href}">
                    <img src="${img.thumbnail_url}"/></a>
    </div>
            </c:if>
            <div class="caption">

        </c:when>

        <c:when test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
        <!-- used pdf images on normal image scrolldown pages -->
    <div class="thumbnail">
            <a href="${img.download_url}">
                <img src="../${pdfThumbnailUrl}">
            </a>
    </div>
            <div class="caption">
            <c:if test="${not empty img.external_sample_id}">sample id: ${img.external_sample_id}<br/></c:if>
            <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}<br/></c:if>
            <c:if test="${not empty img.date_of_experiment}">Exp.date: ${img.date_of_experiment}<br/></c:if>
         </c:when>

         <c:otherwise>
            <!-- used for lacz expression pages -->
                <div class="thumbnail">
            <a href="${img.jpeg_url}" class="fancybox" fullRes="${img.jpeg_url}" original="${img.download_url}">
                <img src="${img.thumbnail_url}">
            </a>
                </div>
            <div class="caption">
          </c:otherwise>
    </c:choose>


    <c:if test="${empty count}">
        <!-- if there is a count then it's from the gene page Phenotype Associations section and then we dont want to display links to the gene page we are already on -->
        <c:if test="${not empty img.gene_symbol}"><a
                            href="${baseUrl}/genes/${img.gene_accession_id}">${img.gene_symbol}</a><br/></c:if>
    </c:if>


    <c:if test="${not empty category}"><a href="${href}">${category}</a><br/></c:if>
    <c:if test="${not empty img.image_link}"><a href="${img.image_link}" target="_blank">Original Image</a><br/></c:if>

                <c:choose>
                    <c:when test="${not empty parameterName }"><b style="word-wrap: normal">${parameterName}</b><br/>
                        <c:if test="${not empty count}">${count} Images<br/></c:if>
                    </c:when>

                    <c:otherwise>
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
                    </c:otherwise>
                </c:choose>

            </div>


</div>
