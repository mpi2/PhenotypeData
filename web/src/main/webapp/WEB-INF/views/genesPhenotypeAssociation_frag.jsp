<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.9/dist/css/bootstrap-select.min.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.9/dist/js/bootstrap-select.min.js"></script>

<script>

    var phenotypeSystemToMpTerms = {
        "mortality/aging": ["MP:0010768"],
        "embryo phenotype": ["MP:0005380"],
        "reproductive system phenotype": ["MP:0005389"],
        "growth/size/body region phenotype": ["MP:0005378"],
        "homeostasis/metabolism phenotype or adipose tissue phenotype": ["MP:0005376", "MP:0005375"],
        "behavior/neurological phenotype or nervous system phenotype": ["MP:0005386", "MP:0003631"],
        "cardiovascular system phenotype": ["MP:0005385"],
        "respiratory system phenotype": ["MP:0005388"],
        "digestive/alimentary phenotype or liver/biliary system phenotype": ["MP:0005381", "MP:0005370"],
        "renal/urinary system phenotype": ["MP:0005367"],
        "limbs/digits/tail phenotype": ["MP:0005371"],
        "skeleton phenotype": ["MP:0005390"],
        "immune system phenotype or hematopoietic system phenotype": ["MP:0005387", "MP:0005397"],
        "muscle phenotype": ["MP:0005369"],
        "integument phenotype or pigmentation phenotype": ["MP:0010771", "MP:0001186"],
        "craniofacial phenotype": ["MP:0005382"],
        "hearing/vestibular/ear phenotype": ["MP:0005377"],
        "taste/olfaction phenotype": ["MP:0005394"],
        "endocrine/exocrine gland phenotype": ["MP:0005379"],
        "vision/eye phenotype": ["MP:0005391"]
    };

    function removeOption(filterName, value) {
        $("#systemSelector" + filterName + " option[value='" + value + "']").prop("selected", false);
        $('#systemSelector' + filterName).selectpicker('render');
        filterAllData(filterName);
    }

    function filterAllData(filterName) {
        var val = [];
        var legend = '';
        $('#systemSelector' + filterName).val().forEach(function (value) {
            var mpTerm = value.split('|')[0];
            var name = value.split('|')[1];
            var significance = value.split('|')[2];
            var icon = value.split('|')[3];
            val.push(phenotypeSystemToMpTerms[name]);
            var color = significance === 'significant' ? 'badge-primary' : 'badge-info';
            legend += '<span class="badge badge-pill filter-badge ' + color + ' mr-1"><i class="badge-icon ' + icon + '"></i> ' + name.replace(/phenotype/g, "") + ' <i class="close-badge fas fa-times" onclick="removeOption(\'' + filterName + '\',\'' + value + '\')"></i></span>';
        });
        legend = legend === '' ? ' all phenotypes' : legend;
        $('#ph' + filterName + 'DataTitle').html(legend);
        var content_id = '#all-' + filterName.toLocaleLowerCase();
        $(content_id).html("     <div class=\"pre-content\">\n" +
            "                        <div class=\"row no-gutters\">\n" +
            "                            <div class=\"col-12 my-5\">\n" +
            "                                <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </div>");
        $('#phenotypesTab').scrollTop();
        $.ajax({
            url: baseUrl + '/experiments' + filterName + 'Frag?geneAccession=' + '${gene.mgiAccessionId}' + '&mpTermId=' + val.join(','),
            type: 'GET',
            success: function (data) {
                $(content_id).html(data);
            }
        });
    }
</script>

