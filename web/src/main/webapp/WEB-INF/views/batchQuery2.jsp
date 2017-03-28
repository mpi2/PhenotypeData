<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
	<jsp:attribute name="title">IMPC dataset batch query</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Batch search</a></jsp:attribute>
	<jsp:attribute name="header">
    
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        
        <style type="text/css">

			div.region-content {
				margin-top: 53px;
			}
			div#tableTool {
				position: relative;
				top: -70px;
				right: 0px;
			}
			table#dataInput td:first-child {
				width: 90px;
				max-width: 90px;
			}
			table.dataTable span.highlight {
				background-color: yellow;
				font-weight: bold;
				color: black;
			}
			.hideMe {
				display: none;
			}
			.showMe {
				display: block;
			}
			.alleleToggle {
				cursor: pointer;
				font-size: 11px;
				font-weight: bold;
			}
			div.srchBox {
				display: none;
			}
			div.srchBox input {
				height: 25px !important;
				padding: 0 0 0 25px;
				width: 200px;
			}
			div.srchBox {position: relative;}
			div.srchBox i.fa-search {position: absolute; top: 10px; left: 10px;}
			div.srchBox i.fa-times {
				position: absolute;
				top: 10px;
				left: 210px;
				cursor: pointer;
			}
			span.sugListPheno {
				font-size: 10px;
			}
			.ui-menu-item:hover {
				background-color: unset !important;
				background-image: none;
				border: none;
				outline-color: unset;
			}
			div#saveTable {
				top: 34px;
				left: -25px;
			}
			div#toolBox {
				top: -58px;
				right: 35px;
			}
			#pasteList {
				font-size: 12px;
			}
			.notes {
				font-size: 10px;
			}
			#accordion {
				font-size: 11px;
				margin-bottom: 20px;
				background-color: white;
			}
			#pastedList, #srcfile {
				padding-left: 0;
			}
			#srcfile  {
				border: 0;
			}
			.lbl {
				font-size: 12px;
				font-weight: bold;
			}
			div#query {
				font-size: 11px !important;
			}
			td.idnote {
				font-size: 12px;
				padding-left: 20px;
				height: 40px;
			}
			.cat {
				font-weight: bold;
				font-size:14px;
				color: black;
				padding: 5px;
			}
			.inner {
				height: auto;
				margin-bottom: 15px;

			}
			div#sec2 {
				display: none;
			}
			.fl2 {
				width: 56%;
				float: right;
				padding: 0 0 20px 0;
			}
			.fl1 {
				float: none; /* not needed, just for clarification */
				/* the next props are meant to keep this block independent from the other floated one */
				width: 40%;
				padding: 0 15px 0 5px;
				border-right: 1px solid #C1C1C1;
			}
			h6.bq {
				color: gray;
				padding: 0;
				margin: 0;
			}
			div#errBlock {
				margin-bottom: 10px;
				display: none;
				color: #8B0A50;
			}
			hr {
				color: #F2F2F2;
				margin: 2px 0 !important;
			}
			button#chkfields {
				margin-top: 10px;
				display: block;
			}
			table.dataTable {
				overflow-x:scroll;
				width:100%;
				display:block;
			}
			input[type=checkbox] {
				height: 25px;
				vertical-align: middle;
			}
			div#tableTool {
				float: right;
				margin-top: 40px;
				clear: right;
			}
			#srchBlock {
				background-color: white;
				padding: 10px 10px 30px 10px;
			}
			div#infoBlock {
				margin-bottom: 10px;
			}
			form#dnld {
				margin: 0;
				padding: 0;
				border: none;
			}
			span#resubmit {
				font-size: 12px;
				padding-left: 20px;
				color: #8B0A50;
			}
			form#dnld {
				padding: 5px;
				border-radius: 5px;
				background: #F2F2F2;
			}
			form#dnld button {
				text-decoration: none;
			}
			table#dataInput {
				border-collapse: collapse;
			}
			tr#humantr {
				background-color: #F2F2F2;
				border-radius: 4px !important;
			}
			table#dataInput td {
				text-align: left;
				vertical-align: middle;
			}
			table#dataInput td.idnote {
				padding-left: 8px;
			}
			div.textright {
				padding: 5px 0;
			}
			div.qtipimpc {
				padding: 30px;
				background-color: white;
				border: 1px solid gray;
				/*height: 250px;
                overflow-x: hidden;*/
			}
			div.qtipimpc p {
				font-size: 12px;
				line-height: 20px;
			}
			div.tipSec {
				font-weight: bold;
				font-size: 14px;
				padding: 5px;
				background-color: #F2F2F2;
			}
			div#docTabs {
				border: none;
			}
			ul.ui-tabs-nav {
				border: none;
				border-bottom: 1px solid #666;
				background: none;
			}
			ul.ui-tabs-nav li:nth-child(1) {
				font-size: 14px;
			}
			ul.ui-tabs-nav li a {
				margin-bottom: -1px;
				border: 1px solid #666;
				font-size: 12px;
			}
			ul.ui-tabs-nav li a:hover {
				color: white;
				background-color: gray;
			}
			a#ftp {
				color: #0978a1 !important;
				text-decoration: none;
			}
			span#sample {
				color: black;
			}
			fieldset {
				padding: 3px 10px;
				border: 1px solid lightgrey;
				font-size: 12px;
				margin: 15px 0;
			}
			fieldset.human {
				background-color: #F2F2F2;
			}
			legend {
				padding: 0 15px;
				border: 1px solid lightgrey;
				background-color: #F2F2F2;
			}
			legend.human {
				margin-left: 50%;
				background-color: white;
			}
			legend i {
				border: none !important;
				margin: 3px 0 0 5px;
			}
			fieldset i {
				float: right;
				right: 10px;
				cursor: pointer;
				border: 1px solid lightgray;
				padding: 1px 2px;
			}
			#fieldList {
				margin-top: 15px;
			}

			/*chr range slider */
			div#rangeBox {
				display: none;
				margin-left: 15px;
			}
			div#chrSlider {
				width: 200px;
				height: 2px;
			}
			.ui-slider .ui-slider-handle {
				height: 5px;
				width: 0px;
				padding-left: 9px; /*add this*/
				margin-top: 2px;
			}
			.ui-slider-horizontal .ui-slider-handle{
				background: lightgray;
			}
			.ui-slider-range.ui-widget-header {
				background: orange;
			}
			input#chrRange {
				border: 0;
				color: gray;
				font-size: 12px;
			}
			span#range {
				font-size: 12px;
			}
			#chrSel {
				display: inline;
			}
			div#rangeBox input {padding: 0 3px;}
			input#rstart {width: 55px;}
			input#rend {width: 55px;}

		</style>

        <script type='text/javascript'>

            $(document).ready(function () {
                'use strict';
                // test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';

                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl}";

                $('div#batchQryLink').hide(); // hide batchquery link for batchquery page


//                if ( window.location.search != ""){
//                    alert(window.location.search)
//                    var oConf = convertParamToObject(decodeURIComponent(window.location.search));
//                    console.log("oConf: " + oConf);
//
//                    prepare_dataTable(oConf.fllist);
//                    fetchBatchQueryDataTable(oConf);
//                }

                addAttrChecker();

                // initialze search example qTip with close button and proper positioning
                var bqDoc = '<h3 id="bqdoc">How to use batch search</h3>'

                    + '<div id="docTabs">'
                    + '<ul>'
                    + '<li><a href="#tabs-1">Interface</a></li>'
                    + '<li><a href="#tabs-2">Data fields</a></li>'
                    + '</ul>'
                    + '<div id="tabs-1">'
                    + '<p>Query keywords can be either datatype-specific ID (eg, mouse gene id: MGI:2682306) or text (eg. mouse gene symbol).<p>'
                    + '<p>Simply click on one of the radio buttons on the left (the<b> Datatype Input panel</b>) to choose the datatype you want to search for.'
                    + '</p>'
                    + '<p>The data fields for the chosen datatype are shown on the right (the <b>Customized Output panel</b>) and can be added/removed using checkboxes.'
                    + '<p>'
                    + '<p>The sample of results (<b>maximum of 10 records</b>) will be updated automatically after checking checkboxes.'
                    + '<p>'
                    + '</div>'
                    + '<div id="tabs-2">'
                    + '<p>The data fields in additional annotations of the customized output panel are based on their being annotated to a datatype of your search.</p>'
                    + '<p>For example, an MP term is annotated to an IMPC gene via phenotypic observations or experiments.</p>'
                    + '<p>A disease term (human disease) is annotated to an IMPC mouse phenotype via <a href="http://database.oxfordjournals.org/content/2013/bat025" target="_blank">Phenodigm</a>, which is a semantic approach to map between clinical features observed in humans and mouse phenotype annotations.</p>'
                    + '<p>An HP term is mapped to an MP term using similar Phenodigm semantic approach.</p>'
                    + '<p><b>hasQc</b>: IMPC lines with phenotyping data that has been quality controlled by the IMPC Data Coordination Center</p>'
                    + '<p><b>p value</b>: statistical confidence that the result is not due to chance. Lower is more significant. IMPC significant threshold value is 0.0001.</p>'
                    + '</div>'
                    + '</div>';

                $("a#bqdoc").qtip({
                    hide: false,
                    content: {
                        text: bqDoc,
                        title: {'button': 'close'}
                    },
                    style: {
                        classes: 'qtipimpc',
                        tip: {corner: 'center top'}
                    },
                    position: {my: 'left top',
                        adjust: {x: 0, y: 0}
                    },
                    show: {
                        event: 'click' //override the default mouseover
                    },
                    events: {
                        show: function(event, api) {
                            $('div#docTabs').tabs();
                            $('ul.ui-tabs-nav li a').click(function(){
                                $('ul.ui-tabs-nav li a').css({'border-bottom':'none', 'background-color':'#F4F4F4', 'border':'none'});
                                $(this).css({'border':'1px solid #666', 'border-bottom':'1px solid white', 'background-color':'white', 'color':'#666'});
                            });

                            $('ul.ui-tabs-nav li:nth-child(1) a').click();  // activate this by default
                        }
                    }
                });

                $( "#accordion" ).accordion();


                // reset to default when page loads
                $('input#mouse_marker_symbol').prop("checked", true) // check datatyep ID as gene by default
                $('textarea#pastedList').val($('input#mouse_marker_symbol').attr("value"));

                // default fields checked
                freezeDefaultCheckboxes();
                //chkboxAllert();  for now, don't want automatic resubmit each time a checkbox is clicked

                var currDataType  = false;

                toggleAllFields();

                $('div.srchBox input').click(function(){
                    if ($(this).val()=="search"){
                        $(this).val("");
                    }
                    $('textarea#pastedList').val('');
                });

                $('div.srchBox i.fa-times').click(function(){
                    $(this).siblings('input').val('');
                    $('textarea#pastedList').val('');
                });

                // fetch dynamic data fields as radios
                $('input.bq').click(function(){

                    //if ( $(this).is(':checked') ){

						$('div.srchBox').hide();

                        currDataType = $(this).attr('id');
                    	checkDataTypeDefaultFields(currDataType);

                        // check fields as default when search datatype changes

                        // assign to hidden field in fileupload section
                        $('input#datatype').val(currDataType);

                        if (currDataType == "mp" || currDataType == "hp" || currDataType == "disease"){
                            var theSrchBox = $(this).next().next().next();
                            var theInput = theSrchBox.find('input');
//                            if (theInput.val() == ''){
//                                theInput.val('search');
//                            }

                            theSrchBox.show();

                            addAutosuggest(theInput);
                        }
                        else if (currDataType == "geneChr"){
                            $(this).next().next().show();

                            //when input change, update $('textarea#pastedList')
                            $('select#chrSel').change(function() {
                                var chrStart = $('input#rstart').val() == "" ? "empty" : $('input#rstart').val();
                                var chrEnd = $('input#rend').val() == "" ? "empty" : $('input#rend').val();
                                $('textarea#pastedList').val("chr" + this.value + ":" + chrStart + "-" + chrEnd);
                            });

							$('input#rstart, input#rend').keyup(function() {
							    console.log("getting " + this.val);
                                var chr = $('select#chrSel').val();
							    var chrStart = $('input#rstart').val() == "" ? "empty" : $('input#rstart').val();
                                var chrEnd = $('input#rend').val() == "" ? "empty" : $('input#rend').val();

                                $('textarea#pastedList').val("chr" + chr + ":" + chrStart + "-" + chrEnd);
                            });
                        }

                        $('textarea#pastedList').val($(this).attr("value"));


                        //currDataType = parseCurrDataDype(currDataType);

                        //console.log($(this).attr('id'));
                        //var id = $(this).attr('id');
                        //$('div#fullDump').html("<input type='checkbox' id='fulldata' name='fullDump' value='" + id + "'>" + "Export full IMPC dataset via " + currDataType2 + " identifiers");
                        $('div#fullDump').html("Please refer to our FTP site");

                        // reload dataset fields for selected datatype Id

//                        $.ajax({
//                            url: baseUrl + '/batchquery2-1?core=' + currDataType,
//                            success: function(htmlStr) {
//                                //console.log(htmlStr);
//                                $('div#fieldList').html(htmlStr);
//                                addAttrChecker();
//                                //freezeDefaultCheckboxes();
//                                toggleAllFields();
//                                //chkboxAllert();
//                            },
//                            error: function() {
//                                window.alert('AJAX error trying to fetch data');
//                            }
//                        });
                   // }
                });

                // $('textarea#pastedList').val(''); // reset
                $('input#fileupload').val(''); // reset
                $('input#fulldata').attr('checked', false); // reset

            });

            function checkDataTypeDefaultFields(currDataType){
                var map = {
                    geneId: "mgi_accession_id",
                	ensembl: "ensembl_gene_id",
                	geneChr: "marker_symbol",
                	mp: "mp_id",
                	human_marker_symbol: "human_gene_symbol",
                	hp: "hp_id",
                	disease: "disease_id"
            	};

                $('fieldset input').prop('checked', false); // reset first before checking for new field

                if (currDataType == "human_marker_symbol") {
                    $('fieldset.human').find("input[value='human_gene_symbol']").prop('checked', true);
                }
                else if (currDataType == "geneChr"){
                    $('fieldset.mouse').find("input[value='" + map[currDataType] + "']").prop('checked', true);
				}
                else {
                    $('fieldset').find("input[value='" + map[currDataType] + "']").prop('checked', true);
				}

				// default
				if ( ! $('fieldset.mouse').find("input[value='marker_symbol']").is(':checked') ) {
                    $('fieldset.mouse').find("input[value='marker_symbol']").prop('checked', true);
                }

            }

