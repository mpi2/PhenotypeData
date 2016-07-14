<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- just for testing with styles remove -->
<head>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
</head>
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
                         	${row.alleleType}
                         </td>
                          <td>
                          <c:forEach var="target" items="${row.geneTargetDetails}">
                          		${target.label} <a href="${target.link}" target="_blank">link here</a>
                          	</c:forEach>
                          </td>
                          <td>
                          	<a href="${row.orderTargetVectorUrl}">Vector button</a>
                          </td>
                          <td>
                          	<a href="${row.orderEsCellUrl}">ES Cell button</a>
                          </td>
                          <td>
                          	<a href="${row.orderMouseUrl}">Mouse button</a>
                          </td>
                            
                        </tr>


                </c:forEach>
        </tbody>

</table>
</c:if>
    
    
