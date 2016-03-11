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

                            <h1>Explore the Diverse Entry Points to Mouse Phenotype Data.</h1>

                            <p>
                                Our phenotype page consists of three main parts: the phenotype information/details, the gene associations summary and the table of gene variants 
                                associated with the current phenotype.
                            </p>

                            <h3 id="details">Phenotype Details</h3>
                            
                            <p>The synonyms and definitions we provide come from the mouse phenotype ontology (MP), which
                               is administered by <a href="http://www.informatics.jax.org/">Mouse Genome Informatics (MGI)</a>.
                               Use the provided MGI link to go to the MGI MP browser.</p>

                            <p> The procedures come from IMPC pipelines and from legacy data pipelines such as EUMODIC and GMC.</p>

                            <figure>
                                <img class="well" src="img/phenotype-information.png"/>
                            </figure>


                            <h3 id="phenotype-stats-panel">Phenotype Association Stats</h3>
                            
                            <p> IMPC mouse strains are subjected to a wide range of phenotype assays, allowing for estimates on the percentage of genes that, when knocked out, contribute to
                                a phenotype. As multiple parameters may map to the same phenotype, percentages are calculated across all parameters that map to a phenotype.</p>
                                <p> The percentage of genes  associated to the current phenotype in one sex only (for example, females) is the number of genes associated to phenotype <i>X</i>
                                in  females divided by the total number of genes tested in females for parameters potentially leading to phenotype <i>X</i> associations. Thus, the percent
                                values we show for females and males may not add up to 1, nor do the percentages for males/females have to be smaller than the combined percentage.</p> 
                                
                            <p> We consider "tested" for a phenotype a gene for which at least one parameter potentially leading to this phenotype association has data and has been statistically 
                            	analyzed by our pipeline.</p>

                            <img class="well" src="img/phenotype-overview.png"/>
                            
                            <p> Currently the data used for this panel is restricted to IMPC data on B6N strains. Mutant strains with the phenotype associations appear in the
                            	gene-phenoytype table found further down the phenotype page.</p>


                            <h3 id="phenGraphs"> Overview Graphs</h3>
                            <p>As the data is extremely varied, for instance, in what controls are selected, what is represented (e.g. individual animals vs strains),
                               and the nature of the values (e.g. means, count, etc.), each is described separately below.</p>
                                
                            <h5> Chart Filters and Selectors</h5>
                            <p> Multiple parameters can indicate the same phenotype. When this is the case, a drop-down list will appear on top of the list allowing you to select the desired parameter.</p>
                           	<figure>
                                <img class="well" src="img/graph-filters-closed.png"/>
                                <img class="well" src="img/graph-filters-open.png"/>
                            </figure>
                            <p> The filters under the chart allow you to filter the plotted data based on sex and phenotyping center. </p>
                            <figure>
                                <img class="well" src="img/graph-selector-closed.png"/>
                                <img class="well" src="img/graph-selector-open.png"/>
                            </figure>
                            <br/> <br/>
                            

                            <h5> Unidimensional Data </h5>
                            <figure>
                                <img class="well" src="img/unidimensional-overview.png"/>
                            </figure>
                            <p> Unidimensional data is plotted as stacked histograms. We take the mean for each line and plot these values as a histogram. Mutant 
                            	lines that have been associated to the phenotype are highlighted, including those lines where the phenotype was only observed in
                                one gender or zygotic state. Some lines may be associated to a phenotype but may not appear to be an outlier. This usually results
                                from controls having relatively low or high values in the time period the mutant lines were tested.
                            </p>
							<p>[Tip] The bars are clickable and will take you to a multi-chart page to analyze the data more closely. </p>
														

                            <!-- h5> Time Series Data  </h5>
                            <figure>
                                <img class="well" src="img/time-series-overview.png"/>
                            </figure>
                            <p> For time-series data we plot mean values for each mutant line. We average the values in each line at each time-point and thus get the plot values. 
                                To simplify visualization of calorimetric parameters, data is binned into discrete time points by taking the mean of values measured between two time 
                                points. The 
                                control data represents the mean of all C57BL/6N baseline animals. For the mutant lines we do not show the error bars in order to avoid overcrowding 
                                the chart.
														<p>[Tip] You can select which lines to see on the chart by simply clicking on the names in the legend. </p-->
														<br/> <br/>

                            <h5> Categorical Data </h5>
                            <figure>
                                <img class="well" src="img/categorical-overview.png"/>
                            </figure>
                            <p> Categorical overview charts represent the percentage of strains in each category. These graphs only display for categorical parameters at the line level 
                            (as opposed to animal level), such as fertility or viability parameters. The animal-level parameters can only be analyzed in the individual charts linked from the associations table.
                            </p>


                            <h3 id="associations">Gene-Phenotype Associations</h3>

                            <p>All gene variants associated with the current phenotype are shown in a table. The table contains several fields of interest, such
                                as the gene name and the corresponding allele, zygosity, sex, data source, parameter, a link to the <a href="graph-help.html">chart</a>
                                when one is available, as well as the procedure used, and directly associated phenotype. The directly associated phenotype is particularly
                                useful for higher level phenotype terms. See <a href="phenotype-help.html#dvi">Direct vs. inferred associations</a> for more information.
                            </p>

                             <h5>Row Collapsing</h5>
                             <p>For better readability, when rows are identical in all fields except for sex, they are
                                collapsed into a single row. Such rows are identified by a <i>both-sexes</i> icon
                                (<img src="img/both-sexes-icon.png"/>). Regardless of row collapsing, the total number of
                                results shown at the top of the table includes all males and all females.</p>
                            <img class="well" src="img/phenotype-association-table.png"/>
                            <br/> <br/>


                            <h5 id="dvi">Direct vs. inferred associations</h5>
                            <p>Some associations are direct calls from our statistical pipeline, whereas some are transitively associated, infered from the direct
                               lower level associations. The value in the 'Phenotype' column will help you disambiguate at which level the gene-phenotype association
                               was made.</p>

                            <h5 id="preqc-postqc"> Pre-QC vs. Post-QC  Calls</h5>
                            <p>Preliminary statistical analysis is performed at the DCC as soon as enough data is gathered, prior to rigorous quality control checking.
                            This analysis produces results, but due to the preliminary state of the QC checks, the results are considered not definitive.
                            Once the data has passed the QC checks at the DCC, a final definitive statistical test is performed and the MP association is made. </p>
                            <p>Post QC calls are presented in the associations table and have blue chart links. </p>
                            <p>Pre-QC calls are presented in the associations table and have orange chart links in that table and in the heatmap below the table. </p>
																		 
																																 
                            <h5 id="table-filtering">Table Filtering</h5>
                            <p>The filters over the gene variants associations table offer flexible filtering possibilities. Multiple checkboxes can be selected from
                                any filter dropdown list and the table will automatically reload with each new selected option. These changes will be mirrored by the
                                total number of results over the table as well as by the table export.
                            </p>
                            <p>Multiple filters from the same dropdown list are joined by a logical OR. Filters between different lists are joined by a logical AND. </p>
                            <img src ="img/phenotype-filters.png"/>


                            <h3 id="table-download">Downloading Results</h3>
                            <p> The results in the table may be downloaded for further processing. The resulting download respects all filters that have been applied to the data.</p>
                            <p> We offer two export options for the data in the table: text file with tab separated variables (TSV) and Microsoft Excel spread sheet (XLS)</p>
                            <img style="vertical-align:text-bottom;" src="img/export.png"/>

                            <p>Please note: while collapsed rows are shown on the <i>page</i>, <i>download file</i> rows are not collapsed;
                               the download file contains a single row for every mouse.</p>

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
