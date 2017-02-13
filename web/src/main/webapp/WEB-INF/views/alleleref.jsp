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

        <style>

            /*------ tabs stuff --------*/
            div.ui-tabs {
                border: none;
                width: 100%;
            }
            div#tabs > ul.ui-tabs-nav {
                border: none;
                border-bottom: 1px solid gray;
                border-radius: 0;
                padding-bottom: 5px;
                margin-bottom: 0px;
                background: none;
                list-style-type: none;
            }
            div#tabs > ul li {
                float: left;
                border: none;
                background: none;
                margin: bottom: 0;
                padding-bottom: 0;
            }
            div#tabs > ul li a {
                margin: 0 0px -3px 15px;
                font-size: 16px;
                text-decoration: none;
                padding: 3px 8px;
                border-radius: 3px;
                color: white;
                background-color: #0978A1;
            }
            #tabs .ui-tabs-active {
                border: none;
                background: none;
            }
            #tabs .ui-tabs-active > a {
                border: 1px solid grey;
                border-bottom: 5px solid white;
                color: #0978A1;
                background-color: white;
            }
            .ui-tabs-panel {
                margin-top: 20px;
            }
            #tabs .ui-tabs-active > a:hover {
                background: none;
            }
            div#tabs > ul li a:hover {
                background: rgb(144,195,212);;
            }

            /*------ highcharts stuff --------*/
            div#chartSec {
                margin: 30px 0;
                border: 1px solid lightgrey;
                width: 100%;
            }
            div#chartSec2, div#chartSec3 {
                display: inline;
                float: left;
                overflow: auto;
            }
            div#chartSec2 {
                width: 40%;
            }
            div#chartSec3 {
                width: 56%;
                margin-left: 20px;
                border-left: 1px solid lightgrey;
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

                $( "#tabs" ).tabs({
                    active: 0,
                    activate: function(event, ui) {

                        // get paper stats as highcharts

                        // need to load highcharts after a tab is activated to ensure the width of charts
                        // will stay inside the parent container
                        if ( ui.newTab.index() == 1){
                            var chartId = "chartSec";
                            $.fn.fetchAllelePaperDataPoints(chartId);

                            var chartId2 = "chartSec2";
                            $.fn.fetchAllelePaperDataPointsIncrementWeekly(chartId2);

                            var chartId3 = "chartSec3";
                            $.fn.fetchAllelePaperDataPointsIncrementMonthly(chartId3);
                        }
                    }
                });

                // test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                //var solrUrl = "${internalSolrUrl};"


                // get alleleref in dataTable
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
                                    <div class="clear"></div>
                                    <div id="tabs-2">

                                        <div id="chartSec" class="chart" style="width:100%; height:auto;"></div>
                                        <div id="mw">
                                            <div id="chartSec2"></div>
                                            <div id="chartSec3"></div>
                                        </div>
                                        <div class="clear"></div>

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

