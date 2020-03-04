<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 9.1</li>
                <li>Published:&nbsp;7 November 2018</li>
            </ul>
        </div>

        <div>
            <h4>Statistical Package</h4>
            <ul class="mt-0">
                <li>PhenStat</li>
                <li>Version:&nbsp;2.14.1</li>
            </ul>
        </div>

        <div>
            <h4>Genome Assembly</h4>
            <ul class="mt-0">
                <li>Mus musculus</li>
                <li>Version:&nbspGRCm38</li>
            </ul>
        </div>
    </div>

    <div class="col-6">
        <div>
            <h4>Summary</h4>
            <ul class="mt-0">
                <li>Number of phenotyped genes:&nbsp;5,570</li>
                <li>Number of phenotyped mutant lines:&nbsp;5,957</li>
                <li>Number of phenotype calls:&nbsp;57,797</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-9.1">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-9.1</a>
                </li>
                <li>RESTful interfaces:&nbsp;<a href="/data/documentation/data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h2 id="new-features">Highlights</h2>
        <div class="col-12">
            <h4>Data release 9.1</h4>

            <div class="well">
                <strong>Data release 9.1 represents a major data release. Changes include:</strong>

                <ul>
                    <li>Around 500 additional genes</li>
                    <li>More 3I data imported</li>
                    <li>Exclusion of embryo parameters that are marked as do not annotate in IMPReSS
                        (specifically, 'Responsive to tactile stimuli' and 'Spontaneous movement')
                    </li>
                    <li>Removal of spurious X-Rray ontology associations where the data were marked
                        as unobservable
                    </li>
                </ul>
                <br/>
                <p><strong>Note:</strong> Data release 9.0 has been intentionally and permanantly removed due to data
                    irregularities</p>
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
                    <td>1,148</td>
                    <td>7,958</td>
                    <td>33,234</td>
                </tr>
                <tr>
                    <td>TCP</td>
                    <td>529</td>
                    <td>6,821</td>
                    <td>23,766</td>
                </tr>
                <tr>
                    <td>HMGU</td>
                    <td>289</td>
                    <td>2,917</td>
                    <td>5,273</td>
                </tr>
                <tr>
                    <td>KMPC</td>
                    <td>37</td>
                    <td>739</td>
                    <td>827</td>
                </tr>
                <tr>
                    <td>MARC</td>
                    <td>232</td>
                    <td>1,877</td>
                    <td>3,776</td>
                </tr>
                <tr>
                    <td>MRC Harwell</td>
                    <td>562</td>
                    <td>5,704</td>
                    <td>14,734</td>
                </tr>
                <tr>
                    <td>RBRC</td>
                    <td>92</td>
                    <td>1,967</td>
                    <td>1,803</td>
                </tr>
                <tr>
                    <td>ICS</td>
                    <td>211</td>
                    <td>2,201</td>
                    <td>3,375</td>
                </tr>
                <tr>
                    <td>WTSI</td>
                    <td>1,328</td>
                    <td>4,563</td>
                    <td>21,297</td>
                </tr>
                <tr>
                    <td>BCM</td>
                    <td>521</td>
                    <td>4,219</td>
                    <td>15,699</td>
                </tr>
                <tr>
                    <td>UC Davis</td>
                    <td>1,008</td>
                    <td>3,762</td>
                    <td>23,220</td>
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
                        13,312,831
                    </td>
                    <td>
                        3,734
                        <sup>*</sup>
                    </td>
                    <td>
                        17,736
                        <sup>*</sup>
                    </td>
                </tr>
                <tr>
                    <td>unidimensional</td>
                    <td>
                        10,063,922
                    </td>
                    <td>
                        36,892
                        <sup>*</sup>
                    <td>
                        282,348
                        <sup>*</sup>
                    </td>
                </tr>
                <tr>
                    <td>time series</td>
                    <td>
                        12,236,102
                    </td>
                    <td>
                        2,570
                        <sup>*</sup>
                    </td>
                    <td>
                        11
                        <sup>*</sup>
                    </td>
                </tr>

                <tr>
                    <td>text</td>
                    <td>
                        180,098
                    </td>
                    <td>
                        11,124
                        <sup>*</sup>
                    </td>
                    <td>
                        45,653
                        <sup>*</sup>
                    </td>
                </tr>
                <tr>
                    <td>image record</td>
                    <td>
                        396,400
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
                            categories: ["adipose tissue phenotype", "behavior\/neurological phenotype", "cardiovascular system phenotype", "craniofacial phenotype", "digestive\/alimentary phenotype", "embryo phenotype", "endocrine\/exocrine gland phenotype", "growth\/size\/body region phenotype", "hearing\/vestibular\/ear phenotype", "hematopoietic system phenotype", "homeostasis\/metabolism phenotype", "immune system phenotype", "integument phenotype", "limbs\/digits\/tail phenotype", "liver\/biliary system phenotype", "mortality\/aging", "muscle phenotype", "nervous system phenotype", "normal phenotype", "pigmentation phenotype", "renal\/urinary system phenotype", "reproductive system phenotype", "respiratory system phenotype", "skeleton phenotype", "vision\/eye phenotype"],
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
                                text: 'Number of genotype-phenotype associations'
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
                            "data": [251, 3667, 1236, 1703, 52, 8, 24, 2051, 1487, 2308, 2876, 1402, 5288, 3967, 1, 55, 41, 424, 4967, 1348, 46, 417, 4, 4493, 3303],
                            "name": "hemizygote"
                        }, {
                            "data": [3347, 66237, 32777, 30777, 1297, 21691, 1099, 43382, 20822, 33144, 45515, 19169, 86987, 68127, 2183, 419, 1031, 16587, 153981, 20759, 1101, 7686, 500, 61827, 66397],
                            "name": "heterozygote"
                        }, {
                            "data": [6434, 114329, 46474, 50455, 1943, 10307, 1782, 66337, 42885, 65892, 79454, 40586, 151634, 117877, 1322, 9220, 1662, 19012, 335343, 37356, 2028, 20456, 967, 120245, 107629],
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
                                    colors: ['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'],
                                    chart: {type: 'column'},
                                    title: {text: 'Genotyping Status'},
                                    credits: {enabled: false},
                                    xAxis: {
                                        type: 'category',
                                        labels: {
                                            rotation: -45,
                                            style: {fontSize: '11px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    },
                                    yAxis: {min: 0, title: {text: 'Number of genes'}},
                                    legend: {enabled: false},
                                    tooltip: {pointFormat: '<b>{point.y}</b>'},
                                    series: [{
                                        name: 'Population',
                                        data: [['Micro-injection in progress', 14], ['Chimeras obtained', 18], ['Genotype confirmed', 4223], ['Cre Excision Complete', 3477],],
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
                                    colors: ['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'],
                                    chart: {type: 'column'},
                                    title: {text: 'Phenotyping Status'},
                                    credits: {enabled: false},
                                    xAxis: {
                                        type: 'category',
                                        labels: {
                                            rotation: -45,
                                            style: {fontSize: '11px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    },
                                    yAxis: {min: 0, title: {text: 'Number of genes'}},
                                    legend: {enabled: false},
                                    tooltip: {pointFormat: '<b>{point.y}</b>'},
                                    series: [{
                                        name: 'Population',
                                        data: [['Phenotype Attempt Registered', 922], ['Phenotyping Started', 187], ['Phenotyping Complete', 5860],],
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
                                categories: ["Micro-injection in progress", "Chimeras obtained", "Genotype confirmed", "Cre Excision Complete"],
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
                            series: [{"data": [0, 0, 715, 705], "name": "JAX"}, {
                                "data": [0, 0, 247, 277],
                                "name": "TCP"
                            }, {"data": [4, 0, 31, 66], "name": "Monterotondo"}, {
                                "data": [0, 0, 3, 0],
                                "name": "VETMEDUNI"
                            }, {"data": [0, 6, 249, 431], "name": "Harwell"}, {
                                "data": [0, 0, 1, 2],
                                "name": "Monterotondo R&D"
                            }, {"data": [0, 0, 15, 11], "name": "SEAT"}, {
                                "data": [0, 0, 202, 74],
                                "name": "MARC"
                            }, {"data": [0, 0, 1, 0], "name": "Oulu"}, {
                                "data": [0, 0, 275, 480],
                                "name": "BCM"
                            }, {"data": [0, 0, 0, 30], "name": "CDTA"}, {
                                "data": [0, 1, 60, 311],
                                "name": "HMGU"
                            }, {"data": [2, 5, 86, 37], "name": "CAM-SU GRC"}, {
                                "data": [5, 0, 83, 24],
                                "name": "CCP-IMG"
                            }, {"data": [0, 0, 13, 17], "name": "CIPHE"}, {
                                "data": [0, 0, 1, 0],
                                "name": "CNB"
                            }, {"data": [0, 5, 29, 12], "name": "KMPC"}, {
                                "data": [1, 0, 760, 741],
                                "name": "UCD"
                            }, {"data": [0, 1, 153, 146], "name": "ICS"}, {
                                "data": [1, 0, 1326, 453],
                                "name": "WTSI"
                            }, {"data": [1, 0, 15, 4], "name": "NARLabs"}, {
                                "data": [0, 0, 70, 33],
                                "name": "RIKEN BRC"
                            }, {"data": [0, 0, 0, 0], "name": "Monash"}]
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
                            series: [{"data": [239, 46, 1137], "name": "JAX"}, {
                                "data": [45, 14, 653],
                                "name": "TCP"
                            }, {"data": [3, 0, 9], "name": "Monterotondo"}, {
                                "data": [31, 24, 677],
                                "name": "Harwell"
                            }, {"data": [0, 0, 2], "name": "SEAT"}, {
                                "data": [5, 12, 199],
                                "name": "MARC"
                            }, {"data": [82, 3, 509], "name": "BCM"}, {
                                "data": [0, 0, 2],
                                "name": "CDTA"
                            }, {"data": [35, 24, 385], "name": "HMGU"}, {
                                "data": [54, 0, 8],
                                "name": "CCP-IMG"
                            }, {"data": [0, 0, 4], "name": "CIPHE"}, {
                                "data": [0, 5, 38],
                                "name": "KMPC"
                            }, {"data": [87, 44, 1015], "name": "UCD"}, {
                                "data": [12, 6, 330],
                                "name": "ICS"
                            }, {"data": [328, 10, 1413], "name": "WTSI"}, {"data": [6, 2, 92], "name": "RIKEN BRC"}]
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
var chart_callProcedureChart;	Highcharts.setOptions({	    colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});   chart_callProcedureChart = new Highcharts.Chart({         chart: {
type: 'column',
height: 800,
renderTo:callProcedureChart        },
title: {
text: 'Phenotype calls per procedure'
},
subtitle: {
text: "Center by center"
},
xAxis: {
categories: ["Combined SHIRPA and Dysmorphology","Open Field","Clinical Chemistry","Body Composition (DEXA lean\/fat)","Electrocardiogram (ECG)","Echo","Eye Morphology","Fertility of Homozygous Knock-out Mice","Grip Strength","Hematology","Viability Primary Screen","X-ray","Auditory Brain Stem Response","Acoustic Startle and Pre-pulse Inhibition (PPI)","Indirect Calorimetry","Heart Weight","Insulin Blood Level","Intraperitoneal glucose tolerance test (IPGTT)","Rotarod","Electroconvulsive Threshold Testing","Hole-board Exploration","Light-Dark Test","Plasma Chemistry","Sleep Wake","Tail Suspension","Urinalysis","Organ Weight","Challenge Whole Body Plethysmography","Trabecular Bone MicroCT"],
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
series:[{"data":[170,873,434,987,187,0,148,44,241,295,722,0,64,136,0,39,33,141,5,25,32,367,73,248,20,14,0,0,0],"name":"JAX"},{"data":[97,340,193,361,31,0,17,17,64,136,320,137,171,99,13,5,0,36,0,0,0,10,0,0,0,0,0,1,0],"name":"TCP"},{"data":[75,231,203,99,42,56,511,10,73,101,132,1,21,120,31,10,1,42,0,0,0,0,0,0,0,0,0,0,0],"name":"HMGU"},{"data":[15,25,13,21,0,0,8,0,18,10,0,22,25,18,10,2,0,1,0,0,0,0,0,0,0,0,0,0,0],"name":"KMPC"},{"data":[49,131,115,165,0,75,6,33,34,299,78,0,34,19,0,9,0,15,0,0,0,0,0,0,0,0,0,0,0],"name":"MARC"},{"data":[304,481,453,362,35,26,68,12,71,500,378,136,33,127,3,0,0,116,0,0,0,0,0,0,0,0,72,0,0],"name":"MRC Harwell"},{"data":[28,22,25,50,56,0,0,7,2,58,54,18,27,14,7,2,0,6,0,0,0,0,0,0,0,0,0,0,0],"name":"RBRC"},{"data":[75,134,160,86,13,15,173,9,71,127,166,35,55,42,7,34,0,19,27,0,0,0,0,0,0,0,0,0,0],"name":"ICS"},{"data":[256,0,976,915,0,0,81,53,167,457,790,201,288,0,29,12,52,78,0,0,0,0,0,0,0,0,0,0,2],"name":"WTSI"},{"data":[6,77,55,22,2,24,30,20,7,15,356,37,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"BCM"},{"data":[151,560,323,483,53,0,14,75,108,407,374,43,113,92,3,20,80,78,0,0,0,0,0,0,0,0,0,0,0],"name":"UC Davis"}]
});
$('#checkAllPhenCalls').click(function(){  for(i=0; i < chart_callProcedureChart.series.length; i++) { if(chart_callProcedureChart.series[i].visible == false){  chart_callProcedureChart.series[i].show(); }}}); $('#uncheckAllPhenCalls').click(function(){  for(i=0; i < chart_callProcedureChart.series.length; i++) {  if(chart_callProcedureChart.series[i].visible == true){  chart_callProcedureChart.series[i].hide(); }}}); });

            </script>
        </div>

        <a id="checkAllPhenCalls" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllPhenCalls"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>

    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h3>Trends</h3>

        <div id="trendsChart">
            <script type="text/javascript">
                $(function () { var chart_trendsChart;
$(document).ready(function() { chart_trendsChart = new Highcharts.Chart({
chart: {
zoomType: 'xy',
renderTo: 'trendsChart'            },
title: {
text: 'Genes/Mutant Lines/MP Calls'
},
subtitle: {
text: 'Release by Release'
},
xAxis: [{
categories: ["1.0","1.1","2.0","3.0","3.1","3.2","3.3","3.4","4.0","4.2","4.3","5.0","6.0","6.1","7.0","8.0","9.1"],
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
series:[{"data":[294,470,518,1458,1468,1468,1466,1466,2218,2432,2432,3328,4364,4364,4820,5115,5570],"name":"Phenotyped genes","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" genes"},"type":"column"},{"data":[301,484,535,1528,1540,1540,1537,1537,2340,2577,2562,3532,4745,4745,5186,5505,5957],"name":"Phenotyped lines","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" lines"},"type":"column"},{"yAxis":1,"data":[1069,2732,2182,6114,5974,6064,6278,6382,12159,13008,21667,28406,52076,52656,60670,59407,57797],"name":"MP calls","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" calls"},"type":"spline"}]
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
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3", "5.0", "6.0", "6.1", "7.0", "8.0", "9.1"],
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
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 756, 756, 756, 930, 3389, 3389, 3221, 3221, 3734],
                                "name": "Categorical (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068, 3956543, 3956543, 5827451, 6431595, 6597532, 8107737, 11074178, 11129868, 12225255, 13047108, 13312831],
                                "name": "Categorical (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [388, 1214, 1764, 3426, 3426, 3426, 3426, 3426, 4544, 4776, 4764, 10766, 14775, 14775, 16529, 15810, 17736],
                                "name": "Categorical (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 5623, 10765, 106682, 107059, 107059, 107044, 107044, 186464, 208924, 211801, 270804, 327658, 315223, 358288, 366858, 396400],
                                "name": "Image record (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 1721, 18591, 18591, 18591, 18591, 18591, 13970, 14643, 14625, 14784, 11431, 11431, 11369, 11176, 11124],
                                "name": "Text (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [2387, 12803, 15355, 51283, 51611, 51611, 51597, 51597, 81962, 89172, 89429, 111319, 143003, 143004, 154731, 160354, 180098],
                                "name": "Text (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 100, 161, 161, 161, 161, 161, 15690, 17178, 17159, 19826, 34141, 34141, 39733, 48130, 45653],
                                "name": "Text (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 13176, 4, 9, 9, 9, 9, 9, 116, 128, 128, 190, 2393, 2393, 2393, 2471, 2570],
                                "name": "Time series (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471, 7383933, 7383933, 8721563, 9138387, 9077104, 8772128, 12001974, 12002305, 12778993, 12844228, 12236102],
                                "name": "Time series (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [83, 83, 39, 82, 77684, 77684, 77684, 77684, 81063, 80413, 80403, 84718, 0, 0, 11, 11, 11],
                                "name": "Time series (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [172, 1443, 992, 3724, 3726, 3726, 3726, 3726, 7342, 7553, 7503, 8081, 24321, 24321, 28370, 29870, 36892],
                                "name": "Unidimensional (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851, 2981261, 2981261, 4399848, 4797248, 4543845, 5938585, 8151407, 8150815, 9099910, 9463677, 10063922],
                                "name": "Unidimensional (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [1454, 5286, 16928, 38737, 39065, 39065, 39065, 39065, 105213, 113998, 113801, 154942, 188283, 188283, 203522, 209608, 282348],
                                "name": "Unidimensional (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
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
        <a id="uncheckAllDataPoints" class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>
    </div>
</div>
