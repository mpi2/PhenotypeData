<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<link type='text/css' rel='stylesheet' href='${baseUrl}/css/geneHeatmapStyling.css'/>

<script>
    $(function () {
        var header_height = 0;
        $('.gene-heatmap-header .vertical').each(function () {
            if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
            $(this).width($(this).height() * 0.05);
        });
        $('geneHeatMap table th').height(header_height);
    });
</script>

<div id="legend">
    <table>
        <tr>
            <td>
                <div class="table_legend_color hm-significant">&nbsp;</div>
                <div class="table_legend_key">Significant</div>
            </td>
            <td>
                <div class="table_legend_color hm-not-significant">&nbsp;</div>
                <div class="table_legend_key">Not Significant</div>
            </td>
            <td>
                <div class="table_legend_color hm-no-data">&nbsp;</div>
                <div class="table_legend_key">No data</div>
            </td>
        </tr>
    </table>
</div>

<table id="secondaryGeneHeatmap" class="geneHeatMap">

    <thead>
    <tr>
        <th class="gene-heatmap-header"><span>Gene</span></th>
        <th><span>Family</span></th>
        <th><span>Availability</span></th>
        <c:forEach var="xAxisBean" items="${xAxisBeans}">
            <th title="${xAxisBean.name}"><span class="vertical"><a
                    href="${baseUrl}/phenotypes/${xAxisBean.id}">${xAxisBean.name}</a></span></th>
        </c:forEach>
    </tr>
    </thead>

    <c:forEach items="${geneRows}" var="row">
        <tr>
            <td>
                <a href="${baseUrl}/genes/${row.accession}">${row.symbol}</a>
                <br/>${row.getHumanSymbolToString()}</td>
            <td>${row.groupLabel}</td>
            <td>${row.miceProduced}</td>
            <c:forEach var="xAxisBean" items="${xAxisBeans}">
                <c:choose>
                    <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'Deviance Significant'}">
                        <td title="${xAxisBean.name}" class="hm-significant">A</td>
                    </c:when>
                    <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'Could not analyse'}">
                        <td title="${xAxisBean.name}" class="hm-not-analysed">C</td>
                    </c:when>
                    <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'Data analysed, no significant call'}">
                        <td title="${xAxisBean.name}" class="hm-not-significant">B</td>
                    </c:when>
                    <c:when test="${row.XAxisToCellMap[xAxisBean.name].status eq 'No data' }">
                        <td title="${xAxisBean.name}" class="hm-no-data">D</td>
                    </c:when>

                </c:choose>

            </c:forEach>
        </tr>
    </c:forEach>

    <script>
        $(document).ready(function () {
            $('#secondaryGeneHeatmap').DataTable({
                sortable: true,
                fixedColumns: {
                    leftColumns: 2
                }
            });

        });
    </script>

</table>

<div id="export">
    <p class="textright"> <a href="http://ftp.ebi.ac.uk/pub/databases/impc/idg/IDG_file_for_IMPC_webportal.tsv">Download complete IDG data</a>
    </p>
</div>
