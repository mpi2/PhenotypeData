<%--
  Created by IntelliJ IDEA.
  User: ckc
  Date: 23/02/2016
  Time: 10:37
  To change this template use File | Settings | File Templates.
--%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

  	<c:if test="${phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}">
    	<jsp:include page="phenotype_icons_frag.jsp"/>
	</c:if>
   
	<c:if test="${phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}">
    	<div id="phenoSumDiv">
    </c:if>
    <c:if test="${!phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}"> <!-- only display a normal div if no phenotype icons displayed -->
    	<div id="phenoSumSmallDiv">
    </c:if>
    <c:if test="${phenotypeDisplayStatus.postQcTopLevelMPTermsAvailable}">
        <h5 class="sectHint">All Phenotypes Summary</h5>
        <p>Based on automated MP annotations supported by experiments on knockout mouse models. 
    
      		Click on icons to go to all ${gene.markerSymbol} data for that phenotype.
      	</p> 
      	
        </c:if>
        
        <c:if test="${phenotypeDisplayStatus.postQcDataAvailable || phenotypeDisplayStatus.displayHeatmap || bodyWeight }">
	        <div id="all_data" class="with-label">
	        
		        <span class="label">All Data:</span> 
		         <c:if test="${bodyWeight}">
		        <a id="bodyWeightBtn" class="btn" href="${baseUrl}/charts?accession=${acc}&parameter_stable_id=IMPC_BWT_008_001&&chart_type=TIME_SERIES_LINE" title="Body Weight Curves">Body Weight Data</a>
		        </c:if>
		        <c:if test="${phenotypeDisplayStatus.postQcDataAvailable}">
		            <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->
		            <a id="allAdultDataBtn" class="btn" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}' title="All Data">${gene.markerSymbol} Measurements</a>
		        </c:if>
		       
				<c:if test="${phenotypeDisplayStatus.displayHeatmap}">
		        	<%-- <jsp:include page="heatmapFrag.jsp"/> split out from frag as need to display heatmap in different location to button --%>
		        	<c:if test="${phenotypeStarted}">
	 						<a id="heatmap_link" class="btn">Heatmap / Table</a>
					
					</c:if>
		        </c:if>
		       
	        </div>
	        
	        				
								
        </c:if>
        <c:if test="${gene.embryoDataAvailable || gene.embryoAnalysisUrl!=null || gene.dmddImageDataAvailable || hasVignette}">
        	<div id="embryo" class="with-label">
        	<span class="label">Embryo Data: </span>
		        <c:if test="${gene.embryoDataAvailable}">
		            <a id="embryoViewerBtn" class="btn" href="${drupalBaseUrl}/embryoviewer/?mgi=${acc}" title="3D Embryo Images are Available">3D Embryo Imaging</a>
		        </c:if>
		        
		        <c:if test="${gene.embryoAnalysisUrl!=null}">
		            <a id="embryoAnalysisBtn" class="btn" href="${gene.embryoAnalysisUrl}" title="Automated 3D Volumetric Analysis of Embryo images">3D Embryo Vol Analysis</a>
		        </c:if>
		        
		        <c:if test="${gene.dmddImageDataAvailable}">
		            <a id="DmddViewerBtn" class="btn" href="https://dmdd.org.uk/mutants/${gene.markerSymbol}" target="_blank" title="Embryo Images and Manual Phenotypes from the DMDD project" >DMDD Images and Phenotypes</a>
		        </c:if> 
		
		        <c:if test="${hasVignette}">
		            <a class="btn" href="${baseUrl}/embryo/vignettes#${acc}" title="embryo vignette exists for this gene">Embryo Vignette</a>
		        </c:if>
			</div>
		</c:if>			
				
				
				<c:if test="${phenotypeDisplayStatus.displayHeatmap}">
		        <div id="heatmap_toggle_div" class="section hidden">
							<h2 class="title" id="heatmap">Phenotype Heatmap of Preliminary Data
								<span class="documentation"><a href='' id='heatmapSection' class="fa fa-question-circle pull-right"></a></span>
							</h2>
	
							<div class="dcc-heatmap-root">
								<div class="phenodcc-heatmap"
									 id="phenodcc-heatmap"></div>
							</div>
				</div><!-- end of Pre-QC phenotype heatmap -->
			</c:if>
       

       
    </div>
  

						
    <c:if test="${!phenotypeDisplayStatus.eitherPostQcOrPreQcSignificantDataIsAvailable}"><!-- no significant postQC data or preQcData-->
    
	   <%--  <c:choose> --%>
	    	<c:if test="${ attemptRegistered && phenotypeStarted }"> 
	    		No results meet the p-value threshold
	 		</c:if>
	 		<%-- <c:if test="${phenotypeDisplayStatus.postQcDataAvailable}">
				 		No significant phenotype associations were found with data that has
			          passed quality control (QC), but you can click
			          on the "All Adult Data" button to see all phenotype data that has
			          passed QC. Preliminary phenotype assocations
			          may appear with new pre-QC phenotype data.
	 		</c:if> --%>
	         <%-- <p> No hits that meet the p value threshold. <jsp:include page="heatmapFrag.jsp"/></p> --%>
	         <c:if test="${ attemptRegistered && !phenotypeStarted }"> 
		        <div class="alert alert-info">
		          <h5>Registered for phenotyping</h5>
		
		          <p>Phenotyping is planned for a knockout strain of this gene but
		            data is not currently available.</p>
		        </div>
		    </c:if>
	    
	    	
	    	<c:if test="${!attemptRegistered}"> 
		        <div class="alert alert-info">
		          <h5>Not currently registered for phenotyping</h5>
		
		          <p>Phenotyping is currently not planned for a knockout strain of this gene.
		          </p>
		        </div>
		    </c:if>
		      
		      	<br/>
	  </c:if> 
	    	
	    	
   
     

     
    <%-- <c:if test="${phenotypeDisplayStatus.postQcDataAvailable && !phenotypeDisplayStatus.eitherPostQcOrPreQcSignificantDataIsAvailable}"> don't think we need this section now??
      <div class="alert alert-info">
        <h5>No Significant Phenotype Associations Found</h5>

        <p>No significant phenotype associations were found with data that has
          passed quality control (QC), but you can click
          on the "All Adult Data" button to see all phenotype data that has
          passed QC. Preliminary phenotype assocations
          may appear with new pre-QC phenotype data.</p>
      </div>
      <br/>
      <!-- best example http://localhost:8080/PhenotypeArchive/genes/MGI:1913955 -->
      <div class="floatright marginup" style="clear: both">
        <a id="allAdultDataBtn" class="btn" href='${baseUrl}/experiments?geneAccession=${gene.mgiAccessionId}'>All Adult Data</a>
      </div>
        <div class="clear"></div>
    </c:if> --%>

  <%--   <c:if
            test="${gene.embryoDataAvailable}">
      <div class="floatright marginup"
           style="clear: both">
        <a class="btn"
           href="${drupalBaseUrl}/embryoviewer?mgi=${acc}">3D Imaging</a>
      </div>
    </c:if> --%>
  <%-- </c:when> --%>
  <%-- <c:when test="${hasPreQcThatMeetsCutOff}"> --%>
    <!-- Only pre QC data available, suppress post QC phenotype summary -->
 <%--  </c:when>
  <c:otherwise> --%>
   <%--  <div class="alert alert-info">There are currently no IMPC phenotype associations
      for the gene ${gene.markerSymbol} </div>
    <br/> --%>
 <%--  </c:otherwise> --%>