<div class="container-fluid">
    <div class="row flex-xl-nowrap pb-5">
        <div class="col-lg-8">
            <!-- only display a normal div if no phenotype icons displayed -->
            <div id="phenoSumSmallDiv" class="text-left">
                <div class="row no-gutters mb-2 mb-sm-0">
                    <div class="col-3 align-middle text-sm-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Name</div>
                    </div>
                    <div class="col-9 align-middle text-sm-left">
                        <span>${gene.markerName}</span>
                    </div>
                </div>
                <div class="row no-gutters mb-2 mb-sm-0">
                    <div class="col-3 align-middle text-sm-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">MGI ID</div>
                    </div>
                    <div class="col-9 align-middle text-sm-left">
                        <span>${gene.mgiAccessionId}</span>
                    </div>
                </div>
                <div class="row no-gutters mb-2 mb-sm-0">
                    <div class="col-3 align-middle text-sm-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Synonyms</div>
                    </div>
                    <div class="col-9 align-middle text-sm-left">
                        <c:if test='${fn:length(gene.markerSynonym) gt 1}'>
                            <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                                <span><t:formatAllele>${synonym}</t:formatAllele></span>
                            </c:forEach>
                        </c:if>
                        <c:if test='${fn:length(gene.markerSynonym) == 1}'>
                            <c:forEach var="synonym" items="${gene.markerSynonym}" varStatus="loop">
                                <span><t:formatAllele>${synonym}</t:formatAllele></span>
                                <c:if test="${!loop.last}">,&nbsp;</c:if>
                            </c:forEach>
                        </c:if>
                        <c:if test="${(empty gene.markerSynonym)}">
                            N/A
                        </c:if>
                    </div>
                </div>

                <div class="row no-gutters mb-2 mb-sm-0">
                    <div class="col-3 align-middle text-sm-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Viability</div>
                    </div>
                    <div class="col-9 align-middle text-sm-left">
                        <t:viabilityButton callList="${viabilityCalls}" geneAcc="${gene.mgiAccessionId}" />
                    </div>
                </div>

                <div class="row no-gutters mb-2 mb-sm-0">
                    <div class="col-3 align-middle text-sm-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Embryo viewer</div>
                    </div>

                    <div class="col-9 align-middle text-sm-left">
                        <c:if test="${gene.embryoDataAvailable}">
                            <a id="embryoViewerBtn" href="${cmsBaseUrl}/embryoviewer/?mgi=${acc}" class="page-nav-link"
                               style="font-size: initial; display: inline;">3D Imaging</a>
                        </c:if>
                        <c:if test="${not gene.embryoDataAvailable}">
                            N/A
                        </c:if>
                    </div>
                </div>

                <div class="row no-gutters mb-2 mb-sm-0">
                    <div class="col-3 align-middle text-sm-right pr-1">
                        <div class="align-middle font-weight-bold pr-2">Other links</div>
                    </div>
                    <div class="col-9 align-middle text-sm-left">
                        <a target="_blank" rel="noopener" class="page-nav-link"
                           href="http://www.informatics.jax.org/marker/${gene.mgiAccessionId}"
                           title="See gene page at JAX" style="font-size: initial; display: inline;">MGI &nbsp;<i
                                class="fas fa-external-link"></i></a>
                        <a target="_blank" rel="noopener" class="page-nav-link"
                           href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.mgiAccessionId}"
                           title="Visualise mouse gene with ensembl genome broswer"
                           style="font-size: initial; display: inline;">Ensembl &nbsp;<i
                                class="fas fa-external-link"></i></a>
                        <c:if test="${gene.isUmassGene}">
                            <a target="_blank" rel="noopener" class="page-nav-link"
                               href="http://blogs.umass.edu/jmager/${gene.markerSymbol}"
                               title="Visualise mouse gene with ensembl genome broswer"
                               style="font-size: initial; display: inline;">Early Embryo Phenotypes &nbsp;<i
                                    class="fas fa-external-link"></i></a>
                        </c:if>
                    </div>
                </div>

                <div class="row no-gutters justify-content-around mt-3 text-center page-content">
                    <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                        <a href="#phenotypesTab" class="col" onclick="$('#significant-tab').trigger('click')">
                            <i class="fal fa-file-medical-alt mb-1 text-dark page-nav-link-icon"></i>
                            <span class="page-nav-link">Significant phenotypes (${rowsForPhenotypeTable.size()})</span>
                        </a>
                    </c:if>
                    <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                        <span class="col">
                            <i class="fal fa-file-medical-alt mb-1 text-muted page-nav-link-icon"></i>
                            <span class="page-nav-link-muted text-muted">Significant phenotypes (0)</span>
                        </span>
                    </c:if>
                    <c:if test='${measurementsChartNumber > 0}'>
                        <a href="#phenotypesTab" class="col" onclick="$('#alldatachart-tab').trigger('click')">
                            <i class="fal fa-chart-scatter mb-1 text-dark page-nav-link-icon"></i>
                            <span class="page-nav-link">Measurements chart (${measurementsChartNumber})</span>
                        </a>
                    </c:if>
                    <c:if test='${measurementsChartNumber <= 0}'>
                        <span class="col">
                            <i class="fal fa-chart-scatter mb-1 text-muted page-nav-link-icon"></i>
                            <span class="page-nav-link-muted text-muted">Measurements chart (0)</span>
                        </span>
                    </c:if>
                    <c:if test='${allMeasurementsNumber > 0}'>
                        <a href="#phenotypesTab" class="col" onclick="$('#alldatatable-tab').trigger('click')">
                            <i class="fal fa-ruler-combined mb-1 text-dark page-nav-link-icon"></i>
                            <span class="page-nav-link">All data table (${allMeasurementsNumber})</span>
                        </a>
                    </c:if>
                    <c:if test='${allMeasurementsNumber <= 0}'>
                        <span class="col">
                            <i class="fal fa-ruler-combined mb-1 text-muted page-nav-link-icon"></i>
                            <span class="page-nav-link-muted text-muted">All data table (0)</span>
                        </span>
                    </c:if>
                </div>

                <div class="row no-gutters justify-content-around mt-3 text-center page-content">
                    <c:if test="${not empty impcAdultExpressionImageFacetsWholemount
                                                  or not empty impcAdultExpressionImageFacetsSection
                                                  or not empty expressionAnatomyToRow
                                                  or not empty impcEmbryoExpressionImageFacets
                                                  or not empty embryoExpressionAnatomyToRow}">
                        <a href="#expression" class="col">
                            <i class="fal fa-images mb-1 text-dark page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link">Expression & images (${expressionAnatomyToRow.size() + embryoExpressionAnatomyToRow.size()})</span>
                        </a>
                    </c:if>
                    <c:if test="${empty impcAdultExpressionImageFacetsWholemount
                                                  and empty impcAdultExpressionImageFacetsSection
                                                  and empty expressionAnatomyToRow
                                                  and empty impcEmbryoExpressionImageFacets
                                                  and empty embryoExpressionAnatomyToRow}">
                        <span class="col">
                            <i class="fal fa-images mb-1 text-muted page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link-muted text-muted">Expression & images (0)</span>
                        </span>
                    </c:if>
                    <c:if test='${hasModelsByOrthology or hasModelAssociations}'>
                        <a href="#diseases" class="col">
                            <i class="fal fa-procedures mb-1 text-dark page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link">Disease models (<span id="diseaseModelTotal">0</span>)</span>
                        </a>
                    </c:if>

                    <c:if test='${not hasModelsByOrthology and not hasModelAssociations}'>
                        <span class="col">
                            <i class="fal fa-procedures mb-1 text-muted page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link-muted text-muted">Disease models (0)</span>
                        </span>
                    </c:if>
                    <c:if test='${rowsForHistopathTable.size() > 0 or hasHistopath}'>
                        <a href="#histopath" class="col">
                            <i class="fal fa-microscope mb-1 text-dark page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link">Histopathology (${rowsForHistopathTable.size()})</span>
                        </a>
                    </c:if>
                    <c:if test='${orderRows.size() > 0}'>
                        <a href="#order" class="col">
                            <i class="fal fa-shopping-cart mb-1 text-dark page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link">Order (${orderRows.size()})</span>
                        </a>
                    </c:if>
                    <c:if test='${orderRows.size() <= 0}'>
                        <span class="col">
                            <i class="fal fa-shopping-cart mb-1 text-muted page-nav-link-icon" data-toggle="tooltip"
                               data-placement="top"></i>
                            <span class="page-nav-link-muted text-muted">Order (0)</span>
                        </span>
                    </c:if>
                </div>
            </div>

            <c:if test="${ attemptRegistered && !phenotypeStarted }">
                <div class="alert alert-info mt-5">
                    <h5>Registered for phenotyping</h5>
                    <p>Phenotyping is planned for a knockout strain of this gene but data is not currently
                        available.</p>
                </div>
            </c:if>

            <c:if test="${!attemptRegistered and allMeasurementsNumber <= 0}">
                <div class="alert alert-info mt-5">
                    <h5>Not currently registered for phenotyping</h5>
                    <p>Phenotyping is currently not planned for a knockout strain of this gene.</p>
                </div>
            </c:if>
        </div>

        <div class="col-lg-4">
            <jsp:include page="phenotype_icons_frag.jsp"/>
            <c:if test="${ bodyWeight }">
                <div id="all_data" class="with-label text-center">
                    <c:if test="${bodyWeight}">
                        <a id="bodyWeightBtn" class="btn btn-primary mt-4"
                           href="${baseUrl}/charts?accession=${acc}&parameter_stable_id=IMPC_BWT_008_001&procedure_stable_id=IMPC_BWT_001&chart_type=TIME_SERIES_LINE"
                           title="Body Weight Curves" style="display: inline-block; max-width: 300px; width: 80%;">View
                            body weight measurements</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>


    <div id="phenotypes"></div>
    <!-- Empty anchor for links, used for disease paper. Don't remove. -->


    <c:if test='${notsignificantTopLevelMpGroups.size() > 0 || rowsForPhenotypeTable.size() > 0 || allMeasurementsNumber > 0}'>


        <ul class="nav nav-tabs" id="phenotypesTab" role="tablist">
            <li class="nav-item">
                <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                    <a class="nav-link active" id="significant-tab" data-toggle="tab" href="#significant"
                       role="tab" aria-controls="significant-tab" aria-selected="true"><i
                            class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes
                        (<span id="significantCount">${rowsForPhenotypeTable.size()}</span>/${rowsForPhenotypeTable.size()})</a>
                </c:if>
                <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                    <a class="nav-link" id="significant-tab" data-toggle="tab" href="#significant"
                       role="tab" aria-controls="significant-tab" aria-selected="false"><i
                            class="fal fa-file-medical-alt"></i>&nbsp; Significant phenotypes (0)</a>
                </c:if>
            </li>
            <li class="nav-item">
                <a class="nav-link${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber > 0) ? ' active' : ''}"
                   id="alldatachart-tab"
                   data-toggle="tab" href="#alldatachart"
                   role="tab" aria-controls="alldatachart-tab"
                   aria-selected="${rowsForPhenotypeTable.size() <= 0 ? 'true' : 'false'}"><i
                        class="fal fa-chart-scatter"></i>&nbsp;
                    Measurements chart (<span
                            id="allDataChartCount">${measurementsChartNumber}</span>/${measurementsChartNumber})</a>
            </li>
            <li class="nav-item">
                <a class="nav-link${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber <= 0)? ' active' : ''}"
                   id="alldatatable-tab"
                   data-toggle="tab" href="#alldatatable"
                   role="tab" aria-controls="alldatatable-tab"
                   aria-selected="${rowsForPhenotypeTable.size() <= 0 ? 'true' : 'false'}"><i
                        class="fal fa-ruler-combined"></i>&nbsp;
                    All data table (<span
                            id="allDataTableCount">${allMeasurementsNumber}</span>/${allMeasurementsNumber})</a>
                <c:if test='${allMeasurementsNumber <= 0}'>
                    <a class="nav-link active" id="alldatatable-tab" data-toggle="tab" href="#alldatatable"
                       role="tab" aria-controls="alldatatable-tab" aria-selected="false"><i
                            class="fal fa-ruler-combined"></i>&nbsp;
                        All data table (0)</a>
                </c:if>
            </li>
        </ul>

        <div class="tab-content" id="phenotypesTabContent" id="phenotypeAssociations">
            <c:if test='${rowsForPhenotypeTable.size() <= 0}'>
                <div class="tab-pane fade" id="significant" role="tabpanel" aria-labelledby="significant-tab">
                    <c:if test="${ attemptRegistered && phenotypeStarted }">
                        <div class="alert alert-warning mt-3" role="alert">
                            No results meet the p-value threshold
                        </div>
                    </c:if>
                </div>
            </c:if>
            <!-- Associations table -->
            <c:if test='${rowsForPhenotypeTable.size() > 0}'>
                <div class="tab-pane fade show active" id="significant" role="tabpanel"
                     aria-labelledby="significant-tab">
                    <div id="phenotypeTableDiv" class="inner-division">
                        <div class="row">
                            <div class="container p-0 p-md-2">
                                <div class="row" id="phenotypesDiv">
                                    <div class="container p-0 p-md-2">
                                        <c:if test="${not empty rowsForPhenotypeTable}">
                                            <div class="row">
                                                <div class="col" id="target" action="destination.html">

                                                        <%--c:forEach
                                                                var="phenoFacet" items="${phenoFacets}"
                                                                varStatus="phenoFacetStatus">
                                                            <select id="top_level_mp_term_name" class="selectpicker"
                                                                    multiple="multiple"
                                                                    title="Filter on ${phenoFacet.key}">
                                                                <c:forEach
                                                                        var="facet" items="${phenoFacet.value}">
                                                                    <option>${facet.key}</option>
                                                                </c:forEach>
                                                            </select>
                                                        </c:forEach--%>

                                                </div>
                                            </div>


                                            <c:set var="count" value="0" scope="page"/>

                                            <c:forEach var="phenotype" items="${rowsForPhenotypeTable}"
                                                       varStatus="status">
                                                <c:forEach var="sex" items="${phenotype.sexes}">
                                                    <c:set var="count" value="${count + 1}" scope="page"/>
                                                </c:forEach>
                                            </c:forEach>

                                            <jsp:include page="PhenoFrag.jsp"></jsp:include>

                                            <div id="export">
                                                <p class="textright">
                                                    Download data as:
                                                    <a id="tsvDownload"
                                                       href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=tsv&fileName=${gene.markerSymbol}"
                                                       target="_blank" class="btn btn-outline-primary"><i
                                                            class="fa fa-download"></i>&nbsp;TSV</a>
                                                    <a id="xlsDownload"
                                                       href="${baseUrl}/genes/export/${gene.getMgiAccessionId()}?fileType=xls&fileName=${gene.markerSymbol}"
                                                       target="_blank" class="btn btn-outline-primary"><i
                                                            class="fa fa-download"></i>&nbsp;XLS</a>
                                                </p>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div><!-- end of div for mini section line -->
                </div>
            </c:if>

            <c:if test='${measurementsChartNumber <= 0}'>
                <div class="tab-pane fade" id="alldatachart" role="tabpanel" aria-labelledby="alldatachart-tab">
                    <div class="alert alert-warning mt-3" role="alert">
                        No results to display
                    </div>
                </div>
            </c:if>

            <c:if test='${measurementsChartNumber > 0}'>
                <div class="tab-pane fade show ${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber > 0)? ' active' : ''}"
                     id="alldatachart" role="tabpanel" aria-labelledby="alldatachart-tab">
                    <div class="mt-3 selector-container">
                        Select physiological systems to view:
                        <select class="selectpicker" multiple data-selected-text-format="count"
                                onchange="filterAllData('Chart')" id="systemSelectorChart">
                            <optgroup label="Significant">
                                <c:forEach var="i" begin="0" end="20">
                                    <c:if test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                        <option title="1 item selected"
                                                value="${significantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|significant|${phenotypeGroupIcons[i]}"
                                                data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                    </c:if>
                                </c:forEach>
                            </optgroup>
                            <optgroup label="Not significant">
                                <c:forEach var="i" begin="0" end="20">
                                    <c:if test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                        <option title="1 item selected"
                                                value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|nonsignificant|${phenotypeGroupIcons[i]}"
                                                data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                    </c:if>
                                </c:forEach>
                            </optgroup>
                        </select>
                    </div>
                    <div class="filters-container">
                        Viewing: <span id="phChartDataTitle"> all phenotypes</span>
                    </div>
                    <div id="all-chart">
                    </div>
                </div>
            </c:if>

            <c:if test='${allMeasurementsNumber <= 0}'>
                <div class="tab-pane fade" id="alldatatable" role="tabpanel" aria-labelledby="alldatatable-tab">
                    <div class="alert alert-warning mt-3" role="alert">
                        No measurements to display
                    </div>
                </div>
            </c:if>

            <c:if test='${allMeasurementsNumber > 0}'>
                <div class="tab-pane fade show ${(rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber <= 0)? ' active' : ''}"
                     id="alldatatable" role="tabpanel" aria-labelledby="alldatatable-tab">
                    <c:if test='${(significantTopLevelMpGroups.size() > 0 || notsignificantTopLevelMpGroups.size() > 0)}'>
                        <div class="mt-3 selector-container">
                            Select physiological systems to view:
                            <select class="selectpicker" multiple data-selected-text-format="count"
                                    onchange="filterAllData('Table')" id="systemSelectorTable">
                                <optgroup label="Significant">
                                    <c:forEach var="i" begin="0" end="20">
                                        <c:if test="${not empty significantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                            <option title="1 item selected"
                                                    value="${significantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|significant|${phenotypeGroupIcons[i]}"
                                                    data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                        </c:if>
                                    </c:forEach>
                                </optgroup>
                                <optgroup label="Not significant">
                                    <c:forEach var="i" begin="0" end="20">
                                        <c:if test="${not empty notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}">
                                            <option title="1 item selected"
                                                    value="${notsignificantTopLevelMpGroups.get(phenotypeGroups[i])}|${phenotypeGroups[i]}|nonsignificant|${phenotypeGroupIcons[i]}"
                                                    data-icon="${phenotypeGroupIcons[i]}">${fn:replace(phenotypeGroups[i], 'phenotype', '')}</option>
                                        </c:if>
                                    </c:forEach>
                                </optgroup>
                            </select>
                        </div>
                        <div class="filters-container">
                            Viewing: <span id="phTableDataTitle"> all phenotypes</span>
                        </div>
                    </c:if>
                    <div id="all-table">
                    </div>
                </div>
            </c:if>
        </div>
    </c:if>
