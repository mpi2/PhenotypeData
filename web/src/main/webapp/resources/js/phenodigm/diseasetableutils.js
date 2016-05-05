/* Formatting function for row details*/
function makeChildRow(clicked) {

    var targetRowId = $(clicked).attr("targetRowId");
    var targetRow = $('#' + targetRowId);
    var geneId = $(clicked).attr("geneid");
    var diseaseId = $(clicked).attr("diseaseid");
    var requestPageType = $(clicked).attr("requestpagetype");

    return $(document.createElement('div'))
        .addClass("inner")
        .attr({
            id: targetRowId,
            geneId: geneId,
            diseaseId: diseaseId,
            requestPageType: requestPageType
        }).css({"padding": "0"});
}

function insertDiseaseAssociations(clicked) {

    var targetRowId = $(clicked).attr("targetRowId");
    var targetRow = $('#' + targetRowId);
    var geneId = $(clicked).attr("geneId");
    var diseaseId = $(clicked).attr("diseaseId");
    var requestPageType = $(clicked).attr("requestPageType");

    var uri = baseUrl + '/phenodigm/diseaseGeneAssociations';

    $.get(uri, {
        geneId: geneId,
        diseaseId: diseaseId,
        requestPageType: requestPageType
    }, function (response) {
        //add the response html to the target row
        $(targetRow).remove('#loadingPlaceholder').html(response);
    });
};

function insertPhenogrid(clicked) {

    var targetRowId = $(clicked).attr("targetRowId");
    var targetRow = $('#' + targetRowId);
    var geneId = $(clicked).attr("geneId");
    var diseaseId = $(clicked).attr("diseaseId");
    var requestPageType = $(clicked).attr("requestPageType");

    var gridColumnWidth = 25;
    var gridRowHeight= 50;

    getPhenoGridSkeleton(geneId, diseaseId, requestPageType).done(function (result) {
        Phenogrid.createPhenogridForElement(targetRow, {
            //monarchUrl is a global variable provided via Spring from application.properties
            serverURL: monarchUrl,
            selectedSort: "Frequency and Rarity", // sort method of sources: "Alphabetic", "Frequency and Rarity", "Frequency,
            gridSkeletonDataVendor: 'IMPC',
            gridSkeletonData: result,
            singleTargetModeTargetLengthLimit: gridColumnWidth,
            sourceLengthLimit: gridRowHeight
        });
    });
    $(targetRow).remove('#loadingPlaceholder');
};

function getPhenoGridSkeleton(geneId, diseaseId, requestPageType) {
    return $.getJSON(baseUrl + '/phenodigm/phenogrid',
        {geneId: geneId, diseaseId: diseaseId, requestPageType: requestPageType}
    );
}

/* Adds on-click functionality to the table to insert and show/hide a child row
 for the row clicked.*/
$.fn.addTableClickCallbackHandler = function (tableId, table) {
    console.log(tableId);
    $(tableId + ' tbody').on('click', 'tr', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);

        if (row.child.isShown()) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
            tr.find("td#toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");
        }
        else {
            // Open this row
            row.child(makeChildRow(tr)).show();
            //row.child(insertDiseaseAssociations(tr)).show();
            row.child(insertPhenogrid(tr)).show();
            tr.addClass('shown');
            tr.find("td#toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
        }
    });
};