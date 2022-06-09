<%@ tag description="Error page template" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%-- -------------------------------------------------------------------------- --%>
<%-- NOTE: All "magic" variables are defined in the DeploymentInterceptor class --%>
<%-- This includes such variables isbaseUrl, cmsBaseUrl and releaseVersion.. --%>
<%-- -------------------------------------------------------------------------- --%>

<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<%@attribute name="breadcrumb" fragment="true"%>
<%@attribute name="bodyTag" fragment="true"%>
<%@attribute name="addToFooter" fragment="true"%>

<c:set var="uri">${pageContext.request.requestURL}</c:set>
<c:set var="domain">${pageContext.request.serverName}</c:set>

<c:if test="${showFullpage}">

<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title><jsp:invoke fragment="title" /> | International Mouse Phenotyping Consortium</title>
    <link rel="profile" href="http://gmpg.org/xfn/11">
    <link rel="pingback" href="${cmsBaseUrl}/xmlrpc.php">
    <link rel="stylesheet" href="${baseUrl}/css/vendor/fapro/css/all.min.css" />
    <link rel="apple-touch-icon-precomposed" sizes="57x57" href="${baseUrl}/img/apple-touch-icon-57x57.png" />
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="${baseUrl}/img/apple-touch-icon-114x114.png" />
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="${baseUrl}/img/apple-touch-icon-72x72.png" />
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="${baseUrl}/img/apple-touch-icon-144x144.png" />
    <link rel="apple-touch-icon-precomposed" sizes="60x60" href="${baseUrl}/img/apple-touch-icon-60x60.png" />
    <link rel="apple-touch-icon-precomposed" sizes="120x120" href="${baseUrl}/img/apple-touch-icon-120x120.png" />
    <link rel="apple-touch-icon-precomposed" sizes="76x76" href="${baseUrl}/img/apple-touch-icon-76x76.png" />
    <link rel="apple-touch-icon-precomposed" sizes="152x152" href="${baseUrl}/img/apple-touch-icon-152x152.png" />
    <link rel="icon" type="image/png" href="${baseUrl}/img/favicon-196x196.png" sizes="196x196" />
    <link rel="icon" type="image/png" href="${baseUrl}/img/favicon-96x96.png" sizes="96x96" />
    <link rel="icon" type="image/png" href="${baseUrl}/img/favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="${baseUrl}/img/favicon-16x16.png" sizes="16x16" />
    <link rel="icon" type="image/png" href="${baseUrl}/img/favicon-128.png" sizes="128x128" />
    <meta name="msapplication-TileColor" content="#FFFFFF" />
    <meta name="msapplication-TileImage" content="${baseUrl}/img/mstile-144x144.png" />
    <meta name="msapplication-square70x70logo" content="${baseUrl}/img/mstile-70x70.png" />
    <meta name="msapplication-square150x150logo" content="${baseUrl}/img/mstile-150x150.png" />
    <meta name="msapplication-wide310x150logo" content="${baseUrl}/img/mstile-310x150.png" />
    <meta name="msapplication-square310x310logo" content="${baseUrl}/img/mstile-310x310.png" />
    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js'></script>
    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/js/bootstrap.bundle.min.js'
            async='async'></script>
    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/object-fit-images/3.2.4/ofi.min.js'></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs4/dt-1.10.18/r-2.2.2/datatables.min.css"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js" integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k" crossorigin="anonymous"></script>
    <script type="text/javascript" src="https://cdn.datatables.net/v/bs4/dt-1.10.18/r-2.2.2/datatables.min.js"></script>
    <script
            src="https://code.jquery.com/ui/1.12.0/jquery-ui.min.js"
            integrity="sha256-eGE6blurk5sHj+rmkfsGYeKyZx3M4bG+ZlFyA7Kns7E="
            crossorigin="anonymous"></script>




    <script type='text/javascript' src="${baseUrl}/js/general/toggle.js?v=${version}"></script>
    <script type="text/javascript" src="${baseUrl}/js/head.min.js?v=${version}"></script>
    <script type='text/javascript' src='${baseUrl}/js/buffaloZoo.js?v=${version}'></script>
    <script type="text/javascript" src="${baseUrl}/js/default.js?v=${version}"></script>
    <link rel="stylesheet" href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/fancyapps/fancybox@3.5.6/dist/jquery.fancybox.min.css" />
    <script src="https://cdn.jsdelivr.net/gh/fancyapps/fancybox@3.5.6/dist/jquery.fancybox.min.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css">
    <!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js" integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut" crossorigin="anonymous"></script>
     replaced with below as unable to get his due to CORS or licence?-->
     
    <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" media='all'/>
    <link rel="stylesheet" type="text/css" href="https://ebi.emblstatic.net/web_guidelines/EBI-Icon-fonts/v1.3/fonts.css">
    <link rel="stylesheet" href="${cmsBaseUrl}/wp-content/themes/impc/css/styles.css?version=20200213">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/css/bootstrap-select.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/js/bootstrap-select.min.js"></script>
    <!-- Latest compiled and minified JavaScript -->
    
    <script>
    <%--
    Some browsers do not provide a console object see:
    http://stackoverflow.com/questions/690251/what-happened-to-console-log-in-ie8
    http://digitalize.ca/2010/04/javascript-tip-save-me-from-console-log-errors/
    // In case we forget to take out console statements. IE fails otherwise
    --%>
    try {
        console.log(" ");
    } catch (err) {
        var console = {};
        console.log = console.error = console.info = console.debug = console.warn = console.trace = console.dir = console.dirxml = console.group = console.groupEnd = console.time = console.timeEnd = console.assert = console.profile = function () {
        };
    }

    <jsp:useBean id="requestConfig" scope="request" type="java.util.Map"/>
    <c:forEach var="entry" items="${requestConfig}">
    var ${entry.key} = "${entry.value}";
    </c:forEach>

