<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Publications with IMPC alleles</jsp:attribute>
    <jsp:attribute
            name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Publications with IMPC alleles</a></jsp:attribute>
    <jsp:attribute name="header">

        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet"/>
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet"/>
        <link type="text/css" rel="stylesheet" href="${baseUrl}/css/vendor/jstree.min.css"/>

        <style>
            table#alleleRef {
                width: 100% !important;
            }
        </style>

        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>
        <script src="https://code.highcharts.com/modules/data.js"></script>
        <script src="https://code.highcharts.com/modules/drilldown.js"></script>
        <script type="text/javascript" src='${baseUrl}/js/vendor/jstree/jstree.min.js'></script>


        <script type='text/javascript'>

            $(document).ready(function () {
                'use strict';

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
                oConf.consortium = false;
                oConf.id = "alleleRef";
                $.fn.fetchAlleleRefDataTable2(oConf);

                var oConf2 = {};
                oConf2.iDisplayLength = 10;
                oConf2.iDisplayStart = 0;
                oConf2.kw = "";
                oConf2.baseUrl = "${baseUrl}";
                oConf2.rowFormat = true;
                oConf2.orderBy = "firstPublicationDate DESC"; // default
                oConf2.consortium = true;
                oConf2.id = "consortiumPapers";

                $.fn.fetchAlleleRefDataTable2(oConf2);
                $.fn.fetchAllelePaperDataPointsIncrement();
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
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">IKMC/IMPC related publications</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="row no-gutters">

                                <div class="col-12">
                                    <ul class="nav nav-tabs" id="publicationsTab" role="tablist">
                                        <li class="nav-item">
                                            <a class="nav-link active" id="browse-tab" data-toggle="tab" href="#browse"
                                               role="tab" aria-controls="browse-tab" aria-selected="false">All
                                                publications</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="stats-tab" data-toggle="tab" href="#stats"
                                               role="tab" aria-controls="stats-tab" aria-selected="false">Publications
                                                stats</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="consortium-tab" data-toggle="tab" href="#consortium"
                                               role="tab" aria-controls="consortium-tab" aria-selected="false">Consortium
                                                publications</a>
                                        </li>
                                    </ul>

                                    <div class="tab-content" id="publicationsTabContent">
                                        <div class="tab-pane fade show active" id="browse" role="tabpanel"
                                             aria-labelledby="browse-tab">
                                            <div class="container">
                                                <div class="row">
                                                    <div id="alleleRef" class="col"></div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="tab-pane fade show" id="stats" role="tabpanel"
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
                                        <div class="tab-pane fade show" id="consortium" role="tabpanel"
                                             aria-labelledby="consortium-tab">
                                            <div id="consortiumPapers"></div>
                                        </div>
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

