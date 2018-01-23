<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-faq">faq</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
        <link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />
        <link href="https://www.ebi.ac.uk/web_guidelines/EBI-Icon-fonts/v1.2/fonts.css" rel="stylesheet" type="text/css" />
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
        <li>How to cite the IMPC resource / data?<br>
          <p>To reference the IMPC, please cite:</p><br>
          <b><a href="https://www.nature.com/articles/nature19356">High-throughput discovery of novel developmental phenotypes.</a></b>
          <p>Dickinson ME, Flenniken AM, Ji X, Teboul L, Wong MD, White JK, Meehan TF, Weninger WJ, Westerberg H, Adissu H, Baker CN, Bower L, Brown JM, Caddle LB, Chiani F, Clary D, Cleak J, Daly MJ, Denegre JM, Doe B, Dolan ME, Edie SM, Fuchs H, Gailus-Durner V, Galli A, Gambadoro A, Gallegos J, Guo S, Horner NR, Hsu CW, Johnson SJ, Kalaga S, Keith LC, Lanoue L, Lawson TN, Lek M, Mark M, Marschall S, Mason J, McElwee ML, Newbigging S, Nutter LM, Peterson KA, Ramirez-Solis R, Rowland DJ, Ryder E, Samocha KE, Seavitt JR, Selloum M, Szoke-Kovacs Z, Tamura M, Trainor AG, Tudose I, Wakana S, Warren J, Wendling O, West DB, Wong L, Yoshiki A, International Mouse Phenotyping Consortium, Jackson Laboratory, Infrastructure Nationale PHENOMIN, Institut Clinique de la Souris (ICS), Charles River Laboratories, MRC Harwell, Toronto Centre for Phenogenomics, Wellcome Trust Sanger Institute, RIKEN BioResource Center, MacArthur DG, Tocchini-Valentini GP, Gao X, Flicek P, Bradley A, Skarnes WC, Justice MJ, Parkinson HE, Moore M, Wells S, Braun RE, Svenson KL, de Angelis MH, Herault Y, Mohun T, Mallon AM, Henkelman RM, Brown SD, Adams DJ, Lloyd KC, McKerlie C, Beaudet AL, Bućan M, Murray SA.</p>
          <p>Nature 537, 508–514 (22 September 2016)</p>
          <p>
            PMID: 27626380
          </p>
          <p>doi:10.1038/nature19356</p>
        </li>
        <li>What is the current version of IMPC release?<br>
          <p>The current version of release can be found on the bottom right of every page in the footer.</p>
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