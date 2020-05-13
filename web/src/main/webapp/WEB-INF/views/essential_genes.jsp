<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>

    <jsp:attribute name="title">Essential Genes</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/gene?kw=*">Essential Genes</a></jsp:attribute>
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

                <noscript>
                    <div class="col-12 no-gutters">
                        <h5 style="float: left">Please enable javascript if you want to log in to follow or stop
                            following this gene.</h5>
                    </div>
                </noscript>

                <div class="col-12 no-gutters">
                    <h2 style="float: left" class="mb-0">Essential Genes</h2>
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
                           <p> Identifying which genes are linked to a rare disease is one of the most difficult challenges geneticists face. By cross comparing viability and phenotyping data from knockout IMPC mice with human cell essential scores from the Cancer Dependency map, genes can be categorised on how essential they are for supporting life and the likelihood they are associated with de novo genetic disorders. In this portal, we present integrated views of viability data to help researchers explore the full genetic spectrum of essentiality and aid in their discovery of new human disease genes.  More in our blog post here.
                           </p>
                            <p>
                                IMPC associated publications: Dickinson et al. Nature 2016 | Muñoz-Fuentes et al. Conservation Genetics Special Issue 2019 | Cacheiro et al. Nature Communications 2020
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>




    </jsp:body>

</t:genericpage>
