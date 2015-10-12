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
                                <div class="half" >
                                <div class="paddingRightMedium">
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
	                               
	                                
	                                <c:if test="${viabilityCalls != null && !(empty viabilityCalls)}">
			                            <p class="with-label no-margin">
			                            	<span class="label">Viability</span>
			                            	<t:viabilityButton callList="${viabilityCalls}"> </t:viabilityButton>
			                            </p>
	                               	</c:if>
	                               	
	                                <h4> <a href="${baseUrl}/genes/${gene.mgiAccessionId}">IMPC Phenotype Annotations </a></h4>
										
	                               	<c:if test="${phenotypeSummaryObjects.keySet().size() > 0}">
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
										</c:if>
										<c:if test="${phenotypeSummaryObjects.keySet().size() == 0}">
											<p class="alert alert-info">IMPC has no phenotype associations to ${gene.markerSymbol} yet.</p>
										</c:if>
										
										<div>
											<c:forEach var="alleleName" items='${alleleCassette.keySet()}'>
												<img alt="${alleleName}" title="${alleleName}" src="${alleleCassette.get(alleleName)}">
											</c:forEach>
										</div>
									</div>
	                            </div>
	                            
	                            <div class="half">
	                            	<div class="paddingLeftMedium">
                                	<h3>Human ortholog <c:forEach var="symbol" items="${gene.humanGeneSymbol}" varStatus="loop">
	                                       ${symbol}    <c:if test="${!loop.last}">, </c:if>    <c:if test="${loop.last}"></c:if> </c:forEach>
	                                </h3>
                                    <p>[Function, synonyms]</p>
                                    <p>[GO annotations]</p>
                                    <c:if test="${not empty orthologousDiseaseAssociations}">
	                                   	<table>                                    
											<thead>
											    <tr>
											        <th><span class="main">Disease Name</span></th>
											        <th><span class="main">Source</span></th>
											        <th>In Locus</th>
											        <th><span class="main">MGI/IMPC</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
											        <th></th>
											    </tr>
											</thead>
											<tbody>
											    <c:forEach var="association" items="${orthologousDiseaseAssociations}" varStatus="loop">
											        <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
											        <tr id="${disease.diseaseIdentifier.databaseAcc}" targetRowId="P${geneIdentifier.databaseAcc}_${association.diseaseIdentifier.databaseAcc}" requestpagetype= "gene" geneid="${geneIdentifier.compoundIdentifier}" diseaseid="${association.diseaseIdentifier.compoundIdentifier}">
											            <td>
											            	<a href="${baseUrl}/disease/${association.diseaseIdentifier}">${association.diseaseTerm}</a>
											            </td>
											            <td>
											                <a id="diseaseId" href="${association.diseaseIdentifier.externalUri}">${association.diseaseIdentifier}</a>
											            </td>
											            <td>
											                <c:if test="${associationSummary.inLocus}"> Yes </c:if>
											                <c:if test="${!associationSummary.inLocus}"> No </c:if>
											            </td>
											            <td>
											                <c:if test="${0.0 != associationSummary.bestModScore}">
											                    <b style="color:#EF7B0B">${associationSummary.bestModScore}</b>   
											                </c:if>   
											                <c:if test="${0.0 == associationSummary.bestModScore}">
											                    <b>-</b>   
											                </c:if>
											                /
											                <c:if test="${0.0 != associationSummary.bestHtpcScore}">
											                    <b style="color:#EF7B0B">${associationSummary.bestHtpcScore}</b>
											                </c:if>
											                <c:if test="${0.0 == associationSummary.bestHtpcScore}">
											                    <b>-</b>
											                </c:if>                                        
											            </td>
											        </tr>
											    </c:forEach>
											</tbody>
										</table>
	                                 </c:if>
									</div>
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