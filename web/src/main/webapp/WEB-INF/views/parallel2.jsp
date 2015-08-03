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
		                    		<option value="${procedure.getStableId().substring(0,8)}">${procedure.getName()}</option>
		                    	</c:forEach>
		                    </select>
		                    <div class="widgets">
								<a href="#" id="shadows" class="button right filter_control btn">Shadows</a>
								<a href="#" id="export_selected" class="button right filter_control btn" title = "Export raw data in the table">Export</a>
								<a href="#" id="remove_filters" class="button right filter_control btn" title = "Remove filters">Clear filters</a>
								<!-- a href="#" id="keep_selected" class="button green filter_control">Keep</a-->
								<!-- div id="totals" class="widget right">Total Selected<br /></div>
								<div id="pie" class="widget right">	Group Breakdown<br /></div-->
								<!-- a href="#" id="remove_selected" class="button red filter_control btn" title = "Remove selections">Remove</a-->
								<!-- div id="legend"></div-->
							
								<input type="range" min="0" max="1" value="0.2" step="0.01"
									name="power" list="powers" id="line_opacity"></input> Opacity:
								<span id="opacity_level">20%</span>
							</div>
		                	<div class="clear"></div>
	                    </form>
									
						<div id="chart-and-table">
						</div>
					</div>
				</div>
			</div>
	
	</jsp:body>
    
</t:genericpage>