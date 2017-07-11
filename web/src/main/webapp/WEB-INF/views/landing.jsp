<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

    <jsp:attribute name="title">Biological Systems Pages </jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
            href="${baseUrl}/biological-systems">Biological Systems</a></jsp:attribute>
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
                    <li><a href="#top">Biological Systems</a></li>
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

                        <h1 class="title" id="top">Biological Systems</h1>

                        <div class="section">
                            <div class=inner>
                                <c:forEach var="page" items="${pages}"  varStatus="loop">

                                    <div <c:if test="${page.image != null}">class="half"</c:if> >

                                    <a href="${baseUrl}/${page.link}"><h2>${page.title}</h2></a>
                                    <div class="half">
                                        <p> ${page.description} </p>
                                        <p> <a href="${baseUrl}/${page.link}">More</a></p>
                                    </div>
                                    <c:if test="${page.image != null}">
                                        <div class="half">
                                            <%--<a href="${baseUrl}/${page.link}"><img src="${impcMediaBaseUrl}/${page.image}" width="80%"></a>--%>
                                            <a href="${baseUrl}/${page.link}"><img src="${page.image}" width="80%"></a>
                                        </div>
                                    </c:if>
                                    </div>

                                    <c:if test="${loop.index%2==1 || loop.last}" >
                                        <div class="clear both"> </div>
                                        <br/> <br/> <br/>
                                    </c:if>

                                </c:forEach>
                                 Note: more systems pages coming soon.
                            </div>
                           
                        </div>
                    </div>

                </div>
            </div>
        </div>


    </jsp:body>


</t:genericpage>
