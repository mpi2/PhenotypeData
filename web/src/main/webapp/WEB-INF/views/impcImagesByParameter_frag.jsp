<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 19:53
  To change this template use File | Settings | File Templates.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<!-- IMPC Phenotype Associated Images -->
<%--<div class="section">--%>
<%--<h2 class="title" id="section-impc-images">IMPC Phenotype Associated Images<span--%>
<%--class="documentation"><a href='' id='impcImagesPanel'--%>
<%--class="fa fa-question-circle pull-right"></a></span>--%>
<%--</h2>--%>

<div class="accordion-body" style="display: block">
    <div id="grid">
        <c:forEach var="group" items="${impcImageGroups}" varStatus="status">


            <c:forEach var="doc"
                       items="${group.result}">
                <ul>

                    <c:set var="label" value="${doc.procedure_name}: ${doc.parameter_name}"/>
                    <c:if test="${doc.parameter_name eq 'Images'}">
                        <c:set var="label" value="${doc.procedure_name}"/>
                    </c:if>
                        <%-- (${entry.count}) --%>
                    <c:choose>
                        <c:when test="${doc.omero_id == '0' && acc != null}"><!-- these are secondary project images so compara image view won't work on them -->
                            <!-- http://localhost:8080/phenotype-archive/impcImages/images?q=*:*%20AND%20observation_type:image_record&qf=imgQf&defType=edismax&fq=procedure_name:%22Brain%20Histopathology%22 -->
                            <c:set var="href" scope="page"
                                   value="${baseUrl}/impcImages/images?q=gene_accession_id:${acc}&fq=parameter_stable_id:${doc.parameter_stable_id}"></c:set>
                        </c:when>
                        <c:when test="${doc.omero_id == '0' && phenotype_id != null}"><!-- these are secondary project images so compara image view won't work on them -->
                            <!-- http://localhost:8080/phenotype-archive/impcImages/images?q=*:*%20AND%20observation_type:image_record&qf=imgQf&defType=edismax&fq=procedure_name:%22Brain%20Histopathology%22 -->
                            <c:set var="href" scope="page"
                                   value='${baseUrl}/impcImages/images?q=mp_id:${phenotype_id}&fq=parameter_stable_id:${doc.parameter_stable_id}'></c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="href" scope="page"
                                   value="${baseUrl}/imageComparator?acc=${acc}&mp_id=${phenotype_id}&parameter_stable_id=${doc.parameter_stable_id}"></c:set>

                        </c:otherwise>
                    </c:choose>

                    <a href="${href}">
                        <t:impcimgdisplay2
                                img="${doc}"
                                impcMediaBaseUrl="${impcMediaBaseUrl}"
                                pdfThumbnailUrl="${pdfThumbnailUrl}"
                                href="${href}"
                                count="${paramToNumber[doc.parameter_stable_id]}"
                                parameterName="${label}"></t:impcimgdisplay2>
                    </a>


                        <%--  <div class="clear"></div>
                            <c:if test="${entry.count>5}">
                                <p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
                            </c:if> --%>

                </ul>
            </c:forEach>


        </c:forEach>
    </div>
</div>


