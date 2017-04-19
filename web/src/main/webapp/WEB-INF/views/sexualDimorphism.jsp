<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Sexual Dimorphism</jsp:attribute>

    <jsp:attribute name="bodyTag">	</jsp:attribute>
    <jsp:attribute name="header">
    	<!-- JS Imports -->
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
    </jsp:attribute>
 		

    <jsp:body>


        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">

                        <h1 class="title" id="top">Sexual dimorphism</h1>

		                        <div class="section">
		                            <div class=inner>
		                                
		                                <h4> IMPC data demonstrates the effect of sex in many phenotypes, regardless of biological system, supporting the need to address the effect of sex in biomedical studies
										</h4>
		                                <ul class="pad10px">
		                                    <li><a href="">In the News</a></li>
		                                    <li> <a href="http://doi.org/10.5281/zenodo.160267?">Supporting material to enable replicable analysis</a></li>
		                                    <li>Manuscript and Supplemental Information</li>
		                                </ul>
		                                <br/> <br/> <br/>
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
		                        </div>	<!-- section -->
		                        
		                        <div class="section">
		                        <h2>
		                        Sexual dimorphism in 14,250 wildtype mice
		                        </h2>
		                        	<div class="inner">
		                        	
		                        	 <div class="half">
		                				<div id="wtCategorical">
		                    				<script type="text/javascript">$(function () { $('#wtCategorical').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Categorical' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'Male Greater', y: 3.7, sliced: true, selected: true },{ name: 'Female Greater', y: 6.2, sliced: true, selected: true },  [' No Sex Effect Detected', 90.1 ] ]  }] }); });</script>
		                				</div>
		            				</div>
		           					 <div class="half">
		                				<div id="wtContinuous">
		                    				<script type="text/javascript">$(function () { $('#wtContinuous').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Continuous' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'Male Greater', y: 30.6, sliced: true, selected: true },{ name: 'Female Greater', y: 26.6, sliced: true, selected: true },  [' No Sex Effect Detected', 90.1 ] ]  }] }); });</script>
		                				</div>
		            				</div>
		                        	The effects of sex on newly and previously collected phenotypes, following the IMPC adult phenotype pipeline (IMPReSS), from wildtype C57BL/6N mice at ten global research centers. Sex effect was detected in  9.9% categorical phenotypes (545 examined, e.g. abnormal corneal opacity)  and in 56.6% continuous phenotypes (903 examined, e.g. blood glucose levels; 5%FDR)
		                        	
		                        	</div>
		                        	
		                        	<div class="clear both"></div>
		                        
		                    	</div>
		                    	
		                    	
		                    	
		                    	<div class="section">
		                        <h2>
		                        Sexual dimorphism in 2,186 knockout mouse strains

		                        </h2>
		                        	<div class="inner">
		                        	
		                        	 <div class="half">
		                				<div id="koCategorical">
		                    				<script type="text/javascript">$(function () { $('#koCategorical').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Categorical' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'Male Greater', y: 6.8, sliced: true, selected: true },{ name: 'Female Greater', y: 6.5, sliced: true, selected: true },  [' No Sex Effect Detected', 86.7 ] ]  }] }); });</script>
		                				</div>
		            				</div>
		           					 <div class="half">
		                				<div id="koContinuous">
		                    				<script type="text/javascript">$(function () { $('#koContinuous').highcharts({  chart: { plotBackgroundColor: null, plotShadow: false},  colors:['rgba(239, 123, 11,1.0)', 'rgba(9, 120, 161,1.0)', 'rgba(119, 119, 119,1.0)', 'rgba(238, 238, 180,1.0)', 'rgba(36, 139, 75,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(255, 201, 67,1.0)', 'rgba(191, 151, 50,1.0)', 'rgba(239, 123, 11,1.0)', 'rgba(247, 157, 70,1.0)', 'rgba(247, 181, 117,1.0)', 'rgba(191, 75, 50,1.0)', 'rgba(151, 51, 51,1.0)', 'rgba(144, 195, 212,1.0)'],  title: {  text: 'Continuous' },  credits: { enabled: false },  tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'}, plotOptions: { pie: { size: 200, allowPointSelect: true, cursor: 'pointer', dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', style: { color: '#666', width:'60px' }  }  },series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} } }, series: [{  type: 'pie',   name: '',  data: [ { name: 'One Sex', y: 12.8, sliced: true, selected: true }, { name: 'Diferent Size', y: 0.8, sliced: true, selected: true },{ name: 'Different Directions', y: 3.5, sliced: true, selected: true }, { name: 'Cannot Classify', y: 0.6, sliced: true, selected: true },  [' Genotype Effect with No Sex Effect', 82.2 ] ]  }] }); });</script>
		                				</div>
		            				</div>
		                        	Analyses on phenotypes collected following the IMPC adult phenotype pipeline (IMPReSS) from knockout mice at ten global research centers. Sex effect was detected in 13.3% categorical phenotypes (1,220 examined; 20%FDR)  and in 17.7% continuous phenotypes (7,929 examined; 5% FDR) in phenotypes with a significant genotype effect.

		                        	</div>
		                        	
		                        	<div class="clear both"></div>
		                        
		                    	</div>


                        </div>
                    </div>

                </div>
        </div>

    </jsp:body>


</t:genericpage>

