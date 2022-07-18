<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="row">
    <div class="col-md-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release:&nbsp;16.0</li>
                <li>Published:&nbsp;21 April 2022</li>
            </ul>
        </div>

        <div>
            <h4>Statistical Package</h4>
            <ul class="mt-0">
                <li>OpenStats</li>
                <li>Version:&nbsp
                    <a href="https://www.mousephenotype.org/help/data-analysis/statistical-analysis/">
                        1.6.0
                    </a>
                </li>
            </ul>
        </div>
        <div>
            <h4>Genome Assembly</h4>
            <ul class="mt-0">
                <li>Mus musculus</li>
                <li>Version:&nbsp
                    <a href="http://www.informatics.jax.org">
                        GRCm38
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <div class="col-md-6">
        <div>
            <h4>Summary</h4>
            <ul class="mt-0">
                <li>Number of phenotyped genes:&nbsp;8,093</li>
                <li>Number of phenotyped mutant lines:&nbsp;8,741</li>
                <li>Number of phenotype calls:&nbsp;93,235</li>
            </ul>
        </div>
        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp interface:&nbsp;<a href="https://www.mousephenotype.org/help/non-programmatic-data-access">ftp</a></li>
                <li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div> <!-- end row -->

<div class="row mt-5">
    <div class="col-12">
        <h2 id="new-features" class="pb-3">Highlights</h2>
        <div class="col-12">
            <h4>Data release 16.0</h4>

            <div class="well">
                <strong>Release notes:</strong>
                <p>Represents a major data release.<br />This release </p><ul><li>Includes approximately 3 million more data points</li><li>Data for genes significantly associated with pain sensitivity included in the gene report pages.</li><li>All mouse production data used in the release was provided by GenTaR</li></ul>
            </div>
        </div>
    </div>
</div> <!-- end row -->

<div class="row py-5">
    <div class="col-12">
        <h2 class="title pb-3" id="data_reports">Data Reports</h2>
        <div class="col-12">
            <h3>Total Number of Lines and Specimens in DR 16.0</h3>
            <table class="table table-striped">
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
                    <td>TCP</td>
                    <td>706</td>
                    <td>8,852</td>
                    <td>29,142</td>
                </tr>




                <tr>
                    <td>JAX</td>
                    <td>1,875</td>
                    <td>14,516</td>
                    <td>54,897</td>
                </tr>




                <tr>
                    <td>HMGU</td>
                    <td>464</td>
                    <td>5,365</td>
                    <td>8,971</td>
                </tr>




                <tr>
                    <td>CCP-IMG</td>
                    <td>234</td>
                    <td>2,515</td>
                    <td>4,345</td>
                </tr>




                <tr>
                    <td>MRC Harwell</td>
                    <td>796</td>
                    <td>7,303</td>
                    <td>20,002</td>
                </tr>




                <tr>
                    <td>MARC</td>
                    <td>233</td>
                    <td>1,858</td>
                    <td>3,588</td>
                </tr>




                <tr>
                    <td>KMPC</td>
                    <td>70</td>
                    <td>2,073</td>
                    <td>2,558</td>
                </tr>




                <tr>
                    <td>RBRC</td>
                    <td>108</td>
                    <td>2,149</td>
                    <td>2,448</td>
                </tr>




                <tr>
                    <td>ICS</td>
                    <td>239</td>
                    <td>2,619</td>
                    <td>3,882</td>
                </tr>




                <tr>
                    <td>WTSI</td>
                    <td>1,614</td>
                    <td>5,364</td>
                    <td>18,660</td>
                </tr>




                <tr>
                    <td>UC Davis</td>
                    <td>1,475</td>
                    <td>8,665</td>
                    <td>36,618</td>
                </tr>




                <tr>
                    <td>BCM</td>
                    <td>928</td>
                    <td>6,865</td>
                    <td>25,576</td>
                </tr>

                </tbody>
            </table>

            <h3>Experimental Data and Quality Checks</h3>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th class="headerSort">Data Type</th>

                    <th class="headerSort">QC Passed Data Points</th>

                </tr>
                </thead>
                <tbody>

                <tr>
                    <td>categorical</td>



                    <td>


                        23,074,548




                    </td>

                </tr>

                <tr>
                    <td>image record</td>



                    <td>


                        738,538




                    </td>

                </tr>

                <tr>
                    <td>time series</td>



                    <td>


                        23,855,539




                    </td>

                </tr>

                <tr>
                    <td>ontological</td>



                    <td>


                        1,726,712




                    </td>

                </tr>

                <tr>
                    <td>text</td>



                    <td>


                        521,859




                    </td>

                </tr>

                <tr>
                    <td>unidimensional</td>



                    <td>


                        19,173,924




                    </td>

                </tr>

                </tbody>
            </table>
            <p><sup>*</sup>&nbsp;Excluded from statistical analysis.</p>
        </div>
    </div>
