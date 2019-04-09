'use strict';

$(document).ready(function () {

    $("#menu-item-16, #about-menu").on('mouseenter', function (e) {
        $("#about-menu").stop().slideDown()
        $('#menu-item-16 a').addClass('active');
    });

    $("#menu-item-16, #about-menu").mouseleave(function () {
        $("#about-menu").stop().slideUp()
        $('#menu-item-16 a').removeClass('active');
    });

    var aboutOpen = false;
    $("#menu-item-16 a").on('touchstart', function (e) {
        if (!aboutOpen) {
            $("#about-menu").stop().slideDown()
            $('#menu-item-16 a').addClass('active');
            aboutOpen = true;
        }
        else {
            $("#about-menu").stop().slideUp()
            $('#menu-item-16 a').removeClass('active');
            aboutOpen = false;
        }
        e.preventDefault();
    })


    $("#menu-item-17, #data-menu").mouseenter(function () {
        $("#data-menu").stop().slideDown()
        $('#menu-item-17 a').addClass('active');
    });

    $("#menu-item-17, #data-menu").mouseleave(function () {
        $("#data-menu").stop().slideUp()
        $('#menu-item-17 a').removeClass('active');
    });

    var dataOpen = false;
    $("#menu-item-17 a").on('touchstart', function (e) {
        if (!dataOpen) {
            $("#data-menu").stop().slideDown()
            $('#menu-item-17 a').addClass('active');
            dataOpen = true;
        }
        else {
            $("#data-menu").stop().slideUp()
            $('#menu-item-17 a').removeClass('active');
            dataOpen = false;
        }
        e.preventDefault();
    })

    $("#menu-item-19, #news-menu").mouseenter(function () {
        $("#news-menu").stop().slideDown()
        $('#menu-item-19 a').addClass('active');
    });

    $("#menu-item-19, #news-menu").mouseleave(function () {
        $("#news-menu").stop().slideUp()
        $('#menu-item-19 a').removeClass('active');
    });

    var newsOpen = false;
    $("#menu-item-19 a").on('touchstart', function (e) {
        if (!newsOpen) {
            $("#news-menu").stop().slideDown()
            $('#menu-item-19 a').addClass('active');
            newsOpen = true;
        }
        else {
            $("#news-menu").stop().slideUp()
            $('#menu-item-19 a').removeClass('active');
            newsOpen = false;
        }

        e.preventDefault();
    })

//    $('.portalTab').on('click', function (e) {
//    	console.log('calling BZjs .portalTab');
//        e.preventDefault();
//        $('.portalTab').removeClass('active');
//        $(this).addClass('active');
//        //not on search page
//        if ($(this).hasClass('portalTabSearchPage')) {
//            return;
//        }
//        $('#searchType').val($(this).data("type"));
//    });

    var searchOn = false;
    $('#searchButton').on('click', function (e) {
    	console.log('calling BZjs searchButton');
        if (!searchOn) {
            $('.click-guard').css('visibility', 'visible').hide().fadeIn('slow');
            $(this).addClass('active');
            $('#search-icon-open').fadeOut();
            $('#search-icon-close').fadeIn(function () {
                $('#searchField').focus();
            });

            searchOn = true;
            return;
        }
        $('.click-guard').fadeOut('slow', function () {
            $(this).hide().css('visibility', 'hidden');
        })
        $('#search-icon-open').fadeIn();
        $('#search-icon-close').fadeOut();
        $(this).removeClass('active');
        searchOn = false;
        return;
    });

});


