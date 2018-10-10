<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">${title}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/summary">Register Interest</a> &raquo; ${title}</jsp:attribute>

    <jsp:attribute name="header">

        <script src='https://www.google.com/recaptcha/api.js'></script>


        <style>

            div.text {
                margin: 0;
                padding: 0;
                padding-bottom: 1.25em;
            }

            div.text label {
                margin: 0;
                padding: 0;
                display: block;
                font-size: 100%;
                padding-top: .1em;
                padding-right: .25em;
                width: 10em;
                text-align: left;
                float: left;
            }

            input[type="text"] {
                width:450px;

            }

        </style>

    </jsp:attribute>

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
                            <input type="hidden" name="requestedAction" value="${title}" />

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


                            <div>
                                <div class="text">
                                    <label for="username">Email address
                                    </label>
                                    <input type="text" id="username" name="emailAddress" value="${emailAddress}" placeholder="myaddress@example.com" required />
                                </div>
                                <div class="text">
                                    <label for="repeatEmailAddress">Repeat email address
                                    </label>
                                    <input type="text" id="repeatEmailAddress" name="repeatEmailAddress" value="${repeatEmailAddress}" placeholder="myaddress@example.com" required />
                                </div>

                                <div style="padding-bottom:1.25em;" class="g-recaptcha" data-sitekey="6LckO3QUAAAAAAYOdZIrVrfhiz7Xueo_9l8mbCcQ"></div>

                                <div>
                                    <input type="submit" class="btn btn-block btn-primary btn-default"
                                           value="${title}"/>
                                </div>

                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>