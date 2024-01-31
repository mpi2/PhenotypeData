<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>

    <jsp:attribute name="title">${gene.markerSymbol} Mouse Gene Details | ${gene.markerName}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body class="page-template page-template-no-sidebar--large"></jsp:attribute>

    <jsp:attribute name="header">

        <link rel="canonical" href="https://www.mousephenotype.org/data/genes/${gene.mgiAccessionId}" />
        <meta name="description" content="Phenotype data for mouse gene ${gene.markerSymbol}. Discover ${gene.markerSymbol}'s significant phenotypes, expression, images, histopathology and more. Data for gene ${gene.markerSymbol} is all freely available for download." />
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <script type="text/javascript">
            var gene_id = '${acc}';
            var monarchUrl = '${monarchUrl}';
            var base_url = '${baseUrl}';
            var geneId = '${gene.mgiAccessionId}';
        </script>

        <script defer type='text/javascript' src="${baseUrl}/js/general/dropDownExperimentPage.js?v=${version}" ></script>

        <script defer type='text/javascript' src="https://code.highcharts.com/10.2/highcharts.js"></script>
        <script defer type='text/javascript' src="https://code.highcharts.com/10.2/modules/exporting.js"></script>
        <script defer type='text/javascript' src="https://code.highcharts.com/10.2/modules/broken-axis.js"></script>


        <script defer type='text/javascript' src="${baseUrl}/js/general/enu.js"></script>
        <script defer type='text/javascript' src="${baseUrl}/js/general/dropdownfilters.js" ></script>
        <script defer type="text/javascript" src="${baseUrl}/js/general/allele.js" ></script>

        <%-- Phenogrid requirements --%>
        <script defer type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.js?v=${version}" ></script>

        <%-- Load async CSS stylesheet, see https://www.filamentgroup.com/lab/load-css-simpler/ --%>
        <link rel="preload" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" as="style" />
        <link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" media="print" onload="this.media='all'" />

        <script type="text/javascript">

            document.addEventListener("DOMContentLoaded", function () {

                // Required for all-data-table
                var hash = location.hash;

                // Enable CSRF processing for forms on this page
                function loadCsRf() {
                    var token = $("meta[name='_csrf']").attr("content");
                    var header = $("meta[name='_csrf_header']").attr("content");
                    $(document).ajaxSend(function(e, xhr, options) {
                        xhr.setRequestHeader(header, token);
                    });
                }
                loadCsRf();

                // Wire up the AJAX callbacks to the appropriate forms
                $('#follow-form').submit(function(event) {

                    // Prevent the form from submitting when JS is enabled
                    event.preventDefault();

                    // Do asynch request to change the state of the follow flag for this gene
                    // and update button appropriately on success
                    $.ajax({
                        type: "POST",
                        url: "${baseUrl}/update-gene-registration",
                        headers: {'asynch': 'true'},
                        data: $(this).serialize(),
                        success: function(data) {

                            // Data is a map of gene accession id -> status
                            // Status is either "Following" or "Not Following"

                            switch(data["${acc}"]) {
                                case "Following":
                                    $('form#follow-form').find("button")
                                        .attr('title', 'You are following ${gene.markerSymbol}. Click to stop following.')
                                        .removeClass('btn-primary')
                                        .addClass('btn-outline-secondary');

                                    $('form#follow-form').find("span")
                                        .text('Unfollow');

                                    $('form#follow-form').find('i')
                                        .removeClass('fa-user-plus')
                                        .addClass('fa-user-minus');
                                    break;

                                case "Not Following":
                                    $('form#follow-form').find("button")
                                        .attr('title', 'Click to follow ${gene.markerSymbol}.')
                                        .addClass('btn-primary')
                                        .removeClass('btn-outline-secondary')

                                    $('form#follow-form').find("span")
                                        .text('Follow');

                                    $('form#follow-form').find('i')
                                        .removeClass('fa-user-minus')
                                        .addClass('fa-user-plus');
                                    break;
                            }
                        },
                        error: function() {
                            window.location = "${baseUrl}/rilogin?target=${baseUrl}/genes/${acc}";
                        }
                    });
                });
            });



        </script>

        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/genes.css"/>

        <c:if test="${liveSite || param.checklive != null}">
            <!-- Google Tag Manager -->
            <script>
                (function (w, d, s, l, i) {
                    w[l] = w[l] || [];
                    w[l].push({
                        'gtm.start':
                            new Date().getTime(), event: 'gtm.js'
                    });
                    var f = d.getElementsByTagName(s)[0],
                        j = d.createElement(s), dl = l != 'dataLayer' ? '&l=' + l : '';
                    j.async = true;
                    j.src =
                        'https://www.googletagmanager.com/gtm.js?id=' + i + dl;
                    f.parentNode.insertBefore(j, f);
                })(window, document, 'script', 'dataLayer', 'GTM-NZPSPWR');
            </script>
            <!-- End Google Tag Manager -->
        </c:if>

        <script type="application/ld+json">
        {
            "@type": "Gene",
            "@id": "http://www.informatics.jax.org/marker/${gene.mgiAccessionId}",
            "identifier": "${gene.mgiAccessionId}",
            "name": "${gene.markerSymbol}",
            "description": "Mouse (Mus musculus) gene ${gene.markerName}",
            "taxonomicRange": "http://purl.obolibrary.org/obo/NCIT_C45247"
        }
        </script>

        <%-- Style bars css--%>
         <style>
             * { box-sizing: border-box; }

             .sizing-box {
                 height: 40px;
                 width: 100px;
             }

             .signal-bars {
                 display: inline-block;
             }

             .signal-bars .bar {
                 width: 10%;
                 margin-left: 1%;
                 min-height: 20%;
                 display: inline-block;
             }
             .signal-bars .bar.first-bar  { height: 20%; }
             .signal-bars .bar.second-bar { height: 40%; }
             .signal-bars .bar.third-bar  { height: 60%; }
             .signal-bars .bar.fourth-bar { height: 80%; }
             .signal-bars .bar.fifth-bar  { height: 99%; }

             .good .bar {
                 /*background-color: #16a085;*/
                 background-color: #8e8e8e;
                 /*border: thin solid darken(#16a085, 7%);*/
                 border: thin solid darken(#8e8e8e, 7%);
             }
             .bad .bar {
                 background-color: #e74c3c;
                 border: thin solid darken(#e74c3c, 20%);
             }
             .ok .bar {
                 background-color: #1e7e34;
                 border: thin solid darken(#1e7e34, 7%);
             }

             .four-bars .bar.fifth-bar,
             .three-bars .bar.fifth-bar,
             .three-bars .bar.fourth-bar,
             .one-bar .bar:not(.first-bar),
             .zero-bars .bar,
             .two-bars .bar:not(.first-bar):not(.second-bar) {
                 background-color: #fafafa;
                 border: thin solid #8e8e8e;
             }


             .card .card-title{
                 text-transform: capitalize;
             }
             .card .card-footer {
                 border-top: none;
             }
             .card .card-footer a {
                 border-radius: 0 0 0.25rem 0.25rem;
             }

             .btn.disabled, fieldset:disabled a.btn{
                 background: #AEAEAE;
                 color: #fff;
                 opacity: 1;
             }

             /* Phenogrid key */
             .phenogrid-key .key span{
                 vertical-align: middle;
             }
             .phenogrid-key .key:before {
                 content: "";
                 display: inline-block;
                 width: 10px;
                 height: 10px;
                 margin-right: 5px;
                 vertical-align: middle;
             }
             .phenogrid-key .key.key-significant:before {
                 background-color: #ce6211;
             }
             .phenogrid-key .key.key-not-significant:before {
                 background-color: #17a2b8;
             }
             .phenogrid-key .key.key-not-tested:before {
                 background-color: #6c757d;
             }

             /* Tabs */
             .nav-tabs {
                 border-bottom: 1px solid #dee2e6;
                 margin-bottom: 30px;
             }
             .nav-tabs .nav-link {
                 border: 1px solid #D6D9DC;
                 border-top-left-radius: 0.25rem;
                 border-top-right-radius: 0.25rem;
                 background: #ECEFF1;
                 color: #000;
                 margin-right: -1px;
             }
             .nav-tabs .nav-link.active, .nav-tabs .nav-item.show .nav-link {
                 color: #000;
                 background-color: #fff;
                 border-color: #ED7B25 #D6D9DC #fff;
                 border-top-width: 4px;
                 padding: 0.4em 1em;
             }

             /* Required for adjusting bootstrap tables default sort arrows */
             .bootstrap-table .fixed-table-container .table thead .th-inner.sortable.both.both,
             .bootstrap-table .fixed-table-container .table thead .th-inner.sortable.both.asc,
             .bootstrap-table .fixed-table-container .table thead .th-inner.sortable.both.desc {
                 background-image: none;
             }
             .th-inner.sortable.both.asc:after {
                 font-family: "Font Awesome 5 Pro";
                 padding-left: 3px;
                 content: "\f0de";
                 font-size: 150%;
                 vertical-align: middle;
             }
             .th-inner.sortable.both.desc:after {
                 font-family: "Font Awesome 5 Pro";
                 padding-left: 3px;
                 content: "\f0dd";
                 font-size: 150%;
                 vertical-align: middle;
             }
             .th-inner.sortable.both:after {
                 font-family: "Font Awesome 5 Pro";
                 padding-left: 3px;
                 content: "\f0dc";
                 font-size: 150%;
                 vertical-align: middle;
             }
         </style>




    </jsp:attribute>


    <jsp:attribute name="addToFooter">

<script>

    var phenotypeSystemToMpTerms = {
        "mortality/aging": ["MP:0010768"],
        "embryo phenotype": ["MP:0005380"],
        "reproductive system phenotype": ["MP:0005389"],
        "growth/size/body region phenotype": ["MP:0005378"],
        "homeostasis/metabolism phenotype or adipose tissue phenotype": ["MP:0005376", "MP:0005375"],
        "behavior/neurological phenotype or nervous system phenotype": ["MP:0005386", "MP:0003631"],
        "cardiovascular system phenotype": ["MP:0005385"],
        "respiratory system phenotype": ["MP:0005388"],
        "digestive/alimentary phenotype or liver/biliary system phenotype": ["MP:0005381", "MP:0005370"],
        "renal/urinary system phenotype": ["MP:0005367"],
        "limbs/digits/tail phenotype": ["MP:0005371"],
        "skeleton phenotype": ["MP:0005390"],
        "immune system phenotype or hematopoietic system phenotype": ["MP:0005387", "MP:0005397"],
        "muscle phenotype": ["MP:0005369"],
        "integument phenotype or pigmentation phenotype": ["MP:0010771", "MP:0001186"],
        "craniofacial phenotype": ["MP:0005382"],
        "hearing/vestibular/ear phenotype": ["MP:0005377"],
        "taste/olfaction phenotype": ["MP:0005394"],
        "endocrine/exocrine gland phenotype": ["MP:0005379"],
        "vision/eye phenotype": ["MP:0005391"]
    };

    function removeOption(filterName, value) {
        $("#systemSelector" + filterName + " option[value='" + value + "']").prop("selected", false);
        $('#systemSelector' + filterName).selectpicker('render');
        filterAllData(filterName);
    }

    function filterAllData(filterName) {
        var val = [];
        var legend = '';
        $('#systemSelector' + filterName).val().forEach(function (value) {
            var mpTerm = value.split('|')[0];
            var name = value.split('|')[1];
            var significance = value.split('|')[2];
            var icon = value.split('|')[3];
            val.push(phenotypeSystemToMpTerms[name]);
            var color = significance === 'significant' ? 'badge-primary' : 'badge-info';
            legend += '<span class="badge badge-pill filter-badge ' + color + ' mr-1"><i class="badge-icon ' + icon + '"></i> ' + name.replace(/phenotype/g, "") + ' <i class="close-badge fas fa-times" onclick="removeOption(\'' + filterName + '\',\'' + value + '\')"></i></span>';
        });
        legend = legend === '' ? ' all phenotypes' : legend;
        $('#ph' + filterName + 'DataTitle').html(legend);
        var content_id = '#all-' + filterName.toLocaleLowerCase();
        $(content_id).html("     <div class=\"pre-content\">\n" +
            "                        <div class=\"row no-gutters\">\n" +
            "                            <div class=\"col-12 my-5\">\n" +
            "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </div>");
        $('#phenotypesTab').scrollTop();
        $.ajax({
            url: baseUrl + '/experiments' + filterName + 'Frag?geneAccession=' + '${gene.mgiAccessionId}' + '&mpTermId=' + val.join(','),
            type: 'GET',
            success: function (data) {
                $(content_id).html(data);
            }
        });
    }

    var firstChart = true;
    var firstTable = true;
    var currentView = 'chart';
    var placeholderText = "<div class=\"pre-content\">\n" +
        "    <div class=\"row no-gutters\">\n" +
        "        <div class=\"col-12 my-5\">\n" +
        "            <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>";

    function chartClick() {
        if (firstChart) {
            $('#all-chart').html(placeholderText);
            $.ajax({
                url: baseUrl + '/experimentsChartFrag?geneAccession=' + '${gene.mgiAccessionId}',
                type: 'GET',
                success: function (data) {
                    $('#all-chart').html(data);
                    firstChart = false;
                }
            });
        }
    }

    function tableClick() {
        if (firstTable) {
            $('#all-table').html(placeholderText);
            $.ajax({
                url: baseUrl + '/experimentsTableFrag?geneAccession=' + '${gene.mgiAccessionId}',
                type: 'GET',
                success: function (data) {
                    $('#all-table').html(data);
                    firstTable = false;
                }
            });
        }
    }

    if (window.location.hash == '#alldatatable') {
        tableClick();
        $("#alldatatable-tab").click();
    }

    <c:if test='${measurementsChartNumber > 0}'>$("#alldatachart-tab").on('click', chartClick);
    </c:if>
    <c:if test='${allMeasurementsNumber > 0}'>$("#alldatatable-tab").on('click', tableClick);
    </c:if>

    <c:if test='${rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber > 0}'>
    $(document).ready(chartClick);
    </c:if>
    <c:if test='${rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber <= 0 && allMeasurementsNumber > 0}'>
    $(document).ready(tableClick);
    </c:if>


    // disease tables drive by phenodigm core

    var diseaseDetailFilter = function(index, row) {
        return $(row[2]).text().length > 1;
    }
    var toggleDetail = function(index) {
        $('#diseases_by_annotation').bootstrapTable('toggleDetailView', index)
        $('#diseases_by_prediction').bootstrapTable('toggleDetailView', index)
    }
    var diseaseDetailFormatter = function(index, row) {
        var gridColumnWidth = 25;
        var gridRowHeight = 50;

        $.ajax({
            url: row._data['link'],
            type: 'GET',
            success: function (data) {
                Phenogrid.createPhenogridForElement($('div#phenodigm' + index), {
                    // sort method of sources: "Alphabetic", "Frequency and Rarity", "Frequency,
                    selectedSort: "Frequency and Rarity",
                    gridSkeletonDataVendor: 'IMPC',
                    gridSkeletonData: data,
                    singleTargetModeTargetLengthLimit: gridColumnWidth,
                    sourceLengthLimit: gridRowHeight
                });
            }
        });
        return '<div id="phenodigm' + index + '"></div>';
    }


    document.addEventListener("DOMContentLoaded", function () {

        $('#diseases_by_annotation').bootstrapTable({ classes: 'table'});
        $('#diseases_by_prediction').bootstrapTable({ classes: 'table'});
        $('#publications_table').bootstrapTable({ classes: 'table'});

        $('#significantPhenotypesTable').on('click-row.bs.table', function (e, row) {
            if (row._data['link']) {
                window.location = row._data['link'];
            }
        });

        $('#histopathPhenotypesTable').on('click-row.bs.table', function (e, row) {
            if (row._data['link']) {
                window.location = row._data['link'];
            }
        });

    });




</script>
    </jsp:attribute>

    <jsp:body>

        <c:if test="${liveSite || param.checklive != null}">
        <!-- Google Tag Manager (noscript) -->
            <noscript>
                <iframe src="https://www.googletagmanager.com/ns.html?id=GTM-NZPSPWR" height="0" width="0" style="display:none;visibility:hidden"></iframe>
            </noscript>
            <!-- End Google Tag Manager (noscript) -->
        </c:if>



        <!-- Orange header layout -->
        <div class="container data-heading">
            <div class="row">
                <noscript>
                    <div class="col-12 no-gutters">
                        <h5 style="float: left">
                            Please enable javascript if you want to log in to follow or stop following this gene.</h5>
                    </div>
                </noscript>

                <div class="col-12 no-gutters">
                    <h1 class="h1 m-0 d-inline-block">
                        <b>Gene: ${gene.markerSymbol}</b>
                        <small style="font-size: 24px" > <a class="text-dark" style="display: inline-flex; align-items: center;" target="_blank" rel="noreferrer" href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}"> <span>${gene.mgiAccessionId} </span> <i class="fal fa-external-link fa-xs" style="margin-left: 2px"></i></a></small>
                    </h1>
                    <span>
                        <c:choose>
                            <c:when test="${isFollowing}">
                                <c:set var="followText" value="Unfollow"/>
                                <c:set var="activeClass" value="btn-outline-secondary"/>
                                <c:set var="activeIconClass" value="fa-user-minus"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="followText" value="Follow"/>
                                <c:set var="activeClass" value="btn-primary"/>
                                <c:set var="activeIconClass" value="fa-user-plus"/>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${isLoggedIn }">
                                <form action="${baseUrl}/update-gene-registration" method="POST" id="follow-form">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <input type="hidden" name="geneAccessionId" value="${acc}" />
                                    <input type="hidden" name="target" value="${baseUrl}/genes/${acc}" />
                                    <button type="submit" style="float: right" class="btn ${activeClass}">
                                        <i class="mt-1 fas ${activeIconClass}"></i>
                                        <span>${followText}</span>
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a href="${baseUrl}/rilogin?target=${baseUrl}/genes/${acc}"
                                   class="mt-1 btn btn-outline-dark"
                                   style="float: right"
                                   title="Log in to My genes">Log in to follow</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${cmsBaseUrl}/help/gene-page/" target="_blank">
                            <i class="fa-2x mt-2 fa fa-question-circle" style="float: right; color: #212529; padding-right: 10px;"></i></a>
                    </span>
                </div>
            </div>
        </div>

        <%-- GENE SUMMARY SECTION --%>
        <div class="container white-bg-small" id="gene-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container-fluid">

                                <c:if test="${ attemptRegistered && !phenotypeStarted }">
                                    <div class="alert alert-info mb-5">
                                        <h5>Registered for phenotyping at IMPC</h5>
                                        <p>Phenotyping is planned for a knockout strain of this gene but data is not currently available.</p>
                                    </div>
                                </c:if>

                                <c:if test="${!attemptRegistered and allMeasurementsNumber <= 0}">
                                    <div class="alert alert-info mb-5">
                                        <h5>Not currently registered for phenotyping at IMPC</h5>
                                        <p>Phenotyping is currently not planned for a knockout strain of this gene.</p>
                                    </div>
                                </c:if>

                                <!-- Gene summary section layout -->
                                <div class="row">

                                    <div class="col-lg-8">
                                        <div id="phenoSumSmallDiv">
                                            <h2>Gene Summary</h2>
                                            <div class="row no-gutters mb-2">
                                                <div class="col-2">
                                                    <div class="font-weight-bold">Name:</div>
                                                </div>
                                                <div class="col">
                                                    <span>${gene.markerName}</span>
                                                </div>
                                            </div>
                                            <div class="row no-gutters mb-4">
                                                <div class="col-2">
                                                    <div class="font-weight-bold">Synonyms:</div>
                                                </div>
                                                <div class="col">
                                                    <c:if test="${(not empty gene.markerSynonym)}">
                                                        <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                                                            <span><t:formatAllele>${synonym}</t:formatAllele><c:if test="${!loop.last}">,&nbsp;</c:if></span>
                                                        </c:forEach>
                                                    </c:if>
                                                    <c:if test="${(empty gene.markerSynonym)}">
                                                        N/A
                                                    </c:if>
                                                </div>
                                            </div>
                                            <div class="mb-4">
                                                <p><a href="#order" class="btn btn-primary btn-sm"><i class="far fa-shopping-cart fa-xs mr-2"></i>Order Alleles</a></p>
                                            </div>
                                            <div>
                                                <h4 class="pt-2">IMPC Data Collections</h4>
                                                <ul class="list-unstyled">
                                                    <li>
                                                        <c:if test="${bodyWeight}">
                                                            <a href="${baseUrl}/charts?accession=${acc}&parameter_stable_id=IMPC_BWT_008_001&procedure_stable_id=IMPC_BWT_001&chart_type=TIME_SERIES_LINE"
                                                               title="Body Weight Measurements">Body Weight Measurements <i class="far fa-chevron-right fa-xs ml-2"></i></a>
                                                        </c:if>
                                                        <c:if test="${not bodyWeight}">
                                                            No Body Weight Data
                                                        </c:if>
                                                    </li>
                                                    <li><c:if test="${gene.embryoDataAvailable}"><a id="embryoViewerBtn" href="${cmsBaseUrl}/embryoviewer/?mgi=${acc}">Embryo Imaging Data</a>
                                                    </c:if><c:if test="${not gene.embryoDataAvailable}">No Embryo Imaging Data</c:if>
                                                    </li>
                                                    <li><t:viabilityButton callList="${viabilityCalls}" geneAcc="${gene.mgiAccessionId}" /></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-lg-4">
                                        <h4 class="mt-2">IMPC Phenotype Summary</h4>
                                        <jsp:include page="phenotype_icons_frag.jsp"/>
                                        <div class="mt-4">
                                            <p><a href="#phenotypes-section">View all our phenotype data below <i class="far fa-chevron-down ml-2"></i></a></p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%-- PHENOTYPE SECTION --%>
        <a name="phenotypes-section"></a>
        <div class="container white-bg-small" id="phenotypes-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-5">
                                <h2>
                                    Phenotypes
                                    <a href="${cmsBaseUrl}/help/gene-page/phenotype/" title="Go to phenotype help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                                <p>The IMPC applies a panel of phenotyping screens to characterise single-gene knockout mice by comparison to wild types. Click on the different tabs to visualise significant phenotypes identified by the IMPC, as well as all data that was measured.</p>
                            </div>
                            <div>

                                <c:if test='${notsignificantTopLevelMpGroups.size() > 0 || rowsForPhenotypeTable.size() > 0 || allMeasurementsNumber > 0}'>


                                    <ul class="nav nav-tabs" id="phenotypesTab" role="tablist">
                                        <li class="nav-item">
                                            <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                                                <a class="nav-link active" id="significant-tab" data-toggle="tab" href="#significant"
                                                   role="tab" aria-controls="significant-tab" aria-selected="true"><i
                                                        class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes
                                                    (<span id="significantCount">${rowsForPhenotypeTable.size()}</span>/${rowsForPhenotypeTable.size()})</a>
                                            </c:if>
                                            <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                                                <a class="nav-link" id="significant-tab" data-toggle="tab" href="#significant"
                                                   role="tab" aria-controls="significant-tab" aria-selected="false"><i
                                                        class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes (0)</a>
                                            </c:if>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber > 0) ? ' active' : ''}"
                                               id="alldatachart-tab"
                                               data-toggle="tab" href="#alldatachart"
                                               role="tab" aria-controls="alldatachart-tab"
                                               aria-selected="${rowsForPhenotypeTable.size() <= 0 ? 'true' : 'false'}"><i
                                                    class="fal fa-chart-scatter"></i>&nbsp;
                                                Measurements chart (<span
                                                        id="allDataChartCount">${measurementsChartNumber}</span>/${measurementsChartNumber})</a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber <= 0)? ' active' : ''}"
                                               id="alldatatable-tab"
                                               data-toggle="tab" href="#alldatatable"
                                               role="tab" aria-controls="alldatatable-tab"
                                               aria-selected="${rowsForPhenotypeTable.size() <= 0 ? 'true' : 'false'}"><i
                                                    class="fal fa-ruler-combined"></i>&nbsp;
                                                All data table (<span
                                                        id="allDataTableCount">${allMeasurementsNumber}</span>/${allMeasurementsNumber})</a>
                                            <c:if test='${allMeasurementsNumber <= 0}'>
                                                <a class="nav-link active" id="alldatatable-tab" data-toggle="tab" href="#alldatatable"
                                                   role="tab" aria-controls="alldatatable-tab" aria-selected="false"><i
                                                        class="fal fa-ruler-combined"></i>&nbsp;
                                                    All data table (0)</a>
                                            </c:if>
                                        </li>
                                    </ul>

                                    <div class="tab-content" id="phenotypesTabContent" id="phenotypeAssociations">
                                        <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                                            <div class="tab-pane fade" id="significant" role="tabpanel" aria-labelledby="significant-tab">
                                                <c:if test="${ attemptRegistered && phenotypeStarted }">
                                                    <div class="alert alert-warning mt-3" role="alert">
                                                        No results meet the p-value threshold
                                                    </div>
                                                </c:if>
                                            </div>
                                        </c:if>
                                        <!-- Associations table -->
                                        <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                                            <div class="tab-pane fade show active" id="significant" role="tabpanel"
                                                 aria-labelledby="significant-tab">
                                                <div id="phenotypeTableDiv" class="inner-division">
                                                    <div class="row">
                                                        <div class="container p-0 p-md-2">
                                                            <div class="row" id="phenotypesDiv">
                                                                <div class="container p-0 p-md-2">
                                                                    <c:if test="${not empty rowsForPhenotypeTable}">
                                                                        <div class="row">
                                                                            <div class="col" id="target" action="destination.html">

                                                                                    <%--c:forEach
                                                                                            var="phenoFacet" items="${phenoFacets}"
                                                                                            varStatus="phenoFacetStatus">
                                                                                        <select id="top_level_mp_term_name" class="selectpicker"
                                                                                                multiple="multiple"
                                                                                                title="Filter on ${phenoFacet.key}">
                                                                                            <c:forEach
                                                                                                    var="facet" items="${phenoFacet.value}">
                                                                                                <option>${facet.key}</option>
                                                                                            </c:forEach>
                                                                                        </select>
                                                                                    </c:forEach--%>

                                                                            </div>
                                                                        </div>


                                                                        <c:set var="count" value="0" scope="page"/>

                                                                        <c:forEach var="phenotype" items="${rowsForPhenotypeTable}"
                                                                                   varStatus="status">
                                                                            <c:forEach var="sex" items="${phenotype.sexes}">
                                                                                <c:set var="count" value="${count + 1}" scope="page"/>
                                                                            </c:forEach>
                                                                        </c:forEach>

                                                                        <jsp:include page="PhenoFrag.jsp"></jsp:include>

                                                                        <div id="export">
                                                                            <p class="textright">
                                                                                Download data as:
                                                                                <a id="tsvDownload"
                                                                                   href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=tsv&fileName=${gene.markerSymbol}"
                                                                                   target="_blank" class="btn btn-outline-primary download-data"><i
                                                                                        class="fa fa-download"></i>&nbsp;TSV</a>
                                                                                <a id="xlsDownload"
                                                                                   href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=xls&fileName=${gene.markerSymbol}"
                                                                                   target="_blank" class="btn btn-outline-primary download-data"><i
                                                                                        class="fa fa-download"></i>&nbsp;XLS</a>
                                                                            </p>
                                                                        </div>
                                                                        <c:if test="${hasPWG}">
                                                                            <div class="text-right small w-100">
                                                                                * Significant with a threshold of 1x10<sup>-3</sup>, check the <a href="https://www.mousephenotype.org/publications/data-supporting-impc-papers/pain/">Pain Sensitivity</a> page for more information.
                                                                            </div>
                                                                        </c:if>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div><!-- end of div for mini section line -->
                                            </div>
                                        </c:if>

                                        <c:if test='${measurementsChartNumber <= 0}'>
                                            <div class="tab-pane fade" id="alldatachart" role="tabpanel" aria-labelledby="alldatachart-tab">
                                                <div class="alert alert-warning mt-3" role="alert">
                                                    No results to display
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:if test='${measurementsChartNumber > 0}'>
                                            <div class="tab-pane fade show ${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber > 0)? ' active' : ''}"
                                                 id="alldatachart" role="tabpanel" aria-labelledby="alldatachart-tab">
                                                <div class="mt-3 selector-container">
                                                    Select physiological systems to view:
                                                    <select class="selectpicker" multiple data-selected-text-format="count"
                                                            onchange="filterAllData('Chart')" id="systemSelectorChart">
                                                        <optgroup label="Significant">
                                                            <c:forEach var="i" begin="0" end="20">
                                                                <c:if test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                                                    <option title="1 item selected"
                                                                            value="${significantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|significant|${phenotypeGroupIcons[i]}"
                                                                            data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                                                </c:if>
                                                            </c:forEach>
                                                        </optgroup>
                                                        <optgroup label="Not significant">
                                                            <c:forEach var="i" begin="0" end="20">
                                                                <c:if test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                                                    <option title="1 item selected"
                                                                            value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|nonsignificant|${phenotypeGroupIcons[i]}"
                                                                            data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                                                </c:if>
                                                            </c:forEach>
                                                        </optgroup>
                                                    </select>
                                                </div>
                                                <div class="filters-container">
                                                    Viewing: <span id="phChartDataTitle"> all phenotypes</span>
                                                </div>
                                                <div id="all-chart">
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:if test='${allMeasurementsNumber <= 0}'>
                                            <div class="tab-pane fade" id="alldatatable" role="tabpanel" aria-labelledby="alldatatable-tab">
                                                <div class="alert alert-warning mt-3" role="alert">
                                                    No measurements to display
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:if test='${allMeasurementsNumber > 0}'>
                                            <div class="tab-pane fade show ${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber <= 0)? ' active' : ''}"
                                                 id="alldatatable" role="tabpanel" aria-labelledby="alldatatable-tab">
                                                <c:if test='${(significantTopLevelMpGroups.size() > 0 || notsignificantTopLevelMpGroups.size() > 0)}'>
                                                    <div class="mt-3 selector-container">
                                                        Select physiological systems to view:
                                                        <select class="selectpicker" multiple data-selected-text-format="count"
                                                                onchange="filterAllData('Table')" id="systemSelectorTable">
                                                            <optgroup label="Significant">
                                                                <c:forEach var="i" begin="0" end="20">
                                                                    <c:if test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                                                        <option title="1 item selected"
                                                                                value="${significantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|significant|${phenotypeGroupIcons[i]}"
                                                                                data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                                                    </c:if>
                                                                </c:forEach>
                                                            </optgroup>
                                                            <optgroup label="Not significant">
                                                                <c:forEach var="i" begin="0" end="20">
                                                                    <c:if test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                                                        <option title="1 item selected"
                                                                                value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|nonsignificant|${phenotypeGroupIcons[i]}"
                                                                                data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                                                    </c:if>
                                                                </c:forEach>
                                                            </optgroup>
                                                        </select>
                                                    </div>
                                                    <div class="filters-container">
                                                        Viewing: <span id="phTableDataTitle"> all phenotypes</span>
                                                    </div>
                                                </c:if>
                                                <div id="all-table">
                                                </div>
                                                <c:if test="${hasPWG}">
                                                    <div class="text-right small w-100">
                                                        * Significant with a threshold of 1x10<sup>-3</sup>, check the <a href="https://www.mousephenotype.org/publications/data-supporting-impc-papers/pain/">Pain Sensitivity</a> page for more information.
                                                    </div>
                                                </c:if>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- EXPRESSION SECTION--%>
        <div class="container white-bg-small" id="expression-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-5">
                                <h2>
                                    lacZ Expression
                                    <a href="${cmsBaseUrl}/help/gene-page/lacz-expression/" title="Go to expression help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                            </div>
                            <div>
                                <c:if test="${empty impcAdultExpressionImageFacetsWholemount
                                                  and empty impcAdultExpressionImageFacetsSection
                                                  and empty expressionAnatomyToRow
                                                  and empty impcEmbryoExpressionImageFacets
                                                  and empty embryoExpressionAnatomyToRow}">
                                    <div class="row">
                                        <div class="col-12">
                                            <div class="alert alert-warning mt-3">Expression data not available</div>
                                        </div>
                                    </div>
                                </c:if>

                                <c:if test="${not empty impcAdultExpressionImageFacetsWholemount
                                                  or not empty impcAdultExpressionImageFacetsSection
                                                  or not empty expressionAnatomyToRow
                                                  or not empty impcEmbryoExpressionImageFacets
                                                  or not empty embryoExpressionAnatomyToRow}">
                                    <div class="row">
                                        <div class="col-12">
                                            <!-- section for expression data here -->
                                            <ul class="nav nav-tabs" id="expressionTab" role="tablist">
                                                <li class="nav-item">
                                                    <a class="nav-link active"
                                                       id="adult-tab"
                                                       data-toggle="tab"
                                                       href="#adult"
                                                       role="tab"
                                                       aria-controls="adult-tab"
                                                       aria-selected="false">Adult
                                                        Expression
                                                        (${expressionAnatomyToRow.size()} tissues)</a>
                                                </li>
                                                <li class="nav-item">
                                                    <a class="nav-link"
                                                       id="embryo-tab"
                                                       data-toggle="tab"
                                                       href="#embryo"
                                                       role="tab"
                                                       aria-controls="embryo-tab"
                                                       aria-selected="true">Embryo
                                                        Expression (${embryoExpressionAnatomyToRow.size()} tissues)</a>
                                                </li>
                                                <li class="nav-item">
                                                    <a class="nav-link"
                                                       id="adult-wt-expression-tab"
                                                       data-toggle="tab"
                                                       href="#adult-wt-expression"
                                                       role="tab"
                                                       aria-controls="adult-wt-expression-tab"
                                                       aria-selected="true">Background staining WT adult</a>
                                                </li>
                                                <li class="nav-item">
                                                    <a class="nav-link"
                                                       id="embryo-wt-expression-tab"
                                                       data-toggle="tab"
                                                       href="#embryo-wt-expression"
                                                       role="tab"
                                                       aria-controls="adult-wt-expression-tab"
                                                       aria-selected="true">Background staining WT embryo</a>
                                                </li>
                                            </ul>
                                            <div class="tab-content" id="expressionTabContent">
                                                <div class="tab-pane fade show active" id="adult" role="tabpanel" aria-labelledby="adult-tab">
                                                    <c:choose>
                                                        <c:when test="${not empty expressionAnatomyToRow }">
                                                            <p class="alert alert-info">An assay measuring the expression of lacZ shows the tissue where the gene is expressed.</p>
                                                            <div>
                                                                <jsp:include page="genesExpressionAdult_frag.jsp"></jsp:include>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="alert alert-warning mt-3">
                                                                No Adult expression data was found for this gene.
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="tab-pane fade" id="embryo" role="tabpanel" aria-labelledby="embryo-tab">
                                                    <c:choose>
                                                        <c:when test="${not empty embryoExpressionAnatomyToRow}">
                                                            <p class="alert alert-info">An assay measuring the expression of lacZ shows the tissue where the gene is expressed.</p>
                                                            <jsp:include page="genesExpressionEmbryo_frag.jsp"></jsp:include>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="alert alert-warning mt-3">
                                                                No Embryo expression data was found for this gene.
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>

                                                <!-- Adult WT expression -->
                                                <div class="tab-pane fade" id="adult-wt-expression" role="tabpanel" aria-labelledby="adult-wt-expression-tab">
                                                    <p class="alert alert-info">Background staining occurs in wild type mice and embryos at an incidental rate.</p>
                                                    <jsp:include page="genesExpressionWTAdult_frag.jsp"></jsp:include>
                                                </div>

                                                <!-- Embryo WT expression -->
                                                <div class="tab-pane fade" id="embryo-wt-expression" role="tabpanel" aria-labelledby="embryo-wt-expression-tab">
                                                    <c:choose>
                                                        <c:when test="${not empty embryoExpressionAnatomyToRow}">
                                                            <p class="alert alert-info">Background staining occurs in wild type mice and embryos at an incidental rate.</p>
                                                            <jsp:include page="genesExpressionWTEmbryo_frag.jsp"></jsp:include>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="alert alert-warning mt-3">
                                                                No Embryo expression data was found for this gene.
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- ASSOCIATED IMAGES --%>
        <div class="container white-bg-small" id="images-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-5">
                                <h2>
                                    Associated Images
                                    <a href="${cmsBaseUrl}/help/gene-page/images/" title="Go to phenotype help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                                <p>
                                    Images submitted by IMPC centres for a selection of procedures. Each set of images is available to view in our image comparator.
                                </p>
                            </div>

                            <div class="row">
                                <div class="col-12">
                                    <c:if test="${empty impcImageGroups and empty solrFacets}">
                                        <div class="alert alert-warning mt-3">Phenotype associated images not available</div>
                                    </c:if>

                                    <c:if test="${not empty impcImageGroups or not empty solrFacets}">
                                        <c:if test="${not empty impcImageGroups}">
                                            <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                        </c:if>
                                    </c:if>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <c:if test="${not empty impcImageFacets and not empty solrFacets}">
                                        <hr>
                                    </c:if>
                                    <c:if test="${not empty solrFacets}">
                                        <h5 class="mt-5">Legacy Phenotype Associated Images</h5>
                                        <jsp:include page="genesLegacyPhenoAssocImg_frag.jsp"></jsp:include>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- DISEASE SECTION --%>
        <div class="container white-bg-small" id="disease-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-4">
                                <h2>
                                    Human diseases caused by ${gene.markerSymbol} mutations
                                    <a href="${cmsBaseUrl}/help/gene-page/disease-models/" title="Go to phenotype help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
