<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 6.1</li>
                <li>Published:&nbsp;03 December 2017</li>
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
                <li>Number of phenotyped genes:&nbsp;4,364</li>
                <li>Number of phenotyped mutant lines:&nbsp;4,745</li>
                <li>Number of phenotype calls:&nbsp;52,656</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-6.1">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-6.1</a></li>
                <li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2 id="new-features">Highlights</h2>

        <div class="col-12">

            <h3>Data release 6.1</h3>

            <div class="well">
                <strong>Release notes:</strong>
                Represents a minor release. Changes include:
                <ul>
                    <li>New data from the 3i project</li>
                    <li>A fix to strain nomenclature for some legacy lines</li>
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
                        <td>920</td>
                        <td>5,742</td>
                        <td>24,912</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>443</td>
                        <td>5,891</td>
                        <td>19,740</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>227</td>
                        <td>2,264</td>
                        <td>4,156</td>
                    </tr>
                    <tr>
                        <td>KMPC</td>
                        <td>24</td>
                        <td>408</td>
                        <td>510</td>
                    </tr>
                    <tr>
                        <td>MARC</td>
                        <td>235</td>
                        <td>1,880</td>
                        <td>3,776</td>
                    </tr>
                    <tr>
                        <td>MRC Harwell</td>
                        <td>449</td>
                        <td>4,341</td>
                        <td>11,359</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>71</td>
                        <td>1,674</td>
                        <td>1,509</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>170</td>
                        <td>1,861</td>
                        <td>2,682</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>1,024</td>
                        <td>3,386</td>
                        <td>16,829</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>403</td>
                        <td>1,207</td>
                        <td>12,839</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>779</td>
                        <td>2,651</td>
                        <td>18,163</td>
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
                            11,129,868
                        </td>
                        <td>
                            3,389
                            <sup>*</sup>
                        </td>
                        <td>
                            14,775
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            8,150,815
                        </td>
                        <td>
                            24,321
                            <sup>*</sup>
                        </td>
                        <td>
                            188,283
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            12,002,305
                        </td>
                        <td>
                            2,393
                            <sup>*</sup>
                        </td>
                        <td>
                            0
                        </td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>
                            143,004
                        </td>
                        <td>
                            11,431
                            <sup>*</sup>
                        </td>
                        <td>
                            34,141
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            315,223
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
                                categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Combined SHIRPA and Dysmorphology", "Open Field", "Rotarod", "FACS", "Organ Weight", "Challenge Whole Body Plethysmography", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Chemistry", "Body Composition (DEXA lean\/fat)", "Electrocardiogram (ECG)", "Echo", "Embryo LacZ", "Eye Morphology", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Gross Morphology Placenta E9.5", "Gross Morphology Placenta E12.5", "Gross Morphology Placenta E14.5-E15.5", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Tail Flick"],
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
                                "data": [695, 574, 711, 459, 0, 0, 0, 0, 0, 0, 794, 564, 100, 899, 0, 372, 611, 736, 0, 659, 586, 89, 130, 23, 32, 81, 109, 18, 668, 360, 135, 584, 96, 354, 688, 297, 434, 414, 89, 286, 678, 836, 171, 520, 261, 219, 0],
                                "name": "JAX"
                            }, {
                                "data": [263, 70, 257, 0, 0, 0, 56, 0, 0, 0, 263, 77, 265, 274, 238, 237, 253, 243, 0, 127, 92, 127, 194, 95, 0, 120, 206, 99, 234, 253, 162, 212, 103, 0, 252, 237, 246, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60],
                                "name": "TCP"
                            }, {
                                "data": [183, 212, 195, 186, 0, 0, 0, 0, 0, 0, 203, 67, 89, 226, 217, 220, 196, 217, 225, 0, 144, 0, 0, 0, 0, 0, 0, 0, 214, 215, 0, 181, 68, 122, 224, 130, 203, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "HMGU"
                            }, {
                                "data": [415, 420, 398, 0, 83, 397, 0, 0, 0, 0, 284, 381, 0, 449, 285, 388, 374, 131, 300, 222, 404, 0, 0, 0, 0, 0, 0, 0, 430, 391, 60, 0, 91, 0, 411, 370, 400, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MRC Harwell"
                            }, {
                                "data": [208, 9, 191, 0, 0, 0, 0, 0, 0, 0, 208, 0, 218, 229, 27, 132, 197, 0, 198, 0, 196, 0, 0, 0, 0, 0, 0, 0, 203, 167, 0, 164, 90, 0, 191, 137, 186, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MARC"
                            }, {
                                "data": [23, 23, 22, 0, 0, 0, 0, 0, 0, 0, 23, 0, 0, 24, 23, 19, 20, 0, 0, 1, 20, 1, 1, 0, 0, 1, 1, 0, 23, 20, 0, 20, 20, 0, 23, 21, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "KMPC"
                            }, {
                                "data": [46, 52, 53, 0, 0, 0, 9, 0, 0, 0, 53, 47, 68, 69, 30, 50, 56, 37, 0, 43, 57, 0, 0, 0, 0, 0, 0, 0, 64, 55, 0, 24, 63, 0, 61, 67, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "RBRC"
                            }, {
                                "data": [113, 94, 140, 141, 0, 0, 163, 133, 140, 133, 156, 0, 0, 168, 126, 108, 141, 4, 145, 13, 53, 2, 0, 0, 0, 0, 0, 0, 137, 132, 0, 139, 0, 0, 139, 110, 148, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "ICS"
                            }, {
                                "data": [0, 620, 0, 0, 0, 0, 0, 0, 0, 0, 507, 518, 0, 692, 470, 635, 618, 0, 0, 0, 623, 0, 0, 0, 0, 0, 0, 0, 617, 638, 50, 610, 0, 592, 638, 0, 689, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "WTSI"
                            }, {
                                "data": [328, 283, 62, 0, 0, 0, 6, 0, 0, 0, 0, 325, 218, 401, 298, 154, 326, 91, 283, 87, 307, 39, 87, 13, 52, 0, 42, 2, 362, 191, 38, 290, 206, 0, 296, 291, 349, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "BCM"
                            }, {
                                "data": [544, 487, 462, 0, 0, 0, 0, 0, 0, 0, 727, 4, 0, 762, 486, 401, 604, 719, 0, 0, 603, 17, 38, 0, 0, 0, 0, 0, 655, 522, 0, 547, 0, 571, 666, 694, 717, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                        <td>a</td>
                        <td>1156</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>1113</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>2235</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>2</td>
                        <td>6</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>c</td>
                        <td>2</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>d</td>
                        <td>2</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>e</td>
                        <td>76</td>
                    </tr>
                </tbody>
            </table>

            <p>Mouse knockout programs:&nbsp;RRG305,FLP1,mirKO,EUCJ0019c12,IST12471H5,EUCJ0004f10,W096D02,EUCJ0079d10,YHD437,RRR454,Thy1-MAPT*P301S,pT1Betageo,RRH308,KOMP,CB0226,M076C04,EUC0047a08,NCC,EUC0050e03,EUC0027g03,XG716,EUCE0233a03,IMPC,RRJ142,EUCOMM,NCOM,YTC001,XG510,EUC0054a05</p>

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
                            categories: ["adipose tissue phenotype", "behavior\/neurological phenotype", "cardiovascular system phenotype", "craniofacial phenotype", "digestive\/alimentary phenotype", "endocrine\/exocrine gland phenotype", "growth\/size\/body region phenotype", "hearing\/vestibular\/ear phenotype", "hematopoietic system phenotype", "homeostasis\/metabolism phenotype", "immune system phenotype", "integument phenotype", "limbs\/digits\/tail phenotype", "mortality\/aging", "muscle phenotype", "reproductive system phenotype", "respiratory system phenotype", "skeleton phenotype", "vision\/eye phenotype", "embryo phenotype", "liver\/biliary system phenotype", "mammalian phenotype", "nervous system phenotype", "pigmentation phenotype", "renal\/urinary system phenotype"],
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
                            "data": [5, 32, 12, 8, 2, 2, 31, 2, 21, 66, 13, 13, 12, 8, 2, 6, 1, 32, 31, 0, 0, 0, 0, 0, 0],
                            "name": "hemizygote"
                        }, {
                            "data": [309, 1623, 551, 64, 20, 115, 1164, 65, 1180, 1890, 539, 220, 216, 0, 16, 133, 51, 715, 428, 453, 9, 20, 306, 76, 57],
                            "name": "heterozygote"
                        }, {
                            "data": [836, 4852, 1212, 419, 66, 265, 2956, 419, 3333, 4893, 1375, 613, 846, 3897, 45, 525, 49, 2624, 1011, 688, 42, 19, 618, 219, 150],
                            "name": "homozygote"
                        }]
                    });
                });

            </script>
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
                            categories: ["Combined SHIRPA and Dysmorphology", "Open Field", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Indirect Calorimetry", "Clinical Chemistry", "Body Composition (DEXA lean\/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Hematology", "Heart Weight", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Auditory Brain Stem Response", "Insulin Blood Level", "Rotarod", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Organ Weight", "Challenge Whole Body Plethysmography"],
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
                            "data": [140, 636, 131, 0, 411, 594, 143, 0, 88, 25, 94, 352, 21, 141, 892, 0, 32, 33, 5, 25, 27, 233, 74, 263, 22, 17, 0, 0],
                            "name": "JAX"
                        }, {
                            "data": [174, 282, 64, 10, 420, 434, 72, 0, 74, 17, 20, 248, 3, 68, 252, 270, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
                            "name": "TCP"
                        }, {
                            "data": [57, 155, 80, 23, 192, 51, 53, 113, 285, 0, 24, 93, 6, 35, 170, 0, 20, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "HMGU"
                        }, {
                            "data": [48, 144, 16, 0, 146, 156, 0, 99, 24, 33, 14, 324, 8, 18, 136, 57, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "MARC"
                        }, {
                            "data": [554, 702, 232, 8, 888, 610, 15, 70, 224, 6, 52, 890, 0, 234, 310, 258, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 160, 0],
                            "name": "MRC Harwell"
                        }, {
                            "data": [26, 18, 13, 7, 33, 45, 124, 0, 0, 5, 1, 40, 0, 5, 44, 15, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "RBRC"
                        }, {
                            "data": [59, 130, 29, 11, 133, 44, 31, 13, 125, 9, 26, 85, 20, 16, 162, 122, 36, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "ICS"
                        }, {
                            "data": [332, 0, 0, 14, 1204, 988, 0, 0, 200, 36, 132, 502, 20, 104, 686, 314, 157, 70, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "WTSI"
                        }, {
                            "data": [39, 3, 38, 2, 36, 238, 31, 72, 36, 20, 15, 25, 9, 32, 292, 31, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "BCM"
                        }, {
                            "data": [167, 567, 92, 1, 211, 360, 49, 0, 12, 46, 52, 557, 18, 76, 296, 39, 61, 68, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                <tr><td>TCP</td><td><a href="/data/page-retired">Browse</a></td><td>TCP_001</td></tr>
                <tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>HMGU_001</td></tr>
                <tr><td>KMPC</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>MARC</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>MRC Harwell</td><td><a href="/data/page-retired">Browse</a></td><td>HRWL_001</td></tr>
                <tr><td>ICS</td><td><a href="/data/page-retired">Browse</a></td><td>ICS_001</td></tr>
                <tr><td>WTSI</td><td><a href="/data/ppage-retired">Browse</a></td><td>MGP_001</td></tr>
                <tr><td>BCM</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>BCM</td><td><a href="/data/page-retired1">Browse</a></td><td>BCM_001</td></tr>
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
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                                "data": [3119, 1042, 743, 690, 552, 520, 557, 402, 374, 355, 420, 373, 338, 363, 313, 317, 350, 350, 348, 319, 294, 272, 360, 357, 290, 348, 328, 322, 294, 329, 275, 314, 336, 295, 292, 259, 240, 281, 240, 333, 259, 286, 353, 244, 311, 305, 288, 305, 288, 249],
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
                                "data": [28605, 9261, 7134, 5991, 5236, 4623, 4100, 3549, 3439, 3227, 3095, 3231, 2999, 2924, 2745, 2763, 2777, 2855, 2671, 2604, 2630, 2700, 2639, 2538, 2546, 2414, 2486, 2498, 2499, 2399, 2306, 2385, 2389, 2402, 2300, 2417, 2317, 2181, 2311, 2280, 2265, 2246, 2321, 2220, 2332, 2389, 2262, 2300, 2194, 2203],
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
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3", "5.0", "6.0", "6.1"],
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
                                "data": [294, 470, 518, 1458, 1468, 1468, 1466, 1466, 2218, 2432, 2432, 3328, 4364, 4364],
                                "name": "Phenotyped genes",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " genes"
                                },
                                "type": "column"
                            }, {
                                "data": [301, 484, 535, 1528, 1540, 1540, 1537, 1537, 2340, 2577, 2562, 3532, 4745, 4745],
                                "name": "Phenotyped lines",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " lines"
                                },
                                "type": "column"
                            }, {
                                "yAxis": 1,
                                "data": [1069, 2732, 2182, 6114, 5974, 6064, 6278, 6382, 12159, 13008, 21667, 28406, 52076, 52656],
                                "name": "MP calls",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
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
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3", "5.0", "6.0", "6.1"],
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
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 756, 756, 756, 930, 3389, 3389],
                                "name": "Categorical (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068, 3956543, 3956543, 5827451, 6431595, 6597532, 8107737, 11074178, 11129868],
                                "name": "Categorical (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [388, 1214, 1764, 3426, 3426, 3426, 3426, 3426, 4544, 4776, 4764, 10766, 14775, 14775],
                                "name": "Categorical (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 5623, 10765, 106682, 107059, 107059, 107044, 107044, 186464, 208924, 211801, 270804, 327658, 315223],
                                "name": "Image record (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 1721, 18591, 18591, 18591, 18591, 18591, 13970, 14643, 14625, 14784, 11431, 11431],
                                "name": "Text (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [2387, 12803, 15355, 51283, 51611, 51611, 51597, 51597, 81962, 89172, 89429, 111319, 143003, 143004],
                                "name": "Text (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 100, 161, 161, 161, 161, 161, 15690, 17178, 17159, 19826, 34141, 34141],
                                "name": "Text (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 13176, 4, 9, 9, 9, 9, 9, 116, 128, 128, 190, 2393, 2393],
                                "name": "Time series (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471, 7383933, 7383933, 8721563, 9138387, 9077104, 8772128, 12001974, 12002305],
                                "name": "Time series (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [83, 83, 39, 82, 77684, 77684, 77684, 77684, 81063, 80413, 80403, 84718, 0, 0],
                                "name": "Time series (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [172, 1443, 992, 3724, 3726, 3726, 3726, 3726, 7342, 7553, 7503, 8081, 24321, 24321],
                                "name": "Unidimensional (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851, 2981261, 2981261, 4399848, 4797248, 4543845, 5938585, 8151407, 8150815],
                                "name": "Unidimensional (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br\/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [1454, 5286, 16928, 38737, 39065, 39065, 39065, 39065, 105213, 113998, 113801, 154942, 188283, 188283],
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
    </div>
</div>
