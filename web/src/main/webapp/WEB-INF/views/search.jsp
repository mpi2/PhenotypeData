<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/${dataType}?kw=*">${dataTypeLabel}</a> &raquo; ${searchQuery}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
		<div class="region region-pinned"></div>
	</jsp:attribute>

	<jsp:body>

		<div id="myGenes">
			<a href="${riBaseUrl}/summary">My Genes</a>
		</div>

		<br />

		<div id='datasets'></div>

		<div id="tabs">
			<ul class="tabLabel">
				<li id="geneT"><a href="${baseUrl}/search/gene?kw=*">Genes</a></li>
				<li id="mpT"><a href="${baseUrl}/search/mp?kw=*">Phenotypes</a></li>
				<li id="diseaseT"><a href="${baseUrl}/search/disease?kw=*">Diseases</a></li>
				<li id="anatomyT"><a href="${baseUrl}/search/anatomy?kw=*">Anatomy</a></li>
				<li id="impc_imagesT"><a href="${baseUrl}/search/impc_images?kw=*&showImgView=false">Images</a></li>
				<!-- li id="imagesT"><a href="${baseUrl}/search/images?kw=*&showImgView=false">Images</a></li-->
				<li id="allele2T"><a href="${baseUrl}/search/allele2?kw=*&showImgView=false">Products</a></li>
			</ul>
		</div>

		<div id="geneTab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div id="filterHint">Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="gene">
									<span class="flabel">Genes</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>

						</div>
					</div>
				</div>

			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>

		<div id="mpTab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div class='filterHint'>Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="mp">
									<span class="flabel">Phenotypes</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>
						</div>
					</div>
				</div>

			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>

		<div id="diseaseTab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div class='filterHint'>Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="disease">
									<span class="flabel">Diseases</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>

		<div id="anatomyTab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div class='filterHint'>Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="anatomy">
									<span class="flabel">Anatomy</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>

		<div id="impc_imagesTab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div class='filterHint'>Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="impc_images">
									<span class="flabel">IMPC Images</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>
						</div>
					</div>
				</div>

			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>


		<div id="imagesTab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div class='filterHint'>Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="images">
									<span class="flabel">Images</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>
						</div>
					</div>
				</div>

			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>

		<div id="allele2Tab" class="hideme">
			<div class="region region-sidebar-first">

				<div class='facet' class='fblock block'>
					<div class='filterHint'>Filter <span class='documentation title textright'>
						<a href='' class='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</span>
					</div>
					<div class='content'>
						<div class='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<li class="fmcat" id="allele2">
									<span class="flabel">Products</span>
									<span class="fcount"></span>
									<ul></ul>
								</li>
							</ul>
						</div>
					</div>
				</div>

			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>
						<!-- container to display dataTable -->
						<div class="HomepageTable, mpi2-search"></div>
					</div>
				</div>
			</div>
			<div style="clear: both"></div>
		</div>
        <div id='saveTable'></div>

		<compress:html enabled="${param.enabled != 'false'}" compressJavaScript="true">
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchFacets.js?v=${version}'></script>
		</compress:html>

		<script>
			$(document).ready(function(){
				'use strict';

				var srchMsg;
				if ( window.location.search != '' ){
					srchMsg = "Results found by your search keyword are grouped in the following categories:";
				}
				else {
					srchMsg = "Results found are grouped in the following categories:";
				}
				$('#datasets').html(srchMsg);

				$.fn.qTip({'pageName':'search'});

				// populate facet counts to all "tabs"
				<%--$('ul.tabLabel li').each(function(){--%>
					<%--var id = $(this).attr('id').replace('T','');--%>
					<%--//if ( id == 'gene' ){id += '2'}  // count for protein coding gene only--%>

					<%--if (${facetCount}[id] == 0){--%>
						<%--$(this).addClass('noData');--%>
						<%--$(this).find('a').addClass('noData')--%>
					<%--}--%>

					<%--$(this).find('a').append("<span class='tabfc'> ("+${facetCount}[id]+")</span>");--%>
				<%--});--%>

				// so that we don't see the "tabs" appear w/o facet counts
				// because the counts are appended after those "tabs" markup are loaded
				$('ul.tabLabel li').css('visibility', 'visible');

//
//				$('ul.tabLabel li.noData').mouseover(function(){
//					// no background change for zero count dataset
//					//$(this).parent().css({'background-color':'white','border':'1px solid grey','border-bottom':'none'});
//				}).click(function(){
//					return false;
//				});

				$('div#resultMsg').css('border-top', '1px solid grey');

				var path = window.location.pathname.replace(baseUrl, '');
				var vals = path.split('/'); // [1]search, [2]corename

				var params = window.location.search; // includes leading "?"

				var query, queryOri, coreName, solrFilters;

				query = queryOri = "*"; // default

				var solrFqs = [];
				var showImgViewStr = "showImgView=false";  // default

				//---------------------- parse URL ----------------------------
				if ( /search\/?\w*\/?.*$/.exec(location.href) ){
					// with filter(s)

					var regex = /search\/?(\w*)\/?(.*)$/;
					var matches = location.href.match(regex);

					var filters = [];

					//query = matches[1];
					var hasFq = false;
					coreName = matches[1];

					if ( coreName == ""){
						coreName = "gene";
					}

					// activate this 'tab'
					//alert('div#' + coreName +'Tab')
					$('div#' + coreName +'Tab').show();

					var paramStr = matches[2].replace(/^\?/,'');
					//alert("paramStr: "+paramStr)

					var kw = paramStr.split("&");

					for ( var i=0; i<kw.length; i++ ){
						var pairs = kw[i].split("=");
						var k = pairs[0];
						var v = pairs[1];
						if( k == 'kw' ){
							query = queryOri = v;
						}
						else if ( k == 'showImgView' ){
							showImgViewStr = kw[i];

						}
						else if ( k == 'fq' ){
							hasFq = true;
							var solrFqStr = decodeURI(v.replace("fq=", "")).replace(/\)|\(/g, "");
							var fv = solrFqStr.split(" AND ");
							for (var j=0; j<fv.length; j++){
								var fv2 = fv[j].split(":");
								var fkey = fv2[0];
								var fval = fv2[1];
								//alert(fval)
								solrFqs.push(fkey+"|"+fval.replace(/"/g,""));
								if ( fkey == 'embryo_data_available' ){
									fval = '"' + fkey + '"';
								}
								else if ( fkey == 'legacy_phenotype_status' ){
									fval = '"Legacy"';
								}
								else  if ( fval == '"Phenotyping Complete"' ){
									fval = '"Approved"';
								}
								else if (coreName == 'disease' && fval == "\"true\"" ){
									fval = '"' + $.fn.labelMap(fkey) + '"';
								}

								filters.push(fval);
							}
						}
					}

					if ( filters.length > 0){
						solrFilters = filters.join(" AND ");
					}

					query = query.replace("\\%3A", ":");

					//$('input#s').val(decodeURI(query));

				}

				//---------------------- end of parse URL ----------------------------

				$("ul.tabLabel > li a").each(function(){
					$(this).removeClass('currDataType noData'); // reset
					$(this).parent().removeClass('noData'); // reset

					var thisId = $(this).parent().attr('id').replace("T","");

					// ----------- update "tab" url ---------------------

					var currKw = $.fn.fetchUrlParams('kw');

					if ( currKw == undefined ){
						query = "*";
					}
					// update url for all other datatypes (tabs)

					if ( thisId != coreName ) {
						//console.log("tab: " + thisId + " --- query: " + query);

						if ( query.indexOf(":") != -1 ){
							query = query.replace(":", "\\%3A");
						}
						//console.log("search.jsp: " + baseUrl + '/search/' + thisId + '?kw=' + query)

						if (${facetCount}[thisId] == 0) {
							$(this).attr('href', '');
							$(this).addClass('noData');
							$(this).parent().addClass('noData');
						}
						else {
							$(this).attr('href', baseUrl + '/search/' + thisId + '?kw=' + query);
						}

					}

					// ----------- highlights current "tab" and populates its facet filters and dataTable -----------
					if ( thisId == coreName ){

						// update "tab" link url
						if ( $.fn.fetchUrlParams('fq') != undefined ){
							$(this).attr('href', baseUrl + '/search/' + thisId + '?kw=' + query + '&fq=' + $.fn.fetchUrlParams('fq'));
						}
						else {
							if (${facetCount}[thisId] == 0) {
								$(this).attr('href', '');
							}
							else {
								$(this).attr('href', baseUrl + '/search/' + thisId + '?kw=' + query);
							}
						}

						$(this).parent().addClass('currDataType');//.click();

						$.fn.displayFacets(coreName, ${jsonStr});

						// check(highlight) filter(s) based on URL fq str
						if ( solrFqs.length != 0 ){
							highlightFilters(solrFqs);
						}

						var tabId = '#' + coreName + 'Tab';
						var parentContainer = $(tabId).find("div.mpi2-search");
						
						$(".activeFilter").removeClass("activeFilter");
						
						$(tabId).addClass("activeFilter");

						// images cores related
						if ( coreName.indexOf('images') != -1 ) {

							$('div.region-content').css({"position": "relative", "top": "-37px"});

							var foundMsg, switcher, viewMsg;
							if (showImgViewStr == "showImgView=false" ){

								viewMsg = '<div id="imgView" rel="annotView">'
										+ '<span id="imgViewSubTitle">Annotation View: groups images by annotation</span>'
										+ '<span id="imgViewSwitcher">Show Image View</span>';
								foundMsg = '<p>Found '
										+ '<span id="resultCount">'
										+ '<span id="annotCount">' + ${jsonStr}.iTotalRecords + ' annotations / </span>'
								+ '<a rel="nofollow" href="' + ${jsonStr}.imgHref + '">' + ${jsonStr}.imgCount + '</a> images'
								+ '</span></div>';
							}
							else {

								viewMsg = '<div id="imgView" rel="imgView">'
										+ '<span id="imgViewSubTitle">Image View: lists annotations to an image</span>'
										+ '<span id="imgViewSwitcher">Show Annotation View</span>';
								foundMsg = '<p>Found '
										+ '<span id="resultCount">'
										+ '<a rel="nofollow" href="' + ${jsonStr}.imgHref + '">' + ${jsonStr}.imgCount + '</a> images'
								+ '</span></div>';
							}
							switcher = viewMsg + foundMsg;

							parentContainer.append(switcher);

							// add js to switcher
							activateImgViewSwitcher();
						}
						else {
							$('div.region-content').css({"position": "relative", "top": "-50px"});
						}

						var tableId = "dTable";

						//console.log("${gridHeaderListStr}");
						prepare_dataTable("${gridHeaderListStr}", tableId, parentContainer);

						var infoDivId = tableId + "_info";
						var paginationDivId = tableId + "_pagination";

						var noSort = {}; // should match all existing columns
						noSort["gene"] = [1,2,3];
						noSort["mp"] = [0,1,2,3];
						noSort["disease"] = [1];
						noSort["anatomy"] = [1];
						noSort["images"] = [0,1];
						noSort["impc_images"] = [0,1];

						$('table#'+tableId).dataTable({
									"bSort" : false,  // sorts on current page only
									"bProcessing" : true,
									//"bServerSide" : true,
								//	"aaSorting" : [[0, "desc"]],
									"columnDefs": [ {
										"targets": noSort[coreName],
										"orderable": false
									} ],
									//"sDom" : "<<'#exportSpinner'><'#tableTool'>r>t<'#" + infoDivId + "'><'#" + paginationDivId + "'>",
									"sDom" : "<<'#exportSpinner'>r>t<'#" + infoDivId + "'><'#" + paginationDivId + "'>",
									"sPaginationType" : "bootstrap",
									"aaData" : ${jsonStr}.aaData,  // array of objects
								"iTotalRecords" : ${jsonStr}.iTotalRecords

						});

						// adjust col width
						adjustColWidth();

						// update pagination control
						addPaginationControl(parentContainer, infoDivId, paginationDivId, ${jsonStr});


						// do these only when there is result found
						if ( $('div#dTable_pagination li.active a').size() > 0 ) {
							// add Download
							addDownloadTool();

							// highlight synonyms
							highlighSynonym();

							// register interest js
							registerInterest();

							if (coreName == 'allele2'){
								decodeAlleleName();

								$('a.genbank, a.iFrameVector').qtip({
									content : {
										text : $(this).attr('title')
									},
									style : {
										classes : 'qtipimpc',
										tip : {
											corner : 'top right'
										}
									},
									position : {
										my : 'right top'
									}
								});
							}
						}
					}

                    _doProductFancyBox();

					$(this).append("<span class='tabfc'> ("+${facetCount}[thisId]+")</span>");

					$('li.noData').click(function(){
						return false;
					});

				});
				// ----------- highlights current "tab" and populates facet filters and dataTable -----------



				// submit query when facet filter is ticked
				fetchResultByFilters();

				//------------------------- FUNCTIONS ------------------------


                function registerInterest() {

                    $('a.regInterest').click(function () {

                        var anchorControl = $(this);
                        var divControl = $(anchorControl).parent();
                        var iconControl = $(anchorControl).prev();
                        var endpoint = $(anchorControl).attr('href');

                        var currentAnchorText = $(anchorControl).text();
                        var currentOldtitle = $(divControl).attr('oldtitle');

                        function riSuccess() {
                            if (currentAnchorText.trim().toUpperCase() === 'Unregister Interest'.toUpperCase()) {

                                // Unregister -> Register
                                currentOldtitle = currentOldtitle.replace('Unregister', 'Register');
                                $(divControl).attr('oldtitle', currentOldtitle);

                                $(iconControl).removeClass('fa-sign-out');
                                $(iconControl).addClass('fa-sign-in');

                                endpoint = endpoint.replace('ruUnregistration', 'riRegistration');
                                $(anchorControl).attr('href', endpoint);

                                currentAnchorText = currentAnchorText.replace('Unregister', 'Register');
                                $(anchorControl).text(currentAnchorText);

                            } else {

                                // Register -> Unregister
                                currentOldtitle = currentOldtitle.replace('Register', 'Unregister');
                                $(divControl).attr('oldtitle', currentOldtitle);

                                $(iconControl).removeClass('fa-sign-in');
                                $(iconControl).addClass('fa-sign-out');

                                endpoint = endpoint.replace('riRegistration', 'riUnregistration');
                                $(anchorControl).attr('href', endpoint);

                                currentAnchorText = currentAnchorText.replace('Register', 'Unregister');
                                $(anchorControl).text(currentAnchorText);
                            }
                        }

                        function riError() {
                            window.alert('Unable to access register interest service at this time.');
                        }




						if (endpoint.includes('login?target')) {
                            $.ajax({
                                url: endpoint,
                                dataType: 'jsonp',
                                beforeSend: setHeader,
                                // success: riSuccess,
                                error: riError
                            });
                        } else {
                            $.ajax({
                                url: endpoint,
								dataType: undefined,
								beforeSend: undefined,
                                success: riSuccess,
                                error: riError
                            });
						}

						return false;
                    });
                }


			function decodeAlleleName(){
				$('span.allelename').each(function(){
					$(this).html(decodeURI($(this).html()));
				});
			}

			function highlighSynonym(){

				// mouseover synonyms in results dataTable
				$('ul.synonym li, ul.hpTerms li, ul.ortholog li').mouseover(function() {
					$(this).addClass("highlight");
				}).mouseout(function() {
					$(this).removeClass("highlight");
				});
			}


			function addDownloadTool(){

				var vals = $('div#dTable_pagination li.active a').attr('href').split("?");
				var params = vals[1];

				var dataType = "dataType=" + coreName;
				var fileTypeTsv = "fileType=tsv";
				var fileTypeXls = "fileType=xls";
				var fileName = "fileName=" + coreName + "_table_dump";

				// only results from current page
				var paramList1 = [dataType, params, fileName];
				var paramStr1 = paramList1.join("&");
				//paramStr1 += "&mode=page";

				// all results in table
				var total = ${jsonStr}.iTotalRecords;
				var regex1 = /iDisplayStart=\d+/;
				var matches1 = params.match(regex1);
				var regex2 = /iDisplayLength=\d+/;
				var matches2 = params.match(regex2);
				var iStart = matches1[0];
				var iEnd = matches2[0];
				var paramStr2 = paramStr1.replace(iEnd, "iDisplayLength=" + total).replace(iStart, "iDisplayStart=0");
				paramStr2 += "&mode=all";


				//var urltsvC = "${baseUrl}/export2?" + paramStr1 + "&" + fileTypeTsv;
				//var urlxlsC = "${baseUrl}/export2?" + paramStr1 + "&" + fileTypeXls;

				var urltsvA = "${baseUrl}/export2?" + paramStr2 + "&" + fileTypeTsv;
				var urlxlsA = "${baseUrl}/export2?" + paramStr2 + "&" + fileTypeXls;
				
				console.log("urltsvA: " + urltsvA);
				
				var toolBox = '<span>Export table as: &nbsp;&nbsp;&nbsp;'
						+ '<a id="tsvA" class="fa fa-download gridDump" href="' + urltsvA + '">TSV</a>&nbsp;&nbsp;&nbsp;or&nbsp;&nbsp;&nbsp;'
						+ '<a id="xlsA" class="fa fa-download gridDump" href="' + urlxlsA + '">XLS</a></span>';
						//+ '<span>For more information, consider <a href=${baseUrl}/batchQuery>Batch search</a></span>';

                $("div#saveTable").html(toolBox);

				var cutoff = 10000;
				$("a#tsvA, a#xlsA").click(function(){
					if (total > cutoff){
						var r = confirm("It will take longer to download a bigger dataset. Please do not interrupt while downloading.\n\nProceed?");
						if (r !== true) {
							return false;
						}
						// when true the href fireds and do query in batch on server side
					}
				})
			}

			function adjustColWidth(){

				if ( coreName == 'disease' || coreName == 'gene' ) {
					$('table th:first-child').css('width', '45%');
				}
				else if ( coreName.indexOf('images') != -1 ) {
					$('table th:first-child').css('width', '30%');
				}
				else if ( coreName.indexOf('mp') != -1 ) {
					$('table th:nth-child(3)').css('width', '10%');
				}
				else if ( coreName.indexOf('anatomy') != -1 ) {
					$('table th:nth-child(2), table td:nth-child(2)').css('text-align', 'center');
				}
			}

			function activateImgViewSwitcher(){
				$('div#imgView').click(function(){
					var fqStr = $.fn.fetchUrlParams("fq") == undefined ? "" : "&fq=" + $.fn.fetchUrlParams("fq");
					var mode = $(this).attr('rel');

					if ( mode == 'annotView' ){
						document.location.href = baseUrl + '/search/' + coreName + "?kw=" + query + fqStr + "&showImgView=true";
					}
					else {
						document.location.href = baseUrl + '/search/' + coreName + "?kw=" + query + fqStr + "&showImgView=false";
					}
				});
			}


			function fetchResultByFilters(){
				$('li.fcat input').click(function () {

					// parse checked filters and build a SOLR fq str
					var fqs = [];
					$('li.fcat span.filterCheck').each(function () {
						var vals = $(this).prev().attr("rel").split("|");
						var fq = vals[1];
						var val = vals[2];
						fqs.push("(" + fq + ":\"" + val + "\")");
					});

					var fqStr = fqs.length != 0 ? "&fq=" + fqs.join(" AND ") : "";

					//document.location.href = baseUrl + '/search/' + query + '/' + coreName + fqStr;
					document.location.href = baseUrl + '/search/' + coreName + "?kw=" + queryOri + fqStr;
				});
			}

			function highlightFilters(solrFqs){

				for( var i=0; i<solrFqs.length; i++){

					$('div.flist li.fcat input').each(function() {

						if ($(this).attr("rel").indexOf(solrFqs[i]+"|") != -1) {
							$(this).next().click();

							// if there is a filter checked, open its container facet if not yet
							var container = $(this).parent().parent().parent();
							if ( ! container.hasClass('open') ){
								container.click();
							}
						}
					});
				}
			}

			function prepare_dataTable(colListStr, tableId, parentContainer){

				var colList = colListStr.split(',');

				var th = '';
				for ( var i=0; i<colList.length; i++){
					th += "<th>" + colList[i] + "</th>";
				}

				var tableHeader = "<thead>" + th + "</thead>";
				var tableCols = colList.length;

				var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, tableId);

				parentContainer.append(dTable);

			}

			function addPaginationControl(parentContainer, infoDivId, paginationDivId, json){
				//console.log("total records: " + json.iTotalRecords);

				var start  = json.iDisplayStart;
				var length = json.iDisplayLength;
				//var total  = json.iTotalRecords;
				var total = ${jsonStr}.iTotalRecords;

				var numX = total > 0 ? parseInt(start+1) : 0;
				var numY = parseInt(start+length) > total ? total : parseInt(start+length);
				var defaultRows = 10;
				var currPageNum = (start/length)+1;


				parentContainer.find('div#' + infoDivId).html("Showing " + numX + " to " + numY + " of " + total + " entries");

				var filters = solrFilters != undefined ? " filtered by " + solrFilters : "";
				//$('#resultMsg').html(numX + " to " + numY + " of " + total + " entries found for <b>\"" + decodeURI(query) + "\"</b>" + filters);

				// work out how many pages
				var pages = Math.ceil(total / length);
				var defaultPaginationLength = pages > 6 ? 5 : pages;
				var lis = [];
				var href = location.href;

				// work out correct url to append start and length for pagination
				if ( /search\/?$/.exec(location.href) ) {
					href = "search/gene?kw=*";
				}
				else if ( location.href.indexOf("&iDisplayStart") != -1 ){
					var pos = location.href.indexOf("&iDisplayStart");
					href = location.href.substr(0, pos);
				}


				var dLen = "&iDisplayLength=10";

				var currHref = href + "&iDisplayStart=0" + dLen;

				lis.push("<li><a href='" + currHref + "'>&larr; First</a>");

				var cycles, loopStart;
				if ( currPageNum < 4 ){
					// show pagination buttons starting from 1
					loopStart = 0;
					cycles = defaultPaginationLength;
				}
				else {
					// show pagination buttons starting > 1 (higher numbers)
					// always display 5 buttons (ie, if there is a total of 12 pages, click on either one of the 11/12 numbers
					// will always show 8,9,10,11,12,
					// but click on 9 will show 7,8,9.10,11..12
					// and click on 10 will show 8,9,10,11,12
					loopStart = currPageNum > pages-4 ? pages-5 : currPageNum - 3;
					cycles = defaultPaginationLength  + loopStart;
				}

				var fifthNum = undefined;
				for (var i = loopStart; i < cycles; i++) {
					currHref = href + "&iDisplayStart=" + parseInt(i * length) + dLen;
					var sClass = i + 1 == currPageNum ? "active" : "";
					if ( i < pages ) {
						lis.push("<li class='" + sClass + "'><a href='" + currHref + "'>" + parseInt(i + 1) + "</a>");
					}
					fifthNum = i+1;
				}

				if (pages > fifthNum ) {
					var sClass = pages == currPageNum ? "active" : "";
					currHref = href + "&iDisplayStart=" + parseInt((pages - 1) * length) + dLen;
					lis.push("<li><span class='ellipse'>...</span></li>");
					lis.push("<li class='" + sClass + "'><a href='" + currHref + "'>" + pages + "</a>");
				}
				else {

				}

				currHref = href + "&iDisplayStart=" + parseInt((pages-1)*length) + dLen;
				lis.push("<li><a href='" + currHref + "'>Last &rarr;</a>");

				parentContainer.find('div#' + paginationDivId).html("<ul>"+ lis.join("") + "</ul>").addClass("dataTables_paginate paging_bootstrap pagination");

			}

		});

        function _doProductFancyBox(){
            $('.iFrameFancy').click(function()
                    {
                      $.fancybox.open([
                                    {
                                        href : $(this).attr('data-url'),
                                        
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
			$('.iFrameVector').click(function()	{
				$.fancybox.open([
							{
								href : $(this).attr('data-url'),
								title : 'Vector map'
							}
						],
						{
							'maxWidth'          : 1000,
							'maxHeight'         : 500,
							'fitToView'         : false,
							'width'             : '100%',
							'height'            : '40%',
							'autoSize'          : false,
							'transitionIn'      : 'none',
							'transitionOut'     : 'none',
							'type'              : 'iframe',
							scrolling           : 'auto'
						});
			});

        }



		</script>

	</jsp:body>

</t:genericpage>


