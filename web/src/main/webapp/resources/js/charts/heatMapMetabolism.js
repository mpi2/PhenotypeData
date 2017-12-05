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
	// console.log(dataTable1);
	
	$('#heatMapContainer').highcharts({
	    chart: {
	        type: 'heatmap',
	        marginTop: 100,
	        marginBottom: 80,
	        plotBorderWidth: 1
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
    		    		}]
    			}],
	    		labels: {
	    			borderColor: '#cccccc',
        	        useHTML: true,
        	        formatter: function () {
        	        		if (this.value == 'Outlier'){
        	        			return '<a id="outlier" class="highlightCols" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a><a style="align: left;" class="button fa fa-download"></a>'
        	        		} else if (this.value == '<5%') {
        	        			return '<a id="below5" class="highlightCols" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a>'
        	        		} else if (this.value == '>95%') {
        	        			return '<a id="above95" class="highlightCols" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a>'
        	        		} else {
        	        			if (this.pos == 0 || this.pos == 1) {
        	        				return '<a id="' + this.value.userOptions.id + '" class="highlightCols below5" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value.userOptions.name + '</a>'
        	        			} else if (this.pos == 2 || this.pos == 3) {
        	        				return '<a id="' + this.value.userOptions.id + '" class="highlightCols above5" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value.userOptions.name + '</a>'
            	        		}
        	        		} 
        	        },
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
        	        			return '<a type="button" id="' + this.value.userOptions.id + '" class="highlightRows" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a>'
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
	        data: [[0, 0, 17], [0, 1, 18], [0, 2, 18], [0, 3, 86], [0, 4, 72], [0, 5, 96], [0, 6, 96], [1, 0, 17], [1, 1, 18], [1, 2, 18], [1, 3, 86], [1, 4, 72], [1, 5, 96], [1, 6, 96], [2, 0, 47], [2, 1, 48], [2, 2, 48], [2, 3, 80], [2, 4, 72], [2, 5, 96], [2, 6, 96], [3, 0, 47], [3, 1, 48], [3, 2, 48], [3, 3, 86], [3, 4, 72], [3, 5, 96], [3, 6, 96]],
	        dataLabels: {
	            enabled: true,
	            useHTML:true,
	            formatter: function(){
		            return '<a style="color: #525151; font-size: 1.25em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">' + this.point.value + '</a>';
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
                    		var clicked = Math.round(Math.random()*50) + 20;
                    		var $div = $('<div></div>')
                	        .dialog({
                	            title: "Genes",
                	            width: 820,
                	            height: 300,
                	            position: ({
                	            		my:'center+'+clicked
                            }),
	                	    }).css("font-size", "10px");
	                        $div.append("<table>");
	                        $div.append("<thead>");
	                        $div.append("<tr>");
	                        $div.append("<th>Parameter</th>");
	                        $div.append("<th>Sex</th>");
	                        $div.append("<th>MGI</th>");
	                        $div.append("<th>GeneID</th>");
	                        $div.append("<th>Gene</th>");
	                        $div.append("<th>Center</th>");
	                        $div.append("<th>Zygosity</th>");
	                        $div.append("<th>Ratio_KO_WT</th>");
	                        $div.append("<th>Tag</th>");
	                        $div.append("</tr>");
	                        $div.append("</thead>");
	                        $div.append("<tbody>");
	                        $.each(dataTable1, function( key, value ) {
		                			var par = value.Parameter;
		                			var sex = value.Sex;
		                			var tag = value.tag;
		                			if ( parameter == par && femaleOrMale == sex && outlier == tag) {
		                				$div.append("<tr>");
		                				$div.append("<td>" + par.toUpperCase() + "</td>");
		                				$div.append("<td>" + sex + "</td>");
		                				$div.append("<td>" + value.MGI + "</td>");
		                				$div.append("<td>" + value.GeneID + "</td>");
		                				$div.append("<td>" + value.Gene + "</td>");
		                				$div.append("<td>" + value.Center + "</td>");
		                				$div.append("<td>" + value.Zygosity + "</td>");
		                				$div.append("<td>" + value.Ratio_KO_WT + "</td>");
		                				$div.append("<td>" + tag + "</td>");
		                				$div.append("</tr>");
		                			}
		                		});
	                        $div.append("<t/body>");
	                        $div.append("</table>");
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
    	});

	$(".highlightRows").click(function () {
		var id = $(this).attr('id');
		var clicked = Math.round(Math.random()*50) + 20;
		var $div = $('<div></div>')
	        .dialog({
	            title: "Genes",
	            width: 820,
	            height: 300,
	            position: ({
		            	my:'center+'+clicked
		        }),
	        }).css("font-size", "10px");
	    $div.append("<table>");
	    $div.append("<thead>");
	    $div.append("<tr>");
	    $div.append("<th>Parameter</th>");
	    $div.append("<th>Sex</th>");
	    $div.append("<th>MGI</th>");
	    $div.append("<th>GeneID</th>");
	    $div.append("<th>Gene</th>");
	    $div.append("<th>Center</th>");
	    $div.append("<th>Zygosity</th>");
	    $div.append("<th>Ratio_KO_WT</th>");
	    $div.append("<th>Tag</th>");
	    $div.append("</tr>");
	    $div.append("</thead>");
	    $div.append("<tbody>");
	    $.each(dataTable1, function( key, value ) {
			var parameter = value.Parameter;
			if ( parameter == id ) {
				$div.append("<tr>");
				$div.append("<td>" + parameter.toUpperCase() + "</td>");
				$div.append("<td>" + value.Sex + "</td>");
				$div.append("<td>" + value.MGI + "</td>");
				$div.append("<td>" + value.GeneID + "</td>");
				$div.append("<td>" + value.Gene + "</td>");
				$div.append("<td>" + value.Center + "</td>");
				$div.append("<td>" + value.Zygosity + "</td>");
				$div.append("<td>" + value.Ratio_KO_WT + "</td>");
				$div.append("<td>" + value.tag + "</td>");
				$div.append("</tr>");
			}
		});
	    $div.append("<t/body>");
	    $div.append("</table>");
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
		
		var clicked = Math.round(Math.random()*50) + 20;
		var $div = $('<div></div>')
	        .dialog({
	            title: "Genes",
	            width: 820,
	            height: 300,
	            position: ({
		            	my:'center+'+clicked
		        }),
	        }).css("font-size", "10px");
	    $div.append("<table>");
	    $div.append("<thead>");
	    $div.append("<tr>");
	    $div.append("<th>Parameter</th>");
	    $div.append("<th>Sex</th>");
	    $div.append("<th>MGI</th>");
	    $div.append("<th>GeneID</th>");
	    $div.append("<th>Gene</th>");
	    $div.append("<th>Center</th>");
	    $div.append("<th>Zygosity</th>");
	    $div.append("<th>Ratio_KO_WT</th>");
	    $div.append("<th>Tag</th>");
	    $div.append("</tr>");
	    $div.append("</thead>");
	    $div.append("<tbody>");
	    $.each(dataTable1, function( key, value ) {
			var sex = value.Sex;
			var tag = value.tag;
//			console.log(sex + ' ' + femaleOrMale);
//			console.log(tag + ' ' + outlier);
			if ( femaleOrMale == sex && outlier == tag ) {
				$div.append("<tr>");
				$div.append("<td>" + value.Parameter.toUpperCase() + "</td>");
				$div.append("<td>" + sex + "</td>");
				$div.append("<td>" + value.MGI + "</td>");
				$div.append("<td>" + value.GeneID + "</td>");
				$div.append("<td>" + value.Gene + "</td>");
				$div.append("<td>" + value.Center + "</td>");
				$div.append("<td>" + value.Zygosity + "</td>");
				$div.append("<td>" + value.Ratio_KO_WT + "</td>");
				$div.append("<td>" + tag + "</td>");
				$div.append("</tr>");
			} else if (femaleOrMale == undefined && outlier == tag) {
				$div.append("<tr>");
				$div.append("<td>" + value.Parameter.toUpperCase() + "</td>");
				$div.append("<td>" + sex + "</td>");
				$div.append("<td>" + value.MGI + "</td>");
				$div.append("<td>" + value.GeneID + "</td>");
				$div.append("<td>" + value.Gene + "</td>");
				$div.append("<td>" + value.Center + "</td>");
				$div.append("<td>" + value.Zygosity + "</td>");
				$div.append("<td>" + value.Ratio_KO_WT + "</td>");
				$div.append("<td>" + tag + "</td>");
				$div.append("</tr>");
			} else {
				$div.append("<tr>");
				$div.append("<td>" + value.Parameter.toUpperCase() + "</td>");
				$div.append("<td>" + sex + "</td>");
				$div.append("<td>" + value.MGI + "</td>");
				$div.append("<td>" + value.GeneID + "</td>");
				$div.append("<td>" + value.Gene + "</td>");
				$div.append("<td>" + value.Center + "</td>");
				$div.append("<td>" + value.Zygosity + "</td>");
				$div.append("<td>" + value.Ratio_KO_WT + "</td>");
				$div.append("<td>" + tag + "</td>");
				$div.append("</tr>");
			}
		});
	    $div.append("<t/body>");
	    $div.append("</table>");
	});

});