</script>

        <%--
        Include google tracking code
        --%>
    <script src="https://www.mousephenotype.org/assets/lib/cookie-consent/consent.js" defer></script>
    <link rel="stylesheet" href="https://www.mousephenotype.org/assets/lib/cookie-consent/consent.css" />
    <script>
        $(document).ready(function () {
            let googleAnalyticsTags = [],
                currentlURL = window.location.href;

            if (currentlURL.indexOf("dev.mousephenotype") > -1) {
                googleAnalyticsTags = ["UA-137368025-1", "G-THTSZ7NZJ1"];
                addCookieBanner(googleAnalyticsTags);
            } else if (currentlURL.indexOf("beta.mousephenotype") > -1) {
                googleAnalyticsTags = ["UA-137368025-2", "G-ZNMLQY0YJ9"];
                addCookieBanner(googleAnalyticsTags);
            } else if (currentlURL.indexOf("www.mousephenotype") > -1) {
                googleAnalyticsTags = ["UA-23433997-1", "G-0CGYY2B2QL"];
                addCookieBanner(googleAnalyticsTags);
            }

            function addCookieBanner(tags) {
                $.CCbanner({
                    "cookies": ["Analytics"],
                    "href": "https://www.mousephenotype.org/about-impc/accessibility-cookies/",
                    "gaTag": tags
                });
            }
        });
    </script>

    <jsp:invoke fragment="header" />

</head>


<c:choose>
    <c:when test="${not empty bodyTag}">
        <jsp:invoke fragment="bodyTag"/>
    </c:when>
    <c:otherwise>
        <body>
    </c:otherwise>
</c:choose>

    <t:menu />

</c:if>

<main id="main" class="main" role="main">

    <jsp:doBody />

    <c:if test="${showFullpage}">
    <div class="news-letter pt-5 pb-5">
        <div class="container">
            <div class="row">
                <div class="col-12 col-md-8 offset-md-2 text-center">
                    <h2>The IMPC Newsletter</h2>
                    <p class="mt-4">
                        Get highlights of the most important data releases,
                        news and events, delivered straight to your email inbox</p>
                    <a class="btn btn-mailing btn-primary" target="_blank" href="https://forms.office.com/Pages/ResponsePage.aspx?id=jYTJ3EdDnkKo2NytnQUzMV3vNFn2DMZLqTjqljGsCfNUMDg0Q09OQThJUkVaVUpONVpSTVVSVEZERy4u">Subscribe
                        to newsletter</a>
                </div>
            </div>
        </div>
    </div>
    </c:if>
</main>

<c:if test="${showFullpage}">

    <t:footer />

    <jsp:invoke fragment="addToFooter"/>

    <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/utils/tools.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/general/ui.dropdownchecklist_modif.js?v=${version}'></script>
    <script type='text/javascript' src='${baseUrl}/js/documentationConfig.js?v=${version}'></script>


</body>

</html>

</c:if>
