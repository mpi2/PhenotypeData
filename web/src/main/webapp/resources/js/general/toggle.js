/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * toggle: used for the image dropdown.
 * 
 */
jQuery(document).ready(	function() {

	function GetURLParameter(sParam)
	{
	    var sPageURL = window.location.search.substring(1);
	    var sURLVariables = sPageURL.split('&');
	    for (var i = 0; i < sURLVariables.length; i++)
	    {
	        var sParameterName = sURLVariables[i].split('=');
	        if (sParameterName[0] == sParam)
	        {
	            return sParameterName[1];
	        }
	    }
	}
	
	    var type = GetURLParameter('type');
	    console.log('type='+type);
	    if(type==='gene'){
	    	console.log('gene is default so do nothing');
	    	$('#geneSearchTab').addClass('active');
	    	$('#phenotypeSearchTab').removeClass('active');
	    }else if(type==='phenotype'){
	    	console.log('phenotype type on load so change style of tabs to match');
	    	$('#phenotypeSearchTab').addClass('active');
	    	$('#geneSearchTab').removeClass('active');
	    }
	    

	
	$( "#geneSearchTab" ).click(function() {
		  console.log( "gene search tab clicked" );
		  $('#searchType').val('gene');
		  $( "#searchForm" ).submit();
		  //BZ submits on clicking on a tab should we do that? probably yes...
		  //note possible conflict in buffaloZoo.js $('.portalTab').on('click',
		});
	
	$( "#phenotypeSearchTab" ).click(function() {
		  console.log( "phenotype search tab clicked" );
		  $('#searchType').val('phenotype');
		  $( "#searchForm" ).submit();
		});
// 18-July-2018 (mrelac) - commented out this function, as it hijacks a.interest and processes it using old, incorrect Harwell rules.


// 	$('a.interest').click(function(){
// 		var termId = $(this).attr('id');
// 		var endpoint = null;
//
// 	     if ( /^MP:/.exec(termId) ){
// 	     	endpoint = "/togglempflagfromjs/";
// 	     }
// 	     else if ( /^MGI:/.exec(termId) ){
// 	     	endpoint = "/toggleflagfromjs/";
// 	     }
//
//
// 		var label = $(this).text();
// 		var regBtn = $(this);
// 		$.ajax({
// 			url: endpoint + termId,
// 			success: function (response) {
// 			function endsWith(str, suffix) {
// 				return str.indexOf(suffix, str.length - suffix.length) !== -1;
//         	}
// 			if(response === 'null') {
// 				window.alert('Null error trying to register interest');
// 			}
// 			else {
// 				if( label == 'Register interest' ) {
// 					regBtn.text('Unregister interest');
// 				}
// 				else {
// 					regBtn.text('Register interest');
// 				}
// 			}
//         },
//         error: function () {
//         	console.log('error on registering interest');
//         }
//     });
// 	return false;
// });


});
