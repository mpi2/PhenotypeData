<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">${allParameters} chart for ${gene.markerSymbol}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; Statistics &raquo; <a
            href='${baseUrl}/genes/${gene.mgiAccessionId}'>${gene.markerSymbol} blah</a></jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
    <jsp:attribute name="header">
			<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>

            <style>
                .table-borderless > tbody > tr > td,
                .table-borderless > tbody > tr > th,
                .table-borderless > tfoot > tr > td,
                .table-borderless > tfoot > tr > th,
                .table-borderless > thead > tr > td,
                .table-borderless > thead > tr > th {
                    border: none !important;
                }

                .border-3 {
                    border-width:3px !important;
                }
            </style>

        </jsp:attribute>

    <jsp:attribute name="addToFooter">
            <script>

                var getData = function (i, id, url) {
                    $.ajax({
                        url: url,
                        cache: true,
                        tryCount: 0,
                        retryLimit: 3,
                        success: function (html) {
                            $('body').dequeue();
                            $('#' + id).append(html);
                            $('#spinner' + (i + 1)).remove();
                        },
                        error: function (jqXHR, textStatus) {
                            $('body').dequeue();
                            if (textStatus === 'timeout') {
                                console.log(textStatus);
                                this.tryCount++;
                                if (this.tryCount <= this.retryLimit) {
                                    $.ajax(this);
                                    return;
                                }
                                return;
                            }
                            if (jqXHR.status === 500) {
                                console.log('There was an error');
                            } else {
                                console.log('There was an error');
                            }
                        }
                    });
                };

                // Enqueue all the charts to be displayed on this page.
                // the getData() function will render the charts one-by-one
                // in order to not overwhelm the server if there are many
                // charts on one page.
                $(document).ready(function () {
                    $('.chart').each(function (i, obj) {
                        var id = $(this).attr('id');
                        var url = $(this).attr('graphUrl') + '&experimentNumber=' + id;
                        $('body').queue(function() {
                            getData(i, id, url);
                        });
                    });
                });
            </script>

        </jsp:attribute>

    <jsp:body>
