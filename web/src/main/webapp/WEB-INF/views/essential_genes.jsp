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

                $('div#anatomo2').hide(); // by default
                $('div#embryo2').hide(); // by default

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
                        url: "${baseUrl}/update-gene-registration?asynch=true",
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


            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                           Page content here!
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


    </jsp:body>

</t:genericpage>
