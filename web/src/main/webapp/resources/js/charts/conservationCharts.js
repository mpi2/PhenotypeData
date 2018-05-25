$(function () {
	$('#figa').highcharts({
	    chart: {
	        type: 'bar',
	        events: {
	          	load: function() {
	          		this.renderer.label('A', 0, 0).add();
	            }
	        }
	    },
	    title: {
	        text: 'All physiological systems'
	    },
	    xAxis: {
	        categories: [
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0010768">mortality/aging</a>', 
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005376">homeostasis/metabolism</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005386">behavior/neurological</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005397">hematopoietic system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005378">growth/size/body region</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005390">skeleton</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005385">cardiovascular system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005387">immune system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005391">vision/eye</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005389">reproductive system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005375">adipose tissue</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0010771">integument</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005371">limbs/digits/tail</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0003631">nervous system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005380">embryo</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005379">endocrine/exocrine gland</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005377">hearing/vestibular/ear</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005367">renal/urinary system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005382">craniofacial</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0001186">pigmentation</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005388">respiratory system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005381">digestive/alimentary</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005370">liver/biliary system</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005369">muscle</a>'
	        	],
	        	labels: {
        	        useHTML: true
	        	},
	        title: {
	            text: 'Mammalian Phenotype Ontology top level terms',
	            style: {
	            		fontWeight: 'bold'
	            }
	        }
	    },
	    yAxis: {
	        min: 0,
	        title: {
	            text: 'Number of genes with significant phenotypes',
	            align: 'high',
	            style: {
	            		fontWeight: 'bold'
	            }
	        },
	        labels: {
	            overflow: 'justify'
	        }
	    },
	    tooltip: {
	        valueSuffix: ' mouse orthologs',
	        backgroundColor: "rgba(245,245,245,1)"
	    },
	    plotOptions: {
	        bar: {
	            dataLabels: {
	                enabled: false
	            }
	        }
	    },
	    legend: {
	    		layout: 'vertical',
	        align: 'right',
	        verticalAlign: 'bottom',
	        x: 0,
	        y: -100,
	        floating: true,
	        borderWidth: 1,
	        backgroundColor: ((Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'),
	        shadow: true
	    },
	    credits: {
	        enabled: false
	    },
	    series: [{
	        name: 'Mouse IMPC',
	        data: [1592,
	        	1341,
	        	1197,
	        	911,
	        	761,
	        	661,
	        	623,
	        	505,
	        	462,
	        	400,
	        	146,
	        	333,
	        	324,
	        	286,
	        	284,
	        	254,
	        	240,
	        	136,
	        	123,
	        	79,
	        	46,
	        	44,
	        	38,
	        	19]
	    }, {
	        name: 'Giant panda',
	        data: [1531,
	        	1266,
	        	1136,
	        	867,
	        	726,
	        	626,
	        	588,
	        	478,
	        	444,
	        	373,
	        	135,
	        	319,
	        	309,
	        	277,
	        	271,
	        	245,
	        	223,
	        	127,
	        	117,
	        	72,
	        	43,
	        	39,
	        	38,
	        	18]
	    }, {
	        name: 'Grey wolf',
	        data: [1523,
	        	1270,
	        	1133,
	        	861,
	        	724,
	        	624,
	        	585,
	        	476,
	        	439,
	        	380,
	        	136,
	        	320,
	        	307,
	        	271,
	        	272,
	        	245,
	        	224,
	        	130,
	        	119,
	        	72,
	        	43,
	        	41,
	        	38,
	        	18]
	    }, {
	        name: 'Western gorilla',
	        data: [1493,
	        	1250,
	        	1109,
	        	844,
	        	699,
	        	609,
	        	585,
	        	470,
	        	435,
	        	371,
	        	310,
	        	296,
	        	269,
	        	273,
	        	242,
	        	219,
	        	134,
	        	121,
	        	113,
	        	75,
	        	43,
	        	36,
	        	38,
	        	19]
	    }, {
	        name: 'Iberian lynx',
	        data: [1445,
	        	1216,
	        	1075,
	        	831,
	        	680,
	        	589,
	        	556,
	        	459,
	        	423,
	        	356,
	        	127,
	        	300,
	        	290,
	        	255,
	        	251,
	        	230,
	        	211,
	        	124,
	        	111,
	        	71,
	        	37,
	        	42,
	        	37,
	        	17]
	    }, {
	        name: 'Tasmanian devil',
	        data: [1354,
	        	1122,
	        	984,
	        	769,
	        	631,
	        	551,
	        	515,
	        	426,
	        	407,
	        	327,
	        	268,
	        	267,
	        	239,
	        	238,
	        	217,
	        	198,
	        	126,
	        	117,
	        	103,
	        	62,
	        	42,
	        	37,
	        	32,
	        	17]
	    }, {
	        name: 'Polar bear',
	        data: [1313,
	        	1073,
	        	967,
	        	748,
	        	617,
	        	513,
	        	488,
	        	408,
	        	366,
	        	303,
	        	110,
	        	269,
	        	263,
	        	226,
	        	223,
	        	211,
	        	189,
	        	104,
	        	95,
	        	60,
	        	38,
	        	35,
	        	32,
	        	16]
	    }]
	});
	
	$('#figb').highcharts({
	    chart: {
	        type: 'bar',
	        events: {
	          	load: function() {
	          		this.renderer.label('B', 0, 0).add();
	            }
	        }
	    },
	    title: {
	        text: 'Reproductive system'
	    },
	    xAxis: {
	        categories: [
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0002059">abnormal seminal vesicle morphology</a>', 
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0001925">male infertility</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0001926">female infertility</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0001147">small testis</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0009709">hydrometra</a>'
	        	],
	        	labels: {
        	        useHTML: true
	        	},
	        title: {
	            text: 'Most frequent phenotypes',
	            style: {
	            		fontWeight: 'bold'
	            }
	        }
	    },
	    tooltip: {
	        valueSuffix: ' mouse orthologs',
	        backgroundColor: "rgba(245,245,245,1)"
	    },
	    plotOptions: {
	        bar: {
	            dataLabels: {
	                enabled: false
	            }
	        }
	    },
	    legend: {
	    		enabled: false
	    },
	    credits: {
	        enabled: false
	    },
	    series: [{
	        name: 'Mouse IMPC',
	        data: [135,
	        	130,
	        	65,
	        	63,
	        	57]
	    }, {
	        name: 'Giant panda',
	        data: [123,
	        	125,
	        	61,
	        	59,
	        	51]
	    }, {
	        name: 'Grey wolf',
	        data: [124,
	        	127,
	        	64,
	        	61,
	        	52]
	    }, {
	        name: 'Western gorilla',
	        data: [119,
	        	121,
	        	62,
	        	57,
	        	55]
	    }, {
	        name: 'Iberian lynx',
	        data: [120,
	        	114,
	        	56,
	        	56,
	        	50]
	    }, {
	        name: 'Tasmanian devil',
	        data: [107,
	        	110,
	        	54,
	        	53,
	        	43]
	    }, {
	        name: 'Polar bear',
	        data: [103,
	        	95,
	        	45,
	        	50,
	        	45]
	    }]
	});
	
	$('#figc').highcharts({
	    chart: {
	        type: 'bar',
	        events: {
	          	load: function() {
	          		this.renderer.label('C', 0, 0).add();
	            }
	        }
	    },
	    title: {
	        text: 'Immune system'
	    },
	    xAxis: {
	        categories: [
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0000221">decreased leukocyte cell number</a>', 
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0000218">increased leukocyte cell number</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0000689">abnormal spleen morphology</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0000702">enlarged lymph nodes</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0000706">small thymus</a>'
	        	],
	        	labels: {
        	        useHTML: true
        	    },
	        title: {
	            text: 'Most frequent phenotypes',
	            style: {
	            		fontWeight: 'bold'
	            }
	        }
	    },
	    tooltip: {
	        valueSuffix: ' mouse orthologs',
	        backgroundColor: "rgba(245,245,245,1)"
	    },
	    plotOptions: {
	        bar: {
	            dataLabels: {
	                enabled: false
	            }
	        }
	    },
	    legend: {
	    		enabled: false
	    },
	    credits: {
	        enabled: false
	    },
	    series: [{
	        name: 'Mouse IMPC',
	        data: [258,
	        	199,
	        	124,
	        	39,
	        	15]
	    }, {
	        name: 'Giant panda',
	        data: [243,
	        	187,
	        	118,
	        	38,
	        	15]
	    }, {
	        name: 'Grey wolf',
	        data: [240,
	        	185,
	        	119,
	        	38,
	        	14]
	    }, {
	        name: 'Western gorilla',
	        data: [241,
	        	185,
	        	115,
	        	39,
	        	14]
	    }, {
	        name: 'Iberian lynx',
	        data: [234,
	        	179,
	        	113,
	        	36,
	        	14]
	    }, {
	        name: 'Tasmanian devil',
	        data: [214,
	        	166,
	        	104,
	        	35,
	        	14]
	    }, {
	        name: 'Polar bear',
	        data: [205,
	        	155,
	        	104,
	        	30,
	        	15]
	    }]
	});
	
	$('#figd').highcharts({
	    chart: {
	        type: 'bar',
	        events: {
	          	load: function() {
	          		this.renderer.label('C', 0, 0).add();
	            }
	        }
	    },
	    title: {
	        text: 'Homeostasis/Metabolism'
	    },
	    xAxis: {
	        categories: [
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005560">decreased circulating glucose level</a>', 
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005179">decreased circulating cholesterol level</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0002968">increased circulating alkaline phosphatase level</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0005419">decreased circulating serum albumin level</a>',
	        		'<a target="_blank" href="http://www.mousephenotype.org/data/phenotypes/MP:0002644">decreased circulating triglyceride level</a>'
	        	],
	        	labels: {
        	        useHTML: true
	        	},
	        title: {
	            text: 'Most frequent phenotypes',
	            style: {
	            		fontWeight: 'bold'
	            }
	        }
	    },
	    yAxis: {
	        min: 0,
	        title: {
	            text: 'Number of genes with significant phenotypes',
	            align: 'high',
	            style: {
	            		fontWeight: 'bold'
	            }
	        },
	        labels: {
	            overflow: 'justify'
	        }
	    },
	    tooltip: {
	        valueSuffix: ' mouse orthologs',
	        backgroundColor: "rgba(245,245,245,1)"
	    },
	    plotOptions: {
	        bar: {
	            dataLabels: {
	                enabled: false
	            }
	        }
	    },
	    legend: {
	    		enabled: false
	    },
	    credits: {
	        enabled: false
	    },
	    series: [{
	        name: 'Mouse IMPC',
	        data: [205,
	        	130,
	        	128,
	        	84,
	        	83]
	    }, {
	        name: 'Giant panda',
	        data: [190,
	        	123,
	        	120,
	        	82,
	        	78]
	    }, {
	        name: 'Grey wolf',
	        data: [195,
	        	122,
	        	122,
	        	81,
	        	78]
	    }, {
	        name: 'Western gorilla',
	        data: [191,
	        	119,
	        	118,
	        	78,
	        	77]
	    }, {
	        name: 'Iberian lynx',
	        data: [185,
	        	117,
	        	113,
	        	81,
	        	75]
	    }, {
	        name: 'Tasmanian devil',
	        data: [174,
	        	114,
	        	112,
	        	69,
	        	66]
	    }, {
	        name: 'Polar bear',
	        data: [153,
	        	107,
	        	106,
	        	69,
	        	67]
	    }]
	});
});