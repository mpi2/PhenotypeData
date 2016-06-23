<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<t:genericpage>

	 <jsp:attribute name="title">${queryTerms} IMPC Images LacZ</jsp:attribute>

		 
	 <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol}</a>&nbsp;&raquo;Adult LacZ Images</jsp:attribute>
	
	
	<jsp:attribute name="bodyTag">
		<body class="chartpage no-sidebars small-header">
	
	</jsp:attribute>
    <jsp:attribute name="header">
		
		<style>
		table th{border-bottom:1px solid #CDC8B1;}
		table tr:last-child th{border-bottom:none;}
		.thumbnail{margin-bottom:30px;}
		.thumbnail p{line-height:0.75em;}
		</style>

		<%-- <script src="${baseUrl}/js/vendor/jquery.autopager-1.0.0.js"></script>
		<script src="${baseUrl}/js/imaging/imageUtils.js"></script> --%>
    </jsp:attribute>


	<jsp:attribute name="addToFooter">
		<%-- <script>$.autopager({link: '#next',content: '#grid'});</script>
		
		<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
            	<c:if test="${imageCount ne 0}">
                	<li><a href="#top">Images</a></li>
                </c:if>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div> --%>
	</jsp:attribute>

<jsp:body>
    <div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
                  <!-- nicolas accordion for images here -->
                            <c:if test="${not empty impcAdultExpressionImageFacets}">
                                <div class="section">
                                    <h2 class="title" id="section-images">LacZ images for gene ${gene.markerSymbol}<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                    <!--  <div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	 -->
                                    <div class="inner">
                                        <c:forEach var="entry" items="${impcAdultExpressionImageFacets}" varStatus="status">
                                            <div class="accordion-group open">
                                                <div class="accordion-heading">
                                                    ${entry.name} (${entry.count})
                                                </div>
                                                <div class="accordion-body" style="display: block;">
                                                    <ul>
                                                        <c:forEach var="doc" items="${impcAdultExpressionFacetToDocs[entry.name]}">
                                                                <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
                                                        </c:forEach>
                                                    </ul>
                                                   
                                                </div><!--  end of accordion body -->
                                            </div>
                                        </c:forEach><!-- solrFacets end -->

                                    </div><!--  end of inner -->
                                </div> <!-- end of section -->
                            </c:if>	
                            
                            
                            <!--                                         if embryo we need to use another attribute as for gene page embryo data needs to be in a different data structure for display on same page -->
                                       <c:if test="${not empty impcEmbryoExpressionImageFacets}">
                                <div class="section">
                                    <h2 class="title" id="section-images">LacZ images for gene ${gene.markerSymbol}<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                    <!--  <div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	 -->
                                    <div class="inner">
                                       
                                       
                                        <c:forEach var="entry" items="${impcEmbryoExpressionImageFacets}" varStatus="status">
                                            <div class="accordion-group open">
                                                <div class="accordion-heading">
                                                    ${fn:replace(entry.name , 'TS20 ','')} (${entry.count})
                                                </div>
                                                <div class="accordion-body" style="display: block;">
                                                    <ul>
                                                        <c:forEach var="doc" items="${impcEmbryoExpressionFacetToDocs[entry.name]}">
                                                                <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
                                                        </c:forEach>
                                                    </ul>
                                                   
                                                </div><!--  end of accordion body -->
                                            </div>
                                        </c:forEach><!-- solrFacets end -->	
                                        </div><!--  end of inner -->
                                </div> <!-- end of section -->
                                </c:if>	
                
				</div>
			</div>

		
</div>
</div>

    </jsp:body>	

</t:genericpage>

