/*
 * Copyright © 2017 QMUL - Queen Mary University of London 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * handling of data on phenodigm2-specific pages 
 * 
 * requires d3, _, $, Phenogrid
 * 
 * d3 - used to generate visualizations, generate tables from json
 * _ - misc manipulations of objects and arrays
 * $ - jquery, used to manipulate dom (legacy)
 * Phenogrid - used to display phenogrid widget
 * 
 * 
 * The code uses an ugly mixture of d3 and jQuery. This is mostly
 * 
 * 
 * Parts of the code are based on diseasetableutils.js
 * 
 */

/* global d3, _, $, impc */

if (typeof impc === "undefined") {
    impc = {};
}
impc.phenodigm2 = {
    scores: ["avgRaw", "avgNorm", "maxRaw", "maxNorm"]
};
impc.sources = ["IMPC", "EuroPhenome"];



/****************************************************************************
 * Generation of tables
 **************************************************************************** */

/**
 * Generates html for a table.
 * 
 * @param {array} darr - array of objects
 * @param {string} target - id of target table (including #)
 * @param {object} config - object with configuration,
 *   should include "markerlist", "requestpagetype", "diseaesid"
 *   
 * @returns {undefined}
 */
impc.phenodigm2.makeTable = function (darr, target, config) {

    console.log("running makeTable " + target);
    var targetdiv = d3.select(target);

    //console.log("mpt " + target + " 2");
    // setup columns. Last column is blank because 
    var thead = targetdiv.append("thead").append("tr");
    ["Gene", "Models", "Max Raw", "Avg Raw", "Phenodigm"].map(
            function (x) {
                thead.append("th").append("span").classed("main", true).html(x);
            });
    // add a last column with blank title (will contain +extra info button)
    thead.append("th");

    // setup data body
    var tbody = targetdiv.append("tbody")
            .classed("phenotable", true)
            .attr("pagetype", config.pagetype);
    if (config.pagetype === "disease") {
        tbody.attr("diseaseid", config.disease);
    }

    // create a subset of the large array with only markers to show
    var dshow = darr;
    if (config.filter) {
        dshow = darr.filter(function (x) {
            return config.markerlist.indexOf(x["markerSymbol"]) >= 0;
        });
    }

    // convert from by-model to by-gene representation    
    dshow = _.groupBy(dshow, function (x) {
        return x["markerSymbol"];
    });
    var dgenes = _.keys(dshow);

    var scorecols = ["maxRaw", "avgRaw"];

    // helper function computes the phenodigm score using max/avg components
    var phenscore = function (x) {
        var phenodigms = _.map(x, function (y) {
            return (y["maxNorm"] + y["avgNorm"]) / 2;
        });
        var phenmax = _.max(phenodigms);
        return phenmax.toPrecision(4);
    };

    // create html table (one line per gene)
    dgenes.map(function (gene) {
        // compute a summary for the gene
        // x is now an array of objects
        var x = dshow[gene];
        var trow = tbody.append("tr").attr("class", "phenotable")
                .attr("geneId", x[0]["markerId"]);
        trow.append("td").append("a").classed("genelink", true)
                .attr("href", impc.baseUrl + "/genes/" + x[0]["markerId"]).html(gene);
        trow.append("td").html(x.length + " <span class='small'>(" + x[0].markerNumModels + ")");
        if (x.length > 1) {
            // display ranges for all scores
            scorecols.map(function (y) {
                var temp = _.pluck(x, y);
                var tempmax = _.max(temp);
                trow.append("td").classed("numeric", true).html(tempmax);
            });
        } else {
            scorecols.map(function (y) {
                trow.append("td").classed("numeric", true).html(x[0][y]);
            });
        }
        trow.append("td").classed("numeric", true).html(phenscore(x));
        trow.append("td").attr("titlef", "Click to display phenotype terms")
                .classed("toggleButton", true)
                .append("i").classed("fa fa-plus-square more", true);
    });

    // attach a special listener to the gene links. This allows users to click 
    // on links without activating the phenogrid widget (on click bound to tr)
    tbody.selectAll("a").on("click", function (x) {
        d3.event.stopPropagation();
    });

};


/****************************************************************************
 * Generation of scatter visualization
 **************************************************************************** */

/**
 * Create html and handlers for a phenodigmScatter widget.
 * 
 * @param {array} darr - input dataset 
 * @param {object} conf - configuration for scatterplot
 * @returns {undefined}
 */
