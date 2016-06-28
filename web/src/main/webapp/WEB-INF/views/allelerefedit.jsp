
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>


	<jsp:attribute name="title">IMPC allele paper references</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Allele references</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">
	
		<script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>
		<script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>  

		<style type="text/css">
			h1#top {
				margin: 20px 0 50px 0;
			}
			div#alleleRef_filter {
				float: left;
			}
			form#allele {
				position: absolute;
				top: 220px;
				width: auto;
				font-size: 12px;
				padding: 0 0 0 5px;
				background-color: #f2f2f2;
				display: none;
			}
			input[type=password] {
       			width: 100px;     
   			} 
   			div#butt {
   				margin: 15px 0;
   			}
   			button.edit {
   				color: white;
   				background-color: #993333;
			    border-radius: 8px;

   			}
   			div#tableTool {
   				position: absolute;
   				top: 180px;
   				right: 20px;
   				
   			}
   			table.dataTable span.highlight {
			  background-color: yellow;
			  font-weight: bold;
			  color: black;
			}
			form#pmidbox{
				/*padding: 0;*/
				/*width: 200px;*/
				display: none;
				margin-bottom: 20px;
				background-color: #F2F2F2;
			}
			form.showpmidbox {
				display: block;
			}
			form#pmidbox legend {
				font-size: 12px;
			}

			
		</style>
	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
		<div class="region region-pinned">
	       
	    </div>		
	
	</jsp:attribute>

    <jsp:body>		
				
		<div class="region region-content">
			<div class="block block-system">
				<div class='content'>
				<div class="node node-gene">
					<h1 class="title" id="top">IMPC allele references curation</h1>
				
					<div id='formBox'>
						<span></span>
						<form id='allele'>
	                      Enter passcode to switch to Edit mode: <input size='10' type='password' name='passcode'>
	                    </form>
                    </div>
                    <div id='butt'><button class='login'>Edit</button>
	                    <%--<a><i class='fa fa-question-circle fa paperEdit'></i></a>--%>
                    </div>


					<%-- inputbox to add pmid --%>
					<form id="pmidbox">
						<fieldset>
							<legend>Add paper containing EUCOMM/KOMP allele(s) by pmid.<br>Separate by comma for multiple papers.</legend>
							<textarea></textarea>
							<input type="submit" value="Submit papers for curation"/>
							<input type="reset" value="Clear">
						</fieldset>
					</form>

					<!-- container to display dataTable -->									
					<div class="HomepageTable" id="alleleRef"></div>	
				</div>				
				</div>
			</div>
		</div>		       

        <script type='text/javascript'>
        
        $(document).ready(function(){
   			'use strict';

//	        addPaperFormJs();

   			//var baseUrl = '//dev.mousephenotype.org/data';
   			//var baseUrl = 'http://localhost:8080/phenotype-archive';
   			var baseUrl = "${baseUrl}";
   			var solrUrl = "${internalSolrUrl};";

			$('button[class=login]').click(function(){
				if ( ! $(this).hasClass('edit') ) {
					$('#formBox span').text("");
					if ( $('form#allele').is(":visible") ){
						$('form#allele').hide();
					}
					else {
						$('formBox span').text("");
						$('form#allele').show();
					}

				}
				else {
					$(this).removeClass('edit').text('Edit');
					$('#formBox span').text("You are now out of editing mode...");

					$('form#pmidbox').hide();
					var oTable = $('table#alleleRef').dataTable();
        			oTable.fnStandingRedraw();
				}
        	});

	        var oConf = {};
	        oConf.doAlleleRef = true;
	        oConf.iDisplayLength = 10;
	        oConf.iDisplayStart = 0;
	        oConf.editMode = false;

	        var tableHeader = "<thead><th>False-positive</th><th>Reviewed</th><th>Allele symbol</th><th>PMID</th><th>Date of publication</th><th>Grant id (Grant agency)</th><th>Paper link</th></thead>";
	        var tableCols = 7;

	        var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef");
	        $('div#alleleRef').append(dTable);

	        fetchAlleleRefDataTable(oConf);

			$('form#allele').submit(function(){

				var passcode = $('form input[type=password]').val();
              	$.ajax({
              		method: "post",
                	url: baseUrl + "/alleleRefLogin?passcode="+passcode,
                	success: function(response) {
                		// verifying passcode
                		// boolean response
                		if ( response ){
                			$('button').addClass('edit').text("Stop editing");

                			$('form#allele').hide();
                			$('#formBox span').text("You are now in editing mode...");

			                $('form#pmidbox').show();

			                addPaperFormJs();

							oConf.editMode = true;
							var oTable = $('table#alleleRef').dataTable();
							oTable.fnStandingRedraw();

			      		}
                		else {
                			alert("Passcode incorrect. Please try again");
                		}
                	},
              	 	error: function() {
                     window.alert('AJAX error trying to verify passcode');
              	 	} 
               	});
              	return false;
			});
   			
   			

   		});

        function addPaperFormJs(){
	        $('form#pmidbox').submit(function(){
		        var idStr = $(this).find('textarea').val();

		        // validate pmid str
		        var badIds = [];
		        var goodIds = [];
		        var ids = idStr.split(",");
		        for ( var i=0; i<ids.length; i++){
			        var id = ids[i].trim();

			        if ( ! id.match(/^\d+$/) ){
				        badIds.push(id);
			        }
			        else {
				        goodIds.push(id);
			        }
		        }

				if (badIds.length > 0) {
					alert("Sorry, your submission contains invalid paper id(s): " + badIds.join(", "));
				}
		        else {
					$.ajax({
						method: "post",
						url: baseUrl + "/addpmid?idStr=" + goodIds.join(","),
						success: function (response) {
							alert(response);
						},
						error: function () {
							window.alert('AJAX error trying to add pmid to database');
						}
					});
				}
		        return false; // do not refresh this page after form submit
	        });
        }

        function fetchAlleleRefDataTable(oConf) {
       	    console.log(oConf);
   		  	var oTable = $('table#alleleRef').dataTable({
   	            "bSort": false,
   	        	"processing": true,
   	        	"serverSide": true,  
   	            //"sDom": "<lr><'#caption'>tip",
   	         	//"sDom": "<<'#exportSpinner'>l<f><'#tableTool'>r>tip",
		        "sDom": "<<'#exportSpinner'>l<f>r>tip",
   	            "sPaginationType": "bootstrap",
   	            "searchHighlight": true,
   	         	"oLanguage": {
   	          		"sLengthMenu": 'Show <select>'+
       	            '<option value="10">10</option>'+
       	            '<option value="30">30</option>'+
       	            '<option value="50">50</option>'+
       	            '</select> allele records',
       	         	"sInfo": "Showing _START_ to _END_ of _TOTAL_ alleles records",
       	         	"sSearch": "Filter: "
   	        	},
   	        	"aoColumns": [{ "bSearchable": false, "bSortable": true },
   	        	              { "bSearchable": true, "bSortable": true },
   	        	           	  { "bSearchable": true, "bSortable": true },
	        	              { "bSearchable": true, "bSortable": true },
	        	              { "bSearchable": true, "bSortable": true },
	        	              { "bSearchable": true, "bSortable": true },
   	        	              { "bSearchable": false, "bSortable": false }
   	        	              ],
//   	        	           "aoColumns": [{ "bSearchable": false},
//		                              { "bSearchable": false },
//   	       	        	              { "bSearchable": true },
//   	       	        	           	  { "bSearchable": true },
//   	    	        	              { "bSearchable": true },
//   	    	        	              { "bSearchable": true },
//   	    	        	              { "bSearchable": true},
//   	       	        	              { "bSearchable": false},
//   	       	        	              ],
   	        	"columnDefs": [                
   	        	              { "type": "alt-string", targets: 4 }   //5th col sorted using alt-string
   	        	              ],
            	"aaSorting": [[ 4, "desc" ]],  // default sort column order
//                 "aoColumns": [
//                     {"bSearchable": false, "sType": "html", "bSortable": true},
//                     {"bSearchable": false, "sType": "string", "bSortable": true},
//                     {"bSearchable": true, "sType": "string", "bSortable": true},
//                     {"bSearchable": true, "sType": "string", "bSortable": true},
//                     {"bSearchable": true, "sType": "string", "bSortable": true},
//                     {"bSearchable": true, "sType": "html", "bSortable": true},
//	                 {"bSearchable": true, "sType": "string", "bSortable": true},
//	                 {"bSearchable": false, "sType": "html", "bSortable": false}
//                 ],
   	            "fnDrawCallback": function(oSettings) {  // when dataTable is loaded

	                if ( oConf.editMode ) {
		                $('table#alleleRef').find('tr th:first-child, tr td:first-child').show();
	                }
	                else {
		                $('table#alleleRef').find('tr th:first-child, tr td:first-child').hide();
	                }

   	            	// download tool
   	            	oConf.externalDbId = 1;
   	            	oConf.fileType = '';
   	            	oConf.fileName = 'impc_allele_references';
   	            	oConf.doAlleleRef = true;
   	            	oConf.legacyOnly = false;
   	            	oConf.filterStr = $(".dataTables_filter input").val();

   	            	$.fn.initDataTableDumpControl(oConf);

   	            	if ( $('button').hasClass('edit')) {

	   	            	// POST
	   	            	var thisTable = $(this);

		                $(this).find('tr td:nth-child(1) input').bind('click', function(){

			                var dbid = $(this).parent().parent().find('td span.pmid').attr('id'); // this comes from concatenation, so is a string

			                var fp = $(this).is(':checked') ? "yes" : "no"; // falsepositive is checked or not

			                $.ajax({
				                method: "get",
				                url: baseUrl + "/dataTableAlleleRefSetFalsePositive?id="+dbid+"&value="+fp,
				                success: function(response) {
					                // boolean response
				                },
				                error: function() {
					                window.alert('AJAX error trying to set false positive value for this paper');
				                }
			                });

		                });

	   	            	//$(this).find('tr td:nth-child(4)').attr('id', dbid); //.css({'cursor':'pointer'}); // set id for the key in POST
	   	            	$(this).find('tr td:nth-child(3)').editable(baseUrl + '/dataTableAlleleRef', {
	   	                    "callback": function( jsonStr, y ) {
	   	                		var j = JSON.parse(jsonStr);
		                        var displayedSymbol = null;
	   	                		if ( j.allAllelesNotFound ){
	   	                			alert("Curation ignored as allele symbol(s)\n\n" + j.symbol + "\n\ncould not be mapped to an MGI allele(s)");
				                    displayedSymbol = "";
	   	                		}
		                        else if (j.hasOwnProperty("someAllelesNotFound")){
				                    alert("Some curation ignored as allele symbol(s)\n\n" + j.someAllelesNotFound + "\n\ncould not be mapped to an MGI allele(s)");
				                    displayedSymbol = j.symbol;
			                    }
		                        else {
				                    displayedSymbol = j.symbol;
			                    }
	   	                     	$(this).text(displayedSymbol);
	   	                  		$(this).parent().find('td:first-child').html("<input type='checkbox'>");
		                        $(this).parent().find('td:nth-child(2)').text(j.reviewed);
		                     },
		                     "event": "click",
		                     "height": "50px",
		                     "width": "350px",
				             "type": "textarea",
				             "submit"  : "OK"
	   	             	});
	   	            	$(this).find('tr td:nth-child(3)').bind('click', function(){
			                $(this).attr('id', $(this).parent().find('td span.pmid').attr('id'));
	   	            		//console.log($(this).parent().find('td span.pmid').attr('id'));
	   	            		// a form is created on the fly by jeditable
	   	            		// change that value for user to save typing as this value 
	   	            		// will be 'yes'
	   	            		$(this).find('form').css('padding','2px');
			               // $(this).find('form input[name=value]').val("");
	   	            	}).mouseover(function(){
	   	            		$(this).css({'border':'1px solid gray'});
	   	            	}).mouseout(function(){
	   	            		$(this).css({'border':'none'});
	   	            	});
   	            	}
   	            },
   	            "sAjaxSource": baseUrl + '/dataTableAlleleRefEdit',
   	            "fnServerParams": function(aoData) {
   	                aoData.push(
   	                        {"name": "doAlleleRefEdit",
   	                        // "value": JSON.stringify(oConf, null, 3)
	                            "value": JSON.stringify(oConf)
   	                        }
   	                );
   	            }
   	        });

        }

        </script>

	</jsp:body>
		
</t:genericpage>

