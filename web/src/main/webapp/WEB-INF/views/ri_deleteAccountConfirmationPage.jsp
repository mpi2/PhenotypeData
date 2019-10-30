<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">Delete account</jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div class="container single single--no-side">

        <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

            <div class="row">
                <div class="col-md-12">
                    <p><a href="${paBaseUrl}">Home</a>
                        <span class="fal fa-angle-right"></span>Delete account
                    </p>
                </div>
            </div>
        </div>

        <div class="row row-over-shadow">
            <div class="col-md-12 white-bg">
                <div class="page-content">
                    <h2 class="title" id="top">Delete account</h2>

                    <div class="login-form">

                        <form action="accountDeleteConfirmation" method="POST" class="form-horizontal">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                            <h3>Delete account <i>${emailAddress}</i></h3>
                            <strong><i>WARNING:</i> this action cannot be undone. This action will remove your "My Genes"
                                account and all of your followed genes. Your e-mail address and followed genes will be
                                permanently lost.</strong>

                            <div class="form-actions mt-3">
                                <input type="submit" class="btn btn-outline-danger" value="Permanently delete account" />
                            </div>

                            <a class="btn" href="${paBaseUrl}/search">Search all genes</a>

                        </form>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>