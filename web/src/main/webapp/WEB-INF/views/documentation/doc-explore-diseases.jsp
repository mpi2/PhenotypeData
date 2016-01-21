<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-explore">explore data</a> &raquo; <a href="${baseUrl}/documentation/doc-explore-diseases">explore Diseases</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">

		<link href="${baseUrl}/css/searchPage.cssssss" rel="stylesheet" type="text/css" />
        <style>


          img {
            /* images will be automatically adjusted to fit container */
            max-width:100%;
            max-height:100%;
          }
          div.breadcrumb:not(:first-child) {
            display: none;
          }

        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>

    <%@ include file="disease-help.jsp" %>

    <script>
      $( "header:eq(1)" ).css( "display", "none" );
      $( "div.breadcrumb:eq(1)" ).css( "display", "none" );
      $("div#main:eq(1)").css({"margin-top": "-80px", "padding":"0"});
      $("div.region-sidebar-first:eq(0)").hide();
    </script>

  </jsp:body>

</t:genericpage>