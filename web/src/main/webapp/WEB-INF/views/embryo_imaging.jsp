<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:genericpage-landing>

    <jsp:attribute name="title">Embryo Viewer</jsp:attribute>
    <jsp:attribute name="pagename">Embryo Viewer</jsp:attribute>
    <jsp:attribute name="breadcrumb">Embryo Viewer</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="page-template page-template-no-sidebar--large  page "></jsp:attribute>
    <jsp:attribute name="heatmap">true</jsp:attribute>

    <jsp:attribute name="header"></jsp:attribute>

    <jsp:attribute name="addToFooter">

        <script>

            $(document).ready(function () {
                var table = $('#heatmap').DataTable({
                    "pageLength": 100,

                    'createdRow': function (row, data, index) {
                        //$(row).find('td:eq(1)').css('background-color', 'grey');
                        $(row).css("cursor", "pointer");
                        for (var i = 1; i < data.length; i++) {
                            if (data[i] == 0) {
                                $(row).find('td:eq(' + i + ')').css('background-color', '#fff').css('color', 'rgba(0, 0, 0, 0.0)').css("pointer-events", "none");
                            } else if (data[i] == 1) {
                                $(row).find('td:eq(' + i + ')').css('background-color', '#808080').css('color', 'rgba(0, 0, 0, 0.0)').css("pointer-events", "none");
                            } else if (data[i] == 2) {
                                $(row).find('td:eq(' + i + ')').css('background-color', '#17a2b8').css('color', 'rgba(0, 0, 0, 0.0)');
                            } else if (data[i] == 4) {
                                $(row).find('td:eq(' + i + ')').css('background-color', '#ce6211').css('color', 'rgba(0, 0, 0, 0.0)');
                            }
                        }
                    },
                });

                table.on('click', 'tbody td', function () {
                    var url = 'https://www.mousephenotype.org/embryoviewer?mgi='
                    //get textContent of the TD
                    var header = table.column(this.cellIndex).header();
                    var row_data = table.row(this).data();
                    var gene_symbol = row_data[0];
                    var gene_acc = row_data[1];
                    //get the value of the TD using the API
                    url = url + gene_acc;
                    var e15data = row_data[3];
                    var umass_data = row_data[5];
                    if ($(header).text() === 'E14.5/E15.5' && e15data === '4') {//analysed data for this cell available
                        url = 'https://www.mousephenotype.org/embryoviewer/?mgi=' + gene_acc + '&wn=Average&wov=jacobian';
                    }
                    if ($(header).text() === 'UMASS Pre E9.5' && umass_data === '2') {//UMASS links available for this cell
                        url = 'http://blogs.umass.edu/jmager/' + gene_symbol;
                    }
                    var win = window.open(url, '_blank');
                    win.focus();
                })

            });


        </script>

    </jsp:attribute>

    <jsp:body>

        <div class="card">
            <div class="card-header"><h3>Embryo Data for Every Lethal Gene</h3></div>
            <div class="card-body">
                <p>
                    Up to one third of homozygous knockout lines are <b>embryonic lethal</b>, which means no homozygous
                    mice or less than expected are observed past the weaning stage (<a
                        href="https://www.mousephenotype.org/impress/ProcedureInfo?action=list&procID=703&pipeID=7">IMPC
                    Viability Primary Screen</a> procedure). Early death may occur during embryonic development or soon
                    after birth, during the pre-weaning stage. For this reason, the IMPC established a systematic
                    phenotyping pipeline to morphologically evaluate mutant embryos to ascertain the primary
                    perturbations that cause early death and thus gain insight into gene function.
                </p>
                <p>
                    As determined in IMPReSS (see interactive diagram <a href="https://www.mousephenotype.org/impress">here</a>),
                    all embryonic lethal lines undergo gross morphology assessment at E12.5 (embryonic day 12.5) to
                    determine whether defects occur earlier or later in development. A comprehensive imaging platform is
                    then used to assess dysmorphology. Embryo gross morphology, as well as 2D and 3D imaging are
                    actively being implemented by the IMPC for embryonic lethal lines.

                    Read more in our paper on <a href="https://europepmc.org/articles/PMC5295821">High-throughput
                    discovery of novel developmental phenotypes, Nature 2016</a>
                </p>
                <p class="alert alert-info w-100 my-3">Click on a cell of the heatmap to activate the IMPC Embryo Viewer.</p>

                <p class="my-0"><b>Legend:</b></p>
                <c:set var="noData" scope="page" value="fa fa-circle"/>
                <c:set var="notApplicable" scope="page" value="fa fa-circle"/>
                <c:set var="notSignificant" scope="page" value="fa fa-circle"/>
                <c:set var="significant" scope="page" value="fa fa-circle"/>
                <c:set var="noDataColour" scope="page" value="#fff"/>
                <c:set var="notApplicableColour" scope="page" value="#808080"/>
                <c:set var="notSignificantColour" scope="page" value="#17a2b8"/>
                <c:set var="significantColour" scope="page" value="#ce6211"/>
                <div class="p-1 mb-2" style="background-color: whitesmoke">
                    <div title="No Images" class="mr-3"><i class="${noData}" style="color: white"></i>&nbsp;&nbsp;No Data</div>
                    <div title="Not Signficant" style="color: ${notSignificantColour}" class="mr-3"><i class="${notSignificant}"></i>&nbsp;&nbsp;<b>Images Available</b></div>
                    <div title="Significant" style="color: ${significantColour}" class="mr-3"><i class="${significant}"></i>&nbsp;&nbsp;<b>Images and Automated Volumetric Analysis Available</b></div>
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
                        <th>Accession</th>
                        <c:forEach var="parameter" items="${modalityHeaders}">
                            <th>${parameter}</th>
                        </c:forEach>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>

    </jsp:body>

</t:genericpage-landing>

