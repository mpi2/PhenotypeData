<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-method">method</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><div id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
        <link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />
        <style>

          span#areaMsg {
            position: absolute;
            width: auto;
            top: 164px;
            left: 250px;
            color: grey;
          }
          .ui-tooltip {display: none !important;}

          .srchdocTab ul {
               list-style-type: square;
               margin-left: 50px;
           }
        </style>


	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-line-chart fa-4x"></i><div class="fleft">Methods</div></div>


      <div id="tabs">
          <ul>
              <li ><a id="statistics" href="#doc-methods-statistics">Statistics</a></li>
              <li ><a id="tools" href="#doc-methods-tools">Tools</a></li>
          </ul>

          <div id="doc-methods-statistics" class="srchdocTab">
              <%@ include file="doc-methods-statistics.jsp" %>
          </div>
          <div id="doc-methods-tools" class="srchdocTab">
              <%@ include file="doc-methods-tools.jsp" %>
          </div>

      </div>
      <script>
          $(function() {
              // find out which tab to open from hash tag
              var matches = window.location.hash.match(/(\d*\w*)+$/);
              $( matches.input ).trigger( "click" );
          });
      </script>

  </jsp:body>

</t:genericpage>
