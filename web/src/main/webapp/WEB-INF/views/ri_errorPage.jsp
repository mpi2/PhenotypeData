<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">${title}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/summary">Register Interest</a> &raquo; Summary</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body>
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <jsp:useBean id="current" class="java.util.Date" />

        <div id="mainWrapper">
            <div class="login-container">
                <div class="login-card">
                    <div class="login-form">
                        <form action="changePasswordEmail" method="POST" class="form-horizontal">

                            <div class="alert alert-danger">
                                <c:if test="${showWhen}">
                                    ${current.toLocaleString()}:&nbsp;
                                </c:if>
                                <p>${error}</p>
                            </div>

                            <br />

                            <a href="${paBaseUrl}/rilogin">Login</a>

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>