<%--                                IMAGE PLACEHOLDER FOR THE DISEASE HEADER GRAPHIC--%>
<%--                                <img src="https://picsum.photos/id/10/800/240?blur=10" class="mb-3" />--%>
                                <div class="alert alert-info pb-0">
                                    <p>
                                        The analysis uses data from IMPC, along with published data on other mouse mutants, in comparison to human disease reports in OMIM, Orphanet, and DECIPHER.
                                    </p>
                                    <p>
                                        Phenotype comparisons summarize the similarity of mouse phenotypes with human disease phenotypes.
                                    </p>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-12">
                                    <!-- section for diseases directly annotated to this gene here -->
                                    <ul class="nav nav-tabs" id="diseaseByAnnotationTab" role="tablist">
                                        <li class="nav-item">
                                            <a class="nav-link active"
                                               id="disease-annotation-tab"
                                               data-toggle="tab"
                                               href="#disease-annotation"
                                               role="tab"
                                               aria-controls="disease-annotation-tab"
                                               aria-selected="false">
                                                Human diseases associated with ${gene.markerSymbol}
                                                (${diseasesByAnnotation.size()} diseases)</a>
                                        </li>
                                        <!-- section for diseases associated by phenotypic similarity to this gene here -->
                                        <li class="nav-item">
                                            <a class="nav-link"
                                               id="disease-prediction-tab"
                                               data-toggle="tab"
                                               href="#disease-prediction"
                                               role="tab"
                                               aria-controls="disease-prediction-tab"
                                               aria-selected="true">
                                                Human diseases predicted to be associated with ${gene.markerSymbol} (${modelAssociations.size()} diseases)</a>
                                        </li>
                                    </ul>
                                    <div class="tab-content" id="diseaseTabContent">
                                        <div class="tab-pane fade show active" id="disease-annotation" role="tabpanel" aria-labelledby="disease-annotation-tab">
                                            <c:choose>
                                                <c:when test="${not empty diseasesByAnnotation}">
                                                    <p>The table below shows human diseases associated to ${gene.markerSymbol} by <b>orthology or direct annotation</b>.</p>

                                                    <div class="row">
                                                        <div class="col-12">
                                                            <table id="diseases_by_annotation"
                                                                   data-pagination="true"
                                                                   data-mobile-responsive="true"
                                                                   data-sortable="true"
                                                                   data-detail-view="true"
                                                                   data-detail-view-align="right"
                                                                   data-detail-formatter="diseaseDetailFormatter"
                                                                   data-detail-filter="diseaseDetailFilter">
                                                                <thead>
                                                                <tr>
                                                                    <th data-width="350">Disease</th>
                                                                    <th data-halign="center" data-align="center">Similarity of<br />phenotypes</th>
                                                                    <th>Matching phenotypes</th>
                                                                    <th data-width="140" data-halign="center" data-align="center">Source</th>
                                                                </tr>
                                                                </thead>
                                                                <tbody>
                                                                <c:forEach items="${diseasesByAnnotation}" var="disease" varStatus="loop">
                                                                    <tr id="diseaseRow${loop.index}" data-link="${baseUrl}/phenodigm2/phenogrid?geneId=${gene.mgiAccessionId}&diseaseId=${disease.diseaseId}&pageType=gene" data-shown="false">
                                                                        <td>${disease.diseaseTerm}</td>
                                                                        <td>
                                                                            <div class="signal-bars mt1 sizing-box good ${disease.scoreIcon}" data-toggle="tooltip" data-placement="top" title="Phenodigm score: <fmt:formatNumber maxFractionDigits="2" value="${disease.phenodigmScore}" />">
                                                                                <div class="first-bar bar"></div>
                                                                                <div class="second-bar bar"></div>
                                                                                <div class="third-bar bar"></div>
                                                                                <div class="fourth-bar bar"></div>
                                                                                <div class="fifth-bar bar"></div>
                                                                            </div>
                                                                        </td>
                                                                        <td><a href="javascript:toggleDetail(${loop.index});">${disease.formattedMatchingPhenotypes}</a></td>
                                                                        <td><a href="${disease.externalUrl}" onclick="even.stopPropagation();">${disease.diseaseId}</a></td>
                                                                    </tr>
                                                                </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="alert alert-warning mt-3">
                                                        No human diseases associated to this gene by <b>orthology or annotation</b>.
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="tab-pane fade show" id="disease-prediction" role="tabpanel" aria-labelledby="disease-prediction-tab">
                                            <c:choose>
                                                <c:when test="${not empty modelAssociations}">
                                                    <p>The table below shows human diseases predicted to be associated to ${gene.markerSymbol} by <b>phenotypic similarity</b>.</p>

                                                    <div class="row">
                                                        <div class="col-12">
                                                            <table id="diseases_by_prediction"
                                                                   data-pagination="true"
                                                                   data-mobile-responsive="true"
                                                                   data-sortable="true"
                                                                   data-detail-view="true"
                                                                   data-detail-view-align="right"
                                                                   data-detail-formatter="diseaseDetailFormatter"
                                                                   data-detail-filter="diseaseDetailFilter">
                                                                <thead>
                                                                <tr>
                                                                    <th data-width="350">Disease</th>
                                                                    <th data-halign="center" data-align="center">Similarity of<br />phenotypes</th>
                                                                    <th>Matching phenotypes</th>
                                                                    <th data-width="140" data-halign="center" data-align="center">Source</th>
                                                                </tr>
                                                                </thead>
                                                                <tbody>
                                                                <c:forEach items="${modelAssociations}" var="disease" varStatus="loop">
                                                                    <tr id="diseaseRow${loop.index}" data-link="${baseUrl}/phenodigm2/phenogrid?geneId=${gene.mgiAccessionId}&diseaseId=${disease.diseaseId}&pageType=gene" data-shown="false">
                                                                        <td>${disease.diseaseTerm}</td>
                                                                        <td>
                                                                            <div class="signal-bars mt1 sizing-box good ${disease.scoreIcon}" data-toggle="tooltip" data-placement="top" title="Phenodigm score: <fmt:formatNumber maxFractionDigits="2" value="${disease.phenodigmScore}" />">
                                                                                <div class="first-bar bar"></div>
                                                                                <div class="second-bar bar"></div>
                                                                                <div class="third-bar bar"></div>
                                                                                <div class="fourth-bar bar"></div>
                                                                                <div class="fifth-bar bar"></div>
                                                                            </div>
                                                                        </td>
                                                                        <td><a href="javascript:toggleDetail(${loop.index});">${disease.formattedMatchingPhenotypes}</a></td>
                                                                        <td><a href="${disease.externalUrl}" onclick="even.stopPropagation();">${disease.diseaseId}</a></td>
                                                                    </tr>
                                                                </c:forEach>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="alert alert-warning mt-3">
                                                        No human diseases associated to this gene by phenotypic similarity.
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- HISTOPATHOLOGY SECTION --%>
        <div class="container white-bg-small" id="histopathology-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-5">
                                <h2>
                                    Histopathology
                                    <a href="${cmsBaseUrl}/help/gene-page/" title="Go to phenotype help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                                <p>
                                    Summary table of phenotypes displayed during the Histopathology procedure which are considered significant. Full histopathology data table, including submitted images, can be accessed by clicking any row in this table.
                                </p>
                            </div>
                            <div>
                                <c:if test="${rowsForHistopathTable.size() == 0}">
                                    <div class="alert alert-warning">
                                        <p>There is no histopathology data for ${gene.markerSymbol}</p>
                                    </div>
                                </c:if>
                                <c:if test="${rowsForHistopathTable.size() > 0}">
                                    <div class="row">
                                        <div class="col-12">
                                            <table id="histopathPhenotypesTable" data-toggle="table" data-pagination="true" data-mobile-responsive="true" data-sortable="true">
                                                <thead>
                                                <tr>
                                                    <th data-sortable="true">Phenotype</th>
                                                    <th data-sortable="true">Allele</th>
                                                    <th title="Zygosity" data-sortable="true">Zygosity</th>
                                                    <th data-sortable="true">Sex</th>
                                                    <th data-sortable="true">Life Stage</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <c:forEach var="phenotype" items="${rowsForHistopathTable}" varStatus="status">
                                                    <c:set var="europhenome_gender" value="Both-Split"/>
                                                    <tr title="${!phenotype.getEvidenceLink().getDisplay() ? 'No supporting data supplied.' : ''}" data-toggle="tooltip" data-link="${fn:escapeXml(phenotype.getEvidenceLink().url)}" class="clickableRow">

                                                        <td>
                                                                ${phenotype.phenotypeTerm.name}
                                                        </td>

                                                        <td>
                                                            <span><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></span>
                                                        </td>

                                                        <td title="${phenotype.zygosity}">${phenotype.zygosity.getShortName()}</td>

                                                        <td>
                                                            <c:set var="count" value="0" scope="page"/>
                                                            <t:displaySexes sexes="${phenotype.sexes}"></t:displaySexes>
                                                        </td>

                                                        <td>${phenotype.lifeStageName}</td>

                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${rowsForHistopathTable.size() == 0 and hasHistopath}">
                                    <div class="row">
                                        <div class="col-12">
                                            <div class="alert alert-warning" role="alert">
                                                This gene doesn't have any significant Histopathology hits. <a href="${baseUrl}/histopath/${gene.markerSymbol}">Please click here to see the raw data</a>.
                                            </div>
                                        </div>

                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- PUBLICATIONS SECTION --%>
        <div class="container white-bg-small" id="publications-section">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-5">
                                <h2>
                                    IMPC related publications
                                    <a href="${cmsBaseUrl}/help/data-visualization/gene-pages/publications/" title="Go to publication help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                                <p>
                                    The table below lists publications which used either products generated by the IMPC or data produced by the phenotyping efforts of the IMPC. These publications have also been associated to ${gene.markerSymbol}.
                                </p>
                            </div>
                            <div>
                                <div class="row">
                                    <div class="col-12">
                                        <c:choose>
                                            <c:when test="${fn:length(publications) != 0}">
                                                <p class="alert alert-info">There are <b>${fn:length(publications)} publication<c:if test="${fn:length(publications)!=1}">s</c:if> which use IMPC produced mice or data.</b></p>
                                                <table id="publications_table"
                                                       data-pagination="true"
                                                       data-mobile-responsive="true"
                                                       data-sortable="true">
                                                    <thead>
                                                    <tr>
                                                        <th>Title</th>
                                                        <th>Journal</th>
                                                        <th>IMPC Allele</th>
                                                        <th data-width="160" data-halign="center" data-align="center">PubMed&nbsp;ID</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <c:forEach items="${publications}" var="publication" varStatus="loop">
                                                        <tr id="publicationRow${loop.index}" data-link="publication" data-shown="false">
                                                            <td><a href="https://www.doi.org/${publication.doi}">${publication.title}</a></td>
                                                            <td>${publication.journalInfo.journal.title} (<fmt:formatDate value="${publication.firstPublicationDate}" pattern="MMMM yyyy" />)</td>
                                                            <td><c:forEach items="${publication.alleles}" var="allele" varStatus="allele_loop">
                                                                <c:if test="${fn:contains(allele.alleleSymbol, gene.markerSymbol)}"><t:formatAllele>${allele.alleleSymbol}</t:formatAllele></c:if>
                                                            </c:forEach>
                                                            </td>
                                                            <td><c:choose>
                                                                <c:when test="${fn:length(publication.pmcid) > 0}"><a href="https://www.ncbi.nlm.nih.gov/pmc/articles/${publication.pmcid}">${publication.pmcid}</a></c:when>
                                                                <c:when test="${fn:length(publication.pmid) > 0}"><a href="https://pubmed.ncbi.nlm.nih.gov/${publication.pmid}/">${publication.pmid}</a></c:when>
                                                            </c:choose>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:when>
                                            <c:when test="${fn:length(publications) == 0}">No publications found that use IMPC mice or data for ${gene.markerSymbol}.</c:when>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- ORDER SECTION --%>
        <div class="container white-bg-small" id="order">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content p-5">
                            <div class="mb-2">
                                <h2>
                                    Order Mouse and ES Cells
                                    <a href="${cmsBaseUrl}/help/gene-page/ordering-mice-or-mouse-products/" title="Go to phenotype help">
                                        <i class="fal fa-question-circle fa-xs text-muted align-middle" style="font-size: 20px;"></i>
                                    </a>
                                </h2>
                                <p>
                                    All available products are supplied via our member's centres or partnerships. When ordering a product from the IMPC you will be redirected to one of their websites or prompted to start an email.
                                </p>
                            </div>
                            <div>
                                <jsp:include page="orderSectionFrag.jsp"></jsp:include>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="modal fade" id="crisprDataModal" tabindex="-1" aria-labelledby="crisprDataModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="crisprDataModalLabel">CRISPR Sequencing Data</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <h2 class="mt-2 fasta-sequence-title">Sequence</h2>
                        <div class="row">
                            <div class="col fasta-sequences">

                            </div>
                        </div>
                        <h2>Guides</h2>
                        <table class="crispr-modal-guides-table"
                               data-toggle="table"
                        >
                            <thead>
                            <tr>
                                <th data-field="sequence">Sequence</th>
                                <th data-field="guideSequence">Guide sequence</th>
                                <th data-field="pam">pam</th>
                                <th data-field="chr">chr</th>
                                <th data-field="start">start</th>
                                <th data-field="stop">stop</th>
                                <th data-field="strand">strand</th>
                                <th data-field="genomeBuild">Genome build</th>
                                <th data-field="grnaConcentration">gRNA Concentration</th>
                                <th data-field="truncatedGuide">Truncated Guide</th>
                                <th data-field="reversed">Reversed</th>
                                <th data-field="sangerService">Sanger service</th>
                                <th data-field="guideFormat">Guide format</th>
                                <th data-field="guideSource">Guide source</th>
                            </tr>
                            </thead>
                        </table>
                        <h2 class="mt-2">Nucleases</h2>
                        <table class="crispr-modal-nucleases-table"
                               data-toggle="table"
                        >
                            <thead>
                            <tr>
                                <th data-field="nucleaseType">Nuclease type</th>
                                <th data-field="nucleaseClass">Nuclease class</th>
                            </tr>
                            </thead>
                        </table>
                        <h2 class="mt-2">Genotype primers</h2>
                        <table class="crispr-modal-genotype-primers"
                               data-toggle="table"
                        >
                            <thead>
                            <tr>
                                <th data-field="sequence">Sequence</th>
                                <th data-field="name">Name</th>
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
    </jsp:body>

</t:genericpage>
