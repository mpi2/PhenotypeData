<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


			<table id="phenotypeAnatomy" class="table tableSorter">
					    <thead>
					    <tr>
					        <th class="headerSort">Phenotype</th>
					        <th class="headerSort">Genes (phenotype only)</th>
					        <th class="headerSort">Genes (phenotype and expression)</th>
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
					           		<c:set var="first" value="true"/>
					            	<c:forEach var="gene" items="${row.getGenes()}" varStatus="geneStatus"><c:if test="${!genesWithExpression.contains(gene.accessionId)}"><c:if test="${!geneStatus.first and not first}">, </c:if><a href="${baseUrl}/genes/${gene.accessionId}">${gene.symbol}</a><c:set var="first" value="false"/></c:if></c:forEach>
					            </td>
					            
					            <td>
					            	<c:set var="first" value="true"/>
					            	<c:forEach var="gene" items="${row.getGenes()}" varStatus="geneStatus"><c:if test="${genesWithExpression.contains(gene.accessionId)}"><c:if test="${not geneStatus.first and not first}">, </c:if><a href="${baseUrl}/genes/${gene.accessionId}">${gene.symbol}</a><c:set var="first" value="false"/></c:if></c:forEach>
					            </td>
					            					           	
					        </tr>
					    </c:forEach>
					    </tbody>
					</table>