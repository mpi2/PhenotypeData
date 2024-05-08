<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Embryo </jsp:attribute>
    <jsp:attribute name="pagename">IMPC Embryo Data</jsp:attribute>
    <jsp:attribute name="breadcrumb">Embryo Data</jsp:attribute>

    <jsp:attribute name="bodyTag"><body class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/slider.js?v=${version}'></script>
        <link rel="stylesheet" href='${baseUrl}/css/slider.css?v=${version}'/>
		<link href="${baseUrl}/css/alleleref.css" rel="stylesheet"/>
        <script defer="defer" src="https://impc-datasets.s3.eu-west-2.amazonaws.com/embryo-landing-assets/main.80b342d8.js"></script>
        <link href="https://ficolo.github.io/impc-labs/static/css/main.073c9b0a.css" rel="stylesheet"/>
        <script>
            function csvToJSON(csv) {
                var lines = csv.split("\n");
                var result = [];
                var headers;
                headers = lines[0].split(",");

                for (var i = 1; i < lines.length; i++) {
                    var obj = {};

                    if (lines[i] == undefined || lines[i].trim() == "") {
                        continue;
                    }

                    var words = lines[i].split(",");
                    for (var j = 0; j < words.length; j++) {
                        obj[headers[j].trim()] = words[j];
                    }

                    result.push(obj);
                }
                return result;
            }

            function tsvToJSON(csv) {
                var lines = csv.split("\n");
                var result = [];
                var headers;
                headers = lines[0].split("\t");

                for (var i = 1; i < lines.length; i++) {
                    var obj = {};

                    if (lines[i] == undefined || lines[i].trim() == "") {
                        continue;
                    }

                    var words = lines[i].split("\t");
                    for (var j = 0; j < words.length; j++) {
                        obj[headers[j].trim()] = words[j];
                    }

                    result.push(obj);
                }
                return result;
            }
        </script>
    </jsp:attribute>

    <jsp:body>
        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">IMPC Embryo Data</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="breadcrumbs clear row">

                <div class="col-12 d-none d-lg-block px-5 pt-5">
                    <aside>
                        <a href="${cmsBaseUrl}/">Home</a> <span class="fal fa-angle-right"></span>
                        <a href="${cmsBaseUrl}/understand/data-collections/ ">
                            IMPC data collections </a> <span class="fal fa-angle-right"></span>
                        <strong style="text-transform: capitalize">Embryo data</strong>
                    </aside>
                </div>
                <div class="col-12 d-block d-lg-none px-3 px-md-5 pt-5">
                    <aside>
                        <a href="${cmsBaseUrl}/understanding-the-data/research-highlights/"><span
                                class="fal fa-angle-left mr-2"></span> Research Highlights</a>
                    </aside>
                </div>
            </div>
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container" style="line-height: 2">
                                <div class="row">
                                    <div class="col-12">
                                        <h2>Introduction to IMPC Embryo Data</h2>

                                    </div>
                                </div>


                                <div class="row">
                                    <div class="col-md-4 col-12 text-center">
                                        <img class="img-fluid " src="${baseUrl}/img/Tmem100_het.jpg"
                                             height="200"/>
                                    </div>
                                    <div class="col-md-8 col-12">

                                        <p>
                                            Up to one third of homozygous knockout lines are lethal, which means no
                                            homozygous mice or
                                            less than expected are observed past the weaning stage (IMPC <a
                                                href="${cmsBaseUrl}/impress/ProcedureInfo?action=list&procID=703&pipeID=7">Viability
                                            Primary Screen procedure</a>). Early death may occur during embryonic
                                            development or soon
                                            after birth, during the pre-weaning stage. For this reason, the IMPC
                                            established a <a
                                                href="${cmsBaseUrl}/impress">systematic embryonic phenotyping
                                            pipeline</a> to
                                            morphologically evaluate mutant embryos to ascertain the primary
                                            perturbations that cause
                                            early death and thus gain insight into gene function.

                                        </p>

                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-12">
                                        <p>
                                            Read more in our paper on <a
                                                href="https://europepmc.org/article/PMC/5295821">High-throughput
                                            discovery of novel developmental phenotypes, Nature 2016.</a>
                                        </p>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container" style="line-height: 2">
                                <div class="row">
                                    <div class="col-12">
                                        <h2>IMPC Embryonic Pipeline</h2>
                                    </div>
                                </div>


                                <div class="row">
                                    <div class="col-12">
                                        <p>
                                            As determined in IMPReSS (see interactive diagram <a
                                                href="${cmsBaseUrl}/impress">here</a>), all embryonic lethal lines
                                            undergo
                                            gross morphology assessment at E12.5 (embryonic day 12.5) to determine
                                            whether defects occur
                                            earlier or later during embryonic development. A comprehensive imaging
                                            platform is then used
                                            to assess dysmorphology. Embryo gross morphology, as well as 2D and 3D
                                            imaging are actively
                                            being implemented by the IMPC for lethal lines.
                                        </p>

                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-12">
                                        <img src="https://www.mousephenotype.org/data/img/embryo-pipeline-diagram.png" alt="">
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container" style="line-height: 2">
                                <div class="row ">
                                    <div class="col-12">
                                        <h2>Determining Lethal Lines</h2>
                                    </div>
                                </div>
                                <div class="row ">
                                    <div class="col-12">
                                        <p>
                                            The IMPC assesses each gene knockout line for viability (Viability
                                            Primary Screen
                                            <a href="${cmsBaseUrl}/impress/ProcedureInfo?action=list&procID=703&pipeID=7">IMPC_VIA_001</a>
                                            and <a
                                                href="${cmsBaseUrl}/impress/ProcedureInfo?action=list&procID=1188&pipeID=7#Parameters">IMPC_VIA_002</a>).
                                            In this procedure, the proportion of homozygous pups is determined soon
                                            after
                                            birth, during the preweaning stage, in litters produced from mating
                                            heterozygous animals. A
                                            line is declared lethal if no homozygous pups for the null allele are
                                            detected at weaning
                                            age, and subviable if pups homozygous for the null allele constitute
                                            less than 12.5% of the
                                            litter.
                                        </p>

                                    </div>

                                </div>

                                <div class="row mb-3">
                                    <div id="viabilityChart" class="col-md-7 h-100">
                                        <script type="text/javascript">${viabilityChart}</script>
                                    </div>
                                    <div id="viabilityChart" class="col-md-5 align-items-center">
                                        <table class="table table-bordered w-100 text-center">
                                            <thead class="table-active">
                                            <tr>
                                                <th class="headerSort">Category</th>
                                                <th>Lines</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="row" items="${viabilityTable}">
                                                <tr>

                                                    <c:if test="${row.mpId != null}">
                                                        <td>
                                                            <a href="${baseUrl}/phenotypes/${row.mpId}"
                                                               class="text-capitalize">${row.category}</a>
                                                        </td>
                                                    </c:if>
                                                    <c:if test="${row.mpId == null}">
                                                        <td><span class="text-capitalize">${row.category}</span></td>
                                                    </c:if>
                                                    <td>${row.count}</td>
                                                </tr>
                                            </c:forEach>
                                            <tr>
                                                <td colspan="2">
                                                    <a href="https://ftp.ebi.ac.uk/pub/databases/impc/all-data-releases/latest/results/viability.csv.gz"
                                                       style="text-decoration:none;" download> <i
                                                            class="fa fa-download"
                                                            alt="Download"></i> Download</a>
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-12">
                                        <p>
                                            Lethal strains are further phenotyped in the <a
                                                href="${cmsBaseUrl}/impress">embryonic
                                            phenotyping pipeline</a>. For embryonic
                                            lethal and subviable strains, heterozygotes are phenotyped in the IMPC
                                            <a
                                                    href="${cmsBaseUrl}/impress">adult phenotyping
                                                pipeline</a>.
                                        </p>
                                    </div>
                                </div>
                                <div class="row mb-3">
                                    <div id="embryoViabilityChart" class="col-md-7 h-100">
                                        <script
                                                type="text/javascript">
                                            $(function () {

                                                var modalTable = $('.embryo-modal-table');
                                                modalTable.bootstrapTable('refreshOptions', {
                                                    paginationParts: ["pageinfo", "pageList"]
                                                })

                                                fetch("//impc-datasets.s3.eu-west-2.amazonaws.com/embryo-landing-assets/wol_all_dr21.0.tsv")
                                                    .then(res => res.ok && res.text())
                                                    .then(data => {
                                                        var dataValues = tsvToJSON(data);

                                                        let dataIndex = {
                                                            "Early lethal": dataValues.filter(d => d.FUSIL.includes("early lethal")),
                                                            "Intermediate lethal": dataValues.filter(d => d.FUSIL.includes("intermediate lethal")),
                                                            "Late lethal": dataValues.filter(d => d.FUSIL.includes("late lethal")),
                                                            "Not determined": dataValues.filter(d => d.FUSIL.includes("not determined")),
                                                            "No data": dataValues.filter(d => d.FUSIL.includes("no data")),
                                                            "Conflicting": dataValues.filter(d => d.FUSIL.includes("conflicting"))
                                                        };

                                                        Object.keys(dataIndex).forEach(d => {
                                                            $('#wolTable' + d.replace(" " , "").replace(".", "")).text(dataIndex[d].length);
                                                        });

                                                        $('#embryoWindowsOfLethalityModal').on('show.bs.modal', function (event) {
                                                            modalTable.bootstrapTable('resetView');
                                                            var button = $(event.relatedTarget) // Button that triggered the modal
                                                            var category = button.data('category') // Extract info from data-* attributes
                                                            // If necessary, you could initiate an AJAX request here (and then do the updating in a callback).
                                                            // Update the modal's content. We'll use jQuery here, but you could use a data binding library or other methods instead.
                                                            var modal = $(this)
                                                            modal.find('.modal-title').text(category + " lines");


                                                            modalTable.bootstrapTable('load', dataIndex[category].map(d => {
                                                                var d1 = {...d};
                                                                d1["gene_id"] = "<a href='" + cmsBaseUrl + "/data/genes/"+ d["gene_id"] + "'>" + d["gene_id"] + "</a>";
                                                                return d1;
                                                            }));


                                                        })

                                                        $('#embryoViabilityChart').highcharts({
                                                            chart: {plotBackgroundColor: null, plotShadow: false},
                                                            colors: ['rgba(9, 120, 161,1)', 'rgba(255, 201, 67, 1)', 'rgba(239, 123, 11, 1)', 'rgba(119, 119, 119, 1)', 'rgba(36, 139, 75, 1)', 'rgba(238, 238, 180, 1)', 'rgba(191, 75, 50, 1)', 'rgba(191, 151, 50, 1)', 'rgba(239, 123, 11, 1)', 'rgba(247, 157, 70, 1)', 'rgba(247, 181, 117, 1)', 'rgba(191, 75, 50, 1)', 'rgba(151, 51, 51, 1)'],
                                                            title: {text: 'Secondary Viability / Windows of Lethality'},
                                                            credits: {enabled: false},
                                                            tooltip: {pointFormat: '<b>{point.percentage:.2f}%</b>'},
                                                            plotOptions: {
                                                                pie: {
                                                                    size: 200,
                                                                    showInLegend: true,
                                                                    allowPointSelect: true,
                                                                    cursor: 'pointer',
                                                                    dataLabels: {
                                                                        enabled: true,
                                                                        format: '{point.percentage:.2f} %',
                                                                        style: {color: '#666', width: '60px'}
                                                                    }
                                                                },
                                                                series: {
                                                                    dataLabels: {
                                                                        enabled: true,
                                                                        format: '{point.percentage:.2f}%'
                                                                    }
                                                                }
                                                            },
                                                            series: [{
                                                                type: 'pie',
                                                                name: '',
                                                                data: Object.keys(dataIndex).map( s => { return {name: s, y: dataIndex[s].length}})
                                                            }]
                                                        });
                                                    })
                                            });


                                            </script>
                                    </div>

                                    <div id="viabilityChart" class="col-md-5 align-items-center">
                                        <table class="table table-bordered w-100 text-center">
                                            <thead class="table-active">
                                            <tr>
                                                <th class="headerSort">Category</th>
                                                <th>Lines</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td>
                                                    <a
                                                       href="#" data-toggle="modal" data-target="#embryoWindowsOfLethalityModal" data-category="Early lethal">Early lethal</a>
                                                </td>
                                                <td id="wolTableEarlylethal"></td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <a
                                                            href="#" data-toggle="modal" data-target="#embryoWindowsOfLethalityModal" data-category="Intermediate lethal">Intermediate lethal</a>
                                                </td>
                                                <td id="wolTableIntermediatelethal"></td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <a href="#" data-toggle="modal" data-target="#embryoWindowsOfLethalityModal" data-category="Late lethal">Late lethal</a>
                                                </td>
                                                <td id="wolTableLatelethal"></td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <a href="#" data-toggle="modal" data-target="#embryoWindowsOfLethalityModal" data-category="Not determined">Not determined</a>
                                                </td>
                                                <td id="wolTableNotdetermined"></td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <a href="#" data-toggle="modal" data-target="#embryoWindowsOfLethalityModal" data-category="No data">No data</a>
                                                </td>
                                                <td id="wolTableNodata"></td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <a href="#" data-toggle="modal" data-target="#embryoWindowsOfLethalityModal" data-category="Conflicting">Conflicting</a>
                                                </td>
                                                <td id="wolTableConflicting"></td>
                                            </tr>
                                            <tr>
                                                <td colspan="2">
                                                    <a href="https://impc-datasets.s3.eu-west-2.amazonaws.com/embryo-landing-assets/wol_all_dr21.0.tsv"
                                                       style="text-decoration:none;" download> <i
                                                            class="fa fa-download"
                                                            alt="Download"></i> Download</a>
                                                </td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container" style="line-height: 2">
                                <div class="row">
                                    <div class="col-12">
                                        <h2>Embryo Data Availability Grid</h2>
                                    </div>
                                </div>


                                <div class="row">
                                    <div class="col-12">
                                        <p>Filter by Window of Lethality</p>
                                        <div id="root"></div>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>


            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container" style="line-height: 2">
                                <div class="row">
                                    <div class="col-12">
                                        <h2>Accessing Embryo Phenotype Data</h2>
                                    </div>
                                </div>


                                <div class="row">
                                    <div class="col-12">
                                        <p>
                                            Embryo phenotype data can be accessed in multiple ways:
                                        </p>
                                        <ul>
                                            <li>
                                                <a href="${baseUrl}/embryo_imaging">Embryo Images: interactive
                                                    heatmap</a>
                                                A compilation of all our Embryo Images, organised by gene and life
                                                stage, with access to the Interactive Embryo Viewer, where you can
                                                compare mutants and wild types side by side and rotate 2D and 3D
                                                images; we also provide access to our external partners' embryo
                                                images.
                                            </li>
                                            <li>
                                                <a href="${baseUrl}/embryo/vignettes">Embryo Vignettes</a>
                                                Showcase of best embryo images with detailed explanations.
                                            </li>
                                            <li>
                                                <a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/all-data-releases/latest/results/">
                                                    From the FTP site, latest release</a>
                                                All our results. Reports need to be filtered by a dedicated column,
                                                Life Stage (E9.5, E12.5, E15.5 and E18.5). Please check the README
                                                file or see documentation <a
                                                    href="https://www.mousephenotype.org/help/non-programmatic-data-access/">here</a>.
                                            </li>
                                            <li>
                                                Using the REST API (see documentation <a
                                                    href="https://www.mousephenotype.org/help/programmatic-data-access/">here</a>)
                                            </li>
                                        </ul>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container" style="line-height: 2">

                                <div class="row">
                                    <div class="col-12">
                                        <jsp:include page="paper_frag.jsp"></jsp:include>
                                    </div>

                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="modal  fade" id="embryoWindowsOfLethalityModal" tabindex="-1" aria-labelledby="embryoWindowsOfLethalityModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="embryoWindowsOfLethalityModalLabel">Modal title</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <table class="embryo-modal-table"
                               data-pagination="true" data-toggle="table" data-search="true">
                            <thead>
                            <tr>
                                <th data-field="gene_symbol">Gene symbol</th>
                                <th data-field="gene_id">MGI Accession ID</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
        </div>
        <!-- Modal -->



    </jsp:body>

</t:genericpage>

