<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false" %>

<t:genericpage>

    <jsp:attribute name="title">Expired link</jsp:attribute>

    <jsp:attribute name="bodyTag">
        <body class="no-sidebars small-header">
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>
        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Expired link</h2>
                </div>
            </div>
        </div>

        <div class="container white-bg-small">

            <div class="breadcrumbs clear row">
                <div class="col-md-12">
                    <aside><a href="${baseUrl}">Home</a>
                        <span class="fal fa-angle-right"></span><a href="${baseUrl}/summary">My Genes</a>
                        <span class="fal fa-angle-right"></span>Create account / reset password
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

                                        <form action="${baseUrl}/summary" method="GET" class="form-horizontal">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <c:choose>
                                                <c:when test="${emailAddress == null}">
                                                    <h3>Create account or reset password</h3>
                                                </c:when>
                                                <c:when test="${emailAddress != null}">
                                                    <h3>Create account or reset password for <i>${emailAddress}</i>
                                                    </h3>
                                                </c:when>
                                            </c:choose>
                                            <div class="alert alert-danger">
                                                The link you clicked on to create your account or change your password
                                                has expired.
                                            </div>
                                            <div class="form-actions mt-4 mb-4">
                                                <c:choose>
                                                    <c:when test="${emailAddress == null}">
                                                        <input type="submit" class="btn btn-primary btn-default"
                                                               value="Continue to My Genes login page"/>
                                                    </c:when>
                                                    <c:when test="${emailAddress != null}">
                                                        <input type="submit" class="btn btn-primary btn-default"
                                                               value="Continue to My Genes"/>
                                                    </c:when>
                                                </c:choose>
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