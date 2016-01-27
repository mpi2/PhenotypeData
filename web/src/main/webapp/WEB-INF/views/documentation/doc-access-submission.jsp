<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-method">Data access, submission</a></jsp:attribute>
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
          ul#method {
            clear: left;
            margin-top: 120px;
          }
          ul {
            list-style-type: none;
            margin: 0;
            padding: 0;
          }

          li {
            /*font: 150 20px/1.5 Helvetica, Verdana, sans-serif;*/
            border-bottom: 1px solid #ccc;
          }

          li:last-child {
            border: none;
          }

          li a {
            text-decoration: none;
            display: block;
            width: 100%;
            padding: 5px;
          }

          li a:hover {
            font-weight: bold;
            text-decoration: none;
          }
        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-download fa-4x"></i><div class="fleft">Data Access, Submission</div></div>
  <ul id="method">
    <li><a href="${baseUrl}/documentation/">Download</a></li>
    <li><a href="${baseUrl}/documentation/doc-access-submission-api-help">Programmatic Access</a></li>
    <li><a href="${baseUrl}/documentation/">Internal Submission</a></li>
  </ul>

  </jsp:body>

</t:genericpage>