<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- just for testing with styles remove -->
<%-- <head>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
</head> --%>
<t:genericpage>

	<jsp:attribute name="title">${allele.markerSymbol}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search/gene?kw=*">Genes</a> &raquo; ${allele.markerSymbol}</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">

	</jsp:attribute>
	<jsp:attribute name="addToFooter">
          
	</jsp:attribute>


	<jsp:attribute name="header">

				<!-- JavaScript Local Imports -->
	</jsp:attribute>
		
	<jsp:body>
		<div class="region region-content">
				<div class="block">
					<div class="content">
						<div class="node node-gene">
							<h1 class="title" id="top">${type.name}: ${allele.markerSymbol} ${allele.alleleName}
								<span class="documentation">
									<a href='' id='summarySection' class="fa fa-question-circle pull-right"></a>
								</span>
							</h1>

							<!-- general Gene info -->
							<div class="section">
								<%--<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>--%>
								<div class="inner">
 									<img alt="image not found!" src="${allele.alleleSimpleImage}" width="930px">
 								</div>
 							</div>
 							
 							<div class="section">
 								<div class="inner half">
 								<c:if test="${fn:length(productsByName)>0}">	
 									<p>This product is available from the following repositories:<p> 
 									<table class="reduce nonwrap">        
        							<thead>
        		 					</thead>
        		 					<tbody>
 										<c:forEach var="entry" items="${productsByName}">
 											<tr><td>${entry.key}</td><td>
 											<!-- just to print out the order link in case there are multiple order names and links that aren't this one -->
		 											<c:forEach var="orderName" items="${ entry.value[0].orderNames}" varStatus="repoIndex">
		 											<c:if test="${orderName eq entry.key}">
		 											<a class="btn" href="${ entry.value[0].orderLinks[repoIndex.index] }" target="_blank"><i class="fa fa-shopping-cart"></i></a>
		 											</c:if>
		 											</c:forEach>
		 									</td>	
 											</tr>
 											<!-- loop through the products so we get their name and qc links -->
 											<c:forEach var="prod" items="${entry.value}">
 											<!--qc data links like this http://localhost:8080/phenotype-archive/alleles/qc_data/es_cell/EPD0386_3_A05/ -->
 											<tr><td>	${prod.name} </td>
 											<td>
 											<c:if test="${fn:length(prod.qcData)>0}">
 											 	<a class="btn" href="${baseUrl}/qcData?type=${type}&productName=${prod.name}&bare=true"><i class="fa fa-info"></i>QC Data</a>
 											 	</c:if>
 											 </td>
 											
 											<c:if test="${fn:length(prod.qcData)==0}">
 											No QC Data Available
 											</c:if>
 											 </tr>
 											</c:forEach> 
 																				
 										</c:forEach>
 									</tbody>
 									</table>
 									</c:if>
 							<c:if test="${fn:length(productsByName)==0}">
 								There are no products of type ${type} for this allele this link shouldn't have been available if no products.
 							</c:if>
 								</div>
 							</div>
 							
 							
 						</div>
 					</div>
 				</div>
 			</div>
 
 
		
	</jsp:body>
</t:genericpage>
	
				
    
    
