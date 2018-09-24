<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">Change password</jsp:attribute>
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
                        <form action="changePasswordEmail" method="post" class="form-horizontal">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                            <c:choose>
                                <c:when test="${not empty error}">
                                    <div class="alert alert-danger">
                                        <c:if test="${showWhen}">
                                            ${current.toLocaleString()}:&nbsp;
                                        </c:if>
                                        <p>${error}</p>
                                    </div>
                                </c:when>
                                <c:when test="${not empty status}">
                                    <div class="alert alert-success">
                                        <c:if test="${showWhen}">
                                            ${current.toLocaleString()}:&nbsp;
                                        </c:if>
                                        <p>${status}</p>
                                    </div>
                                </c:when>
                            </c:choose>

                            <div class="input-group input-sm">
                                <label class="input-group-addon" for="username"><i class="fa fa-user"></i></label>
                                <input type="text" class="form-control" id="username" name="emailAddress" value="${emailAddress}" placeholder="Enter email address" required />
                            </div>

                            <div class="input-group input-sm">
                                <label class="input-group-addon" for="repeatUsername"><i class="fa fa-user"></i></label>
                                <input type="text" class="form-control" id="repeatUsername" name="repeatEmailAddress" placeholder="Enter email address again" required />
                            </div>

                            <div class="form-actions">
                                <input type="submit" class="btn btn-block btn-primary btn-default" value="${buttonText}" />
                            </div>

                            <br />

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>