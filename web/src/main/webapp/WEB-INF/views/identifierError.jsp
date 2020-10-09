<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">The website has encountered an error</jsp:attribute>
	<jsp:attribute
			name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
	</jsp:attribute>

	<jsp:attribute name="addToFooter">
    </jsp:attribute>

	<jsp:body>

		<div class="container single single--no-side" style="">
			<div class="row">
				<div class="col-12 col-md-2 pseudo-padding"></div>
				<div class="col-12 col-md-8 white-bg">
					<div class="pr-md-25">
						<div class="breadcrumbs">
							<div class="row">
								<div class="col-12">
									<p><a href="/">Home</a> <span>></span>
										Identifier not found.
									</p>
								</div>
							</div>
						</div>
					</div>

					<div class="pre-content pr-md-25">

						<div class="page-content">
							<div class="row">
								<div class="col-8">
									<h2>We're sorry</h2>
									<hr />
									<p class="my-1">You have reached an error page. The identifer could not be found. This could be due to:</p>
									<ul class="my-1">
										<li>A bookmarked URL has moved</li>
										<li>A typo in the URL</li>
										<li>Temporary technical difficulties</li>
									</ul>

									<c:if test="${fn:length(phenotypeSuggestions) > 0 || fn:length(geneSuggestions) > 0}">
										<div class="my-3">
										<h2>Perhaps you were looking for:</h2>
										<hr />
										<ul class="m-0">
											<c:forEach var="i" items="${phenotypeSuggestions}">
												<li>
													<a href="${baseUrl}/search?term=${i}&type=${type}" aria-controls="suggestion" data-dt-idx="0" tabindex="0" class="">${i}</a>

												</li>
											</c:forEach>
											<c:forEach var="i" items="${geneSuggestions}">
												<li>
													<a href="${baseUrl}/search?term=${i}&type=${type}" aria-controls="suggestion" data-dt-idx="0" tabindex="0" class="">${i}</a>

												</li>
											</c:forEach>
										</ul>
										</div>
									</c:if>

								</div>
								<div class="col-4 text-justify align-middle" style="font-size: 8rem; font-weight: 500;"><i class="fal fa-question-circle"></i></div>
							</div>


							<div class="alert alert-warning">
								<h3>Where to go from here...</h3>
								<ul class="my-1">
									<li>You can visit the <a href="${cmsBaseUrl}/">IMPC home page</a></li>
									<li>You can <a href="/">learn more about the IMPC</a></li>
									<li>You can search the data portal:</li>
								</ul>
								<div class="row text-center">
									<div class="col-12">
										<div class="portal-search my-1">
											<div class="portal-search__tabs">
												<a id="geneSearchTab" data-type="gene" class="portalTab portalTabSearchPage left-shadow <c:if test="${type != 'phenotype'}">active</c:if>" href="${baseUrl}/search">Genes</a>
												<a id="phenotypeSearchTab" data-type="pheno" class=" portalTab portalTabSearchPage right-shadow <c:if test="${type == 'phenotype'}">active</c:if>" href="${baseUrl}/search?type=phenotype">Phenotypes</a>
											</div>
											<div class="portal-search__inputs">
												<form id="searchForm" action="${baseUrl}/search">
													<input id="searchTerm" name="term" class="portal-search__input" value="${term}" placeholder="Search the data..." type="text"/>
													<button id="searchIcon" type="submit"><i class="fas fa-search"></i></button>
													<input id="searchType" type="hidden" name="type" value="${type}">
													<div id="searchLoader" class="lds-ring">
														<div></div>
														<div></div>
														<div></div>
														<div></div>
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
			</div>
		</div>
	</jsp:body>
</t:genericpage>


