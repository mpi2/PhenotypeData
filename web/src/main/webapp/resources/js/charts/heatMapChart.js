var drawHeatMap = function () {
	Highcharts.chart('heatMapContainer', {
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
	    		useHTML: true,
	    		categories: [{
	                name: '<5%',
	                categories: ['<i class="fa fa-venus" aria-hidden="true" style="padding-top: 10px;"></i>', 
	                		'<i class="fa fa-mars" aria-hidden="true" style="padding-top: 10px;"></i>']
		    		}, {
	                name: '>95%',
	                categories: ['<i class="fa fa-venus" aria-hidden="true" style="padding-top: 10px;"></i>', 
	                		'<i class="fa fa-mars" aria-hidden="true" style="padding-top: 10px;"></i>']
	    		}],
	    		labels: {
        	        useHTML: true,
        	        formatter: function () {
        	            return '<a href="#" target="_blank" class="button fa fa-download" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a>'
        	        }
        	    },
	    		opposite: true,
	    		title: {
	    			useHTML: true,
	    			text: '<a href="#" target="_blank" class="button fa fa-download" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">Outlier</a>'
	    		}
        },
	
	    yAxis: {
	        categories: ['Respiratory Exchange Ratio (RER)', 
	        		'Oxygen Consumption Rate (V02)', 
	        		'Metabolic Rate (MR)', 
	        		'Body Mass (BM)', 
	        		'Triglycerides (TG)', 
	        		'Glucose Response (AUC)', 
	        		'Fasting Glucose (T0)'],
        		labels: {
        	        formatter: function () {
        	            return '<a href="#" target="_blank" class="button fa fa-download" style="color: #525151; font-size: 1.5em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.value + '</a>'
        	        },
        	        useHTML: true
        	    },
	        title: null,
	        
	    },
	
	    colorAxis: {
	        min: 0,
	        minColor: '#FFFFFF',
	        maxColor: Highcharts.getOptions().colors[0]
	    },
	
	    legend: {
	    		enabled: false
	    },
	
	    tooltip: {
	    		useHTML: true,
            shadow: false,
            backgroundColor: "rgba(245,245,245,1)",
	        formatter: function () {
	            return '<b>' + this.series.xAxis.categories[this.point.x] + '</b> outlier <br><b>' +
	                this.point.value + '</b> genes on <br><b>' + this.series.yAxis.categories[this.point.y] + '</b>';
	        },
	        style: {
	            fontFamily: 'Source Sans Pro, Arial, Helvetica, sans-serif'
	        }
	    },
	
	    series: [{
	        name: 'Genes per sex and outlier',
	        borderWidth: 1,
	        data: [[0, 0, 17], [0, 1, 18], [0, 2, 18], [0, 3, 86], [0, 4, 72], [0, 5, 96], [0, 6, 96], [1, 0, 17], [1, 1, 18], [1, 2, 18], [1, 3, 86], [1, 4, 72], [1, 5, 96], [1, 6, 96], [2, 0, 47], [2, 1, 48], [2, 2, 48], [2, 3, 80], [2, 4, 72], [2, 5, 96], [2, 6, 96], [3, 0, 47], [3, 1, 48], [3, 2, 48], [3, 3, 86], [3, 4, 72], [3, 5, 96], [3, 6, 96]],
	        dataLabels: {
	            enabled: true,
	            useHTML:true,
	            formatter: function(){
		            return '<a href="#" target="_blank" class="button fa fa-download" style="color: #525151; font-size: 1.25em; font-family: Source Sans Pro, Arial, Helvetica, sans-serif, FontAwesome;">  ' + this.point.value + '</a>';
		        }
	        }
	    }]
	
	});
}