impc.phenodigm2.makeScatterplot = function (darr, conf) {
    // create an svg element and a details box, but draw the contents elsewhere
    var container = d3.select(conf.id).classed("phenoscatter", true);
    var fullwidth = parseInt(container.style("width"));
    // add two side-by-side divs
    var svg = container.append("div").attr("class", "leftright")
            .style("width", (fullwidth - conf.detailwidth - 2) + "px")
            .append("svg").style("height", conf.h + "px");
    var detail = container.append("div").attr("class", "leftright detail")
            .style("width", (conf.detailwidth - (2 * conf.detailpad)) + "px")
            .style("margin-top", conf.margin[0] + "px")
            .style("margin-bottom", conf.margin[2] + "px")
            .style("padding", conf.detailpad + "px")
            .style("height", (conf.h - conf.margin[0] - conf.margin[2] - (2 * conf.detailpad)) + "px")
            .style("display", "none");

    var brsection = function (title, classname) {
        detail.append("strong").text(title);
        detail.append("br");
        detail.append("span").attr("class", classname).text(title + " value");
        detail.append("br");
        detail.append("br");
    };

    brsection("Model", "model");
    brsection("Source", "source");
    brsection("Description", "description");
    brsection("Scores", "scores");

    container.append("div").style("clear", "both");
    impc.phenodigm2.drawScatterplot(darr, conf);
};

// d3 functions to move items in an svg to the front or back 
// https://github.com/wbkd/d3-extended
d3.selection.prototype.moveToFront = function () {
    return this.each(function () {
        this.parentNode.appendChild(this);
    });
};
d3.selection.prototype.moveToBack = function () {
    return this.each(function () {
        var firstChild = this.parentNode.firstChild;
        if (firstChild) {
            this.parentNode.insertBefore(this, firstChild);
        }
    });
};

/**
 * Generate a scatter plot from the scores
 * 
 * @param {type} darr
 * @param {object} conf - configuration for scatter plot
 * 
 * @returns {undefined}
 */
