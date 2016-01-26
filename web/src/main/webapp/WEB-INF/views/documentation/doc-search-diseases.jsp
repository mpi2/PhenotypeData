<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a> &raquo; <a href="${baseUrl}/documentation/doc-search-diseases">Search Diseases</a></jsp:attribute>
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
        <li><a href="#tabs-2">Specific Disease Search</a></li>
      </ul>

      <div id="tabs-1">
        <%@ include file="search-generic-features.jsp" %>
      </div>
      <div id="tabs-2">

        <div>First, click on the "Diseases" datatype tab. It will show all the diseases that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a human disease ID/name into the main input box and/or click on a filter on the left panel to narrow down your result to relevant disease(s).<br><br>
          <p>The screenshot below shows searching the human "cardiac" diseases:<br>
        </div><br>


        <img src='img/search-disease.png' /><p>

        The filters on the left are grayed out if they are not related to your search.<br><br>

        <b>Columns in the dataset on the right panel:</b>
        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Disease</td>
            <td>This column shows the human disease name from OMIM/DECIPHER/ORPHANET with a linkout to the IMPC disease page.<br>
              In the disease page, mouse genes are mapped to this human disease which is based on the PhenoDigm phenotypic similarity tool using either Sanger-MGP or MGI data</td>
          </tr>
          <tr>
            <td>Source</td>
            <td>A human disease term is from either OMIM or DECIPHER or ORPHANET
            </td>
          </tr>
          <tr>
            <td>Curated Genes</td>
            <td>Possible values are:<br>
              (1) human: known disease-gene associations in human from OMIM/DECIPHER/ORPHANET in blue box<br>
              (2) mice: literature curated mouse models of disease from MGI in blue box.<br><br>

              Depending on the disease, it can have a mixture of information for both
            </td>
          </tr>
          <tr>
            <td>Candidate Genes by phenotype</td>
            <td>The value is MGI in blue box if applicable. It represents predicted mouse models of disease based on the PhenoDigm phenotypic similarity tool using either Sanger-MGP or MGI data</td>
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