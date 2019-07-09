<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

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
    var firstDTLoad = true;
    $(document).ready(function () {
            console.log('hi');
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
