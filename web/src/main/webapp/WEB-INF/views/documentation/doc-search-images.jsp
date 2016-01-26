<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a> &raquo; <a href="${baseUrl}/documentation/doc-search-images">Search Images</a></jsp:attribute>
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
        <li><a href="#tabs-2">Specific Images Search</a></li>
      </ul>

      <div id="tabs-1">
        <%@ include file="search-generic-features.jsp" %>
      </div>
      <div id="tabs-2">

        Currently, there are two major types of images. The "IMPC images" (the IMPC Images datatype) and the legacy ones (the Images datatype).<br>
        The Images datatype is legacy data from previous Europhenome/Eumodic phenotyping projects.<br><br>

        As both datatypes can be searched by similar interfaces, the document here refers to the "IMPC Images" datatype.<br><br>

        <div>First, click on the "IMPC Images" datatype tab. It will show all images that IMPC knows about if no filters are ticked on the left panel.<br>
          You could enter a mouse anatomy ID/name or procedure name from IMPReSS into the main input box and/or click on a filter on the left panel to narrow down your result to relevant IMPC images.<br>
          IMPReSS contains standardized phenotyping protocols essential for the characterization of mouse phenotypes.<br><br>
          <p>The screenshot below shows searching the IMPC Images datatype with the mouse anatomy term "trunk":<br>
        </div><br>

        There are two ways to view images data: Annotation View and Image View.<br>
        The former groups images by annotations which can be genes, anatomy terms (MA) or procedures. The latter lists annotations (genes, MA and procedure) to an image.<br><br>


        <h6 id="quick-image-search">Annotation View (default)</h6>
        <img src='img/quick-img-search-annotView.png' /><br>


        <h6 id="quick-image-search-image-view">Image View</h6>
        <p>To list annotations to an image, simply click on the "Show Image View" link in the top-right corner of the results grid. The label of the same link will then change to "Show Annotation View" so that you can toggle the views.</p><br>
        <img src='img/quick-img-search-imageView.png' /><br>

        <b>Columns in the dataset on the right panel:</b>

        <table>
          <tr>
            <th>Column</th>
            <th>Explaination</th>
          </tr>
          <tr>
            <td>Name</td>
            <td><b>Annotation View</b>:<br>This column shows name of an annotation and the number of annotation-associated images.<br>A gene annotation is a linkout to IMPC gene page; a procedure annotation does not have a link for now and a MA annotation is a linkout to anatomy page<br><br>
              <b>Image View</b>:<br>This column shows a group of different annotations (gene, procedure, MA)<br>
            </td>>
          </tr>
          <tr>
            <td>Images</td>
            <td>Shows thumbnail image (Image View) or thumbnail images (Annotation View, maximun of 4) with linkout to images page</td>
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