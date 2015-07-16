<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Experiment details for alleles of ???</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&facet=gene">Genes</a> &raquo; <a href="${baseUrl}/genes/${allelePageDTO.getGeneAccession()}">${allelePageDTO.getGeneSymbol()}</a> &raquo; allData </jsp:attribute>
   
	<jsp:attribute name="header">
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>
  	</jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">All data for ${allelePageDTO.getGeneSymbol()}</h1>
							<div class="section">
							
								<div class="inner">
									<form class="tablefiltering no-style" id="target" action="destination.html">
									 
										<select id="alleleFilter" class="impcdropdown"  multiple="multiple" title="Filter on allele symbol">
		                                	<c:forEach var="allele" items="${allelePageDTO.getEscapedAlleleSymbols()}">
		                                        <option value="${allele}">${allele}</option>
		                                    </c:forEach>
		                                </select>
		                                
										<select id="pipelinesFilter" class="impcdropdown"  multiple="multiple" title="Filter on allele symbol">
		                                	<c:forEach var="pipeline" items="${allelePageDTO.getPipelineNames()}">
		                                        <option value="${pipeline}">${pipeline}</option>
		                                    </c:forEach>
		                                </select> 
		                                
		                                <select id="phenotypingCenterFilter"  class="impcdropdown"  multiple="multiple" title="Filter on allele symbol">
		                                	<c:forEach var="pCenter" items="${allelePageDTO.getPhenotypingCenters()}">
		                                        <option value="${pCenter}">${pCenter}</option>
		                                    </c:forEach>
		                                </select> 
	                                    <div class="clear"></div>
	                                </form>
								                                
									<div id="spinner-experiments-page"><i class="fa fa-refresh fa-spin"></i></div>
									
									<div id="chart-and-table"></div>
									
								</div>
							</div> <!-- parameter list -->
      					</div> <!--end of node wrapper should be after all secions  -->
    				</div>
    			</div>
   			</div>
 	</jsp:body>
  
</t:genericpage>

<script>


$(document).ready(function(){
	
	$( '#spinner-experiments-page' ).hide();
	removeFilterSelects();
	
	var dropdownsList = new Array();	
	var allDropdowns = new Array();
	allDropdowns[0] = $('#pipelinesFilter');
	createDropdown(allDropdowns[0],"Pipeline: All", allDropdowns);
	allDropdowns[1] = $('#alleleFilter');
	createDropdown(allDropdowns[1],"Allele: All", allDropdowns);
	allDropdowns[2] = $('#phenotypingCenterFilter');
	createDropdown(allDropdowns[2],"Center: All", allDropdowns);

	
	function createDropdown(multipleSel, emptyText,  allDd){
	
		$(multipleSel).dropdownchecklist( { 
			firstItemChecksAll: false, 
			emptyText: emptyText, 
			icon: {}, 
			minWidth: 150, 
			onItemClick: function(checkbox, selector){	
				
				var values  = getSelectedValues(selector, checkbox);				
				// add current one and create drop down object 
				dd1 = new Object();
				dd1.name = multipleSel.attr('id'); 
				dd1.array = values; 
				dropdownsList[0] = dd1;				
				var ddI  = 1; 
				for (var ii=0; ii<allDd.length; ii++) { 
					if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
						dd = new Object();
						dd.name = allDd[ii].attr('id'); 
						dd.array = allDd[ii].val() || []; 
						dropdownsList[ddI++] = dd;
					}
				}
				
				if (selector.id === "phenotypingCenterFilter"){
					reloadChartAndTable("${allelePageDTO.getGeneAccession()}", values);
				} else {
					reloadChartAndTable("${allelePageDTO.getGeneAccession()}", allDropdowns[2].val());
				}
				
				
			}, 
			
			textFormatFunction: function(options) {
				
				var selectedOptions = options.filter(":selected");
		        var text = "";
		        if (options.size() > 1){
		        	options.each(function() {
	                    if ($(this).prop("selected")) {
	                        if ( text != "" ) { text += ", "; }
	                        var optCss = $(this).attr('style');
	                        var tempspan = $('<span/>');
	                        tempspan.html( $(this).html() );
	                        if ( optCss == null ) {
	                        	text += tempspan.html();
	                        } else {
	                        	tempspan.attr('style',optCss);
	                        	text += $("<span/>").append(tempspan).html();
	                        }
	                    }
	                });
		        }
		        
		        switch(selectedOptions.size()) {
		           case 0: return emptyText;
		           case 1:return selectedOptions.text();
		           case options.size():  return emptyText;
		           default: return text;
		        }
			}
		});
	}	

	
	function getSelectedValues(selector, checkbox){
		
		var justChecked = checkbox.prop("checked");
		var values = [];
		for(var  i=0; i < selector.options.length; i++ ) {
			if (selector.options[i].selected && (selector.options[i].value != "")) {
				values .push(selector.options[i].value);
			}
		}
		if(justChecked){				    		 
			values.push( checkbox.val());
		}else{
			//uncheck
			var index = $.inArray(checkbox.val(), values);
			values.splice(index, 1);
		}  
		return values;
	}
	
		
	function removeFilterSelects(){ 
		// Remove selected options when going back to the page
		$("option:selected").removeAttr("selected");
	};
	
	function reloadChartAndTable(geneAccession, phenotypingCenter){
		console.log ("Got called ++ " + phenotypingCenter);
	 	$( '#spinner-experiments-page' ).show();
		var tableUrl = base_url + "/experimentsFrag?geneAccession=" + geneAccession;
		if (phenotypingCenter != null){
			tableUrl += "&phenotypingCenter=" + phenotypingCenter.join("&phenotypingCenter=");
		}
		
		console.log("AJAX URL " + tableUrl);
		$.ajax({
		  url: tableUrl,
		  cache: false
		})
		.done(function( html ) {
			$( '#spinner-experiments-page' ).hide();
			$( '#chart-and-table' ).html( html );
		}); 
		
	}
	
})


</script>