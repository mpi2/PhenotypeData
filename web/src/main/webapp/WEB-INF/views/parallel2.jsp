<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">Parrallel Coordinates Chart for ${procedure}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
    
    <jsp:attribute name="header">
                    
        <!-- JavaScript Local Imports -->
		<script type='text/javascript' src="${baseUrl}/js/general/dropDownParallelCoordinatesPage.js?v=${version}"></script> 			
		  	
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>		
		
    </jsp:attribute>
    
    <jsp:body>
    		
			<div class="content">
				<div class="section"> 
					<div class="inner">
						<div id="spinner"><i class="fa fa-refresh fa-spin"></i></div>
						
						<form class="tablefiltering no-style" id="target" action="destination.html">
							<select id="proceduresFilter" class="impcdropdown"  multiple="multiple" title="Select procedures to display">
		                    	<c:forEach var="procedure" items="${procedures}">
		                    		<option value="${procedure.getProcedureStableId().substring(0,8)}">${procedure.getProcedureName()}</option>
		                    	</c:forEach>
		                    </select>
		                	<div class="clear"></div>
	                    </form>
									
						<div id="chart-and-table">
						</div>
					</div>
				</div>
			</div>
	
	</jsp:body>
    
</t:genericpage>