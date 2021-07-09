<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage-landing>

	<jsp:attribute name="title">Pain</jsp:attribute>
    <jsp:attribute name="pagename">Pain</jsp:attribute>
    <jsp:attribute name="breadcrumb">Pain</jsp:attribute>

	<jsp:attribute name="header">

        <!-- CSS Local Imports -->
  
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
        <link href="${baseUrl}/css/biological_system/style.css"
			rel="stylesheet" />

        <!-- JS Imports -->
        <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript'
			src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script type="text/javascript"
			src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

        <style>
            /* Override allele ref style for datatable */
            table.dataTable thead tr {
                display: table-row;
            }
        </style>


	</jsp:attribute>


	<jsp:attribute name="bodyTag"><body class="phenotype-node no-sidebars small-header"></jsp:attribute>

	<jsp:attribute name="addToFooter"></jsp:attribute>

	<jsp:body>


        <div class="container">
            <div class="row">
                <div class="col-12">
                    <h1 class="title" id="top">IMPC Genetics of Pain Sensitivity Pilot</h1>
                    <h3 style='margin-top:0;'>The IMPC is developing validated high throughput methods to assess Pain Sensitivity, in order to identify genes involved in opioid addiction</h3>
                    <ul>
                        <li> Every day, more than 115 Americans die after overdosing on opioid medications</li>
                        <li> The NIH KOMP funded IMPC Centers are advancing understanding of the genetic factors that put patients at risk for opioid misuse by developing validated methods of assessing pain sensitivity that are compatible with a high throughput pipeline</li>
                        <li> This work will assess > 95 hypothesis driven candidate genes for their role in pain response and help researchers form precision prevention strategies</li>
                    </ul>
                    <%-- <c:import url="landing_overview_frag.jsp"/> removed as requested by Mike author of hearing paper --%>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <h2 class="title" id="approach">Approach</h2>
                    <p> In order to identify the role of genes in pain sensitivity and response, IMPC centers will establish pain sensitivity thresholds in knockout mice using assays that are considered informative and humane.</p>
                    <div class="row align-items-center">
                                <div class="col-md-6">Mechanical nociception is assessed using <em>von Frey</em> filaments.
										A small nylon filament is applied to the plantar surface of the paw 
										and time of withdrawal is measured.  Different size fibers diameters 
										allow application of different forces. The von Frey test is commonly 
										used in patients.
								</div>
								<div class="col-md-6">
									<img src="../img/landing/pain1.png"/>DOI: <a href="https://doi.org/10.13045/acupunct.2017083">https://doi.org/10.13045/acupunct.2017083</a>
								</div>
                    </div>
                    <div class="row align-items-center">
                                
                        <div class="col-md-6">
                            <img src="../img/landing/pain2.png"/>
                        </div>
                        <div class="col-md-6">
                            <p>
                            Thermal nociception is assessed using Hargreaves testing. Heat is applied 
                            to the plantar surface of the hind paw via a precisely calibrated infrared 
                            light. The latency to remove the paw from the heat is measured to assess 				
                            sensitivity to thermal stimulation.  Hot Plate testing is also being 
                            considered to measure thermal nociception
							</p>
                        </div>
                    </div>

                    <p>These assays are being applied to naive, unchallenged mice, as well following sensitization of
                        the hind paw through administration of a chemical (formalin) or immunological (complete Freund’s
                        adjuvant) challenge.</p>
                    <p><em>P2rx4</em> and <em>Trpa1</em> knockout mice are being tested by all centers as a positive
                        control existing null alleles of these genes are published as having decreased pain sensitivity.
                    </p>

                </div>
            </div>



            <div class="row">
                <div class="col-12">
                    <%--deafness manuscript --%>

                    <h2 class="title">When Will Data Be Available?</h2>
                        <p>The Pain Sensitivity pilot project was initiated in September 2017. Establishing and
                            validating methods, generating and breeding mice and phenotype data collection, analysis and
                            interpretation is expected to take 20 months. All efforts will be made to make data
                            available on mousephenotype.org as soon as preliminary analysis is performed.</p>
                </div>
            </div>


            <div class="row">
                <div class="col-12">
                    <h2 class="title">An Ethical Approach</h2>
                    <p>All studies have the approval of performing institute's ethical review committee. The IMPC will
                        make experimental data freely available without restriction to facilitate research and minimize
                        unnecessary duplication. In addition, the IMPC DCC will continue to apply the ARRIVE guidelines
                        to ensure analysis can be reproduced.</p>
                </div>
            </div>

        </div>

    </jsp:body>

</t:genericpage-landing>


