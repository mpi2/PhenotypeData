<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="org.apache.commons.text.WordUtils" %>

<%-- EXPRESSION DATA TABLE --%>
<div id="adult-expression-table" class="row justify-content-center">
    <div class="container p-0 p-md-2">
        <div class="row justify-content-center">
            <div class="col-sm-12">
                <table id="expressionTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true" data-sort-name="images">
                    <thead>
                    <th data-sortable="true">Anatomy</th>
                    <th data-field="images" data-sortable="true">Images</th>
                    <th title="">Mutant Expr</th>
                    <%--                <th data-width="100px" data-halign="right">WT Expr</th>--%>
                    </thead>
                    <tbody>
                    <c:forEach var="mapEntry" items="${expressionAnatomyToRow}">
                        <tr>
                            <td><span>${mapEntry.value.abnormalAnatomyName}</span></td>

                            <td>
                                <c:if test="${mutantImagesAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                                    <a
                                            href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ALZ_076_001'
                                            class="mr-1" style="font-size: small"><i
                                            title="Wholemount Images available (click on this icon to view images)"
                                            class="fa fa-image"
                                            alt="Images"></i>&nbsp;Wholemount images
                                    </a>
                                </c:if>
                                <c:if
                                        test="${mutantImagesAnatomyToRow[mapEntry.key].sectionImagesAvailable}">
                                    <a
                                            href='${baseUrl}/imageComparator?acc=${acc}&anatomy_id=${mapEntry.value.abnormalAnatomyId}&parameter_stable_id=IMPC_ALZ_075_001'
                                            class="mr-1" style="font-size: small"><i
                                            title="Section Images available (click on this icon to view images)"
                                            class="fa fa-image"
                                            alt="Images"></i>&nbsp;Section images
                                    </a>
                                </c:if>
                                <c:if test="${not mutantImagesAnatomyToRow[mapEntry.key].sectionImagesAvailable and not mutantImagesAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                                <span>
                                    N/A
                                </span>
                                </c:if>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${mapEntry.value.expression}">
                                        <span><fmt:formatNumber pattern="#.##">${fn:length(mapEntry.value.specimenExpressed) / fn:length(mapEntry.value.specimen) * 100}</fmt:formatNumber>% (${fn:length(mapEntry.value.specimenExpressed)} of ${fn:length(mapEntry.value.specimen)})</span>
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

                                <%--                        <td data-align="right">--%>
                                <%--                            <c:choose>--%>
                                <%--                                <c:when test="${wtAnatomyToRow[mapEntry.key].expression}">--%>
                                <%--                                    <fmt:formatNumber pattern="#.##">${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed) / fn:length(wtAnatomyToRow[mapEntry.key].specimen) * 100}</fmt:formatNumber>%--%>
                                <%--                                </c:when>--%>
                                <%--                                <c:when test="${wtAnatomyToRow[mapEntry.key].notExpressed}">--%>
                                <%--                                    0.0%--%>
                                <%--                                </c:when>--%>
                                <%--                                <c:when test="${wtAnatomyToRow[mapEntry.key].noTissueAvailable}">--%>
                                <%--                                    Unavailable--%>
                                <%--                                </c:when>--%>

                                <%--                                <c:otherwise>--%>
                                <%--                                    <span title="Ambiguous" class="${ambiguousIcon} ${amColor}"></span>--%>
                                <%--                                </c:otherwise>--%>
                                <%--                            </c:choose>--%>
                                <%--                        </td>--%>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%--EXPRESSION IMAGES--%>
