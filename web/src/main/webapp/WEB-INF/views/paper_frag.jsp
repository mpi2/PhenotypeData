<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<style>
    div.dataTables_wrapper div.dataTables_info { padding-top: 0px; }
</style>
<h2 id="publications" class="title capitalize">${systemName} IKMC/IMPC related publications</h2>
<div class="container">
    <div class="row">
        <%--<p>These papers shown have MESH terms containing "<span id='kw'></span>".--%>
        <div class="col-12 no-gutters" id="alleleRef"></div>
    </div>
</div>

<script type='text/javascript' src='${baseUrl}/js/vendor/jquery/jquery.highlight.js'></script>
<script type='text/javascript' src='${baseUrl}/js/utils/dataTables.searchHighlight.min.js'></script>

<script type="text/javascript">
    $(document).ready(function () {

        'use strict';

        var pageKw = {
            embryo: 'embryo',
            hearing: 'deafness|hearing loss',
            metabolism: 'metaboli',
            cardiovascular: 'cardio|cardia|heart',
            vision: 'vision|eye',
            nervous: 'nervous',
            neurological: 'neurologic|behavior|behaviour',
            conservation: 'development|embryo|disease'
        }

        var page = window.location.href;
        page = page.endsWith("/") ? page.substring(0, page.length - 1) : page;
        page = page.split('/').pop()
        var kw = pageKw[page];

        var tableHeader = "<thead><th></th></thead>";
        var tableCols = 1;
        var isAlleleRef = true;

        var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "cardio", isAlleleRef);
        $('div#alleleRef').append(dTable);

        var oConf = {};
        oConf.id = "cardio";
        oConf.iDisplayLength = 10;
        oConf.iDisplayStart = 0;
        oConf.kw = kw;
        oConf.baseUrl = "${baseUrl}";
        oConf.rowFormat = true;
        oConf.orderBy = "date_of_publication DESC"; // default
        $.fn.fetchAlleleRefDataTable2(oConf);

    });
</script>