</div> <!-- end row -->

<div class="row mt-5">
    <div class="col-12">
        <h2>Distribution of Phenotype Annotations</h2>
        <div id="distribution">
            <script type="text/javascript">
                $(function () {
                    var chart_distribution;	Highcharts.setOptions({	    colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});   chart_distribution = new Highcharts.Chart({         chart: {
                            type: 'column',
                            height: 800,
                            renderTo:distribution        },
                        title: {
                            text: 'Distribution of Phenotype Associations by Top-level MP Term'
                        },
                        subtitle: {
                            text: ""
                        },
                        xAxis: {
                            categories: ["adipose tissue phenotype","behavior\/neurological phenotype","cardiovascular system phenotype","craniofacial phenotype","digestive\/alimentary phenotype","embryo phenotype","endocrine\/exocrine gland phenotype","growth\/size\/body region phenotype","hearing\/vestibular\/ear phenotype","hematopoietic system phenotype","homeostasis\/metabolism phenotype","immune system phenotype","integument phenotype","limbs\/digits\/tail phenotype","liver\/biliary system phenotype","mortality\/aging","muscle phenotype","nervous system phenotype","pigmentation phenotype","renal\/urinary system phenotype","reproductive system phenotype","respiratory system phenotype","skeleton phenotype","vision\/eye phenotype"],
                            labels: {
                                rotation: -90,
                                align: 'right',
                                style: {
                                    fontSize: '11px',
                                    fontFamily: '"Roboto", sans-serif'
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
                        series:[{"data":[2,108,35,24,9,2,40,80,2,86,89,68,47,4,4,4,2,6,7,30,35,7,47,48],"name":"hemizygote"},{"data":[190,2185,935,89,74,425,487,1325,76,1836,1608,1471,485,123,173,5,9,377,136,392,461,48,512,1101],"name":"heterozygote"},{"data":[474,7280,2457,778,299,1607,1676,4304,592,6056,4596,4776,1379,806,426,4701,54,1512,326,1064,1870,222,2186,2563],"name":"homozygote"}]
                    });
                });

            </script>
        </div>
    </div>
</div> <!-- end row -->

<div class="row mt-5">
    <div class="col-12">
        <h2 class="title" id="section-associations">Production Status </h2>
        <div class="row">
            <div class="col-12">
                <h4>Overall</h4>
                <div class="row">
                    <div class="col-md-6">
                        <div id="genotypeStatusChart">
                            <script type="text/javascript">$(function () { $('#genotypeStatusChart').highcharts({ colors:['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'], chart: {type: 'column' }, title: {text: 'Genotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -45, style: {fontSize: '11px', fontFamily: '"Roboto", sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Genotype confirmed', 5844], ['Cre Excision Complete', 3537], ['Chimeras obtained', 38], ['Micro-injection in progress', 27], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: '"Roboto", sans-serif' } } }] }); });</script>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div id="phenotypeStatusChart">
                            <script type="text/javascript">$(function () { $('#phenotypeStatusChart').highcharts({ colors:['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'], chart: {type: 'column' }, title: {text: 'Phenotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -45, style: {fontSize: '11px', fontFamily: '"Roboto", sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Phenotype attempt registered', null], ['Phenotyping started', 1], ['Phenotyping data available', 8413], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: '"Roboto", sans-serif' } } }] }); });</script>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="col-12">
        <h4>By Center</h4>
        <div class="row">
            <div class="col-md-6">
                <div id="genotypeStatusByCenterChart">
                    <script type="text/javascript">$(function () {
                        var chart_genotypeStatusByCenterChart;	Highcharts.setOptions({	    colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});   chart_genotypeStatusByCenterChart = new Highcharts.Chart({         chart: {
                                type: 'column',
                                height: 800,
                                renderTo:genotypeStatusByCenterChart        },
                            title: {
                                text: 'Genotyping Status by Center'
                            },
                            subtitle: {
                                text: ""
                            },
                            xAxis: {
                                categories: ["Micro-injection in progress","Chimeras obtained","Genotype confirmed","Cre Excision Complete"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: '"Roboto", sans-serif'
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
                            series:[{"data":[0,0,1171,706],"name":"JAX"},{"data":[0,0,341,277],"name":"TCP"},{"data":[0,0,29,67],"name":"Monterotondo"},{"data":[0,0,3,0],"name":"VETMEDUNI"},{"data":[9,24,436,450],"name":"Harwell"},{"data":[0,0,1,2],"name":"Monterotondo R&D"},{"data":[0,0,15,11],"name":"SEAT"},{"data":[0,0,201,75],"name":"MARC"},{"data":[0,0,1,0],"name":"Oulu"},{"data":[0,0,499,479],"name":"BCM"},{"data":[0,0,0,30],"name":"CDTA"},{"data":[1,3,162,331],"name":"HMGU"},{"data":[4,3,40,11],"name":"CAM-SU GRC"},{"data":[6,3,343,39],"name":"CCP-IMG"},{"data":[0,0,13,17],"name":"CIPHE"},{"data":[0,0,1,0],"name":"CNB"},{"data":[0,6,81,23],"name":"KMPC"},{"data":[4,0,1026,742],"name":"UCD"},{"data":[1,0,163,151],"name":"ICS"},{"data":[1,0,1362,460],"name":"WTSI"},{"data":[1,0,15,4],"name":"NARLabs"},{"data":[0,0,77,34],"name":"RIKEN BRC"},{"data":[0,0,0,0],"name":"Monash"}]
                        });

                        $('#checkAllGenByCenter').click(function(){  chart_genotypeStatusByCenterChart.showLoading(); for(i=0; i < chart_genotypeStatusByCenterChart.series.length; i++) { if(chart_genotypeStatusByCenterChart.series[i].visible == false){  chart_genotypeStatusByCenterChart.series[i].setVisible(true, false);
                        } chart_genotypeStatusByCenterChart.series[i].checkbox.checked = true;
                            chart_genotypeStatusByCenterChart.series[i].selected = true; breaks[i] = {};}
                            chart_genotypeStatusByCenterChart.hideLoading();  chart_genotypeStatusByCenterChart.redraw(); });
                        $('#uncheckAllGenByCenter').click(function(){  chart_genotypeStatusByCenterChart.showLoading(); for(i=0; i < chart_genotypeStatusByCenterChart.series.length; i++) {  if(chart_genotypeStatusByCenterChart.series[i].visible == true){  chart_genotypeStatusByCenterChart.series[i].setVisible(false, false);
                        }
                            chart_genotypeStatusByCenterChart.series[i].checkbox.checked = false;
                            chart_genotypeStatusByCenterChart.series[i].selected = false; breaks[i] = {};}
                            chart_genotypeStatusByCenterChart.xAxis[0].update({ breaks: breaks }); chart_genotypeStatusByCenterChart.hideLoading(); chart_genotypeStatusByCenterChart.redraw(); });
                    });
                    </script>
                </div>
                <a id="checkAllGenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                <a id="uncheckAllGenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                <div class="clear both"></div>
            </div>
            <div class="col-md-6">
                <div id="phenotypeStatusByCenterChart">
                    <script type="text/javascript">$(function () {
                        var chart_phenotypeStatusByCenterChart;	Highcharts.setOptions({	    colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});   chart_phenotypeStatusByCenterChart = new Highcharts.Chart({         chart: {
                                type: 'column',
                                height: 800,
                                renderTo:phenotypeStatusByCenterChart        },
                            title: {
                                text: 'Phenotyping Status by Center'
                            },
                            subtitle: {
                                text: ""
                            },
                            xAxis: {
                                categories: ["Phenotype Attempt Registered","Phenotyping Started","Phenotyping Complete"],
                                labels: {
                                    rotation: -90,
                                    align: 'right',
                                    style: {
                                        fontSize: '11px',
                                        fontFamily: '"Roboto", sans-serif'
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
                            series:[{"data":[46,519,1295],"name":"JAX"},{"data":[30,63,680],"name":"TCP"},{"data":[0,0,10],"name":"Monterotondo"},{"data":[8,71,800],"name":"Harwell"},{"data":[0,0,2],"name":"SEAT"},{"data":[10,2,201],"name":"MARC"},{"data":[54,147,659],"name":"BCM"},{"data":[0,0,2],"name":"CDTA"},{"data":[5,63,450],"name":"HMGU"},{"data":[9,91,97],"name":"CCP-IMG"},{"data":[0,0,4],"name":"CIPHE"},{"data":[0,11,50],"name":"KMPC"},{"data":[38,143,1246],"name":"UCD"},{"data":[0,0,360],"name":"ICS"},{"data":[5,2,1624],"name":"WTSI"},{"data":[6,2,102],"name":"RIKEN BRC"}]
                        });

                        $('#checkAllPhenByCenter').click(function(){  chart_phenotypeStatusByCenterChart.showLoading(); for(i=0; i < chart_phenotypeStatusByCenterChart.series.length; i++) { if(chart_phenotypeStatusByCenterChart.series[i].visible == false){  chart_phenotypeStatusByCenterChart.series[i].setVisible(true, false);
                        } chart_phenotypeStatusByCenterChart.series[i].checkbox.checked = true;
                            chart_phenotypeStatusByCenterChart.series[i].selected = true; breaks[i] = {};}
                            chart_phenotypeStatusByCenterChart.hideLoading();  chart_phenotypeStatusByCenterChart.redraw(); });
                        $('#uncheckAllPhenByCenter').click(function(){  chart_phenotypeStatusByCenterChart.showLoading(); for(i=0; i < chart_phenotypeStatusByCenterChart.series.length; i++) {  if(chart_phenotypeStatusByCenterChart.series[i].visible == true){  chart_phenotypeStatusByCenterChart.series[i].setVisible(false, false);
                        }
                            chart_phenotypeStatusByCenterChart.series[i].checkbox.checked = false;
                            chart_phenotypeStatusByCenterChart.series[i].selected = false; breaks[i] = {};}
                            chart_phenotypeStatusByCenterChart.xAxis[0].update({ breaks: breaks }); chart_phenotypeStatusByCenterChart.hideLoading(); chart_phenotypeStatusByCenterChart.redraw(); });
                    });
                    </script>
                </div>
                <a id="checkAllPhenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                <a id="uncheckAllPhenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                <div class="clear both"></div>
            </div>
        </div>
        <p>
            More charts and status information are available from our mouse tracking services
            <a href="https://www.mousephenotype.org/imits/">iMits</a>
            and
            <a href="https://www.gentar.org/">GenTaR</a>.
        </p>

    </div>
</div> <!-- end row -->

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
                            text: 'Number of Phenotype Calls by Procedure'
                        },
                        subtitle: {
                            text: "Further categorized by Embryo, Late Adult, and Early Adult"
                        },
                        xAxis: {
                            categories: ["Gross Morphology Embryo E9.5","Viability E9.5 Secondary Screen","Gross Morphology Embryo E12.5","Gross Morphology Placenta E12.5","Viability E12.5 Secondary Screen","Gross Morphology Embryo E14.5-E15.5","Gross Morphology Placenta E14.5-E15.5","Viability E14.5-E15.5 Secondary Screen","Gross Morphology Embryo E18.5","Gross Morphology Placenta E18.5","Viability E18.5 Secondary Screen","Acoustic Startle and Pre-pulse Inhibition (PPI)","Age monitoring","Auditory Brain Stem Response","Body Composition (DEXA lean\/fat)","Challenge Whole Body Plethysmography","Clinical Chemistry","Combined SHIRPA and Dysmorphology","Dark-Light Test","Dysmorphology","Echo","Electrocardiogram (ECG)","Electroconvulsive Threshold Testing","Eye Morphology","Fear Conditioning","Fertility of Homozygous Knock-out Mice","Grip Strength","Gross Pathology and Tissue Collection","Heart Weight","Hematology","Hole-board Exploration","Immunophenotyping","Indirect Calorimetry","Insulin Blood Level","Intraperitoneal glucose tolerance test (IPGTT)","Light-Dark Test","Lung mechanics by forced oscillations","Open Field","Open Field - centre start","Organ Weight","Plasma Chemistry","Rotarod","Sleep Wake","Tail Suspension","Trabecular Bone MicroCT","Urinalysis","Viability Primary Screen","X-ray","Y-maze"],
                            labels: {
                                rotation: -90,
                                align: 'right',
                                style: {
                                    fontSize: '11px',
                                    fontFamily: '"Roboto", sans-serif'
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
                        series:[{"data":[1579,531,447,41,315,1048,227,107,889,33,21,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"Embryo"},{"data":[0,0,0,0,0,0,0,0,0,0,0,9,0,0,139,0,155,193,0,1,11,28,0,302,6,0,65,2651,22,196,3,19,0,0,32,21,0,163,95,19,0,0,0,0,0,0,0,8,2],"name":"Late Adult"},{"data":[0,0,0,0,0,0,0,0,0,0,0,1250,2,637,2679,104,3757,2207,31,0,311,421,10,2243,183,399,1146,8841,299,3478,63,201,101,65,737,589,98,2525,1664,377,71,22,118,13,5,48,3736,909,1],"name":"Early Adult"}]
                    });

                    $('#checkAllPhenCalls').click(function(){  chart_callProcedureChart.showLoading(); for(i=0; i < chart_callProcedureChart.series.length; i++) { if(chart_callProcedureChart.series[i].visible == false){  chart_callProcedureChart.series[i].setVisible(true, false);
                    } chart_callProcedureChart.series[i].checkbox.checked = true;
                        chart_callProcedureChart.series[i].selected = true; breaks[i] = {};}
                        chart_callProcedureChart.hideLoading();  chart_callProcedureChart.redraw(); });
                    $('#uncheckAllPhenCalls').click(function(){  chart_callProcedureChart.showLoading(); for(i=0; i < chart_callProcedureChart.series.length; i++) {  if(chart_callProcedureChart.series[i].visible == true){  chart_callProcedureChart.series[i].setVisible(false, false);
                    }
                        chart_callProcedureChart.series[i].checkbox.checked = false;
                        chart_callProcedureChart.series[i].selected = false; breaks[i] = {};}
                        chart_callProcedureChart.xAxis[0].update({ breaks: breaks }); chart_callProcedureChart.hideLoading(); chart_callProcedureChart.redraw(); });
                });

            </script>
        </div>

        <a id="checkAllPhenCalls" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllPhenCalls"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>

    </div>
</div> <!-- end row -->

<div class="row mt-5">

    <div class="col-12">
        <h2>Trends</h2>

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
                            text: 'By Data Release'
                        },
                        xAxis: [{
                            categories: ["1.0","1.1","2.0","3.0","3.1","3.2","3.3","3.4","4.0","4.2","4.3","5.0","6.0","6.1","7.0","8.0","9.1","9.2","10.0","10.1","11.0","12.0","13.0","14.0","15.0","15.1","16.0"],
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
                        series:[{"data":[294,470,518,1458,1468,1468,1466,1466,2218,2432,2432,3328,4364,4364,4820,5115,5570,5614,5861,5861,6440,7022,7360,7590,7824,7824,8093],"type":"column","name":"Phenotyped genes","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" genes"}},{"data":[301,484,535,1528,1540,1540,1537,1537,2340,2577,2562,3532,4745,4745,5186,5505,5957,6006,6255,6255,6900,7606,7970,8221,8457,8457,8741],"type":"column","name":"Phenotyped lines","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" lines"}},{"data":[1069,2732,2182,6114,5974,6064,6278,6382,12159,13008,21667,28406,52461,52656,60670,59407,57797,58107,69982,70692,75844,98621,86536,89329,93706,90010,93235],"type":"spline","name":"MP calls","yAxis":1,"tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" calls"}}]
                    });
                    });
                });
            </script>
        </div>

        <div id="datapointsTrendsChart">
            <script type="text/javascript">
                $(function () { var chart_datapointsTrendsChart;
                    $(document).ready(function() { chart_datapointsTrendsChart = new Highcharts.Chart({
                        chart: {
                            zoomType: 'xy',
                            renderTo: 'datapointsTrendsChart'            },
                        title: {
                            text: 'Data Points by Data Release'
                        },
                        subtitle: {
                            text: ''
                        },
                        xAxis: [{
                            categories: ["1.0","1.1","2.0","3.0","3.1","3.2","3.3","3.4","4.0","4.2","4.3","5.0","6.0","6.1","7.0","8.0","9.1","9.2","10.0","10.1","11.0","12.0","13.0","14.0","15.0","15.1","16.0"],
                        }],
                        yAxis: [{ // Primary yAxis
                            labels: {
                                format: '{value}',
                                style: {
                                    color: Highcharts.getOptions().colors[1]
                                }
                            },
                            title: {
                                text: 'Data Points',
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
                        series:[{"data":[149194,958957,1173776,3935293,3956068,3956068,3956543,3956543,5827451,6431595,6597532,8107737,11129732,11129868,12225255,13047108,13312831,13312831,14917863,14917864,17290216,19587640,20780695,21255224,22185336,22185336,23074548],"type":"spline","name":"Categorical (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "}},{"data":[0,5623,10765,106682,107059,107059,107044,107044,186464,208924,211801,270804,327658,315223,358288,366858,396400,396400,469960,469960,519670,538628,572772,599052,623331,623331,738538],"type":"spline","name":"Image record (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "}},{"data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1142459,1240938,1340729,1535242,1535242,1726712],"name":"ontological_datapoints_QC_passed","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "}},{"data":[2387,12803,15355,51283,51611,51611,51597,51597,81962,89172,89429,111319,143003,143004,154731,160354,180098,180098,192176,192175,239514,383858,415798,449449,475896,475896,521859],"type":"spline","name":"Text (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "}},{"data":[63773,1451844,1798263,7237320,7415471,7415471,7383933,7383933,8721563,9138387,9077104,8772128,12001974,12002305,12778993,12844228,12236102,12236110,13444019,13444019,15043961,16917206,18293301,20057483,22148393,22148393,23855539],"type":"spline","name":"Time series (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "}},{"data":[381406,1011637,988022,2782486,2970851,2970851,2981261,2981261,4399848,4797248,4543845,5938585,8151407,8150815,9099910,9463677,10063922,10094468,11068097,11116529,12660671,14377881,15584538,16650398,18032421,18032421,19173924],"type":"spline","name":"Unidimensional (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "}}]
                    });
                    });

                    $('#checkAllDataPoints').click(function(){  chart_datapointsTrendsChart.showLoading(); for(i=0; i < chart_datapointsTrendsChart.series.length; i++) { if(chart_datapointsTrendsChart.series[i].visible == false){  chart_datapointsTrendsChart.series[i].setVisible(true, false);
                    } chart_datapointsTrendsChart.series[i].checkbox.checked = true;
                        chart_datapointsTrendsChart.series[i].selected = true; breaks[i] = {};}
                        chart_datapointsTrendsChart.hideLoading();  chart_datapointsTrendsChart.redraw(); });
                    $('#uncheckAllDataPoints').click(function(){  chart_datapointsTrendsChart.showLoading(); for(i=0; i < chart_datapointsTrendsChart.series.length; i++) {  if(chart_datapointsTrendsChart.series[i].visible == true){  chart_datapointsTrendsChart.series[i].setVisible(false, false);
                    }
                        chart_datapointsTrendsChart.series[i].checkbox.checked = false;
                        chart_datapointsTrendsChart.series[i].selected = false; breaks[i] = {};}
                        chart_datapointsTrendsChart.xAxis[0].update({ breaks: breaks }); chart_datapointsTrendsChart.hideLoading(); chart_datapointsTrendsChart.redraw(); });
                });
            </script>
        </div>
        <a id="checkAllDataPoints" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllDataPoints"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>
        <br/><br/>

    </div>
</div>

