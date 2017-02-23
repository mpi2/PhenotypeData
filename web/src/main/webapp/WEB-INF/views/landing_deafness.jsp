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
                            <jsp:include page="paper_frag.jsp"></jsp:include>
                        </div>

                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


