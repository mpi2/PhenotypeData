var drawTreeMap = function () {
	var data = {
        'Glucose Response': {
        		'Female outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Female outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            }
        },
        'Triglycerides': {
	        	'Female outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Female outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            }
        },
        'Body Mass': {
	        	'Female outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Female outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            }
        },
        'Metabolic Rate': {
	        	'Female outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Female outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            }
        },
        'V02': {
	        	'Female outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Female outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            }
        },
        'Resp. Exchange Rate': {
	        	'Female outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Female outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier <5%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            },
            'Male outlier >95%': {
            		'D17Wsu92e': 'MGI:106281',
            		'Slitrk1': 'MGI:2679446',
            		'Sik1': 'MGI:104754',
            		'Taar2': 'MGI:2685071'
            }
        }
    },
    points = [],
    parameterP,
    parameterVal,
    parameterI = 0,
    sexAndOutlierP,
    sexAndOutlierI,
    genesP,
    genesI,
    parameter,
    sexAndOutlier,
    genes,
    geneName = {
		'Female outlier <5%': 'Female outlier <5%',
        'Female outlier >95%': 'Female outlier >95%',
        'Male outlier <5%': 'Male outlier <5%',
        'Male outlier >95%': 'Male outlier >95%'
    };
	
	for (parameter in data) {
		if (data.hasOwnProperty(parameter)) {
	        parameterVal = 0;
	        parameterP = {
	            id: 'id_' + parameterI,
	            name: parameter,
	            color: Highcharts.getOptions().colors[parameterI]
	        };
	        sexAndOutlierI = 0;
	        for (sexAndOutlier in data[parameter]) {
	            if (data[parameter].hasOwnProperty(sexAndOutlier)) {
	                sexAndOutlierP = {
	                    id: parameterP.id + '_' + sexAndOutlierI,
	                    name: sexAndOutlier,
	                    parent: parameterP.id,
	                    // color: Highcharts.getOptions().colors[sexAndOutlierI]
	                };
	                genesI = 0;
	                for (genes in data[parameter][sexAndOutlier]) {
	                    if (data[parameter][sexAndOutlier].hasOwnProperty(genes)) {
	                        genesP = {
	                            id: sexAndOutlierP.id + '_' + genesI,
	                            name: genes,
	                            parent: sexAndOutlierP.id,
	                            description: data[parameter][sexAndOutlier][genes],
	                            value: 1
	                        };
	                        points.push(genesP);
	                        genesI = genesI + 1;
	                    }
	                }
	                sexAndOutlierI = sexAndOutlierI + 1;
	                sexAndOutlierP.value = genesI;
	                points.push(sexAndOutlierP);
	            }
	            parameterVal += sexAndOutlierP.value;
	        }
	        parameterP.value = Math.round(parameterVal); // ' / sexAndOutlierI '
	        points.push(parameterP);
	        parameterI = parameterI + 1;
	    }
	}
	Highcharts.chart('treeMapContainer', {
	    series: [{
	        type: 'treemap',
	        layoutAlgorithm: 'squarified',
	        allowDrillToNode: true,
	        animationLimit: 1000,
	        dataLabels: {
	            enabled: false
	        },
	        levelIsConstant: false,
	        levels: [{
	            level: 1,
	            dataLabels: {
	                useHTML:true,
	                enabled: true,
	                formatter:function(){
	                    if(this.point.node.isLeaf) {
	                        return '<a href="/data/genes/' + this.point.description + '"' + 'style="text-decoration: none; color: white;" target="_blank">' + this.point.name + '</a>';
	                    } else {
	                        return this.point.name;
	                    }
	                }
	            },
	            borderWidth: 3,
	            colorVariation: {
	                key: 'brightness',
	                to: -0.5
	            }
	        }
	        ],
	        data: points
	    }],
	    subtitle: {
	        text: 'Click points to drill down. Source: <a href="http://apps.who.int/gho/data/node.main.12?lang=en">WHO</a>.'
	    },
	    title: {
	        text: 'Click points to drill down.Source: WHO.'
	    },
	    tooltip: {
	    		formatter: function (tooltip) {
	            return this.point.node.level !== 3 ? this.point.value + ' genes' : false;
	            // this.point.description
	    		}
	    },
	});
	
	console.log(points);

};