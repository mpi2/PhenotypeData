<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>

    <jsp:attribute name="title">${gene.markerSymbol} - ${gene.markerName}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body class="gene-node no-sidebars small-header">

        </jsp:attribute>


    <jsp:attribute name="header">
        		<script type="text/javascript">
                    var base_url = '${baseUrl}';
                    var geneId = '${gene.mgiAccessionId}';
                </script>
        <script type='text/javascript' src="${baseUrl}/js/general/dropDownExperimentPage.js?v=${version}" async></script>
		<%--script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script--%>
        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>

        <%--script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script--%>
        <%--script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script--%>

            <script src="${baseUrl}/js/general/enu.js"></script>
            <script src="${baseUrl}/js/general/dropdownfilters.js" async></script>
            <script type="text/javascript" src="${baseUrl}/js/general/allele.js" async></script>
        <%-- Phenogrid requirements --%>
        <script type="text/javascript"
                src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.js?v=${version}" async></script>
            <link rel="stylesheet" type="text/css"
                  href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" async>

        <%-- Phenodigm2 requirements --%>
        <script src="//d3js.org/d3.v4.min.js"></script>
            <script type="text/javascript">var impc = {baseUrl: "${baseUrl}"}</script>        
            <script type="text/javascript" src="${baseUrl}/js/vendor/underscore/underscore-1.8.3.min.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/phenodigm2/phenodigm2.js?v=${version}" async></script>
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/phenodigm2.css" async>
        <%-- End of phenodigm2 requirements --%>

        <script type="text/javascript">
            var gene_id = '${acc}';
            var monarchUrl = '${monarchUrl}';

            $(document).ready(function () {
                var heatmap_generated = 0;
                var expressionTab = 0;
                var hash = location.hash;
                if (hash.indexOf("tabs-") > -1) {
                    expressionTab = $('a[href="' + hash + '"]').parent().index();
                    $("#section-expression").focus();
                }

                $("#exptabs").tabs({active: expressionTab});
                $("#phenotabs").tabs({active: 0});
                $("#phenotabs2").tabs({active: 0});
                $("#tabs").tabs();

                $('div#anatomo1').hide(); // by default
                $('div#embryo1').hide(); // by default

                $('.wtExp').hide();
                $('div#toggleWt').click(function () {
                    if ($('.wtExp').is(':visible')) {
                        $('.wtExp').hide();
                        $(this).text("Show Wildtype Expression");
                    } else {
                        $('.wtExp').show();
                        $(this).text("Hide Wildtype Expression");
                    }
                });

                $('div#anatogramToggle').change(function () {
                    if ($('#anatomo1').is(':visible')) {
                        $('#anatomo1').hide();
                        $('#anatomo2').show();
                        $(this).text("Show expression table");
                    } else {
                        $('#anatomo1').show();
                        $('#anatomo2').hide();
                        $(this).text("Hide expression table");
                    }
                });

                $('input[name=options]').change(function () {
                    var value = $('input[name=options]:checked').val();
                    if (value === 'anatogram') {
                        $('#anatomo1').hide();
                        $('#anatomo2').show();
                    } else {
                        $('#anatomo2').hide();
                        $('#anatomo1').show();
                    }
                });

                $('input[name=optionsEmbryo]').change(function () {
                    var value = $('input[name=optionsEmbryo]:checked').val();
                    if (value === 'anatogram') {
                        $('#embryo1').hide();
                        $('#embryo2').show();
                    } else {
                        $('#embryo2').hide();
                        $('#embryo1').show();
                    }
                });




                /*$('#heatmap_link').click(function () {
                    console.log('heatmap link clicked');

                    /* //load the css
                     var cssId = 'myCss';  // you could encode the css path itself to generate id..
                     if (!document.getElementById(cssId))
                     {
                     var head  = document.getElementsByTagName('head')[0];
                     var link  = document.createElement('link');
                     link.id   = cssId;
                     link.rel  = 'stylesheet';
                     link.type = 'text/css';
                     link.href = '

                    drupalBaseUrl/heatmap/css/heatmap.1.3.1.css';
                         link.media = 'all';
                         head.appendChild(link);
                         }

                    if ($('#heatmap_toggle_div').length) {//check if this div exists first as this will ony exist if phenotypeStarted and we don't want to do this if not.
                        $('#heatmap_toggle_div').toggleClass('hidden');//toggle the div whether the heatmap has been generated or not.
                        $('#phenotypeTableDiv').toggleClass('hidden');
                        if (!heatmap_generated) {

                            /*var script = document.createElement('script');
                            //script.src = "${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js";
                            script.src = "${baseUrl}/js/vendor/dcc/heatmap.js";
                            script.onload = function () {

                                //do stuff with the script
                                new dcc.PhenoHeatMap({
                                    'container': 'phenodcc-heatmap',
                                    'mgiid': '${gene.mgiAccessionId}',
                                    'mode': 'ontological',
                                    'ncol': 5,
                                    'title': '${gene.markerSymbol}',
                                    'url': {
                                        'jssrc': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/js/',
                                        'json': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/',
                                        'viz': dcc.heatmapUrlGenerator
                                    }
                                });
                                heatmap_generated = 1;

                            };


                            document.head.appendChild(script);


                        }//end of if heatmap generated

                    }


                });

                // registerInterest();*/

            });


            function registerInterest() {

                $('a.regInterest').click(function () {

                    var anchorControl = $(this);
                    var iconControl = $(anchorControl).find('i');
                    var endpoint = $(anchorControl).attr('href');

                    var currentAnchorText = $(anchorControl).text().trim();

                    function riSuccess() {

                        if (currentAnchorText.toUpperCase() === 'Unregister Interest'.toUpperCase()) {

                            $(iconControl).removeClass('fa-sign-out');
                            $(iconControl).addClass('fa-sign-in');

                            endpoint = endpoint.replace('unregistration', 'registration');
                            $(anchorControl).attr('href', endpoint);

                            $(anchorControl).html('Register interest');

                        } else {

                            // Register -> Unregister
                            $(iconControl).removeClass('fa-sign-in');
                            $(iconControl).addClass('fa-sign-out');

                            endpoint = endpoint.replace('registration', 'unregistration');
                            $(anchorControl).attr('href', endpoint);

                            $(anchorControl).html('Unregister interest');
                        }
                    }

                    function riError() {
                        window.alert('Unable to access register interest service at this time.');
                    }

                    if (endpoint.includes('login?target')) {
                        $.ajax({
                            url: endpoint,
                            dataType: 'jsonp',
                            beforeSend: setHeader,
                            error: riError
                        });
                    } else {
                        $.ajax({
                            url: endpoint,
                            dataType: undefined,
                            beforeSend: undefined,
                            success: riSuccess,
                            error: riError
                        });
                    }

                    return false;
                });
            }


        </script>

            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/genes.css"/>				

            <c:if test="${phenotypeStarted}">
                <!--[if !IE]><!-->
                <link rel="stylesheet" type="text/css"
                      href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
                <!--<![endif]-->
                <!--[if IE 8]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
                <![endif]-->
                <!--[if gte IE 9]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
                <![endif]-->
            </c:if>

        </jsp:attribute>

    <jsp:body>
        <div class="container data-heading">
            <div class="row row-shadow">
                <div class="col-12 no-gutters">
                    <h2>Gene: ${gene.markerSymbol} <i class="fal fa-bell" style="float: right"></i></h2>
                </div>
            </div>
        </div>

        <!--div class="container single single--no-side">
        <div class="row">
        <div class="col-12 white-bg">
        <div class="page-content pt-5 pb-5">
        <%--jsp:include page="genesGene_frag.jsp"/--%>
        </div>
        </div>
        </div>
        </div-->

        <!--div class="container">
        <div class="row">
        <div class="col-12 no-gutters">
        <h3>Phenotypes for ${gene.markerSymbol}</h3>
        </div>
        </div>
        </div-->

        <div class="container single single--no-side">
            <div class="row row-over-shadow">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">
                        <jsp:include page="genesPhenotypeAssociation_frag.jsp"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" id="expression">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h3><i class="icon icon-conceptual icon-expression"></i>&nbsp;Expression</h3>
                </div>
            </div>
        </div>

        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">
                        <c:if test="${empty impcAdultExpressionImageFacetsWholemount
                                                  and empty impcAdultExpressionImageFacetsSection
                                                  and empty expressionAnatomyToRow
                                                  and empty impcEmbryoExpressionImageFacets
                                                  and empty embryoExpressionAnatomyToRow
                                                  and empty expressionFacets}">
                            <div class="alert alert-warning mt-3">Expression data not available</div>
                        </c:if>

                        <c:if test="${not empty impcAdultExpressionImageFacetsWholemount
                                                  or not empty impcAdultExpressionImageFacetsSection
                                                  or not empty expressionAnatomyToRow
                                                  or not empty impcEmbryoExpressionImageFacets
                                                  or not empty embryoExpressionAnatomyToRow}">

                            <h4>IMPC lacZ Expression Data</h4>
                            <!-- section for expression data here -->
                            <ul class="nav nav-tabs" id="expressionTab" role="tablist">
                                <li class="nav-item">
                                    <a class="nav-link active" id="adult-tab" data-toggle="tab" href="#adult"
                                       role="tab" aria-controls="adult-tab" aria-selected="false">Adult Expression Images</a>
                                </li>
                                <!--li class="nav-item">
                                    <a class="nav-link" id="adult-image-tab" data-toggle="tab" href="#adult-image"
                                       role="tab" aria-controls="adult-image-tab" aria-selected="false">Adult Expression
                                        Image</a>
                                </li-->
                                <li class="nav-item">
                                    <a class="nav-link" id="_embryo-tab" data-toggle="tab" href="#_embryo"
                                       role="tab" aria-controls="_embryo-tab" aria-selected="true">Embryo
                                        Expression Images</a>
                                </li>
                                <!--li class="nav-item">
                                    <a class="nav-link" id="embryo-image-tab" data-toggle="tab" href="#embryo-image"
                                       role="tab" aria-controls="embryo-image-tab" aria-selected="false">Embryo
                                        Expression Images</a>
                                </li-->
                            </ul>
                            <div class="tab-content" id="expressionTabContent">
                                <div class="tab-pane fade show active" id="adult" role="tabpanel"
                                     aria-labelledby="adult-tab">
                                    <c:choose>
                                        <c:when test="${not empty expressionAnatomyToRow }">
                                            <div>
                                                <!-- Expression in Anatomogram -->
                                                <jsp:include page="genesAnatomogram_frag.jsp"></jsp:include>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert alert-warning mt-3">
                                                <!-- Expression in Anatomogram -->
                                                No expression data was found for this adult tab
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <%--div class="tab-pane fade" id="adult-image" role="tabpanel"
                                     aria-labelledby="adult-image-tab">
                                    <c:choose>
                                        <c:when test="${not empty wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms && not empty sectionExpressionImagesBean.filteredTopLevelAnatomyTerms}">
                                            <jsp:include page="genesAdultLacZExpImg_frag.jsp"></jsp:include>
                                        </c:when>
                                        <c:otherwise>
                                            <h5>
                                                No expression image was found for this adult tab
                                            </h5>
                                        </c:otherwise>
                                    </c:choose>
                                </div--%>
                                <div class="tab-pane fade" id="_embryo" role="tabpanel" aria-labelledby="_embryo-tab">
                                    <c:choose>
                                        <c:when test="${not empty embryoExpressionAnatomyToRow}">
                                            <jsp:include page="genesEmbExpData_frag.jsp"></jsp:include>
                                        </c:when>
                                        <c:otherwise>
                                        <div class="alert alert-warning mt-3">
                                                No expression data was found for this embryo tab
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <%--div class="tab-pane fade" id="embryo-image" role="tabpanel"
                                     aria-labelledby="embryo-image-tab">
                                    <c:choose>
                                        <c:when test="${not empty wholemountExpressionImagesEmbryoBean.expFacetToDocs || not empty sectionExpressionEmbryoImagesBean.expFacetToDocs}">
                                            <jsp:include page="genesEmbExpImg_frag.jsp"></jsp:include>
                                        </c:when>
                                        <c:otherwise>
                                            <h5>
                                                No expression image was found for this embryo tab
                                            </h5>
                                        </c:otherwise>
                                    </c:choose>
                                </div--%>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" id="images">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h3><i class="fal fa-images"></i>&nbsp;Associated Images</h3>
                </div>
            </div>
        </div>

        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">
                        <div>
                            <c:if test="${empty impcImageGroups and empty solrFacets}">
                                <div class="alert alert-warningmt-3">Phenotype associated images not available</div>
                            </c:if>

                            <c:if test="${not empty impcImageGroups or not empty solrFacets}">
                                <c:if test="${not empty impcImageGroups}">
                                    <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                </c:if>

                                <c:if test="${not empty impcImageFacets and not empty solrFacets}">
                                    <hr>
                                </c:if>

                                <c:if test="${not empty solrFacets}">
                                    <h5>Legacy Phenotype Associated Images</h5>
                                    <jsp:include page="genesLegacyPhenoAssocImg_frag.jsp"></jsp:include>
                                </c:if>

                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="container" id="diseases">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h3><i class="fal fa-procedures"></i>&nbsp;Disease Models</h3>
                </div>
            </div>
        </div>


        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 white-bg">
                    <div class="page-content pb-5">
                        <ul class="nav nav-tabs" id="diseasesTab" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link active" id="byAnnotation-tab" data-toggle="tab" href="#byAnnotation"
                                   role="tab" aria-controls="byAnnotation-tab" aria-selected="false">By Annotation and
                                    Orthology</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" id="byPhenotype-tab" data-toggle="tab" href="#byPhenotype"
                                   role="tab" aria-controls="byPhenotype-tab" aria-selected="false">By phenotypic
                                    Similarity</a>
                            </li>
                        </ul>
                        <div class="tab-content mt-2" id="diseasesTabContent">
                            <div class="tab-pane fade show active" id="byAnnotation" role="tabpanel"
                                 aria-labelledby="byAnnotation-tab">
                                <c:choose>
                                    <c:when test="${!hasModelsByOrthology}">
                                        <div class="alert alert-warning mt-3">
                                            No associations by disease annotation and gene orthology found.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <table id="diseases_by_annotation"
                                               class="table tablesorter disease" style="width:100%"></table>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="tab-pane fade" id="byPhenotype" role="tabpanel"
                                 aria-labelledby="byPhenotype-tab">
                                <c:choose>
                                    <c:when test="${empty modelAssociations}">
                                    <div class="alert alert-warning mt-3">
                                        No associations by phenotypic similarity found.
                                    </div>
                                    </c:when>
                                    <c:otherwise>
                                        <table id="diseases_by_phenotype" class="table tablesorter disease"
                                               style="width:100%"></table>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="container" id="order">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h3><i class="fal fa-shopping-cart"></i>&nbsp;Order Mouse and ES Cells</h3>
                </div>
            </div>
        </div>

        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">
                        <jsp:include page="orderSectionFrag.jsp"></jsp:include>
                    </div>
                </div>
            </div>
        </div>

        <!-- End of Order Mouse and ES Cells -->


        <%-- Block augmenting/filling phenodigm tables --%>
        <script type="text/javascript">
            var curatedDiseases = ${curatedDiseases};
            var modelAssociations = ${modelAssociations}; // this object name is required in phenodigm2.js
        </script>
        <script type="text/javascript">
            // disease tables drive by phenodigm core
            var diseaseTableConfs = [
                {
                    id: '#diseases_by_annotation',
                    tableConf: {
                        paging: false,
                        info: false,
                        searching: false,
                        order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                        pagingType: "full_numbers",
                        responsive: true
                    },
                    phenodigm2Conf: {
                        pageType: "genes",
                        gene: "${gene.mgiAccessionId}",
                        groupBy: "diseaseId",
                        filterKey: "diseaseId",
                        filter: curatedDiseases,
                        minScore: 0,
                        innerTables: true,
                        responsive: true
                    }
                },
                {
                    id: '#diseases_by_phenotype',
                    tableConf: {
                        order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                        pageLength: 20,
                        lengthMenu: [20, 50, 100],
                        pagingType: "full_numbers",
                        responsive: true
                    },
                    phenodigm2Conf: {
                        pageType: "genes",
                        gene: "${gene.mgiAccessionId}",
                        groupBy: "diseaseId",
                        filterKey: "diseaseId",
                        filter: [],
                        minScore: 1,
                        innerTables: true,
                        responsive: true
                    }
                }];

            $(document).ready(function () {
                // create phenodigm tables
                for (var i = 0; i < diseaseTableConfs.length; i++) {
                    var dTable = diseaseTableConfs[i];
                    impc.phenodigm2.makeTable(modelAssociations, dTable.id, dTable.phenodigm2Conf);
                    var dataTable = $(dTable.id).DataTable(dTable.tableConf);
                    $.fn.addTableClickPhenogridHandler(dTable.id, dataTable);
                }
            });
        </script>


    </jsp:body>

</t:genericpage>