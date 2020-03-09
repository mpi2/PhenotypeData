<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 4.2</li>
                <li>Published:&nbsp;08 December 2015</li>
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
                <li>Number of phenotyped mutant lines:&nbsp;2,577</li>
                <li>Number of phenotype calls:&nbsp;13,008</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-4.2">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-4.2</a>
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

        <h3>Data release 4.2</h3>
            <strong>Release notes:</strong>
            Represents a minor data release. Changes include:
            <ul>
                <li>More final QCed data</li>
                <li>Corrected genetic background strain for some legacy lines</li>
            </ul>
        </div>
        <div class="clear"></div>
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
                        <td>2,649</td>
                        <td>10,249</td>
                    </tr>
                    <tr>
                        <td>NING</td>
                        <td>93</td>
                        <td>985</td>
                        <td>1,362</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>254</td>
                        <td>3,885</td>
                        <td>10,582</td>
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
                        <td>2,982</td>
                        <td>7,212</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>32</td>
                        <td>968</td>
                        <td>696</td>
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
                        <td>144</td>
                        <td>919</td>
                        <td>3,719</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>475</td>
                        <td>1,319</td>
                        <td>10,362</td>
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
                            6,431,595
                        </td>
                        <td>
                            756
                            <sup>*</sup>
                        </td>
                        <td>
                            4,776
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            4,797,248
                        </td>
                        <td>
                            7,553
                            <sup>*</sup>
                        </td>
                        <td>
                            113,998
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            9,138,387
                        </td>
                        <td>
                            128
                            <sup>*</sup>
                        </td>
                        <td>413
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>
                            89,172
                        </td>
                        <td>
                            14,643
                            <sup>*</sup>
                        </td>
                        <td>
                            17,178
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            208,924
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
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                                categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Combined SHIRPA and Dysmorphology", "Open Field", "DSS Histology", "Rotarod", "FACS", "Organ Weight", "Challenge Whole Body Plethysmography", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Embryo LacZ", "Eye Morphology", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Gross Morphology Placenta E9.5", "Gross Morphology Placenta E12.5", "Gross Morphology Placenta E14.5-E15.5", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Anti-nuclear antibody assay", "Buffy coat peripheral blood leukocyte immunophenotyping", "Bone marrow immunophenotyping", "Ear epidermis immunophenotyping", "Spleen Immunophenotyping", "Mesenteric Lymph Node Immunophenotyping", "Whole blood peripheral blood leukocyte immunophenotyping", "Tail Flick", "Trichuris"],
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
                                "data": [316, 268, 254, 0, 129, 0, 0, 0, 0, 0, 0, 321, 251, 84, 414, 0, 195, 272, 273, 0, 126, 291, 9, 29, 12, 13, 0, 0, 0, 266, 109, 118, 198, 207, 57, 234, 140, 257, 161, 70, 99, 259, 371, 139, 209, 197, 35, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "JAX"
                            }, {
                                "data": [58, 1, 53, 0, 0, 0, 0, 0, 0, 0, 0, 56, 0, 64, 91, 13, 30, 52, 0, 56, 0, 41, 0, 0, 0, 0, 0, 0, 0, 70, 30, 0, 38, 39, 0, 56, 45, 80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "NING"
                            }, {
                                "data": [156, 169, 180, 0, 0, 0, 0, 34, 0, 0, 0, 132, 40, 184, 230, 166, 152, 174, 169, 0, 16, 170, 60, 108, 48, 0, 60, 114, 48, 164, 149, 110, 86, 48, 0, 159, 182, 184, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60, 0],
                                "name": "TCP"
                            }, {
                                "data": [72, 90, 85, 0, 80, 0, 0, 0, 0, 0, 0, 87, 67, 9, 120, 95, 86, 74, 104, 112, 0, 92, 0, 0, 0, 0, 0, 0, 0, 85, 89, 0, 48, 51, 56, 99, 67, 89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "HMGU"
                            }, {
                                "data": [275, 295, 283, 0, 0, 87, 268, 0, 0, 0, 0, 167, 168, 0, 309, 213, 273, 260, 131, 227, 28, 271, 0, 0, 0, 0, 0, 0, 0, 291, 266, 0, 0, 2, 0, 273, 154, 286, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MRC Harwell"
                            }, {
                                "data": [22, 18, 25, 0, 0, 0, 0, 9, 0, 0, 0, 1, 19, 28, 32, 29, 23, 27, 3, 0, 24, 27, 0, 0, 0, 0, 0, 0, 0, 29, 25, 0, 24, 25, 0, 28, 26, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "RBRC"
                            }, {
                                "data": [59, 45, 84, 0, 87, 0, 0, 96, 81, 67, 78, 88, 0, 0, 102, 86, 39, 66, 97, 102, 0, 55, 0, 0, 0, 0, 0, 0, 0, 84, 48, 0, 53, 0, 0, 68, 55, 95, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "ICS"
                            }, {
                                "data": [0, 520, 0, 1, 0, 0, 0, 0, 0, 0, 0, 170, 0, 0, 609, 344, 505, 474, 0, 0, 0, 491, 0, 0, 0, 0, 0, 0, 0, 495, 518, 37, 425, 0, 340, 544, 0, 591, 0, 0, 0, 0, 0, 0, 0, 0, 0, 314, 347, 288, 321, 316, 313, 289, 0, 83],
                                "name": "WTSI"
                            }, {
                                "data": [142, 143, 122, 0, 0, 0, 0, 4, 0, 0, 0, 129, 113, 5, 132, 50, 17, 106, 127, 133, 38, 102, 8, 31, 10, 21, 0, 0, 0, 125, 1, 0, 52, 45, 0, 113, 130, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "BCM"
                            }, {
                                "data": [150, 191, 218, 0, 0, 0, 0, 0, 0, 0, 0, 424, 184, 0, 455, 183, 111, 236, 415, 0, 0, 343, 0, 0, 0, 0, 0, 0, 0, 363, 227, 0, 196, 0, 57, 366, 269, 230, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "UC Davis"
                            }]
                        });
                    });

                </script>
            </div>

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
                        <td>433</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>733</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>1327</td>
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
                    Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                            categories: ["adipose tissue phenotype", "behavior/neurological phenotype", "cardiovascular system phenotype", "craniofacial phenotype", "growth/size/body region phenotype", "hematopoietic system phenotype", "homeostasis/metabolism phenotype", "immune system phenotype", "limbs/digits/tail phenotype", "skeleton phenotype", "vision/eye phenotype", "digestive/alimentary phenotype", "embryogenesis phenotype", "hearing/vestibular/ear phenotype", "integument phenotype", "mortality/aging", "muscle phenotype", "nervous system phenotype", "pigmentation phenotype", "renal/urinary system phenotype", "respiratory system phenotype", "liver/biliary system phenotype", "reproductive system phenotype"],
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
                            "data": [4, 17, 1, 2, 22, 16, 20, 9, 3, 13, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "hemizygote"
                        }, {
                            "data": [122, 578, 114, 21, 598, 525, 802, 288, 92, 414, 272, 3, 1, 47, 62, 1, 2, 76, 47, 9, 32, 0, 0],
                            "name": "heterozygote"
                        }, {
                            "data": [420, 2065, 297, 117, 1987, 1616, 2065, 871, 414, 1549, 494, 8, 39, 302, 214, 1881, 7, 175, 152, 19, 23, 1, 157],
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
			          		<script type="text/javascript">$(function () { $('#genotypeStatusChart').highcharts({ chart: {type: 'column' }, title: {text: 'Genotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Micro-injection in progress', 107], ['Chimeras obtained', 54], ['Genotype confirmed', 2484], ['Cre Excision Started', 55], ['Cre Excision Complete', 2779], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
                        </div>
                    </div>

                    <div class="col-6">
			         	<div id="phenotypeStatusChart">
			          		<script type="text/javascript">$(function () { $('#phenotypeStatusChart').highcharts({ chart: {type: 'column' }, title: {text: 'Phenotyping Status'}, credits: { enabled: false },   xAxis: { type: 'category', labels: { rotation: -90, style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'} } }, yAxis: { min: 0, title: { text: 'Number of genes' } }, legend: { enabled: false }, tooltip: { pointFormat: '<b>{point.y}</b>' }, series: [{ name: 'Population',  data: [['Phenotype Attempt Registered', 1112], ['Phenotyping Started', 669], ['Phenotyping Complete', 2608], ], dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: 'Verdana, sans-serif' } } }] }); });</script>
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
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                            series: [{"data": [0, 1, 124, 0, 670], "name": "JAX"}, {
                                "data": [0, 0, 42, 0, 262],
                                "name": "TCP"
                            }, {"data": [7, 0, 5, 1, 10], "name": "IMG"}, {
                                "data": [3, 0, 33, 0, 3],
                                "name": "Monterotondo"
                            }, {"data": [0, 0, 1, 0, 0], "name": "VETMEDUNI"}, {
                                "data": [71, 23, 104, 18, 349],
                                "name": "Harwell"
                            }, {"data": [1, 6, 13, 0, 0], "name": "SEAT"}, {
                                "data": [0, 0, 185, 0, 58],
                                "name": "MARC"
                            }, {
                                "data": [0, 0, 3, 0, 0],
                                "name": "INFRAFRONTIER-VETMEDUNI"
                            }, {"data": [0, 0, 154, 15, 280], "name": "BCM"}, {
                                "data": [0, 0, 1, 0, 0],
                                "name": "INFRAFRONTIER-CNB"
                            }, {"data": [0, 0, 0, 0, 3], "name": "CDTA"}, {
                                "data": [0, 0, 40, 19, 236],
                                "name": "HMGU"
                            }, {"data": [0, 8, 5, 0, 0], "name": "CAM-SU GRC"}, {
                                "data": [0, 0, 19, 0, 0],
                                "name": "CIPHE"
                            }, {"data": [0, 0, 1, 0, 0], "name": "INFRAFRONTIER-Oulu"}, {
                                "data": [18, 0, 5, 0, 0],
                                "name": "KMPC"
                            }, {"data": [0, 0, 380, 2, 638], "name": "UCD"}, {
                                "data": [2, 2, 106, 0, 96],
                                "name": "ICS"
                            }, {"data": [1, 0, 3, 0, 0], "name": "INFRAFRONTIER-IMG"}, {
                                "data": [4, 14, 1239, 14, 146],
                                "name": "WTSI"
                            }, {"data": [0, 0, 7, 0, 0], "name": "NARLabs"}, {
                                "data": [0, 0, 14, 0, 28],
                                "name": "RIKEN BRC"
                            }]
                        });
                    });
                    </script>
                </div>
            </div>

            <div class="col-6">
                <div id="phenotypeStatusByCenterChart">
                    <script type="text/javascript">$(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                            series: [{"data": [255, 150, 391], "name": "JAX"}, {
                                "data": [98, 22, 192],
                                "name": "TCP"
                            }, {"data": [5, 0, 0], "name": "IMG"}, {
                                "data": [3, 0, 0],
                                "name": "Monterotondo"
                            }, {"data": [89, 57, 347], "name": "Harwell"}, {
                                "data": [20, 72, 61],
                                "name": "MARC"
                            }, {"data": [148, 44, 120], "name": "BCM"}, {
                                "data": [1, 0, 0],
                                "name": "CDTA"
                            }, {"data": [128, 45, 125], "name": "HMGU"}, {
                                "data": [1, 0, 0],
                                "name": "CAM-SU GRC"
                            }, {"data": [1, 0, 0], "name": "CIPHE"}, {
                                "data": [112, 154, 411],
                                "name": "UCD"
                            }, {"data": [28, 9, 170], "name": "ICS"}, {
                                "data": [361, 146, 836],
                                "name": "WTSI"
                            }, {"data": [14, 11, 21], "name": "RIKEN BRC"}]
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
                    Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                            categories: ["Combined SHIRPA and Dysmorphology", "Open Field", "Auditory Brain Stem Response", "Indirect Calorimetry", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Heart Weight", "Immunophenotyping", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Hematology", "Insulin Blood Level", "Rotarod", "Gross Morphology Embryo E9.5", "Gross Morphology Embryo E12.5", "Gross Morphology Embryo E14.5-E15.5", "Gross Morphology Embryo E18.5", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Urinalysis", "Organ Weight", "Gross Morphology Placenta E12.5"],
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
                            "data": [57, 113, 23, 0, 232, 307, 82, 0, 54, 7, 20, 6, 2, 51, 250, 0, 32, 68, 2, 7, 2, 14, 19, 19, 4, 14, 135, 58, 152, 11, 1, 0, 0],
                            "name": "JAX"
                        }, {
                            "data": [10, 55, 7, 0, 29, 54, 0, 10, 4, 1, 18, 2, 0, 6, 56, 35, 2, 39, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "NING"
                        }, {
                            "data": [75, 148, 15, 2, 124, 273, 37, 0, 36, 10, 16, 3, 1, 13, 170, 118, 10, 144, 0, 0, 33, 15, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2],
                            "name": "TCP"
                        }, {
                            "data": [22, 115, 15, 4, 64, 14, 11, 15, 213, 13, 31, 3, 0, 22, 58, 14, 27, 56, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "HMGU"
                        }, {
                            "data": [192, 220, 35, 5, 244, 278, 18, 30, 84, 3, 54, 0, 0, 77, 228, 144, 49, 98, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 0],
                            "name": "MRC Harwell"
                        }, {
                            "data": [3, 2, 0, 3, 11, 38, 0, 0, 0, 4, 0, 4, 0, 4, 22, 25, 2, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "RBRC"
                        }, {
                            "data": [29, 62, 20, 2, 25, 35, 14, 9, 52, 2, 8, 8, 0, 4, 108, 23, 6, 25, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "ICS"
                        }, {
                            "data": [0, 0, 90, 2, 519, 380, 0, 0, 115, 19, 126, 11, 0, 41, 262, 369, 0, 194, 23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "WTSI"
                        }, {
                            "data": [37, 10, 54, 1, 3, 114, 15, 38, 35, 2, 23, 6, 4, 7, 116, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "BCM"
                        }, {
                            "data": [47, 52, 58, 1, 39, 157, 17, 0, 5, 14, 24, 12, 0, 42, 138, 69, 10, 60, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                <tr>
                    <td>UC Davis</td>
                    <td><a href="/data/page-retired">Browse</a></td>
                    <td>UCD_001</td>
                </tr>
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
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                                "data": [6368, 2405, 1748, 1326, 1208, 1133, 1357, 1204, 721, 859, 507, 252, 742, 488, 288, 387, 1116, 513, 453, 167, 165, 279, 387, 153, 369, 290, 100, 402, 320, 169, 814, 193, 254, 162, 167, 188, 74, 125, 125, 197, 132, 98, 129, 61, 28, 21, 15, 9, 0, 574697],
                                "name": "Fisher's exact test"
                            }]
                        });
                    });

                </script>
            </div>

            <div id="WilcoxonChart">
                <script type="text/javascript">
                    $(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                                "data": [3545, 4567, 4299, 4971, 4931, 5131, 5100, 5466, 5463, 5583, 5625, 5626, 5663, 5615, 6144, 6070, 5789, 6054, 5763, 6189, 5817, 6470, 5993, 6063, 5905, 5453, 5515, 5478, 5320, 5162, 5144, 4974, 5709, 4934, 4454, 4466, 4301, 4187, 4053, 4942, 5345, 5053, 5493, 4710, 4057, 2679, 2128, 1919, 2337, 3604],
                                "name": "Wilcoxon rank sum test with continuity correction"
                            }]
                        });
                    });

                </script>
            </div>

            <div id="MMglsChart">
                <script type="text/javascript">
                    $(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                                "data": [2761, 962, 681, 644, 468, 467, 424, 406, 383, 347, 420, 380, 391, 369, 365, 321, 322, 321, 315, 352, 362, 322, 296, 355, 313, 419, 292, 352, 406, 360, 381, 316, 349, 312, 324, 372, 349, 345, 432, 373, 378, 407, 423, 427, 408, 387, 367, 428, 447, 462],
                                "name": "Mixed Model framework, generalized least squares, equation withoutWeight"
                            }]
                        });
                    });

                </script>
            </div>

            <div id="MMlmeChart">
                <script type="text/javascript">
                    $(function () {
                        Highcharts.setOptions({colors: ['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)']});
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
                                "data": [20664, 6955, 5168, 4391, 3972, 3443, 2958, 2662, 2492, 2385, 2283, 2281, 2278, 2203, 2134, 2064, 2043, 2048, 1994, 2009, 2019, 1939, 1905, 1951, 1942, 1857, 1819, 1797, 1803, 1673, 1794, 1789, 1748, 1745, 1730, 1780, 1825, 1780, 1736, 1638, 1741, 1735, 1729, 1723, 1700, 1653, 1767, 1802, 1752, 1779],
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
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2"],
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
                            "data": [294, 470, 518, 1458, 1468, 1468, 1466, 1466, 2218, 2432],
                            "name": "Phenotyped genes",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " genes"
                            },
                            "type": "column"
                        }, {
                            "data": [301, 484, 535, 1528, 1540, 1540, 1537, 1537, 2340, 2577],
                            "name": "Phenotyped lines",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " lines"
                            },
                            "type": "column"
                        }, {
                            "yAxis": 1,
                            "data": [1069, 2732, 2182, 6114, 5974, 6064, 6278, 6382, 12159, 13008],
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
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2"],
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
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 756, 756],
                            "name": "Categorical (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068, 3956543, 3956543, 5827451, 6431595],
                            "name": "Categorical (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [388, 1214, 1764, 3426, 3426, 3426, 3426, 3426, 4544, 4776],
                            "name": "Categorical (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "Image record (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 5623, 10765, 106682, 107059, 107059, 107044, 107044, 186464, 208924],
                            "name": "Image record (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "Image record (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 1721, 18591, 18591, 18591, 18591, 18591, 13970, 14643],
                            "name": "Text (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [2387, 12803, 15355, 51283, 51611, 51611, 51597, 51597, 81962, 89172],
                            "name": "Text (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 100, 161, 161, 161, 161, 161, 15690, 17178],
                            "name": "Text (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 13176, 4, 9, 9, 9, 9, 9, 116, 128],
                            "name": "Time series (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471, 7383933, 7383933, 8721563, 9138387],
                            "name": "Time series (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [83, 83, 39, 82, 77684, 77684, 77684, 77684, 81063, 80413],
                            "name": "Time series (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [172, 1443, 992, 3724, 3726, 3726, 3726, 3726, 7342, 7553],
                            "name": "Unidimensional (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851, 2981261, 2981261, 4399848, 4797248],
                            "name": "Unidimensional (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [1454, 5286, 16928, 38737, 39065, 39065, 39065, 39065, 105213, 113998],
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
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2", "3.3", "3.4", "4.0", "4.2"],
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
                            "data": [73, 116, 93, 241, 243, 243, 243, 243, 452, 480],
                            "name": "adipose tissue phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [74, 377, 394, 636, 624, 716, 716, 716, 2254, 1916],
                            "name": "behavior/neurological phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [12, 26, 44, 296, 261, 261, 261, 261, 379, 366],
                            "name": "cardiovascular system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 4, 24, 56, 55, 55, 55, 55, 77, 87],
                            "name": "craniofacial phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 2, 5],
                            "name": "digestive/alimentary phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 33, 40],
                            "name": "embryogenesis phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [289, 473, 561, 533, 536, 536, 536, 536, 982, 1053],
                            "name": "growth/size/body region phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [84, 125, 237, 245, 245, 245, 245, 245, 331, 349],
                            "name": "hearing/vestibular/ear phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [131, 366, 495, 704, 690, 692, 694, 694, 775, 792],
                            "name": "hematopoietic system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [342, 1046, 1370, 1368, 1389, 1385, 1383, 1383, 1576, 1703],
                            "name": "homeostasis/metabolism phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [18, 69, 81, 183, 169, 172, 174, 174, 214, 214],
                            "name": "immune system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 19, 55, 79, 77, 77, 77, 77, 145, 189],
                            "name": "integument phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 26, 132, 204, 205, 205, 205, 205, 358, 426],
                            "name": "limbs/digits/tail phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 0, 0, 0, 0, 0, 1, 1],
                            "name": "liver/biliary system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 80, 858, 906, 906, 1122, 1346, 1670, 1880],
                            "name": "mortality/aging",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 5, 7, 7, 7, 7, 9, 9],
                            "name": "muscle phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 42, 79, 73, 66, 66, 66, 66, 178, 183],
                            "name": "nervous system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [10, 22, 51, 88, 80, 80, 80, 80, 143, 170],
                            "name": "pigmentation phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 8, 27, 27, 27, 27, 27, 26, 25],
                            "name": "renal/urinary system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 3, 43, 43, 43, 43, 86, 126, 152],
                            "name": "reproductive system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 1, 1, 1, 1, 1, 2, 3],
                            "name": "respiratory system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [101, 162, 223, 714, 718, 718, 718, 718, 1352, 1455],
                            "name": "skeleton phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [70, 79, 127, 521, 316, 316, 316, 316, 592, 611],
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
