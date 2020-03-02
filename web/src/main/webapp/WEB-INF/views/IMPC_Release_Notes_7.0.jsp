<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">


                <li>Release: 7.0</li>
                <li>Published: 26 March 2018</li>
            </ul>
        </div>

        <div>
            <h4>Statistical Package</h4>
            <ul class="mt-0">
                <li>PhenStat</li>
                <li>Version:&nbsp;2.14.0</li>
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
                <li>Number of phenotyped genes:&nbsp;4,820</li>
                <li>Number of phenotyped mutant lines:&nbsp;5,186</li>
                <li>Number of phenotype calls:&nbsp;60,670</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-7.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-7.0</a>
                </li>
                <li>RESTful interfaces:&nbsp;<a
                        href="/data/documentation/data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h2 id="new-features">Highlights</h2>
        <div class="col-12">
            <h3>Data release 7.0</h3>

            <div class="well">
                <strong>Data release 10.0 represents a major data release. Changes include:</strong>
                <ul>
                    <li>More than 7,000 new phenotypes</li>
                    <li>More than 500 new genes</li>
                    <li>Statistical analysis of EYE parameters no longer include categories 'no data left eye' and
                        'no data right eye'
                    </li>
                    <li>Viability data correction and new viability data</li>
                    <li>Corrected metadata group calculation for several parameters</li>
                    <li>Corrected extraneous zygosity phenotype association when data was available for multiple
                        zygosities, but insufficient numbers were available for one of the datasets to perform
                        statistical analysis
                    </li>
                    <li>Inclusion of derived parameter calculation for Heart weight normalised against body weight
                        (IMPC_HWT_012_001)
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
                        <td>1,028</td>
                        <td>6,994</td>
                        <td>29,299</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>471</td>
                        <td>6,345</td>
                        <td>20,873</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>261</td>
                        <td>2,664</td>
                        <td>4,773</td>
                    </tr>
                    <tr>
                        <td>KMPC</td>
                        <td>30</td>
                        <td>549</td>
                        <td>628</td>
                    </tr>
                    <tr>
                        <td>MARC</td>
                        <td>235</td>
                        <td>1,879</td>
                        <td>3,776</td>
                    </tr>
                    <tr>
                        <td>MRC Harwell</td>
                        <td>502</td>
                        <td>5,179</td>
                        <td>12,953</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>79</td>
                        <td>1,785</td>
                        <td>1,621</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>193</td>
                        <td>2,008</td>
                        <td>3,091</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>1,088</td>
                        <td>3,504</td>
                        <td>17,552</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>467</td>
                        <td>1,607</td>
                        <td>13,943</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>832</td>
                        <td>2,891</td>
                        <td>19,715</td>
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
                        12,225,255
                    </td>
                    <td>
                        3,221
                        <sup>*</sup>
                    </td>
                    <td>
                        16,529
                        <sup>*</sup>
                    </td>
                </tr>
                <tr>
                    <td>unidimensional</td>
                    <td>
                        9,099,910
                    </td>
                    <td>
                        28,370
                        <sup>*</sup>
                    </td>
                    <td>
                        203,522
                        <sup>*</sup>
                    </td>
                </tr>
                <tr>
                    <td>time series</td>
                    <td>
                        12,778,993
                    </td>
                    <td>
                        2,393
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
                        154,731
                    </td>
                    <td>
                        11,369
                        <sup>*</sup>
                    </td>
                    <td>
                        39,733
                        <sup>*</sup>
                    </td>
                </tr>`
                <tr>
                    <td>image record</td>
                    <td>
                        358,288
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

            <h3>Allele Types</h3>

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
                        <td>a</td>
                        <td>793</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>1100</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>2320</td>
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
                        <td>3</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>e</td>
                        <td>62</td>
                    </tr>
                </tbody>
            </table>

            <p>Mouse knockout programs:&nbsp;KOMP,IMPC,EUCOMM,NCOM,mirKO,IST12471H5</p>
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
                            categories: ["adipose tissue phenotype","behavior/neurological phenotype","cardiovascular system phenotype","craniofacial phenotype","digestive/alimentary phenotype","endocrine/exocrine gland phenotype","growth/size/body region phenotype","hearing/vestibular/ear phenotype","hematopoietic system phenotype","homeostasis/metabolism phenotype","immune system phenotype","integument phenotype","limbs/digits/tail phenotype","mortality/aging","muscle phenotype","nervous system phenotype","normal phenotype","pigmentation phenotype","renal/urinary system phenotype","reproductive system phenotype","respiratory system phenotype","skeleton phenotype","vision/eye phenotype","embryo phenotype","liver/biliary system phenotype"],
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
                        series:[{"data":[375,4101,1407,2794,28,19,3068,1493,3757,5008,2156,9890,6484,2,46,469,4316,2284,37,599,4,7982,4271,0,0],"name":"hemizygote"},{"data":[5490,80325,39164,41245,972,864,53318,25017,44232,65397,23336,126414,93467,0,1526,16888,159260,27856,1188,9301,659,96795,72814,18606,2048],"name":"heterozygote"},{"data":[10005,136512,59375,71217,1306,1410,86157,47923,84927,119372,46346,230406,166679,7478,2449,21518,344634,51591,2253,22536,1208,188776,126619,10463,1399],"name":"homozygote"}]
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
                            <script type="text/javascript">$(function () { $('#genotypeStatusChart').highcharts({ colors:['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'], chart: {type: 'column' }, title: {text: 'Genotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Micro-injection in progress', 64], ['Chimeras obtained', 26], ['Genotype confirmed', 3587], ['Cre Excision Started', null], ['Cre Excision Complete', 3406], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
                        </div>
                    </div>

                    <div class="col-6">
                        <div id="phenotypeStatusChart">
                            <script type="text/javascript">$(function () { $('#phenotypeStatusChart').highcharts({ colors:['rgba(239, 123, 11,50.0)', 'rgba(9, 120, 161,50.0)', 'rgba(119, 119, 119,50.0)', 'rgba(238, 238, 180,50.0)', 'rgba(36, 139, 75,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(255, 201, 67,50.0)', 'rgba(191, 151, 50,50.0)', 'rgba(239, 123, 11,50.0)', 'rgba(247, 157, 70,50.0)', 'rgba(247, 181, 117,50.0)', 'rgba(191, 75, 50,50.0)', 'rgba(151, 51, 51,50.0)', 'rgba(144, 195, 212,50.0)'], chart: {type: 'column' }, title: {text: 'Phenotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Phenotype Attempt Registered', 1183], ['Phenotyping Started', 358], ['Phenotyping Complete', 4362], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
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
                            series:[{"data":[0,0,503,704],"name":"JAX"},{"data":[0,0,206,277],"name":"TCP"},{"data":[3,0,39,20],"name":"IMG"},{"data":[4,0,37,60],"name":"Monterotondo"},{"data":[0,0,3,1],"name":"VETMEDUNI"},{"data":[50,0,230,426],"name":"Harwell"},{"data":[0,0,1,1],"name":"Monterotondo R&D"},{"data":[0,0,15,11],"name":"SEAT"},{"data":[0,0,202,74],"name":"MARC"},{"data":[0,0,1,0],"name":"Oulu"},{"data":[0,0,197,463],"name":"BCM"},{"data":[0,0,0,30],"name":"CDTA"},{"data":[1,0,40,291],"name":"HMGU"},{"data":[0,0,9,5],"name":"CAM-SU GRC"},{"data":[0,0,13,17],"name":"CIPHE"},{"data":[0,0,1,0],"name":"CNB"},{"data":[5,0,35,3],"name":"KMPC"},{"data":[0,0,623,740],"name":"UCD"},{"data":[0,0,152,143],"name":"ICS"},{"data":[1,2,1294,438],"name":"WTSI"},{"data":[0,0,14,4],"name":"NARLabs"},{"data":[0,0,65,33],"name":"RIKEN BRC"},{"data":[0,0,0,0],"name":"Monash"}]
                        });
                        $('#checkAllGenByCenter').click(function(){  for(i=0; i < chart_genotypeStatusByCenterChart.series.length; i++) { if(chart_genotypeStatusByCenterChart.series[i].visible == false){  chart_genotypeStatusByCenterChart.series[i].show(); }}}); $('#uncheckAllGenByCenter').click(function(){  for(i=0; i < chart_genotypeStatusByCenterChart.series.length; i++) {  if(chart_genotypeStatusByCenterChart.series[i].visible == true){  chart_genotypeStatusByCenterChart.series[i].hide(); }}}); });
                    </script>
                </div>
                <a id="checkAllGenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
                <a id="uncheckAllGenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
                <div class="clear both"></div>
            </div>

            <div class="col-6">
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
                            series:[{"data":[15,0,792],"name":"JAX"},{"data":[0,0,526],"name":"TCP"},{"data":[0,8,3],"name":"IMG"},{"data":[0,0,9],"name":"Monterotondo"},{"data":[0,0,0],"name":"VETMEDUNI"},{"data":[5,22,533],"name":"Harwell"},{"data":[0,0,2],"name":"SEAT"},{"data":[0,0,198],"name":"MARC"},{"data":[2,1,302],"name":"BCM"},{"data":[0,0,2],"name":"CDTA"},{"data":[3,0,311],"name":"HMGU"},{"data":[0,0,0],"name":"CAM-SU GRC"},{"data":[0,0,4],"name":"CIPHE"},{"data":[0,2,6],"name":"KMPC"},{"data":[6,0,746],"name":"UCD"},{"data":[0,0,274],"name":"ICS"},{"data":[2,1,1165],"name":"WTSI"},{"data":[0,0,57],"name":"RIKEN BRC"}]
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
                            categories: ["Combined SHIRPA and Dysmorphology","Open Field","Auditory Brain Stem Response","Acoustic Startle and Pre-pulse Inhibition (PPI)","Indirect Calorimetry","Clinical Chemistry","Body Composition (DEXA lean/fat)","Electrocardiogram (ECG)","Echo","Eye Morphology","Fertility of Homozygous Knock-out Mice","Grip Strength","Hematology","Heart Weight","Intraperitoneal glucose tolerance test (IPGTT)","Viability Primary Screen","X-ray","Insulin Blood Level","Rotarod","Electroconvulsive Threshold Testing","Hole-board Exploration","Light-Dark Test","Plasma Chemistry","Sleep Wake","Tail Suspension","Urinalysis","Organ Weight","Challenge Whole Body Plethysmography"],
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
                        series:[{"data":[145,666,53,142,0,417,865,153,0,103,30,114,232,29,132,658,0,33,5,25,29,279,74,255,22,15,0,0],"name":"JAX"},{"data":[80,306,72,93,10,336,686,56,0,34,17,26,320,12,56,286,268,0,0,0,0,0,0,0,0,0,0,1],"name":"TCP"},{"data":[56,199,22,104,29,187,88,44,65,482,11,26,86,8,39,186,1,2,0,0,0,0,0,0,0,0,0,0],"name":"HMGU"},{"data":[11,16,3,10,9,13,12,0,0,4,0,5,10,3,1,0,23,0,0,0,0,0,0,0,0,0,0,0],"name":"KMPC"},{"data":[50,126,14,19,0,139,179,0,91,10,33,14,318,8,18,78,50,0,0,0,0,0,0,0,0,0,0,0],"name":"MARC"},{"data":[560,856,44,244,7,930,842,78,52,186,12,52,876,0,230,324,268,0,0,0,0,0,0,0,0,0,144,0],"name":"MRC Harwell"},{"data":[5,16,9,13,7,29,71,54,0,0,5,1,59,0,6,48,16,0,0,0,0,0,0,0,0,0,0,0],"name":"RBRC"},{"data":[59,122,42,39,6,137,101,4,16,74,9,36,99,28,16,158,129,0,10,0,0,0,0,0,0,0,0,0],"name":"ICS"},{"data":[557,0,252,0,41,2081,1742,0,0,292,39,222,823,36,130,706,593,108,0,0,0,0,0,0,0,0,0,0],"name":"WTSI"},{"data":[61,76,75,62,4,68,325,30,46,51,20,25,18,12,35,308,43,0,0,0,0,0,0,0,0,0,0,0],"name":"BCM"},{"data":[89,538,63,91,1,284,369,46,0,14,52,50,293,18,74,326,45,68,0,0,0,0,0,0,0,0,0,0],"name":"UC Davis"}]
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
                <tr><td>TCP</td><td><a href="/data/page-retired">Browse</a></td><td>TCP_001</td></tr>
                <tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>HMGU_001</td></tr>
                <tr><td>KMPC</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>MARC</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>MRC Harwell</td><td><a href="/data/page-retired">Browse</a></td><td>HRWL_001</td></tr>
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
                            categories: ["1.0","1.1","2.0","3.0","3.1","3.2","3.3","3.4","4.0","4.2","4.3","5.0","6.0","6.1","7.0"],
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
                        series:[{"data":[294,470,518,1458,1468,1468,1466,1466,2218,2432,2432,3328,4364,4364,4820],"name":"Phenotyped genes","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" genes"},"type":"column"},{"data":[301,484,535,1528,1540,1540,1537,1537,2340,2577,2562,3532,4745,4745,5186],"name":"Phenotyped lines","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" lines"},"type":"column"},{"yAxis":1,"data":[1069,2732,2182,6114,5974,6064,6278,6382,12159,13008,21667,28406,52076,52656,60670],"name":"MP calls","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" calls"},"type":"spline"}]
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
                            text: 'Data points'
                        },
                        subtitle: {
                            text: ''
                        },
                        xAxis: [{
                            categories: ["1.0","1.1","2.0","3.0","3.1","3.2","3.3","3.4","4.0","4.2","4.3","5.0","6.0","6.1","7.0"],
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
                        series:[{"data":[0,0,0,0,0,0,0,0,756,756,756,930,3389,3389,3221],"name":"Categorical (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[149194,958957,1173776,3935293,3956068,3956068,3956543,3956543,5827451,6431595,6597532,8107737,11074178,11129868,12225255],"name":"Categorical (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[388,1214,1764,3426,3426,3426,3426,3426,4544,4776,4764,10766,14775,14775,16529],"name":"Categorical (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"Image record (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[0,5623,10765,106682,107059,107059,107044,107044,186464,208924,211801,270804,327658,315223,358288],"name":"Image record (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],"name":"Image record (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,1721,18591,18591,18591,18591,18591,13970,14643,14625,14784,11431,11431,11369],"name":"Text (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[2387,12803,15355,51283,51611,51611,51597,51597,81962,89172,89429,111319,143003,143004,154731],"name":"Text (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[0,0,100,161,161,161,161,161,15690,17178,17159,19826,34141,34141,39733],"name":"Text (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[0,13176,4,9,9,9,9,9,116,128,128,190,2393,2393,2393],"name":"Time series (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[63773,1451844,1798263,7237320,7415471,7415471,7383933,7383933,8721563,9138387,9077104,8772128,12001974,12002305,12778993],"name":"Time series (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[83,83,39,82,77684,77684,77684,77684,81063,80413,80403,84718,0,0,11],"name":"Time series (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[172,1443,992,3724,3726,3726,3726,3726,7342,7553,7503,8081,24321,24321,28370],"name":"Unidimensional (QC failed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[381406,1011637,988022,2782486,2970851,2970851,2981261,2981261,4399848,4797248,4543845,5938585,8151407,8150815,9099910],"name":"Unidimensional (QC passed)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"},{"data":[1454,5286,16928,38737,39065,39065,39065,39065,105213,113998,113801,154942,188283,188283,203522],"name":"Unidimensional (issues)","tooltip":{"pointFormat":"<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>","valueSuffix":" "},"type":"spline"}]
                    });
                    });
                    $('#checkAllDataPoints').click(function(){  for(i=0; i < chart_datapointsTrendsChart.series.length; i++) { if(chart_datapointsTrendsChart.series[i].visible == false){  chart_datapointsTrendsChart.series[i].show(); }}}); $('#uncheckAllDataPoints').click(function(){  for(i=0; i < chart_datapointsTrendsChart.series.length; i++) {  if(chart_datapointsTrendsChart.series[i].visible == true){  chart_datapointsTrendsChart.series[i].hide(); }}}); });
            </script>
        </div>

        <a id="checkAllDataPoints" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
        <a id="uncheckAllDataPoints"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
        <div class="clear both"></div>
    </div>
</div>