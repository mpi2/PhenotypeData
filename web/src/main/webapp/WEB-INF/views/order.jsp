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
 							
 							<div class="section">
 							
 								<div class="inner">
 								<p>
									Mouse strains produced by the IMPC are made available to researchers by depositing them with the KOMP mouse repositiory at the University of California-Davis and/or the European Mutant Mouse Archive maintained by INFRAFRONTIER, Gmbh. IMPC centers may also provide breeders on a colloborative basis based on availability.
								</p>
								<p>
									We provide links and contact information based on tracking data supplied by the IMPC centers, but are not responsible for the ordering process.
								</p>
								<p>
									<b>Clicking the link will send you to a third party site to find more about ordering a particular strain.</b>
								</p>
								<div>
 								<c:if test="${fn:length(productsByName)>0}">
 								<br/>	
 								<br/>
 									<h4>This product is available from the following repositories:</h4> 
 									<table class="nonwrap">        
        							<thead>
        		 					</thead>
        		 					<tbody>
 										<c:forEach var="repo" items="${productsByName}">
 										
 											<tr>
 												<td>repo=${ repo}value=${ repo.value} <img src="img/rep_icons/${fn:toLowerCase(repo.key)}.jpg" alt="${repo.key}"  width="100px" onerror="this.style.display='none';this.parentElement.parentElement.innerHTML='${repo.key}';" />
 												</td>
 												<td>
 												<!-- just to print out the order link in case there are multiple order names and links that aren't this one -->
		 											<c:forEach var="orderName" items="${ repo.value[0].orderNames}" varStatus="repoIndex">
		 											<c:if test="${orderName eq repo}">
		 												<a class="btn" href="${ entry.value[0].orderLinks[repoIndex.index] }" target="_blank"><i class="fa fa-shopping-cart"></i>&nbsp;&nbsp;&nbsp;&nbsp;Third party site for info about ordering</a> OR <div class="btn close">Return to previous page</div>
		 											</c:if>
		 											</c:forEach>
		 										</td>
		 										<td></td>	
 											</tr>
 											<!-- loop through the products so we get their name and qc links -->
 											<c:forEach var="prod" items="${repo.value}">
 											<!--qc data links like this http://localhost:8080/phenotype-archive/alleles/qc_data/es_cell/EPD0386_3_A05/ -->
 											<tr>
	 											<td>should be empty</td>
	 											<td>prod name=	${prod.name} </td>
	 											<td>
	 											<c:if test="${fn:length(prod.qcData)>0}">
	 											 	<a class="btn" href="${baseUrl}/qcData?type=${type}&productName=${prod.name}&alleleName=${prod.alleleName}&bare=true"><i class="fa fa-info"></i>QC Data</a>
	 											 	</c:if>
	 											
	 											
	 											<c:if test="${fn:length(prod.qcData)==0}">
	 											No QC Data Available
	 											</c:if>
	 											</td>
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
 							
 							<div class="section">
								<%--<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>--%>
								<div class="inner">
 									<img alt="image not found!" src="${allele.alleleSimpleImage}" width="930px">
 								</div>
 							</div>
 							
 							
 						</div>
 					</div>
 				</div>
 			</div>
 
 <script type="text/javascript">

//close the fancybox if the return to previous page button is clicked and they don't want to go to order page
$(document).ready(function () {
	$('.btn.close').click(function()
		{
		parent.$.fancybox.close(); 
		}
 	);
});
    
 </script>
		
	</jsp:body>
</t:genericpage>
	
				
    
    
