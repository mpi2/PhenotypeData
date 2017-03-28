<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Mesh terms</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;</jsp:attribute>
    <jsp:attribute name="header">
        
        <style>
           table {
               border-collapse: collapse;
           }
           table.dataTable span.highlight {
              background-color: yellow;
              font-weight: bold;
              color: black;
           }
           div#mesh {
              margin-top: 30px;
           }

        </style>
        <script type='text/javascript' src='${baseUrl}/js/vendor/jquery/jquery.highlight.js'></script>
        <script type='text/javascript' src='${baseUrl}/js/utils/dataTables.searchHighlight.min.js'></script>

        <script type='text/javascript'>
            $(document).ready(function(){
//                $('tbody td:first-child').click(function(){
//                    $(this).next().next().toggle();
//                });
                $.ajax({
                    'url': baseUrl + '/fetchMeshMapping',
                    'async': true,
                    'jsonp': 'json.wrf',
                    'success': function (html){

                        console.log(html)
                        $('div#mesh').html(html);

                        $('table#mesh').dataTable({
                            bPaginate: false,
                            order: [[ 2, "desc" ]], // 3rd col
                            searchHighlight: true,
                        });
                    },
                    'error' : function(jqXHR, textStatus, errorThrown) {
                        alert("error: " + errorThrown);
                    }
                });
            });
        </script>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">
        <div class="region region-pinned">
        </div>
    </jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class='content'>
                    <div class="node node-gene">
                        <h1>Mapping of MESH headings to top level MESH terms</h1>

                        <div class="section">

                            <h6>IKMC/ IMPC related publications</h6><p></p>
                            <div class="inner">

                                <div id="mesh"></div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>

