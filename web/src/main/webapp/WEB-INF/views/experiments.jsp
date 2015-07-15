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
		                                	<c:forEach var="allele" items="${allelePageDTO.getAlleleSymbols()}">
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
									<button onclick='ajaxToBe("${allelePageDTO.getGeneAccession()}")'>CLICK</button>
									<div id="chart-and-table"></div>
									
									<jsp:include page="experimentsFrag.jsp" flush="true">
										<jsp:param name="geneAccession" value="<%=request.getParameter(\"geneAccession\")%>"/>
									</jsp:include>
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
	var selectedFilters = "";
	var dropdownsList = new Array();
	
	var allDropdowns = new Array();
	allDropdowns[0] = $('#pipelinesFilter');
	createDropdown(allDropdowns[0],"Pipeline: All", allDropdowns);
	allDropdowns[1] = $('#alleleFilter');
	createDropdown(allDropdowns[1],"Allele: All", allDropdowns);
	allDropdowns[2] = $('#phenotypingCenterFilter');
	createDropdown(allDropdowns[2],"Phenotyping center: All", allDropdowns);

	function createDropdown(multipleSel, emptyText,  allDd){
		console.log("called createDropdown -- "+ multipleSel.size());
		$(multipleSel).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
			minWidth: 150, onItemClick: function(checkbox, selector){
				var justChecked = checkbox.prop("checked");
				var values = [];
				for(var  i=0; i < selector.options.length; i++ ) {
					if (selector.options[i].selected && (selector.options[i].value != "")) {
						values .push(selector.options[i].value);
					}
				}

				if(justChecked){				    		 
					values.push( checkbox.val());
				}else{//just unchecked value is in the array so we remove it as already ticked
					var index = $.inArray(checkbox.val(), values);
					values.splice(index, 1);
				}  
				
				// add current one and create drop down object 
				dd1 = new Object();
				dd1.name = multipleSel.attr('id'); 
				dd1.array = values; // selected values
				
				dropdownsList[0] = dd1;
				console.log("Added dd1");
				var ddI  = 1; 
				for (var ii=0; ii<allDd.length; ii++) { 
					if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
						dd = new Object();
						dd.name = allDd[ii].attr('id'); 
						dd.array = allDd[ii].val() || []; 
						dropdownsList[ddI++] = dd;
					}
				}
				console.log("call with " + dropdownsList.length);
//				refreshGenesPhenoFrag(dropdownsList);
			}, textFormatFunction: function(options) {
				var selectedOptions = options.filter(":selected");
		        var countOfSelected = selectedOptions.size();
		        var size = options.size();
		        var text = "";
		        if (size > 1){
		        	options.each(function() {
	                    if ($(this).prop("selected")) {
	                        if ( text != "" ) { text += ", "; }
	                        /* NOTE use of .html versus .text, which can screw up ampersands for IE */
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
		        switch(countOfSelected) {
		           case 0: console.log("Switch 0"); return emptyText;
		           case 1: console.log("Switch 1"); return selectedOptions.text();
		           case options.size(): console.log("Switch n"); return emptyText;
		           default: console.log("Switch default"); return text;
		        }
			}
		} );
	}	

	$(".filterTrigger").click(function() {
		//Do stuff when clicked
		//the id is set as the field to be filtered on
		//set the value of the current id of the trigger
		
		var filter=$(this).attr("id").replace("phenIconsBox_", "");
		var values = filter.split(" or ");
		console.log ("filterTrigger" + values);
		$(allDropdowns[0]).val(values);
		$(allDropdowns[0]).dropdownchecklist("refresh");
		$(allDropdowns[1]).val([]);
		$(allDropdowns[1]).dropdownchecklist("refresh");
		var dropdownsList = new Array(); 
		
		var dd1 = new Object();
		dd1.name = allDropdowns[0].attr("id");
		dd1.array = new Array; // selected values
		dd1.array = values;
		dropdownsList[0] = dd1;
		
		var dd2 = new Object();
		dd2.name = allDropdowns[1].attr("id");
		dd2.array = []; //set array for second dropdown to empty so we get the same 
		dropdownsList[1] = dd2;

//		refreshGenesPhenoFrag(dropdownsList);
	});
	
	function removeFilterSelects(){ // Remove selected options when going back to the page
		$("option:selected").removeAttr("selected");
	};
	
	//if filter parameters are already set then we need to set them as selected in the dropdowns
	var previousParams = $("#filterParams").html();
	
	function refreshGenesPhenoFrag(dropdownsList) {
		var rootUrl=window.location.href;
		var newUrl=rootUrl.replace("genes", "genesPhenoFrag").split("#")[0];
		selectedFilters = "";
		for (var it = 0; it < dropdownsList.length; it++){
			if(dropdownsList[it].array.length == 1){//if only one entry for this parameter then don't use brackets and or
				selectedFilters += '&fq=' + dropdownsList[it].name + ':"' + dropdownsList[it].array+'"';
			} 
			if(dropdownsList[it].array.length > 1)	{
				selectedFilters += '&fq='+dropdownsList[it].name+':(\"' + dropdownsList[it].array.join("\"OR\"") + '\")';
			}			    			 
		}
		newUrl+= "?" + selectedFilters;
	///	refreshPhenoTable(newUrl);
		return false;
	}
})

function ajaxToBe(geneAccession){
	console.log ("Got called");
	$( '#spinner-experiments-page' ).show();
	var tableUrl = base_url + "/experimentsFrag?geneAccession=" + geneAccession;
	$.ajax({
	  url: tableUrl,
	  cache: false
	})
	.done(function( html ) {
		$( '#spinner-experiments-page' ).hide();
		$( '#chart-and-table' ).html( html );
	});
	
}
</script>