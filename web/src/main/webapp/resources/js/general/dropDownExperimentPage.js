$(document).ready(function(){
	
	$( '#spinner-experiments-page' ).hide();
	
	removeFilterSelects();
	
/*	var dropdownsList = new Array();
	var allDropdowns = new Array();
	allDropdowns[0] = $('#proceduresFilter');
	createDropdown(allDropdowns[0],"Procedure: All", allDropdowns);
	allDropdowns[1] = $('#alleleFilter');
	createDropdown(allDropdowns[1],"Allele: All", allDropdowns);
	allDropdowns[2] = $('#phenotypingCenterFilter');
	createDropdown(allDropdowns[2],"Center: All", allDropdowns);
	allDropdowns[3] = $('#phenotypes');
	createDropdown(allDropdowns[3],"Phenotype: All", allDropdowns);*/
	
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
					reloadChartAndTable(geneId, values, allDropdowns[1].val(), allDropdowns[0].val(), allDropdowns[3].val());
				} else if (selector.id === "alleleFilter") {
					reloadChartAndTable(geneId, allDropdowns[2].val(), values, allDropdowns[0].val(), allDropdowns[3].val());
				} else if (selector.id === "proceduresFilter") {
					reloadChartAndTable(geneId, allDropdowns[2].val(), allDropdowns[1].val(), values, allDropdowns[3].val());
				} else if (selector.id === "phenotypes") {
					reloadChartAndTable(geneId, allDropdowns[2].val(), allDropdowns[1].val(), allDropdowns[0].val(), values);
				}
								
			}, 
			
			textFormatFunction: function(options) {
				
				var selectedOptions = options.filter(":selected");
		        var text = "";
		        if (options.length > 1){
		        if (options.length > 1){
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
		        
		        switch(selectedOptions.length) {
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
	
	function reloadChartAndTable(geneAccession, phenotypingCenter, allele, procedure, phenotypes){
		
	 	$( '#spinner-experiments-page' ).show();
		var tableUrl = base_url + "/experimentsFrag?geneAccession=" + geneAccession;
		tableUrl += getParametersForUrl(phenotypingCenter, "phenotypingCenter");
		tableUrl += getParametersForUrl(allele, "alleleSymbol");
		tableUrl += getParametersForUrl(procedure, "procedureName");
		tableUrl += getParametersForUrl(phenotypes, "mpTermId");
		
		$.ajax({
		  url: tableUrl,
		  cache: false
		})
		.done(function( html ) {
			$( '#spinner-experiments-page' ).hide();
			$( '#chart-and-table' ).html( html );
		}); 
		
	}
	
	function getParametersForUrl(paramValues, paramName){
		var url = "";
		if (paramValues != null && paramValues != ""){
			url += "&" + paramName + "=" + paramValues.join("&" + paramName + "=");
		}
		return url;
	}
	
})