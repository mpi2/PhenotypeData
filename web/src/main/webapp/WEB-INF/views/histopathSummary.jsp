<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">Histopath Summary for ${gene.markerName}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo; Histopath</jsp:attribute>

	<jsp:attribute name="header">

       
    </jsp:attribute>


	<jsp:attribute name="addToFooter">
   

    </jsp:attribute>

	<jsp:body>
        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                    
                     <h2 class="title "
									id="section-associations"> Abnormal Histopathology Summary for ${gene.markerSymbol}<%-- : ${gene.markerName} --%></h2>
                     
                    
                    <div class="section">
                    
                   
							<div class="inner">
							
							 
							 <table id="histopath" class="table tableSorter">
							
							<thead>
							<tr>
							
							
							
							
							<th class="headerSort">
							Tissue
							</th>
							
							<th>
							MPATH Process Term
							</th>
							
							<%-- <th>
							Diagnostic
							</th> --%>
							<%-- <th>
							Description
							</th> --%>
							
							<th>
							Significant Finding Incidence Rate
							</th>
							<th>
							Data
							</th>
						
							
							
							</tr>
							</thead>
								<c:forEach var="histRow" items="${histopathRows}">
								
								<tr>
									<td>
										${histRow.anatomyName}
												
										<%-- <c:choose>
											<c:when test="${fn:length(histRow.patoOntologyBeans) == 0}">
										
											</c:when>
										<c:otherwise>
											<c:forEach var="parameter" items="${histRow.patoOntologyBeans }">
											,
												<c:forEach var="value" items="${parameter.value }">
													${value.name }											
												</c:forEach>
											</c:forEach>
										</c:otherwise>
										</c:choose> 
									 --%>
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
													<%-- </td> --%>
													</c:forEach>
										
												</c:forEach> 
											</c:otherwise>
										</c:choose>
										
									</td>
										
									
										
										
										
									</td>
									
						
									
									<%-- <c:choose>
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
									</c:choose>  --%>
									
									
									<%-- <td>
										<c:forEach var="parameter" items="${histRow.descriptionTextParameters }">
										
										${parameter.textValue }
										
										</c:forEach>
									</td>  --%>
									<%-- <td>
										<c:forEach var="parameter" items="${histRow.freeTextParameters }">
										
										${parameter.textValue }
										
										</c:forEach> 
									</td> --%>
									<td>
											${histRow.significantCount } / ${histRow.nonSignificantCount + histRow.significantCount }
									
									</td>
									
									<td>
									 <a  href='${baseUrl}/histopath/${gene.mgiAccessionId}#${histRow.sampleId}_${histRow.anatomyName}' title='All Histopath Data for this Gene'><i class="fa fa-table" alt="All Histopath Data"></i>
									 </a>
										<%-- <c:choose>
										<c:when test="${histRow.hasImages}">
										Yes
										</c:when>
										<c:otherwise>
										No
										</c:otherwise>
							
										</c:choose> --%>
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

