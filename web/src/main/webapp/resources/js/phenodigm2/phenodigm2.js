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
 * requires: 
 * 
 * d3 - used to generate visualizations, generate tables from json
 * _ - misc manipulations of objects and arrays
 * $ - jquery, used to manipulate dom (legacy)
 * Phenogrid - used to display phenogrid widget
 * modelAssociations - object expected to be defined in page before these
 *                     functions are called
 * 
 * The code uses an ugly mixture of d3 and jQuery. This is mostly
 * 
 * 
 * Parts of the code are based on diseasetableutils.js
 * 
 * Status: needs cleanup
 * 
 */

/* global d3, _, $, impc, monarchUrl, Phenogrid, modelAssociations */

if (typeof impc === "undefined") {
    impc = {};
}
impc.phenodigm2 = {
    scores: ["avgRaw", "avgNorm", "maxRaw", "maxNorm"]
};
// values for model source that IMPC can take credit for
impc.sources = ["IMPC", "EuroPhenome", "EUCOMM", "3i", "3i,IMPC"];
impc.logo = impc.baseUrl + "/img/impc.png";
console.log("logo url " + impc.logo);


/****************************************************************************
 * General helper functions
 **************************************************************************** */

// identify whether the info fields contain IMPC

/**
 * Identify whether x refers to an IMPC model
 * This function handles multiple cases:
 *   - when x is an array of objects
 *   - when x is an object with a source field
 *   - when x is an object without a source field (but with an info field)
 * 
 * @param {type} x
 * @returns {Boolean}
 */
impc.isImpc = function (x) {
    var result = false;
    
    // handle case of an array
    if (x instanceof Array) {
        x.map(function (y) {
            result = result || impc.isImpc(y);
        });
        return result;
    }

    console.log("in here checking impc");
    if (_.has(x, "source")) {
        result = impc.sources.indexOf(x.source) >= 0;
    } else if (_.has(x, "info")) {
        x.info.map(function (y) {
            if (y.id.startsWith("Source")) {
                result = impc.sources.indexOf(y.value) >= 0;
            }
        });
    }
    
    return result;
};


/****************************************************************************
 * Generation of tables
 **************************************************************************** */

/**
 * Generates html for a table. 
 * Used on disease pages and on genes pages with different column headings.
 * 
 * @param {array} darr - array of objects
 * @param {string} target - id of target table (including #)
 * @param {object} config - object with configuration,
 *   should include "markerlist", "requestpagetype", "diseaesid"
 *   
 * @returns {undefined}
 */
