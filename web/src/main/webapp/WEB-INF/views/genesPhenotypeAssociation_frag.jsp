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
            <c:if test="${phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}">
            <div id="phenoSumDiv">
                </c:if>
                <c:if test="${!phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}">
                <!-- only display a normal div if no phenotype icons displayed -->
                <div id="phenoSumSmallDiv">
                    </c:if>
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
                                    <a target="_blank" class="page-nav-link" href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}"
                                       title="See gene page at JAX" style="font-size: initial; display: inline;">MGI &nbsp;<i class="fas fa-external-link"></i></a>
                                    <a target="_blank" class="page-nav-link" href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.mgiAccessionId}"
                                       title="Visualise mouse gene with ensembl genome broswer" style="font-size: initial; display: inline;">Ensembl &nbsp;<i class="fas fa-external-link"></i></a>
                                </div>
                            </div>
                        </c:if>
                        <div class="row no-gutters justify-content-center mt-3 text-center page-content">
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
                                <i class="fal fa-images mb-1 text-dark" style="font-size: 5em;" data-toggle="tooltip" data-placement="top"></i>
                                <span class="page-nav-link">Expression & images</span>
                            </a>
                            <a href="#diseases" class="col-sm-2">
                                <i class="fal fa-procedures mb-1 text-dark" style="font-size: 5em;" data-toggle="tooltip" data-placement="top"></i>
                                <span class="page-nav-link">Disease models</span>
                            </a>
                            <a href="#order" class="col-sm-2">
                                <i class="fal fa-shopping-cart mb-1 text-dark" style="font-size: 5em;" data-toggle="tooltip" data-placement="top"></i>
                                <span class="page-nav-link">Order</span>
                            </a>
                        </div>



                    <%--c:if test="${gene.embryoDataAvailable || gene.embryoAnalysisUrl!=null || gene.dmddImageDataAvailable || hasVignette}">
                        <div id="embryo" class="with-label">
                            <c:if test="${gene.embryoDataAvailable}">
                                <a id="embryoViewerBtn" class="btn" href="${drupalBaseUrl}/embryoviewer/?mgi=${acc}" title="3D Embryo Images are Available">3D Embryo Imaging</a>
                            </c:if>

                            <c:if test="${gene.embryoAnalysisUrl!=null}">
                                <a id="embryoAnalysisBtn" class="btn" href="${gene.embryoAnalysisUrl}" title="Automated 3D Volumetric Analysis of Embryo images">3D Embryo Vol Analysis</a>
                            </c:if>

                            <c:if test="${gene.dmddImageDataAvailable}">
                                <a id="DmddViewerBtn" class="btn" href="https://dmdd.org.uk/mutants/${gene.markerSymbol}" target="_blank" title="Embryo Images and Manual Phenotypes from the DMDD project" >DMDD Embryo</a>
                            </c:if>

                            <c:if test="${hasVignette}">
                                <a class="btn" href="${baseUrl}/embryo/vignettes#${acc}" title="embryo vignette exists for this gene">Embryo Vignette</a>
                            </c:if>
                        </div>
                    </c:if--%>


                    <c:if test="${phenotypeDisplayStatus.displayHeatmap}">
                        <div id="heatmap_toggle_div" class="section hidden">
                            <h2 class="title" id="heatmap">Phenotype Heatmap of Preliminary Data
                                <span class="documentation"><a href='' id='heatmapSection'
                                                               class="fa fa-question-circle pull-right"></a></span>
                            </h2>

                            <div class="dcc-heatmap-root">
                                <div class="phenodcc-heatmap"
                                     id="phenodcc-heatmap"></div>
                            </div>
                        </div>
                        <!-- end of Pre-QC phenotype heatmap -->
                    </c:if>


                </div>


                <c:if test="${!phenotypeDisplayStatus.eitherPostQcOrPreQcSignificantDataIsAvailable}"><!-- no significant postQC data or preQcData-->

                    <%--  <c:choose> --%>
                <c:if test="${ attemptRegistered && phenotypeStarted }">
                    No results meet the p-value threshold
                </c:if>
                    <%-- <c:if test="${phenotypeDisplayStatus.postQcDataAvailable}">
                                No significant phenotype associations were found with data that has
                             passed quality control (QC), but you can click
                             on the "All Adult Data" button to see all phenotype data that has
                             passed QC. Preliminary phenotype assocations
                             may appear with new pre-QC phenotype data.
                    </c:if> --%>
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

                <br/>
                </c:if>
            </div>
            <div class="col-lg-4">
                <c:if test="${phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}">
                    <jsp:include page="phenotype_icons_frag.jsp"/>
                </c:if>
                <c:if test="${phenotypeDisplayStatus.postQcDataAvailable || phenotypeDisplayStatus.displayHeatmap || bodyWeight }">
                    <div id="all_data" class="with-label text-center">

                        <%--c:if test="${phenotypeDisplayStatus.postQcDataAvailable}">
                            <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->
                            <a id="allAdultDataBtn" class="btn btn-outline-primary mb-2 mt-2"
                               href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}' title="All Data"
                               style="display: inline-block; max-width: 300px; width: 80%;">Phenotype measurements</a>
                        </c:if--%>

                        <c:if test="${bodyWeight}">
                            <a id="bodyWeightBtn" class="btn btn-outline-primary mt-2"
                               href="${baseUrl}/charts?accession=${acc}&parameter_stable_id=IMPC_BWT_008_001&&chart_type=TIME_SERIES_LINE"
                               title="Body Weight Curves" style="display: inline-block; max-width: 300px; width: 80%;">Body
                                weight</a>
                        </c:if>

                        <c:if test="${phenotypeDisplayStatus.displayHeatmap}">
                            <%-- <jsp:include page="heatmapFrag.jsp"/> split out from frag as need to display heatmap in different location to button --%>
                            <c:if test="${phenotypeStarted}">
                                <!--a id="heatmap_link" class="btn">Heatmap / Table</a-->

                            </c:if>
                        </c:if>

                        <!-- Button trigger modal -->
                        <!--button type="button" class="btn btn-outline-primary" data-toggle="modal" data-target="#exampleModal" id="heatmap_link">
                            Preliminary data
                        </button-->

                        <!-- Modal -->
                        <!--div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div class="modal-dialog" role="document" style="max-width: 60%">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="exampleModalLabel">Phenotype Heatmap of Preliminary Data</h5>
                                        <span class="documentation"><a href='' id='heatmapSection' class="fa fa-question-circle pull-right"></a></span>
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </div>
                                    <div class="modal-body">
                                        <div id="heatmap_toggle_div" class="section">
                                            <div class="dcc-heatmap-root">
                                                <div class="phenodcc-heatmap"
                                                     id="phenodcc-heatmap"></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                            </div>
                        </div-->
                    </div>


                </c:if>
            </div>
        </div>

        <%-- <c:if test="${phenotypeDisplayStatus.postQcDataAvailable && !phenotypeDisplayStatus.eitherPostQcOrPreQcSignificantDataIsAvailable}"> don't think we need this section now??
          <div class="alert alert-info">
            <h5>No Significant Phenotype Associations Found</h5>

            <p>No significant phenotype associations were found with data that has
              passed quality control (QC), but you can click
              on the "All Adult Data" button to see all phenotype data that has
              passed QC. Preliminary phenotype assocations
              may appear with new pre-QC phenotype data.</p>
          </div>
          <br/>
          <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->
          <div class="floatright marginup" style="clear: both">
            <a id="allAdultDataBtn" class="btn" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}'>All Adult Data</a>
          </div>
            <div class="clear"></div>
        </c:if> --%>

        <%--   <c:if
                  test="${gene.embryoDataAvailable}">
            <div class="floatright marginup"
                 style="clear: both">
              <a class="btn"
                 href="${drupalBaseUrl}/embryoviewer?mgi=${acc}">3D Imaging</a>
            </div>
          </c:if> --%>
        <%-- </c:when> --%>
        <%-- <c:when test="${hasPreQcThatMeetsCutOff}"> --%>
        <!-- Only pre QC data available, suppress post QC phenotype summary -->
        <%--  </c:when>
         <c:otherwise> --%>
        <%--  <div class="alert alert-info">There are currently no IMPC phenotype associations
           for the gene ${gene.markerSymbol} </div>
         <br/> --%>
        <%--  </c:otherwise> --%>
        <%-- </c:choose> --%>

        <div id="phenotypes"></div> <!-- Empty anchor for links, used for disease paper. Don't remove.  -->


        <%--<c:if test='${hasPreQcThatMeetsCutOff || rowsForPhenotypeTable.size() > 0}'>--%>
        <c:if test='${rowsForPhenotypeTable.size() > 0}'>

            <ul class="nav nav-tabs" id="phenotypesTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="significant-tab" data-toggle="tab" href="#significant"
                       role="tab" aria-controls="significant-tab" aria-selected="true"><i class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="alldata-tab" data-toggle="tab" href="#alldata"
                       role="tab" aria-controls="alldata-tab" aria-selected="false"><i class="fal fa-ruler-combined"></i>&nbsp; All measurements</a>
                </li>
            </ul>
            <div class="tab-content" id="phenotypesTabContent" id="phenotypeAssociations">
                <div class="tab-pane fade show active" id="significant" role="tabpanel"
                     aria-labelledby="significant-tab">
                    <!-- Associations table -->
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
                                            </div>

                                        </c:if>

                                    </div>
                                </div>
                            </div>
                        </div>

                    </div><!-- end of div for mini section line -->
                </div>
                <div class="tab-pane fade show" id="alldata" role="tabpanel"
                     aria-labelledby="alldata-tab">
                    <div id="all-chart">
                        <jsp:include page="/experimentsFrag" flush="true">
                            <jsp:param name="geneAccession"
                                       value="${gene.mgiAccessionId}"/>
                        </jsp:include>
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

        </c:if>
    </div>