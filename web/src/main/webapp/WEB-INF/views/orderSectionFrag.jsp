<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- just for testing with styles remove -->
<%-- <head>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
</head> --%>
<c:if test="${orderRows.size() > 0}">
<table class="reduce nonwrap">        
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
                <c:set var="rowSpan" value="1"></c:set>
                <c:if test="${fn:length(row.geneTargetDetails)>0 }">
                	<c:set var="rowSpan" value="${fn:length(row.geneTargetDetails)}"></c:set>
                </c:if>
                      <tr>
                        
                         <td rowspan="${rowSpan}">
                         	${row.markerSymbol}<sup>${row.alleleName}</sup>
                         </td>
                         <%-- <td>
                         	${row.strainOfOrigin}
                         </td> --%>
                         <td rowspan="${rowSpan}">
                         	${row.alleleDescription}
                         </td>
                          
                          <c:set var="target" value="${row.geneTargetDetails[0]}"></c:set>
                          
                               
                               <td style="text-align: center;">${target.label}</td>
                               <td>
                               		
	                               		<a class="fancybox" target="_blank" style="text-align:right" href="${target.link}" fullRes="${target.link}" original="${target.link}">
	                                   
	                                   		<i class="fa fa-th-list fa-lg" title="Image"></i>
	                                   
	                                   </a>
	                            </td>
	                            <td >
	                                <c:if test="${not empty target.genbankLink}">
		                               		<a href="${target.genbankLink}" target="_blank"><i class="fa fa-file-text fa-lg" title="Genbank File"></i></a>
	                               	</c:if>
	                            </td>
	                              
                               
                              
                              
                               <%-- <c:if test="${not empty alleleProduct['genbank_file']}">
                               	<div style="padding:3px;"><a href="${alleleProduct['genbank_file']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                               </c:if> --%>
                          	
                          
                          <td style="text-align: center;" rowspan="${rowSpan}">
                          <c:if test="${row.targetingVectorAvailable}">
                          	<a class="btn" href=""><i class="fa fa-shopping-cart"></i></a>
                          </c:if>
                          </td>
                          <td style="text-align: center;" rowspan="${rowSpan}">
                           <c:if test="${not empty row.esCellAvailable}">
                          	<a class="btn btn-lg" href="${orderEsCell.link}"><i class="fa fa-shopping-cart"></i></a>
                          </c:if>
                          </td>
                          <td style="text-align: center;" rowspan="${rowSpan}">
                           <c:if test="${row.mouseAvailable}">
                          	<a class="btn" href="${orderMouse.link}"><i class="fa fa-shopping-cart"></i></a>
                          </c:if>
                          </td>
                            
                        </tr>
                        <c:if test="${rowSpan==2 }">
                         <c:set var="target" value="${row.geneTargetDetails[1]}"></c:set>
                        <tr>
                     
                        
		                            <td style="text-align: center;">${target.label}</td>
                               		<td >
                               		
	                               		<a class="fancybox" target="_blank" style="text-align:right" href="${target.link}" fullRes="${target.link}" original="${target.link}">
	                                   		<i class="fa fa-th-list fa-lg" title="Image"></i>
	                                   </a>
	                                </td>
	                                <td>
	                                	<c:if test="${not empty target.genbankLink}">
		                               		<a href="${target.genbankLink}" target="_blank"><i class="fa fa-file-text fa-lg" title="Genbank File"></i></a>
		                                
	                               		</c:if>
	                               	</td>
	                               	
                       
                        
                        </tr>    
                        
                        </c:if>


                </c:forEach>
        </tbody>

</table>
</c:if>
    
    
