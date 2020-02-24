<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage-landing>

    <jsp:attribute name="title">Sexual Dimorphism | IMPC Phenotype Information</jsp:attribute>
	<jsp:attribute name="pagename">Sexual Dimorphism</jsp:attribute>
	<jsp:attribute name="breadcrumb">Sexual Dimorphism</jsp:attribute>

	<jsp:attribute name="bodyTag"><body class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
    	<!-- JS Imports -->
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
		<link href="${baseUrl}/css/vendor/font-awesome/font-awesome.min.css" rel="stylesheet" type="text/css" />

		<!-- Specific styling for the SD graphs -->
		<style>
			.large-arrow { font-size: 20px; padding: 0 5px; }
		</style>


    </jsp:attribute>
 		

    <jsp:body>


		<div class="container">
			<div class="row">
				<div class="col-12">

					<p>IMPC data demonstrates the effect of sex on many phenotypes, supporting the importance of
						including males and females in biomedical research.</p>
					<ul>
						<li>Press releases: <a href="http://www.bbc.co.uk/programmes/b08vwn6w">BBC Radio 4 - "inside Science"</a>&nbsp;|&nbsp;<a href="https://www.newscientist.com/article/2138671-research-on-male-animals-prevents-women-from-getting-best-drugs/">New Scientist</a>&nbsp;|&nbsp;<a href="http://www.dailymail.co.uk/wires/reuters/article-4640316/Sexual-equality-medical-research-long-overdue-study-finds.html">Daily Mail</a>&nbsp;|&nbsp;<a href="https://www.sciencedaily.com/releases/2017/06/170626124505.htm">Science Daily</a>&nbsp;|&nbsp;<a href="http://fortune.com/2017/06/26/sexual-bias-medical-research/">Fortune</a>&nbsp;|&nbsp;<a href="${cmsBaseUrl}/news/study-of-unprecedented-size-reveals-how-sex-blindspot-could-misdirect-medical-research/">IMPC</a>&nbsp;|&nbsp;<a href="http://www.ebi.ac.uk/about/news/press-releases/sexual-dimorphism-dilemma">EMBL-EBI</a>&nbsp;|&nbsp;<a href="http://www.sanger.ac.uk/news/view/study-reveals-how-sex-blindspot-could-misdirect-medical-research">Sanger</a></li>
						<li> <a href="https://zenodo.org/record/260398#.WVIkChPys-c">Supporting material to enable replicable analysis</a></li>
						<li><a href="https://www.nature.com/articles/ncomms15475">Publication: Nature Communications 26 June 2017</a></li>
					</ul>

					<!-- <p class="smallerAlleleFont">

						1. Beery, Annaliese K., and Irving Zucker. "Sex bias in neuroscience and biomedical research." <i>Neuroscience & Biobehavioral Reviews</i> 35.3 (2011): 565-572.
						<br/><br/>
						2.  Yoon, Dustin Y., et al. "Sex bias exists in basic science and translational surgical research." <i>Surgery</i> 156.3 (2014): 508-516.
						<br/><br/>
						3. Geller, Stacie E., et al. "Inclusion, analysis, and reporting of sex and race/ethnicity in clinical trials: have we made progress?." <i>Journal of Women's Health</i> 20.3 (2011): 315-320.
						<br/>
						4. Flanagan, Katie L. "Sexual dimorphism in biomedical research: a call to analyse by sex." <i>Transactions of the Royal Society of Tropical Medicine and Hygiene</i> 108.7 (2014): 385-387.
						<br/>
						5. Woodruff, Teresa K. "Sex, equity, and science." <i>Proceedings of the National Academy of Sciences</i> 111.14 (2014): 5063-5064.
						<br/>
						6. Morselli, Eugenia, et al. "Sex and Gender: Critical Variables in Pre-Clinical and Clinical Medical Research."  <i>Cell Metabolism</i> 24.2 (2016): 203-209.


					</p> -->
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2>Sexual dimorphism in 14,250 wildtype mice</h2>
					<div class="row">
						<div class="col-6">
							<div id="wtCategorical">
								<script type="text/javascript">$(function () { $('#wtCategorical').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Categorical' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'Male Greater', y: 3.7, sliced: true, selected: true },{ name: 'Female Greater', y: 6.2, sliced: true, selected: true },  [' No Sex Effect Detected', 90.1 ] ]  }] }); });</script>
							</div>
						</div>
						<div class="col-6">
							<div id="wtContinuous">
								<script type="text/javascript">$(function () { $('#wtContinuous').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Continuous' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'Male Greater', y: 30.6, sliced: true, selected: true },{ name: 'Female Greater', y: 26.6, sliced: true, selected: true },  [' No Sex Effect Detected', 90.1 ] ]  }] }); });</script>
							</div>
						</div>
					</div>
					<p>The impact of sex on phenotypes of wild-type C57BL/6N mice measured using  the IMPC adult phenotype pipeline at ten global research centers.</p>
					<p>Sex effect (5% FDR) was detected in:</p>
					<ul>
						<li>9.9% categorical phenotypes (545 examined, e.g. abnormal corneal opacity)</li>
						<li>56.6% continuous phenotypes (903 examined, e.g. blood glucose levels)</li>
					</ul>

				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2>Sexual dimorphism in 2,186 knockout mouse strains</h2>
					<div class="row">
						<div class="col-6">
							<div id="koCategorical">
								<script type="text/javascript">$(function () { $('#koCategorical').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Categorical' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'Male Greater', y: 6.8, sliced: true, selected: true },{ name: 'Female Greater', y: 6.5, sliced: true, selected: true },  [' No Sex Effect Detected', 86.7 ] ]  }] }); });</script>
							</div>
						</div>
						<div class="col-6">
							<div id="koContinuous">
								<script type="text/javascript">$(function () { $('#koContinuous').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Continuous' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'One Sex', y: 12.8, sliced: true, selected: true }, { name: 'Diferent Size', y: 0.8, sliced: true, selected: true },{ name: 'Different Directions', y: 3.5, sliced: true, selected: true }, { name: 'Cannot Classify', y: 0.6, sliced: true, selected: true },  [' Genotype Effect with No Sex Effect', 82.2 ] ]  }] }); });</script>
							</div>
						</div>
					</div>
					<p>The impact of sex on phenotypes of knockout C57BL/6N mouse lines measured using the IMPC adult phenotype pipeline at ten global research centers. Only strains with an abnormal phenotype attributable to the knockout allele (i.e genotype effect) were included.</p>
					<p>Sex modified the genotype effect on mutant phenotypes in:</p>
					<ul>
						<li>13.3% categorical phenotypes (1,220 examined; 20%FDR)</li>
						<li>17.7% continuous phenotypes (7,929 examined; 5% FDR)</li>
					</ul>
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2>Approach</h2>
					<p>In 2017, the IMPC analysed phenotype data collected over a 5 year period  from 14,250 wildtype and 40,192 mutant mice representing 2,186 knockout lines.</p>
					<ul>
						<li>Weight was included as a covariate in the continuous data set analyses, as body size is dimorphic between male and female mice and many continuous traits correlate with body weight.</li>
						<li>For wild-type mice, males and females within a center were compared</li>
						<li>For the mutant mice, lines were selected (1) if there was an abnormal phenotype associated with the knockout allele (2) if significant, whether sex influenced the phenotype.</li>
					</ul>
					<p>The wild-type analysis is from the original manuscript. The mutant line analysis is updated with each IMPC data release</p>
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2>Vignettes</h2>
					<div class="row">
						<div class="col-4" style="text-align:center">
							<i class="fa fa-long-arrow-up large-arrow" aria-hidden="true"></i> in <img src="${baseUrl}/img/male.jpg">, no effect in <img src="${baseUrl}/img/female.jpg">
							<div id="hdlCholesterol">
								<script type="text/javascript">chart = new Highcharts.Chart({  colors:['rgba(239, 123, 11,0.7)', 'rgba(9, 120, 161,0.7)', 'rgba(247, 157, 70,0.7)', 'rgba(61, 167, 208,0.7)', 'rgba(247, 181, 117,0.7)', 'rgba(100, 178, 208,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(3, 77, 105,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(36, 139, 75,0.7)', 'rgba(255, 201, 67,0.7)', 'rgba(1, 121, 46,0.7)', 'rgba(144, 195, 212,0.7)', 'rgba(51, 51, 51,0.7)', 'rgba(119, 119, 119,0.7)', 'rgba(191, 151, 50,0.7)'], chart: { type: 'boxplot', renderTo: 'hdlCholesterol'},   tooltip: { formatter: function () { if(typeof this.point.high === 'undefined'){ return '<b>Observation</b><br/>' + this.point.y; } else { return '<b>Genotype: ' + this.key + '</b><br/>UQ + 1.5 * IQR: ' + this.point.options.high + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>Median: ' + this.point.options.median + '<br/>Lower Quartile: ' + this.point.options.q1 +'<br/>LQ - 1.5 * IQR: ' + this.point.low; } } }    ,
									title: {  text: 'HDL Cholesterol', useHTML:true } ,  credits: { enabled: false },   legend: { enabled: false },  xAxis: { categories:  ["Female WT","Female HOM","Male WT","Male HOM","Female WT","Female HOM","Male WT","Male HOM"], labels: {            rotation: -45,            align: 'right',            style: {               fontSize: '15px',              fontFamily: 'Verdana, sans-serif'         }      },  },
plotOptions: {series:{ groupPadding: 0.25, pointPadding: -0.5 }}, yAxis: { max: 124.485,  min: 9.665,labels: { },title: { text: 'mg/dl' }, tickAmount: 5 },
series: [{ color: 'rgba(239, 123, 11,0.7)' , name: 'Observations', data:[[41.5595,63.0158,70.3612,77.32,98.7763]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(9, 120, 161,1.0)' , name: 'Observations', data:[[], [64.3689,68.4282,69.2014,71.1344,75.1937]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(239, 123, 11,0.7)' , name: 'Observations', data:[[], [], [51.3211,74.8071,83.8922,90.4644,113.9504]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(9, 120, 161,1.0)' , name: 'Observations', data:[[], [], [], [95.491,103.609,107.088,109.021,117.139]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },] });
</script>
							</div>
							<a href="${baseUrl}/charts?accession=MGI:1922246&allele_accession_id=MGI:5605792&zygosity=homozygote&parameter_stable_id=IMPC_CBC_016_001&pipeline_stable_id=IMPC_001&phenotyping_center=NING">Usp47<sup>tm1b(EUCOMM)Wtsi</sup></a>

						</div>
						<div class="col-4" style="text-align:center">
							<i class="fa fa-long-arrow-down large-arrow" aria-hidden="true"></i> in <img src="${baseUrl}/img/male.jpg">, no effect in <img src="${baseUrl}/img/female.jpg">
							<div id="boneMineralDensity">
								<script type="text/javascript">
										chart = new Highcharts.Chart({  colors:['rgba(239, 123, 11,0.7)', 'rgba(9, 120, 161,0.7)', 'rgba(247, 157, 70,0.7)', 'rgba(61, 167, 208,0.7)', 'rgba(247, 181, 117,0.7)', 'rgba(100, 178, 208,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(3, 77, 105,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(36, 139, 75,0.7)', 'rgba(255, 201, 67,0.7)', 'rgba(1, 121, 46,0.7)', 'rgba(144, 195, 212,0.7)', 'rgba(51, 51, 51,0.7)', 'rgba(119, 119, 119,0.7)', 'rgba(191, 151, 50,0.7)'], chart: { type: 'boxplot', renderTo: 'boneMineralDensity'},   tooltip: { formatter: function () { if(typeof this.point.high === 'undefined'){ return '<b>Observation</b><br/>' + this.point.y; } else { return '<b>Genotype: ' + this.key + '</b><br/>UQ + 1.5 * IQR: ' + this.point.options.high + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>Median: ' + this.point.options.median + '<br/>Lower Quartile: ' + this.point.options.q1 +'<br/>LQ - 1.5 * IQR: ' + this.point.low; } } }    , title: {  text: 'Bone Mineral Density', useHTML:true } ,  credits: { enabled: false },   legend: { enabled: false },  xAxis: { categories:  ["Female WT","Female HOM","Male WT","Male HOM","Female WT","Female HOM","Male WT","Male HOM"], labels: {            rotation: -45,            align: 'right',            style: {               fontSize: '15px',              fontFamily: 'Verdana, sans-serif'         }      },  },
											 plotOptions: {series:{ groupPadding: 0.25, pointPadding: -0.5 }}, yAxis: { max: 0.0567,  min: 0.0373,labels: { },title: { text: 'g/cm^2' }, tickAmount: 5 },
											 series: [{ color: 'rgba(239, 123, 11,0.7)' , name: 'Observations', data:[[0.0401,0.0449,0.0466,0.0481,0.0529]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(9, 120, 161,1.0)' , name: 'Observations', data:[[], [0.0421,0.0443,0.0452,0.0458,0.0481]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(239, 123, 11,0.7)' , name: 'Observations', data:[[], [], [0.0398,0.0455,0.0472,0.0493,0.055]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(9, 120, 161,1.0)' , name: 'Observations', data:[[], [], [], [0.038,0.0417,0.0435,0.0442,0.0479]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },] });
										</script>
							</div>
							<a href="${baseUrl}/charts?accession=MGI:1890081&allele_accession_id=MGI:5471395&zygosity=homozygote&parameter_stable_id=IMPC_DXA_004_001&pipeline_stable_id=JAX_001&phenotyping_center=JAX">Foxo3<sup>tm1.1(KOMP)Vlcg</sup></a>
						</div>
						<div class="col-4" style="text-align:center">
							<i class="fa fa-long-arrow-down large-arrow" aria-hidden="true"></i> in <img src="${baseUrl}/img/male.jpg">, <i class="fa fa-long-arrow-up large-arrow" aria-hidden="true"></i> in <img src="${baseUrl}/img/female.jpg">
							<div id="fructose">
								<script type="text/javascript">
										chart = new Highcharts.Chart({  colors:['rgba(239, 123, 11,0.7)', 'rgba(9, 120, 161,0.7)', 'rgba(247, 157, 70,0.7)', 'rgba(61, 167, 208,0.7)', 'rgba(247, 181, 117,0.7)', 'rgba(100, 178, 208,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(3, 77, 105,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(36, 139, 75,0.7)', 'rgba(255, 201, 67,0.7)', 'rgba(1, 121, 46,0.7)', 'rgba(144, 195, 212,0.7)', 'rgba(51, 51, 51,0.7)', 'rgba(119, 119, 119,0.7)', 'rgba(191, 151, 50,0.7)'], chart: { type: 'boxplot', renderTo: 'fructose'},   tooltip: { formatter: function () { if(typeof this.point.high === 'undefined'){ return '<b>Observation</b><br/>' + this.point.y; } else { return '<b>Genotype: ' + this.key + '</b><br/>UQ + 1.5 * IQR: ' + this.point.options.high + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>Median: ' + this.point.options.median + '<br/>Lower Quartile: ' + this.point.options.q1 +'<br/>LQ - 1.5 * IQR: ' + this.point.low; } } }    , title: {  text: 'Fructosamine', useHTML:true } ,  credits: { enabled: false },   legend: { enabled: false },  xAxis: { categories:  ["Female WT","Female HOM","Male WT","Male HOM","Female WT","Female HOM","Male WT","Male HOM"], labels: {            rotation: -45,            align: 'right',            style: {               fontSize: '15px',              fontFamily: 'Verdana, sans-serif'         }      },  },
											 plotOptions: {series:{ groupPadding: 0.25, pointPadding: -0.5 }}, yAxis: { max: 260.15,  min: 143.42,labels: { },title: { text: 'umol/l' }, tickAmount: 5 },
											 series: [{ color: 'rgba(239, 123, 11,0.7)' , name: 'Observations', data:[[174.28,199.16,207.26,215.75,240.63]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(9, 120, 161,1.0)' , name: 'Observations', data:[[], [194.45,209.98,216.12,220.33,235.86]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(239, 123, 11,0.7)' , name: 'Observations', data:[[], [], [170.4,194.86,203.31,211.17,235.63]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },{ color: 'rgba(9, 120, 161,1.0)' , name: 'Observations', data:[[], [], [], [160.11,175.28,179.02,185.39,200.55]],  tooltip: { headerFormat: '<em>Genotype No. {point.key}</em><br/>' }  },] });

								</script>
							</div>
							<a href="${baseUrl}/charts?accession=MGI:2446239&allele_accession_id=MGI:5637158&zygosity=homozygote&parameter_stable_id=IMPC_CBC_020_001&pipeline_stable_id=MGP_001&phenotyping_center=WTSI">Galnt18<sup>tm1b(KOMP)Wtsi</sup></a>
						</div>
					</div>
				</div>
			</div>
		</div>
    </jsp:body>


</t:genericpage-landing>


