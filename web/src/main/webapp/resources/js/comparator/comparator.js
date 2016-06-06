$(document).ready(function(){						
	
	 /* Prepare the left viewport */
    var viewport = $.WeblitzViewport($("#viewport"), "https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/", {
        'mediaroot': "https://wwwdev.ebi.ac.uk/mi/media/static/"
    });
    /* Load the selected image into the viewport */
    var temp=$(".thumb").first();
    var id=temp.attr('data-id');
    $('#control_annotation').text(temp.attr('oldtitle'));
    if(id){
    	viewport.load(id);
    	var previousControlId=id;
    }
    temp.toggleClass( "img_selected");
    
    


    // Alternative for testing non-big image viewer
    // viewport = $.WeblitzViewport($("#viewport"), "https://learning.openmicroscopy.org/dundee/webgateway/", {
    //         'mediaroot': "https://learning.openmicroscopy.org/dundee/static/"
    //     });
    // viewport.load(1296);


    /* Prepare right viewport */
    var viewport2 = $.WeblitzViewport($("#viewport2"), "https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/", {
        'mediaroot': "https://wwwdev.ebi.ac.uk/mi/media/static/"
    });
    /* Load the selected image into the viewport */
    var temp2=$(".thumb2").first();
    var id2=temp2.attr('data-id');
    $('#mutant_annotation').text(temp2.attr('oldtitle'));
    if(id2){
    	viewport2.load(id2);
    	var previousMutantId=id2;
    }
    temp2.toggleClass( "img_selected");

    $(".thumb").click(function(){
    	console.log('click');
        var iid = $(this).attr('data-id');
        //iid = parseInt(iid);
//        console.log('control id='+iid);
//        console.log('veiwport found='+$('#viewport-img-tiles').attr('class'));
//        $("#viewport").empty();
//        var viewport = $.WeblitzViewport($("#viewport"), "https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/", {
//            'mediaroot': "https://wwwdev.ebi.ac.uk/mi/media/static/"
//        });
        viewport.load(iid);
		if(previousControlId){
		$('#'+previousControlId).toggleClass( "img_selected");
		}
		
        $('#'+this.id).toggleClass( "img_selected");
		previousControlId=this.id;
		$('#control_annotation').text($(this).attr('oldtitle'));
    });

    $(".thumb2").click(function(){
        var iid = $(this).attr('data-id');
        iid = parseInt(iid);
        console.log('mutant id='+iid);
        viewport2.load(iid);
        
  	  if(previousMutantId){
		  $('#'+previousMutantId).toggleClass( "img_selected");
	  }
  	  $('#'+this.id).toggleClass( "img_selected");
  	  previousMutantId=this.id;
  	  //change the text under the main image to be the same as the title
  	  $('#mutant_annotation').text($(this).attr('oldtitle'));
    });
    
    
    
console.log('comparator.js ready');

//
//var previousControlId=$(".clickable_image_control").first().attr('id');
//console.log('control prev'+previousControlId)
//var previousMutantId=$(".clickable_image_mutant").first().attr('id');
//
//
//console.log('ready...2');
///* Prepare the viewport */
//var viewport = $.WeblitzViewport($("#control_frame"), "https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/", {
//    'mediaroot': "https://wwwdev.ebi.ac.uk/mi/media/static/"
//});
//viewport.load(87269);
//
//$(".clickable_image_control").click(function() {
//		console.log( this.id );
//		if(previousControlId){
//			$('#'+previousControlId).toggleClass( "img_selected");
//		}
//		if(this.src.indexOf('_pdf')>-1){
//			$('#control_frame').attr('src',googlePdf.replace('replace',pdfWithoutId+'/'+this.id));
//		}else{
//			$('#control_frame').attr('src',jpegUrlDetailWithoutId+'/'+this.id);
//		}
//		
//		$('#'+this.id).toggleClass( "img_selected");
//		previousControlId=this.id;
//		$('#control_annotation').text($(this).attr('oldtitle'));
//});
//
//$(".clickable_image_mutant").click(function() {
//	  console.log('title='+ $(this).attr('oldtitle'));
//	  if(previousMutantId){
//		  $('#'+previousMutantId).toggleClass( "img_selected");
//	  }
//	  if(this.src.indexOf('_pdf')>-1){
//		  $('#mutant_frame').attr('src',googlePdf.replace('replace',pdfWithoutId+'/'+this.id));//replace the placeholder string with the id string.
//	  }else
//	 {
//		  $('#mutant_frame').attr('src',jpegUrlDetailWithoutId+'/'+this.id);
//	 }
//	  $('#'+this.id).toggleClass( "img_selected");
//	  previousMutantId=this.id;
//	  //change the text under the main image to be the same as the title
//	  $('#mutant_annotation').text($(this).attr('oldtitle'));
//	  
//	  
//	});
//
//$("#mutant_only_button").click(function() {
//	  console.log('click on mutant only');
//	  $('#control_box').toggle('no_box');//need to set this to display none instead of hidden which still tatkes up space
//	  $('#mutant_box').toggleClass('half_box_right full_box');
//	  $('#mutant_frame').toggleClass('full_frame');
//	  if($('#mutant_only_button').text() === 'Display Mutant Only'){
//		  $('#mutant_only_button').text('Display WT and Mutant');
//	  }else{
//		  $('#mutant_only_button').text('Display Mutant Only');
//	  }
//	  console.log('mutant: '+$('#mutant_only_button').attr('value'));
//	  //$('#mutant_only_button').toggleAttr('value','full_frame');
//	});

});
