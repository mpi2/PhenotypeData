$(document).ready(function(){						
	
	// bubble popup for brief panel documentation
//	$.fn.qTip({
//		'pageName': 'phenotypes',	
//		'tip': 'top right',
//		'corner' : 'right top'
//	}); removed as causing errors and should use bootstrap instead???? JW
	
	var selectedFilters = "";
	var dropdownsList = new Array();

    initPhenoDataTable();
	removeFilterSelects();
	
	function initPhenoDataTable(){
            var aDataTblCols = [0,1,2,3,4,5,6,7,8];
            $('table#phenotypes').dataTable( );
        }
	
	function removeFilterSelects(){ // Remove selected options when going back to the page
		$("option:selected").removeAttr("selected");
	};
	
	// AJAX calls for the baseline charts
	$('.baselineChart').each(function(i, obj) {
		console.log('calling baselineChart function');
		$( '#spinner-baseline-charts' ).show();
		var mp = $(this).attr('mp');
		var id = $(this).attr('parameter');
		var chartUrl = document.URL.split("/phenotypes/")[0];
		chartUrl += "/baselineCharts/" + mp + "?parameter_id=" + id;
		console.log('baselineChartUrl='+chartUrl);
		$.ajax({
		  url: chartUrl,
		  cache: false
		})
		.done(function( html ) {
			$( '#spinner-baseline-charts' ).hide();
			$( '#baseline-chart-div' ).html( html );		
			$( '#baseline-chart-div' ).attr("parameter", id);
		});
	});	 
	
	// AJAX calls for the overview charts
	$('.oChart').each(function(i, obj) {
		console.log('calling ochart function');
		$( '#spinner-overview-charts' ).show();
		var mp = $(this).attr('mp');
		var id = $(this).attr('parameter');
		var chartUrl = document.URL.split("/phenotypes/")[0];
		chartUrl += "/overviewCharts/" + mp + "?parameter_id=" + id;
		console.log('overviewChartUrl='+chartUrl);
		$.ajax({
		  url: chartUrl,
		  cache: false
		})
		.done(function( html ) {
			$( '#spinner-overview-charts' ).hide();
			$( '#single-chart-div' ).html( html );		
			$( '#single-chart-div' ).attr("parameter", id);
		});
	});	 
	
	$( "#show_other_procedures" ).click(function() {
		$( "#other_procedures" ).toggle( "slow", function() {
		// Animation complete.
		 });
		
		var text = $('#procedureToogleLink').text();
		$('#procedureToogleLink').text(
                text == "Show more" ? "Show less" : "Show more");
	});
	
	        
//	function refreshPhenoTable(newUrl){
//
//		$.ajax({
//			url: newUrl,
//			cache: false
//		}).done(function( html ) {
//			$("#phenotypes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
//			initPhenoDataTable();
//			addParamsToURL();
//		});
//	}
	//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
	//to move the input text or reassign the div that does it and hide the other one??
	//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc

	
	$('.selectpicker').selectpicker();
	
	
	//stuff for dropdown tick boxes here
	var allDropdowns = new Array();
	allDropdowns[0] = $('#resource_fullname');
	allDropdowns[1] = $('#procedure_name');
	allDropdowns[2] = $('#marker_symbol');
	allDropdowns[3] = $('#mp_term_name');
//	createDropdown(allDropdowns[3].sort(), "Phenotype: All", allDropdowns);
//	createDropdown(allDropdowns[0],"Source: All", allDropdowns);
//	createDropdown(allDropdowns[1], "Procedure: All", allDropdowns);
//	createDropdown(allDropdowns[2].sort(), "Gene: All", allDropdowns);
	
//	function createDropdown(multipleSel, emptyText,  allDd){
//		$(multipleSel).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
//			minWidth: 150, onItemClick: function(checkbox, selector){
//				var justChecked = checkbox.prop("checked");
//				var values = [];
//				for(var  i=0; i < selector.options.length; i++ ) {
//					if (selector.options[i].selected && (selector.options[i].value != "")) {
//						values .push(selector.options[i].value);
//					}
//				}
//
//				if(justChecked){				    		 
//					values.push( checkbox.val());
//				}else{//just unchecked value is in the array so we remove it as already ticked
//					var index = $.inArray(checkbox.val(), values);
//					values.splice(index, 1);
//				}  
//				
//				// add current one and create drop down object 
//				dd1 = new Object();
//				dd1.name = multipleSel.attr('id'); 
//				dd1.array = values; // selected values
//				
//				dropdownsList[0] = dd1;
//				
//				var ddI  = 1; 
//				for (var ii=0; ii<allDd.length; ii++) { 
//					if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
//						dd = new Object();
//						dd.name = allDd[ii].attr('id'); 
//						dd.array = allDd[ii].val() || []; 
//						dropdownsList[ddI++] = dd;
//					}
//				}
//				refreshGenesPhenoFrag(dropdownsList);
//				addParamsToURL();
//			}, textFormatFunction: function(options) {
//				var selectedOptions = options.filter(":selected");
//				console.log('select options='+selectedOptions.html());
//		        var countOfSelected = selectedOptions.size();
//		        var size = options.size();
//		        var text = "";
//		        if (size > 1){
//		        	options.each(function() {
//	                    if ($(this).prop("selected")) {
//	                        if ( text != "" ) { text += ", "; }
//	                        /* NOTE use of .html versus .text, which can screw up ampersands for IE */
//	                        var optCss = $(this).attr('style');
//	                        var tempspan = $('<span/>');
//	                        tempspan.html( $(this).html() );
//	                        if ( optCss == null ) {
//	                                text += tempspan.html();
//	                        } else {
//	                                tempspan.attr('style',optCss);
//	                                text += $("<span/>").append(tempspan).html();
//	                        }
//	                    }
//	                });
//		        }
//		        switch(countOfSelected) {
//		           case 0: return emptyText;
//		           case 1: return selectedOptions.text();
//		           case options.size(): return emptyText;
//		           default: return text;
//		        }
//			}
//		} );
//	}
	
	//if filter parameters are already set then we need to set them as selected in the dropdowns
//	var previousParams=$("#filterParams").html();
//	
//	function refreshGenesPhenoFrag(dropdownsList) {
//		var rootUrl = window.location.href;
//		var newUrl = rootUrl.replace("phenotypes", "geneVariantsWithPhenotypeTable").split("#")[0];
//		var output ='?';
//		selectedFilters = "";
//		for (var it = 0; it < dropdownsList.length; it++){
//			if (dropdownsList[it].array.length > 0){
//				selectedFilters += '&' + dropdownsList[it].name + '=' + dropdownsList[it].array.join('&' + dropdownsList[it].name + '=');
//			}
//		}
//		newUrl += output + selectedFilters;
//		refreshPhenoTable(newUrl);
//		return false;
//	}
	
	
	/** 
	 * Add selected filters to URLs for download
	 */
	function addParamsToURL(){
		
		if (!$("#tsvDownload").attr('baseDownloadLink')){
			$("#tsvDownload").attr('baseDownloadLink', $("#tsvDownload").attr('href'));
		}

		if (!$("#xlsDownload").attr('baseDownloadLink')){
			$("#xlsDownload").attr('baseDownloadLink', $("#xlsDownload").attr('href'));
		}
		
		var link = $("#tsvDownload").attr('baseDownloadLink');
		link += selectedFilters;
		$("#tsvDownload").attr('href', link);

		link = $("#xlsDownload").attr('baseDownloadLink');
		link += selectedFilters;
		$("#xlsDownload").attr('href', link);
	}
	
});

