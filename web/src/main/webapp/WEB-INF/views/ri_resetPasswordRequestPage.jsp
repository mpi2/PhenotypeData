);<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">Reset password</jsp:attribute>

    <jsp:attribute name="bodyTag">
          <body class="no-sidebars small-header">
      </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>
        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Reset password</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">

            <div class="breadcrumbs clear row">
                <div class="col-md-12">
                    <aside><a href="${baseUrl}">Home</a>
                        <span class="fal fa-angle-right"></span><a href="${baseUrl}/summary">My Genes</a>
                        <span class="fal fa-angle-right"></span>Reset password
                    </aside>
                </div>
            </div>

            <div class="row">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="row no-gutters">
                                <div lass="col-12 px-0">
                                    <div class="login-form">

                                        <form action="${baseUrl}/resetPasswordConfirmation" method="POST" class="form-horizontal">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <h3>Reset password for <i>${emailAddress}</i></h3>
                                            This action sends you an e-mail that you may use to reset your password.
                                            <div class="form-actions mt-4 mb-4">
                                                <input type="submit" class="btn btn-primary btn-default" value="Reset my password" />
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>