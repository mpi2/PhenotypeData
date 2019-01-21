$(document).ready(function(){						

	// bubble popup for brief panel documentation
//	$.fn.qTip({
//		'pageName': 'gene',				
//		'tip': 'top right',
//		'corner' : 'right top'
//	});							
	
    //function to fire off a refresh of a table and it's dropdown filters

	removeFilterSelects();
	var selectedFilters = "";
	var dropdownsList = new Array();
    initGenePhenotypesTable();
	/* var oDataTable = $('table#phenotypes').dataTable();
						oDataTable.fnDestroy();  */
	// use jquery DataTable for table searching/sorting/pagination
    function initGenePhenotypesTable(){
		var aDataTblCols = [0,1,2,3,4,5,6,7];
		$('table#genes').dataTable( );
		//	var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
//	    $('table#genes').dataTable( {
//			"aoColumns": [{ "sType": "string",  "bSortable" : true},
//			              { "sType": "html", "mRender":function( data, type, full ) {
//			            	  return (type === "filter") ? $(data).text() : data;
//			              }},
//			              { "sType": "html", "mRender":function( data, type, full ) {
//			            	  return (type === "filter") ? $(data).text() : data;
//			              }},
//			              { "sType": "string"},
//			              { "sType": "string"},
//			              { "sType": "string"},
////			              { "sType": "string"},
////			              { "sType": "html"},
//	                      { "sType": "allnumeric"},
//			              { "sType": "string", "bSortable" : false }
//	
//			              ],
//		    "aaSorting": [[ 6, 'asc' ]],//sort on pValue first
//			"bDestroy": true,
//			"bFilter":false,
//			"bPaginate":true,
//	        "sPaginationType": "bootstrap"
//		});
    }

	// Sort the individual table containing p-values
	$.fn.dataTableExt.oSort['pvalues-asc']  = function(a,b) {
		var x = 0;
		var y = 0;
		if (!a || !a.length) { a = 10; }
		if (!b || !b.length) { b = 10; }
		x = parseFloat( a );
		y = parseFloat( b );
		return ((x < y) ? -1 : ((x > y) ?  1 : 0));
	};
	
	$.fn.dataTableExt.oSort['pvalues-desc']  = function(a,b) {
		var x = 0;
		var y = 0;
		if (!a || !a.length) { a = 10; }
		if (!b || !b.length) { b = 10; }
		x = parseFloat( a );
		y = parseFloat( b );
		return ((x < y) ?  1 : ((x > y) ? -1 : 0));
	};
	
	// the number of columns should be kept in sync in the JSP
//	var oDataTable = $.fn.initDataTable($('table#strainPhenome'), {
//		"aoColumns": [
//		              { "sType": "string" },		              
//		              { "sType": "string" },
//		              { "sType": "string" },		              
//		              { "sType": "string" },
//		              { "sType": "string" },
//		             // { "sType": "string" }, // Statistical Method			              
//		              { "sType": "pvalues" }, // or numeric
//		              { "sType": "string" },
//		              { "sType": "string", "bSortable" : false }
//
//		              ],
//		              "bDestroy": true,
//		              "bFilter":false
//	});
	
	//$('[rel=tooltip]').tooltip();
	//$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
		
	function refreshPhenoTable(newUrl){
		$.ajax({
			url: newUrl,
			cache: false
		}).done(function( html ) {
			$("#genes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
			initGenePhenotypesTable();
		});
	}
	
	//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
	//to move the input text or reassign the div that does it and hide the other one??
	//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc
	
	//stuff for dropdown tick boxes here
	$('.selectpicker').selectpicker();
	$('#top_level_mp_term_name').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
		console.log('mp term selected');
		 refreshGenesPhenoFrag();
		});
	
	function refreshGenesPhenoFrag() {
		console.log('in refreshGenesPhenoFrag');
		var rootUrl=window.location.href;
		var newUrl=rootUrl.replace("genes", "genesPhenoFrag").split("#")[0];
		selectedFilters = "";
		if($('#top_level_mp_term_name').val().length > 0){
			
			for (var it = 0; it < $('#top_level_mp_term_name').val().length; it++){
			selectedFilters += '&' +'top_level_mp_term_name' + '='+$('#top_level_mp_term_name').val()[it];
			}
		}
//		for (var it = 0; it < dropdownsList.length; it++){
//			if (dropdownsList[it].array.length > 0){
//				selectedFilters += '&' + dropdownsList[it].name + '=' + dropdownsList[it].array.join('&' + dropdownsList[it].name + '=');
//			}
//		}
		newUrl += "?" + selectedFilters;		
		refreshPhenoTable(newUrl);
		return false;
	}
//	var allDropdowns = new Array();
//	allDropdowns[0] = $('#top_level_mp_term_name');
//	allDropdowns[1] = $('#resource_fullname');
//	createDropdown(allDropdowns[0],"Phenotype: All", allDropdowns);
//	createDropdown(allDropdowns[1], "Source: All", allDropdowns);

//	function createDropdown(multipleSel, emptyText,  allDd){
//		console.log("called createDropdown "+ multipleSel.size());
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
////						console.log ("here " + allDd[ii].val() + " " + allDd[ii].attr('id'));
//						dd = new Object();
//						dd.name = allDd[ii].attr('id'); 
//						dd.array = allDd[ii].val() || []; 
//						dropdownsList[ddI++] = dd;
//					}
//				}
////				console.log("call with " + dropdownsList.length);
//				refreshGenesPhenoFrag(dropdownsList);
//				addParamsToURL();
//			}, textFormatFunction: function(options) {
//				var selectedOptions = options.filter(":selected");
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
//		           case 0: console.log("Switch 0"); return emptyText;
//		           case 1: console.log("Switch 1"); return selectedOptions.text();
//		           case options.size(): console.log("Switch n"); return emptyText;
//		           default: console.log("Switch default"); return text;
//		        }
//			}
//		} );
//	}	
	
	function removeFilterSelects(){ // Remove selected options when going back to the page
		$("option:selected").removeAttr("selected");
	};
	
	//if filter parameters are already set then we need to set them as selected in the dropdowns
	var previousParams = $("#filterParams").html();
	
	$(".filterTrigger").click(function() {
		//Do stuff when clicked
		//the id is set as the field to be filtered on
		//set the value of the current id of the trigger
		
		var filter=$(this).attr("id").replace("phenIconsBox_", "");
		var values = filter.split(" or ");
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

		refreshGenesPhenoFrag(dropdownsList);
		addParamsToURL();
	});

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
        
        
        
        
