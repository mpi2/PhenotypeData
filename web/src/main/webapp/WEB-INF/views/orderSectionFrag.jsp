<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- just for testing with styles remove -->
<c:if test="${orderRows.size() > 0}">
	<c:if test="${creLine}">
		<c:set var="creLineParam" value="&creLine=true"/>
	</c:if>
<table>        
        <thead>
        		 <tr>
                        <th style="border-bottom:0px;"></th>
                        <th style="border-bottom:0px;"></th>
                        <th colspan="3" style="border-bottom:0px;  text-align:center;">Targeting Detail</th>
                        <th colspan="3" style="border-bottom:0px; text-align:center;">Product Ordering</th>
                        
                      
                </tr>
                <tr>
                        <th>MGI Allele</th>
                        <th style="width:22%">Allele Type</th>
                        
                        <th >Type</th>
                        <th>Map</th>
                        <th>Seq
                        <th>Vector</th>
                        <th>ES Cell</th>
                        <th>Mouse</th>
                      
                </tr>
        </thead>
        <tbody>
                <c:forEach var="row" items="${orderRows}" varStatus="status">
                <c:set var="rowSpan" value="2"></c:set>
                <c:if test="${row.geneMapLink || row.vectorMapLink }">
                	<c:set var="rowSpan" value="2"></c:set>
                </c:if>
                      <tr>
                        <!-- /alleles/MGI:2443967/tm1a(EUCOMM)Hmgu -->
                         <td rowspan="${rowSpan}">
                         	<a href="${baseUrl}/alleles/${acc}/${row.alleleName}?${creLineParam}">${row.markerSymbol}<sup>${row.alleleName}</sup></a>
                         </td>
                         <%-- <td>
                         	${row.strainOfOrigin}
                         </td> --%>
                         <td rowspan="${rowSpan}">
                         	${row.alleleDescription}
                         </td>
                          
                          
                          
                               
                               <td style="text-align: center;">Vector</td>
                               <td>
                               			<a class="fancybox" target="_blank" style="text-align:right" href="${row.vectorMapLink}" fullRes="${row.vectorMapLink}" original="${row.vectorMapLink}">
	                                   
	                                   		<i class="fa fa-th-list fa-lg" title="Image"></i>
	                                   
	                                   </a>
	                            </td>
	                            <td >
	                                <c:if test="${not empty row.vectorGenbankLink}">
		                               		<a href="${row.vectorGenbankLink}" target="_blank"><i class="fa fa-file-text fa-lg" title="Genbank File"></i></a>
	                               	</c:if>
	                            </td>
	                              
                               
                              
                              
                               <%-- <c:if test="${not empty alleleProduct['genbank_file']}">
                               	<div style="padding:3px;"><a href="${alleleProduct['genbank_file']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                               </c:if> --%>
                          	
                          
                          <td style="text-align: center;" rowspan="${rowSpan}">
	                          <c:if test="${row.targetingVectorAvailable}">
	                          	<a class="iFrameFancy btn" data-url="${baseUrl}/order?acc=${acc}&allele=${row.alleleName}&type=targeting_vector${creLineParam}&bare=true"><i class="fa fa-shopping-cart"></i></a>
	                          	<%-- <a class="iFrameFancy" style="text-align:right" data-url="${baseUrl}/order?acc=${acc}&allele=${row.alleleName}&type=targeting_vector" >
		                                   
		                                   		<i class="fa fa-th-list fa-lg" title="Image"></i>
		                                   
		                                   </a> --%>
	                          </c:if>
                          </td>
                         
                          <td style="text-align: center;" rowspan="${rowSpan}">
                           
	                           <c:if test="${row.esCellAvailable}">
	                          	<a class="iFrameFancy btn" data-url="${baseUrl}/order?acc=${acc}&allele=${row.alleleName}&type=es_cell${creLineParam}&bare=true"><i class="fa fa-shopping-cart"></i></a>
	                          </c:if>
                          </td>
                          
                          <td style="text-align: center;" rowspan="${rowSpan}">
	                           <c:if test="${row.mouseAvailable}">
	                          	<a class="iFrameFancy btn" data-url="${baseUrl}/order?acc=${acc}&allele=${row.alleleName}&type=mouse${creLineParam}&bare=true"><i class="fa fa-shopping-cart"></i></a>
	                           </c:if>
                          </td> 
                        </tr>
                        <%-- <c:if test="${rowSpan==2 }"> --%>
                         
                        <tr>
                     
                        
		                            <td style="text-align: center;">Gene</td>
                               		<td >
                               			<c:if test="${not empty row.geneMapLink}">
	                               			<a class="fancybox" target="_blank" style="text-align:right" href="${row.geneMapLink}" fullRes="${row.geneMapLink}" original="${row.geneMapLink}">
	                                   			<i class="fa fa-th-list fa-lg" title="Image"></i>
	                                   		</a>
	                                   </c:if>
	                                </td>
	                                <td>
	                                	<c:if test="${not empty row.geneGenbankLink}">
		                               		<a href="${row.geneGenbankLink}" target="_blank"><i class="fa fa-file-text fa-lg" title="Genbank File"></i></a>
		                                
	                               		</c:if>
	                               	</td>
	                               	
                       
                        
                        </tr>    
                        
                       <%--  </c:if> --%>


                </c:forEach>
        </tbody>

</table>
</c:if>


<c:choose>
    <c:when test="${creLineAvailable}">
        <div><a href="${baseUrl}/order/creline?acc=${acc}" target="_blank">Cre Knockin ${alleleProductsCre2.get("product_type")} are available for this gene.</a></div>       
    </c:when>
</c:choose>
                            	