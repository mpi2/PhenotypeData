<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>


<t:genericpage-landing>

    <jsp:attribute name="title">${systemName}</jsp:attribute>
    <jsp:attribute name="pagename">${pageTitle}</jsp:attribute>
    <jsp:attribute name="breadcrumb">${systemName}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
        <!-- CSS Local Imports -->
		<link rel="stylesheet" href="${baseUrl}/css/vendor/slick.grid.css" type="text/css" media="screen"/>
		<link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css" />
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
        <link href="${baseUrl}/css/biological_system/style.css" rel="stylesheet" />

        <!-- JS Imports -->
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

        <!-- parallel coordinates JavaScriptdependencies -->

		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.csv.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>
        <script src="${baseUrl}/js/vendor/jquery/jquery.event.drag-2.0.min.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.core.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.grid.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.dataview.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.pager.js"></script>

	</jsp:attribute>



    <jsp:attribute name="addToFooter"></jsp:attribute>


    <jsp:body>

        <div class="container">
            <div class="row">
                <div class="col-12">
                    <p>
                        This page introduces cardiovascular related phenotypes present in mouse lines produced by the
                        IMPC. The cardiovascular system
                        refers to the observable morphological and physiological characteristics of the mammalian heart,
                        blood vessels, or circulatory
                        system that are manifested through development and lifespan.
                    </p>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                <c:import url="landing_overview_frag.jsp"/>
                </div>
            </div>

            <div class="row">
                <div class="col-12">

                    <h2 id="approach">Approach</h2>

                        <p>In order to identify the function of genes, the consortium uses a series of
                            standardised protocols as described in <a href="${baseUrl}/../impress">IMPReSS</a> (International Mouse Phenotyping Resource of Standardised Screens).</p>
                        <p>Heart and vascular function/physiology are measured through several procedures like echocardiography and electrocardiogram and non-invasive blood pressure.
                            Cardiovascular system morphology is assessed through macroscopic and microscopic measurements, like heart weight,
                            gross pathology and gross morphology in both embryo and adult animals. A complete list of protocols and related phenotypes are
                            presented in the table below. Links to impress are provided for more details on the procedure. </p>

                        <h4>Procedures that can lead to relevant phenotype associations</h4>
                        <c:import url="landing_procedures_frag.jsp"/>

                </div>
            </div>

            <div class="row">
                <div class="col-12">

                    <h2 class="title">Phenotypes distribution</h2>

                    <p>This graph shows genes with a significant effect on at least one cardiovascular system phenotype.</p>

                    <div id="phenotypeChart">
                        <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                    </div>

                    <p>The following diagram represents the various biological system phenotypes associations for
                        genes linked to cardiovascular system phenotypes. The line thickness is correlated with the
                        strength of the association.</p>
                    <p>Clicking on chosen phenotype(s) on the diagram allow to select common genes. Corresponding
                        gene lists can be downloaded using the download icon.</p>

                    <div id="chordContainer"></div>
                    <svg id="chordDiagramSvg" width="960" height="960"></svg>
                    <script>
                        var mpTopLevelTerms = ["cardiovascular system phenotype"];
                        drawChords("chordDiagramSvg", "chordContainer", false, mpTopLevelTerms, false, null, false);
                    </script>

                </div>
            </div>

            <div class="row">
                <div class="col-12" id="gene-ko-effect" >

                    <h2 class="title">Gene KO effect comparator for ${systemName} continuous parameters</h2>


                    <p>The parallel coordinates viewer allows to compare variation across parameters for a particular mouse line, as well as across lines, for continous parameters.</p>

                    <p>The values displayed are the genotype effect. The measurement values are corrected to account for batch effects to represent the true genotype effect, thus allowing a side-by-side comparison.</p>

                    <p>Use this interactive graph and table:</p>

                    <ul>
                        <li>Drag your mouse pointer on any parameter axis to select a region of interest and highlight the relevant mouse lines in the chart. The associated gene/s will be automatically filtered for in the gene table below.</li>
                        <li>You can click on a line to highlight it. The associated gene will be automatically filtered for in the gene table below.</li>
                        <li>You can filter by procedure. You can learn more about the parameters by searching for them in <a href="${cmsBaseUrl}/impress">IMPReSS</a></li>
                        <li>Click on any row in the gene table to highlight the corresponding values in the graph above. Clicking on the gene name will open the associated gene page.</li>
                        <li>When you select a gene row, the parameter name in the graph will change to bold if genotype is significant.</li>
                        <li>Reload page to return to the default view.</li>
                    </ul>


                    <div id="widgets_pc" class="widgets">	</div>
                    <div id="spinner"><i class="fa fa-refresh fa-spin"></i></div>
                    <br style="clear: both" />
                    <div id="chart-and-table"> </div>
                    <script>
                        $(document).ready(function(){
                            var base_url = '${baseUrl}';
                            var tableUrl = base_url + "/parallelFrag?top_level_mp_id=${mpId}";
                            console.log(tableUrl);
                            $.ajax({
                                url: tableUrl,
                                cache: false
                            })
                                .done(function( html ) {
                                    $( '#spinner' ).hide();
                                    $( '#chart-and-table' ).html( html );
                                });
                        })
                    </script>

                </div>
            </div>

            <div class="row">
                <div class="col-12" id="paper">

                    <jsp:include page="paper_frag.jsp"></jsp:include>

                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage-landing>


