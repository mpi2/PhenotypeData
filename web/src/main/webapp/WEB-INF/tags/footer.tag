<%@ tag import="uk.ac.ebi.phenotype.web.util.CmsMenu, java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%
    /*
     Get the menu JSON from CMS, fallback to a default menu when CMS endpoint is not responding
     */
    String url = (String) request.getAttribute("cmsBaseUrl");
    CmsMenu proxy = new CmsMenu();

    List<CmsMenu.MenuItem> menu = proxy.getCmsMenu(url);
    String baseUrl = (request.getAttribute("baseUrl") != null && !((String) request.getAttribute("baseUrl")).isEmpty()) ? (String) request.getAttribute("baseUrl") : application.getInitParameter("baseUrl");

    jspContext.setAttribute("baseUrl", baseUrl);
    jspContext.setAttribute("menu", menu);
%>

<div class="footer">
    <div class="container">
        <div class="row">

            <div class="col-12 col-md-6 footer-text mb-5 mb-md-0">
                <p><strong>&copy; 2019 IMPC International Mouse Phenotyping Consortium.</strong></p>
                <p><strong>All Rights Reserved.</strong></p>
                <p><strong><br />
                    <a href="${cmsBaseUrl}/about-impc/accessibility-cookies/">Accessibility &amp; Cookies</a></strong><br />
                    <a href="${cmsBaseUrl}/about-impc/terms-of-use/"><strong>Terms of use</strong></a></p>
            </div>

            <div class="col-12 col-md-3 footer-nav">
                <div class="menu-main-nav-container">
                    <ul id="menu-main-nav-1" class="menu">
                        <c:forEach begin="0" end="${menu.size()-1}" var="i">
                            <li id="${menu.get(i).cssId}" class='menu-item ${menu.get(i).cssId}'><a href="${menu.get(i).url}">${menu.get(i).name}</a></li>
                        </c:forEach>
                    </ul>
                </div>
            </div>

            <div class="col-12 col-md-3 footer-nav">
                <div class="menu-top-nav-container">
                    <ul id="menu-top-nav-1" class="menu">
                        <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-13"><a href="${cmsBaseUrl}/help/">Help</a></li>
                        <li class="menu-item menu-item-type-custom menu-item-object-custom menu-item-14"><a href="https://www.mousephenotype.org/forum">Forum</a></li>
                        <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-15"><a href="${cmsBaseUrl}/contact-us/">Contact us</a></li>
                    </ul>
                </div>
            </div>

        </div>
        <div class="row mt-3">
            <div class="col">
                <ul class="footer__social">
                    <li>
                        <a href="https://twitter.com/impc"
                           target="_blank"><i class="fab fa-twitter"></i></a>
                    </li>
                    <li>
                        <a href="https://www.instagram.com/mousephenotyping/"
                           target="_blank"><i class="fab fa-instagram"></i></a>
                    </li>
                    <li>
                        <a href="https://www.youtube.com/channel/UCXp3DhDYbpJHu4MCX_wZKww"
                           target="_blank"><i class="fab fa-youtube"></i></a>
                    </li>
                    <li>
                        <a href="https://www.facebook.com/InternationalMousePhenotypingConsortium"
                           target="_blank"><i class="fab fa-facebook"></i></a>
                    </li>
                    <li>
                        <a href="https://www.reddit.com/user/MousePhenotyping"
                           target="_blank"><i class="fab fa-reddit"></i></a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>


