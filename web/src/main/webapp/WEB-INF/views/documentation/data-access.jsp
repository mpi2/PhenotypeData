<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC data portal documentation</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-method">Data access, submission</a></jsp:attribute>
    <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

    <jsp:attribute name="header">
		<link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css"/>
		<link href="${baseUrl}/css/custom.css" rel="stylesheet" type="text/css"/>
        <style>
            .srchdocTab ul {
                list-style-type: square;
                margin-left: 50px;
            }
        </style>
	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

    <jsp:body>
        <h1>IMPC data portal documentation</h1>
        <div><i class="fleft fa fa-download fa-4x"></i>
            <div class="fleft">Data Access and Submission</div>
        </div>


        <div id="tabs">
            <ul>
                <li><a href="#tabs-0">IMPC Genotype Phenotype API</a></li>
                <li><a href="#tabs-1">IMPC Statistical Result API</a></li>
                <li><a href="#tabs-2">IMPC Observation API</a></li>
                <li><a href="#tabs-3">IMPC Images API</a></li>
                <li><a href="#tabs-4">Data submission to IMPC</a></li>
            </ul>

            <div id="tabs-0" class="srchdocTab">
                <%@ include file="data-access-api-genotype-phenotype.jsp" %>
            </div>
            <div id="tabs-1" class="srchdocTab">
                <%@ include file="data-access-api-statistical-result.jsp" %>
            </div>
            <div id="tabs-2" class="srchdocTab">
                <%@ include file="data-access-api-observation.jsp" %>
            </div>
            <div id="tabs-3" class="srchdocTab">
                <%@ include file="data-access-api-images.jsp" %>
            </div>
            <div id="tabs-4" class="srchdocTab">
                <%@ include file="data-access-submission-api-help.jsp" %>
            </div>
        </div>

        <script>

            // determin tab to open from hash tag
            var matches = window.location.hash.match(/(\d)$/);

            // tab indexing is zero based
            var tabIndex = matches == null ? 0 : matches[0];

            // activate tab
            $("#tabs").tabs({active: tabIndex});

        </script>

    </jsp:body>

</t:genericpage>
