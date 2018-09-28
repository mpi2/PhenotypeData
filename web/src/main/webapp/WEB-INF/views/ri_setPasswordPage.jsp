<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">Set password</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/summary">Register Interest</a> &raquo; Set password</jsp:attribute>
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

                        <form action="setPassword" method="POST" class="form-horizontal">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <input type="hidden" name="token" value="${token}" />
                            <input type="hidden" name="action" value = "${action}" />

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger" style="color:crimson">
                                    <p>${error}</p>
                                </div>
                            </c:if>
                            <c:if test="${not empty status}">
                                <div class="alert alert-success" style="color:indigo">
                                    <c:if test="${showWhen}">
                                        ${current.toLocaleString()}:&nbsp;
                                    </c:if>
                                    <p>${status}</p>
                                </div>
                            </c:if>

                            <h6>Set password for: ${emailAddress}</h6>

                            <br />

                            <div class="input-group input-sm">
                                <div class="onethird" style="padding-bottom: 10px">
                                    <label class="input-group-addon" for="newPassword">New password</label>
                                </div>
                                <div class="twothird" style="padding-bottom: 10px">
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" required />
                                </div>
                            </div>

                            <div class="input-group input-sm">
                                <div class="onethird" style="padding-bottom: 10px">
                                    <label class="input-group-addon" for="repeatPassword">Repeat password</label>
                                </div>
                                <div class="twothird" style="padding-bottom: 10px">
                                    <input type="password" class="form-control" id="repeatPassword" name="repeatPassword" required />
                                </div>
                            </div>

                            <br />

                            <div class="form-actions">
                                <input type="submit" class="btn btn-block btn-primary btn-default" value="Set password" />
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