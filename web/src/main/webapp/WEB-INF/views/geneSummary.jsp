<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Gene details for ${gene.markerName}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search#q=*:*&facet=gene">Genes</a> &raquo; ${gene.markerSymbol}</jsp:attribute>
	
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">	
	</jsp:attribute>
	
	<jsp:attribute name="addToFooter">
	    <script type="text/javascript" src="http://www.ebi.ac.uk/gxa/resources/js-bundles/vendor.bundle.js"></script>
		<script type="text/javascript" src="http://www.ebi.ac.uk/gxa/resources/js-bundles/expression-atlas-heatmap.bundle.js"></script>
		<script type="text/javascript">
		    var AtlasHeatmapBuilder = window.exposed;
		    AtlasHeatmapBuilder({
		        gxaBaseUrl: "http://www.ebi.ac.uk/gxa/",
		        params: "geneQuery=ASPM&species=mus%20musculus",
		        isMultiExperiment: true,
		        target: "heatmapContainer"
		    });
		</script>
		<link rel="stylesheet" href="${baseUrl}/css/customanatomogram.css" />
      
	</jsp:attribute>
	
	<jsp:body>
        <div class="region region-content">
            <div class="block">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Gene: ${gene.markerSymbol}
                            <span class="documentation">
                                <a href='' id='detailsPanel'
								class="fa fa-question-circle pull-right"></a>
                            </span>
                        </h1>

                        <div class="section">
                            <div class="inner">
                                <div class="half">
                                	<h3>Mouse ${gene.markerSymbol} </h3>
                                    
									<c:if test="${gene.markerName != null}">
		                                <p class="with-label no-margin">
		                                    <span class="label">Name</span>
		                                        ${gene.markerName}
		                                </p>
									</c:if>
									
	                                <c:if test="${!(empty gene.markerSynonym)}">
	                                    <p class="with-label no-margin">
	                                        <span class="label">Synonyms</span>
	                                        <c:forEach var="synonym"
												items="${gene.markerSynonym}" varStatus="loop">
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
	                               
	                                
		                            <p class="with-label no-margin">
		                            	<span class="label">Viability</span>
		                            	<t:viabilityButton callList="${viabilityCalls}"> </t:viabilityButton>
		                            </p>
	                               
	                                <h4> <a href="${baseUrl}/genes/${gene.mgiAccessionId}">IMPC Phenotype Annotations </a></h4>
										
										<div class="half">
											<c:forEach var="zyg"  items="${phenotypeSummaryObjects.keySet()}">
	                                            <p>In <b>${zyg} :</b>  </p>
	                                            <ul>
	                                                <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true).size() > 0}'>
	                                                	<c:forEach var="summaryObj"  items='${phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true)}'>
	                                                      	<li>
	                                                           	<a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a> [m/f]
	                                                        </li>
	                                                    </c:forEach>
	                                                </c:if>
	
	                                                <c:if  test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true).size() > 0}'>
	                                                	<c:forEach var="summaryObj"  items='${phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true)}'>
	                                                    	<li>
	                                                        	<a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a> [f]
	                                                        </li>
	                                                  	</c:forEach>
	                                                </c:if>
	
	                                                <c:if  test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true).size() > 0}'>
	                                                    <li>
		                                                    <c:forEach var="summaryObj" items='${phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true)}'>
	                                                            <li>
	                                                            	<a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a> [m] 
	                                                            </li>
		                                                    </c:forEach>
	                                                    </li>
	                                                </c:if>
	                                            </ul>
	                                        </c:forEach>
	                                    </div>
	                                    <div class="half">
                                        	<jsp:include page="phenotype_icons_frag.jsp"/>
										</div>
										
	                            </div>
	                            
	                            <div class="half">
                                	<h3>Human ortholog <c:forEach var="symbol" items="${gene.humanGeneSymbol}" varStatus="loop">
	                                       ${symbol}    <c:if test="${!loop.last}">, </c:if>    <c:if test="${loop.last}"></c:if> </c:forEach>
	                                </h3>
                                    <p>[Function, synonyms]</p>
                                    <p>[GO annotations]</p>
									
	                            </div>
	                            
	                            <div class="clear"></div>
	                            <br/>
	                            
	                            
	                            <div class="bordertop">
	                            	<div id="heatmapContainer" class="bordertop"></div>
	                            </div>
	                             
                        	</div>
                        <!-- section end -->
                      </div>
                   </div>
                </div>
             </div>
          </div>
      </jsp:body>

	
</t:genericpage>