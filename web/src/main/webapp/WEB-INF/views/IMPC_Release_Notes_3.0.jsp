<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
	<div class="col-md-6">
		<div>
			<h4>IMPC</h4>
			<ul class="mt-0">
				<li>Release: 3.0</li>
				<li>Published:&nbsp;06 February 2015</li>
			</ul>
		</div>

		<div>
			<h4>Statistical Package</h4>
			<ul class="mt-0">
				<li>PhenStat</li>
				<li>Version:&nbsp;2.0.1</li>
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
				<li>Number of phenotyped genes:&nbsp;1,458</li>
				<li>Number of phenotyped mutant lines:&nbsp;1,528</li>
				<li>Number of phenotype calls:&nbsp;6,114</li>
			</ul>
		</div>

		<div>
			<h4>Data access</h4>
			<ul class="mt-0">
				<li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-3.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-3.0</a></li>
				<li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
			</ul>
		</div>
	</div>
</div>

<div class="row mt-5">
	<div class="col-12">

		<h2 id="new-features">Highlights</h2>

		<div class="col-12">

			<h4>Data release 3.0</h4>

			<div class="well">
                <strong>Release notes:</strong>
				Represents a major data release. Changes include:

				<ul>
					<li>1,456 mouse lines with phenotype data that has
						passed quality control. This is a marked increase since data release 2.0 and
						demonstrates the acceleration of data generation for the IMPC effort.
					</li>
				</ul>

				<h3>Statistical Analysis</h3>

				<p>The statistical analysis package used in data release 2.0 (PhenStat) has been updated
					to Version: 2.0.1.</p>
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
						<td>6</td>
						<td>142</td>
						<td>98</td>
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
						<td>4,678</td>
					</tr>
					<tr>
						<td>UC Davis</td>
						<td>273</td>
						<td>883</td>
						<td>6,320</td>
					</tr>
					<tr>
						<td>TCP</td>
						<td>161</td>
						<td>571</td>
						<td>2,824</td>
					</tr>
					<tr>
						<td>BCM</td>
						<td>72</td>
						<td>464</td>
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
							3,935,293
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
							2,782,486
						</td>
						<td>
							3,724
							<sup>*</sup>
						</td>
						<td>
							38,737
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>time series</td>
						<td>
							7,237,320
						</td>
						<td>
							9
							<sup>*</sup>
						</td>
						<td>
							82
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>text</td>
						<td>
							51,283
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
							106,682
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
								categories: ["Rotarod", "FACS", "Organ Weight", "Fear Conditioning", "Hot Plate", "Shock Threshold", "Auditory Brain Stem Response", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Adult LacZ", "Tissue Embedding and Block Banking", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Challenge Whole Body Plethysmography", "Combined SHIRPA and Dysmorphology", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Embryo LacZ", "Eye Morphology", "Grip Strength", "Hematology", "Histopathology", "Heart Weight", "Immunophenotyping", "Insulin Blood Level", "Intraperitoneal glucose tolerance test (IPGTT)", "Open Field", "Gross Pathology and Tissue Collection", "X-ray", "Electroconvulsive Threshold Testing", "Electroretinography", "Electroretinography 2", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Sleep Wake", "Tail Suspension", "Anti-nuclear antibody assay", "Buffy coat peripheral blood leukocyte immunophenotyping", "Bone marrow immunophenotyping", "Ear epidermis immunophenotyping", "Whole blood peripheral blood leukocyte immunophenotyping", "Tail Flick"],
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
								"name": "MRC Harwell",
								"data": [0, 87, 166, 0, 0, 0, 51, 182, 75, 0, 185, 129, 165, 0, 185, 173, 131, 137, 0, 169, 184, 165, 0, 0, 0, 0, 169, 184, 23, 166, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "RBRC",
								"data": [0, 0, 0, 0, 0, 0, 9, 6, 9, 13, 13, 12, 8, 0, 1, 13, 7, 0, 0, 13, 13, 12, 0, 13, 8, 0, 13, 10, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "NING",
								"data": [0, 0, 0, 0, 0, 0, 6, 6, 0, 1, 5, 0, 6, 0, 5, 4, 0, 0, 0, 5, 6, 6, 0, 4, 0, 0, 5, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "HMGU",
								"data": [63, 0, 0, 0, 0, 0, 47, 46, 64, 0, 71, 68, 67, 0, 67, 58, 68, 78, 0, 62, 67, 66, 0, 46, 0, 42, 70, 67, 58, 55, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "ICS",
								"data": [57, 0, 0, 38, 54, 40, 51, 34, 0, 0, 0, 59, 29, 0, 16, 62, 62, 63, 0, 57, 61, 57, 0, 63, 0, 0, 62, 60, 0, 62, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "WTSI",
								"data": [0, 0, 0, 0, 0, 0, 381, 0, 0, 0, 387, 376, 388, 0, 390, 388, 0, 0, 0, 390, 388, 394, 0, 295, 0, 365, 385, 0, 0, 387, 0, 0, 0, 0, 0, 0, 0, 0, 129, 345, 125, 148, 117, 0]
							}, {
								"name": "JAX",
								"data": [186, 0, 0, 0, 0, 0, 214, 202, 24, 84, 220, 0, 183, 0, 204, 189, 173, 0, 2, 178, 216, 61, 118, 170, 0, 73, 202, 175, 8, 0, 167, 67, 10, 209, 214, 190, 142, 208, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "UC Davis",
								"data": [0, 0, 0, 0, 0, 0, 230, 200, 184, 0, 220, 226, 181, 0, 262, 183, 241, 0, 0, 226, 256, 173, 0, 220, 0, 111, 236, 0, 0, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
							}, {
								"name": "TCP",
								"data": [0, 0, 0, 0, 0, 0, 85, 112, 21, 113, 118, 121, 98, 37, 115, 103, 108, 0, 0, 107, 115, 100, 45, 97, 0, 0, 109, 115, 110, 110, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 58]
							}, {
								"name": "BCM",
								"data": [0, 0, 0, 0, 0, 0, 57, 6, 9, 0, 60, 60, 16, 4, 69, 41, 54, 48, 0, 49, 65, 15, 0, 42, 11, 0, 59, 0, 46, 21, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
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
						<td>413</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>e</td>
						<td>24</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>b</td>
						<td>775</td>
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

			<p>Mouse knockout programs:&nbsp;KOMP,EUCOMM,NCOM,IST12471H5</p>
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
							categories: ["adipose tissue phenotype", "behavior/neurological phenotype", "cardiovascular system phenotype", "craniofacial phenotype", "growth/size/body phenotype", "hearing/vestibular/ear phenotype", "hematopoietic system phenotype", "homeostasis/metabolism phenotype", "immune system phenotype", "limbs/digits/tail phenotype", "skeleton phenotype", "vision/eye phenotype", "integument phenotype", "muscle phenotype", "nervous system phenotype", "pigmentation phenotype", "renal/urinary system phenotype", "reproductive system phenotype", "respiratory system phenotype", "digestive/alimentary phenotype", "mortality/aging"],
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
							"name": "hemizygote",
							"data": [2, 3, 1, 2, 18, 2, 13, 24, 6, 1, 10, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "homozygote",
							"data": [230, 1010, 249, 100, 1678, 205, 1249, 1887, 533, 251, 998, 537, 116, 3, 100, 105, 15, 49, 21, 2, 858]
						}, {
							"name": "heterozygote",
							"data": [77, 260, 90, 9, 540, 38, 394, 715, 163, 53, 270, 148, 31, 2, 42, 18, 12, 1, 32, 0, 0]
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
					<div class="col-md-6">
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
										data: [['Micro-injection in progress', 146], ['Chimeras obtained', 314], ['Genotype confirmed', 2385], ['Cre Excision Started', 167], ['Cre Excision Complete', 2185],],
										dataLabels: {
											enabled: true,
											style: {fontSize: '13px', fontFamily: 'Verdana, sans-serif'}
										}
									}]
								});
							});</script>
						</div>
					</div>

					<div class="col-md-6">
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
										data: [['Phenotype Attempt Registered', 1387], ['Phenotyping Started', 444], ['Phenotyping Complete', 1411],],
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

	<div class="col-12">
		<h4>By Center</h4>
		<div class="row">

			<div class="col-md-6">
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
							series: [{"name": "INFRAFRONTIER-CNB", "data": [0, 0, 1, 0, 0]}, {
								"name": "SEAT",
								"data": [11, 4, 12, 0, 0]
							}, {"name": "Harwell", "data": [50, 67, 168, 1, 200]}, {
								"name": "HMGU",
								"data": [1, 13, 93, 1, 157]
							}, {"name": "RIKEN BRC", "data": [2, 12, 20, 6, 21]}, {
								"name": "INFRAFRONTIER-Oulu",
								"data": [0, 0, 1, 0, 0]
							}, {"name": "INFRAFRONTIER-VETMEDVie", "data": [0, 0, 2, 0, 0]}, {
								"name": "Monterotondo",
								"data": [3, 0, 49, 0, 48]
							}, {"name": "KMPC", "data": [2, 0, 0, 0, 0]}, {
								"name": "WTSI",
								"data": [13, 40, 1075, 3, 221]
							}, {"name": "NARLabs", "data": [0, 0, 1, 0, 0]}, {
								"name": "JAX",
								"data": [5, 31, 41, 102, 483]
							}, {"name": "MARC", "data": [0, 1, 49, 12, 38]}, {
								"name": "TCP",
								"data": [0, 68, 55, 1, 188]
							}, {"name": "INFRAFRONTIER-IMG", "data": [1, 0, 1, 0, 0]}, {
								"name": "UCD",
								"data": [39, 66, 466, 36, 498]
							}, {"name": "ICS", "data": [0, 2, 103, 0, 92]}, {
								"name": "CIPHE",
								"data": [9, 1, 14, 1, 0]
							}, {"name": "IMG", "data": [10, 0, 6, 0, 6]}, {"name": "BCM", "data": [0, 9, 227, 4, 233]}]
						});
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
							series: [{"name": "Harwell", "data": [138, 44, 187]}, {
								"name": "HMGU",
								"data": [129, 12, 82]
							}, {"name": "RIKEN BRC", "data": [30, 7, 6]}, {
								"name": "Monterotondo",
								"data": [6, 0, 0]
							}, {"name": "JAX", "data": [302, 70, 239]}, {
								"name": "WTSI",
								"data": [346, 154, 398]
							}, {"name": "MARC", "data": [50, 21, 6]}, {
								"name": "TCP",
								"data": [79, 20, 149]
							}, {"name": "ICS", "data": [13, 15, 60]}, {
								"name": "UCD",
								"data": [165, 76, 256]
							}, {"name": "CIPHE", "data": [2, 0, 0]}, {"name": "IMG", "data": [8, 0, 0]}, {
								"name": "BCM",
								"data": [185, 36, 66]
							}]
						});
					});
					</script>
				</div>
				<a id="checkAllPhenByCenter" class="buttonForHighcharts"><i class="fa fa-check" aria-hidden="true"></i> Select all</a>
				<a id="uncheckAllPhenByCenter"  class="buttonForHighcharts"><i class="fa fa-times" aria-hidden="true"></i> Deselect all</a>
				<div class="clear both"></div>
			</div>
		</div>

		<p>More charts and status information are available from <a	href="https://www.mousephenotype.org/imits/v2/reports/mi_production/komp2_graph_report_display">iMits</a>. </p>
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
							categories: ["Auditory Brain Stem Response", "Clinical Blood Chemistry", "Body Composition (DEXA lean/fat)", "Electrocardiogram (ECG)", "Echo", "Eye Morphology", "Fertility of Homozygous Knock-out Mice", "Grip Strength", "Hematology", "Heart Weight", "Intraperitoneal glucose tolerance test (IPGTT)", "Viability Primary Screen", "X-ray", "Indirect Calorimetry", "Combined SHIRPA and Dysmorphology", "Insulin Blood Level", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Electroconvulsive Threshold Testing", "Hole-board Exploration", "Light-Dark Test", "Plasma Chemistry", "Rotarod", "Tail Suspension", "FACS", "Organ Weight", "Tail Flick"],
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
							"name": "MRC Harwell",
							"data": [18, 159, 204, 16, 21, 156, 3, 51, 97, 0, 46, 132, 115, 3, 144, 0, 145, 0, 0, 0, 0, 0, 0, 16, 58, 0]
						}, {
							"name": "RBRC",
							"data": [7, 12, 0, 6, 0, 0, 3, 0, 26, 0, 0, 0, 14, 0, 18, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "NING",
							"data": [0, 1, 14, 0, 0, 0, 0, 5, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "HMGU",
							"data": [7, 68, 12, 8, 10, 54, 13, 0, 50, 0, 13, 40, 8, 8, 14, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "ICS",
							"data": [15, 20, 26, 10, 2, 102, 1, 0, 22, 7, 4, 64, 8, 6, 2, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "WTSI",
							"data": [93, 354, 284, 0, 0, 84, 6, 81, 161, 7, 24, 212, 187, 10, 0, 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "JAX",
							"data": [14, 172, 0, 86, 0, 74, 1, 0, 14, 12, 60, 140, 0, 0, 36, 10, 0, 10, 12, 100, 94, 13, 12, 0, 0, 0]
						}, {
							"name": "UC Davis",
							"data": [25, 114, 211, 23, 0, 5, 8, 0, 58, 7, 28, 74, 12, 0, 59, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "TCP",
							"data": [11, 83, 0, 19, 0, 26, 5, 0, 104, 6, 7, 130, 75, 5, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2]
						}, {
							"name": "BCM",
							"data": [25, 2, 56, 33, 8, 20, 3, 7, 5, 2, 3, 66, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
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
				<tr><td>MRC Harwell</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
				<tr><td>MRC Harwell</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>HRWL_001</td></tr>
				<tr><td>RBRC</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
				<tr><td>ICS</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
				<tr><td>ICS</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>ICS_001</td></tr>
				<tr><td>WTSI</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>MGP_001</td></tr>
				<tr><td>BCM</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
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
								"name": "Fisher's exact test",
								"data": [5017, 1821, 1442, 1363, 1007, 1615, 1319, 1508, 756, 634, 866, 447, 1069, 825, 375, 475, 2452, 516, 420, 422, 582, 271, 229, 274, 356, 421, 265, 93, 385, 248, 691, 187, 225, 193, 76, 109, 65, 141, 36, 136, 53, 42, 45, 98, 32, 27, 6, 4, 0, 413430]
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
								"name": "Wilcoxon rank sum test with continuity correction",
								"data": [1791, 1469, 1472, 1430, 1739, 1822, 1869, 1897, 1871, 1982, 1931, 1959, 1940, 2029, 2039, 2002, 2025, 2108, 2040, 2141, 2016, 2027, 1976, 1949, 1916, 1901, 1868, 1866, 1804, 1785, 1713, 1688, 1631, 1616, 1525, 1480, 1570, 1544, 1429, 1977, 1732, 2418, 1872, 1638, 1554, 1555, 2265, 845, 757, 894]
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
								"name": "Mixed Model framework, generalized least squares, equation withoutWeight",
								"data": [2533, 814, 587, 529, 462, 402, 337, 314, 267, 258, 333, 309, 293, 275, 284, 257, 253, 295, 287, 213, 242, 232, 221, 239, 230, 226, 216, 235, 227, 198, 222, 235, 185, 203, 191, 240, 198, 183, 212, 253, 229, 176, 158, 207, 221, 232, 189, 205, 172, 215]
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
								"name": "Mixed Model framework, linear mixed-effects model, equation withoutWeight",
								"data": [13399, 4518, 3534, 2862, 2508, 2219, 1972, 1678, 1666, 1634, 1623, 1500, 1467, 1411, 1456, 1371, 1344, 1308, 1370, 1249, 1284, 1244, 1301, 1304, 1232, 1232, 1224, 1179, 1229, 1283, 1158, 1227, 1144, 1208, 1168, 1142, 1161, 1207, 1175, 1129, 1204, 1064, 1185, 1149, 1103, 1158, 1188, 1147, 1074, 1107]
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
							categories: ["1.0", "1.1", "2.0", "3.0"],
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
							"data": [294, 470, 518, 1458],
							"type": "column",
							"tooltip": {
								"valueSuffix": " genes",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Phenotyped lines",
							"data": [301, 484, 535, 1528],
							"type": "column",
							"tooltip": {
								"valueSuffix": " lines",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"yAxis": 1,
							"name": "MP calls",
							"data": [1069, 2732, 2182, 6114],
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
							categories: ["1.0", "1.1", "2.0", "3.0"],
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
							"data": [0, 0, 0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Categorical (QC passed)",
							"data": [149194, 958957, 1173776, 3935293],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Categorical (issues)",
							"data": [388, 1214, 1764, 3426],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (QC failed)",
							"data": [0, 0, 0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (QC passed)",
							"data": [0, 5623, 10765, 106682],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (issues)",
							"data": [0, 0, 0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (QC failed)",
							"data": [0, 0, 1721, 18591],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (QC passed)",
							"data": [2387, 12803, 15355, 51283],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (issues)",
							"data": [0, 0, 100, 161],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (QC failed)",
							"data": [0, 13176, 4, 9],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (QC passed)",
							"data": [63773, 1451844, 1798263, 7237320],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (issues)",
							"data": [83, 83, 39, 82],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (QC failed)",
							"data": [172, 1443, 992, 3724],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (QC passed)",
							"data": [381406, 1011637, 988022, 2782486],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (issues)",
							"data": [1454, 5286, 16928, 38737],
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
							categories: ["1.0", "1.1", "2.0", "3.0"],
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
							"data": [73, 116, 93, 241],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "behavior/neurological phenotype",
							"data": [74, 377, 394, 636],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "cardiovascular system phenotype",
							"data": [12, 26, 44, 296],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "craniofacial phenotype",
							"data": [0, 4, 24, 56],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "growth/size/body phenotype",
							"data": [289, 473, 561, 533],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "hearing/vestibular/ear phenotype",
							"data": [84, 125, 237, 245],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "hematopoietic system phenotype",
							"data": [131, 366, 495, 704],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "homeostasis/metabolism phenotype",
							"data": [342, 1046, 1370, 1368],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "immune system phenotype",
							"data": [18, 69, 81, 183],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "integument phenotype",
							"data": [0, 19, 55, 79],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "limbs/digits/tail phenotype",
							"data": [0, 26, 132, 204],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "mammalian phenotype",
							"data": [0, 0, 0, 55],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "mortality/aging",
							"data": [0, 0, 80, 858],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "muscle phenotype",
							"data": [0, 0, 0, 5],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "nervous system phenotype",
							"data": [0, 42, 79, 73],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "pigmentation phenotype",
							"data": [10, 22, 51, 88],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "renal/urinary system phenotype",
							"data": [0, 0, 8, 27],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "reproductive system phenotype",
							"data": [0, 0, 3, 43],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "respiratory system phenotype",
							"data": [0, 0, 0, 1],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "skeleton phenotype",
							"data": [101, 162, 223, 714],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "vision/eye phenotype",
							"data": [70, 79, 127, 521],
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
