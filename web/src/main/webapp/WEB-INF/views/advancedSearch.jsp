<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
	<jsp:attribute name="title">IMPC advanced search</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Advanced search</a></jsp:attribute>
	<jsp:attribute name="header">

        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
		<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.range.css">

        <style type="text/css">

			div.region-content {
				margin-top: 53px;
			}
			div#graphInfo {
				font-size: 12px;
			}

			/* two col div */
            .fl2 {
                /*width: 82%;*/
                /*float: right;*/
				/*border-left: 1px solid #C1C1C1;*/
				/*padding-left: 10px;*/
            }
            /*.fl1 {*/
                /*float: none; !* not needed, just for clarification *!*/
                /*!* the next props are meant to keep this block independent from the other floated one *!*/
                /*width: 160px;*/
                /*!*padding-right: 10px;*!*/
                /*!*border-right: 1px solid #C1C1C1;*!*/
				/*font-size: 12px;*/
            /*}*/
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
                margin-bottom: 3px;
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
				width: 80px !important;
				color: white;
			}
			i.pr {
				margin-left: 90px;
				cursor: pointer;
			}
			form#goSubmit {
				padding: 0;
				border: none;
			}
			/*form#goSubmit input[type="submit"] {*/
				/*background: lightgrey;*/
				/*!*cursor: not-allowed;*!*/
			/*}*/
			/*form#goSubmit input[type="submit"].active {*/
				/*background: #0978A1;*/
				/*cursor: pointer;*/
			/*}*/
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
			table.dataTable {
				border-collapse: collapse;
			}
			table.dataTable td {
				border: 1px solid #F2F2F2;
			}
			table.dataTable td:last-child {
				border-right: none;
			}
			button.showMore {
				cursor: pointer;
				color: #0978A1;
			}
			div.srchBox {
				margin: 2px !important;
			}
			div.srchBox input.termFilter {
				height: 25px !important;
				padding: 0 0 0 25px;
				width: 350px;
			}
			div.srchBox {position: relative;}
			div.srchBox i.fa-search {position: absolute; top: 10px; left: 10px;}
			div.srchBox i.fa-times {
				position: absolute;
				top: 10px;
				left: 360px;
				cursor: pointer;
			}
			button.andOr {
				position: absolute;
				top: 6px;
				left: 590px;
				margin-left: 5px;
                padding: 0 !important;
				display: none;
			}
			.andOr2 {
				display: none;
			}
			button.andOr2, button.andOrClear {

				color: #942a25;
				border: 1px solid white;
				border-radius: 3px;
				padding: 1px;
				margin-left: 10px;
				width: 35px;
			}
			.andOrClear {
				color: black;
				width: 45px;
			}
			button.ontoview {
				position: absolute;
				top: 6px;
				left: 700px;
				margin-left: 5px;
                padding: 0 !important;
			}
			button {
				cursor: pointer;
			}
			button.ap{
				background-color: darkorange;
				color: white;
				border: none;
				border-radius: 3px;
				padding: 5px 12px;
				margin: 3px 0 10px 3px;
			}
            span.pvalue input {
                padding: 3px !important;
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
				margin-top: 15px;
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
				width: 40px;
			}
			div#rangeBox input {padding: 0 3px;}
			input#rstart {width: 55px;}
			input#rend {width: 55px;}

			a#bqdoc, i.pheno, i.disease {
				cursor: pointer;
			}
			div.sliderBox {
				margin: 20px 0 30px 10px;
			}
			i.fa-info-circle {
				cursor: pointer;
				font-size: 18px;
				padding-left: 15px;
			}
			img.boolHow {
				display: none;
				border: 1px solid orange;
				margin-top: 20px;
				padding-top: 10px;
			}
			span.msg {
				display: none;
				margin-right: 50px;
			}
			.andOrClear {
				display: none;
			}
			div.modal {
				display:    none;
				position:   fixed;
				z-index:    1000;
				top:        0;
				left:       0;
				height:     100%;
				width:      100%;
				background: rgba( 255, 255, 255, .8 )
				url('img/ajax-spinner.gif')
				50% 50%
				no-repeat;
			}
			/* When the body has the loading class, we turn
               the scrollbar off with overflow:hidden */
			body.loading {
				overflow: hidden;
			}

			/* Anytime the body has the loading class, our
               modal element will be visible */
			body.loading .modal {
				display: block;
			}
			div#single {
				margin-bottom: 8px;
			}
			div#chrinfo {
				border: 1px solid orange;
				margin: 15px 0 5px 0;
				padding: 10px;
				display: none;
				-webkit-column-count: 5; /* Chrome, Safari, Opera */
				-moz-column-count: 5; /* Firefox */
				column-count: 5;
				-webkit-column-gap: 40px; /* Chrome, Safari, Opera */
				-moz-column-gap: 40px; /* Firefox */
				column-gap: 40px;
				-webkit-column-rule: 1px solid lightblue; /* Chrome, Safari, Opera */
				-moz-column-rule: 1px solid lightblue; /* Firefox */
				column-rule: 1px solid lightblue;
			}
			span.chrname {
				display: inline-block;
				width: 40px;
			}
			span.chrlen {
				padding-left: 20px;
				width: 45px;
				text-align: left;
			}
			div#userFilters {
				padding: 10px;
				margin-bottom: 10px;
				border: 1px solid gray;
			}
			img.advsrchHow {
				width: 440px;
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
                var bqDoc = '<h4 id="bqdoc">How to use the advanced search</h4>'
					+ "<p>The interface allows you to <b>search for genes that have associations with one or more mouse phenotypes AND one or more human diseases</b>.</p>"
                + "<p>You can apply <b>filters</b> on one or all of the Phenotype / Gene / Disease section on the interface by ticking the checkboxes or radio buttons.</p>"
                + "<p>The '<b>Customized output columns</b>' allows you to view your results in different levels of granularity.</p>"
                + "<p>After you have applied filters and selected columns to display, simply hit the '<b>Submit Query' button</b> at the bottom of the interface for the result. The result will then appear below the 'Submit Query' button.<br>See figure below.</p>"
                + "<p>On top of the result, your <b>search filters</b> will be shown to provide you a handy recap of how you have done the search.</p>"
                + "<p>On the upper right hand corner, there are <b>3 download buttons</b> for popular output formats to export the full dataset.</p><p>"
				+ "<img class='advsrchHow' src='${baseUrl}/img/advSearch/advsrch_result.png'/>";

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
                        x: 400,
                        y: 140,
                        r:30,
                        fields: [
                            {"MGI allele symbol":"alleleSymbol"},
                            {"MGI allele id":"alleleMgiAccessionId"},
                            {"HGNC gene symbol (human ortholog)":"humanGeneSymbol"},   // human ortholog
                            {"MGI gene symbol": "markerSymbol"},
                            {"MGI gene id": "mgiAccessionId"},
                            {"MGI gene type": "markerType"},
                            {"MGI gene name": "markerName"},
                            {"chromosome id": "chrId"},
                            {"chromosome start": "chrStart"},
                            { "chromosome end": "chrEnd"},
                            {"chromosome strand": "chrStrand"},
                            {"MGI gene synonym":"markerSynonym"},
                            {"Ensembl gene id":"ensemblGeneId"},
							// allele
                            {"allele description":"alleleDescription"},
                            {"allele type":"alleleType"},  // tm1a, tm1b, etc
                            {"allele mutation type":"mutationType"},
                            {"ES cell available?":"esCellStatus"},
                            {"mouse available?":"mouseStatus"},
                            {"phenotyping data available?":"phenotypeStatus"}
                        ],
                        selected: [
                            "alleleSymbol", "markerSymbol"
                        ]
                    },
                    "HumanGeneSymbol": {
                        text: "Human\nOrtholog",
                        x: 660,
                        y: 130,
                        r:30,
                        fields: [
                            {"HGNC gene symbol":"humanGeneSymbol"}
                        ],
                        selected: [

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
                        x: 500,
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
                        x: 350,
                        y: 50,
                        r:30,
                        fields: [
                            {"disease term":"diseaseTerm"},
                            {"disease id":"diseaseId"},
                            {"disease to model score":"diseaseToModelScore"},
                            {"disease classes":"diseaseClasses"},
                            {"predicted by IMPC":"impcPredicted"},
                            {"predicted by MGI":"mgiPredicted"}
                           // {"model to disease score":"modelToDiseaseScore"}
                        ],
                        selected: [
                            "diseaseTerm"
                        ]
                    },
                    "MouseModel": {
                        text: "Mouse\nModel",
                        x: 230,
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
                        x: 190,
                        y: 40,
                        r:30,
                        fields: [
                            {"human phenotype ontology id":"hpId"},
                            {"human phenotype ontology term":"hpTerm"}
                        ],
                        selected: [

                        ]
                    },
                    "Mp": {
                        text: "Mouse\nPhenotype",
                        x: 60,
                        y: 100,
                        r:30,
                        fields: [
                            {"mouse phenotype ontology term":"mpTerm"},
                            {"mouse phenotype ontology id":"mpId"},
                            {"mouse phenotype ontology definition":"mpDefinition"},
                            {"top level mouse phenotype ontology id":"topLevelMpId"},
                            {"top level mouse phenotype ontology term":"topLevelMpTerm"},
                            {"mouse phenotype ontology term synonym": "ontoSynonym"},
                            {"measured parameter name": "parameterName"},
                            {"p value": "pvalue"}
                        ],
                        selected: [
                           	"mpTerm",
							"parameterName",
                        	"pvalue"
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


                addDatatypeFiltersAttributes("Mp");
                addDatatypeFiltersAttributes("Gene");
                addDatatypeFiltersAttributes("DiseaseModel");

//                paper = new Raphael(document.getElementById('graph'), 570, 180);
//
//                for (var id in idsVar) {
//                    var text = id;
//                    drawCircle(id, idsVar[id].text, idsVar[id].x, idsVar[id].y, idsVar[id].r, paper);
//                }

//                // connect circles
//                connectCircle("Gene", "HumanGeneSymbol");
//                //connectCircle("Gene", "EnsemblGeneId");
//                //connectCircle("Gene", "MarkerSynonym");
//                connectCircle("Gene", "Allele");
//                connectCircle("MouseModel", "Gene");
//                connectCircle("MouseModel", "Mp");
////                connectCircle("DiseaseGene", "Gene");
////                connectCircle("DiseaseGene", "Hp");
//                connectCircle("DiseaseModel", "Gene");
//                connectCircle("DiseaseModel", "Allele");
//                connectCircle("DiseaseModel", "MouseModel");
//                connectCircle("DiseaseModel", "Hp");
//                connectCircle("DiseaseModel", "Mp");
//                connectCircle("Mp", "Hp");
//                //connectCircle("Mp", "OntoSynonym");

//				// default search type
//				$("input[name='queryType'][value='Gene']").prop("checked", true);
//                drawnCircles["Gene"].click();

//				// gene or phenotype centric search
//				$("input[name='queryType']").on('change', function () {
//					// remove overlay on top of graph to make circles clickable after a query type is chosen
//                    $('#overlay').hide();
//
//                    var dataType = $("input[name='queryType']:checked").val();
//                    var circle = drawnCircles[dataType];
//
//                    // select if not yet
//                    if (circle.attr("stroke") != "darkorange") {
//                        drawnCircles[dataType].click();
//                    }
//				});

//                $('button#clearAllDt').click(function(){
//                    var nodeType = mapInputId2NodeType($('input.bq:checked').attr('id'));
//                    for(var c=0; c<selectedCircles.length; c++){
//                        if (selectedCircles[c].data('id') != nodeType) {
//                            var circle = selectedCircles[c];
//                            selectedCircles.slice(c, 1);
//                            // now remove color and filter
//                            circle.attr({"stroke":"black", "stroke-width": 1});
//                            removeDatatypeFiltersAttributes(circle.data("id"));
//                        }
//                    }
//                	// grayout submit button as there is no filters/attributes selected
//                    $("input[type='submit']").prop('disabled', true).removeClass('active');
//                    return false;
//                });

                $( "#accordion" ).accordion();

            });

            function catchOnReset(){
//                var dataType = $("input[name='queryType']:checked").val();
//				    console.log(dataType);
//                $("input[name=mygroup][value=" + dataType + "]").prop('checked', true);
				//$('textarea').val("");
			}
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

                    //-------------------------
                    //   columns (attributes)
                    //-------------------------
                    $('fieldset input.column:checked').each(function () {
                        var dataType = $(this).parent().attr('id');
                        var property = $(this).val();

                        //console.log("check property: " + property);
                        // some conversion here for markerSynonym and Ontosynonym
                        // as they are included in gene or mp, hp for user friendly
                        // purpose, but they themselves are nodeEntities
                        // other than gene, mp or hp
                        if (property == "markerSynonym") {
                            dataType = "Gene";
                        }
                        else if (property == "ontoSynonym") {
                            dataType = "OntoSynonym";
                        }
                        else if (property == "ensemblGeneId") {
                            dataType = "Gene";
                        }
                        else if (property == 'parameterName'){
                            dataType = 'StatisticalResult';
						}
                        else if (property == 'pvalue'){
                            dataType = 'StatisticalResult';
                        }
                        else if (property == "alleleMgiAccessionId") {
                            dataType = "Allele";
                        }
                        else if (property == "alleleSymbol") {
                            dataType = "Allele";
                        }
                        else if (property == "alleleDescription") {
                            dataType = "Allele";
                        }
                        else if (property == "alleleType") {
                            dataType = "Allele";
                        }
                        else if (property == "mutationType") {
                            dataType = "Allele";
                        }
                        else if (property == "esCellStatus") {
                            dataType = "Allele";
                        }
                        else if (property == "mouseStatus") {
                            dataType = "Allele";
                        }
                        else if (property == "phenotypeStatus") {
                            dataType = "Allele";
                        }

                        //console.log("datatype: "+ dataType + " --- " + property);

                        if (!kv.hasOwnProperty(dataType)) {
                            kv[dataType] = [];
                        }
                        kv[dataType].push(property);

                        kv["properties"].push(property);
                    });

                    //-----------------
                    //     filters
                    //-----------------

					// noDefault for mp search (no children)

					var shownFilter = [];

                    var phenotypeSexes = [];
                    $("fieldset.MpFilter input[name='sex']:checked").each(function () {
                        if ($(this).val() != undefined) {
                            phenotypeSexes.push($(this).val());
                        }
                    });
                    if (phenotypeSexes.length > 0) {
                        kv['phenotypeSexes'] = phenotypeSexes;
                        shownFilter.push("mouse sex = '" + phenotypeSexes + "'");
                    }


                    if ($('input#noMpChild').is(':checked')){
                        kv['noMpChild'] = true;
                        shownFilter.push("Exclude nested phenotypes = 'yes'");
					}

                	// mouse phenotype
					if ($('textarea.Mp').val() != ''){
                        var vals = $('textarea.Mp').val().toLowerCase();

                        if (vals.indexOf(" or ") != -1 && vals.toLowerCase().indexOf(" and ") != -1
                            && (vals.toLowerCase().indexOf("(") == -1 || vals.toLowerCase().indexOf(")") == -1)){
                            alert("Found ambiguous boolean relationship. Please specify");
                            return false;
                        }
                        else {
                            if ( (vals.match(/ or /g) || []).length == 2 || (vals.match(/ and /g) || []).length == 2) {
                                $('textarea.Mp').val( $('textarea.Mp').val().replace(/\)|\(/g, ""));
                            }
                        }

                        kv['srchMp'] = $('textarea.Mp').val();
    	                shownFilter.push("mouse phenotype = '" + kv.srchMp + "'");

                        // TODO: work out mp term and pvalue pairs
						var mpPval = {};
                        $('input.srchMp').each(function(){

                            var mpVal = $(this).val();
                            var gtval = $(this).siblings('span.pvalue').find('input.gtpvalue').val();
                            var ltval = $(this).siblings('span.pvalue').find('input.ltpvalue').val();
                            mpPval[mpVal] = {};
                            if (gtval != ""){
                                mpPval[mpVal]["gtpvalue"] = gtval;
                            }
                            if (ltval != ""){
                                mpPval[mpVal]["ltpvalue"] = ltval;
                            }

                            if (gtval != '' && ltval != ''){
                                shownFilter.push("pvalue: " + mpVal + " - " + gtval + "< P <" + ltval);
							}
							else if (gtval != ''){
                                shownFilter.push("pvalue: " + mpVal + " - " + "p > " + gtval);
							}
							else if (ltval != ''){
                                shownFilter.push("pvalue: " + mpVal + " - " + "p < " + ltval);
							}
                        });
						kv["pvaluesMap"] = mpPval;

					}
					else if ($('input.srchMp').val() != 'search' && $('input.srchMp').val() != ''){

                        kv['srchMp'] = $('input.srchMp').val();
                        shownFilter.push("mouse phenotype = '" + kv.srchMp + "'");

                        var gtval = $('input.srchMp').siblings('span.pvalue').find('input.gtpvalue').val();
                        if (gtval != ''){
                            kv['gtpvalue'] = gtval;
                        }
                        var ltval = $('input.srchMp').siblings('span.pvalue').find('input.ltpvalue').val();
                        if (ltval != ''){
                            kv['ltpvalue'] = ltval;
                        }

                        var mpVal = kv.srchMp;
                        if (gtval != '' && ltval != ''){
                            shownFilter.push("pvalue: " + mpVal + " - " + gtval + "< P <" + ltval);
                        }
                        else if (gtval != ''){
                            shownFilter.push("pvalue: " + mpVal + " - " + "p > " + gtval);
                        }
                        else if (ltval != ''){
                            shownFilter.push("pvalue: " + mpVal + " - " + "p < " + ltval);
                        }
                    }

                    // IMPReSS parameter name
                    if ($('input.srchPipeline').val() != 'search' && $('input.srchPipeline').val() != ''){
                       // console.log("check parameter: "+ $('input.srchPipeline').val());
                        kv['srchPipeline'] = $('input.srchPipeline').val();
                        shownFilter.push("measured parameter = '" + kv.srchPipeline + "'");
                    }
                    if ($("input[name='significance']").is(":checked")){
						shownFilter.push("Only significant p values = true");
						kv['onlySignificantPvalue'] = true;
					}
					else {
                        shownFilter.push("Only significant p values = false");
                        kv['onlySignificantPvalue'] = false;
					}

                    // chr range for genes
                    if ($('fieldset#chromosome').is(":visible")) {
                        var chrId = $('select#chrSel').val();
                        var chrStart = $('input#rstart').val();
                        var chrEnd = $('input#rend').val();

                        kv = checkChrCoords(chrId, chrStart, chrEnd, kv);
                        if (kv == false) {
                            return false;
                        }
                        else {
                            if (kv.hasOwnProperty("chr")){
                                shownFilter.push("chromosome = '" + kv.chr + "'");
                            }
                            else if ( kv.hasOwnProperty("chrRange")) {
                                shownFilter.push("chromosome range = '" + kv.chrRange + "'");
                            }
                            else {
                                shownFilter.push("chromosome = 'all'");
                            }
                        }
                    }

                    // mouse or human gene list
                    var species = $("fieldset.GeneFilter input[name='species']:checked").val();
					var geneList = $('textarea#geneList').val().split(/\n|,|\t|\s+|;/);
					var geneList2 = [];

					for (var i=0; i<geneList.length; i++){
					    var val = geneList[i];
					    if (val != "") {
					        //console.log("gene: "+ val);
                            //geneList2.push($.fn.upperCaseFirstLetter(val).trim());
                            geneList2.push(val.trim());
                        }
					}
					if (geneList2.length>0) {
                        kv[species + 'GeneList'] = geneList2;
                        shownFilter.push("search with " + species + " gene list = 'yes'");
                    }

                    // zygosity
                    var genotypes = [];
                    $("fieldset.GeneFilter input[name='genotype']:checked").each(function(){
                        console.log("genotype: "+ $(this).val());
                        genotypes.push($(this).val());
                    })
                    if (genotypes.length > 0){
                        kv['genotypes'] = genotypes;
                        shownFilter.push("genotype = '" + genotypes + "'");
                    }

                    // allele type
					var alleleTypes = [];
                    $("fieldset.GeneFilter input[name='alleleType']:checked").each(function(){
                        console.log("alleletype: "+ $(this).val());
                        alleleTypes.push($(this).val());
                    })
                    if (alleleTypes.length > 0){
                        kv['alleleTypes'] = alleleTypes;
                        shownFilter.push("allele type(s) = '" + alleleTypes + "'");
                    }

					// disease - gene association
					var dgAssoc = [];
					$("fieldset.DiseaseModelFilter input[name='assoc']:checked").each(function () {
                        dgAssoc.push($(this).val());
                    });
                    if ( dgAssoc.length > 0){
                        kv['diseaseGeneAssociation'] = dgAssoc;
                        var assocVal =  dgAssoc == 'humanCurated' ? "gene ortholog" : "phenotypic similarity";
                        shownFilter.push("disease gene association = '" + assocVal + "'");
                    }

                    // phenotypic similarity slider
					var sliderLow = $('div.slider-container div.low').text();
                    var sliderHigh = $('div.slider-container div.high').text();

                    kv['phenodigmScore'] = sliderLow + "," + sliderHigh;

                    shownFilter.push("phenodigm score = '" + sliderLow + " - " + sliderHigh + "'");

                    // disease, hp autosuggest
					var srchSugs = ["DiseaseModel"];
					for (var s=0; s<srchSugs.length; s++) {
					    var v = srchSugs[s];
                        var type = 'srch' + v;
                        console.log("dm: " + $('input.'+type).val());
						if ($('input.'+type).val() != 'search' && $('input.'+type).val() != ''){
                            kv[type] = $('input.'+type).val();
                            shownFilter.push("human disease term = '" + kv[type] + "'");
						}
                    }

                    kv['shownFilter'] = shownFilter.join(",  ");
                    $("div#userFilters").html("<h6>Search filters</h6>" + kv.shownFilter);

                    console.log("check kv");
                    console.log(kv)

                    return kv;
                }

                function checkChrCoords(chrId, chrStart, chrEnd, kv) {
					if (chrId == "All" && chrStart == "" && chrEnd == ""){
					    return kv;
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
                	else if (chrId != "All" && chrStart == "" && chrEnd == ""){
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
                    else if ( thisInput.hasClass('srchPipeline') ){
                        docType = 'pipeline';
                    }

                    thisInput.autocomplete({
                        source: function( request, response ) {
                            //var qfStr = request.term.indexOf("*") != -1 ? "auto_suggest" : "string auto_suggest";
							var qfStr = "auto_suggest"; // works for proximity search, string as qf will not work with proximity search
                            // var facetStr = "&facet=on&facet.field=docType&facet.mincount=1&facet.limit=-1";
                            var sortStr = "&sort=score desc";
                            // alert(solrUrl + "/autosuggest/select?rows=10&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr);                        )

                            $.ajax({
                                //url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,
                                url: solrUrl + "/autosuggest/select?rows=15&fq=docType:" + docType + "&wt=json&qf=" + qfStr + "&defType=edismax" + solrBq + sortStr,
                                dataType: "jsonp",
                                'jsonp': 'json.wrf',
                                data: {
                                    q: '"'+request.term+'"~2'
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
                                                //console.log(key + " --- " + term);

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

                    refreshResult(); // refresh first

                    var flList = [];
                    var dtypes = ["Allele", "Gene", "Mp", "DiseaseModel", "StatisticalResult"];
                    for (var d=0; d<dtypes.length; d++){
                        var cols = oJson[dtypes[d]];
                        console.log(cols);
                        for(var c=0; c<cols.length; c++) {
                            flList.push(cols[c]);
                        }
                    }

                    prepare_dataTable(flList);

                    var oConf = {};

                    oConf.params = JSON.stringify(oJson);

					//console.log(oConf);
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
                    kv.fllist = fllist;

                    return kv;
                }

                function prepare_dataTable(flList){

                    console.log("list: "+ flList);
                    var th = '';
                    for ( var i=0; i<flList.length; i++){
                        var colname = flList[i];//.replace(/_/g, " ");
                        if (colname == 'parameterName'){
                            colname = '(procedureName) parameterName';
                        }
                        colname = colname.replace(/([a-z])([A-Z])/g, '$1' + ' ' + '$2');

                        th += "<th>" + colname.toLowerCase() + "</th>";
                    }

                    var tableHeader = "<thead>" + th + "</thead>";
                    var tableCols = flList.length;


                    var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "batchq");
                    console.log(dTable);

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

                    $('body').addClass("loading");  // to activate modal

                    var oTable = $('table#batchq').dataTable({
                        "bSort": false, // true is default
                        "processing": true,
                        "paging": false,
                        //"serverSide": false,  // do not want sorting to be processed from server, false by default
                        //"sDom": "<<'#exportSpinner'>l<f><'#tableTool'>r>tip",
                        "sDom": "<<'#exportSpinner'>l<'#tableTool'>r>itp",
                        "sPaginationType": "bootstrap",
                        "searchHighlight": true,
                        "iDisplayLength": 50,
                        "oLanguage": {
                            "sSearch": "Filter: ",
                            //"sInfo": "Showing _START_ to _END_ of _TOTAL_ genes (for complete dataset of your search, please use export buttons)"
                            "sInfo": "<b>Data overview</b>: all columns are collapsed to show only unique values.<br>It shows you ONLY genes that have associations with phenotypes and diseases.<br><br>Please use 'Export full dataset' for row by row details"
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

                            $('body').removeClass("loading");  // when table loads, remove modal

                            $('div#sec2').show();

                            document.getElementById('sec2').scrollIntoView(true); // scrolls to results when datatable loads

                            // show pvalue as float via tooltip
                            $('table#batchq li span.pv').each(function(){
                                var val = $(this).text();
                                var index = val.indexOf('E');
                                var exp = val.replace(val.substr(0, index+2), "");
                                $(this).qtip({ // Grab all elements with a title attribute
                                    content: {
                                        text: parseFloat($(this).text()).toFixed(exp)
                                    }
                                });
                            });

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

                            var endPoint = baseUrl + '/exportAdvancedSearch?';//?param=' + JSON.stringify(oConf);

                            $('div#tableTool').html("<span id='expoWait'></span><form id='dnld' method='POST' action='" + endPoint + "'>"
                                + "<span class='export2'>Export full dataset as</span>"
                                + "<input name='fileType' value='' type='hidden' />"
                                + "<input name='fileName' value='' type='hidden' />"
                                + "<input name='param' value='' type='hidden' />"
                                + "<button class='tsv fa fa-download gridDump'>TSV</button>"
                                + " or<button class='xls fa fa-download gridDump'>XLS</button>"
                                + " or<button class='html fa fa-download gridDump'>HTML</button>"
                                + "</form>");

                            $('button.gridDump').click(function(){

                                var fileType = null;
								if ( $(this).hasClass('tsv') ){
								    fileType = 'tsv';
								}
								else if ( $(this).hasClass('xls') ){
                                    fileType = 'xls';
                                }
                                else if ( $(this).hasClass('html') ){
                                    fileType = 'html';
                                }

                                $("form#dnld input[name='fileType']").val(fileType);
                                $("form#dnld input[name='fileName']").val("IMPC_advancedSearch");
                                $("form#dnld input[name='param']").val(oConf.params);


                                //$('button').unbind('click');
                                $("form#dnld").submit();

                                return false;
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

                    console.log("clicked on: "+ dataType);

                    if (dataType == "Gene"){
                        addChomosomeRangeFilter(dataType);
                        addMgiGeneListBox(dataType);
                        addAlleleTypes(dataType);
						compartmentAttrsFilters(dataType, "Gene");
					}
                    else if (dataType == "Mp"){
                        addSex(dataType);
                        addAutosuggestFilter(dataType);
                        addParameterFilter(dataType);
                        //addOntologyChildrenLevelFilter(dataType);
                        compartmentAttrsFilters(dataType, "Phenotype");

                    }
                    else if (dataType == "Hp"){
                        addAutosuggestFilter(dataType);
                        //addOntologyChildrenLevelFilter(dataType);
                        compartmentAttrsFilters(dataType, "HP");
                    }
                    else if (dataType == "DiseaseModel"){
                        addDiseaseUi(dataType);
                        compartmentAttrsFilters(dataType, "Disease");

                        addSliderJs();
                    }
                }

                function addSliderJs(){
                    $('.range-slider').jRange({
                        from: 0,
                        to: 100,
                        step: 1,
                        scale: [0,25,50,75,100],
                        format: '%s',
                        width: 200,
                        showLabels: true,
                        isRange : true
                    });
				}

                function removeDatatypeFiltersAttributes(dataType) {
                    $('fieldSet#' + dataType).remove();

                    if (dataType == "Gene" || dataType == "Mp" || dataType == "Hp" || dataType == "DiseaseModel"){
                        $('table#c' + dataType).remove();
					}
                }

                function compartmentAttrsFilters(dataType, name){

                    var objs = idsVar[dataType].fields;
                    var defaultFields = idsVar[dataType].selected;

                    var inputs = "";
                    for (var i = 0; i < objs.length; i++) {
                        for (var k in objs[i]) {
                            var display = k;
                            var dbproperty = objs[i][k];
                            //console.log(k + " - " + objs[i][k]);

                            var checked = $.fn.inArray(dbproperty, defaultFields) ? "checked" : "";

                            inputs += "<input class='column' type='checkbox' name='" + display + "' value='" + dbproperty + "'" + checked + ">" + display;
                        }
                    }
                    var legend = "<legend>Customized output columns</legend>";
                    var attr = "<fieldset class='fsAttrs " + dataType + "' id='" + dataType + "'>" + legend + inputs + "</fieldset>";
                    $('div#dataAttributes').append(attr);


                    var table = $("<table id='c" + dataType + "' class='nbox'><td></td><td></td></table>");

                    table.find('td:first-child').html(name);
					$('fieldset.'+ dataType).appendTo(table.find('td:nth-child(2)'));

					table.appendTo($('div#dataAttributes'));
				}

				function addSex(dataType){
					var legend = "<legend>Mouse sex</legend>";
					var msg = "<div>Restrict results done with male or female samples or both</div>";
					var male = "<input type='checkbox' name='sex' value='male'> Male";
					var female = "<input type='checkbox' name='sex' value='female'> Female";
					var filter = "<fieldset class='" + dataType + "Filter dfilter " + dataType + "'>" + legend + msg + male + female + "</fieldset>";

					$('div#dataAttributes').append(filter);
				}

				function addParameterFilter(dataType){

                    var legend = "<legend>Measured parameter</legend>";
                    var msg = "Find the parameter of experiment:";
                    var significance = "<input type='checkbox' name='significance' value='yes' checked> Only measurements with significant p values";
                    var input = "<div class='block srchBox'>" +
                        "<i class='fa fa-search'></i>" +
                        "<input class='termFilter srchPipeline' value='search'>" +
                        "<i class='fa fa-times' id='paramClear'></i>" + significance +
                        "</div>";


                    //var filter = "<fieldset class='" + dataType + "Filter dfilter " + dataType + "'>" + legend + msg + input + "</fieldset>";
                    var filter = "<fieldset class='parameter"  + "Filter dfilter " + dataType + "'>" + legend + msg + input + "</fieldset>";

                    $('div#dataAttributes').append(filter);


                    // clear input
                    $("input.srchPipeline").click(function(){
                        if ($(this).val() == "search"){
                            $(this).val("");
                        }
                    });

                    // clear input when the value is not default: "search"
                    $("#paramClear").click(function(){
                        $(this).siblings($("input.srchPipeline")).val("");
                    });

                    // exception
                    addAutosuggest($('input.srchPipeline'));

				}

                function addChomosomeRangeFilter(dataType){

                    // add chromosome range filter
                    var chrs = [];
                    chrs.push("All")
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

                    var legend = '<legend>Mouse chromosome</legend>';
                    var msg = "Filter genes either by chromosome only OR both chromosome and region coordinates. Supports selecting multiple chromosomes.<br>";
                    var inputs = 'Chr: <select multiple size="4" id="chrSel">' + chrSel + '</select> ' +
                        'Start: <input id="rstart" type="text" name="chr"> ' +
                        'End: <input id="rend" type="text" name="chr">';

                    var chrInfo = "<i class='fa fa-info-circle' title='toggle chromosome coordinates'></i><div id='chrinfo'></div>";

                    var filter = "<fieldset id='chromosome' class='dfilter " + dataType + "'>" + legend + msg + inputs + chrInfo + "</fieldset>";

                    $('div#dataAttributes').append(filter);

                    $("fieldset#chromosome .fa-info-circle").click(function(){
                        var info = $('div#chrinfo');
                        if (info.is(":visible")) {
                            info.hide();
                        }
                        else {
                            info.show();
                        }

                        if (info.text() == ""){
                            // fetch chr coords range
                            $.ajax({
                                'url': baseUrl + '/chrlen',
                                'async': true,
                                'jsonp': 'json.wrf',
                                'success': function (len) {
                                    info.html("<h4>Chromosome length</h4>" + len);
                                },
                                'error' : function(jqXHR, textStatus, errorThrown) {
                                    alert("Sorry, failed to load chromosome coordinate range. This should be temporary and not affect your search");
                                }
                            });
						}
                    });

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

				function addDiseaseUi(dataType){

                    var assoc = "<b>Association by</b>: " +
						"<input type='checkbox' name='assoc' value='humanCurated'> Gene ortholog" +
						"<input type='checkbox' name='assoc' value='phenotypicSimilarity'> Phenotypic similarity<br><br>";

                    var slider = "<b>Phenodigm score range</b>:<div class='sliderBox'><input type='hidden' class='range-slider' value='100' /></div>";

                    var input = "<div class='block srchBox'>" +
                        "<i class='fa fa-search'></i>" +
                        "<input class='termFilter srch" + dataType + "' value='search'>" +
                        "<i class='fa fa-times' id='" + dataType + "Clear'></i>" +
                        "</div>";

					var legendLabel = "Human disease";
					var restriction = "Narrow your query to the human disease you are interested in";

                    var legend = "<legend>"+ legendLabel + "</legend>";

                    var filter = "<fieldset class='" + dataType + "Filter dfilter " + dataType + "'>" + legend + assoc + slider + restriction + input + "</fieldset>";

                    $('div#dataAttributes').append(filter);

                    // clear input
                    $("fieldset." + dataType + "Filter").on("click", "input.srch" + dataType, function(){
                        if ($(this).val() == "search"){
                            $(this).val("");
                        }
                    });

                    // clear input when the value is not default: "search"
                    $("fieldset." + dataType + "Filter").on("click", "i#" + dataType + "Clear", function(){
                        $(this).siblings($("input.srch" + dataType)).val("");
                    });

                    addAutosuggest($('input.srch' + dataType));
				}

				function addAutosuggestFilter(dataType){
				    var idname = dataType.toLowerCase();

					var input = "<div class='block srchBox'>" +
						"<i class='fa fa-search'></i>" +
						"<input class='termFilter srch" + dataType + "' value='search'>" +
						"<i class='fa fa-times' id='" + idname + "Clear'></i>" +
                        "<span class='pvalue'> p value: <input class='gtpvalue' type='text'> < P < <input class='ltpvalue' type='text'></span>" +
						"<button class='andOr " + dataType + "'>add to query</button>" +
                        "<button class='ontoview " + dataType + "'>ontology view</button>" +
						"</div>";

					var legendLabel, buttLabel, restriction = null;
					//var buttLabel = null;
                    //var restriction = null;
					if (dataType == "Mp"){
                        legendLabel = "Mouse phenotype (MP)";
                        buttLabel = "Add another phenotype";
                        restriction = "Narrow your query to the mouse phenotype you are interested in. <i class='fa fa-info-circle' title='toggle help'></i><br>"
							+ "<b>By default</b>, all nested phenotypes of a given term will be included in the search. Eg. querying for 'abnormal retina morphology' will include 'retinal hemorrhage'.<br>";
					}
					else if (dataType == "Hp"){
                        legendLabel = "Human phenotype (HP)";
                        buttLabel = "Add another phenotype";
                        restriction = "Narrow your query to the human phenotype you are interested in <i class='fa fa-info-circle' title='toggle help'></i>";
					}
                    else if (dataType == "DiseaseModel"){
                        legendLabel = "Human disease";
                        buttLabel = "Add another disease";
                        restriction = "Narrow your query to the human disease you are interested in <i class='fa fa-info-circle' title='toggle help'></i>";
                    }

					var legend = "<legend>"+ legendLabel + "</legend>";

                    var noMpChild = "<div id='single'><input type='checkbox' id='noMpChild'> exclude nested phenotypes</div>";
                    var butt = "<button class='ap'>" + buttLabel + "</button><br>";
                    var msg = "<span class='msg'>Build your query with boolean relationships (refer to info button above for help)<br>Eg. (A OR B) and C</span>";
                    var boolButts =
                    	  "<button class='andOr2 " + dataType + "'>AND</button>"
                    	+ "<button class='andOr2 " + dataType + "'>OR</button>"
                    	//+ "<button class='andOr2 " + dataType + "'>NOT</button>"
                    	+ "<button class='andOr2 " + dataType + "'>(</button>"
                    	+ "<button class='andOr2 " + dataType + "'>)</button>"
						+ "<button class='andOrClear " + dataType + "'>clear</button>";
                    var boolText = "<textarea class='andOr2 " + dataType + "' rows='2' cols=''></textarea>";
                    var help = "<img class='boolHow' src='${baseUrl}/img/advSearch/how-to-build-boolean-query.png' />";
					var filter = "<fieldset class='" + dataType + "Filter dfilter " + dataType + "'>" + legend + restriction + noMpChild + input + butt + msg + boolButts + boolText + help + "</fieldset>";

					$('div#dataAttributes').append(filter);

                    addAutosuggest($('input.srch' + dataType));

                    var fieldsetFilter = "fieldset." + dataType + "Filter ";

                    $(fieldsetFilter + ' button.ontoview').first().click(function(){
                        // ajax call to fetch for mp id
                        //var termName = $(fieldsetFilter + " input.srchMp").val();
                        var termName = $(this).siblings("input.srchMp").val();
                        //$('body').addClass("loading");  // to activate modal

                        $.ajax({
                            'url': baseUrl + '/fetchmpid?name=' + termName,
                            'async': true,
                            'jsonp': 'json.wrf',
                            'success': function (id) {
                               // $('body').removeClass("loading");  // to activate modal
                                console.log(id);
                                window.open(baseUrl + "/ontologyBrowser?termId=" + id, '_blank');
                            },
                            'error' : function(jqXHR, textStatus, errorThrown) {
                                alert("Sorry, this phenotype does not have Ontology View");
                            }
                        });

                        return false;
                    })


                    $(fieldsetFilter + ".fa-info-circle").click(function(){
                    	var imgHow = $(fieldsetFilter + "img.boolHow");
                       	if (imgHow.is(":visible")) {
                        	imgHow.hide();
                       	}
                       	else {
                            imgHow.show();
					   	}
					});

					// clear input
                    $(fieldsetFilter).on("click", "input.srch" + dataType, function(){
                        if ($(this).val() == "search"){
                            $(this).val("");
						}
                    });

                    // clear input when the value is not default: "search"
                    $(fieldsetFilter).on("click", "i#" + idname + "Clear", function(){
						$(this).siblings($("input.srch" + dataType)).val("");
					});

                    var boolTextarea = $(fieldsetFilter +"textarea.andOr2");

                    // define and/or for term filters
                    $(fieldsetFilter + "button.andOr").click(function(){
                        var termVal = $(this).siblings('.termFilter').val();
                        if (termVal == 'search' || termVal == ""){
                            alert("INFO: please enter a phenotype");
                        }
                        else {
                            boolTextarea.val(boolTextarea.val() + termVal + " ");
                        }
                        return false;
                    });
                    $(fieldsetFilter + "button.andOr2").click(function(){
                        var bool = $(this).text();
                        boolTextarea.val(boolTextarea.val() + bool + " ");
                        return false;
                    });


                    // add new input
                    $(fieldsetFilter + "button.ap").click(function(){

                        // only allow max of 3, otherwise it gets to complicated with the AND/OR combinataions
                        if ($(fieldsetFilter +".srchBox").size() < 3){
							$(input).insertAfter($(fieldsetFilter + ".srchBox").last());

							var lastButt = $(fieldsetFilter + "button.andOr").last();
							//lastButt.text($(fieldsetFilter +".srchBox").size());
							lastButt.text('add to query');

							lastButt.click(function () {
								var termVal = $(this).siblings('.termFilter').val();
								if (termVal == 'search') {
									alert("INFO: please enter a phenotype");
								}
								else {
									boolTextarea.val(boolTextarea.val() + termVal + " ");
								}
								return false;
							});

							if ($(fieldsetFilter + ".srchBox").size() > 1) {
								$(fieldsetFilter + ".andOr").show();
								$(fieldsetFilter + ".andOr2").show();
								$(fieldsetFilter + ".msg").show();
								$(fieldsetFilter + ".andOrClear").show();
							}
							$(fieldsetFilter + ".andOrClear").click(function () {
								boolTextarea.val("");
								return false;
							});

							$(fieldsetFilter + "button.ontoview").last().click(function() {
                                // ajax call to fetch for mp id
                                var termName = $(this).siblings('input.srchMp').val();
                                $.ajax({
                                    'url': baseUrl + '/fetchmpid?name=' + termName,
                                    'async': true,
                                    'jsonp': 'json.wrf',
                                    'success': function (id) {
                                        //console.log(id);
                                        window.open(baseUrl + "/ontologyBrowser?termId=" + id, '_blank');
                                    },
                                    'error': function (jqXHR, textStatus, errorThrown) {
                                        alert("Sorry, this phenotype does not have Ontology View");
                                    }
                                });
                                return false;
                            })

							// allow remove input just added
							$("<i class='pr fa fa-minus-square-o' aria-hidden='true'></i>").insertAfter($(fieldsetFilter + "button.andOr").last());

							$('i.pr').click(function () {
								$(this).siblings('input.termFilter').parent().remove();

								if ($(fieldsetFilter + ".srchMp").size() == 1) {
									$(fieldsetFilter + ".andOr").hide();
									$(fieldsetFilter + ".andOr2").hide();
									$(fieldsetFilter + ".msg").hide();
									$(fieldsetFilter + ".andOrClear").hide();
                                	$('textarea.andOr2').val("");
								}
							});

                        	addAutosuggest($('input.srch' + dataType).last());

                        	return false;
                    	}
                    	else {
                            alert("Currently, the query takes maximum of 3 phenotypes");
                            return false;
						}
                    });
                }

                function addAlleleTypes(dataType){
                    var legend = "<legend>Genotype and allele type</legend>";

					var genotypes = ["homozygote","heterozygote","hemizygote"];
                    var genotypesStr = "<b>Genotype</b>:";
                    for(var g=0; g<genotypes.length; g++){
                        var val = genotypes[g];
                        genotypesStr += "<input type='checkbox' name='genotype' value='" + val + "'> " + val;
                    }


					var alleletypesStr = "<b>Allele type</b>: ";
					var types = ["CRISPR(em)", "KOMP", "KOMP.1", "EUCOMM A", "EUCOMM B", "EUCOMM C", "EUCOMM D", "EUCOMM E"];
                    for(var t=0; t<types.length; t++){
                        var val = types[t];
                        alleletypesStr += "<input type='checkbox' name='alleleType' value='" + val +"'> " + val;
					}

                    var filter = "<fieldset class='" + dataType + "Filter dfilter " + dataType + "'>" + legend + genotypesStr + "<br>" + alleletypesStr + "</fieldset>";

                    $('div#dataAttributes').append(filter);
				}

            function addMgiGeneListBox(dataType){
                var legend = "<legend>Mouse or Human gene list</legend>";
                var mouse = "<input class='species' type='radio' name='species' value='mouse' checked> Mouse";
                var human = "<input class='species' type='radio' name='species' value='human'> Human ortholog";
                var hint = "<br>Eg. Nxn (symbol) or MGI:109331 (ID) for Mouse. <b>Symbol only for Human</b>. Please do not mix symbol and ID in your list. Use comma, space, tab or new line as separator."
                var input = "<textarea id='geneList' rows='3' cols=''></textarea>"
                var filter = "<fieldset class='" + dataType + "Filter dfilter " + dataType + "'>" + legend + mouse + human + hint + input + "</fieldset>";

                $('div#dataAttributes').append(filter);
            }

		</script>


        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.form.js"></script>
        <script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>
		<script type='text/javascript' src='${baseUrl}/js/vendor/jquery.range-min.js'></script>

    </jsp:attribute>

	<jsp:attribute name="addToFooter">
        <div class="region region-pinned">

		</div>

    </jsp:attribute>

	<jsp:body>
		<div class="modal"></div>
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC Dataset Advanced Search<a id="bqdoc" class=""><i class="fa fa-question-circle pull-right"></i></a></h1>
						<form id='goSubmit' method='post' onreset="catchOnReset()">
						<div class="section">

							<!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
							<div class='inner' id='srchBlock'>

								<div class='fl2'>

									<%--<h6 class='bq'>IMPC Data Model</h6><hr>--%>
									<%--<div id="graphInfo">Click the datatypes to add/remove filters or attributes.  <button id="clearAllDt">Clear all selections</button></div>--%>
									<%--<div id='graph'></div>--%>
									<%--<h6 class='bq'>Customized data filter and attributes</h6><hr>--%>
									<div id="dataAttributes"></div>
								</div>

                                <%--<div class='fl1'>--%>
                                    <%--This search is<br><input type="radio" name="queryType" value="Gene"> Mouse-gene centric<br>--%>
                                    <%--<input type="radio" name="queryType" value="Mp"> Mouse-phenotype centric<br>--%>
                                <%--</div>--%>
                                <%--<div style="clear: both"></div>--%>
							</div>
						</div> <!-- end of section -->
							<div id='sq'>
								<input type="submit" id="pastedlistSubmit" onclick="return submitQuery()" />
								<input type="reset" name="reset" value="Reset">
							</div>
						</form>
						<div class="section" id="sec2">
							<h2 id="section-gotable" class="title ">Advanced Search Result</h2>
							<div id="userFilters"></div>
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

