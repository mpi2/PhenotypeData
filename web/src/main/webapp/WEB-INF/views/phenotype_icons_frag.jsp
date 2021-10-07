<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<div style="min-width: 300px">
    <div class="row no-gutters">

        <div class="container p-0 text-muted"  id="phIconGrid" onclick="$('div#phenotypes-section')[0].scrollIntoView()">
            <c:forEach var="i" begin="0" end="3">
                <div class="row no-gutters btn-group-toggle pheno-row" data-toggle="buttons">
                    <c:forEach var="j" begin="0" end="4">
                        <c:choose>
                            <c:when test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <div class="col-sm col-3 tile btn-outline-primary btn-icon significant-tile m-1" href="#phenotypes-section" title="${fn:replace(phenotypeGroups[i*5 + j], ' phenotype', '')}: significant" data-toggle="tooltip">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>
                                </div>
                            </c:when>
                            <c:when test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i*5 + j])}">
                                <div class="col-sm col-3 tile btn-outline-info btn-icon non-significant-tile m-1" href="#phenotypes-section" title="${fn:replace(phenotypeGroups[i*5 + j], ' phenotype', '')}: not significant" data-toggle="tooltip">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="col-sm col-3 tile btn-outline-light btn-icon non-tested-tile m-1" href="#phenotypes-section" title="${fn:replace(phenotypeGroups[i*5 + j], ' phenotype', '')}: not tested" data-toggle="tooltip">
                                    <i class="${phenotypeGroupIcons[i*5 + j]}"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="row phenogrid-key pt-3 no-gutters font-weight-light" style="font-size: 0.8em;">
        <div class="col key key-significant">
            <span>Significant</span>
        </div>
        <div class="col text-center key key-not-significant">
            <span>Not Significant</span>
        </div>
        <div class="col text-right key key-not-tested">
            <span>Not tested</span>
        </div>
    </div>
</div>

