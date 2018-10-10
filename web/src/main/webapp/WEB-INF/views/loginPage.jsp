<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page isELIgnored="false"%>

<t:genericpage>

    <jsp:attribute name="title">IMPC Login</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${paBaseUrl}/rilogin">Register Interest</a> &raquo; Login</jsp:attribute>

    <jsp:attribute name="header">

        <script src='https://www.google.com/recaptcha/api.js'></script>

        <!-- This style makes a button look like an IMPC link. It is meant to be used where you need to do a POST
             on a part of the page that has nearby buttons that are meant to stand out more than the POST link decorated
             with this class. -->
        <%--<style>--%>
            <%--.like_anchor {--%>
                <%--align-items: normal;--%>
                <%--background-color: rgba(0,0,0,0);--%>
                <%--border-color: rgb(0, 0, 238);--%>
                <%--border-style: none;--%>
                <%--box-sizing: content-box;--%>
                <%--cursor: pointer;--%>
                <%--display: inline;--%>
                <%--font: inherit;--%>
                <%--height: auto;--%>
                <%--padding: 0;--%>
                <%--perspective-origin: 0 0;--%>
                <%--text-align: start;--%>
                <%--transform-origin: 0 0;--%>
                <%--width: auto;--%>
                <%---moz-appearance: none;--%>
                <%---webkit-logical-height: 1em; /* Chrome ignores auto, so we have to use this hack to set the correct height  */--%>
                <%---webkit-logical-width: auto; /* Chrome ignores auto, but here for completeness */--%>

                <%--color: #0978a1;--%>
                <%--fill: #0978a1;--%>
                <%--text-decoration: none;--%>
            <%--}--%>

            <%--/* Mozilla uses a pseudo-element to show focus on buttons, */--%>
            <%--/* but anchors are highlighted via the focus pseudo-class. */--%>

            <%--@supports (-moz-appearance:none) { /* Mozilla-only */--%>
            <%--like_anchor::-moz-focus-inner { /* reset any predefined properties */--%>
            <%--border: none;--%>
            <%--padding: 0;--%>
            <%--}--%>
            <%--like_anchor:focus { /* add outline to focus pseudo-class */--%>
            <%--outline-style: dotted;--%>
            <%--outline-width: 1px;--%>
            <%--}--%>
            <%--}--%>

        <%--</style>--%>

    </jsp:attribute>
    <jsp:attribute name="bodyTag">
        <body>
    </jsp:attribute>

    <jsp:attribute name="addToFooter"></jsp:attribute>

    <jsp:body>

        <div id="mainWrapper" style="display: flex">
            <div class="login-container" style="width: 45%">

                <h1>Register Interest</h1>
                <p>
                    The Register Interest system is a place where you can register and unregister genes of interest,
                    change your Register Interest password, and delete all of the genes for which you have registered.
                </p>

                <br />

                <div class="login-card">
                    <div class="login-form">
                        <form action="${paBaseUrl}/rilogin" method="POST" class="form-horizontal">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

                            <c:if test="${param.error != null}">

                                <c:if test="${empty param.errorMessage}">
                                    <div class="messages error">
                                        <p>Invalid username and password.</p>
                                    </div>
                                </c:if>
                                <c:if test="${not empty param.errorMessage}">
                                    <div class="messages error">
                                        <p>${param.errorMessage}</p>
                                    </div>
                                </c:if>
                            </c:if>
                            <c:if test="${param.logout != null}">
                                <div class="messages" style="color: indigo">
                                    <p>You have been logged out successfully.</p>
                                </div>
                            </c:if>
                            <c:if test="${param.deleted != null}">
                                <div class="messages" style="color: indigo">
                                    <p>Your account has been deleted as requested.</p>
                                </div>
                            </c:if>
                            <c:if test="${param.status != null}">
                                <div class="messages" style="color: indigo">
                                    <p>${param.status}</p>
                                </div>
                            </c:if>

                            <div class="form-item form-type-textfield form-item-name">
                                <input type="text" class="form-conrol required" id="username" name="ssoId" placeholder="Enter e-mail address" required />
                            </div>

                            <div class="form-item form-type-password form-item-pass">
                                <input type="password" class="form-control  required" id="password" name="password" placeholder="Enter password" required />
                            </div>

                            <div class="g-recaptcha" data-sitekey="6LckO3QUAAAAAAYOdZIrVrfhiz7Xueo_9l8mbCcQ"></div>

                            <div class="form-actions">
                                <input type="submit" class="btn btn-block btn-primary btn-default" value="Log in" />
                            </div>

                            <br/>

                        </form>



                        <a href="${paBaseUrl}/newAccountRequest">New account</a>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="${paBaseUrl}/resetPasswordRequest">Forgot password?</a>
                    </div>
                </div>
            </div>

            <div style="width: 10%"></div>

            <div style="width: 45%">
                <h1>Forum</h1>
                <p>
                    The IMPC forum is a place where you can apply for roles, create bookmarks, edit your IMPC Forum details, view and manage subscriptions, and send private messages.
                </p>

                <br />

                <a href="${drupalBaseUrl}/user/login">Log in to the IMPC forum</a>
            </div>
        </div>

    </jsp:body>
</t:genericpage>