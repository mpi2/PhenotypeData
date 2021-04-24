<%@ tag description="Overall Page template" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <c:if test="${not fn:contains(uri, 'comparator.jsp' )}">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    </c:if>
    <title><jsp:invoke fragment="title" /> | International Mouse Phenotyping Consortium</title>
    <link rel="profile" href="http://gmpg.org/xfn/11">
    <link rel="pingback" href="${cmsBaseUrl}/xmlrpc.php">

    <%-- --- --%>
    <%-- Load async CSS stylesheet, see https://www.filamentgroup.com/lab/load-css-simpler/ --%>
    <%-- --- --%>

    <link rel="preload" type="text/css" href="${baseUrl}/css/vendor/fapro/css/all.min.css" as="style"/>
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/vendor/fapro/css/all.min.css" media="print" onload="this.media='all'" />

    <link rel="preload" type="text/css" href="https://ebi.emblstatic.net/web_guidelines/EBI-Icon-fonts/v1.3/fonts.css" as="style" />
    <link rel="stylesheet" type="text/css" href="https://ebi.emblstatic.net/web_guidelines/EBI-Icon-fonts/v1.3/fonts.css" media="print" onload="this.media='all'" />

    <link rel="preload" type="text/css" href="${cmsBaseUrl}/wp-content/themes/impc/css/styles.css?v=${version}" as="style"/>
    <link rel="stylesheet" type="text/css" href="${cmsBaseUrl}/wp-content/themes/impc/css/styles.css?v=${version}" />

    <link rel="preload" type="text/css" href="${baseUrl}/css/default.css" as="style"/>
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/default.css" media="print" onload="this.media='all'" />

    <link rel="preload" type="text/css" href="${baseUrl}/css/impc-icons.css" as="style"/>
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/impc-icons.css" media="print" onload="this.media='all'" />

    <link rel="preload" type="text/css" href="https://unpkg.com/bootstrap-table@1.18.3/dist/bootstrap-table.min.css" as="style"/>
    <link rel="stylesheet" type="text/css" href="https://unpkg.com/bootstrap-table@1.18.3/dist/bootstrap-table.min.css" media="print" onload="this.media='all'" />

    <%-- --- --%>
    <%-- Finish with async stylesheets --%>
    <%-- --- --%>

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

    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>
    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/object-fit-images/3.2.4/ofi.min.js'></script>
    <script type='text/javascript' src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>

    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs4/dt-1.10.18/r-2.2.2/datatables.min.css"/>

    <script defer type="text/javascript" src="https://cdn.datatables.net/v/bs4/dt-1.10.18/r-2.2.2/datatables.min.js"></script>
    <script type='text/javascript' src="https://unpkg.com/bootstrap-table@1.18.3/dist/bootstrap-table.min.js"></script>
    <script type='text/javascript' src="https://unpkg.com/bootstrap-table@1.18.3/dist/extensions/mobile/bootstrap-table-mobile.min.js"></script>
    <script type='text/javascript' src="https://unpkg.com/bootstrap-table@1.18.3/dist/extensions/cookie/bootstrap-table-cookie.min.js"></script>



    <script defer
            src="https://code.jquery.com/ui/1.12.0/jquery-ui.min.js"
            integrity="sha256-eGE6blurk5sHj+rmkfsGYeKyZx3M4bG+ZlFyA7Kns7E="
            crossorigin="anonymous"></script>

    <script defer
            src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
            integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
            crossorigin="anonymous"></script>

<%--    <script type='text/javascript' src="${baseUrl}/js/general/toggle.js?v=${version}" async></script>--%>
    <script defer type="text/javascript" src="${baseUrl}/js/head.min.js?v=${version}" ></script>
    <script defer type='text/javascript' src='${baseUrl}/js/buffaloZoo.js?v=${version}' ></script>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/fancyapps/fancybox@3.5.6/dist/jquery.fancybox.min.css" />
    <script src="https://cdn.jsdelivr.net/gh/fancyapps/fancybox@3.5.6/dist/jquery.fancybox.min.js" defer></script>
    <script type="text/javascript" src="${baseUrl}/js/default.js?v=${version}" defer></script>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/css/bootstrap-select.min.css">

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/js/bootstrap-select.min.js" async></script>
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

    <c:forEach var="entry" items="${requestConfig}">
    <c:if test="${entry.key != 'internalSolrUrl' && entry.key != 'mappedHostname' && entry.key != 'isProxied'}">var ${entry.key} = "${entry.value}";</c:if></c:forEach>


    $(document).ready(function () {
        $.widget.bridge('uibutton', $.ui.button);
        $.widget.bridge('uitooltip', $.ui.tooltip);
        $('[data-toggle="tooltip"]').tooltip({
            trigger : 'hover'
        });
    });
</script>


    <%--
    Include google tracking code on live site
    --%>
    <c:if test="${liveSite}">
        <script>
            (function (i, s, o, g, r, a, m) {
                i['GoogleAnalyticsObject'] = r;
                i[r] = i[r] || function () {
                    (i[r].q = i[r].q || []).push(arguments)
                }, i[r].l = 1 * new Date();
                a = s.createElement(o),
                    m = s.getElementsByTagName(o)[0];
                a.async = 1;
                a.src = g;
                m.parentNode.insertBefore(a, m)
            })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

            ga('create', 'UA-23433997-1', 'auto');
            ga('set', 'anonymizeIp', true);
            ga('send', 'pageview');
        </script>
    </c:if>

    <style>
        .table-borderless > tbody > tr > td,
        .table-borderless > tbody > tr > th,
        .table-borderless > tfoot > tr > td,
        .table-borderless > tfoot > tr > th,
        .table-borderless > thead > tr > td,
        .table-borderless > thead > tr > th {
            border: none !important;
        }
        .table-reduced-padding > tbody > tr > td,
        .table-reduced-padding > tbody > tr > th,
        .table-reduced-padding > tfoot > tr > td,
        .table-reduced-padding > tfoot > tr > th,
        .table-reduced-padding > thead > tr > td,
        .table-reduced-padding > thead > tr > th {
            padding: 0.2rem !important;
        }
    </style>

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

<main id="main" class="main" role="main">

    <div class="container-fluid">
        <div class="single-header">
            <img src="${cmsBaseUrl}/wp-content/uploads/2019/02/understanding-150x150.png"
                 srcset="${cmsBaseUrl}/wp-content/uploads/2019/02/understanding-300x62.png 300w, ${cmsBaseUrl}/wp-content/uploads/2019/02/understanding-768x158.png 768w, ${cmsBaseUrl}/wp-content/uploads/2019/02/understanding-1024x210.png 1024w, ${cmsBaseUrl}/wp-content/uploads/2019/02/understanding.png 1440w"
                 sizes="100%"
                 alt="banner" />

            <div class="container">
                <div class="row text-center justify-content-md-center">
                    <div class="col-12 col-md-8">
                        <t:searchBox isPhenotypePage="${isPhenotypePage}" baseUrl="${baseUrl}" cmsBaseUrl="${cmsBaseUrl}" type="${type}" ></t:searchBox>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:doBody />

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

</main>

    <t:footer />

<jsp:invoke fragment="addToFooter"/>
    <script defer type='text/javascript' src='${baseUrl}/js/utils/tools.js?v=${version}'></script>
</body>

</html>
