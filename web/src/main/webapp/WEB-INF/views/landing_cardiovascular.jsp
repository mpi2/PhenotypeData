<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>


<t:genericpage>

    <jsp:attribute name="title">${systemName} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/biological-system">Biological Systems</a> &nbsp;&raquo; ${systemName}</jsp:attribute>

    <jsp:attribute name="header">
        <!-- CSS Local Imports -->
		<link rel="stylesheet" href="${baseUrl}/css/vendor/slick.grid.css" type="text/css" media="screen"/>
		<link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css" />
        <link href="${baseUrl}/css/alleleref.css" rel="stylesheet" />
        <link href="${baseUrl}/css/biological_system/style.css" rel="stylesheet" />

        <!-- JS Imports -->
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

        <!-- parallel coordinates JavaScriptdependencies -->

		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.csv.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>
        <script src="${baseUrl}/js/vendor/jquery/jquery.event.drag-2.0.min.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.core.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.grid.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.dataview.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.pager.js"></script>

	</jsp:attribute>


    <jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="addToFooter">



	</jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">${systemName} </h1>

                        <c:import url="landing_overview_frag.jsp"/>
                        <br/><br/>

                        <div class="section">
                            <h2 class="title">Approach</h2>
                             <div class="inner">

                                <p> To measure ${systemName} function in the mouse, IMPC uses a series of standardised protolcols. These protocols are described in <a href="${baseUrl}/../impress">IMPReSS</a> (International Mouse Phenotyping Resource of Standardised Screens). </p>
                                <p>Heart and vascular function/physiology are measured through several procedures like echocardiography and electrocardiogram, Non-Invasive blood pressure for example. Cardiovascular system morphology is assessed through macroscopic and microscopic measurements, like heart weight, gross pathology and gross morphology in both embryo and adult animals. A complete list of protocols and related phenotypes are presented in the table below. Links to impress are provided for more details on the procedure. </p>
                                <br/><br/>
                                <c:import url="landing_procedures_frag.jsp"/>
                            </div>
                        </div>


                        <%--removed images section until we have appropriate associated images
                         <div class="section">
                            IMPC images
                            <c:if test="${not empty impcImageGroups}">
                                <div class="section" id="imagesSection">
                                    <h2 class="title">Associated Images </h2>
                                    <div class="inner">
                                        <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                    </div>
                                </div>
                            </c:if>
                        </div> --%>


                        <div class="section">

                            <h2 class="title">Phenotypes distribution</h2>
                            <div class="inner">
                                <p>The following graph represents the distribution of genes according to their phenotypes. Genes have at least one phenotype linked to cardiovascular system.
                                </p>
                                <br/> <br/>
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                                </div>

                                <br/><br/>
                                    <p>The following diagram represents the various biological system phenotypes associations for genes linked to cardiovascular system phenotypes. The line thickness is correlated with the strength of the association.</p>
                                    <p>Clicking on chosen phenotype(s) on the diagram allow to select common genes. Corresponding gene lists can be downloaded using the download icon.</p>
                                <br/>
                                <div id="chordContainer"></div>
                                <svg id="chordDiagramSvg" width="960" height="960"></svg>
                                <script>
                                    var mpTopLevelTerms = ["cardiovascular system phenotype"];
                                    drawChords("chordDiagramSvg", "chordContainer", false, mpTopLevelTerms, false, null);
                                </script>
                            </div>
                        </div>


                        <div class="section">

                            <h2 class="title">Gene KO effect comparator for ${systemName} continuous parameters</h2>

                            <div class="inner">
                                <p>Visualize multiple strain across several continuous parameters used for ${systemName} phenotyping.
                                The measurement values are corrected to account for batch effects to represent the true genotype effect thus allowing
                                a side by side comparison/visualisation. Only continuous parameters can be visualized using this methodology.
                                Results are represented with a graph and a table.</p>

                                <p>How to use the tool?</p>
                                <p>You can unselect/select ${systemName} procedures by clicking on the term directly.
                                    The graph is interactive and allows filtering on each axis (parameter) by selecting the region of interest. Several regions of interests can be selected one by one.
                                    Clicking on a chosen line on the graph or on a gene row from the table will highlight the corresponding gene. For a selected gene,
                                    if any significant phenotype is associated with a parameter, the parameter colour will change to orange.
                                </p>

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
                        </div>


                        <div class="section">

                            <h2 class="title">Cardiovascular disease associations by orthology and phenotypic similarity</h2>
                            <div class="inner">
                            
                            <p>
									<b>These Venn diagrams represent different sets of mouse genes potentially associated to ${systemName} diseases using different methodologies.</b>
									</p>
									<p> 
The human curated gene set contains mouse gene orthologs to known genes causing ${systemName} diseases in humans.
The <span class="ven_phenotype">IMPC phenotype</span> set contains all genes displaying ${systemName} phenotypes.
Finally, the IMPC predicted or MGI predicted set contains predicted mouse gene candidates for ${systemName} diseases based on the phenotypic similarity of the disease clinical symptoms
and the mouse phenotype annotations. As stated, the source of the mouse model phenotype is either <a href="http://www.informatics.jax.org/">Mouse Genome Informatics</a>, which is a curated set of mouse models
 from the literature, or IMPC produced mice. The phenotypic similarity is calculated using the <a target="_blank" href="http://www.sanger.ac.uk/science/tools/phenodigm">PhenoDigm</a>
  algorithm developed by the Monarch Initiative which allows integration of data from model organisms to identify gene candidates for human genetic diseases.
The complete dataset can be downloaded using the download icon.

									</p>

                            <div class="half">
                                <jsp:include page="gene_orthologs_frag.jsp" >
                                    <jsp:param name="currentSet" value="impcSets"/>
                                    <jsp:param name="divId" value="impcVenn"/>
                                </jsp:include>
                            </div>
                            <div class="half">
                                <jsp:include page="gene_orthologs_frag.jsp" >
                                    <jsp:param name="currentSet" value="mgiSets"/>
                                    <jsp:param name="divId" value="mgiVenn"/>
                                </jsp:include>
                            </div>

                            <div class="clear both"></div>

                            <a id="tsvDownload" href="${baseUrl}/orthology.csv?diseaseClasses=cardiac&diseaseClasses=circulatory system&diseaseClasses=cardiac malformations&mpId=MP:0005385" download="diseases_${systemName}" target="_blank" class="button fa fa-download">Download</a>

			
                            </div>
       
                        </div>

                        <div class="section">
                            <jsp:include page="paper_frag.jsp"></jsp:include>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>


