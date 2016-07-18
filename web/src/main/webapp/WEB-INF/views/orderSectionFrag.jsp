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
                        <th style="border-bottom:0px;"></th>
                        <th colspan="3" style="text-align:center;">Product Ordering</th>
                        
                      
                </tr>
                <tr>
                        <th>MGI Allele</th>
                        <th style="width:22%">Allele Type</th>
                        <th style="width:22%">Gene Targeting Details</th>
                        <th>Target Vector</th>
                        <th>ES Cells</th>
                        <th>Mouse</th>
                      
                </tr>
        </thead>
        <tbody>
                <c:forEach var="row" items="${orderRows}" varStatus="status">
                        <tr>
                         <td>
                         	${row.alleleName}
                         </td>
                         <%-- <td>
                         	${row.strainOfOrigin}
                         </td> --%>
                         <td>
                         	${row.alleleDescription}
                         </td>
                          <td style="text-align:left">
                          <c:forEach var="target" items="${row.geneTargetDetails}">
                          
                               
                               <div style="padding:3px;"><span>${target.label}</span>
                               		<span id="imitsIconsContainer" style="float:right;">
	                               		<a class="fancybox" target="_blank" style="text-align:right" href="${target.link}" fullRes="${target.link}" original="${target.link}">
	                                   
	                                   		<i class="fa fa-th-list fa-lg"></i>
	                                   
	                                   </a>
	                                 <c:if test="${not empty target.genbankLink}">
		                               		<a href="${target.genbankLink}" target="_blank"><i class="fa fa-file-text fa-lg"></i></a>
		                                
	                               	</c:if>
	                               </span>
                               </div>
                              
                              
                               <%-- <c:if test="${not empty alleleProduct['genbank_file']}">
                               	<div style="padding:3px;"><a href="${alleleProduct['genbank_file']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                               </c:if> --%>
                          	</c:forEach>
                          </td>
                          <td>
                          <c:forEach var="orderVector" items="${row.orderTargetVectorDetails}">
                          	<a class="btn btn-sm" href="${orderVector.link}"><i class="fa fa-shopping-cart">${orderVector.label}</i></a>
                          </c:forEach>
                          </td>
                          <td>
                          <c:forEach var="orderEsCell" items="${row.orderEsCellDetails}">
                          	<a class="btn btn-sm" href="${orderEsCell.link}"><i class="fa fa-shopping-cart">${orderEsCell.label}</i></a>
                          </c:forEach>
                          </td>
                          <td>
                          <c:forEach var="orderMouse" items="${row.orderMouseDetails}">
                          	<a class="btn btn-sm" href="${orderMouse.link}"><i class="fa fa-shopping-cart">${orderMouse.label}</i></a>
                          </c:forEach>
                          </td>
                            
                        </tr>


                </c:forEach>
        </tbody>

</table>
</c:if>
    
    
