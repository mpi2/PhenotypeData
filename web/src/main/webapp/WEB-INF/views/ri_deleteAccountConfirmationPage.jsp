<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">Delete account</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/summary">Register Interest</a> &raquo; Delete account</jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body>
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>
        <div id="mainWrapper">
            <div class="login-container">
                <div class="login-card">
                    <div class="login-form">

                        <form action="accountDeleteConfirmation" method="POST" class="form-horizontal">
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

                            <a href="${paBaseUrl}/summary">Summary</a>

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>