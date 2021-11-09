<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">Parallel Coordinates Chart for ${procedure}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>

    <jsp:attribute name="header">
                    
        <!-- CSS Local Imports -->
		<link rel="stylesheet" href="${baseUrl}/css/vendor/slick.grid.css" type="text/css" media="screen"/>
		<link rel="stylesheet" href="${baseUrl}/css/parallelCoordinates/style.css" type="text/css"/>
		<link rel="stylesheet" href="${baseUrl}/css/genes.css" type="text/css"/>

        <!-- JavaScript Local Imports -->
        <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/jquery/jquery.ui.widget.min.js"></script>
        <script type='text/javascript' src='${baseUrl}/js/general/ui.dropdownchecklist_modif.js?v=${version}'></script>
		<script type='text/javascript' src="${baseUrl}/js/general/dropDownParallelCoordinatesPage.js?v=${version}"></script>
		<script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>
		<script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/d3/d3.js"></script>
		<script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/d3/d3.csv.js"></script>
		<script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>

			 
		<script src="${baseUrl}/js/vendor/jquery/jquery.event.drag-2.0.min.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.core.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.grid.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.dataview.js"></script>
		<script src="${baseUrl}/js/vendor/slick/slick.pager.js"></script>
		<script type="text/javascript">
            var base_url = '${baseUrl}';
        </script>

    </jsp:attribute>

    <jsp:body>

        <div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">Gene knock-out effect comparator</h2>
                </div>
            </div>
        </div>

        <div id="allele-page" class="container white-bg-small">
            <div class="breadcrumbs clear row">
                <div class="col-12 d-none d-lg-block px-5 pt-5">
                    <aside>
                        <a href="/">Home</a> <span class="fal fa-angle-right"></span>
                        <a href="${baseUrl}/search">Genes</a> <span class="fal fa-angle-right"></span>
                        KO effect comparator
                    </aside>
                </div>
            </div>
            <div class="row pb-5">
                <div class="col-12 col-md-12">
                    <div class="pre-content clear-bg">
                        <div class="page-content people py-5 white-bg">
                            <div class="container p-0 p-md-2">
                                <div class="row no-gutters">
                                    <div class="col-12 px-5 pb-5">
                                        <p>The parallel coordinates viewer allows to visualize multiple mouse lines for multiple
                                            parameters for selected procedures. Only continuous parameters can be visualized using
                                            this methodology.</p>

                                        <p>The values displayed are genotype effects. The measurement values are corrected to
                                            account for batch effects to represent the true genotype effect, thus allowing a
                                            side-by-side comparison.</p>

                                        <h3>How to use this tool?</h3>

                                        <p>Select one or several procedure(s) using the procedure filter tab. Additional optional
                                            filtering include phenotyping center or mouse gene list of interest selection. If no
                                            genes are selected, the tool will show all genes in IMPC for the selected procedure(s)
                                            and/or center selected.</p>

                                        <p>The graph is interactive and allows filtering on each axis (parameter) by selecting the
                                            region of interest. Several regions of interests can be selected one by one.</p>

                                        <p>Clicking on a chosen line on the graph or on a gene name from the table will highlight
                                            the corresponding gene. For a selected gene, if any significant phenotype is associated
                                            with a parameter, the parameter name will change to bold. To remove all filters, simply
                                            reload the page.</p>

                                        <p><b>More information can be found in the <a
                                                href="${cmsBaseUrl}/help/data-visualization/faqs/how-do-i-use-the-parallel-coordinates-viewer/">Help
                                            pages</a>.</b></p>

                                        <form class="shadow-sm p-3 mb-5 bg-light rounded" id="target">
                                            <div class="row align-middle">
                                                <div class="col-3">
                                                    <select id="proceduresFilter" class="impcdropdown" multiple="multiple" title="Select procedures to display">
                                                        <c:forEach var="procedure" items="${procedures}">
                                                            <option value="${procedure.getStableId().substring(0,8)}">${procedure.getName()}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="col-3">
                                                    <select id="centersFilter" class="impcdropdown" multiple="multiple" title="Select centers to display">
                                                        <c:forEach var="center" items="${centers}">
                                                            <option value="${center}">${center}</option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="col-5">
                                <textarea onfocus="if(this.value==this.defaultValue)this.value=''"
                                          onblur="if(this.value=='')this.value=this.defaultValue" id="geneIds" rows="2"
                                          cols="30"
                                          style="width:auto">Filter by gene symbols comma separated.	</textarea>
                                                </div>
                                                <div class="col-1">
                                                    <a href="#" id="geneFilterButton" class="btn btn-primary" title="Filter by gene">Go</a>
                                                </div>
                                            </div>

                                            <div id="widgets_pc" class="widgets" class="right"></div>
                                        </form>

                                        <div id="chart-and-table">
                                            <div id="spinner" class="row no-gutters">
                                                <div class="col-12 my-5">
                                                    <p class="h4 text-center text-justify"><i class="fas fa-atom fa-spin"></i> A moment please while we gather the data . . . .</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </jsp:body>

</t:genericpage>