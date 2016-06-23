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
              <li><a href="#tabs-1">Statistics</a></li>
              <li><a href="#tabs-2">Tools</a></li>

          </ul>

          <div id="tabs-1" class="srchdocTab">
              <%@ include file="doc-methods-statistics.jsp" %>
          </div>
          <div id="tabs-2" class="srchdocTab">
              <%@ include file="doc-methods-tools.jsp" %>
          </div>

      </div>

      <script>
          $(function() {
              $( "#tabs" ).tabs({ active: 0 });
          });
      </script>

  </jsp:body>

</t:genericpage>
