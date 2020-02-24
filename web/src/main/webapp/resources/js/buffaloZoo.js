'use strict';

$(document).ready(function() {


    $(".page-template-help #menu-item-13").addClass("current-menu-item");
    $("#menu-main-nav .about-impc, #about-menu").on('mouseenter', function (e) {
        $("#about-menu").stop().slideDown()
        $('#menu-main-nav .about-impc').addClass('active');
    });
    $("#menu-main-nav .about-impc, #about-menu").mouseleave(function () {
        $("#about-menu").stop().slideUp()
        $('#menu-main-nav .about-impc a').removeClass('active');
    });
    var aboutOpen = false;
    $("#menu-main-nav .about-impc a").on('touchstart', function (e) {
        if (!aboutOpen) {
            $("#about-menu").stop().slideDown()
            $('#menu-main-nav .about-impc a').addClass('active');
            aboutOpen = true;
        } else {
            $("#about-menu").stop().slideUp()
            $('#menu-main-nav .about-impc a').removeClass('active');
            aboutOpen = false;
        }
        e.preventDefault();
    })
    $("#menu-main-nav .data, #data-menu").mouseenter(function () {
        $("#data-menu").stop().slideDown()
        $('#menu-main-nav .data a').addClass('active');
    });
    $("#menu-main-nav .data, #data-menu").mouseleave(function () {
        $("#data-menu").stop().slideUp()
        $('#menu-main-nav .data a').removeClass('active');
    });
    var dataOpen = false;
    $("#menu-main-nav .data a").on('touchstart', function (e) {
        if (!dataOpen) {
            $("#data-menu").stop().slideDown()
            $('#menu-main-nav .data a').addClass('active');
            dataOpen = true;
        } else {
            $("#data-menu").stop().slideUp()
            $('#menu-main-nav .data a').removeClass('active');
            dataOpen = false;
        }
        e.preventDefault();
    });
    $("#menu-main-nav .publications, #publications-menu").mouseenter(function () {
        $("#publications-menu").stop().slideDown()
        $('#menu-main-nav .publications a').addClass('active');
    });
    $("#menu-main-nav .publications, #publications-menu").mouseleave(function () {
        $("#publications-menu").stop().slideUp()
        $('#menu-main-nav .publications a').removeClass('active');
    });
    var pubOpen = false;
    $("#menu-main-nav .publications a").on('touchstart', function (e) {
        if (!pubOpen) {
            $("#publications-menu").stop().slideDown()
            $('#menu-main-nav .publications a').addClass('active');
            pubOpen = true;
        } else {
            $("#publications-menu").stop().slideUp()
            $('#menu-main-nav .publications a').removeClass('active');
            pubOpen = false;
        }
        e.preventDefault();
    });

});
