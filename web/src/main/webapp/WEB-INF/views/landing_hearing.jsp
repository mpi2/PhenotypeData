<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/biological-system">biological systems</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

	<jsp:attribute name="header">

        <!-- CSS Local Imports -->
  
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
        <link href="${baseUrl}/css/biological_system/style.css"
			rel="stylesheet" />

        <!-- JS Imports -->
        <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript'
			src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script type="text/javascript"
			src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

        <!-- parallel coordinates JavaScriptdependencies -->

        <style>
/* Override allele ref style for datatable */
table.dataTable thead tr {
	display: table-row;
}
</style>


	</jsp:attribute>


	<jsp:attribute name="bodyTag">
		<body class="phenotype-node no-sidebars small-header">
	</jsp:attribute>

	<jsp:attribute name="addToFooter">

		 <div class="region region-pinned">

             <div id="flyingnavi" class="block smoothScroll">

                 <a href="#top"><i class="fa fa-chevron-up"
					title="scroll to top"></i></a>

                 <ul>
                     <li><a href="#top">Hearing</a></li>
                     <li><a href="#approach">Approach</a></li>
                     <!--  always a section for this even if says no phenotypes found - do not putting in check here -->

                     <li><a href="#manuscript">Manuscript</a></li>
                     <li><a href="#phenotypes-distribution">Phenotypes Distribution</a></li>

                         <%--<c:if test="${not empty impcImageFacets}">--%>
                     <li><a href="#gene-ko-effect">Gene KO Effect</a></li>
                     <li><a href="#vignettes">Vignettes</a></li>
                         <%--</c:if>--%>

                         <%--<c:if test="${not empty orthologousDiseaseAssociations}">--%>
                         <%--<li><a href="#disease-associations">Disease Associations</a></li>--%>
                         <%--</c:if>--%>

                         <%--<c:if test="${!countIKMCAllelesError}">--%>
                     <li><a href="#paper">Publications</a></li>
                         <%--</c:if>--%>
                 </ul>

                 <div class="clear"></div>

             </div>

         </div>
				<!--  end of floating menu for genes page -->

	</jsp:attribute>
	<jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">Hearing</h1>
							<div class="section">
    							<div class="inner">
    							 <p> ${shortDescription} </p>
    							</div>
    						</div>	
                        <%-- <c:import url="landing_overview_frag.jsp"/> removed as requested by Mike author of hearing paper --%>

                        <div style="padding: 30px;" class="clear both"></div>

                        <div class="section">
                            <h2 class="title" id="approach">Approach</h2>
                            <div class="inner">
                                <p>In order to identify the function of genes, the consortium uses a series of
                                    standardised protocols as described in IMPReSS (International Mouse Phenotyping Resource of Standardised Screens).</p>
                                <p>Hearing capacity is assessed using an <a
										href="http://www.mousephenotype.org/impress/protocol/149/7">Auditory Brain Stem
                                    response (ABR)</a> test conducted
                                    at 14 weeks of age. Hearing is assessed at five frequencies – 6kHz, 12kHz, 18kHz, 24kHz and 30kHz –
                                    as well as a broadband click stimulus.  Increased thresholds are indicative of abnormal hearing.
                                    Abnormalities in adult ear morphology are
                                    recorded as part of the <a
										href="http://www.mousephenotype.org/impress/protocol/186">Combined SHIRPA and Dysmorphology (CSD)</a> protocol, which 
                                    includes a response to a click box test (absence is indicative of a strong hearing deficit) and  visual inspection for behavioural signs that may indicate vestibular dysfunction e.g. head bobbing or circling.
                                </p>
                                

                                <c:import
									url="landing_procedures_frag.jsp" />
                            </div>
                        </div>
                        

                        <div class="section" id="manuscript">
                            <%--deafness manuscript --%>
                            <h2 class="title">IMPC Deafness Publication</h2>
                            <div class="inner">
                               <h3>Hearing loss investigated in 3,006 knockout mouse lines</h3>
                               <p>
									<a
										href="http://bit.ly/IMPCDeafness">A large scale hearing loss screen reveals an extensive unexplored genetic landscape for auditory dysfunction<br/>
										Nature Communications (released 12/10/2017)</a>
									
								</p>

                               	<ul>
                               	<li>
                               		67 genes identified as candidate hearing loss genes
                               	</li>
                               	<li>
                               		52 genes are not previously associated with hearing loss and encompass a wide range of functions from structural proteins to transcription factors                               	
                               	</li>
                               	<li>
					Among the novel candidate genes, <i>Atp2b1</i> is expressed in the inner ear and <i>Sema3f</i> plays a role in sensory hair cell innervation in the cochlea
                               	</li>
                               	<li>
                               		The IMPC will continue screening for hearing loss mutants in its second 5 year phase
                   								
                               	</li>
                               	</ul>
                                
				<p>Press releases: <a
										href="https://www.ebi.ac.uk/about/news/press-releases/hearing-loss-genes/">EMBL-EBI</a>&nbsp;|&nbsp;<a
										href="https://www.mrc.ac.uk/news/browse/genes-critical-for-hearing-identified/">MRC</a>&nbsp;|&nbsp;<a
										href="http://bit.ly/DeafnessNewsStory">IMPC</a><!--&nbsp;|&nbsp;<a
										href="http://www.sanger.ac.uk/news/view/study-reveals-how-sex-blindspot-could-misdirect-medical-research">Sanger</a> -->
								</p>

                               	<h3>
                               		Methods
                               	</h3>
                               	<p>Response data from the <a
										href="http://www.mousephenotype.org/impress/protocol/149/7">Auditory Brain Stem
                                    response (ABR)</a> test was used – hearing at five frequencies, 6kHz, 12kHz, 18kHz, 24kHz and 30kHz was measured.</p>
								<ul>
								<li>
									Control wildtype mice from each phenotypic centre included, matched for gender, age, phenotypic pipeline and metadata (e.g. instrument)
								</li>
								<li>
									Our production statistical approach that automatically detects mutants with abnormal hearing was manually curated to yield 67 genes with profound hearing loss
								</li>
								</ul>
                         
                               	<h3>
                               		Gene table
                               	</h3>
				<p>
									<a
										href="https://static-content.springer.com/esm/art%3A10.1038%2Fs41467-017-00595-4/MediaObjects/41467_2017_595_MOESM1_ESM.pdf">Supplementary Material</a>
								</p>
				<p>Sixty-seven deafness genes were identified:</p>

                                <table id="hearing-genes"
									class="table tableSorter">
                                    <thead>
                                    <tr>
                                        <th class="headerSort ">Gene symbol</th>
                                        <th class="headerSort">Zygosity</th>
                                        <th class="headerSort">Status</th>
                                        <th class="headerSort">Hearing loss</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2442934">A730017C20Rik</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1098687">Aak1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1354713">Acsl4</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:102806">Acvr2a</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1933736">Adgrb1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1274784">Adgrv1</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:107189">Ahsg</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1924337">Ankrd11</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1929214">Ap3m2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1337062">Ap3s1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:104653">Atp2b1</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:3588238">B020004J07Rik</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2652819">Baiap2l2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1915589">Ccdc88c</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:106485">Ccdc92</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1929293">Cib2</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2388124">Clrn1</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:88466">Col9a2</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2444415">Cyb5r2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:103157">Dnase1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1914061">Duoxa2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:3583900">Elmod1</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:95321">Emb</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1914675">Eps8l1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:99960">Ewsr1</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:95662">Gata2</a></td>
											<td>Het</td>
											<td>Known</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2146207">Gga1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2387006">Gipc3</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2685519">Gpr152</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1333877">Gpr50</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1914393">Ikzf5</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:96546">Il1r2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2146574">Ildr1</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:107953">Klc2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2143315">Klhl18</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2446166">Marveld2</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1914249">Med28</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1343489">Mpdz</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1339711">Myh1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:104510">Myo7a</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1933754">Nedd4l</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:103296">Nfatc3</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:105108">Nin</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1928323">Nisch</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:108077">Nptn</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:97401">Ocm</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2686003">Odf3l2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2149209">Otoa</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1918248">Phf6</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:99878">Ppm1a</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1096347">Sema3f</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2150150">Slc4a10</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2149330">Slc5a5</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2384936">Spns2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1916205">Srrm4</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2442082">Tmem30b</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1921050">Tmtc4</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2181659">Tox</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2139535">Tprn</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1924817">Tram2</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:102944">Ube2b</a></td>
											<td>Het</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1914378">Ube2g1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1919338">Ush1c</a></td>
											<td>Hom</td>
											<td>Known</td>
											<td alt="d">Severe</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:1855699">Vti1a</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="b">Mild</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2685541">Wdtc1</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="c">High</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2159407">Zcchc14</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="a">Low</td>
										</tr>
                                    <tr>
											<td><a
												href="http://www.mousephenotype.org/data/genes/MGI:2444708">Zfp719</a></td>
											<td>Hom</td>
											<td>Novel</td>
											<td alt="d">Severe</td>
										</tr>
                                    </tbody>
                                </table>
				    
                            </div>
                        </div>

                        <script>
							$(document).ready(function() {
								$('#hearing-genes').DataTable({
									"bDestroy" : true,
									"searching" : false,
									"bPaginate" : true,
									"sPaginationType" : "bootstrap",
									"columnDefs": [
										{ "type": "alt-string", targets: 3 }   //4th col sorted using alt-string
									],
									"aaSorting": [[0, "asc"]], // 0-based index
									"aoColumns": [
									    null, null,null,
//										 {"sType": "html", "bSortable": true},
//										 {"sType": "string", "bSortable": true},
//										 {"sType": "string", "bSortable": true},
										 {"sType": "html", "bSortable": true}
									]
								});
							});
							</script>

                        <style>