function ajaxToBe(phenotype, parameter){
	console.log('calling ajaxToBe');
	$( '#spinner-overview-charts' ).show();
	var chartUrl = document.URL.split("/phenotypes/")[0];
	chartUrl += "/overviewCharts/" + phenotype + "?parameter_id=" + parameter;
	$.ajax({
	  url: chartUrl,
	  cache: false
	})
	.done(function( html ) {
		$( '#spinner-overview-charts' ).hide();
		$( '#single-chart-div' ).html( html );
		$( '#single-chart-div' ).attr("parameter", parameter);
	});
	
	$( '#spinner-baseline-charts' ).show();
	var chartUrl = document.URL.split("/phenotypes/")[0];
	chartUrl += "/baselineCharts/" + phenotype + "?parameter_id=" + parameter;
	console.log('calling ajaxToBeBaseline');
	$.ajax({
	  url: chartUrl,
	  cache: false
	})
	.done(function( html ) {
		$( '#spinner-baseline-charts' ).hide();
		$( '#baseline-chart-div' ).html( html );
		$( '#baseline-chart-div' ).attr("parameter", parameter);
	});
	
}


/* new sorting functions */
//http://datatables.net/forums/discussion/5894/datatable-sorting-scientific-notation
jQuery.fn.dataTableExt.oSort['allnumeric-asc']  = function(a,b) {
          var x = parseFloat(a);
          var y = parseFloat(b);
          return ((x < y) ? -1 : ((x > y) ?  1 : 0));
        };
 
jQuery.fn.dataTableExt.oSort['allnumeric-desc']  = function(a,b) {
          var x = parseFloat(a);
          var y = parseFloat(b);
          return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
        };
 