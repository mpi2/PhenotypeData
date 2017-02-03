<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">IDG | IMPC Project Information</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/IDG">IDG</a> &raquo; IDG</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="chartpage no-sidebars small-header">
	</jsp:attribute>
	<jsp:attribute name="header">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
    </jsp:attribute>

	<jsp:attribute name="addToFooter">

		<div class="region region-pinned">
			<div id="flyingnavi" class="block smoothScroll">
				<a href="#top"><i class="fa fa-chevron-up"
						title="scroll to top"></i></a>
				<ul>
					<li><a href="#top">IDG</a></li>
				</ul>
				<div class="clear"></div>
			</div>
    	</div>
		
    </jsp:attribute>


	<jsp:body>
    <!-- Assign this as a variable for other components -->
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>
		
 	<div class="region region-content">
		<div class="block block-system">
			<div class="content">
				<div class="node node-gene">
				
						<h1 class="title" id="top">Project Page: IDG</h1>

						<div class="section">
							<div class=inner>
									<div class="floatright">
										<img src="${baseUrl}/img/idgLogo.png" height="85" width="130">
									</div>
									<h3>Illuminating the Druggable Genome (IDG)</h3>
									<p>
										IDG is an NIH Common Fund project focused on collecting, integrating and making available biological data on 395 genes from three key druggable protein families that have been identified as potential therapeutic targets: non-olfactory G-protein coupled receptors (GPCRs), ion channels, and protein kinases. KOMP2 - funded IMPC Centers are creating where possible knockout mouse strains for this consortium.
									</p>
									
									<p>
									<b>Human-Mouse orthology mapping</b>
Orthologous genes between human and mouse were mapped using <a href="https://www.ncbi.nlm.nih.gov/homologene">HomoloGene</a>. 89% of IDG genes had mouse orthologs with one-to-one, one-to-many relationships (364 unique mouse orthologs). 43 IDG genes did not have a mouse ortholog: ADCK4, ADGRD2, ADGRE2, ADGRE3, BEST4, CLCA3P, CLIC2, FXYD6P3, GNRHR2, GPR32, GPR32P1, GPR42, GPR78, HTR1E, HTR3C, HTR3D, HTR3E, KCNJ18, MAS1L, NPBWR2, NPY6R, OPN1MW2, OXER1, P2RY11, PAK7, PRKACG, PRKY, PSKH2, SCNN1D, SGK223, STK17A, TAS2R19, TAS2R20, TAS2R30, TAS2R43, TAS2R5, TAS2R50, TAS2R8, TAS2R9, VN1R1, VN1R17P, VN1R3, VN1R5
									</p>
									
									<table>
									
										<thead>
											<tr >
												<th style="border-bottom: none"></th>
												<th style="border-bottom: none"></th>
												<th colspan="2">human-mouse ortholog relationship</th>
												<th style="border-bottom: none"></th>
											</tr>
											<tr>
												<th>Family</th>
												<th>Total Number</th>
												<th>One to One</th>
												<th>One to Many</th>
												<th>Human Genes with No Orthologs</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>GPCRs</td>
												<td>143</td>
												<td>113</td>
												<td>3</td>
												<td>27</td>
											</tr>
											<tr>
												<td>Kinases</td>
												<td>134</td>
												<td>127</td>
												<td>0</td>
												<td>7</td>
											</tr>
											<tr>
												<td>Ion Channels</td>
												<td>118</td>
												<td>107</td>
												<td>2</td>
												<td>9</td>
											</tr>
										</tbody>
									
									</table>
							</div>
						</div>	<!-- section -->
				
				
						 <div class="section" >
								<h2 class="title"	id="section-associations"> IMPC Production Status for IDG Gene Set </h2>
		           					 <div class="inner">
		            	<p>
		            	The IMPC consortium is using different complementary targeting strategies to produce Knockout alleles, namely ES cell based chromosome engineering and CRISPR/Cas-mediated genome engineering. Mouse are then produced and submitted to phenotyping pipelines. Below are Statistics representing the IMPC production status for IDG gene lists.
		            	</p>
									<div  class="half">
										<div id="idgOrthologPie">
			            		<script type="text/javascript">
													${idgOrthologPie}
											</script> 
										</div>       	
									</div>
									<div  class="half">
			            	<div id=idgChart>
			            		<script type="text/javascript">
													${idgChartTable.getChart()}
											</script> 
										</div>   
									</div>
		            	  <div class="clear"></div>   
		            </div>
		            
		        </div> <!-- section -->
		                            
		        
		        <div class="section" id="phenotypePValueDistribution">
								<h2 class="title"	id="section-associations"> Phenotype P value distribution for IDG genes </h2>		
		            <div class="inner">
										<!-- Associations table -->
										<c:if test="${chart != null}">
											<!-- phenome chart here -->
							  				<div id="phenomeChart">
							  				<a class="various" id="iframe" data-fancybox-type="iframe"></a></div>
											<script type="text/javascript">
												${chart}
											</script>	
										</c:if>	
		
		            </div>
		        </div> <!-- section -->                    
		        
						
						<div class="section">
						<h2 class="title">Gene to Phenotype Heat Map</h2>


							<div class=inner>

								<p>
									Heat Map representing the data status of IDG orthologs in IMPC.
								</p>
									<div id="geneHeatmap" class="geneHeatMap" style="overflow: hidden; overflow-x: auto;">
									</div>
							</div>
						</div>
					</div>

		</div>
	</div>
</div>

		
	<script>
		$(document).ready(function() {
			$.fn.qTip({
				'pageName' : 'idg',
				'textAlign' : 'left',
				'tip' : 'topLeft'
			}); // bubble popup for brief panel documentation					
		});
		var geneHeatmapUrl = "../geneHeatMap?project=idg";
		$.ajax({
			url : geneHeatmapUrl,
			cache : false
		}).done(function(html) {
			$('#geneHeatmap').append(html);
			//$( '#spinner'+ id ).html('');

		});
	</script>
	
</jsp:body>


</t:genericpage>
