<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="omeroStaticUrl" value="${fn:replace(impcMediaBaseUrl,'/omero/webgateway', '/static/')}"/>

<t:genericpage>

    <jsp:attribute name="title">${gene.markerSymbol} Image Picker</jsp:attribute>
    <jsp:attribute name="bodyTag"><body class="chartpage no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
        <link rel="canonical" href="https://www.mousephenotype.org/data/imageComparator?${requestScope['javax.servlet.forward.query_string']}" />
		<link href="${omeroStaticUrl}webgateway/css/ome.viewport.css" type="text/css" rel="stylesheet"/>
		<link href="${baseUrl}/css/comparator/comparator.css" rel="stylesheet" type="text/css"/><!-- put after default omero css so we can override -->
		<link href="${omeroStaticUrl}3rdparty/panojs-2.0.0/panojs.css" type="text/css" rel="stylesheet"/>
	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<script src="${omeroStaticUrl}/omeroweb.viewer.min.js" type="text/javascript"></script>
		<script type='text/javascript' src="${baseUrl}/js/comparator/comparator.js?v=${version}"></script>
	</jsp:attribute>

    <jsp:body>
        <div class="container row">
            <div class="col-12 no-gutters">
                <div class="node">

                    <c:set var="jpegUrlThumbWithoutId" value="${impcMediaBaseUrl}/render_birds_eye_view"/>
                    <c:set var="jpegUrlDetailWithoutId" value="${impcMediaBaseUrl}/img_detail"/>
                    <c:set var="pdfWithoutId" value="http:${fn:replace(impcMediaBaseUrl,'webgateway','webclient/annotation')}"/>

                    <!-- Orange header layout -->
                    <div class="container data-heading">
                        <div class="row">
                            <div class="col-12 no-gutters">
                                <h1 class="h1 m-0 d-inline-block">
                                    <b>${procedure}: ${parameter.name}</b>
                                </h1>
                            </div>
                        </div>
                    </div>

                    <div class="container row"
                         style="background-color: white;">
                        <div class="col-12 d-none d-lg-block px-3 py-3">
                            <aside>
                                <a href="/">Home</a> <span class="fal fa-angle-right"></span>
                                <a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
                                <a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> <span
                                    class="fal fa-angle-right"></span>
                                Image comparator
                            </aside>
                        </div>
                    </div>

                    <div class="container row" style="background-color:  white;">

                        <div class="row text-center" style="margin-right: 0 !important; width: 100%;">
                            <div id="control_box" class="col col-6 m-0 px-2">
                                <h3>WT Images</h3>

                                <div id="viewport" class="viewport"></div>

                                <div id="control_annotation" class="annotation">
                                </div>

                                <div class="thumbList pt-2">

                                    <div class="row text-left">

                                        <c:forEach var="img" items="${controls}" varStatus="controlLoop">

                                            <c:choose>
                                                <c:when test="${img.sex eq 'male' }">
                                                    <c:set var="imgSex" value="fal fa-mars"/>
                                                </c:when>
                                                <c:when test="${img.sex eq 'female' }">
                                                    <c:set var="imgSex" value="fal fa-venus"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="imgSex" value="nosex"/>
                                                </c:otherwise>
                                            </c:choose>

                                            <div class="col mb-1 col-4" data-toggle="tooltip" data-placement="top" title="Specimen ID: ${img.externalSampleId}">
                                                <!--  Observation ID: ${img.id} -->
                                                <div class="card image">
                                                    <div class="card-img-top img-fluid text-center">
                                                        <img class="thumb" id="${img.omeroId}"
                                                             data-id="${img.omeroId}"
                                                             src="https:${jpegUrlThumbWithoutId}/${img.omeroId}/" />
                                                    </div>
                                                    <div class="card-body">
                                                        <i class="${imgSex}"></i>
                                                        <c:if test="${not empty img.ageInWeeks}">
                                                        <p><small class="text-muted">${img.ageInWeeks} weeks old</small></p>
                                                        </c:if>
                                                        <c:if test="${img.parameterAssociationName.size() > 0}">
                                                            <p><small class="text-muted">${specimenExpression[img.id]}</small></p>
                                                        </c:if>
                                                    </div>

                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                            <div id="mutant_box" class="col col-6 m-0 px-2">
                                <h3>Mutant Images</h3>
                                <div id="viewport2" class="viewport"></div>
                                <div id="mutant_annotation" class="annotation">
                                </div>

                                <div class="thumbList pt-2">

                                    <div class="row text-left">

                                        <c:forEach var="img" items="${mutants}" varStatus="mutantLoop">

                                            <c:choose>
                                                <c:when test="${img.sex eq 'male' }">
                                                    <c:set var="imgSex" value="fal fa-mars"/>
                                                </c:when>
                                                <c:when test="${img.sex eq 'female' }">
                                                    <c:set var="imgSex" value="fal fa-venus"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="imgSex" value="nosex"/>
                                                </c:otherwise>
                                            </c:choose>

                                            <div class="col mb-1 col-4" data-toggle="tooltip" data-placement="top" title="Specimen ID: ${img.externalSampleId}">
                                                <!--  Observation ID: ${img.id} -->
                                                <div class="card image">
                                                    <div class="card-img-top img-fluid text-center">
                                                        <img class="thumb2" id="${img.omeroId}"
                                                             data-id="${img.omeroId}"
                                                             src="https:${jpegUrlThumbWithoutId}/${img.omeroId}/" />
                                                    </div>
                                                    <div class="card-body">
                                                        <i class="${imgSex}"></i>
                                                        <c:if test="${not empty img.ageInWeeks}">
                                                        <p><small class="text-muted">${img.ageInWeeks} weeks old</small></p>
                                                        </c:if>
                                                        <p><small class="text-muted"><t:formatAllele>${img.alleleSymbol}</t:formatAllele> ${img.zygosity}</small></p>
                                                        <c:if test="${img.parameterAssociationName.size() > 0}">
                                                            <p><small class="text-muted">${specimenExpression[img.id]}</small></p>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>

                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script type='text/javascript'>
            var jpegUrlDetailWithoutId = "${jpegUrlDetailWithoutId}";
            var pdfWithoutId = "${pdfWithoutId}";
            var googlePdf = "//docs.google.com/gview?url=replace&embedded=true";
            var omeroStaticUrl = "${omeroStaticUrl}";
            var acc = "${gene.mgiAccessionId}";
        </script>

    </jsp:body>

</t:genericpage>
