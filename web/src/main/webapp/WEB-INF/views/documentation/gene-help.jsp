<link href="${baseUrl}/css/impc-doc.css" rel="stylesheet" type="text/css" />

The Gene page contain several subsections which will be described here.
<ul  class="subUl">
    <li>Gene summary</li>
    <li>Phenotype Associations</li>
    <li>Phenotype Heatmap</li>
    <li>Expression</li>
    <li>Associated Images</li>
    <li>Disease Models</li>
    <li>Order Mouse and ES Cells</li>
</ul>

<h3 id="details"><a name="summarySection0">Gene Summary</a></h3>
The first section of the page shows summary information about the gene.
<img src="img/gene-details.png" />
The information displayed includes:
<ul class="docLi">
    <li><strong>Name</strong></li>
    <li><strong>Synonyms</strong></li>
    <li><strong>MGI Id</strong>
        Links to the corresponding gene detail page at <a href="http://www.informatics.jax.org">Mouse Genome Informatics</a>
    </li>
    <li><strong>Status</strong>
            The latest IMPC production status for this gene.  This aggregates the ES and mouse production statuses as well
            as phenotyping statuses of all ongoing projects for this gene in <a href="https://www.mousephenotype.org/imits/">iMits</a>
            and displays the status of the project that is closest to producing a mouse.
    </li>
    <li><strong>Links</strong>
        Links to different views of the gene in the Ensembl genome browser
        <ul class="subUl">
            <li><strong>Ensembl Gene</strong>
                Links to the browser centered on the gene
            </li>
            <li><strong>Ensembl Compara</strong>
                Links to a view of the cross-species resources and analyses, at both the sequence level and the gene level provided by Ensembl
            </li>
        </ul>
    </li>
    <li><strong>Other Links</strong>
        <ul class="subUl">
            <li><strong>IMPC Gene Browser</strong> Interactive graphical gene browser. Clicking
                the link shows a genome browser displaying a graphical view of the gene's location
                and surrounding features. The browser is interactive and you can use your mouse to zoom and scroll.
                <br/>
                <img src="img/gene-browser.png" />
            </li>
            <li><strong>ENU</strong> Links to the ENU mutant library at the Australian Phenomics Facility</li>
        </ul>
    </li>
    <li><strong>Viability</strong> Shows lethality in homo-/hemi-/heterogyzosity background</li>
</ul>
<p class="sectBr">


<h3 id="phenotype-associations"><a name="phenoAssocSection0">Phenotype associations</a></h3>

<p>This section shows the association of genes to <a href="http://www.informatics.jax.org/searches/MP_form.shtml">Mammalian phenotype</a> terms.</p>
<p>It contains 4 ways of data viewing:</p>
(1) See all adult phenotypes<br>
(2) See all embryo images, when available<br>
(3) A quick glance of how phenotypes were discovered by icons<br>
(4) Table view of significant phenotypes<br>
</br>
<img src="img/gene-phenoAssoc.png" />

<p class="intraSectBr">

<h4>(1) See all adult phenotypes:</h4>
Press the "All Adult Data" button.<br />
<p>You will be taken to a separate page for details of the statistical analysis. Significant P values appear to the right of the green dashed vertical line; insignificant values to the left.
    Hovering over any point shows some important experiment details. Clicking on any point shows a graph of the experiment data.
</p><img src="img/gene-all-phenotype-data.png" />

<p class="intraSectBr">

<h4>(2) See all embryo images:</h4>Press the "3D Imaging" button.<br>
<p>You will be taken to a new page with an interactive graphical interface with which you could view the embryos</p>
<br/><img src="img/embryo-viewer.png" /><br><br>

<h4>(3) A quick glance of how phenotypes were discovered by icons:</h4>
The icons on the right hand side show a visual summary of the same data. The colors indicate how significant phenotypes
were discovered.</h4>

<p class="intraSectBr">

<h4>(4) Table view of significant phenotypes</h4>
This phenotype table lists the individual phenotypes associated to this gene through a specific allele. If both sexes are associated,
then both are shown on the same row indicated by the male / female icons (<img src="img/both-sexes-icon.png" />).<br><br>
<p>The results shown in the phenotype table may be filtered using the dropdown filters.  Select the check boxes to include entries pertaining to the selection. The displayed rows are the result of logically ORing within and logically ANDing between dropdowns.</p>
<img src="img/gene-phenotype-filter.png" alt="Filter be top level MP term" />

<p class="intraSectBr">

<h4 id="phenotype-download">Gene-phenotype association download</h4>
<p>The results in the table may be downloaded for further processing.  The resulting download respects all filters that have been applied to the data.</p>
<p>We offer 2 export options for the data in the table: </p>
<ul>
    <li> TSV, text file with tab separated variables </li>
    <li> XLS, Microsoft Excel spread sheet</li>
</ul>
<img style="vertical-align:text-bottom;" src="img/export.png"/>
<p>
    In the table displayed on our page entry lines are collapsed based on sex. That is, if for 2 lines all fields are identical except the gender, they will be shown together for a better user experience.
    In the export file however we export all lines separately, to allow easier further processing of the data. This holds for both XLS and TSV files.
</p>

<p class="sectBr">