impc.phenodigm2.drawScatterplot = function (darr, conf) {

    console.log("running makePhenodigmScatter " + conf.id);
    var outer = d3.select(conf.id).select("svg");
    var detail = d3.select(conf.id).select(".detail");

    // clear the contents of the svg
    outer.html("");

    // set some additional elements in conf, pertaining to size of page/drawing
    conf.w = parseInt(outer.style("width"));
    conf.h = parseInt(outer.style("height"));
    conf.winner = +conf.w - conf.margin[1] - conf.margin[3];
    conf.hinner = +conf.h - conf.margin[0] - conf.margin[2];

    console.log("configuration is: " + JSON.stringify(conf));

    // adjust the svg, create a drawing box inside
    var svg = outer.attr("width", conf.w + "px").attr("height", conf.h + "px")
            .append("g").attr("width", conf.winner + "px").attr("height", conf.hinner + "px")
            .attr("transform", "translate(" + conf.margin[3] + "," + conf.margin[0] + ")");

    // find maximum values for axes     
    var xmax = _.max(_.pluck(darr, conf.axes[0]));
    var ymax = _.max(_.pluck(darr, conf.axes[1]));
    xmax = (xmax > 90) ? 100 : xmax;
    ymax = (ymax > 90) ? 100 : ymax;

    console.log("maximum values are " + xmax + " " + ymax);

    // create the x axis
    var xscale = d3.scaleLinear().range([0, conf.winner]).domain([0, xmax]);
    var xaxis = d3.axisBottom(xscale);
    svg.append("g").attr("class", "xaxis")
            .attr("transform", "translate(0," + conf.hinner + ")").call(xaxis)
            .selectAll("path.domain").attr("d", "M" + conf.winner + ",0.5H-0.5");
    svg.append("text").text(conf.labs[0])
            .attr("class", "xlab")
            .attr("x", conf.winner / 2).attr("y", conf.hinner)
            .attr("dy", conf.offset[0]);

    // create the y axis
    console.log("drawing y axis " + JSON.stringify(ymax));
    var yscale = d3.scaleLinear().range([0, conf.hinner]).domain([ymax, 0]);
    var yaxis = d3.axisLeft(yscale);
    svg.append("g").attr("class", "yaxis").call(yaxis)
            .selectAll("path.domain").attr("d", "M0.5," + conf.hinner + "V0.5");
    svg.append("text").text(conf.labs[1]).attr("transform", "rotate(-90)")
            .attr("class", "ylab")
            .attr("x", 0 - (conf.h - conf.margin[0] - conf.margin[2]) / 2)
            .attr("y", 0).attr("dy", conf.offset[1]);

    // create a path showing boundary between hits and non-hits
    var boundary = [];
    var thresh = conf.threshold;
    for (var i = 0; i <= Math.PI / 2; i += Math.PI / 120) {
        var ix = thresh * Math.cos(i);
        var iy = thresh * Math.sin(i);
        if (ix < xmax && iy < ymax) {
            boundary.push([xscale(ix), yscale(iy)]);
        }
    }
    var lineGenerator = d3.line();
    svg.append("path")
            .attr("fill", "none")
            .attr("stroke", conf.linecolor)
            .attr("stroke-linecap", "none")
            .attr("stroke-linejoin", "round")
            .attr("stroke-width", conf.linewidth)
            .attr("d", lineGenerator(boundary));


    // split the input array into known and new genes    
    var isKnown = function (x) {
        return conf.knowngenes.indexOf(x.markerSymbol) >= 0;
    };
    var darr_known = darr.filter(isKnown);
    var darr_new = darr.filter(function (x) {
        return !isKnown(x);
    });

    // create the annotated functions (fills in the right-hand box)
    var showModelInfo = function (d) {
        console.log("clicked " + JSON.stringify(d));
        detail.style("display", "block");
        detail.select(".source").text(d.source);
        detail.select(".model").text(d.id);
        detail.select(".description").text(d.description);
        detail.select(".scores").text("(" + d[conf.axes[0]] + ", " + d[conf.axes[1]] + ")");
        d3.event.stopPropagation();
    };

    // add points to the chart 
    //  - rect for models with annotated genes
    //  - circle for models with other genes
    svg.selectAll(".marker1").data(darr_known).enter().append("rect")
            .attr("class", function (d) {
                return "marker curated " + d.source;
            })
            .attr("x", function (d) {
                return xscale(d[conf.axes[0]]) - (1.5 * conf.radius);
            })
            .attr("y", function (d) {
                return yscale(d[conf.axes[1]]) - (1.5 * conf.radius);
            })
            .attr("width", 3 * conf.radius)
            .attr("height", 3 * conf.radius)
            .on("click", showModelInfo);
    svg.selectAll(".marker2").data(darr_new).enter().append("circle")
            .attr("class", function (d) {
                return "marker " + d.source;
            })
            .attr("cx", function (d) {
                return xscale(d[conf.axes[0]]);
            })
            .attr("cy", function (d) {
                return yscale(d[conf.axes[1]]);
            })
            .attr("r", function (d) {
                return conf.radius;
            })
            .on("click", showModelInfo);

    // style and add behaviors to the markers
    svg.selectAll(".marker")
            .attr("fill", function (d) {
                if (d3.select(this).attr("class").search(/IMPC/) >= 0) {
                    return conf.color[0];
                }
                return conf.color[1];
            })
            .style("fill-opacity", function (d) {
                return 0.7;
            });

    // move MGI models to back (to highlight IMPC models)
    svg.selectAll(".MGI").each(function (d) {
        d3.select(this).moveToBack();
    });
    // show any models with curated genes (to highlight existing knowledge)
    svg.selectAll(".curated").each(function (d) {
        d3.select(this).moveToFront();
    });

    // add legend on left-hand corner
    svg.append("circle").attr("class", "legendmarker IMPC")
            .attr("fill", conf.color[0])
            .attr("cx", conf.legendpos[0])
            .attr("cy", conf.legendpos[2])
            .attr("r", 0.9 * conf.radius);
    svg.append("text").attr("y", conf.legendpos[2])
            .attr("class", "legendtext").text("IMPC model");
    svg.append("circle").attr("class", "legendmarker MGI")
            .attr("fill", conf.color[1])
            .attr("cx", conf.legendpos[0])
            .attr("cy", conf.legendpos[2] + (1 * conf.legendspacing))
            .attr("r", 0.9 * conf.radius);
    svg.append("text").attr("y", conf.legendpos[2] + (1 * conf.legendspacing))
            .attr("class", "legendtext").text("Literature model");
    svg.append("rect").attr("class", "legendmarker curated")
            .attr("fill", "#666666")
            .attr("x", conf.legendpos[0] - conf.radius)
            .attr("y", conf.legendpos[2] + (2 * conf.legendspacing) - conf.radius)
            .attr("width", 2 * conf.radius)
            .attr("height", 2 * conf.radius);
    svg.append("text").attr("y", conf.legendpos[2] + (2 * conf.legendspacing))
            .attr("class", "legendtext").text("model with DAG-KO");
    svg.append("text").attr("y", conf.legendpos[2] + (3.5 * conf.legendspacing))
            .attr("x", conf.legendpos[0] - conf.radius).attr("class", "legendsource")
            .text("Data sources: IMPC, MGI");
    svg.selectAll(".legendtext").attr("x", conf.legendpos[1]);

    console.log("mp2 end");
};



