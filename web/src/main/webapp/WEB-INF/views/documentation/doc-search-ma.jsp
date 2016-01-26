<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a> &raquo; <a href="${baseUrl}/documentation/doc-search-ma">Search Anatomy</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.cssssss" rel="stylesheet" type="text/css" />
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

          div#tabs-1, div#tabs-2 {
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
        <li><a href="#tabs-2">Specific Anatomy Search</a></li>
      </ul>

      <div id="tabs-1">
        <%@ include file="search-generic-features.jsp" %>
      </div>
      <div id="tabs-2">

        <div>First, click on the "Anatomy" datatype tab. It will show all the anatomy terms that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a mouse anatomy ID/name into the main input box and/or click on a filter (all top level mouse anatomy ontology terms) on the left panel to narrow down your result to relevant anatomy term(s).<br><br>
          <p>The screenshot below shows searching the mouse anatomy terms containing "eye":<br>
        </div><br>
        <img src='img/search-anatomy.png' /><p>

        The filters on the left are grayed out if they are not related to your search.<br><br>

        <b>Columns in the dataset on the right panel:</b>
        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Anatomy</td>
            <td>This column shows the ontological term of a mouse anatomical tissue/organ with a linkout to the IMPC anatomy page</td>
          </tr>
        </table>
      </div>
    </div>


    <script>
      $(function() {
        $( "#tabs" ).tabs({ active: 1 });
      });
    </script>

  </jsp:body>

</t:genericpage>