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
							Significant
							/Non Sig
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
										<a title='Click for detailed view' href='${baseUrl}/histopath/${gene.mgiAccessionId}#${histRow.sampleId}_${histRow.anatomyName}'>${histRow.anatomyName}</a>
									</td>
									<td>
											${histRow.significantCount } / ${histRow.nonSignificantCount }
									
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
										<c:choose>
										<c:when test="${histRow.hasImages}">
										Yes
										</c:when>
										<c:otherwise>
										No
										</c:otherwise>
							
										</c:choose>
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

