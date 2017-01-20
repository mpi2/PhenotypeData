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
							<th>Sample Id</th>
							<th>
							OntologyTerm
							</th>
							<th>
							Free Text
							</th>
							
						
							
							
							</tr>
							</thead>
								<c:forEach var="histRow" items="${histopathRows}">
								
								<tr>
									<td id="${histRow.sampleId}_${histRow.anatomyName}">
										${histRow.anatomyName}
									</td>
									<td>
										${histRow.sampleId}
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
										
										${histRow.textValue}
										
									</td>
									
									
	
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
                                           
												<t:impcimghistdisplay img="${image}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimghistdisplay>
											
										<!-- </div> -->
										</c:forEach> 
							</ul>
										</div>
									</div>
								</div>
					</div> --%>
					
					
					
					
					
					
					
					
					
            </div>
        </div>
      <script> 
        $(document).ready(function() {
    $('#histopath').DataTable(
    		{"paging":   false, "searching": false, "order": [[ 2, "desc" ]]});
} );
        </script> 
    </jsp:body>

</t:genericpage>

