
<h3>More information about the way IMPC uses disease data.</h3>
<p class="sectBr"></p>

<h4>Explore Disease Data</h4>

<p>The ultimate goal of studying model organisms is to translate what is learned into useful knowledge about normal human biology and disease.
<p>The IMPC disease details page contains known gene associations (via orthology to human disease genes) and known mouse models from the literature (from MGI) for the disease as well as predicted gene candidates and mouse models based on the phenotypic similarity of the disease clinical symptoms and the mouse phenotype annotations.
    The phenotypic similarity is calculated using the PhenoDigm algorithm (Phenotype comparisons for DIsease Genes and Models) developed by the Monarch Initiative which will allow integration of data from model organisms to identify data-supported gene candidates for human genetic diseases (Link to Methods). Mouse Genotype-Phenotype and Human disease resources are described below.

<h4>Disease details pages</h4>
<p>Results are broken down in 2 parts, depending on the association methodology (by gene orthology or by phenotypic similarity).

 <p>Clicking the row for a disease/gene will expand the row to show the details of the phenotype terms involved in the association between the disease and the mouse model. The orange number next to the genotype is the PhenoDigm score (see below) which is a percentage-based score . These are ranked from highest to lowest in two groups. The first group will show the manually curated mouse models from MGI. The second group will list the purely phenodigm predicted associations.

<h6><a name="orthologySection2">1: By Gene Ortholgy</a></h6>
<img src="img/disease-models-by-orthology.png" />
    Human genes/regions causing human disease were extracted from human disease resources described above. Matching mouse orthologues were identified from HomoloGene and associated mouse models were retrieved from IMPC and MGI resources. Phenotypes corresponding to mouse models were then compared to human phenotypes gene matched human disease using PhenoDIgm. Depending on the similarity between the phenotypes, a PhenoDigm score is calculated based on the number and specificity of the matches between the human and mouse phenotypes where 100% represents a perfect match and 0% no match.
<p class="intraSectBr"></p>

<h6><a name="similaritySection2">2: By Phenotypic Similarity</a></h6>
<img src="img/disease-models-by-similarity.png" />
    We compared Mouse and Human Phenotypes using Phenodigm to provide evidence about gene-disease associations. Mouse phenotypes were extracted from mouse models from the IMPC and MGI repositories.  Mouse phenotypes were then compared with human disease/phenotypes extracted from human disease resources (OMIM, ORPHANET, DECIPHER) using PhenoDigm as above. In this interface, we only display high-scoring (> 60%) matches that show a reasonable phenotypic similarity between the two species.


<p class="intraSectBr"></p>

<h4>Human disease resources</h4>
<table class="twoCols">
    <thead>
        <th>Source</th>
        <th>Description</th>
    </thead>
    <tr>
        <td><a href="http://www.omim.org/">OMIM</a> (Online Mendelian Inheritance in Man)</td>
        <td>An Online Catalog of Human Genes and Genetic Disorders</td>
    </tr>
    <tr>
        <td><a href="http://www.orpha.net/">Orphanet</a></td>
        <td>The portal for rare diseases and orphan drugs</td>
    </tr>
    <tr>
        <td><a href="http://decipher.sanger.ac.uk/">DECIPHER</a> (DatabasE of genomiC varIation and Phenotype in Humans using Ensembl Resource)</td>
        <td>Interactive web-based database which incorporates a suite of tools designed to aid the interpretation of genomic variants</td>
    </tr>
</table>

<p class="intraSectBr"></p>

<h4>Mouse Genotype-Phenotype resources</h4>
<table class="twoCols">
    <thead>
    <th>Source</th>
    <th>Description</th>
    </thead>
    <tr>
        <td><a href="http://mousephenotype.org/">IMPC</a> (International Mouse Phenotyping Consortium)</td>
        <td>Functional catalogue of mouse mammalian genome</td>
    </tr>
    <tr>
        <td><a href="http://www.informatics.jax.org">MGI</a> (Mouse Genome Informatics)</td>
        <td>International database resource for the laboratory mouse</td>
    </tr>
</table>



<%--<h3 id="details"><a name="summarySection2"></a></h3>--%>

<%--<p>The IMPC disease page contains known gene associations and mouse models--%>
    <%--for the disease as well as predicted gene candidates and mouse models based on the--%>
    <%--phenotypic similarity of the disease clinical symptoms and the mouse phenotype--%>
    <%--annotations. The latter uses data from both the MGI curated dataset as well as--%>
    <%--high-throughput phenotype assignments from the IMPC pipeline and will display--%>
    <%--the highest score available for this set.--%>
<%--</p>--%>
<%--<p>--%>
    <%--<img src="img/disease-detail-header.png">--%>
<%--</p>--%>
<%--<h3 id="ortholog"><a name="orthologySection2">Disease model by gene orthology</a></h3>--%>
<%--<p>--%>
    <%--Clicking the row for a disease/gene will expand the row to show the details of the phenotype terms--%>
    <%--involved in the association between the disease and the mouse model. The disease phenotypes are--%>
    <%--those supplied by the disease resource (OMIM/Orphanet/DECIPHER). The orange number next to the genotype--%>
    <%--is the PhenoDigm score (see below) which is a percentage-based score. These are ranked from highest to--%>
    <%--lowest in two groups. The first group will show the manually curated mouse models from MGI, if present.--%>
    <%--The second group will list the purely phenodigm predicted associations.--%>
<%--</p>--%>
<%--<p>--%>
    <%--<img src="img/disease-detail-expanded-cell.png">--%>
<%--</p>--%>

<%--<h3 id="similarity"><a name="similaritySection2">Disease model by phenotypic similarity</a></h3>--%>
<%--<p>--%>
    <%--We use our <a href=http://www.sanger.ac.uk/resources/databases/phenodigm/> PhenoDigm</a>--%>
    <%--algorithm to calculate a percentage similarity score where the best possible mouse--%>
    <%--model match to a disease would score 100%. In this interface, we only display--%>
    <%--high-scoring (> 60%) matches that show a reasonable phenotypic similarity,--%>
    <%--with the exception of the known gene associations where we display results for--%>
    <%--all possible mouse models.--%>
<%--</p>--%>
