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
        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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



      <%--<span id="areaMsg"></span>--%>
    <%--<img src="${baseUrl}/documentation/img/data_flow_v2.png" alt="" usemap="#Map" />--%>
    <%--<map name="Map" id="Map">--%>
      <%--<!-- if title is NOT empty it will trigger tooltip from default.js -->--%>
      <%--<area title="" alt="Acquisition: IMPC Centers" href="#" shape="poly" coords="33,241,35,254,128,255,125,244" />--%>
      <%--<area title="" alt="Validation: Phenotyping Protocols" href="#" shape="poly" coords="231,106,233,140,326,138,325,106" />--%>
      <%--<area title="" alt="Validation: Mouse Tracking System" href="#" shape="poly" coords="220,352,222,389,338,388,336,354" />--%>
      <%--<area title="" alt="Pre QC Data: Statistical Analysis" href="#" shape="poly" coords="329,182,329,219,400,217,398,185" />--%>
      <%--<area title="" alt="Final Data: Quality Control" href="#" shape="poly" coords="536,175,536,182,637,182,635,175" />--%>
      <%--<area title="" alt="Final Data: Statistical Analysis" href="${baseUrl}/documentation/doc-method-statistics" shape="poly" coords="535,189,536,198,659,196,659,187" />--%>
      <%--<area title="" alt="Final Data: Disease Associations" href="#" shape="poly" coords="535,203,536,213,672,214,670,205" />--%>
      <%--<area title="" alt="Final Data: Data Access" href="#" shape="poly" coords="552,414,553,425,629,424,626,414" />--%>
      <%--<area title="" alt="Final Data: Data Visualization Tool" href="#" shape="poly" coords="552,427,554,452,663,455,660,426" />--%>
      <%--<area title="" alt="Final Data: Downloads API" href="#" shape="poly" coords="693,390,695,425,783,425,780,390" />--%>
    <%--</map>--%>


    <%--<script type="text/javascript">--%>
      <%--$('document').ready(function(){--%>

        <%--$('map#Map area').mouseover(function(){--%>
          <%--$('span#areaMsg').text($(this).attr('alt'));--%>
        <%--}).mouseout(function(){--%>
          <%--$('span#areaMsg').text("");--%>
        <%--});--%>

      <%--});--%>

    <%--</script>--%>
    <%--<p class="sectBr"></p>--%>
    <%--<h3>Statistics</h3>--%>
      <%--<ul class="subUl">--%>
          <%--<li><a href="http://www.mousephenotype.org/data/documentation/statistics-help">About how IMPC uses statistics</a></li>--%>
      <%--</ul>--%>
    <%--<p class="intraSectBr"></p>--%>
    <%--<h3>Tools</h3>--%>
    <%--<ul class="subUl">--%>
        <%--<li><a href="https://dev.mousephenotype.org/data/gene2go">GO lookup tool: GO annotations to phenotyped IMPC genes</a></li>--%>
        <%--<li><a href="https://dev.mousephenotype.org/data/alleleref">Paper lookup tool: References using IKMC and IMPC resources</a></li>--%>
        <%--<li><a href="http://www.mousephenotype.org/data/documentation/parallel-help">Parallel coordinate</a></li>--%>
    <%--</ul>--%>


  </jsp:body>

</t:genericpage>
