<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<script>
    var resTemp = document.getElementsByClassName("resultCount");
    if (resTemp.length > 1) {
        resTemp[0].remove();
    }

    function sortString(sortName, sortOrder, data) {
        var order = sortOrder === 'desc' ? -1 : 1;
        data.sort(function (a, b) {
            var aa = sortName === 6 ? parseFloat(a['_' + sortName + '_data']['value']) || 0.0 : a['_' + sortName + '_data']['value'];
            var bb = sortName === 6 ? parseFloat(b['_' + sortName + '_data']['value']) || 0.0 : b['_' + sortName + '_data']['value'];
            if (aa < bb) {
                return order * -1
            }
            if (aa > bb) {
                return order
            }
            return 0
        })
    }

</script>

<div id="phTable">

    <div class="btn-group btn-group-toggle" data-toggle="buttons" id="stage-selector">
        <label class="btn btn-outline-primary btn-sm active">
            <input type="radio" name="options" id="all" autocomplete="off" checked> All
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="options" id="embryo" autocomplete="off"> Embryo
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="options" id="early" autocomplete="off"> Early adult
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="options" id="middle" autocomplete="off"> Adult
        </label>
        <label class="btn btn-outline-primary btn-sm">
            <input type="radio" name="options" id="late" autocomplete="off"> Late adult
        </label>
    </div>

    <table id="strainPvalues" data-toggle="table" data-pagination="true" data-mobile-responsive="true"
           data-sortable="true" style="margin-top: 10px;"
           data-show-search-clear-button="true" data-search="true" data-toolbar="#stage-selector"
           data-row-style="rowStyler"
    >
        <thead>
        <tr>
            <th data-sortable="true" data-field="allele_symbol" data-formatter="formatAllele">Allele</th>
            <th data-sortable="true" data-field="phenotyping_center">Center</th>
            <th data-sortable="true" data-field="procedure_name" data-sorter="procedureSorter"
                data-formatter="formatProcedureParameter">Procedure /
                Parameter
            </th>
            <th data-formatter="formatMutants">Mutants</th>
            <th data-sortable="true" data-field="life_stage">Life stage</th>
            <th data-sortable="true" data-field="p_value" data-formatter="formatPvalue">P Value</th>
            <th data-sortable="true" data-field="status">Status</th>
        </tr>
        </thead>
    </table>
</div>

<script type="text/javascript">
    var firstDTLoad = true;
    var optionToLifeStage = {
        early: 'Early adult',
        late: 'Late adult',
        middle: 'Middle aged adult'
    };
    var zygosityMap = {
        'HET': 'heterozygote',
        'HOM': 'homozygote'
    }
    $(document).ready(function () {
        var allData = JSON.parse('${allData}');
        allData.forEach(function (row) {
            row.evidence_link = buildLink(row);
        })

        $('#allDataTableCount').html(${rows});
        if (firstDTLoad) {
            $("#strainPvalues").bootstrapTable({
                data: allData,
                onSearch: function (event) {
                    $('#allDataTableCount').html($("#strainPvalues").bootstrapTable('getData').length);
                },
                onClickRow: function (row) {
                    if (row.evidence_link) window.open(row.evidence_link);
                }
            });
            firstDTLoad = false;
        }
        $("#stage-selector :input").change(function () {
                var option = this.id;
                var selectedLifeStage = '';
                if (option != 'all') {
                    if (option != 'embryo') {
                        $("#strainPvalues").bootstrapTable('filterBy', {life_stage: optionToLifeStage[option]}, {'filterAlgorithm': 'and'});
                    } else {
                        $("#strainPvalues").bootstrapTable('filterBy', {life_stage: 'embryo'}, {
                            'filterAlgorithm': function (row, filters) {
                                return !!row.life_stage.match(/E\d+\.\d+/);
                            }
                        });
                    }
                } else {
                    $("#strainPvalues").bootstrapTable('filterBy', {}, {'filterAlgorithm': 'and'});
                }
                $('#allDataTableCount').html($("#strainPvalues").bootstrapTable('getData').length);
            }
        );

        var url_string = window.location.href;
        var url = new URL(url_string);
        var stage = url.searchParams.get("dataLifeStage");
        if (stage) $('#' + stage).click();

        var searchValue = url.searchParams.get("dataSearch");
        if (searchValue) {
            $("#strainPvalues").bootstrapTable('resetSearch', searchValue);
        }


    });

    function formatProcedureParameter(value, row) {
        return row['procedure_name'] + ' / ' + row['parameter_name'];
    }

    function formatAllele(value) {
        var geneSymbol = value.split('<')[0];
        var sup = value.split('<')[1].split('>')[0];
        return geneSymbol + '<sup>' + sup + '</sup>';
    }

    function formatMutants(value, row) {
        return row['zygosity'] + ' ' + row['female_mutants'] + 'f:' + row['male_mutants'] + 'm';
    }

    function sortTable(sortName, sortOrder, data) {
        console.log(sortName, sortOrder, data);
    }

    function procedureSorter(a, b, rowA, rowB) {
        return formatProcedureParameter(null, rowA) < formatProcedureParameter(null, rowB) ? 1 : -1;
    }

    function formatPvalue(value) {
        if (value) {
            var formatted = value.toExponential(2);
            var base = formatted.split('e')[0];
            var exp = formatted.split('e')[1];
            if (exp < -1) {
                value = base + '&#215;10<sup>' + exp + '</sup>';
            } else {
                value = value.toFixed(2);
            }
        }
        return value;
    }

    function buildLink(row) {
        var baseUrl = "${baseUrl}";
        var link = null;
        if(row.procedure_name.startsWith("Histopathology")) {
            link = baseUrl + '/histopath/' + row.gene_accession_id;

            if(row.phenotype_term) {
                var term = row.phenotype_term.split('-')[0];
                link = link + '?anatomy="' + term + '"';
            }
        } else if(row.procedure_name.startsWith("Gross Pathology and Tissue Collectio")) {
            link = baseUrl + '/grosspath/' + row.gene_accession_id + '/' + row.parameter_stable_id;
        } else {
            link =  baseUrl + "/charts?accession=" + row.gene_accession_id;
            link += "&allele_accession_id=" + row.allele_accession_id;
            link += "&parameter_stable_id=" + row.parameter_stable_id;
            if(zygosityMap[row.zygosity]) {
                link += "&zygosity=" + zygosityMap[row.zygosity];
            }
            link += "&phenotyping_center=" + row.phenotyping_center;
            link += "&pipeline_stable_id=" + row.pipeline_stable_id;
        }

        return link;
    }

    function rowStyler(row) {
        var cssClass = row.evidence_link ? 'clickableRow' : 'unClickableRow';
        return {
            classes: [cssClass]
        };
    }
</script>
