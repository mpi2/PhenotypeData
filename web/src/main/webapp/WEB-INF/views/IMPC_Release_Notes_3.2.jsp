<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
    <div class="col-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 3.2</li>
                <li>Published:&nbsp;16 June 2015</li>
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
                <li>Number of phenotyped genes:&nbsp;1,468</li>
                <li>Number of phenotyped mutant lines:&nbsp;1,540</li>
                <li>Number of phenotype calls:&nbsp;6,064</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/3.2">ftp://ftp.ebi.ac.uk/pub/databases/impc/3.2</a>
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

        <h3>Data release 3.2</h3>

            <div class="well">
                <strong>Release notes:</strong>
                Represents a minor data release. Changes include:
                <ul>
                    <li>
                        More <a href="/data/secondaryproject/3i">3I</a> lines, bringing
                        the number of 3I datapoints to 1,118,939
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
                        <td>MRC Harwell</td>
                        <td>186</td>
                        <td>2,520</td>
                        <td>4,191</td>
                    </tr>
                    <tr>
                        <td>RBRC</td>
                        <td>13</td>
                        <td>523</td>
                        <td>244</td>
                    </tr>
                    <tr>
                        <td>NING</td>
                        <td>13</td>
                        <td>142</td>
                        <td>196</td>
                    </tr>
                    <tr>
                        <td>HMGU</td>
                        <td>86</td>
                        <td>818</td>
                        <td>1,429</td>
                    </tr>
                    <tr>
                        <td>ICS</td>
                        <td>65</td>
                        <td>675</td>
                        <td>1,007</td>
                    </tr>
                    <tr>
                        <td>WTSI</td>
                        <td>434</td>
                        <td>1,751</td>
                        <td>6,380</td>
                    </tr>
                    <tr>
                        <td>JAX</td>
                        <td>232</td>
                        <td>1,322</td>
                        <td>4,675</td>
                    </tr>
                    <tr>
                        <td>UC Davis</td>
                        <td>273</td>
                        <td>883</td>
                        <td>6,138</td>
                    </tr>
                    <tr>
                        <td>TCP</td>
                        <td>166</td>
                        <td>571</td>
                        <td>2,917</td>
                    </tr>
                    <tr>
                        <td>BCM</td>
                        <td>72</td>
                        <td>435</td>
                        <td>1,487</td>
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
                            3,956,068
                        </td>
                        <td>
                            0
                        </td>
                        <td>
                            3,426
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>
                            2,970,851
                        </td>
                        <td>
                            3,726
                            <sup>*</sup>
                        </td>
                        <td>
                            39,065
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>time series</td>
                        <td>
                            7,415,471
                        </td>
                        <td>
                            9
                            <sup>*</sup>
                        </td>
                        <td>
                            77,684
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>
                            51,611
                        </td>
                        <td>
                            18,591
                            <sup>*</sup>
                        </td>
                        <td>
                            161
                            <sup>*</sup>
                        </td>
                    </tr>
                    <tr>
                        <td>image record</td>
                        <td>
                            107,059
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
                                categories: ["Acoustic Startle and Pre-pulse Inhibition (PPI)", "Rotarod", "FACS", "Organ Weight", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Challenge Whole Body Plethysmography", "Combined SHIRPA and Dysmorphology", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Open Field", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Anti-nuclear antibody assay", "Buffy coat peripheral blood leukocyte immunophenotyping", "Bone marrow immunophenotyping", "Ear epidermis immunophenotyping", "Spleen Immunophenotyping", "Mesenteric Lymph Node Immunophenotyping", "Whole blood peripheral blood leukocyte immunophenotyping"],
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
                                "data": [13, 0, 0, 0, 0, 0, 0, 13, 0, 4, 13, 0, 13, 0, 11, 10, 0, 0, 11, 13, 11, 0, 10, 0, 0, 12, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "NING"
                            }, {
                                "data": [0, 186, 0, 0, 0, 0, 0, 214, 25, 84, 220, 0, 183, 0, 204, 0, 173, 0, 178, 0, 61, 118, 170, 0, 73, 202, 2, 8, 0, 167, 67, 10, 209, 214, 190, 142, 208, 0, 0, 0, 0, 0, 0, 0],
                                "name": "JAX"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 88, 23, 117, 165, 125, 102, 38, 119, 0, 112, 0, 111, 0, 104, 48, 101, 0, 0, 113, 0, 114, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "TCP"
                            }, {
                                "data": [0, 63, 0, 0, 0, 0, 0, 47, 64, 0, 78, 68, 67, 0, 67, 58, 68, 78, 62, 9, 66, 0, 46, 0, 42, 70, 0, 58, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "HMGU"
                            }, {
                                "data": [182, 0, 87, 166, 0, 0, 0, 51, 75, 0, 186, 129, 165, 0, 185, 173, 131, 137, 169, 184, 165, 0, 0, 0, 0, 169, 0, 23, 166, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "MRC Harwell"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 9, 9, 13, 13, 6, 8, 0, 1, 2, 7, 0, 13, 1, 12, 0, 13, 8, 0, 13, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "RBRC"
                            }, {
                                "data": [34, 57, 0, 0, 38, 54, 40, 51, 0, 0, 65, 59, 29, 0, 16, 62, 62, 63, 57, 0, 57, 0, 63, 0, 0, 62, 0, 0, 62, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "ICS"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 381, 0, 0, 388, 376, 388, 0, 390, 388, 0, 0, 390, 388, 394, 0, 295, 0, 365, 385, 0, 0, 387, 0, 0, 0, 0, 0, 0, 0, 0, 155, 345, 123, 155, 151, 148, 123],
                                "name": "WTSI"
                            }, {
                                "data": [6, 0, 0, 0, 0, 0, 0, 57, 9, 0, 67, 60, 16, 4, 69, 41, 54, 48, 49, 65, 15, 0, 42, 11, 0, 59, 0, 46, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                                "name": "BCM"
                            }, {
                                "data": [0, 0, 0, 0, 0, 0, 0, 230, 184, 0, 234, 0, 181, 0, 262, 183, 241, 0, 226, 0, 173, 0, 220, 0, 111, 236, 0, 0, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
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
                        <td>2</td>
                        <td>5</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>d</td>
                        <td>1</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>420</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>e</td>
                        <td>25</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>779</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>c</td>
                        <td>2</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>a</td>
                        <td>359</td>
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
                            categories: ["adipose tissue phenotype", "behavior/neurological phenotype", "cardiovascular system phenotype", "craniofacial phenotype", "growth/size/body region phenotype", "hearing/vestibular/ear phenotype", "hematopoietic system phenotype", "homeostasis/metabolism phenotype", "immune system phenotype", "limbs/digits/tail phenotype", "skeleton phenotype", "vision/eye phenotype", "integument phenotype", "mortality/aging", "muscle phenotype", "nervous system phenotype", "pigmentation phenotype", "renal/urinary system phenotype", "reproductive system phenotype", "respiratory system phenotype", "digestive/alimentary phenotype"],
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
                            "data": [2, 3, 1, 2, 18, 2, 15, 24, 8, 1, 10, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "hemizygote"
                        }, {
                            "data": [79, 267, 85, 9, 541, 38, 487, 722, 262, 53, 272, 118, 31, 1, 2, 40, 20, 12, 1, 32, 0],
                            "name": "heterozygote"
                        }, {
                            "data": [230, 1058, 219, 99, 1660, 205, 1438, 1881, 720, 252, 999, 360, 112, 908, 5, 95, 95, 15, 49, 21, 2],
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
                                        data: [['Micro-injection in progress', 17], ['Chimeras obtained', 84], ['Genotype confirmed', 2277], ['Cre Excision Started', 127], ['Cre Excision Complete', 2407],],
                                        dataLabels: {
                                            enabled: true,
                                            style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}
                                        }
                                    }]
                                });
                            });</script>
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
                                            data: [['Phenotype Attempt Registered', 1513], ['Phenotyping Started', 799], ['Phenotyping Complete', 1408],],
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
                            series: [{"data": [0, 5, 78, 0, 589], "name": "JAX"}, {
                                "data": [0, 12, 30, 0, 204],
                                "name": "TCP"
                            }, {"data": [0, 0, 5, 0, 9], "name": "IMG"}, {
                                "data": [0, 0, 42, 0, 6],
                                "name": "Monterotondo"
                            }, {"data": [1, 49, 89, 59, 292], "name": "Harwell"}, {
                                "data": [1, 2, 7, 0, 0],
                                "name": "SEAT"
                            }, {"data": [0, 0, 139, 0, 47], "name": "MARC"}, {
                                "data": [0, 0, 2, 0, 0],
                                "name": "INFRAFRONTIER-VETMEDUNI"
                            }, {"data": [0, 0, 174, 2, 278], "name": "BCM"}, {
                                "data": [0, 0, 1, 0, 0],
                                "name": "INFRAFRONTIER-CNB"
                            }, {"data": [0, 0, 0, 0, 1], "name": "CDTA"}, {
                                "data": [0, 3, 72, 1, 201],
                                "name": "HMGU"
                            }, {"data": [0, 1, 11, 0, 0], "name": "CIPHE"}, {
                                "data": [0, 0, 1, 0, 0],
                                "name": "INFRAFRONTIER-Oulu"
                            }, {"data": [15, 0, 403, 6, 504], "name": "UCD"}, {
                                "data": [0, 4, 101, 0, 91],
                                "name": "ICS"
                            }, {"data": [0, 0, 2, 0, 0], "name": "INFRAFRONTIER-IMG"}, {
                                "data": [0, 1, 1106, 16, 156],
                                "name": "WTSI"
                            }, {"data": [0, 0, 1, 0, 0], "name": "NARLabs"}, {
                                "data": [0, 7, 13, 0, 29],
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
                            series: [{"data": [306, 149, 244], "name": "JAX"}, {
                                "data": [93, 47, 137],
                                "name": "TCP"
                            }, {"data": [2, 0, 0], "name": "IMG"}, {
                                "data": [6, 0, 0],
                                "name": "Monterotondo"
                            }, {"data": [145, 76, 184], "name": "Harwell"}, {
                                "data": [17, 61, 6],
                                "name": "MARC"
                            }, {"data": [175, 58, 65], "name": "BCM"}, {
                                "data": [98, 32, 82],
                                "name": "HMGU"
                            }, {"data": [1, 0, 0], "name": "CIPHE"}, {
                                "data": [126, 160, 266],
                                "name": "UCD"
                            }, {"data": [24, 25, 59], "name": "ICS"}, {
                                "data": [638, 206, 401],
                                "name": "WTSI"
                            }, {"data": [25, 12, 12], "name": "RIKEN BRC"}]
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
                            categories: ["Auditory Brain Stem Response", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Hematology", "Heart Weight", "Immunophenotyping", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Indirect Calorimetry", "Combined SHIRPA and Dysmorphology", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Insulin Blood Level", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Rotarod", "Sleep Wake", "Tail Suspension", "Organ Weight"],
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
                            "data": [14, 172, 0, 86, 0, 19, 1, 0, 15, 12, 0, 60, 168, 0, 0, 36, 0, 10, 10, 12, 100, 94, 13, 92, 12, 0],
                            "name": "JAX"
                        }, {
                            "data": [0, 10, 18, 0, 0, 0, 0, 7, 2, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "NING"
                        }, {
                            "data": [11, 85, 0, 21, 0, 28, 5, 0, 108, 6, 0, 7, 154, 75, 7, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "TCP"
                        }, {
                            "data": [7, 68, 12, 8, 10, 49, 13, 0, 50, 0, 0, 13, 40, 8, 8, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "HMGU"
                        }, {
                            "data": [16, 159, 204, 16, 21, 57, 3, 51, 97, 0, 0, 46, 134, 118, 3, 143, 99, 0, 0, 0, 0, 0, 0, 0, 0, 58],
                            "name": "MRC Harwell"
                        }, {
                            "data": [7, 12, 0, 6, 0, 1, 3, 0, 26, 0, 0, 0, 0, 14, 0, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "RBRC"
                        }, {
                            "data": [15, 20, 26, 10, 4, 50, 1, 0, 22, 7, 0, 4, 68, 8, 6, 2, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "ICS"
                        }, {
                            "data": [93, 360, 284, 0, 0, 84, 6, 81, 161, 7, 0, 24, 172, 187, 10, 0, 0, 26, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "WTSI"
                        }, {
                            "data": [25, 2, 56, 33, 8, 23, 3, 7, 5, 2, 2, 3, 78, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                            "name": "BCM"
                        }, {
                            "data": [25, 114, 211, 23, 0, 5, 8, 0, 58, 7, 0, 28, 92, 12, 0, 59, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0],
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
                <tr><td>MRC Harwell</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>MRC Harwell</td><td><a href="/data/page-retired">Browse</a></td><td>HRWL_001</td></tr>
                <tr><td>RBRC</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>ICS</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
                <tr><td>ICS</td><td><a href="/data/page-retired">Browse</a></td><td>ICS_001</td></tr>
                <tr><td>WTSI</td><td><a href="/data/page-retired">Browse</a></td><td>MGP_001</td></tr>
                <tr><td>BCM</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
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
                                "data": [4439, 1596, 1178, 1094, 871, 1489, 1202, 1294, 641, 539, 715, 355, 974, 624, 273, 360, 2248, 538, 408, 412, 503, 273, 180, 259, 432, 413, 274, 112, 361, 270, 690, 160, 277, 167, 124, 100, 64, 102, 48, 137, 43, 40, 41, 90, 42, 18, 7, 3, 0, 429757],
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
                                "data": [1896, 1539, 1525, 1475, 1794, 1867, 1933, 1957, 1928, 2041, 1991, 2027, 2008, 2101, 2113, 2079, 2096, 2182, 2119, 2217, 2089, 2099, 2051, 2025, 1981, 1975, 1937, 1932, 1867, 1845, 1776, 1745, 1695, 1675, 1579, 1531, 1622, 1592, 1470, 2018, 1774, 2463, 1902, 1663, 1575, 1572, 2281, 857, 767, 911],
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
                                "data": [2591, 824, 596, 537, 470, 409, 346, 315, 271, 261, 340, 317, 294, 279, 287, 262, 260, 302, 289, 219, 245, 237, 227, 246, 234, 230, 218, 236, 235, 202, 228, 239, 190, 207, 196, 239, 201, 190, 211, 255, 234, 177, 163, 207, 224, 240, 193, 210, 175, 220],
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
                                "data": [13380, 4502, 3546, 2878, 2518, 2231, 1967, 1686, 1662, 1632, 1635, 1503, 1457, 1420, 1459, 1369, 1345, 1308, 1373, 1253, 1277, 1251, 1301, 1306, 1227, 1240, 1218, 1174, 1229, 1287, 1163, 1217, 1143, 1204, 1168, 1138, 1157, 1210, 1173, 1135, 1194, 1068, 1184, 1144, 1106, 1140, 1195, 1148, 1079, 1114],
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
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2"],
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
                            "data": [294, 470, 518, 1458, 1468, 1468],
                            "name": "Phenotyped genes",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " genes"
                            },
                            "type": "column"
                        }, {
                            "data": [301, 484, 535, 1528, 1540, 1540],
                            "name": "Phenotyped lines",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " lines"
                            },
                            "type": "column"
                        }, {
                            "yAxis": 1,
                            "data": [1069, 2732, 2182, 6114, 5974, 6064],
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
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2"],
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
                            "data": [0, 0, 0, 0, 0, 0],
                            "name": "Categorical (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [149194, 958957, 1173776, 3935293, 3956068, 3956068],
                            "name": "Categorical (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [388, 1214, 1764, 3426, 3426, 3426],
                            "name": "Categorical (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 0, 0, 0, 0],
                            "name": "Image record (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 5623, 10765, 106682, 107059, 107059],
                            "name": "Image record (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 0, 0, 0, 0],
                            "name": "Image record (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 1721, 18591, 18591, 18591],
                            "name": "Text (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [2387, 12803, 15355, 51283, 51611, 51611],
                            "name": "Text (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 0, 100, 161, 161, 161],
                            "name": "Text (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [0, 13176, 4, 9, 9, 9],
                            "name": "Time series (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [63773, 1451844, 1798263, 7237320, 7415471, 7415471],
                            "name": "Time series (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [83, 83, 39, 82, 77684, 77684],
                            "name": "Time series (issues)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [172, 1443, 992, 3724, 3726, 3726],
                            "name": "Unidimensional (QC failed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [381406, 1011637, 988022, 2782486, 2970851, 2970851],
                            "name": "Unidimensional (QC passed)",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            },
                            "type": "spline"
                        }, {
                            "data": [1454, 5286, 16928, 38737, 39065, 39065],
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
                            categories: ["1.0", "1.1", "2.0", "3.0", "3.1", "3.2"],
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
                            "data": [73, 116, 93, 241, 243, 243],
                            "name": "adipose tissue phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [74, 377, 394, 636, 624, 716],
                            "name": "behavior/neurological phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [12, 26, 44, 296, 261, 261],
                            "name": "cardiovascular system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 4, 24, 56, 55, 55],
                            "name": "craniofacial phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [289, 473, 561, 533, 536, 536],
                            "name": "growth/size/body phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [84, 125, 237, 245, 245, 245],
                            "name": "hearing/vestibular/ear phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [131, 366, 495, 704, 690, 692],
                            "name": "hematopoietic system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [342, 1046, 1370, 1368, 1389, 1385],
                            "name": "homeostasis/metabolism phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [18, 69, 81, 183, 169, 172],
                            "name": "immune system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 19, 55, 79, 77, 77],
                            "name": "integument phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 26, 132, 204, 205, 205],
                            "name": "limbs/digits/tail phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 80, 858, 906, 906],
                            "name": "mortality/aging",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 5, 7, 7],
                            "name": "muscle phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 42, 79, 73, 66, 66],
                            "name": "nervous system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [10, 22, 51, 88, 80, 80],
                            "name": "pigmentation phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 8, 27, 27, 27],
                            "name": "renal/urinary system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 3, 43, 43, 43],
                            "name": "reproductive system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [0, 0, 0, 1, 1, 1],
                            "name": "respiratory system phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [101, 162, 223, 714, 718, 718],
                            "name": "skeleton phenotype",
                            "tooltip": {
                                "pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>",
                                "valueSuffix": " "
                            }
                        }, {
                            "data": [70, 79, 127, 521, 316, 316],
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
