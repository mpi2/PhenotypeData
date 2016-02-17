<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a> &raquo; <a href="${baseUrl}/documentation/doc-search-mp">Search Phenotypes</a></jsp:attribute>
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
        <li><a href="#tabs-2">Specific Phenotype Search</a></li>
      </ul>

      <div id="tabs-1">
        <%@ include file="doc-search-generic-features.jsp" %>
      </div>
      <div id="tabs-2">

          <div>Firts, click on the "Phenotypes" datatype tab. It will show all the mouse phenotypes that IMPC knows about if no filters are ticked on the left panel.<br>
            You could enter a human or mouse phenotype (either by text or by ID) into the main input box and/or click on a filter (all top level mouse phenotype ontology terms) on the left panel to narrow down your result to relevant phenotype(s).
            <p>The screenshot below shows searching phenotypes containing the word "glucose":<br>

          </div><br>
          <img src='img/search-phenotype.png' /><p>
          The filters on the left are grayed out if they are not related to your search.<br><br>

          <b>Columns in the dataset on the right panel:</b>
          <table>
            <tr>
              <th>Column</th>
              <th>Explaination</th>
            </tr>
            <tr>
              <td>Phenotype</td>
              <td>This column shows the ontological term of a phenotype with a linkout to the IMPC phenotype page. When mouseover this column, its computationally mapped HP term(s) and/or term synonym(s) will be displayed if available</td>
            </tr>
            <tr>
              <td>Definition</td>
              <td>Ontological definition of this mouse phenotype term
              </td>
            </tr>
            <tr>
              <td>Phenotyping Call(s)</td>
              <td>This tells you at a glance whether a phenotype has phenotyping data to date
              </td>
            </tr>
            <tr>
              <td>Register Interest</td>
              <td>The "Interest" link takes you to login page if you have not. Once you have logged in you will be taken back to this column and the label will be changed to "Register interest".<br>
                Click on it will register you to the interest alert system of IMPC and you will be alerted once the phenotyping data is available/updated
              </td>
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