/****************************************************************************
 * Handling for phenogrid widget
 **************************************************************************** */

/**
 * Create a div with attributes. (The div can be used to hold a phenogrid)
 * 
 * @param pagetype - string, use "disease"
 * @param geneId - string, use a gene identifier (e.g. MGI:xxxxx)
 * @param diseaseId - string, use a disease identifier (e.g. OMIM:xxxxx)
 * @return a jquery div 
 */
impc.phenodigm2.makeTableChildRow = function (pagetype, geneId, diseaseId) {
    var genenum = geneId.split(":")[1]
    console.log("creating childrow");
    var innerdiv = $(document.createElement('div'))
            .addClass("inner")
            .attr({
                geneId: geneId,
                diseaseId: diseaseId,
                pageType: pagetype
            }).css({"padding": "0"});
    innerdiv.append("<div class='inner_table'></div>");
    innerdiv.append("<div class='inner_pg' id='pg_" + genenum + "'></div>");
    return innerdiv;
};



/**
 * Fetch data for a phenogrid from server through an ajax call.
 * The result of this query will be a quasi-filled skeleton for the phenogrid.
 * Some fields in the grid will not be accurate and will have to be filled in 
 * separately.
 * 
 * @param {string} geneId
 * @param {string} diseaseId
 * @param {string} pageType
 * @returns {jqXHR}
 */
impc.phenodigm2.getPhenoGridSkeleton = function (geneId, diseaseId, pageType) {
    console.log("starting fetch: " + new Date());
    return $.getJSON(impc.baseUrl + "/phenodigm2/phenogrid",
            {geneId: geneId, diseaseId: diseaseId, pageType: pageType}
    );
};


/* global modelAssociations */


/**
 * 
 * @param {type} skeleton
 * @returns {undefined}
 */
impc.phenodigm2.completeGridSkeleton = function (skeleton) {

    // change the modelAssociations array into an indexed object    
    var scoredModels = _.indexBy(modelAssociations, "id");

    // helper to transfer scores from scoredModels into the skeleton
    var completeEntities = function (group) {
        // fill in missing scores into the entities
        group.entities = group.entities.map(function (x) {
            if (_.has(scoredModels, x.id)) {
                var xmodel = scoredModels[x.id];
                x.score.score = (xmodel["maxNorm"] + xmodel["avgNorm"]) / 2;
            }
            return x;
        });
        // sort the entities by decreasing score
        group.entities = _.sortBy(group.entities, function (x) {
            return -x.score.score;
        });
        // replace zero scores 
        group.entities = group.entities.map(function (x) {
            if (x.score.score === 0) {
                x.score.score = " ";
            }
            return x;
        });
        //console.log("output group is "+JSON.stringify(group));
        return group;
    };

    skeleton.xAxis = skeleton.xAxis.map(function (x) {
        return completeEntities(x);
    });

    return skeleton;
};


/* global modelAssociations */

/**
 * Create a small table with details about individual models
 * 
 * @param {type} tableId
 * @param {type} geneId
 * @param {type} skeleton
 * @returns {undefined}
 */
