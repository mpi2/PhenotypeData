<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:set var="count" value="0" scope="page"/>
<c:set var="maleCount" value="0" scope="page"/>
<c:set var="femaleCount" value="0" scope="page"/>
<c:set var="noSexCount" value="0" scope="page"/>
<c:forEach var="phenotype" items="${rowsForPhenotypeTable}" varStatus="status">
    <c:forEach var="sex" items="${phenotype.sexes}">
        <c:set var="count" value="${count + 1}" scope="page"/>
        <c:if test='${sex.equalsIgnoreCase("male")}'>
            <c:set var="maleCount" value="${maleCount + 1}" scope="page"/>
        </c:if>
        <c:if test='${sex.equalsIgnoreCase("female")}'>
            <c:set var="femaleCount" value="${femaleCount + 1}" scope="page"/>
        </c:if>
        <c:if test='${sex.equalsIgnoreCase("no_data")}'>
            <c:set var="noSexCount" value="${noSexCount + 1}" scope="page"/>
        </c:if>
    </c:forEach>
</c:forEach>

<script>
    function sortString(sortName, sortOrder, data) {
        var order = sortOrder === 'desc' ? -1 : 1;
        data.sort(function (a, b) {
            var aa = sortName === 6 ? parseFloat(a['_' + sortName + '_data']['value']) || 0.0: a['_' + sortName + '_data']['value'];
            var bb = sortName === 6 ? parseFloat(b['_' + sortName + '_data']['value']) || 0.0: b['_' + sortName + '_data']['value'];
            if (aa < bb) {
                return order * -1
            }
            if (aa > bb) {
                return order
            }
            return 0
        })
    }

    function sortPValue(a, b, rowA, rowB) {
        console.log(rowA);
        if (parseFloat(rowA._6_data.value) < parseFloat(rowB._6_data.value)) return 1;
        if (parseFloat(rowA._6_data.value) > parseFloat(rowB._6_data.value)) return -1;
        return 0;
    }
</script>

<table id="significantPhenotypesTable" data-toggle="table"  data-pagination="true" data-mobile-responsive="true"
       data-sortable="true" data-custom-sort="sortString" data-search="true" data-show-search-clear-button="true">
    <thead>
    <tr>
        <th data-sortable="true" >Phenotype</th>
        <th data-sortable="true">System</th>
        <th data-sortable="true">Allele</th>
        <th title="Zygosity" data-sortable="true">Zyg</th>
        <th data-sortable="true">Sex</th>
        <th data-sortable="true">Life Stage</th>
        <th data-sortable="true" data-sorter="sortPValue" data-sort-name="value">P Value</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="phenotype" items="${rowsForPhenotypeTable}" varStatus="status">
        <c:set var="europhenome_gender" value="Both-Split"/>
        <tr title="${!phenotype.getEvidenceLink().getDisplay() ? 'No supporting data supplied.' : ''}" data-toggle="tooltip" data-link="${phenotype.getEvidenceLink().url}" class="${phenotype.getEvidenceLink().getDisplay() ? 'clickableRow' : 'unClickableRow'}">

            <td data-value="${phenotype.phenotypeTerm.name}">
                <a href="${phenotype.getEvidenceLink().url}">
                        ${phenotype.phenotypeTerm.name}
                </a>
            </td>
            <td class="text-lg-center" style="font-size: 1.25em;" data-value="${phenotype.topLevelMpGroups.first()}">
                <a href="${phenotype.getEvidenceLink().url}">
                <span class="row_abnormalities">
                    <c:set var="marginLeftCount" value="0"/>
                    <c:forEach var="topLevelMpGroup" items="${phenotype.topLevelMpGroups}" varStatus="groupCount">
                        <c:choose>
                            <c:when test="${topLevelMpGroup eq 'NA' }">
                            </c:when>
                            <c:otherwise>

                                <i class="${phenotypeGroupIcons[phenotypeGroups.indexOf(topLevelMpGroup)]} text-primary"
                                   data-hasqtip="27" title="${topLevelMpGroup}"></i>
                            </c:otherwise>
                        </c:choose>

                    </c:forEach>
                </span>
                </a>
            </td>
            <td data-value="${phenotype.allele.symbol}">
                <a href="${phenotype.getEvidenceLink().url}">
                <span style="white-space: nowrap"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></span>
                </a>

            </td>
            <td title="${phenotype.zygosity}" data-value="${phenotype.zygosity}"><a href="${phenotype.getEvidenceLink().url}">${phenotype.zygosity.getShortName()}</a></td>
            <td data-value="${phenotype.sexes}">
                <a href="${phenotype.getEvidenceLink().url}">
                <c:set var="count" value="0" scope="page"/>
                <t:displaySexes sexes="${phenotype.sexes}"></t:displaySexes>
                </a>
            </td>
            <td data-value="${phenotype.lifeStageName}"><a href="${phenotype.getEvidenceLink().url}">${phenotype.lifeStageName}</a></td>


            <td data-value="${phenotype.prValueAsString}">
                <a href="${phenotype.getEvidenceLink().url}" style="white-space: nowrap"><t:formatScientific>${phenotype.prValueAsString}</t:formatScientific></a></td>

        </tr>
    </c:forEach>
    </tbody>
</table>

<script>
    $(document).ready(function () {
        $("#significantPhenotypesTable").on('search.bs.table', function () {
            $('#significantCount').html($("#significantPhenotypesTable").bootstrapTable('getData').length);
        });
    });
</script>