//            function parseCurrDataDype(currDataType){
//                if (currDataType.startsWith("mp")){
//                    currDataType = "mp";
//                }
//                else if (currDataType.startsWith("phenodigm")){
//                    currDataType = "phenodigm";
//                }
//                else if (currDataType.startsWith("gene")){
//                    currDataType = "gene";
//                }
//                return currDataType;
//            }

            function addAttrChecker() {
                $('fieldset i').click(function () {
                    if ($(this).hasClass("fa-plus")) {
                        $(this).siblings('input').prop('checked', true);
                        $(this).removeClass('fa-plus').addClass('fa-minus');
                    }
                    else {
                        $(this).siblings('input').prop('checked', false);
                        $(this).removeClass('fa-minus').addClass('fa-plus');
                    }
                });
            }
            function convertParamToObject(params){
                oConf = {};
                var paramsStr = params.replace("?", "");
                var kv = paramsStr.split("&");
                for ( var i=0; i<kv.length; i++){
                    var kv2 = kv[i].split("=");
                    oConf[kv2[0]] = kv2[1];
                }
                return oConf;
            }

            function easyReadBp(bp){
                return bp.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            }


            function addAutosuggest(thisInput){

                // generic search input autocomplete javascript
                var docType = null;
                var inputType = thisInput.attr('id');
                var solrBq = "";
                if ( inputType == 'srchMp'){
                    docType = 'mp';
                    solrBq = "&bq=mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75";
                }
                else if ( inputType == 'srchHp'){
                    docType = 'hp';
                    solrBq = "&bq=hp_term:*^90 hp_term_synonym:*^80";
                }
                else if ( inputType == 'srchDisease'){
                    docType = 'disease';
                }

                thisInput.autocomplete({
                    source: function( request, response ) {
                        var qfStr = request.term.indexOf("*") != -1 ? "auto_suggest" : "string auto_suggest";
                        // var facetStr = "&facet=on&facet.field=docType&facet.mincount=1&facet.limit=-1";
                        var sortStr = "&sort=score desc";
                        // alert(solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr);                        )

                        $.ajax({
                            //url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,
                            url: solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr,
                            dataType: "jsonp",
                            'jsonp': 'json.wrf',
                            data: {
                                q: '"'+request.term+'"'
                            },
                            success: function( data ) {

                                var docs = data.response.docs;

                                var suggests = [];
                                var seenTerm = {};
                                for ( var i=0; i<docs.length; i++ ){
                                    var facet = null;

                                    for ( var key in docs[i] ){
                                        if ( key != 'docType' ){

                                            var term = docs[i][key].toString();
                                            var termHl = term;

                                            // highlight multiple matches
                                            // (partial matches) while users
                                            // typing in search keyword(s)
                                            // let jquery autocomplet UI handles
                                            // the wildcard
                                            // var termStr =
                                            // $('input#s').val().trim('
                                            // ').split('
                                            // ').join('|').replace(/\*|"|'/g,
                                            // '');
                                            var termStr = $('input#s').val().trim(' ').split(' ').join('|').replace(/\*|"|'/g, '').replace(/\(/g,'\\(').replace(/\)/g,'\\)');

                                            var re = new RegExp("(" + termStr + ")", "gi") ;
                                            var termHl = termHl.replace(re,"<b class='sugTerm'>$1</b>");

                                            // add only once with the top score
                                            var lowerCaseTerm = term.toLowerCase();
                                            if ( seenTerm[lowerCaseTerm] == undefined){
                                                seenTerm[lowerCaseTerm]++;
                                                suggests.push("<span class='" + facet + " sugListPheno'>" + termHl + "</span>");
                                            }

                                        }
                                    }
                                }
                                var dataTypeVal = [];

                                if ( suggests.length > 0 ) {
                                    for (var i = 0; i < suggests.length; i++) {
                                        dataTypeVal.push(suggests[i]);
                                    }
                                }

                                response(dataTypeVal);
                            }
                        });
                    },
                    focus: function (event, ui) {
                        var thisInput = $(ui.item.label).text().replace(/<\/?span>|^\w* : /g,'');
                        // assign value to input box
                        this.value = thisInput.trim();

                        event.preventDefault(); // Prevent the default focus behavior.
                    },
                    minLength: 3,
                    select: function( event, ui ) {
                        // select by mouse / KB
                        var q = this.value;
                        q = encodeURIComponent(q).replace("%3A", "\\%3A");

                        $('textarea#pastedList').val(decodeURIComponent(q));

                        // prevents escaped html tag displayed in input box
                        event.preventDefault(); return false;

                    },
                    open: function(event, ui) {
                        // fix jQuery UIs autocomplete width
                        $(this).autocomplete("widget").css({
                            "width": ($(this).width() + "px")
                        });

                        $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
                    },
                    close: function() {
                        $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
                    }
                }).data("ui-autocomplete")._renderItem = function( ul, item) {
                    // prevents HTML tags being escaped

                    return $( "<li></li>" )
                        .data( "item.autocomplete", item )
                        .append( $( "<a></a>" ).html( item.label ) )
                        .appendTo( ul );
                };

                // User press ENTER
                thisInput.keyup(function (e) {
                    if (e.keyCode == 13) { // user hits enter
                        $(".ui-menu-item").hide();
                        //$('ul#ul-id-1').remove();

                        //alert('enter: '+ MPI2.searchAndFacetConfig.matchedFacet)
                        var input = thisInput.val().trim();

                        //alert(input + ' ' + solrUrl)
                        input = /^\*\**?\*??$/.test(input) ? '' : input;  // lazy matching

                        var re = new RegExp("^'(.*)'$");
                        input = input.replace(re, "\"$1\""); // only use double quotes for phrase query

                        // NOTE: solr special characters to escape
                        // + - && || ! ( ) { } [ ] ^ " ~ * ? : \

                        input = encodeURIComponent(input);

                        input = input.replace("%5B", "\\[");
                        input = input.replace("%5D", "\\]");
                        input = input.replace("%7B", "\\{");
                        input = input.replace("%7D", "\\}");
                        input = input.replace("%7C", "\\|");
                        input = input.replace("%5C", "\\\\");
                        input = input.replace("%3C", "\\<");
                        input = input.replace("%3E", "\\>");
                        input = input.replace(".", "\\.");
                        input = input.replace("(", "\\(");
                        input = input.replace(")", "\\)");
                        input = input.replace("%2F", "\\/");
                        input = input.replace("%60", "\\`");
                        input = input.replace("~", "\\~");
                        input = input.replace("%", "\\%");
                        input = input.replace("!", "\\!");
                        input = input.replace("%21", "\\!");
                        input = input.replace("-", "\\-");

                        if (/^\\%22.+%22$/.test(input)) {
                            input = input.replace(/\\/g, ''); //remove starting \ before double quotes
                        }

                        // no need to escape space - looks cleaner to the users
                        // and it is not essential to escape space
                        input = input.replace(/\\?%20/g, ' ');

                        $('textarea#pastedList').val(input);
                        $(".ui-menu-item").hide();
                    }
                });

            }


            function chkboxAllert() {
                // resubmit automatically whenever checkbox is clicked
                $("div.fl2").find("input[class!='default']").click(function(){
                    resubmit();
                });
            }
            function freezeDefaultCheckboxes(){
                $('input.isDefault').click(function(){
                    return false;
                })
            }

            function toggleAllFields(){
                $('button#chkFields').click(function(){
                    if ( $(this).hasClass('checkAll') ){
                        $(this).removeClass('checkAll').html('Check all fields');
                        $("div.fl2").find("input[type='checkbox']").prop('checked', false);
                        $("div.fl2 input.default").prop('checked', true);
                        $('fieldset i').removeClass('fa-minus').addClass('fa-plus');
                    }
                    else {
                        $(this).addClass('checkAll').html('Reset to default fields')
                        $("div.fl2").find("input[type='checkbox']").prop('checked', true);
                        $('fieldset i').removeClass('fa-plus').addClass('fa-minus');
                    }

                    //resubmit();
                });
            }

            function resubmit(){
                $('div#accordion').find('form:visible').find("input[type='submit']").click();
            }

            function submitPastedList(){

                // verify chromosome coords
                if ($('input.bq:checked').attr('id') == "geneChr"){
                    var chr = $('select#chrSel').val();
                    $.ajax({
                        url: baseUrl + '/chrlen?chr=' + chr,
                        success: function(chrlen) {

                            var easyReadChrLen = chrlen.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");

                            console.log("chr range check " + chrlen);
                            var chrStart = $('input#rstart').val();
                            var chrEnd = $('input#rend').val();

                            console.log("check: "+ chrStart > chrEnd);
                            if (! chrStart.match(/^\d+$/)){
                                alert("ERROR: start coordinate should be integer");
                            }
                            else if (! chrEnd.match(/^\d+$/)) {
                                alert("ERROR: end coordinate should be integer");
                            }
                            else if (chrStart > chrEnd){
                                alert("ERROR: start coord is larger than end coord");
							}
							else if (chrStart == chrEnd){
                                alert("ERROR: start coord is same as end coord");
							}
                            else if (chrStart > chrlen) {
                                alert("ERROR: start coord exceeds chromosome range: " + easyReadChrLen);
                            }
                            else if (chrEnd > chrlen) {
                                alert("ERROR: end coord exceeds chromosome range: " + easyReadChrLen);
                            }
                            else if (chrStart < 1 || chrEnd < 1){
                                alert("ERROR: coordinate cannot be less than 1");
							}
                            return false;
                        },
                        error: function() {
                            window.alert('AJAX error trying to fetch length of chromosome' + chr);
                        }
                    });
                }

                if ( $('textarea#pastedList').val() == ''){
                    alert('Oops, search keyword is missing...');
                }
                else {

                    refreshResult(); // refresh first

                    var currDataType = $('input.bq:checked').attr('id');
                    idList = parsePastedList($('textarea#pastedList').val(), currDataType);

                    if ( idList !== false ){
                        var kv = fetchSelectedFieldList();

                        prepare_dataTable(kv.fllist);

                        var oConf = {};
                        oConf.idlist = idList;
                        oConf.labelFllist = kv.labelFllist;
                        oConf.dataType = currDataType;

                        fetchBatchQueryDataTable(oConf);
                    }
                }
                return false;
            }

            function refreshResult(){
                $('div#infoBlock, div#errBlock, div#bqResult').html(''); // refresh first
                var sampleData = "<p><span id='sample'>Showing maximum of 10 records for how your data looks like.<br>For complete dataset of your search, please use export buttons.</span>";
                //$('div#infoBlock').html("Your datatype of search: " + parseCurrDataDype($('input.bq:checked').attr('id')).toUpperCase() + sampleData);
            }

            function uploadJqueryForm(){

                refreshResult(); // refresh first

                var currDataType = $('input.bq:checked').attr('id');
                $('input#dtype').val(currDataType);

                if ( $('input#fileupload').val() == '' ){
                    alert("Please upload a file with a list of identifiers");
                }
                else {
                    $('#bqResult').html('');

                    $("#ajaxForm").ajaxForm({
                        success:function(jsonStr) {
                            //$('#bqResult').html(idList);
                            //console.log(jsonStr)
                            var j = JSON.parse(jsonStr);

                            if ( j.badIdList != ''){
                                $('div#errBlock').html("UPLOAD ERROR: unprocessed identifier(s): " + j.badIdList).show();
                            }

                            var kv = fetchSelectedFieldList();
                            prepare_dataTable(kv.fllist);

                            var oConf = {};
                            oConf.idlist = j.goodIdList;
                            //oConf.labelFllist = kv.labelFllist

                            fetchBatchQueryDataTable(oConf);
                        },
                        dataType:"text"
                    }).submit();
                }
                return false; // so that the form can only be submitted via ajax
            }

            function fetchSelectedFieldList(){
                var kv = {};
                var labelList = {};
                var fllist = [];
                $("div#fieldList").find("input:checked").each(function(){
                    var dbLabel = $(this).attr("name");
                    if ( ! (dbLabel in labelList) ){
                        labelList[dbLabel] = [];
                    }
                    labelList[dbLabel].push($(this).val());
                    fllist.push($(this).val());

                    //console.log(dbLabel + " -- " + $(this).val());
                });
                kv.labelFllist = JSON.stringify(labelList);
                console.log("fields: "+ kv.labelFllist);
                kv.fllist = fllist;

                return kv;
            }

            function prepare_dataTable(flList){

                console.log("list: "+ flList);
                var th = '';
                for ( var i=0; i<flList.length; i++){
                    var colname = flList[i].replace(/_/g, " ");
                    th += "<th>" + colname + "</th>";
                }

                var tableHeader = "<thead>" + th + "</thead>";
                var tableCols = flList.length;

                var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "batchq");

                $('div#bqResult').append(dTable);
            }

            function parsePastedList(val, dataType){
                val = val.trim();
                var aVals = val.split(/\n|,|\t|\s+/);
                var aVals2 = [];
                for ( var i=0; i<aVals.length; i++){
                    if ( aVals[i] == "" ){
                        continue;
                    }
                    var oriId = aVals[i].trim();
                    var uppercaseId = aVals[i].toUpperCase().trim();
                    var errMsg = "ERROR - " + uppercaseId + " is not an expected " + dataType + " identifier. Please try changing the datatype input.";

                    if ( dataType == 'disease' ){
                        if ( ! (uppercaseId.indexOf('OMIM') == 0 ||
                            uppercaseId.indexOf('ORPHANET') == 0 ||
                            uppercaseId.indexOf('DECIPHER') == 0) ){

                            alert(errMsg);
                            return false;
                        }
                    }
                    else if ( dataType == 'geneId' && uppercaseId.indexOf('MGI:') != 0 ){
                        alert(errMsg);
                        return false;
                    }
                    else if ( dataType == 'ensembl' && uppercaseId.indexOf('ENSMUSG') != 0 ){
                        alert(errMsg);
                        return false;
                    }
//                    else if ( (dataType == 'mp' || dataType == 'ma' || dataType == 'hp' ) && uppercaseId.indexOf(dataType.toUpperCase()) !== 0 ){
//                        alert(errMsg);
//                        return false;
//                    }
                    var thisId = dataType=="human_marker_symbol" ? uppercaseId : oriId;
                    aVals2.push('"' + thisId + '"');
                }

                return aVals2.join(",");
            }

            function getUniqIdsStr(inputIdListStr) {
                var ids = inputIdListStr.split(",");
                return $.fn.getUnique(ids).join(",");
            }
            function getFirstTenUniqIdsStr(inputIdListStr) {
                var ids = inputIdListStr.split(",");
                return $.fn.getUnique(ids.slice(0,10)).join(",");
            }

            function fetchBatchQueryDataTable(oConf) {
                console.log(oConf);
                // deals with duplicates and take a max of first 10 records to show the users
                oConf.idlist = getFirstTenUniqIdsStr(getUniqIdsStr(oConf.idlist));
                //oConf.idlist = getUniqIdsStr(oConf.idlist);
                console.log(oConf);
                //var aDataTblCols = [0,1,2,3,4,5];
                var oTable = $('table#batchq').dataTable({
                    "bSort": false, // true is default
                    "processing": true,
                    "paging": false,
                    //"serverSide": false,  // do not want sorting to be processed from server, false by default
                    //"sDom": "<<'#exportSpinner'>l<f><'#tableTool'>r>tip",
                    "sDom": "<<'#exportSpinner'>l<'#tableTool'>r>tip",
                    "sPaginationType": "bootstrap",
                    "searchHighlight": true,
                    "iDisplayLength": 50,
                    "oLanguage": {
                        "sSearch": "Filter: ",
                        "sInfo": "Showing _START_ to _END_ of _TOTAL_ genes (for complete dataset of your search, please use export buttons)"
                    },
					/*  "columnDefs": [
					 { "type": "alt-string", targets: 3 }   //4th col sorted using alt-string
					 ], */
                    "aaSorting": [[ 0, "asc" ]],  // default sort column order, won't work if bSort is false
					/*"aoColumns": [
					 {"bSearchable": true, "sType": "html", "bSortable": true},
					 {"bSearchable": true, "sType": "string", "bSortable": true},
					 {"bSearchable": true, "sType": "string", "bSortable": true},
					 {"bSearchable": true, "sType": "string", "bSortable": true},
					 {"bSearchable": true, "sType": "string", "bSortable": true},
					 {"bSearchable": false, "sType": "html", "bSortable": true}
					 ],*/
                    "fnDrawCallback": function (oSettings) {  // when dataTable is loaded

                        $('div#sec2').show();

                        document.getElementById('sec2').scrollIntoView(true); // scrolls to results when datatable loads

                        var endPoint = baseUrl + '/bqExport';

                        $('div#tableTool').html("<span id='expoWait'></span><form id='dnld' method='POST' action='" + endPoint + "'>"
                            + "<span class='export2'>Export full dataset as</span>"
                            + "<input name='coreName' value='' type='hidden' />"
                            + "<input name='fileType' value='' type='hidden' />"
                            + "<input name='gridFields' value='' type='hidden' />"
                            + "<input name='idList' value='' type='hidden' />"
                            + "<button class='tsv fa fa-download gridDump'>TSV</button>"
                            + " or<button class='xls fa fa-download gridDump'>XLS</button>"
                            + "</form>");

                        $('button.gridDump').click(function(){

                            var kv = fetchSelectedFieldList();
                            var fllist = kv.fllist;
                            var errMsg = 'AJAX error trying to export dataset';
                            var currDataType = $('input.bq:checked').attr('id');
                            var idList = null;
                            var fileType = $(this).hasClass('tsv') ? 'tsv' : 'xls';

                            var formId = $('div#accordion').find('form:visible').attr('id');
                            var isForm = false;

                            if ( formId == 'ajaxForm' ){
                                isForm = true;
                                $("#ajaxForm").ajaxForm({
                                    url: baseUrl + '/batchQuery?dataType=' + currDataType,
                                    success:function(jsonStr) {
                                        var j = JSON.parse(jsonStr);
                                        idList = j.goodIdList;
                                        doExport(currDataType, fileType, fllist, idList, isForm);
                                    },
                                    dataType:"text",
                                    type: 'POST',
                                }).submit();
                            }
                            else if ( formId == 'pastedIds' ){
                                idList = parsePastedList($('textarea#pastedList').val(), currDataType);
                                doExport(currDataType, fileType, fllist, idList, isForm);
                            }
                            else {
                                idList = '*';
                                doExport(currDataType, fileType, fllist, idList, isForm);
                            }

                            if (formId == 'ajaxForm' ){
                                return false;
                            }
                        });

                        $('body').removeClass('footerToBottom');
                    },
                    "ajax": {
                        "url": baseUrl + "/dataTable_bq2?",
                        "data": oConf,
                        "type": "POST",
                        "error": function() {
                            $('div.dataTables_processing').text("Failed to fetch your query: keyword not found");
                            $('td.dataTables_empty').text("");
                        }
                    }
                });
            }
            function doExport(currDataType, fileType, fllist, idList, isForm, phenoSimilarity_id, phenoSimilarity_term,
                              wantHumanCurated_id, wantHumanCurated_term){

                // deals with duplicates
                if ( idList.split(",").length > 500 ){
                    var isOk = window.confirm("Please be aware that you have submitted > 500 identifiers and it will take longer to download\n\nWould you like to proceed?");
                    if ( !isOk ){
                        return false; // won't do the rest
                    }
                }

                idList = getUniqIdsStr(idList);

                $("form#dnld input[name='coreName']").val(currDataType);
                $("form#dnld input[name='fileType']").val(fileType);
                $("form#dnld input[name='gridFields']").val(fllist);
                $("form#dnld input[name='idList']").val(idList);

                if ( isForm ) {
                    $('button').unbind('click');
                    $("form#dnld").submit();
					/*
					 $('button.xls').click(function() {
					 // submit the form
					 $("form#dnld").ajaxSubmit();
					 // return false to prevent normal browser submit and page navigation
					 return false;
					 });*/
                }
            }


            // submit query


		</script>

        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.form.js"></script>
        <script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>

    </jsp:attribute>

	<jsp:attribute name="addToFooter">
        <div class="region region-pinned">

		</div>

    </jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC Dataset Batch Query<a id="bqdoc" class=""><i class="fa fa-question-circle pull-right"></i></a></h1>

						<div class="section">

							<!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
							<div class='inner' id='srchBlock'>

								<div class='fl2'>

									<h6 class='bq'>Customized Output</h6><hr>
									<div id='fieldList'>${outputFields}</div>
								</div>

								<div class='fl1'>
									<h6 class='bq'>Datatype Input</h6><hr>
									<div id='query'>
										<table id='dataInput'>
											<tr>
												<td><span class='cat'><i class="icon icon-species">M</i>Mouse<br>&nbsp;(GRCm38):</span></td>
												<td>
													<input type="radio" id="mouse_marker_symbol" value="Eg. Car4 or CAR4 (case insensitive). Synonym search supported" name="dataType" class='bq' checked="checked">MGI gene symbol<br>
													<input type="radio" id="geneId" value="Eg. MGI:106209" name="dataType" class='bq' >MGI id<br>
													<input type="radio" id="ensembl" value="Eg.. ENSMUSG00000011257" name="dataType" class='bq'>Ensembl id<br>

													<input type="radio" id="geneChr" value="Eg. chr12:53694976-56714605" name="dataType" class='bq'>Chromosomal coordinates<br>
													<div id="rangeBox" class="srchBox">
														Chr:
														<select id="chrSel">
															<option value="1" selected="selected">1</option>
															<option value="2">2</option>
															<option value="3">3</option>
															<option value="4">4</option>
															<option value="5">5</option>
															<option value="6">6</option>
															<option value="7">7</option>
															<option value="8">8</option>
															<option value="9">9</option>
															<option value="10">10</option>
															<option value="11">11</option>
															<option value="12">12</option>
															<option value="13">13</option>
															<option value="14">14</option>
															<option value="15">15</option>
															<option value="16">16</option>
															<option value="17">17</option>
															<option value="18">18</option>
															<option value="19">19</option>
															<option value="X">X</option>
															<option value="Y">Y</option>
															<option value="MT">MT</option>
														</select>
														Start: <input id='rstart' type="text" name="pin" size="8">
														End: <input id='rend' type="text" name="pin" size="8">
													</div>

													<input type="radio" id="mp" value="Eg. cardiovescular phenotype or MP:0001926" name="dataType" class='bq'>Mouse phenotype <i class="fa fa-info-circle" aria-hidden="true"></i><br>
													<div class='block srchBox'>
														<i class='fa fa-search'></i>
														<input id='srchMp' value="search">
														<i class='fa fa-times'></i>
													</div>

												</td>
											</tr>
											<tr id="humantr">
												<td><span class='cat'><i class="icon icon-species">H</i>Human<br>&nbsp;(GRCh38):</span></td>
												<td>
													<input type="radio" id="human_marker_symbol" value="Eg. Car4 or CAR4 (case insensitive). Synonym search supported" name="dataType" class='bq'>HGNC gene symbol<br>

													<input type="radio" id="hp" value="Eg. Hyperchloremia or HP:0000400" name="dataType" class='bq'>Human phenotype <i class="fa fa-info-circle" aria-hidden="true"></i><br>
													<div class='block srchBox'>
														<i class='fa fa-search'></i>
														<input id='srchHp' value="search">
														<i class='fa fa-times'></i>
													</div>

													<input type="radio" id="disease" value="Eg. Apert syndrome or OMIM:100300 or ORPHANET:10 or DECIPHER:38" name="dataType" class='bq'>Human disease<i></i><br>
													<div class='block srchBox'>
														<i class='fa fa-search'></i>
														<input id='srchDisease' value="search">
														<i class='fa fa-times'></i>
													</div>
												</td>
											</tr>
										</table>

										<div id="accordion">
											<p class='header'>Paste in your list or single query</p>
											<div>
												<p>
													<form id='pastedIds'>
														<textarea id='pastedList' rows="5" cols="50"></textarea>
														<input type="submit" id="pastedlistSubmit" name="" value="Submit" onclick="return submitPastedList()" />
														<input type="reset" name="reset" value="Reset"><p>
												<p class='notes'>Supports space, comma, tab or new line separated identifier list</p>
												<p class='notes'>Please DO NOT submit a mix of identifiers from different datatypes</p>
												</form>
												</p>
											</div>
											<p class='header'>Upload your list from file</p>
											<div>

												<form id="ajaxForm" method="post" action="${baseUrl}/batchQuery" enctype="multipart/form-data">
													<!-- File input -->
													<input name="fileupload" id="fileupload" type="file" /><br/>
													<input name="dataType" id="dtype" value="" type="hidden" /><br/>
													<input type="submit" id="upload" name="upload" value="Upload" onclick="return uploadJqueryForm()" />
													<input type="reset" name="reset" value="Reset"><p>
													<p class='notes'>Supports comma, tab or new line separated identifier list</p>
													<p class='notes'>Please DO NOT submit a mix of identifiers from different datatypes</p>
												</form>

											</div>
											<p class='header'>Full dataset</p>
											<form>
												<div id='fullDump'></div>
												Please use our <a id='ftp' href='ftp://ftp.ebi.ac.uk/pub/databases/impc/' target='_blank'>FTP</a> site for large dataset.
												<!-- <input type="submit" id="fulldata" name="" value="Submit" onclick="return fetchFullDataset()" /><p> -->
											</form>
										</div>

									</div>

								</div>
							</div>

							<div style="clear: both"></div>

						</div>

					</div><!-- end of section -->
					<div class="section" id="sec2">
						<h2 id="section-gotable" class="title ">Batch Query Result</h2>

						<div class="inner">
							<div id='infoBlock'></div>
							<div id='errBlock'></div>
							<div id='bqResult'></div>

						</div>
					</div>
				</div>
			</div>
		</div>
		</div>

	</jsp:body>
</t:genericpage>

