<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">Conservation landing page | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/biological-system">Biological Systems</a> &nbsp;&raquo; Conservation</jsp:attribute>

	<jsp:attribute name="header">

	<!-- CSS Local Imports -->
    <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
    <link href="${baseUrl}/css/heatmap.css" rel="stylesheet" />

	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
    <%-- <script type='text/javascript' src='${baseUrl}/js/charts/conservationCharts.js'></script> --%>
    
    <style>
    		body {
    			 text-align: justify;
    		}
		table {
			border-collapse: collapse;
			border-spacing: 0;
		}
		/* Override allele ref style for datatable */
		table.dataTable thead tr {
			display: table-row;
		}
		
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
                     <li><a href="#top">Conservation</a></li>
                     <li><a href="#status">Approach</a></li>
                     <li><a href="#conservationPaper">IMPC Publication</a></li>
                     <li><a href="#significant-phenotypes">IMPC significant phenotypes</a></li>
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
                        <h1 class="title" id="top">Translating to other species</h1>

                       	<div class="section">
    							<div class="inner">
			                 	<h3>The IMPC aids the functional annotation of genes in wild species</h3>
			                    <ul>
			                    		<li>The IMPC has focused on translating knowledge from mouse to human, to identify models for human disease. However, translating to other species is relevant as well</li>
			                    		<li>Genetic functional data from the IMPC is  relevant to identify genes essential for development or associated to disease or adaptation in other species</li>
			                    		<li>The type of analyses presented here could be used to improve the breeding management of endangered mammals</li>
			                    	</ul>
			                    	</br>
            						Press releases or new posts:&nbsp;
            						<a target='_blank' href='http://blog.mousephenotype.org/new-research-suggests-laboratory-mouse-data-can-help-in-wildlife-conservation/'>IMPC blog</a>&nbsp;|&nbsp;
            						<a target='_blank' href=''>EMBL-EBI</a>&nbsp;|&nbsp;
            						<a target='_blank' href=''>QMUL</a>
                       		</div>
    							<div class="clear both"></div>
						</div>      
						
						<div style="padding: 20px;" class="clear both"></div>
						
                        <div class="section">
                            <h2 id="status" class="title">Approach</h2>
                            <div class="inner">
                            		<p>
                            			We used IMPC data derived from the <a target="_blank" href="https://www.mousephenotype.org/impress/protocol/154/7">IMPC Viability Primary Screen [IMPC_VIA_001]</a> to identify genes essential for organismal viability (development and survival) collected up to DR 7.0., 
                            			as well as all phenotype associations collected by the IMPC up to DR 6.2. We used these data in combination with (human) cell viability data to inform wild species genomic data sets 
                            			collected from the literature. 
                            		</p>
                            </div>
                        </div>
                        
						<br /><br />
					   	<div class="section">
					   		<h2 id="conservationPaper" class="title">IMPC Conservation Publication</h2>
					   		<div class="inner">
					   			<h3>Aiding the functional annotation of genes relevant for development and adaptation in mammalian species</h3>
					   			<p>
									<a target='_blank' href="https://link.springer.com/article/10.1007/s10592-018-1072-9">Conservation Genetics Special Issue on Adaptation</a><br>
									<a target='_blank' href="https://link.springer.com/article/10.1007/s10592-018-1072-9#SupplementaryMaterial">Supplementary Material</a>
								</p>
					   			<ul>
					   				<li>We used IMPC organism viability data in combination with human cell viability data to identify essential genes.</li>
					   				<li>Genes in gorillas with loss-of-function (LoF) alleles have been previously identified; we inferred gorilla-to-mouse and gorilla-to-human orthologues of those genes and associated them to genes essential for organism and cell viability.</li>
					   				<li>For this set of genes with LoF alleles, we found that the percentage of lethal/essential genes was lower than for protein-coding genes, but the difference was not significant (P > 0.1 in all comparisons).</li>
					   				<li>Future research may include improved methods to detect LoF variants, detailed observation of gorillas carrying these variants, investigating whether the LoF variants affect or not the functional exons, and whether there are paralogues or alternative genes providing functional compensation.</li>
					   				<li>We also show how phenotype associations collected by the IMPC can aid the functional annotation of regions putatively targeted by positive selection or associated to disease using examples from wild species, such as polar bears and cheetahs.</li>
					   				<li>This investigation is based on about 15% of mammalian protein-coding genes. The IMPC continues screening for genes essential for organism survival and development  and collecting phenotype association data.</li>
					   			</ul>
								
								<h3 style="clear: left;">Methods</h3>
                               	<ul>
                               		<li>We developed custom scripts to infer orthologues based on available resources (<a target='_blank' href="https://www.genenames.org/cgi-bin/hcop">HGNC Comparison of Orthology Predictions (HCOP)</a>, <a target='_blank' href="http://metaphors.phylomedb.org/">MetaPhOrs</a>) and to conduct gene overlaps</li>
                               		<li>We collected IMPC primary viability screen data and combined it with human cell viability data from 3 different studies (<a target='_blank' href="https://europepmc.org/abstract/MED/26472760">Blomen et al. 2015</a>; <a target='_blank' href="https://europepmc.org/abstract/MED/26627737">Hartl et al. 2015</a>; <a target='_blank' href="https://europepmc.org/abstract/MED/26472758">Wang et al. 2015</a>) to identify essential genes in cells and in mouse (for data, see <a target='_blank' href="https://link.springer.com/article/10.1007/s10592-018-1072-9#SupplementaryMaterial">Supplementary Material</a>)</li>
                               		<li>We collected IMPC phenotype annotations (also MGI) to characterize genes potentially linked to disease or to regions putatively under positive selection in selected endangered species (for data, see <a target='_blank' href="https://link.springer.com/article/10.1007/s10592-018-1072-9#SupplementaryMaterial">Supplementary Material</a>)</li>
                               	</ul>
                               	
					   		</div>
					   	</div>
					   	
                       	<div class="section">
                            <h2 id="significant-phenotypes" class="title">IMPC significant phenotypes</h2>
                            <div class="inner">
                            		<p>IMPC significant phenotypes for selected mammal species, based on orthologue inferences (source: <a target="_blank" href="https://link.springer.com/article/10.1007/s10592-018-1072-9">IMPC Conservation Genetics paper</a>). <!-- Note links are to phenotype pages depicting data as in the current Data Release. --></p>
                            		<br><br>
                            		<!-- Horizontal bar charts -->
                            		<!-- <div id="container" style="min-width: 310px; max-width: 800px; height: 400px; margin: 0 auto"></div> -->
                            		<img src='${baseUrl}/img/conservation_charts.png' alt="Charts for significant phenotypes" height="700" width="700"></img>
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
