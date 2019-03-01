<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 23/02/2016
  Time: 10:37
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<div class="container-fluid">
<div class="row flex-xl-nowrap">
    <div class="col-lg-8">
        <!-- only display a normal div if no phenotype icons displayed -->
        <div id="phenoSumSmallDiv">
            <div class="row no-gutters">
                <div class="col-md-2 align-middle text-right pr-1">
                    <div class="align-middle font-weight-bold pr-2">Name</div>
                </div>
                <div class="col-md-10 align-middle">
                    <span>${gene.markerName}</span>
                </div>
            </div>
            <div class="row no-gutters">
                <div class="col-md-2 align-middle text-right pr-1">
                    <div class="align-middle font-weight-bold pr-2">MGI ID</div>
                </div>
                <div class="col-md-10 align-middle">
                    <span>${gene.mgiAccessionId}</span>
                </div>
            </div>
            <c:if test="${!(empty gene.markerSynonym)}">
                <div class="row no-gutters">
                    <div class="col-md-2 align-middle text-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Synonyms</div>
                    </div>
                    <div class="col-md-10 align-middle">
                        <c:if test='${fn:length(gene.markerSynonym) gt 1}'>

                            <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                                <span>${synonym}</span>
                            </c:forEach>

                        </c:if>
                        <c:if test='${fn:length(gene.markerSynonym) == 1}'>

                            <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                                <span>${synonym}</span>
                                <c:if test="${!loop.last}">,&nbsp;</c:if>
                            </c:forEach>

                        </c:if>
                    </div>
                </div>
            </c:if>
            <c:if test="${viabilityCalls != null && viabilityCalls.size() > 0}">
                <div class="row no-gutters">
                    <div class="col-md-2 align-middle text-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Viability</div>
                    </div>
                    <div class="col-md-10 align-middle">
                        <t:viabilityButton callList="${viabilityCalls}" link=""></t:viabilityButton>
                    </div>
                </div>
            </c:if>
            <c:if test="${viabilityCalls != null && viabilityCalls.size() > 0}">
                <div class="row no-gutters">
                    <div class="col-md-2 align-middle text-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Other links</div>
                    </div>
                    <div class="col-md-10 align-middle">
                        <a target="_blank" class="page-nav-link"
                           href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}"
                           title="See gene page at JAX" style="font-size: initial; display: inline;">MGI &nbsp;<i
                                class="fas fa-external-link"></i></a>
                        <a target="_blank" class="page-nav-link"
                           href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.mgiAccessionId}"
                           title="Visualise mouse gene with ensembl genome broswer"
                           style="font-size: initial; display: inline;">Ensembl &nbsp;<i
                                class="fas fa-external-link"></i></a>
                    </div>
                </div>
            </c:if>
            <div class="row no-gutters justify-content-around mt-3 text-center page-content">
                <a href="#phenotypesTab" class="col-sm-2" onclick="$('#significant-tab').trigger('click')">
                    <i class="fal fa-file-medical-alt mb-1 text-dark" style="font-size: 5em;"></i>
                    <span class="page-nav-link">Significant phenotypes</span>
                </a>
                <a href="#phenotypesTab" class="col-sm-2" onclick="$('#alldata-tab').trigger('click')">
                    <i class="fal fa-ruler-combined mb-1 text-dark" style="font-size: 5em;"></i>
                    <span class="page-nav-link">All measurements</span>
                </a>
                <!--a class="col-sm-2">
                    <i class="icon icon-conceptual icon-expression" style="font-size: 5em;"></i>
                    <span style="display: block; font-size: smaller"></span>
                </a-->
                <a href="#expression" class="col-sm-2">
                    <i class="fal fa-images mb-1 text-dark" style="font-size: 5em;" data-toggle="tooltip"
                       data-placement="top"></i>
                    <span class="page-nav-link">Expression & images</span>
                </a>
                <a href="#diseases" class="col-sm-2">
                    <i class="fal fa-procedures mb-1 text-dark" style="font-size: 5em;" data-toggle="tooltip"
                       data-placement="top"></i>
                    <span class="page-nav-link">Disease models</span>
                </a>
                <a href="#order" class="col-sm-2">
                    <i class="fal fa-shopping-cart mb-1 text-dark" style="font-size: 5em;" data-toggle="tooltip"
                       data-placement="top"></i>
                    <span class="page-nav-link">Order</span>
                </a>
            </div>


        </div>


        <%-- <p> No hits that meet the p value threshold. <jsp:include page="heatmapFrag.jsp"/></p> --%>
        <c:if test="${ attemptRegistered && !phenotypeStarted }">
            <div class="alert alert-info">
                <h5>Registered for phenotyping</h5>

                <p>Phenotyping is planned for a knockout strain of this gene but
                    data is not currently available.</p>
            </div>
        </c:if>


        <c:if test="${!attemptRegistered}">
            <div class="alert alert-info">
                <h5>Not currently registered for phenotyping</h5>

                <p>Phenotyping is currently not planned for a knockout strain of this gene.
                </p>
            </div>
        </c:if>

    </div>
    <div class="col-lg-4">
        <jsp:include page="phenotype_icons_frag.jsp"/>
        <c:if test="${ bodyWeight }">
            <div id="all_data" class="with-label text-center">

                <c:if test="${bodyWeight}">
                    <a id="bodyWeightBtn" class="btn btn-outline-primary mt-2"
                       href="${baseUrl}/charts?accession=${acc}&parameter_stable_id=IMPC_BWT_008_001&&chart_type=TIME_SERIES_LINE"
                       title="Body Weight Curves" style="display: inline-block; max-width: 300px; width: 80%;">Body
                        weight</a>
                </c:if>
            </div>
        </c:if>
    </div>
