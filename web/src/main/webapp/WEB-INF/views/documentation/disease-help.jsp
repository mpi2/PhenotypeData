


<h3 id="details"><a name="summarySection2"></a></h3>

<p>The IMPC disease page contains known gene associations and mouse models
    for the disease as well as predicted gene candidates and mouse models based on the
    phenotypic similarity of the disease clinical symptoms and the mouse phenotype
    annotations. The latter uses data from both the MGI curated dataset as well as
    high-throughput phenotype assignments from the IMPC pipeline and will display
    the highest score available for this set.
</p>
<p>
    <img src="img/disease-detail-header.png">
</p>
<h3 id="ortholog"><a name="orthologySection2">Disease model by gene orthology</a></h3>
<p>
    Clicking the row for a disease/gene will expand the row to show the details of the phenotype terms
    involved in the association between the disease and the mouse model. The disease phenotypes are
    those supplied by the disease resource (OMIM/Orphanet/DECIPHER). The orange number next to the genotype
    is the PhenoDigm score (see below) which is a percentage-based score. These are ranked from highest to
    lowest in two groups. The first group will show the manually curated mouse models from MGI, if present.
    The second group will list the purely phenodigm predicted associations.
</p>
<p>
    <img src="img/disease-detail-expanded-cell.png">
</p>

<h3 id="similarity"><a name="similaritySection2">Disease model by phenotypic similarity</a></h3>
<p>
    We use our <a href=http://www.sanger.ac.uk/resources/databases/phenodigm/> PhenoDigm</a>
    algorithm to calculate a percentage similarity score where the best possible mouse
    model match to a disease would score 100%. In this interface, we only display
    high-scoring (> 60%) matches that show a reasonable phenotypic similarity,
    with the exception of the known gene associations where we display results for
    all possible mouse models.
</p>
