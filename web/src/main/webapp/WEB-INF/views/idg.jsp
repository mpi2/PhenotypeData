<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

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
                        <div  class="col-6">
                           <div id="idgOrthologPie">
			            		<script type="text/javascript">${idgOrthologPie}</script>
                            </div>
                        </div>
                        <div  class="col-6">
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
                    <div id="chordContainerIonChannels" class="half"></div>
                    <svg id="chordDiagramSvgIonChannels" width="960" height="960"></svg>
                    <script>
                        drawChords("chordDiagramSvgIonChannels", "chordContainerIonChannels", false, [], true, "IC", true);
                    </script>

                    <h3>GPCRs</h3>
                    <div id="chordContainerGPCRs" class="half"></div>
                    <svg id="chordDiagramSvgGPCRs" width="960" height="960"></svg>
                    <script>
                        drawChords("chordDiagramSvgGPCRs", "chordContainerGPCRs", false, [], true, "GPCR", true);
                    </script>


                    <h3>Kinases</h3>
                    <div id="chordContainerKinases" class="half"></div>
                    <svg id="chordDiagramSvgKinases" width="960" height="960"></svg>
                    <script>
                        drawChords("chordDiagramSvgKinases", "chordContainerKinases", false, [], true, "Kinase", true);
                    </script>
                </div>
            </div>

    </jsp:body>


</t:genericpage-landing>
