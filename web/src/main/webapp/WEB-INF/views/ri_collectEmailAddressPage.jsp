<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">${title}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/summary">Register Interest</a> &raquo; ${title}</jsp:attribute>
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
                        <form action="sendEmail" method="POST" class="form-horizontal">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <input type="hidden" name="action" value="${title}" />

                            <c:choose>
                                <c:when test="${not empty error}">
                                    <div class="messages error">
                                        <c:if test="${showWhen}">
                                            ${current.toLocaleString()}:&nbsp;
                                        </c:if>
                                        <p>${error}</p>
                                    </div>
                                </c:when>
                                <c:when test="${not empty status}">
                                    <div class="messages" style="color: indigo">
                                        <c:if test="${showWhen}">
                                            ${current.toLocaleString()}:&nbsp;
                                        </c:if>
                                        <p>${status}</p>
                                    </div>
                                </c:when>
                            </c:choose>

                            <div class="input-group input-sm">
                                <div class="onethird" style="padding-bottom: 10px">
                                    <label class="input-group-addon" for="username">Email address</label>
                                </div>
                                <div class="twothird" style="padding-bottom: 10px">
                                    <input type="text" class="form-control" id="username" name="emailAddress" value="${emailAddress}" placeholder="myaddress@example.com" required />
                                </div>
                            </div>

                            <div class="input-group input-sm">
                                <div class="onethird" style="padding-bottom: 10px">
                                    <label class="input-group-addon" for="repeatEmailAddress">Repeat email address</label>
                                </div>
                                <div class="twothird" style="padding-bottom: 10px">
                                    <input type="text" class="form-control" id="repeatEmailAddress" name="repeatEmailAddress" value="${repeatEmailAddress}" placeholder="myaddress@example.com" required />
                                </div>
                            </div>

                            <!-- Show the button by default. Allow it to be hidden. -->
                            <c:if test="${empty hideButton}">
                                <div class="form-actions">
                                    <input type="submit" class="btn btn-block btn-primary btn-default"
                                           value="${title}"/>
                                </div>
                            </c:if>

                            <br />

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>