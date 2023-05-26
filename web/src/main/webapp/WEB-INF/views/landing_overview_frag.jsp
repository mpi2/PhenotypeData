<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<br/>

<c:if test="${genePercentage.getDisplay()}">

    <div class="row">
        <div class="col-md-6">
            <div>Current Total Number of Genes Tested: ${genePercentage.totalGenesTested}</div>
            <div id="pieChart">
                <script type="text/javascript">${genePercentage.getPieChartCode()}</script>
            </div>
        </div>


        <div class="col-md-6">
            <table>
                <thead>
                <tr>
                    <th class="headerSort"> Phenotype</th>
                    <th> # Genes</th>
                </tr>
                </thead>
                <tbody>

                <c:forEach var="row" items="${phenotypes}" varStatus="loop">
                    <tr <c:if test="${loop.index >= 10}"> class="hidden hideable" </c:if> >
                        <td class="capitalize"><a href="${baseUrl}/phenotypes/${row.getMpId()}">${row.getCategory()}</a>
                        </td>
                        <c:if test="${row.getMpId() != null}">
                            <td>
                                <a href="${baseUrl}/phenotypes/export/${row.getMpId()}?fileType=tsv&fileName=IMPC_${row.getCategory()}"
                                   target="_blank">${row.getCount()} </a></td>
                        </c:if>
                        <c:if test="${row.getMpId() == null}">
                            <td><h4>${row.getCount()}</h4></td>
                        </c:if>
                    </tr>
                </c:forEach>
                <tr>
                    <td>
                        <c:if test="${phenotypes.size() > 10}"> <a id="showMore">Show more</a> </c:if></td>

                    <td>
                        <a id="tsvDownload"
                           href="${baseUrl}/phenotypes/export/${mpId}?fileType=tsv&fileName=IMPC_${pageTitle}"
                           target="_blank" class="button fa fa-download download-data">Download</a>
                    </td>
                </tr>
                </tbody>
            </table>

        </div>
    </div>
</c:if>

<script type="text/javascript">

    $(document).ready(function () {
        $('#showMore').click(function () {
            $(".hideable").toggleClass("hidden");
            var text = $('#showMore').text();
            $(this).text(
                text == "Show more" ? "Show less" : "Show more");
        });
    });
</script>