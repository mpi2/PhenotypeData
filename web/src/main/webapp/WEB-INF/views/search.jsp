<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/${dataType}?kw=*">${dataTypeLabel}</a> &raquo; ${searchQuery}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<%-- <link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" /> --%>
		<link href="https://www.mousephenotypetest.org/wp-content/themes/impc/css/styles.css?ver=40ab77c511c0b72810d6828792c28c78" rel="stylesheet" type="text/css" />
		<script type="application/ld+json">
			{
			  "@context": "http://schema.org",
			  "@type": "Dataset",
			  "@id": "http://www.mousephenotype.org",
			  "name": "Mouse phenotype data of knockout mouse lines for protein-coding genes",
			  "description": "The International Mouse Phenotyping Consortium (IMPC) is systematically generating mouse knockouts for every protein-coding gene in the mouse genome (approx. 20,000 genes) and carries out high-throughput phenotyping of each line in order to determine gene function by determining the biological systems affected in the absence of the gene. This dataset contains all the genotype-to-phenotype associations, protocols, parameters and measurements currently generated using this approach.",
			  "url": "http://www.mousephenotype.org",
			  "keywords": "gene, phenotype, mouse, mammalian, human disease",
			  "identifier": "DR8.0",
			  "creator": {
			    "@type": "Organization",
			    "name": "International Mouse Phenotyping Consortium"
			  },
			  "provider": {
			    "@type": "Organization",
			    "name": "International Mouse Phenotyping Consortium"
			  },
			  "version": "8.0",
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
			      "contentURL": "http://ftp.ebi.ac.uk/pub/databases/impc/release-8.0/"
			    },
			    {
			      "@type": "DataDownload",
			      "name": "Binary Solr Schemas",
			      "fileFormat": "application/octet-stream",
			      "contentURL": "http://ftp.ebi.ac.uk/pub/databases/impc/release-8.0/"
			    }
			  ]
			}
		</script>
	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
		<div class="region region-pinned"></div>
	</jsp:attribute>


	<jsp:body>
	
	<div class="container single single--no-side">
  <div class="row">
    <div class="col-12 col-md-2 pseudo-padding"></div>
    <div class="col-12 col-md-8 white-bg">
      <div class="pr-md-25">
        <div class="breadcrumbs">
          <div class="row">
            <div class="col-12">
              <p><a href="/">Home</a> <span>&gt;</span>
                Portal Search
              </p>
            </div>
          </div>
        </div>

      </div>
      <div class="pre-content">
        <h2>Search
        </h2>

        <div class="page-content pb-5">
          <p>Number of results: <span id="numResults">${numberOfResults}</span></p>
          <div id="results">
          	
          	<c:forEach var="gene" items="${genes}">
          	<div class="search-result">
          		<a href="${drupalBaseUrl}/genes/${gene.mgiAccessionId}"><h4>${gene.markerSymbol}</h4></a>
          		<div class="row">
          			<div class="col-12 col-md-6">
          				<p><b>Name: </b>${gene.markerName}<br>
          				<b>Human orthologs:  </b><c:forEach var="orth" items="${gene.humanGeneSymbol}">${orth}</c:forEach><br>
          				<b>Synonyms: </b>
          				<c:forEach var="syn" items="${gene.markerSynonym}">${syn} <m03jus></c:forEach>
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
          			<a href="${drupalBaseUrl}/phenotypes/${phenotype.accession}"><h4>${phenotype.mpTerm}</h4></a>
          			<div class="row"><div class="col-12 col-md-6"><b>Synonym: </b>
          			<c:forEach var="synonym" items="${phenotype.mpTermSynonym}">${synonym}</c:forEach><p></p></div>
          			<div class="col-12 col-md-6">
          				<b>Definition: </b>${phenotype.mpDefinition}<p></p>
          			</div>
          			</div>
          			</div>
          	</c:forEach>
          	
          </div>
         </div>

          <div class="row pagination">
            <div class="col-md-3 col-12">
            </div>
            <div class="col-md-9 col-12 text-left text-md-center">
             <div id="pagination">
             <c:forEach var="i" begin="0" end="${pages-1}">
             <a data-start="${i*rows }" class="page-numbers page-num-search <c:if test='${start eq i*rows }'>current</c:if>" href="${baseUrl}/search?term=${param.term}&type=${param.type}&start=${i*rows}&rows=${rows}">${i+1}</a>
             <!-- <a data-start="10" class="page-numbers page-num-search false href=" #"="">2</a> -->
             </c:forEach>
             </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</jsp:body>
</t:genericpage>


