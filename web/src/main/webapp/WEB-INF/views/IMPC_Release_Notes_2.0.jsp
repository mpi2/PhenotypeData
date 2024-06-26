<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<div class="row">
	<div class="col-md-6">
		<div>
			<h4>IMPC</h4>
			<ul class="mt-0">
				<li>Release: 2.0</li>
				<li>Published:&nbsp;06 November 2014</li>
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
				<li>Number of phenotyped genes:&nbsp;518</li>
				<li>Number of phenotyped mutant lines:&nbsp;535</li>
				<li>Number of phenotype calls:&nbsp;2,182</li>
			</ul>
		</div>

		<div>
			<h4>Data access</h4>
			<ul class="mt-0">
				<li>Ftp site:&nbsp;<a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/release-2.0">ftp://ftp.ebi.ac.uk/pub/databases/impc/release-2.0</a></li>
				<li>RESTful interfaces:&nbsp;<a href="https://www.mousephenotype.org/help/programmatic-data-access">APIs</a></li>
			</ul>
		</div>
	</div>
</div>

<div class="row mt-5">
	<div class="col-12">

		<h2 id="new-features">Highlights</h2>

		<div class="col-12">

			<h3>Data release 2.0</h3>

			<div class="well">
				<strong>Release notes:</strong>
				Represents a major data release.

				<h3 class="mt-4">New lines added, some taken away</h3>

				<p>In data release 2.0, we now have 535 mouse lines with phenotype data that has passed quality control. This positive step demonstrates the majority of IMPC centers are now able to generate, capture, export, passed quality control (QC), and distribute high-throughput data for hundreds of new mouse lines. Going forward, we anticipate bi-monthly data releases.</p>
				<p>In review of our processes, we noticed some lines passed QC in the previous data release that should not have. We have reverted the statuses of these lines to a pre-QC state, meaning they our visible on the portal but not being distributed via the API. The majority if not all of these lines should be available in the next data release.</p>

				<h3>Pre-QC vs Post-QC</h3>

				<p>In striking a balance between making data available to the public as soon as possible versus ensuring data is of the highest quality, the data coordination center (DCC) provides both "Pre-QC" and "Post-QC" data.</p>
				<p>Pre-QC data is defined as phenotype data that has been uploaded to the DCC and has passed automated checks but has not been signed off by a data wrangler.  Reasons for not signing off include not enough animals being tested, not all mandatory procedures have had data uploaded, or a known issue has been reported to a phenotyping center and is waiting to be resolved. Lines with pre-QC data can be found by filtering on "phenotype started" under  "Gene" -> "IMPC phenotype status" on the search page.</p>
				<p>Post-QC data is defined as phenotype data for a mouse line that has been signed off by a data wrangler. This means a mouse line has had enough animals tested for the core procedures and has passed quality control. In this phase of the project, we have restricted the number of core procedures that data is needed for as certain data types are more difficult to upload than others. Lines with Post-QC data can be found by filtering on "phenotype complete" under  "Gene" -> "IMPC phenotype status" on the search page.</p>
				<p>In this latest release, we present both pre and post QC data on the portal. Both sets of data are analysed using the same statistical analysis package. Phenotype associations made from Pre-QC data have orange graph links while post-QC data have blue links. Only phenotype associations made from post-QC data are available in the API and pushed to third parties.</p>

				<h3> Statistical Analysis</h3>

				<p>The same statistical analysis package version used in our last data release is used for this release, Version: 1.2.0. We anticipate use PhenStat 2.0 in our next data release.</p>
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
						<td>82</td>
						<td>2,318</td>
						<td>1,886</td>
					</tr>
					<tr>
						<td>HMGU</td>
						<td>16</td>
						<td>511</td>
						<td>244</td>
					</tr>
					<tr>
						<td>ICS</td>
						<td>30</td>
						<td>525</td>
						<td>473</td>
					</tr>
					<tr>
						<td>WTSI</td>
						<td>246</td>
						<td>1,631</td>
						<td>3,713</td>
					</tr>
					<tr>
						<td>JAX</td>
						<td>23</td>
						<td>1,114</td>
						<td>583</td>
					</tr>
					<tr>
						<td>UC Davis</td>
						<td>57</td>
						<td>618</td>
						<td>1,243</td>
					</tr>
					<tr>
						<td>TCP</td>
						<td>66</td>
						<td>312</td>
						<td>1,288</td>
					</tr>
					<tr>
						<td>BCM</td>
						<td>16</td>
						<td>292</td>
						<td>354</td>
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
							1,173,776
						</td>
						<td>
							0
						</td>
						<td>
							1,764
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>unidimensional</td>
						<td>
							988,022
						</td>
						<td>
							992
							<sup>*</sup>
						</td>
						<td>
							16,928
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>time series</td>
						<td>
							1,798,263
						</td>
						<td>
							4
							<sup>*</sup>
						</td>
						<td>
							39
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>text</td>
						<td>
							15,355
						</td>
						<td>
							1,721
							<sup>*</sup>
						</td>
						<td>
							100
							<sup>*</sup>
						</td>
					</tr>
					<tr>
						<td>image record</td>
						<td>
							10,765
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
								categories: ["Rotarod", "Organ Weight", "Auditory Brain Stem Response", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Body Weight", "Indirect Calorimetry", "Clinical Blood Chemistry", "Combined SHIRPA and Dysmorphology", "Body Composition (DEXA lean/fat)", "Eye Morphology", "Grip Strength", "Hematology", "Heart Weight", "Intraperitoneal glucose tolerance test (IPGTT)", "X-ray", "Hole-board Exploration", "Plasma Chemistry"],
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
								"data": [0, 82, 6, 82, 82, 62, 82, 82, 82, 82, 82, 82, 0, 82, 82, 0, 0]
							}, {
								"name": "HMGU",
								"data": [16, 0, 8, 0, 16, 16, 16, 12, 16, 16, 7, 16, 0, 16, 0, 0, 0]
							}, {
								"name": "ICS",
								"data": [0, 0, 27, 13, 0, 25, 6, 2, 30, 0, 0, 25, 30, 30, 30, 0, 0]
							}, {
								"name": "WTSI",
								"data": [0, 0, 246, 0, 246, 246, 246, 0, 246, 246, 246, 246, 246, 246, 0, 0, 0]
							}, {
								"name": "JAX",
								"data": [0, 0, 23, 0, 23, 0, 23, 23, 0, 23, 0, 23, 23, 23, 0, 23, 23]
							}, {
								"name": "UC Davis",
								"data": [0, 0, 56, 0, 56, 0, 1, 57, 1, 57, 0, 57, 1, 57, 0, 0, 0]
							}, {
								"name": "TCP",
								"data": [0, 0, 0, 0, 65, 0, 64, 65, 0, 66, 0, 60, 65, 66, 65, 0, 0]
							}, {"name": "BCM", "data": [0, 0, 16, 3, 16, 0, 0, 0, 16, 16, 16, 0, 0, 16, 0, 0, 0]}]
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
						<td>3</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>1</td>
						<td>77</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>e</td>
						<td>16</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>b</td>
						<td>231</td>
					</tr>
					<tr>
						<td>Targeted Mutation</td>
						<td>a</td>
						<td>211</td>
					</tr>
				</tbody>
			</table>

			<p>Mouse knockout programs:&nbsp;EUCOMM,KOMP</p>
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
							categories: ["adipose tissue phenotype", "behavior/neurological phenotype", "cardiovascular system phenotype", "craniofacial phenotype", "digestive/alimentary phenotype", "growth/size/body phenotype", "hearing/vestibular/ear phenotype", "hematopoietic system phenotype", "homeostasis/metabolism phenotype", "immune system phenotype", "integument phenotype", "limbs/digits/tail phenotype", "mortality/aging", "nervous system phenotype", "pigmentation phenotype", "renal/urinary system phenotype", "reproductive system phenotype", "respiratory system phenotype", "skeleton phenotype", "vision/eye phenotype"],
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
							"data": [2, 0, 0, 2, 0, 21, 0, 7, 15, 1, 0, 1, 0, 0, 0, 0, 0, 0, 5, 4]
						}, {
							"name": "homozygote",
							"data": [131, 680, 52, 70, 2, 1966, 121, 678, 1199, 207, 98, 161, 0, 64, 75, 6, 5, 12, 480, 210]
						}, {
							"name": "heterozygote",
							"data": [33, 119, 15, 4, 0, 494, 5, 237, 477, 86, 29, 29, 0, 22, 12, 2, 1, 32, 108, 69]
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
										data: [['Micro-injection in progress', 18], ['Chimeras obtained', 39], ['Genotype confirmed', 938], ['Cre Excision Started', 218], ['Cre Excision Complete', 1964],],
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
										data: [['Phenotype Attempt Registered', 1471], ['Phenotyping Started', 1018], ['Phenotyping Complete', 495],],
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
		<h3>By Center</h3>
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
							series: [{"name": "SEAT", "data": [0, 0, 2, 0, 0]}, {
								"name": "Harwell",
								"data": [9, 12, 85, 2, 240]
							}, {"name": "HMGU", "data": [0, 3, 44, 1, 161]}, {
								"name": "INFRAFRONTIER-Oulu",
								"data": [0, 0, 1, 0, 0]
							}, {"name": "RIKEN BRC", "data": [0, 1, 11, 8, 22]}, {
								"name": "Monterotondo",
								"data": [0, 0, 3, 0, 6]
							}, {"name": "WTSI", "data": [1, 3, 655, 8, 174]}, {
								"name": "JAX",
								"data": [0, 2, 4, 135, 427]
							}, {"name": "MARC", "data": [0, 0, 23, 20, 34]}, {
								"name": "TCP",
								"data": [2, 6, 18, 1, 218]
							}, {"name": "UCD", "data": [4, 5, 38, 36, 506]}, {
								"name": "ICS",
								"data": [0, 0, 8, 0, 84]
							}, {"name": "CIPHE", "data": [1, 0, 2, 1, 0]}, {
								"name": "IMG",
								"data": [0, 0, 0, 0, 8]
							}, {"name": "BCM", "data": [0, 7, 44, 6, 181]}]
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
							series: [{"name": "Harwell", "data": [153, 116, 81]}, {
								"name": "HMGU",
								"data": [115, 70, 19]
							}, {"name": "RIKEN BRC", "data": [35, 7, 1]}, {
								"name": "Monterotondo",
								"data": [6, 0, 0]
							}, {"name": "JAX", "data": [311, 231, 25]}, {
								"name": "WTSI",
								"data": [389, 203, 236]
							}, {"name": "MARC", "data": [61, 14, 0]}, {
								"name": "TCP",
								"data": [62, 101, 62]
							}, {"name": "ICS", "data": [22, 36, 28]}, {
								"name": "UCD",
								"data": [221, 234, 51]
							}, {"name": "CIPHE", "data": [2, 0, 0]}, {"name": "IMG", "data": [7, 0, 0]}, {
								"name": "BCM",
								"data": [155, 60, 17]
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
							categories: ["Auditory Brain Stem Response", "Body Composition (DEXA lean/fat)", "Eye Morphology", "Grip Strength", "Intraperitoneal glucose tolerance test (IPGTT)", "Clinical Blood Chemistry", "Combined SHIRPA and Dysmorphology", "Hematology", "Fertility of Homozygous Knock-out Mice", "Viability Primary Screen", "Heart Weight", "Hole-board Exploration", "Plasma Chemistry", "Organ Weight", "Acoustic Startle and Pre-pulse Inhibition (PPI)", "Indirect Calorimetry", "X-ray"],
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
							"data": [27, 115, 52, 49, 57, 98, 171, 55, 0, 48, 0, 0, 0, 40, 78, 9, 60]
						}, {
							"name": "HMGU",
							"data": [0, 2, 5, 0, 0, 21, 12, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "ICS",
							"data": [0, 0, 0, 0, 0, 0, 0, 0, 1, 24, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "WTSI",
							"data": [81, 156, 60, 61, 42, 230, 0, 114, 0, 0, 46, 0, 0, 0, 0, 23, 0]
						}, {
							"name": "JAX",
							"data": [4, 0, 0, 0, 24, 21, 2, 12, 0, 0, 4, 2, 23, 0, 0, 0, 0]
						}, {
							"name": "UC Davis",
							"data": [5, 0, 0, 0, 14, 0, 44, 9, 2, 0, 0, 0, 0, 0, 0, 0, 0]
						}, {
							"name": "TCP",
							"data": [0, 0, 9, 0, 8, 55, 49, 44, 0, 8, 15, 0, 0, 0, 0, 0, 23]
						}, {"name": "BCM", "data": [6, 37, 1, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}]
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
				<tr><td>TCP</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>TCP_001</td></tr>
				<tr><td>HMGU</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
				<tr><td>HMGU</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>HMGU_001</td></tr>
				<tr><td>MRC Harwell</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>IMPC_001</td></tr>
				<tr><td>MRC Harwell</td><td><a href="${baseUrl}/page-retired">Browse</a></td><td>HRWL_001</td></tr>
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
								"data": [1012, 351, 372, 311, 192, 318, 194, 292, 181, 160, 123, 132, 146, 182, 122, 115, 54, 81, 103, 74, 74, 59, 49, 39, 48, 58, 73, 28, 31, 62, 81, 52, 109, 23, 16, 18, 16, 60, 10, 38, 3, 21, 14, 61, 5, 18, 1, 0, 0, 72657]
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
								"data": [120, 149, 339, 271, 351, 387, 367, 371, 359, 397, 354, 475, 381, 430, 418, 406, 457, 398, 395, 448, 388, 374, 394, 390, 439, 389, 394, 366, 360, 356, 350, 349, 351, 389, 322, 310, 313, 309, 372, 404, 388, 408, 352, 271, 432, 228, 204, 135, 124, 232]
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
								"name": "MM framework, generalized least squares, equation withoutWeight",
								"data": [592, 172, 151, 122, 124, 88, 80, 58, 59, 70, 77, 55, 74, 69, 75, 48, 58, 73, 60, 39, 46, 52, 59, 47, 44, 50, 50, 59, 51, 37, 54, 50, 59, 38, 33, 41, 62, 48, 44, 62, 59, 20, 32, 38, 45, 53, 45, 39, 29, 50]
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
								"name": "MM framework, linear mixed-effects model, equation withoutWeight",
								"data": [4293, 1394, 1092, 896, 796, 640, 632, 517, 505, 519, 490, 481, 466, 447, 458, 383, 403, 426, 441, 408, 404, 351, 397, 372, 383, 399, 382, 397, 361, 350, 371, 356, 338, 360, 331, 369, 335, 375, 361, 366, 370, 345, 323, 325, 348, 344, 362, 356, 330, 359]
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
							categories: ["1.0", "1.1", "2.0"],
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
							"data": [294, 470, 518],
							"type": "column",
							"tooltip": {
								"valueSuffix": " genes",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Phenotyped lines",
							"data": [301, 484, 535],
							"type": "column",
							"tooltip": {
								"valueSuffix": " lines",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"yAxis": 1,
							"name": "MP calls",
							"data": [1069, 2732, 2182],
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
							categories: ["1.0", "1.1", "2.0"],
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
							"data": [0, 0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Categorical (QC passed)",
							"data": [149194, 958957, 1173776],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Categorical (issues)",
							"data": [388, 1214, 1764],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (QC failed)",
							"data": [0, 0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (QC passed)",
							"data": [0, 5623, 10765],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Image record (issues)",
							"data": [0, 0, 0],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (QC failed)",
							"data": [0, 0, 1721],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (QC passed)",
							"data": [2387, 12803, 15355],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Text (issues)",
							"data": [0, 0, 100],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (QC failed)",
							"data": [0, 13176, 4],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (QC passed)",
							"data": [63773, 1451844, 1798263],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Time series (issues)",
							"data": [83, 83, 39],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (QC failed)",
							"data": [172, 1443, 992],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (QC passed)",
							"data": [381406, 1011637, 988022],
							"type": "spline",
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "Unidimensional (issues)",
							"data": [1454, 5286, 16928],
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
							categories: ["1.0", "1.1", "2.0"],
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
							"data": [73, 116, 93],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "behavior/neurological phenotype",
							"data": [74, 377, 394],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "cardiovascular system phenotype",
							"data": [12, 26, 44],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "craniofacial phenotype",
							"data": [0, 4, 24],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "growth/size/body phenotype",
							"data": [289, 473, 561],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "hearing/vestibular/ear phenotype",
							"data": [84, 125, 237],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "hematopoietic system phenotype",
							"data": [131, 366, 495],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "homeostasis/metabolism phenotype",
							"data": [342, 1046, 1370],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "immune system phenotype",
							"data": [18, 69, 81],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "integument phenotype",
							"data": [0, 19, 55],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "limbs/digits/tail phenotype",
							"data": [0, 26, 132],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "mortality/aging",
							"data": [0, 0, 80],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "nervous system phenotype",
							"data": [0, 42, 79],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "pigmentation phenotype",
							"data": [10, 22, 51],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "renal/urinary system phenotype",
							"data": [0, 0, 8],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "reproductive system phenotype",
							"data": [0, 0, 3],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "skeleton phenotype",
							"data": [101, 162, 223],
							"tooltip": {
								"valueSuffix": " ",
								"pointFormat": "<span style=\"color:{series.color}\">●<\/span> {series.name}: <b>{point.y}<\/b><br/>"
							}
						}, {
							"name": "vision/eye phenotype",
							"data": [70, 79, 127],
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
