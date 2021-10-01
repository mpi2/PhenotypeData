<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="row">
    <div class="col-md-6">
        <div>
            <h4>IMPC</h4>
            <ul class="mt-0">
                <li>Release: 1.0</li>
                <li>Published:&nbsp;16 June 2014</li>
            </ul>
        </div>

        <div>
            <h4>Statistical Package</h4>
            <ul class="mt-0">
                <li>PhenStat</li>
                <li>Version:&nbsp;1.2.0</li>
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
                <li>Number of phenotyped genes:&nbsp;294</li>
                <li>Number of phenotyped mutant lines:&nbsp;301</li>
                <li>Number of phenotype calls:&nbsp;1069</li>
            </ul>
        </div>

        <div>
            <h4>Data access</h4>
            <ul class="mt-0">
                <li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-1.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-1.0</a></li>
                <li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">
        <h2 id="new-features">Highlights</h2>
        <div class="col-12">
            <h3>Data release 1.0</h3>

            <div class="well">
                <strong>Release notes:</strong>
               Represents the first IMPC data release.

                <h3 class="mt-4">Phenotype Association Versioning</h3>
                <p>Many factors contribute to the identification of phenodeviants by statistical analysis. This includes
                    the number of mutant and baseline mice, the statistical test used, the selected thresholds and
                    changes to the underlying software that runs the analysis. For these reasons, we will be versioning
                    genotype-to-phenotype associations from data release to data release. A given genotype-to-phenotype
                    may change from release to release.</p>

                <h3>Statistical Tests</h3>
                <p>In general, we are applying a Fisher Exact Test for categorical data and linear regression for
                    continuous data. In cases where there is no variability in values for a data parameter in a control
                    or mutant mouse group, a rank sum test is applied instead of a linear regression. The statistical
                    test used is always noted when displayed on the portal or when obtained by the API. Documentation on
                    statistical analysis is available here:
                    <a href="${baseUrl}/page-retired">Statistics help</a>
                </p>

                <h3>P-value threshold</h3>
                <p>In this first release, we are using a p value threshold of &le; 1 x10-4 for all statistical tests to
                    make a phenotype call. This threshold may be adjusted for some parameters upon further review by
                    statistical experts.</p>

                <h3>Clinical Blood Chemistry and Hematology</h3>
                <p>Review of PhenStat calls for clinical blood chemistry and hematology by phenotypers at WTSI suggest
                    our current analysis maybe giving a high false positive rate. Alternative statistical approaches are
                    being considered. We suggest looking at the underlying data that supports a phenotype association if
                    it's critical to your research.</p>

            </div>
        </div>
    </div>
</div>

