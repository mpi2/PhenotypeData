<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-md-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release:&nbsp;10.0</li>
                <li>Published:&nbsp;27 March 2019</li>
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
                <li>Version: GRCm38</li>
            </ul>
        </div>
    </div>

    <div class="col-md-6">
        <div>
            <h4>Summary</h4>
            <ul class="mt-0">
                <li>Number of phenotyped genes:&nbsp;5,861</li>
                <li>Number of phenotyped mutant lines:&nbsp;6,255</li>
                <li>Number of phenotype calls:&nbsp;69,982</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-10.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-10.0</a></li>
                <li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h2 id="new-features">Highlights</h2>
        <div class="col-12">
            <h4>Data release 10.0</h4>

            <div class="well">
                <strong>Release notes:</strong>
                Represents a major data release. Changes include:
                <ul>
                    <li>A significant increase in Acoustic Startle procedure data</li>
                    <li>About 30,000 missing images due to image file corruption</li>
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
                        <td>1,226</td>
                        <td>8,413</td>
                        <td>35,252</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>546</td>
                        <td>7,104</td>
                        <td>24,892</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>311</td>
                        <td>3,173</td>
                        <td>5,770</td>
                    </tr>
                    <tr>
                        <td>KMPC</td>
                        <td>43</td>
                        <td>869</td>
                        <td>1,067</td>
                    </tr>
                    <tr>
                        <td>MARC</td>
                        <td>232</td>
                        <td>1,877</td>
                        <td>3,776</td>
                    </tr>
                    <tr>
                        <td>MRC Harwell</td>
                        <td>596</td>
                        <td>5,893</td>
                        <td>15,626</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>96</td>
                        <td>2,023</td>
                        <td>1,859</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>223</td>
                        <td>2,325</td>
                        <td>3,554</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>1,329</td>
                        <td>4,540</td>
                        <td>21,316</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>597</td>
                        <td>4,572</td>
                        <td>17,476</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>1,056</td>
                        <td>4,156</td>
                        <td>23,749</td>
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
                            14,917,863
                        </td>
                        <td>
                            3,835
                            <sup>*</sup>
                        </td>
                        <td>
                            16,974
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            11,068,097
                        </td>
                        <td>
                            43,508
                            <sup>*</sup>
                        </td>
                        <td>
                            293,835
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            13,444,019
                        </td>
                        <td>
                            2,615
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
                            192,176
                        </td>
                        <td>
                            11,124
                            <sup>*</sup>
                        </td>
                        <td>
                            45,751
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            469,960
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
                    var chart_distribution;	Highcharts.setOptions({	    colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});   chart_distribution = new Highcharts.Chart({         chart: {
                            type: 'column',
                            height: 800,
                            renderTo:distribution        },
                        title: {
                            text: 'Distribution of Phenotype Associations in IMPC'
                        },
                        subtitle: {
                            text: ""
                        },
                        xAxis: {
                            categories: ["adipose tissue phenotype","behavior\/neurological phenotype","cardiovascular system phenotype","craniofacial phenotype","digestive\/alimentary phenotype","embryo phenotype","endocrine\/exocrine gland phenotype","growth\/size\/body region phenotype","hearing\/vestibular\/ear phenotype","hematopoietic system phenotype","homeostasis\/metabolism phenotype","immune system phenotype","integument phenotype","limbs\/digits\/tail phenotype","liver\/biliary system phenotype","mortality\/aging","muscle phenotype","nervous system phenotype","normal phenotype","pigmentation phenotype","renal\/urinary system phenotype","reproductive system phenotype","respiratory system phenotype","skeleton phenotype","vision\/eye phenotype"],
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
                        series:[{"data":[277,4099,1445,1817,52,8,26,2203,1544,2329,2947,1420,5672,4275,1,57,51,466,5820,1431,48,456,5,4745,3620],"name":"hemizygote"},{"data":[3868,74248,40523,35027,1596,27262,1214,49727,22006,33527,48349,19419,97644,77080,2770,419,1381,19281,204083,22623,1236,8711,537,67432,73238],"name":"heterozygote"},{"data":[7408,127527,57157,55565,2231,13030,1912,73724,45718,66113,83682,40799,166744,130050,1637,9676,2212,21625,421043,40401,2180,22538,1014,128954,118698],"name":"homozygote"}]
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
                    <div class="col-md-6">
                        <div id="genotypeStatusChart">
                            <script type="text/javascript">$(function () { $('#genotypeStatusChart').highcharts({ colors:['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'], chart: {type: 'column' }, title: {text: 'Genotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -45, style: {fontSize: '11px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Micro-injection in progress', 20], ['Chimeras obtained', 20], ['Genotype confirmed', 4436], ['Cre Excision Complete', 3489], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div id="phenotypeStatusChart">
                            <script type="text/javascript">$(function () { $('#phenotypeStatusChart').highcharts({ colors:['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'], chart: {type: 'column' }, title: {text: 'Phenotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -45, style: {fontSize: '11px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Phenotype Attempt Registered', 1111], ['Phenotyping Started', 188], ['Phenotyping Complete', 5860], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
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
                            series:[{"data":[0,0,785,706],"name":"JAX"},{"data":[0,0,258,277],"name":"TCP"},{"data":[4,0,32,65],"name":"Monterotondo"},{"data":[0,0,3,0],"name":"VETMEDUNI"},{"data":[0,6,270,432],"name":"Harwell"},{"data":[0,0,1,2],"name":"Monterotondo R&D"},{"data":[0,0,15,11],"name":"SEAT"},{"data":[0,0,202,74],"name":"MARC"},{"data":[0,0,1,0],"name":"Oulu"},{"data":[1,0,1343,458],"name":"WSI"},{"data":[0,0,314,480],"name":"BCM"},{"data":[0,0,0,30],"name":"CDTA"},{"data":[1,3,79,316],"name":"HMGU"},{"data":[5,4,27,18],"name":"CAM-SU GRC"},{"data":[7,3,114,29],"name":"CCP-IMG"},{"data":[0,0,13,17],"name":"CIPHE"},{"data":[0,0,1,0],"name":"CNB"},{"data":[0,4,40,12],"name":"KMPC"},{"data":[1,0,801,742],"name":"UCD"},{"data":[0,0,153,147],"name":"ICS"},{"data":[1,0,15,4],"name":"NARLabs"},{"data":[0,0,70,33],"name":"RIKEN BRC"},{"data":[0,0,0,0],"name":"Monash"}]
                        });
                        $('#checkAllGenByCenter').click(function(){  for(i=0; i < chart_genotypeStatusByCenterChart.series.length; i++) { if(chart_genotypeStatusByCenterChart.series[i].visible == false){  chart_genotypeStatusByCenterChart.series[i].show(); }}}); $('#uncheckAllGenByCenter').click(function(){  for(i=0; i < chart_genotypeStatusByCenterChart.series.length; i++) {  if(chart_genotypeStatusByCenterChart.series[i].visible == true){  chart_genotypeStatusByCenterChart.series[i].hide(); }}}); });
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
                            series:[{"data":[307,46,1138],"name":"JAX"},{"data":[55,14,653],"name":"TCP"},{"data":[3,0,9],"name":"Monterotondo"},{"data":[42,24,677],"name":"Harwell"},{"data":[0,0,2],"name":"SEAT"},{"data":[5,12,199],"name":"MARC"},{"data":[329,11,1414],"name":"WSI"},{"data":[119,3,509],"name":"BCM"},{"data":[0,0,2],"name":"CDTA"},{"data":[42,24,385],"name":"HMGU"},{"data":[62,0,8],"name":"CCP-IMG"},{"data":[0,0,4],"name":"CIPHE"},{"data":[0,5,38],"name":"KMPC"},{"data":[130,45,1016],"name":"UCD"},{"data":[17,6,330],"name":"ICS"},{"data":[6,2,92],"name":"RIKEN BRC"}]
                        });
                        $('#checkAllPhenByCenter').click(function(){  for(i=0; i < chart_phenotypeStatusByCenterChart.series.length; i++) { if(chart_phenotypeStatusByCenterChart.series[i].visible == false){  chart_phenotypeStatusByCenterChart.series[i].show(); }}}); $('#uncheckAllPhenByCenter').click(function(){  for(i=0; i < chart_phenotypeStatusByCenterChart.series.length; i++) {  if(chart_phenotypeStatusByCenterChart.series[i].visible == true){  chart_phenotypeStatusByCenterChart.series[i].hide(); }}}); });
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
                            categories: ["Combined SHIRPA and Dysmorphology","Open Field","Acoustic Startle and Pre-pulse Inhibition (PPI)","Indirect Calorimetry","Clinical Chemistry","Body Composition (DEXA lean\/fat)","Electrocardiogram (ECG)","Echo","Eye Morphology","Fertility of Homozygous Knock-out Mice","Grip Strength","Hematology","Heart Weight","Light-Dark Test","Organ Weight","Viability Primary Screen","X-ray","Auditory Brain Stem Response","Insulin Blood Level","Intraperitoneal glucose tolerance test (IPGTT)","Rotarod","Electroconvulsive Threshold Testing","Hole-board Exploration","Plasma Chemistry","Sleep Wake","Tail Suspension","Urinalysis","Fear Conditioning","Challenge Whole Body Plethysmography","Trabecular Bone MicroCT"],
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
                        series:[{"data":[178,935,153,0,439,1039,192,0,174,44,265,303,39,373,0,714,0,57,33,143,15,25,35,74,252,20,14,0,0,0],"name":"JAX"},{"data":[109,359,109,13,193,377,39,0,17,22,73,144,3,11,0,326,141,176,0,34,0,0,0,0,0,0,0,14,1,0],"name":"TCP"},{"data":[73,265,139,31,223,103,46,56,534,10,80,109,15,0,0,138,1,20,3,40,0,0,0,0,0,0,0,0,0,0],"name":"HMGU"},{"data":[15,23,26,12,15,47,0,0,8,0,20,16,2,0,0,0,22,25,0,1,0,0,0,0,0,0,0,0,0,0],"name":"KMPC"},{"data":[50,122,19,0,123,165,0,75,6,33,32,302,6,0,0,78,0,34,0,16,0,0,0,0,0,0,0,0,0,0],"name":"MARC"},{"data":[328,511,102,3,429,433,35,26,81,12,75,508,0,0,67,386,138,33,0,109,0,0,0,0,0,0,0,0,0,0],"name":"MRC Harwell"},{"data":[28,22,14,7,25,50,10,0,0,7,2,60,2,0,0,58,18,27,0,6,0,0,0,0,0,0,0,0,0,0],"name":"RBRC"},{"data":[101,128,52,7,155,84,13,15,177,9,73,128,38,0,0,166,37,40,0,23,31,0,0,0,0,0,0,0,0,0],"name":"ICS"},{"data":[257,0,0,29,974,917,0,0,81,53,167,457,12,0,0,788,201,288,52,80,0,0,0,0,0,0,0,0,0,2],"name":"WTSI"},{"data":[94,154,64,2,115,453,38,134,60,20,71,30,22,12,8,448,22,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"BCM"},{"data":[151,256,132,1,328,552,51,0,21,84,130,159,20,0,0,484,43,104,73,88,0,0,0,0,0,0,0,0,0,0],"name":"UC Davis"}]
                    });
                    $('#checkAllPhenCalls').click(function(){  for(i=0; i < chart_callProcedureChart.series.length; i++) { if(chart_callProcedureChart.series[i].visible == false){  chart_callProcedureChart.series[i].show(); }}}); $('#uncheckAllPhenCalls').click(function(){  for(i=0; i < chart_callProcedureChart.series.length; i++) {  if(chart_callProcedureChart.series[i].visible == true){  chart_callProcedureChart.series[i].hide(); }}}); });

            </script>
        </div>

        <a id="checkAllPhenCalls" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllPhenCalls"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>
    </div>
</div>

<div class="section">

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
                                text: 'Release by Release'
                            },
                            xAxis: [{
                                categories: ["1.0","1.1","2.0","3.2","3.3","3.4","3.0","3.1","4.3","4.0","4.2","5.0","6.0","6.1","7.0","8.0","9.1","9.2","10.0"],
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
                            series:[{"data":[294,470,518,1468,1466,1466,1458,1468,2432,2218,2432,3328,4364,4364,4820,5115,5570,5614,5861],"name":"Phenotyped genes","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" genes"},"type":"column"},{"data":[301,484,535,1540,1537,1537,1528,1540,2562,2340,2577,3532,4745,4745,5186,5505,5957,6006,6255],"name":"Phenotyped lines","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" lines"},"type":"column"},{"yAxis":1,"data":[1069,2732,2182,6064,6278,6382,6114,5974,21667,12159,13008,28406,52076,52656,60670,59407,57797,58107,69982],"name":"MP calls","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" calls"},"type":"spline"}]
                        });
                        });
                    });
                </script>
            </div>
            <br/>
            <br/>
            <br/>

            <div id="datapointsTrendsChart">
                <script type="text/javascript">
                    $(function () { var chart_datapointsTrendsChart;
                        $(document).ready(function() { chart_datapointsTrendsChart = new Highcharts.Chart({
                            chart: {
                                zoomType: 'xy',
                                renderTo: 'datapointsTrendsChart'            },
                            title: {
                                text: 'Data points'
                            },
                            subtitle: {
                                text: ''
                            },
                            xAxis: [{
                                categories: ["1.0","1.1","2.0","3.2","3.3","3.4","3.0","3.1","4.3","4.0","4.2","5.0","6.0","6.1","7.0","8.0","9.1","9.2","10.0"],
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
                            series:[{"data":[0,0,0,0,0,0,0,0,756,756,756,930,3389,3389,3221,3221,3734,3734,3835],"name":"Categorical (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[149194,958957,1173776,3956068,3956543,3956543,3935293,3956068,6597532,5827451,6431595,8107737,11074178,11129868,12225255,13047108,13312831,13312831,14917863],"name":"Categorical (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[388,1214,1764,3426,3426,3426,3426,3426,4764,4544,4776,10766,14775,14775,16529,15810,17736,17736,16974],"name":"Categorical (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"Image record (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[0,5623,10765,107059,107044,107044,106682,107059,211801,186464,208924,270804,327658,315223,358288,366858,396400,396400,469960],"name":"Image record (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"Image record (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,1721,18591,18591,18591,18591,18591,14625,13970,14643,14784,11431,11431,11369,11176,11124,11124,11124],"name":"Text (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[2387,12803,15355,51611,51597,51597,51283,51611,89429,81962,89172,111319,143003,143004,154731,160354,180098,180098,192176],"name":"Text (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,100,161,161,161,161,161,17159,15690,17178,19826,34141,34141,39733,48130,45653,45653,45751],"name":"Text (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[0,13176,4,9,9,9,9,9,128,116,128,190,2393,2393,2393,2471,2570,2570,2615],"name":"Time series (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[63773,1451844,1798263,7415471,7383933,7383933,7237320,7415471,9077104,8721563,9138387,8772128,12001974,12002305,12778993,12844228,12236102,12236110,13444019],"name":"Time series (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[83,83,39,77684,77684,77684,82,77684,80403,81063,80413,84718,0,0,11,11,11,11,11],"name":"Time series (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[172,1443,992,3726,3726,3726,3724,3726,7503,7342,7553,8081,24321,24321,28370,29870,36892,36902,43508],"name":"Unidimensional (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[381406,1011637,988022,2970851,2981261,2981261,2782486,2970851,4543845,4399848,4797248,5938585,8151407,8150815,9099910,9463677,10063922,10094468,11068097],"name":"Unidimensional (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"},{"data":[1454,5286,16928,39065,39065,39065,38737,39065,113801,105213,113998,154942,188283,188283,203522,209608,282348,283622,293835],"name":"Unidimensional (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>","valueSuffix":" "},"type":"spline"}]
                        });
                        });
                        $('#checkAllDataPoints').click(function(){  for(i=0; i < chart_datapointsTrendsChart.series.length; i++) { if(chart_datapointsTrendsChart.series[i].visible == false){  chart_datapointsTrendsChart.series[i].show(); }}}); $('#uncheckAllDataPoints').click(function(){  for(i=0; i < chart_datapointsTrendsChart.series.length; i++) {  if(chart_datapointsTrendsChart.series[i].visible == true){  chart_datapointsTrendsChart.series[i].hide(); }}}); });
                </script>
            </div>
            <a id="checkAllDataPoints" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
            <a id="uncheckAllDataPoints"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        </div>
    </div>
</div>
