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
                margin-bottom: 0;
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
            table#alleleRef {
                table-layout: fixed;
                width: 100% !important;
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
                oConf.orderBy = "firstPublicationDate DESC"; // default
                oConf.id = "alleleRef";
                $.fn.fetchAlleleRefDataTable2(oConf);

                // find out which tab to open from hash tag
                var matches = window.location.hash.match(/(\d)$/);
                var tabIndex = matches == null ? 0 : matches[0];

                var tabs = $( "#tabs" ).tabs({
                    active: 0,
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
                            $.fn.fetchAllelePaperDataPointsIncrement();//chartYearIncrease, chartMonthIncrease, chartQuarter, chartGrantQuarter);
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
                tabs.tabs({active: tabIndex});
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

        <div class="container data-heading">
            <div class="row row-shadow">
                <div class="col-12 no-gutters">
                    <h2>IKMC/IMPC related publications</h2>
                </div>
            </div>
        </div>

        <div class="container single single--no-side">
            <div class="row row-over-shadow">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">

                        <ul class="nav nav-tabs" id="publicationsTab" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link active" id="browse-tab" data-toggle="tab" href="#browse"
                                   role="tab" aria-controls="browse-tab" aria-selected="false">All publications</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="stats-tab" data-toggle="tab" href="#stats"
                                   role="tab" aria-controls="stats-tab" aria-selected="false">Publications stats</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="consortium-tab" data-toggle="tab" href="#consortium"
                                   role="tab" aria-controls="consortium-tab" aria-selected="false">Consortium publications</a>
                            </li>
                        </ul>

                        <div class="tab-content" id="publicationsTabContent">
                            <div class="tab-pane fade show active" id="browse" role="tabpanel"
                                 aria-labelledby="browse-tab">
                                <div id="alleleRef"></div>
                            </div>
                            <div class="tab-pane fade show active" id="stats" role="tabpanel"
                                 aria-labelledby="stats-tab">
                                <div id="chartYearIncrease" class="chart"></div>
                                <div id="chartQuarter" class="chart"></div>
                                <div id="chartMonthIncrease" class="chart"></div>
                                <div id="chartGrantQuarter" class="chart"></div>
                                <div id='agencyBox'>
                                    <div id="agencyName"></div>
                                    <div id="agency"></div>
                                </div>
                            </div>
                            <div class="tab-pane fade show active" id="consortium" role="tabpanel"
                                 aria-labelledby="consortium-tab">
                                <div id="consortiumPapers"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>

