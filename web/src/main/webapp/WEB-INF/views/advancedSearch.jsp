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
			div#graphInfo {
				font-size: 12px;
			}
			div#bqFilter {
				margin: 2px;
			}
			button#clearAllDt {
				margin-left: 50px;
			}
			fieldset.fsAttrs {
				background-color: aliceblue;
			}
			fieldset {
				padding: 3px 10px;
				border: 1px solid lightgrey;
				font-size: 12px;
			}
			legend {
				padding: 0 15px;
				border: 1px solid lightgrey;
				background-color: #6196B4;
				border-radius: 4px;
				color: white;
			}
			fieldset input[type=checkbox] {
				height: 10px;
				padding: 0 !important;
				margin-right: 0px !important;
			}
			input[type=text] {
				height: 10px;
				width: 60px;
			}
			fieldset.dfilter {
				border: 1px solid darkslategray;
			}
			table.nbox {
				border-spacing: 5px;
			}
			table.nbox td:first-child {
				background-color: #5F93AF;
				vertical-align: middle;
				text-align: center;
				padding: 0 5px;
				width: 50px;
				color: white;
			}
			form#goSubmit {
				padding: 0;
				border: none;
			}
			form#goSubmit input[type="submit"] {
				background: lightgrey;
				cursor: not-allowed;
			}
			form#goSubmit input[type="submit"].active {
				background: #0978A1;
				cursor: pointer;
			}
			div#sq {
				text-align: center;
				margin-top: 20px;
			}
			div#tableTool {
				position: relative;
				top: -70px;
				right: 0px;
			}
			table.dataTable {
				overflow-x:scroll;
				width:100%;
				display:block;
			}
			table.dataTable span.highlight {
				background-color: yellow;
				font-weight: bold;
				color: black;
			}
			table.dataTable tr {
				 /*width: 100% !important;*/
			 }
			table.dataTable td > li {
				list-style: none;
			}
			table.dataTable td ul {
				list-style-position:outside;
			}
			table.dataTable td ul li:nth-child(1), table.dataTable td ul li:nth-child(2) {
				display: list-item;
			}
			table.dataTable td ul li {
				/* 0 based */
				min-width: 120px;
				display: none;
			}
			button.showMore {
				cursor: pointer;
				color: #0978A1;
			}
			div.srchBox {
				margin: 2px !important;
			}
			div.srchBox input {
				height: 25px !important;
				padding: 0 0 0 25px;
				width: 450px;
			}
			div.srchBox {position: relative;}
			div.srchBox i.fa-search {position: absolute; top: 10px; left: 10px;}
			div.srchBox i.fa-times {
				position: absolute;
				top: 10px;
				left: 460px;
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
			/*.inner {*/
			/*height: auto;*/
			/*!*margin-bottom: 15px;*!*/
			/*}*/
			div#sec2 {
				display: none;
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
				padding: 10px;
			}
			div#infoBlock {
				margin-bottom: 10px;
			}
			form#dnld {
				margin: 0;
				padding: 0;
				border: none;
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
			table#dataInput td:nth-child(2) {
				width: 260px;
			}
			span.cat {
				font-weight: bold;
				font-size:14px;
				color: black;
				padding: 5px;
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
			#fieldList {
				margin-top: 15px;
			}
			#chrSel {
				display: inline;
			}
			div#rangeBox input {padding: 0 3px;}
			input#rstart {width: 55px;}
			input#rend {width: 55px;}

			a#bqdoc, i.pheno, i.disease {
				cursor: pointer;
			}

		</style>

 		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.2.7/raphael.min.js"></script>
        <script type='text/javascript'>


            var selectedCircles = [];
            var drawnCircles = {};
            var idsVar = null;
            var paper = null;

            $(document).ready(function () {
                'use strict';
                // test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';

                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl}";

                $('div#batchQryLink').hide(); // hide batchquery link for batchquery page


                // initialze search example qTip with close button and proper positioning
                var bqDoc = '<h4 id="bqdoc">How to use batch search</h4>'

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

                var ontologyHelp = '<h4 id="pheno">About phenotypes</h4>'
                    + '<a target="_blank" href="http://www.ebi.ac.uk/ols/ontologies/hp">Human</a> and <a target="_blank" href="http://www.ebi.ac.uk/ols/ontologies/hp">mouse</a> phenotypes are described by a structrued and controlled vocabulary for the phenotypic features encountered in human and mouse hereditary and other disease, respectively.<br>'

                var diseaseHelp = '<h4 id="pheno">About diseases</h4>'
				+ 'IMPC has disease details contains known gene associations (via orthology to human disease genes) and known mouse models from the literature (from MGI) for the disease as well as predicted gene candidates and mouse models based on the phenotypic similarity of the disease clinical symptoms and the mouse phenotype annotations. '
                + 'The diseases can be search by<br><b>name</b>: eg. Apert syndrome or<br><b>unique identifiers (Id)</b>:<a href="http://www.omim.org/">OMIM</a> (Online Mendelian Inheritance in Man) or '
                + '<a href="http://www.orpha.net/">Orphanet</a> (The portal for rare diseases and orphan drugs<) or '
                + '<a href="http://decipher.sanger.ac.uk/">DECIPHER</a> (DatabasE of genomiC varIation and Phenotype in Humans using Ensembl Resource).';

                var tipMap = {'a#bqdoc':bqDoc, 'i.pheno':ontologyHelp, 'i.disease':diseaseHelp};
                for(var selector in tipMap) {
                    activateQtip(selector, tipMap[selector]);
                }

                function activateQtip(selector, tip) {
                    $(selector).qtip({
                        hide: false,
                        content: {
                            text: tip,
                            title: {'button': 'close'}
                        },
                        style: {
                            classes: 'qtipimpc',
                            tip: {corner: 'center top'}
                        },
                        position: {
                            my: 'left top',
                            adjust: {x: 0, y: 0}
                        },
                        show: {
                            event: 'click' //override the default mouseover
                        },
                        events: {
                            show: function (event, api) {
                                $('div#docTabs').tabs();
                                $('ul.ui-tabs-nav li a').click(function () {
                                    $('ul.ui-tabs-nav li a').css({
                                        'border-bottom': 'none',
                                        'background-color': '#F4F4F4',
                                        'border': 'none'
                                    });
                                    $(this).css({
                                        'border': '1px solid #666',
                                        'border-bottom': '1px solid white',
                                        'background-color': 'white',
                                        'color': '#666'
                                    });
                                });

                                $('ul.ui-tabs-nav li:nth-child(1) a').click();  // activate this by default
                            }
                        }
                    });
                }
                //----------------------
                // Raphael JS stuff
                //----------------------

                idsVar = {
                    "Gene": {
                        text: "MGI\nGene",
                        x: 480,
                        y: 130,
                        r:30,
                        fields: [
                            {"MGI gene id": "mgiAccessionId"},
                            {"MGI gene symbol": "markerSymbol"},
                            {"MGI gene type": "markerType"},
                            {"MGI gene name": "markerName"},
                            {"chromosome id": "chrId"},
                            {"chromosome start": "chrStart"},
                            { "chromosome end": "chrEnd"},
                            {"chromosome strand": "chrStrand"},
                            {"MGI gene synonym":"markerSynonym"},
                            {"Ensembl gene id":"ensemblGeneId"}

                        ],
                        findBy: [
                            {"symbol":{
                                "property":"markerSymbol",
                                "eg":"Nxn",
                            }
                            },
                            {"ID":{
                                "property":"mgiAccessionId",
                                "eg":"MGI:109331"
                            }
                            }
                        ]
                    },
                    "HumanGeneSymbol": {
                        text: "Human\nOrtholog",
                        x: 720,
                        y: 110,
                        r:30,
                        fields: [
                            {"HGNC gene symbol":"humanGeneSymbol"}
                        ],
                        findBy: [
                            {
                                "symbol": {
                                    "property": "humanGeneSymbol",
                                    "eg": "NXN"
                                }
                            }
                        ]
                    },
//                    "EnsemblGeneId": {
//                        text: "Ensembl\nGene Id",
//                        x: 800,
//                        y: 40,
//                        r:30,
//                        fields: [
//                            {"Ensembl gene id":"ensemblGeneId"}
//                        ],
//                        findBy: [
//                            {
//                                "ID": {
//                                    "property": "ensemblGeneId",
//                                    "eg": "ENSMUSG00000020844"
//                                }
//                            }
//                        ]
//                    },
//                    "MarkerSynonym": {
//                        text: "MGI Marker\nSynonym",
//                        x: 470,
//                        y: 40,
//                        r:30,
//                        fields: [
//                            {"MGI gene synonym":"markerSynonym"}
//                        ]
//                    },
                    "Allele": {
                        text: "Mouse\nAllele",
                        x: 540,
                        y: 40,
                        r:30,
                        fields: [
                            {"MGI allele id":"alleleMgiAccessionId"},
                            {"MGI allele symbol":"alleleSymbol"},
                            {"allele description":"alleleDescription"},
                            {"allele mutation type":"mutationType"},
                            {"ES cell available?":"esCellStatus"},
                            {"mouse available?":"mouseStatus"},
                            {"phenotyping data available?":"phenotypeStatus"}
                        ]
                    },
//                    "DiseaseGene": {
//                        text: "Disease-\nGene",
//                        x: 570,
//                        y: 50,
//                        r:30
//                    },
                    "DiseaseModel": {
                        text: "Human\nDisease",
                        x: 380,
                        y: 50,
                        r:30,
                        fields: [
                            {"disease id":"diseaseId"},
                            {"disease term":"diseaseTerm"},
                            {"disease classes":"diseaseClasses"},
                            {"predicted by IMPC":"impcPredicted"},
                            {"predicted by MGI":"mgiPredicted"},
                            {"disease to model score":"diseaseToModelScore"},
                            {"model to disease score":"modelToDiseaseScore"}
                        ],
                        findBy: [
                            {"name":{
                                "property":"diseaseTerm",
                                "eg":"Atrial Standstill",
                            }
                            },
                            {"ID":{
                                "property":"diseaseId",
                                "eg":"OMIM:108770"
                            }
                            }
                        ]
                    },
                    "MouseModel": {
                        text: "Mouse\nModel",
                        x: 250,
                        y: 120,
                        r:30,
                        fields: [
                            {"allelic composition":"allelicComposition"},
                            {"genetic background":"geneticBackground"},
                            {"homozygous or heterozygous":"homHet"}
                        ]
                    },
                    "Hp": {
                        text: "Human\nPhenotype",
                        x: 210,
                        y: 40,
                        r:30,
                        fields: [
                            {"human phenotype ontology id":"hpId"},
                            {"human phenotype ontology term":"hpTerm"}
                        ],
                        findBy: [
                            {"name":{
                                "property":"hpTerm",
                                "eg":"Hypocalcemia",
                            }
                            },
                            {"ID":{
                                "property":"hpId",
                                "eg":"HP:0002901"
                            }
                            }
                        ]
                    },
                    "Mp": {
                        text: "Mouse\nPhenotype",
                        x: 120,
                        y: 100,
                        r:30,
                        fields: [
                            {"mouse phenotype ontology id":"mpId"},
                            {"mouse phenotype ontology term":"mpTerm"},
                            {"mouse phenotype ontology definition":"mpDefinition"},
                            {"top level mouse phenotype ontology id":"topLevelMpId"},
                            {"top level mouse phenotype ontology term":"topLevelMpTerm"},
                            {"mouse phenotype ontology term synonym": "ontoSynonym"}

                        ],
                        findBy: [
                            {"name":{
                                "property":"mpTerm",
                                "eg":"abnormal midbrain development",
                            }
                            },
                            {"ID":{
                                "property":"mpId",
                                "eg":"MP:0003864"
                            }
                            }
                        ]
                    }
//                    "OntoSynonym": {
//                        text: "Mouse\nPhenotype\nSynonym",
//                        x: 45,
//                        y: 60,
//                        r: 30,
//                        fields: [
//                            {"mouse phenotype ontology term synonym": "ontoSynonym"}
//                        ]
//                    }
                };

                paper = new Raphael(document.getElementById('graph'), 600, 180);

                for (var id in idsVar) {
                    var text = id;
                    drawCircle(id, idsVar[id].text, idsVar[id].x, idsVar[id].y, idsVar[id].r, paper);
                }

                // connect circles
                connectCircle("Gene", "HumanGeneSymbol");
                //connectCircle("Gene", "EnsemblGeneId");
                //connectCircle("Gene", "MarkerSynonym");
                connectCircle("Gene", "Allele");
                connectCircle("MouseModel", "Gene");
                connectCircle("MouseModel", "Mp");
//                connectCircle("DiseaseGene", "Gene");
//                connectCircle("DiseaseGene", "Hp");
                connectCircle("DiseaseModel", "Gene");
                connectCircle("DiseaseModel", "Allele");
                connectCircle("DiseaseModel", "MouseModel");
                connectCircle("DiseaseModel", "Hp");
                connectCircle("DiseaseModel", "Mp");
                connectCircle("Mp", "Hp");
                //connectCircle("Mp", "OntoSynonym");

                $('button#clearAllDt').click(function(){
                    var nodeType = mapInputId2NodeType($('input.bq:checked').attr('id'));
                    for(var c=0; c<selectedCircles.length; c++){
                        if (selectedCircles[c].data('id') != nodeType) {
                            var circle = selectedCircles[c];
                            selectedCircles.slice(c, 1);
                            // now remove color and filter
                            circle.attr({"stroke":"black", "stroke-width": 1});
                            removeDatatypeFiltersAttributes(circle.data("id"));
                        }
                    }
                	// grayout submit button as there is no filters/attributes selected
                    $("input[type='submit']").prop('disabled', true).removeClass('active');
                    return false;
                });

                $( "#accordion" ).accordion();

            });
                function connectCircle(id1, id2) {

                    var x1 = idsVar[id1].x;
                    var y1 = idsVar[id1].y;
                    var r1 = idsVar[id1].r;
                    var x2 = idsVar[id2].x;
                    var y2 = idsVar[id2].y;
                    var r2 = idsVar[id2].r;

                    // Compute the path strings
                    var c1path = circlePath(x1, y1, r1);
                    var c2path = circlePath(x2, y2, r2);
                    var linepath = linePath(x1, y1, x2, y2);

                    // Get the path intersections
                    // In this case we are guaranteed 1 intersection, but you could find any intersection of interest
                    var c1i = Raphael.pathIntersection(linepath, c1path)[0];
                    var c2i = Raphael.pathIntersection(linepath, c2path)[0];

                    var line = paper.path(linePath(c1i.x, c1i.y, c2i.x, c2i.y));

                    var lineColor = "#c1d7d7";  // different color to distinguish between DiseaeGene and DiseaseModel
                    if ( (id1=="DiseaseGene" && id2=="Hp") || (id1=="DiseaseGene" && id2=="Gene") ){
                        lineColor = "#507c7c";
                    }

                    line.attr ("stroke", lineColor);
                }

                // Computes a path string for a circle
                function circlePath(x, y, r) {
                    return "M" + x + "," + (y - r) + "A" + r + "," + r + ",0,1,1," + (x - 0.1) + "," + (y - r) + " z";
                }

                // Computes a path string for a line
                function linePath(x1, y1, x2, y2) {
                    return "M" + x1 + "," + y1 + "L" + x2 + "," + y2;
                }

                function fetchDatatypeProperties() {

                    var kv = {};
                    kv["properties"] = [];

                    //-----------------
                    //   attributes
                    //-----------------
                    $('fieldset input:checked').each(function () {
                        var dataType = $(this).parent().attr('id');
                        var property = $(this).val();

                        // some conversion here for markerSynonym and Ontosynonym
						// as they are included in gene or mp, hp for user friendly
						// purpose, but they themselves are nodeEntities
						// other than gene, mp or hp
                        if (property == "markerSynonym"){
                            dataType = "MarkerSynonym";
						}
						else if (property == "ontoSynonym"){
                            dataType = "OntoSynonym";
						}
						else if (property == "ensemblGeneId"){
						    dataType = "EnsemblGeneId";
						}

                        console.log(dataType + " --- " + property);

                        if (!kv.hasOwnProperty(dataType)) {
                            kv[dataType] = [];
                        }
                        kv[dataType].push(property);

                        kv["properties"].push(property);
                    });

                    //-----------------
                    //     filters
					//-----------------
					// chr range for genes
                    if ($('fieldset#chromosome').is(":visible")) {
                        var chrId = $('select#chrSel').val();
                        var chrStart = $('input#rstart').val();
                        var chrEnd = $('input#rend').val();

						kv = checkChrCoords(chrId, chrStart, chrEnd, kv);
						if (kv == false) {
							return false;
						}
                    }

                    // phenotype child level
                    $('select.inputlevel').each(function() {
                        var fs = $(this).parent();
                        if (fs.is(":visible")) {
                            kv[fs.find('select').attr('id')] = fs.find('select').val();
                        }
                    });

                    // disease, mp, hp autosuggest
					var srchSugs = ["Mp", "Hp", "DiseaseModel"];
					for (var s=0; s<srchSugs.length; s++) {
					    var v = srchSugs[s];
                        var type = 'srch' + v;
                        kv[type] = [];
                        if ($('fieldset#'+v).is(":visible")){
							$('input.'+type).each(function(){
								kv[type].push($(this).val());
							});
						}
                    }

                    console.log("check kv");
                    console.log(kv)
                    return kv;
                }

                function checkChrCoords(chrId, chrStart, chrEnd, kv) {
					if (chrId == "" && chrStart == "" && chrEnd == ""){
					    return kv;
					}
                    else if (chrId == "") {
                        alert("ERROR: chromosome is not chosen");
                        return false;
                    }
                    else if (chrStart != "" || chrEnd != "") {

						if (chrStart == 0 || chrEnd == 0) {
							alert("ERROR: chromosome coordinate cannot be 0");
							return false;
						}
						else if (!$.isNumeric(chrStart) || !$.isNumeric(chrEnd)) {
							alert("ERROR: chromosome coordinate must be numeric");
							return false;
						}
						else if (chrStart == chrEnd) {
							alert("ERROR: chromosome start and end cannot be the same");
							return false;
						}
						else if (parseInt(chrStart) > parseInt(chrEnd)) {
							alert("ERROR: chromosome range is not right");
							return false;
						}
						else {
							kv['chrRange'] = "chr" + chrId + ":" + chrStart + "-" + chrEnd;
							return kv;
						}
                	}
                	else if (chrId != "" && chrStart == "" && chrEnd == ""){
						kv["chr"] = chrId;
						return kv;
					}
                }

                function mapInputId2NodeType(key){
                    var map = {
                        "mouseMarkerSymbol":"Gene",
                        "mouseGeneId":"Gene",
                        "geneChr":"Gene",
                        "ensembl":"EnsemblGeneId",
                        "mpTerm":"Mp",
                        "mpId":"Mp",
                        "human_marker_symbol":"HumanGeneSymbol",
                        "hpTerm":"Hp",
						"hpId":"Hp",
                        "diseaseTerm":"DiseaseModel",
						"diseaseId":"DiseaseModel"
                    };
                    return map[key];

                }

                function convertParamToObject(params){
                    var oConf = {};
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
                    //var inputType = thisInput.attr('id');
                    var solrBq = "";
                    if ( thisInput.hasClass('srchMp') ){
                        docType = 'mp';
                        solrBq = "&bq=mp_term:*^90 mp_term_synonym:*^80 mp_narrow_synonym:*^75";
                    }
                    else if ( thisInput.hasClass('srchHp') ){
                        docType = 'hp';
                        solrBq = "&bq=hp_term:*^90 hp_term_synonym:*^80";
                    }
                    else if ( thisInput.hasClass('srchDiseaseModel') ){
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
                                                if (docType == 'hp' && term.startsWith("MP:")){
                                                    continue;  // due to hp-mp hybrid ontology, but we don't need mp here
												}

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
                                                var termStr = thisInput.val().trim(' ').split(' ').join('|').replace(/\*|"|'/g, '').replace(/\(/g,'\\(').replace(/\)/g,'\\)');

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
                            var inputVal = $(ui.item.label).text().replace(/<\/?span>|^\w* : /g,'');
                            // assign value to input box
                            this.value = inputVal.trim();

                            event.preventDefault(); // Prevent the default focus behavior.
                        },
                        minLength: 3,
                        select: function( event, ui ) {
                            // select by mouse / KB
                            var q = this.value;
                            q = encodeURIComponent(q).replace("%3A", "\\%3A");

                            $('textarea#pastedList').val('"'+ decodeURIComponent(q) + '"');

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

                            $('textarea#pastedList').val('"'+ input + '"');
                            $(".ui-menu-item").hide();
                        }
                    });

                }

                //-------------------
				// SUBMIT THE FORM
				//-------------------

                function submitQuery(){

                   	var hasUnchecked = [];
                    $('fieldset.fsAttrs').each(function(){
                       if ($(this).find('input:checked').length == 0){
                           hasUnchecked.push($(this).find('legend').text());
					   }
					});
                    if (hasUnchecked.length != 0){
                        alert('Oops,\n' + hasUnchecked.join(",\n") + ' unchecked...');
                        return false;
					}

                    var oJson = fetchDatatypeProperties();
					if ( oJson == false){
						return false;
					}

//                    if ($('fieldset.fsAttrs input:checked').length == 0){
//                        alert('Oops, customized output filter is not checked...');
//                        return false;
//                    }
//                    else {
//                        oJson = fetchDatatypeProperties();
//                        if ( oJson == false){
//                            return false;
//                        }
//                    }

                    refreshResult(); // refresh first

					//alert('before table')
                    prepare_dataTable(oJson.properties);

                    var oConf = {};
                    oConf.properties = oJson.properties.join(","); // order is important


//					var excludes = ["properties", "childLevel", "chr"];
//					for(var e=0; e < excludes.length; e++) {
//                        delete datatypeProperties[excludes[e]];
//                    }

                    oConf.params = JSON.stringify(oJson);
                   // oConf.dataType = mapInputId2NodeType(currDataType);

					console.log(oConf);
                    fetchBatchQueryDataTable(oConf);

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

                                //fetchBatchQueryDataTable(oConf);
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
                    $("fieldset").find("input:checked").each(function(){
                        var dbLabel = $(this).attr("name");
                        if ( ! (dbLabel in labelList) ){
                            labelList[dbLabel] = [];
                        }
                        labelList[dbLabel].push($(this).val());
                        fllist.push($(this).val());

                        console.log(dbLabel + " -- " + $(this).val());
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

                function getUniqIdsStr(inputIdListStr) {
                    var ids = inputIdListStr.split(",");
                    return $.fn.getUnique(ids).join(",");
                }
                function getFirstTenUniqIdsStr(inputIdListStr) {
                    var ids = inputIdListStr.split(",");
                    return $.fn.getUnique(ids.slice(0,10)).join(",");
                }

                function fetchBatchQueryDataTable(oConf) {
                    //console.log(oConf);
                    // deals with duplicates and take a max of first 10 records to show the users
                    //oConf.idlist = getFirstTenUniqIdsStr(getUniqIdsStr(oConf.idlist));
                    //oConf.idlist = getUniqIdsStr(oConf.idlist);
                    //console.log(oConf);

//                    console.log($('table#batchq'));
//                    console.log($('table#batchq').html())

                    //alert("here")
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
//                        "aoColumns": [
//                            {"bSearchable": true, "sType": "html", "bSortable": true}
//                        ],
						/*  "columnDefs": [
						 { "type": "alt-string", targets: 3 }   //4th col sorted using alt-string
						 ], */
                        //"aaSorting": [[ 0, "asc" ]],  // default sort column order, won't work if bSort is false
						/*"aoColumns": [
						 {"bSearchable": true, "sType": "html", "bSortable": true},
						 {"bSearchable": true, "sType": "string", "bSortable": true},
						 {"bSearchable": true, "sType": "string", "bSortable": true},
						 {"bSearchable": true, "sType": "string", "bSortable": true},
						 {"bSearchable": true, "sType": "string", "bSortable": true},
						 {"bSearchable": false, "sType": "html", "bSortable": true}
						 ],*/
                        "initComplete": function (oSettings) {  // when dataTable is loaded

                            $('div#sec2').show();

                            document.getElementById('sec2').scrollIntoView(true); // scrolls to results when datatable loads

							// js to toggle hidden values in cells
							$('button.showMore').click(function(){
							    if ($(this).hasClass('hideMe')){
                                    $(this).removeClass("hideMe");
                                    $(this).text("show all (" + $(this).attr('rel') + ")");
                                    $(this).siblings('li:gt(1)').hide();

								}
								else {
							        $(this).addClass("hideMe");
                                    $(this).text("show less");
                                    $(this).siblings('li:gt(1)').show();
								}
							});


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
                                else if ( formId == 'goSubmit' ){
                                    //idList = parsePastedList($('textarea#pastedList').val(), currDataType);
                                    //doExport(currDataType, fileType, fllist, idList, isForm);
                                }
                                else {
                                    //idList = '*';
                                    //doExport(currDataType, fileType, fllist, idList, isForm);
                                }

                                if (formId == 'ajaxForm' ){
                                    return false;
                                }
                            });

                            $('body').removeClass('footerToBottom');
                        },
                        "ajax": {
                            "url": baseUrl + "/dataTableNeo4jAdvSrch?",
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
                    }
                }

                function drawCircle(id, text, x, y, r, paper) {
                    paper.text(x, y, text);
                    var label = paper.text(x, y, text).attr({fill: '#fff'});
                    label.attr({opacity: 100, 'font-size': 10}).toFront();

                    var circle = paper.circle(x, y, r).attr({gradient: '90-#526c7a-#64a0c1'}).toBack();
                    circle.data('id', id);

                    var jCircle = $(circle.node);
                    var jLabel = $(label.node);

                    drawnCircles[id] = jCircle;

                    jCircle.click(function () {

                        if ($(this).attr("stroke") == "darkorange") {
							$(this).attr({"stroke":"black", "stroke-width": 1});
							removeDatatypeFiltersAttributes(circle.data("id"));
                        }
                        else {
                            if (! selectedCircles.includes(circle)){
                                selectedCircles.push(circle);
                            }
                            $(this).attr({"stroke":"darkorange", "stroke-width": 5});
                            addDatatypeFiltersAttributes(circle.data("id"));
                        }

                        if ($('fieldset.fsAttrs').size() > 0){
                            $("input[type='submit']").prop('disabled', false).addClass('active');
                        }
                        else {
                            $("input[type='submit']").prop('disabled', true).removeClass('active');
                        }
                    });

                    jLabel.click(function () {
                        jCircle.click();
                    });

                    label.node.onmouseover = function () {
                        this.style.cursor = 'pointer';
                    }

                    circle.node.onmouseover = function () {
                        this.style.cursor = 'pointer';
                    }
                }

                function addDatatypeFiltersAttributes(dataType) {

                    var objs = idsVar[dataType].fields;

                    var inputs = "";
                    for (var i = 0; i < objs.length; i++) {
                        for (var k in objs[i]) {
                            var display = k;
                            var dbproperty = objs[i][k];
                            //console.log(k + " - " + objs[i][k]);
                            inputs += "<input type='checkbox' name='" + display + "' value='" + dbproperty + "'>" + display;
                        }
                    }
                    var legend = "<legend>" + idsVar[dataType].text + " attributes</legend>";
                    var attr = "<fieldset class='fsAttrs " + dataType + "' id='" + dataType + "'>" + legend + inputs + "</fieldset>";
                    $('div#dataAttributes').append(attr);

                    console.log("clicked on: "+ dataType);

                    if (dataType == "Gene"){
                        addChomosomeRangerFilter(dataType);
						compartmentAttrsFilters(dataType, "Gene");
					}
                    else if (dataType == "Mp"){
                        addAutosuggestFilter(dataType);
                        addOntologyChildrenLevelFilter(dataType);
                        compartmentAttrsFilters(dataType, "MP");
                    }
                    else if (dataType == "Hp"){
                        addAutosuggestFilter(dataType);
                        addOntologyChildrenLevelFilter(dataType);
                        compartmentAttrsFilters(dataType, "HP");
                    }
                    else if (dataType == "DiseaseModel"){
                        addAutosuggestFilter(dataType);
                        compartmentAttrsFilters(dataType, "Disease");
                    }

                }

                function removeDatatypeFiltersAttributes(dataType) {
                    $('fieldSet#' + dataType).remove();

                    if (dataType == "Gene"){
                        $('fieldset#chromosome').remove();
					}
                    if (dataType == "Mp"){
                        $('fieldset#MouseOntoLevel').remove();
                        $('fieldset#MpFilter').remove();
                    }
                    if (dataType == "Hp"){
                        $('fieldset#HumanOntoLevel').remove();
                        $('fieldset#HpFilter').remove();
                    }
                    if (dataType == "DiseaseModel"){
                        $('fieldset#DiseaseModelFilter').remove();
                    }
                }

                function compartmentAttrsFilters(dataType, name){
                    var table = $("<table class='nbox'><td></td><td></td></table>");

					table.find('td:first-child').html(name);
					$('fieldset.'+ dataType).appendTo(table.find('td:nth-child(2)'));

					table.appendTo($('div#dataAttributes'));
				}

                function addChomosomeRangerFilter(dataType){

                    // add chromosome range filter
                    var chrs = [];
                    chrs.push("")
                    for (var r = 1; r < 20; r++) {
                        chrs.push(r);
                    }
                    chrs.push("X", "Y", "MT");

                    var chrSel = "";
                    for (var i = 0; i < chrs.length; i++) {
                        if (i == 0) {
                            chrSel += '<option value="' + chrs[i] + '" selected="selected">' + chrs[i] + '</option>';
                        }
                        else {
                            chrSel += '<option value="' + chrs[i] + '">' + chrs[i] + '</option>';
                        }
                    }

                    var legend = '<legend>Mouse chromosome filter</legend>';
                    var inputs = 'Chr: <select id="chrSel">' + chrSel + '</select> ' +
                        'Start: <input id="rstart" type="text" name="chr"> ' +
                        'End: <input id="rend" type="text" name="chr"> (restricts genes on region of this chromosome)';
                    var filter = "<fieldset id='chromosome' class='dfilter " + dataType + "'>" + legend + inputs + "</fieldset>";

                    $('div#dataAttributes').append(filter);
                }

                function addOntologyChildrenLevelFilter(dataType){
                    var levels = [0,3,'all'];
                    var levelSel = "";
                    for (var i = 0; i < levels.length; i++) {
                        if (i == 0) {
                            levelSel += '<option value="' + levels[i] + '" selected="selected">' + levels[i] + '</option>';
                        }
                        else {
                            levelSel += '<option value="' + levels[i] + '">' + levels[i] + '</option>';
                        }
                    }

                    var id, species = null;
                    if ( dataType == 'Mp'){
                        id = "MouseChildLevel";
                        species = "Mouse";
					}
					else if ( dataType == 'Hp'){
                        id = "HumanChildLevel";
                        species = "Human";
                    }
                    var legend = "<legend>" + species + " phenotype filter (depth of children)</legend>";
                    var input = "level:<select class='inputlevel' id=" + id + ">" + levelSel + "</select>";
                    var filter = "<fieldset id='" + species + "OntoLevel' class='dfilter " + dataType + "'>" + legend + input + " (0 means none, 3 means three levels down in the ontology hierarchy, etc.)</fieldset>";

                    $('div#dataAttributes').append(filter);
				}

				function addAutosuggestFilter(dataType){
				    var idname = dataType.toLowerCase();
					var input = "<div class='block srchBox'>" +
						"<i class='fa fa-search'></i>" +
						"<input class='inputfilter srch" + dataType + "' value='search'>" +
						"<i class='fa fa-times' id='" + idname + "Clear'></i>  " +
						"</div>";

					var legendLabel, buttLabel, restriction = null;
					//var buttLabel = null;
                    //var restriction = null;
					if (dataType == "Mp"){
                        legendLabel = "Mouse phenotype filter";
                        buttLabel = "Add phenotype";
                        restriction = "Narrow your query to the mouse phenotype you are interested in";
					}
					else if (dataType == "Hp"){
                        legendLabel = "Human phenotype filter";
                        buttLabel = "Add phenotype";
                        restriction = "Narrow your query to the human phenotype you are interested in";
					}
                    else if (dataType == "DiseaseModel"){
                        legendLabel = "Human disease filter";
                        buttLabel = "Add disease";
                        restriction = "Narrow your query to the human disease you are interested in";
                    }

					var legend = "<legend>"+ legendLabel + "</legend>";
                    var butt = "<button class='ap'>" + buttLabel + "</button>";
					var filter = "<fieldset id='" + dataType + "Filter' class='dfilter " + dataType + "'>" + legend + restriction + input + butt + "</fieldset>";

					$('div#dataAttributes').append(filter);

                    addAutosuggest($('input.srch' + dataType));

					// clear input
                    $("fieldset#" + dataType + "Filter").on("click", "input.srch" + dataType, function(){
                        if ($(this).val() == "search"){
                            $(this).val("");
						}
                    });
                    $("fieldset#" + dataType + "Filter").on("click", "i#" + idname + "Clear", function(){
						$(this).siblings($("input.srch" + dataType)).val("");
					});

                    // add new input
                    $("fieldset#" + dataType + "Filter button.ap").click(function(){
                        $(input).insertAfter($("fieldset#" + dataType + "Filter .srchBox").last());
                        // allow remove input just added
                        $("<button class='pr'>Remove</button>").insertAfter($("fieldset#" + dataType + "Filter .inputfilter").last());
                        $('button.pr').click(function(){
                           $(this).siblings('input').parent().remove();
						});

                        addAutosuggest($('input.srch' + dataType).last());

                        return false;
                    });
                }

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
						<h1 class="title" id="top">IMPC Dataset Advanced Search<a id="bqdoc" class=""><i class="fa fa-question-circle pull-right"></i></a></h1>
						<form id='goSubmit'>
						<div class="section">

							<!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
							<div class='inner' id='srchBlock'>

								<div class='fl2'>

									<h6 class='bq'>IMPC Data Model</h6><hr>
									<div id="graphInfo">Click the datatypes to add/remove filters or attributes.  <button id="clearAllDt">Clear all selections</button></div>
									<div id='graph'></div>
									<h6 class='bq'>Customized data filter and attributes</h6><hr>
									<div id="dataAttributes"></div>
								</div>
							</div>
						</div> <!-- end of section -->
							<div id='sq'>
								<input type="submit" id="pastedlistSubmit" onclick="return submitQuery()" />
								<input type="reset" name="reset" value="Reset">
							</div>
						</form>
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

