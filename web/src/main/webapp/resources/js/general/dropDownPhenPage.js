$(document).ready(function(){						
	
	// bubble popup for brief panel documentation
//	$.fn.qTip({
//		'pageName': 'phenotypes',	
//		'tip': 'top right',
//		'corner' : 'right top'
//	}); removed as causing errors and should use bootstrap instead???? JW
	
	var selectedFilters = "";
	var dropdownsList = new Array();

    // initPhenoDataTable();
	removeFilterSelects();

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
	
	        
	function refreshPhenoTable(newUrl){

		$.ajax({
			url: newUrl,
			cache: false
		}).done(function( html ) {
			$("#phenotypes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
			initPhenoDataTable();
			addParamsToURL();
		});
	}
	//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
	//to move the input text or reassign the div that does it and hide the other one??
	//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc

	//stuff for dropdown tick boxes here
	$('.selectpicker').selectpicker();
	$('#procedure_name').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
		 refreshGenesPhenoFrag();
		});
	$('#marker_symbol').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
		 refreshGenesPhenoFrag();
		});
	
	$('#mp_term_name').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {	
		 refreshGenesPhenoFrag();
		});
	
	
	function refreshGenesPhenoFrag() {
		var rootUrl = window.location.href;
		var newUrl = rootUrl.replace("phenotypes", "geneVariantsWithPhenotypeTable").split("#")[0];
		var output ='?';
		selectedFilters = "";
		
		
		if($('#procedure_name').val().length > 0){
			
			for (var it = 0; it < $('#procedure_name').val().length; it++){
			selectedFilters += '&' +'procedure_name' + '='+$('#procedure_name').val()[it];
			}
		}
		
		if($('#marker_symbol').val().length > 0){
			
			for (var it = 0; it < $('#marker_symbol').val().length; it++){
			selectedFilters += '&' +'marker_symbol' + '='+$('#marker_symbol').val()[it];
			}
		}
		if($('#mp_term_name').val().length > 0){
			
			for (var it = 0; it < $('#mp_term_name').val().length; it++){
			selectedFilters += '&' +'mp_term_name' + '='+$('#mp_term_name').val()[it];
			}
		}
		
		newUrl += output + selectedFilters;
		console.log('new url='+newUrl);
		refreshPhenoTable(newUrl);
		return false;
	}
	
	
	
	
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
