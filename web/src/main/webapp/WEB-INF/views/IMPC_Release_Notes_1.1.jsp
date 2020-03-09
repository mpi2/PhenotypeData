<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
	<div class="col-6">
		<div>
			<h4>IMPC</h4>
			<ul class="mt-0">
				<li>Release:&nbsp;1.1</li>
				<li>Published:&nbsp;26 June 2014</li>
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
				<li>Version:&nbspGRCm38</li>
			</ul>
		</div>
	</div>

	<div class="col-6">
		<div>
			<h4>Summary</h4>
			<ul class="mt-0">
				<li>Number of phenotyped genes:&nbsp;470</li>
				<li>Number of phenotyped mutant lines:&nbsp;484</li>
				<li>Number of phenotype calls:&nbsp;2,732</li>
			</ul>
		</div>

		<div>
			<h4>Data access</h4>
			<ul class="mt-0">
				<li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/latest">ftp://ftp.ebi.ac.uk/pub/databases/impc/latest</a>
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

			<h3>Data release 1.1</h3>

			<div class="well">
				<strong>Release notes:</strong>
				Represents a major data release.

				<h3 class="mt-4">Phenotype Association Versioning</h3>
				<p>Many factors contribute to the identification of phenodeviants by statistical analysis. This includes the number of mutant and baseline mice, the statistical test used, the selected thresholds and changes to the underlying software that runs the analysis. For these reasons, we will be versioning genotype-to-phenotype associations from data release to data release. A given genotype-to-phenotype may change from release to release.</p>

				<h3>Statistical Tests</h3>
				<p>In general, we are applying a Fisher Exact Test for categorical data and linear regression for continuous data.  In cases where there is no variability in values for a data parameter in a control or mutant mouse group, a rank sum test is applied instead of a linear regression. The statistical test used is always noted when displayed on the portal or when obtained by the API. Documentation on statistical analysis is available here:
				<a href="http://www.mousephenotype.org/data/documentation/statistics-help">http://www.mousephenotype.org/data/documentation/statistics-help</a></p>

				<h3>P-value threshold</h3>
				<p>In this first release, we are using a p value threshold of &le; 1 x10-4 for all statistical tests to make a phenotype call.  This threshold may be adjusted for some parameters upon further review by statistical experts.</p>

				<h3>Clinical Blood Chemistry and Hematology</h3>
				<p>Review of PhenStat calls for clinical blood chemistry and hematology by phenotypers at WTSI suggest our current analysis maybe giving a high false positive rate. Alternative statistical approaches are being considered. We suggest looking at the underlying data that supports a phenotype association if it's critical to your research.</p>
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
						<td>47</td>
						<td>1,945</td>
						<td>1,122</td>
					</tr>
					<tr>
						<td>HMGU</td>
						<td>13</td>
						<td>365</td>
						<td>194</td>
					</tr>
					<tr>
						<td>ICS</td>
						<td>15</td>
						<td>375</td>
						<td>234</td>
					</tr>
					<tr>
						<td>WTSI</td>
						<td>301</td>
						<td>1,469</td>
						<td>4,463</td>
					</tr>
					<tr>
						<td>JAX</td>
						<td>18</td>
						<td>1,004</td>
						<td>514</td>
					</tr>
					<tr>
						<td>UC Davis</td>
						<td>37</td>
						<td>1,018</td>
						<td>807</td>
					</tr>
					<tr>
						<td>TCP</td>
						<td>49</td>
						<td>288</td>
						<td>987</td>
					</tr>
					<tr>
						<td>BCM</td>
						<td>5</td>
						<td>224</td>
						<td>121</td>
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
							958,957
						</td>
						<td>
							0
						</td>
						<td>
							1,214
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>unidimensional</td>
						<td>
							1,011,637
						</td>
						<td>
							1,443
							<sup>*</sup>
						</td>
						<td>
							5,286
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>time series</td>
						<td>
							1,451,844
						</td>
						<td>
							13,176
							<sup>*</sup>
						</td>
						<td>
							83
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>text</td>
						<td>
							12,803
						</td>
						<td>
							0
						</td>
						<td>
							0
						</td>
					</tr>
					<tr>
						<td>image record</td>
						<td>
							5,623
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
								categories: ["Rotarod", "Auditory Brain Stem Response", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Combined SHIRPA and Dysmorphology", "Body Composition (DEXA lean/fat)", "Eye Morphology", "Grip Strength", "Hematology", "Heart Weight", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry"],
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
							series: [{
								"name": "MRC Harwell",
								"data": [0, 0, 47, 47, 46, 47, 47, 47, 0, 47, 47, 0, 0, 47, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "HMGU",
								"data": [13, 5, 0, 13, 13, 13, 9, 13, 13, 7, 13, 0, 0, 13, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "ICS",
								"data": [0, 12, 1, 0, 15, 15, 15, 15, 0, 0, 13, 15, 0, 15, 15, 0, 0, 0, 0, 0]
							}, {
								"name": "WTSI",
								"data": [0, 295, 0, 301, 0, 301, 0, 301, 301, 301, 301, 268, 280, 301, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "JAX",
								"data": [0, 18, 0, 18, 0, 18, 18, 0, 18, 0, 18, 18, 0, 18, 0, 18, 18, 18, 18, 18]
							}, {
								"name": "UC Davis",
								"data": [0, 36, 0, 36, 0, 0, 37, 37, 37, 0, 34, 36, 0, 36, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "TCP",
								"data": [0, 0, 0, 49, 18, 27, 49, 0, 49, 0, 49, 49, 0, 49, 49, 0, 0, 0, 0, 0]
							}, {"name": "BCM", "data": [0, 5, 2, 5, 5, 0, 0, 0, 5, 5, 0, 5, 0, 5, 0, 0, 0, 0, 0, 0]}]
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
						<td>63</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>e</td>
						<td>17</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>b</td>
						<td>139</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>a</td>
						<td>263</td>
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
							categories: ["Indirect Calorimetry", "Intraperitoneal glucose tolerance test (IPGTT)", "Auditory Brain Stem Response", "Clinical Blood Chemistry", "Combined SHIRPA and Dysmorphology", "Body Composition (DEXA lean/fat)", "Eye Morphology", "Hematology", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Heart Weight", "X-ray", "Electroconvulsive Threshold Testing", "Light-Dark Test", "Plasma Chemistry", "Grip Strength", "Insulin Blood Level"],
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
						series: [{
							"name": "MRC Harwell",
							"data": [10, 18, 0, 217, 39, 75, 0, 72, 189, 0, 0, 0, 0, 0, 27, 0]
						}, {"name": "HMGU", "data": [0, 0, 4, 79, 4, 2, 2, 16, 0, 0, 0, 0, 0, 0, 0, 0]}, {
							"name": "ICS",
							"data": [1, 2, 16, 25, 8, 25, 0, 2, 41, 2, 1, 0, 0, 0, 0, 0]
						}, {
							"name": "WTSI",
							"data": [0, 36, 96, 319, 0, 258, 70, 131, 0, 54, 0, 0, 0, 0, 112, 21]
						}, {
							"name": "JAX",
							"data": [0, 6, 9, 120, 2, 0, 0, 42, 0, 4, 0, 2, 9, 20, 0, 0]
						}, {
							"name": "UC Davis",
							"data": [0, 8, 10, 0, 40, 34, 3, 11, 0, 26, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "TCP",
							"data": [22, 2, 0, 240, 30, 0, 4, 24, 0, 7, 12, 0, 0, 0, 0, 0]
						}, {"name": "BCM", "data": [1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}]
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
				<tr><td>JAX</td><td><a href="/data/page-retired">Browse</a></td><td>JAX_001</td></tr>
				<tr><td>TCP</td><td><a href="/data/page-retired">Browse</a></td><td>TCP_001</td></tr>
				<tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>IMPC_001</td></tr>
				<tr><td>HMGU</td><td><a href="/data/page-retired">Browse</a></td><td>HMGU_001</td></tr>
				<tr><td>MRC Harwell</td><td><a href="/data/page-retired">Browse</a></td><td>HRWL_001</td></tr>
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
								"data": [861, 335, 277, 251, 174, 255, 200, 241, 140, 103, 150, 173, 39, 201, 114, 68, 54, 117, 78, 51, 51, 9, 42, 77, 76, 19, 27, 85, 12, 53, 78, 21, 74, 89, 26, 19, 15, 61, 12, 21, 58, 5, 13, 55, 2, 6, 1, 3, 0, 54356]
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
								"data": [118, 107, 153, 169, 198, 287, 285, 232, 251, 248, 246, 296, 265, 302, 279, 275, 276, 266, 297, 289, 273, 269, 278, 266, 259, 244, 277, 231, 242, 217, 205, 199, 207, 198, 198, 187, 199, 176, 171, 165, 175, 149, 134, 156, 102, 96, 92, 71, 50, 42]
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
								"data": [717, 146, 108, 90, 70, 63, 53, 55, 61, 69, 57, 55, 44, 59, 47, 30, 32, 44, 49, 40, 45, 55, 23, 32, 42, 48, 22, 36, 43, 18, 35, 36, 25, 26, 42, 40, 26, 26, 55, 43, 38, 31, 26, 27, 19, 62, 32, 34, 34, 19]
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
								"data": [5589, 1512, 1199, 929, 764, 731, 581, 539, 493, 461, 435, 419, 423, 405, 411, 375, 351, 404, 418, 348, 354, 336, 365, 338, 329, 330, 318, 338, 343, 352, 335, 292, 299, 304, 331, 299, 346, 322, 283, 308, 317, 315, 340, 313, 301, 321, 304, 294, 317, 334]
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
							categories: ["1.0", "1.1"],
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
							"name": "Phenotyped genes",
							"data": [294, 470],
							"type": "column",
							"tooltip": {
								"valueSuffix": " genes",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Phenotyped lines",
							"data": [301, 484],
							"type": "column",
							"tooltip": {
								"valueSuffix": " lines",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"yAxis": 1,
							"name": "MP calls",
							"data": [1069, 2732],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " calls",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
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
							categories: ["1.0", "1.1"],
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
							"name": "Categorical (QC failed)",
							"data": [0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Categorical (QC passed)",
							"data": [149194, 958957],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Categorical (issues)",
							"data": [388, 1214],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (QC failed)",
							"data": [0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (QC passed)",
							"data": [0, 5623],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (issues)",
							"data": [0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (QC failed)",
							"data": [0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (QC passed)",
							"data": [2387, 12803],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (issues)",
							"data": [0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (QC failed)",
							"data": [0, 13176],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (QC passed)",
							"data": [63773, 1451844],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (issues)",
							"data": [83, 83],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (QC failed)",
							"data": [172, 1443],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (QC passed)",
							"data": [381406, 1011637],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (issues)",
							"data": [1454, 5286],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
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
							categories: ["1.0", "1.1"],
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
							"name": "adipose tissue phenotype",
							"data": [73, 116],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "behavior/neurological phenotype",
							"data": [74, 377],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "cardiovascular system phenotype",
							"data": [12, 26],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "craniofacial phenotype",
							"data": [0, 4],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "growth/size/body phenotype",
							"data": [289, 473],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "hearing/vestibular/ear phenotype",
							"data": [84, 125],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "hematopoietic system phenotype",
							"data": [131, 366],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "homeostasis/metabolism phenotype",
							"data": [342, 1046],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "immune system phenotype",
							"data": [18, 69],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "integument phenotype",
							"data": [0, 19],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "limbs/digits/tail phenotype",
							"data": [0, 26],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "nervous system phenotype",
							"data": [0, 42],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "pigmentation phenotype",
							"data": [10, 22],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "skeleton phenotype",
							"data": [101, 162],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "vision/eye phenotype",
							"data": [70, 79],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}]
					});
				});

			</script>
		</div>
	</div>
</div>
