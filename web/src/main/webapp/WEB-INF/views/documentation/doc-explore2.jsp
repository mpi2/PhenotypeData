<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Portal Documentation</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-explore">Explore data</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

  <jsp:attribute name="header">
      <style>

        div#tabs {
          border-top: none;
          width: 100%;
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
      <h1>Explore IMPC data</h1>

      <div id="tabs">
        <ul>
          <li><a href="#tabs-1">Genes</a></li>
          <li><a href="#tabs-2">Phenotypes</a></li>
          <li><a href="#tabs-3">Diseases</a></li>
          <li><a href="#tabs-4">Anatomy</a></li>
          <li><a href="#tabs-5">Images</a></li>

        </ul>


        <div id="tabs-1" class="srchdocTab">
          <%@ include file="doc-explore-genes.jsp" %>
        </div>
        <div id="tabs-2" class="srchdocTab">
          <%@ include file="doc-explore-mp.jsp" %>
        </div>
        <div id="tabs-3" class="srchdocTab">
          <%@ include file="doc-explore-diseases.jsp" %>
        </div>
        <div id="tabs-4" class="srchdocTab">
          <%@ include file="doc-explore-mp.jsp" %>
        </div>
        <div id="tabs-5" class="srchdocTab">
          <%@ include file="doc-explore-images.jsp" %>
        </div>

      </div>

      <script>
        $(function() {
          //$( "#tabs" ).tabs({ active: 0 });
        });
      </script>

    </jsp:body>



  <%--<jsp:body>--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
    <%--<h1>IMPC data portal documentation</h1>--%>
    <%--<div><i class="fleft fa fa-map-o fa-4x"></i><div class="fleft">Explore data</div></div>--%>

  <%--<table style="width:100%">--%>
    <%--<tr>--%>
      <%--<td><i class="fa-4x"></i></td>--%>
      <%--<td><i class="fa fa-bar-chart fa-4x"></i></td>--%>
      <%--<td><i class="fa fa-users fa-4x"></i></td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
      <%--<td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-genes">Genes</a></td>--%>
      <%--<td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-mp">Phenotypes</a></td>--%>
      <%--<td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-diseases">Diseases</a></td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
      <%--<td><i class="fa "></i></td>--%>
      <%--<td><i class="fa fa-camera fa-4x"></i></td>--%>
      <%--<td><i class="fa fa-wrench fa-4x"></i></td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
      <%--<td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-ma">Anatomy</a></td>--%>
      <%--<td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-images">Images</a></td>--%>
      <%--<td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-tools">Tools</a></td>--%>
      <%--</td>--%>
    <%--</tr>--%>
  <%--</table>--%>

    <%--<div>A note about data: To be transparent and provide fast access to data,--%>
      <%--phenotype data that is undergoing quality control is visible on the portal.--%>
      <%--Such “Pre-QC Data” is clearly labeled and graphical representations are from the Phenoview QC platform.--%>
      <%--“Post-QC Data” has passed Quality Control and is released in periodic data releases onto the portal, APIs and downloads.--%>
      <%--More information is here</div>--%>


  <%--</jsp:body>--%>

</t:genericpage>