       jQuery(document).ready(function ($) {

        	  $('#checkbox').change(function(){
        	    setInterval(function () {
        	        moveRight();
        	    }, 3000);
        	  });
        	  
        		var slideCount = $('#slider ul li').length;
        		var slideWidth = $('#slider ul li').width();
        		var slideHeight = $('#slider ul li').height();
        		var sliderUlWidth = slideCount * slideWidth;
        		
        		$('#slider').css({ width: slideWidth, height: slideHeight });
        		$('#sliderDiv').append("<p id=\"sliderControl\" class=\"sliderControl\" > All images</p>");
        		$( "#slider ul" ).clone().appendTo( "#sliderControl" );
        		$('#slider ul').css({ width: sliderUlWidth, marginLeft: - slideWidth });
        		
        	    $('#slider ul li:last-child').prependTo('#slider ul');

        	    function moveLeft() {
        	        $('#slider ul').animate({
        	            left: + slideWidth
        	        }, 500, function () {
        	            $('#slider ul li:last-child').prependTo('#slider ul');
        	            $('#slider ul').css('left', '');
        	        });
        	    };

        	    function moveRight() {
        	        $('#slider ul').animate({
        	            left: - slideWidth
        	        }, 500, function () {
        	            $('#slider ul li:first-child').appendTo('#slider ul');
        	            $('#slider ul').css('left', '');
        	        });
        	    };

        	    $('.control_prev').click(function () {
        	        moveLeft();
        	    });

        	    $('.control_next').click(function () {
        	        moveRight();
        	    });

        	});   