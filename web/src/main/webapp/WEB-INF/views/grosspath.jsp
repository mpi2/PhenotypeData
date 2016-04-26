<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Gross Pathology Information for ${gene.markerName}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search/genes?kw=*">Genes</a> &raquo; Results</jsp:attribute>

	<jsp:attribute name="header">

       
    </jsp:attribute>


	<jsp:attribute name="addToFooter">
   

        <div class="region region-pinned">

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
							<th>Sample Id</th>
							
							
							
							<th class="headerSort">
							Anatomy
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
								<c:forEach var="histRow" items="${histopathRows}">
								
								<tr>
									<td>
										${histRow.sampleId}
									</td>
									<td>
										${histRow.anatomyName}
									</td>
									
								
									
									<c:choose>
									<c:when test="${fn:length(histRow.subOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.subOntologyBeans }">
										<td >
											<%-- subOntologyParam: ${parameter.key} gives anatomy term which should match the anatomy in the row --%>
											
										
										<c:forEach var="value" items="${parameter.value }">
										${value.id } - ${value.name }
										</c:forEach>
										</td>
									</c:forEach>
									</c:otherwise>
									</c:choose> 
									
								
									
									
									
									
									<td>
										<c:forEach var="textParam" items="${histRow.textParameters}">
										<%-- <c:if test="${textParam.parameter.name eq parameterName }"> --%>
										
											 Parameter:${textParam.parameter.name} Text: ${textParam.textValue }
										
										<%-- </c:if> --%>
										
										</c:forEach>
									</td>
									
									<td>
										<c:forEach var="image" items="${histRow.imageList }">
										
										<%-- <img src="${impcMediaBaseUrl}render_thumbnail/${image.omeroId}/200"/>  --%>
										increment= ${image.increment_value}
										 <!-- <div id="grid"> -->
                                           
												<t:impcimghistdisplay img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimghistdisplay>
											
										<!-- </div> -->
										</c:forEach> 
									</td>
									
									<%-- <c:forEach var="parameter" items="${histRow.categoryList }">
										<td>
											category= ${parameter }
										</td>
									</c:forEach> 

									<c:forEach var="parameter" items="${histRow.subOntologyBeans }">
										<td>
											ont= ${parameter }
										</td>
									</c:forEach>  --%>
									
									
									<%--  <td>
									 <div style="height: 100px; overflow:auto">
										<c:forEach var="entry" items="${histRow.subOntologyBeans}">
									
											<c:if test="${entry.key eq parameterName }">
												<c:forEach var="subOntology" items="${ entry.value}">													
														${subOntology.id } : ${subOntology.name } description= ${subOntology.description}	
												</c:forEach>
										
											</c:if>
										</c:forEach>
									
									
										<c:forEach var="category" items="${histRow.categoryList}">
										<c:if test="${category.parameter.name eq parameterName }">
										
											 Parameter:${category.parameter.name} experim  ${category.textValue }
										
										</c:if>
										</c:forEach>
									
									
									
									
									
										<c:forEach var="textParam" items="${histRow.textParameters}">
										<c:if test="${textParam.parameter.name eq parameterName }">
										
											 Parameter:${textParam.parameter.name} Text: ${textParam.textValue }
										
										</c:if>
										
										</c:forEach>
									</div>
									</td> --%>
									<%-- </c:forEach> --%>
									
									
								</tr>
								
								</c:forEach>
								
							
							</table>	
							
							
							</div>
						
					</div>
                    
                    
                    
                    
                    <div class="section">
								<%--<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>--%>
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
						</div>
						
						
						
						
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