<%-- </c:choose> --%>

<div id="phenotypes"></div> <!-- Empty anchor for links, used for disease paper. Don't remove.  -->


<c:if
        test='${hasPreQcThatMeetsCutOff || rowsForPhenotypeTable.size() > 0}'>
  <!-- Associations table -->
  <div id="phenotypeTableDiv" class="inner-division">
  <h5>Significant Phenotypes</h5>


  <div class="row-fluid">
    <div class="container span12">
      <br/>

      <div class="row-fluid" id="phenotypesDiv">

        <div class="container span12">
 
          <c:if test="${not empty rowsForPhenotypeTable}">
            <form class="tablefiltering no-style" id="target" action="destination.html">
           
              <c:forEach
                      var="phenoFacet" items="${phenoFacets}"
                      varStatus="phenoFacetStatus">
                <select id="${phenoFacet.key}" class="impcdropdown"
                        multiple="multiple"
                        title="Filter on ${phenoFacet.key}">
                  <c:forEach
                          var="facet" items="${phenoFacet.value}">
                    <option>${facet.key}</option>
                  </c:forEach>
                </select>
              </c:forEach>
           
              <div class="clear"></div>
            </form> 
            <div class="clear"></div>

            <c:set var="count" value="0" scope="page"/>
            <c:forEach
                    var="phenotype" items="${rowsForPhenotypeTable}"
                    varStatus="status">
              <c:forEach
                      var="sex" items="${phenotype.sexes}">
                <c:set var="count" value="${count + 1}" scope="page"/>
              </c:forEach>
            </c:forEach>

            <jsp:include page="PhenoFrag.jsp"></jsp:include>
            <br/>

              <div id="export">
                  <p class="textright">
                      Download data as:
                      <a id="tsvDownload" href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=tsv&fileName=${gene.markerSymbol}" target="_blank" class="button fa fa-download">TSV</a>
                      <a id="xlsDownload" href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=xls&fileName=${gene.markerSymbol}" target="_blank" class="button fa fa-download">XLS</a>
                  </p>
              </div>

          </c:if>

        </div>
      </div>
    </div>
  </div>

</div><!-- end of div for mini section line -->
</c:if>

