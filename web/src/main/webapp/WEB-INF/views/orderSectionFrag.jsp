<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- just for testing with styles remove -->
<c:if test="${orderRows.size() > 0}">
	<c:if test="${creLine}">
		<c:set var="creLineParam" value="&creLine=true"/>
	</c:if>
<table id="creLineTable" class="table tableSorter">        
        <thead>
        		 <tr>
                        <th style="border-bottom:0px;"></th>
                        <th style="border-bottom:0px;"></th>
                        <th colspan="1" style="border-bottom:0px;  text-align:center;">Targeting Detail</th>
                        <th colspan="4" style="border-bottom:0px; text-align:center;">Product Ordering</th>
                        
                      
                </tr>
                <tr>
                        <th>MGI Allele</th>
                        <th>Allele Type</th>
                        
                        <th>
                        <span>Type</span>
                        <span>Map</span>
                        <span>Seq</span>
                        <th>Vector</th>
                        <th>ES Cell</th>
                        <th>Mouse</th>
                        <th>Tissue</th>
                      
                </tr>
        </thead>
        <tbody>
                <c:forEach var="row" items="${orderRows}" varStatus="status">
                <c:set var="rowSpan" value="1"></c:set>
                <%-- <c:if test="${row.geneMapLink || row.vectorMapLink }">
                	<c:set var="rowSpan" value="2"></c:set>
                </c:if> --%>
                      <tr>
                        <!-- /alleles/MGI:2443967/tm1a(EUCOMM)Hmgu -->
                         <td rowspan="${rowSpan}">
                         	<a href="${baseUrl}/alleles/${row.mgiAccessionId}/${row.encodedAlleleName}?${creLineParam}">${row.markerSymbol}<sup>${row.alleleName}</sup></a>
                         </td>
                         <%-- <td>
                         	${row.strainOfOrigin}
                         </td> --%>
                         <td rowspan="${rowSpan}">
                         	${row.alleleDescription}
                         </td>
                          
                          
                          <td>
                               <table>
                               <tr>
	                               <td style="text-align: center;">Vector</td>
	                               <td>
	                               		<c:if test="${not empty row.vectorMapLink}">
	                               			<a class="fancybox" target="_blank" style="text-align:right" href="${row.vectorMapLink}" fullRes="${row.vectorMapLink}" original="${row.vectorMapLink}">
		                                   
		                                   		<i class="fa fa-th-list fa-lg" title="Image"></i>
		                                   
		                                   </a>
		                                 </c:if>
		                            </td>
		                            <td >
		                                <c:if test="${not empty row.vectorGenbankLink}">
			                               		<a href="${row.vectorGenbankLink}" target="_blank"><i class="fa fa-file-text fa-lg" title="Genbank File"></i></a>
		                               	</c:if>
		                            </td>
	                            </tr>
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
		                                		<a href="${row.geneGenbankLink}" target="_blank"><i class="fa fa-file-text fa-lg" title="Genbank File"></i></a>
	                               		</c:if>
	                               	</td>
	                            </tr>  
                               </table>
                            </td>
                              
                               <%-- <c:if test="${not empty alleleProduct['genbank_file']}">
                               	<div style="padding:3px;"><a href="${alleleProduct['genbank_file']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                               </c:if> --%>
                          	
                          
                          <td style="text-align: center;" rowspan="${rowSpan}">
	                          <c:if test="${row.targetingVectorAvailable}">
	                          	<a class="iFrameFancy btn" data-url="${baseUrl}/order?acc=${row.mgiAccessionId}&allele=${row.alleleName}&type=targeting_vector${creLineParam}&bare=true"><i class="fa fa-shopping-cart"></i></a>
	                          	<%-- <a class="iFrameFancy" style="text-align:right" data-url="${baseUrl}/order?acc=${acc}&allele=${row.alleleName}&type=targeting_vector" >
		                                   
		                                   		<i class="fa fa-th-list fa-lg" title="Image"></i>
		                                   
		                                   </a> --%>
	                          </c:if>
                          </td>
                         
                          <td style="text-align: center;" rowspan="${rowSpan}">
                           
	                           <c:if test="${row.esCellAvailable}">
	                          	<a class="iFrameFancy btn" data-url="${baseUrl}/order?acc=${row.mgiAccessionId}&allele=${row.alleleName}&type=es_cell${creLineParam}&bare=true"><i class="fa fa-shopping-cart"></i></a>
	                          </c:if>
                          </td>
                          
                          <td style="text-align: center;" rowspan="${rowSpan}">
	                           <c:if test="${row.mouseAvailable}">
	                          	<a class="iFrameFancy btn" data-url="${baseUrl}/order?acc=${row.mgiAccessionId}&allele=${row.alleleName}&type=mouse${creLineParam}&bare=true"><i class="fa fa-shopping-cart"></i></a>
	                           </c:if>
                          </td>
                          
                          
                          <!-- Tissue enquiries -->
                          <td style="text-align: left;" rowspan="${rowSpan}">
	                           <c:if test="${row.tissuesAvailable}">
	                          	<c:forEach items="${row.getTissueTypes()}" var="item" varStatus="loop">
								    	<a class="btn" href="${row.getTissueEnquiryLinks().get(loop.index)}" style="margin-bottom: 0.25em;"><i class="fa fa-envelope"></i> ${item}</a><br>
								</c:forEach>
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
                            	