<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 4.3</li>
                <li>Published:&nbsp;26 April 2016</li>
            </ul>
        </div>

        <div>
            <h4>Statistical Package</h4>
            <ul class="mt-0">
                <li>PhenStat</li>
                <li>Version:&nbsp;2.2.0</li>
            </ul>
        </div>

        <div>
            <h4>Genome Assembly</h4>
            <ul class="mt-0">
                <li>Mus musculus</li>
                <li>Version: GRCm38</li>
            </ul>
        </div>
    </div>

    <div class="col-6">
        <div>
            <h4>Summary</h4>
            <ul class="mt-0">
                <li>Number of phenotyped genes:&nbsp;2,432</li>
                <li>Number of phenotyped mutant lines:&nbsp;2,562</li>
                <li>Number of phenotype calls:&nbsp;21,667</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-4.3">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-4.3</a>
                </li>
                <li>RESTful interfaces:&nbsp;<a href="/data/documentation/api-help">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2 id="new-features">Highlights</h2>

        <div class="col-12">

        <h3>Data release 4.3</h3>

            <div class="well">
                <strong>Data release 4.3 represents a minor data release. Changes include:</strong>
                <ul>
                    <li>Additional embryo data</li>
                    <li>Additional viability data</li>
                    <li>Changes to the analysis of Auditory Brain Stem Response parameters. When statistical analysis is
                        not appropriate for mixed model analysis, analysis will proceed using the Reference Range Plus
                        method as described in the PhenStat user guide
                    </li>
                    <li>Control group formation is split when specimen production centre is different from phenotyping
                        centre
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2 class="title" id="data_reports">Data Reports</h2>

        <div class="col-12">

            <h3>Lines and Specimens</h3>

            <table id="lines_specimen">
                <thead>
                    <tr>
                        <th class="headerSort">Phenotyping Center</th>
                        <th class="headerSort">Mutant Lines</th>
                        <th class="headerSort">Baseline Mice</th>
                        <th class="headerSort">Mutant Mice</th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>JAX</td>
                        <td>429</td>
                        <td>3,636</td>
                        <td>12,248</td>
                    </tr>
                    <tr>
                        <td>NING</td>
                        <td>93</td>
                        <td>985</td>
                        <td>1,362</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>240</td>
                        <td>3,889</td>
                        <td>11,093</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>126</td>
                        <td>1,277</td>
                        <td>2,189</td>
                    </tr>
                    <tr>
                        <td>MRC Harwell</td>
                        <td>309</td>
                        <td>2,983</td>
                        <td>7,331</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>32</td>
                        <td>980</td>
                        <td>701</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>104</td>
                        <td>1,038</td>
                        <td>1,655</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>611</td>
                        <td>2,664</td>
                        <td>13,944</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>143</td>
                        <td>1,426</td>
                        <td>4,817</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>476</td>
                        <td>1,370</td>
                        <td>10,520</td>
                    </tr>

                </tbody>
            </table>

            <h3>Experimental Data and Quality Checks</h3>

            <table id="exp_data">
                <thead>
                    <tr>
                        <th class="headerSort">Data Type</th>
                        <th class="headerSort">QC passed</th>
                        <th class="headerSort">QC failed</th>
                        <th class="headerSort">issues</th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>categorical</td>
                        <td>
                            6,597,532
                        </td>
                        <td>
                            756
                            <sup>*</sup>
                        </td>
                        <td>
                            4,764
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            4,543,845
                        </td>
                        <td>
                            7,503
                            <sup>*</sup>
                        </td>
                        113,801
                        <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            9,077,104
                        </td>
                        <td>
                            128
                            <sup>*</sup>
                        </td>
                        <td>
                            80,403
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>
                            89,429
                        </td>
                        <td>
                            14,625
                            <sup>*</sup>
                        </td>
                        <td>
                            17,159
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            211,801
                        </td>
                        <td>
                            0
                        </td>
                        <td>
                            0
                        </td>
                    </tr>
                </tbody>
            </table>

            <p><sup>*</sup>&nbsp;Excluded from statistical analysis.</p>

            <h3>Procedures</h3>

            <div id="lineProcedureChart">
                <script type="text/javascript">
                    $(function () {
                        var chart_lineProcedureChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_lineProcedureChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: lineProcedureChart
                            },
                            title: {
                                text: 'Lines per procedure'
                            },
                            subtitle: {
                                text: "Center by center"
                            },
                            xAxis: {
                                categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Combined SHIRPA and Dysmorphology", "Open Field", "DSS Histology", "Rotarod", "FACS", "Organ Weight", "Challenge Whole Body Plethysmography", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Embryo LacZ", "Eye Morphology", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Gross Morphology Placenta E9.5", "Gross Morphology Placenta E12.5", "Gross Morphology Placenta E14.5-E15.5", "Gross Morphology Placenta E18.5", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Anti-nuclear antibody assay", "Buffy coat peripheral blood leukocyte immunophenotyping", "Bone marrow immunophenotyping", "Ear epidermis immunophenotyping", "Spleen Immunophenotyping", "Mesenteric Lymph Node Immunophenotyping", "Whole blood peripheral blood leukocyte immunophenotyping", "Tail Flick", "Trichuris"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Number of lines'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f} lines</b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "data": [316, 268, 254, 0, 129, 0, 0, 0, 0, 0, 0, 321, 251, 84, 414, 0, 195, 272, 273, 0, 159, 291, 49, 81, 13, 20, 0, 26, 4, 2, 266, 109, 118, 198, 207, 57, 234, 140, 257, 161, 70, 99, 259, 371, 139, 209, 197, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "JAX"
                            }, {
                                "data": [58, 1, 53, 0, 0, 0, 0, 0, 0, 0, 0, 56, 0, 64, 91, 13, 30, 52, 0, 56, 0, 41, 0, 0, 0, 0, 0, 0, 0, 0, 70, 30, 0, 38, 39, 0, 56, 45, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "NING"
                            }, {
                                "data": [156, 169, 180, 0, 0, 0, 0, 34, 0, 0, 0, 132, 40, 184, 230, 166, 152, 174, 169, 0, 77, 170, 53, 81, 36, 0, 53, 81, 36, 0, 164, 149, 110, 86, 48, 0, 159, 182, 184, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0],
                                "name": "TCP"
                            }, {
                                "data": [72, 90, 85, 0, 80, 0, 0, 0, 0, 0, 0, 87, 67, 9, 120, 95, 86, 74, 104, 112, 0, 92, 0, 0, 0, 0, 0, 0, 0, 0, 85, 89, 0, 48, 51, 56, 99, 67, 89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "HMGU"
                            }, {
                                "data": [275, 295, 283, 0, 0, 87, 268, 0, 0, 0, 0, 167, 168, 0, 309, 213, 273, 260, 131, 227, 119, 271, 0, 0, 0, 0, 0, 0, 0, 0, 291, 266, 0, 0, 2, 0, 273, 154, 286, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MRC Harwell"
                            }, {
                                "data": [22, 18, 25, 0, 0, 0, 0, 9, 0, 0, 0, 1, 19, 28, 32, 29, 23, 27, 3, 0, 25, 27, 0, 0, 0, 0, 0, 0, 0, 0, 29, 25, 0, 24, 25, 0, 28, 26, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "RBRC"
                            }, {
                                "data": [59, 45, 84, 0, 87, 0, 0, 96, 81, 67, 78, 88, 0, 0, 102, 86, 39, 66, 97, 102, 0, 55, 0, 0, 0, 0, 0, 0, 0, 0, 84, 48, 0, 53, 0, 0, 68, 55, 95, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "ICS"
                            }, {
                                "data": [0, 520, 0, 1, 0, 0, 0, 0, 0, 0, 0, 170, 0, 0, 609, 344, 505, 474, 0, 0, 0, 491, 0, 0, 0, 0, 0, 0, 0, 0, 495, 518, 37, 425, 0, 340, 544, 0, 591, 0, 0, 0, 0, 0, 0, 0, 0, 0, 327, 347, 295, 325, 316, 313, 289, 0, 83],
                                "name": "WTSI"
                            }, {
                                "data": [141, 142, 121, 0, 0, 0, 0, 4, 0, 0, 0, 128, 112, 5, 131, 50, 17, 106, 126, 132, 50, 102, 14, 42, 11, 26, 0, 0, 0, 0, 124, 1, 0, 52, 45, 0, 113, 129, 111, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "BCM"
                            }, {
                                "data": [151, 191, 219, 0, 0, 0, 0, 0, 0, 0, 0, 425, 185, 0, 456, 183, 111, 237, 416, 0, 0, 343, 6, 0, 0, 0, 0, 0, 0, 0, 364, 227, 0, 196, 0, 57, 367, 270, 231, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "UC Davis"
                            }]
                        });
                        $('#checkAllProcedures').click(function () {
                            for (i = 0; i < chart_lineProcedureChart.series.length; i++) {
                                if (chart_lineProcedureChart.series[i].visible == false) {
                                    chart_lineProcedureChart.series[i].show();
                                }
                            }
                        });
                        $('#uncheckAllProcedures').click(function () {
                            for (i = 0; i < chart_lineProcedureChart.series.length; i++) {
                                if (chart_lineProcedureChart.series[i].visible == true) {
                                    chart_lineProcedureChart.series[i].hide();
                                }
                            }
                        });
                    });

                </script>
            </div>
	
            <a id="checkAllProcedures" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
            <a id="uncheckAllProcedures"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
            <div class="clear both"></div>

            <h3 class="mt-4">Allele Types</h3>

            <table id="allele_types">
                <thead>
                    <tr>
                        <th class="headerSort">Mutation</th>
                        <th class="headerSort">Name</th>
                        <th class="headerSort">Mutant Lines</th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>737</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>a</td>
                        <td>433</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>1331</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>2</td>
                        <td>5</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>c</td>
                        <td>2</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>d</td>
                        <td>1</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>e</td>
                        <td>44</td>
                    </tr>
                </tbody>
            </table>

            <p>Mouse knockout programs:&nbsp;KOMP,EUCOMM,NCOM,mirKO,IST12471H5</p>

        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h2>Distribution of Phenotype Annotations</h2>
        <div id="distribution">
            <script type="text/javascript">
                $(function () {
                    var chart_distribution;
                    Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                    chart_distribution = new Highcharts.Chart({
                        chart: {
                            type: 'column',
                            height: 800,
                            renderTo: distribution
                        },
                        title: {
                            text: 'Distribution of Phenotype Associations in IMPC'
                        },
                        subtitle: {
                            text: ""
                        },
                        xAxis: {
                            categories: ["adipose tissue phenotype", "behavior/neurological phenotype", "craniofacial phenotype", "endocrine/exocrine gland phenotype", "growth/size/body region phenotype", "hearing/vestibular/ear phenotype", "hematopoietic system phenotype", "homeostasis/metabolism phenotype", "immune system phenotype", "integument phenotype", "limbs/digits/tail phenotype", "mortality/aging", "reproductive system phenotype", "respiratory system phenotype", "skeleton phenotype", "vision/eye phenotype", "cardiovascular system phenotype", "digestive/alimentary phenotype", "embryo phenotype", "liver/biliary system phenotype", "muscle phenotype", "nervous system phenotype", "pigmentation phenotype", "renal/urinary system phenotype", "normal phenotype"],
                            labels: {
                                rotation: -90,
                                align: 'right',
                                style: {
                                    fontSize: '11px',
                                    fontFamily: 'Verdana, sans-serif'
                                }
                            },
                            showLastLabel: true
                        },
                        yAxis: {
                            min: 0,
                            title: {
                                text: 'Number of Lines'
                            }
                        },
                        credits: {
                            enabled: false
                        },
                        tooltip: {
                            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                '<td style="padding:0"><b>{point.y:.0f}  lines</b></td></tr>',
                            footerFormat: '</table>',
                            shared: true,
                            useHTML: true
                        },
                        plotOptions: {
                            column: {
                                stacking: 'normal',
                                pointPadding: 0.2,
                                borderWidth: 0
                            }
                        },
                        series: [{
                            "data": [4, 19, 2, 1, 19, 1, 16, 22, 11, 2, 3, 6, 5, 1, 12, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "hemizygote"
                        }, {
                            "data": [139, 861, 35, 65, 625, 54, 729, 998, 458, 97, 108, 0, 56, 41, 454, 337, 182, 7, 185, 5, 4, 211, 63, 32, 0],
                            "name": "heterozygote"
                        }, {
                            "data": [463, 2945, 195, 129, 1846, 363, 2459, 2795, 1519, 318, 496, 2350, 269, 42, 1761, 675, 551, 36, 273, 26, 12, 342, 190, 78, 4],
                            "name": "homozygote"
                        }]
                    });
                });

            </script>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h2 class="title" id="section-associations"> Status </h2>
        <div class="row">
            <div class="col-12">
                <h4>Overall</h4>
                <div class="row">
                    <div class="col-6">
                        <div id="genotypeStatusChart">
                            <script type="text/javascript">$(function () {
                                $('#genotypeStatusChart').highcharts({
                                    chart: {type: 'column'},
                                    title: {text: 'Genotyping Status'},
                                    credits: {enabled: false},
                                    xAxis: {
                                        type: 'category',
                                        labels: {
                                            rotation: -90,
                                            style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    },
                                    yAxis: {min: 0, title: {text: 'Number of genes'}},
                                    legend: {enabled: false},
                                    tooltip: {pointFormat: '<b>{point.y}</b>'},
                                    series: [{
                                        name: 'Population',
                                        data: [['Micro-injection in progress', 113], ['Chimeras obtained', 70], ['Genotype confirmed', 2371], ['Cre Excision Started', 61], ['Cre Excision Complete', 3101],],
                                        dataLabels: {
                                            enabled: true,
                                            style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    }]
                                });
                            });</script>
                        </div>
                    </div>

                    <div class="col-6">
                        <div id="phenotypeStatusChart">
                            <script type="text/javascript">$(function () {
                                $('#phenotypeStatusChart').highcharts({
                                    chart: {type: 'column'},
                                    title: {text: 'Phenotyping Status'},
                                    credits: {enabled: false},
                                    xAxis: {
                                        type: 'category',
                                        labels: {
                                            rotation: -90,
                                            style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    },
                                    yAxis: {min: 0, title: {text: 'Number of genes'}},
                                    legend: {enabled: false},
                                    tooltip: {pointFormat: '<b>{point.y}</b>'},
                                    series: [{
                                        name: 'Population',
                                        data: [['Phenotype Attempt Registered', 1089], ['Phenotyping Started', 606], ['Phenotyping Complete', 3262],],
                                        dataLabels: {
                                            enabled: true,
                                            style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    }]
                                });
                            });</script>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-12">
        <h3>By Center</h3>
        <div class="row">
            <div class="col-6">
                <div id="genotypeStatusByCenterChart">
                    <script type="text/javascript">$(function () {
                        var chart_genotypeStatusByCenterChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_genotypeStatusByCenterChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: genotypeStatusByCenterChart
                            },
                            title: {
                                text: 'Genotyping Status by Center'
                            },
                            subtitle: {
                                text: ""
                            },
                            xAxis: {
                                categories: ["Micro-injection in progress", "Chimeras obtained", "Genotype confirmed", "Cre Excision Started", "Cre Excision Complete"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Number of Genes'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f}  genes</b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{"data": [0, 0, 187, 0, 683], "name": "JAX"}, {
                                "data": [0, 0, 46, 0, 263],
                                "name": "TCP"
                            }, {"data": [6, 0, 9, 1, 12], "name": "IMG"}, {
                                "data": [4, 0, 34, 0, 2],
                                "name": "Monterotondo"
                            }, {"data": [0, 0, 1, 0, 0], "name": "VETMEDUNI"}, {
                                "data": [90, 39, 116, 26, 369],
                                "name": "Harwell"
                            }, {"data": [4, 9, 4, 0, 0], "name": "SEAT"}, {
                                "data": [0, 0, 184, 3, 60],
                                "name": "MARC"
                            }, {
                                "data": [0, 0, 1, 0, 1],
                                "name": "INFRAFRONTIER-VETMEDUNI"
                            }, {"data": [0, 0, 37, 11, 410], "name": "BCM"}, {
                                "data": [0, 0, 1, 0, 0],
                                "name": "INFRAFRONTIER-CNB"
                            }, {"data": [0, 0, 0, 0, 21], "name": "CDTA"}, {
                                "data": [3, 0, 35, 21, 236],
                                "name": "HMGU"
                            }, {"data": [0, 2, 8, 0, 0], "name": "CAM-SU GRC"}, {
                                "data": [0, 0, 0, 0, 0],
                                "name": "CIPHE"
                            }, {"data": [0, 0, 1, 0, 0], "name": "INFRAFRONTIER-Oulu"}, {
                                "data": [4, 14, 6, 0, 1],
                                "name": "KMPC"
                            }, {"data": [0, 0, 373, 2, 685], "name": "UCD"}, {
                                "data": [0, 5, 101, 0, 116],
                                "name": "ICS"
                            }, {"data": [0, 0, 4, 0, 0], "name": "INFRAFRONTIER-IMG"}, {
                                "data": [2, 1, 1202, 32, 212],
                                "name": "WTSI"
                            }, {"data": [0, 0, 7, 0, 2], "name": "NARLabs"}, {
                                "data": [0, 0, 14, 0, 28],
                                "name": "RIKEN BRC"
                            }]
                        });
                        $('#checkAllGenByCenter').click(function () {
                            for (i = 0; i < chart_genotypeStatusByCenterChart.series.length; i++) {
                                if (chart_genotypeStatusByCenterChart.series[i].visible == false) {
                                    chart_genotypeStatusByCenterChart.series[i].show();
                                }
                            }
                        });
                        $('#uncheckAllGenByCenter').click(function () {
                            for (i = 0; i < chart_genotypeStatusByCenterChart.series.length; i++) {
                                if (chart_genotypeStatusByCenterChart.series[i].visible == true) {
                                    chart_genotypeStatusByCenterChart.series[i].hide();
                                }
                            }
                        });
                    });
                    </script>
                </div>
                <a id="checkAllGenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                <a id="uncheckAllGenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                <div class="clear both"></div>
            </div>

            <div class="col-6">
                <div id="phenotypeStatusByCenterChart">
                    <script type="text/javascript">$(function () {
                        var chart_phenotypeStatusByCenterChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_phenotypeStatusByCenterChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: phenotypeStatusByCenterChart
                            },
                            title: {
                                text: 'Phenotyping Status by Center'
                            },
                            subtitle: {
                                text: ""
                            },
                            xAxis: {
                                categories: ["Phenotype Attempt Registered", "Phenotyping Started", "Phenotyping Complete"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Number of Genes'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f}  genes</b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{"data": [206, 1, 552], "name": "JAX"}, {
                                "data": [37, 0, 243],
                                "name": "TCP"
                            }, {"data": [7, 0, 0], "name": "IMG"}, {
                                "data": [2, 0, 0],
                                "name": "Monterotondo"
                            }, {"data": [110, 10, 398], "name": "Harwell"}, {
                                "data": [85, 0, 96],
                                "name": "MARC"
                            }, {"data": [1, 0, 0], "name": "INFRAFRONTIER-VETMEDUNI"}, {
                                "data": [210, 1, 161],
                                "name": "BCM"
                            }, {"data": [73, 0, 163], "name": "HMGU"}, {
                                "data": [1, 0, 0],
                                "name": "CAM-SU GRC"
                            }, {"data": [2, 0, 0], "name": "CIPHE"}, {
                                "data": [1, 2, 1],
                                "name": "KMPC"
                            }, {"data": [95, 1, 545], "name": "UCD"}, {
                                "data": [52, 1, 187],
                                "name": "ICS"
                            }, {"data": [437, 0, 985], "name": "WTSI"}, {"data": [7, 1, 31], "name": "RIKEN BRC"}]
                        });
                        $('#checkAllPhenByCenter').click(function () {
                            for (i = 0; i < chart_phenotypeStatusByCenterChart.series.length; i++) {
                                if (chart_phenotypeStatusByCenterChart.series[i].visible == false) {
                                    chart_phenotypeStatusByCenterChart.series[i].show();
                                }
                            }
                        });
                        $('#uncheckAllPhenByCenter').click(function () {
                            for (i = 0; i < chart_phenotypeStatusByCenterChart.series.length; i++) {
                                if (chart_phenotypeStatusByCenterChart.series[i].visible == true) {
                                    chart_phenotypeStatusByCenterChart.series[i].hide();
                                }
                            }
                        });
                    });
                    </script>
                </div>
                <a id="checkAllPhenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                <a id="uncheckAllPhenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                <div class="clear both"></div>
            </div>
        </div>
        <p>More charts and status information are available from <a href="https://www.mousephenotype.org/imits/v2/reports/mi_production/komp2_graph_report_display">iMits</a>. </p>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2>Phenotype Associations</h2>

        <div id="callProcedureChart">
            <script type="text/javascript">
                $(function () {
                    var chart_callProcedureChart;
                    Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                    chart_callProcedureChart = new Highcharts.Chart({
                        chart: {
                            type: 'column',
                            height: 800,
                            renderTo: callProcedureChart
                        },
                        title: {
                            text: 'Phenotype calls per procedure'
                        },
                        subtitle: {
                            text: "Center by center"
                        },
                        xAxis: {
                            categories: ["Combined SHIRPA and Dysmorphology", "Open Field", "Auditory Brain Stem Response", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Heart Weight", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Hematology", "Immunophenotyping", "Insulin Blood Level", "Rotarod", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Organ Weight"],
                            labels: {
                                rotation: -90,
                                align: 'right',
                                style: {
                                    fontSize: '11px',
                                    fontFamily: 'Verdana, sans-serif'
                                }
                            },
                            showLastLabel: true
                        },
                        yAxis: {
                            min: 0,
                            title: {
                                text: 'Number of phenotype calls'
                            }
                        },
                        credits: {
                            enabled: false
                        },
                        tooltip: {
                            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                '<td style="padding:0"><b>{point.y:.0f} calls</b></td></tr>',
                            footerFormat: '</table>',
                            shared: true,
                            useHTML: true
                        },
                        plotOptions: {
                            column: {
                                stacking: 'normal',
                                pointPadding: 0.2,
                                borderWidth: 0
                            }
                        },
                        series: [{
                            "data": [56, 89, 28, 0, 234, 308, 83, 0, 54, 7, 45, 6, 51, 366, 0, 32, 58, 119, 2, 7, 4, 14, 131, 58, 152, 11, 1, 0],
                            "name": "JAX"
                        }, {
                            "data": [9, 51, 7, 0, 29, 54, 0, 10, 4, 1, 18, 2, 6, 72, 35, 2, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "NING"
                        }, {
                            "data": [74, 127, 16, 2, 124, 275, 37, 0, 34, 6, 33, 3, 12, 138, 118, 11, 144, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "TCP"
                        }, {
                            "data": [23, 108, 15, 5, 62, 18, 13, 13, 215, 10, 41, 3, 23, 70, 14, 27, 56, 19, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "HMGU"
                        }, {
                            "data": [191, 205, 36, 5, 243, 277, 18, 30, 82, 3, 54, 0, 79, 250, 142, 49, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63],
                            "name": "MRC Harwell"
                        }, {
                            "data": [3, 2, 0, 3, 11, 38, 0, 0, 0, 3, 3, 4, 4, 28, 25, 2, 26, 45, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "RBRC"
                        }, {
                            "data": [25, 62, 20, 2, 25, 35, 18, 8, 53, 2, 31, 8, 5, 118, 25, 6, 29, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "ICS"
                        }, {
                            "data": [0, 0, 100, 2, 517, 370, 0, 0, 110, 13, 126, 11, 41, 318, 365, 0, 193, 0, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "WTSI"
                        }, {
                            "data": [37, 32, 50, 1, 3, 110, 15, 31, 35, 2, 12, 6, 7, 148, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "BCM"
                        }, {
                            "data": [44, 45, 73, 1, 39, 157, 14, 0, 4, 4, 63, 12, 40, 324, 69, 10, 60, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "UC Davis"
                        }]
                    });
                    $('#checkAllPhenCalls').click(function () {
                        for (i = 0; i < chart_callProcedureChart.series.length; i++) {
                            if (chart_callProcedureChart.series[i].visible == false) {
                                chart_callProcedureChart.series[i].show();
                            }
                        }
                    });
                    $('#uncheckAllPhenCalls').click(function () {
                        for (i = 0; i < chart_callProcedureChart.series.length; i++) {
                            if (chart_callProcedureChart.series[i].visible == true) {
                                chart_callProcedureChart.series[i].hide();
                            }
                        }
                    });
                });

            </script>
        </div>
							
        <a id="checkAllPhenCalls" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllPhenCalls"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2>Phenotype Associations Overview</h2>

        <p>We provide a 'phenome' overview of statistically significant calls.
            By following the links below, you'll access the details of the phenotype calls for each center.</p>
        <table>
            <thead>
                <tr>
                    <th class="headerSort">Phenotyping Center</th>
                    <th class="headerSort">Significant MP Calls</th>
                    <th class="headerSort">Pipeline</th>
                </tr>
            </thead>

            <tbody>
                <tr><td>JAX</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>JAX</td><td><a href="/data/page-retired">Browse</a></td><td>JAX_001</td></tr>
                <tr><td>NING</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>TCP</td><td><a href="/data/page-retired">Browse</a></td><td>TCP_001</td></tr>
                <tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>HMGU_001</td></tr>
                <tr><td>MRC Harwell</td><td><a href="/data/page-retired">Browse</a></td><td>HRWL_001</td></tr>
                <tr><td>RBRC</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>ICS</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>ICS</td><td><a href="/data/page-retired">Browse</a></td><td>ICS_001</td></tr>
                <tr><td>WTSI</td><td><a href="/data/page-retired">Browse</a></td><td>MGP_001</td></tr>
                <tr><td>BCM</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>BCM</td><td><a href="/data/page-retired">Browse</a></td><td>BCM_001</td></tr>
                <tr><td>UC Davis</td><td><a href="/data/page-retired">Browse</a></td><td>UCD_001</td></tr>
            </tbody>
        </table>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2 class="title" id="statistical-analysis">Statistical Analysis</h2>

        <div class="col-12">

            <h3>Statistical Methods</h3>

            <table id="statistical_methods">
                <thead>
                    <tr>
                        <th class="headerSort">Data</th>
                        <th class="headerSort">Statistical Method</th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>categorical</td>
                        <td>Fisher's exact test</td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>Wilcoxon rank sum test with continuity correction</td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>Mixed Model framework, generalized least squares, equation withoutWeight</td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>Mixed Model framework, linear mixed-effects model, equation withoutWeight</td>
                    </tr>
                </tbody>
            </table>

            <h3>P-value distributions</h3>

            <div id="FisherChart">
                <script type="text/javascript">
                    $(function () {
                        var chart_FisherChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_FisherChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: FisherChart
                            },
                            title: {
                                text: 'P-value distribution'
                            },
                            subtitle: {
                                text: "Fisher's exact test"
                            },
                            xAxis: {
                                categories: ["0.02", "0.04", "0.06", "0.08", "0.1", "0.12", "0.14", "0.16", "0.18", "0.2", "0.22", "0.24", "0.26", "0.28", "0.3", "0.32", "0.34", "0.36", "0.38", "0.4", "0.42", "0.44", "0.46", "0.48", "0.5", "0.52", "0.54", "0.56", "0.58", "0.6", "0.62", "0.64", "0.66", "0.68", "0.7", "0.72", "0.74", "0.76", "0.78", "0.8", "0.82", "0.84", "0.86", "0.88", "0.9", "0.92", "0.94", "0.96", "0.98", "1"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Frequency'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "data": [5642, 2253, 1591, 1225, 1124, 1030, 1279, 1154, 684, 797, 489, 233, 707, 470, 240, 403, 347, 490, 412, 156, 149, 343, 288, 156, 334, 280, 107, 380, 282, 152, 754, 172, 247, 165, 172, 181, 75, 117, 129, 189, 129, 102, 121, 60, 30, 22, 15, 8, 0, 541428],
                                "name": "Fisher's exact test"
                            }]
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_FisherChart.series.length; i++) {
                                if (chart_FisherChart.series[i].visible == false) {
                                    chart_FisherChart.series[i].show();
                                }
                            }
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_FisherChart.series.length; i++) {
                                if (chart_FisherChart.series[i].visible == true) {
                                    chart_FisherChart.series[i].hide();
                                }
                            }
                        });
                    });

                </script>
            </div>

            <div id="WilcoxonChart">
                <script type="text/javascript">
                    $(function () {
                        var chart_WilcoxonChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_WilcoxonChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: WilcoxonChart
                            },
                            title: {
                                text: 'P-value distribution'
                            },
                            subtitle: {
                                text: "Wilcoxon rank sum test with continuity correction"
                            },
                            xAxis: {
                                categories: ["0.02", "0.04", "0.06", "0.08", "0.1", "0.12", "0.14", "0.16", "0.18", "0.2", "0.22", "0.24", "0.26", "0.28", "0.3", "0.32", "0.34", "0.36", "0.38", "0.4", "0.42", "0.44", "0.46", "0.48", "0.5", "0.52", "0.54", "0.56", "0.58", "0.6", "0.62", "0.64", "0.66", "0.68", "0.7", "0.72", "0.74", "0.76", "0.78", "0.8", "0.82", "0.84", "0.86", "0.88", "0.9", "0.92", "0.94", "0.96", "0.98", "1"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Frequency'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "data": [2401, 2220, 2277, 3072, 3154, 3354, 3356, 3647, 3659, 3728, 3708, 3702, 3762, 3778, 4342, 4158, 3973, 4273, 4013, 4178, 3970, 4656, 4117, 4179, 4206, 3824, 3832, 3836, 3715, 3567, 3599, 3446, 4216, 3534, 3116, 3099, 2993, 2860, 2914, 3748, 4185, 4085, 4572, 3818, 3254, 2113, 1513, 1435, 2029, 3194],
                                "name": "Wilcoxon rank sum test with continuity correction"
                            }]
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_WilcoxonChart.series.length; i++) {
                                if (chart_WilcoxonChart.series[i].visible == false) {
                                    chart_WilcoxonChart.series[i].show();
                                }
                            }
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_WilcoxonChart.series.length; i++) {
                                if (chart_WilcoxonChart.series[i].visible == true) {
                                    chart_WilcoxonChart.series[i].hide();
                                }
                            }
                        });
                    });

                </script>
            </div>

            <div id="MMglsChart">
                <script type="text/javascript">
                    $(function () {
                        var chart_MMglsChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_MMglsChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: MMglsChart
                            },
                            title: {
                                text: 'P-value distribution'
                            },
                            subtitle: {
                                text: "Mixed Model framework, generalized least squares, equation withoutWeight"
                            },
                            xAxis: {
                                categories: ["0.02", "0.04", "0.06", "0.08", "0.1", "0.12", "0.14", "0.16", "0.18", "0.2", "0.22", "0.24", "0.26", "0.28", "0.3", "0.32", "0.34", "0.36", "0.38", "0.4", "0.42", "0.44", "0.46", "0.48", "0.5", "0.52", "0.54", "0.56", "0.58", "0.6", "0.62", "0.64", "0.66", "0.68", "0.7", "0.72", "0.74", "0.76", "0.78", "0.8", "0.82", "0.84", "0.86", "0.88", "0.9", "0.92", "0.94", "0.96", "0.98", "1"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Frequency'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "data": [2788, 961, 688, 611, 467, 456, 425, 397, 380, 347, 389, 358, 353, 334, 322, 299, 297, 281, 307, 313, 316, 264, 256, 296, 269, 326, 228, 270, 305, 289, 293, 250, 263, 235, 243, 266, 268, 241, 307, 217, 233, 269, 276, 251, 290, 219, 232, 241, 263, 263],
                                "name": "Mixed Model framework, generalized least squares, equation withoutWeight"
                            }]
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_MMglsChart.series.length; i++) {
                                if (chart_MMglsChart.series[i].visible == false) {
                                    chart_MMglsChart.series[i].show();
                                }
                            }
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_MMglsChart.series.length; i++) {
                                if (chart_MMglsChart.series[i].visible == true) {
                                    chart_MMglsChart.series[i].hide();
                                }
                            }
                        });
                    });
                </script>
            </div>

            <div id="MMlmeChart">
                <script type="text/javascript">
                    $(function () {
                        var chart_MMlmeChart;
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
                        chart_MMlmeChart = new Highcharts.Chart({
                            chart: {
                                type: 'column',
                                height: 800,
                                renderTo: MMlmeChart
                            },
                            title: {
                                text: 'P-value distribution'
                            },
                            subtitle: {
                                text: "Mixed Model framework, linear mixed-effects model, equation withoutWeight"
                            },
                            xAxis: {
                                categories: ["0.02", "0.04", "0.06", "0.08", "0.1", "0.12", "0.14", "0.16", "0.18", "0.2", "0.22", "0.24", "0.26", "0.28", "0.3", "0.32", "0.34", "0.36", "0.38", "0.4", "0.42", "0.44", "0.46", "0.48", "0.5", "0.52", "0.54", "0.56", "0.58", "0.6", "0.62", "0.64", "0.66", "0.68", "0.7", "0.72", "0.74", "0.76", "0.78", "0.8", "0.82", "0.84", "0.86", "0.88", "0.9", "0.92", "0.94", "0.96", "0.98", "1"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: 'Verdana, sans-serif'
                                    }
                                },
                                showLastLabel: true
                            },
                            yAxis: {
                                min: 0,
                                title: {
                                    text: 'Frequency'
                                }
                            },
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                    '<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    stacking: 'normal',
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "data": [21117, 7128, 5252, 4491, 4067, 3499, 3019, 2696, 2578, 2420, 2330, 2318, 2321, 2232, 2194, 2127, 2061, 2120, 2041, 2084, 2099, 1985, 1972, 1998, 1977, 1857, 1871, 1823, 1866, 1704, 1828, 1856, 1792, 1794, 1755, 1842, 1835, 1843, 1778, 1669, 1802, 1791, 1741, 1779, 1726, 1690, 1819, 1833, 1788, 1834],
                                "name": "Mixed Model framework, linear mixed-effects model, equation withoutWeight"
                            }]
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_MMlmeChart.series.length; i++) {
                                if (chart_MMlmeChart.series[i].visible == false) {
                                    chart_MMlmeChart.series[i].show();
                                }
                            }
                        });
                        $('#xxx').click(function () {
                            for (i = 0; i < chart_MMlmeChart.series.length; i++) {
                                if (chart_MMlmeChart.series[i].visible == true) {
                                    chart_MMlmeChart.series[i].hide();
                                }
                            }
                        });
                    });
                </script>
            </div>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h3>Trends</h3>

        <div id="trendsChart">
            <script type="text/javascript">
                $(function () {
                    var chart_trendsChart;
                    $(document).ready(function () {
                        chart_trendsChart = new Highcharts.Chart({
                            chart: {
                                zoomType: 'xy',
                                renderTo: 'trendsChart'
                            },
                            title: {
                                text: 'Genes/Mutant Lines/MP Calls'
                            },
                            subtitle: {
                                text: 'Release by Release'
                            },
                            xAxis: [{
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3"],
                            }],
                            yAxis: [{ // Primary yAxis
                                labels: {
                                    format: '{value}',
                                    style: {
                                        color: Highcharts.getOptions().colors[1]
                                    }
                                },
                                title: {
                                    text: 'Genes/Mutant Lines',
                                    style: {
                                        color: Highcharts.getOptions().colors[1]
                                    }
                                }
                            },
                                { // Secondary yAxis
                                    title: {
                                        text: 'Phenotype Calls',
                                        style: {
                                            color: Highcharts.getOptions().colors[0]
                                        }
                                    },
                                    labels: {
                                        format: '{value}',
                                        style: {
                                            color: Highcharts.getOptions().colors[0]
                                        }
                                    },
                                    opposite: true
                                }
                            ],
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                shared: true
                            },
                            series: [{
                                "data": [294, 470, 518, 1458, 1468, 1468, 1466, 1466, 2218, 2432, 2432],
                                "name": "Phenotyped genes",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " genes"
                                },
                                "type": "column"
                            }, {
                                "data": [301, 484, 535, 1528, 1540, 1540, 1537, 1537, 2340, 2577, 2562],
                                "name": "Phenotyped lines",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " lines"
                                },
                                "type": "column"
                            }, {
                                "yAxis": 1,
                                "data": [1069, 2732, 2182, 6114, 5974, 6064, 6278, 6382, 12159, 13008, 21667],
                                "name": "MP calls",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " calls"
                                },
                                "type": "spline"
                            }]
                        });
                    });
                });
            </script>
        </div>

        <div id="datapointsTrendsChart">
            <script type="text/javascript">
                $(function () {
                    var chart_datapointsTrendsChart;
                    $(document).ready(function () {
                        chart_datapointsTrendsChart = new Highcharts.Chart({
                            chart: {
                                zoomType: 'xy',
                                renderTo: 'datapointsTrendsChart'
                            },
                            title: {
                                text: 'Data points'
                            },
                            subtitle: {
                                text: ''
                            },
                            xAxis: [{
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3"],
                            }],
                            yAxis: [{ // Primary yAxis
                                labels: {
                                    format: '{value}',
                                    style: {
                                        color: Highcharts.getOptions().colors[1]
                                    }
                                },
                                title: {
                                    text: 'Data points',
                                    style: {
                                        color: Highcharts.getOptions().colors[1]
                                    }
                                }
                            },
                            ],
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                shared: true
                            },
                            series: [{
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 756, 756, 756],
                                "name": "Categorical (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068, 3956543, 3956543, 5827451, 6431595, 6597532],
                                "name": "Categorical (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [388, 1214, 1764, 3426, 3426, 3426, 3426, 3426, 4544, 4776, 4764],
                                "name": "Categorical (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 5623, 10765, 106682, 107059, 107059, 107044, 107044, 186464, 208924, 211801],
                                "name": "Image record (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 1721, 18591, 18591, 18591, 18591, 18591, 13970, 14643, 14625],
                                "name": "Text (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [2387, 12803, 15355, 51283, 51611, 51611, 51597, 51597, 81962, 89172, 89429],
                                "name": "Text (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 100, 161, 161, 161, 161, 161, 15690, 17178, 17159],
                                "name": "Text (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 13176, 4, 9, 9, 9, 9, 9, 116, 128, 128],
                                "name": "Time series (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471, 7383933, 7383933, 8721563, 9138387, 9077104],
                                "name": "Time series (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [83, 83, 39, 82, 77684, 77684, 77684, 77684, 81063, 80413, 80403],
                                "name": "Time series (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [172, 1443, 992, 3724, 3726, 3726, 3726, 3726, 7342, 7553, 7503],
                                "name": "Unidimensional (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851, 2981261, 2981261, 4399848, 4797248, 4543845],
                                "name": "Unidimensional (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [1454, 5286, 16928, 38737, 39065, 39065, 39065, 39065, 105213, 113998, 113801],
                                "name": "Unidimensional (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }]
                        });
                    });
                    $('#checkAllDataPoints').click(function () {
                        for (i = 0; i < chart_datapointsTrendsChart.series.length; i++) {
                            if (chart_datapointsTrendsChart.series[i].visible == false) {
                                chart_datapointsTrendsChart.series[i].show();
                            }
                        }
                    });
                    $('#uncheckAllDataPoints').click(function () {
                        for (i = 0; i < chart_datapointsTrendsChart.series.length; i++) {
                            if (chart_datapointsTrendsChart.series[i].visible == true) {
                                chart_datapointsTrendsChart.series[i].hide();
                            }
                        }
                    });
                });
            </script>
        </div>
        <a id="checkAllDataPoints" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllDataPoints"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>

        <div id="topLevelTrendsChart">
            <script type="text/javascript">
                $(function () {
                    var chart_topLevelTrendsChart;
                    $(document).ready(function () {
                        chart_topLevelTrendsChart = new Highcharts.Chart({
                            chart: {
                                zoomType: 'xy',
                                renderTo: 'topLevelTrendsChart'
                            },
                            title: {
                                text: 'Top Level Phenotypes'
                            },
                            subtitle: {
                                text: ''
                            },
                            xAxis: [{
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3"],
                            }],
                            yAxis: [{ // Primary yAxis
                                labels: {
                                    format: '{value}',
                                    style: {
                                        color: Highcharts.getOptions().colors[1]
                                    }
                                },
                                title: {
                                    text: 'MP Calls',
                                    style: {
                                        color: Highcharts.getOptions().colors[1]
                                    }
                                }
                            },
                            ],
                            credits: {
                                enabled: false
                            },
                            tooltip: {
                                shared: true
                            },
                            series: [{
                                "data": [73, 116, 93, 241, 243, 243, 243, 243, 452, 480, 478],
                                "name": "adipose tissue phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [74, 377, 394, 636, 624, 716, 716, 716, 2254, 1916, 2110],
                                "name": "behavior/neurological phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [12, 26, 44, 296, 261, 261, 261, 261, 379, 366, 445],
                                "name": "cardiovascular system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 4, 24, 56, 55, 55, 55, 55, 77, 87, 142],
                                "name": "craniofacial phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 2, 5, 24],
                                "name": "digestive/alimentary phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 33, 40, 411],
                                "name": "embryo phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 95],
                                "name": "endocrine/exocrine gland phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [289, 473, 561, 533, 536, 536, 536, 536, 982, 1053, 836],
                                "name": "growth/size/body region phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [84, 125, 237, 245, 245, 245, 245, 245, 331, 349, 390],
                                "name": "hearing/vestibular/ear phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [131, 366, 495, 704, 690, 692, 694, 694, 775, 792, 995],
                                "name": "hematopoietic system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [342, 1046, 1370, 1368, 1389, 1385, 1383, 1383, 1576, 1703, 1753],
                                "name": "homeostasis/metabolism phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [18, 69, 81, 183, 169, 172, 174, 174, 214, 214, 446],
                                "name": "immune system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 19, 55, 79, 77, 77, 77, 77, 145, 189, 217],
                                "name": "integument phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 26, 132, 204, 205, 205, 205, 205, 358, 426, 582],
                                "name": "limbs/digits/tail phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 16],
                                "name": "liver/biliary system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 80, 858, 906, 906, 1122, 1346, 1670, 1880, 2416],
                                "name": "mortality/aging",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 5, 7, 7, 7, 7, 9, 9, 9],
                                "name": "muscle phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 42, 79, 73, 66, 66, 66, 66, 178, 183, 377],
                                "name": "nervous system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4],
                                "name": "normal phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [10, 22, 51, 88, 80, 80, 80, 80, 143, 170, 165],
                                "name": "pigmentation phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 8, 27, 27, 27, 27, 27, 26, 25, 57],
                                "name": "renal/urinary system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 3, 43, 43, 43, 43, 86, 126, 152, 149],
                                "name": "reproductive system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 1, 1, 1, 1, 1, 2, 3, 17],
                                "name": "respiratory system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [101, 162, 223, 714, 718, 718, 718, 718, 1352, 1455, 1473],
                                "name": "skeleton phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [70, 79, 127, 521, 316, 316, 316, 316, 592, 611, 663],
                                "name": "vision/eye phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }]
                        });
                    });
                    $('#checkAllTopLevels').click(function () {
                        for (i = 0; i < chart_topLevelTrendsChart.series.length; i++) {
                            if (chart_topLevelTrendsChart.series[i].visible == false) {
                                chart_topLevelTrendsChart.series[i].show();
                            }
                        }
                    });
                    $('#uncheckAllTopLevels').click(function () {
                        for (i = 0; i < chart_topLevelTrendsChart.series.length; i++) {
                            if (chart_topLevelTrendsChart.series[i].visible == true) {
                                chart_topLevelTrendsChart.series[i].hide();
                            }
                        }
                    });
                });
            </script>
        </div>
        <a id="checkAllTopLevels" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllTopLevels"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>
    </div>
</div>
