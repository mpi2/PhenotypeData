<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="org.apache.commons.text.WordUtils" %>

<%-- EXPRESSION DATA TABLE --%>
<div id="embryo-expression-table" class="row justify-content-center">
    <div class="container p-0 p-md-2">
        <div class="row justify-content-center">
            <div class="col-sm-12">
                <table id="expressionTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true" data-sort-name="images">
                    <thead>
                    <th data-sortable="true">Anatomy</th>
                    <th data-field="images" data-sortable="true">Images</th>
                    <th title="">Mutant Expr</th>
                    </thead>
                    <tbody>
                    <c:forEach var="mapEntry" items="${embryoExpressionAnatomyToRow}">
                        <tr>
                            <td><span>${mapEntry.value.abnormalAnatomyName}</span></td>

                            <td>
                                <c:if test="${embryoWtAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                                    <a
                                            href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ELZ_064_001'
                                            class="mr-1" style="font-size: small"><i
                                            title="Wholemount Images available (click on this icon to view images)"
                                            class="fa fa-image"
                                            alt="Images"></i>&nbsp;Wholemount images
                                    </a>
                                </c:if>
                                <c:if
                                        test="${embryoWtAnatomyToRow[mapEntry.key].sectionImagesAvailable}">
                                    <a
                                            href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ELZ_063_001'
                                            class="mr-1" style="font-size: small"><i
                                            title="Section Images available (click on this icon to view images)"
                                            class="fa fa-image"
                                            alt="Images"></i>&nbsp;Section images
                                    </a>
                                </c:if>
                                <c:if test="${not embryoWtAnatomyToRow[mapEntry.key].sectionImagesAvailable and not embryoWtAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                                <span>
                                    N/A
                                </span>
                                </c:if>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${mapEntry.value.expression}">
                                        <span><fmt:formatNumber pattern="#.##">${(fn:length(mapEntry.value.specimenExpressed)+0.0) / fn:length(mapEntry.value.specimen) * 100}</fmt:formatNumber>% (${fn:length(mapEntry.value.specimenExpressed)} of ${fn:length(mapEntry.value.specimen)})</span>
                                    </c:when>
                                    <c:when test="${mapEntry.value.notExpressed}">
                                        <span>0.0% (0 of ${fn:length(mapEntry.value.specimen)})</span>
                                    </c:when>
                                    <c:when test="${mapEntry.value.noTissueAvailable}">
                                        Not available
                                    </c:when>
                                    <c:otherwise>
                                        Ambiguous
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%--EXPRESSION IMAGES--%>
<div id="embryo-expression-images" class="mt-3">
    <%--ACCORDION--%>
    <div class="accordion expressionAccordion" id="embryoExpressionAccordion">
        <div>
            <div id="embryoHeadingWholemount">
                <h4 class="accordion-title" data-toggle="collapse" data-target="#embryoWholemountImages" aria-expanded="true" aria-controls="embryoWholemountImages">
                    LacZ Wholemount Images (${fn:length(wholemountExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms)} tissue<c:if test="${fn:length(wholemountExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms) != 1}">s</c:if>)
                    <i class="far fa-plus float-right"></i>
                </h4>
            </div>
            <div id="embryoWholemountImages" class="collapse" aria-labelledby="embryoHeadingWholemount" data-parent="#embryoExpressionAccordion">
                <c:choose>
                    <c:when test="${fn:length(wholemountExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms) < 1}">
                        <div class="alert alert-warning">
                            <p class="mb-0">There are no <b>Embryo LacZ Wholemount images</b> for <b>${gene.markerSymbol}</b></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="entry" items="${wholemountExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms}" varStatus="status">
                                <c:set var="image_url"
                                       scope="page"
                                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ELZ_064_001" />
                                <c:set var="img"
                                       value="${wholemountExpressionImagesEmbryoBean.expFacetToDocs[entry.name][0]}" />
                                <div class="col mb-4 col-6 col-md-4 col-lg-3">
                                    <div class="card">
                                        <img src="${img.thumbnail_url}" class="card-img-top" alt="${entry.name} thumbnail image">
                                        <div class="card-body">
                                            <c:set var="tissue_name" value="${WordUtils.capitalize(entry.name)}" />
                                            <c:if test="${entry.name == 'Unassigned Top Level MA'}">
                                                <c:set var="tissue_name" value="Unassigned tissue" />
                                            </c:if>
                                            <h5 class="card-title">${fn:replace(tissue_name, 'TS20 ','')} (${entry.count})</h5>
                                            <p class="card-text">
                                                <c:if test="${not empty img.parameter_association_name}">
                                                    <c:forEach items="${img.parameter_association_name}" varStatus="status">
                                                        <c:out value="${img.parameter_association_name[status.index]}" /><c:if test="${!status.last}">, </c:if>
                                                    </c:forEach>
                                                </c:if></p>
                                        </div>
                                        <div class="card-footer p-0">
                                            <a class="btn btn-primary btn-block" href="${image_url}">View images</a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div>
            <div id="embryoHeadingSection">
                <h4 class="accordion-title" data-toggle="collapse" data-target="#embryoSectionImages" aria-expanded="true" aria-controls="embryoSectionImages">
                    LacZ Section Images (${fn:length(sectionExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms)} tissue<c:if test="${fn:length(sectionExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms) != 1}">s</c:if>)
                    <i class="far fa-plus float-right"></i>
                </h4>
            </div>
            <div id="embryoSectionImages" class="collapse" aria-labelledby="embryoHeadingSection" data-parent="#embryoExpressionAccordion">
                <c:choose>
                    <c:when test="${fn:length(sectionExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms) < 1}">
                        <div class="alert alert-warning">
                            <p class="mb-0">There are no <b>Embryo LacZ Section images</b> for <b>${gene.markerSymbol}</b></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="entry" items="${sectionExpressionImagesEmbryoBean.filteredTopLevelAnatomyTerms}" varStatus="status">
                                <c:set var="image_url"
                                       scope="page"
                                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ELZ_063_001"></c:set>
                                <c:set var="img"
                                       value="${sectionExpressionImagesEmbryoBean.expFacetToDocs[entry.name][0]}"></c:set>
                                <div class="col mb-4 col-6 col-md-4 col-lg-3">
                                    <div class="card">
                                        <img src="${img.thumbnail_url}" class="card-img-top" alt="${entry.name} thumbnail image">
                                        <div class="card-body">
                                            <c:set var="tissue_name" value="${WordUtils.capitalize(entry.name)}" />
                                            <c:if test="${entry.name == 'Unassigned Top Level MA'}">
                                                <c:set var="tissue_name" value="Unassigned tissue" />
                                            </c:if>
                                            <h5 class="card-title">${tissue_name} (${entry.count})</h5>
                                            <p class="card-text">
                                            <c:if test="${not empty img.parameter_association_name}">
                                                <c:forEach items="${img.parameter_association_name}" varStatus="status">
                                                    <c:out value="${img.parameter_association_name[status.index]}" /><c:if test="${!status.last}">, </c:if>
                                                </c:forEach>
                                            </c:if></p>
                                        </div>
                                        <div class="card-footer p-0">
                                            <a class="btn btn-primary btn-block" href="${image_url}">View images</a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>


