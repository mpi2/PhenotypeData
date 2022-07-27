$(document).ready(function () {

    var previousControlId = $(".clickable_image_control").first().attr('id');
    var previousMutantId = $(".clickable_image_mutant").first().attr('id');

    $(".clickable_image_control").click(function () {

        if (previousControlId) {
            $('#' + previousControlId).parent().parent().toggleClass("img_selected");
        }

        if ($(this).attr('data-type') === "pdf") {
            $('#control_frame').attr('src', googlePdf.replace('replace', pdfWithoutId + '/' + this.id));
        } else if ($(this).attr('data-imageLink')) {
            $('#control_frame').attr('src', $(this).attr('data-imageLink').replace('http:', 'https:'));
        } else {
            $('#control_frame').attr('src', jpegUrlDetailWithoutId + '/' + this.id);
        }

        $('#' + this.id).parent().parent().toggleClass("img_selected");
        previousControlId = this.id;
    });

    $(".clickable_image_mutant").click(function () {

        if (previousMutantId) {
            $('#' + previousMutantId).parent().parent().toggleClass("img_selected");
        }
        if ($(this).attr('data-type') === "pdf") {
            $('#mutant_frame').attr('src', googlePdf.replace('replace', pdfWithoutId + '/' + this.id));
        } else if ($(this).attr('data-imageLink')) {
            $('#mutant_frame').attr('src', $(this).attr('data-imageLink').replace('http:', 'https:'));
        } else {
            $('#mutant_frame').attr('src', jpegUrlDetailWithoutId + '/' + this.id);
        }
        $('#' + this.id).parent().parent().toggleClass("img_selected");
        previousMutantId = this.id;

    });

    $(".clickable_image_control").first().click();
    $(".clickable_image_mutant").first().click();

});