<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Histopath Landing Page</jsp:attribute>


    <jsp:body>
        <div class="container single single--no-side">

            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="/">Home</a>
                            <span class="fal fa-angle-right"></span> Histopathology
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <div class="page-content">

                        <h2>Histopathology for ${gene.markerSymbol}</h2>
                        <p>Gene name: ${gene.markerName}</p>

                        <div class="card">
                            <div class="card-header">Score Definitions</div>
                            <div class="card-body">
                                <p class="my-0"><b>Severity Score:</b></p>
                                <ul class="my-0">
                                    <li>0 = Normal,</li>
                                    <li>1 = Mild (observation barely perceptible and not believed to have clinical
                                        significance),
                                    </li>
                                    <li>2 = Moderate (observation visible but involves minor proportion of tissue and
                                        clinical consequences of observation are most likely subclinical),
                                    </li>
                                    <li>3 = Marked (observation clearly visible involves a significant proportion of
                                        tissue and is likely to have some clinical manifestations generally expected to
                                        be minor),
                                    </li>
                                    <li>4 = Severe (observation clearly visible involves a major proportion of tissue
                                        and clinical manifestations are likely associated with significant tissue
                                        dysfunction or damage)
                                    </li>
                                </ul>

                                <p class="my-0"><b>Significance Score:</b></p>
                                <ul class="my-0">
                                    <li>0 = Not significant (histopathology finding that is interpreted by the
                                        histopathologist to be within normal limits of background strain-related
                                        findings or an incidental finding not related to genotype)
                                    </li>
                                    <li>1 = Significant (histopathology finding that is interpreted by the
                                        histopathologist to not be a background strain-related finding or an incidental
                                        finding)
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <table id="histopath" class="table table-sm table-striped dataTable">

                            <thead>
                            <tr>
                                <th>Zyg</th>
                                <th>Sex</th>
                                <th>Mouse</th>
                                <th>Tissue</th>
                                <th>MPATH Process Term</th>

                                <th>Severity Score</th>
                                <th>Significance Score</th>
                                <th>MPATH Diagnostic</th>
                                <th>PATO Descriptor</th>
                                <th>Free Text</th>
                                <th>Images</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="histRow" items="${histopathRows}">

                                <tr>

                                    <td>${histRow.zygosity}</td>
                                    <td>
                                        <c:if test="${histRow.sex eq 'female'}">F</c:if>
                                        <c:if test="${histRow.sex eq 'male'}">M</c:if>
                                    </td>
                                    <td>${histRow.sampleId}</td>
                                    <td id="${histRow.anatomyName}">${histRow.anatomyName}
                                        <c:if test="${histRow.anatomyId !=null && histRow.anatomyId !=''}">
                                            [${histRow.anatomyId}]
                                        </c:if>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${fn:length(histRow.mpathProcessOntologyBeans) == 0}">

                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="parameter"
                                                           items="${histRow.mpathProcessOntologyBeans }">
                                                    <c:forEach var="value" items="${parameter.value }">
                                                        ${value.name }
                                                        [${value.id }]
                                                    </c:forEach>

                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:forEach var="parameter" items="${histRow.severity }">
                                            ${parameter.textValue}
                                        </c:forEach>
                                    </td>


                                    <td>

                                        <c:if test="${fn:length(histRow.significance) ==0 }">
                                            Not Annotated
                                        </c:if>
                                        <c:forEach var="parameter" items="${histRow.significance }">
                                            <c:choose>
                                                <c:when test="${parameter.textValue eq 'Significant'}">
                                                    1
                                                </c:when>
                                                <c:otherwise>
                                                    0
                                                </c:otherwise>
                                            </c:choose>

                                        </c:forEach>
                                    </td>

                                    <c:choose>
                                        <c:when test="${fn:length(histRow.mpathDiagnosticOntologyBeans) == 0}">
                                            <td>

                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td>
                                                <c:forEach var="entry" items="${histRow.mpathDiagnosticOntologyBeans }">


                                                    <c:forEach var="bean" items="${entry.value}">
                                                        ${bean.name}
                                                        ${bean.id}

                                                    </c:forEach>
                                                </c:forEach>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>

                                    <td>
                                        <c:choose>
                                            <c:when test="${fn:length(histRow.patoOntologyBeans) == 0}">

                                            </c:when>
                                            <c:otherwise>

                                                <c:forEach var="parameter" items="${histRow.patoOntologyBeans }">
                                                    <c:forEach var="value" items="${parameter.value }">
                                                        ${value.name }
                                                    </c:forEach>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>

                                    </td>


                                    <td>
                                        <c:forEach var="parameter" items="${histRow.freeTextParameters }">
                                            ${parameter.textValue }
                                        </c:forEach>
                                    </td>


                                    <td>
                                        <c:forEach var="image" items="${histRow.imageList }">

                                            <t:hist_img_display img="${image}"
                                                                impcMediaBaseUrl="${impcMediaBaseUrl}"></t:hist_img_display>

                                        </c:forEach>
                                    </td>

                                </tr>

                            </c:forEach>

                        </tbody>
                        </table>

                        <div class="row my-5">
                            <div class="col-12">
                                <h2>Associated histopathology images</h2>
                            </div>
                            <c:forEach var="image" items="${histopathImagesForGene }">
                                <t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}" />
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>





        </div>

        <script>
            $(document).ready(function () {
                $('#histopath').DataTable(
                    {"paging": true, lengthChange: false, "searching": false, "order": [[6, "desc"]]});
            });
        </script>
    </jsp:body>

</t:genericpage>

