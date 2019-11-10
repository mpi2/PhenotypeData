<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">${action}</jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div class="container">
        <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

            <div class="row">
                <div class="col-md-12">
                    <p><a href="${baseUrl}">Home</a>
                        <span class="fal fa-angle-right"></span><a href="${baseUrl}/summary">My Genes</a>
                        <span class="fal fa-angle-right"></span> ${action}
                    </p>
                </div>
            </div>
        </div>

        <div class="row row-over-shadow">
            <div class="col-md-12 white-bg">
                <h2 class="title">${action}</h2>

                <h6>Setting password for: ${emailAddress}</h6>

                <div class="login-form row ml-0 mr-0 mb-2">

                    <form
                            class="col-md-6 mt-3"
                            action="${baseUrl}/setPassword?action=${action}"
                            method="POST">

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

                        <div class="form-group row">
                            <label for="newPassword" class="col-4 pl-0 col-form-label">New password</label>
                            <div class="col-6 m-0 pl-0">
                                <input
                                        class="m-0 pl-1 col-md-12"
                                        type="password"
                                        class="form-control"
                                        id="newPassword"
                                        name="newPassword"
                                        required />
                            </div>
                        </div>

                        <div class="form-group row">
                            <label for="repeatPassword" class="col-4 pl-0 col-form-label">Repeat password</label>
                            <div class="col-6 m-0 pl-0">
                                <input
                                        class="m-0 pl-1 col-md-12"
                                        type="password"
                                        class="form-control"
                                        id="repeatPassword"
                                        name="repeatPassword"
                                        required />
                            </div>
                        </div>

                        <br />

                        <div class="form-actions mb-3">
                            <input type="submit" class="btn btn-block btn-primary btn-default" value="${action}" />
                        </div>
                    </form>
                </div>
            </div>
        </div>

    </jsp:body>
</t:genericpage>