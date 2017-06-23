<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${pageTitle} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/landing">Landing
        Pages</a> &nbsp;&raquo; ${pageTitle}</jsp:attribute>

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

                        <c:import url="landing_overview_frag.jsp"/>

                        <div class="section">
                            <h2 class="title" id="approach">Approach</h2>
                            <div class="inner">
                                <p>In order to identify the function of genes, the consortium uses a series of
                                    standardised protocols as described in IMPReSS (International Mouse Phenotyping Resource of Standardised Screens).</p>
                                <p>Hearing capacity is assessed using an <a
                                        href="https://www.mousephenotype.org/impress/protocol/149/7">auditory brain stem
                                    response</a> (ABR) test conducted
                                    at 14 weeks of age. Hearing is assessed at five frequencies – 6kHz, 12kHz, 18kHz, 24kHz and 30kHz –
                                    as well as a broadband click stimulus.  Increased thresholds are indicative of abnormal hearing.
                                    Abnormalities in adult ear morphology are
                                    recorded as part of the “Combined SHIRPA and Dysmorphology” protocol and in the developing embryo during gross pathology examination.
                                    Full procedures details are provided in the list below.
                                </p>
                                </p>

                                <c:import url="landing_procedures_frag.jsp"/>
                            </div>
                        </div>

                        <div class="section" id="manuscript">
                            <%--deafness manuscript --%>
                            <h2 class="title">IMPC Deafness Manuscript</h2>
                            <div class="inner">
                                Coming soon.....
                            </div>
                        </div>

                        <div class="section">
                            <h2 class="title" id="phenotypes-distribution">Phenotypes distribution</h2>
                            <div class="inner">
                                <p>This graph shows genes with a significant effect on at least one hearing phenotype.</p>
                                <p></p>
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                                </div>
                            </div>
                        </div>

                        <div class="section" id="gene-ko-effect">

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
                        </div>

                        <div class="section">
                                <%--IMPC images--%>
                            <c:if test="${not empty impcImageGroups}">
                                <div class="section" id="vignettes">
                                    <h2 class="title">Vignettes </h2>
                                    <div class="inner">
                                        <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                    </div>
                                </div>
                            </c:if>
                        </div>

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


                        <div class="section" id="paper">
                            <jsp:include page="paper_frag.jsp"></jsp:include>
                        </div>

                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


