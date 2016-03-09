<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
							 Histopath page here ${gene.markerName}
							 
								<c:forEach var="histRow" items="${histopathRows}">
								<table>
								<tr>
									<td>
										${histRow.sampleId}
									</td>
									<c:forEach var="entry" items="${histRow.subOntologyBeans}">
									<td>
									Key=${entry.key}
										<c:forEach var="subOntology" items="${ entry.value}">
									
											subOntology= ${subOntology.id } : ${subOntology.name } <%-- description= ${subOntology.description} --%>
									
										</c:forEach>
										</td>
									</c:forEach>
									<td>
										<c:forEach var="textParam" items="${histRow.textParameters}">
										<td>
											 Parameter:${textParam.parameter.name} Text: ${textParam.textValue }
										</td>
										</c:forEach>
									
									
								</tr>
								</table>	
								</c:forEach>
							
							
							
							</div>
						
					</div>
                    
                    
                    
                    
                    <div class="section">
								<%--<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>--%>
								<div class="inner">

									

                     
                      <table>
                     
                      <c:forEach var="entry"
										items="${extSampleIdToObservations}">
                       <c:forEach var="obs" items="${entry.value}">
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

