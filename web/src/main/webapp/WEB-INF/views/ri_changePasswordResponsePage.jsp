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


    <body>
        <div id="mainWrapper">
            <div class="login-container">
                <div class="login-card">
                    <div class="login-form">

                        <form action="changePasswordResponse" method="POST" class="form-horizontal">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <input type="hidden" name="token" value="${token}" />

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">
                                    <p>${error}</p>
                                </div>
                            </c:if>

                            <div class="input-group input-sm">
                                <label class="input-group-addon" for="newPassword"><i class="fa fa-lock"></i></label>
                                <input type="password" class="form-control" id="newPassword" name="newPassword" placeholder="New password" required />
                            </div>

                            <div class="input-group input-sm">
                                <label class="input-group-addon" for="repeatPassword"><i class="fa fa-lock"></i></label>
                                <input type="password" class="form-control" id="repeatPassword" name="repeatPassword" placeholder="Repeat password" required />
                            </div>

                            <br />

                            <div class="form-actions">
                                <input type="submit" class="btn btn-block btn-primary btn-default" value="Change password" />
                            </div>

                            <br/>

                            <a href="${paBaseUrl}/summary">Summary</a>

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>