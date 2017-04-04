<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC dataset batch query</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Batch search</a></jsp:attribute>

	<jsp:attribute name="header">


		<style>
			div.region-content {width: 100% !important; margin-left: 0 !important;}
			div#graph {
				height: 300px;
			}
			div.border {
				border: 4px solid darkred;
			}
			.clear{width: 100%; clear: both; height: 0px; line-height:0px;}


		</style>

		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/raphael/2.2.7/raphael.min.js"></script>


	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>

	<jsp:body>



		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC Dataset Batch Query<a id="bqdoc" class=""><i class="fa fa-question-circle pull-right"></i></a></h1>

						<div class="section">
							<div class='inner' id='srchBlock'>
								<div>This is the IMPC data model. Select the datatypes to build your query. Click <button id="path">OK</button> to continue.</div>
								<div id="graph">

								</div>
							</div>
						</div>
						<div class="section" id="sec2">
							<h4>Batch Query Filters</h4>
							<div id="bqFilter" class="inner"></div>
						</div>

						<div class="section" id="sec3">
							<h4>Batch Query Result</h4>
							<div id="bqTable" class="inner"></div>
						</div>
					</div>
				</div>
			</div>
		</div>


		<script>

			$(document).ready(function () {

                var idsVar = {
                    "Gene": {
                        text: "Gene",
                        x: 690,
                        y: 250,
                        r: 40,
						fields: {
                            "MGI gene id": "mgiAccessionId",
                            "MGI gene symbol": "markerSymbol",
                            "MGI gene type": "markerType",
                            "MGI gene name": "markerName",
                            "Chromosome id": "chrId",
                            "Chromosome start": "chrStart",
                            "Chromosome end": "chrEnd",
                            "Chromosome strand": "chrStrand"
                        }
                    },
                    "HumanOrtholog": {
                        text: "Human\nOrtholog",
                        x: 800,
                        y: 250,
                        r: 40,
                        fields: {
                            "HGNC gene symbol":"humanGeneSymbol"
                        }
                    },
                    "EnsemblGeneId": {
                        text: "Ensembl\nGene Id",
                        x: 800,
                        y: 160,
                        r: 40,
                        fields: {
                            "Ensembl gene id":"ensembleGeneId"
                        }
                    },
                    "MarkerSynonym": {
                        text: "Marker\nSynonym",
                        x: 800,
                        y: 50,
                        r: 40,
                        fields: {
                            "MGI gene synonym":"markerSynonym"
                        }
                    },
                    "Allele": {
                        text: "Allele",
                        x: 550,
                        y: 170,
                        r: 40,
                        fields: {
                            "MGI allele id":"alleleMgiAccessionId",
                            "MGI allele symbol":"alleleSymbol",
                            "allele description":"alleleDescription",
                            "allele mutation type":"mutationType",
                            "ES cell available?":"esCellStatus",
                            "mouse available?":"mouseStatus",
                            "phenotyping data available?":"phenotypeStatus"
                        }
                    },
//                    "DiseaseGene": {
//                        text: "Disease-\nGene",
//                        x: 570,
//                        y: 50,
//                        r: 40
//                    },
                    "DiseaseModel": {
                        text: "Disease-\nModel",
                        x: 400,
                        y: 110,
                        r: 40,

                    },
                    "MouseModel": {
                        text: "Mouse\nModel",
                        x: 380,
                        y: 260,
                        r: 40
                    },
                    "Hp": {
                        text: "Human\nPhenotype",
                        x: 200,
                        y: 55,
                        r: 40
                    },
                    "Mp": {
                        text: "Mouse\nPhenotype",
                        x: 120,
                        y: 220,
                        r: 40
                    },
                    "OntoSynonym": {
                        text: "Mouse\nPhenotype\nSynonym",
                        x: 80,
                        y: 100,
                        r: 40
                    },


                };

                var paths = [];

                paper = new Raphael(document.getElementById('graph'), 500, 500);

                for (var id in idsVar) {
                    var text = id;
                    drawCircle(id, idsVar[id].text, idsVar[id].x, idsVar[id].y, idsVar[id].r, paper);
                }


                function drawCircle(id, text, x, y, r, paper) {
                    paper.text(x, y, text);
                    var label = paper.text(x, y, text).attr({fill: '#fff'});
                    label.attr({opacity: 100, 'font-size': 12}).toFront();

                    var circle = paper.circle(x, y, r).attr({gradient: '90-#526c7a-#64a0c1', 'id':id}).toBack();
					circle.data('id', id);

//                    circle.attr(
//                        {
//                            gradient: '90-#526c7a-#64a0c1',
//                            stroke: '#3b4449',
//                            //'stroke-width': 10,
//                            //'stroke-linejoin': 'round',
//                            rotation: -90
//                        }
//                    );

					var jCircle = $(circle.node);
					var jLabel = $(label.node);

                    jCircle.click(function () {
                        if ($(this).attr("stroke") == "darkorange") {
                            $(this).attr({"stroke":"black", "stroke-width": 1});

                        }
                        else {
							paths.push(circle.data("id"));
                            $(this).attr({"stroke":"darkorange", "stroke-width": 5});
                            console.log(paths);
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
//                    circle.node.onclick = function () {
//                        console.log(text);
//                        this.attr({"stroke":"red"});
//                    }
//
//                   // p.node.setAttribute("class","track");
//                    label.node.onclick = function () {
//
//                       // alert(text);
//                        this.attr({"stroke-with":5});
//                    }
                }

                // connect circles
                connectCircle("Gene", "HumanOrtholog");
                connectCircle("Gene", "EnsemblGeneId");
                connectCircle("Gene", "MarkerSynonym");
                connectCircle("Gene", "Allele");
                connectCircle("MouseModel", "Gene");
                connectCircle("MouseModel", "Mp");
//                connectCircle("DiseaseGene", "Gene");
//                connectCircle("DiseaseGene", "Hp");
                connectCircle("DiseaseModel", "Allele");
                connectCircle("DiseaseModel", "MouseModel");
                connectCircle("DiseaseModel", "Hp");
                connectCircle("DiseaseModel", "Mp");
                connectCircle("Mp", "Hp");
                connectCircle("Mp", "OntoSynonym");



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

                    var lineColor = "#c1d7d7";
                    if ( (id1=="DiseaseGene" && id2=="Hp") || (id1=="DiseaseGene" && id2=="Gene") ){
                        lineColor = "#507c7c";
					}

                    line.attr ("stroke", lineColor);


                }

                // hit OK button to continue to next step
				$('button#path').click(function(){

				});





                // Computes a path string for a circle
                function circlePath(x, y, r) {
                    return "M" + x + "," + (y - r) + "A" + r + "," + r + ",0,1,1," + (x - 0.1) + "," + (y - r) + " z";
                }

                // Computes a path string for a line
                function linePath(x1, y1, x2, y2) {
                    return "M" + x1 + "," + y1 + "L" + x2 + "," + y2;
                }




            });

		</script>


	</jsp:body>
</t:genericpage>

