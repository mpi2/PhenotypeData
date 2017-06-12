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



	</jsp:attribute>
    <jsp:body>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">
                        <h1 class="title" id="top">${pageTitle} </h1>

                        <c:import url="landing_overview_frag.jsp"/>

                        <div class="section">
                            <h2 class="title">Approach</h2>
                            <div class="inner">
                                <p>In order to identify genes required for hearing function, the consortium uses an
                                    auditory brainstem response (ABR) test in the adult pipeline at week 14 that
                                    assesses hearing at five frequencies – 6kHz, 12kHz, 18kHz, 24kHz and 30kHz – as well
                                    as a broadband click stimulus. The consortium aimed to analyse a minimum of 4 mutant
                                    mice for each gene and, in most cases, mutant males and females were analysed.</p>
                                <p>For the statistical analysis of the IMPC ABR dataset, we used a reference range
                                    approach with the aim of eliminating false positives (see Methods). Briefly, we used
                                    the total set of matched baseline control data from wild-type C57BL/6N mice that is
                                    generated at each IMPC centre to establish a reference range. For each mutant a
                                    contingency table is employed for both appropriate wild-type control mice and
                                    mutants, and a Fisher’s exact test performed to identify if mutants deviate
                                    significantly from the wild-type distribution. We determined a suitable reference
                                    range and critical p value by the examination of known deafness genes, and selected
                                    a stringent 98% reference range and p value of 0.01 for the initial selection of
                                    putative deafness loci. </p>
                                <p>Details of the experimental design of <a
                                        href="https://www.mousephenotype.org/impress/protocol/176/7"> acoustic startle
                                    and Pre-pulse Inhibition </a> and <a
                                        href="https://www.mousephenotype.org/impress/protocol/149/7">Auditory Brain Stem
                                    Response</a> are available on IMPRESS.</p>
                                <c:import url="landing_procedures_frag.jsp"/>
                            </div>
                        </div>

                        <div class="section">
                                <%--IMPC images--%>
                            <c:if test="${not empty impcImageGroups}">
                                <div class="section" id="imagesSection">
                                    <h2 class="title">Associated Images </h2>
                                    <div class="inner">
                                        <jsp:include page="impcImagesByParameter_frag.jsp"></jsp:include>
                                    </div>
                                </div>
                            </c:if>
                        </div>

                        <div class="section">
                            <h2 class="title">Phenotypes distribution</h2>
                            <div class="inner">
                                <div id="phenotypeChart">
                                    <script type="text/javascript"> $(function () {  ${phenotypeChart} }); </script>
                                </div>
                            </div>
                        </div>

                        <div class="section">

                            <h2 id="gene-ko-effect" class="title">Gene KO effect comparator for ${systemName} continuous parameters</h2>

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
                            <jsp:include page="paper_frag.jsp"></jsp:include>
                        </div>

                    </div>
                </div>
            </div>
        </div>


    </jsp:body>

</t:genericpage>