impc.phenodigm2.makeTable = function (darr, target, config) {

    // start working with d3 selections
    var targetdiv = d3.select(target);

    // shorthand for pagetype
    var pt = config.pagetype;
    if (pt !== "genes" && pt !== "disease") {
        console.log("phenodigm2.makeTable - pagetype must be either 'genes' or 'disease'");
    }

    // setup columns
    var thead = targetdiv.append("thead").append("tr");
    var colnames = ["Gene", "Models", "Max Raw", "Avg Raw", "Phenodigm"];
    if (pt === "genes") {
        colnames = ["Disease", "Source", "Max Raw", "Avg Raw", "Phenodigm"];
    }
    colnames.map(function (x) {
        thead.append("th").append("span").classed("main", true).html(x);
    });
    // add a last column with blank title (will contain button indicating expansion)
    thead.append("th");

    // setup data body
    var tbody = targetdiv.append("tbody").classed("phenotable", true)
            .attr("pagetype", config.pagetype);
    if (pt === "disease") {
        tbody.attr("diseaseid", config.disease);
    } else {
        tbody.attr("geneid", config.gene);
    }

    // create a subset of the large array with only markers to show
    var dshow = darr;
    if (config.filter.length > 0) {
        dshow = darr.filter(function (x) {
            return config.filter.indexOf(x[config.filterkey]) >= 0;
        });
    }

    // convert from by-model to by-gene or by-disease representation        
    dshow = _.groupBy(dshow, function (x) {
        return x[config.groupby];
    });
    var dkeys = _.keys(dshow);

    var scorecols = ["maxRaw", "avgRaw"];

    // helper function computes the phenodigm score using max/avg components
    var phenscore = function (x) {
        var phenodigms = _.map(x, function (y) {
            return (y["maxNorm"] + y["avgNorm"]) / 2;
        });
        var phenmax = _.max(phenodigms);
        return phenmax.toPrecision(4);
    };
    // helper to add a series of <td> elements to a row summarizing scores
    var addScoreTds = function (trow, x) {
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
        trow.append("td").attr("titlef", "Click to display phenotype details")
                .classed("toggleButton", true)
                .append("i").classed("fa fa-plus-square more", true);
    };

    var impcimg = "<img class='small-logo' src='"+impc.logo+"'/>";
    console.log("imcpimg "+impcimg);

    if (pt === "disease") {
        // create html table (one line per gene)
        dkeys.map(function (key) {
            var x = dshow[key];
            // x is now an array of objects, display a summary row
            var trow = tbody.append("tr").attr("class", "phenotable").attr("geneid", x[0]["markerId"]);
            trow.append("td").append("a").classed(pt + "link", true)
                    .attr("href", impc.baseUrl + "/genes/" + x[0]["markerId"]).html(x[0]["markerSymbol"]);
            //var modeltd = trow.append("td").html(x.length);
            //modeltd.append("span").classed("small").html("("+x[0].markerNumModels+")");
            //console.log("new modeltd");
            var ximg = "";
            if (impc.isImpc(x)) {
                console.log("this gene "+x[0]["markerSymbol"]+" has an impc model");
                ximg = impcimg;
            } else {
                console.log("this gene "+x[0]["markerSymbol"]+" does NOT have an impc model");
            }
            
            trow.append("td").html(x.length + " <span class='small'>(" + x[0].markerNumModels + ") "+ximg);
            addScoreTds(trow, x);
        });
    } else {
        // create html table (one line per disease)    
        dkeys.map(function (key) {
            var x = dshow[key];
            // x is now an array of objects, display a summary row
            var trow = tbody.append("tr").attr("class", "phenotable").attr("diseaseid", x[0]["diseaseId"]);
            trow.append("td").append("a").classed(pt + "link", true)
                    .attr("href", impc.baseUrl + "/disease/" + x[0]["diseaseId"]).html(x[0]["diseaseTerm"]);
            trow.append("td").append("a").classed(pt + "link", true)
                    .attr("href", x[0]["diseaseUrl"]).html(x[0]["diseaseId"]);
            addScoreTds(trow, x);
        });
    }

    // attach a special listener to the gene links. This allows users to click 
    // on links without activating the phenogrid widget
    tbody.selectAll("a").on("click", function () {
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
            .style("width", (fullwidth - conf.detailwidth - 3) + "px")
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

    var outer = d3.select(conf.id).select("svg");
    var detail = d3.select(conf.id).select(".detail");

    // clear the contents of the svg
    outer.html("");

    // set some additional elements in conf, pertaining to size of page/drawing
    conf.w = parseInt(outer.style("width"));
    conf.h = parseInt(outer.style("height"));
    conf.winner = +conf.w - conf.margin[1] - conf.margin[3];
    conf.hinner = +conf.h - conf.margin[0] - conf.margin[2];
    //console.log("configuration is: " + JSON.stringify(conf));

    // adjust the svg, create a drawing box inside
    var svg = outer.attr("width", conf.w + "px").attr("height", conf.h + "px")
            .append("g").attr("width", conf.winner + "px").attr("height", conf.hinner + "px")
            .attr("transform", "translate(" + conf.margin[3] + "," + conf.margin[0] + ")");

    // find maximum values for axes     
    var xmax = _.max(_.pluck(darr, conf.axes[0]));
    var ymax = _.max(_.pluck(darr, conf.axes[1]));
    xmax = xmax < 0 ? conf.threshold : xmax;
    ymax = ymax < 0 ? conf.threshold : ymax;

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
        //console.log("clicked " + JSON.stringify(d));
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

    // shift the legend position down (avoid overlap with threshold line)
    var legyshift = Math.max(0, ymax - conf.threshold);
    conf.legendpos[2] += Math.ceil(yscale(0) - yscale(legyshift));
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
            .attr("class", "legendtext").text("Model w. modification in\ndisease-associated gene");
    svg.append("text").attr("y", conf.legendpos[2] + (3.5 * conf.legendspacing))
            .attr("x", conf.legendpos[0] - conf.radius).attr("class", "legendsource")
            .text("Data sources: IMPC, MGI");
    svg.selectAll(".legendtext").attr("x", conf.legendpos[1]);

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
    var genenum = geneId.split(":")[1];
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
 * Fills in data into a phenogrid skeleton object. A skeleton obtained from
 * the IMPC API is incomplete - it lacks some information stored within the
 * modelAssociations object defined on the page. This function transfers that 
 * data from the modelAssociations into the skeleton.
 * 
 * @param {object} skeleton - an incomplete phenogrid skeleton as obtained from 
 * the IMPC API
 * @returns {object} - a ready-to-use phenogrid skeleton 
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
        return group;
    };

    skeleton.xAxis = skeleton.xAxis.map(function (x) {
        return completeEntities(x);
    });

    return skeleton;
};


/* global modelAssociations */

/**
 * Create a table with phenotype details about individual models
 * 
 * @param {selection} targetdiv - d3 selection, output will be appended here
 * @param {string} geneId - gene marker
 * @param {array} models - array of objects defining mouse models
 * @returns {undefined}
 */
impc.phenodigm2.insertModelDetails = function (targetdiv, geneId, models) {

    // create a table for the outpt    
    var tablediv = targetdiv.append("table").classed("table", true);

    // identify the rows in modelAssociations that are relevant
    var details = modelAssociations.filter(function (x) {
        return x["markerId"] === geneId;
    });
    details = _.indexBy(details, "id");
    // here, "details" is either an object with markerIds as keys,
    // or is an empty object

    // setup columns. Can have headers with or without scores.
    var thead = tablediv.append("thead").append("tr");
    var headcols = ["Model", "Genotype", "Phenotypes"];
    if ((_.keys(details)).length > 0) {
        headcols = ["Model", "Genotype", "Max Raw", "Avg Raw", "Phenodigm", "Phenotypes"];
    }
    headcols.map(function (x) {
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

    // create html table (one line per model)    
    models.map(function (modeldata) {
        var trow = tbody.append("tr");
        var modelid = modeldata["id"];
        trow.append("td").html(modelid);
        if (_.has(modeldata, "label")) {
            trow.append("td").html(tohtml(modeldata["label"]));
        } else {
            trow.append("td").html(tohtml(modeldata["description"]));
        }
        if (headcols.length > 4) {
            if (_.has(details, modelid)) {
                trow.append("td").classed("numeric", true).html(details[modelid]["maxRaw"]);
                trow.append("td").classed("numeric", true).html(details[modelid]["avgRaw"]);
                trow.append("td").classed("numeric", true).html(phenscore(details[modelid]));
            } else {
                trow.append("td");
                trow.append("td");
                trow.append("td");
            }
        }
        var tdpheno = trow.append("td");
        modeldata.phenotypes.map(function (pheno, i) {
            if (i > 0) {
                tdpheno.append("span").classed("semicolon", true).html("; ");
            }
            tdpheno.append("a").attr("href", monarchUrl + "/phenotype/" + pheno.id).html(pheno.term);
        });
        // identify whether model was impc and highlight it
        if (impc.isImpc(modeldata)) {
            trow.classed("impc", true);
        }
    });
};


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

    // fetch a core skeleton from the IMPC api. 
    var ajax = $.getJSON(impc.baseUrl + "/phenodigm2/phenogrid",
            {geneId: geneId, diseaseId: diseaseId, pageType: pageType}
    );
    // when fetch complete, use the skeleton data to create a phenogrid    
    ajax.done(function (result) {
        // complete the skeleton using modelAssociations
        result = impc.phenodigm2.completeGridSkeleton(result);
        // perhaps create an inner table (disease pages only)
        if (pageType === "disease") {
            var innertab = d3.select(tableId + " .inner[geneid='" + geneId + "'] .inner_table");
            impc.phenodigm2.insertModelDetails(innertab, geneId, result.xAxis[0].entities);
        }
        // generate phenogrid widget (heatmap)
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
    var diseaseId = "", geneId = "";
    if (pagetype === "disease") {
        diseaseId = tbody.attr("diseaseid");
    } else if (pagetype === "genes") {
        geneId = tbody.attr("geneid");
    }

    // allow users to click on a row and see a PhenoGrid widget
    $(tableId + ' tbody').on('click', 'tr.phenotable', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);
        if (pagetype === "disease") {
            geneId = tr.attr("geneid");
        } else {
            diseaseId = tr.attr("diseaseid");
        }
        if (typeof geneId === "undefined") {
            return;
        }

        // toggle (show/hide) an inner box
        if (row.child.isShown()) {
            row.child.hide();
            tr.find("td.toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");
        } else {
            row.child(impc.phenodigm2.makeTableChildRow(pagetype, geneId, diseaseId)).show();
            impc.phenodigm2.insertPhenogrid(tableId, geneId, diseaseId, pagetype);
            tr.find("td.toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
        }
    });
};


/****************************************************************************
 * Loading of mouse model tables
 **************************************************************************** */

/**
 * registers a button to fetch info about mouse models
 * 
 * @param linkid - id of element that should be clicked to fetch data
 * @param targetid - id of element that will hold result. Must have a child div
 * with class inner
 */
impc.phenodigm2.initLoadModels = function (linkid, targetid) {
    var linkobj = d3.select("#" + linkid);
    var geneId = linkobj.attr("geneid");
    // set up a behavior that when user clicks on button, ajax loads a set of 
    // models with the desired gene
    linkobj.on("click", function () {
        var targetdiv = d3.select("#" + targetid + " .inner");
        targetdiv.html("Fetching data...");
        var ajax = $.getJSON(impc.baseUrl + "/phenodigm2/mousemodels",
                {geneId: geneId});
        ajax.done(function (result) {
            targetdiv.html("");
            impc.phenodigm2.insertModelDetails(targetdiv, geneId, result);
        });
    });

};
