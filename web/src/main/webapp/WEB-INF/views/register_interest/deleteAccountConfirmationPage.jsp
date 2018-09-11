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
    <title>Delete account</title>
    <link href="<c:url value='/resources/css/bootstrap.css' />"  rel="stylesheet"></link>
    <link href="<c:url value='/resources/css/login.css' />" rel="stylesheet"></link>
    <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.css" />
</head>

<body>
<div id="mainWrapper">
    <div class="login-container">
        <div class="login-card">
            <div class="login-form">

                <form action="account" method="post" class="form-horizontal">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                    <h3>Delete account <i>${emailAddress}</i></h3>
                    <strong><i>WARNING:</i> this action cannot be undone. This action will remove you entirely from the Register Interest
                        system. Your e-mail address, roles, and registered genes of interest will be permanently lost.</strong>

                    <br />

                    <div class="form-actions">
                        <input type="submit" class="btn btn-block btn-primary btn-default" value="Permanently delete account" />
                    </div>

                    <br/>
                    <br />

                    <a href="${riBaseUrl}/summary">Summary</a>

                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>