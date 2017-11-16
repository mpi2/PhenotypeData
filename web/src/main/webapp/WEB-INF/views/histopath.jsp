<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Histopath Information for ${gene.markerName}</jsp:attribute>

<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/histopathsum/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Histopathology Detailed View</jsp:attribute>


	<jsp:attribute name="header">

       
    </jsp:attribute>


	<jsp:attribute name="addToFooter">
   

        <%-- <div class="region region-pinned">

            <div id="flyingnavi" class="block">

                <a href="#top"><i class="fa fa-chevron-up"
					title="scroll to top"></i></a>

                <ul>
                    <c:if test="${imageCount ne 0}">
                        <li><a href="#top">Images</a></li>
                        </c:if>
                </ul>

                <div class="clear"></div>

            </div>

        </div> --%>
    </jsp:attribute>

	<jsp:body>
        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                    
                     <h2 class="title "
									id="section-associations">Histopathology for ${gene.markerSymbol}<%-- : ${gene.markerName} --%></h2>
                     
                    
                    
                    <div class="section">
							<div class="inner">
							 ${gene.markerSymbol}: ${gene.markerName}
							 
							 <table id="histopath" class="table tableSorter">
							
							<thead>
							<tr>
							
							<th>
							Age in Weeks
							</th>
							<th>
							Zygosity
							</th>
							<th>
							Sex
							</th>
							<th>
							SampleId
							</th>
							<th> <%-- class="headerSort"> --%>
							Tissue
							</th>
							<th>
							MPATH Process Term
							</th>
							
							<th>
							Severity
							</th>
							<th>
							Significance
							</th>
							<th>
							MPATH Diagnostic
							</th>
							<th>
							PATO Descriptor
							</th>
							<%-- <th>
							Diagnostic
							</th> --%>
							<%-- <th>
							Description
							</th> --%>
							<th>
							Free Text
							</th>
							
							<%-- <th>
							Images
							</th> --%>
							<%-- <th>Sample Id</th> --%>
						
							
							
							</tr>
							</thead>
								<c:forEach var="histRow" items="${histopathRows}">
								
								<tr>
									<td>
									${histRow.ageInWeeks}
									</td>
									<td>
									${histRow.zygosity}
					
									</td>
									<td>
									 <c:if test="${histRow.sex eq 'female'}">
									<img alt="Female"  title="female" src="${baseUrl}/img/female.jpg"/>
									 </c:if>
									  <c:if test="${histRow.sex eq 'male'}">
									 <img alt="Male" title="male" src="${baseUrl}/img/male.jpg"/>
									 </c:if>
									</td>
									<td>
									Mouse ${histRow.sampleId}<%-- / ${histRow.sequenceId} --%>
									
									</td>
									<td id="${histRow.anatomyName}">
										${histRow.anatomyName}
									</td>
									
									<td>
										<c:choose>
											<c:when test="${fn:length(histRow.mpathProcessOntologyBeans) == 0}">
										
											</c:when>
											<c:otherwise>
												<c:forEach var="parameter" items="${histRow.mpathProcessOntologyBeans }">
													<!-- do for each here values-->
													<c:forEach var="value" items="${parameter.value }">
											 		<%-- <td title="${value.description }"> --%>
										
														${value.name }
														[${value.id }]
													<%-- </td> --%>
													</c:forEach>
										
												</c:forEach> 
											</c:otherwise>
										</c:choose>
										
										
										
									</td>

									
									<%-- <td>
									${histRow.sequenceId}
									</td> --%>
									
									<td>
									<c:forEach var="parameter" items="${histRow.severity }">
										
										<%-- <c:choose>
											<c:when test="${parameter.textValue==0}">
											Normal
											</c:when>
											<c:when test="${parameter.textValue==1 }">
											Mild
											</c:when>
											<c:when test="${parameter.textValue==2 }">
											Moderate
											</c:when>
											<c:when test="${parameter.textValue==3 }">
											Marked
											</c:when>
											<c:when test="${parameter.textValue==4 }">
											Severe
											</c:when>
										</c:choose> --%>
										${parameter.textValue}
									</c:forEach> 
									</td>
									
									
									<td>
									
									<c:if test="${fn:length(histRow.significance) ==0 }">
									Not Annotated
									</c:if>
									<c:forEach var="parameter" items="${histRow.significance }">
										<c:choose>
											<c:when test="${parameter.textValue eq 'Significant'}">
											1
											</c:when>
											<c:otherwise>
											0
											</c:otherwise>
										</c:choose>
										
									</c:forEach>
									</td>
									
									<c:choose>
									<c:when test="${fn:length(histRow.mpathDiagnosticOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.mpathDiagnosticOntologyBeans }">
										
											
										<td>
										<c:forEach var="value" items="${parameter.value }">
										<td title="${value.description }">
											${value.name }										
											</td>
										</c:forEach>
										</td>
									</c:forEach>
									</c:otherwise>
									</c:choose>
									
									<td>	
										<c:choose>
											<c:when test="${fn:length(histRow.patoOntologyBeans) == 0}">
										
											</c:when>
										<c:otherwise>
										
											<c:forEach var="parameter" items="${histRow.patoOntologyBeans }">
												<c:forEach var="value" items="${parameter.value }">
													${value.name }											
												</c:forEach>
											</c:forEach>
										</c:otherwise>
										</c:choose> 
									
									</td>
										
									
									<%-- <td>
										<c:forEach var="parameter" items="${histRow.descriptionTextParameters }">
										
										${parameter.textValue }
										
										</c:forEach>
									</td>  --%>
									<td>
										<c:forEach var="parameter" items="${histRow.freeTextParameters }">
										
										${parameter.textValue }
										
										</c:forEach> 
									</td>
									
									
									
									<%-- <td>
										<c:forEach var="image" items="${histRow.imageList }"> --%>
										
										<%-- <img src="${impcMediaBaseUrl}render_thumbnail/${image.omeroId}/200"/>  --%>
										<%-- increment= ${image.increment_value} --%>
										 <!-- <div id="grid"> -->
                                           
												<%-- <t:impcimghistdisplay img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimghistdisplay> --%>
											
										<!-- </div> -->
										<%-- </c:forEach> 
									</td> --%>
									<%-- <td id="${histRow.sampleId}_${histRow.anatomyName}">
										${histRow.sampleId}
									</td> --%>
									
									
									
								</tr>
								
								</c:forEach>
								
							
							</table>	
							
						</div>
					</div>
					
					
					
					
					
					
					<div  class="section">
								<div class="inner">
									<div class="accordion-body" style="display: block">
										<div id="grid">
										<ul>
											<c:forEach var="image" items="${histopathImagesForGene }">
												
													<t:impcimgdisplay2 img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2> 
															 
											</c:forEach> 
										</ul>
										</div>
									</div>
								</div>
					</div>
                    
                    
		
						
                    </div>
                </div>
            </div>
        </div>
      <script> 
        $(document).ready(function() {
    $('#histopath').DataTable(
    		{"paging":   false, "searching": false, "order": [[ 3, "asc" ]]});
} );
        </script> 
    </jsp:body>

</t:genericpage>

