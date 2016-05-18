$(document).ready(function(){						
	
console.log('comparator.js ready');


var previousControlId;
var previousMutantId;

$(".clickable_image_control").click(function() {
  console.log( this.id );
  if(previousControlId){
	  $('#'+previousControlId).toggleClass( "img_selected");
  }
  $('#control_frame').attr('src','http://www.ebi.ac.uk/mi/media/omero/webgateway/img_detail/'+this.id+'/');
  $('#'+this.id).toggleClass( "img_selected");
  previousControlId=this.id;
});

$(".clickable_image_mutant").click(function() {
	  console.log( this.id );
	  if(previousMutantId){
		  $('#'+previousMutantId).toggleClass( "img_selected");
	  }
	  $('#mutant_frame').attr('src','http://www.ebi.ac.uk/mi/media/omero/webgateway/img_detail/'+this.id+'/');
	  $('#'+this.id).toggleClass( "img_selected");
	  previousMutantId=this.id;
	});

});
