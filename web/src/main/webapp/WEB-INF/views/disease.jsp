<%-- 
    Document   : disease2
    Comment    : Page generating disease pages.                 
    Created on : 1-Sep-2017    
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>
    <jsp:attribute name="title">${disease.id} - ${disease.term}</jsp:attribute>

    <jsp:attribute name="header">

        <script type="text/javascript">var impc = {baseUrl: "${baseUrl}"}</script>

        <%-- Phenogrid requirements --%>
        <script defer type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.js?v=${version}" ></script>

        <%-- Load async CSS stylesheet, see https://www.filamentgroup.com/lab/load-css-simpler/ --%>
        <link rel="preload" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" as="style" />
        <link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.min.css?v=${version}" media="print" onload="this.media='all'" />

        <%-- Phenodigm2 requirements --%>
        <script defer type="text/javascript" src="https://d3js.org/d3.v4.min.js" ></script>
        <script defer type="text/javascript" src="${baseUrl}/js/vendor/underscore/underscore-1.8.3.min.js" ></script>
        <script defer type="text/javascript" src="${baseUrl}/js/phenodigm2/phenodigm2.js?v=${version}" ></script>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/phenodigm2.css" />
        <%-- End of phenodigm2 requirements --%>

        <style>.small-logo { width: 60px;} </style>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">		
        <div class="region region-pinned">
            <div id="flyingnavi" class="block smoothScroll">
                <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
                <ul>
                    <li><a href="#top">Disease</a></li>
                    <li><a href="#mouse_models_phenoscatter">Mouse Models - visualization</a></li>
                    <li><a href="#mouse_models_phenotables">Mouse Models - table</a></li>
                </ul>
                <div class="clear"></div>
            </div>
        </div>
    </jsp:attribute>

    <jsp:attribute name="breadcrumb">
        &nbsp;&raquo; <a href="${baseUrl}/search/disease?kw=*"> Diseases</a>&nbsp;&raquo; ${disease.id}
    </jsp:attribute>

    <jsp:body>

        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Disease: ${disease.term}</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
        <div class="row pb-5">
        <div class="col-12 col-md-12">
            <div class="pre-content clear-bg">
                <div class="page-content people py-5 white-bg">

                    <%-- Name --%>
                    <div class="row no-gutters mb-2 mb-sm-0">
                        <div class="col-2 text-right pr-1">
                            <div class="pr-2">Name</div>
                        </div>
                        <div class="col-10 font-weight-bold text-left">
                            <span>${disease.term}</span>
                        </div>
                    </div>

                    <%-- Synomyms --%>
                    <div class="row no-gutters mb-2 mb-sm-0">
                        <div class="col-2 text-sm-right pr-1">
                            <div class="pr-2">Synonmys</div>
                        </div>
                        <div class="col-10 font-weight-bold text-sm-left">
                            <span>
                                <c:choose>
                                    <c:when test="${empty disease.alts}">
                                        -
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="synonym" items="${disease.alts}" varStatus="loop">
                                            ${synonym} <c:if test="${!loop.last}">, </c:if>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>

                            <%-- Classification --%>
                        <div class="row no-gutters mb-2 mb-sm-0">
                            <div class="col-2 text-right pr-1">
                                <div class="pr-2">Classification </div>
                            </div>
                            <div class="col-10 font-weight-bold text-left">
                                <span>
                                                                <c:choose>
                                                                    <c:when test="${empty disease.classes}">
                                                                        -
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <c:forEach var="diseaseClass" items="${disease.classes}" varStatus="loop">
                                                                            ${diseaseClass}<c:if test="${!loop.last}">, </c:if>
                                                                        </c:forEach>
                                                                    </c:otherwise>
                                                                </c:choose>
                                </span>
                            </div>
                        </div>
                            <%-- Associated Genes  --%>
                        <div class="row no-gutters mb-2 mb-sm-0">
                            <div class="col-2 text-right pr-1">
                                <div class="pr-2">Associated Genes</div>
                            </div>
                            <div class="col-10 font-weight-bold text-left">
                                <span>
                                    <c:choose>
                                        <c:when test="${empty curatedAssociations}">
                                            -
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="assoc" items="${curatedAssociations}" varStatus="loop">
                                                <a href="${assoc.externalUrl}">${assoc.symbol}</a>
                                                <c:choose>
                                                    <c:when test="${empty assoc.symbolsWithdrawn}"></c:when>
                                                    <c:otherwise>
                                        <span class="small">(Withdrawn symbols:
                                            <c:forEach var="withdrawn" items="${assoc.symbolsWithdrawn}"
                                                       varStatus="loopwithdrawn">
                                                ${withdrawn}<c:if test="${!loopwithdrawn.last}">, </c:if>
                                            </c:forEach>
                                            )</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${!loop.last}">, </c:if>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>

                            <%-- Mouse Orthologs   --%>
                        <div class="row no-gutters mb-2 mb-sm-0">
                            <div class="col-2 text-right pr-1">
                                <div class="pr-2">Mouse Orthologs</div>
                            </div>
                            <div class="col-10 font-weight-bold text-left">
                                <span>
                                    <c:choose>
                                        <c:when test="${empty orthologousAssociations}">
                                            -
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="assoc" items="${orthologousAssociations}" varStatus="loop">
                                                <a href="${assoc.externalUrl}">${assoc.symbol}</a>
                                                <c:choose>
                                                    <c:when test="${empty assoc.symbolsWithdrawn}"></c:when>
                                                    <c:otherwise>
                                        <span class="small">(Withdrawn symbols:
                                            <c:forEach var="withdrawn" items="${assoc.symbolsWithdrawn}"
                                                       varStatus="loopwithdrawn">
                                                ${withdrawn}<c:if test="${!loopwithdrawn.last}">, </c:if>
                                            </c:forEach>
                                            )</span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${!loop.last}">, </c:if>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </div>

                            <%-- Source   --%>
                        <div class="row no-gutters mb-2 mb-sm-0">
                            <div class="col-2 text-right pr-1">
                                <div class="pr-2">Source</div>
                            </div>
                            <div class="col-10 font-weight-bold text-left">
                                <span>
                                    <a href="${disease.externalUrl}">${disease.id}</a> <span class="small">(names, synonyms, disease associated genes)</span>,
                            <br/> Orphanet <span class="small">(disease classes)</span>,
                            <br/> HGNC, Ensembl, MGI <span class="small">(gene symbols, gene orthology)</span>
                            <br/> HPO <span class="small">(phenotypes)</span>
                                </span>
                            </div>
                        </div>

                </div>
            </div>
        </div>
        </div>
        </div>



        <%-- A tabbed view of mouse models by orthology/phenotypic similarity --%>
        <div class="container">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h3>Mouse Models</h3>
                </div>
            </div>
        </div>

        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 white-bg">
                    <div class="page-content pt-5 pb-5">
                        <p>Learn about how to interpret our results in the <a href="${cmsBaseUrl}/help/data-visualization/gene-pages/disease-models/">Disease Models Help pages</a></p>
                        <div id="phenotabs" class="phenotabs">
                            <ul class='tabs'>
                                <li><a href="#by-annotation">By orthology</a></li>
                                <li><a href="#by-phenotype">By phenotypic similarity</a></li>
                            </ul>
                            <div id="by-annotation">
                                <c:choose>
                                    <c:when test="${!hasModelsByOrthology}">
                                        No mouse models associated with ${disease.id} by orthology to a human gene.
                                    </c:when>
                                    <c:otherwise>
                                        <table id="models_by_annotation" class="table tablesorter disease"></table>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div id="by-phenotype">
                                <c:choose>
                                    <c:when test="${!hasModelAssociations}">
                                        No mouse models associated with ${disease.id} by phenotypic similarity.
                                    </c:when>
                                    <c:otherwise>
                                        <table id="models_by_phenotype" class="table tablesorter disease"></table>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <%-- Js objects used to generate html on pageload: 
             here relevant/curated mouse genes, relevant mouse models --%>
        <script type="text/javascript">
            var curatedGenes = ${curatedMouseGenes};
            var modelAssociations = ${modelAssociations};
        </script>
        <%-- Configuration of tables, e.g. how they appear sorted and paginated.
             This is executed after pageload, thus page may first appear, then change. 
        --%>
        <script type="text/javascript">
            // configuration for scatterplot
            var diseaseScatterConf = {
                id: "#phenoscatter",
                knowngenes: curatedGenes,
                h: 350, // height in pixels 
                margin: [20, 60, 50, 60], // top, right, bottom, left margins
                offset: [40, -40], // distance of x, y labels from axes
                legendpos: [18, 30, 22], // x1, x2, top positions of legend
                legendspacing: 18, // spacing between legend lines                
                detailwidth: 200, // width of the detail box on the right
                detailpad: 10, // internal padding within the details box
                linecolor: "#444444", // threshold line
                linewidth: 3,
                color: ["#f27823", "#d2d2d2"], // highlighted, non-highlighted models
                radius: 4,
                axes: ["maxRaw", "avgRaw"],
                labs: ["Max score", "Avg score"],
                threshold: 2.2
            };
            // configuration for tables, used by jquery datatable and by phenodigm2
            var diseaseTableConfs = [
                {
                    id: '#models_by_annotation',
                    tableConf: {
                        paging: false,
                        info: false,
                        searching: false,
                        order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                        sPaginationType: "bootstrap"
                    },
                    phenodigm2Conf: {
                        pageType: "disease",
                        disease: "${disease.id}",
                        groupBy: "markerId",
                        filterKey: "markerSymbol",
                        filter: curatedGenes,
                        minScore: 0,
                        innerTables: true
                    }
                },
                {
                    id: '#models_by_phenotype',
                    tableConf: {
                        order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                        pageLength: 20,
                        lengthMenu: [20, 50, 100]
                    },
                    phenodigm2Conf: {
                        pageType: "disease",
                        disease: "${disease.id}",
                        groupBy: "markerId",
                        filterKey: "markerSymbol",
                        filter: [],
                        minScore: 1,
                        innerTables: true
                    }
                }];

            $(document).ready(function () {
                $("#phenotabs").tabs({active: 1});
                // create tables
                for (var i = 0; i < diseaseTableConfs.length; i++) {
                    var dTable = diseaseTableConfs[i];
                    // create raw table
                    impc.phenodigm2.makeTable(modelAssociations, dTable.id, dTable.phenodigm2Conf);
                    // apply jquery transformation (pagination, etc)
                    var dataTable = $(dTable.id).DataTable(dTable.tableConf);
                    // add phenodigm handlers
                    $.fn.addTableClickPhenogridHandler(dTable.id, dataTable);
                }
                $.fn.qTip({'pageName': 'diseases'});
            });

        </script>

    </jsp:body>

</t:genericpage>
