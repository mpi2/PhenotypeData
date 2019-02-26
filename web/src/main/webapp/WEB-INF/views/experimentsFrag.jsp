<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<%-- <p class="resultCount">Total number of results: ${rows}</p> --%>

<div class="row justify-content-end mt-3 mr-2">
    <div class="btn-group btn-group-toggle" data-toggle="buttons">
        <label class="btn btn-outline-primary btn-sm active">
            <input type="radio" name="optionsPh" id="phChartToggle" autocomplete="off" value="phChart"> Chart
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="optionsPh" id="phChartTableToggle" value="phTable" autocomplete="off" checked>
            Table
        </label>
    </div>
</div>

<div id="phChart">
    <c:if test="${chart != null}">
        <!--p>Hints: Use the dropdown filters to filter both the chart and the table. Hover over points in chart to see the parameter name and information. Click on a point to view the chart for that data point. Click and drag on the chart to zoom to that area.
        </p-->
        <!-- chart here -->
        <p class="alert alert-warning w-100 mt-2">Mouseover the data points for more information. Click and drag to zoom the chart. Click on the legends to disable/enable data.</p>
        <button id="checkAll" class="btn btn-sm btn-outline-success"><i class="fa fa-check" aria-hidden="true"></i> Select all</button>
        <button id="uncheckAll" class="btn btn-sm btn-outline-danger"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</button>
        <div id="chartDiv"></div>
        <div class="clear both"></div>

        <script type="text/javascript" async>${chart}</script>
    </c:if>
</div>
<!-- Associations table -->


<c:set var="count" value="0" scope="page"/>

<script>
    var resTemp = document.getElementsByClassName("resultCount");
    if (resTemp.length > 1) {
        resTemp[0].remove();
    }
</script>

<div id="phTable">
    <table id="strainPvalues" class="table dt-responsive clickableRows" style="width:100%;">
        <thead>
        <tr>
            <th class="headerSort">Allele</th>
            <th class="headerSort">Center</th>
            <th class="headerSort">Procedure / Parameter</th>
            <th class="headerSort">Zygosity</th>
            <th class="headerSort">Mutants</th>
            <th class="headerSort">Statistical<br/>Method</th>
            <th class="headerSort">P Value</th>
            <th class="headerSort">Status</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="stableId" items="${experimentRows.keySet()}"
                   varStatus="status">
            <c:set var="stableIdExperimentsRow" value="${experimentRows[stableId]}"/>
            <c:forEach var="row" items="${stableIdExperimentsRow}">
                <tr>
                    <td><t:formatAllele>${row.getAllele().getSymbol()}</t:formatAllele></td>
                    <td>${row.getPhenotypingCenter()}</td>
                    <td>${row.getProcedure().getName()} / ${row.getParameter().getName()}</td>
                    <td>${row.getZygosity().getShortName()}</td>
                    <td>${row.getFemaleMutantCount()}f:${row.getMaleMutantCount()}m</td>
                    <td>${row.getStatisticalMethod()}</td>
                    <c:choose>
                        <c:when
                                test="${ ! empty row && row.getStatus() == 'SUCCESS'}">
                            <c:set var="paletteIndex" value="${row.colorIndex}"/>
                            <c:set var="Rcolor" value="${palette[0][paletteIndex]}"/>
                            <c:set var="Gcolor" value="${palette[1][paletteIndex]}"/>
                            <c:set var="Bcolor" value="${palette[2][paletteIndex]}"/>
                            <td style="background-color:rgb(${Rcolor},${Gcolor},${Bcolor})">
                                <t:formatScientific> ${row.getpValue()}</t:formatScientific>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td data-sort="${row.getpValue()}"><t:formatScientific>${row.getpValue()}</t:formatScientific></td>
                        </c:otherwise>
                    </c:choose>
                    <td>${row.status}</td>

                    <c:if test="${row.getEvidenceLink().getDisplay()}">
                        <td data-sort="${row.getEvidenceLink().getUrl()}">
                        </td>

                    </c:if>
                    <c:if test="${!row.getEvidenceLink().getDisplay()}">
                        <td data-sort="none">

                        </td>
                    </c:if>

                </tr>
            </c:forEach>
        </c:forEach>
        </tbody>
    </table>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        $('div#phTable').hide();
        $('input[name=optionsPh]').change(function () {
            var value = $('input[name=optionsPh]:checked').val();
            if (value === 'phChart') {
                $('#phTable').hide();
                $('#phChart').show();
            } else {
                $('#phChart').hide();
                $('#phTable').show();
            }
        });
        var oTable = $('#strainPvalues').dataTable({
            "bFilter": false,
            "bLengthChange": false,
            'columnDefs': [
                {
                    "targets": [8],
                    "visible": false
                },
                {
                    "targets": [0],
                    "max-width": "100px"
                }
            ],
            'rowCallback': function (row, data, index) {
                $(row).on('click', function () {
                    var url = data[8]['@data-sort'];
                    if (url !== "none") {
                        window.location.href = decodeURIComponent(url);
                    } else {
                        console.log(row);
                        row.removeClass('clickableRows');
                        row.addClass('unClickableRows');
                        row.addClass('text-muted');
                    }
                });
            }
        });
    });
</script>
