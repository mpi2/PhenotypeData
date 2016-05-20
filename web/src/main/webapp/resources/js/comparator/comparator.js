$(document).ready(function(){						
	
console.log('comparator.js ready');


var previousControlId=$(".clickable_image_control").first().attr('id');
console.log('control prev'+previousControlId)
var previousMutantId=$(".clickable_image_mutant").first().attr('id');

$(".clickable_image_control").click(function() {
		console.log( this.id );
		if(previousControlId){
			$('#'+previousControlId).toggleClass( "img_selected");
		}
		if(this.src.indexOf('_pdf')>-1){
			$('#control_frame').attr('src',googlePdf.replace('replace',pdfWithoutId+'/'+this.id));
		}else{
			$('#control_frame').attr('src',jpegUrlDetailWithoutId+'/'+this.id);
		}
		
		$('#'+this.id).toggleClass( "img_selected");
		previousControlId=this.id;
});

$(".clickable_image_mutant").click(function() {
	  console.log( this.id );
	  if(previousMutantId){
		  $('#'+previousMutantId).toggleClass( "img_selected");
	  }
	  if(this.src.indexOf('_pdf')>-1){
		  $('#mutant_frame').attr('src',googlePdf.replace('replace',pdfWithoutId+'/'+this.id));
	  }else
	 {
		  $('#mutant_frame').attr('src',jpegUrlDetailWithoutId+'/'+this.id);
	 }
	  $('#'+this.id).toggleClass( "img_selected");
	  previousMutantId=this.id;
	});

});
