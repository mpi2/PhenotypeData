$(document).ready(function(){						
	
//console.log('comparator js ready');
//console.log('window.location='+window.location);
//mediaBaseUrl=https://dev.mousephenotype.org/data/media
//var mediaBaseUrl='http://wwwdev.ebi.ac.uk/mi/media/omero/';
//var solrUrl='//www.ebi.ac.uk/mi/impc/beta/solr';
//var omero_gateway_root="//www.ebi.ac.uk/mi/media/omero/webgateway";
var solrUrl='//wwwdev.ebi.ac.uk/mi/impc/dev/solr';
var omero_gateway_root="//wwwdev.ebi.ac.uk/mi/media/omero/webgateway";
var backTo;//where should the back button point
if(window.location.href.indexOf('beta') > -1){
	solrUrl='//www.ebi.ac.uk/mi/impc/beta/solr';
	omero_gateway_root="//www.ebi.ac.uk/mi/media/omero/webgateway";
}
if(window.location.href.indexOf('www.mousephenotype.org') > -1 || window.location.href.indexOf('ves-oy-d8') > -1 || window.location.href.indexOf('ves-pg-d8') > -1 || window.location.href.indexOf('ves-hx-d8') > -1){
	solrUrl='//www.ebi.ac.uk/mi/impc/solr';
	omero_gateway_root="//www.ebi.ac.uk/mi/media/omero/webgateway";
}

var detailUrlExt='/img_detail/';
var url=omero_gateway_root+detailUrlExt;//may need for this to be passed as a parameter for each request if not being set by jsp?
var annotationBreak='<br/>';
console.log('solrUrl='+solrUrl);
//get all the ids from the parameter list and get solrDocs for each
function getURLParameter(sParam, location)
{
	 location = location || window.parent.location;
    var sPageURL =location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    var imgIds=[];
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == 'omero_gateway_root') //set the default omero url to be this
        {
        	//console.log('setting omero_gateway_root='+sParameterName[1]);
        	omero_gateway_root=sParameterName[1];
        }
        if (sParameterName[0] == sParam) 
        {
            imgIds.push(sParameterName[1]);
        }
    }
    return imgIds;
}

var mediaType=getURLParameter('mediaType', window.location);
console.log('mediaType='+mediaType+'|');
//get whether we are the subframe for control or experimental from the arg passed to the page from the imageComparator frame src attribute
var controlOrExp=getURLParameter('controlOrExp', window.location);
//console.log('location='+window.location);
//console.log('string='+getURLParameter('controlOrExp', window.location));
//if(location.pathname.substring(1).search('Control')>0){
//	controlOrExp='control';
//};

//console.log('control or exp='+controlOrExp);

//console.log('ctrImgId='+getURLParameter('ctrImgId'));
//console.log('expImgId='+getURLParameter('expImgId'));
var ids=[];
if(controlOrExp=='experimental'){
	ids=getURLParameter('expImgId', window.parent.location);
}else{
	ids=getURLParameter('ctrImgId', window.parent.location);
}

