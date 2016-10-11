<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>
	<jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute> 

	<jsp:attribute name="header">
	<style>

		.tabLabel{margin-bottom: 10px;}
		.ui-widget-header {
			border-bottom: 1px solid grey;
		}

		li.ui-corner-top {
			float: left;
			padding: 5px 20px;

		}
		li a {
			padding: 5px;
			color: black;
			text-decoration: none;
			font-weight: bold;
			font-size: 18px;
		}
		li a:hover {
			background-color: orange;
			color: white;
			text-decoration: none;
		}

	</style>
	<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />




	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block smoothScroll">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Search</a></li>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>		
	
	</jsp:attribute>


    <jsp:body>

		<div id="tabs">

			<ul class="tabLabel">
				<li><a href="#geneTab">Genes</a></li>
				<li><a href="#mpTab">Phenotypes</a></li>
				<li><a href="#diseaseTab">Diseases</a></li>
				<li><a href="#maTab">Anatomy</a></li>
				<li><a href="#impcImagesTab">IMPC Images</a></li>
				<li><a href="#impcTab">Images</a></li>
			</ul>

		<div id="geneTab">
			<div class="region region-sidebar-first">
				<div id='facet' class='fblock block'>
					<div class="head">Filter your search</div>
					<div class='content'>
						<p class='documentation title textright'>
							<a href='' id='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
						</p>

						<div id='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div>
						<div class="flist">
							<ul>
								<c:if test="${currTab != 'gene' }">
									<li class="fmcat" id="gene">
										<span class="flabel">Genes</span>
										<span class="fcount"></span>
										<ul></ul>
									</li>
								</c:if>
							</ul>
						</div>
					</div>
				</div>
			</div>

			<div class="region region-content">
				<div class="block block-system">
					<div class='content'>


						<div class="textright">
							<a id="searchExample" class="">View example search</a>
						</div>

						<div class="clear"></div>

						<div class="HomepageTable" id="mpi2-search"></div>
					</div>
				</div>
			</div>
		</div>

		


<!--
		<div id="tabs">
			<ul>
				<li><a href="#geneTab">Genes</a></li>
				<li><a href="#mpTab">Phenotypes</a></li>
				<li><a href="#diseaseTab">Diseases</a></li>
				<li><a href="#maTab">Anatomy</a></li>
				<li><a href="#impcImagesTab">IMPC Images</a></li>
				<li><a href="#impcTab">Images</a></li>
			</ul>


		<div id="geneTab">
		<div class="region region-sidebar-first">
			<div id='facet' class='fblock block'>	
				<div class="head">Filter your search</div>
				<div class='content'>

										
					<p class='documentation title textright'>
						<a href='' id='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
					</p>
										
					<div id='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div> 
					<div class="flist">

						<ul>
							<c:if test="${currTab != 'gene' }">
							<li class="fmcat" id="gene">
								<span class="flabel">Genes</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							</c:if>

							<c:if test="${currTab == 'mp' }">
							<li class="fmcat" id="mp">
								<span class="flabel">Phenotypes</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							</c:if>

							<c:if test="${currTab == 'disease' }">
							<li class="fmcat" id="disease">
								<span class="flabel">Diseases</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							</c:if>

							<c:if test="${currTab == 'ma' }">
							<li class="fmcat" id="ma">
								<span class="flabel">Anatomy</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							</c:if>


							<c:if test="${currTab == 'impc_images' }">
							<li class="fmcat" id="impc_images">
								<span class="flabel">IMPC Images</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							</c:if>

							<c:if test="${currTab == 'images' }">
							<li class="fmcat" id="images">
								<span class="flabel">Images</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							</c:if>

						</ul>
					</div>				
				</div>
			</div>	
		</div>	
				
		<div class="region region-content">
			<div class="block block-system">
				<div class='content'>
					<!--  <div class='searchcontent'>
						<div id="bigsearchbox" class="block">
							<div class="content">								
								<p><i id="sicon" class="fa fa-search"></i>
									
									<div class="ui-widget">
										<input id="s">
									</div>
								</p>									
							</div>
						</div>
					</div>
					
					<div class="textright">
						<a id="searchExample" class="">View example search</a>						
					</div>	

					<div class="clear"></div>

					<div class="HomepageTable" id="mpi2-search"></div>				
				</div>
			</div>
		</div>
		</div>
		</div>
		-->
        <compress:html enabled="${param.enabled != 'false'}" compressJavaScript="true">	    
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/geneFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/mpFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/maFacetWidget.js?v=${version}'></script>
			<!--  <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/pipelineFacetWidget.js?v=${version}'></script> -->
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/diseaseFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/impc_imagesFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/imagesFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/search.js?v=${version}'></script> 
	    </compress:html>




		<script>
       	$(document).ready(function(){
       		'use strict';
			$( "#tabs" ).tabs();

        });        
        </script>
			
						
    </jsp:body>

</t:genericpage>


