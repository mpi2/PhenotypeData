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
          div#tabs {
              border-top: none;
              width: 100%;
              margin-top: 50px;
          }

          div#tabs > ul {
              border: none;
              border-bottom: 1px solid #666;
              padding-bottom: 2px;
              margin-bottom: 0px;
              background: none;
              list-style-type: none;
          }

          div#tabs > ul li {
              float: left;
          }

          div.presentationTab {
              border: 1px solid gray;
              border-top: none;
              padding: 45px;
          }
          div#tabs > ul li a {
              margin: 0 0px -3px 20px;
              border: 1px solid #666;
              border-bottom: none;
              font-size: 16px;
              text-decoration: none;
              padding: 3px 5px 3px 5px;
              border-radius: 4px;
              color: gray;
          }
          #tabs .ui-tabs-active > a {
              border-bottom: 2px solid white;
              color: black;
          }


          ul.docLi li, ul.subUl li {
              margin-bottom: 10px;
          }
          ul.subUl {
              list-style-type: square;
              margin-left: 50px;
          }
          table.twoCols th:nth-child(1) {
              width: 45%;
          }
          .highlight {
              background-color: #F1F2F2;
          }


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
                                    <ul>
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
                // find out which tab to open from hash tag
                var matches = window.location.hash.match(/(\d)$/);
                var tabIndex = matches == null ? 0 : matches[0];
                $( "#tabs" ).tabs({ active: tabIndex });

                $('tr').mouseover(function(){
                   $(this).find('td').addClass('highlight');
                }).mouseout(function(){
                    $(this).find('td').removeClass('highlight');
                });
            });

        </script>

    </jsp:body>

</t:genericpage>

