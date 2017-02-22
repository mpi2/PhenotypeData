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
                                <c:import url="landing_procedures_frag.jsp"/>
                            </div>
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

                            <h2 class="title">Related papers citing IMPC resources</h2>
                            <div class="inner">
                                <%--<p>These papers shown have MESH terms containing "neurologic" or "behavior" or "behaviour".--%>
                                </p>
                                <br/> <br/>
                                <div class="HomepageTable" id="alleleRef"></div>

                                <script type="text/javascript">
                                    $(document).ready(function () {

                                        'use strict';

                                        var tableHeader = "<thead><th></th></thead>";
                                        var tableCols = 1;
                                        var isAlleleRef = true;

                                        var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef", isAlleleRef);
                                        $('div#alleleRef').append(dTable);

                                        var oConf = {};
                                        oConf.iDisplayLength = 10;
                                        oConf.iDisplayStart = 0;
                                        oConf.kw = "neurologic|behavior|behaviour";
                                        oConf.baseUrl = "${baseUrl}";
                                        oConf.rowFormat = true;
                                        oConf.orderBy = "date_of_publication DESC"; // default

                                        //$('span#kw').text(oConf.kw);
                                        $.fn.fetchAlleleRefDataTable2(oConf);

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


