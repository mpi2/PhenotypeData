<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 4.0</li>
                <li>Published:&nbsp;20 October 2015</li>
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
                <li>Number of phenotyped genes:&nbsp;2,218</li>
                <li>Number of phenotyped mutant lines:&nbsp;2,340</li>
                <li>Number of phenotype calls:&nbsp;12,159</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-4.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-4.0</a></li>
                <li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2 id="new-features">Highlights</h2>

        <div class="col-12">

            <h3>Data release 4.0</h3>

            <div class="well">
                <strong>Release notes:</strong>
                Represents a major data release.
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
                        <td>390</td>
                        <td>2,495</td>
                        <td>9,309</td>
                    </tr>
                    <tr>
                        <td>NING</td>
                        <td>65</td>
                        <td>734</td>
                        <td>943</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>228</td>
                        <td>3,669</td>
                        <td>9,239</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>102</td>
                        <td>1,134</td>
                        <td>1,791</td>
                    </tr>
                    <tr>
                        <td>MRC Harwell</td>
                        <td>286</td>
                        <td>2,905</td>
                        <td>6,757</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>26</td>
                        <td>859</td>
                        <td>578</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>99</td>
                        <td>1,001</td>
                        <td>1,521</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>588</td>
                        <td>2,632</td>
                        <td>13,519</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>129</td>
                        <td>815</td>
                        <td>3,170</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>427</td>
                        <td>1,249</td>
                        <td>9,427</td>
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
                            5,827,451
                        </td>
                        <td>
                            756
                            <sup>*</sup>
                        </td>
                        <td>
                            4,544
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            4,399,848
                        </td>
                        <td>
                            7,342
                            <sup>*</sup>
                        </td>
                        <td>
                            105,213
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            8,721,563
                        </td>
                        <td>
                            116
                            <sup>*</sup>
                        </td>
                        <td>
                            81,063
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>
                            81,962
                        </td>
                        <td>
                            13,970
                            <sup>*</sup>
                        </td>
                        <td>
                            15,690
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            186,464
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
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#lineProcedureChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
                            },
                            title: {
                                text: 'Lines per procedure'
                            },
                            subtitle: {
                                text: "Center by center"
                            },
                            xAxis: {
                                categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Combined SHIRPA and Dysmorphology", "Open Field", "DSS Histology", "Rotarod", "FACS", "Organ Weight", "Challenge Whole Body Plethysmography", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Embryo LacZ", "Eye Morphology", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Gross Morphology Placenta E9.5", "Gross Morphology Placenta E12.5", "Gross Morphology Placenta E14.5-E15.5", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Anti-nuclear antibody assay", "Buffy coat peripheral blood leukocyte immunophenotyping", "Bone marrow immunophenotyping", "Ear epidermis immunophenotyping", "Whole blood peripheral blood leukocyte immunophenotyping", "Salmonella Challenge", "Tail Flick", "Trichuris"],
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
                                "data": [243, 238, 245, 0, 129, 0, 0, 0, 0, 0, 0, 319, 248, 84, 384, 0, 196, 262, 260, 0, 126, 258, 9, 29, 12, 13, 0, 0, 0, 262, 107, 118, 190, 217, 53, 231, 140, 257, 161, 72, 90, 253, 355, 139, 209, 197, 3, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "JAX"
                            }, {
                                "data": [40, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 33, 0, 51, 61, 13, 20, 40, 0, 31, 0, 38, 0, 0, 0, 0, 0, 0, 0, 47, 20, 0, 28, 34, 0, 46, 45, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "NING"
                            }, {
                                "data": [147, 151, 164, 0, 0, 0, 0, 34, 0, 0, 0, 127, 40, 167, 226, 157, 147, 158, 153, 0, 16, 154, 52, 90, 35, 0, 52, 90, 35, 150, 148, 108, 87, 0, 0, 158, 166, 166, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0],
                                "name": "TCP"
                            }, {
                                "data": [60, 81, 72, 0, 65, 0, 0, 0, 0, 0, 0, 71, 67, 2, 102, 79, 78, 63, 89, 97, 0, 81, 0, 0, 0, 0, 0, 0, 0, 76, 80, 0, 47, 26, 54, 84, 61, 77, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "HMGU"
                            }, {
                                "data": [261, 269, 259, 0, 0, 89, 261, 0, 0, 0, 0, 137, 147, 0, 285, 197, 242, 237, 129, 206, 28, 241, 0, 0, 0, 0, 0, 0, 0, 266, 235, 0, 0, 0, 0, 244, 127, 254, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MRC Harwell"
                            }, {
                                "data": [17, 13, 20, 0, 0, 0, 0, 9, 0, 0, 0, 1, 17, 23, 25, 23, 18, 23, 3, 0, 19, 24, 0, 0, 0, 0, 0, 0, 0, 25, 21, 0, 22, 19, 0, 22, 22, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "RBRC"
                            }, {
                                "data": [49, 34, 75, 0, 76, 0, 0, 87, 70, 58, 67, 80, 0, 0, 94, 80, 31, 61, 86, 98, 0, 50, 0, 0, 0, 0, 0, 0, 0, 74, 41, 0, 46, 0, 0, 61, 45, 84, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "ICS"
                            }, {
                                "data": [0, 476, 0, 1, 0, 0, 0, 0, 0, 0, 0, 170, 0, 0, 575, 349, 474, 434, 0, 0, 0, 456, 0, 0, 0, 0, 0, 0, 0, 453, 476, 0, 391, 0, 315, 503, 0, 526, 0, 0, 0, 0, 0, 0, 0, 0, 0, 305, 346, 276, 301, 266, 22, 0, 75],
                                "name": "WTSI"
                            }, {
                                "data": [123, 126, 117, 0, 0, 0, 0, 3, 0, 0, 0, 114, 94, 4, 124, 54, 18, 91, 113, 114, 21, 89, 6, 26, 10, 18, 0, 0, 0, 114, 1, 0, 43, 14, 0, 101, 95, 103, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "BCM"
                            }, {
                                "data": [147, 144, 149, 0, 0, 0, 0, 0, 0, 0, 0, 383, 184, 0, 415, 183, 142, 231, 370, 0, 0, 297, 0, 0, 0, 0, 0, 0, 0, 315, 205, 0, 192, 0, 58, 323, 262, 142, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "UC Davis"
                            }]
                        });
                    });
                </script>
            </div>

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
                        <td>433</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>648</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>1196</td>
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
                        <td>40</td>
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
                    Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                    $('#distribution').highcharts({
                        chart: {
                            type: 'column',
                            height: 800
                        },
                        title: {
                            text: 'Distribution of Phenotype Associations in IMPC'
                        },
                        subtitle: {
                            text: ""
                        },
                        xAxis: {
                            categories: ["behavior/neurological phenotype", "craniofacial phenotype", "growth/size/body region phenotype", "hematopoietic system phenotype", "homeostasis/metabolism phenotype", "immune system phenotype", "limbs/digits/tail phenotype", "skeleton phenotype", "vision/eye phenotype", "adipose tissue phenotype", "cardiovascular system phenotype", "digestive/alimentary phenotype", "embryogenesis phenotype", "hearing/vestibular/ear phenotype", "integument phenotype", "mortality/aging", "muscle phenotype", "nervous system phenotype", "pigmentation phenotype", "renal/urinary system phenotype", "respiratory system phenotype", "liver/biliary system phenotype", "reproductive system phenotype"],
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
                            "data": [3, 2, 16, 5, 6, 4, 1, 5, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "hemizygote"
                        }, {
                            "data": [660, 20, 554, 503, 762, 272, 79, 388, 265, 109, 130, 5, 1, 51, 59, 3, 2, 81, 45, 11, 32, 0, 0],
                            "name": "heterozygote"
                        }, {
                            "data": [2340, 110, 1901, 1618, 1973, 875, 369, 1474, 486, 411, 291, 11, 32, 280, 181, 1677, 7, 163, 133, 17, 22, 1, 131],
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
			          		<script type="text/javascript">$(function () { $('#genotypeStatusChart').highcharts({ chart: {type: 'column' }, title: {text: 'Genotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Micro-injection in progress', 16], ['Chimeras obtained', 60], ['Genotype confirmed', 2369], ['Cre Excision Started', 62], ['Cre Excision Complete', 2576], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
                        </div>

                        <div class="col-6">
                            <div id="phenotypeStatusChart">
                                <script type="text/javascript">$(function () { $('#phenotypeStatusChart').highcharts({ chart: {type: 'column' }, title: {text: 'Phenotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Phenotype Attempt Registered', 1189], ['Phenotyping Started', 621], ['Phenotyping Complete', 2088], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
                            </div>
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
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#genotypeStatusByCenterChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
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
                            series: [{"data": [0, 2, 82, 0, 618], "name": "JAX"}, {
                                "data": [0, 1, 3, 0, 263],
                                "name": "TCP"
                            }, {"data": [0, 0, 6, 0, 10], "name": "IMG"}, {
                                "data": [0, 0, 40, 0, 6],
                                "name": "Monterotondo"
                            }, {"data": [0, 41, 92, 23, 336], "name": "Harwell"}, {
                                "data": [1, 6, 8, 0, 0],
                                "name": "SEAT"
                            }, {"data": [0, 0, 175, 0, 52], "name": "MARC"}, {
                                "data": [0, 0, 2, 0, 0],
                                "name": "INFRAFRONTIER-VETMEDUNI"
                            }, {"data": [0, 0, 174, 2, 278], "name": "BCM"}, {
                                "data": [0, 0, 1, 0, 0],
                                "name": "INFRAFRONTIER-CNB"
                            }, {"data": [0, 0, 0, 0, 2], "name": "CDTA"}, {
                                "data": [0, 0, 52, 1, 226],
                                "name": "HMGU"
                            }, {"data": [0, 1, 14, 0, 0], "name": "CIPHE"}, {
                                "data": [0, 0, 1, 0, 0],
                                "name": "INFRAFRONTIER-Oulu"
                            }, {"data": [0, 0, 4, 0, 0], "name": "KMPC"}, {
                                "data": [15, 0, 404, 0, 523],
                                "name": "UCD"
                            }, {"data": [0, 3, 102, 0, 93], "name": "ICS"}, {
                                "data": [0, 0, 3, 0, 0],
                                "name": "INFRAFRONTIER-IMG"
                            }, {"data": [0, 1, 1189, 14, 141], "name": "WTSI"}, {
                                "data": [0, 0, 4, 0, 0],
                                "name": "NARLabs"
                            }, {"data": [0, 5, 13, 0, 28], "name": "RIKEN BRC"}]
                        });
                    });
                    </script>
                </div>
            </div>

            <div class="col-6">
                <div id="phenotypeStatusByCenterChart">
                    <script type="text/javascript">$(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#phenotypeStatusByCenterChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
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
                            series: [{"data": [274, 0, 389], "name": "JAX"}, {
                                "data": [104, 0, 179],
                                "name": "TCP"
                            }, {"data": [4, 0, 0], "name": "IMG"}, {
                                "data": [6, 0, 0],
                                "name": "Monterotondo"
                            }, {"data": [116, 1, 274], "name": "Harwell"}, {
                                "data": [20, 0, 61],
                                "name": "MARC"
                            }, {"data": [160, 0, 121], "name": "BCM"}, {
                                "data": [120, 0, 96],
                                "name": "HMGU"
                            }, {"data": [1, 0, 0], "name": "CIPHE"}, {
                                "data": [97, 0, 412],
                                "name": "UCD"
                            }, {"data": [26, 0, 85], "name": "ICS"}, {
                                "data": [381, 1, 505],
                                "name": "WTSI"
                            }, {"data": [23, 0, 22], "name": "RIKEN BRC"}]
                        });
                    });
                    </script>
                </div>
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
                    Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                    $('#callProcedureChart').highcharts({
                        chart: {
                            type: 'column',
                            height: 800
                        },
                        title: {
                            text: 'Phenotype calls per procedure'
                        },
                        subtitle: {
                            text: "Center by center"
                        },
                        xAxis: {
                            categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Combined SHIRPA and Dysmorphology", "Open Field", "Auditory Brain Stem Response", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Heart Weight", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Indirect Calorimetry", "Hematology", "Insulin Blood Level", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Rotarod", "Sleep Wake", "Tail Suspension", "Organ Weight", "Gross Morphology Placenta E12.5"],
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
                            "data": [91, 62, 79, 29, 214, 282, 100, 0, 50, 7, 20, 6, 57, 250, 0, 0, 71, 2, 2, 9, 13, 13, 4, 16, 135, 58, 4, 146, 16, 0, 0],
                            "name": "JAX"
                        }, {
                            "data": [4, 3, 0, 0, 14, 40, 0, 0, 0, 0, 14, 2, 4, 50, 22, 0, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "NING"
                        }, {
                            "data": [83, 52, 131, 15, 109, 249, 41, 0, 35, 10, 18, 5, 12, 170, 113, 2, 131, 0, 30, 15, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2],
                            "name": "TCP"
                        }, {
                            "data": [68, 16, 103, 13, 61, 9, 34, 18, 210, 13, 31, 3, 19, 52, 12, 4, 54, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "HMGU"
                        }, {
                            "data": [252, 175, 169, 32, 185, 249, 16, 28, 84, 0, 48, 0, 65, 130, 125, 3, 90, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 0],
                            "name": "MRC Harwell"
                        }, {
                            "data": [4, 1, 2, 0, 11, 38, 0, 0, 2, 3, 0, 4, 2, 14, 19, 2, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "RBRC"
                        }, {
                            "data": [33, 11, 56, 18, 21, 35, 18, 6, 42, 2, 5, 8, 3, 106, 22, 2, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "ICS"
                        }, {
                            "data": [0, 0, 0, 90, 509, 400, 0, 0, 116, 11, 123, 11, 39, 222, 345, 2, 190, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "WTSI"
                        }, {
                            "data": [34, 10, 41, 58, 2, 111, 24, 38, 35, 2, 19, 4, 9, 104, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "BCM"
                        }, {
                            "data": [10, 28, 43, 41, 66, 159, 16, 0, 5, 14, 25, 11, 34, 130, 64, 1, 65, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "UC Davis"
                        }]
                    });
                });
            </script>
        </div>
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
                <tr><td>JAX</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>JAX</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>JAX_001</td></tr>
                <tr><td>NING</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>TCP</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>TCP_001</td></tr>
                <tr><td>HMGU</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>HMGU</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>HMGU_001</td></tr>
                <tr><td>MRC Harwell</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>HRWL_001</td></tr>
                <tr><td>RBRC</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>ICS</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>ICS</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>ICS_001</td></tr>
                <tr><td>WTSI</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>MGP_001</td></tr>
                <tr><td>BCM</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>BCM</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>BCM_001</td></tr>
                <tr><td>UC Davis</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>UCD_001</td></tr>
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
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#FisherChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
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
                                "data": [5579, 2277, 1486, 1251, 1007, 1155, 1083, 975, 793, 696, 462, 310, 575, 476, 182, 490, 278, 334, 467, 158, 136, 313, 294, 96, 302, 211, 144, 295, 298, 157, 797, 182, 176, 151, 240, 159, 66, 124, 21, 97, 210, 19, 39, 68, 21, 8, 6, 5, 0, 505058],
                                "name": "Fisher's exact test"
                            }]
                        });
                    });
                </script>
            </div>

            <div id="WilcoxonChart">
                <script type="text/javascript">
                    $(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#WilcoxonChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
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
                                "data": [2353, 2307, 2313, 3298, 3261, 3454, 3385, 3866, 3785, 3873, 3928, 3900, 4018, 3943, 4692, 4302, 4197, 4274, 4258, 4396, 4313, 4921, 4478, 4529, 4457, 4023, 4029, 3986, 3924, 3785, 3837, 3630, 4602, 3765, 3324, 3258, 3221, 3145, 3052, 3796, 4225, 3953, 4734, 3833, 3458, 2103, 1634, 1337, 2065, 3089],
                                "name": "Wilcoxon rank sum test with continuity correction"
                            }]
                        });
                    });
                </script>
            </div>

            <div id="MMglsChart">
                <script type="text/javascript">
                    $(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#MMglsChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
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
                                "data": [2998, 992, 703, 662, 522, 502, 432, 425, 391, 368, 418, 361, 427, 410, 403, 337, 322, 359, 348, 348, 383, 369, 345, 315, 384, 381, 380, 367, 460, 375, 374, 338, 378, 338, 354, 400, 362, 387, 432, 395, 444, 455, 475, 391, 452, 477, 394, 435, 488, 543],
                                "name": "Mixed Model framework, generalized least squares, equation withoutWeight"
                            }]
                        });
                    });
                </script>
            </div>

            <div id="MMlmeChart">
                <script type="text/javascript">
                    $(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)']});
                        $('#MMlmeChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
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
                                "data": [19991, 6619, 4952, 4168, 3713, 3289, 2858, 2420, 2352, 2246, 2110, 2181, 2118, 2039, 1926, 1954, 1882, 1980, 1835, 1934, 1867, 1888, 1804, 1866, 1789, 1714, 1710, 1684, 1688, 1594, 1675, 1634, 1613, 1666, 1633, 1659, 1662, 1679, 1629, 1567, 1637, 1572, 1640, 1595, 1653, 1606, 1609, 1713, 1705, 1648],
                                "name": "Mixed Model framework, linear mixed-effects model, equation withoutWeight"
                            }]
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
                    $('#trendsChart').highcharts({
                        chart: {
                            zoomType: 'xy'
                        },
                        title: {
                            text: 'Genes/Mutant Lines/MP Calls'
                        },
                        subtitle: {
                            text: 'Release by Release'
                        },
                        xAxis: [{
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0"],
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
                            "data": [294, 470, 518, 1458, 1468, 1468, 1466, 1466, 2218],
                            "name": "Phenotyped genes",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " genes"
                            },
                            "type": "column"
                        }, {
                            "data": [301, 484, 535, 1528, 1540, 1540, 1537, 1537, 2340],
                            "name": "Phenotyped lines",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " lines"
                            },
                            "type": "column"
                        }, {
                            "yAxis": 1,
                            "data": [1069, 2732, 2182, 6114, 5974, 6064, 6278, 6382, 12159],
                            "name": "MP calls",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " calls"
                            },
                            "type": "spline"
                        }]
                    });
                });

            </script>
        </div>

        <div id="datapointsTrendsChart">
            <script type="text/javascript">
                $(function () {
                    $('#datapointsTrendsChart').highcharts({
                        chart: {
                            zoomType: 'xy'
                        },
                        title: {
                            text: 'Data points'
                        },
                        subtitle: {
                            text: ''
                        },
                        xAxis: [{
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0"],
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
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 756],
                            "name": "Categorical (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068, 3956543, 3956543, 5827451],
                            "name": "Categorical (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [388, 1214, 1764, 3426, 3426, 3426, 3426, 3426, 4544],
                            "name": "Categorical (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "Image record (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 5623, 10765, 106682, 107059, 107059, 107044, 107044, 186464],
                            "name": "Image record (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "Image record (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 1721, 18591, 18591, 18591, 18591, 18591, 13970],
                            "name": "Text (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [2387, 12803, 15355, 51283, 51611, 51611, 51597, 51597, 81962],
                            "name": "Text (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 100, 161, 161, 161, 161, 161, 15690],
                            "name": "Text (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 13176, 4, 9, 9, 9, 9, 9, 116],
                            "name": "Time series (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471, 7383933, 7383933, 8721563],
                            "name": "Time series (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [83, 83, 39, 82, 77684, 77684, 77684, 77684, 81063],
                            "name": "Time series (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [172, 1443, 992, 3724, 3726, 3726, 3726, 3726, 7342],
                            "name": "Unidimensional (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851, 2981261, 2981261, 4399848],
                            "name": "Unidimensional (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [1454, 5286, 16928, 38737, 39065, 39065, 39065, 39065, 105213],
                            "name": "Unidimensional (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }]
                    });
                });

            </script>
        </div>

        <div id="topLevelTrendsChart">
            <script type="text/javascript">
                $(function () {
                    $('#topLevelTrendsChart').highcharts({
                        chart: {
                            zoomType: 'xy'
                        },
                        title: {
                            text: 'Top Level Phenotypes'
                        },
                        subtitle: {
                            text: ''
                        },
                        xAxis: [{
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0"],
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
                            "data": [73, 116, 93, 241, 243, 243, 243, 243, 452],
                            "name": "adipose tissue phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [74, 377, 394, 636, 624, 716, 716, 716, 2254],
                            "name": "behavior/neurological phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [12, 26, 44, 296, 261, 261, 261, 261, 379],
                            "name": "cardiovascular system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 4, 24, 56, 55, 55, 55, 55, 77],
                            "name": "craniofacial phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 2],
                            "name": "digestive/alimentary phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 33],
                            "name": "embryogenesis phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [289, 473, 561, 533, 536, 536, 536, 536, 982],
                            "name": "growth/size/body region phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [84, 125, 237, 245, 245, 245, 245, 245, 331],
                            "name": "hearing/vestibular/ear phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [131, 366, 495, 704, 690, 692, 694, 694, 775],
                            "name": "hematopoietic system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [342, 1046, 1370, 1368, 1389, 1385, 1383, 1383, 1576],
                            "name": "homeostasis/metabolism phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [18, 69, 81, 183, 169, 172, 174, 174, 214],
                            "name": "immune system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 19, 55, 79, 77, 77, 77, 77, 145],
                            "name": "integument phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 26, 132, 204, 205, 205, 205, 205, 358],
                            "name": "limbs/digits/tail phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 1],
                            "name": "liver/biliary system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 80, 858, 906, 906, 1122, 1346, 1670],
                            "name": "mortality/aging",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 5, 7, 7, 7, 7, 9],
                            "name": "muscle phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 42, 79, 73, 66, 66, 66, 66, 178],
                            "name": "nervous system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [10, 22, 51, 88, 80, 80, 80, 80, 143],
                            "name": "pigmentation phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 8, 27, 27, 27, 27, 27, 26],
                            "name": "renal/urinary system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 3, 43, 43, 43, 43, 86, 126],
                            "name": "reproductive system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 1, 1, 1, 1, 1, 2],
                            "name": "respiratory system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [101, 162, 223, 714, 718, 718, 718, 718, 1352],
                            "name": "skeleton phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [70, 79, 127, 521, 316, 316, 316, 316, 592],
                            "name": "vision/eye phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }]
                    });
                });

            </script>
        </div>
    </div>
</div>
