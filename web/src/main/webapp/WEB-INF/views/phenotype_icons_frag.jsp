<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<div class="container" style="min-width: 300px">
    <div class="row no-gutters">

        <div class="container text-center text-muted"  id="phIconGrid">
            <c:forEach var="i" begin="0" end="3">
                <div class="row no-gutters btn-group-toggle pheno-row" data-toggle="buttons">
                    <c:forEach var="j" begin="0" end="4">
                        <c:choose>
                            <c:when test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <div class="col-sm col-3 tile btn-outline-primary btn-icon significant-tile m-1" href="#phenotypesTab" title="${fn:replace(phenotypeGroups[i*5 + j], ' phenotype', '')}: significant" data-toggle="tooltip">
<%--                                    <input type="checkbox"  autocomplete="off" data-value="${significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}" onchange="filterAllData()" title="${phenotypeGroups[i*5 + j]}" value="significant">--%>
                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>
                                </div>
                            </c:when>
                            <c:when test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <div class="col-sm col-3 tile btn-outline-info btn-icon non-significant-tile m-1" href="#phenotypesTab" title="${fn:replace(phenotypeGroups[i*5 + j], ' phenotype', '')}: not significant" data-toggle="tooltip">
<%--                                    <input type="checkbox" autocomplete="off" data-value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}" onchange="filterAllData()" title="${phenotypeGroups[i*5 + j]}" value="no_significant">--%>
                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>
                                </div>
                            </c:when>
                            <c:otherwise>
<%--                                <span class="col-sm m-1" title="${fn:replace(phenotypeGroups[i*5 + j], 'phenotype', '')}" data-toggle="tooltip">--%>
<%--                                                                    <a class="btn btn-outline-light btn-icon non-tested disabled " >--%>
<%--                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>--%>
<%--                                </a>--%>
<%--                                </span>--%>

                                <div class="col-sm col-3 tile btn-outline-light btn-icon non-tested-tile m-1" href="#phenotypesTab" title="${fn:replace(phenotypeGroups[i*5 + j], ' phenotype', '')}: not tested" data-toggle="tooltip">
<%--                                    <input type="checkbox" autocomplete="off" data-value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}" onchange="filterAllData()" title="${phenotypeGroups[i*5 + j]}" value="no_significant" class="disabled">--%>
                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>
                                </div>

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

