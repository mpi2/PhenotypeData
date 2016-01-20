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
                            
                            <h3>Explore the diverse entry points to mouse phenotype data.</h3>
							Currently, the IMPC portal supports 6 main datatypes on the search page:
							Genes, Phenotypes, Diseases, Anatomy, Impc Images, and legacy Images.<br>
							Each main datatype has filters to allow for data filtering.<p>
                            <img src='img/search-main-data-type.png' /><p>

                            <div>Clicking on a main datatype tab under the main input box on top of the search page (eg, Genes, Phenotypes, Diseases) without ticking its filter(s) (in left side panel) will display all records of that datatype.
                            The screenshot above shows the total number of genes in the portal.<p><p></p>

                            Depending on the datatype, the filters will be changed dynamically.<br>

                            <h6>Filter control and behavior</h6>
                            <div>You are in control of whether a filter is open or closed by ticking on it.<p>
                                When a filter is checked (ie, ticking on the checkbox), the result counts will change dynamically. The counts of non-matching filters will become zero and disabled (grayed out) and when you hover over them, a no-entry sign will appear indicating that that filter is now unclickable until it matches other filter combinations.
                            </div>

                        </div><br>
                            A table explaining each filter will be provided for each datatype described below.<p></p>


                            <h4 id="autosuggest_srch">Auto-suggest Support for Keyword Search</h4>
                            <div>Enter at least 3 letters to invoke a drop-down list of terms (prefixed by datatype) related to your search keyword.
                                The top ten most relevant terms will be shown, in order of relevance scores.
                                You can select the desired term by using the UP/DOWN keys or by selecting the term with the mouse and pressing the ENTER key.<p></p><p></p>

                                <img src='img/search-autosuggest.png' /><p>

                                <b>Keyword search and datatype</b><br>
                                When a datatype is chosen, typing a keyword in the main input box and hit ENTER w/o choosing an item from the dropdown list will show the result with the same main datatype selected.<br>
                                However, when you specifically choose an item from the dropdown list, the selected datatype will be the one associated with the selected item.<br>

                            </div><br>

                            The table shows the possible datatypes currently searchable.

                            <table>
                                <tr>
                                    <th>Datatype</th>
                                    <th>Example of value</th>
                                    <th>Source</th>
                                    <th>Method</th>
                                </tr>
                                <tr>
                                    <td>gene</td>
                                    <td>Akt2</td>
                                    <td><a href="http://www.informatics.jax.org">MGI</a></td>
                                    <td>-</td>
                                </tr>
                                <tr>
                                    <td>mp</td>
                                    <td>abnormal IgM level</td>
                                    <td><a href="http://www.obofoundry.org/ontology/mp.html">Mammalian phenotype </a></td>
                                    <td>MPs in IMPC are associated with phenotypes assessed using <a href="http://www.mousephenotype.org/impress">IMPReSS</a> phenotyping protocols</td>
                                </tr>
                                <tr>
                                    <td>ma</td>
                                    <td>brain</td>
                                    <td><a href="http://www.obofoundry.org/ontology/ma.html">Mouse adult gross anatomy </a></td>
                                    <td>MAs in IMPC are associated with tissues/organs assessed using <a href="http://www.mousephenotype.org/impress">IMPReSS</a> phenotyping protocols</td>
                                </tr>
                                <tr>
                                    <td>disease (human disease)</td>
                                    <td>Bloom Syndrome</td>
                                    <td>OMIM/ORPHANET/DECIPHER</a></td>
                                    <td>Human to mouse disease mapping using <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a></td>
                                </tr>
                                <tr>
                                    <td>hp</td>
                                    <td>Glucose intolerance » MP:0005293 - impaired glucose tolerance</td>
                                    <td><a href="http://www.obofoundry.org/ontology/hp.html">Human phenotype ontology</a></td>
                                    <td>Hp to Mp mapping using <a href="http://www.sanger.ac.uk/resources/databases/phenodigm/">Phenodigm</a></td>
                                </tr>

                            </table>


                            <h4 id="quick_gene_srch">Gene Search</h4>
                            <div>First, click on the "Genes" datatype tab. It will show all the genes that IMPC knows about if no filters are ticked on the left panel.<br>
                                You could enter a gene symbol/ID/name or human orthologue into the main input box and/or click on a filter on the left panel to narrow down your result to relevant gene(s).
                                <p>The screenshot below shows searching the gene symbol "mtf1":<br>
                            </div><br>
                            <img src='img/search-gene.png' /><p>

                                The filters on the left are grayed out if they are not related to your search.<br><br>

                                <b>Columns in the dataset on the right panel:</b>
                            <table>
                                <tr>
                                    <th>Column</th>
                                    <th>Explaination</th>
                                </tr>
                                <tr>
                                    <td>Gene</td>
                                    <td>This column shows the symbol of a gene with a linkout to the IMPC gene page. When mouseover this column, its gene name, human ortholog(s) and synonym will be displayed whichever is available</td>
                                </tr>
                                <tr>
                                    <td>Production Status</td>
                                    <td>Possible information in this column are one of the four values:<br>
                                        (1) "mice produced" labelled as "Mice" in blue box<br>
                                        (2) "assigned for mouse production and phenotyping" labelled as "Mice" in orange box<br>
                                        (3) "ES cells produced" labelled as "ES Cells" in blue box<br>
                                        (4) "not assigned for ES cell production" which is not shown<br><br>
                                        Depending on the status of a gene, it can have a mixture of information for both mice and ES cells
                                    </td>
                                </tr>
                                <tr>
                                    <td>Phenotype Status</td>
                                    <td>Possible information in this column are:<br>
                                        (1) "phenotype data available" in blue box: this indicates a gene with phenotyping data already being approved or a center has started producing phenotyping data of a gene<br>
                                        (2) "legacy data available" in gray box: this indicates a gene has old phenotyping data from the Eumodic / Europhenome projects<br><br>
                                        Depending on the status of a gene, it can have a mixture of information for both
                                    </td>
                                </tr>
                                <tr>
                                    <td>Register Interest</td>
                                    <td>The "Interest" link takes you to login page if you have not. Once you have logged in you will be taken back to this column and the label will be changed to "Register interest".<br>
                                        Click on it will register you to the interest alert system of IMPC and you will be alerted once the gene status data is available/updated
                                    </td>
                                </tr>
                            </table>

                            <h4 id="quick_pheno_srch">Phenotype Search</h4>
                            <div>Firts, click on the "Phenotype" datatype tab. It will show all the mouse phenotypes that IMPC knows about if no filters are ticked on the left panel.<br>
                                You could enter a human or mouse phenotype (either by text or by ID) into the main input box and/or click on a filter (all top level mouse phenotype ontology terms) on the left panel to narrow down your result to relevant phenotype(s).
                                <p>The screenshot below shows searching phenotypes containing the word "glucose":<br>

                            </div><br>
                            <img src='img/search-phenotype.png' /><p>
                                The filters on the left are grayed out if they are not related to your search.<br><br>

                                <b>Columns in the dataset on the right panel:</b>
                            <table>
                                <tr>
                                    <th>Column</th>
                                    <th>Explaination</th>
                                </tr>
                                <tr>
                                    <td>Phenotype</td>
                                    <td>This column shows the ontological term of a phenotype with a linkout to the IMPC phenotype page. When mouseover this column, its computationally mapped HP term(s) and/or term synonym(s) will be displayed if available</td>
                                </tr>
                                <tr>
                                    <td>Definition</td>
                                    <td>Ontological definition of this mouse phenotype term
                                    </td>
                                </tr>
                                <tr>
                                    <td>Phenotyping Call(s)</td>
                                    <td>This tells you at a glance whether a phenotype has phenotyping data to date
                                    </td>
                                </tr>
                                <tr>
                                    <td>Register Interest</td>
                                    <td>The "Interest" link takes you to login page if you have not. Once you have logged in you will be taken back to this column and the label will be changed to "Register interest".<br>
                                        Click on it will register you to the interest alert system of IMPC and you will be alerted once the phenotyping data is available/updated
                                    </td>
                                </tr>
                            </table>


                            <h4 id="quick_disease_srch">Disease Search</h4>
                            <div>First, click on the "Diseases" datatype tab. It will show all the diseases that IMPC knows about if no filters are ticked on the left panel.<br>
                                You could enter a human disease ID/name into the main input box and/or click on a filter on the left panel to narrow down your result to relevant disease(s).<br><br>
                                <p>The screenshot below shows searching the human "cardiac" diseases:<br>
                            </div><br>

                            </div><br>
                            <img src='img/search-disease.png' /><p>

                            The filters on the left are grayed out if they are not related to your search.<br><br>

                            <b>Columns in the dataset on the right panel:</b>
                            <table>
                                <tr>
                                    <th>Column</th>
                                    <th>Explaination</th>
                                </tr>
                                <tr>
                                    <td>Disease</td>
                                    <td>This column shows the human disease name from OMIM/DECIPHER/ORPHANET with a linkout to the IMPC disease page.<br>
                                        In the disease page, mouse genes are mapped to this human disease which is based on the PhenoDigm phenotypic similarity tool using either Sanger-MGP or MGI data</td>
                                </tr>
                                <tr>
                                    <td>Source</td>
                                    <td>A human disease term is from either OMIM or DECIPHER or ORPHANET
                                    </td>
                                </tr>
                                <tr>
                                    <td>Curated Genes</td>
                                    <td>Possible values are:<br>
                                        (1) human: known disease-gene associations in human from OMIM/DECIPHER/ORPHANET in blue box<br>
                                        (2) mice: literature curated mouse models of disease from MGI in blue box.<br><br>

                                        Depending on the disease, it can have a mixture of information for both
                                    </td>
                                </tr>
                                <tr>
                                    <td>Candidate Genes by phenotype</td>
                                    <td>The value is MGI in blue box if applicable. It represents predicted mouse models of disease based on the PhenoDigm phenotypic similarity tool using either Sanger-MGP or MGI data</td>
                                </tr>
                            </table>

                            <h4 id="quick_anatomy_srch">Anatomy Search</h4>
                            <div>First, click on the "Anatomy" datatype tab. It will show all the anatomy terms that IMPC knows about if no filters are ticked on the left panel.<br>
                                You could enter a mouse anatomy ID/name into the main input box and/or click on a filter (all top level mouse anatomy ontology terms) on the left panel to narrow down your result to relevant anatomy term(s).<br><br>
                                <p>The screenshot below shows searching the mouse anatomy terms containing "eye":<br>
                            </div><br>
                            <img src='img/search-anatomy.png' /><p>

                            The filters on the left are grayed out if they are not related to your search.<br><br>

                            <b>Columns in the dataset on the right panel:</b>
                            <table>
                                <tr>
                                    <th>Column</th>
                                    <th>Explaination</th>
                                </tr>
                                <tr>
                                    <td>Anatomy</td>
                                    <td>This column shows the ontological term of a mouse anatomical tissue/organ with a linkout to the IMPC anatomy page</td>
                                </tr>
                            </table>


                        </div><br>

                            <h4>IMPC Image Search</h4>

                            Currently, there are two major types of images. The "IMPC images" (the IMPC Images datatype) and the legacy ones (the Images datatype).<br>
                            The Images datatype is legacy data from previous Europhenome/Eumodic phenotyping projects.<br><br>

                            As both datatypes can be searched by similar interfaces, the document here refers to the "IMPC Images" datatype.<br><br>

                            <div>First, click on the "IMPC Images" datatype tab. It will show all images that IMPC knows about if no filters are ticked on the left panel.<br>
                            You could enter a mouse anatomy ID/name or procedure name from IMPReSS into the main input box and/or click on a filter on the left panel to narrow down your result to relevant IMPC images.<br>
                            IMPReSS contains standardized phenotyping protocols essential for the characterization of mouse phenotypes.<br><br>
                            <p>The screenshot below shows searching the IMPC Images datatype with the mouse anatomy term "trunk":<br>
                        </div><br>

                            There are two ways to view images data: Annotation View and Image View.<br>
                            The former groups images by annotations which can be genes, anatomy terms (MA) or procedures. The latter lists annotations (genes, MA and procedure) to an image.<br><br>


                    <h6 id="quick-image-search">Annotation View (default)</h6>
                    <img src='img/quick-img-search-annotView.png' /><br>


                    <h6 id="quick-image-search-image-view">Image View</h6>
                    <p>To list annotations to an image, simply click on the "Show Image View" link in the top-right corner of the results grid. The label of the same link will then change to "Show Annotation View" so that you can toggle the views.</p><br>
                    <img src='img/quick-img-search-imageView.png' /><br>

                    <b>Columns in the dataset on the right panel:</b>

                    <table>
                        <tr>
                            <th>Column</th>
                            <th>Explaination</th>
                        </tr>
                        <tr>
                            <td>Name</td>
                            <td><b>Annotation View</b>:<br>This column shows name of an annotation and the number of annotation-associated images.<br>A gene annotation is a linkout to IMPC gene page; a procedure annotation does not have a link for now and a MA annotation is a linkout to anatomy page<br><br>
                                <b>Image View</b>:<br>This column shows a group of different annotations (gene, procedure, MA)<br>
                            </td>>
                        </tr>
                        <tr>
                            <td>Images</td>
                            <td>Shows thumbnail image (Image View) or thumbnail images (Annotation View, maximun of 4) with linkout to images page</td>
                        </tr>
                    </table>


                    <h4 id="export">Data Export of Search Results</h4>
                            <div>Click on the download icon <p><img src='img/search-download.png' />
                            <br>in the top-right corner of the results grid to expand or hide it.
                                When expanded, it looks like this:<p> <img src='img/search-export-expanded.png' /><p><p>
                                    Click on either the TSV (tab separated) or the XLS (MS Excel) link for the desired report format.
                                <p>To download data for the currently displayed page only, choose the set of links under the label "Current paginated entries in table".
                                   To download larger dataset, click on the "Batch query" link.</p>

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
