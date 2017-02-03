<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Publications with IMPC alleles</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Publications with IMPC alleles</a></jsp:attribute>
    <jsp:attribute name="header">
        
        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <link href="${baseUrl}/css/default.css" rel="stylesheet" />
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
                
				// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                var solrUrl = "${internalSolrUrl};"

                //var tableHeader = "<thead><th>Paper title</th><th>Allele symbol</th><th>Pmid</th><th>Journal</th><th>Date of publication</th><th title='Grant agency cited in manuscript'>Grant agency</th><th>Paper link</th></thead>";
                //var tableCols = 7;
                var tableHeader = "<thead><th>Paper title</th><th>Allele symbol</th><th>Journal</th><th>Date of publication</th><th title='Grant agency cited in manuscript'>Grant agency</th><th>PMID</th><th>Paper link</th><th>Mesh</th></thead>";
                //var tableHeader = "<thead><th>Paper title</th><th>Allele symbol</th><th>Journal</th><th>Date of publication</th><th title='Grant agency cited in manuscript'>Grant agency</th><th>Paper link</th></thead>";

                var tableCols = 8;


                var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef");
                $('div#alleleRef').append(dTable);

                var oConf = {};
                oConf.doAlleleRef = true;
                oConf.iDisplayLength = 10;
                oConf.iDisplayStart = 0;
                oConf.baseUrl = "${baseUrl}";
                oConf.kw = "";

                $.fn.fetchAlleleRefDataTable(oConf);
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

