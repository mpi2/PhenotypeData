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

        <div class="modal fade in w-100"  data-backdrop="true" id="conditions" tabindex="-1" role="dialog">
            <div class="modal-dialog modal-dialog-centered  modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Experimental conditions</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <table class="table table-striped">
                            <c:forEach var="entry" items="${metadataMap}">
                                <tr>
                                <th>${entry.key}</th>
                                <td>${entry.value}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>

        <c:if test="${ ! chartOnly}">

            <!-- <div class="container mt-3" id="section-associations"> -->
                <%-- <div class="row">
                    <div class="col-md-12 no-gutters">
                        <h3>Allele -<t:formatAllele>${symbol}</t:formatAllele></h3>
                    </div>
                </div> --%>
            <!-- </div> -->


            <div class="container single single--no-side">

            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">
                <div style="float: right;"><a href="${cmsBaseUrl}/help/quick-guide-to-the-website/chart-page/" target="_blank" style="color: #ce6211;"><i class="fa fa-question-circle" style="font-size: xx-large"></i></a></div>

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="/">Home</a> <span><span class="fal fa-angle-right"></span></span>
                            <a href="${baseUrl}/search">Genes</a> <span><span class="fal fa-angle-right"></span></span>
                            <a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> <span><span class="fal fa-angle-right"></span></span>
                            ${parameter.procedureNames[0]} / ${parameter.name}
                        </p>
                    </div>
                </div>
            </div>


            <div class="row row-over-shadow">
                    <div class="col-md-12 white-bg">
                        <div class="page-content">

            <c:if test="${viabilityDTO!=null}">
                <h5>${viabilityDTO.category}</h5>
            </c:if>

            <c:if test="${embryoViabilityDTO!=null}">
                <h5>${embryoViabilityDTO.proceedureName}</h5>
                <h5>Outcome: ${embryoViabilityDTO.category}</h5>
            </c:if>


            <div class="row">
            <div class="col-md-6">
                <div class="card w-100">
                    <div class="card-header">
                        Description of the experiments performed
                    </div>
                    <div class="card-body">
                        <p>
        <c:if test="${embryoViabilityDTO==null}">
                            A <b>${parameter.procedureNames[0]}</b> phenotypic assay was performed on <b>${numberMice}
                            mice</b>. The charts show the results of measuring <b>${parameter.name}</b> in <b>${numberFemaleMutantMice}
                            female</b>, <b>${numberMaleMutantMice} male</b> mutants compared to
                            <b>${numberFemaleControlMice} female</b>, <b>${numberMaleControlMice} male</b> controls.  The
            mutants <b>${zygosity}</b> the <b><t:formatAllele>${alleleSymbol}</t:formatAllele></b> allele.
        </c:if>

        <c:if test="${embryoViabilityDTO!=null}">
            A <b>${parameter.procedureNames[0]}</b> phenotypic assay was performed on a mutant strain carrying the <b><t:formatAllele>${alleleSymbol}</t:formatAllele></b> allele. The
            charts below show the proportion of wild type, heterozygous, and homozygous offspring.
        </c:if>
                        </p>
                    </div>
                </div>
            </div>

                <div class="col-md-6">
                    <table class="table table-striped">
                        <tr>
                            <th style="font-weight: bolder;">Testing protocol</th>
                            <td><a href="${procedureUrl}" style="font-weight: bolder;">${parameter.procedureNames[0]}</a></td>
                        </tr>
                        <tr>
                            <th>Measured value</th>
                            <td><a href="${parameterUrl}">${parameter.name}</a></td>
                        </tr>
                        <tr>
                            <th>Testing environment</th>
                            <td><a class="w-100" data-toggle="modal" data-target="#conditions" href="#">Lab conditions and equipment</a></td>
                        </tr>
                        <tr>
                            <th>Background Strain</th>
                            <td><t:formatAllele>${geneticBackgroundString}</t:formatAllele></td>
                        </tr>
                        <tr>
                            <th>Phenotyping center</th>
                            <td>${phenotypingCenter}</td>
                        </tr>
                    </table>


                </div>
            </div>
        </c:if>

        <p class="alert alert-info w-100">Mouseover the charts for more information. Click and drag to zoom the chart. Click on the legends to disable/enable data.</p>

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