/* required for the graphs to not clip the axis labels */
.highcharts-root {
	padding: 10px;
}

.inner .half h3 {
	font-weight: bold;
	color: rgb(191, 75, 50);
}
</style>

                        <div style="padding: 30px;" class="clear both"></div>

                        <div class="section">
                            <h2>
                                Vignettes
                            </h2>
                            <div class="inner">
                                <div class="half"
									style="text-align: center">
                                    <h3>Novel, mild hearing loss</h3>
                                    <a
										href="${baseUrl}/genes/MGI:1337062">Ap3s1<sup>tm1b(EUCOMM)Hmgu</sup></a>
                                    <div class="chart" id="tram2"
										graphUrl="${baseUrl}/chart?accession=MGI:1337062&parameter_stable_id=IMPC_ABR_008_001&chart_type=UNIDIMENSIONAL_ABR_PLOT&pipeline_stable_id=HMGU_001&zygosity=homozygote&phenotyping_center=HMGU&strain_accession_id=MGI:2164831&allele_accession_id=MGI:5548436&metadata_group=d5fe13bb3cac96104edd5493d1c33d63&chart_only=true">
                                        <div id="spinner_tram2">
											<i class="fa fa-refresh fa-spin"></i>
										</div>
                                    </div>

                                </div>
                                <div class="half"
									style="text-align: center">
                                    <h3>Known, severe hearing loss</h3>
                                    <a
										href="${baseUrl}/genes/MGI:3583900">Elmod1<sup>tm1b(EUCOMM)Hmgu</sup></a>
                                    <div class="chart" id="ush1c"
										graphUrl="${baseUrl}/chart?accession=MGI:3583900&parameter_stable_id=IMPC_ABR_006_001&chart_type=UNIDIMENSIONAL_ABR_PLOT&pipeline_stable_id=HRWL_001&zygosity=homozygote&phenotyping_center=MRC%20Harwell&strain_accession_id=MGI:2164831&allele_accession_id=MGI:5548895&metadata_group=e35a6844b77bb571edd0697582d736bb&chart_only=true">
                                        <div id="spinner_ush1c">
											<i class="fa fa-refresh fa-spin"></i>
										</div>
                                    </div>
                                </div>

                            <div style="padding: 30px;"
									class="clear both"></div>

                                <div class="half"
									style="text-align: center">
                                    <h3>Novel, high-frequency hearing loss</h3>
                                    <a
										href="${baseUrl}/genes/MGI:1915589">Ccdc88c<sup>tm1b(KOMP)Mbp</sup></a>
                                    <div class="chart" id="wdtc1"
										graphUrl="${baseUrl}/chart?accession=MGI:1915589&parameter_stable_id=IMPC_ABR_012_001&chart_type=UNIDIMENSIONAL_ABR_PLOT&pipeline_stable_id=UCD_001&zygosity=homozygote&phenotyping_center=UC%20Davis&strain_accession_id=MGI:2683688&allele_accession_id=NULL-387FB49D6&chart_only=true">
                                        <div id="spinner_wdtc1">
											<i class="fa fa-refresh fa-spin"></i>
										</div>
                                    </div>
                                </div>

                            <div class="half" style="text-align: center">
                                <h3>Novel, severe hearing loss</h3>
                                <a href="${baseUrl}/genes/MGI:2444708">Zfp719<sup>tm1b(EUCOMM)Wtsi</sup></a>
                                <div class="chart" id="zfp719"
										graphUrl="${baseUrl}/chart?accession=MGI:2444708&parameter_stable_id=IMPC_ABR_012_001&chart_type=UNIDIMENSIONAL_ABR_PLOT&pipeline_stable_id=MGP_001&zygosity=homozygote&phenotyping_center=WTSI&strain_accession_id=MGI:2159965&allele_accession_id=MGI:5548829&chart_only=true">
                                    <div id="spinner_zfp719">
											<i class="fa fa-refresh fa-spin"></i>
										</div>
                                </div>
                            </div>

                            <div class="clear both"></div>
                            </div>

                        <script>
							//ajax chart caller code
							$(document).ready(function() {
								$('.chart').each(function(i, obj) {
									var graphUrl = $(this).attr('graphUrl');
									var id = $(this).attr('id');
									var chartUrl = graphUrl+ '&experimentNumber='+ id;
									console.log(chartUrl);
									$.ajax({
										url : chartUrl,
										cache : false
									}).done(function(html) {
                                        $('#' + id).append(html);
                                        $('#spinner_' + id).html('');
                                    });
								});
							});
						</script>

                        </div>
                        

                        <!-- <div class="section">
                            <h2 class="title"
								id="phenotypes-distribution">Phenotypes distribution</h2>
                            <div class="inner">
                                <p>This graph shows genes with a significant effect on at least one hearing phenotype.</p>
                                <p></p>
                                <div id="phenotypeChart">
                                    <script type="text/javascript">
																																					$(function() {
																																						$
																																						{
																																							phenotypeChart
																																						}
																																					});
																																				</script>
                                </div>
                            </div>
                        </div> -->

                        <%-- <div class="section" id="gene-ko-effect"> removed this section as per TM instructions

                            <h2 class="title">Gene KO effect comparator for ${systemName} continuous parameters</h2>

                            <div class="inner">

                                <p>Visualize continuous parameters used by the consortium to assess hearing phenotypes.
                                    The measurement values are corrected to account for batch effects to represent the true genotype effect
                                    thus allowing a side by side comparison/visualisation.</p>
                                <p>Use this interactive graph and table:</p>


                                <li>Drag your mouse pointer on any parameter axis to select a region of interest, while the associated gene/s will be automatically filtered for in the gene table below. You can click on a line to highlight it and filter by procedure from the “Procedures” list. Click on the parameter name to know more about it – you get redirected to the IMPReSS pages.</li>
                                <li>Click on any row in the gene table (space next to the gene name) to highlight the corresponding values in the graph above, or click on the gene name to open the associated gene page. When you select a gene row, the parameter name in the graph will change to orange if genotype is significant.</li>
                                <li>Click “Clear filters” to return to the default view.</li>
                                <p></p><p></p>

                                <div id="widgets_pc" class="widgets">	</div>
                                <div id="spinner"><i class="fa fa-refresh fa-spin"></i></div>
                                <div id="chart-and-table"> </div>
                                <script>
                                    $(document).ready(function(){
                                        var base_url = '${baseUrl}';
                                        var tableUrl = base_url + "/parallelFrag?top_level_mp_id=${mpId}";
                                        $.ajax({
                                            url: tableUrl,
                                            cache: false
                                        })
                                            .done(function( html ) {
                                                $( '#spinner' ).hide();
                                                $( '#chart-and-table' ).html( html );
                                            });
                                    })
                                </script>
                            </div>
                        </div> --%>

                       

                        <%-- commented out venn diaggram for now as disease classification that it relies on is problematic in phenodigm --%>
                        <%--<div class="section">--%>

                            <%--<h2 id="disease-associations" class="title">Hearing/Vestibular/Ear disease associations by orthology and phenotypic similarity</h2>--%>
                            <%--<div class="inner">--%>

                                <%--<p>Venn diagrams showing different sets of mouse genes potentially associated to hearing/vestibular/ear (HVE) system diseases using different methodologies.</p>--%>
                                <%--<ul>--%>
                                    <%--<li>The <b>IMPC HVE phenotypes set</b> contains all mouse genes associated to abnormal? hearing/vestibular/ear system phenotypes using the IMPC pipeline of dedicated screens (above).</li>--%>
                                    <%--<li>The <b>IMPC HVE disease predicted set</b> contains all mouse gene that are candidates for hearing/vestibular/ear system diseases based on the phenotypic similarity between the disease clinical symptoms described for humans and the phenotype annotations from the IMPC. The phenotypic similarity is calculated using the PhenoDigm algorithm, which allows the integration of data from model organisms and humans to identify gene candidates for human genetic diseases.--%>
                                    <%--</li>--%>
                                    <%--<li>The <b>orthologs to HVE human genes set</b> contains mouse gene orthologs to known genes causing HVE system diseases in humans.</li>--%>
                                <%--</ul>--%>

                                <%--<p>These data sets as well as the data at the intersections can be obtained using the download icon below.</p>--%>
                                <%--<div class="half">--%>
                                    <%--<jsp:include page="gene_orthologs_frag.jsp" >--%>
                                        <%--<jsp:param name="currentSet" value="impcSets"/>--%>
                                        <%--<jsp:param name="divId" value="impcVenn"/>--%>
                                    <%--</jsp:include>--%>
                                <%--</div>--%>
                                    <%--&lt;%&ndash;<div class="half">&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;<jsp:include page="gene_orthologs_frag.jsp" >&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;<jsp:param name="currentSet" value="mgiSets"/>&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;<jsp:param name="divId" value="mgiVenn"/>&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;</jsp:include>&ndash;%&gt;--%>
                                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>

                                <%--<div class="clear both"></div>--%>

                                <%--&lt;%&ndash;<a id="tsvDownload" href="${baseUrl}/orthology.tsv?diseaseClasses=cardiac&diseaseClasses=circulatory system&diseaseClasses=cardiac malformations&mpId=MP:0005385&phenotypeShort=CV" download="diseases_${systemName}" target="_blank" class="button fa fa-download">Download</a>&ndash;%&gt;--%>
                                <%--<a id="tsvDownload"  download="diseases_${systemName}" target="_blank" class="button fa fa-download">Download will be updated</a>--%>


                            <%--</div>--%>

                        <%--</div>--%>



						<div class="section">
                            <h2 class="title">Phenotypes distribution</h2>
                            <div class="inner">
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                                </div>
                            </div>
                        </div>
                        
                        
                        <div class="section" id="paper">
                            <jsp:include page="paper_frag.jsp"></jsp:include>
                        </div>

                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


