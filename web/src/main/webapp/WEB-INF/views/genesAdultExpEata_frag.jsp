<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="expressionIcon" scope="page" value="fa fa-circle"/>
<c:set var="noTissueIcon" scope="page" value="fa fa-times"/>
<c:set var="noExpressionIcon" scope="page" value="fa fa-circle-o"/>
<c:set var="ambiguousIcon" scope="page" value="fa fa-adjust"/>
<c:set var="yesColor" scope="page" value="text-primary"/>
<c:set var="noColor" scope="page" value="text-info"/>
<c:set var="amColor" scope="page" value="text-warning"/>
<c:set var="noAvaColor" scope="page" value="text-danger"/>

<div class="container p-0 p-md-2">
    <div class="row justify-content-center">
        <span title="Expression" class="${yesColor} mr-3"><i class="${expressionIcon}"></i>&nbsp;Expression</span>
        <span title="No Expression" class="${noColor} mr-3"> <i class="${noExpressionIcon}"></i>&nbsp;No Expression</span>
        <span title="Ambiguous" class="${amColor} mr-3"><i class="${ambiguousIcon}"></i>&nbsp;Ambiguous</span>
        <span title="No Tissue Available" class="${noAvaColor}"><i class="${noTissueIcon}"></i>&nbsp;No Tissue Available</span>
    </div>

    <div class="row justify-content-center">
        <div class="col-sm-12">
            <table id="expressionTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true" data-sort-name="images">
                <thead>
                <th data-sortable="true">Anatomy</th>
                <th data-field="images" data-sortable="true">Images</th>
                <th title="">Mutant Expr</th>
                <th data-width="100px" data-halign="right">WT Expr</th>
                </thead>
                <tbody>
                <c:forEach var="mapEntry" items="${expressionAnatomyToRow}">
                    <tr>
                        <td><span>${mapEntry.value.abnormalAnatomyName}</span></td>

                        <td>
                            <c:if
                                    test="${mutantImagesAnatomyToRow[mapEntry.key].wholemountImagesAvailable}">
                                <!-- imageComparator?acc=MGI:1859162&anatomy_term=respiratory%20system&parameter_stable_id=IMPC_ALZ_075_001 -->
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
                                    <span>&nbsp<fmt:formatNumber pattern="#.##">${fn:length(mapEntry.value.specimenExpressed) / fn:length(mapEntry.value.specimen) * 100}</fmt:formatNumber>% (${fn:length(mapEntry.value.specimenExpressed)} of ${fn:length(mapEntry.value.specimen)})</span>
                                </c:when>
                                <c:when test="${mapEntry.value.notExpressed}">
                                    <span>0.0% (0 of ${fn:length(mapEntry.value.specimen)})</span>
                                </c:when>
                                <c:when test="${mapEntry.value.noTissueAvailable}">
                                    <span class="${noTissueIcon} ${noAvaColor}"></span>
                                </c:when>

                                <c:otherwise>
                                    <span class="${ambiguousIcon} ${amColor}"></span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td data-align="right">
                            <c:choose>
                                <c:when test="${wtAnatomyToRow[mapEntry.key].expression}">
                                    <fmt:formatNumber pattern="#.##">${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed) / fn:length(wtAnatomyToRow[mapEntry.key].specimen) * 100}</fmt:formatNumber>%
                                </c:when>
                                <c:when test="${wtAnatomyToRow[mapEntry.key].notExpressed}">
                                    0.0%
                                </c:when>
                                <c:when test="${wtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                                    Unavailable
                                </c:when>

                                <c:otherwise>
                                    <span title="Ambiguous" class="${ambiguousIcon} ${amColor}"></span>
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


