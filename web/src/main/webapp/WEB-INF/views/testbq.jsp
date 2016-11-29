<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Publications with IMPC alleles</jsp:attribute>
    <jsp:attribute name="header">

        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <link href="${baseUrl}/css/default.css" rel="stylesheet" />

        <style type="text/css">


        </style>

        <script type='text/javascript'>

            $(document).ready(function () {
                'use strict';

                // test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';

                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl};"





            });

        </script>

        <%--<script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>--%>
        <%--<script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>--%>
        <%--<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>--%>

    </jsp:attribute>

    <jsp:attribute name="addToFooter">
        <div class="region region-pinned">

        </div>

    </jsp:attribute>
    <jsp:body>


        <form action="${baseUrl}/batchQuery" method="">
            <input type="hidden" name="idlist" value="MGI:106209" />
            <input type="hidden" name="fllist" value="marker_symbol,mgi_accession_id" />
            <input type="hidden" name="corename" value="gene" />
            <button>submit</button>
        </form>
    </jsp:body>

</t:genericpage>

