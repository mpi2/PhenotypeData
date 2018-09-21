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
    <title>Change Password</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

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
</body>
</html>