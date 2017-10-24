<%-- 
    Document   : disease2
    Comment    : Page generating disease pages.
                 This file is largely based on disease.jsp, but uses a different 
                 data model.
    Created on : 1-Sep-2017    
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>
    <jsp:attribute name="title">${disease.id} - ${disease.term}</jsp:attribute>

    <jsp:attribute name="header">
        <script src="//d3js.org/d3.v4.min.js"></script> 
        <script type="text/javascript" src="${baseUrl}/js/vendor/underscore/underscore-1.8.3.min.js"></script>        
        <script type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.js?v=${version}"></script>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.css?v=${version}">
        <script type="text/javascript">var impc = {baseUrl: "${baseUrl}"}</script>        
        <script type="text/javascript" src="${baseUrl}/js/phenodigm2/phenodigm2.js?v=${version}"></script>        
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css"/>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/dev.css"/>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/phenodigm2.css"/>
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/genes.css"/>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">		
        <div class="region region-pinned">
            <div id="flyingnavi" class="block smoothScroll">
                <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
                <ul>
                    <li><a href="#top">Disease</a></li>
                    <li><a href="#orthologous_mouse_models">Mouse Models</a></li>
                    <li><a href="#potential_mouse_models">Potential Mouse Models</a></li>
                </ul>
                <div class="clear"></div>
            </div>
        </div>
    </jsp:attribute>

    <jsp:attribute name="breadcrumb">
        &nbsp;&raquo; <a href="${baseUrl}/search/disease?kw=*"> Diseases</a>&nbsp;&raquo; ${disease.id}
    </jsp:attribute>

    <jsp:body>

        <h1 class="title" id="top">Disease: ${disease.term}
            <span class="documentation"><a href='' id='summarySection' class="fa fa-question-circle pull-right"></a></span>
        </h1>

        <div class="section">
            <div class="inner">
                <p class="with-label">
                    <span class="label">Name</span>
                    ${disease.term}
                </p>

                <p class="with-label">
                    <span class="label">Synonyms</span>
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
                </p>

                <p class="with-label">
                    <span class="label">Classification</span>
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
                </p>
                
                <%-- Section with associated phenotypes --%>
                <p class="with-label">
                    <span class="label">Phenotypes</span>
                    <c:choose>
                        <c:when test="${empty disease.phenotypes}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="dphen" items="${disease.phenotypes}" varStatus="dphenloop">                                    
                                <a href="https://monarchinitiative.org/phenotype/${dphen.id}">${dphen.term}</a><c:if test="${!dphenloop.last}"><span class="semicolon">;</span></c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                </p>
                <%-- End of section on phenotypes --%>

                <p class="with-label">
                    <span class="label">Associated Genes</span>
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
                                            <c:forEach var="withdrawn" items="${assoc.symbolsWithdrawn}" varStatus="loopwithdrawn">                                
                                                ${withdrawn}<c:if test="${!loopwithdrawn.last}">, </c:if>
                                            </c:forEach>
                                            )</span>  
                                        </c:otherwise>                                                                        
                                    </c:choose>
                                    <c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </p>

                <p class="with-label">
                    <span class="label">Mouse Orthologs</span>
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
                                            <c:forEach var="withdrawn" items="${assoc.symbolsWithdrawn}" varStatus="loopwithdrawn">                                
                                                ${withdrawn}<c:if test="${!loopwithdrawn.last}">, </c:if>
                                            </c:forEach>
                                            )</span>  
                                        </c:otherwise>                                                                        
                                    </c:choose>
                                    <c:if test="${!loop.last}">, </c:if>
                            </c:forEach>                            
                        </c:otherwise>
                    </c:choose>                           
                </p>

                <p class="with-label">
                    <span class="label">Source</span>
                    <a href="${disease.externalUrl}">${disease.id}</a> <span class="small">(names, synonyms, disease associated genes)</span>, 
                    <br/> Orphanet <span class="small">(disease classes)</span>,                    
                    <br/> HGNC, Ensembl, MGI <span class="small">(gene symbols, gene orthology)</span>
                    <br/> HPO <span class="small">(phenotypes)</span>
                </p>

            </div>
        </div>              


        <%-- Display visualization of all model (hits) --%>
        <div class="section" id="mouse_models_phenoscatter">
            <h2 class="title">Mouse Models <small class="sub"> phenotype-based associations</small>                
            </h2>
            <div class="inner">                 
                <div id="phenoscatter"></div>
                <div id="phenoscatter-help">Disease phenotypes are compared with mouse phenotypes, with each 
                    mouse model contributing one point to the diagram. Scores are computed according
                    to phenotype specificity and cross-species similarity. 
                    <b>X-axis</b> shows the score of the single phenotype that contributes most. 
                    <b>Y-axis</b> shows an average among all phenotype matches. The <b>curved line</b>
                    designates a visualization threshold; models below threshold are omitted, except when
                    their genotype matches a disease-associated gene.</div>
            </div>
        </div>

        <%-- A tabbed view of mouse models by orthology/phenotypic similarity --%>
        <div class="section" id="mouse_models_phenotables">                               
            <h2 class="title">Mouse Models 
                <span class="documentation"><a href='' id='orthologySection' class="mpPanel fa fa-question-circle pull-right"></a></span>
            </h2>
            <div class="inner">  
                <div id="phenotabs" class="phenotabs">
                    <ul class='tabs'>
                        <li><a href="#by-annotation">By disease annotation</a></li>
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
                        lengthMenu: [20, 50, 100],
                        sPaginationType: "bootstrap"
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
                $("#phenotabs").tabs({active: 0});
                // create visualization at the top
                impc.phenodigm2.makeScatterplot(modelAssociations, diseaseScatterConf);
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
