<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Publications with IMPC alleles</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Publications with IMPC alleles</a></jsp:attribute>
    <jsp:attribute name="header">

        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <%--<link href="${baseUrl}/css/default.css" rel="stylesheet" />--%>
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />

          <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
  <%--<link rel="stylesheet" href="/resources/demos/style.css">--%>
        <style>
            div#chartSec {
                margin: 30px 0;
                border: 1px solid lightgrey;
            }
            div#chartSec2, div#chartSec3 {
                display: inline;
                float: left;
            }
            div#mw {
                border: 1px solid lightgrey;
                overflow: auto;
                margin-bottom: 30px;
            }
            div.clear {
                clear: both;
            }

        </style>

        <%--<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>--%>
        <%--<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>--%>

        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>

        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';

                $( "#tabs" ).tabs();
                // get paper number data points
                var chartId = "chartSec";
                $.fn.fetchAllelePaperDataPoints(chartId);

                var chartId2 = "chartSec2";
                $.fn.fetchAllelePaperDataPointsIncrementWeekly(chartId2);

                var chartId3 = "chartSec3";
                $.fn.fetchAllelePaperDataPointsIncrementMonthly(chartId3);

				// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                //var solrUrl = "${internalSolrUrl};"

                var tableHeader = "<thead><th></th></thead>";
                var tableCols = 1;
                var isAlleleRef = true;

                var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef", isAlleleRef);
                $('div#alleleRef').append(dTable);

                var oConf = {};
                oConf.iDisplayLength = 10;
                oConf.iDisplayStart = 0;
                oConf.kw = "";
                oConf.baseUrl = "${baseUrl}";
                oConf.rowFormat = true;
                oConf.orderBy = "date_of_publication DESC"; // default

                $.fn.fetchAlleleRefDataTable2(oConf);
            });


        </script>


        
        <script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>  
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>  
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  

    </jsp:attribute>

    <jsp:attribute name="addToFooter">
        <div class="region region-pinned">

        </div>

    </jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block">
                <div class='content'>
                    <div class="node node-gene">
                        <h1 class="title" id="top">Publications using IKMC and IMPC resources</h1>

                        <%--<div id="chartSec" style="width:100%; height:400px;"></div>--%>
                        <%--<div id="mw">--%>
                            <%--<div id="chartSec2" style="width:33%; height:auto"></div>--%>
                            <%--<div id="chartSec3" style="width:65%; height:auto"></div>--%>
                        <%--</div>--%>
                        <%--<div class="clear"></div>--%>
                        <div class="section">
                            <div class="inner">
                                <div class="clear"></div>

                                <div id="tabs">
                                    <ul>
                                        <li><a href="#tabs-1">Browse paper</a></li>
                                        <li><a href="#tabs-2">View paper by graphs</a></li>
                                    </ul>
                                    <div id="tabs-1">
                                        <!-- container to display dataTable -->
                                        <div class="HomepageTable" id="alleleRef"></div>

                                    </div>

                                    <div id="tabs-2">
                                        <div id="chartSec" style="width:100%; height:400px;"></div>
                                        <div id="mw">
                                            <div id="chartSec2" style="width:33%; height:auto"></div>
                                            <div id="chartSec3" style="width:65%; height:auto"></div>
                                        </div>


                                    </div>
                                </div>

                                <%--<div id="chartSec" style="width:100%; height:400px;"></div>--%>
                                <%--<div id="mw">--%>
                                    <%--<div id="chartSec2" style="width:33%; height:auto"></div>--%>
                                    <%--<div id="chartSec3" style="width:65%; height:auto"></div>--%>
                                <%--</div>--%>



                                <%--<!-- container to display dataTable -->--%>
                                <%--<div class="HomepageTable" id="alleleRef"></div>--%>




                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>

