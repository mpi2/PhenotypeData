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
                    <h2 style="float: left" class="mb-0">Essential Genes</h2>
                    <h2>
                        <a href="${cmsBaseUrl}/help/essential-genes-page/" target="_blank">
                            <i class="fa fa-question-circle" style="float: right; color: #212529; padding-right: 10px;"></i></a>
                    </h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="row pb-5">

                <div class="col-12 col-md-12">


                    <div class="pre-content clear-bg">
                        <div class="breadcrumbs clear row">
                            <div class="col-12 d-none d-lg-block pt-5">
                                <aside>
                                    <a href="/">Home</a> <span class="fal fa-angle-right"></span>
                                    <a href="${baseUrl}/search">Genes</a><span class="fal fa-angle-right"></span>
                                    Essential Genes
                                </aside>
                            </div>
                        </div>
                        <div class="page-content people py-5 white-bg">
                            <p>Identifying which genes are linked to a rare disease is one of the most difficult
                                challenges geneticists face. By cross comparing viability and phenotyping data from
                                knockout IMPC mice with human cell essential scores from the Cancer Dependency map,
                                genes can be categorised on how essential they are for supporting life and the
                                likelihood they are associated with de novo genetic disorders. In this portal, we
                                present integrated views of viability data to help researchers explore the full genetic
                                spectrum of essentiality and aid in their discovery of new human disease genes. More in
                                our blog post <a href="">here</a>.
                            </p>
                            <p><b>IMPC associated publications</b>: <a href="">Dickinson et al. Nature 2016</a> | <a href="">Muñoz-Fuentes
                                et al. Conservation Genetics Special Issue 2019</a> | <a href="">Cacheiro et al. Nature
                                Communications 2020</a>
                            </p>

                            <div class="row">
                                <div class="col-4">
                                    <img class="mx-auto d-block" src="img/essential_genes/fusil.png"/>
                                </div>

                                <div class="col-4">
                                    <img class="mx-auto d-block" src="img/essential_genes/ballclump.png"/>
                                </div>

                                <div class="col-4">
                                    <img class="mx-auto d-block" src="img/essential_genes/batchq.png"/>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-4">
                                    <h5 class="text-center">FUSIL</h5>
                                    <u><b>Fu</b></u>ll <u><b>S</b></u>pectrum of Intolerence to <u><b>L</b></u>oss of
                                    Function
                                </div>

                                <div class="col-4">
                                    <h5 class="text-center">HaploEssential</h5>
                                    <p>IMPC haploessential screen to study genes most important to life</p>
                                </div>

                                <div class="col-4">
                                    <h5 class="text-center">Batch Query</h5>
                                    <p>Submit your gene list to find human and mouse essential scores</p>
                                </div>
                            </div>

                            <h2 class="pt-5">IMPC and human cell viability data</h2>
                            <div class="row">
                                <div class="col-md-6">
                                </div>
                                <div class="col-md-6">
                                    <p>FUSIL scores available for NNNN human genes</p>
                                    <ul>
                                        <li>
                                            CL- Cellular Essential
                                        </li>
                                        <li>
                                            DL- Developmental Essential
                                        </li>
                                        <li>
                                            SV- Subviable
                                        </li>
                                        <li>
                                            VP- Viable with phenotype
                                        </li>
                                        <li>
                                            VN- Viable with no phenotype detected
                                        </li>
                                    </ul>

                                    <p>DL genes are enriched for de novo genetic diseases as described in<a href="">Cacheiro
                                        et al. Nature Communications 2020</a>
                                    </p>
                                </div>
                            </div>

                            <h2 class="pt-5">Batch Query</h2>
                            <p>Visit the IMPC <a href="${baseUrl}/batchQuery">Batch Query</a> to submit your gene list to find human and mouse
                            essentiality scores.
                            </p>

                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>




    </jsp:body>

</t:genericpage>
