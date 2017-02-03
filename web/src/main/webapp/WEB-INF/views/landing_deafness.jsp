<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/landing">Landing
        Pages</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

    <jsp:attribute name="header">

	<!-- CSS Local Imports -->
    <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />

    <!-- JS Local Imports -->
	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>


	</jsp:attribute>


    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter">



	</jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">${pageTitle} </h1>

                        <c:import url="landing_overview_frag.jsp"/>

                        <div class="section">
                            <h2 class="title">Approach</h2>
                            <div class="inner">
                                <p>In order to identify genes required for hearing function, the consortium uses an
                                    auditory brainstem response (ABR) test in the adult pipeline at week 14 that
                                    assesses hearing at five frequencies – 6kHz, 12kHz, 18kHz, 24kHz and 30kHz – as well
                                    as a broadband click stimulus. The consortium aimed to analyse a minimum of 4 mutant
                                    mice for each gene and, in most cases, mutant males and females were analysed.</p>
                                <p>For the statistical analysis of the IMPC ABR dataset, we used a reference range
                                    approach with the aim of eliminating false positives (see Methods). Briefly, we used
                                    the total set of matched baseline control data from wild-type C57BL/6N mice that is
                                    generated at each IMPC centre to establish a reference range. For each mutant a
                                    contingency table is employed for both appropriate wild-type control mice and
                                    mutants, and a Fisher’s exact test performed to identify if mutants deviate
                                    significantly from the wild-type distribution. We determined a suitable reference
                                    range and critical p value by the examination of known deafness genes, and selected
                                    a stringent 98% reference range and p value of 0.01 for the initial selection of
                                    putative deafness loci. </p>
                                <p>Details of the experimental design of <a
                                        href="https://www.mousephenotype.org/impress/protocol/176/7"> acoustic startle
                                    and Pre-pulse Inhibition </a> and <a
                                        href="https://www.mousephenotype.org/impress/protocol/149/7">Auditory Brain Stem
                                    Response</a> are available on IMPRESS.</p>
                                <c:import url="landing_procedures_frag.jsp"/>
                            </div>
                        </div>

                        <div class="section">
                                <%--IMPC images--%>
                            <c:if test="${not empty impcImageGroups}">
                                <div class="section" id="imagesSection">
                                    <h2 class="title">Associated Images </h2>
                                    <div class="inner">
                                        <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                    </div>
                                </div>
                            </c:if>
                        </div>

                        <div class="section">
                            <h2 class="title">Phenotypes distribution</h2>
                            <div class="inner">
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                                </div>
                            </div>
                        </div>

                        <div class="section">

                            <h2 class="title">Related papers using IMPC resources</h2>
                            <div class="inner">
                                <%--<p>These papers shown have MESH terms containing "<span id='kw'></span>".--%>
                                </p>
                                <br/> <br/>
                                <div class="HomepageTable" id="alleleRef"></div>

                                <script type="text/javascript">
                                    $(document).ready(function () {

                                        'use strict';
                                        var tableHeader = "<thead><th>Paper title</th><th>Allele symbol</th><th>Journal</th><th>Date of publication</th><th title='Grant agency cited in manuscript'>Grant agency</th><th>PMID</th><th>Paper link</th><th>Mesh</th></thead>";
                                        var tableCols = 8;

                                        var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef");
                                        $('div#alleleRef').append(dTable);

                                        var oConf = {};
                                        oConf.doAlleleRef = true;
                                        oConf.iDisplayLength = 10;
                                        oConf.iDisplayStart = 0;
                                        oConf.kw = "deaf";
                                        oConf.baseUrl = "${baseUrl}";

                                        $('span#kw').text(oConf.kw);
                                        $.fn.fetchAlleleRefDataTable(oConf);

                                    });
                                </script>

                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


