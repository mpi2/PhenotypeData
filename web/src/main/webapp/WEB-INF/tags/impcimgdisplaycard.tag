<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="img" required="true" type="java.util.Map" %>
<%@ attribute name="impcMediaBaseUrl" required="true" %>
<%@ attribute name="pdfThumbnailUrl" required="false" %>
<%@ attribute name="count" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="category" required="false" %>
<%@ attribute name="parameterName" required="false" %>
<%@ attribute name="procedureName" required="false" %>
<!-- href specified as arg to tag as in the case of gene page to image picker links -->
<!-- pdf annotation not image -->
<!-- defaults to image -->

<c:set var="target_url" value="${href}" />

<div class="col mb-4 col-6 col-md-4 col-lg-3">
    <div class="card">
<c:choose>

    <%-- When we have a link to an image, but it hasn't been loaded into OMERO (PDFs for instance--%>
    <c:when test="${not empty href and img.omero_id != '-1'}">

        <!-- href specified as arg to tag as in the case of gene page to image picker links -->
        <c:if test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
            <!-- if this image is a pdf on the gene page we want to link to a list view of the pdfs for that gene not the image picker -->
            <div class="text-center" >
                <c:set var="target_url" value="${href}&mediaType=pdf" />
                <a href="${target_url}" class="text-dark">
                    <svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false">
                        <title>PDF thumbnail</title>
                        <rect width="100%" height="100%" fill="#6c757d"></rect>
                        <text dominant-baseline="middle" text-anchor="middle" x="50%" y="50%" fill="#dee2e6" dy=".3em">PDF</text>
                    </svg>
                </a>
            </div>
        </c:if>

        <!-- if has no annotation in string then not a pdf -->
        <c:if test="${!fn:containsIgnoreCase(img.download_url, 'annotation') and img.omero_id != '-1'}"> <!-- if has no annotation in string then not a pdf -->
            <div class="card-img-top img-fluid">
                <a href="${target_url}" class="text-dark">
                    <img src="${img.thumbnail_url}" class="card-img-top img-fluid" /></a>
            </div>
        </c:if>

    </c:when>

    <%-- When omero id is missing and the files is a PDF file--%>
    <c:when test="${fn:containsIgnoreCase(img.download_url, 'annotation') and img.omero_id != '-1'}">
        <!-- used pdf images on normal image scrolldown pages -->
        <div class="card-img-top img-fluid text-center">
            <c:set var="target_url" value="${img.download_url}" />
            <a href="${target_url}" class="text-dark">
                <svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false">
                    <title>PDF thumbnail</title>
                    <rect width="100%" height="100%" fill="#6c757d"></rect>
                    <text dominant-baseline="middle" text-anchor="middle" x="50%" y="50%" fill="#dee2e6" dy=".3em">PDF</text>
                </svg>
            </a>
        </div>
    </c:when>

    <%-- FCS file--%>
    <c:when test="${(fn:containsIgnoreCase(img.file_type, 'octet-stream') or fn:containsIgnoreCase(img.file_type, 'fcs')) and img.omero_id == '-1'}">
        <!-- used fcs images on normal image scrolldown pages -->
        <div class="card-img-top img-fluid text-center">
            <c:set var="target_url" value="${baseUrl}/impcImages/download?acc=${img.gene_accession_id}&parameter_stable_id=${img.parameter_stable_id}" />
            <a href="${target_url}" class="text-dark">
                <svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false">
                    <title>FCS thumbnail</title>
                    <rect width="100%" height="100%" fill="#6c757d"></rect>
                    <text dominant-baseline="middle" text-anchor="middle" x="50%" y="50%" fill="#dee2e6" dy=".3em">FCS</text>
                </svg>
            </a>
        </div>
    </c:when>

    <%-- When the image has not been loaded into OMERO--%>
    <c:when test="${img.omero_id == '-1'}">
        <!-- Use generic thumbnail when image is missing -->
        <div class="card-img-top img-fluid text-center">
            <c:set var="target_url" value="${baseUrl}/impcImages/download?acc=${img.gene_accession_id}&parameter_stable_id=${img.parameter_stable_id}" />
            <a href="${target_url}" class="text-dark">
                <svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false">
                    <title>Download thumbnail</title>
                    <rect width="100%" height="100%" fill="#6c757d"></rect>
                    <text dominant-baseline="middle" text-anchor="middle" x="50%" y="50%" fill="#dee2e6" dy=".3em">Download</text>
                </svg>
            </a>
        </div>
    </c:when>

</c:choose>

        <div class="card-body">
            <h5 class="card-title">${procedureName}</h5>
            <p class="card-text">${parameterName}</p>
            <p><small class="text-muted">${count} Images</small></p>
        </div>

        <div class="card-footer p-0">
            <a class="btn btn-primary btn-block" href="${target_url}">View images</a>
        </div>
    </div>
</div>

