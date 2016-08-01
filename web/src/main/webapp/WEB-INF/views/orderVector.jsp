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
							<h1 class="title" id="top">Vector: ${allele.markerSymbol} ${allele.alleleName}
								<span class="documentation">
									<a href='' id='summarySection' class="fa fa-question-circle pull-right"></a>
								</span>
							</h1>

							<!-- general Gene info -->
							<div class="section">
								<%--<a href='' id='detailsPanel' class="fa fa-question-circle pull-right"></a>--%>
								<div class="inner">
 									<img alt="image not found!" src="${allele.alleleSimpleImage}" width="930px">	
 									<p>This product is available from the following repositories:<p> 
 								</div>
 							</div>
 						</div>
 					</div>
 				</div>
 			</div>
 
 
		
	</jsp:body>
</t:genericpage>
	
				
    
    
