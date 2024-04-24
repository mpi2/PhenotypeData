<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>


<t:genericpage>

    <jsp:attribute name="title">${geneSymbol} Mouse Gene ${parameterName} | ${gene.markerName} download</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="header">

        <link rel="canonical" href="https://www.mousephenotype.org/data/genes/${gene.mgiAccessionId}" />
        <meta name="description" content="Phenotype data for mouse gene ${gene.markerSymbol}. Discover ${gene.markerSymbol}'s significant phenotypes, expression, images, histopathology and more. Data for gene ${gene.markerSymbol} is all freely available for download." />
        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <script type="text/javascript">
            var impc = {baseUrl: "${baseUrl}"};
            var gene_id = '${acc}';
            var monarchUrl = '${monarchUrl}';
            var base_url = '${baseUrl}';
            var geneId = '${gene.mgiAccessionId}';
        </script>
    </jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body class="page-template page-template-no-sidebar--large">
    </jsp:attribute>
    <jsp:body>
        <div class="container data-heading">
            <div class="row">

                <noscript>
                    <div class="col-12 no-gutters">
                        <h5 style="float: left">Please enable javascript if you want to log in to follow or stop
                            following this gene.</h5>
                    </div>
                </noscript>

                <div class="col-12 no-gutters">
                    <h1 style="float: left" class="h1 m-0"><b>${geneSymbol} ${parameterName}</b></h1>
                </div>
            </div>
        </div>


        <div class="container white-bg-small">
            <div class="breadcrumbs clear row">
                <div class="col-10 d-none d-lg-block px-5 pt-5">
                    <aside>
                        <a href="/">Home</a> <span class="fal fa-angle-right"></span>
                        <a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
                        <a href="${baseUrl}/genes/${geneAcc}">${geneSymbol}</a> <span class="fal fa-angle-right"></span>
                            ${procedureName} / ${parameterName}
                    </aside>
                </div>
            </div>
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                    <tr>
                                        <th scope="col">Allele Symbol</th>
                                        <th scope="col">Sex</th>
                                        <th scope="col">Zygosity</th>
                                        <th scope="col">Experimental sample group</th>
                                        <th scope="col">Procedure</th>
                                        <th scope="col">Parameter</th>
                                        <th scope="col"></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="file" items="${files}">
                                        <tr>
                                            <td><t:formatAllele>${file.allele_symbol}</t:formatAllele></td>
                                            <td><t:displaySexes sexes="${file.sex}"></t:displaySexes></td>
                                            <td>${file.zygosity}</td>
                                            <td>${file.biological_sample_group}</td>
                                            <td>${file.procedure_name}</td>
                                            <td>${file.parameter_name}</td>
                                            <td><a class="btn btn-outline-primary download-data"  download href="${file.download_file_path.replace("http://", "https://")}"><i class="fal fa-download"></i>&nbsp;Download</a></td>
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
    </jsp:body>
</t:genericpage>