<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-search">Search data</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.cssssss" rel="stylesheet" type="text/css" />
        <style>
          table {
            margin-top: 120px;
          }
          td {
            border: none;
            text-align: center;
            width: 33%;
          }
          .fleft {float: left;}
          div.fleft {
            font-size: 25px;
            margin-top: 20px;
          }
        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-search fa-4x"></i><div class="fleft">Search data</div></div>

  <table style="width:100%">
    <tr>
      <td><i class="fa-4x"></i></td>
      <td><i class="fa fa-bar-chart fa-4x"></i></td>
      <td><i class="fa fa-users fa-4x"></i></td>
    </tr>
    <tr>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-search-genes">Genes</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-search-mp">Phenotypes</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-search-diseases">Diseases</a></td>
    </tr>
    <tr>
      <td><i class="fa "></i></td>
      <td><i class="fa fa-camera fa-4x"></i></td>
    </tr>
    <tr>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-search-ma">Anatomy</a></td>
      <td class="descTxt"><a href="${baseUrl}/documentation/doc-search-images">Images</a></td>
      </td>
    </tr>
  </table>


  </jsp:body>

</t:genericpage>