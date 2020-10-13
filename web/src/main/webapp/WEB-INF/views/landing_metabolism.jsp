<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage-landing>

	<jsp:attribute name="title">${pageTitle}</jsp:attribute>
	<jsp:attribute name="pagename">${pageTitle}</jsp:attribute>
	<jsp:attribute name="breadcrumb">${systemName}</jsp:attribute>

	<jsp:attribute name="header">

	<!-- CSS Local Imports -->
    <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
    <link href="${baseUrl}/css/heatmap.css" rel="stylesheet" />

	<script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
    <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>

    <script
			src="https://blacklabel.github.io/grouped_categories/grouped-categories.js"></script>
    
    <style>
table {
	border-collapse: collapse;
	border-spacing: 0;
}
/* Override allele ref style for datatable */
table.dataTable thead tr {
	display: table-row;
}

#metabolism-table_length {
	width: 50%;
	float: left;
	/* text-align: right; */
}

#metabolism-table_filter {
	width: 50%;
	float: right;
	text-align: right;
}
/* .background_hover_axis {
			background-color: rgb(173,216,230);
		} */
</style>
	
	</jsp:attribute>

	<jsp:attribute name="bodyTag"><body class="phenotype-node no-sidebars small-header"> </jsp:attribute>

	<jsp:attribute name="addToFooter">
	    <script type='text/javascript'
				src='${baseUrl}/js/charts/modules/heatmap.js?v=${version}'></script>
    <script type="text/javascript"
			src='${baseUrl}/js/charts/heatMapMetabolism.js?v=${version}'></script>

	</jsp:attribute>

	<jsp:body>

		<div class="container">

			<div class="row">
				<div class="col-12">
					<h2>The IMPC is increasing our understanding of the genetic basis for metabolic diseases</h2>
					<ul>
							<li>Metabolic diseases, such as obesity and diabetes, affect people worldwide</li>
							<li>The function of many genes in the genome is still unknown</li>
							<li>Knockout mice allow us to understand metabolic procedures and relate them to human disease</li>
						</ul>
						Press releases:
						<a target='_blank' href='https://www.ebi.ac.uk/about/news/press-releases/mouse-study-identifies-new-diabetes-genes'>EMBL-EBI</a>&nbsp;|&nbsp;
						<a target='_blank' href='https://www.infrafrontier.eu/news/new-diabetes-genes-discovered-international-mouse-phenotyping-study'>Infrafrontier</a>&nbsp;|&nbsp;
						<a target='_blank' href='${cmsBaseUrl}/blog/2018/04/05/new-diabetes-genes-discovered-in-latest-impc-research/'>IMPC</a>
						</br><a target='_blank' href='http://bit.ly/IMPCMetabolism'>Nature communications publication</a>
						</br><a target='_blank' href='http://bit.ly/MetabolismSuppMaterial'>Supporting information</a>
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2 id="status" class="title">Approach</h2>
					<p align="justify">
						To identify the function of genes, the IMPC uses a series of standardised protocols described in <a
						href="${baseUrl}/../impress">IMPReSS</a> (International Mouse Phenotyping Resource of Standardised Screens).
						Tests addressing the metabolic function are conducted on young adult mice at 11-16 weeks of age.
					</p>

					<h4>Procedures that can lead to relevant phenotype associations</h4>
					<c:import url="landing_procedures_frag.jsp" />
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2 id="metabolismPaper" class="title">IMPC Metabolism Publication</h2>
					<h3>Metabolic diseases investigated in 2,016 knockout mouse lines</h3>
					<p>
						<a target='_blank' href="https://bit.ly/IMPCMetabolism">Nature Communications publication</a>
					</p>
					<ul>
						<li>974 genes were identified with strong metabolic phenotypes (see gene table, below).</li>
						<li>429 genes had not been previously associated with metabolism, 51 completely lacked functional annotation, and 25 had single nucleotide polymorphisms associated to human metabolic disease phenotypes.</li>
						<li>515 genes linked to at least one disease in OMIM.</li>
						<li>Networks of co-regulated genes were identified, and genes of predicted metabolic function found.</li>
						<li>Pathway mapping revealed sexual dimorphism in genes and pathways.</li>
						<li>This investigation is based on about 10% of mammalian protein-coding genes. The IMPC will continue screening for genes associated to metabolic diseases in its second 5 year phase.</li>
					</ul>

					<h3>Gene table</h3>
					<p>Mutant/wildtype ratios below the 5th percentile and above the 95th percentile of the ratio
						distributions yielded 28 gene lists that serve as a data mining resource for further
						investigation into potential links to human metabolic disorders.</p>
					<p>By hovering over the table you can select cells and click to explore the underlying data.</p>

					<div id="heatMapContainer" style="height: 450px; min-width: 310px; max-width: 894px; position: relative;"></div>
					<div id="metabolismTableDiv" style="display: none; position: relative; z-index: 10; margin-top: -50px;">
						<table id="metabolism-table" class='table tableSorter'>
							<thead>
							<tr>
									<th>Parameter</th>
									<th>Sex</th>
									<th>MGI_ID</th>
									<th>Gene_symbol</th>
									<th>Center</th>
									<th>Zygosity</th>
									<th>Ratio_KO_WT</th>
									<th>Tag</th>
								</tr>
							</thead>
							<!-- BODY -->
						</table>
					</div>

					<h3>Strong metabolic phenotype genes form regulatory networks</h3>
					<div class="row">
						<div class="col-6">
							<img src="${baseUrl}/documentation/img/more-cassette.png"
								 alt="Illustration of the action of MORE cassettes in regulatory networks"
								 width="100%" />
						</div>
						<div class="col-6">
							<ul>
								<li>Transcriptional co-regulation often involves a common set of transcription factor binding sites (TFBSs) shared between co-regulated promoters and in a particular organization (known as Multiple Organized Regulatory Element (MORE)–cassettes).</li>
								<li>Identification of shared MORE-cassettes in promoters of candidate genes allowed to discover extensive metabolic phenotype-associated networks of potentially co-regulated genes.</li>
								<li>MORE-cassettes are invariant genomic sequence features (similar to reading frames).</li>
								<li>The presence of MORE-cassettes enabled to a priori predict phenotypes and identify genes potentially linked to metabolic functions.</li>
							</ul>
						</div>
					</div>

					<h3>Methods</h3>
					<p>Genes with phenotypes associated to the following <b>seven metabolic parameters</b>, with
						diagnostic relevance in human clinical research, were further analysed:</p>
					<ul>
						<li>Fasting basal blood glucose level before glucose tolerance test (T0)</li>
						<li>Area under the curve of blood glucose level after intraperitoneal glucose administration
							relative to basal blood glucose level (AUC)
						</li>
						<li>Plasma triglyceride levels (TG)</li>
						<li>Body mass (BM)</li>
						<li>Metabolic rate (MR)</li>
						<li>Oxygen consumption rate (VO2)</li>
						<li>Respiratory exchange ratio (RER) – a measure of whole-body metabolic fuel utilization</li>
					</ul>
					<p>Mutant/wildtype ratios (mean value of metabolic parameters of mutants divided by the mean value
						obtained for wildtypes) were calculated:</p>
					<ul>
						<li>Control wildtype mice from each phenotypic center included, matched for gender, age,
							phenotypic pipeline and metadata (e.g. instrument).
						</li>
						<li>Males and females analyzed separately to account for sexual dimorphism.</li>
					</ul>

					<h3>New mouse models</h3>
					<ul>
						<li>IMPC generated and identified new genetic disease models.</li>
						<li>New models available to the research community to perform in-depth investigations of novel
							genetic elements associated to metabolic disease mechanisms.
						</li>
						<li>These models fill the gap between genome-wide association studies and functional validation
							using a mammalian model organism.
						</li>
					</ul>
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<h2 id="phenotypes-distribution" class="title">Phenotype distribution</h2>
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>							
                                </div>
				</div>
			</div>

			<div class="row">
				<div class="col-12">
					<div id="paper" class="section">
						<jsp:include page="paper_frag.jsp"></jsp:include>
					</div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage-landing>