</div>

<script>


    var firstChart = true;
    var firstTable = true;
    var currentView = 'chart';
    var placeholderText = "<div class=\"pre-content\">\n" +
        "    <div class=\"row no-gutters\">\n" +
        "        <div class=\"col-12 my-5\">\n" +
        "            <p class=\"h4 text-center text-justify\"><i class=\"fas fa-atom fa-spin\"></i> A moment please while we gather the data . . . .</p>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>";

    function chartClick() {
        if (firstChart) {
            $('#all-chart').html(placeholderText);
            $.ajax({
                url: baseUrl + '/experimentsChartFrag?geneAccession=' + '${gene.mgiAccessionId}',
                type: 'GET',
                success: function (data) {
                    $('#all-chart').html(data);
                    firstChart = false;
                }
            });
        }
    }

    function tableClick() {
        if (firstTable) {
            $('#all-table').html(placeholderText);
            $.ajax({
                url: baseUrl + '/experimentsTableFrag?geneAccession=' + '${gene.mgiAccessionId}',
                type: 'GET',
                success: function (data) {
                    $('#all-table').html(data);
                    firstTable = false;
                }
            });
        }
    }

    if (window.location.hash == '#alldatatable') {
        tableClick();
        $("#alldatatable-tab").click();
    }

    <c:if test='${measurementsChartNumber > 0}'>$("#alldatachart-tab").on('click', chartClick);
    </c:if>
    <c:if test='${allMeasurementsNumber > 0}'>$("#alldatatable-tab").on('click', tableClick);
    </c:if>

    <c:if test='${rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber > 0}'>
    $(document).ready(chartClick);
    </c:if>
    <c:if test='${rowsForPhenotypeTable.size() <= 0 && measurementsChartNumber <= 0 && allMeasurementsNumber > 0}'>
    $(document).ready(tableClick);
    </c:if>

</script>
