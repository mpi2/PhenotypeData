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


                <div class="col-12 no-gutters">
                    <h2 style="float: left" class="mb-0">High Throughput Gene Targeting Design Id: ${designId}</h2>
                    <h2>

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

<%--                            <jsp:include page="genesPhenotypeAssociation_frag.jsp"/>--%>
                            <div class="container-fluid">
                                <div>
                                    <c:if test="${gene != null}">
                                        &nbsp;&raquo; <a href="${baseUrl}/search">Genes</a> &raquo; <a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a> &raquo; design
                                    </c:if>
                                </div>
                                <div class="row flex-xl-nowrap pb-5">
                                    <c:if test="${designs == null || fn:length(designs) eq 0}">
                                       <p> No IKMC designs were found for this design id : ${designId}</p>
                                    </c:if>
                                    <c:if test="${designs != null && fn:length(designs) != 0}">
                                        <img src="${baseUrl}/img/target_design_trimmed.png" style="width: 100%;  height: auto;"/>
                                    </c:if>


                                </div>
                            </div>


                        </div>
                    </div>
                </div>
            </div>
        </div>

        <c:if test="${designs != null && fn:length(designs) != 0}">
        <div class="container white-bg-small">
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <h3>Oligos</h3>
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div>Design Id: ${designId}</div>

                            <table id="designs"
                                   class="table"
                                   style="width:100%">
                                <tr><th>Type</th><th>start</th><th>stop</th><th>sequence</th><th>assembly</th><th>chr</th><th>strand</th></tr>
                            <c:forEach varStatus="index" items="${designs}" var="design">
                                <tr>
                                    <td><c:out value="${design.featureType}"/></td><td><c:out value="${design.oligoStart}"/></td><td><c:out value="${design.oligoStop}"/></td><td><c:out value="${design.oligoSequence}"/></td><td><c:out value="${design.assembly}"/><td><c:out value="${design.chr}"/></td><td><c:out value="${design.strand}"/></td>
                                </tr>
                            </c:forEach>

                            </table>

<%--                            ${designs}--%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>







    </jsp:body>

</t:genericpage>
