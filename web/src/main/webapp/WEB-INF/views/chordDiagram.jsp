<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Chord diagram</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search">search</a> &raquo; Chord diagram </jsp:attribute>

    <jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>



    <jsp:attribute name="header">

    <script src="//d3js.org/d3.v4.min.js"></script>
    <script src="//d3js.org/queue.v1.min.js"></script>
    <script type="text/javascript" src="${baseUrl}/js/charts/chordDiagram.js?v=${version}"></script>

    </jsp:attribute>

    <jsp:body>

        <div class="region region-content">
        <div class="block">
            <div class="content">
                <div class="node node-gene">
                    <h1 class="title" id="top">IMPC Phenotype Diagram</h1>

                    <div class="section">
                        <div class="inner" >
                            <div id="chordContainer"></div>
                            <svg id="chordDiagramSvg" width="960" height="960"></svg>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <script>
            var mpTopLevelTerms = [];
            <c:if test="${phenotypeName != null}">
                mpTopLevelTerms = ${phenotypeName};
            </c:if>
            drawChords("chordDiagramSvg", "chordContainer", true, mpTopLevelTerms, false);
        </script>
    </jsp:body>

</t:genericpage>


