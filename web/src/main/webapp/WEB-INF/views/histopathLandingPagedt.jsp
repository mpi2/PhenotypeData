<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:genericpage>

    <jsp:attribute name="title">Histopath Landing Page</jsp:attribute>
    <jsp:attribute name="header">
   <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.css"/>

<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.js"></script>

    </jsp:attribute>

    <jsp:attribute name="addToFooter">

        <script>

            $(document).ready( function () {
                var table = $('#heatmap').DataTable({
                    'createdRow': function(row, data, index){


                            $(row).find('td:eq(1)').css('background-color', 'Orange');

                        for(var i=1; i<data.length; i++) {
                            if (data[i] == 0) {
                                $(row).find('td:eq('+i+')').css('background-color', '#fff').css('color', 'rgba(0, 0, 0, 0.0)').css("pointer-events", "none");

                            }else
                            if (data[i] == 1) {
                                $(row).find('td:eq('+i+')').css('background-color', '#808080').css('color', 'rgba(0, 0, 0, 0.0)').css("pointer-events", "none");;
                                console.log('found 1');
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
                    var url='${baseUrl}/histopath/'+data[0];
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
                            <span class="fal fa-angle-right"></span> Histopathology
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <div class="page-content">

                        <div class="card">
                            <div class="card-header">Histopathology for every gene tested</div>
                            <div class="card-body">

                                <p class="my-0"><b>Significance Score:</b></p>
                                <ul class="my-0">
                                    <li><b>Not significant</b> (histopathology finding that is interpreted by the
                                        histopathologist to be within normal limits of background strain-related
                                        findings or an incidental finding not related to genotype)
                                    </li>
                                    <li><b>Significant</b> (histopathology finding that is interpreted by the
                                        histopathologist to not be a background strain-related finding or an incidental
                                        finding)
                                    </li>
                                </ul>
                            </div>
                        </div>




                            <table id="heatmap" class="display cell-border compact" style="font-size: 10px">
                                <thead>
                                <tr>
                                    <th><div class="rotate90">Gene</div></th>
                                    <c:forEach items="${anatomyHeaders}" var="parameter">
                                        <th><div class="rotate90">${parameter}</div></th>
                                    </c:forEach>

                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="arow" items="${rows}" varStatus="status">
                                    <tr>
                                        <td>${geneSymbols[status.index]}</td>
                                            <c:forEach var="acolumn" items="${arow}">
                                                <td>${acolumn}</td>
                                            </c:forEach>
                                    </tr>
                                </c:forEach>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <th><div class="rotate90">Gene</div></th>
                                    <c:forEach var="parameter" items="${anatomyHeaders}">
                                        <th><div class="rotate90">${parameter}</div></th>
                                    </c:forEach>
                                </tr>
                                </tfoot>
                            </table>



                    </div>
                </div>
            </div>





        </div>

    </jsp:body>


    </t:genericpage>

