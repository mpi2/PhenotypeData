$(document).ready(function(){						
		
	var previousControlId;
	var previousMutantId;

	var viewport = $.WeblitzViewport($("#viewport"), impcMediaBaseUrl, {
        'mediaroot': omeroStaticUrl
    });
	 //viewport.load(87269);
	
	  var temp=$(".thumb").first();
	    var id=temp.attr('data-id');
	$('#control_annotation').text(temp.attr('oldtitle'));
    if(id){
    	id = parseInt(id);
    	console.log('loading id='+id);
    	viewport.load(id);
    	previousControlId=id;
    }
    temp.toggleClass( "img_selected");
    
    
	/* Prepare right viewport */
	var viewport2 = $.WeblitzViewport($("#viewport2"), impcMediaBaseUrl, {
    'mediaroot': omeroStaticUrl
	});
	//viewport2.load(87043);
    /* Load the selected image into the viewport */
	    var temp2=$(".thumb2").first();
	    var id2=temp2.attr('data-id');

	    	$('#mutant_annotation').text(temp2.attr('oldtitle'));
	    	if(id2){
	    		id2 = parseInt(id2);
	    		//viewport2.load(87043);
	    		//id2=87043;
	    		console.log('loading id2='+id2);
	    		viewport2.load(id2);
	    		previousMutantId=id2;
	    	}
	    	temp2.toggleClass( "img_selected");
    
    
    
    
    
    
    $(".thumb").click(function(){
    	console.log('click');
        var iid = $(this).attr('data-id');
       
		
			viewport.load(iid);
		
		if(previousControlId){
		$('#'+previousControlId).toggleClass( "img_selected");
		}
		
        $('#'+this.id).toggleClass( "img_selected");
		previousControlId=this.id;
		id=this.id;
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
  	  id2=this.id;
  	  //change the text under the main image to be the same as the title
  	  $('#mutant_annotation').text($(this).attr('oldtitle'));
  	//viewport2.initialise();
    });
    
    
  $("#mutant_only_button").click(function() {
	  console.log('click on mutant only');
	  $('#control_box').toggle('no_box');//need to set this to display none instead of hidden which still tatkes up space
	  $('#mutant_box').toggleClass('half_box_right full_box');
	  $('#viewport2').toggleClass('full_frame');
	  $('.thumbList').toggleClass('full_frame');
	  $('.mutant_box').toggleClass('full_frame');
	  if($('#mutant_only_button').text() === 'Display Mutant Only'){
		  $('#mutant_only_button').text('Display WT and Mutant');
	  }else{
		  $('#mutant_only_button').text('Display Mutant Only');
	  }
	  console.log('mutant: '+$('#mutant_only_button').attr('value'));
	  viewport2.refresh();
	  //$('#mutant_only_button').toggleAttr('value','full_frame');
	});
    
  $("#overlap").click(function() {
	  console.log('calling overlap');
	  window.open(baseUrl+'/overlap?acc='+acc+'&id1='+id+'&id2='+id2, 'Overlap');
	  //need to change the classes for the two images to overlap them and add the js to move the top one around.
	  //change mutant_box to the resizable class
//	  $('#mutant_box').toggleClass('half_box_right resizable');
//	  $( "#resizable" ).draggable({ cursor: "crosshair"});
//	    $("#resizable").resizable({
//	        aspectRatio: true
//	    });
//	    
//	    $("#reset").click(function () {
//			  console.log('calling reset');
//			    $("#resizable").animate({
//			        top: "20px",
//			        left: "20px"
//			    });
//			});
	  
  });
console.log('comparator.js ready');


});
