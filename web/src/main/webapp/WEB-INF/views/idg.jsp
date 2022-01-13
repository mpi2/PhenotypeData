<%@ page import="org.mousephenotype.cda.solr.service.EssentialGeneService" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage-landing>

    <jsp:attribute name="title">IDG | IMPC Project Information</jsp:attribute>
    <jsp:attribute name="pagename">Illuminating the Druggable Genome (IDG)</jsp:attribute>
    <jsp:attribute name="breadcrumb">IDG</jsp:attribute>

    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script><script
            type="text/javascript" src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <link rel="stylesheet" href="https://cdn.datatables.net/fixedcolumns/3.3.0/css/fixedColumns.dataTables.min.css" />
        <script src="https://cdn.datatables.net/fixedcolumns/3.3.0/js/dataTables.fixedColumns.min.js"></script>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">

        <script>
            $(document).ready(function () {
                $.fn.qTip({
                    'pageName': 'idg',
                    'textAlign': 'left',
                    'tip': 'topLeft'
                }); // bubble popup for brief panel documentation
            });
            var geneHeatmapUrl = "../geneHeatMap?project=idg";
            $.ajax({
                url: geneHeatmapUrl,
                cache: false
            }).done(function (html) {
                $('#geneHeatmap').append(html);
                //$( '#spinner'+ id ).html('');

            });
            $('#publications_table').bootstrapTable({ classes: 'table'});
        </script>

    </jsp:attribute>


    <jsp:body>
        <!-- Assign this as a variable for other components -->
        <script type="text/javascript">
            var base_url = '${baseUrl}';
        </script>

        <div class="row">
                <div class="col-9">
                    <p>
                        <a href="https://commonfund.nih.gov/idg/index">IDG</a> is an NIH Common Fund project focused on collecting, integrating and making available biological
                        data on ${idgGeneCount} human genes from three key druggable protein families that have been identified
                        as potential therapeutic targets: non-olfactory G-protein coupled receptors (GPCRs), ion channels,
                        and protein kinases. The <a href="http://dev.mousephenotype.org/data/documentation/aboutImpc">IMPC consortium</a> is creating knockout mouse strains for the IDG project to
                        better understand the function of these proteins.
                    </p>
                </div>
                <div class="col-3">
                    <img src="${baseUrl}/img/idgLogo.png" width="100%">
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <h2 id="section-associations">IMPC data representation for IDG genes</h2>
                    <p>
                        IDG human genes are mapped to mouse orthologs using <a href="${baseUrl}/secondaryproject/idg/mapping">HomoloGene</a>. The
                        <a href="${cmsBaseUrl}/about-impc/">IMPC consortium</a> is using different <a href="${baseUrl}/documentation/aboutImpc#howdoesimpcwork">complementary
                        targeting strategies</a> to produce Knockout strains. Mice are produced and submitted to standardised phenotyping pipelines.
                        Currently ${idgPercent} % of mouse IDG gene have data representation in IMPC, the bar charts and heatmap below capture the
                        IMPC data representation at different levels. The percentage might increase as we get more data and this page will reflect the change.
                    </p>

