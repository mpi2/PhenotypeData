<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

  <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/documentation/doc-overview">Documentation</a> &raquo; <a href="${baseUrl}/documentation/doc-method">Related resources doc</a></jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
		<link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />

	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <h1>IMPC data portal documentation</h1>
    <div><i class="fleft fa fa-sitemap fa-4x"></i><div class="fleft">Related Resources</div></div>

    <p class="sectBr"></p>
    <p class="sectBr"></p>

    <h3>Ontologies</h3>
      <table>
        <thead>
          <th>Name</th>
          <th>Descriptioin</th>
          <th>Example</th>
        </thead>
        <tr>
          <td><a href="http://www.ebi.ac.uk/ols/beta/ontologies/mp">MP (Mammalian Phenotype Ontology)</a></td>
          <td><a href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC2801442/">Standard terms for annotating mammalian phenotypic data</a></td>
          <td><a href="http://www.informatics.jax.org/searches/MP_form.shtml">MGI MP browser</a></td>
        </tr>
        <tr>
          <td><a href="http://www.ebi.ac.uk/ols/beta/ontologies/ma">MA (Adult Mouse Anatomy Ontology)</a></td>
          <td>Controlled vocabulary of the adult anatomy of the mouse</td>
          <td><a href="http://www.informatics.jax.org/searches/AMA_form.shtml">MGI MA browser</a></td>
        </tr>
        <tr>
          <td><a href="http://www.ebi.ac.uk/ols/beta/ontologies/emap">EMAP (Mouse gross anatomy and development, timed)</a></td>
          <td>Structured controlled vocabulary of stage-specific anatomical structures of the mouse</td>
          <td><a href="http://www.emouseatlas.org/emap/">e-Mouse Atlas Project</a></td>
        </tr>
        <tr>
          <td><a href="http://www.ebi.ac.uk/ols/beta/ontologies/mpath">MPATH (Mouse pathology phenotypes Ontology)</a></td>
          <td><a href="http://www.ncbi.nlm.nih.gov/pubmed/24033988">Structured controlled vocabulary of mutant and transgenic mouse pathology phenotypes</a></td>
          <td><a href="http://www.pathbase.net/">Pathbase project</a></td>
        </tr>

      </table>
    </li>
    <h3>Disease Resources</h3>
    <table class="twoCols">
      <thead>
        <th>Source</th>
        <th>Descriptioin</th>
      </thead>
      <tr>
        <td><a href="www.omim.org/">OMIM</a> (Online Mendelian Inheritance in Man)</td>
        <td>An Online Catalog of Human Genes and Genetic Disorders</td>
      </tr>
      <tr>
        <td><a href="www.orpha.net/">Orphanet</a></td>
        <td>The portal for rare diseases and orphan drugs</td>
      </tr>
      <tr>
        <td><a href="decipher.sanger.ac.uk/">DECIPHER</a> (DatabasE of genomiC varIation and Phenotype in Humans using Ensembl Resource)</td>
        <td>Interactive web-based database which incorporates a suite of tools designed to aid the interpretation of genomic variants</td>
      </tr>
      <tr>
        <td><a href="https://www.ebi.ac.uk/gwas/">GWAS CATALOG</a></td>
        <td>The MHGRI-EBI catalog of published genome-wide association studies</td>
      </tr>
    </table>

    <h3>Other Resources</h3>
    <table class="twoCols">
      <thead>
        <th>Source</th>
        <th>Descriptioin</th>
      </thead>
      <tr>
        <td><a href="http://www.informatics.jax.org/">MGI</a> (Mouse Genome Informatics)</td>
        <td>An international database resource for the laboratory mouse</td>
      </tr>
      <tr>
        <td><a href="https://www.mousephenotype.org/impress-help-documentation">IMPReSS</a> (Mouse Genome Informatics)</td>
        <td>International Mouse Phenotyping Resource of Standardised Screens</td>
      </tr>
    </table>

  </jsp:body>

</t:genericpage>