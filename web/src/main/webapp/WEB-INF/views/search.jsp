<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
    <jsp:attribute
            name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

    <jsp:attribute name="header">

        <meta name="_csrf" content="${_csrf.token}"/>
        <meta name="_csrf_header" content="${_csrf.headerName}"/>

        <link href="${cmsBaseUrl}/wp-content/themes/impc/css/styles.css?version=20200213" rel="stylesheet" type="text/css"/>

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
        <script>
            $(document).ready(function () {

                // Enable CSRF processing for forms on this page
            function loadCsRf() {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                $(document).ajaxSend(function(e, xhr, options) {
                    xhr.setRequestHeader(header, token);
                });
            }
            loadCsRf();

            // Wire up the AJAX callbacks to the approprate forms
            $('form.follow-form').submit(function(event) {

                // Prevent the form from submitting when JS is enabled
                event.preventDefault();

                // Get which gene this is a form for
                var $form = $(this),
                    acc = $form.find('input[name="geneAccessionId"]').val();

                // Do asynch request to change the state of the follow flag for this gene
                // and update button appropriately on success
                $.ajax({
                    type: "POST",
                    url: "${baseUrl}/update-gene-registration",
                    headers: {'asynch': 'true'},
                    data: $(this).serialize(),
                    success: function(data) {

                        // Data is a map of gene accession id -> status
                        // Status is either "Following" or "Not Following"
                        var acc = Object.keys(data)[0];
                        switch(data[acc]) {
                            case "Following":
                                $('form#follow-form-'+acc.replace(":", "")).find("button")
                                    .attr('title', 'You are following ${gene.markerSymbol}. Click to stop following.')
                                    .removeClass('btn-primary')
                                    .addClass('btn-outline-secondary');

                                $('form#follow-form-'+acc.replace(":", "")).find("span")
                                    .text('Unfollow');


                                $('form#follow-form-'+acc.replace(":", "")).find('i')
                                    .removeClass('fa-user-plus')
                                    .addClass('fa-user-minus');
                                break;

                            case "Not Following":
                                $('form#follow-form-'+acc.replace(":", "")).find("button")
                                    .attr('title', 'Click to follow ${gene.markerSymbol}.')
                                    .addClass('btn-primary')
                                    .removeClass('btn-outline-secondary')

                                $('form#follow-form-'+acc.replace(":", "")).find("span")
                                    .text('Follow');

                                $('form#follow-form-'+acc.replace(":", "")).find('i')
                                    .removeClass('fa-user-minus')
                                    .addClass('fa-user-plus');
                                break;
                            default:
                                console.log("Cannot find response type for response: " + acc);
                                break;
                        }
                    },
                    error: function() {
                        window.location("${baseUrl}/rilogin?target=${baseUrl}/search?term=${term}&type=${type}&page=${currentPage}");
                    }
                });
            });
        });

        </script>

        <style>
            .container-small {
                max-width: 1000px;
            }
            .page-content .search-result p{
                font-size: 16px;
            }
            .alert-light {
                color: #000;
                background-color: #f7f7f7;
                border-color: #f7f7f7;
            }
        </style>

	</jsp:attribute>

    <jsp:attribute name="addToFooter" />

    <jsp:body>





        <div class="container container-small data-heading">
            <div class="row row-shadow">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">
                        <c:choose>
                            <c:when test = "${fn:startsWith(type, 'pheno')}">
                                Phenotype search results<%--c:if test="${fn:length(term) > 0}">: <span class="gene-name">"${term}"</span></c:if--%>
                            </c:when>
                            <c:otherwise>
                                Gene Search Results<%--c:if test="${fn:length(term) > 0}">: <span class="gene-name">"${term}"</span></c:if--%>
                            </c:otherwise>
                        </c:choose>
                    </h2>
                </div>
            </div>
        </div>
        <div class="container container-small white-bg-small">
            <div class="breadcrumbs clear row">
                <div class="col-12 d-none d-lg-block px-5 pt-5">
                    <aside>
                        <a href="https://www.mousephenotype.org/phenodcc/">Home</a>
                        <span class="fal fa-angle-right"></span>
                        <strong>Search<c:if test="${fn:length(term) > 0}">: "${term}"</c:if></strong>
                    </aside>
                </div>
                <div class="col-12 d-block d-lg-none px-3 px-md-5 pt-5">
                    <aside>
                        <a href="${cmsBaseUrl}"><span class="fal fa-angle-left mr-2"></span> Home</a>
                    </aside>
                </div>
            </div>
            <div class="row">
                <div class="col-12 col-md-12">
                <div class="pre-content clear-bg">
                    <div class="page-content px-0 px-md-5 pb-5 white-bg">

                        <c:if test="${numberOfResults == 0}">
                            <div>
                                <p>No results found for ${type} "${term}".</p>
                                <c:if test="${fn:length(phenotypeSuggestions) > 0 || fn:length(geneSuggestions) > 0}">
                                    <hr />
                                    Perhaps you were searching for:
                                    <ul class="my-2">
                                        <c:forEach var="i" items="${phenotypeSuggestions}">
                                            <li>
                                                <a href="${baseUrl}/search?term=${i}&type=phenotype" aria-controls="suggestion" data-dt-idx="0" tabindex="0" class="">${i}</a>
                                            </li>
                                        </c:forEach>
                                        <c:forEach var="i" items="${geneSuggestions}">
                                            <li>
                                                <a href="${baseUrl}/search?term=${i}&type=gene" aria-controls="suggestion" data-dt-idx="0" tabindex="0" class="">${i}</a>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:if>
                                <p>This search only looked in our ${type} database.</p>
                                <p class="my-3">
                                    <a class="btn btn-success" href="${cmsBaseUrl}/?s=${term}">Search documentation, news, and blog posts for <b>${term}</b></a>
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${numberOfResults != 0}">
                            <p class="py-0 my-0"><small>Showing ${start+1} to ${((start + rows) < numberOfResults) ? (start + rows) : numberOfResults} of ${numberOfResults} entries</small></p>
                        </c:if>

                        <div id="results">

                            <c:forEach var="gene" items="${genes}">

                            <div class="search-result">
                                <div class="row">
                                    <div class="col-12 col-md-6">
                                        <h3 class="mb-4"><a href="${baseUrl}/genes/${gene.mgiAccessionId}">${gene.markerSymbol}</a></h3>
                                        <dl class="row mb-3">
                                            <dt class="col-sm-3">Name:</dt>
                                            <dd class="col-sm-9">${gene.markerName}</dd>
                                            <dt class="col-sm-3">Synonyms:</dt>
                                            <dd class="col-sm-9"><c:forEach var="syn" items="${gene.markerSynonym}" varStatus="loop">${syn}<c:if test="${!loop.last}">, </c:if></c:forEach></dd>
                                        </dl>

                                        <c:if test="${fn:contains(fn:toLowerCase(gene.esCellProductionStatus), 'abort')}">
                                        <p class="text-danger">
                                            <i class="fas fa-ban"></i>
                                            <b>Phenotype Production Aborted</b>
                                        </p>
                                        </c:if>

                                        <div>
                                            <a href="${baseUrl}/genes/${gene.mgiAccessionId}#phenotypesTab" class="btn btn-primary"><i class="far fa-chart-bar fa-sm"></i> View Data</a>
                                            <a href="${baseUrl}/genes/${gene.mgiAccessionId}#order" class="btn btn-info text-white"><i class="fas fa-shopping-cart fa-sm"></i> Order Mice</a>

                                            <form action="${baseUrl}/update-gene-registration"
                                                  method="POST"
                                                  class="follow-form d-inline"
                                                  id="follow-form-${fn:replace(gene.mgiAccessionId, ":", "")}">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                                <input type="hidden" name="geneAccessionId" value="${gene.mgiAccessionId}" />
                                                <input type="hidden" name="target" value="${baseUrl}/rilogin?target=${baseUrl}/search?term=${term}&type=${type}&page=${currentPage}" />

                                                <c:choose>
                                                    <c:when test="${not empty isLoggedIn and isLoggedIn}">
                                                        <c:choose>
                                                            <c:when test="${isFollowing[gene.mgiAccessionId]}">
                                                                <button
                                                                        title="You are following ${gene.markerSymbol}. Click to stop following."
                                                                        class="btn btn-outline-secondary"
                                                                        type="submit">
                                                                    <i class="fas fa-user-minus"></i>
                                                                    <span>Unfollow</span>
                                                                </button>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <button
                                                                        title="Click to follow ${gene.markerSymbol}"
                                                                        class="btn btn-primary"
                                                                        type="submit">
                                                                    <i class="fas fa-user-plus"></i>
                                                                    <span>Follow</span>
                                                                </button>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a
                                                                href="${baseUrl}/rilogin?target=${baseUrl}/search?term=${term}&type=${type}&page=${currentPage}"
                                                                title="Log in to My genes"
                                                                class="btn btn-dark">
                                                            Log in to follow
                                                        </a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </div>
                                    </div>



                                    <div class="col-12 col-md-6">
                                        <div class="alert alert-light p-4">
                                            <h6>
                                                <b>Production Status</b>
                                                <i class="fas fa-question-circle float-right" data-toggle="tooltip" data-placement="bottom" title="These statuses are a way to indicate how &quot;close&quot; we are from having phenotype data available"></i>
                                            </h6>
                                            <c:if test="${fn:trim(gene.esCellProductionStatus) != '' and !fn:contains(fn:toLowerCase(gene.esCellProductionStatus), 'not assigned')}"><p class="mb-0">${gene.esCellProductionStatus}</p></c:if>
                                            <c:if test="${fn:trim(gene.mouseProductionStatus) != ''}"><p class="mb-0">${gene.mouseProductionStatus}</p></c:if>
                                            <c:if test="${fn:trim(gene.phenotypeStatus) != ''}"><p class="mb-0">${gene.phenotypeStatus}</p></c:if>
                                            <c:if test="${ fn:contains(fn:toLowerCase(gene.esCellProductionStatus), 'not assigned') and fn:length(gene.esCellProductionStatus)==0 and fn:length(gene.esCellProductionStatus)==0}">
                                                <p>
                                                    <i class="fas fa-exclamation-circle"></i>
                                                    <b>${gene.esCellProductionStatus}</b>
                                                </p>
                                            </c:if>
                                            <c:if test="${ fn:length(gene.phenotypeStatus) == 0 }">
                                                <p>
                                                    <i class="fas fa-exclamation-circle"></i>
                                                    <c:if test="${ fn:length(gene.esCellProductionStatus) == 0 and fn:length(gene.mouseProductionStatus) == 0 }"><b>Production and phenotyping are currently not planned for a knockout strain of this gene.</b></c:if>
                                                    <c:if test="${ fn:length(gene.esCellProductionStatus) > 0 or fn:length(gene.mouseProductionStatus) > 0 }"><b>Phenotyping is currently not planned for a knockout strain of this gene.</b></c:if>
                                                </p>
                                            </c:if>
                                        </div>
                                    </div>


                                </div>
                            </div>

                            </c:forEach>

                            <c:forEach var="phenotype" items="${phenotypes}">
                                <div class="search-result">
                                    <h3><a href="${baseUrl}/phenotypes/${phenotype.mpId}">${phenotype.mpTerm}</a></h3>
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
                                                ${syn}<c:if test="${!loop.last}">, </c:if>
                                            </c:forEach><p></p></div>
                                        <div class="col-12 col-md-6">
                                            <b>Definition: </b>${phenotype.mpDefinition}
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>

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
                                <div class="row">
                                    <div class="col mt-3">
                                        <ul class="pagination my-0 float-right">
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
                                <p class="py-0 my-0 float-right"><small>Showing ${start+1} to ${((start + rows) < numberOfResults) ? (start + rows) : numberOfResults} of ${numberOfResults} entries</small></p>

                            </c:if>

                        </div>
                    </div>


                </div>


            </div>
            </div>

        </div>




    </jsp:body>

</t:genericpage>




