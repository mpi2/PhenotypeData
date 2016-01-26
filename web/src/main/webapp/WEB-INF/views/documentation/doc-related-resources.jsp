<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-method">Related resources doc</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/searchPage.cssssss" rel="stylesheet" type="text/css" />
        <style>

          .fleft {float: left;}
          div.fleft {
            font-size: 25px;
            margin-top: 20px;
            padding-left: 20px;
          }
          ul#method {
            clear: left;
            margin-top: 120px;
            list-style-type: none;
            padding: 0;
          }
          ul#method ul {
            list-style-type: none;
            margin: 0 0 0 30px;
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

          li a.noLink {
            color: #666;
            font-size: 20px;
          }
        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-sitemap fa-4x"></i><div class="fleft">Related Resources</div></div>

  <ul id="method">
    <li><a href="" class="noLink">Ontologies</a>
      <ul>
        <li><a href="">MP</a></li>
        <li><a href="">MA</a></li>
        <li><a href="">EMAPA</a></li>
        <li><a href="">HP</a></li>
        <li><a href="">MPATH</a></li>
      </ul>
    </li>
    <li><a href="" class="noLink">Disease Resources</a>
      <ul>
        <li><a href="">OMIM</a></li>
        <li><a href="">DECIPHER</a></li>
        <li><a href="">ORPHANET</a></li>
        <li><a href="">Others</a></li>
      </ul>
    </li>
    <li><a href="">GWAS Catalog</a></li>
    <li><a href="${baseUrl}/">Repositories</a></li>
  </ul>

  </jsp:body>

</t:genericpage>