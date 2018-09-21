<%--
  Created by IntelliJ IDEA.
  User: mrelac
  Date: 06/14/2018
  Time: 08:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${title}</title>
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
                <form action="changePasswordEmail" method="POST" class="form-horizontal">
                    <div class="alert alert-success">
                        <c:if test="${showWhen}">
                            ${current.toLocaleString()}:&nbsp;
                        </c:if>
                        <p>${status}</p>
                    </div>

                    <br />

                    <c:if test="${showLoginLink}">
                        <a href="${paBaseUrl}/rilogin">Log in</a>
                    </c:if>
                    <c:if test="${showSummaryLink}">
                        <a href="${paBaseUrl}/summary">Summary</a>
                    </c:if>

                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>