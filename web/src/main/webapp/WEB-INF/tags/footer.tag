<%@ tag import="uk.ac.ebi.phenotype.web.util.CmsMenu, java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%
    /*
     Get the menu JSON from CMS, fallback to a default menu when CMS endpoint is not responding
     */

    String baseUrl = (request.getAttribute("baseUrl") != null && !((String) request.getAttribute("baseUrl")).isEmpty()) ? (String) request.getAttribute("baseUrl") : application.getInitParameter("baseUrl");
    jspContext.setAttribute("baseUrl", baseUrl);

    String url = (String) request.getAttribute("cmsBaseUrl");
    CmsMenu proxy = new CmsMenu();
    List<CmsMenu.MenuItem> menu = proxy.getCmsMenu(url, baseUrl);
    jspContext.setAttribute("menu", menu);
%>

<div class="footer">
    <div class="container">
        <div class="row">

            <div class="col-12 col-md-6 footer-text mb-3">
                <p><i class="fab fa-creative-commons fa-2x mr-2"></i> <i class="fab fa-creative-commons-by fa-2x"></i></p>
                <p>Content on this site is licensed under a <a href="${cmsBaseUrl}/help/faqs/is-impc-data-freely-available/">Creative Commons Attribution 4.0 International license</a></p>
                <p><a href="${cmsBaseUrl}/about-impc/accessibility-cookies/">Privacy &amp; Cookies</a><a href="${cmsBaseUrl}/about-impc/terms-of-use/">Terms of use</a></p>
            </div>

            <div class="col-12 col-md-3 footer-nav">
                <div class="menu-main-nav-container">
                    <ul id="menu-main-nav-1" class="menu">
                        <c:forEach begin="0" end="${menu.size()-1}" var="i">
                            <li id="${menu.get(i).cssId}" class='menu-item ${menu.get(i).cssId}'><a
                                    href="${menu.get(i).url}">${menu.get(i).name}</a></li>
                        </c:forEach>
                    </ul>
                </div>
            </div>

            <div class="col-12 col-md-3 footer-nav">
                <div class="menu-top-nav-container">
                    <ul id="menu-top-nav-1" class="menu">
                        <li class="impc-badge menu-item"><a href="${cmsBaseUrl}/help/faqs/how-do-i-cite-the-impc/">Cite IMPC</a></li>
                        <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-13"><a
                                href="${cmsBaseUrl}/help/">Help</a></li>
                        <li class="menu-item menu-item-type-custom menu-item-object-custom menu-item-14"><a href="http://cloud.mousephenotype.org">IMPC Cloud</a></li>
                        <li class="menu-item menu-item-type-post_type menu-item-object-page menu-item-15"><a
                                href="${cmsBaseUrl}/contact-us/">Contact us</a></li>
                    </ul>
                </div>
            </div>

        </div>

        <div class="row mt-3">
            <div class="col-12 col-md-6">
                <ul class="footer__social">
                    <li>
                        <a href="https://twitter.com/impc" target="_blank" rel="noreferrer">
                            <span class="sr-only">IMPC Twitter</span>
                            <i class="fab fa-twitter"></i>
                        </a>
                    </li>
                    <li>
                        <a href="https://www.youtube.com/channel/UCXp3DhDYbpJHu4MCX_wZKww" target="_blank" rel="noreferrer">
                            <span class="sr-only">IMPC YouTube</span>
                            <i class="fab fa-youtube"></i>
                        </a>
                    </li>
                    <li>
                        <a href="https://www.facebook.com/InternationalMousePhenotypingConsortium" target="_blank" rel="noreferrer">
                            <span class="sr-only">IMPC Facebook</span>
                            <i class="fab fa-facebook"></i>
                        </a>
                    </li>
                    <li>
                        <a href="https://www.reddit.com/user/MousePhenotyping" target="_blank" rel="noreferrer">
                            <span class="sr-only">IMPC Reddit</span>
                            <i class="fab fa-reddit"></i>
                        </a>
                    </li>
                </ul>
            </div>
            <div class="col-12 col-md-6 text-right">
                <h6>
                    <a href="https://www.mousephenotype.org/data/release">
                        <small>Access Data Release <span id="data-no">${releaseVersion}</span> Data</small>
                    </a>
                </h6>
                <div class="menu-footer-access-container">
                    <ul id="menu-footer-access" class="menu">
                        <li id="menu-item-1887"
                            class="menu-item menu-item-type-post_type menu-item-object-page menu-item-1887"><a
                                href="${cmsBaseUrl}/understand/accessing-the-data/batch-query/">Batch query</a></li>
                        <li id="menu-item-1889"
                            class="menu-item menu-item-type-post_type menu-item-object-page menu-item-1889"><a
                                href="${cmsBaseUrl}/understand/accessing-the-data/access-via-api/">Access via API</a>
                        </li>
                        <li id="menu-item-1890"
                            class="menu-item menu-item-type-post_type menu-item-object-page menu-item-1890"><a
                                href="${cmsBaseUrl}/understand/accessing-the-data/access-via-ftp/">Access via FTP</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>


