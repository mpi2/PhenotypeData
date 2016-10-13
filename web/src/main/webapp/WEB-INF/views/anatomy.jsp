<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${anatomy.getAnatomyId()} (${anatomy.getAnatomyTerm()}) | IMPC anatomy Information</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/anatomy?kw=*">anatomy</a> &raquo; ${anatomy.getAnatomyTerm()}</jsp:attribute>
	<jsp:attribute name="header">
        <link rel="stylesheet" href="${baseUrl}/css/treeStyle.css">

		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
		<script type="text/javascript" src="${baseUrl}/js/vendorCommons.bundle.js?v=${version}"></script>
		<script type="text/javascript" src="${baseUrl}/js/expressionAtlasAnatomogram.bundle.js?v=${version}"></script>
       	    	
	</jsp:attribute>
	
    <jsp:attribute name="addToFooter">
    	
    	<script type="text/javascript">
			// Stuff dor parent-child. Will be used in parentChildTree.js.
			var ont_id = '${anatomy.getAnatomyId()}';
			var ontPrefix = "anatomy";
			var page = "anatomy";
			var hasChildren = ${hasChildren};
			var hasParents = ${hasParents};
		</script>
		   	
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>		
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>	
		<script type="text/javascript" src="${baseUrl}/js/parentChildTree.js"></script>	
		
		<div class="region region-pinned">
            
        <div id="flyingnavi" class="block smoothScroll">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Anatomy Term</a></li>
                <li><a href="#expression">LacZ Expression</a></li>
                <li><a href="#phenotypes">Associated Phenotypes</a></li>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
		
    </jsp:attribute>

                
    <jsp:body>
    
 		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">						
						<h1 class="title" id="top">Anatomy: ${anatomy.getAnatomyTerm()}</h1>

						<div class="section">
							<div class="inner">
								<div class="half">
									<c:if test="${fn:length(anatomy.getAnatomyTermSynonym()) > 0 }">

										<div id="synonyms" class="with-label"> <span class="label">Synonyms</span>

											<c:if test='${fn:length(anatomy.getAnatomyTermSynonym()) gt 1}'>
												<ul>
													<c:forEach var="synonym" items="${anatomy.getAnatomyTermSynonym()}" varStatus="loop">
														<li>${synonym}</li>
													</c:forEach>
												</ul>
											</c:if>
											<c:if test='${fn:length(anatomy.getAnatomyTermSynonym()) == 1}'>

												<c:forEach var="synonym" items="${anatomy.getAnatomyTermSynonym()}" varStatus="loop">
													${synonym}
													<%--<c:if test="${!loop.last}">,&nbsp;</c:if>--%>
												</c:forEach>

											</c:if>
										</div>

									</c:if>
									<p class="with-label"> <span class="label">Stage </span>
										<c:if  test='${anatomy.getAnatomyId().startsWith("MA:")}'>adult</c:if>
										<c:if  test='${anatomy.getAnatomyId().startsWith("EMAPA:")}'>embryo</c:if>
									</p>

								</div>

								<div id="parentChild" class="half">
									<h4>Browse mouse anatomy ontology</h4>
									<c:if test="${hasChildren && hasParents}">
										<div class="half" id="parentDiv"></div>
										<div class="half" id="childDiv"></div>
									</c:if>
									<c:if test="${hasChildren && !hasParents}">
										<div id="childDiv"></div>
									</c:if>
									<c:if test="${!hasChildren && hasParents}">
										<div id="parentDiv"></div>
									</c:if>
								</div>

								<div class="clear"></div>
							</div>
						</div>


							
							<div class="section" id="expression"> 
							
								<h2 class="title">Reporter gene expression associated with ${anatomy.getAnatomyTerm()}</h2>
									<div class="inner">
										<div class="container span12">
										<div id="spinner"></div>
										  <div id="filterParams" >
							                     <c:forEach var="filterParameters" items="${paramValues.fq}">
							                         ${filterParameters}
							                     </c:forEach>
					                      </div> 
						                  <c:if test="${not empty phenoFacets}">
						                     <form class="tablefiltering no-style" id="target" action="destination.html">
						                        <c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
						                             <select id="${phenoFacet.key}" class="impcdropdown" multiple="multiple" title="Filter on ${phenoFacet.key}">
						                                  <c:forEach var="facet" items="${phenoFacet.value}">
						                                       <option>${facet}</option>
						                                  </c:forEach>
						                             </select> 
						                        </c:forEach>
						                        <div class="clear"></div>
						                     </form>
						                 </c:if>
					                 	<jsp:include page="anatomyFrag.jsp"></jsp:include>						 
									</div>
						    	</div>
							</div>	
				 
							<c:if test="${genesTested > 0}">
						 		<div class="section" id="phenotypes"> 
									<h2 class="title">Phenotypes associated with ${anatomy.getAnatomyTerm()}</h2>
									<div class="inner">
										<div id="phenotypesByAnatomy" class="onethird"><script type="text/javascript">${pieChartCode}</script></div>
										<div class="clear both"> </div>
					 					<c:if test="${phenotypeTable.size() > 0}">
											<div class="container span12">
							                	<jsp:include page="anatomyPhenFrag.jsp"></jsp:include>						 
											</div>
										</c:if>
										<c:if test="${phenotypeTable.size() == 0}">
											<div class="container info"> No significant phenotype associations found. </div>
										</c:if>
								    </div>
								</div>
							</c:if>				 			
							<c:if test="${not empty expressionImages && fn:length(expressionImages) !=0}">
								<div class="section">
									<h2 class="title"> Expression images from the WellcomeTrust's MGP </h2>
									<div class=inner>
										<div class="accordion-group">
			              					<div class="accordion-heading">Expression Associated Images</div>
											<div class="accordion-body">
							    				<ul>                                    
							    					<c:forEach var="doc" items="${expressionImages}">
			                   							<li class="span2">
															<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
			                      						</li>
			                   						 </c:forEach>                              
												</ul>
											
												<c:if test="${numberExpressionImagesFound>5}">
			                   						<p class="textright">
														<a href='${baseUrl}/images?anatomy_id=${anatomy.getAnatomyId()}&fq=expName:Wholemount Expression'><i class="fa fa-caret-right"></i>show all ${numberExpressionImagesFound} images</a>
													</p>
												</c:if>
											</div>
										</div>
									</div>
								</div>
							</c:if>
				 
				</div>
			</div>
		</div>
	</div>

		
	<script>
	$(document).ready(function(){						
					
			initAnatomyDataTable();
			
			var selectedFilters = "";
			var dropdownsList = new Array();
			
		  function initAnatomyDataTable(){
			  
				var aDataTblCols = [0,1,2,3,4,5,6,7,8];
				console.log("initialising anatomy table");
				$('table#anatomy').dataTable( {
					"order": [[ 7, 'desc' ], [ 0, 'asc' ]],/*  order by desc number of images and then gene symbol alphabetically on startup */
							"bDestroy": true,
							"bFilter":false,
							"bPaginate":true,
				      "sPaginationType": "bootstrap"
					});
		  			  
		  
				  $('table#phenotypeAnatomy').dataTable( {
						"aoColumns": [
						              { "sType": "html"},
						              { "sType": "html"},
						              { "sType": "html"}
						              ],
							"bDestroy": true,
							"bFilter":true,
							"bPaginate":true,
				      "sPaginationType": "bootstrap"
					});
		  }
			
			function refreshAnatomyTable(newUrl){
				$.ajax({
					url: newUrl,
					cache: false
				}).done(function( html ) {
					$("#anatomy_wrapper").html(html);
					initAnatomyDataTable();
					console.log('stoping spinner now');
					$('#spinner').html('');
				});
			}
			

			//function to fire off a refresh of a table and it's dropdown filters
			var selectedFilters = "";
			var dropdownsList = new Array();
			
			var allDropdowns = new Array();
			allDropdowns[0] = $('#anatomy_term');
			allDropdowns[1] = $('#procedure_name');
			allDropdowns[2] = $('#parameter_association_value');
			allDropdowns[3] = $('#phenotyping_center');
			createDropdown(allDropdowns[3], "Source: All", allDropdowns);
			createDropdown(allDropdowns[0],"Anatomy: All", allDropdowns);
			createDropdown(allDropdowns[1], "Procedure: All", allDropdowns);
			createDropdown(allDropdowns[2], "Expression: All", allDropdowns);
			
			function createDropdown(multipleSel, emptyText,  allDd){
				
				$(multipleSel).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
					minWidth: 150, onItemClick: function(checkbox, selector){
						console.log("IN dropdownchecklist");
						var justChecked = checkbox.prop("checked");
						console.log("justChecked="+justChecked);
						console.log("clicked="+ checkbox.val());
						var values = [];
						for(var  i=0; i < selector.options.length; i++ ) {
							if (selector.options[i].selected && (selector.options[i].value != "")) {
								values .push(selector.options[i].value);
							}
						}

						if(justChecked){				    		 
							values.push( checkbox.val());
						}else{//just unchecked value is in the array so we remove it as already ticked
							var index = $.inArray(checkbox.val(), values);
							values.splice(index, 1);
						}  
						
						console.log("values="+values );
						// add current one and create drop down object 
						dd1 = new Object();
						dd1.name = multipleSel.attr('id'); 
						dd1.array = values; // selected values
						
						dropdownsList[0] = dd1;
						
						var ddI  = 1; 
						for (var ii=0; ii<allDd.length; ii++) { 
							if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
								dd = new Object();
								dd.name = allDd[ii].attr('id'); 
								dd.array = allDd[ii].val() || []; 
								dropdownsList[ddI++] = dd;
							}
						}
						refreshAnatomyFrag(dropdownsList);
					}, textFormatFunction: function(options) {
						var selectedOptions = options.filter(":selected");
				        var countOfSelected = selectedOptions.size();
				        var size = options.size();
				        var text = "";
				        if (size > 1){
				        	options.each(function() {
			                    if ($(this).prop("selected")) {
			                        if ( text != "" ) { text += ", "; }
			                        /* NOTE use of .html versus .text, which can screw up ampersands for IE */
			                        var optCss = $(this).attr('style');
			                        var tempspan = $('<span/>');
			                        tempspan.html( $(this).html() );
			                        if ( optCss == null ) {
			                                text += tempspan.html();
			                        } else {
			                                tempspan.attr('style',optCss);
			                                text += $("<span/>").append(tempspan).html();
			                        }
			                    }
			                });
				        }
				        switch(countOfSelected) {
				           case 0: return emptyText;
				           case 1: return selectedOptions.text();
				           case options.size(): return emptyText;
				           default: return text;
				        }
					}
				} );
			}
			
			//if filter parameters are already set then we need to set them as selected in the dropdowns
			var previousParams=$("#filterParams").html();
			
			function refreshAnatomyFrag(dropdownsList) {
				console.log('starting spinner now');
				 $("#spinner").html('<i class="fa fa-refresh fa-spin"></i>');
				var rootUrl = window.location.href;
				var newUrl = rootUrl.replace("anatomy", "anatomyFrag").split("#")[0];
				newUrl += '?';
				selectedFilters = "";
				for (var it = 0; it < dropdownsList.length; it++){
					if(dropdownsList[it].array.length == 1){//if only one entry for this parameter then don't use brackets and or
						selectedFilters += '&' + dropdownsList[it].name + '=' + dropdownsList[it].array;
					} 
					if(dropdownsList[it].array.length > 1)	{
						selectedFilters += '&' + dropdownsList[it].name + '=' + dropdownsList[it].array.join('&' + dropdownsList[it].name + '=');
					}			    			 
				}
				newUrl += selectedFilters;
				refreshAnatomyTable(newUrl);
				return false;
			}


			// Ignore adding anatomogram to anatomy page for now
		    // There are too many MA terms that anatomogram does not cover.
		    // The EMPAS terms are not supported by anatomogram as well.
			<%--console.log('${anatomogram}');--%>

			<%--if (Object.keys('${anatomogram}')) {--%>

				<%--// anatomogram stuff--%>
				<%--var expData = JSON.parse('${anatomogram}');--%>

				<%--var maId2MaNameMap       = expData.maId2MaNameMap;--%>
				<%--var maName2maIdMap       = expData.maName2maIdMap;--%>
				<%--var topLevelName2maIdMap = expData.topLevelName2maIdMap;--%>
				<%--var maId2UberonEfoMap       = expData.maId2UberonEfoMap;--%>
				<%--var uberonEfo2MaIdMap       = expData.uberonEfo2MaIdMap;--%>
				<%--var maId2topLevelNameMap = expData.maId2topLevelNameMap;--%>


                <%--if (! $.isEmptyObject(maId2UberonEfoMap)){--%>

                    <%--var anatomogramData = {--%>

                        <%--"maleAnatomogramFile": "mouse_male.svg",--%>
                        <%--"toggleButtonMaleImageTemplate": "/resources/images/male",--%>
                        <%--"femaleAnatomogramFile": "mouse_female.svg",--%>
                        <%--"toggleButtonFemaleImageTemplate": "/resources/images/female",--%>
                        <%--//"brainAnatomogramFile": "mouse_brain.svg",--%>
                        <%--//"toggleButtonBrainImageTemplate": "/resources/images/brain",--%>

                        <%--// all tested tissues (expressed + tested but not expressed)--%>
                        <%--"allSvgPathIds": expData.allPaths,--%>
                        <%--// test only--%>
                        <%--//"allSvgPathIds": [],--%>
                        <%--//"allSvgPathIds": ["UBERON_0000029", "UBERON_0001736", "UBERON_0001831"], // lymph nodes--%>
                        <%--//"allSvgPathIds": ["UBERON_0000947", "UBERON_0001981", "UBERON_0001348", "UBERON_0001347", "EFO_0000962"],--%>

                        <%--"contextRoot": "/gxa"--%>
                    <%--};--%>

                    <%--// tissues having expressions--%>
                    <%--var profileRows = [--%>
                        <%--{--%>
                            <%--"name": "tissues with expression",--%>
                            <%--"expressions": expData.expression--%>
                        <%--}--%>
                    <%--];--%>

                    <%--//console.log(profileRows);--%>

                    <%--var eventEmitter = expressionAtlasAnatomogram.eventEmitter;--%>

                    <%--expressionAtlasAnatomogram.render(--%>
                            <%--document.getElementById("anatomogram"),--%>
                            <%--anatomogramData,--%>
                            <%--profileRows,--%>
                            <%--"grey",--%>
                            <%--"red"--%>
                            <%--// "vader" is equivalent to <link rel="stylesheet" href="https://code.jquery.com/ui/1.11.4/themes/vader/jquery-ui.css">--%>
                    <%--);--%>



                    <%--// MA List talks to anatomogram--%>
                    <%--var MaListContainer = $("<ul></ul>").attr({'id':'tissues'});--%>
                    <%--//var nonTopLevels = false;--%>

                    <%--//if (Object.keys(maId2UberonEfoMap)){--%>
                    <%--//nonTopLevels = true;--%>
                    <%--var maIds = Object.keys(maId2UberonEfoMap);--%>
                    <%--for ( var n=0; n<maIds.length; n++) {--%>
                        <%--var liContainer = $("<li></li>").append(maId2MaNameMap[maIds[n]]);--%>
                        <%--MaListContainer.append(liContainer);--%>
                    <%--}--%>

                    <%--$('span#tissueList').html(MaListContainer);--%>

                    <%--$("ul#tissues li").on("mouseover", function() {--%>
                        <%--$(this).addClass('mahighlight');--%>
                        <%--var maName = $(this).text();--%>
                        <%--//console.log('name: '+maName);--%>
                        <%--var uberonIds = [];--%>
                        <%--var maIds = [];--%>
                        <%--maIds.push(maName2maIdMap[maName]);--%>

                        <%--for (var a = 0; a < maIds.length; a++) {--%>
                            <%--uberonIds = uberonIds.concat(maId2UberonEfoMap[maIds[a]]);--%>
                        <%--}--%>
                        <%--uberonIds = $.fn.getUnique(uberonIds);--%>

                        <%--//console.log(maName + " : " + uberonIds);--%>

                        <%--eventEmitter.emit("gxaHeatmapColumnHoverChange", uberonIds[0]);--%>
                        <%--//eventEmitter.emit("gxaHeatmapColumnHoverChange", "UBERON_0000955"); // test for brain--%>
                    <%--}).on("mouseout", function(){--%>
                        <%--$(this).removeClass('mahighlight');--%>
                        <%--eventEmitter.emit("gxaHeatmapColumnHoverChange", "");--%>
                    <%--});--%>


                    <%--// anatomogram tissue talks to MA list--%>
                    <%--eventEmitter.addListener("gxaAnatomogramTissueMouseEnter", function(e) {--%>

                        <%--var maIds = uberonEfo2MaIdMap[e];--%>
                        <%--//var topLevelNames = [];--%>
                        <%--var maNames = [];--%>
                        <%--for( var i=0; i<maIds.length; i++) {--%>
<%--//                            if (!nonTopLevels) {--%>
<%--//                                console.log("top");--%>
<%--//                                var tops = maId2topLevelNameMap[maIds[i]];--%>
<%--//                                for (var j = 0; j < tops.length; j++) {--%>
<%--//                                    //topLevelNames.push(tops[j]);--%>
<%--//                                    maNames.push(tops[j]);--%>
<%--//                                }--%>
<%--//                            }--%>
<%--//                            else {--%>
                                <%--maNames.push(maId2MaNameMap[maIds[i]]);--%>
                            <%--//}--%>
                        <%--}--%>
                        <%--console.log("hover in diagram for ma names in list: " + maNames);--%>

                        <%--//topLevelNames = $.fn.getUnique(topLevelNames);--%>
                        <%--maNames = $.fn.getUnique(maNames);--%>

                        <%--$('ul#tissues li').each(function () {--%>
                            <%--if ($.fn.inArray($(this).text(), maNames)) {--%>
                                <%--$(this).addClass("mahighlight");--%>
                            <%--}--%>
                        <%--});--%>

                    <%--});--%>
                    <%--eventEmitter.addListener("gxaAnatomogramTissueMouseLeave", function(e) {--%>
                        <%--$('ul#tissues li').removeClass("mahighlight");--%>
                    <%--});--%>

                <%--}--%>
                <%--else {--%>
                    <%--$('span#anatomogram').html("Currently , no tissue/organ diagram available for this anatomy term");--%>
                <%--}--%>
<%--//				else {--%>
<%--//					var topLevelNames = Object.keys(topLevelName2maIdMap);--%>
<%--//					//console.log("top level names : "+ topLevelNames);--%>
<%--//					for ( var n=0; n<topLevelNames.length; n++) {--%>
<%--//						var liContainer = $("<li></li>").append(topLevelNames[n]);--%>
<%--//						MaListContainer.append(liContainer);--%>
<%--//					}--%>
<%--//				}--%>


			<%--}--%>
			
	});				
	</script>
	
</jsp:body>
	

</t:genericpage>