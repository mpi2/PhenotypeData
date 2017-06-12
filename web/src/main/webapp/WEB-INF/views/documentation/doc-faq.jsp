<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-faq">faq</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
        <link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />
        <style>


          div#faq {
            clear: both;
            margin-top: 50px;
            width: 100%;
          }


        </style>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-info fa-4x"></i><div class="fleft">FAQ</div></div>
    <div id="faq">
      <ul class="subUl">
        <li>How to cite the IMPC resource / data<br>
          To reference the IMPC, please cite:
          1: Koscielny G, et al. The International Mouse Phenotyping Consortium Web Portal, a unified point of access for knockout mice and related phenotyping data. Nucleic Acids Res. 2014 Jan. PubMed PMID: 24194600; PubMed Central PMCID: PMC3964955
          In your work you should include the IMPC data release (e.g. Release : 3.1) you extracted data from, as this allows your future readers to find the data you used. The latest release can be found on the bottom right of every page in the footer.
        </li>
        <li><a href="${baseUrl}/documentation/doc-explore#phenoAssocSection0">Find gene to phenotype associations, allele maps and more</a></li>
        <li><a href="${baseUrl}/documentation/doc-explore#1">Find a list of genes associated with a phenotype, assays used to measure the phenotype and more</a></li>
        <li><a href="${baseUrl}/documentation/doc-explore#2">Find genes associated with rare diseases</a></li>
        <li><a href="${baseUrl}/documentation/doc-explore#4">Retrieve LacZ and phenotype images</a></li>
        <li><a href="${baseUrl}/documentation/graph-help.html">Learn how we visualize phenotype data</a></li>
        <li><a href="${baseUrl}/documentation/statistics-help.html">Learn about the statistical tests used to determine gene to phenotype associations</a></li>

      </ul>


    </div>

  </jsp:body>

</t:genericpage>