<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/biological-system">Biological Systems</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

	<jsp:attribute name="header">

	<!-- CSS Local Imports -->
    <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
    <link href="${baseUrl}/css/heatmap.css" rel="stylesheet" />

	<script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    
    <script type='text/javascript'
			src='${baseUrl}/js/charts/modules/heatmap.js?v=${version}'></script> 
    <script type="text/javascript"
			src='${baseUrl}/js/charts/heatMapMetabolism.js?v=${version}'></script>
    <script
			src="https://blacklabel.github.io/grouped_categories/grouped-categories.js"></script>
    
    <style>
table {
	border-collapse: collapse;
	border-spacing: 0;
}
/* Override allele ref style for datatable */
table.dataTable thead tr {
	display: table-row;
}

#metabolism-table_length {
	width: 50%;
	float: left;
	/* text-align: right; */
}

#metabolism-table_filter {
	width: 50%;
	float: right;
	text-align: right;
}
/* .background_hover_axis {
			background-color: rgb(173,216,230);
		} */
</style>
	
	</jsp:attribute>

	<jsp:attribute name="bodyTag">
		<body class="phenotype-node no-sidebars small-header">
	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned">

             <div id="flyingnavi" class="block smoothScroll">

                 <a href="#top"><i class="fa fa-chevron-up"
					title="scroll to top"></i></a>

                 <ul>
                     <li><a href="#top">Metabolism</a></li>
                     <li><a href="#status">Approach</a></li>
                     <li><a href="#metabolismPaper">IMPC Publication</a></li>
                     <li><a href="#phenotypes-distribution">Phenotype Distribution</a></li>
                     <li><a href="#paper">Publications</a></li>
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
                        <h1 class="title" id="top">${pageTitle} </h1>

                       	<div class="section">
    							<div class="inner">
                       			${shortDescription}
                       		</div>
    							<div class="clear both"></div>
						</div>
						
						<div style="padding: 20px;" class="clear both"></div>
						
                        <div class="section">
                            <h2 id="status" class="title">Approach</h2>
                            <div class="inner">
                            		<p align="justify">
                            			To identify the function of genes, the IMPC uses a series of standardised protocols described in <a
										href="${baseUrl}/../impress">IMPReSS</a> (International Mouse Phenotyping Resource of Standardised Screens). 
                            			Tests addressing the metabolic function are conducted on young adult mice at 11-16 weeks of age.  
                            		</p>
                            		<br /><br />
                            		<h4>
                            			Procedures that can lead to relevant phenotype associations
                            			<button id="showHideApproachList"
										class="toggleButton" title="Click to display"
										style="background: none !important; border: none;">
                            				<i class="fa more fa-plus-square"></i>
                            			</button>
                            		</h4>
                            		<div id="approachList"
									style="display: none">
                                		<c:import
										url="landing_procedures_frag.jsp" />
                                	</div>
                            </div>
                        </div>
                        
						<script type="text/javascript">
							$(document).ready(function() {
								$("#showHideApproachList").click(function() {
									$("i").toggleClass("fa-minus-square");
									$("#approachList").toggle();
								});
							});
						</script>
						
						<br /><br />
					   	<div class="section">
					   		<h2 id="metabolismPaper" class="title">IMPC Metabolism Publication</h2>
					   		<div class="inner">
					   			<h3>Metabolic diseases investigated in 2,016 knockout mouse lines</h3>
					   			<p>
									<a target='_blank' href="https://bit.ly/IMPCMetabolism">Nature Communications publication</a>
								</p>
					   			<ul>
					   				<li>974 genes were identified with strong metabolic phenotypes (see gene table, below).</li>
					   				<li>429 genes had not been previously associated with metabolism, 51 completely lacked functional annotation, and 25 had single nucleotide polymorphisms associated to human metabolic disease phenotypes.</li>
					   				<li>515 genes linked to at least one disease in OMIM.</li>
					   				<li>Networks of co-regulated genes were identified, and genes of predicted metabolic function found.</li>
					   				<li>Pathway mapping revealed sexual dimorphism in genes and pathways.</li>
					   				<li>This investigation is based on about 10% of mammalian protein-coding genes. The IMPC will continue screening for genes associated to metabolic diseases in its second 5 year phase.</li>
					   			</ul>
					   			
								<h3>Gene table</h3> 
                               	<p>Mutant/wildtype ratios below the 5th percentile and above the 95th percentile of the ratio distributions yielded 28 gene lists that serve as a data mining resource for further investigation into potential links to human metabolic disorders.</p>
                                 <p>By hovering over the table you can select cells and click to explore the underlying data.</p>
                                 <br /> <br />
                               	<div id="heatMapContainer"
									style="height: 450px; min-width: 310px; max-width: 894px; position: relative;"></div>
                               	<div id="metabolismTableDiv"
									style="display: none; position: relative; z-index: 10; margin-top: -50px;">
	                               	<table id="metabolism-table"
										class='table tableSorter'>
			                        		<thead>
					                        <tr>
					                        		<th>Parameter</th>
					                        		<th>Sex</th>
				                        			<th>MGI_ID</th>
					                        		<th>Gene_symbol</th>
					                        		<th>Center</th>
					                        		<th>Zygosity</th>
					                        		<th>Ratio_KO_WT</th>
					                        		<th>Tag</th>
					                        	</tr>
			                        		</thead>
			                        		<!-- BODY -->
			                        	</table>
			                        	<div id="tsv-result" style="display: none;"></div>
									<br />
									<div id="export">
										<a id="hideTable" style="float: left;">Hide</a> <!-- href="#heatMapContainer" -->
					                  	<p class="textright">
					                      	Download data as:
					                      	<a id="downloadTsv"
												class="button fa fa-download">TSV</a>
					   						<a id="downloadExcel" class="button fa fa-download">XLS</a>
					                      	<%-- <a id="tsvDownload" href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=tsv&fileName=${gene.markerSymbol}" target="_blank" class="button fa fa-download">TSV</a>
					                      	<a id="xlsDownload" href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=xls&fileName=${gene.markerSymbol}" target="_blank" class="button fa fa-download">XLS</a> --%>
					                  	</p>
					              	</div>
					              	<br /> <br />
		                        	</div>
								
								<h3>Strong metabolic phenotype genes form regulatory networks</h3>
								<div style="width: 100%;">
									<div style="float: left; width: 50%;">
										<img src="${baseUrl}/documentation/img/more-cassette.png"
											alt="Illustration of the action of MORE cassettes in regulatory networks"
											width="100%" />
                               		</div>
	                               	<br />
	                               	<ul>
	                               		<li>Transcriptional co-regulation often involves a common set of transcription factor binding sites (TFBSs) shared between co-regulated promoters and in a particular organization (known as Multiple Organized Regulatory Element (MORE)–cassettes).</li>
	                               		<li>Identification of shared MORE-cassettes in promoters of candidate genes allowed to discover extensive metabolic phenotype-associated networks of potentially co-regulated genes.</li>
	                               		<li>MORE-cassettes are invariant genomic sequence features (similar to reading frames).</li>
	                               		<li>The presence of MORE-cassettes enabled to a priori predict phenotypes and identify genes potentially linked to metabolic functions.</li>
	                               	</ul>
	                               	<br />
                               	</div>                         	
								<br /> <br />
								
								<h3 style="clear: left;">Methods</h3>
                               	<p>Genes with phenotypes associated to the following <b>seven metabolic parameters</b>, with diagnostic relevance in human clinical research, were further analysed:</p>
                               	<ul>
                               		<li>Fasting basal blood glucose level before glucose tolerance test (T0)</li>
                               		<li>Area under the curve of blood glucose level after intraperitoneal glucose administration relative to basal blood glucose level (AUC)</li>
                               		<li>Plasma triglyceride levels (TG)</li>
                               		<li>Body mass (BM)</li>
                               		<li>Metabolic rate (MR)</li>
                               		<li>Oxygen consumption rate (VO2)</li>
                               		<li>Respiratory exchange ratio (RER) – a measure of whole-body metabolic fuel utilization</li>                               		
                               	</ul>
                               	<p>Mutant/wildtype ratios (mean value of metabolic parameters of mutants divided by the mean value obtained for wildtypes) were calculated:</p>
                               	<ul>
                               		<li>Control wildtype mice from each phenotypic center included, matched for gender, age, phenotypic pipeline and metadata (e.g. instrument).</li>
                               		<li>Males and females analyzed separately to account for sexual dimorphism.</li>
                               	</ul>
                               	
                               	<h3>New mouse models</h3>
                               	<ul>
                               		<li>IMPC generated and identified new genetic disease models.</li>
                               		<li>New models available to the research community to perform in-depth investigations of novel genetic elements associated to metabolic disease mechanisms.</li>
                               		<li>These models fill the gap between genome-wide association studies and functional validation using a mammalian model organism.</li>
                               	</ul>
                               	
                               	
					   		</div>
					   	</div>
					   	
                       	<!-- <div class="section">
                            <h2>
                                Vignettes
                            </h2>
                            <div class="inner"></div>
                       	</div> -->
	                            
                       	<div class="section">
                            <h2 id="phenotypes-distribution"
								class="title">Phenotype distribution</h2>
                            <div class="inner">
                            		<p></p>
                                <br /> <br />
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>							
                                </div>
                            </div>
                        	</div>
                        
                        <div id="paper" class="section">
                            <jsp:include page="paper_frag.jsp"></jsp:include>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>
