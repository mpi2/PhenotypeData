<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<c:choose>


    <c:when test="${emptyExperiment}">
        <!-- <div class="alert alert-error">
        <strong>Error:</strong> experiment empty
        </div> -->
    </c:when>

    <c:otherwise>

        <div class="modal fade in w-75"  data-backdrop="false" id="conditions" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-dialog-centered  modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Experimental conditions</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <dl class="small">
                            <c:forEach var="entry" items="${metadataMap}">
                                <dt>${entry.key}</dt>
                                <dd>${entry.value}</dd>
                            </c:forEach>
                        </dl>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>

        <c:if test="${ ! chartOnly}">

            <!-- <div class="container mt-3" id="section-associations"> -->
                <%-- <div class="row">
                    <div class="col-12 no-gutters">
                        <h3>Allele -<t:formatAllele>${symbol}</t:formatAllele></h3>
                    </div>
                </div> --%>
            <!-- </div> -->


            <div class="container single single--no-side">

            <div class="breadcrumbs" style="margin-top: auto; margin: auto; padding: auto">
                <div class="row">
                    <div class="col-12">
                        <p><a href="/">Home</a> <span>></span>
                            <a href="${baseUrl}/search">Genes</a> <span>></span>
                            <a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> <span>></span>
                            ${parameter.procedureNames[0]} / ${parameter.name}
                        </p>
                    </div>
                </div>
            </div>


            <div class="row row-over-shadow">
                    <div class="col-12 white-bg">
                        <div class="page-content">

            <c:if test="${viabilityDTO!=null}">
                <h5>${viabilityDTO.category}</h5>
            </c:if>

            <c:if test="${embryoViabilityDTO!=null}">
                <h5>${embryoViabilityDTO.proceedureName}</h5>
                <h5>Outcome: ${embryoViabilityDTO.category}</h5>
            </c:if>


            <div class="row">
            <div class="col-6">
                <div class="card w-100">
                    <div class="card-header">
                        Description of the experiments performed
                    </div>
                    <div class="card-body">
                        <p>
                            A <b>${parameter.procedureNames[0]}</b> phenotypic assay was performed on <b>${numberMice}
                            mice</b> carrying the <b><t:formatAllele>${alleleSymbol}</t:formatAllele></b> allele. The
                            charts
                            show the results of measuring <b>${parameter.name}</b> in <b>${numberFemaleMutantMice}
                            female</b>, <b>${numberMaleMutantMice} male</b> mutants compared to
                            <b>${numberFemaleControlMice} female</b>, <b>${numberMaleControlMice} male</b> controls.
                        </p>
                    </div>
                </div>
            </div>

                <div class="col-6">
                    <table class="table table-striped">
                        <tr>
                            <th>Phenotyping center</th>
                            <td>${phenotypingCenter}</td>
                        </tr>
                        <tr>
                            <th>Phenotyping procedure</th>
                            <td><a href="${procedureUrl}">${parameter.procedureNames[0]}</a></td>
                        </tr>
                        <tr>
                            <th>Measured value</th>
                            <td><a href="${parameterUrl}">${parameter.name}</a></td>
                        </tr>
                        <tr>
                            <th>Background Strain</th>
                            <td><t:formatAllele>${geneticBackgroundString}</t:formatAllele></td>
                        </tr>
                        <tr>
                            <td colspan="2">

                                <button type="button" class="btn btn-primary w-100" data-toggle="modal" data-target="#conditions">Show experimental conditions</button>

                                <!-- Metadata group: ${metadata} -->
                            </td>
                        </tr>
                    </table>


                </div>
            </div>
        </c:if>

        <span class="badge badge-info w-100">Mouseover the charts for more information. Click and drag to zoom the chart. Click on the legends to disable/enable data.</span>

        <c:choose>
            <c:when test="${param['chart_type'] eq 'UNIDIMENSIONAL_SCATTER_PLOT'}">
                <jsp:include page="scatterStatsFrag.jsp"/>
            </c:when>
            <c:when test="${param['chart_type'] eq 'UNIDIMENSIONAL_ABR_PLOT'}">
                <jsp:include page="abrFrag.jsp"/>
            </c:when>
            <c:when test="${viabilityDTO!=null}">
                <jsp:include page="pieFrag.jsp"/>
            </c:when>
            <c:when test="${embryoViabilityDTO!=null}">
                <jsp:include page="embryoViabilityPieFrag.jsp"/>
            </c:when>
            <c:when test="${fertilityDTO!=null}">
                <jsp:include page="fertPieFrag.jsp"/>
            </c:when>
            <c:otherwise>
                <jsp:include page="unidimensionalStatsFrag.jsp"/>
            </c:otherwise>
        </c:choose>

        <!-- only show scatter if scatter else only show unidimensional - otherwise we get unidimensional tables showing twice on the same page -->

        <jsp:include page="timeSeriesStatsFrag.jsp"/>

        <jsp:include page="categoricalStatsFrag.jsp"/>

    </c:otherwise>
</c:choose>
                        </div>
                    </div>
                </div>
            </div>

<script>
    $(function () {
        $('[data-toggle="tooltip"]').tooltip()
    })
</script>