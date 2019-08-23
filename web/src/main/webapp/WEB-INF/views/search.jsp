<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
    <jsp:attribute
            name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

    <jsp:attribute name="header">
        <link href="${cmsBaseUrl}/wp-content/themes/impc/css/styles.css?ver=40ab77c511c0b72810d6828792c28c78"
              rel="stylesheet" type="text/css"/>
		<script type="application/ld+json">
            {
                "@context": "http://schema.org",
                "@type": "Dataset",
                "@id": "http://www.mousephenotype.org",
                "name": "Mouse phenotype data of knockout mouse lines for protein-coding genes",
                "description": "The International Mouse Phenotyping Consortium (IMPC) is systematically generating mouse knockouts for every protein-coding gene in the mouse genome (approx. 20,000 genes) and carries out high-throughput phenotyping of each line in order to determine gene function by determining the biological systems affected in the absence of the gene. This dataset contains all the genotype-to-phenotype associations, protocols, parameters and measurements currently generated using this approach.",
                "url": "http://www.mousephenotype.org",
                "keywords": "gene, phenotype, mouse, mammalian, human disease",
                "identifier": "DR${releaseVersion}",
                "creator": {
                    "@type": "Organization",
                    "name": "International Mouse Phenotyping Consortium"
                },
                "provider": {
                    "@type": "Organization",
                    "name": "International Mouse Phenotyping Consortium"
                },
                "version": "${releaseVersion}",
                "dateCreated": "2014",
                "dateModified": "2018",
                "citation": "Dickinson et al. 2016. High-throughput discovery of novel developmental phenotypes. Nature 537, 508–514. PMID: 27626380. doi:10.1038/nature19356",
                "temporalCoverage": "2014..",
                "sameAs": "http://www.mousephenotype.org",
                "distribution": [
                    {
                        "@type": "DataDownload",
                        "name": "MySQL database dump",
                        "fileFormat": "application/octet-stream",
                        "contentURL": "http://ftp.ebi.ac.uk/pub/databases/impc/release-${releaseVersion}/"
                    },
                    {
                        "@type": "DataDownload",
                        "name": "Binary Solr Schemas",
                        "fileFormat": "application/octet-stream",
                        "contentURL": "http://ftp.ebi.ac.uk/pub/databases/impc/release-${releaseVersion}/"
                    }
                ]
            }
        </script>
	</jsp:attribute>

    <jsp:attribute name="addToFooter" />

    <jsp:body>

        <div class="container single single--no-side">
            <div class="row">
                <div class="col-12 col-md-2 pseudo-padding"></div>
                <div class="col-12 col-md-8 white-bg">
                    <div class="pr-md-25">
                        <div class="breadcrumbs">
                            <div class="row">
                                <div class="col-12">
                                    <p><a href="/">Home</a> <span>></span>
                                        Portal Search
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="pre-content pr-md-25">
                        <h2>Search</h2>

                        <div class="page-content pb-5">

                            <c:if test="${numberOfResults != 0}">
                            <p>Number of results: <span id="numResults">${numberOfResults}</span></p>
                            </c:if>
                            <c:if test="${numberOfResults == 0}">
                                <div>
                                    <p>No results found for search term "${term}".</p>
                                    <c:if test="${fn:length(phenotypeSuggestions) > 0 || fn:length(geneSuggestions) > 0}">
                                        <hr />
                                        Perhaps you were searching for:
                                        <ul class="my-2">
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
                                    </c:if>
                                    <p class="my-3">
                                        <a class="btn btn-success" href="${cmsBaseUrl}/?s=${term}">Search static content for the term <b>${term}</b></a>
                                    </p>
                                </div>
                            </c:if>

                            <div id="results">

                                <c:forEach var="gene" items="${genes}">
                                    <div class="search-result">
                                        <a href="${baseUrl}/genes/${gene.mgiAccessionId}"><h4>${gene.markerSymbol}</h4></a>
                                        <div class="row">
                                            <div class="col-12 col-md-6">
                                                <p><b>Name: </b>${gene.markerName}<br>
                                                    <b>Human orthologs: </b>
                                                    <c:forEach var="orth" items="${gene.humanGeneSymbol}" varStatus="loop">${orth}<c:if test="${!loop.last}">, </c:if></c:forEach><br>
                                                    <b>Synonyms: </b>
                                                    <c:forEach var="syn" items="${gene.markerSynonym}" varStatus="loop">
                                                        ${syn}<c:if test="${!loop.last}">,</c:if>
                                                    </c:forEach>
                                                </p>
                                            </div>

                                            <div class="col-12 col-md-6">
                                                <p>
                                                    <b>ES Cell Status: </b>${gene.latestEsCellStatus}<br>
                                                    <b>Mouse Status: </b>${gene.latestMouseStatus}<br>
                                                    <b>Phenotype Status: </b>${gene.latestPhenotypeStatus}</p>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>

                                <c:forEach var="phenotype" items="${phenotypes}">
                                    <div class="search-result">
                                        <a href="${baseUrl}/phenotypes/${phenotype.mpId}">
                                            <h4>${phenotype.mpTerm}</h4></a>
                                            <c:if test="${phenotype.geneCount == 0}">
                                            <div class="alert alert-light" role="alert">
                                                No IMPC genes are currently associated with this phenotype
                                            </div>
                                            </c:if>
                                            <c:if test="${phenotype.geneCount != 0}">
                                            <div class="alert alert-primary" role="alert">
                                                ${phenotype.geneCount} gene<c:if test="${phenotype.geneCount != 1}">s</c:if> associated with this phenotype
                                            </div>
                                            </c:if>
                                        <div class="row">
                                            <div class="col-12 col-md-6"><b>Synonym: </b>
                                                <c:forEach var="syn" items="${phenotype.mpTermSynonym}" varStatus="loop">
                                                    ${syn}<c:if test="${!loop.last}">,</c:if>
                                                </c:forEach><p></p></div>
                                            <div class="col-12 col-md-6">
                                                <b>Definition: </b>${phenotype.mpDefinition}
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${currentPage == 1}">
                                <c:set var="prevDisplayPage" value="1" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="prevDisplayPage" value="${currentPage-1}" />
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${currentPage == numPages}">
                                <c:set var="nextDisplayPage" value="${numPages}" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="nextDisplayPage" value="${currentPage+1}" />
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${numberOfResults > rows}">
                        <div class="row justify-content-between">
                            <div class="col-md-auto my-2">
                                <p class="">Showing ${start+1} to ${((start + rows) < numberOfResults) ? (start + rows) : numberOfResults} of ${numberOfResults} entries</p>
                            </div>
                            <div class="col-md-auto">
                                <ul class="pagination my-0">
                                    <c:if test="${currentPage != 1}">
                                        <li class="paginate_button page-item previous" id="previous">
                                            <a href="${baseUrl}/search?term=${term}&type=${type}&page=${prevDisplayPage}&rows=${rows}" class="page-link">Previous</a></li>
                                    </c:if>
                                    <c:if test="${currentPage == 1}">
                                        <li class="paginate_button page-item previous disabled" id="previous">
                                            <a class="page-link disabled">Previous</a></li>
                                    </c:if>

                                    <li class="paginate_button page-item <c:if test="${currentPage == 1}">active</c:if>">
                                        <a href="${baseUrl}/search?term=${term}&type=${type}&page=1&rows=${rows}" class="page-link">1</a></li>

                                    <c:if test="${currentPage > 3 }">
                                        <li class="paginate_button page-item disabled" id="cardio_ellipsis">
                                            <a href="#" data-dt-idx="2" tabindex="0" class="page-link">...</a></li>
                                    </c:if>

                                    <c:forEach var="i" begin="${prevDisplayPage}" end="${nextDisplayPage}">
                                        <c:if test="${i != numPages && i!=1}">
                                        <li class="paginate_button page-item <c:if test="${currentPage == i}">active</c:if>" id="navigate-${i}">
                                            <a href="${baseUrl}/search?term=${term}&type=${type}&page=${i}&rows=${rows}" class="page-link">${i}</a></li>
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${currentPage < (numPages-2) && numPages>5 }">
                                        <li class="paginate_button page-item disabled" id="next_ellipsis">
                                            <a href="#" class="page-link">...</a></li>
                                    </c:if>

                                    <li class="paginate_button page-item <c:if test="${currentPage == numPages}">active</c:if>">
                                        <a href="${baseUrl}/search?term=${term}&type=${type}&page=${numPages}&rows=${rows}" class="page-link">${numPages}</a></li>
                                    <c:if test="${currentPage != numPages}">
                                        <li class="paginate_button page-item next" id="next">
                                            <a href="${baseUrl}/search?term=${term}&type=${type}&page=${nextDisplayPage}&rows=${rows}" class="page-link">Next</a></li>
                                    </c:if>
                                    <c:if test="${currentPage == numPages}">
                                        <li class="paginate_button page-item next disabled" id="next">
                                            <a class="page-link disabled">Next</a></li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                        </c:if>

                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>




