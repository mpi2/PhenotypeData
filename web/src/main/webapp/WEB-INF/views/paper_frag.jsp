<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 15/02/2016
  Time: 19:53
  To change this template use File | Settings | File Templates.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<h2 id="publications" class="title capitalize">${systemName} IKMC/IMPC related publications</h2>
<div class="inner">
    <%--<p>These papers shown have MESH terms containing "<span id='kw'></span>".--%>
    </p>
    <br/> <br/>
    <div class="HomepageTable" id="alleleRef"></div>

    <script type='text/javascript' src='${baseUrl}/js/vendor/jquery/jquery.highlight.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/utils/dataTables.searchHighlight.min.js'></script>

    <script type="text/javascript">
        $(document).ready(function () {

            'use strict';

            var pageKw = {
                embryo: 'embryo',
                deafness : 'deaf|hearing|vestibular',
                metabolism : 'metaboli',
                cardiovascular : 'cardio|cardia|heart',
                vision : 'vision|eye',
                nervous : 'nervous',
                neurological : 'neurologic|behavior|behaviour'
            }

            var kw = null;
            for( var page in pageKw ){
                if ( window.location.href.indexOf(page) != -1){
                    kw = pageKw[page];
                    break;
                }
            }

            var tableHeader = "<thead><th></th></thead>";
            var tableCols = 1;
            var isAlleleRef = true;

            var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef", isAlleleRef);
            $('div#alleleRef').append(dTable);

            var oConf = {};
            oConf.id = "alleleRef";
            oConf.iDisplayLength = 10;
            oConf.iDisplayStart = 0;
            oConf.kw = kw;
            oConf.baseUrl = "${baseUrl}";
            oConf.rowFormat = true;
            oConf.orderBy = "date_of_publication DESC"; // default
            //$('span#kw').text(oConf.kw);
            $.fn.fetchAlleleRefDataTable2(oConf);

        });
    </script>

</div>

