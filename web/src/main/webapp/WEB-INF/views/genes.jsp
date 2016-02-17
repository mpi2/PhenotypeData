<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Gene details for ${gene.markerName}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">

	</jsp:attribute>
	<jsp:attribute name="addToFooter">
            <!--  start of floating menu for genes page -->
            <div class="region region-pinned">

                <div id="flyingnavi" class="block">

                    <a href="#top"><i class="fa fa-chevron-up"
                                      title="scroll to top"></i></a>

                    <ul>
                        <li><a href="#top">Gene</a></li>
                        <li><a href="#section-associations">Phenotype Associations</a></li>
                        <!--  always a section for this even if says no phenotypes found - do not putting in check here -->
                        <c:if test="${phenotypeStarted}">
                            <li><a href="#heatmap">Heatmap</a></li>
                        </c:if>
                        <c:if test="${not empty solrFacets}">
                            <li><a href="#section-images">Associated Images</a></li>
                        </c:if>
                        <c:if test="${not empty expressionFacets}">
                            <li><a href="#section-expression">Expression</a></li>
                        </c:if>
                       	<c:if test="${not empty impcExpressionImageFacets or not empty expressionAnatomyToRow or not empty impcEmbryoExpressionImageFacets or not empty embryoExpressionAnatomyToRow}" >
                            <li><a href="#impc-expression">Expression</a></li>
                        </c:if>
                        <c:if test="${not empty impcImageFacets}">
                            <li><a href="#section-impc-images">Impc Images</a></li>
                        </c:if>
                        <c:if
                                test="${not empty orthologousDiseaseAssociations}">
                            <li><a href="#section-disease-models">Disease Models</a></li>
                        </c:if>
                        <c:if
                                test="${not empty phenotypicDiseaseAssociations}">
                            <li><a
                                    href="#section-potential-disease-models">Potential Disease Models</a></li>
                        </c:if>
                        <c:if test="${!countIKMCAllelesError}">
                            <li><a href="#order2">Order Mouse and ES Cells</a></li>
                        </c:if>
                    </ul>

                    <div class="clear"></div>

                </div>

            </div>
            <!--  end of floating menu for genes page -->

            <c:if test="${phenotypeStarted}">
                <script type="text/javascript"
                        src="${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js"></script>
                <!--[if !IE]><!-->
                <script>
                    dcc.heatmapUrlGenerator = function (genotype_id, type) {
                        return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
                    };
                </script>
                <!--<![endif]-->
                <!--[if gte IE 9]>
                <script>
                dcc.heatmapUrlGenerator = function(genotype_id, type) {
                return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
                };
                </script>
                <![endif]-->
                <script>
                    //new dcc.PhenoHeatMap('procedural', 'phenodcc-heatmap', 'Fam63a', 'MGI:1922257', 6, '//dev.mousephenotype.org/heatmap/rest/heatmap/');
                    new dcc.PhenoHeatMap({
                        /* identifier of <div> node that will host the heatmap */
                        'container': 'phenodcc-heatmap',
                        /* colony identifier (MGI identifier) */
                        'mgiid': '${gene.mgiAccessionId}',
                        /* default usage mode: ontological or procedural */
                        'mode': 'ontological',
                        /* number of phenotype columns to use per section */
                        'ncol': 5,
                        /* heatmap title to use */
                        'title': '${gene.markerSymbol}',
                        'url': {
                            /* the base URL of the heatmap javascript source */
                            'jssrc': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/js/',
                            /* the base URL of the heatmap data source */
                            'json': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/',
                            /* function that generates target URL for data visualisation */
                            'viz': dcc.heatmapUrlGenerator
                        }
                    });
                </script>
            </c:if>

        </jsp:attribute>


	<jsp:attribute name="header">

            <!-- JavaScript Local Imports -->

            <script src="${baseUrl}/js/general/enu.js"></script>
            <script src="${baseUrl}/js/general/dropdownfilters.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>

            <script type="text/javascript">
                var gene_id = '${acc}';

                $(function () {
                    console.log('calling tabs now');

                    $("#tabs").tabs();

                    $('ul.tabs li a').click(function () {

                        $(this).css({
                            'border': '1px solid #666',
                            'border-bottom': '1px solid white',
                            'background-color': 'white',
                            'color': '#666'
                        });
                    });


                    $('ul.tabs li a#ui-id-1').css({
                        'border': '1px solid #666',
                        'border-bottom': '1px solid white',
                        'background-color': 'white',
                        'color': '#666'
                    });

                });
            </script>
            <style>
                #svgHolder div div {
                    z-index: 100;
                }

                span.direct {
                    color: #698B22;
                }

                span.indirect {
                    color: #CD8500;
                }

                /* copied from CKs gwas page */
                div#tabs {
                    border: none;
                }

                ul.tabs {
                    border: none;
                    border-bottom: 1px solid #666;
                    background: none;
                }

                ul.tabs li:nth-child(1) {
                    font-size: 14px;
                }

                ul.tabs li a {
                    margin-bottom: -1px;
                    border: 1px solid #666;
                    font-size: 12px;
                }

                div.ui-tabs-panel {
                    padding: 0 !important;
                }

                .acontainer {
                    height: auto;
                    overflow: hidden;
                }

                .aright {
                    width: 200px;
                    float: left;
                    text-align: center;
                }

                .aleft {
                    float: none;
                    background: white;
                    width: auto;
                    overflow: hidden;
                    margin-left: 250px;
                }

                ul#expList {
                    /*Dividing long list of <li> tags into columns*/
                    -moz-column-count: 2;
                    -moz-column-gap: 30px;
                    -webkit-column-count: 2;
                    -webkit-column-gap: 30px;
                    column-count: 2;
                    column-gap: 30px;
                }

                ul#expList li {
                    padding-left: 0;
                    width: auto;
                }


                .ui-widget { font-family: Verdana,Arial,sans-serif/*{ffDefault}*/; font-size: 1.0em !important/*{fsDefault}*/; }
                .ui-widget  { font-size: 1.0em !important; }


            </style>

            <c:if test="${phenotypeStarted}">
                <!--[if !IE]><!-->
                <link rel="stylesheet" type="text/css"
                      href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
                <!--<![endif]-->
                <!--[if IE 8]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
                <![endif]-->
                <!--[if gte IE 9]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
                <![endif]-->
            </c:if>

        </jsp:attribute>

    <jsp:body>
        <div class="region region-content">
            <div class="block">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Gene: ${gene.markerSymbol}
                            <span class="documentation">
                                <a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>
                            </span>
                        </h1>

                        <div class="section">
                            <div class="inner">
                                <!--  login interest button -->
                                <div class="floatright">
                                    <c:choose>
                                        <c:when test="${registerButtonAnchor!=''}">
                                            <p><a class="btn" href='${registerButtonAnchor}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a>
                                            </p>
                                        </c:when>
                                        <c:otherwise>
                                            <p><a class="btn interest" id='${registerButtonId}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a>
                                            </p>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${orderPossible}">
                                        <p><a class="btn" href="#order2"> <i class="fa fa-shopping-cart"></i> Order </a></p>
                                    </c:if>
                                </div>

                                <c:if test="${gene.markerName != null}">
                                    <p class="with-label no-margin">
                                        <span class="label">Name</span> ${gene.markerName}
                                    </p>
                                </c:if>

                                <c:if test="${!(empty gene.markerSynonym)}">
                                    <p class="with-label no-margin">
                                        <span class="label">Synonyms</span>
                                        <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                                            ${synonym}
                                            <c:if test="${!loop.last}">, </c:if>
                                            <c:if test="${loop.last}"></c:if>
                                        </c:forEach>
                                    </p>
                                </c:if>

                                <p class="with-label">
                                    <span class="label">MGI Id</span>
                                    <a href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}">${gene.mgiAccessionId}</a>
                                </p>

                                <c:if test="${!(prodStatusIcons == '')}">
                                    <p class="with-label">
                                        <span class="label">Status</span>
                                            ${prodStatusIcons}
                                    </p>
                                </c:if>
                                <p class="with-label">
                                    <span class="label">Links</span>
                                    <a href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.mgiAccessionId}">Gene&nbsp;View</a>&nbsp;&nbsp;
                                    <a href="http://www.ensembl.org/Mus_musculus/Location/View?g=${gene.mgiAccessionId};contigviewbottom=das:http://das.sanger.ac.uk/das/ikmc_products=labels">Location&nbsp;View</a>&nbsp;&nbsp;
                                    <a href="http://www.ensembl.org/Mus_musculus/Location/Compara_Alignments/Image?align=677;db=core;g=${gene.mgiAccessionId}">Compara&nbsp;View</a>
                                     &nbsp;<a href="../genomeBrowser/${acc}" target="new"> Gene Browser</a><span id="enu"></span>
                                </p>
                                <c:if test="${viabilityCalls != null && viabilityCalls.size() > 0}">
									<p class="with-label">
	                                    <span class="label">Viability</span>
	                                	<t:viabilityButton callList="${viabilityCalls}" link=""></t:viabilityButton>
	                                </p>
								</c:if>
                                <!-- GWAS stuff -->
                                <c:if test="${!isLive}">
                                    <c:if test="${gwasPhenoMapping != null }">

                                       	<c:if test="${gwasPhenoMapping == 'no mapping' }">
                               	 			<p class="with-label">
                                   				<span class="label">GWAS mapping</span>
                                   				<a href="http://www.ebi.ac.uk/gwas/search?query=${gene.markerSymbol}"><i class="fa fa-external-link"></i>&nbsp;GWAS catalog</a>&nbsp;&nbsp;
                               				</p>
                               			</c:if>
                               			<c:if test="${gwasPhenoMapping == 'indirect' }">
                               	 			<p class="with-label">
                                   				<span class="label">GWAS mapping</span>
                                   				<a href="http://www.ebi.ac.uk/gwas/search?query=${gene.markerSymbol}"><i class="fa fa-external-link"></i>&nbsp;GWAS catalog</a>&nbsp;&nbsp;
                                   				<a href="${baseUrl}/phenotype2gwas?mgi_gene_symbol=${gene.markerSymbol}"><i class="fa fa-external-link"></i>&nbsp;<span class='indirect'>${gwasPhenoMapping} phenotypic mapping</span></a>&nbsp;&nbsp;

                               				</p>
                               			</c:if>
                               			<c:if test="${gwasPhenoMapping == 'direct' }">
                               	 			<p class="with-label">
                                   				<span class="label">GWAS mapping</span>
                                   				<a href="http://www.ebi.ac.uk/gwas/search?query=${gene.markerSymbol}"><i class="fa fa-external-link"></i>&nbsp;GWAS catalog</a>&nbsp;&nbsp;
                                   				<a href="${baseUrl}/phenotype2gwas?mgi_gene_symbol=${gene.markerSymbol}"><i class="fa fa-external-link"></i>&nbsp;<span class='direct'>${gwasPhenoMapping} phenotypic mapping</span></a>&nbsp;&nbsp;
                               				</p>
                               			</c:if>

                               		</c:if>
                                </c:if>

                            </div>

                        </div>
                        <!-- section end -->

                        <!--  Phenotype Associations -->
                        <div class="section">

                            <h2 class="title "
                                id="section-associations"> Phenotype associations for ${gene.markerSymbol}
                                    <span class="documentation"><a
                                            href='' id='mpPanel' class="fa fa-question-circle pull-right"></a></span>
                                <!--  this works, but need js to drive tip position -->
                            </h2>

                            <div class="inner">
                                <c:choose>
                                    <c:when test="${summaryNumber > 0}">

                                        <jsp:include page="phenotype_icons_frag.jsp"/>


                                        <c:if
                                                test="${!(empty dataMapList)}">
                                            <br/>
                                            <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->

        									<div class="floatright"  style="clear: both">
												<p>
                                                	<a class="btn" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}'>All Adult Data</a>
												<br/> 
                                                </p>
                                            </div>

                                        </c:if>

                                        <c:if
                                                test="${gene.embryoDataAvailable}">
                                            <div class="floatright"
                                                 style="clear: both">
                                                <a class="btn"
                                                   href="${drupalBaseUrl}/embryoviewer?mgi=${acc}"
                                                   style="margin: 10px">Embryo Viewer</a>
                                            </div>
                                        </c:if>

                                        <p> Phenotype Summary based on automated MP annotations supported by experiments
                                            on knockout mouse models. </p>

                                        <c:forEach var="zyg"  items="${phenotypeSummaryObjects.keySet()}">
                                            <p>In <b>${zyg} :</b>
                                            </p>
                                            <ul>
                                                <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true).size() > 0}'>
                                                    <li><p><b>Both sexes</b> have the following phenotypic abnormalities
                                                    </p>
                                                        <ul>
                                                            <c:forEach var="summaryObj"
                                                                       items='${phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true)}'>
                                                                <li>
                                                                    <a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>.
                                                                    Evidence from
                                                                    <c:forEach var="evidence"
                                                                               items="${summaryObj.getDataSources()}"
                                                                               varStatus="loop">
                                                                        ${evidence}
                                                                        <c:if test="${!loop.last}">,&nbsp;
                                                                        </c:if>
                                                                    </c:forEach> &nbsp;&nbsp;&nbsp; (<a
                                                                        class="filterTrigger"
                                                                        id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </li>
                                                </c:if>

                                                <c:if
                                                        test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true).size() > 0}'>
                                                    <li><p> Following phenotypic abnormalities occured in <b>females</b>
                                                        only</p>
                                                        <ul>
                                                            <c:forEach
                                                                    var="summaryObj"
                                                                    items='${phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true)}'>
                                                                <li><a
                                                                        href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>.
                                                                    Evidence from <c:forEach
                                                                            var="evidence"
                                                                            items="${summaryObj.getDataSources()}"
                                                                            varStatus="loop"> ${evidence} <c:if
                                                                            test="${!loop.last}">,&nbsp;</c:if>
                                                                    </c:forEach> &nbsp;&nbsp;&nbsp; (<a
                                                                            class="filterTrigger"
                                                                            id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </li>
                                                </c:if>

                                                <c:if
                                                        test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true).size() > 0}'>
                                                    <li><p> Following phenotypic abnormalities occured in <b>males</b>
                                                        only</p>
                                                        <ul>
                                                            <c:forEach
                                                                    var="summaryObj"
                                                                    items='${phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true)}'>
                                                                <li><a
                                                                        href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>.
                                                                    Evidence from <c:forEach
                                                                            var="evidence"
                                                                            items="${summaryObj.getDataSources()}"
                                                                            varStatus="loop"> ${evidence} <c:if
                                                                            test="${!loop.last}">,&nbsp;</c:if>
                                                                    </c:forEach> &nbsp;&nbsp;&nbsp; (<a
                                                                            class="filterTrigger"
                                                                            id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </c:forEach>


                                    </c:when>
                                    <c:when test="${summaryNumber == 0}">

                                        <c:if  test="${empty dataMapList && empty phenotypes}">
                                        	<c:if test="${attemptRegistered}">
	                                        	<div class="alert alert-info">
	                                                <h5>Registered for phenotyping</h5>
	                                                <p>Phenotyping is planned for a knockout strain of this gene but data is not currently available.</p>
	                                            </div>
                                        	</c:if>

                                        	<c:if test="${!attemptRegistered}">
	                                            <div class="alert alert-info">
                                                    <h5>Currently not registered for phenotyping</h5>
                                                    <p>Phenotyping is not currently planned for this gene.</p>
	                                            </div>
                                            </c:if>
                                            <br/>
                                        </c:if>
                                        <c:if  test="${!(empty dataMapList) && empty phenotypes}">
                                            <div class="alert alert-info">
                                                <h5>No Significant Phenotype Associations Found</h5>

                                                <p>No significant phenotype associations were found with data that has passed quality control (QC), but you can click
                                                on the "All Adult Data" button to see all phenotype data that has passed QC. Preliminary phenotype assocations
                                                may appear with new pre-QC phenotype data.</p>
                                            </div>
                                            <br/>
                                            <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->
                                            <div class="floatright"
                                                 style="clear: both">
												<p>
                                                	<a class="btn" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}'>All Adult Data</a>
												<br/> 
                                                </p>
                                            </div>
                                            <div class="clear"></div>
                                        </c:if>

                                        <c:if
                                                test="${gene.embryoDataAvailable}">
                                            <div class="floatright"
                                                 style="clear: both">
                                                <a class="btn"
                                                   href="${drupalBaseUrl}/embryoviewer?mgi=${acc}"
                                                   style="margin: 10px">Embryo Viewer</a>
                                            </div>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${hasPreQcData}">
                                        <!-- Only pre QC data available, suppress post QC phenotype summary -->
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert alert-info">There are currently no IMPC phenotype associations for the gene ${gene.markerSymbol} </div>
                                        <br/>
                                    </c:otherwise>
                                </c:choose>

                                <c:if
                                        test='${hasPreQcData || summaryNumber > 0 || phenotypes.size() > 0}'>
                                    <!-- Associations table -->
                                    <h5>Filter this table</h5>


                                    <div class="row-fluid">
                                        <div
                                                class="container span12">
                                            <br/>

                                            <div class="row-fluid"
                                                 id="phenotypesDiv">

                                                <div
                                                        class="container span12">

                                                    <c:if
                                                            test="${not empty phenotypes}">
                                                        <form
                                                                class="tablefiltering no-style" id="target"
                                                                action="destination.html">
                                                            <c:forEach
                                                                    var="phenoFacet" items="${phenoFacets}"
                                                                    varStatus="phenoFacetStatus">
                                                                <select
                                                                        id="${phenoFacet.key}" class="impcdropdown"
                                                                        multiple="multiple"
                                                                        title="Filter on ${phenoFacet.key}">
                                                                    <c:forEach
                                                                            var="facet" items="${phenoFacet.value}">
                                                                        <option>${facet.key}</option>
                                                                    </c:forEach>
                                                                </select>
                                                            </c:forEach>
                                                            <div
                                                                    class="clear"></div>
                                                        </form>
                                                        <div
                                                                class="clear"></div>

                                                        <c:set
                                                                var="count" value="0" scope="page"/>
                                                        <c:forEach
                                                                var="phenotype" items="${phenotypes}"
                                                                varStatus="status">
                                                            <c:forEach
                                                                    var="sex" items="${phenotype.sexes}">
                                                                <c:set var="count" value="${count + 1}" scope="page"/>
                                                            </c:forEach>
                                                        </c:forEach>

                                                        <jsp:include
                                                                page="PhenoFrag.jsp"></jsp:include>
                                                        <br/>

                                                        <div
                                                                id="exportIconsDiv"></div>
                                                    </c:if>

                                                    <!-- if no data to show -->
                                                    <c:if
                                                            test="${empty phenotypes}">
                                                        <div
                                                                class="alert alert-info">Pre QC data has been submitted
                                                            for this gene. Once the QC process is finished phenotype
                                                            associations stats will be made available.
                                                        </div>
                                                    </c:if>

                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                </c:if>
                            </div>

                        </div>
                        <!-- end of phenotype associations -->

                        <c:if test="${phenotypeStarted}">
                            <!-- Pre-QC phenotype heatmap -->
                            <div class="section">
                                <h2 class="title" id="heatmap">Pre-QC phenotype heatmap <span
                                        class="documentation"><a href='' id='preqcPanel'
                                                                 class="fa fa-question-circle pull-right"></a></span>
                                </h2>

                                <div class="inner">
                                    <div class="alert alert-info">
                                        <h5>Caution</h5>

                                        <p>These are the results of a preliminary statistical analysis. Data are still
                                            in the process of being quality controlled and results may change.</p>
                                    </div>
                                </div>
                                <div class="dcc-heatmap-root">
                                    <div class="phenodcc-heatmap"
                                         id="phenodcc-heatmap"></div>
                                </div>
                            </div>
                            <!-- section end -->
                        </c:if>

                        <c:if test="${not empty imageErrors}">
                            <div class="row-fluid dataset">
                                <div class="alert">
                                    <strong>Warning!</strong>${imageErrors }</div>
                            </div>
                        </c:if>

						<div class="clear"></div>
						<br/> <br/>
                            <!-- Expression in Anatomogram -->
                            <c:if test="${!isLive}">
							<c:if test="${not empty anatomogram}">
        					<div class="section">
                                <h2 class="title" id="expression-anatomogram">Expression in Anatomogram<span
                                        class="documentation"><a href='' id='expressionAnatomogramPanel'
                                                                 class="fa fa-question-circle pull-right"></a></span>
                                    <!--  this works, but need js to drive tip position -->
                                </h2>

                                <div class="inner acontainer" style="display: block;">
                                    <div class='aright' id='anatomogramContainer'></div>
                                    <div class='aleft'>
                                        <h6>Annotated tissues / organs:</h6>
                                        <ul id='expList'>
                                            <c:forEach var="entry" items="${impcExpressionImageFacets}"
                                                       varStatus="status">
                                                <c:set var="href"
                                                       scope="page"
                                                       value="${baseUrl}/impcImages/laczimages/${acc}/${entry.name}">
                                                </c:set>
                                                <li>${entry.name}(${entry.count})</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </div>
                                <!-- end of inner div -->
                            </div>
                            </c:if>
                            </c:if>
                            <!-- end of anatomogram section -->

                            <!-- Expression (IMPC) -->
