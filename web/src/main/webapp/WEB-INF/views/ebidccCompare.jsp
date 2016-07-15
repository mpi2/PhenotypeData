<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${anatomy.getAnatomyId()} (${anatomy.getAnatomyTerm()}) | IMPC anatomy Information</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search/anatomy?kw=*">anatomy</a> &raquo; ${anatomy.getAnatomyTerm()}</jsp:attribute>
	<jsp:attribute name="header">
        <meta name="robots" content="noindex">
		<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
       	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
       	    	
	</jsp:attribute>
	
                
    <jsp:body>
    
 		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">						
						<h1 class="title" id="top">Compare EBI and DCC calls</h1>
						
						<div class="section">
							<div class="inner">		
								<div id="chartDiv"></div>
								<script>${chart}</script>								
									<p>EBI calls with exact match at DCC: ${exact} (${exact/total*100})</p>
									<p>EBI calls with same MP but different pValue at DCC: ${differentPvalue} (${differentPvalue/total*100})</p>
									<p>EBI calls with more general call at DCC (DCC calls the parent, i.e. "abnormal" instead of "decreased"): ${moreGeneralCallDcc} (${moreGeneralCallDcc/total*100})</p>
									<p>EBI calls with more general call at EBI (EBI calls the parent, i.e. "abnormal" instead of "decreased"): ${moreGeneralCallEbi} (${moreGeneralCallEbi/total*100})</p>
									<p>EBI and DC calls are significant but different MPs are assigned: ${differentMps} (${differentMps/total*100})</p>
									<p>EBI calls missing from DCC, but for un-analysed parameters: ${ignore} (${ignore/total*100})</p>
									<p>Total EBI calls: ${total}</p>
									<p class="info">If the numbers look odd, it's probably because preqc was not re-indexed with common data. By default we exclude DCC calls when EBI has analysed the data too. </p>					
							</div>
						</div>	
				</div>
			</div>
		</div>
	</div>

</jsp:body>
	

</t:genericpage>