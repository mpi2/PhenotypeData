<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/landing">Landing
        Pages</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

    <jsp:attribute name="header">

	<!-- CSS Local Imports -->
    <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />  
    <link href="${baseUrl}/css/biological_system/style.css" rel="stylesheet" />  

	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/modules/exporting.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/pieChartCmg.js?v=${version}'></script>
    
	
  	<style>
		/* Override allele ref style for datatable */
		table.dataTable thead tr {
			display: table-row;
		}
	</style>

	</jsp:attribute>

    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter">

	</jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">${pageTitle}</h1>
						
						<div style="padding: 30px;" class="clear both"></div>
						
                        <div class="section">
                            <div class="inner">
	                            	<h1>
	                            		<img src="${baseUrl}/img/landing/cmg-logo_1.png" alt="Centers for Mendelian Genetics logo" style="float: left;">
	                            		<p style="color: #23bcb7; font-weight: bold; padding-top: 50px; float: right;">${pageTitle}</p>
	                            	</h1>
	                            	<br/><br/>
	                            	<div style="text-align: justify; clear: both;">${shortDescription}</div>
                            		<br/><br/>
                            </div>
                        </div>
					   
					   <div class="section">
					   		<h2 class="title">Status of CMG genes in mouse lines in IMPC</h2>
					   		<div class="inner" style="height: 450px;">
					   			<!-- Pie Chart Tier1 -->
					   			<div class="half">
					   				<div id="pieChart1"></div>
				                </div>
				                
				                <!-- Pie Chart Tier2 -->
				                <div class="half">
				                    <div id="pieChart2"></div>
				                </div>
				                <br/><br/>
					   		</div>
					   	</div>
					  	
					   	<div class="section">
					   		<h2 class="title">Table of CMG genes in mouse lines in IMPC</h2>
					   		<div class="inner">
					   			<!-- TABLE -->
					   			<table id="cmg-genes" class="table tableSorter">
				   					<thead>
				   						<tr>
				   							<th rowspan="2" colspan="1" class="headerSort">Disease</th>
				   							<th rowspan="2" colspan="1" class="headerSort">OMIM ID</th>
				   							<th rowspan="2" colspan="1" class="headerSort">Tier 1 gene</th>
				   							<th rowspan="2" colspan="1" class="headerSort">Tier 2 gene</th>
				   							<th rowspan="2" colspan="1" class="headerSort">Mouse Orthologue(s)</th>
				   							<th rowspan="2" colspan="1" class="headerSort">IMPC Status</th>
				   							<th rowspan="1" colspan="3" class="headerSort">Phenotype Overlap Score</th>
				   						</tr>
				   						<tr>
				   							<th rowspan="1" colspan="1" class="headerSort">Other Human Disease</th>
				   							<th rowspan="1" colspan="1" class="headerSort">IMPC mouse</th>
				   							<th rowspan="1" colspan="1" class="headerSort">Published Mouse</th>
				   						</tr>
				   					</thead>
				   					
				   				</table>
				   			</div>
					   	</div>
					   	
					   	<script>
							$(document).ready(function() {
								var cmgTableCleanedUpFile = baseUrl + '/documentation/json/CMG-table-cleanedup.json';
								var matchingInferencesFile = baseUrl + '/documentation/json/Matching_inferences.json';
								
								function getDataCleanedUpFile(filePath) {
								    var genesWithOmim = [];
								    $.ajax({
								    		type: 'GET',
								    		async: false,
								    		url: filePath,
								    		dataType: "json",
								    		success: function(data) {
								    			$.each(data, function( key, value ) {
								    				if (value.OMIM_id != "_") {
													genesWithOmim.push({
														"disease": value.Disease,
														"omim_id": value.OMIM_id,
														"tier_1_gene": value.Tier_1_gene,
														"tier_2_gene": value.Tier_2_gene
													});
								    				}
								    			});
								    		}
								    });
								    return genesWithOmim;
								}
								
								function getDataMatchingInferencesFile(filePath, genesWithOmim) {
								    var matchingInferencesGenesWithOmim = null;
								    $.ajax({
								    		type: 'GET',
								    		async: false,
								    		url: filePath,
								    		dataType: "json",
								    		success: function(data) {
								    			matchingInferencesGenesWithOmim = genesWithOmim.map(function(v){
								    		        var ret;
								    		        $.each(data, function(k, v2){
								    		            if (v2.Human_symbol == v.tier_1_gene || v2.Human_symbol == v.tier_2_gene){
								    		                ret = $.extend({}, v, 
									    		                		{
									    		                			"human_symbol": v2.Human_symbol,
								    		                				"mouse_orthologue": v2.Mouse_orthologue,
								    		                				"impc_status": v2.Latest_project_status,
								    		                				"impc_link": v2.IMPC_link
								    		                		});
								    		                return false;
								    		            }      
								    		        });
								    		        return ret;
								    		    });
								    		}
								    });
								    return matchingInferencesGenesWithOmim;
								}
								
								var genesWithOmim = getDataCleanedUpFile(cmgTableCleanedUpFile);
								var matchingInferencesGenesWithOmim = getDataMatchingInferencesFile(matchingInferencesFile, genesWithOmim);
								// console.log(genesWithOmim);
								// console.log(matchingInferencesGenesWithOmim);
								
								function cleanDataForTable(matchingInferencesGenesWithOmim) {
									var contentTable = [];
									$.each(matchingInferencesGenesWithOmim, function( key, value ) {
										if (value != undefined) {
											var content = $.extend({}, value, 
								    		                		{
																"other_human_disease": "",
																"impc_mouse": "",
																"published_mouse": ""
							    		                			});
											contentTable.push(content);
										}
									}); 
									return contentTable;
								}
								
								var contentTable = cleanDataForTable(matchingInferencesGenesWithOmim);
								console.log(contentTable);
								
								$('#cmg-genes').DataTable({
									"bDestroy" : true,
									"searching" : false,
									"bPaginate" : true,
									"sPaginationType" : "bootstrap",
									/* "columnDefs": [
										{ "type": "alt-string", targets: 3 }   //4th col sorted using alt-string
									], */
									"aaSorting": [[0, "asc"]], // 0-based index
									"aoColumns": [
									    null, null,null,
//										 {"sType": "html", "bSortable": true},
//										 {"sType": "string", "bSortable": true},
//										 {"sType": "string", "bSortable": true},
										 {"sType": "html", "bSortable": true}
									],
									"aaData": contentTable,
							        "aoColumns": [
							            { "mDataProp": "disease"},
							            { "mDataProp": "omim_id",
								            	"render": function ( data, type, full, meta ) {
					                		 		return '<a href="http://www.mousephenotype.org/data/disease/OMIM:'+data+'" target="_blank">'+data+'</a>';
					                		 	}
							            },
							            { "mDataProp": "tier_1_gene"},
							            { "mDataProp": "tier_2_gene"},
							            { "mDataProp": "mouse_orthologue",
					                        "render": function ( data, type, full, meta ) {
					                		 		return '<a href="'+full.impc_link+'" target="_blank">'+data+'</a>';
					                		 	}
					                 	},
							            { "mDataProp": "impc_status"},
							            { "mDataProp": "other_human_disease"},
							            { "mDataProp": "impc_mouse"},
							            { "mDataProp": "published_mouse"},
							       	], 
								});
							});
						</script>
					   	
					   <br/><br/>
                        <div class="section">
                            <h2>
                                Vignettes
                            </h2>
                            <div class="inner"></div>
                        </div>
	                            
                        <div class="section">
                            <h2 class="title">Phenotypes distribution</h2>
                            <div class="inner">
                            		<p></p>
                                	<br/> <br/>
                               	<!--  <div id="phenotypeChart">
                                    	<script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>							
                                	</div> -->
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>



