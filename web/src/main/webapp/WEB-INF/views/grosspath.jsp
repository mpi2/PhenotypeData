<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Gross Pathology Information for ${gene.markerName}</jsp:attribute>

    <jsp:attribute
            name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Gross Pathology And Tissue Collection View</jsp:attribute>

    <jsp:attribute name="header">

       
    </jsp:attribute>

    <jsp:body>
        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Gross Pathology Information for ${gene.markerSymbol}</h2>
                </div>
            </div>
        </div>
        <div class="container white-bg-small">
            <div class="row">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-5 pb-5">
                            <div class="container">
                                <div class="row text-center justify-content-center">
                                    <div class="col">
                                        Observations Numbers
                                    </div>
                                </div>
                                <div class="row justify-content-center">
                                    <div class="col-md-12">
                                        <table id="histopath" class="table table-striped table-bordered dt-responsive" style="width:100%">
                                            <thead>
                                            <tr>
                                                <th>
                                                    Anatomy
                                                </th>
                                                    <%-- <th>Sample Id</th> --%>
                                                <th>
                                                    Zyg
                                                </th>
                                                    <%-- <th>
                                                    OntologyTerm
                                                    </th> --%>
                                                    <%-- <th>
                                                    Free Text
                                                    </th> --%>
                                                <th>
                                                    Abnormal
                                                </th>
                                                <th>
                                                    Normal
                                                </th>
                                                <th>
                                                    Center
                                                </th>


                                            </tr>
                                            </thead>
                                            <c:forEach var="pathRow" items="${pathRows}">

                                                <tr>
                                                    <td id="${pathRow.sampleId}_${pathRow.anatomyName}">
                                                            ${pathRow.anatomyName}
                                                    </td>
                                                        <%-- <td>
                                                           ${pathRow.sampleId}
                                                       </td> --%>
                                                    <td>
                                                            ${pathRow.zygosity}
                                                    </td>
                                                        <%-- <c:choose>
                                                        <c:when test="${fn:length(pathRow.subOntologyBeans) == 0}">
                                                            <td>
                                                            </td>
                                                        </c:when>
                                                        <c:otherwise>
                                                        <c:forEach var="parameter" items="${pathRow.subOntologyBeans }">
                                                            <td >
                                                                subOntologyParam: ${parameter.key} gives anatomy term which should match the anatomy in the row


                                                            <c:forEach var="value" items="${parameter.value }" varStatus="loop"  >
                                                            ${value.id } - ${value.name }<c:if test="${!loop.last}">,</c:if>
                                                            </c:forEach>
                                                            </td>
                                                        </c:forEach>
                                                        </c:otherwise>
                                                        </c:choose>  --%>


                                                        <%-- <td>

                                                            ${pathRow.textValue}

                                                        </td> --%>
                                                    <td>
                                                            ${pathRow.numberOfAbnormalObservations}
                                                    </td>
                                                    <td>
                                                            ${pathRow.numberOfNormalObservations}
                                                    </td>
                                                    <td>
                                                            ${pathRow.center}
                                                    </td>

                                                        <%-- <c:if test="${fn:length(pathRow.imageList) >0}">
                                                            <tr>
                                                                <c:forEach var="img" items="${pathRow.imageList }">
                                                                    <td>

                                                                          <div id="grid">

                                                                            <t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>

                                                                        </div>

                                                                        <a href="${impcMediaBaseUrl}/render_image/${img.omero_id}/" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}/" original="${impcMediaBaseUrl}/archived_files/download/${img.omero_id}/">
                                     <img  src="${impcMediaBaseUrl}/render_birds_eye_view/${img.omero_id}/" class="thumbnailStyle"></a>

                                                                    </td>
                                                                </c:forEach>

                                                        </tr>
                                                        </c:if> --%>


                                                </tr>

                                            </c:forEach>


                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <script>
            $(document).ready(function () {
                $('#histopath').DataTable(
                    {"paging": false, "searching": false, "order": [[0, "asc"]],  "responsive": true, "bInfo": false });
            });
        </script>
    </jsp:body>

</t:genericpage>

