<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">${title}</jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <jsp:useBean id="current" class="java.util.Date" />

        <div class="container data-heading">
            <div class="row">

                <div class="col-12 no-gutters">
                    <h2 class="mb-0">${title}</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">
            <div class="breadcrumbs clear row">
                    <div class="col-12 px-5 pt-5">
                        <aside><a href="${baseUrl}">Home</a>
                            <span class="fal fa-angle-right"></span><a href="${baseUrl}/summary">My Genes</a>
                            <span class="fal fa-angle-right"></span> ${title}
                        </aside>
                    </div>
            </div>
            <div class="row">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="row no-gutters">
                                <div class="col-md-12 px-0">

                                    <div class="messages" style="color: indigo">
                                            ${current.toLocaleString()}:&nbsp;
                                        <p>${status}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>


    </jsp:body>
</t:genericpage>