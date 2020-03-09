<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 5.0</li>
                <li>Published:&nbsp;2 August 2016</li>
            </ul>
        </div>

        <div>
            <h4>Statistical Package</h4>
            <ul class="mt-0">
                <li>PhenStat</li>
                <li>Version:&nbsp;2.3.3</li>
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
                <li>Number of phenotyped genes:&nbsp;3,328</li>
                <li>Number of phenotyped mutant lines:&nbsp;3,532</li>
                <li>Number of phenotype calls:&nbsp;28,406</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-5.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-5.0</a>
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

        <h3>Data release 5.0</h3>

            <div class="well">
                <strong>Release notes:</strong>
                Represents a major data release. Changes include:
                <ul>
                    <li>
                        Data for an additional 1000 phenotyped lines
                    </li>
                    <li>
                        Additional control data for Grip strength procedures (protocol IMPC_GRS).
                        The following list of genes has had one or more phenotype associations removed:
                        <ul>
                            <li>
                                Abca13, Aif1l, Aldob, Alg10b, Anapc13, Arhgap23, Btbd16, Ccdc64, Ccdc94,
                                Cdkl2, Cilp, Col14a1, Cox5b, Cyp2ab1, Cyp7b1, Dact2, Dst, Foxj3, Gstcd,
                                Gstm4, Hip1r, Igfbp7, Il12rb1, Kcng2, Lss, Mmp15, Mogs, Nanos2, Nxn,
                                Patl1, Podn, Psca, Rad9a, Rims3, Rps19bp1, Scarb1, Serpinf1, Skiv2l,
                                Smo, Spata5, Spdye4b, Sptssa, Tas2r131, Wdr59
                            </li>
                        </ul>
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
                        <td>563</td>
                        <td>4,153</td>
                        <td>15,531</td>
                    </tr>
                    <tr>
                        <td>NING</td>
                        <td>157</td>
                        <td>1,321</td>
                        <td>2,337</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>291</td>
                        <td>4,692</td>
                        <td>12,426</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>140</td>
                        <td>1,512</td>
                        <td>2,455</td>
                    </tr>
                    <tr>
                        <td>MRC Harwell</td>
                        <td>362</td>
                        <td>3,285</td>
                        <td>8,685</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>42</td>
                        <td>1,217</td>
                        <td>972</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>129</td>
                        <td>1,306</td>
                        <td>1,990</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>1,008</td>
                        <td>3,184</td>
                        <td>16,204</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>204</td>
                        <td>1,611</td>
                        <td>5,906</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>636</td>
                        <td>1,742</td>
                        <td>14,275</td>
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
                            8,107,737
                        </td>
                        <td>
                            930
                            <sup>*</sup>
                        </td>
                        <td>
                            10,766
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            5,938,585
                        </td>
                        <td>
                            8,081
                            <sup>*</sup>
                        </td>
                        <td>
                            154,942
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            8,772,128
                        </td>
                        <td>
                            190
                            <sup>*</sup>
                        </td>
                        <td>
                            84,718
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>
                            111,319
                        </td>
                        <td>
                            14,784
                            <sup>*</sup>
                        </td>
                        <td>
                            19,826
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            270,804
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
                                categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Combined SHIRPA and Dysmorphology", "Open Field", "DSS Histology", "Rotarod", "FACS", "Organ Weight", "Challenge Whole Body Plethysmography", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Embryo LacZ", "Eye Morphology", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Gross Morphology Placenta E9.5", "Gross Morphology Placenta E12.5", "Gross Morphology Placenta E14.5-E15.5", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Anti-nuclear antibody assay", "Buffy coat peripheral blood leukocyte immunophenotyping", "Bone marrow immunophenotyping", "Cytotoxic T cell function", "Ear epidermis immunophenotyping", "Spleen Immunophenotyping", "Mesenteric Lymph Node Immunophenotyping", "Whole blood peripheral blood leukocyte immunophenotyping", "Antigen Specific Immunoglobulin Assay", "Salmonella Challenge", "Tail Flick", "Trichuris"],
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
                                "data": [431, 438, 479, 0, 444, 0, 0, 0, 0, 0, 0, 484, 279, 100, 559, 0, 350, 403, 497, 0, 230, 432, 54, 89, 16, 20, 0, 27, 5, 439, 319, 134, 430, 99, 345, 464, 203, 443, 393, 89, 204, 470, 533, 176, 413, 260, 164, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "JAX"
                            }, {
                                "data": [111, 8, 90, 0, 0, 0, 0, 0, 0, 0, 0, 120, 0, 118, 147, 19, 76, 108, 0, 122, 0, 98, 0, 0, 0, 0, 0, 0, 0, 112, 65, 0, 74, 90, 0, 119, 113, 133, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "NING"
                            }, {
                                "data": [174, 177, 193, 0, 0, 0, 0, 44, 0, 0, 0, 199, 66, 197, 210, 4, 192, 195, 182, 0, 93, 194, 72, 126, 58, 0, 72, 127, 58, 176, 192, 116, 93, 63, 0, 188, 195, 198, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0],
                                "name": "TCP"
                            }, {
                                "data": [107, 124, 122, 0, 119, 0, 0, 0, 0, 0, 0, 114, 67, 18, 138, 127, 129, 117, 129, 133, 0, 121, 0, 0, 0, 0, 0, 0, 0, 125, 127, 0, 81, 56, 82, 131, 82, 121, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "HMGU"
                            }, {
                                "data": [346, 352, 343, 0, 0, 84, 327, 0, 0, 0, 0, 221, 235, 0, 362, 277, 320, 326, 132, 273, 133, 330, 0, 0, 0, 0, 0, 0, 0, 348, 317, 0, 0, 91, 0, 336, 257, 339, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MRC Harwell"
                            }, {
                                "data": [29, 26, 33, 0, 0, 0, 0, 9, 0, 0, 0, 38, 38, 41, 42, 29, 31, 35, 23, 0, 32, 35, 0, 0, 0, 0, 0, 0, 0, 38, 35, 0, 24, 37, 0, 36, 40, 42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "RBRC"
                            }, {
                                "data": [77, 61, 102, 0, 105, 0, 0, 119, 95, 103, 95, 114, 0, 0, 126, 112, 69, 104, 116, 127, 5, 7, 0, 0, 0, 0, 0, 0, 0, 102, 101, 0, 102, 0, 0, 103, 71, 115, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "ICS"
                            }, {
                                "data": [0, 547, 0, 1, 0, 0, 0, 0, 0, 0, 0, 384, 521, 0, 679, 383, 596, 552, 0, 0, 0, 557, 0, 0, 0, 0, 0, 0, 0, 542, 582, 51, 542, 0, 546, 596, 0, 658, 0, 0, 0, 0, 0, 0, 0, 0, 0, 397, 346, 365, 331, 397, 393, 390, 365, 327, 197, 0, 104],
                                "name": "WTSI"
                            }, {
                                "data": [190, 196, 171, 0, 0, 0, 0, 5, 0, 0, 0, 176, 159, 7, 204, 25, 167, 156, 167, 170, 54, 156, 15, 44, 12, 27, 0, 0, 0, 171, 108, 34, 128, 43, 0, 161, 180, 171, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "BCM"
                            }, {
                                "data": [417, 239, 218, 0, 0, 0, 0, 0, 0, 0, 0, 562, 426, 0, 634, 344, 276, 250, 569, 0, 0, 477, 11, 0, 0, 0, 0, 0, 0, 497, 363, 0, 447, 0, 393, 483, 543, 281, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                        <td>764</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>921</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>1670</td>
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
                        <td>52</td>
                    </tr>
                </tbody>
            </table>

            <p>Mouse knockout programs:&nbsp;KOMP,EUCOMM,NCOM,mirKO,IST12471H5</p>

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
                            categories: ["Combined SHIRPA and Dysmorphology", "Open Field", "Auditory Brain Stem Response", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Hematology", "Heart Weight", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Immunophenotyping", "Insulin Blood Level", "Rotarod", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Organ Weight", "Challenge Whole Body Plethysmography"],
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
                            "data": [107, 250, 58, 0, 420, 459, 142, 0, 67, 12, 88, 209, 16, 89, 364, 0, 34, 16, 35, 15, 22, 20, 185, 81, 226, 13, 9, 0, 0],
                            "name": "JAX"
                        }, {
                            "data": [23, 53, 11, 0, 55, 113, 0, 24, 12, 6, 21, 62, 6, 14, 70, 21, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "NING"
                        }, {
                            "data": [73, 150, 22, 2, 155, 310, 48, 0, 41, 10, 40, 192, 4, 15, 182, 141, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3],
                            "name": "TCP"
                        }, {
                            "data": [37, 122, 17, 9, 107, 26, 11, 22, 291, 13, 46, 68, 5, 27, 64, 17, 44, 27, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "HMGU"
                        }, {
                            "data": [251, 282, 38, 5, 325, 334, 18, 41, 116, 6, 67, 136, 0, 97, 258, 153, 33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 74, 0],
                            "name": "MRC Harwell"
                        }, {
                            "data": [3, 8, 10, 3, 13, 38, 8, 0, 0, 5, 2, 28, 4, 4, 30, 24, 4, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "RBRC"
                        }, {
                            "data": [36, 78, 24, 5, 66, 68, 13, 11, 3, 2, 49, 58, 25, 12, 124, 38, 6, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "ICS"
                        }, {
                            "data": [139, 0, 105, 2, 599, 426, 0, 0, 124, 19, 139, 217, 11, 44, 408, 448, 0, 0, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "WTSI"
                        }, {
                            "data": [143, 58, 92, 2, 37, 187, 31, 55, 63, 2, 20, 16, 7, 17, 168, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "BCM"
                        }, {
                            "data": [56, 53, 99, 7, 151, 164, 25, 0, 7, 31, 86, 102, 20, 59, 272, 86, 43, 0, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                                "data": [7113, 2946, 1973, 1643, 1522, 1365, 1411, 1362, 1147, 812, 474, 368, 941, 334, 515, 430, 286, 584, 554, 327, 282, 227, 570, 189, 360, 261, 309, 369, 296, 192, 698, 282, 124, 252, 286, 130, 100, 213, 46, 185, 82, 269, 120, 107, 31, 19, 27, 11, 1, 682878],
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
                                "data": [3757, 3116, 3165, 3227, 4041, 4125, 4264, 4512, 4485, 4750, 4631, 4845, 4654, 4966, 4968, 4980, 5025, 5109, 5040, 5055, 5030, 4955, 4855, 4855, 4751, 4593, 4820, 4550, 4462, 4395, 4237, 4080, 4024, 3887, 3623, 3749, 3468, 3307, 3194, 4494, 4735, 4422, 4146, 4172, 3532, 3764, 2004, 1537, 1684, 3184],
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
                                "data": [3660, 1266, 906, 858, 590, 561, 535, 471, 510, 509, 497, 480, 424, 486, 434, 364, 418, 407, 404, 390, 409, 400, 391, 373, 343, 410, 347, 423, 423, 323, 373, 299, 392, 331, 354, 320, 374, 322, 341, 315, 377, 332, 311, 300, 383, 342, 273, 382, 315, 351],
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
                                "data": [29777, 9851, 7417, 6281, 5555, 5047, 4302, 3791, 3511, 3586, 3361, 3291, 3241, 3182, 2976, 2998, 2987, 2930, 2966, 2976, 2655, 2821, 2846, 2670, 2695, 2689, 2662, 2635, 2495, 2557, 2641, 2604, 2611, 2540, 2506, 2571, 2506, 2456, 2471, 2409, 2450, 2511, 2498, 2459, 2434, 2466, 2445, 2579, 2511, 2400],
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
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3", "5.0"],
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
                                "data": [294, 470, 518, 1458, 1468, 1468, 1466, 1466, 2218, 2432, 2432, 3328],
                                "name": "Phenotyped genes",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " genes"
                                },
                                "type": "column"
                            }, {
                                "data": [301, 484, 535, 1528, 1540, 1540, 1537, 1537, 2340, 2577, 2562, 3532],
                                "name": "Phenotyped lines",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " lines"
                                },
                                "type": "column"
                            }, {
                                "yAxis": 1,
                                "data": [1069, 2732, 2182, 6114, 5974, 6064, 6278, 6382, 12159, 13008, 21667, 28406],
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
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3", "5.0"],
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
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 756, 756, 756, 930],
                                "name": "Categorical (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068, 3956543, 3956543, 5827451, 6431595, 6597532, 8107737],
                                "name": "Categorical (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [388, 1214, 1764, 3426, 3426, 3426, 3426, 3426, 4544, 4776, 4764, 10766],
                                "name": "Categorical (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 5623, 10765, 106682, 107059, 107059, 107044, 107044, 186464, 208924, 211801, 270804],
                                "name": "Image record (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "Image record (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 1721, 18591, 18591, 18591, 18591, 18591, 13970, 14643, 14625, 14784],
                                "name": "Text (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [2387, 12803, 15355, 51283, 51611, 51611, 51597, 51597, 81962, 89172, 89429, 111319],
                                "name": "Text (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 0, 100, 161, 161, 161, 161, 161, 15690, 17178, 17159, 19826],
                                "name": "Text (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [0, 13176, 4, 9, 9, 9, 9, 9, 116, 128, 128, 190],
                                "name": "Time series (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471, 7383933, 7383933, 8721563, 9138387, 9077104, 8772128],
                                "name": "Time series (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [83, 83, 39, 82, 77684, 77684, 77684, 77684, 81063, 80413, 80403, 84718],
                                "name": "Time series (issues)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [172, 1443, 992, 3724, 3726, 3726, 3726, 3726, 7342, 7553, 7503, 8081],
                                "name": "Unidimensional (QC failed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851, 2981261, 2981261, 4399848, 4797248, 4543845, 5938585],
                                "name": "Unidimensional (QC passed)",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                },
                                "type": "spline"
                            }, {
                                "data": [1454, 5286, 16928, 38737, 39065, 39065, 39065, 39065, 105213, 113998, 113801, 154942],
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
                                categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2", "4.3", "5.0"],
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
                                "data": [73, 116, 93, 241, 243, 243, 243, 243, 452, 480, 478, 591],
                                "name": "adipose tissue phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [74, 377, 394, 636, 624, 716, 716, 716, 2254, 1916, 2110, 3109],
                                "name": "behavior/neurological phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [12, 26, 44, 296, 261, 261, 261, 261, 379, 366, 445, 802],
                                "name": "cardiovascular system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 4, 24, 56, 55, 55, 55, 55, 77, 87, 142, 179],
                                "name": "craniofacial phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 2, 5, 24, 43],
                                "name": "digestive/alimentary phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 33, 40, 411, 505],
                                "name": "embryo phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 95, 198],
                                "name": "endocrine/exocrine gland phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [289, 473, 561, 533, 536, 536, 536, 536, 982, 1053, 836, 1095],
                                "name": "growth/size/body region phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [84, 125, 237, 245, 245, 245, 245, 245, 331, 349, 390, 520],
                                "name": "hearing/vestibular/ear phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [131, 366, 495, 704, 690, 692, 694, 694, 775, 792, 995, 1389],
                                "name": "hematopoietic system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [342, 1046, 1370, 1368, 1389, 1385, 1383, 1383, 1576, 1703, 1753, 2687],
                                "name": "homeostasis/metabolism phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [18, 69, 81, 183, 169, 172, 174, 174, 214, 214, 446, 569],
                                "name": "immune system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 19, 55, 79, 77, 77, 77, 77, 145, 189, 217, 364],
                                "name": "integument phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 26, 132, 204, 205, 205, 205, 205, 358, 426, 582, 577],
                                "name": "limbs/digits/tail phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 16, 32],
                                "name": "liver/biliary system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 80, 858, 906, 906, 1122, 1346, 1670, 1880, 2416, 2423],
                                "name": "mortality/aging",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 5, 7, 7, 7, 7, 9, 9, 9, 20],
                                "name": "muscle phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 42, 79, 73, 66, 66, 66, 66, 178, 183, 377, 512],
                                "name": "nervous system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4],
                                "name": "normal phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [10, 22, 51, 88, 80, 80, 80, 80, 143, 170, 165, 239],
                                "name": "pigmentation phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 8, 27, 27, 27, 27, 27, 26, 25, 57, 111],
                                "name": "renal/urinary system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 3, 43, 43, 43, 43, 86, 126, 152, 149, 333],
                                "name": "reproductive system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [0, 0, 0, 1, 1, 1, 1, 1, 2, 3, 17, 33],
                                "name": "respiratory system phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [101, 162, 223, 714, 718, 718, 718, 718, 1352, 1455, 1473, 1812],
                                "name": "skeleton phenotype",
                                "tooltip": {
                                    "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                    "valueSuffix": " "
                                }
                            }, {
                                "data": [70, 79, 127, 521, 316, 316, 316, 316, 592, 611, 663, 908],
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
