<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">${systemName} landing page | IMPC Phenotype Information</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/landing">Landing
        Pages</a> &nbsp;&raquo; ${systemName}</jsp:attribute>

    <jsp:attribute name="header">

        <!-- JS Imports -->
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script src="//d3js.org/d3.v4.min.js"></script>
        <script src="//d3js.org/queue.v1.min.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

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

                                <p> To measure cardiovascular function in the mouse, IMPC uses a series of standardised protolcols. These protocols are described in <a href="${baseUrl}/../impress">IMPReSS</a> (International Mouse Phenotyping Resource of Standardised Screens). </p>
                                <p>Heart and vascular function/physiology are measured through several procedures like echocardiography and electrocardiogram, Non-Invasive blood pressure for example. Cardiovascular system morphology is assessed through macroscopic and microscopic measurements, like heart weight, gross pathology and gross morphology in both embryo and adult animals. A complete list of protocols and related phenotypes are presented in the table below. Links to impress are provided for more details on the procedure. </p>
                                <br/><br/>
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
                                    drawChords(false, mpTopLevelTerms);
                                </script>
                            </div>
                        </div>

                        <div class="section">

                            <h2 class="title">Cardiovascular disease associations by orthology and phenotypic similarity</h2>
                            <div class="inner">

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
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>


