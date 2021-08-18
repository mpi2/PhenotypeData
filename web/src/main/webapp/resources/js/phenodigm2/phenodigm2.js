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
 * d3 - used to generate visualizations, generate tables from json
 * _ - misc manipulations of objects and arrays
 * $ - jquery, used to manipulate dom (legacy)
 * Phenogrid - used to display phenogrid widget
 * modelAssociations - object expected to be defined in page before these
 *                     functions are called
 * 
 * The code uses an ugly mixture of d3 and jQuery. This is mostly for historic.
 * Development should gradually switch to d3-only. 
 * 
 * Parts of the code are based on diseasetableutils.js
 *  
 */

/* global d3, _, $, impc, monarchUrl, Phenogrid, modelAssociations */

if (typeof impc === "undefined") {
    impc = {};
}
impc.phenodigm2 = {
    scores: ["avgRaw", "avgNorm", "maxRaw", "maxNorm"]
};
impc.logo = impc.baseUrl + "/img/impc.png";
impc.logohtml = "<img class='small-logo' src='" + impc.logo + "'/>";

// definitions of urls 
impc.urls = {
    mgigenoview: "http://www.informatics.jax.org/allele/genoview/",
    genes: impc.baseUrl + "/genes/",
    disease: impc.baseUrl + "/disease/"
};


/****************************************************************************
 * General helper functions
 **************************************************************************** */

/**
 * Identify whether x refers to an IMPC model
 * This function handles multiple cases:
 *   - when x is an array of objects
 *   - when x is an object with a source field
 *   - when x is an object without a source field (but with an info field)
 * 
 * @param {type} x
 * @returns {Boolean} The implementation is such that when a model comes from
 * MGI, the return value is False. Otherwise, the return value is True. This
 * accepts models sources by 3i, MGP, Eurocomm, IMPC, etc.
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

    // handling of x as an object of various types, default as a string
    if (_.has(x, "source")) {
        return impc.isImpc(x.source);
    } else if (_.has(x, "info")) {
        x.info.map(function (y) {
            if (y.id.startsWith("Source")) {
                result = impc.isImpc(y.value);
            }
        });
    } else {
        return x !== "MGI";
    }

    return result;
};


/**
 * Compute phenodigm score using max & avg components 
 * 
 * @param {type} x can be an object or an array
 * when object, should have keys maxNorm and avgNorm
 * when array, each component should have those two keys.
 * @returns {impc.phenscore.phenmax}
 */
impc.phenscore = function (x) {
    // test if x is a simple object with the right fields
    if (_.has(x, "maxNorm") && _.has(x, "avgNorm")) {
        var result = ((x["maxNorm"] + x["avgNorm"]) / 2);
        return (result > 0 ? +result.toPrecision(4) : result);
    }
    // interpret x as an array of such objects        
    var phenmax = _.max(_.map(x, impc.phenscore));
    return (phenmax > 0 ? phenmax.toPrecision(4) : phenmax);
};


/**
 * select either geneId or diseaseId as a hitId
 * 
 * @param {type} geneId
 * @param {type} diseaseId
 * @param {type} pageType
 * @returns {unresolved}
 */
