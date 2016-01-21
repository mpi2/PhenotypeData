<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-explore">explore doc</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.cssssss" rel="stylesheet" type="text/css" />
        <style>
          table {
            margin-top: 80px;
          }
          td {
            border: none;
          }
          td {border: 1px solid white;
            width: 33%;
          }

          .fleft {float: left;}
          div.fleft {
            font-size: 25px;
            margin-top: 20px;
            padding-left: 20px;
          }

        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-map-o fa-4x"></i><div class="fleft">Explore data</div></div>

  <table style="width:100%">
    <tr>
      <td><i class="fa-4x"></i></td>
      <td><i class="fa fa-bar-chart fa-4x"></i></td>
      <td><i class="fa fa-users fa-4x"></i></td>
    </tr>
    <tr>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-genes">Genes</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-mp">Phenotypes</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-diseases">Diseases</a></td>
    </tr>
    <tr>
      <td><i class="fa "></i></td>
      <td><i class="fa fa-camera fa-4x"></i></td>
      <td><i class="fa fa-wrench fa-4x"></i></td>
    </tr>
    <tr>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-ma">Anatomy</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-images">Images</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-explore-tools">Tools</a></td>
      </td>
    </tr>
  </table>

    <div>A note about data: To be transparent and provide fast access to data,
      phenotype data that is undergoing quality control is visible on the portal.
      Such “Pre-QC Data” is clearly labeled and graphical representations are from the Phenoview QC platform.
      “Post-QC Data” has passed Quality Control and is released in periodic data releases onto the portal, APIs and downloads.
      More information is here</div>


  </jsp:body>

</t:genericpage>