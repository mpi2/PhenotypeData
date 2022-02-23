<%@ tag description="Menu template" pageEncoding="UTF-8" import="uk.ac.ebi.phenotype.web.util.CmsMenu" %>
<%@ tag import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%
    /*
     Get the menu JSON from CMS, fallback to a default menu when CMS endpoint is not responding
     */
    String baseUrl = (request.getAttribute("baseUrl") != null && !((String) request.getAttribute("baseUrl")).isEmpty()) ? (String) request.getAttribute("baseUrl") : application.getInitParameter("base_url");
    jspContext.setAttribute("baseUrl", baseUrl);

    String url = (String) request.getAttribute("cmsBaseUrl");
    CmsMenu proxy = new CmsMenu();
    List<CmsMenu.MenuItem> menu = proxy.getCmsMenu(url, baseUrl);
    jspContext.setAttribute("menu", menu);
%>

<div class="header">
    <div class="header__nav-top d-none d-lg-block">
        <div class="container text-right">
            <div class="row">
                <div class="col">
                    <div class="menu-top-nav-container">
                        <ul id="menu-top-nav" class="menu">
                            <li class="menu-item"><a href="${cmsBaseUrl}/help/">Help</a></li>
                            <li class="menu-item"><a href="https://cloud.mousephenotype.org">IMPC Cloud</a></li>
                            <li class="menu-item"><a href="${cmsBaseUrl}/contact-us/">Contact us</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="header__nav">
        <div class="container">
            <div class="row">
                <div class="col-3">
                    <a href="/" class="header__logo-link active">
                        <img class="header__logo lazy loaded"
                             src="https://www.mousephenotype.org/wp-content/themes/impc/images/IMPC_10_YEAR_Logo.svg"
                             data-src="https://www.mousephenotype.org/wp-content/themes/impc/images/IMPC_10_YEAR_Logo.svg"
                             alt="Internation Mouse Phenotyping Consortium Office Logo"
                             data-was-processed="true"
                             width="190px" />
                    </a>
                </div>
                <div class="col-9 text-right">
                    <span class="d-none d-lg-block">
                        <div class="menu-main-nav-container">
                            <ul id="menu-main-nav" class="menu">
                                <c:forEach begin="0" end="${menu.size()-1}" var="i">
                                    <li id="${menu.get(i).cssId}" class='menu-item ${menu.get(i).cssId} <c:if test="${
                                    (menu.get(i).name.toLowerCase().contains('data') && !requestScope['javax.servlet.forward.request_uri'].contains('sexual-dimorphism') && !requestScope['javax.servlet.forward.request_uri'].contains('hearing')) && !requestScope['javax.servlet.forward.request_uri'].contains('publications') ||
                                    (menu.get(i).name.toLowerCase().contains('publications') && (requestScope['javax.servlet.forward.request_uri'].contains('sexual-dimorphism') || requestScope['javax.servlet.forward.request_uri'].contains('hearing') || requestScope['javax.servlet.forward.request_uri'].contains('publications')))
                                    }">current-menu-item</c:if>'>
                                        <a href="${menu.get(i).url}">${menu.get(i).name}</a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </span>

                    <button class="navbar-toggler collapsed d-inline d-lg-none "
                            type="button" data-toggle="collapse" data-target="#navbarToggleExternalContent "
                            aria-controls="navbarToggleExternalContent"
                            aria-expanded="false" aria-label="Toggle navigation">
                        <span class="icon-bar top-bar"></span>
                        <span class="icon-bar middle-bar"></span>
                        <span class="icon-bar bottom-bar"></span>
                    </button>
                    <div class="collapse" id="searchBar">
                        <form action="/">
                            <div class="row search-pop no-gutters">
                                <div class="search-pop__input col col-9 text-left">
                                    <p><br/></p>
                                    <input id="searchField" type="search" class="form-control" id="s" name="s" placeholder="Search documentation and news">
                                </div>
                                <div class="col col-3 text-right search-submit">
                                    <button type="submit">Search <i class="fal fa-search"></i></button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="header__drop"></div>

    <div class="mobile-nav collapse" id="navbarToggleExternalContent">
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarToggleExternalContent "
                aria-controls="navbarToggleExternalContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="icon-bar top-bar"></span>
            <span class="icon-bar middle-bar"></span>
            <span class="icon-bar bottom-bar"></span>
        </button>
        <p class="mobile-nav__search-text"><br/></p>
        <div class="mobile-nav__search mb-3">
            <form action="/">
                <div class="row">
                    <div class="col col-10 text-left">
                        <input type="search" class="form-control" id="s" name="s"
                               placeholder="Search documentation and news">
                    </div>
                    <div class="col col-2 text-right">
                        <button type="submit"><i class="fas fa-search"></i></button>
                    </div>
                </div>
            </form>
        </div>
        <div class="row">
            <div class="col-12">

                <c:forEach begin="0" end="${menu.size()-1}" var="i">
                    <h3 class="mt-2"><a class="${menu.get(i).mobileCssId}" href="${menu.get(i).url}">${menu.get(i).name}</a></h3>
                    <div class="mobile-nav__sub-pages">
                        <c:if test="${menu.get(i).children.size() > 0}">
                            <c:forEach begin="0" end="${menu.get(i).children.size()-1}" var="j">
                                <p><a href="${menu.get(i).children.get(j).url}">${menu.get(i).children.get(j).name}</a></p>
                                <div class="sub-pages">
                                    <c:if test="${menu.get(i).children.get(j).children.size() > 0}">
                                        <c:forEach begin="0" end="${menu.get(i).children.get(j).children.size()-1}" var="k">
                                            <p><a href="${menu.get(i).children.get(j).children.get(k).url}">${menu.get(i).children.get(j).children.get(k).name}</a></p>
                                        </c:forEach>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </c:forEach>

                <h3 class="mt-2"><a class="object-id-11" href="${cmsBaseUrl}/help/">Help</a></h3>
                <div class="mobile-nav__sub-pages"></div>

                <h3 class="mt-2"><a href="http://cloud.mousephenotype.org">IMPC Cloud</a></h3>
                <div class="mobile-nav__sub-pages"></div>

                <h3 class="mt-2"><a class="object-id-12" href="${cmsBaseUrl}/contact-us/">Contact us</a></h3>
                <div class="mobile-nav__sub-pages"></div>
            </div>
        </div>
    </div>

    <c:forEach begin="0" end="${menu.size()-1}" var="i">
    <div class="${menu.get(i).menuId} sub-menu collapse" id="${menu.get(i).menuId}">
        <c:if test="${menu.get(i).children.size() > 0}">
            <div class="${menu.get(i).menuId}__inside">
                    <div class="container">
                        <div class="row no-gutters justify-content-end">

                            <c:if test="${menu.get(i).name.toLowerCase().contains('about')}">
                                <div class="col col-auto text-left">
                                    <a href="${menu.get(i).url}">${menu.get(i).name}</a>
                                </div>
                            </c:if>
                            <c:forEach begin="0" end="${menu.get(i).children.size()-1}" var="j">
                                <div class="col col-auto text-left">
                                    <a href="${menu.get(i).children.get(j).url}">${menu.get(i).children.get(j).name}</a>
                                    <div class="sub-pages">
                                        <c:if test="${menu.get(i).children.get(j).children.size() > 0}">
                                            <c:forEach begin="0" end="${menu.get(i).children.get(j).children.size()-1}" var="k">
                                                <p><a href="${menu.get(i).children.get(j).children.get(k).url}">${menu.get(i).children.get(j).children.get(k).name}</a></p>
                                            </c:forEach>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
            </div>
            <div class="${menu.get(i).menuId}__drop"></div>
        </c:if>
    </div>
    </c:forEach>
</div>


<div class="click-guard"></div>

