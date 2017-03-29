<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Gross Pathology Information for ${gene.markerName}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Gross Pathology And Tissue Collection View</jsp:attribute>

	<jsp:attribute name="header">

       
    </jsp:attribute>


	<jsp:attribute name="addToFooter">
   

        <div class="region region-pinned">

            <div id="flyingnavi" class="block smoothScroll">

                <a href="#top"><i class="fa fa-chevron-up"
					title="scroll to top"></i></a>

                <ul>
                    <c:if test="${imageCount ne 0}">
                        <li><a href="#top">Images</a></li>
                        </c:if>
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
                    
                    
                    
                    
                    <div class="section">
							<div class="inner">
							 ${gene.markerSymbol}: ${gene.markerName}
							 
							 <table id="histopath" class="table tableSorter">
							
							<thead>
							<tr>
							<th>
							Anatomy
							</th>
							<%-- <th>Sample Id</th> --%>
							<th>
							Zyg
							</th>
							<th>
							OntologyTerm
							</th>
							<th>
							Free Text
							</th>
							<th>
							Images
							</th>
							
						
							
							
							</tr>
							</thead>
								<c:forEach var="pathRow" items="${pathRows}">
								
								<tr>
									<td id="${pathRow.sampleId}_${pathRow.anatomyName}">
										${pathRow.anatomyName}
									</td>
									<%-- <td>
										${pathRow.sampleId}
									</td> --%>
									<td>
										${pathRow.zygosity}
									</td>
									<c:choose>
									<c:when test="${fn:length(pathRow.subOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${pathRow.subOntologyBeans }">
										<td >
											<%-- subOntologyParam: ${parameter.key} gives anatomy term which should match the anatomy in the row --%>
											
										
										<c:forEach var="value" items="${parameter.value }" varStatus="loop"  >
										<%-- ${value.id } - --%> ${value.name }<c:if test="${!loop.last}">,</c:if>
										</c:forEach>
										</td>
									</c:forEach>
									</c:otherwise>
									</c:choose> 
									
									
									<td>
										
										${pathRow.textValue}
										
									</td>
									
									<c:if test="${fn:length(pathRow.imageList) >0}">
										<%-- <tr> --%>
											<c:forEach var="img" items="${pathRow.imageList }">
												<td>
																			
												  	<%-- <div id="grid">
		                                           
														<t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
													
													</div> --%>
													
													<a href="${impcMediaBaseUrl}/render_image/${img.omero_id}/" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}/" original="${impcMediaBaseUrl}/archived_files/download/${img.omero_id}/">
         		<img  src="${impcMediaBaseUrl}/render_birds_eye_view/${img.omero_id}/" class="thumbnailStyle"></a>
         		
												</td>
											</c:forEach>
										
									<%-- </tr>	 --%> 
									</c:if>
									
									
	
								</tr>
								
								</c:forEach>
								
							
							</table>	
							
							
							
                    
                    
                    
                    
                    
                
						
						
						
						
                    </div>
                </div>
                
                
                
               <%--  <div  class="section">
								<div class="inner">
									<div class="accordion-body" style="display: block">
										<div id="grid">
										<ul>
										<c:forEach var="image" items="${images }">
										
										
										
										 <!-- <div id="grid"> -->
                                           
												<t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
											
										<!-- </div> -->
										</c:forEach> 
							</ul>
										</div>
									</div>
								</div>
					</div>  --%>
					
					
					
					
					
					
					
					
					
            </div>
        </div>
      <script> 
        $(document).ready(function() {
    $('#histopath').DataTable(
    		{"paging":   false, "searching": false, "order": [[ 0, "asc" ]]});
} );
        </script> 
    </jsp:body>

</t:genericpage>

