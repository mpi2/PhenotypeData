$(document).ready(function () {

    var previousControlId;
    var previousMutantId;

    var viewport = $.WeblitzViewport($("#viewport"), impcMediaBaseUrl, {
        'mediaroot': omeroStaticUrl
    });

    var temp = $(".thumb").first();
    var id = temp.attr('data-id');
    $('#control_annotation').text(temp.attr('oldtitle'));
    if (id) {
        id = parseInt(id);
        viewport.load(id);
        previousControlId = id;
    }
    temp.parent().parent().toggleClass("img_selected");


    /* Prepare right viewport */
    var viewport2 = $.WeblitzViewport($("#viewport2"), impcMediaBaseUrl, {
        'mediaroot': omeroStaticUrl
    });
    /* Load the selected image into the viewport */
    var temp2 = $(".thumb2").first();
    var id2 = temp2.attr('data-id');

    $('#mutant_annotation').text(temp2.attr('oldtitle'));
    if (id2) {
        id2 = parseInt(id2);
        viewport2.load(id2);
        previousMutantId = id2;
    }
    temp2.parent().parent().toggleClass("img_selected");


    $(".thumb").click(function () {
        var iid = $(this).attr('data-id');
        viewport.load(iid);

        if (previousControlId) {
            $('#' + previousControlId).parent().parent().toggleClass("img_selected");
        }

        $('#' + this.id).parent().parent().toggleClass("img_selected");
        previousControlId = this.id;
        id = this.id;
        $('#control_annotation').text($(this).attr('oldtitle'));
    });

    $(".thumb2").click(function () {
        var iid = $(this).attr('data-id');
        iid = parseInt(iid);
        viewport2.load(iid);

        if (previousMutantId) {
            $('#' + previousMutantId).parent().parent().toggleClass("img_selected");
        }
        $('#' + this.id).parent().parent().toggleClass("img_selected");
        previousMutantId = this.id;
        id2 = this.id;
        //change the text under the main image to be the same as the title
        $('#mutant_annotation').text($(this).attr('oldtitle'));
    });

});