<div class="container data-heading">
            <div class="row">
                <div class="col-12 no-gutters">
                    <h2 class="mb-0">${gene.markerSymbol} data chart</h2>
                </div>
            </div>
        </div>

        <c:if test="${statsError}">
            <div class="alert alert-error">
                <strong>Error:</strong> An issue occurred processing the statistics for this page - results on this page
                maybe incorrect.
            </div>
        </c:if>

        <c:forEach var="graphUrl" items="${allGraphUrlSet}" varStatus="graphUrlLoop">

            <div class="chart container white-bg-small pb-5" id="chart${graphUrlLoop.count}" graphUrl="${baseUrl}/chart?${graphUrl}" id="divChart_${graphUrlLoop.count}">
                <div id="spinner${graphUrlLoop.count}" class="row pb-5">
                    <div class="col-12 col-md-12">
                        <div class="pre-content clear-bg">
                            <div class="page-content pt-3 pb-5">
                                <div class="container p-0 p-md-2">
                                    <div class="row justify-content-center pb-1">
                                        <p class="h4 text-center text-justify"><i class="fas fa-atom fa-spin"></i> A moment please while we gather the data . . . .</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </c:forEach>

        <c:if test="${param.parameter_stable_id != 'IMPC_VIA_001_001' && param.parameter_stable_id != 'IMPC_EVL_001_001' && param.parameter_stable_id != 'IMPC_EVM_001_001' && param.parameter_stable_id != 'IMPC_EVO_001_001' && param.parameter_stable_id != 'IMPC_EVP_001_001'}"><!-- only show downloads if not viability or embryo viability pie charts as we don't have download for line level params yet -->
            <div class="container" id="expression">
                <div class="row">
                    <div class="col-12 no-gutters">
                        <h3><i class="fa fa-download"></i>&nbsp;Download all the data</h3>
                    </div>
                </div>
            </div>
            <div class="container white-bg-small">
                <div class="row pb-5">
                    <div class="col-12 col-md-12">
                        <div class="pre-content clear-bg">
                            <div class="page-content people py-5 white-bg">
                                <div class="container p-md-2">
                                    <div class="row pb-3">
                                <div id="exportIconsDivGlobal"></div>
                                    </div>
                                    <div class="row pb-3 alert alert-warning">
                                        <p>NOTE: Data from all charts will be aggregated into one download file.</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <script>

            $(document)
                .ready(function () {
                    //		alert("unidimensional");
                    var background = getBackground();

                    $('div#exportIconsDivGlobal').html("");
                    $('div#exportIconsDivGlobal').html(
                        $.fn.loadFileExporterUI({
                            label: 'Export data as: ',
                            textPos: "textleft",
                            formatSelector: {
                                TSV: 'tsv_phenoAssoc',
                                XLS: 'xls_phenoAssoc'
                            },
                            class: 'btn btn-outline-primary fileIcon'
                        }));

                    var params = window.location.href.split("/charts?")[1]; //.split("&");
                    var paramList = window.location.href.split("/charts?")[1].split("&");
                    var windowLocation = window.location;
                    var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;
                    var paramIdList = [];
                    var mgiGeneId = [];
                    var phenotypingCenter = [];
                    var strains = [];
                    var zygosity = [];
                    var pipelineStableId = [];
                    var allele = [];
                    for (var k = 0; k < paramList.length; k++) {
                        if (paramList[k].indexOf("parameter_stable_id") >= 0) {
                            paramIdList.push(paramList[k].replace("parameter_stable_id=", ""));
                        }
                        else if (paramList[k].indexOf("allele_accession_id") >= 0) {
                            allele.push(paramList[k].replace("allele_accession_id=", ""));
                        }
                        else if (paramList[k].indexOf("accession") >= 0) {
                            mgiGeneId.push(paramList[k].replace("accession=", ""));
                        }
                        else if (paramList[k].indexOf("phenotyping_center") >= 0) {
                            phenotypingCenter.push(paramList[k].replace("phenotyping_center=", ""));
                        }
                        else if (paramList[k].indexOf("strain") >= 0) {
                            strains.push(paramList[k].replace("strain=", ""));
                        }
                        else if (paramList[k].indexOf("zygosity") >= 0) {
                            zygosity.push(paramList[k].replace("zygosity=", ""));
                        }
                        else if (paramList[k].indexOf("pipeline_stable_id") >= 0) {
                            pipelineStableId.push(paramList[k].replace(/pipeline_stable_id=/, ""));
                        }
                    }

                    initFileExporter();

                    function initFileExporter() {
                        var conf = {
                            mgiGeneId: mgiGeneId,
                            externalDbId: 3,
                            fileName: 'graphDataDump_'
                            + mgiGeneId[0].replace(/:/g, '_'),
                            solrCoreName: 'experiment',
                            dumpMode: 'all',
                            baseUrl: windowLocation,
                            parameterStableId: paramIdList,
                            zygosity: zygosity,
                            sex: sex,
                            allele_accession_id: allele,
                            strains: strains,
                            phenotypingCenter: phenotypingCenter,
                            pipelineStableId: pipelineStableId,
                            page: "unidimensionalData",
                            gridFields: '',
                            params: ""
                        };

                        var exportObj = buildExportUrl(conf);               // Build the export url, page url, and form strings.
                        $('div#exportIconsDivGlobal').attr("data-exporturl", exportObj.exportUrl);    // Initialize the url.
                        // WARNING NOTE: FILTER CHANGES DO NOT UPDATE data-exporturl; THUS, THE data-exporturl VALUE WILL BE OUT-OF-SYNC SHOULD
                        // THE USER CHANGE FILTERS. THIS WILL LIKELY RESULT IN A HARD-TO-FIND BUG.
                        // RECOMMENDATION: ANY FILTER CHANGES SHOULD TRIGGER AN UPDATE OF THE data-exporturl.

                        $('button.fileIcon').click(function () {
                            var exportObj = buildExportUrl(conf, $(this).text());                       // Build the export url, page url, and form strings.
                            $('div#exportIconsDiv').attr("data-exporturl", exportObj.exportUrl);        // Update the url in case the filters changed.
                            _doDataExport(exportObj.url, exportObj.form);
                        });
                    }

                    function buildExportUrl(conf, fileType) {
                        if (fileType === undefined)
                            fileType = '';
                        console.log(fileType);
                        fileType = fileType.indexOf('TSV') >= 0 ? 'tsv' : 'xls';
                        var url = baseUrl + '/export';
                        var sInputs = '';
                        for (var k in conf) {
                            sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";
                        }
                        sInputs += "<input type='text' name='fileType' value='"
                            + fileType
                                .toLowerCase()
                            + "'>";
                        var form = $("<form action='" + url + "' method=get>"
                            + sInputs + "</form>");

                        var exportUrl = url + '?' + $(form).serialize();

                        var retVal = new Object();
                        retVal.url = url;
                        retVal.form = form;
                        retVal.exportUrl = exportUrl;
                        return retVal;
                    }

                    // Returns an array of string containing the background.
                    function getBackground() {
                        var background = $('div.chart p').text();
                        var m = $('div.chart h2.title').text();
                        m.toString();
                        //      background = background.replace('Background - involves: ','');
                        //      var space = background.indexOf('&nbsp;');
                        //      background = background.substr(0, space - 1);

                        var h2 = $('#section-associations').text();
                        h2.toString();


                        var retVal = [];
                        retVal.push(background);
                        return retVal;
                    }

                    function _doDataExport(url, form) {
                        console.log('hi');
                        $(form).appendTo('body').submit().remove();
                    }
                });
        </script>
    </jsp:body>

</t:genericpage>

