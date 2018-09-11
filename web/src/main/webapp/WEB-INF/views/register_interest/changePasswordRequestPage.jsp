<%--
  Created by IntelliJ IDEA.
  User: mrelac
  Date: 06/14/2018
  Time: 08:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<%@ page isELIgnored="false"%>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Change password</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

<jsp:useBean id="current" class="java.util.Date" />

<body>
<div id="mainWrapper">
    <div class="login-container">
        <div class="login-card">
            <div class="login-form">
                <form action="changePasswordEmail" method="post" class="form-horizontal">
                    <%--<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />--%>

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
                        <input type="submit" class="btn btn-block btn-primary btn-default" value="Send e-mail to change password" />
                    </div>

                    <br />

                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>