<h3 id="preqc-heatmap"><a name="heatmapSection0">Phenotype Heatmap</a></h3>
<p>When there is data available, but not yet complete, from the IMPC resource, the Pre-QC panel will appear.  The pre QC panel shows a heatmap of the results of preliminary analysis on data that has been collected by the IMPC production centers to date, but is not yet complete. In order to be marked Complete, 7 males and 7 females must complete all core screens required by the IMPC pipeline.
</p>
<p>Please visit the <a href="https://www.mousephenotype.org/heatmap/manual.html">comprehensive heatmap documentation</a> for more information about the heatmap.</p>
<p>Please visit the <a href="https://www.mousephenotype.org/impress">IMPReSS</a> website for more information about the IMPC pipeline.</p>
<p>NOTE: Use this analysis with caution as the analysis is likely to change when more data becomes available. </p>
<p>Below is an example of a typical IMPC heatmap.</p>
<br/>
<img src="img/gene-heatmap.png" />

<p class="sectBr">

<h3 id="impc-expression"><a name="expressionSection0">Expression</a></h3>
<p>
This section displays IMPC lacZ expression data (A). In some cases where legacy lacZ expression data is available (B), they will be shown as well.<br>
</p>
<br>
<img src="img/gene-expression.png" />

<p class="intraSectBr">

<h4>(1) IMPC lacZ Expression Data:</h4>
<p>Eventually, all genes should have both embyo and adult expression data. They are organized in tabs.
<ul class="subUl">
    <li>Adult Expression tab
        <p>An anatomogram displays all lacZ expressing tissues in blue. Mouseover will light up a tissue currently under the cursor. Click on a tissue in the list will take you to a page with expression images.<br>
        <p>Click on "Show expression table" on top right corner will display expression data in a table. Click again to hide it.</p>
        <p>The Expression Data Overview tab is a summary of the number of animals assessed for a given tissue, the number of times
            positive staining for LacZ was observed, and links to uploaded images. Images for tissues not exhibiting LacZ staining
            are not typically uploaded.<br /><img src="img/gene-adult-exp-data.png" />
    </li>
    <li>Adult Expression Image tab
    <p>Click on this tab to see all images expressed in the tissues shown in the "Adult Expression" tab.</p>
        <br />
        <img src="img/gene-adult-exp-img.png" />
    </li>
    <li>Embryo Expression tab
    <p>Click to show expression data in table by tissues.</p>
        <br /><img src="img/gene-emb-exp-data.png" />
    </li>
    <li>Embryo Expression Image tab</li>
    Some IMPC centers are assessing the expression of a LacZ reporter element contained as part of the allele constructs.
    Histology images stained to show LacZ images are presented along with metadata describing the tissue assessed.
The number in parentheses indicates how many images are available for a given image.
    <br /><img src="img/gene-emb-exp-img.png" />
    </li>
</ul>
<p class="intraSectBr">

<h4>(2) Legacy lacZ Expression Data:</h4>
<p>The data are organized by anatomy terms from the Mouse adult gross anatomy ontology.</p>

<p class="sectBr">

<h3 id="impc-images"><a name="phenoAssocImgSection0">Phenotype Associated Images</a></h3>
<p>A number of assays generate image data and are used by the phenotyping centers to score the presence or absence
    of an abnormal phenotype. Uploaded phenotype images are presented here organised by the procedure generating the image. In addition to the IMPC images, some genes have legacy ones.</p>
<p>The screenshot below shows the number of images grouped by procedures. Click to reveal the content.</p>
<p class="intraSectBr"></p>
<img src="img/gene-phenoAssoc-img.png" />

<p class="sectBr">


<h3 id="disease"><a name="diseaseSection0">Disease Models</a></h3>
<p>Model organisms represent a valuable resource for the characterization as well as identification of disease-gene associations, especially where the molecular basis is unknown and there is no clue to the candidate gene’s function, pathway involvement or expression pattern.
<p>Shown here are human diseases found by <a target="_blank" href="http://www.sanger.ac.uk/resources/databases/phenodigm/">PhenoDigm</a> analysis (PHENOtype comparisons for DIsease and Gene Models) which uses a semantic approach to map between clinical features observed in humans and mouse phenotype (either from MGI or IMPC phenotype evidences) annotations.
<p class="intraSectBr"><img src="img/gene-disease-models.png" /><p class="intraSectBr">
<p>The human disease model associations are based on gene orthology or phenotypic similarity that you can browse by clicking on the respective tabs.</p>
<p>In the table, each row is a human disease to mouse phenotype association. To see what mouse genes (alleles) are annotated to these mapped mouse phenotypes, clicked on the "plus" icon.</p>

<p class="sectBr">

<h3 id="order"><a name="orderSection0">Order Mouse and ES Cells</a></h3>
<p>The alleles and ES cells section describes the mutations available from the IKMC resource.  Each row corresponds to an allele of this gene.  A diagram is included depicting the mutation the allele carries.</p>
<p>The links in the <strong>Order/Contact</strong> column will take you to the purchase place of the ES cell or mouse when available.</p>
<p>The <strong>genbank file</strong> link points to a genbank file describing the genomic sequence of the allele.</p>
<br/>
<img src="img/gene-order-mouse-escells.png" />

