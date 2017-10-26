<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>

<t:genericpage>

    <jsp:attribute name="title">${gene.markerSymbol} - ${gene.markerName}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body class="gene-node no-sidebars small-header">

        </jsp:attribute>
        <jsp:attribute name="addToFooter">
            <%--  floating menu, displays quick links to sections of long page --%>
            <div class="region region-pinned">
                <div id="flyingnavi" class="block smoothScroll">
                    <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
                        <%-- Menu list always displays a fixed number of headings --%> 
                    <ul>                                                
                        <li><a href="#top">Gene</a></li>
                        <li><a href="#section-associations">Phenotype Associations</a></li>						
                        <li><a href="#section-expression">Expression</a></li>
                        <li><a href="#section-images">Associated Images</a></li>
                        <li><a href="#section-disease-models">Disease Models</a></li>
                        <li><a href="#order2">Order Mouse and ES Cells</a></li>
                    </ul>
                    <div class="clear"></div>
                </div>
            </div>
        </jsp:attribute>


        <jsp:attribute name="header">

            <script src="${baseUrl}/js/general/enu.js"></script>
            <script src="${baseUrl}/js/general/dropdownfilters.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>
            <%-- Phenodigm1 requirements --%>
            <script type="text/javascript" src="${baseUrl}/js/phenodigm/diseasetableutils.js?v=${version}"></script>
            <%-- Phenogrid requirements --%>
            <script type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.js?v=${version}"></script>
            <link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.css?v=${version}">                               
            <%-- Phenodigm2 requirements --%>                                
            <script src="//d3js.org/d3.v4.min.js"></script>
            <script type="text/javascript">var impc = {baseUrl: "${baseUrl}"}</script>        
            <script type="text/javascript" src="${baseUrl}/js/vendor/underscore/underscore-1.8.3.min.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/phenodigm2/phenodigm2.js?v=${version}"></script>       
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/phenodigm2.css"/>                                
            <%-- End of phenodigm2 requirements --%>

            <script type="text/javascript">
                var gene_id = '${acc}';

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

                    $('div#anatomo1').hide(); // by default

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

                    $('div#expDataView').click(function () {
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

                    $('#heatmap_link').click(function () {
                        console.log('heatmap link clicked');

                        /* //load the css
                         var cssId = 'myCss';  // you could encode the css path itself to generate id..
                         if (!document.getElementById(cssId))
                         {
                         var head  = document.getElementsByTagName('head')[0];
                         var link  = document.createElement('link');
                         link.id   = cssId;
                         link.rel  = 'stylesheet';
                         link.type = 'text/css';
                         link.href = '${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css';
                         link.media = 'all';
                         head.appendChild(link);
                         } */

                        if ($('#heatmap_toggle_div').length) {//check if this div exists first as this will ony exist if phenotypeStarted and we don't want to do this if not.
                            $('#heatmap_toggle_div').toggleClass('hidden');//toggle the div whether the heatmap has been generated or not.
                            $('#phenotypeTableDiv').toggleClass('hidden');
                            if (!heatmap_generated) {

                                var script = document.createElement('script');
                                script.src = "${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js";
                                script.onload = function () {

                                    //do stuff with the script
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
                                    heatmap_generated = 1;

                                };


                                document.head.appendChild(script);



                            }//end of if heatmap generated

                        }



                    });

                });
            </script>

            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/genes.css"/>				

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
                                    <a href='' id='summarySection' class="fa fa-question-circle pull-right"></a>
                                </span>
                            </h1>

                            <!-- general Gene info -->
                            <div class="section">
                                <%--<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>--%>
                                <div class="inner">

                                    <jsp:include page="genesGene_frag.jsp"/>
                                </div>
                            </div>
                            <!-- end of general Gene info -->

                            <!--  Phenotype Associations -->
                            <div class="section">

                                <h2 class="title "
                                    id="section-associations"> Phenotype associations for ${gene.markerSymbol}
                                    <span class="documentation">
                                        <a href='' id='phenoAssocSection' class="fa fa-question-circle pull-right"></a>
                                    </span>
                                    <!--  this works, but need js to drive tip position -->
                                </h2>

                                <div class="inner">
                                    <jsp:include page="genesPhenotypeAssociation_frag.jsp"/>
                                </div>

                            </div>
                            <!-- end of Phenotype Associations -->




                            <c:if test="${not empty imageErrors}">
                                <div class="row-fluid dataset">
                                    <div class="alert"><strong>Warning!</strong>${imageErrors }</div>
                                </div>
                            </c:if>

                            <div class="clear"></div>
                            <br/> <br/>


                            <!-- IMPC / legacy Expressions -->
                            <div class="section">

                                <h2 class="title" id="section-expression">Expression
                                    <span class="documentation"><a href='' id='expressionSection' goto="geneTab" class="fa fa-question-circle pull-right"></a></span>
                                </h2>

                                <div class="inner" style="display: block;">
                                    <c:if test="${empty impcAdultExpressionImageFacetsWholemount
                                                  and empty impcAdultExpressionImageFacetsSection
                                                  and empty expressionAnatomyToRow
                                                  and empty impcEmbryoExpressionImageFacets
                                                  and empty embryoExpressionAnatomyToRow
                                                  and empty expressionFacets}">
                                          <div class="alert alert_info">Expression data not available</div>
                                    </c:if>


                                    <c:if test="${not empty impcAdultExpressionImageFacetsWholemount
                                                  or not empty impcAdultExpressionImageFacetsSection
                                                  or not empty expressionAnatomyToRow
                                                  or not empty impcEmbryoExpressionImageFacets
                                                  or not empty embryoExpressionAnatomyToRow}">

                                          <h5 class="sectHint">IMPC lacZ Expression Data</h5>
                                          <!-- section for expression data here -->
                                          <div id="exptabs">
                                              <ul class='tabs'>
                                                  <li><a href="#tabs-1">Adult Expression</a></li>

                                                  <%--<c:if test="${not empty expressionAnatomyToRow }">--%>
                                                  <%--<li><a href="#tabs-1">Adult Expression</a></li>--%>
                                                  <%--</c:if>--%>

                                                  <%--<c:if test="${not empty impcAdultExpressionImageFacets}">--%>
                                                  <li><a href="#tabs-3">Adult Expression Image</a></li>
                                                      <%--</c:if>--%>

                                                  <%--<c:if test="${not empty embryoExpressionAnatomyToRow}">--%>
                                                  <li><a href="#tabs-4">Embryo Expression</a></li>
                                                      <%--</c:if>--%>

                                                  <%--<c:if test="${not empty impcEmbryoExpressionImageFacets}">--%>
                                                  <li><a href="#tabs-5">Embryo Expression Image</a></li>
                                                      <%--</c:if>--%>

                                              </ul>

                                              <c:choose>
                                                  <c:when test="${not empty expressionAnatomyToRow }">
                                                      <div id="tabs-1">
                                                          <!-- Expression in Anatomogram -->
                                                          <jsp:include page="genesAnatomogram_frag.jsp"></jsp:include>
                                                          </div>
                                                  </c:when>
                                                  <c:otherwise>
                                                      <div id="tabs-1">
                                                          <!-- Expression in Anatomogram -->
                                                          No expression data was found for this adult tab
                                                      </div>
                                                  </c:otherwise>
                                              </c:choose>
                                              <%--<c:if test="${ not empty expressionAnatomyToRow}"><!-- if size greater than 1 we have more data than just unassigned which we will -->--%>
                                              <%--<div id="tabs-2">--%>
                                              <%--<jsp:include page="genesAdultExpEata_frag.jsp"></jsp:include>--%>
                                              <%--</div>--%>
                                              <%--</c:if>--%>

                                              <!-- section for expression data here -->
                                              <c:choose>
                                                  <c:when test="${not empty wholemountExpressionImagesBean.filteredTopLevelAnatomyTerms && not empty sectionExpressionImagesBean.filteredTopLevelAnatomyTerms}">
                                                      <div id="tabs-3">
                                                          <jsp:include page="genesAdultLacZ+ExpImg_frag.jsp"></jsp:include>
                                                          </div>
                                                  </c:when>
                                                  <c:otherwise>
                                                      <div id="tabs-3">
                                                          No expression image was found for this adult tab
                                                      </div>
                                                  </c:otherwise>
                                              </c:choose>

                                              <c:choose>
                                                  <c:when test="${not empty embryoExpressionAnatomyToRow}">
                                                      <div id="tabs-4" style="height: 500px; overflow: auto;">
                                                          <jsp:include page="genesEmbExpData_frag.jsp"></jsp:include>
                                                          </div>
                                                  </c:when>
                                                  <c:otherwise>
                                                      <div id="tabs-4">
                                                          No expression data was found for this embryo tab
                                                      </div>
                                                  </c:otherwise>
                                              </c:choose>
                                              <c:choose>
                                                  <c:when  test="${not empty wholemountExpressionImagesEmbryoBean.expFacetToDocs || not empty sectionExpressionEmbryoImagesBean.expFacetToDocs}">
                                                      <div id="tabs-5">
                                                          <jsp:include page="genesEmbExpImg_frag.jsp"></jsp:include>
                                                          </div>
                                                  </c:when>
                                                  <c:otherwise>
                                                      <div id="tabs-5">
                                                          No expression image was found for this embryo tab
                                                      </div>
                                                  </c:otherwise>
                                              </c:choose>

                                              <br style="clear: both">
                                          </div><!-- end of tabs -->
                                    </c:if>

                                    <c:if test="${not empty expressionFacets and (not empty impcAdultExpressionImageFacets
                                                  or not empty expressionAnatomyToRow
                                                  or not empty impcEmbryoExpressionImageFacets
                                                  or not empty embryoExpressionAnatomyToRow)}">
                                          <hr>
                                    </c:if>

                                    <!-- Expression (legacy) -->
                                    <c:if test="${not empty expressionFacets}">
                                        <h5 class="sectHint">Secondary lacZ Expression Data</h5>

                                        <!-- thumbnail scroller markup begin -->
                                        <c:forEach var="entry" items="${expressionFacets}" varStatus="status">
                                            <div class="accordion-group">
                                                <div class="accordion-heading">
                                                    ${entry.name} (${entry.count})
                                                </div>
                                                <div class="accordion-body">
                                                    <ul>
                                                        <c:forEach var="doc" items="${expFacetToDocs[entry.name]}">
                                                            <li>
                                                                <t:imgdisplay
                                                                    img="${doc}"
                                                                    mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                                </li>
                                                        </c:forEach>
                                                    </ul>
                                                    <div class="clear"></div>
                                                    <c:if test="${entry.count>5}">
                                                        <p class="textright">
                                                            <a href='${baseUrl}/images?gene_id=${acc}&fq=sangerProcedureName:"Wholemount Expression"&fq=selected_top_level_ma_term:"${entry.name}"'>show
                                                                all ${entry.count} images</a>
                                                        </p>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <%--</div>--%>
                                    </c:if>
                                    <br style="clear: both">
                                </div>
                            </div><!-- end of IMPC / legacy Expressions -->




                            <!-- nicolas accordion for IMPC / Legacy phenotype associated images here -->
                            <div class="section">
                                <h2 class="title" id="section-images">Associated Images
                                    <span class="documentation"><a href="" id="phenoAssocImgSection" class="fa fa-question-circle pull-right"></a></span>
                                </h2>

                                <div class="inner" style="display: block;">
                                    <c:if test="${empty impcImageGroups and empty solrFacets}">
                                        <div class="alert alert_info">Phenotype associated images not available</div>
                                    </c:if>

                                    <c:if test="${not empty impcImageGroups or not empty solrFacets}">
                                        <c:if test="${not empty impcImageGroups}">
                                            <h5 class="sectHint">Associated Images</h5>
                                            <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                        </c:if>

                                        <c:if test="${not empty impcImageFacets and not empty solrFacets}">
                                            <hr>
                                        </c:if>

                                        <c:if test="${not empty solrFacets}">
                                            <h5 class="sectHint">Legacy Phenotype Associated Images</h5>
                                            <jsp:include page="genesLegacyPhenoAssocImg_frag.jsp"></jsp:include>
                                        </c:if>

                                    </c:if>
                                </div>
                            </div>                            

                            <%--Disease section (phenodigm2) --%>
                            <div class="section">
                                <h2 class="title" id="section-disease-models">Disease Models
                                    <span class="documentation">
                                        <a href="" id="diseaseSection" class="fa fa-question-circle pull-right"></a>
                                    </span>
                                </h2>
                                <div class="inner">
                                    <div id="phenotabs2" class="phenotabs">
                                        <ul class='tabs'>
                                            <li><a href="#by-annotation">By annotation and orthology</a></li>
                                            <li><a href="#by-phenotype">By phenotypic similarity</a></li>
                                        </ul>
                                        <div id="by-annotation">
                                            <c:choose>
                                                <c:when test="${!hasModelsByOrthology}">                                
                                                    No associations by disease annotation and gene orthology found.
                                                </c:when>
                                                <c:otherwise>
                                                    <table id="diseases_by_annotation" class="table tablesorter disease"></table>
                                                </c:otherwise>
                                            </c:choose>                                                                        
                                        </div>
                                        <div id="by-phenotype">
                                            <c:choose>
                                                <c:when test="${empty modelAssociations}">
                                                    No associations by phenotypic similarity found.
                                                </c:when>
                                                <c:otherwise>
                                                    <table id="diseases_by_phenotype" class="table tablesorter disease"></table>
                                                </c:otherwise>
                                            </c:choose>                                                                        
                                        </div>
                                    </div>
                                </div>
                            </div>



                            <div class="section" id="order2">
                                <h2 class="title documentation" id="order-panel">Order Mouse and ES Cells<span
                                        class="documentation"><a href='' id='orderSection' class="fa fa-question-circle pull-right"></a></span>
                                </h2>

                                <div class="inner">

                                    <jsp:include page="orderSectionFrag.jsp"></jsp:include>

                                    </div>
                                </div>
                                <!-- End of Order Mouse and ES Cells -->

                            </div>	<!--end of node wrapper: immediate container of all sections  -->
                        </div> <!-- end of content -->
                    </div> <!-- end of block -->
                </div> <!-- end of region content -->




                <!-- new anatomogram built using npm run dist -- --output-public-path /path/to/new anatomogram js/
                     this should take of of https -->
            <c:choose>
                <c:when test='${baseUrl.startsWith("/phenotype-archive")}'>
                    <%-- path: /phenotype-archive/js/newanatomogram/ --%>
                    <script type="text/javascript" src="${baseUrl}/js/newanatomogram/vendorCommons.bundle.local.js"></script>
                </c:when>
                <c:otherwise>
                    <%-- path: /data/js/newanatomogram/ --%>
                    <script type="text/javascript" src="${baseUrl}/js/newanatomogram/vendorCommons.bundle.js"></script>
                </c:otherwise>
            </c:choose>

            <script type="text/javascript" src="${baseUrl}/js/newanatomogram/anatomogram.bundle.js"></script>

            <%-- Block augmenting/filling phenodigm tables --%>
            <script type="text/javascript">
                var curatedDiseases = ${curatedDiseases};
                var modelAssociations = ${modelAssociations}; // this object name is required in phenodigm2.js                
            </script>
            <script type="text/javascript">
                // disease tables drive by phenodigm core
                var diseaseTableConfs = [
                    {
                        id: '#diseases_by_annotation',
                        tableConf: {
                            paging: false,
                            info: false,
                            searching: false,
                            order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                            sPaginationType: "bootstrap"
                        },
                        phenodigm2Conf: {
                            pageType: "genes",
                            gene: "${gene.mgiAccessionId}",
                            groupBy: "diseaseId",
                            filterKey: "diseaseId",
                            filter: curatedDiseases,
                            minScore: 0,
                            innerTables: true
                        }
                    },
                    {
                        id: '#diseases_by_phenotype',
                        tableConf: {
                            order: [[4, 'desc'], [3, 'desc'], [2, 'desc']],
                            pageLength: 20,
                            lengthMenu: [20, 50, 100],
                            sPaginationType: "bootstrap"
                        },
                        phenodigm2Conf: {
                            pageType: "genes",
                            gene: "${gene.mgiAccessionId}",
                            groupBy: "diseaseId",
                            filterKey: "diseaseId",
                            filter: [],
                            minScore: 1,
                            innerTables: true
                        }
                    }];
                    
                $(document).ready(function () {
                    // create phenodigm tables
                    for (var i = 0; i < diseaseTableConfs.length; i++) {
                        var dTable = diseaseTableConfs[i];                        
                        impc.phenodigm2.makeTable(modelAssociations, dTable.id, dTable.phenodigm2Conf);                        
                        var dataTable = $(dTable.id).DataTable(dTable.tableConf);                        
                        $.fn.addTableClickPhenogridHandler(dTable.id, dataTable);
                    }
                });
            </script>

            <%-- Block augmenting/filling anatomy content--%>
            <script type="text/javascript">
                $(document).ready(function () {

                    // --- new anatomogram as of 2017-07 ------
                    //console.log(${anatomogram});
                    // invoke anatomogram only when
                    // this check is not empty: impcAdultExpressionImageFacets

                    if ($('div#anatomogramContainer').size() == 1) {

                        // anatomogram stuff
                        //var expData = JSON.parse(${anatomogram});
                        var expData = ${anatomogram};
                        //console.log(expData);
                        var topLevelName2maIdMap = expData.topLevelName2maIdMap;
                        var maId2UberonEfoMap = expData.maId2UberonEfoMap;
                        var uberonEfo2MaIdMap = expData.uberonEfo2MaIdMap;
                        var maId2topLevelNameMap = expData.maId2topLevelNameMap;

                        var uberons2Gene = expData.allPaths;

                        mouseAnatomogram(uberons2Gene, [], []);

                        // top level MA term talks to anatomogram
                        $("ul#expList table td").on("mouseover", function () {
                            var topname = $(this).text().trim();
                            var maIds = topLevelName2maIdMap[topname];
                            //console.log(topname + " - " + maIds);
                            var uberonIds = [];
                            for (var a = 0; a < maIds.length; a++) {
                                uberonIds = uberonIds.concat(maId2UberonEfoMap[maIds[a]]);
                            }
                            uberonIds = $.fn.getUnique(uberonIds);
                            mouseAnatomogram(uberons2Gene, uberonIds, []);
                        }).on("mouseout", function () {
                            mouseAnatomogram(uberons2Gene, [], []);
                        });
                    }

                    //------------ end of new anatomogram ----------


                    function mouseAnatomogram(uberons2Gene, highlightIds, selectIds) {
                        anatomogram.render({
                            showColour: 'gray',
                            highlightColour: 'red',
                            selectColour: 'purple',
                            showOpacity: '0.3',
                            highlightOpacity: '0.6',
                            selectOpacity: '0.8',
                            species: 'mus_musculus',
                            showIds: uberons2Gene,
                            highlightIds: highlightIds,
                            selectIds: selectIds,
                            onMouseOver: function (id) {
                                var maIds = uberonEfo2MaIdMap[id];
                                var topLevelNames = [];
                                for (var i = 0; i < maIds.length; i++) {
                                    var tops = maId2topLevelNameMap[maIds[i]];
                                    for (var j = 0; j < tops.length; j++) {
                                        topLevelNames.push(tops[j]);
                                    }
                                }

                                topLevelNames = $.fn.getUnique(topLevelNames);
                                console.log("TOP: " + topLevelNames);

                                $('ul#expList table td.showAdultImage').each(function () {

                                    if ($.fn.inArray($(this).text().trim(), topLevelNames)) {
                                        console.log("top: " + $(this).text().trim());
                                        $(this).addClass("mahighlight");
                                    }
                                });
                            },
                            onMouseOut: function (id) {
                                $('ul#expList table td').removeClass("mahighlight");
                            },
                            onClick: function (id) {
                                console.log(id + " click")

                            }
                        }, 'anatomogramContainer')
                    }

                    $('.iFrameFancy').click(function ()
                    {
                        $.fancybox.open([
                            {
                                href: $(this).attr('data-url'),

                            }
                        ],
                                {
                                    'maxWidth': 1000,
                                    'maxHeight': 1900,
                                    'fitToView': false,
                                    'width': '100%',
                                    'height': '85%',
                                    'autoSize': true,
                                    'transitionIn': 'none',
                                    'transitionOut': 'none',
                                    'type': 'iframe',
                                    scrolling: 'auto'
                                });
                    }
                    );

                });


            </script>

        </jsp:body>

    </t:genericpage>
