<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
      <style>

          div#tabs {
            border-top: none;
          }

          div#tabs > ul {
            border: none;
            border-bottom: 1px solid #666;
            padding-bottom: 3px;
            margin-bottom: 0px;
            background: none;
            list-style-type: none;
          }

          div#tabs > ul li {
            float: left;
          }

          div.srchdocTab {
            border: 1px solid gray;
            border-top: none;
            padding: 45px;
          }


          div#tabs > ul li a {
            margin: 0 0px -3px 20px;
            border: 1px solid #666;
            border-bottom: none;
            font-size: 16px;
            text-decoration: none;
            padding: 3px 5px 3px 5px;
            border-radius: 4px;
            color: gray;
          }

          #tabs .ui-tabs-active {

          }
          #tabs .ui-tabs-active > a {
            border-bottom: 1px solid white;
            color: black;
          }
          img {
            /* images will be automatically adjusted to fit container */
            max-width:100%;
            max-height:100%;
          }
          div#note {color: darkorange;}

        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>

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
        $( "#tabs" ).tabs({ active: 0 });
      });
    </script>

  </jsp:body>

</t:genericpage>