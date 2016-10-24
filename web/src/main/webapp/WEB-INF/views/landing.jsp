<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Landing Pages </jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/landing">Landing</a></jsp:attribute>
    <jsp:attribute name="bodyTag">
		<body class="chartpage no-sidebars small-header">

	</jsp:attribute>
    <jsp:attribute name="header">
    </jsp:attribute>
    <jsp:attribute name="addToFooter">
		<div class="region region-pinned">

            <div id="flyingnavi" class="block smoothScroll">

                <a href="#top"><i class="fa fa-chevron-up"
                                  title="scroll to top"></i></a>

                <ul>
                    <li><a href="#top">Landing</a></li>
                </ul>

                <div class="clear"></div>

            </div>

        </div>

    </jsp:attribute>


    <jsp:body>
        <!-- Assign this as a variable for other components -->
        <script type="text/javascript">
            var base_url = '${baseUrl}';
        </script>

        <div class="region region-content">
            <div class="block block-system">
                <div class="content">
                    <div class="node node-gene">

                        <h1 class="title" id="top">Landing page index</h1>

                        <div class="section">
                            <div class=inner>
                                <c:forEach var="page" items="${pages}">
                                    <a href="${baseUrl}/${page.link}"><h2>${page.title}</h2></a>
                                    <div class="half">
                                        <p> ${page.description} </p>
                                        <p> <a href="${baseUrl}/${page.link}">More</a></p>
                                    </div>
                                    <div class="half">
                                        <img src="${baseUrl}/${page.image}" width="80%">
                                    </div>

                                    <div class="clear both"> </div>
                                    <br/> <br/> <br/>

                                </c:forEach>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>


    </jsp:body>


</t:genericpage>
