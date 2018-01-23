$(function () {
	var url = baseUrl + '/documentation/json/metabolism_table1.json';
	var dataTable1 = getDataMetabolismTable1();
	
	function getDataMetabolismTable1() {
	    var result = null;
	    $.ajax({
	    		type: 'GET',
	    		async: false,
	    		url: url,
	    		dataType: "json",
	    		success: function(data) {
	            result = data;
	    		}
	    });
	    return result;
	}
	// console.log(dataTable);
	
	function showGeneTable(contentTable) {
		// console.log(contentTable);
		document.getElementById('metabolismTableDiv').style.display="block";
		
		$('#metabolism-table').DataTable({
			"bDestroy" : true,
			"bFilter" : true,
	        "bInfo": true, // Footer
			"bPaginate": true,
			"sPaginationType" : "bootstrap",
			"initComplete": function(settings, json) {
	            $('.dataTables_scrollBody thead tr').css({visibility:'collapse'});
	        },
			"aaSorting": [[0, "asc"]], // 0-based index
			"aoColumns": [
			    null, null,null,
	//			 {"sType": "html", "bSortable": true},
	//			 {"sType": "string", "bSortable": true},
	//			 {"sType": "string", "bSortable": true},
				 {"sType": "html", "bSortable": true}
			],
			"aaData": contentTable,
			"bDeferRender": true,
	        "aoColumns": [
	            { "mDataProp": "Parameter",
		            	"render": function ( data, type, full, meta ) {
	        		 		return data.toUpperCase();
	        		 	}
	            },
	            { "mDataProp": "Sex"},
	            { "mDataProp": "MGI_ID",
		            	"render": function ( data, type, full, meta ) {
	        		 		return '<a href="https://www.mousephenotype.org/data/genes/'+data+'" target="_blank">'+data+'</a>';
	        		 	}
	            },
	            { "mDataProp": "Gene_symbol"},
	            { "mDataProp": "Center"},
	            { "mDataProp": "Zygosity"}, 
	            { "mDataProp": "Ratio_KO_WT",
		            	"render": function ( data, type, full, meta ) {
	                		var num = parseFloat(data);
	                 	if (num > 0 && num != NaN ) {
	                 		return num.toFixed(3);
	                 	}
	    		 			return data;
	                }
	            },
	            { "mDataProp": "tag",
	                "render": function ( data, type, full, meta ) {
	                 	if (data == "below5") {
	                 		return "< 5%";
	                 	}
	                 	else return "> 95%";
	                }
	    		 	},
	       	]
		});
		
		var tsv = jsonToTsv(contentTable);
		$('#tsv-result').html(tsv);
	}
	
	$('#heatMapContainer').highcharts({
	    chart: {
	        type: 'heatmap',
	        marginTop: 100,
	        marginBottom: 80,
	        plotBorderWidth: 1
	    },
	    credits: {
	        enabled: false
	    },
	    title: {
	        text: '' 
	    },
	    xAxis: {
	    		categories: [{
	    			id: 'outlier',
    				name: 'Outlier',
    				categories: [
    					{
    						id: 'below5',
    						name: '<5%',
    		                	categories: [ {id: 'female', name: '<i class="fa fa-venus" aria-hidden="true" style="padding-top: 10px;"></i>'}, 
    		                		{id: 'male', name: '<i class="fa fa-mars" aria-hidden="true" style="padding-top: 10px;"></i>'}]
    			    		}, {
    			    			id: 'above5',
    			    			name: '>95%',
    			    			categories: [ {id: 'female', name: '<i class="fa fa-venus" aria-hidden="true" style="padding-top: 10px;"></i>'}, 
    			    				{id: 'male', name: '<i class="fa fa-mars" aria-hidden="true" style="padding-top: 10px;"></i>'}]
    		    		}],
    			}],
	    		labels: {
	    			borderColor: '#e3e3e3',
        	        useHTML: true,
        	        formatter: function () {
        	        		if (this.value == 'Outlier'){
        	        			return '<a id="outlier" class="highlightCols" style="font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome; margin: -293px; padding: 8px 293px 3px 293px;">  ' + this.value + '</a>'
        	        		} else if (this.value == '<5%') {
        	        			return '<a id="below5" class="highlightCols" style="font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome; margin: -141px; padding: 8px 141px 4px 145px;">  ' + this.value + '</a>'
        	        		} else if (this.value == '>95%') {
        	        			return '<a id="above95" class="highlightCols" style="font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome; margin: -133px; padding: 8px 133px 4px 145px;">  ' + this.value + '</a>'
        	        		} else {
        	        			if (this.pos == 0 || this.pos == 1) {
        	        				return '<a id="' + this.value.userOptions.id + '" class="highlightCols below5" style="font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome; margin: -72px; padding: 6px 72px 10px 73px;">  ' + this.value.userOptions.name + '</a>'
        	        			} else if (this.pos == 2 || this.pos == 3) {
        	        				return '<a id="' + this.value.userOptions.id + '" class="highlightCols above5" style="font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome; margin: -72px; padding: 6px 72px 10px 73px;">  ' + this.value.userOptions.name + '</a>'
            	        		}
        	        		} 
        	        }
        	    },
	    		opposite: true
        },
	    yAxis: {
	        categories: [
	        		{id: 'rer', name: 'Respiratory Exchange Ratio (RER)'}, 
	        		{id: 'vo2', name: 'Oxygen Consumption Rate (V02)'}, 
	        		{id: 'mr', name: 'Metabolic Rate (MR)'}, 
	        		{id: 'bm', name: 'Body Mass (BM)'}, 
	        		{id: 'tg', name: 'Triglycerides (TG)'}, 
	        		{id: 'auc', name: 'Glucose Response (AUC)'}, 
	        		{id: 't0', name: 'Fasting Glucose (T0)'}
	        	],
        		labels: {
        			useHTML: true, 
        	        formatter: function () {
        	        		try {
        	        			return '<a type="button" id="' + this.value.userOptions.id + '" class="highlightRows" style="font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a>'
        	        		} catch(err) {
        	        		    // console.log(err.message);
        	        		}
        	        }
        	    },
	        title: null
	    },
	    colorAxis: {
	        min: 0,
	        max: 1,
	        minColor: '#FFFFFF',
	        maxColor: '#FFFFFF'
	    },
	    legend: {
	    		enabled: false
	    },
	    tooltip: {
	    		useHTML: true,
            shadow: false,
            backgroundColor: "rgba(245,245,245,1)",
	        formatter: function () {
	            return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> </br> <b>' +
	                this.point.value + '</b> genes on <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';
	        },
	        style: {
	            fontFamily: 'Source Sans Pro, Arial, Helvetica, sans-serif'
	        }
	    },
	    series: [{
	        name: 'Genes per sex and outlier and parameter',
	        borderWidth: 1,
	        borderColor: '#cccccc',
	        data: [[0, 0, 17], [0, 1, 18], [0, 2, 18], [0, 3, 86], [0, 4, 72], [0, 5, 96], [0, 6, 94], [1, 0, 47], [1, 1, 48], [1, 2, 48], [1, 3, 86], [1, 4, 72], [1, 5, 96], [1, 6, 96], [2, 0, 17], [2, 1, 18], [2, 2, 18], [2, 3, 86], [2, 4, 72], [2, 5, 96], [2, 6, 96], [3, 0, 47], [3, 1, 48], [3, 2, 48], [3, 3, 86], [3, 4, 72], [3, 5, 96], [3, 6, 96]],
	        dataLabels: {
	            enabled: true,
	            useHTML:true,
	            formatter: function(){
		            return '<a style="font-size: 1.25em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">' + this.point.value + '</a>';
		        }
	        },
	        point: {
                events: {
                    click: function () {
                    		var arrayParameters = ['rer', 'vo2', 'mr', 'bm', 'tg', 'auc', 't0'];
                    		var xfemaleOrMale;
                    		var outlier;
                    		if (this.x == 0) {
                    			femaleOrMale = 'female';
                    			outlier = 'below5';
                    		} else if (this.x == 1 ) {
                    			femaleOrMale = 'male';
                    			outlier = 'below5';
                    		} else if (this.x == 2 ) {
                    			femaleOrMale = 'female';
                    			outlier = 'above95';
                    		} else {
                    			femaleOrMale = 'male';
                    			outlier = 'above95';
                    		}
                    		var parameter = arrayParameters[this.y];
                    		var contentTable = [];
                    		$.each(dataTable1, function( key, value ) {
	                			var par = value.Parameter;
	                			var sex = value.Sex;
	                			var tag = value.tag;
	                			if ( parameter == par && femaleOrMale == sex && outlier == tag) {
	                				contentTable.push(value);
	                			}
	                		});
                    		showGeneTable(contentTable);
                    }	
                }
            },
            states: {
	            	hover: {
	            		color: 'rgb(173,216,230)'
	            },
                select: {
                    color: 'rgb(173,216,230)'
                }
            }
	    }]
	});
	
	$(".highlightRows").hover(function () {
		var y;
		if ($(this).attr('id') == 'rer') {
			y = 0;
		} else if ($(this).attr('id') == 'vo2') {
			y = 1;
		} else if ($(this).attr('id') == 'mr') {
			y = 2;
		} else if ($(this).attr('id') == 'bm') {
			y = 3;
		} else if ($(this).attr('id') == 'tg') {
			y = 4;
		} else if ($(this).attr('id') == 'auc') {
			y = 5;
		} else if ($(this).attr('id') == 't0') {
			y = 6;
		}
		
		var chart = $('#heatMapContainer').highcharts();
		// console.dir(chart);
		for (var i=0; i < chart.series[0].data.length; i++){
	        var currentFeature = chart.series[0].data[i];
	        // console.dir(currentFeature);
	        if (currentFeature.y == y) {
	        		currentFeature.select(null,true);
	        }
		}
		
//		$(this).toggleClass("background_hover_axis");
    	});

	$(".highlightRows").click(function () {
		var id = $(this).attr('id');
		var contentTable = [];
	    $.each(dataTable1, function( key, value ) {
			var parameter = value.Parameter;
			if ( parameter == id ) {
				contentTable.push(value);
			}
		});
	    showGeneTable(contentTable);
	});
	
	$(".highlightCols").hover(function () {
		var id1;
		var id2;
		if ($(this).attr('id') == 'female') {
			if ($(this).attr('class').indexOf('below5') > -1) {
				id1 = 0;
			} else {
				id1 = 2;
			}
			
		} else if ($(this).attr('id') == 'male') {
			if ($(this).attr('class').indexOf('below5') > -1) {
				id1 = 1;
			} else {
				id1 = 3;
			}
		} else if ($(this).attr('id') == 'below5') {
			id1 = 0;
			id2 = 1;
		} else if ($(this).attr('id') == 'above95') {
			id1 = 2;
			id2 = 3;
		}
		
		var chart = $('#heatMapContainer').highcharts();
		// console.dir(chart);
		for (var i=0; i < chart.series[0].data.length; i++){
	        var currentFeature = chart.series[0].data[i];
	        // console.dir(currentFeature);
	        if (currentFeature.x == id1 || currentFeature.x == id2) {
	        		currentFeature.select(null,true);
	        } else if ( id1 == undefined && id2 == undefined ) {
	        		currentFeature.select(null,true);
	        }
		}
		
//	    $(this).toggleClass("background_hover_axis");
	});
	
	$(".highlightCols").click(function () {
		var femaleOrMale;
		var outlier;
		if ($(this).attr('id') == 'female') {
			femaleOrMale = 'female';
			if ($(this).attr('class').indexOf('below5') > -1) {
				outlier = 'below5';
			} else {
				outlier = 'above95';
			}
			
		} else if ($(this).attr('id') == 'male') {
			femaleOrMale = 'male';
			if ($(this).attr('class').indexOf('below5') > -1) {
				outlier = 'below5';
			} else {
				outlier = 'above95';
			}
		} else if ($(this).attr('id') == 'below5') {
			outlier = 'below5';
		} else if ($(this).attr('id') == 'above95') {
			outlier = 'above95';
		}

		var contentTable = [];
	    $.each(dataTable1, function( key, value ) {
			var sex = value.Sex;
			var tag = value.tag;
			// Filter by tag and sex
			if (femaleOrMale != undefined) {
				if (femaleOrMale == sex && outlier == tag) {
					contentTable.push(value);
				}
			}
			// Filter by tag
			else {
				if (outlier == tag) {
					contentTable.push(value);
				}
			} 
			// All content
			if (femaleOrMale == undefined && outlier == undefined) {
				contentTable.push(value);
			}
		});
	    showGeneTable(contentTable);
	});
	
	$("#hideTable").click(function () {
		document.getElementById('metabolismTableDiv').style.display="none";
	});
   
	function jsonToTsv (input){
        var json = input,
            tsv = '',
            firstLine = [],
            lines = [];

        // Helper to add double quotes if
        // the value is string
        var addQuotes = function(value){
            if (isNaN(value)){
                /* return '"'+value+'"'; */
                return value;
            }
            return value;
        };
        $.each(json, function(index, item){
            var newLine = [];
            $.each(item, function(key, value){
                if (index === 0){
                    firstLine.push(addQuotes(key));
                }
                newLine.push(addQuotes(value));
            });
            lines.push(newLine.join('\t'));
        });
        tsv = firstLine.join('\t');
        tsv += '\n'+lines.join('\n');
        return tsv;
    }; 
    
    function downloadInnerHtml(filename, elId, mimeType) {
        var elHtml = document.getElementById(elId).innerHTML;
        var link = document.createElement('a');
        mimeType = mimeType || 'text/plain';

        link.setAttribute('download', filename);
        link.setAttribute('href', 'data:' + mimeType + ',' + encodeURIComponent(elHtml));
        document.body.appendChild(link);
        link.click(); 
        document.body.removeChild(link);
    }

    var fileNameTsv =  'metabolism-genes.tsv'; // You can use the .txt extension if you want
    var fileNameExcel =  'metabolism-genes.xls'; // You can use the .txt extension if you want

    $('#downloadTsv').click(function(){
        downloadInnerHtml(fileNameTsv, 'tsv-result','text/html');
    });
    
    $('#downloadExcel').click(function(){
        downloadInnerHtml(fileNameExcel, 'tsv-result','application/xls;charset=utf-8');
    });
    
});
