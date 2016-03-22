<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Histopath Information for ${gene.markerName}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search/impc_images?kw=*">IMPC Images</a> &raquo; Results</jsp:attribute>

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
							 ${gene.markerName}
							 
							 <table id="histopath" class="table tableSorter">
							
							<thead>
							<tr>
							<th>Sample Id</th>
							
							
							
							<th class="headerSort">
							Anatomy
							</th>
							<th>
							Significance
							</th>
							<th>
							Severity
							</th>
							<th>
							Process
							</th>
							<th>
							Diagnostic
							</th>
							<th>
							PATO
							</th>
							<th>
							Description
							</th>
							<th>
							Free Text
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
									<td>
									<c:forEach var="parameter" items="${histRow.significance }">
										
											${parameter.textValue }
										
									</c:forEach>
									</td>
									<td>
									<c:forEach var="parameter" items="${histRow.severity }">
										
											${parameter.textValue }
										
									</c:forEach> 
									</td>
									
									<c:choose>
									<c:when test="${fn:length(histRow.mpathProcessOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.mpathProcessOntologyBeans }">
										 <td title="${parameter}">
											
										
									<!-- do for each here values-->
										<c:forEach var="value" items="${parameter.value }">
											
											${value.name }:
											
											${value.description }
											
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
										<td title="${parameter.value }">
											
										
										<c:forEach var="value" items="${parameter.value }">
											${value.name }:											
											${value.description }
										</c:forEach>
										</td>
									</c:forEach>
									</c:otherwise>
									</c:choose> 
									
									<c:choose>
									<c:when test="${fn:length(histRow.patoOntologyBeans) == 0}">
										<td>
										</td>
									</c:when>
									<c:otherwise>
									<c:forEach var="parameter" items="${histRow.patoOntologyBeans }">
										<td title="${parameter.value }">
											
										
										<c:forEach var="value" items="${parameter.value }">
											${value.name }:											
											${value.description }
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
    </jsp:body>

</t:genericpage>

