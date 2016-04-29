<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Histopath Summary for ${gene.markerName}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search/genes?kw=*">Genes</a> &raquo; Results</jsp:attribute>

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
                    
                    
                    
                    
                    <div class="section">
							<div class="inner">
							 ${gene.markerSymbol}: ${gene.markerName}
							 
							 <table id="histopath" class="table tableSorter">
							
							<thead>
							<tr>
							
							
							
							
							<th class="headerSort">
							Anatomy
							</th>
							<th>
							Significance
							</th>
							<th>
							PATO
							</th>
							<th>
							Process
							</th>
							<th>
							Diagnostic
							</th>
							<th>
							Description
							</th>
							<th>
							Free Text
							</th>
							<th>
							Images
							</th>
						
							
							
							</tr>
							</thead>
								<c:forEach var="histRow" items="${histopathRows}">
								
								<tr>
									<td>
										${histRow.anatomyName}
									</td>
									<td>
									<c:forEach var="parameter" items="${histRow.significance }">
										
											${parameter.textValue }
										
									</c:forEach>
									</td>
									<%-- <td>
									<c:forEach var="parameter" items="${histRow.severity }">
										
											${parameter.textValue }
										
									</c:forEach> 
									</td> --%>
									
									<c:choose>
									<c:when test="${fn:length(histRow.patoOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.patoOntologyBeans }">
										<td title="${value.description }">
											
										
										<c:forEach var="value" items="${parameter.value }">
											${value.name }											
											
										</c:forEach>
										</td>
									</c:forEach>
									</c:otherwise>
									</c:choose> 
									
									
									<c:choose>
									<c:when test="${fn:length(histRow.mpathProcessOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.mpathProcessOntologyBeans }">
										
											
										<td>
									<!-- do for each here values-->
										<c:forEach var="value" items="${parameter.value }">
											 <%-- <td title="${value.description }"> --%>
													${value.name }
											<%-- </td> --%>
										</c:forEach>
										</td>
									</c:forEach> 
									</c:otherwise>
									</c:choose>
									
									<c:choose>
									<c:when test="${fn:length(histRow.mpathDiagnosticOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.mpathDiagnosticOntologyBeans }">
										
											
										<td>
										<c:forEach var="value" items="${parameter.value }">
										<%-- <td title="${value.description }"> --%>
											${value.name }										
											<%-- </td> --%>
										</c:forEach>
										</td>
									</c:forEach>
									</c:otherwise>
									</c:choose> 
									
									
									<td>
										<c:forEach var="parameter" items="${histRow.descriptionTextParameters }">
										
										${parameter.textValue }
										
										</c:forEach>
									</td> 
									<td>
										<c:forEach var="parameter" items="${histRow.freeTextParameters }">
										
										${parameter.textValue }
										
										</c:forEach> 
									</td>
									
									<td>
										<c:forEach var="image" items="${histRow.imageList }">
										
										<%-- <img src="${impcMediaBaseUrl}render_thumbnail/${image.omeroId}/200"/>  --%>
										<%-- increment= ${image.increment_value} --%>
										 <!-- <div id="grid"> -->
                                           
												<t:impcimghistdisplay img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimghistdisplay>
											
										<!-- </div> -->
										</c:forEach> 
									</td>
									
									
									
								</tr>
								
								</c:forEach>
								
							
							</table>	
							
							
							</div>
						
					</div>
                    
                    
                    
                    
          <%--           <div class="section">
								<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>
								<div class="inner">

									

                     
                     <table>
                     
                      <c:forEach var="obs"
										items="${extSampleIdToObservations}">
                      
                      	<tr>
                      		<td>
                      			${obs.externalSampleId }
                      		</td>
                      		<td>
                      			${obs.observationType }
                      		</td>
                      		<td>
                      			${obs.parameterName }
                      		</td>
                      		<td>
                      			${obs.category }
                      		</td>
                      		<td>
                      			${obs.textValue }
                      		</td>
                      		<td>
                      			${obs.subTermId }
                      		</td>
                      		<td>
                      			name: ${obs.subTermName }
                      		</td>
                      		<td>
                      			${obs.subTermDescription }
                      		</td>
                      	</tr>
                      	
                      </c:forEach>
                      
                      </table>
                      
                      
                     
							</div>
						</div> --%>
						
						
						
						
                    </div>
                </div>
            </div>
        </div>
      <script> 
        $(document).ready(function() {
    $('#histopath').DataTable(
    		{"paging":   false, "searching": false});
} );
        </script> 
    </jsp:body>

</t:genericpage>

