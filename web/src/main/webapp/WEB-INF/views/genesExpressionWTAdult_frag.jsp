<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="org.apache.commons.text.WordUtils" %>

<%-- EXPRESSION DATA TABLE --%>
<div id="adult-wt-expression-table" class="row justify-content-center">
    <div class="container p-0 p-md-2">
        <div class="row justify-content-center">
            <div class="col-sm-12">
                <table id="adultWtExpressionTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true">
                    <thead>
                    <th data-sortable="true">Anatomy</th>
                    <th data-width="100px" data-halign="right">Background staining in controls (WT)</th>
                    </thead>
                    <tbody>
                    <c:forEach var="mapEntry" items="${wtAnatomyToRow}">
                        <tr>
                            <td><span>${mapEntry.key}</span></td>

                            <td data-align="right">
                                <c:choose>
                                    <c:when test="${wtAnatomyToRow[mapEntry.key].expression}">
                                        <fmt:formatNumber pattern="#.##">${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed) / fn:length(wtAnatomyToRow[mapEntry.key].specimen) * 100}</fmt:formatNumber>% (${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed)} of ${fn:length(wtAnatomyToRow[mapEntry.key].specimen)})
                                    </c:when>
                                    <c:when test="${wtAnatomyToRow[mapEntry.key].notExpressed}">
                                        0.0%
                                    </c:when>
                                    <c:when test="${wtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                                        Unavailable
                                    </c:when>

                                    <c:otherwise>
                                        <span title="Ambiguous"></span>
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
<%--<div id="adult-wt-expression-images" class="mt-3">--%>
<%--    &lt;%&ndash;ACCORDION&ndash;%&gt;--%>
<%--    <div class="accordion expressionAccordion" id="adultWtExpressionAccordion">--%>
<%--        <div>--%>
<%--            <div id="adultWtHeadingWholemount">--%>
<%--                <h4 class="accordion-title" data-toggle="collapse" data-target="#adultWtWholemountImages" aria-expanded="true" aria-controls="adultWtWholemountImages">--%>
<%--                    Images of background staining in adult WT mice--%>
<%--                    <i class="far fa-plus float-right"></i>--%>
<%--                </h4>--%>
<%--            </div>--%>
<%--            <div id="adultWtWholemountImages" class="collapse" aria-labelledby="adultWtHeadingWholemount" data-parent="#expressionAccordion">--%>
<%--                <c:choose>--%>
<%--                    <c:when test="${fn:length(wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms) < 1}">--%>
<%--                        <div class="alert alert-warning">--%>
<%--                            <p class="mb-0">There are no <b>LacZ Wholemount images</b> for <b>${gene.markerSymbol}</b></p>--%>
<%--                        </div>--%>
<%--                    </c:when>--%>
<%--                    <c:otherwise>--%>
<%--                        <div class="row">--%>

<%--                            <c:forEach var="entry" items="${wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms}" varStatus="status">--%>
<%--                                <c:set var="image_url"--%>
<%--                                       scope="page"--%>
<%--                                       value="${baseUrl}/imageComparator?acc=${acc}&anatomy_term=${entry.name}&parameter_stable_id=IMPC_ALZ_076_001" />--%>
<%--                                <c:set var="img"--%>
<%--                                       value="${wholemountExpressionImagesBean.expFacetToDocs[entry.name][0]}" />--%>
<%--                                <div class="col mb-4 col-6 col-md-4 col-lg-3">--%>
<%--                                    <div class="card">--%>
<%--                                        <img src="${img.thumbnail_url}" class="card-img-top" alt="${entry.name} thumbnail image">--%>
<%--                                        <div class="card-body">--%>
<%--                                            <c:set var="tissue_name" value="${WordUtils.capitalize(entry.name)}" />--%>
<%--                                            <c:if test="${entry.name == 'Unassigned Top Level MA'}">--%>
<%--                                                <c:set var="tissue_name" value="Unassigned tissue" />--%>
<%--                                            </c:if>--%>
<%--                                            <h5 class="card-title">${tissue_name} (${entry.count})</h5>--%>
<%--                                            <p class="card-text">--%>
<%--                                                <c:if test="${not empty img.parameter_association_name}">--%>
<%--                                                    <c:forEach items="${img.parameter_association_name}" varStatus="status">--%>
<%--                                                        <c:out value="${img.parameter_association_name[status.index]}" /><c:if test="${!status.last}">, </c:if>--%>
<%--                                                    </c:forEach>--%>
<%--                                                </c:if></p>--%>
<%--                                        </div>--%>
<%--                                        <div class="card-footer p-0">--%>
<%--                                            <a class="btn btn-primary btn-block" href="${image_url}">View images</a>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </c:forEach>--%>
<%--                        </div>--%>
<%--                    </c:otherwise>--%>
<%--                </c:choose>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</div>--%>
