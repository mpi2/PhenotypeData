<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>

    <jsp:attribute name="title">${gene.markerSymbol} - ${gene.markerName}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body class="page-template page-template-no-sidebar--large page-template-no-sidebar--large-php page page-id-3162 page-child parent-pageid-42">
    </jsp:attribute>

    <jsp:attribute name="header">
        		<script type="text/javascript">
                    var base_url = '${baseUrl}';
                    var geneId = '${gene.mgiAccessionId}';
                </script>
        <script type='text/javascript' src="${baseUrl}/js/general/dropDownExperimentPage.js?v=${version}" async></script>

        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>

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
            <script type="text/javascript" src="${baseUrl}/js/phenodigm2/phenodigm2.js?v=${version}"></script>
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/phenodigm2.css" async>
        <%-- End of phenodigm2 requirements --%>

        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <script type="text/javascript">
            var gene_id = '${acc}';
            var monarchUrl = '${monarchUrl}';

            $(document).ready(function () {
            }


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

    </jsp:attribute>

    <jsp:body>

        <c:if test="${liveSite || param.checklive != null}">
        <!-- Google Tag Manager (noscript) -->
            <noscript>
                <iframe src="https://www.googletagmanager.com/ns.html?id=GTM-NZPSPWR" height="0" width="0" style="display:none;visibility:hidden"></iframe>
            </noscript>
            <!-- End Google Tag Manager (noscript) -->
        </c:if>


        <div class="container data-heading">
            <div class="row">

                <noscript>
                    <div class="col-12 no-gutters">
                        <h5 style="float: left">Please enable javascript if you want to log in to follow or stop
                            following this gene.</h5>
                    </div>
                </noscript>

                <div class="col-12 no-gutters">
                    <h2 style="float: left" class="mb-0">Gene: ${gene.markerSymbol}</h2>
                    <h2>
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
                                        <i class="fas ${activeIconClass}"></i>
                                        <span>${followText}</span>
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a href="${baseUrl}/rilogin?target=${baseUrl}/genes/${acc}"
                                   class="btn btn-primary"
                                   style="float: right"
                                   title="Log in to My genes">Log in to follow</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${cmsBaseUrl}/help/gene-page/" target="_blank">
                            <i class="fa fa-question-circle" style="float: right; color: #212529; padding-right: 10px;"></i></a>
                    </h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <jsp:include page="genesPhenotypeAssociation_frag.jsp"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" id="expression">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h3><i class="icon icon-conceptual icon-expression"></i>&nbsp;Expression</h3>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">

            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <div class="row justify-content-end pb-3">
                                    <div class="float-right"><a href="${cmsBaseUrl}/help/gene-page/lacz-expression/"
                                                                target="_blank"><i class="fa fa-question-circle"
                                                                                   style="font-size: xx-large"></i></a>
                                    </div>
                                </div>


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
                                            <h4>IMPC lacZ Expression Data</h4>
                                            <!-- section for expression data here -->
                                            <ul class="nav nav-tabs" id="expressionTab" role="tablist">
                                                <li class="nav-item">
                                                    <a class="nav-link active" id="adult-tab" data-toggle="tab"
                                                       href="#adult"
                                                       role="tab" aria-controls="adult-tab" aria-selected="false">Adult
                                                        Expression
                                                        (${expressionAnatomyToRow.size()})</a>
                                                </li>
                                                <!--li class="nav-item">
                                                    <a class="nav-link" id="adult-image-tab" data-toggle="tab" href="#adult-image"
                                                       role="tab" aria-controls="adult-image-tab" aria-selected="false">Adult Expression
                                                        Image</a>
                                                </li-->
                                                <li class="nav-item">
                                                    <a class="nav-link" id="_embryo-tab" data-toggle="tab" href="#_embryo"
                                                       role="tab" aria-controls="_embryo-tab" aria-selected="true">Embryo
                                                        Expression (${embryoExpressionAnatomyToRow.size()})</a>
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
                                                                No Adult expression data was found for this gene.
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
                                                <div class="tab-pane fade" id="_embryo" role="tabpanel"
                                                     aria-labelledby="_embryo-tab">
                                                    <c:choose>
                                                        <c:when test="${not empty embryoExpressionAnatomyToRow}">
                                                            <jsp:include page="genesEmbExpData_frag.jsp"></jsp:include>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="alert alert-warning mt-3">
                                                                No Embryo expression data was found for this gene.
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
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" id="images">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h3><i class="fal fa-images"></i>&nbsp;Associated Images</h3>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <div class="row justify-content-end pb-1">
                                    <div class="float-right"><a href="${cmsBaseUrl}/help/gene-page/images/" target="_blank"><i
                                            class="fa fa-question-circle" style="font-size: xx-large"></i></a></div>
                                </div>
                                <div class="row">
                                    <div class="col-12">
                                        <c:if test="${empty impcImageGroups and empty solrFacets}">
                                            <div class="alert alert-warning mt-3">Phenotype associated images not
                                                available
                                            </div>
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
                                            <h5>Legacy Phenotype Associated Images</h5>
                                            <jsp:include page="genesLegacyPhenoAssocImg_frag.jsp"></jsp:include>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="container" id="diseases">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h3><i class="fal fa-procedures"></i>&nbsp;Disease Models</h3>
                </div>
            </div>
        </div>


        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <div class="row justify-content-end">
                                    <div class="float-right"><a href="${cmsBaseUrl}/help/gene-page/disease-models/"
                                                                target="_blank"><i class="fa fa-question-circle"
                                                                                   style="font-size: xx-large"></i></a>
                                    </div>
                                </div>
                                <div class="row d-lg-flex d-none">
                                    <div class="col-12">
                                        <ul class="nav nav-tabs" id="diseasesTab" role="tablist">
                                            <li class="nav-item">
                                                <a class="nav-link active" id="byAnnotation-tab" data-toggle="tab"
                                                   href="#byAnnotation"
                                                   role="tab" aria-controls="byAnnotation-tab" aria-selected="false">By
                                                    Annotation and
                                                    Orthology (<span id="diseases_by_annotation_count">0</span>)</a>
                                            </li>
                                            <li class="nav-item">
                                                <a class="nav-link" id="byPhenotype-tab" data-toggle="tab"
                                                   href="#byPhenotype"
                                                   role="tab" aria-controls="byPhenotype-tab" aria-selected="false">By
                                                    phenotypic
                                                    Similarity (<span id="diseases_by_phenotype_count">0</span>)</a>
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
                                                        <table id="diseases_by_phenotype"
                                                               class="table tablesorter disease hidden-xs"
                                                               style="width:100%"></table>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row d-lg-none d-flex">
                                    <span class="alert alert-secondary">Please visit the desktop version to see this section</span>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>


        <c:if test="${rowsForHistopathTable.size() > 0}">
            <div class="container" id="histopath">
                <div class="row pb-2">
                    <div class="col-12 col-md-12">
                        <h3><i class="fal fa-microscope"></i>&nbsp;Histopathology</h3>
                    </div>
                </div>
            </div>
            <div class="container white-bg-small">
                <div class="row pb-5">
                    <div class="col-12 col-md-12">
                        <div class="pre-content clear-bg">
                            <div class="page-content pt-3 pb-5">
                                <div class="container p-0 p-md-2">
                                    <div class="row justify-content-end">
                                        <div><a href="${cmsBaseUrl}/help/gene-page/"><i class="fa fa-question-circle"
                                                                                        style="font-size: xx-large"></i></a>
                                        </div>
                                    </div>
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
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="container" id="order">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h3><i class="fal fa-shopping-cart"></i>&nbsp;Order Mouse and ES Cells</h3>

                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <div class="row justify-content-end">
                                    <div><a href="${cmsBaseUrl}/help/gene-page/ordering-mice-or-mouse-prodcuts/"><i
                                            class="fa fa-question-circle" style="font-size: xx-large"></i></a></div>
                                </div>
                                <div class="row">
                                    <div class="col-12">
                                        <jsp:include page="orderSectionFrag.jsp"></jsp:include>
                                    </div>
                                </div>
                            </div>
                        </div>
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

            var diseaseModelTotal = 0;

            var diseaseTableConfs = [
                {
                    id: '#diseases_by_annotation',
                    tableConf: {
                        paging: false,
                        info: false,
                        searching: false,
                        order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                        pagingType: "full_numbers",
                        initComplete: function (settings, json) {
                            $('#diseases_by_annotation_count').text(settings.aoData.length);
                            diseaseModelTotal += settings.aoData.length;
                            $("#diseaseModelTotal").text(diseaseModelTotal);
                        }
                    },
                    phenodigm2Conf: {
                        pageType: "genes",
                        gene: "${gene.mgiAccessionId}",
                        groupBy: "diseaseId",
                        filterKey: "diseaseId",
                        filter: curatedDiseases,
                        minScore: 0,
                        innerTables: true,
                    }
                },
                {
                    id: '#diseases_by_phenotype',
                    tableConf: {
                        order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                        pageLength: 20,
                        lengthMenu: [20, 50, 100],
                        pagingType: "full_numbers",
                        responsive: true,
                        initComplete: function (settings, json) {
                            $('#diseases_by_phenotype_count').text(settings.aoData.length);
                            diseaseModelTotal += settings.aoData.length;
                            $("#diseaseModelTotal").text(diseaseModelTotal);
                        }
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

            function renderAllele(acc, alleleName) {
                return '<div id="' + alleleName + '"></div>'
            };

            $(document).ready(function () {
                // create phenodigm tables
                for (var i = 0; i < diseaseTableConfs.length; i++) {
                    var dTable = diseaseTableConfs[i];
                    impc.phenodigm2.makeTable(modelAssociations, dTable.id, dTable.phenodigm2Conf);
                    if (dTable.phenodigm2Conf.count && dTable.phenodigm2Conf.count > 0) {
                        var dataTable = $(dTable.id).DataTable(dTable.tableConf);
                        $.fn.addTableClickPhenogridHandler(dTable.id, dataTable);
                    } else {
                        $(dTable.id).parent().html('<div class="alert alert-warning mt-3">\n' +
                            '                                            No associations found.\n' +
                            '                                        </div>');
                    }
                }
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
                /*                var orderTable = $("#creLineTable").DataTable({
                                    "bFilter": false,
                                    "order": [],
                                    "bLengthChange": false,
                                    "columnDefs": [
                                        {
                                            "orderable": false,
                                            "targets": 'no-sort',
                                            "className": 'ordering-info'
                                        },
                                        {
                                            "orderable": false,
                                            "targets": 'hidden',
                                            "visible": false
                                        }
                                    ]
                                });

                                orderTable.rows().every( function (idx) {
                                    this
                                        .child(
                                            $(
                                                "<div class='container'>" +
                                                '<div id="orderAllele' + idx + '" class="col-12">' +
                                                "     <div class=\"pre-content\">\n" +
                                                "                        <div class=\"row no-gutters\">\n" +
                                                "                            <div class=\"col-12 my-5\">\n" +
                                                "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
                                                "                            </div>\n" +
                                                "                        </div>\n" +
                                                "                    </div>" +
                                                '</div>' +
                                                '</div>'
                                            )
                                        );
                                } );

                                $('#creLineTable tbody').on('click', 'td.ordering-info', function () {
                                    var tr  = $(this).closest('tr'),
                                        row = orderTable.row(tr);

                                    if (row.child.isShown()) {
                                        tr.find('a').html('Show ordering information&nbsp;<i class="fa fa-caret-down"></i>');
                                        tr.next('tr').removeClass('ordering-info');
                                        row.child.hide();
                                        tr.removeClass('shown');
                                    }
                                    else {
                                        tr.find('a').html('Hide ordering information&nbsp;<i class="fa fa-caret-up"></i>');
                                        if(row.data()[1] === 'false') {
                                            $.ajax({
                                                url: row.data()[0],
                                                type: 'GET',
                                                success: function (data) {
                                                    $('#orderAllele' + row.index()).html(data);
                                                    row.data()[1] = 'true';
                                                }
                                            });
                                        }
                                        row.child.show();
                                        tr.next('tr').addClass('ordering-info');
                                        tr.addClass('shown');
                                    }
                                });*/
            });
        </script>

    </jsp:body>

</t:genericpage>
