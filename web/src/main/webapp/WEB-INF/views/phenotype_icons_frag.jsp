<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<div class="container" style="min-width: 300px">
    <div class="row no-gutters justify-content-center">View data by physiological system</div>
    <div class="row no-gutters">

        <div class="container text-center text-muted">
            <c:forEach var="i" begin="0" end="3">
                <div class="row no-gutters">
                    <c:forEach var="j" begin="0" end="4">
                        <c:choose>
                            <c:when test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <a class="col-sm btn btn-outline-primary btn-icon significant m-1" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}&${significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}'>
                                    <i class="${phenotypeGroupIcons[i*5 + j]}" title="${gene.markerSymbol} ${phenotypeGroups[i*5 + j]} measurements" data-toggle="tooltip" data-placement="top"></i>
                                </a>
                            </c:when>
                            <c:when test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <a class="col-sm btn btn-outline-info btn-icon m-1" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}&${notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}'>
                                    <i class="${phenotypeGroupIcons[i*5 + j]}" title="${gene.markerSymbol} ${phenotypeGroups[i*5 + j]} measurements" data-toggle="tooltip" data-placement="top"></i>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a class="col-sm btn btn-outline-light btn-icon non-tested disabled m-1">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}" title="${gene.markerSymbol} ${phenotypeGroups[i*5 + j]} measurements" data-toggle="tooltip" data-placement="top"></i>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="row no-gutters font-weight-light text-center" style="font-size: 0.8em;">
        <div class="col text-primary">
            Significant
        </div>
        <div class="col text-info">
            Not Significant
        </div>
        <div class="col text-muted">
            Not tested
        </div>
    </div>
</div>

