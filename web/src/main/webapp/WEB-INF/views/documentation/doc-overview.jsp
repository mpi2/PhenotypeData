<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a></jsp:attribute>


  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">

        <style>
          table {
            margin-top: 80px;
          }
          td {
            border: none;
            text-align: center;
            width: 33%;
          }


        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>

  <%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <h1>IMPC data portal documentation</h1>

    <table style="width:100%">
      <tr>
        <td><a href='${baseUrl}/documentation/doc-search'><i class="fa fa-search fa-4x"></i></a></td>
        <td><a href='${baseUrl}/documentation/doc-explore'><i class="fa fa-map-o fa-4x"></i></a></td>
        <td><a href='${baseUrl}/documentation/doc-faq'><i class="fa fa-info fa-4x"></i></a></td>
      </tr>
      <tr>
        <td class="descTxt"><a href='${baseUrl}/documentation/doc-search'>Search</a></td>
        <td class="descTxt"><a href='${baseUrl}/documentation/doc-explore'>Explore</a></td>
        <td class="descTxt"><a href='${baseUrl}/documentation/doc-faq'>FAQ</a></td>
      </tr>
      <tr>
        <td><a href='${baseUrl}/documentation/doc-method'><i class="fa fa-line-chart fa-4x"></i></a></td>
        <td><a href='${baseUrl}/documentation/doc-related-resources'><i class="fa fa-sitemap fa-4x"></i></a></td>
        <td><a href='${baseUrl}/documentation/doc-access'><i class="fa fa-download fa-4x"></i></a></td>
      </tr>
      <tr>
        <td class="descTxt"><a href='${baseUrl}/documentation/doc-method'>Methods</a></td>
        <td class="descTxt"><a href='${baseUrl}/documentation/doc-related-resources'>Related Resources</a></td>
        <td class="descTxt"><a href='${baseUrl}/documentation/doc-access'>Data Access & Submission</a></td>
        </td>
      </tr>
    </table>


  </jsp:body>

</t:genericpage>