impc.phenodigm2.insertModelDetails = function (tableId, geneId, skeleton) {

    // identify div that should hold the details table
    var targetdiv = d3.select(tableId + " .inner[geneid='" + geneId + "'] .inner_table");
    var tablediv = targetdiv.append("table").classed("table", true);
    
    // identify the rows in modelAssociations that are relevant
    var details = modelAssociations.filter(function (x) {
        return x["markerId"] === geneId;
    });
    details = _.indexBy(details, "id");
    //console.log("association details: " + JSON.stringify(details));

    // identify parts of the phenogrid skeleton that are relevant
    var models = skeleton.xAxis[0].entities;
    //console.log("skeleton details " + JSON.stringify(models));

    // setup columns. Last column is blank because 
    var thead = tablediv.append("thead").append("tr");
    ["Model", "Genotype", "Max Raw", "Avg Raw", "Phenodigm", "Phenotypes"].map(
            function (x) {
                thead.append("th").append("span").classed("main", true).html(x);
            });

    // setup data body
    var tbody = tablediv.append("tbody").attr("geneid", geneId);

    // helper function computes the phenodigm score using max/avg components
    var phenscore = function (x) {
        return ((x["maxNorm"] + x["avgNorm"]) / 2).toPrecision(4);
    };
    // pretty printing of models e.g. ABC<xyz>
    var tohtml = function (x) {
        return x.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\//g, "/ ");
    };
    // identify whether the info fields contain IMPC
    var isImpc = function (x) {
        var result = false;
        console.log("checking " + JSON.stringify(x));
        console.log("info " + JSON.stringify(x.info));
        console.log("\n\n");
        x.info.map(function (y) {
            if (y.id.startsWith("Source")) {
                result = impc.sources.indexOf(y.value) >= 0;
            }
        });
        return result;
    };

    // create html table (one line per model)    
    models.map(function (modeldata) {
        var trow = tbody.append("tr");
        var modelid = modeldata["id"];
        trow.append("td").html(modelid);
        trow.append("td").html(tohtml(modeldata["label"]));
        if (_.has(details, modelid)) {
            trow.append("td").classed("numeric", true).html(details[modelid]["maxRaw"]);
            trow.append("td").classed("numeric", true).html(details[modelid]["avgRaw"]);
            trow.append("td").classed("numeric", true).html(phenscore(details[modelid]));
        } else {
            trow.append("td");
            trow.append("td");
            trow.append("td");
        }
        var tdpheno = trow.append("td");
        modeldata.phenotypes.map(function (pheno, i) {
            if (i > 0) {
                tdpheno.append("span").classed("semicolon", true).html("; ");
            }
            tdpheno.append("a").attr("href", monarchUrl + "/phenotype/" + pheno.id).html(pheno.term);
        });
        // identify whether model was impc and highlight it
        if (isImpc(modeldata)) {
            trow.classed("impc", true);
        }
    });

};



/* global monarchUrl */

/**
 * Create a phenogrid object and insert it within a clicked div
 * 
 * @param {string} tableId  
 * @param {string} geneId
 * @param {string} diseaseId
 * @param {string} pageType
 */
impc.phenodigm2.insertPhenogrid = function (tableId, geneId, diseaseId, pageType) {

    // identify the div that should hold the phenodigm widget
    var targetdiv = $(tableId).find(".inner[geneid='" + geneId + "']").find(".inner_pg");

    var gridColumnWidth = 25;
    var gridRowHeight = 50;

    impc.phenodigm2.getPhenoGridSkeleton(geneId, diseaseId, pageType)
            .done(function (result) {
                //console.log("raw result is: " + JSON.stringify(result));
                result = impc.phenodigm2.completeGridSkeleton(result);
                impc.phenodigm2.insertModelDetails(tableId, geneId, result);
                Phenogrid.createPhenogridForElement(targetdiv, {
                    //monarchUrl is a global variable provided via Spring from application.properties
                    serverURL: monarchUrl,
                    // sort method of sources: "Alphabetic", "Frequency and Rarity", "Frequency,
                    selectedSort: "Frequency and Rarity",
                    gridSkeletonDataVendor: 'IMPC',
                    gridSkeletonData: result,
                    singleTargetModeTargetLengthLimit: gridColumnWidth,
                    sourceLengthLimit: gridRowHeight
                });
            });
};

/** 
 * Add on-click functionality to a table object generated by DataTable
 * 
 * show/hide a child row when a row is clicked. 
 * 
 * @param tableId - string, id of table
 * @param table - object contructed by DataTable()
 * 
 */
$.fn.addTableClickPhenogridHandler = function (tableId, table) {

    var tbody = $(tableId + ' tbody');
    var pagetype = tbody.attr("pagetype");
    var diseaseId = tbody.attr("diseaseid");

    // allow users to click on a row and see a PhenoGrid widget
    $(tableId + ' tbody').on('click', 'tr.phenotable', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);
        var geneId = tr.attr("geneid");

        if (typeof geneId === "undefined") {
            return;
        }

        if (row.child.isShown()) {
            // This row is already open - close it
            row.child.hide();
            tr.find("td.toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");
        } else {
            // Open this row            
            row.child(impc.phenodigm2.makeTableChildRow(pagetype, geneId, diseaseId)).show();
            impc.phenodigm2.insertPhenogrid(tableId, geneId, diseaseId, pagetype);
            tr.find("td.toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
        }
    });

};
