(function(d3) {

	// Example for hover-over lines 
	// http://bl.ocks.org/mbostock/3709000

	window.parallel = function(model, colors, defaults, highlighter, axes) {
		
		var labelColorList = [
		      				'rgb(36, 139, 75)',
		      				'rgb(191, 75, 50)',
		      				'rgb(255, 201, 67)',
		      				'rgb(191, 151, 50)',
		      				'rgb(247, 157, 70)',
		      				'#16532D',  
		      				'#0978A1'];
		var self = {}, dimensions, dragging = {}, highlighted = null, highlighted2 = null, container = d3.select("#parallel");
		var text = null;
		var line = d3.svg.line().interpolate('cardinal').tension(0.85), axis = d3.svg.axis().orient("left"), background, foreground;
		var axisColors = {};
		var cars = model.get('data');
		var i = 0;
		var inactiveGroups = [];
		var geneList = [];
		model.get('filtered').map(function(d,i){geneList.push(d.gene);});

		for (var key in groups){
			if (!axisColors[groups[key]]){
				axisColors[groups[key]] = labelColorList[i];
				i++;
			}
		}
		
		self.update = function(data, defaults) {
  			cars = data;
  			geneList = [];
  			model.get('filtered').map(function(d,i){geneList.push(d.gene);});
  		};

  		model.bind('change:filtered', function() { self.update()});

		function redraw(){
			cars = model.get('data');
			self.render();
		}
		
		self.render = function() {

			container.selectAll("svg").remove();
			// Styling config
			var cellWidth = 16;
			var cellPadding = 10;
			var cellHeight = 12;
			
			var bounds = [ $(container[0]).width(), $(container[0]).height() ], m = [ 170, 10, 10, 10 ], w = bounds[0] - m[1] - m[3], h = bounds[1] - m[0] - m[2] - 2*(cellPadding + cellHeight);
			var x = d3.scale.ordinal().rangePoints([ 0, w ], 1), y = {};
			var legend = container.append("svg:svg").attr("width", w + m[1] + m[3]).attr("height", (cellHeight + cellPadding)).append("svg:g").attr("class", "highcharts-legend");
			var labelXStart = []; 
			
			legend.selectAll("g.legendCells")
			    .data(Object.keys(axisColors))
			    .enter()
			    .append("g")
			    .attr("class", "legendCells")
			    .attr("transform", function(d,i) { return "translate(" + getXTransform(d,i) + ", 0)"})
			    .classed("legendCellInactive", function(d){ return (inactiveGroups.indexOf(d) >= 0 );});
			
			legend.selectAll("g.legendCells")
				.append("rect")
			    .attr("height", cellHeight)
			    .attr("width", cellWidth)
			    .style("fill", function(d) {return (inactiveGroups.indexOf(d) >=0 ) ? "grey" : axisColors[d];});
		    
			legend.selectAll("g.legendCells")
		    	.append("text")
		    	.attr("class", "legendLabels");
						
			legend.selectAll("g.legendCells")
				.select("text.legendLabels").style("display", "block")
				.style("text-anchor", "start").attr("x", cellWidth + cellPadding)
				.attr("y", 5 + (cellHeight / 2)).text(function(d) {return d;});
			
			
			// Click actions on legend items
			legend.selectAll(".legendCells, .legendCells rect").on("click", function() { 
				if (!d3.select(this).classed("legendCellInactive")){ // toggle to inactive
					d3.select(this).classed("legendCellInactive", true);
					d3.select(this).selectAll("rect").style("fill", "grey");
					inactiveGroups.push(d3.select(this).selectAll("text").text());
					redraw();
				} else { // toggle to active
					d3.select(this).classed("legendCellInactive", false);
					d3.select(this).selectAll("rect").style("fill", axisColors[d3.select(this).selectAll("text").text()]);					
					inactiveGroups.splice(inactiveGroups.indexOf(d3.select(this).selectAll("text").text()),1);
					redraw();
				}
			});
			
			var lineLegend = container.append("svg:svg").attr("width", w + m[1] + m[3]).attr("height", (cellHeight*2 + cellPadding)).append("svg:g").attr("class", "highcharts-legend");
			labelXStart = []; 
			lineLegend.selectAll("g.lineLegendCells")
			    .data(Object.keys(colors))
			    .enter()
			    .append("g").attr("width", cellWidth).attr("height", cellHeight).classed("lineLegendCells", "true")
			    .attr("transform", function(d,i) { return "translate(" + getXTransform(d,i) + ", 0)"})
			    .append("line").attr("x1", 0).attr("y1", cellHeight/2).attr("x2", cellWidth).attr("y2", cellHeight/2)
			    .style("stroke", function(d,i){return colors[d];}).style("stroke-width", "2");
			lineLegend.selectAll("g.lineLegendCells")
	    		.append("text")
	    		.attr("class", "legendLabels");
			lineLegend.selectAll("g.lineLegendCells")
				.select("text.legendLabels").style("display", "block")
				.style("text-anchor", "start").attr("x", cellWidth + cellPadding)
				.attr("y", 5 + (cellHeight / 2)).text(function(d) {return d;});
		
			lineLegend.append("text").attr("id","geneHover").attr("transform", "translate(0,28)").classed("legendLabels", "true");
			
			function getXTransform(d,i){ 
				var res = labelXStart.reduce(function(a, b) {
					  return a + b;
					}, 0);
				labelXStart[i] = cellWidth + cellPadding*2 + d.length * 7;
				return res;
			}
				
			var svg = container.append("svg:svg").attr("width", w + m[1] + m[3]).attr("height", h + m[0] + m[2]).append("svg:g").attr("transform", "translate(" + m[3] + "," + m[0] + ")");
			
			// Extract the list of dimensions and create a scale for each.
			x.domain(dimensions = d3.keys(cars[0]).filter(function(d) {
				return d != "gene" && d != "significantMask" && d != "group" && d != "accession" && d != "id" && inactiveGroups.indexOf(groups[d]) < 0 && (y[d] = d3.scale.linear().domain(d3.extent(cars, function(p) {
					return +p[d];
				})).range([ h, 0 ]));
			}));

			// Add grey background lines for context.
			background = svg.append("svg:g").attr("class", "background").selectAll("path").data(cars).enter().append("svg:path").attr("d", path).attr("style", function(d) {
				return getStyles(d,"background");
			});

			// Add blue foreground lines for focus.
			foreground = svg.append("svg:g").attr("class", "foreground").selectAll("path").data(cars).enter().append("svg:path").attr("d", path)
				.attr("style", function(d) {return "stroke:" + colors[d.group] + ";" + getStyles(d,"foreground");})
				.attr("class", function(d) {return d.gene;})
				.on("click", function (d,i){ highlighter.deselect(); d3.select("#geneHover").html("Genotype effect for gene: &nbsp; &nbsp;&nbsp;    " + d.gene.split("(")[0] + ""); highlighter.select(geneList.indexOf(d.gene));})
				;

			// Add a group element for each dimension.
			var g = svg.selectAll(".dimension").data(dimensions).enter().append("svg:g").attr("class", "dimension").attr("transform", function(d) {
				return "translate(" + x(d) + ")";
			}).call(d3.behavior.drag().on("dragstart", function(d) {
				dragging[d] = this.__origin__ = x(d);
				background.attr("visibility", "hidden");
			}).on("drag", function(d) {
				dragging[d] = Math.min(w, Math.max(0, this.__origin__ += d3.event.dx));
				foreground.attr("d", path);
				dimensions.sort(function(a, b) {
					return position(a) - position(b);
				});
				x.domain(dimensions);
				g.attr("transform", function(d) {
					return "translate(" + position(d) + ")";
				})
			}).on("dragend", function(d) {
				delete this.__origin__;
				delete dragging[d];
				transition(d3.select(this)).attr("transform", "translate(" + x(d) + ")");
				transition(foreground).attr("d", path);
				background.attr("d", path).transition().delay(50).duration(0).attr("visibility", null);
			}));

			// Add an axis and title.
			var barTitles = g.append("svg:g").attr("class", "axis").each(function(d) {
				d3.select(this).call(axis.scale(y[d]));
			}).append("a").attr("xlink:href", function(d) {
				return links[d];
			}).append("svg:text").attr("text-anchor", "start").attr("y", 0).attr("x", 5).attr("transform", function(d) {
				return "rotate(-90)";
			}).text(String).style("fill", function(d) { return axisColors[groups[d]]; }).classed("axis-label", true).attr("class", function(d) {
				return groups[d].replace(/ /g, "_");
			}).attr("id", function(d){ return "id"+d.replace(/ /g, "_");}).append("svg:title").text(String);

			// Add and store a brush for each axis.
			g.append("svg:g").attr("class", "brush").each(function(d) {
				d3.select(this).call(y[d].brush = d3.svg.brush().y(y[d]).on("brush", brush));
			}).selectAll("rect").attr("x", -12).attr("width", 24);

		
			
			function position(d) {
				var v = dragging[d];
				return v == null || v == "N/A" ? x(d) : null;
			}

			
			function getStyles(d, plan){
				var style = "";
				if (d.group == "Normal" || d.group == "Mean") {
					style = "stroke-opacity: 1;";
					if (plan == "background"){
						style += " stroke:" + colors[d.group] + ";"
					}
					
				} else {
					if (plan == "foreground"){
						style = "stroke-opacity: 0.35;";
					} else if (plan == "background"){
						style = "stroke-opacity: 1;";
					}else {
						style = "stroke-opacity: 0.45;";
					}
				}
				return style;
			}
			
			// Returns the path for a given data point.
			function path(d) {
				//return line(dimensions.map(function(p) { return [position(p), y[p](d[p])]; }));
				return line(dimensions.map(function(p) {
					// check for undefined values
					if (d[p] == null || d[p] == "N/A") {
						return [ x(p), y[p](defaults[p]) ];
					} else {
						return [ x(p), y[p](d[p]) ];
					}
				}));
			}

			// Handles a brush event, toggling the display of foreground lines.
			function brush() {
				var actives = dimensions.filter(function(p) {
					return !y[p].brush.empty();
				});

				var extents = actives.map(function(p) {
					return y[p].brush.extent();
				});

				var filter = {};
				_(actives).each(function(key, i) {
					filter[key] = {
						min : extents[i][0] - 0.000001,
						max : extents[i][1] + 0.000001
					};
				});
				model.set({
					filter : filter
				});

				foreground.style("display", function(d) {
					return actives.every(function(p, i) {
						return extents[i][0] - 0.000001 <= d[p] && d[p] <= extents[i][1] + 0.000001;
					}) ? null : "none";
				});
			}

			function transition(g) {
				return g.transition().duration(500);
			}

			function getRadius(y) {
				if (y == null) {
					return 2;
				}
				return 0;
			}

			function arrayFromMaskArray(masksArray, coulmnCount){

                var aFromMask = [];
				if (masksArray.length == 0){
					return aFromMask;
				} else if (masksArray.length == 1){
                    aFromMask = arrayFromMask(masksArray[0]);
					if (coulmnCount > aFromMask.length){
						// pad falses for the positions not visible in the mask
						var needPadding = Math.min(coulmnCount, 31) - aFromMask.length;
						for (i = 0; i < needPadding; i++){
							aFromMask.push(false);
						}
					}
				} else {
					masksArray.forEach(function(mask){
						var tempBooleanArray = arrayFromMask(mask);
                        if (tempBooleanArray.length < 31){
                            var needPadding = 31 - tempBooleanArray.length;
                            for (i = 0; i < needPadding; i++){
                                tempBooleanArray.push(false);
                            }
                        }
                        aFromMask.push(...tempBooleanArray);
					});
				}

				return aFromMask;
			}

            function arrayFromMask (nMask) {
            	var aFromMask = [];
                // nMask must be between -2147483648 and 2147483647
                if (nMask > 0x7fffffff || nMask < -0x80000000) {
                    throw new TypeError("arrayFromMask - out of range");
                }
                for (var nShifted = nMask, aFromMask = []; nShifted;
                     aFromMask.push(Boolean(nShifted & 1)), nShifted >>>= 1);
                return aFromMask;
            }


			self.highlight = function(i) {

				if (typeof i == "undefined") {
					d3.select("#parallel .foreground").style("opacity", function(d, j) {
						return "1";
					});
					d3.select("#parallel .series").style("opacity", function(d, j) {
						return "1";
					});
					highlighted.remove();
					highlighted2.remove();
                    d3.selectAll("text").style("font-weight", "normal");
					text.remove();
				} else {
					d3.select("#parallel .foreground").style("opacity", function(d, j) {
						return "0.35";
					});
					d3.select("#parallel .series").style("opacity", function(d, j) {
						return "0.45";
					});
					if (highlighted != null) {
						highlighted.remove();
					}
					if (highlighted2 != null) {
						highlighted2.remove();
					}
					if (text != null) {
						text.remove();
					}
                    d3.selectAll("text").style("font-weight", "normal");

					highlighted = svg.append("svg:g").attr("class", "highlight").selectAll("path").data([ model.get('filtered')[i] ]).enter().append("svg:path").attr("d", path).attr("style", function(d) {
                        var significanceArray = arrayFromMaskArray(d.significantMask, axes.length - 1);
                        axes.forEach(function(axis){
                            if(axis != "gene" && isSignificant(significanceArray,axes.indexOf(axis)-1)){ // first column is actually the gene, therefore also substract 1 otherwise bitmask will be offset
                                d3.select("#id" + axis.replace(/ /g, "_")).style("font-weight", "bold");
                            }
						});
						return "stroke:" + colors[d.group] + ";"; + getStyles(d,"foreground");
					});

					highlighted2 = svg.append("svg:g").attr("class", "highlight2").selectAll(".serie").data(dimensions).enter().append("svg:circle").filter(function(d) {
						return !hasData(d,i);
					}).attr("cx", function(d) {
						return x(d);
					}).attr("cy", function(d) {
						//return y[d](model.get('filtered')[i][d]) ;
						return y[d](defaults[d]);
					}).attr("r", 3).attr("style", function(d) {
						return "stroke:" + colors[model.get('filtered')[i].group] + ";";
					});

					text = svg.append("svg:g").attr("class", "label").selectAll("text").data(dimensions).enter().append("text").filter(function(d) {
						return !hasData(d,i);
					});


					text.attr("x", function(d) {
						return x(d) + 5;
					}).attr("y", function(d) {
						return y[d](defaults[d]) - 5;
					}).text(function(d){
						return "No data";
					});

					function hasData(d,i){
                        return !(model.get('filtered')[i][d] == null || model.get('filtered')[i][d] == "N/A");
					}
					
					function isSignificant(array,i){
                        return array[i];
					}
				}
			};
		};

		return self;
	};

})(d3);
