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

        <div class="container">
            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="${paBaseUrl}">Home</a>
                            <span class="fal fa-angle-right"></span><a href="${paBaseUrl}/rilogin">My Genes</a>
                            <span class="fal fa-angle-right"></span> ${title}
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <h2 class="title">${title}</h2>

                    <div class="messages" style="color: indigo">
                            ${current.toLocaleString()}:&nbsp;
                        <p>${status}</p>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>