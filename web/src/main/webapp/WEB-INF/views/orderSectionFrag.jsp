<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:if test="${orderRows.size() > 0}">
    <c:if test="${creLine}">
        <c:set var="creLineParam" value="&creLine=true"/>
    </c:if>
    <div class="alert alert-warning">
        <p>
            This service may be affected by the Covid-19 pandemic. <a href="https://www.mousephenotype.org/news/impc-covid-19-update/">See how</a>
        </p>
    </div>
    <table id="creLineTable"
           data-toggle="table"
           data-pagination="true"
           data-mobile-responsive="true"
           data-sortable="true"
           data-detail-view="true"
           data-detail-formatter="detailFormatter">
        <thead>
        <tr>
            <th>MGI Allele</th>
            <th>Allele Type</th>
            <th>Produced</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="row" items="${orderRows}" varStatus="status">
            <tr data-link="${baseUrl}/allelesFrag/${row.mgiAccessionId}/${row.encodedAlleleName}?${creLineParam}" data-shown="false">
                <td>
                    <span class="text-dark" style="font-size: larger; font-weight: bolder;">${row.markerSymbol}<sup>${row.alleleName}</sup></span>
                </td>
                <td>
                        ${row.alleleDescription}
                </td>
                <td>
                    <c:if test="${row.mouseAvailable}">
                        <span>
                            Mice<c:if test="${row.targetingVectorAvailable or row.esCellAvailable or row.tissuesAvailable}">,</c:if>
                        </span>

                    </c:if>
                    <c:if test="${row.targetingVectorAvailable}">
                        <span>Targeting vectors<c:if test="${row.esCellAvailable or row.tissuesAvailable}">,</c:if></span>
                    </c:if>
                    <c:if test="${row.esCellAvailable}">
                        <span>ES Cells<c:if test="${row.tissuesAvailable}">,</c:if></span>
                    </c:if>
                    <c:if test="${row.tissuesAvailable}">
                        <span>Tissue</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>

    </table>
</c:if>

<c:choose>
    <c:when test="${creLineAvailable}">
        <div><a href="${baseUrl}/order/creline?acc=${acc}" target="_blank">Cre
            Knockin ${alleleProductsCre2.get("product_type")} are available for this gene.</a></div>
    </c:when>
</c:choose>

<script>
    var orderContent = {};
    function detailFormatter(index, row) {
        if(!row._data['shown']) {
            $.ajax({
                url: row._data['link'],
                type: 'GET',
                success: function (data) {
                    $('#orderAllele' + index).html(data);
                    row._data['shown'] = true;
                    orderContent[index] = data;
                }
            });
            return "<div class='container'>" +
                '<div id="orderAllele' + index + '" class="col-12">' +
                "     <div class=\"pre-content\">\n" +
                "                        <div class=\"row no-gutters\">\n" +
                "                            <div class=\"col-12 my-5\">\n" +
                "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>" +
                '</div>' +
                '</div>';
        } else {
            return "<div class='container'>" +
                '<div id="orderAllele' + index + '" class="col-12">' +
                orderContent[index] +
                '</div>' +
                '</div>';
        }
    }

    $('#creLineTable').on('click-row.bs.table',function(e,row,$tr){
        $tr.find('>td>.detail-icon').trigger('click');
    });
</script>