<%--                    <h1>Maintenance</h1>--%>
<%--                    <p>This page integrating the status of the <strong>IDG</strong> project with the <strong>International Mouse Phenotyping Consortium</strong>--%>
<%--                        is temporarily down for maintenance.</p>--%>

                    <div class="row">
                        <div  class="col-md-6">
                           <div id="idgOrthologPie">
			            		<script type="text/javascript">${idgOrthologPie}</script>
                            </div>
                        </div>
                        <div  class="col-md-6">
                            <div id=idgChart>
			            		<script type="text/javascript">${idgChartTable.getChart()}</script>
                            </div>
                        </div>
                    </div>

                    <h4>IMPC IDG data Heat Map</h4>
                    <p>
                        The heat map indicates the detailed IDG gene data representation in IMPC, from product availability to phenotypes.
                        Phenotypes are grouped by biological systems.
                    </p>

                    <div id="geneHeatmap" class="geneHeatMap" style="overflow: hidden; overflow-x: auto;"></div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <h2 id="section-associations">Phenotype Associations</h2>
                    <p>The following chord diagrams represent the various biological systems phenotype associations for IDG genes categorized both in all and in each family group. The line thickness is correlated with the strength of the association.
                        Clicking on chosen phenotype(s) on the diagram allow to select common genes. Corresponding gene lists can be downloaded using the download icon.</p>

                    <h3>All families</h3>
                    <div id="chordContainer" class="half"></div>
                    <svg id="chordDiagramSvg" width="960" height="960"></svg>
                    <script>
                        drawChords("chordDiagramSvg", "chordContainer", false, [], true, null, true);
                    </script>

                    <h3>Ion channels</h3>
                    <table class="table w-25">
                        <tr>
                            <th>IMPC/IDG genes</th><td>${fn:length(ionChannelRows)}</td>
                        </tr>
                        <tr>
                            <th>ES Cells produced</th><td>${ionChannelEsCellsProduced}</td>
                        </tr>
                        <tr>
                            <th>Mice produced</th><td>${ionChannelMiceProduced}</td>
                        </tr>
                        <tr>
                            <th>Phenotypes</th><td>${ionChannelPhenotypesProduced}</td>
                        </tr>
                    </table>
                    <button class="btn btn-success mb-3" data-toggle="collapse" href="#ionChannelTable" role="button" aria-expanded="false" aria-controls="ionChannelTable">View all ${fn:length(ionChannelRows)} IMPC/IDG Ion Channel genes</button>
                    <div id="ionChannelTable" class="collapsed collapse">
                        <table class="table-condensed w-100 table-bordered table-striped">
                            <thead>
                                <tr>
                                    <th>Mouse Genes</th>
                                    <th>Human Genes</th>
                                    <th style="width: 340px;">Data available</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${ionChannelRows}" var="row">
                                <tr>
                                    <td><a href="${baseUrl}/genes/${row.accession}" data-tooltip="">${row.symbol}</a> (${row.accession})</td>
                                    <td>
                                        <c:forEach var="h_gene" items="${fn:split(row.humanSymbolToString,' ')}">
                                            <a target="_blank" rel="noopener" href="https://pharos.nih.gov/targets?q=${h_gene}">${h_gene} <i
                                                    class="fas fa-external-link"></i></a>
                                        </c:forEach>
                                    </td>
                                    <td>${row.miceProduced}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div id="chordContainerIonChannels" class="half"></div>
                    <svg id="chordDiagramSvgIonChannels" width="960" height="960" class="d-none d-md-block"></svg>
                    <script>
                        drawChords("chordDiagramSvgIonChannels", "chordContainerIonChannels", false, [], true, "${ION_CHANNEL}", true);
                    </script>

                    <h3>GPCRs</h3>
                    <table class="table w-25">
                        <tr>
                            <th>IMPC/IDG genes</th><td>${fn:length(gpcrRows)}</td>
                        </tr>
                        <tr>
                            <th>ES Cells produced</th><td>${gpcrEsCellsProduced}</td>
                        </tr>
                        <tr>
                            <th>Mice produced</th><td>${gpcrMiceProduced}</td>
                        </tr>
                        <tr>
                            <th>Phenotypes</th><td>${gpcrPhenotypesProduced}</td>
                        </tr>
                    </table>
                    <button class="btn btn-success mb-3" data-toggle="collapse" href="#gpcrTable" role="button" aria-expanded="false" aria-controls="gpcrTable">View all ${fn:length(gpcrRows)} IMPC/IDG GPCR genes</button>
                    <div id="gpcrTable" class="collapsed collapse">
                        <table class="table-condensed w-100 table-bordered table-striped">
                            <thead>
                            <tr>
                                <th>Mouse Genes</th>
                                <th>Human Genes</th>
                                <th style="width: 340px;">Data available</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${gpcrRows}" var="row">
                                <tr>
                                    <td><a href="${baseUrl}/genes/${row.accession}" data-tooltip="">${row.symbol}</a> (${row.accession})</td>
                                    <td>
                                        <c:forEach var="h_gene" items="${fn:split(row.humanSymbolToString,' ')}">
                                            <a target="_blank" rel="noopener" href="https://pharos.nih.gov/targets?q=${h_gene}">${h_gene} <i
                                                    class="fas fa-external-link"></i></a>
                                        </c:forEach>
                                    </td>
                                    <td>${row.miceProduced}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div id="chordContainerGPCRs" class="half"></div>
                    <svg id="chordDiagramSvgGPCRs" width="960" height="960" class="d-none d-md-block"></svg>
                    <script>
                        drawChords("chordDiagramSvgGPCRs", "chordContainerGPCRs", false, [], true, "${GPCR}", true);
                    </script>


                    <h3>Kinases</h3>
                    <table class="table w-25">
                        <tr>
                            <th>IMPC/IDG genes</th><td>${fn:length(kinaseRows)}</td>
                        </tr>
                        <tr>
                            <th>ES Cells produced</th><td>${kinaseEsCellsProduced}</td>
                        </tr>
                        <tr>
                            <th>Mice produced</th><td>${kinaseMiceProduced}</td>
                        </tr>
                        <tr>
                            <th>Phenotypes</th><td>${kinasePhenotypesProduced}</td>
                        </tr>
                    </table>
                    <button class="btn btn-success mb-3" data-toggle="collapse" href="#kinaseTable" role="button" aria-expanded="false" aria-controls="kinaseTable">View all ${fn:length(kinaseRows)} IMPC/IDG Kinase genes</button>
                    <div id="kinaseTable" class="collapsed collapse">
                        <table class="table-condensed w-100 table-bordered table-striped">
                            <thead>
                            <tr>
                                <th>Mouse Genes</th>
                                <th>Human Genes</th>
                                <th style="width: 340px;">Data available</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${kinaseRows}" var="row">
                                <tr>
                                    <td><a href="${baseUrl}/genes/${row.accession}" data-tooltip="">${row.symbol}</a> (${row.accession})</td>
                                    <td>
                                        <c:forEach var="h_gene" items="${fn:split(row.humanSymbolToString,' ')}">
                                            <a target="_blank" rel="noopener" href="https://pharos.nih.gov/targets?q=${h_gene}">${h_gene} <i
                                                    class="fas fa-external-link"></i></a>
                                        </c:forEach>
                                    </td>
                                    <td>${row.miceProduced}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div id="chordContainerKinases" class="half"></div>
                    <svg id="chordDiagramSvgKinases" width="960" height="960" class="d-none d-md-block"></svg>
                    <script>
                        drawChords("chordDiagramSvgKinases", "chordContainerKinases", false, [], true, "${KINASE}", true);
                    </script>
                </div>
            </div>

        <%-- PUBLICATIONS SECTION --%>
        <div class="container white-bg-small" id="publications-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-5">
                                <h2>
                                    IMPC related publications
                                    <a href="${cmsBaseUrl}/help/data-visualization/gene-pages/publications/" title="Go to publication help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                                <p>
                                    The table below lists publications which used either products generated by the IMPC or data produced by the phenotyping efforts of the IMPC. These publications have also been associated to ${gene.markerSymbol}.
                                </p>
                            </div>
                            <div>
                                <div class="row">
                                    <div class="col-12">
                                        <c:choose>
                                            <c:when test="${fn:length(publications) != 0}">
                                                <p class="alert alert-info">There are <b>${fn:length(publications)} publication<c:if test="${fn:length(publications)!=1}">s</c:if> which use IMPC produced mice or data.</b></p>
                                                <table id="publications_table"
                                                       data-pagination="true"
                                                       data-mobile-responsive="true"
                                                       data-sortable="true">
                                                    <thead>
                                                    <tr>
                                                        <th data-width="300">Title</th>
                                                        <th>Journal</th>
                                                        <th>IMPC Allele</th>
                                                        <th data-width="160" data-halign="center" data-align="center">PubMed&nbsp;ID</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <c:forEach items="${publications}" var="publication" varStatus="loop">
                                                        <tr id="publicationRow${loop.index}" data-link="publication" data-shown="false">
                                                            <td><a href="https://www.doi.org/${publication.doi}">${publication.title}</a></td>
                                                            <td>${publication.journalInfo.journal.title} (<fmt:formatDate value="${publication.firstPublicationDate}" pattern="MMMM yyyy" />)</td>
                                                            <td><c:forEach items="${publication.alleles}" var="allele" varStatus="allele_loop">
                                                                <t:formatAllele>${allele.alleleSymbol}</t:formatAllele>
                                                            </c:forEach>
                                                            </td>
                                                            <td><c:choose>
                                                                <c:when test="${fn:length(publication.pmcid) > 0}"><a href="https://www.ncbi.nlm.nih.gov/pmc/articles/${publication.pmcid}">${publication.pmcid}</a></c:when>
                                                                <c:when test="${fn:length(publication.pmid) > 0}"><a href="https://pubmed.ncbi.nlm.nih.gov/${publication.pmid}/">${publication.pmid}</a></c:when>
                                                            </c:choose>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:when>
                                            <c:when test="${fn:length(publications) == 0}">No publications found that use IMPC mice or data for ${gene.markerSymbol}.</c:when>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>



    </jsp:body>


</t:genericpage-landing>
