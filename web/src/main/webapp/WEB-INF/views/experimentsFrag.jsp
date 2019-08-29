<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<%-- <p class="resultCount">Total number of results: ${rows}</p> --%>

<div class="row justify-content-end mt-3 mr-2">
    <div class="btn-group btn-group-toggle" data-toggle="buttons">
        <label class="btn btn-outline-primary btn-sm active" id="phChartToggle">
            <input type="radio" name="optionsPh"  autocomplete="off" value="phChart"> Chart
        </label>
        <label class="btn btn-outline-primary btn-sm" id="phChartTableToggle">
            <input type="radio" name="optionsPh"  value="phTable" autocomplete="off">
            Table
        </label>
    </div>
</div>

<div id="phChart" style="display: none">
    <c:if test="${chart != null}">
        <!--p>Hints: Use the dropdown filters to filter both the chart and the table. Hover over points in chart to see the parameter name and information. Click on a point to view the chart for that data point. Click and drag on the chart to zoom to that area.
        </p-->
        <!-- chart here -->
        <p class="alert alert-info w-100 mt-2">Mouseover the data points for more information. Click and drag to zoom the chart. Click on the legends to disable/enable data.</p>
        <button id="checkAll" class="btn btn-sm btn-outline-success"><i class="fa fa-check" aria-hidden="true"></i> Select all</button>
        <button id="uncheckAll" class="btn btn-sm btn-outline-danger"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</button>
        <div id="chartDiv"></div>
        <div class="clear both"></div>

        <script type="text/javascript" async>${chart}</script>
    </c:if>
    <c:if test="${chart == null}">
        <p class="alert alert-warning w-100 mt-2">Statistical analisys has not been perform on any of the selected systems. Check the table view to see the raw data.</p>
        <script type="text/javascript">
            $(function () {
                $('#phChartTableToggle').button('toggle');
                $('#phChart').hide();
                $('#phTable').show();
                if(firstDTLoad) {
                    $("#strainPvalues").bootstrapTable();
                    firstDTLoad = false;
                }
            })
        </script>
    </c:if>
</div>
<!-- Associations table -->


<c:set var="count" value="0" scope="page"/>

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

<div id="phTable" style="display: none">
    <table id="strainPvalues" data-toggle="table"   data-cookie="true"
           data-cookie-id-table="strainPvaluesTable${gene.markerSymbol}" data-pagination="true" data-mobile-responsive="true" data-sortable="true" style="margin-top: 10px;" data-custom-sort="sortString" data-search="true">
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
        <c:forEach var="stableId" items="${experimentRows.keySet()}"
                   varStatus="status">
            <c:set var="stableIdExperimentsRow" value="${experimentRows[stableId]}"/>
            <c:forEach var="row" items="${stableIdExperimentsRow}">
                <tr title="${!row.getEvidenceLink().getDisplay() ? 'No supporting data supplied.' : ''}" data-toggle="tooltip" data-link="${row.getEvidenceLink().url}" class="${row.getEvidenceLink().getDisplay() ? 'clickableRow' : 'unClickableRow'}">
                    <td data-value="${row.getAllele().getSymbol()}">
                        <a href="${row.getEvidenceLink().url}">
                        <t:formatAllele>${row.getAllele().getSymbol()}</t:formatAllele>
                        </a>
                    </td>
                    <td data-value="${row.getPhenotypingCenter()}">
                        <a href="${row.getEvidenceLink().url}">
                            ${row.getPhenotypingCenter()}
                        </a>
                    </td>
                    <td data-value="${row.getProcedure().getName()}">
                        <a href="${row.getEvidenceLink().url}">
                            ${row.getProcedure().getName()} / ${row.getParameter().getName()}
                        </a>
                    </td>
                    <td data-value="${row.getZygosity().getShortName()}">
                        <a href="${row.getEvidenceLink().url}">
                            ${row.getZygosity().getShortName()}
                        </a>
                    </td>
                    <td data-value="${row.getFemaleMutantCount()}f:${row.getMaleMutantCount()}m">
                        <a href="${row.getEvidenceLink().url}">
                            ${row.getFemaleMutantCount()}f:${row.getMaleMutantCount()}m
                        </a>
                    </td>
                    <td data-value="${row.getStatisticalMethod()}">
                        <a href="${row.getEvidenceLink().url}">
                            ${row.getStatisticalMethod()}
                        </a>
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
        </c:forEach>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    var firstDTLoad = true;
    $(document).ready(function () {
        if("${param.currentView}" === 'table') {
            console.log("${param.currentView}");
            $('#phChartTableToggle').button('toggle');
            $('#phChart').hide();
            $('#phTable').show();
            if(firstDTLoad) {
                $("#strainPvalues").bootstrapTable();
                firstDTLoad = false;
            }
        } else {
            $('#phTable').hide();
            $('#phChart').show();
        }


        $('input[name=optionsPh]').change(function () {
            var value = $('input[name=optionsPh]:checked').val();
            if (value === 'phChart') {
                $('#phTable').hide();
                $('#phChart').show();
                currentView = 'chart';
            } else {
                currentView = 'table';
                $('#phChart').hide();
                $('#phTable').show();
                if(firstDTLoad) {
                    $("#strainPvalues").bootstrapTable();
                    firstDTLoad = false;
                }

            }
        });
    });
</script>
