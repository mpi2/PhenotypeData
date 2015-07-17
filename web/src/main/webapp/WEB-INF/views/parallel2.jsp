<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">Parrallel Coordinates Chart for ${procedure}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
    
    <jsp:attribute name="header">
        
            <!-- CSS Local Imports -->
            <link rel="stylesheet" type="text/css" href="${baseUrl}/css/parallel.css"/> 
            <link rel="stylesheet" href="${baseUrl}/css/vendor/slick.grid.css" type="text/css" media="screen"/>
            <link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css"/>
            
            <!-- JavaScript Local Imports -->
            <script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
    		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.js"></script>
			<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.csv.js"></script>
			<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>		
			<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.js"></script>
			<script type="text/javascript" src="${baseUrl}/js/vendor/underscore.js"></script>
			<script type="text/javascript" src="${baseUrl}/js/vendor/backbone.js"></script>
 			<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.event.drag-2.0.min.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/vendor/slick/slick.core.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/vendor/slick/slick.grid.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/vendor/slick/slick.dataview.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/vendor/slick/slick.pager.js"></script>
			<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery-ui-1.8.16.custom.min.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/charts/parallel/grid.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/charts/parallel/pie.js"></script>
  			<script type="text/javascript" src="${baseUrl}/js/charts/parallel/options.js"></script>
			<script type="text/javascript" src="${baseUrl}/js/charts/parallel/parallel-coordinates.js"></script>	
			<script type="text/javascript" src="${baseUrl}/js/charts/parallel/filter.js"></script>					
			<script type="text/javascript" src="${baseUrl}/js/data/IMPC_CBC.js"></script>
		  					
    </jsp:attribute>
    
    <jsp:body>
    		
			<div class="content">
				<div class="section"> 
					<div class="inner">
						<form class="tablefiltering no-style" id="target" action="destination.html">
							<select id="procedures" class="impcdropdown"  multiple="multiple" title="Select procedures to display">
		                    	<c:forEach var="procedure" items="${procedures}">
		                    		<option value="${procedure.getProcedureStableId()}">${procedure.getProcedureName()}</option>
		                    	</c:forEach>
		                    </select>
		                	<div class="clear"></div>
	                    </form>
					
						<jsp:include page="parallelFrag.jsp"></jsp:include>
					</div>
				</div>
			</div>
	
	</jsp:body>
    
</t:genericpage>