<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<script>
    var resTemp = document.getElementsByClassName("resultCount");
    if (resTemp.length > 1) {
        resTemp[0].remove();
    }

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

</script>

<div id="phTable">

    <table id="strainPvalues" data-toggle="table"  data-pagination="true" data-mobile-responsive="true" data-sortable="true" style="margin-top: 10px;" data-custom-sort="sortString" data-show-search-clear-button="true" data-search="true">
        <thead>
        <tr>
            <th data-sortable="true">Allele</th>
            <th data-sortable="true">Center</th>
            <th data-sortable="true">Procedure / Parameter</th>
            <th data-sortable="true">Zygosity</th>
            <th data-sortable="true">Mutants</th>
            <th data-sortable="true">Statistical<br/>Method</th>
            <th data-sortable="true">P Value</th>
            <th data-sortable="true">Status</th>
        </tr>
        </thead>

        <tbody>

            <c:forEach var="row" items="${experimentRows}">
                <tr title="${!row.getEvidenceLink().getDisplay() ? 'No supporting data supplied.' : ''}" data-toggle="tooltip" data-link="${row.getEvidenceLink().url}" class="${row.getEvidenceLink().getDisplay() ? 'clickableRow' : 'unClickableRow'}">
                    <td data-value="${row.getAllele().getSymbol()}" class="allele-symbol">

                        <t:formatAllele>${row.getAllele().getSymbol()}</t:formatAllele>

                    </td>
                    <td data-value="${row.getPhenotypingCenter()}">

                            ${row.getPhenotypingCenter()}

                    </td>
                    <td data-value="${row.getProcedure().getName()}">

                            ${row.getProcedure().getName()} / ${row.getParameter().getName()}

                    </td>
                    <td data-value="${row.getZygosity().getShortName()}">

                            ${row.getZygosity().getShortName()}

                    </td>
                    <c:if test="${row.getFemaleMutantCount() != null && row.getMaleMutantCount() != null}">
                        <td data-value="${row.getFemaleMutantCount()}f:${row.getMaleMutantCount()}m">

                                    ${row.getFemaleMutantCount()}f:${row.getMaleMutantCount()}m

                        </td>
                    </c:if>
                    <c:if test="${row.getFemaleMutantCount() == null || row.getMaleMutantCount() == null}">
                        <td data-value="${row.getFemaleMutantCount()}f:${row.getMaleMutantCount()}m">



                        </td>
                    </c:if>

                    <td data-value="${row.getStatisticalMethod()}">

                            ${row.getStatisticalMethod()}

                    </td>
                    <c:choose>
                        <c:when
                                test="${ ! empty row && row.getStatus() == 'SUCCESS'}">
                            <c:set var="paletteIndex" value="${row.colorIndex}"/>
                            <c:set var="Rcolor" value="${palette[0][paletteIndex]}"/>
                            <c:set var="Gcolor" value="${palette[1][paletteIndex]}"/>
                            <c:set var="Bcolor" value="${palette[2][paletteIndex]}"/>
                            <td style="background-color:rgb(${Rcolor},${Gcolor},${Bcolor})" data-value="${row.getpValue()}">
                                <a href="${row.getEvidenceLink().url}">
                                    <t:formatScientific>${row.getpValue()}</t:formatScientific>
                                </a>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td data-value="${row.getpValue()}">
                                <a href="${row.getEvidenceLink().url}">
                                <t:formatScientific>${row.getpValue()}</t:formatScientific>
                                </a>
                            </td>
                        </c:otherwise>
                    </c:choose>
                    <td data-value="${row.status}">${row.status}</td>
                </tr>
            </c:forEach>

        </tbody>
    </table>
</div>

<script type="text/javascript">
    var firstDTLoad = true;
    $(document).ready(function () {
        $('#allDataTableCount').html(${rows});
        if(firstDTLoad) {
            $("#strainPvalues").bootstrapTable({
                onSearch: function (event) {
                    $('#allDataTableCount').html($("#strainPvalues").bootstrapTable('getData').length);
                }
            });
            firstDTLoad = false;
        }
    });
</script>
