<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/${dataType}?kw=*">${dataTypeLabel}</a> &raquo; ${searchQuery}</jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.cssssss" rel="stylesheet" type="text/css" />
	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<table style="width:100%">
  <tr>
    <td><i class="fa fa-search fa-4x"></i></td>
    <td><i class="fa fa-map-o fa-4x"></i></td>
    <td><i class="fa fa-info fa-4x"></i></td>
  </tr>
  <tr>
    <td>Search</td>
    <td>Explore</td>
    <td>Faq</td>
  </tr>
  <tr>
    <td><i class="fa fa-line-chart fa-4x"></i></td>
    <td><i class="fa fa-sitemap fa-4x"></i></td>
    <td><i class="fa fa-download fa-4x"></i></td>
  </tr>
  <tr>
    <td>Methods</td>
    <td>related resources</td>
    <td>Download</td>
  </tr>
</table>


  </jsp:body>

</t:genericpage>