</div>


<div id="phenotypes"></div>
<!-- Empty anchor for links, used for disease paper. Don't remove. -->


<c:if test='${hasPreQcThatMeetsCutOff || rowsForPhenotypeTable.size() > 0}'>


    <ul class="nav nav-tabs" id="phenotypesTab" role="tablist">
        <li class="nav-item">
            <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                <a class="nav-link active" id="significant-tab" data-toggle="tab" href="#significant"
                   role="tab" aria-controls="significant-tab" aria-selected="true"><i
                        class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes</a>
            </c:if>
            <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                <a class="nav-link" id="significant-tab" data-toggle="tab" href="#significant"
                   role="tab" aria-controls="significant-tab" aria-selected="true"><i
                        class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes</a>
            </c:if>

        </li>
        <li class="nav-item">
            <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                <a class="nav-link" id="alldata-tab" data-toggle="tab" href="#alldata"
                   role="tab" aria-controls="alldata-tab" aria-selected="false"><i class="fal fa-ruler-combined"></i>&nbsp;
                    All measurements</a>
            </c:if>
            <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                <a class="nav-link active" id="alldata-tab" data-toggle="tab" href="#alldata"
                   role="tab" aria-controls="alldata-tab" aria-selected="false"><i class="fal fa-ruler-combined"></i>&nbsp;
                    All measurements</a>
            </c:if>
        </li>
    </ul>

    <div class="tab-content" id="phenotypesTabContent" id="phenotypeAssociations">
        <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
            <div class="tab-pane fade show" id="significant" role="tabpanel"
                 aria-labelledby="significant-tab">
                <c:if test="${ attemptRegistered && phenotypeStarted }">
                    <div class="alert alert-warning mt-3" role="alert">
                        No results meet the p-value threshold
                    </div>
                </c:if>
            </div>
        </c:if>
        <!-- Associations table -->
        <c:if test='${rowsForPhenotypeTable.size() > 0}'>
            <div class="tab-pane fade show active" id="significant" role="tabpanel"
                 aria-labelledby="significant-tab">
                <div id="phenotypeTableDiv" class="inner-division">
                    <div class="row">
                        <div class="container">

                            <div class="row" id="phenotypesDiv">

                                <div class="container">

                                    <c:if test="${not empty rowsForPhenotypeTable}">
                                    <div class="row">
                                        <div class="col" id="target" action="destination.html">

                                                <%--c:forEach
                                                        var="phenoFacet" items="${phenoFacets}"
                                                        varStatus="phenoFacetStatus">
                                                    <select id="top_level_mp_term_name" class="selectpicker"
                                                            multiple="multiple"
                                                            title="Filter on ${phenoFacet.key}">
                                                        <c:forEach
                                                                var="facet" items="${phenoFacet.value}">
                                                            <option>${facet.key}</option>
                                                        </c:forEach>
                                                    </select>
                                                </c:forEach--%>

                                        </div>
                                    </div>


                                    <c:set var="count" value="0" scope="page"/>
                                    <c:forEach
                                            var="phenotype" items="${rowsForPhenotypeTable}"
                                            varStatus="status">
                                        <c:forEach
                                                var="sex" items="${phenotype.sexes}">
                                            <c:set var="count" value="${count + 1}" scope="page"/>
                                        </c:forEach>
                                    </c:forEach>

                                    <jsp:include page="PhenoFrag.jsp"></jsp:include>

                                    <div id="export">
                                        <p class="textright">
                                            Download data as:
                                            <a id="tsvDownload"
                                               href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=tsv&fileName=${gene.markerSymbol}"
                                               target="_blank" class="btn btn-outline-primary"><i
                                                    class="fa fa-download"></i>&nbsp;TSV</a>
                                            <a id="xlsDownload"
                                               href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=xls&fileName=${gene.markerSymbol}"
                                               target="_blank" class="btn btn-outline-primary"><i
                                                    class="fa fa-download"></i>&nbsp;XLS</a>
                                        </p>
                                        </c:if>
                                    </div>


                                </div>
                            </div>
                        </div>
                    </div>

                </div><!-- end of div for mini section line -->
            </div>
        </c:if>
        <c:if test='${rowsForPhenotypeTable.size() > 0}'>
        <div class="tab-pane fade show" id="alldata" role="tabpanel"
             aria-labelledby="alldata-tab">
            </c:if>
            <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
            <div class="tab-pane fade show active" id="alldata" role="tabpanel"
                 aria-labelledby="alldata-tab">
                </c:if>
                <p class="mt-3">
                    Filtered by: <span id="phDataTitle"> all phenotypes</span>
                </p>
                <div id="all-chart">
                    <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                        <jsp:include page="/experimentsFrag" flush="true">
                            <jsp:param name="geneAccession"
                                       value="${gene.mgiAccessionId}"/>
                        </jsp:include>
                    </c:if>
                        <%--p class="textright">
                            Download data as:
                            <a id="tsvDownload"
                               href="${baseUrl}/experiments/export?${requestScope['javax.servlet.forward.query_string']}&fileType=tsv&fileName=allData${allelePageDTO.getGeneSymbol()}"
                               target="_blank" class="button fa fa-download">TSV</a>
                            <a id="xlsDownload"
                               href="${baseUrl}/experiments/export?${requestScope['javax.servlet.forward.query_string']}&fileType=xls&fileName=allData${allelePageDTO.getGeneSymbol()}"
                               target="_blank" class="button fa fa-download">XLS</a>
                        </p--%>
                </div>
            </div>

        </div>
    </div>
</c:if>
    </div>

<c:if test='${rowsForPhenotypeTable.size() > 0}'>
    <script>
        var first = true;
        $("#alldata-tab").on('click', function () {
            if (first) {
                $('#all-chart').html("     <div class=\"pre-content\">\n" +
                    "                        <div class=\"row no-gutters\">\n" +
                    "                            <div class=\"col-12 my-5\">\n" +
                    "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
                    "                            </div>\n" +
                    "                        </div>\n" +
                    "                    </div>");
                $.ajax({
                    url: '/data/experimentsFrag?geneAccession=' + '${gene.mgiAccessionId}',
                    type: 'GET',
                    success: function (data) {
                        $('#all-chart').html(data);
                        first = false;
                    }
                });
            }
        });
    </script>
</c:if>

