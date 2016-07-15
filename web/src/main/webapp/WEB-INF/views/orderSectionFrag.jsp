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
                        <th>MGI Allele</th>
                        <th>Strain of Origin</th>
                        <th>Allele Type</th>
                        <th>Gene Targeting Details</th>
                        <th>Target Vector</th>
                        <th>ES Cells</th>
                        <th>Mouse</th>
                        <th>Control</th>
                </tr>
        </thead>
        <tbody>
                <c:forEach var="row" items="${orderRows}" varStatus="status">
                        <tr>
                         <td>
                         	${row.alleleName}
                         </td>
                         <td>
                         	${row.strainOfOrigin}
                         </td>
                         <td>
                         	${row.alleleDescription}
                         </td>
                          <td>
                          <c:forEach var="target" items="${row.geneTargetDetails}">
                          
                               <span>&nbsp;&nbsp;${target.label}</span>
                               <div style="padding:3px;"><a class="fancybox" target="_blank" href="${target.link}.jpg" fullRes="${target.link}.jpg" original="${target.link}.jpg">
                                                <i class="fa fa-th-list fa-lg"></i></a>
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
    
    