impc.phenodigm2.getHitId = function (geneId, diseaseId, pageType) {
    return pageType === "disease" ? geneId : diseaseId;
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
 *   should include "markerlist", "requestpagetype", "diseaseid"
 *   
 * @returns {undefined}
 */
impc.phenodigm2.makeTable = function (darr, target, config) {

    // start working with d3 selections
    var targetdiv = d3.select(target);

    // shorthand for pagetype
    var pt = config.pageType;
    if (pt !== "genes" && pt !== "disease") {
        console.log("phenodigm2.makeTable - pageType must be either 'genes' or 'disease'; found " + pt);
    }

    // record setting for inner table display
    if (_.has(config, "innerTables")) {
        targetdiv.attr("innerTables", config.innerTables);
    } else {
        targetdiv.attr("innerTables", false);
    }

    // setup columns
    var thead = targetdiv.append("thead").append("tr");
    var colnames = ["Gene", "Models Scored (Total)", "Max Score", "Avg Score", "Phenodigm"];
    if (pt === "genes") {
        colnames = ["Disease", "Source", "Max Score", "Avg Score", "Phenodigm"];
    }
    colnames.map(function (x) {
        thead.append("th").append("span").classed("main", true).html(x);
    });
    // add a last column with blank title (will contain button indicating expansion)
    thead.append("th").classed("nobackground", true);

    // setup data body
    var tbody = targetdiv.append("tbody").classed("phenotable", true)
            .attr("pageType", config.pageType);
    if (pt === "disease") {
        tbody.attr("diseaseid", config.disease);
    } else {
        tbody.attr("geneid", config.gene);
    }

    // create a subset of the large array with only markers to show
    var dshow = darr;
    if (config.filter.length > 0) {
        dshow = darr.filter(function (x) {
            return config.filter.indexOf(x[config.filterKey]) >= 0;
        });
    }

    // convert from by-model to by-gene or by-disease representation        
    dshow = _.groupBy(dshow, function (x) {
        return x[config.groupBy];
    });
    var dkeys = _.keys(dshow);

    var scorecols = ["maxRaw", "avgRaw"];

    // helper adds a series of <td> elements to a table row
    var addScoreTds = function (trow, x) {
        if (x.length > 1) {
            // display a summary of all scores in x in a single td
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
        trow.append("td").classed("numeric", true).html(impc.phenscore(x));
        trow.append("td").attr("titlef", "Click to display phenotype details")
                .classed("toggleButton", true)
                .append("i").classed("fa fa-plus-square more", true);
    };
    // a part of addToRow for disease pages
    var addToDiseaseRow = function (x) {
        var trow = tbody.append("tr").attr("class", "phenotable").attr("geneid", x[0]["markerId"]);
        trow.append("td").append("span").classed(pt + "link", true)
                .html(x[0]["markerSymbol"]);
        var ximg = impc.isImpc(x) ? impc.logohtml : "";
        trow.append("td").html(x.length + " <span class='small'>(" + x[0].markerNumModels + ") " + ximg);
        return trow;
    };
    // a part of addToRow for genes pages
    var addToGenesRow = function (x) {        
        // x is now an array of objects, display a summary row
        var trow = tbody.append("tr").attr("class", "phenotable").attr("diseaseid", x[0]["diseaseId"]);
        trow.append("td").append("span").classed(pt + "link", true)
                .html(x[0]["diseaseTerm"]);
        trow.append("td").append("span").classed(pt + "link", true)
                .html(x[0]["diseaseId"]);
        return trow;
    };
    // Create a row in table (but skip x if phenoscore below threshold)
    var addToRow = function (x, addSpecific) {
        if (impc.phenscore(x) < config.minScore) {
            return;
        }
        var trow = addSpecific(x);
        if (config.count === undefined) {
            config.count = 0;
        }
        config.count += 1;
        addScoreTds(trow, x);
    };

    // main part of function that generates the table (one line at a time)
    if (pt === "disease") {
        dkeys.map(function (key) {
            var x = dshow[key];
            // x is now an array of objects
            addToRow(x, addToDiseaseRow);
        });
    } else {
        dkeys.map(function (key) {
            var x = dshow[key];
            // x is now an array of objects            
            addToRow(x, addToGenesRow);
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
    // create an svg filter for shadows
    var filter = svg.append("defs").append("filter").attr("id", "drop-shadow")
            .attr("x", "-50%").attr("y", "-50%").attr("width", "200%").attr("height", "200%");
    filter.append("feGaussianBlur").attr("in", "SourceAlpha").attr("stdDeviation", 3);
    filter.append("feOffset").attr("dx", 0).attr("dx", 0);
    var filtermerge = filter.append("feMerge");
    filtermerge.append("feMergeNode");
    filtermerge.append("feMergeNode").attr("in", "SourceGraphic");

    var detail = container.append("div").attr("class", "leftright detail")
            .style("width", (conf.detailwidth - (2 * conf.detailpad)) + "px")
            .style("margin-top", conf.margin[0] + "px")
            .style("margin-bottom", conf.margin[2] + "px")
            .style("padding", conf.detailpad + "px")
            .style("height", (conf.h - conf.margin[0] - conf.margin[2] - (2 * conf.detailpad)) + "px")
            .style("display", "none");

    var brsection = function (title, classname) {
        detail.append("div").classed("infotitle", true).text(title);
        detail.append("div").classed(classname + " info", true).text(title + " value");
    };

    brsection("Model", "model");
    brsection("Source", "source");
    brsection("Background", "background");
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
    outer.selectAll("g").remove();

    // set some additional elements in conf, pertaining to size of page/drawing
    conf.w = parseInt(outer.style("width"));
    conf.h = parseInt(outer.style("height"));
    conf.winner = +conf.w - conf.margin[1] - conf.margin[3];
    conf.hinner = +conf.h - conf.margin[0] - conf.margin[2];

    // adjust the svg, create a drawing box inside
    var svg = outer.attr("width", conf.w + "px").attr("height", conf.h + "px")
            .append("g").attr("width", conf.winner + "px").attr("height", conf.hinner + "px")
            .attr("transform", "translate(" + conf.margin[3] + "," + conf.margin[0] + ")");

    // find maximum values for axes     
    var xmax = _.max(_.pluck(darr, conf.axes[0]));
    var ymax = _.max(_.pluck(darr, conf.axes[1]));
    xmax = xmax <= 0 ? conf.threshold : xmax + (conf.threshold * 0.01);
    ymax = ymax <= 0 ? conf.threshold : ymax + (conf.threshold * 0.01);

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
        // cast a highlighting shadow for this model
        svg.selectAll(".marker").classed("shadow", false).style("fill-opacity", 0.7);
        d3.select(this).style("fill-opacity", 1).classed("shadow", true);
        // transfer data about this model into the info box
        detail.style("display", "block");
        detail.select(".source").text(d.source);
        detail.select(".background").text(d.background);
        detail.select(".model").text(d.id);
        detail.select(".description").text(d.description);
        detail.select(".scores").text("(" + d[conf.axes[0]] + ", " + d[conf.axes[1]] + ")");
        detail.style("background-color", "#fff")
                .transition().duration(50)
                .style("background-color", "#fafafa")
                .transition().delay(200).duration(1000)
                .style("background-color", "#fff");
        d3.event.stopPropagation();
    };

    var sourceString = function (d) {
        return impc.isImpc(d.source) ? "IMPC" : "MGI";
    };

    // add points to the chart 
    //  - rect for models with annotated genes
    //  - circle for models with other genes
    svg.selectAll(".marker1").data(darr_known).enter().append("rect")
            .attr("class", function (d) {
                return "marker curated " + sourceString(d);
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
                return "marker " + sourceString(d);
            })
            .attr("cx", function (d) {
                return xscale(d[conf.axes[0]]);
            })
            .attr("cy", function (d) {
                return yscale(d[conf.axes[1]]);
            })
            .attr("r", conf.radius)
            .on("click", showModelInfo);

    // style for the markers
    svg.selectAll(".marker")
            .attr("fill", function (d) {
                if (d3.select(this).attr("class").search(/IMPC/) >= 0) {
                    return conf.color[0];
                }
                return conf.color[1];
            })
            .style("fill-opacity", 0.7);

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
    var longtext = svg.append("text").attr("y", conf.legendpos[2] + (2 * conf.legendspacing))
            .attr("class", "legendtext");
    longtext.append("tspan").classed("legendtext", true).text("Model w. modification in");
    longtext.append("tspan").classed("legendtext", true).attr("dy", "1.2em").text("disease-associated gene");
    svg.append("text").attr("y", conf.legendpos[2] + (4.0 * conf.legendspacing))
            .attr("x", conf.legendpos[0] - conf.radius).attr("class", "legendsource")
            .text("Data sources: IMPC, MGI");
    svg.selectAll(".legendtext").attr("x", conf.legendpos[1]);

};


/****************************************************************************
 * Handling for phenogrid widget
 **************************************************************************** */

/**
 * Create an id string that can identify a phenogrid. The id is a composite
 * of the table and geneId/diseaseId, depending on the pagetype.
 *  
 * @param {type} tableId
 * @param {type} geneId
 * @param {type} diseaseId
 * @param {type} pageType
 * @returns {undefined}
 */
impc.phenodigm2.makePgid = function (tableId, geneId, diseaseId, pageType) {
    var result = "pg_" + tableId.replace("#", "") + "_";
    if (pageType === "genes") {
        result += diseaseId.replace(":", "_");
    } else {
        result += geneId.replace(":", "_");
    }
    return result;
};


/**
 * Create a div with attributes. (The div can be used to hold a phenogrid)
 *  
 * @param tableId - id of background element, will be included as part of an id
 * @param geneId - string, use a gene identifier (e.g. MGI:xxxxx)
 * @param diseaseId - string, use a disease identifier (e.g. OMIM:xxxxx)
 * @param pageType - string, use "disease" or "genes"
 * @return a jquery div 
 */
impc.phenodigm2.makeTableChildRow = function (tableId, geneId, diseaseId, pageType) {
    var pgid = impc.phenodigm2.makePgid(tableId, geneId, diseaseId, pageType);
    var hitId = impc.phenodigm2.getHitId(geneId, diseaseId, pageType);
    var innerdiv = $(document.createElement('div'))
            .addClass("inner")
            .attr({
                geneId: geneId,
                diseaseId: diseaseId,
                hitId: hitId,
                pageType: pageType
            }).css({"padding": "0"});
    innerdiv.append("<div class='inner_table' pgid='" + pgid + "'></div>");
    innerdiv.append("<div class='inner_pg' id='" + pgid + "'></div>");
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
 * @param {string} pageType - either disease or genes, breakdown of models by ids 
 * is handled differently in these two cases. Disease pages have modelAssociations
 * with exactly one entry per model id. Gene pages can have multiple.
 * @param {string} geneId
 * @param {string} diseaseId
 * @returns {object} - a ready-to-use phenogrid skeleton 
 */
impc.phenodigm2.completeGridSkeleton = function (skeleton, geneId, diseaseId, pageType) {

    // find those modelAssociations relevant only to this gene and disease
    var nowassoc = [];
    if (pageType === "disease") {
        nowassoc = modelAssociations.filter(function (x) {
            return x["markerId"] === geneId;
        });
    } else {
        nowassoc = modelAssociations.filter(function (x) {
            return x["diseaseId"] === diseaseId;
        });
    }

    // change the modelAssociations array into an indexed object        
    var scoredModels = _.groupBy(nowassoc, "id");

    // helper to transfer scores from scoredModels into the skeleton
    var completeEntities = function (group) {
        // fill in missing scores into the entities
        group.entities = group.entities.map(function (x) {
            if (_.has(scoredModels, x.id)) {
                x.score.score = impc.phenscore(scoredModels[x.id]);
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


/**
 * Create a table with phenotype details about individual models
 * 
 * @param {selection} targetdiv - d3 selection, output will be appended here
 * @param {string} hitId - id used as filter
 * @param {array} models - array of objects defining mouse models
 * @returns {undefined}
 */
impc.phenodigm2.insertModelDetails = function (targetdiv, hitId, models) {

    // create a table for the outpt    
    var tablediv = targetdiv.append("table").classed("table", true);

    // identify the rows in modelAssociations that are relevant
    var details = modelAssociations.filter(function (x) {
        if (_.has(x, "markerId")) {
            return x["markerId"] === hitId;
        } else if (_.has(x, "diseaseId")) {
            return x["diseaseId"] === hitId;
        } else {
            return false;
        }
    });
    details = _.indexBy(details, "id");
    // here, "details" is either an object with markerIds as keys,
    // or is an empty object

    // setup columns. Can have headers with or without scores.
    var thead = tablediv.append("thead").append("tr");
    var headcols = ["Model", "Genotype", "Max Raw", "Avg Raw", "Phenodigm", "Phenotypes"];
    headcols.map(function (x) {
        var xclass = (x === "Phenotypes" ? "th-wide" : "");
        thead.append("th").classed(xclass, true).append("span").classed("main", true).html(x);
    });

    // setup data body
    var tbody = tablediv.append("tbody").attr("hitid", hitId);

    // pretty printing of models e.g. ABC<xyz>
    var tohtml = function (x) {
        return x.replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\//g, "/ ");
    };
    // helper to insert table tds with numeric values
    var tdhtml = function (target, text) {
        target.append("td").classed("numeric", true).html(text);
    };

    // create html table (one line per model)    
    models.map(function (modeldata) {
        var trow = tbody.append("tr");
        var rowimpc = impc.isImpc(modeldata);
        var modelid = modeldata["id"];
        if (rowimpc) {
            trow.append("td").html(modelid + impc.logohtml);
        } else {
            trow.append("td").append("a").attr("href", impc.urls.mgigenoview + modelid).html(modelid);
        }
        if (_.has(modeldata, "label")) {
            // find background from the modeldata info fields            
            var bg = modeldata["info"].filter(function (x) {
                return x["id"].startsWith("Background");
            });
            var bg = bg[0]["value"] + "<br/>";
            trow.append("td").html(bg + tohtml(modeldata["label"]));
        } else {
            var bg = modeldata["geneticBackground"] + "<br/>";
            trow.append("td").html(bg + tohtml(modeldata["description"]));
        }

        if (headcols.length > 4) {
            if (_.has(details, modelid)) {
                tdhtml(trow, details[modelid]["maxRaw"]);
                tdhtml(trow, details[modelid]["avgRaw"]);
                tdhtml(trow, impc.phenscore(details[modelid]));
            } else {
                trow.append("td").attr("colspan", 3).classed("numeric", true).html("Below threshold");
            }
        }
        var tdpheno = trow.append("td").attr("state", 0);
        var ps = modeldata.phenotypes.length !== 1 ? "s" : "";
        tdpheno.append("a").classed("phenotype-toggle", true)
                .html("[" + modeldata.phenotypes.length + " phenotype" + ps + "]");
        modeldata.phenotypes.map(function (pheno, i) {
            if (i > 0) {
                tdpheno.append("span").classed("semicolon phenotype phenotype-hidden", true).html("; ");
            }
            tdpheno.append("a").classed("phenotype phenotype-hidden", true)
                    .attr("href", monarchUrl + "/phenotype/" + pheno.id).html(pheno.term);
        });
    });

    // attach click handlers for "a" that toggle phenotype display
    tbody.selectAll(".phenotype-toggle").on("click", function (d) {
        var thistd = d3.select(this.parentNode);
        var phenotypes = thistd.selectAll(".phenotype");
        var thisstate = +thistd.attr("state");
        phenotypes.classed("phenotype-hidden", thisstate === 1);
        thistd.attr("state", (thisstate + 1) % 2);
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

    var hitId = impc.phenodigm2.getHitId(geneId, diseaseId, pageType);

    // identify the div that should hold the phenodigm widget
    var targetdiv = $(tableId).find(".inner[hitid='" + hitId + "']").find(".inner_pg");
    // identify whether the table requires inner-table inserts
    var innerTables = d3.select(tableId).attr("innerTables") === "true";

    var gridColumnWidth = 25;
    var gridRowHeight = 50;

    // fetch a core skeleton from the IMPC api. 
    var ajax = $.getJSON(impc.baseUrl + "/phenodigm2/phenogrid",
            {geneId: geneId, diseaseId: diseaseId, pageType: pageType}
    );
    // when fetch complete, use the skeleton data to create a phenogrid    
    ajax.done(function (result) {
        // complete the skeleton using modelAssociations
        result = impc.phenodigm2.completeGridSkeleton(result, geneId, diseaseId, pageType);
        // perhaps create an inner table?
        if (innerTables) {
            var pgid = impc.phenodigm2.makePgid(tableId, geneId, diseaseId, pageType);
            var innertab = d3.select(tableId + " .inner_table[pgid='" + pgid + "']");
            impc.phenodigm2.insertModelDetails(innertab, hitId, result.xAxis[0].entities);
        }
        // generate phenogrid widget (heatmap)
        Phenogrid.createPhenogridForElement(targetdiv, {
            serverURL: 'https://legacy.monarchinitiative.org',
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
 * @param tableId - string, id of table (includeing #)
 * @param table - object contructed by DataTable()
 * 
 */
$.fn.addTableClickPhenogridHandler = function (tableId, table) {
    var tbody = $(tableId + ' tbody');
    var pageType = tbody.attr("pageType");
    var diseaseId = "", geneId = "";
    if (pageType === "disease") {
        diseaseId = tbody.attr("diseaseid");
    } else if (pageType === "genes") {
        geneId = tbody.attr("geneid");
    }

    // allow users to click on a row and see a PhenoGrid widget
    $(tableId + ' tbody').on('click', 'tr.phenotable', function () {
        var tr = $(this).closest('tr');
        var row = table.row(tr);
        if (pageType === "disease") {
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
            row.child(impc.phenodigm2.makeTableChildRow(tableId, geneId, diseaseId, pageType)).show();
            impc.phenodigm2.insertPhenogrid(tableId, geneId, diseaseId, pageType);
            tr.find("td.toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
        }
    });
};
