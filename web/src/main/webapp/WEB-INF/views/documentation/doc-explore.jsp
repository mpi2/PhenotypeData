<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Portal Documentation</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-explore">Explore data</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
      <link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />
      <style></style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-map-o fa-4x"></i><div class="fleft">Explore</div></div>

    <div id="tabs">
      <ul>

        <li><a href="#tabs-1">Genes Page</a></li>
        <li><a href="#tabs-2">Phenotypes Page</a></li>
        <li><a href="#tabs-3">Diseases Page</a></li>
        <li><a href="#tabs-4">Anatomy Page</a></li>
        <li><a href="#tabs-5">Graphs Page</a></li>
        <li><a href="#tabs-6">Landing Page</a></li>
        <li><a href="#tabs-7">Others</a></li>
      </ul>

      <div id="tabs-1" class="srchdocTab">
        <%@ include file="gene-help.jsp" %>
      </div>
      <div id="tabs-2" class="srchdocTab">
        <%@ include file="phenotype-help.jsp" %>
      </div>
      <div id="tabs-3" class="srchdocTab">
        <%@ include file="disease-help.jsp" %>
      </div>
      <div id="tabs-4" class="srchdocTab">
        <%@ include file="anatomy-help.jsp" %>
      </div>
      <div id="tabs-5" class="srchdocTab">
        <%@ include file="doc-graphs.jsp" %>
      </div>
      <div id="tabs-6" class="srchdocTab">
        <%@ include file="landing-help.jsp" %>
      </div>
      <div id="tabs-7" class="srchdocTab">
        <%@ include file="data-release-overview.jsp" %>
      </div>
  

    </div>

    <script>
      $(function() {
        // find out which tab to open from hash tag
        var matches = window.location.hash.match(/(\d)$/);
        var tabIndex = matches == null ? 0 : matches[0];
        $( "#tabs" ).tabs({ active: tabIndex });
      });
    </script>

  </jsp:body>

</t:genericpage>