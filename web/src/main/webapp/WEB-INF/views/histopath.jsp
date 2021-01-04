<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Histopath Information for ${gene.markerName}</jsp:attribute>


    <jsp:body>
        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Histopathology data for ${gene.markerSymbol}</h2>
                </div>
            </div>
        </div>
        <div class="container white-bg-small">

            <div class="breadcrumbs clear row">
                    <div class="col-12 d-none d-lg-block px-5 pt-5">
                        <p><a href="/">Home</a>
                            <span class="fal fa-angle-right"></span><a href="${baseUrl}/search">Genes</a>
                            <span class="fal fa-angle-right"></span><a
                                    href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a>
                            <span class="fal fa-angle-right"></span> Histopathology
                        </p>
                    </div>
            </div>

            <div class="row">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
<div class="page-content people py-5 white-bg">
    <div class="row no-gutters">
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
                    <li>0 = <i>Not significant</i>: Interpreted by the histopathologist to be a finding attributable to background strain (e.g. low-incidence hydrocephalus, microphthalmia) or incidental to mutant phenotype (e.g. hair-induced glossitis, focal hyperplasia, mild mononuclear cell infiltrate).
                    </li>
                    <li>1 = <i>Significant</i>: Interpreted by the histopathologist as a finding not attributable to background strain and not incidental to mutant phenotype.
                    </li>
                </ul>
            </div>
        </div>

        <br/>
        <br/>

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

<%--                        <c:if test="${fn:length(histRow.significance) ==0 }">--%>
<%--                            Not Annotated--%>
<%--                        </c:if>--%>
                        <c:forEach var="parameter" items="${histRow.significance }">
                            ${parameter.textValue}
<%--                            <c:choose>--%>
<%--                                <c:when test="${parameter.textValue eq 'Significant'}">--%>
<%--                                    1--%>
<%--                                </c:when>--%>
<%--                                <c:otherwise>--%>
<%--                                    0--%>
<%--                                </c:otherwise>--%>
<%--                            </c:choose>--%>

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

                            <a href="${image.jpeg_url}"><img src="${image.thumbnail_url}"/></a>

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
                <t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"/>
            </c:forEach>
        </div>
    </div>
</div>
                    </div>
                </div>
            </div>


        </div>

        <script>
            $(document).ready(function () {
                var getUrlParameter = function getUrlParameter(sParam) {
                    var sPageURL = window.location.search.substring(1),
                        sURLVariables = sPageURL.split('&'),
                        sParameterName,
                        i;

                    for (i = 0; i < sURLVariables.length; i++) {
                        sParameterName = sURLVariables[i].split('=');

                        if (sParameterName[0] === sParam) {
                            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
                        }
                    }
                };

                var anatomy = getUrlParameter("anatomy");
                if (anatomy !== undefined) {
                    anatomy = anatomy.replace('\"', '').replace('\"', '');//for some of the anatomy terms they have spaces
                    console.log('anatomy Param=' + anatomy);
                }

                $('#histopath').DataTable(
                    {
                        "pageLength": 25,
                        "order": [[6, "desc"]],
                        //code to highlight rows with the anatomy if specified as a parameter
                        'createdRow': function (row, data, index) {
                            //$(row).find('td:eq(1)').css('background-color', 'grey');
                            $(row).css("cursor", "pointer");
                            if (anatomy) {
                                //for (var i = 1; i < data.length; i++) {
                                if (data[3].includes(anatomy)) {
                                    $(row).css('background-color', 'rgb(206, 98, 17, 0.8)');
                                }
                                // }
                            }
                        }
                        //    "paging": true, lengthChange: false, "searching": false, "order": [[6, "desc"]]
                    });
            });
        </script>
    </jsp:body>

</t:genericpage>

