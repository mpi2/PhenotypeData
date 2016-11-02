<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Presentations</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/presentations">Presentations</a></jsp:attribute>
    <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

    <jsp:attribute name="header">
      <style>
          div.region-content {
            width: 100% !important;
            margin: 0 !important;
          }

          table {
              border: 2px solid #0978A1;
              margin-top: 0px;
              padding: 15px 20px 20px 20px;
              border-radius: 5px;
              z-index: 0;
          }
          ul.preTabLabel{
              list-style: none;
          }

          ul.preTabLabel li {
              float: left;
              margin: 0 0 0 15px;
              border-radius: 5px 5px 0 0;
              background-color: #0978A1;
          }

          ul.preTabLabel li a {
              padding: 0 15px 0px 15px;
              color: white;
              text-decoration: none;
              font-weight: bold;
              font-size: 20px;
              vertical-align: middle;
              cursor: pointer;
              display: block;
          }

          ul.preTabLabel li:hover {
              background-color: rgb(144,195,212); /* light blue */
              border: none;
          }

          ul.preTabLabel > li.ui-tabs-active {
              background-color: white;
              border: 2px solid #0978A1;
              border-bottom: 0px solid white;
          }
          ul.preTabLabel > li.ui-tabs-active a {
              color: #0978A1;
              margin-bottom: -4px;
              border-bottom: 4px solid white;
          }
          tr:nth-child(even) td {
              background-color: #F1F2F2;
          }
          /*.highlight {*/
              /*background-color: #F1F2F2;*/
          /*}*/

      </style>

	</jsp:attribute>

    <jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>


    <jsp:body>
        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node">
                        <h1 class="title" id="top">IMPC Presentations</h1>

                        <div class="section">
                            <!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
                            <div class='inner' id='presentationBlock'>

                                <%--${impcPresentations}--%>

                                <div id="tabs">
                                    <ul class="preTabLabel">
                                    <c:forEach var="type" items="${impcPresentations}" varStatus="loop">
                                        <li><a href="#tabs-${loop.index}">${type.key}</a></li>
                                    </c:forEach>
                                    </ul>
                                    <c:forEach var="type" items="${impcPresentations}"  varStatus="loop">
                                        <div id="tabs-${loop.index}" class="presentationTab">${type.value}</div>
                                    </c:forEach>
                                </div>
                            </div>

                        </div><!-- end of section -->

                    </div><!-- end of node div -->
                </div><!-- end of content div -->
            </div>
        </div>

        <script>

            $(function() {
                // find out which tab to open from hash tag. Eg. .../presentations#1 (second tab)
                var matches = window.location.hash.match(/(\d)$/);
                var tabIndex = matches == null ? 0 : matches[0];
                //alert(tabIndex);
                $( "#tabs" ).tabs({ active: tabIndex });

//                $('tr').mouseover(function(){
//                    $(this).find('td').addClass('highlight');
//                }).mouseout(function(){
//                    $(this).find('td').removeClass('highlight');
//                });
            });

        </script>

    </jsp:body>

</t:genericpage>