<div id="adult-expression-images" class="mt-3">
    <%--ACCORDION--%>
    <div class="accordion expressionAccordion" id="expressionAccordion">
        <div>
            <div id="adultHeadingWholemount">
                <h4 class="accordion-title" data-toggle="collapse" data-target="#adultWholemountImages" aria-expanded="true" aria-controls="adultWholemountImages">
                    LacZ Wholemount Images (${fn:length(wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms)} tissue<c:if test="${fn:length(wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms) != 1}">s</c:if>)
                    <i class="far fa-plus float-right"></i>
                </h4>
            </div>
            <div id="adultWholemountImages" class="collapse" aria-labelledby="adultHeadingWholemount" data-parent="#expressionAccordion">
                <c:choose>
                    <c:when test="${fn:length(wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms) < 1}">
                        <div class="alert alert-warning">
                            <p class="mb-0">There are no <b>LacZ Wholemount images</b> for <b>${gene.markerSymbol}</b></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">

                            <c:forEach var="entry" items="${wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms}" varStatus="status">
                                <c:set var="image_url"
                                       scope="page"
                                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ALZ_076_001" />
                                <c:set var="img"
                                       value="${wholemountExpressionImagesBean.expFacetToDocs[entry.name][0]}" />
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
        <div>
            <div id="adultHeadingSection">
                <h4 class="accordion-title" data-toggle="collapse" data-target="#adultSectionImages" aria-expanded="true" aria-controls="adultSectionImages">
                    LacZ Section Images (${fn:length(sectionExpressionImagesBean.filteredTopLevelAnatomyTerms)} tissue<c:if test="${fn:length(sectionExpressionImagesBean.filteredTopLevelAnatomyTerms) != 1}">s</c:if>)
                    <i class="far fa-plus float-right"></i>
                </h4>
            </div>
            <div id="adultSectionImages" class="collapse" aria-labelledby="adultHeadingSection" data-parent="#expressionAccordion">
                <c:choose>
                    <c:when test="${fn:length(sectionExpressionImagesBean.filteredTopLevelAnatomyTerms) < 1}">
                        <div class="alert alert-warning">
                            <p class="mb-0">There are no <b>LacZ Section images</b> for <b>${gene.markerSymbol}</b></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="entry" items="${sectionExpressionImagesBean.filteredTopLevelAnatomyTerms}" varStatus="status">
                                <c:set var="image_url"
                                       scope="page"
                                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ALZ_076_001"></c:set>
                                <c:set var="img"
                                       value="${sectionExpressionImagesBean.expFacetToDocs[entry.name][0]}"></c:set>
                                <div class="col mb-4 col-6 col-md-4 col-lg-3">
                                    <div class="card">
                                        <img src="${img.thumbnail_url}" class="card-img-top" alt="${entry.name} thumbnail image">
                                        <div class="card-body">
                                            <c:set var="tissue_name" value="${WordUtils.capitalize(entry.name)}" />
                                            <c:if test="${entry.name == 'Unassigned Top Level MA'}">
                                                <c:set var="tissue_name" value="Unassigned tissue" />
                                            </c:if>
                                            <h5 class="card-title">${tissue_name} (${entry.count})</h5>                                <p class="card-text">
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
            <div id="adultHeadingOther">
                <h4 class="accordion-title" data-toggle="collapse" data-target="#adultOtherImages" aria-expanded="true" aria-controls="adultOtherImages">
                    Other associated LacZ Section Images (${fn:length(expressionFacets)} tissue<c:if test="${fn:length(expressionFacets) != 1}">s</c:if>)
                    <i class="far fa-plus float-right"></i>
                </h4>
            </div>
            <div id="adultOtherImages" class="collapse" aria-labelledby="adultHeadingOther" data-parent="#expressionAccordion">
                <c:choose>
                    <c:when test="${fn:length(expressionFacets) < 1}">
                        <div class="alert alert-warning">
                            <p class="mb-0">There are no <b>Other associated LacZ Section Images</b> for <b>${gene.markerSymbol}</b></p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="entry" items="${expressionFacets}" varStatus="status">
                                <c:set var="image_url"
                                       scope="page"
                                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ALZ_076_001"></c:set>
                                <c:set var="img"
                                       value="${expFacetToDocs[entry.name][0]}"></c:set>
                                <div class="col mb-4 col-6 col-md-4 col-lg-3">
                                    <div class="card">
                                        <img src="${mediaBaseUrl}/${img.smallThumbnailFilePath}" class="card-img-top" alt="${entry.name} thumbnail image">
                                        <div class="card-body">
                                            <c:set var="tissue_name" value="${WordUtils.capitalize(entry.name)}" />
                                            <h5 class="card-title">${tissue_name} (${entry.count})</h5>
                                            <p class="card-text">
                                                <c:if test="${not empty img.annotationTermName}">
                                                    <c:forEach var="maTerm" items="${img.annotationTermName}"
                                                               varStatus="status">${WordUtils.capitalize(maTerm)}<c:if test="${!status.last}">, </c:if> </c:forEach>
                                                </c:if></p>
                                        </div>
                                        <div class="card-footer p-0">
                                            <a class="btn btn-primary btn-block" href='${baseUrl}/images?gene_id=${acc}&fq=sangerProcedureName:"Wholemount Expression"&fq=selected_top_level_ma_term:"${entry.name}"'>View images</a>
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
