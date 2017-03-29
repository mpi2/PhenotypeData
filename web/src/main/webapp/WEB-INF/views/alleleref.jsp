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
        <link type="text/css" rel="stylesheet" href="${baseUrl}/css/vendor/jstree.min.css"/>

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
            div.chart {
                border: 1px solid lightgrey;
                width: 99%;
                margin: 30px auto;
            }
            div.clear {
                clear: both;
            }
            div#agencyName {
                font-weight: bold;
                font-size: 20px;
                margin: 10px 0;
            }
            .highcharts-axis-labels {
                cursor: text !important;
            }


         </style>

        <%--<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>--%>
        <%--<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>--%>

        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>
        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/data.js"></script>
        <script src="https://code.highcharts.com/modules/drilldown.js"></script>
        <script type="text/javascript" src='${baseUrl}/js/vendor/jstree/jstree.min.js'></script>


        <script type='text/javascript'>

            $(document).ready(function () {
                'use strict';

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

                var dTableConsortium = $.fn.fetchEmptyTable(tableHeader, tableCols, "consortiumPapers", isAlleleRef);
                $('div#consortiumPapers').append(dTableConsortium);

                var oConf = {};
                oConf.iDisplayLength = 10;
                oConf.iDisplayStart = 0;
                oConf.kw = "";
                oConf.baseUrl = "${baseUrl}";
                oConf.rowFormat = true;
                oConf.orderBy = "date_of_publication DESC"; // default
                oConf.id = "alleleRef";
                $.fn.fetchAlleleRefDataTable2(oConf);


                // find out which tab to open from hash tag
                var matches = window.location.hash.match(/(\d)$/);
                var tabIndex = matches == null ? 0 : matches[0];

                $( "#tabs" ).tabs({
                    active: tabIndex,
                    activate: function(event, ui) {
                        if (ui.newTab.index() == 0) {
                            $('div#agencyBox').hide();
                        }
                        else if (ui.newTab.index() == 1){
                            // get paper stats as highcharts
                            // need to load highcharts after a tab is activated to ensure the width of charts
                            // will stay inside the parent container
                            $('div#agencyBox').show();
                            var chartWeek = "chartWeek";
                            var chartMonth = "chartMonth";
                            $.fn.fetchAllelePaperDataPointsIncrement(chartYearIncrease, chartMonthIncrease, chartQuarter, chartGrantQuarter);
                        }
                        else if (ui.newTab.index() == 2){
                            $('div#agencyBox').hide();  // container for agency funded papers
                            var oConf2 = oConf;
                            oConf2.consortium = true;
                            oConf2.id = "consortiumPapers";

                            $.fn.fetchAlleleRefDataTable2(oConf2);
                        }
                    }
                });

            });

        </script>

        <script type='text/javascript' src='${baseUrl}/js/vendor/jquery/jquery.highlight.js'></script>
        <script type='text/javascript' src='${baseUrl}/js/utils/dataTables.searchHighlight.min.js'></script>
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
                        <h1 class="title" id="top">IKMC/IMPC related publications</h1>

                        <div class="section">
                            <div class="inner">
                                <div class="clear"></div>

                                <div id="tabs">
                                    <ul>
                                        <li><a href="#tabs-1">Browse publication</a></li>
                                        <li><a href="#tabs-2">Publication stats</a></li>
                                        <li><a href="#tabs-3">Consortium publications</a></li>
                                    </ul>
                                    <div id="tabs-1">
                                        <!-- container to display dataTable -->
                                        <div id="alleleRef"></div>

                                    </div>
                                    <div class="clear"></div>
                                    <div id="tabs-2">
                                        <div id="chartYearIncrease" class="chart"></div>
                                        <div id="chartQuarter" class="chart"></div>
                                        <div id="chartMonthIncrease" class="chart"></div>
                                        <div id="chartGrantQuarter" class="chart"></div>
                                        <div class="clear"></div>

                                    </div>
                                    <div id="tabs-3">
                                        <!-- container to display dataTable -->
                                        <div id="consortiumPapers"></div>

                                    </div>
                                </div>
                                <div id='agencyBox'>
                                    <div id="agencyName"></div>
                                    <div id="agency"></div>
                                </div>

                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>

