<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<t:genericpage>

    <jsp:attribute name="title">${systemName}</jsp:attribute>
    <jsp:attribute name="pagename">${pageTitle}</jsp:attribute>
    <jsp:attribute name="breadcrumb">${systemName}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">

        <!-- CSS Local Imports -->
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet"/>
        <link href="${baseUrl}/css/biological_system/style.css" rel="stylesheet"/>

        <!-- JS Imports -->
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

	</jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Cardiovascular System</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="breadcrumbs clear row">

                <div class="col-12 d-none d-lg-block px-5 pt-5">
                    <aside>
                        <a href="${cmsBaseUrl}/">Home</a> <span class="fal fa-angle-right"></span>
                        <a href="${cmsBaseUrl}/understand/data-collections/ ">
                            IMPC data collections </a> <span class="fal fa-angle-right"></span>
                        <strong style="text-transform: capitalize">Cardiovascular System</strong>
                    </aside>
                </div>
                <div class="col-12 d-block d-lg-none px-3 px-md-5 pt-5">
                    <aside>
                        <a href="${cmsBaseUrl}/understanding-the-data/research-highlights/"><span
                                class="fal fa-angle-left mr-2"></span> Research Highlights</a>
                    </aside>
                </div>
            </div>
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container">
                                <div class="row">
                                    <div class="col-12">
                                        <p>
                                            This page introduces cardiovascular related phenotypes present in mouse lines produced by the
                                            IMPC. The cardiovascular system refers to the observable morphological and physiological
                                            characteristics of the mammalian heart, blood vessels, or circulatory system that are manifested
                                            through development and lifespan.
                                        </p>
                                    </div>
                                </div>

                                <c:import url="landing_overview_frag.jsp"/>

                                <div class="row">
                                    <div class="col-12">

                                        <h2 id="approach">Approach</h2>

                                        <p>In order to identify the function of genes, the consortium uses a series of
                                            standardised protocols as described in <a href="${baseUrl}/../impress">IMPReSS</a>
                                            (International Mouse Phenotyping Resource of Standardised Screens).</p>
                                        <p>Heart and vascular function/physiology are measured through several procedures like
                                            echocardiography and electrocardiogram and non-invasive blood pressure.
                                            Cardiovascular system morphology is assessed through macroscopic and microscopic measurements,
                                            like heart weight,
                                            gross pathology and gross morphology in both embryo and adult animals. A complete list of
                                            protocols and related phenotypes are
                                            presented in the table below. Links to impress are provided for more details on the
                                            procedure. </p>

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
                                        <svg id="chordDiagramSvg" width="960" height="960" class="d-none d-md-block"></svg>
                                        <script>
                                            var mpTopLevelTerms = ["cardiovascular system phenotype"];
                                            drawChords("chordDiagramSvg", "chordContainer", false, mpTopLevelTerms, false, null, false);
                                        </script>

                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-12" id="paper">

                                        <jsp:include page="paper_frag.jsp"></jsp:include>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


