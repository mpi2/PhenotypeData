<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


			<table id="phenotypeAnatomy" class="table tableSorter">
					    <thead>
					    <tr>
					        <th class="headerSort">Phenotype</th>
					        <th class="headerSort">Genes</th>
					    </tr>
					    </thead>
					    <tbody>
					    <c:forEach var="row" items="${phenotypeTable}" varStatus="status">
					        <tr>
					            <td>
					            	<c:if test="${row.phenotypeTerm.id != null}">
					            	 	<a href="${baseUrl}/phenotypes/${row.phenotypeTerm.id}">${row.phenotypeTerm.name} </a>
					             	</c:if>
					            </td>
					            
					            <td>
					            	<c:forEach var="gene" items="${row.getGenes()}" varStatus="geneStatus">
					            		<a href="${baseUrl}/genes/${gene.accessionId}">${gene.symbol}</a><c:if test="${!geneStatus.last}">,&nbsp;</c:if>
					            	</c:forEach>
					            </td>
					            					           	
					        </tr>
					    </c:forEach>
					    </tbody>
					</table>