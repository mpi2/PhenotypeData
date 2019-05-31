<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Histopathology Summary for ${gene.markerName}</jsp:attribute>

    <jsp:attribute name="addToFooter">
        <script>
            $(document).ready(function () {
                $('#histopath').DataTable(
                    {"paging": false, "searching": false});
            });
        </script>
    </jsp:attribute>

    <jsp:body>

        <div class="container single single--no-side">

            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="/">Home</a>
                            <span class="fal fa-angle-right"></span><a href="${baseUrl}/search">Genes</a>
                            <span class="fal fa-angle-right"></span><a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a>
                            <span class="fal fa-angle-right"></span> Summary of abnormal Histopathology
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <div class="page-content">
                        <h2>Abnormal Histopathology Summary for ${gene.markerSymbol}</h2>
                        <table id="histopath" class="table tableSorter">

                            <thead>
                            <tr>
                                <th>Tissue</th>
                                <th>MPATH Process Term</th>
                                <th>Significant Finding Incidence Rate</th>
                                <th>Data</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="histRow" items="${histopathRows}">
                                <tr>
                                    <td>${histRow.anatomyName}</td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${fn:length(histRow.mpathProcessOntologyBeans) == 0}">

                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="parameter"
                                                           items="${histRow.mpathProcessOntologyBeans }">
                                                    <c:forEach var="value" items="${parameter.value }">
                                                        ${value.name }
                                                    </c:forEach>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>${histRow.significantCount } / ${histRow.nonSignificantCount }</td>
                                    <td>
                                        <a href='${baseUrl}/histopath/${gene.mgiAccessionId}#${histRow.sampleId}_${histRow.anatomyName}'><i
                                                class="fa fa-table" alt="All Histopath Data"></i></a></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>