<c:if test="${not empty impcExpressionImageFacets or not empty expressionAnatomyToRow or not empty impcEmbryoExpressionImageFacets or not empty embryoExpressionAnatomyToRow}">
                        
	<div class="section">

		<h2 class="title" id="impc-expression">Expression<span
         class="documentation"><a href='' id='impcExpressionPanel'
                                                                 class="fa fa-question-circle pull-right"></a></span>
        </h2>

        <div class="inner" style="display: block;">

                                   <!-- section for expression data here -->
               <div id="tabs">
                     <ul class='tabs'>
                     <c:if test="${not empty impcExpressionImageFacets}">
                          <li><a href="#tabs-1">Adult Expression Images View</a></li>
                     </c:if>
                     <c:if test="${not empty expressionAnatomyToRow }">
                        <li><a href="#tabs-2">Expression Data Overview</a></li>
                     </c:if>
                     <c:if test="${not empty impcEmbryoExpressionImageFacets}">
                        <li><a href="#tabs-3">Embryo Expression Images Overview</a></li>
                     </c:if>
                     <c:if test="${not empty embryoExpressionAnatomyToRow}">
                        <li><a href="#tabs-4">Embryo Expression Data Overview</a></li>
                     </c:if>
                     </ul>
                     <c:if test="${ not empty expressionAnatomyToRow}"><!-- if size greater than 1 we have more data than just unassigned which we will -->
					<div id="tabs-2" style="height: 500px; overflow: auto;">
					<br/>
 									<c:set var="expressionIcon" scope="page" value="fa fa-check"/>
                                    <c:set var="noTissueIcon" scope="page" value="fa fa-circle-o"/>
                                    <c:set var="noExpressionIcon" scope="page" value="fa fa-times"/>
                                    <c:set var="ambiguousIcon" scope="page" value="fa fa-circle"/>
                                    <c:set var="yesColor" scope="page" value="#0978a1"/>
                                    <c:set var="noColor" scope="page" value="gray"/>

                                    <span title="Expression" class="${expressionIcon}"
                                          style="color:${yesColor}">&nbsp;Expression</span>&nbsp;&nbsp;
                                    <span title="No Expression" class="${noExpressionIcon}"
                                          style="color: gray">&nbsp;No Expression</span>&nbsp;&nbsp;
                                    <span title="No Tissue Available" class="${noTissueIcon}"
                                          style="color: gray">&nbsp;No Tissue Available</span>&nbsp;&nbsp;
                                    <span title="Ambiguous" class="${ambiguousIcon}"
                                          style="color: gray">&nbsp;Ambiguous</span>&nbsp;&nbsp;

									<br/> <br/>

                                            <!-- <h2 class="title" id="section-impc_expression">Expression Overview<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                            -->

                                            <table>
                                                <tr>
                                                    <th>Anatomy</th>
                                                    <th
                                                            title="Number of heterozygous mutant specimens with data for the specified anatomy">
                                                        #HET Specimens
                                                    </th>
                                                    <th
                                                            title="If there are images for homozygous specimens this value will be 'Yes'">
                                                        HOM Images?
                                                    </th>
                                                    <th
                                                            title="Status of expression for Wild Type specimens from any colony with data for this anatomy">
                                                        WT Expr
                                                    </th>
                                                    <th title="">Mutant Expr</th>
                                                        <%-- <th>Mutant specimens</th> --%>
                                                    <th
                                                            title="An clickable image icon will show if images are available for mutant specimens">
                                                        Images
                                                    </th>
                                                </tr>
                                                <c:forEach var="mapEntry"
                                                           items="${expressionAnatomyToRow}">
                                                    <tr>
                                                        <td><a
                                                                href="${baseUrl}/anatomy/${mapEntry.value.abnormalMaId}">${mapEntry.value.abnormalMaName}</a>
                                                            <c:if
                                                                    test="${!fn:containsIgnoreCase(mapEntry.key, mapEntry.value.abnormalMaName)}"> <span
                                                                    title="IMPReSS Term differs from MA term">(${mapEntry.key})</span>
                                                            </c:if></td>
                                                        <td><span
                                                                title="${mapEntry.value.numberOfHetSpecimens} Heterozygous Mutant Mice">${mapEntry.value.numberOfHetSpecimens}</span>
                                                        </td>
                                                        <td
                                                                <c:if test="${mutantImagesAnatomyToRow[mapEntry.key].homImages}">style="color:${yesColor}"</c:if>>
                                     			<span
                                                        title="Homozygote Images are
                                     			<c:if test="${!mutantImagesAnatomyToRow[mapEntry.key].homImages}">not</c:if> available"><c:if
                                                        test="${mutantImagesAnatomyToRow[mapEntry.key].homImages}">Yes</c:if>
																<c:if
                                                                        test="${!mutantImagesAnatomyToRow[mapEntry.key].homImages}">No</c:if></span>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when
                                                                        test="${wtAnatomyToRow[mapEntry.key].expression}">
                                     				<span
                                                            title="WT Expressed: ${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed)} wild type specimens expressed from a total of ${fn:length(wtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${expressionIcon}"
                                                            style="color:${yesColor}"></span>(${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed)}/${fn:length(wtAnatomyToRow[mapEntry.key].specimen)})
                                                                </c:when>
                                                                <c:when
                                                                        test="${wtAnatomyToRow[mapEntry.key].notExpressed}">
                          							<span
                                                            title="WT NOT expressed: ${fn:length(wtAnatomyToRow[mapEntry.key].specimenNotExpressed)} Not Expressed ${fn:length(wtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${noExpressionIcon}" style="color:${noColor}"></span>
                                                                </c:when>
                                                                <c:when
                                                                        test="${wtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                                                                    <i title="WT No Tissue Available"
                                                                       class="${noTissueIcon}"
                                                                       style="color:${noColor}"></i>
                                                                </c:when>

                                                                <c:otherwise>
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon}" style="color:${noColor}"></span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when
                                                                        test="${mapEntry.value.expression}">
                                     				<span
                                                            title="Expressed: ${fn:length(mapEntry.value.specimenExpressed)} mutant specimens expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${expressionIcon}"
                                                            style="color:${yesColor}"></span>(${fn:length(mapEntry.value.specimenExpressed)}/${fn:length(mapEntry.value.specimen)})
                                                                </c:when>
                                                                <c:when
                                                                        test="${mapEntry.value.notExpressed}">
                          							<span
                                                            title="Not Expressed: ${fn:length(mapEntry.value.specimenNotExpressed)} Not Expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${noExpressionIcon}" style="color:${noColor}"></span>
                                                                </c:when>
                                                                <c:when
                                                                        test="${mapEntry.value.noTissueAvailable}">
                          							<span title="No Tissue Available"
                                                          class="${noTissueIcon}" style="color:${noColor}"></span>
                                                                </c:when>

                                                                <c:otherwise>
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon}" style="color:${noColor}"></span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                            <%-- <td>

                                                            <c:forEach var="specimen" items="${mapEntry.value.specimen}">
                                                            <i title="zygosity= ${specimen.value.zyg}">${specimen.key}</i>

                                                            </c:forEach></td> --%>

                                                        <td>
                                                            <c:if
                                                                    test="${mutantImagesAnatomyToRow[mapEntry.key].imagesAvailable}">
                                                                <a
                                                                        href='${baseUrl}/impcImages/images?q=*:*&fq=(procedure_name:"Adult LacZ" AND ma_id:"${mapEntry.value.abnormalMaId}" AND marker_symbol:"${gene.markerSymbol}")'><i
                                                                        title="Images available (click on this icon to view images)"
                                                                        class="fa fa-image"
                                                                        alt="Images">(${mutantImagesAnatomyToRow[mapEntry.key].numberOfImages})</i>
                                                                </a>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>

                                            </table>

                                        </div>
                                        </c:if>
                                        

                                        <!-- section for expression data here -->
										<c:if test="${not empty impcExpressionImageFacets}">
                                        <div id="tabs-1">

                                            <!-- <h2 class="title" id="section-impc_expression">Expression Data<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                             -->
                                            <div class="accordion-body"
                                                 style="display: block;">

                                                <a href="${baseUrl}/impcImages/laczimages/${acc}">All Images</a>
                                                <c:forEach var="entry" items="${impcExpressionImageFacets}"
                                                           varStatus="status">

                                                    <c:set var="href"
                                                           scope="page"
                                                           value="${baseUrl}/impcImages/laczimages/${acc}/${entry.name}"></c:set>
                                                    <ul>
                                                        <t:impcimgdisplay2
                                                                category="${entry.name}(${entry.count})" href="${href}"
                                                                img="${impcExpressionFacetToDocs[entry.name][0]}"
                                                                impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
                                                    </ul>

                                                </c:forEach> <!-- solrFacets end -->

                                            </div>
                                            <!--  end of tabs-2 -->

                                        </div>
                                        </c:if>

									<c:if test="${not empty embryoExpressionAnatomyToRow}">
									<div id="tabs-4" style="height: 500px; overflow: auto;">
											<br/>
		 									<c:set var="expressionIcon" scope="page" value="fa fa-check"/>
		                                    <c:set var="noTissueIcon" scope="page" value="fa fa-circle-o"/>
		                                    <c:set var="noExpressionIcon" scope="page" value="fa fa-times"/>
		                                    <c:set var="ambiguousIcon" scope="page" value="fa fa-circle"/>
		                                    <c:set var="yesColor" scope="page" value="#0978a1"/>
		                                    <c:set var="noColor" scope="page" value="gray"/>
		
		                                    <span title="Expression" class="${expressionIcon}"
		                                          style="color:${yesColor}">&nbsp;Expression</span>&nbsp;&nbsp;
		                                    <span title="No Expression" class="${noExpressionIcon}"
		                                          style="color: gray">&nbsp;No Expression</span>&nbsp;&nbsp;
		                                    <span title="No Tissue Available" class="${noTissueIcon}"
		                                          style="color: gray">&nbsp;No Tissue Available</span>&nbsp;&nbsp;
		                                    <span title="Ambiguous" class="${ambiguousIcon}"
		                                          style="color: gray">&nbsp;Ambiguous</span>&nbsp;&nbsp;

									<br/> <br/>
                                            <!-- <h2 class="title" id="section-impc_expression">Expression Overview<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                            -->

                                            <table>
                                                <tr>
                                                    <th>Anatomy</th>
                                                    <th
                                                            title="Number of heterozygous mutant specimens with data for the specified anatomy">
                                                        #HET Specimens
                                                    </th>
                                                    <th
                                                            title="If there are images for homozygous specimens this value will be 'Yes'">
                                                        HOM Images?
                                                    </th>
                                                    <th
                                                            title="Status of expression for Wild Type specimens from any colony with data for this anatomy">
                                                        WT Expr
                                                    </th>
                                                    <th title="">Mutant Expr</th>
                                                        <%-- <th>Mutant specimens</th> --%>
                                                    <th
                                                            title="An clickable image icon will show if images are available for mutant specimens">
                                                        Images
                                                    </th>
                                                </tr>
                                                <c:forEach var="mapEntry"
                                                           items="${embryoExpressionAnatomyToRow}">
                                                    <tr>
                                                        <td><a
                                                                href="${baseUrl}/anatomy/${mapEntry.value.abnormalMaId}">${mapEntry.value.abnormalMaName}</a>
                                                            <c:if
                                                                    test="${!fn:containsIgnoreCase(mapEntry.key, mapEntry.value.abnormalMaName)}"> <span
                                                                    title="IMPReSS Term differs from EMAP term">(${mapEntry.key})</span>
                                                            </c:if></td>
                                                        <td><span
                                                                title="${mapEntry.value.numberOfHetSpecimens} Heterozygous Mutant Mice">${mapEntry.value.numberOfHetSpecimens}</span>
                                                        </td>
                                                        <td
                                                                <c:if test="${embryoMutantImagesAnatomyToRow[mapEntry.key].homImages}">style="color:${yesColor}"</c:if>>
                                     			<span
                                                        title="Homozygote Images are
                                     			<c:if test="${!embryoMutantImagesAnatomyToRow[mapEntry.key].homImages}">not</c:if> available"><c:if
                                                        test="${embryoMutantImagesAnatomyToRow[mapEntry.key].homImages}">Yes</c:if>
																<c:if
                                                                        test="${!embryoMutantImagesAnatomyToRow[mapEntry.key].homImages}">No</c:if></span>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when
                                                                        test="${embryoWtAnatomyToRow[mapEntry.key].expression}">
                                     				<span
                                                            title="WT Expressed: ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenExpressed)} wild type specimens expressed from a total of ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${expressionIcon}"
                                                            style="color:${yesColor}"></span>(${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenExpressed)}/${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)})
                                                                </c:when>
                                                                <c:when
                                                                        test="${embryoWtAnatomyToRow[mapEntry.key].notExpressed}">
                          							<span
                                                            title="WT NOT expressed: ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimenNotExpressed)} Not Expressed ${fn:length(embryoWtAnatomyToRow[mapEntry.key].specimen)} wild type specimens"
                                                            class="${noExpressionIcon}" style="color:${noColor}"></span>
                                                                </c:when>
                                                                <c:when
                                                                        test="${embryoWtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                                                                    <i title="WT No Tissue Available"
                                                                       class="${noTissueIcon}"
                                                                       style="color:${noColor}"></i>
                                                                </c:when>

                                                                <c:otherwise>
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon}" style="color:${noColor}"></span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when
                                                                        test="${mapEntry.value.expression}">
                                     				<span
                                                            title="Expressed: ${fn:length(mapEntry.value.specimenExpressed)} mutant specimens expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${expressionIcon}"
                                                            style="color:${yesColor}"></span>(${fn:length(mapEntry.value.specimenExpressed)}/${fn:length(mapEntry.value.specimen)})
                                                                </c:when>
                                                                <c:when
                                                                        test="${mapEntry.value.notExpressed}">
                          							<span
                                                            title="Not Expressed: ${fn:length(mapEntry.value.specimenNotExpressed)} Not Expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens"
                                                            class="${noExpressionIcon}" style="color:${noColor}"></span>
                                                                </c:when>
                                                                <c:when
                                                                        test="${mapEntry.value.noTissueAvailable}">
                          							<span title="No Tissue Available"
                                                          class="${noTissueIcon}" style="color:${noColor}"></span>
                                                                </c:when>

                                                                <c:otherwise>
                                     				<span title="Ambiguous"
                                                          class="${ambiguousIcon}" style="color:${noColor}"></span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                            <%-- <td>

                                                            <c:forEach var="specimen" items="${mapEntry.value.specimen}">
                                                            <i title="zygosity= ${specimen.value.zyg}">${specimen.key}</i>

                                                            </c:forEach></td> --%>

                                                        <td>
                                                            <c:if
                                                                    test="${embryoMutantImagesAnatomyToRow[mapEntry.key].imagesAvailable}">
                                                                <a
                                                                        href='${baseUrl}/impcImages/images?q=*:*&fq=(procedure_name:"Embryo LacZ" AND emap_id:"${mapEntry.value.abnormalMaId}" AND marker_symbol:"${gene.markerSymbol}")'><i
                                                                        title="Images available (click on this icon to view images)"
                                                                        class="fa fa-image"
                                                                        alt="Images">(${embryoMutantImagesAnatomyToRow[mapEntry.key].numberOfImages})</i>
                                                                </a>
                                                            </c:if>
                                                        </td>
                                                    </tr>
                                                </c:forEach>

                                            </table>

                                        </div>
                                        </c:if>

                                       <!--  <a href="/phenotype-archive/imagePicker/MGI:1922730/IMPC_ELZ_063_001">
         		<img src="//wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/177626/200/" style="max-height: 200px;"></a> -->
         								<c:if test="${not empty impcEmbryoExpressionImageFacets}">
         								<div id="tabs-3">



                                             	<div class="accordion-body"
                                                 style="display: block;">

                                                <a href="${baseUrl}/impcImages/embryolaczimages/${acc}">All Images</a>
                                                <c:forEach var="entry" items="${impcEmbryoExpressionImageFacets}"
                                                           varStatus="status">

                                                    <c:set var="href"
                                                           scope="page"
                                                           value="${baseUrl}/impcImages/embryolaczimages/${acc}/${entry.name}"></c:set>
                                                    <ul>
                                                        <t:impcimgdisplay2
                                                                category="${entry.name}(${entry.count})" href="${href}"
                                                                img="${impcEmbryoExpressionFacetToDocs[entry.name][0]}"
                                                                impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
                                                    </ul>

                                                </c:forEach> <!-- solrFacets end -->

                                            </div>


                                        </div>
                                        </c:if>
			</div><!-- end of tabs -->
        </div><!-- end of innner -->

     </div><!-- end of impc expression section (excluding anatomogram) -->
     </c:if>
                       
                        <!-- end of expression section if -->
                        <!-- end of inner ide is wrong when displayed in browser these divs are needed-->

                        <!-- nicolas accordion for images here -->
                        <c:if test="${not empty impcImageFacets}">
                            <!-- IMPC Phenotype Associated Images -->
                            <div class="section">
                                <h2 class="title" id="section-impc-images">IMPC Phenotype Associated Images<span
                                        class="documentation"><a href='' id='impcImagesPanel' class="fa fa-question-circle pull-right"></a></span>
                                </h2>

                                <div class="inner">
                                    <c:forEach var="entry"
                                               items="${impcImageFacets}" varStatus="status">


                                        <c:forEach var="doc"
                                                   items="${impcFacetToDocs[entry.name]}">
                                             <c:if test="${doc.procedure_name ne 'Embryo LacZ' }">
                                            <div
                                                    id="impc-images-heading" class="accordion-group">

                                                <div
                                                        class="accordion-heading">
                                                        ${doc.parameter_name}(${entry.count})
                                                </div>
                                                <div
                                                        class="accordion-body">
                                                    <ul>
                                                        <c:set var="href" scope="page"
                                                               value="${baseUrl}/imagePicker/${acc}/${entry.name}"></c:set>
                                                        <a
                                                                href="${href}">
                                                            <t:impcimgdisplay2
                                                                    img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"
                                                                    pdfThumbnailUrl="${pdfThumbnailUrl}" href="${href}"
                                                                    count="${entry.count}"></t:impcimgdisplay2>
                                                        </a>
                                                    </ul>


                                                        <%--  <div class="clear"></div>
                                                            <c:if test="${entry.count>5}">
                                                                <p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
                                                            </c:if> --%>
                                                </div>

                                                <!--  end of accordion body -->
                                            </div>
                                             </c:if>
                                        </c:forEach>

                                    </c:forEach>


                                </div>
                                <!--  end of inner -->
                            </div>
                            <!-- end of section -->
                        </c:if>

                        <!-- nicolas accordion for images here -->
                        <c:if test="${not empty solrFacets}">
                            <!-- Phenotype Associated Images -->
                            <div class="section">
                                <h2 class="title" id="section-images">Phenotype Associated Images<span
                                        class="documentation"><a href='' id='legacyImagesPanel' class="fa fa-question-circle pull-right"></a></span>
                                </h2>
                                <!--  <div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	 -->
                                <div class="inner">
                                    <c:forEach var="entry"
                                               items="${solrFacets}" varStatus="status">
                                        <div class="accordion-group">
                                            <div
                                                    class="accordion-heading">
                                                    ${entry.name} (${entry.count})
                                            </div>
                                            <div
                                                    class="accordion-body">
                                                <ul>
                                                    <c:forEach
                                                            var="doc" items="${facetToDocs[entry.name]}">
                                                        <li>
                                                            <t:imgdisplay
                                                                    img="${doc}"
                                                                    mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                                <div class="clear"></div>
                                                <c:if
                                                        test="${entry.count>5}">
                                                    <p
                                                            class="textright">
                                                        <a
                                                                href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i
                                                                class="fa fa-caret-right"></i> show all ${entry.count}
                                                            images</a>
                                                    </p>
                                                </c:if>
                                            </div>
                                            <!--  end of accordion body -->
                                        </div>
                                    </c:forEach>
                                    <!-- solrFacets end -->

                                </div>
                                <!--  end of inner -->
                            </div>
                            <!-- end of section -->
                        </c:if>

                        <c:if test="${not empty expressionFacets}">
                            <!-- Expression (legacy) -->
                            <div class="section">
                                <h2 class="title" id="section-expression">Expression<span
                                        class="documentation"><a href='' id='legacyExpressionPanel' class="fa fa-question-circle pull-right"></a></span>
                                </h2>

                                <div class="inner">

                                    <!-- thumbnail scroller markup begin -->
                                    <c:forEach var="entry"
                                               items="${expressionFacets}" varStatus="status">
                                        <div class="accordion-group">
                                            <div
                                                    class="accordion-heading">
                                                    ${entry.name} (${entry.count})
                                            </div>
                                            <div
                                                    class="accordion-body">

                                                <ul>
                                                    <c:forEach
                                                            var="doc" items="${expFacetToDocs[entry.name]}">
                                                        <li>
                                                            <t:imgdisplay
                                                                    img="${doc}"
                                                                    mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                                <div class="clear"></div>
                                                <c:if
                                                        test="${entry.count>5}">
                                                    <p
                                                            class="textright">
                                                        <a
                                                                href='${baseUrl}/images?gene_id=${acc}&fq=sangerProcedureName:"Wholemount Expression"&fq=selected_top_level_ma_term:"${entry.name}"'>show
                                                            all ${entry.count} images</a>
                                                    </p>
                                                </c:if>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>

                        <!--Disease Sections-->
                        <c:if
                            test="${not empty orthologousDiseaseAssociations}">
                            <!-- Disease Models associated by gene orthology -->
                            <div class="section"
                                 id="orthologous-diseases">
                                <h2 class="title"
                                    id="section-disease-models">Disease Models
                                    <small
                                        class="sub">associated by gene orthology
                                    </small>
                                    <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
                                        <span class="documentation">
                                            <a href='${baseUrl}/documentation/disease-help.html#details' class="mpPanel">
                                               <i class="fa fa-question-circle pull-right"></i>
                                            </a>
                                        </span>
                                </h2>
                                <div class="inner">
                                    <table
                                            id="orthologous_diseases_table"
                                            class="table tableSorter disease">
                                        <jsp:include
                                                page="genes_orthologous_diseases_table_frag.jsp"></jsp:include>
                                    </table>
                                </div>
                            </div>
                        </c:if>

                        <c:if
                            test="${not empty phenotypicDiseaseAssociations}">
                            <!-- Potential Disease Models predicted by phenotypic similarity -->
                            <div class="section"
                                 id="predicted-diseases">
                                <h2 class="title"
                                    id="section-potential-disease-models">Potential Disease Models
                                    <small
                                            class="sub">predicted by phenotypic similarity
                                    </small>
                                    <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
                                        <span class="documentation">
                                            <a href='${baseUrl}/documentation/disease-help.html#details' class="mpPanel">
                                                <i class="fa fa-question-circle pull-right"></i>
                                            </a>
                                        </span>
                                </h2>
                                <div class="inner">
                                    <table id="predicted_diseases_table" class="table tableSorter disease">
                                        <jsp:include page="genes_predicted_diseases_table_frag.jsp"></jsp:include>
                                    </table>
                                </div>
                            </div>
                        </c:if>

                        <!-- Order Mouse and ES Cells -->
                        <div class="section" id="order2">
                            <h2 class="title documentation" id="order-panel">Order Mouse and ES Cells<span
                                    class="documentation"><a href='' id='orderPanel' class="fa fa-question-circle pull-right"></a></span>
                            </h2>

                            <div class="inner">
                                <div id="allele2"></div>
                            </div>
                        </div>
                    </div>
                    <!--end of node wrapper should be after all secions  -->
                </div>
            </div>
        </div>

        <script type="text/javascript" src="${baseUrl}/js/phenodigm/diseasetableutils.min.js?v=${version}"></script>
        <script type="text/javascript" src="${baseUrl}/js/vendor.bundle.js?v=${version}"></script>
		<c:if test="${!isLive}">
              <script type="text/javascript" src="${baseUrl}/js/anatomogram.bundle.js?v=${version}"></script>
        </c:if>
        <script type="text/javascript">
            var diseaseTables = [{
                id: '#orthologous_diseases_table',
                tableConf: {
                    processing: true,
                    paging: false,
                    info: false,
                    searching: false,
                    order: [[2, 'desc'], [4, 'desc'], [3, 'desc']],
                    "sPaginationType": "bootstrap"
                }
            }, {
                id: '#predicted_diseases_table',
                tableConf: {
                    order: [[2, 'desc'], [4, 'desc'], [3, 'desc']],
                    "sPaginationType": "bootstrap"
                }
            }];


             $(document).ready(function () {
                for (var i = 0; i < diseaseTables.length; i++) {
                    var diseaseTable = diseaseTables[i];
                    var dataTable = $(diseaseTable.id).DataTable(diseaseTable.tableConf);
                    $.fn.addTableClickCallbackHandler(diseaseTable.id, dataTable);
                }

                // invoke anatomogram only when
                // this check is not empty: impcExpressionImageFacets
                if ($('div#anatomogramContainer').size() == 1) {

                    // anatomogram stuff
                    var expData = JSON.parse('${anatomogram}');

                    //console.log("no expression: ")
                    //console.log(expData.noExpression);
                    //console.log("all paths: ")
                    //console.log(expData.allPaths);

                    var anatomogramData = {

                        "maleAnatomogramFile": "mouse_male.svg",
                        "toggleButtonMaleImageTemplate": "/resources/images/male",
                        "femaleAnatomogramFile": "mouse_female.svg",
                        "toggleButtonFemaleImageTemplate": "/resources/images/female",
                        "brainAnatomogramFile": "mouse_brain.svg",
                        "toggleButtonBrainImageTemplate": "/resources/images/brain",

                        // all tested tissues (expressed + tested but not expressed)
                        "allSvgPathIds": expData.allPaths,
                        // test only
                        //"allSvgPathIds": [],
                        //"allSvgPathIds": ["UBERON_0000029", "UBERON_0001736", "UBERON_0001831"], // lymph nodes
                        //"allSvgPathIds": ["UBERON_0000947", "UBERON_0001981", "UBERON_0001348", "UBERON_0001347", "EFO_0000962"],

                        "contextRoot": "/gxa"
                    };

                    // tissues having expressions
                    var profileRows = [
                        {
                            "name": "tissues with expression",
                            "expressions": expData.expression
                        }
                    ];
                    //console.log("profile: ");
                    //console.log(profileRows);

                    var EventEmitter = window.exposed.EventEmitter;
                    var eventEmitter = new EventEmitter();

                    var AnatomogramBuilder = window.exposed.AnatomogramBuilder;

                    AnatomogramBuilder(
                            document.getElementById("anatomogramContainer"),
                            anatomogramData,
                            profileRows,
                            // make color the same to disguise mouseover highlight
                            "blue",  // all tissues being tested
                            "blue",  // tissue color when mouseover
                            eventEmitter);
                }

                $("img.ui-button").each(function(){
                    // hide brain toggle for now
                    if ($(this).attr('src').indexOf('brain') != -1){
                        $(this).hide();
                    }
                });
            });


        </script>

    </jsp:body>

</t:genericpage>
