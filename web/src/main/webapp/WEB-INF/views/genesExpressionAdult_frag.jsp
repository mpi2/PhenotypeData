<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- EXPRESSION DATA TABLE --%>
<div id="adult-expression-table" class="row justify-content-center">
    <div class="container p-0 p-md-2">
        <div class="row justify-content-center">
            <div class="col-sm-12">
                <table id="expressionTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true" data-sort-name="images">
                    <thead>
                    <th data-sortable="true">Anatomy</th>
                    <th data-field="images" data-sortable="true">Images</th>
                    <th>Zygosity</th>
                    <th title="">Mutant Expr</th>
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

                            <td><span>${mapEntry.value.zygosity}</span></td>

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
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
