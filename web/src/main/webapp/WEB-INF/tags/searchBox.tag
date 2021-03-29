<%@ tag import="org.mousephenotype.cda.common.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ tag import="java.util.List" %>
<%@ tag import="java.util.ArrayList" %>
<%@ tag import="org.apache.commons.text.WordUtils" %>

<%@ attribute name="isPhenotypePage" required="true" type="java.lang.Boolean"%>
<%@ attribute name="baseUrl" required="true" type="java.lang.String"%>
<%@ attribute name="cmsBaseUrl" required="true" type="java.lang.String"%>
<%@ attribute name="term" required="false" type="java.lang.String"%>
<%@ attribute name="type" required="true" type="java.lang.String"%>

<div class="portal-search pb-5 mb-5 mt-5">
    <div class="portal-search__tabs">
        <a id="geneSearchTab" data-type="gene" class="portalTab <c:if test="${param.type != 'phenotype' and !isPhenotypePage}"> left-shadow  active</c:if>" href="${baseUrl}/search">Genes</a>
        <a id="phenotypeSearchTab" data-type="pheno" class=" portalTab right-shadow <c:if test="${param.type == 'phenotype'}">active</c:if> <c:if test="${isPhenotypePage}">active</c:if>" href="${baseUrl}/search?type=phenotype">Phenotypes</a>
        <a id="everythingElseSearchTab" data-type="other" class=" portalTab right-shadow" href="${cmsBaseUrl}/?s=">Help,&nbsp;News,&nbsp;Blog</a>
    </div>
    <div class="portal-search__inputs">
        <form id="searchForm" action="${baseUrl}/search">
            <input id="searchTerm" name="term" class="portal-search__input" value="${term}" placeholder="Search All ${requestConfig["data_release_genes"]} Knockout Data..." type="text"/>
            <button id="searchIcon" type="submit"><i class="fas fa-search"></i></button>
            <c:choose>
                <c:when test="${isPhenotypePage}">
                    <input id="searchType" type="hidden" name="type" value="phenotype">
                </c:when>
                <c:otherwise>
                    <input id="searchType" type="hidden" name="type" value="${type}">
                </c:otherwise>
            </c:choose>
            <div id="searchLoader" class="lds-ring">
                <div></div>
                <div></div>
                <div></div>
                <div></div>
            </div>
        </form>
    </div>
</div>