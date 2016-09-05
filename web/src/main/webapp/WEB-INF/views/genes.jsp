<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">Gene details for ${gene.markerName}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/searchoverview">Search overview &raquo; </a><a
			href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
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


						<li><a href="#section-expression">Expression</a></li>

						<%--<c:if test="${not empty impcImageFacets}">--%>
							<li><a href="#section-images">Associated Images</a></li>
						<%--</c:if>--%>

						<%--<c:if test="${not empty orthologousDiseaseAssociations}">--%>
							<li><a href="#section-disease-models">Disease Models</a></li>
						<%--</c:if>--%>

						<%--<c:if test="${!countIKMCAllelesError}">--%>
							<li><a href="#order2">Order Mouse and ES Cells</a></li>
						<%--</c:if>--%>
						</ul>

						<div class="clear"></div>

					</div>

				</div>
				<!--  end of floating menu for genes page -->

			</jsp:attribute>


		<jsp:attribute name="header">

				<!-- JavaScript Local Imports -->

				<script src="${baseUrl}/js/general/enu.js"></script>
				<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
				<script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>

				<script type="text/javascript">
					var gene_id = '${acc}';

					$(document).ready(function() {
						var heatmap_generated=0;
						var expressionTab = 0;
						var hash = location.hash;
						if (hash.indexOf("tabs-") > -1){
							expressionTab = $('a[href="' + hash + '"]').parent().index();
							$("#section-expression").focus();
						}
						
						$("#exptabs").tabs({ active: expressionTab});
						$("#diseasetabs").tabs({ active: 0 });

						//$("#diseasetabs").find("td[class!='shown']").css('color','#666');

						$('div#anatomo1').hide(); // by default

						$('.wtExp').hide();
						$('div#toggleWt').click(function(){
							if ($('.wtExp').is(':visible')) {
								$('.wtExp').hide();
								$(this).text("Show Wildtype Expression");
							}
							else {
								$('.wtExp').show();
								$(this).text("Hide Wildtype Expression");
							}
						});

						$('div#expDataView').click(function(){
							if ($('#anatomo1').is(':visible')) {
								$('#anatomo1').hide();
								$('#anatomo2').show();
								$(this).text("Show expression table");
							}
							else {
								$('#anatomo1').show();
								$('#anatomo2').hide();
								$(this).text("Hide expression table");
							}
						});
						
						$('#heatmap_link').click(function(){
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

							if($('#heatmap_toggle_div').length){//check if this div exists first as this will ony exist if phenotypeStarted and we don't want to do this if not.
								$('#heatmap_toggle_div').toggleClass('hidden');//toggle the div whether the heatmap has been generated or not.
								$('#phenotypeTableDiv').toggleClass('hidden');
								if(!heatmap_generated){

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
										heatmap_generated=1;

									};


								document.head.appendChild(script);

		

								}//end of if heatmap generated

						}
							


						});
						 
					});
				</script>
				<style>
					li.showAdultImage {
						cursor: pointer;
					}
					div#expDataView, div#toggleWt {
						font-size: 12px;
						color: #0978a1;
						background-color: white;
						border: 0;
						border-radius: 4px;
						padding: 3px 5px;
						cursor: pointer;
					}
					div#expDataView {
						float: right;
						margin-bottom: 20px;
					}
					#svgHolder div div {
						z-index: 100;
					}

					span.direct {
						color: #698B22;
					}

					span.indirect {
						color: #CD8500;
					}

					div.ui-tabs {
						border: none;
						width: 100%;
					}
					div#exptabs > ul, div#diseasetabs > ul {
						border: none;
						border-bottom: 1px solid #666;
						border-radius: 0;
						padding-bottom: 2px;
						margin-bottom: 0px;
						background: none;
						list-style-type: none;
					}
					div#exptabs > ul li, div#diseasetabs > ul li {
						float: left;
						border: none;
						background: none;
					}

					div#exptabs > ul li a, div#diseasetabs > ul li a {
						margin: 0 0px -3px 20px;
						border: 1px solid grey;
						border-bottom: none;
						font-size: 14px;
						text-decoration: none;
						padding: 3px 8px;
						border-radius: 4px;
						color: gray;
						font-family: 'Source Sans Pro', Arial, Helvetica, sans-serif;
					}
					#exptabs .ui-tabs-active, #diseasetabs .ui-tabs-active {
						border: none;
						background: none;
					}
					#exptabs .ui-tabs-active > a, #diseasetabs .ui-tabs-active a {
						border-bottom: 1px solid white;
						color: black;
					}
					.ui-tabs-panel {
						margin-top: 10px;
					}
					div#exptabs > ul li a:hover, div#diseasetabs > ul li a:hover {
						background: none;
					}

					.acontainer {
						height: auto;
						overflow: hidden;
					}

					.aright {
						width: 200px;
						float: left;
						text-align: center;
						margin-top: -45px;
					}

					.aleft {
						float: none;
						background: white;
						width: auto;
						overflow: hidden;
						margin-top: 0px;
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

					/* override the anatomogram .ui-widget font */

					div.ui-dropdownchecklist-item, div#diseasetabs.ui-widget, div#exptabs.ui-widget {
						font-family: 'Source Sans Pro', Arial, Helvetica, sans-serif;
						font-size: 1.0em;
						color: #333;
					}

					div.ui-dropdownchecklist-dropcontainer.ui-widget-content {
						border: none;
					}

					div.ui-dropdownchecklist-dropcontainer-wrapper.ui-widget.filtersMoreLikeNicolas {
						overflow: auto;
						min-width: 21%;
						max-height: 18em;
						white-space: nowrap;
						margin: 0 0 0 -1px;
						border: none;
					}
					/*div.ui-dropdownchecklist-dropcontainer-wrapper.ui-widget.filtersMoreLikeNicolas.open {*/
						/*border: 1px solid gray;*/
					/*}*/

					div.ui-dropdownchecklist-selector {
						background: none;
						background-color: lightgray;
						border: none;
					}
					div.ui-dropdownchecklist-selector:hover {
						background-color: white;
						border: none;
					}

					div.ui-dropdownchecklist-item.ui-state-default {
						background: none;
						border: none;
						font-weight: normal;
						padding: 5px 10px;
					}

					div#genes_wrapper th, div#diseasetabs th, div#exptabs th, div#allele2 th,
					div#genes_wrapper td, div#diseasetabs td, div#exptabs td, div#allele2 td {
						color: #666;
						padding: 0.5em 0.7em 0.5em 0.2em;

					}
					div#diseasetabs a, div#exptabs a {
						color: #0978a1;
					}
					div#diseasetabs div.dataTables_paginate li a {
						color: #666;
					}
					div#diseasetabs div.dataTables_paginate a:hover {
						color: white;
					}
					div#diseasetabs div.dataTables_paginate li.active a {
						color: white;
					}
					div.gxaAnatomogram {
						margin-top: 50px;
					}
					ul#expList li a.mahighlight {
						color: #E2701E;
					}
					
					/*li.ui-menu-divider {*/
						/*display: none;*/
					/*}*/
                        #fancybox-close {
	position: absolute;
	top: -15px;
	right: -15px;
	width: 30px;
	height: 30px;
	background: transparent url('fancybox.png') -40px 0px;
	cursor: pointer;
	z-index: 51103;
	display: none;
}


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
										<span class="documentation"><a
												href='' id='phenoAssocSection' class="fa fa-question-circle pull-right"></a></span>
									<!--  this works, but need js to drive tip position -->
								</h2>

								<div class="inner">
									<jsp:include page="genesPhenotypeAssociation_frag.jsp"/>
									
									
								</div>

							</div>
							<!-- end of Phenotype Associations -->


							

							<c:if test="${not empty imageErrors}">
								<div class="row-fluid dataset">
									<div class="alert">
										<strong>Warning!</strong>${imageErrors }</div>
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
									<c:if test="${empty impcAdultExpressionImageFacets
										and empty expressionAnatomyToRow
										and empty impcEmbryoExpressionImageFacets
										and empty embryoExpressionAnatomyToRow
										and empty expressionFacets}">
										<div class="alert alert_info">Expression data not available</div>
									</c:if>


									<c:if test="${not empty impcAdultExpressionImageFacets
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
															No expression data was found
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
													<c:when test="${not empty impcAdultExpressionImageFacets}">
														<div id="tabs-3">
															<jsp:include page="genesAdultLacZ+ExpImg_frag.jsp"></jsp:include>
														</div>
													</c:when>
													<c:otherwise>
														<div id="tabs-3">
															No expression image was found
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
															No expression data was found
														</div>
													</c:otherwise>
												</c:choose>
												<c:choose>
													<c:when  test="${not empty impcEmbryoExpressionImageFacets}">
														<div id="tabs-5">
															<jsp:include page="genesEmbExpImg_frag.jsp"></jsp:include>
														</div>
													</c:when>
													<c:otherwise>
														<div id="tabs-5">
															No expression image was found
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
								<h2 class="title" id="section-images">Phenotype Associated Images new
									<span class="documentation"><a href="" id="phenoAssocImgSection" class="fa fa-question-circle pull-right"></a></span>
								</h2>

								<div class="inner" style="display: block;">
									<c:if test="${empty impcImageGroups and empty solrFacets}">
										<div class="alert alert_info">Phenotype associated images not available</div>
									</c:if>

									<c:if test="${not empty impcImageGroups or not empty solrFacets}">
										<c:if test="${not empty impcImageGroups}">
											<h5 class="sectHint">IMPC Phenotype Associated Images</h5>
											<jsp:include page="genesImpcImagesAssocFrag.jsp"></jsp:include>
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

						<!--Disease Sections-->
						<div class="section">
							<h2 class="title" id="section-disease-models">Disease Models

								<a target="_blank" href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
                                <span class="documentation">
	                                <a href="" id="diseaseSection" class="mpPanel">
		                                <i class="fa fa-question-circle pull-right"></i>
	                                </a>
                                </span>
							</h2>

							<div class="inner">
								<c:choose>
									<c:when test="${not empty orthologousDiseaseAssociations || not empty phenotypicDiseaseAssociations}">

										<!-- section for expression data here -->
										<div id="diseasetabs">
											<ul class='tabs'>
												<li><a href="#dtabs-1">By gene orthology</a></li>
												<li><a href="#dtabs-2">By phenotypic similarity</a></li>
											</ul>
											<div id="dtabs-1">
												<c:choose>
													<c:when test="${not empty orthologousDiseaseAssociations}">
														<table id="orthologous_diseases_table" class="table tableSorter disease">
															<jsp:include page="genesOrthologousDiseasesTable_frag.jsp"></jsp:include>
														</table>
													</c:when>
													<c:otherwise>
														<p>No disease model association by gene orthology was found</p>
													</c:otherwise>
												</c:choose>
											</div>
											<div id="dtabs-2">
												<c:choose>
													<c:when test="${not empty phenotypicDiseaseAssociations}">
														<table id="predicted_diseases_table" class="table tableSorter disease">
															<jsp:include page="genesPredictedDiseasesTable_frag.jsp"></jsp:include>
														</table>
													</c:when>
													<c:otherwise>
														<p>No disease model association by phenotypic similarity was found</p>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="alert alert_info">Human disease model not found</div>
									</c:otherwise>
								</c:choose>
							</div><!-- end of inner -->
						</div><!-- end of Disease -->


						<!-- End of Order Mouse and ES Cells -->
						
						
						<div class="section" id="order2">
							<h2 class="title documentation" id="order-panel">Order Mouse and ES Cells<span
									class="documentation"><a href='' id='orderSection' class="fa fa-question-circle pull-right"></a></span>
							</h2>

							<div class="inner">
								
								<jsp:include page="orderSectionFrag.jsp"></jsp:include>
								
							</div>
						</div>

					</div>	<!--end of node wrapper: immediate container of all sections  -->
				</div> <!-- end of content -->
			</div> <!-- end of block -->
		</div> <!-- end of region content -->

		<script type="text/javascript" src="${baseUrl}/js/phenodigm/diseasetableutils.js?v=${version}"></script>
		<script type="text/javascript" src="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.js?v=${version}"></script>
		<link rel="stylesheet" type="text/css" href="${baseUrl}/js/phenogrid-1.3.1/dist/phenogrid-bundle.css?v=${version}">

		<%-- these copies have the http:// changed to // --%>
		<script type="text/javascript" src="${baseUrl}/js/vendorCommons.bundle.js?v=${version}"></script>
		<script type="text/javascript" src="${baseUrl}/js/expressionAtlasAnatomogram.bundle.js?v=${version}"></script>

		<%--reinvoke this when atlas people are ready supporting https--%>
		<%--<script language="JavaScript" type="text/javascript" src="//www.ebi.ac.uk/gxa/resources/js-bundles/vendorCommons.bundle.js"></script>--%>
		<%--<script language="JavaScript" type="text/javascript" src="//www.ebi.ac.uk/gxa/resources/js-bundles/expressionAtlasAnatomogram.bundle.js"></script>--%>


		<script type="text/javascript">
			var diseaseTables = [{
				id: '#orthologous_diseases_table',
				tableConf: {
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

				console.log(${anatomogram});
				// invoke anatomogram only when
				// this check is not empty: impcAdultExpressionImageFacets

				 if ($('div#anatomogramContainer').size() == 1) {

					// anatomogram stuff
					//var expData = JSON.parse(${anatomogram});
					var expData = ${anatomogram};
					var topLevelName2maIdMap = expData.topLevelName2maIdMap;
					var maId2UberonMap = expData.maId2UberonMap;
					var uberon2MaIdMap = expData.uberon2MaIdMap;
					var maId2topLevelNameMap = expData.maId2topLevelNameMap;

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

					//console.log(profileRows);

					var eventEmitter = expressionAtlasAnatomogram.eventEmitter;

					expressionAtlasAnatomogram.render(
							document.getElementById("anatomogramContainer"),
							anatomogramData,
							profileRows,
							"grey",
							"red"
							// "vader" is equivalent to <link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/vader/jquery-ui.css">
					);

					// top level MA term talks to anatomogram
					$("ul#expList li a").on("mouseover", function() {
						var topname = $(this).text();
						var maIds = topLevelName2maIdMap[topname];
						//log(topname + " - " + maIds);
						var uberonIds = [];
						for( var a=0; a<maIds.length; a++){
							uberonIds = uberonIds.concat(maId2UberonMap[maIds[a]]);
						}
						uberonIds = $.fn.getUnique(uberonIds);

						//console.log(topname + " : " + uberonIds);

						eventEmitter.emit("gxaHeatmapColumnHoverChange", uberonIds[0]);
						//eventEmitter.emit("gxaHeatmapColumnHoverChange", "UBERON_0000955"); // test for brain
					}).on("mouseout", function(){
						eventEmitter.emit("gxaHeatmapColumnHoverChange", "");
					});

					// anatomogram tissue talks to MA list
					eventEmitter.addListener("gxaAnatomogramTissueMouseEnter", function(e) {
						//console.log(e)

						var maIds = uberon2MaIdMap[e];
						var topLevelNames = [];
						for( var i=0; i<maIds.length; i++) {
							var tops = maId2topLevelNameMap[maIds[i]];
							for (var j=0; j<tops.length; j++){
								topLevelNames.push(tops[j]);
							}
						}

						topLevelNames = $.fn.getUnique(topLevelNames);

						$('ul#expList li a').each(function () {
							if ($.fn.inArray($(this).text(), topLevelNames)) {
								$(this).addClass("mahighlight");
							}
						});

					});
					eventEmitter.addListener("gxaAnatomogramTissueMouseLeave", function(e) {
						$('ul#expList li a').removeClass("mahighlight");
					});
				} 

				$("img.ui-button").each(function () {
					// hide brain toggle for now
					if ($(this).attr('src').indexOf('brain') != -1) {
						$(this).hide();
					}
				});
				
				$('.iFrameFancy').click(function()
						{
				 			$.fancybox.open([ 
				                  {
				                     href : $(this).attr('data-url'), 
				                     title : 'Order Products'
				                  } 
				                  ], 
				                   { 
				                     'maxWidth'          : 1000, 
				                     'maxHeight'         : 1900, 
				                     'fitToView'         : false, 
				                     'width'             : '100%',  
				                     'height'            : '85%',  
				                     'autoSize'          : false,  
				                     'transitionIn'      : 'none', 
				                     'transitionOut'     : 'none', 
				                     'type'              : 'iframe', 
				                     scrolling           : 'auto' 
				                  }); 
						}
				 	);

			});


		</script>

	</jsp:body>

</t:genericpage>
