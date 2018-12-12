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
    	<!-- script type="text/javascript" src="${baseUrl}/js/vendor/jquery.tooltipster.min.js"></script-->
    	
		<script type="text/javascript">
		    var AtlasHeatmapBuilder = window.exposed;
		    AtlasHeatmapBuilder({
		        params: "geneQuery=${gene.markerSymbol}&species=mus%20musculus",
		        isMultiExperiment: true,
		        target: "heatmapContainer"
		    });
		    $(document).ready(function() {
	            $('.tooltip').tooltipster();
	        });
		</script>
    
		<link rel="stylesheet" href="${baseUrl}/css/customanatomogram.css" />
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/vendor/tooltipster.css" /-->
      
	</jsp:attribute>
	
	<jsp:body>
        <div class="region region-content">
            <div class="block">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Gene: ${gene.markerSymbol}  </h1>

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
			                            	<t:viabilityButton callList="${viabilityCalls}" link="${baseUrl}/genes/${gene.mgiAccessionId}"> </t:viabilityButton>
			                            </p>
	                               	</c:if>
	                               	
	                               	<c:if test="${!(prodStatusIcons == '')}">
		                            	<p class="with-label">
		                                	<span class="label">Status</span>
		                                    ${prodStatusIcons}
		                                </p>
		                            </c:if>
	                               	
	                               	<c:if test="${alleleCassette.keySet().size() > 0}">
		                               	<div>
											<h4>Allele Map</h4>
											<c:forEach var="alleleName" items='${alleleCassette.keySet()}'>
												<img class="alleleCassette" alt="${alleleName}" title="${alleleName}" src="${alleleCassette.get(alleleName)}">
											</c:forEach>
										</div>
									</c:if>
									
	                               	<br/>
	                               	<div>
		                                <h4> <a href="${baseUrl}/genes/${gene.mgiAccessionId}">IMPC Phenotype Annotations </a></h4>
											
		                               	<c:if test="${phenotypeSummaryObjects.keySet().size() > 0}">
											<div class="half">
												<c:forEach var="zyg"  items="${phenotypeSummaryObjects.keySet()}">
		                                            <p>In <b>${zyg} :</b>  </p>
		                                            <ul>
		                                                <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true).size() > 0}'>
		                                                	<c:forEach var="summaryObj"  items='${phenotypeSummaryObjects.get(zyg).getBothPhenotypes(true)}'>
		                                                      	<li>
		                                                           	<a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a> <img alt="Female" title="Female" src="${baseUrl}/img/female.jpg"/><img alt="Male" title="Male" src="${baseUrl}/img/male.jpg"/>
		                                                        </li>
		                                                    </c:forEach>
		                                                </c:if>
		
		                                                <c:if  test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true).size() > 0}'>
		                                                	<c:forEach var="summaryObj"  items='${phenotypeSummaryObjects.get(zyg).getFemalePhenotypes(true)}'>
		                                                    	<li>
		                                                        	<a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a> <img alt="Female" title="Female" src="${baseUrl}/img/female.jpg"/>
		                                                        </li>
		                                                  	</c:forEach>
		                                                </c:if>
		
		                                                <c:if  test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true).size() > 0}'>
		                                                    <li>
			                                                    <c:forEach var="summaryObj" items='${phenotypeSummaryObjects.get(zyg).getMalePhenotypes(true)}'>
		                                                            <li>
		                                                            	<a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a> <img alt="Male" title="Male" src="${baseUrl}/img/male.jpg"/>
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
									</div>
									
									<c:if test="${phenotypeSummaryObjects.keySet().size() == 0}">
											<p class="alert alert-info">IMPC has no phenotype associations to ${gene.markerSymbol} yet.</p>
									</c:if>
									<div class="clear"></div>
										
									<c:if test="${imageSummary.size() > 0}">
										<br/>
										<div>
											<h4>IMPC Images</h4>
											<c:forEach var="image" items="${imageSummary}">
												<div class="inline-block paddingLeftMedium"> <img src="${image.getThumbnailUrl()}"> <br/>
													<a href='${baseUrl}/impcImages/images?q=*:*&defType=edismax&wt=json&fq=procedure_name:"${image.getProcedureName()}" AND gene_accession_id:"${acc}"'>${image.getProcedureName()}</a> (${image.getNumberOfImages()})
												</div> 			
																			 
											</c:forEach>
										</div>
									</c:if>
								</div>
	                         </div>
	                            
	                            <div class="half">
	                            	<div class="paddingLeftMedium">
                                	<h3>Human ortholog <c:forEach var="symbol" items="${gene.humanGeneSymbol}" varStatus="loop">
	                                       ${symbol}<c:if test="${!loop.last}">, </c:if>    <c:if test="${loop.last}"></c:if> </c:forEach>
	                                </h3>
                                    
                                    <c:if test="${gene.getUniprotHumanCanonicalAcc() == null}">
                                    	<p class="alert alert-info"> No human orthologs could be found.</p>
                                    </c:if>

										<c:if test="${pharos != null}">
											<p class="with-label">
												<span class="label">Target druggability</span>
												<a title="${pharos.getDescription()}</br>Source: PHAROS" href="${pharos.getPageLink()}"> ${pharos.getTdl()} </a>
											</p>
										</c:if>

                                    <c:if test="${gene.getUniprotHumanCanonicalAcc() != null}">
	                                    <div> 
	                                   		<c:if test="${uniprotData.getFunction() != null}">
		                                     	<p class="with-label">
			                                    	<span class="label">Function</span>${uniprotData.getFunctionSummary()}
			                                    	<c:if test="${!uniprotData.getFunction().equalsIgnoreCase(uniprotData.getFunctionSummary())}">
														<a href="#" class="tooltip" title="${uniprotData.getFunction()}">...</a>
			                                    	</c:if>
			                               	 	</p>
		                               	 	</c:if>
		                               	 	<c:if test="${uniprotData.getGoProcess() != null && uniprotData.getGoProcess().size() > 0}">
		                               	 		<c:set var="count" value="0" scope="page" />
			                               		<p class="with-label">
			                                    	<span class="label">GO Process</span>
			                                    	<t:restrictListDisplay title="GO Process" displayList="${uniprotData.getGoProcess()}" numberToDisplay="5"> </t:restrictListDisplay>
			                               	 	</p>
			                               	</c:if>
		                               	 	<c:if test="${uniprotData.getGoMolecularFunction() != null && uniprotData.getGoMolecularFunction().size() > 0}">
		                                   		<p class="with-label">
			                                    	<span class="label">GO Function</span>
			                                    	<t:restrictListDisplay title="GO Function" displayList="${uniprotData.getGoMolecularFunction()}" numberToDisplay="5"> </t:restrictListDisplay>
		                               	 		</p>
		                               	 	</c:if>
		                               	 	<c:if test="${uniprotData.getGoCell() != null && uniprotData.getGoCell().size() > 0}">
			                               	 	<p class="with-label">
			                                    	<span class="label">GO Cellular Component</span>
			                                    	<t:restrictListDisplay title="GO Cellular Component" displayList="${uniprotData.getGoCell()}" numberToDisplay="5"> </t:restrictListDisplay>
			                               	 	</p>
		                               	 	</c:if>
		                               	 	<br/><br/>
		                               	 	<p class="credit"> These annotations were provided by <a href="https://www.uniprot.org/uniprot/${gene.getUniprotHumanCanonicalAcc()}">Uniprot</a>.</p>
		                               	 	<br/>
	                                    </div>
                                    </c:if>
                                    <div>
                                    	<c:if test="${gene.getUniprotHumanCanonicalAcc() != null}">
	                                    	<h4>Domains for canonical protein</h4>	                                    	
	                                    	<iframe id="pfam" src="${baseUrl}/pFam/${gene.mgiAccessionId}"></iframe>
	                                    	<p class="credit"> This image was generated by <a href="http://pfam.xfam.org/protein/${gene.getUniprotHumanCanonicalAcc()}">Pfam</a>. Hover for domain description.</p>
	                                    	<br/> <br/>
	                                    </c:if>
                                    </div>
                                   
                                    <c:if test="${not empty orthologousDiseaseAssociations}">
                                    	<div class="bordertop">
		                                   	<table>                                    
												<thead>
												    <tr>
												        <th><span class="main">Disease Name</span></th>
												        <th><span class="main">Source</span></th>
												        <th>In Locus</th>
												        <th><span class="main">MGI/IMPC</span><span class="sub">Phenotype Evidence</span></th>
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
												                <a id="diseaseId" href="${association.diseaseIdentifier.externalUri}">${association.diseaseIdentifier.toString().split(":")[0]}</a>
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
											<p class="credit"> These associations are provided by <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a>.</p>
										</div>
	                                 </c:if>
									</div>
	                            </div>
	                            
	                            <div class="clear"></div>
	                            <br/>
	                            
	                            <div class="bordertop">
	                            	<br/>
                        			<h3>STRING Predicted Functional Partners (Mouse)</h3>
                        			
                        			<c:if test="${stringDbTable.size() > 0}">
	                        			<div class="half">
		                        			<img src="http://string-db.org/api/image/network?identifier=${gene.markerSymbol}&amp;species=10090">
		                        			<p class="credit"> This network is provided by <a href="http://string-db.org/">STRING</a>.</p> <br/>
	                        			</div>
	                        			<div class="half">
	                        				<table> 
	                        					<thead> <tr> <td>Symbol</td> <td> STRING Score</td> </tr></thead>
	                        					<tbody> 
	                        						<c:forEach var="key" items="${stringDbTable.keySet()}" varStatus="loop" >
	                        							<tr> <td>${key}</td> <td>${stringDbTable.get(key)}</td></tr>
	                        						</c:forEach>                        					
	                        					</tbody>
	                        				</table>
	                        			</div>
	                        		</c:if>
                        			<c:if test="${stringDbTable.size() == 0}">
                        				<div class="alert alert-info">STRING has no information for ${gene.markerSymbol}.</div>
                        			</c:if>
	                        		
                        		</div>
	                            <div class="clear"></div>
	                            
	                            <div class="bordertop"> <!-- EXPRESSION heatmap from ExpressionAtlas -->
	                            	<div id="heatmapContainer" class="bordertop"></div>
	                            </div>
	                            <div class="clear"></div>
	                            
                        	</div>
                        	
                        	
                        <!-- section end -->
                      </div>
                   </div>
                </div>
             </div>
          </div>
      </jsp:body>

	
</t:genericpage>