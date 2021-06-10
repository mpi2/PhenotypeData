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

        <script defer type='text/javascript' src="https://code.highcharts.com/highcharts.js"></script>
        <script defer type='text/javascript' src="https://code.highcharts.com/modules/exporting.js"></script>

        <script defer type='text/javascript' src="${baseUrl}/js/general/enu.js"></script>
        <script defer type='text/javascript' src="${baseUrl}/js/general/dropdownfilters.js" ></script>
        <script defer type="text/javascript" src="${baseUrl}/js/general/allele.js" ></script>

        <%-- Phenogrid requirements --%>
        <script defer type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.js?v=${version}" ></script>

        <%-- Load async CSS stylesheet, see https://www.filamentgroup.com/lab/load-css-simpler/ --%>
        <link rel="preload" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" as="style" />
        <link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" media="print" onload="this.media='all'" />

        <script type="text/javascript">

            document.addEventListener("DOMContentLoaded", function () {
                var heatmap_generated = 0;
                var expressionTab = 0;
                var hash = location.hash;
                if (hash.indexOf("tabs-") > -1) {
                    expressionTab = $('a[href="' + hash + '"]').parent().index();
                    $("#section-expression").focus();
                }

                $("#exptabs").tabs({active: expressionTab});
                $("#tabs").tabs();

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

                $('input[name=optionsEmbryo]').change(function () {
                    var value = $('input[name=optionsEmbryo]:checked').val();
                    console.log('hi');
                    if (value === 'table') {
                        $('#embryo1').show();
                        $('#embryo2').hide();
                    } else {
                        $('#embryo1').hide();
                        $('#embryo2').show();
                    }
                });

                // Enable CSRF processing for forms on this page
                function loadCsRf() {
                    var token = $("meta[name='_csrf']").attr("content");
                    var header = $("meta[name='_csrf_header']").attr("content");
                    console.log('_csrf:_csrf_header' + token + ':' + header);
                    $(document).ajaxSend(function(e, xhr, options) {
                        xhr.setRequestHeader(header, token);
                    });
                }
                loadCsRf();

                // Wire up the AJAX callbacks to the approprate forms
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
         </style>

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
                    <h1 style="float: left" class="h1 m-0"><b>Gene: ${gene.markerSymbol}</b></h1>
                    <span class="fa-2x">
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
                                   class="mt-1 btn btn-primary"
                                   style="float: right"
                                   title="Log in to My genes">Log in to follow</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${cmsBaseUrl}/help/gene-page/" target="_blank">
                            <i class="mt-2 fa fa-question-circle" style="float: right; color: #212529; padding-right: 10px;"></i></a>
                    </span>
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
                    <h2 class="h2"><b><i class="icon icon-conceptual icon-expression"></i>&nbsp;Expression</b></h2>
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
                    <h2 class="h2"><b><i class="fal fa-images"></i>&nbsp;Associated Images</b></h2>
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
        </div>


        <div class="container" id="diseases">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h2 class="h2"><b><i class="fal fa-procedures"></i>&nbsp;Human diseases caused by ${gene.markerSymbol} mutations</b></h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <div class="row">
                                    <div class="mb-3 col-11" style="height:240px; background-repeat: no-repeat; background-position-x: 1.4rem; background-image: linear-gradient(to bottom, rgba(255,255,255,0.6) 0%,rgba(255,255,255,0.9) 100%), url(https://picsum.photos/800/240?blur=3)">
<%--                                        <img class="img-fluid" src="https://picsum.photos/800/240?blur=3" alt="IMPC / Human disease Infographic" />--%>
                                        <h2>Monarch supplied infographic (800x240)</h2>
                                    </div>
                                    <div class="col-1 text-right">
                                        <a href="${cmsBaseUrl}/help/gene-page/disease-models/"
                                            target="_blank"><i class="fa fa-question-circle" style="font-size: xx-large"></i></a>
                                    </div>
                                </div>
                                <p class="ml-4">The table shows similarity analysis between the mouse phenotypes and human phenotypes <b>when the orthologous human
                                    gene is known to cause disease</b>. Strong similarities may indicate paths for research.
                                </p>
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

                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>




        <c:if test="${rowsForHistopathTable.size() > 0 or hasHistopath}">
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
        </c:if>


        <div class="container" id="order">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h2 class="h2"><b><i class="fal fa-book"></i>&nbsp;IMPC related publications</b></h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content pt-3 pb-5">
                            <div class="container p-0 p-md-2">
                                <p class="ml-4">The table below lists publications which used either products generated by the IMPC or data produced by the phenotyping efforts of the IMPC.  These publications have also been associated to ${gene.markerSymbol}.</p>
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
                                                                <c:if test="${fn:contains(allele.geneSymbol, gene.markerSymbol)}"><t:formatAllele>${allele.alleleSymbol}</t:formatAllele></c:if>
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

        <div class="container" id="order">
            <div class="row pb-2">
                <div class="col-12 col-md-12">
                    <h2 class="h2"><b><i class="fal fa-shopping-cart"></i>&nbsp;Order Mouse and ES Cells</b></h2>
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
                                    <div><a href="${cmsBaseUrl}/help/gene-page/ordering-mice-or-mouse-products/"><i
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


        <script  type="text/javascript">
            // disease tables drive by phenodigm core

            var diseaseDetailFilter = function(index, row) {
                return $(row[2]).text().length > 1;
            }
            var toggleDetail = function(index) {
                $('#diseases_by_annotation').bootstrapTable('toggleDetailView', index)
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

    </jsp:body>

</t:genericpage>
