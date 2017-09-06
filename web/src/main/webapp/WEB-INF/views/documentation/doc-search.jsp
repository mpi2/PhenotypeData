<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Portal Documentation</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a></jsp:attribute>
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
    <div><i class="fleft fa fa-search fa-4x"></i><div class="fleft">Search</div></div>
    <div id="tabs">
      <ul>
        <li><a href="#tabs-1">Generic Search Features</a></li>
        <li><a href="#tabs-2">Genes Search</a></li>
        <li><a href="#tabs-3">Phenotypes Search</a></li>
        <li><a href="#tabs-4">Diseases Search</a></li>
        <li><a href="#tabs-5">Anatomy Search</a></li>
        <li><a href="#tabs-6">Images Search</a></li>

      </ul>


      <div id="tabs-1" class="srchdocTab">
        <%@ include file="doc-search-generic-features.jsp" %>
      </div>
      <div id="tabs-2" class="srchdocTab">
        <%@ include file="doc-search-genes.jsp" %>
      </div>
      <div id="tabs-3" class="srchdocTab">
        <%@ include file="doc-search-mp.jsp" %>
      </div>
      <div id="tabs-4" class="srchdocTab">
        <%@ include file="doc-search-diseases.jsp" %>
      </div>
      <div id="tabs-5" class="srchdocTab">
        <%@ include file="doc-search-ma.jsp" %>
      </div>
      <div id="tabs-6" class="srchdocTab">
        <%@ include file="doc-search-images.jsp" %>
      </div>


    </div>

    <script>
        $(function() {

            // find out which tab to open from hash tag
            var matches = window.location.hash.match(/(\d)$/);
            var tabIndex = matches == null ? 0 : matches[0]-1;
            var tabs = $( "#tabs" ).tabs({ active: 0 });
            tabs.tabs({active: tabIndex});
        });
    </script>

  </jsp:body>

</t:genericpage>