<div class="row mt-5">
	<div class="col-12">
		<h2 class="title" id="data_reports">Data Reports</h2>
		<div class="col-12">

			<h3>Lines and Specimens</h3>

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
                        <td>WTSI</td>
                        <td>301</td>
                        <td>1469</td>
                        <td>4463</td>
                    </tr>
                </tbody>
            </table>

            <h3>Experimental Data and Quality Checks</h3>

            <table id="exp_data">
                <thead>
                    <tr>
                        <th class="headerSort">Data Type</th>
                        <th class="headerSort">QC Status</th>
                        <th class="headerSort">Data Points</th>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>unidimensional</td>
                        <td>QC_passed</td>
                        <td>381406</td>
                    </tr>
                    <tr>
                        <td>time_series</td>
                        <td>QC_passed</td>
                        <td>63773</td>
                    </tr>
                    <tr>
                        <td>text</td>
                        <td>QC_passed</td>
                        <td>2387</td>
                    </tr>
                    <tr>
                        <td>categorical</td>
                        <td>QC_passed</td>
                        <td>149194</td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>QC_failed</td>
                        <td>172<sup>*</sup></td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>issues</td>
                        <td>1454<sup>*</sup></td>
                    </tr>
                    <tr>
                        <td>time_series</td>
                        <td>issues</td>
                        <td>83<sup>*</sup></td>
                    </tr>
                    <tr>
                        <td>categorical</td>
                        <td>issues</td>
                        <td>388<sup>*</sup></td>
                    </tr>
                </tbody>
            </table>

            <p><sup>*</sup>&nbsp;Excluded from statistical analysis.</p>

            <h3>Procedures</h3>

            <div id="lineProcedureChart">
                <script type="text/javascript">
                    $(function () {
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
                                categories: ["Auditory Brain Stem Response", "Body Weight", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Eye Morphology", "Grip Strength", "Hematology", "Heart Weight", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)"],
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
                                    '<td style="padding:0"><b>{point.y:.1f} lines</b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{"name": "WTSI", "data": [295, 301, 301, 301, 301, 301, 301, 268, 280, 301]}]
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
                        <td>2</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>1</td>
                        <td>10</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>e</td>
                        <td>13</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>b</td>
                        <td>16</td>
                    </tr>
                    <tr>
                        <td>Targeted Mutation</td>
                        <td>a</td>
                        <td>259</td>
                    </tr>
                </tbody>
            </table>
            <p>Mouse knockout programs:&nbsp;EUCOMM,KOMP</p>
        </div>
    </div>
</div>

<div class="row mt-5">
    <div class="col-12">

        <h2>Phenotype Associations</h2>

        <div id="callProcedureChart">
            <script type="text/javascript">
                $(function () {
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
                            categories: ["Auditory Brain Stem Response", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Eye Morphology", "Grip Strength", "Hematology", "Heart Weight", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)"],
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
                                '<td style="padding:0"><b>{point.y:.1f} calls</b></td></tr>',
                            footerFormat: '</table>',
                            shared: true,
                            useHTML: true
                        },
                        plotOptions: {
                            column: {
                                pointPadding: 0.2,
                                borderWidth: 0
                            }
                        },
                        series: [{"name": "WTSI", "data": [96, 320, 258, 70, 83, 131, 54, 21, 36]}]
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
                <tr><td>WTSI</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>MGP_001</td></tr>
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
                        <td>MM framework, generalized least squares, equation withoutWeight</td>
                    </tr>
                    <tr>
                        <td>unidimensional</td>
                        <td>MM framework, linear mixed-effects model, equation withoutWeight</td>
                    </tr>
                </tbody>
            </table>

            <h3>P-value distributions</h3>

            <div id="FisherChart">
                <script type="text/javascript">
                    $(function () {
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
                                    '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "name": "Fisher's exact test",
                                "data": [379, 187, 136, 122, 69, 183, 100, 109, 43, 18, 37, 85, 18, 148, 78, 32, 5, 76, 0, 28, 26, 1, 1, 40, 65, 2, 3, 70, 3, 0, 6, 0, 60, 70, 8, 6, 2, 50, 4, 0, 50, 2, 0, 52, 0, 0, 0, 0, 0, 14018]
                            }]
                        });
                    });

                </script>
            </div>

            <div id="WilcoxonChart">
                <script type="text/javascript">
                    $(function () {
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
                                    '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "name": "Wilcoxon rank sum test with continuity correction",
                                "data": [36, 52, 40, 49, 81, 74, 76, 70, 64, 64, 78, 70, 71, 82, 80, 77, 71, 71, 77, 74, 68, 72, 78, 75, 67, 68, 66, 64, 61, 62, 54, 55, 53, 54, 52, 53, 57, 49, 50, 43, 39, 31, 29, 29, 23, 25, 22, 18, 9, 10]
                            }]
                        });
                    });

                </script>
            </div>

            <div id="MMglsChart">
                <script type="text/javascript">
                    $(function () {
                        $('#MMglsChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
                            },
                            title: {
                                text: 'P-value distribution'
                            },
                            subtitle: {
                                text: "MM framework, generalized least squares, equation withoutWeight"
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
                                    '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "name": "MM framework, generalized least squares, equation withoutWeight",
                                "data": [113, 39, 30, 42, 21, 26, 15, 26, 22, 40, 22, 22, 15, 39, 24, 10, 14, 16, 30, 14, 30, 30, 6, 16, 23, 21, 11, 16, 31, 1, 28, 26, 10, 12, 24, 22, 8, 9, 32, 24, 22, 15, 13, 10, 6, 41, 16, 16, 14, 4]
                            }]
                        });
                    });

                </script>
            </div>

            <div id="MMlmeChart">
                <script type="text/javascript">
                    $(function () {
                        $('#MMlmeChart').highcharts({
                            chart: {
                                type: 'column',
                                height: 800
                            },
                            title: {
                                text: 'P-value distribution'
                            },
                            subtitle: {
                                text: "MM framework, linear mixed-effects model, equation withoutWeight"
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
                                    '<td style="padding:0"><b>{point.y:.1f} </b></td></tr>',
                                footerFormat: '</table>',
                                shared: true,
                                useHTML: true
                            },
                            plotOptions: {
                                column: {
                                    pointPadding: 0.2,
                                    borderWidth: 0
                                }
                            },
                            series: [{
                                "name": "MM framework, linear mixed-effects model, equation withoutWeight",
                                "data": [3047, 992, 792, 621, 528, 513, 406, 399, 347, 338, 326, 304, 318, 292, 311, 268, 263, 305, 307, 252, 253, 242, 268, 245, 234, 230, 244, 235, 257, 265, 229, 209, 206, 220, 254, 218, 258, 240, 208, 203, 240, 232, 248, 224, 216, 232, 209, 209, 246, 235]
                            }]
                        });
                    });

                </script>
            </div>
        </div>
    </div>
</div>
