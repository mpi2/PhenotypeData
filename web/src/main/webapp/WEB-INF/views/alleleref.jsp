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
            div#alleleRef {
                margin-top: 30px;
            }
        </style>
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
                
				// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                //var solrUrl = "${internalSolrUrl};"

                var tableHeader = "<thead><th></th></thead>";
                var tableCols = 1;

                var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef");
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

                                <!-- container to display dataTable -->
                                <div class="HomepageTable" id="alleleRef"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>

