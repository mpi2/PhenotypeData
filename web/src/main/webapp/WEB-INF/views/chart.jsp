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
            <div class="breadcrumbs clear row">
                <div class="col-10 d-none d-lg-block px-3 py-3">
                    <aside>
                        <a href="/">Home</a> <span class="fal fa-angle-right"></span>
                        <a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
                        <a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> <span class="fal fa-angle-right"></span>
                            ${parameter.procedureNames[0]} / ${parameter.name}
                    </aside>
                </div>
                <div class="col-2 d-none d-lg-block px-3 py-3">
                    <aside>
                        <a href="${cmsBaseUrl}/help/quick-guide-to-the-website/chart-page/" target="_blank" ><i class="fa fa-question-circle" style="font-size: xx-large; color: #ce6211;"></i></a>
                    </aside>
                </div>
            </div>




            <div class="row">
                    <div class="col-12 col-md-12">
            <div class="pre-content clear-bg">
                        <div class="page-content people white-bg">

            <c:if test="${embryoViabilityDTO!=null}">
                <h5>${embryoViabilityDTO.proceedureName}</h5>
                <h5>Outcome: ${embryoViabilityDTO.category}</h5>
            </c:if>


            <div class="row">

                <c:if test="${isViability}">
                    <div class="alert alert-warning w-100">
                        <p>Please note:</p>
                        <ul>
                            <li>data for different colonies will be presented separately (e.g. different alleles; same allele but different background strain; same allele but in different phenotyping centers)</li>
                            <li>phenotype calls are made when a statistically significant abnormal phenotype is detected (that is, preweaning lethality or absence of expected number of homozygote pups based on Mendelian ratios)</li>
                        </ul>
                    </div>
                </c:if>

                <div class="col-12 alert alert-secondary">
                    Description of the experiments performed
                </div>

                <div class="col-md-6 border-right border-3">
                    <p>
                        <c:if test="${embryoViabilityDTO==null && viabilityDTO==null}">
                            A <b>${parameter.procedureNames[0]}</b> phenotypic assay was performed on <b>${numberMice} <c:if test="${!isPostnatal}">embryo</c:if><c:if test="${isPostnatal}">mice</c:if></b>. The charts show the results of measuring <b>${parameter.name}</b> in <b>${numberFemaleMutantMice}
                            female</b>, <b>${numberMaleMutantMice} male</b> mutants compared to
                            <b>${numberFemaleControlMice} female</b>, <b>${numberMaleControlMice} male</b> controls.  The
                            mutants are <b>${zygosity}</b> for the <b><t:formatAllele>${alleleSymbol}</t:formatAllele></b> allele.
                        </c:if>

                        <c:if test="${embryoViabilityDTO!=null || viabilityDTO!=null}">
                            A <b>${parameter.procedureNames[0]}</b> phenotypic assay was performed on a mutant strain carrying the <b><t:formatAllele>${alleleSymbol}</t:formatAllele></b> allele. The
                            charts below show the proportion of wild type, heterozygous, and homozygous offspring.
                        </c:if>
                    </p>

                    <c:if test="${numberMice > 500}">
                        <small>* The high throughput nature of the IMPC means that large control sample sizes may accumulate over a long period of time.  See the <a href="${cmsBaseUrl}/about-impc/animal-welfare">animal welfare guidelines</a> for more information.</small>
                    </c:if>
                </div>

                <div class="col-md-6">
                    <table class="table table-borderless table-reduced-padding">
                        <tbody>
                        <tr>
                            <td>Testing protocol</td>
                            <td><a class="font-weight-bold" href="${procedureUrl}">${parameter.procedureNames[0]}</a></td>
                        </tr>
                        <tr>
                            <td>Testing environment</td>
                            <td><a class="font-weight-bold w-100" data-toggle="modal" data-target="#conditions" href="#">Lab conditions and equipment</a></td>
                        </tr>
                        <tr>
                            <td>Measured value</td>
                            <td class="font-weight-bold">${parameter.name}</td>
                        </tr>
                        <tr>
                            <td>Life stage</td>
                            <td class="font-weight-bold">${lifeStage} </td>
                        </tr>
                        <tr>
                            <td>Background Strain</td>
                            <td class="font-weight-bold"><t:formatAllele>${geneticBackgroundString}</t:formatAllele></td>
                        </tr>
                        <tr>
                            <td>Phenotyping center</td>
                            <td class="font-weight-bold">${phenotypingCenter}</td>
                        </tr>
                        <tr>
                            <td>Associated Phenotype</td>
                            <c:if test="${phenotypes != null && phenotypes.size() >= 1}"><td><c:forEach var="phenotype" items="${phenotypes}" varStatus="mpIndex"><div><a class="font-weight-bold" href="${baseUrl}/phenotypes/${phenotypeIds[mpIndex.index]}">${phenotype}</a></div></c:forEach></td></c:if>
                            <c:if test="${phenotypes == null || phenotypes.size() < 1}"><td>No significant association</td></c:if>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

        </c:if>


        <p class="alert alert-info w-100 my-3">
Mouseover the charts for more information. Click and drag to zoom the chart. Click on the legends to disable/enable data.</p>

        <c:choose>
            <c:when test="${param['chart_type'] eq 'TEXT'}">
                <jsp:include page="textFrag.jsp"/>
            </c:when>
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