//make a solr request for these ids
//console.log('ids='+ids);
if(ids.length!=0){//only search for info related to ids if we have them.

//console.log('solrUrl='+solrUrl);
var thisSolrUrl = solrUrl + '/impc_images/select';
var joinedIds=ids.join(" OR ");
var paramStr = 'q=omero_id:(' +joinedIds + ')&wt=json&defType=edismax&qf=auto_suggest&rows=100000';
if(typeof mediaType != 'undefined' && mediaType[0]==='pdf'){
	
		paramStr+='&fq=full_resolution_file_path:*.pdf';
	
}else{
		paramStr+='&fq=-full_resolution_file_path:*.pdf';//make sure we don't get pdfs back when we want images
}
var docs;
var i = 0;
var len=0;
	$.ajax({
	    'url': thisSolrUrl,
	    'data': paramStr,
	    'dataType': 'jsonp',
	    'jsonp': 'json.wrf',
	    'success': function(json) {
	        //console.log(json.response.numFound + ' images');
	        docs=json.response.docs;
	      //loop over solrDocs and split into control and experimental list
	        //console.log(docs);
	        //
	       //docs=["http://ves-ebi-cf/omero/webgateway/img_detail/5818/", "http://ves-ebi-cf/omero/webgateway/img_detail/5819/"];
	        len = docs.length;
	        var frame = $('#'+controlOrExp, window.parent.document);
	        //var experimentalFrame = $('#experimental', window.parent.document);
	        
	        //initialise navigation to first image annotations
	        doc=docs[0];
	        displayDocAnnotations(doc, frame, mediaType);
	        //console.log('mediaBaseUrl='+mediaBaseUrl);
	        $('#next').click(function(){
	        		//console.log('nextControl clicked');
	        		var doc=docs[++i % len];
//	        		frame.attr('src', url+doc.omero_id);
//	        		$('#annotations').html(getAnnoataionsDisplayString(doc));
	        		displayDocAnnotations(doc, frame, mediaType);
	        	});
	
	        $('#prev').click(function(){
	        	//console.log('nextControl clicked');
	        	var doc=docs[--i % len];
	        	displayDocAnnotations(doc, frame, mediaType);
//	        	frame.attr('src', url+doc.omero_id);
//	        	$('#annotations').html(getAnnoataionsDisplayString(doc));
	        });
	       // if(doc.gene_accession_id){
	        backTo='../imagePicker/'+doc.gene_accession_id+'/'+doc.parameter_stable_id;
	        if(typeof mediaType != 'undefined' && mediaType[0]==='pdf')backTo+='?mediaType=pdf';
	        if(doc.biological_sample_group === 'experimental'){//only add to experiment pane otherwise we don't know the gene page to go back to
	        $("#back").addClass("btn").html("back to image picker");
	        }
	       // }
	        $('#back').click(function(){
	        	//console.log('nextControl clicked');
	        	goBack();
//	        	frame.attr('src', url+doc.omero_id);
//	        	$('#annotations').html(getAnnoataionsDisplayString(doc));
	        });
	
	        
	    }
	});
	

}else{//else we don't have ids so display an error to the user
		//console.log('no ids for '+controlOrExp);
		var frame = $('#'+controlOrExp, window.parent.document);
		if(controlOrExp=='experimental'){
			frame.attr('src', 'experimental_images_error.html');
		}else{
			frame.attr('src', 'control_images_error.html');
		}
}
	
	
function displayDocAnnotations(doc, frame, mediaType){
	console.log('mediatype in displayDocAnnotations='+mediaType+'|');
	
	if(typeof mediaType != 'undefined' && mediaType[0]==='pdf'){
		console.log('mediaType is pdf in display annotation='+mediaType+'|');
		//wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/8128
		var protocol=window.parent.location.protocol;
		var pdfUrl=doc.download_url.replace('//', protocol+'//');//replace with http or https depending on the protocol from js url.
		//http://wwwdev.ebi.ac.uk/mi/media/omero/webclient/annotation/8128
		//console.log("pdfUrl="+pdfUrl);
		//console.log(window.parent.location.protocol);
		var pdfDetailView=protocol+"//docs.google.com/gview?url="+pdfUrl+"&embedded=true";
		console.log('pdfDetailView='+pdfDetailView)
		frame.attr('src',pdfDetailView);//get the jpeg url and change it to a img_detail view but idea is we get the correct context from the solr we are pointing at. so no need to pass it as a parameter
		
	}else{
		var imgDetailView=doc.jpeg_url.replace('render_image', 'img_detail').replace('http://','//');
		console.log('imgDetailView='+imgDetailView)
		frame.attr('src', imgDetailView);//get the jpeg url and change it to a img_detail view but idea is we get the correct context from the solr we are pointing at. so no need to pass it as a parameter
	}
	//frame.attr('src','http://omeroweb.jax.org/omero/webgateway/img_detail/7541/?c=1%7C0:255$FF0000,2%7C0:255$00FF00,3%7C0:255$0000FF&m=c&p=normal&ia=0&q=0.9&zm=6.25&t=1&z=1&x=50824&y=19576');
	$('#annotations').html(getAnnotationsDisplayString(doc));
	//label+=i+1+'/'+len;
	var count=i % len+1;
	$('#image_counter').html('&nbsp;'+count+'/'+len+'&nbsp;');
}
function getAnnotationsDisplayString(doc){
	//filename removed from display but here for debug if needed
	var filename=doc.full_resolution_file_path.substring(doc.full_resolution_file_path.lastIndexOf("/")+1, doc.full_resolution_file_path.length);
	var label= "";
	if(doc.external_sample_id){
		label+=doc.external_sample_id+annotationBreak;;
	}
	if(doc.biological_sample_group === 'experimental'){
		label+=doc.zygosity+annotationBreak+superscriptSymbol(doc.allele_symbol)+annotationBreak;
	}else{
		label+="Wild Type"+annotationBreak;
	}
	if(doc.sex){
		label+=doc.sex+annotationBreak;
	}
	if(doc.parameter_association_name){
		label+=doc.parameter_association_name+annotationBreak+doc.parameter_association_value+annotationBreak;
	}
	
	if(doc.download_url){
		if(mediaType[0] !== 'pdf'){
		label+="<a target='_blank' href='"+doc.jpeg_url+"'>"+"jpeg</a>"+annotationBreak;
		}
		label+="<a href='"+doc.download_url+"'>"+"download original</a>"+annotationBreak;
	}
	
	if(doc.parameter_name){
		label+=doc.parameter_name+annotationBreak;
		
	}
	
	if(doc.procedure_name){
		label+=doc.procedure_name+annotationBreak;
		
	}
	if(doc.pipeline_name){
		label+=doc.pipeline_name+annotationBreak;
		
	}

	if(doc.date_of_experiment){
		label+=doc.date_of_experiment+annotationBreak;
	}
	
	
	return label;
}
//setInterval(function () {
//	console.log('changing image');
//    iframe.attr('src', locations[++i % len]);
//}, 3000);
function superscriptSymbol(allele){
	console.log('allele='+allele);
	var newString=allele.replace('<', '££');
	newString=newString.replace('>', '##');
	newString=newString.replace('££','<sup>');
	newString=newString.replace('##','</sup>');
	return newString;
}

function goBack() {
    console.log('backTo='+backTo);
    window.parent.location=backTo;
}

});
