jQuery(document).ready(function ($) {

    $('#checkbox').change(function () {
        setInterval(function () {
            moveRight();
        }, 3000);
    });

    var slideCount = $('#slider ul li').length;
    var slideWidth = $('#slider ul li').width();
    var slideHeight = $('#slider ul li').height();
    var sliderUlWidth = slideCount * slideWidth;
    var sliderControlWidth = $('#sliderControl ul li').length * ($('#sliderControl ul li').width() + 10);

    $('#slider').css({width: slideWidth, height: slideHeight});
    $('#slider ul').css({width: sliderUlWidth, marginLeft: -slideWidth});
    $('#sliderControl ul li:first-child').clone().appendTo($('#sliderHighlight'));
    moveToPosition(0);
    $('.slider img').wrap('<a href="' + $('#sliderHighlight').attr('imgUrl') + '"> </a>');

    $("#sliderControl ul").css({width: sliderControlWidth}); // Set width of gallery scroll

    function moveLeft() {
        var index = $("#sliderOnDisplay").attr("index");
        if (index > 0) {
            moveToPosition(index - 1);
        } else {
            moveToPosition($("#sliderControl ul li").length - 1);
        }
    };

    function moveRight() {
        var index = $("#sliderOnDisplay").attr("index");
        if (index < $("#sliderControl ul li").length - 1) {
            index++;
            moveToPosition(index);
        } else {
            moveToPosition(0);
        }
    };

    function moveToPosition(pos) {

        $('#sliderHighlight').animate({
            left: -slideWidth
        }, 0, function () {
            $('#sliderHighlight li').replaceWith($('#item' + pos).clone().attr("id", "sliderOnDisplay").attr("index", pos));
            $('#sliderHighlight li').css('left', '');
            $('#sliderHighlight li').prepend("<h2 class='sliderTitle'>" + $('#item' + pos + ' p.sliderTitle').html() + "</h2>");
            $('.sliderSelectedControl').removeClass("sliderSelectedControl");
            $('#item' + pos).addClass("sliderSelectedControl");
        });
    };

    $('.control_prev').click(function () {
        moveLeft();
    });

    $('.control_next').click(function () {
        moveRight();
    });

    $('#sliderControl ul li').click(function () {
        var index = $(this).index();
        moveToPosition(index);
    })

});