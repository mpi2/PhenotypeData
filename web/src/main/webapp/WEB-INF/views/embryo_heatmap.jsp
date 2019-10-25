<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:genericpage-landing>

    <jsp:attribute name="title">Embryo Heat Map</jsp:attribute>
    <jsp:attribute name="header">
   <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.css"/>

<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.js"></script>


    </jsp:attribute>

    <jsp:attribute name="addToFooter">
<%--<style type="text/css" media="all">--%>

<%--        table{--%>
<%--  margin: 0 auto;--%>
<%--  width: 100%;--%>
<%--  clear: both;--%>
<%--  border-collapse: collapse;--%>
<%--  table-layout: fixed; // ***********add this--%>
<%--  word-wrap:break-word; // ***********and this--%>
<%--}--%>

<%--</style>--%>

        <script>

            $(document).ready( function () {
                var table = $('#heatmap').DataTable({
                    // "columnDefs": [
                    //     { "width": "2%", "targets": 0 }
                    // ], only use if scroll set to false
                    //"scrollX": true,
                    'createdRow': function(row, data, index){
                        //$(row).find('td:eq(1)').css('background-color', 'grey');

                        for(var i=1; i<data.length; i++) {
                            if (data[i] == 0) {
                                $(row).find('td:eq('+i+')').css('background-color', '#fff').css('color', 'rgba(0, 0, 0, 0.0)').css("pointer-events", "pointer");
                            }else
                            if (data[i] == 1) {
                                $(row).find('td:eq('+i+')').css('background-color', '#808080').css('color', 'rgba(0, 0, 0, 0.0)').css("pointer-events", "pointer");;
                            }else
                            if (data[i] == 2) {
                                $(row).find('td:eq('+i+')').css('background-color', '#17a2b8').css('color', 'rgba(0, 0, 0, 0.0)').css('cursor','pointer');
                            }else
                            if (data[i] == 4) {
                                $(row).find('td:eq('+i+')').css('background-color', '#ce6211').css('color', 'rgba(0, 0, 0, 0.0)').css('cursor','pointer');
                            }
                        }
                    },
                });

                $('#heatmap tbody').on('click', 'tr', function () {
                    var data = table.row( this ).data();
                    var isAnalysed= false;
                    var i;
                    for(i=0; i<data.length; i++){
                        if(data[i]==4){
                            isAnalysed=true;
                        }
                    }
                    var url='https://www.mousephenotype.org/embryoviewer?mgi='+data[1];
                    if(isAnalysed)url='https://www.mousephenotype.org/embryoviewer/?mgi=MGI:1932915&wn=Average&wov=jacobian';
                    var win = window.open(url, '_blank');
                    win.focus();
                } );



                });


        </script>

    </jsp:attribute>

    <jsp:body>
        <div class="container single single--no-side">

            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="/">Home</a>
                            <span class="fal fa-angle-right"></span> Embryo Heat Map
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <div class="page-content">

                        <div class="card">
                            <div class="card-header">Embryo data for every gene tested</div>
                            <div class="card-body">
                                <p class="my-0"><b>Significance Score:</b></p>
                                <c:set var="noData" scope="page" value="fa fa-circle"/>
                                <c:set var="notApplicable" scope="page" value="fa fa-circle"/>
                                <c:set var="notSignificant" scope="page" value="fa fa-circle"/>
                                <c:set var="significant" scope="page" value="fa fa-circle"/>
                                <c:set var="noDataColour" scope="page" value="#fff"/>
                                <c:set var="notApplicableColour" scope="page" value="#808080"/>
                                <c:set var="notSignificantColour" scope="page" value="#17a2b8"/>
                                <c:set var="significantColour" scope="page" value="#ce6211"/>
                                <div style="background-color: whitesmoke">
                                    <div title="No Data"  class="mr-3"><i class="${noData}" style="color: white"></i>&nbsp;&nbsp;No Data</div>
                                    <div title="Not Applicable" style="color: ${notApplicableColour}" class="mr-3"> <i class="${notApplicable}"></i>&nbsp;&nbsp;Not Applicable</div>
                                    <div title="Not Signficant" style="color: ${notSignificantColour}" class="mr-3"><i class="${notSignificant}"></i>&nbsp;&nbsp;<b>Images Available</b> </div>
                                    <div title="Significant" style="color: ${significantColour}" class="mr-3"><i class="${significant}"></i>&nbsp;&nbsp;<b>Images and Automated Volumetric Analysis Available</b> </div>
                                </div>
                            </div>
                        </div>



                            <table id="heatmap" class="display cell-border compact">
                                <thead>
                                <tr>
                                    <th>Gene</th>
                                    <th>Accession</th>
                                    <c:forEach items="${modalityHeaders}" var="parameter">
                                        <th>${parameter}</th>
                                    </c:forEach>

                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="arow" items="${rows}" varStatus="status">
                                    <tr>
                                        <td>${geneSymbols[status.index]}</td>
                                        <td>${mgiAccessions[status.index]}</td>
                                            <c:forEach var="acolumn" items="${arow}">
                                                <td>${acolumn}</td>
                                            </c:forEach>
                                    </tr>
                                </c:forEach>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <th>Gene</th>
                                    <c:forEach var="parameter" items="${modalityHeaders}">
                                        <th>${parameter}</th>
                                    </c:forEach>
                                </tr>
                                </tfoot>
                            </table>



                    </div>
                </div>
            </div>





        </div>

    </jsp:body>


    </t:genericpage-landing>

