<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">International Mouse Phenotyping Consortium Documentation</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/documentation/index">Documentation</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body class="page-node searchpage one-sidebar sidebar-first small-header"></body></jsp:attribute>
	<jsp:attribute name="addToFooter"></jsp:attribute>
	<jsp:attribute name="header"></jsp:attribute>

	<jsp:body>
		
        <div id="wrapper">

            <div id="main">

                <!-- Sidebar First -->
                <jsp:include page="doc-menu.jsp"></jsp:include>

                <!-- Maincontent -->

                <div class="region region-content">

                    <div class="block block-system">

                        <div id="top" class="content node">
                            
                            <h1>Gene Page Documentation</h1>
		

                            <h3 id="details">Gene Details</h3>
                            The first section of the page shows detail information about the gene. The information displayed includes:
                            <ul>
                                <li><strong>Name</strong></li>
                                <li><strong>Synonyms</strong></li>
                                <li><strong>MGI Id</strong>
                                    Links to the corresponding gene detail page at <a href="http://www.informatics.jax.org">Mouse Genome Informatics</a>
                                </li>
                                <li><strong>Status</strong>
                                        The latest IMPC production status for this gene.  This aggregates the ES and mouse production statuses as well
                                        as phenotyping statuses of all ongoing projects for this gene in <a href="https://www.mousephenotype.org/imits/">iMits</a>
                                        and displays the status of the project that is closest to producing a mouse. It also mentions if legacy data
                                        is available (i.e. from EuroPhenome). Genes with "legacy data available" may have IMPC data available as well.
                                        <img src="img/gene-status-1.png"/>
                                </li>
                                <li><strong>ENSEMBL Links</strong>
                                    Links to different views of the gene in the Ensembl genome browser
                                    <ul>
                                        <li><strong>Gene view</strong>
                                            Links to the browser centered on the gene
                                        </li>
                                        <li><strong>Location view</strong>
                                            Links to a view of the chromosome
                                        </li>
                                        <li><strong>Compara</strong>
                                            Links to the Ensembl compara comparative genomics view for this gene
                                        </li>
                                    </ul>
                                </li>
                                <li><strong>Other Links</strong>
                                    <ul>
                                        <li><strong>Gene Browser</strong> Interactive graphical gene browser. Clicking
                                            the link shows a genome browser displaying a graphical view of the gene's location
                                            and surrounding features.  The browser is interactive and you can use your mouse to zoom and scroll.
                                            <br/>
                                            <img src="img/gene-browser.png" />
                                        </li>

                                        <li><strong>ENU</strong> Links to the ENU mutant library at the Australian Phenomics Facility</li>
                                    </ul>
                                </li>
                            </ul>
                            <br/>
                            <img src="img/gene-details.png" />

                            <h3 id="phenotype-associations">Phenotype associations</h3>
                            <p>This section shows the association of genes to <a href="http://www.informatics.jax.org/searches/MP_form.shtml">Mammalian phenotype</a> terms.</p>

                            <h4 id="phenotype-summary">Gene Phenotype Summary</h4>

                            <p>The section starts with a summary of phenotype top level terms for this gene, with associated counts. The counts indicate the number of calls for
                            		the current gene, grouped by zygosity, sex and top level MP term. The icons on the right hand side show a visual summary of the same data. </p>
                            <p>Because MP terms may have more than one high level parent, the counts in the summary may not equal the total number of entries in the table.</p>
                            <p>The grid at the top right contains icons intended to discover test phenotype status at a glance. Icons in orange indicate significant phenotypes
                            were discovered. Icons in blue indicate tests were performed but no significant phenotypes were discovered. Icons in gray indicate no phenotype tests
                             were performed.</p>
                            <p>To see all test results for adult data, press the "All Adult Data" button (described below).
                            <br />
                            If there are embryo images, press the "3D Imaging" button (described below) to view them.</p>
                            <img src="img/gene-phenotype-summary.png" />


                            <h4 id="phenotype-phenome-hyperlink">Browse all adult phenotype data for a gene</h4>
                            <p>Access to the details of the statistical analysis is provided in a separate page by clicking on the
                                <strong>All Adult Data</strong> button. This page provides access to all statistical results for a
                                mutant line whether significant or not.
                            </p>
                            <p>Significant P values appear to the right of the green dashed vertical line; insignificant values to the left.
                               Hovering over any point shows some important experiment details. Clicking on any point shows a graph of the experiment data.
                            </p>
                            <br />
                            <img src="img/all-data.png" />
                            <br/><br/>

                            <h4 id="embryo-viewer">3D Imaging</h4>
                            <p>This button appears when there are embryo images. Click this button to launch the embryo viewer.</p>
                            <br />
                            <img src="img/embryo-viewer.png" />
                            <br /><br />

                            <h3 id="phenotype-table">Gene Phenotype Table</h3>
                            <p>The phenotype table lists the individual phenotypes associated to this gene through a specific allele.  If both sexes are associated, 
                            		then both are shown on the same row indicated by the male / female icons (<img src="img/both-sexes-icon.png" />).</p>
                            <br/>
                            <img src="img/gene-table.png" alt="" />


                            <h3 id="phenotype-filtering">Gene Phenotype Filtering</h3>
                            <p>The results shown in the phenotype table may be filtered using the dropdown filters.  Select the check boxes to include entries pertaining to the selection.  The displayed rows are the result of logically ORing within and logically ANDing between dropdowns.</p>
                            <img src="img/gene-phenotype-filter.png" alt="Filter be top level MP term" />
							<br/><br/>

                            <h3 id="phenotype-download">Gene Phenotype Download</h3>
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


                            <h3 id="preqc-heatmap">Pre-QC Phenotype Heatmap</h3>
                            <p>When there is data available, but not yet complete, from the IMPC resource, the Pre-QC panel will appear.  The pre QC panel shows a heatmap of the results of preliminary analysis on data that has been collected by the IMPC production centers to date, but is not yet complete. In order to be marked Complete, 7 males and 7 females must complete all core screens required by the IMPC pipeline.
                            </p>
                            <p>Please visit the <a href="https://www.mousephenotype.org/heatmap/manual.html">comprehensive heatmap documentation</a> for more information about the heatmap.</p>			
                            <p>Please visit the <a href="https://www.mousephenotype.org/impress">IMPReSS</a> website for more information about the IMPC pipeline.</p>			
                            <p>NOTE: Use this analysis with caution as the analysis is likely to change when more data becomes available. </p>
                            <br/>
                            <img src="img/gene-pre-qc.png" />
                            <br/> <br/>


                            <%--<h3 id="expression-anatomogram">Expression in Anatomogram</h3>--%>
                            <%--<p>The mouse anatomogram shows the lacZ expressions in blue, at a glance.--%>
                            <%--</p>--%>


                            <h3 id="impc-expression">Expression</h3>
                            <p>
                                <h4>Expression Images View</h4>
                                Some IMPC centers are assessing the expression of a LacZ reporter element contained as part of the allele constructs.
                                Histology images stained to show LacZ images are presented along with metadata describing the the tissue assessed.
                                The number in parentheses indicates how many images are available for a given image.
                                <br /><br />
                                <img src="img/gene-expression-images-view.png" />
                            </p>
                            <p>
                                <h4>Expression Data Overview</h4>
                                The Expression Data Overview tab is a summary of the number of animals assessed for a given tissue, the number of times
                                positive staining for LacZwas observed, and links to uploaded images. Images for tissues not exhibiting LacZ staining
                                are not typically uploaded.
                            <br /><br />
                            <img src="img/gene-expression-data-overview.png" />
                            </p>

                            <h3 id="impc-images">Phenotype Associated Images</h3>
                            <p>
                                A number of assays generate image data and are used by the phenotyping centers to score the presence or absence
                                of an abnormal phenotype. Uploaded phenotype images are presented here organised by the procedure generating the image. </p>
                            <img src="img/gene-phenotype-images.png" />

                            <h3 id="order">Order Mouse and ES Cells</h3>
                            <p>The alleles and ES cells section describes the mutations available from the IKMC resource.  Each row corresponds to an allele of this gene.  A diagram is included depicting the mutation the allele carries.</p>
                            <p>The links in the <strong>Order</strong> column will take you to the purchase place of the ES cell or mouse when available.</p>  
                            <p>The <strong>genbank file</strong> link points to a genbank file describing the genomic sequence of the allele.</p>
                            <br/> <br/>
                            <img src="img/gene-alleles.png" />
				

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
