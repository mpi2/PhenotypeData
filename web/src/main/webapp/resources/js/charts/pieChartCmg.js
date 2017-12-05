$(function () {
	var tier1JsonFile = baseUrl + '/documentation/json/tier1.json';
	var tier2JsonFile = baseUrl + '/documentation/json/tier2.json';
	
	function getData(urlFile) {
	    var result = null;
	    $.ajax({
	    		type: 'GET',
	    		async: false,
	    		url: urlFile,
	    		dataType: "json",
	    		success: function(data) {
	            result = data;
	    		}
	    });
	    return result;
	}
	
	var dataTier1 = getData(tier1JsonFile);
	var dataTier2 = getData(tier2JsonFile);
	
	var inProgress1 = 0;
	var produced1 = 0;
	var phenotyped1 = 0;
	var other1 = 0;
	var esCell1 = 0;
	$.each(dataTier1, function( key, value ) {
		var status = value.latest_project_status;
		if (status == "Chimeras obtained" || status == "Micro-injection in progress") {
			inProgress1++;
		} else if (status == "Genotype confirmed" || status == "Phenotype Attempt Registered" || status == "Phenotyping Started" || status == "Rederivation Complete") {
			produced1++;
		} else if (status == "Phenotyping Complete") {
			phenotyped1++;
		} else if (status == "ES Cell Targeting Confirmed") {
			esCell1++;
		} else {
			other1++;
		}
	});
	
	var inProgress2 = 0;
	var produced2 = 0;
	var phenotyped2 = 0;
	var other2 = 0;
	var esCell2 = 0;
	$.each(dataTier2, function( key, value ) {
		var status = value.latest_project_status;
		if (status == "Chimeras obtained" || status == "Micro-injection in progress") {
			inProgress2++;
		} else if (status == "Genotype confirmed" || status == "Phenotype Attempt Registered" || status == "Phenotyping Started" || status == "Rederivation Complete") {
			produced2++;
		} else if (status == "Phenotyping Complete") {
			phenotyped2++;
		} else if (status == "ES Cell Targeting Confirmed") {
			esCell2++;
		} else {
			other2++;
		}
	});
	
	// Build the chart 1
	$('#pieChart1').highcharts({
	    chart: {
	        plotBackgroundColor: null,
	        plotBorderWidth: null,
	        plotShadow: false,
	        type: 'pie'
	    },
	    title: {
	        text: 'CMG Tier 1 candidates'
	    },
	    tooltip: {
	        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	    },
	    plotOptions: {
	        pie: {
	            allowPointSelect: true,
	            cursor: 'pointer',
	            dataLabels: {
	                enabled: false
	            },
	            showInLegend: true
	        }
	    },
	    series: [{
	        name: 'Brands',
	        colorByPoint: true,
	        data: [
	    		{
	    			name: "In progress",
	    			y: inProgress1,
	    			color: "#317e9a"
	    		}, {
	    			name:"Produced", 
	    			y: produced1,
	    			color: "#6D9E80"
	    		}, {
	    			name: "Phenotyped", 
	    			y: phenotyped1,
	    			color: "#EBD4A2"
	    		}, {
	    			name: "ES Call Available",
	    			y: esCell1,
	    			color: "#741739"
	    		}, {
	    			name: "Other",
	    			y: other1,
	    			color: "#D6A847"
	    		}
	    	]
	    }]
	});
	
	// Build the chart 1
	$('#pieChart2').highcharts({
	    chart: {
	        plotBackgroundColor: null,
	        plotBorderWidth: null,
	        plotShadow: false,
	        type: 'pie'
	    },
	    title: {
	        text: 'CMG Tier 2 candidates'
	    },
	    tooltip: {
	        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	    },
	    plotOptions: {
	        pie: {
	            allowPointSelect: true,
	            cursor: 'pointer',
	            dataLabels: {
	                enabled: false
	            },
	            showInLegend: true
	        }
	    },
	    series: [{
	        name: 'Brands',
	        colorByPoint: true,
	        data: [
	    		{
	    			name: "In progress",
	    			y: inProgress2,
	    			color: "#317e9a"
	    		}, {
	    			name:"Produced", 
	    			y: produced2,
	    			color: "#6D9E80"
	    		}, {
	    			name: "Phenotyped", 
	    			y: phenotyped2,
	    			color: "#EBD4A2"
	    		}, {
	    			name: "ES Call Available",
	    			y: esCell2,
	    			color: "#741739"
	    		}, {
	    			name: "Other",
	    			y: other2,
	    			color: "#D6A847"
	    		}
	    	]
	    }]
	});
	
});