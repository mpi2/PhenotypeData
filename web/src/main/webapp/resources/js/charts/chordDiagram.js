/**
 * Created by ilinca on 05/12/2016.
 */
/** Initialize var mpTopLevelTerms if you want it filtered **/

var drawChords = function (openNewPage, mpTopLevelTerms) {

console.log(mpTopLevelTerms);
    var jsonSource = (mpTopLevelTerms && mpTopLevelTerms.length > 0) ? baseUrl + "/chordDiagram.json?phenotype_name=" + mpTopLevelTerms.join("&phenotype_name=") : baseUrl+ "/chordDiagram.json";
    var url = (window.location.href.indexOf("chordDiagram?") >= 0) ? baseUrl + "/chordDiagram?" : window.location.href.replace("chordDiagram", "chordDiagram?");

    // Attach download action
    if (mpTopLevelTerms && mpTopLevelTerms.length > 0) {
        $('#chordContainer').html("<p>Genes with at least on phenotype association in each of the following systems: <b>" + mpTopLevelTerms.join(",") + "</b></p><p><a href='" + url.replace("chordDiagram", "chordDiagram.csv") + "' download='" + "genes with " + mpTopLevelTerms.join(" ") +
            "genes_by_top_level_phenotype_associations.csv" + "'>Get gene list</a> </p>");
    }

    queue().defer(d3.json, jsonSource)
        .await(ready);

    function ready(error, json) {

        if (error) throw error;

        else {
            var labels = json.labels;
            var matrix = json.matrix;
            d3.select("#chordDiagramSvg").selectAll("*").remove();
            var svg = d3.select("#chordDiagramSvg"),
                width = +svg.attr("width"),
                height = +svg.attr("height"),
                outerRadius = Math.min(width, height) * 0.5 - 200,
                innerRadius = outerRadius - 30;

            var chord = d3.chord()
                .padAngle(0.05)
                .sortSubgroups(d3.descending);

            var arc = d3.arc()
                .innerRadius(innerRadius)
                .outerRadius(outerRadius);

            var ribbon = d3.ribbon()
                .radius(innerRadius);

            var color = d3.scaleOrdinal()
                .domain(d3.range(4))
                .range(["rgb(239, 123, 11)", "rgb(9, 120, 161)", "rgb(119, 119, 119)", "rgb(238, 238, 180)", "rgb(36, 139, 75)", "rgb(191, 75, 50)", "rgb(255, 201, 67)", "rgb(191, 151, 50)", "rgb(239, 123, 11)", "rgb(247, 157, 70)", "rgb(247, 181, 117)", "rgb(191, 75, 50)", "rgb(151, 51, 51)", "rgb(144, 195, 212)"]);

            var g = svg.append("g")
                .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")")
                .datum(chord(matrix));

            var group = g.append("g")
                .attr("class", "groups")
                .selectAll("g")
                .data(function (chords) {
                    return chords.groups;
                })
                .enter().append("g")
                .attr("class", "group")
                .on("mouseover", fade(.02))
                .on("mouseout", fade(.80))
                .on("click", function (d) {
                    if (mpTopLevelTerms.indexOf(labels[d.index].name) < 0) { // top level is not already selected
                        if (openNewPage) {
                            window.open(url + "&phenotype_name=" + labels[d.index].name, "_self");
                        }
                        else {
                            mpTopLevelTerms.push(labels[d.index].name);
                            drawChords(openNewPage, mpTopLevelTerms);
                        }
                    }
                });

            group.append("path")
                .style("fill", function (d) {
                    return color(d.index);
                })
                .style("stroke", function (d) {
                    return d3.rgb(color(d.index)).darker();
                })
                .attr("d", arc);

            var groupTick = group.selectAll(".group-tick")
                .data(function (d) {
                    return groupTicks(d, 1e3);
                })
                .enter().append("g")
                .attr("class", "group-tick")
                .attr("transform", function (d) {
                    return "rotate(" + (d.angle * 180 / Math.PI - 90) + ") translate(" + outerRadius + ",0)";
                });

            groupTick.append("line")
                .attr("x2", 6);

            groupTick
                .filter(function (d) {
                    return d.value % 5e3 === 0;
                })
                .append("text")
                .attr("x", 8)
                .attr("dy", ".35em")
                .attr("transform", function (d) {
                    return d.angle > Math.PI ? "rotate(180) translate(-16)" : null;
                })
                .style("text-anchor", function (d) {
                    return d.angle > Math.PI ? "end" : null;
                })
                .text(function (d) {
                    return labels[d.index].name.replace("phenotype", "");
                });

            g.append("g")
                .attr("class", "ribbons")
                .selectAll("path")
                .data(function (chords) {
                    return chords;
                })
                .enter().append("path")
                .attr("d", ribbon)
                .attr("class", "chord")
                .style("fill", function (d) {
                    return color(d.target.index);
                })
                .style("stroke", function (d) {
                    return d3.rgb(color(d.target.index)).darker();
                })
                .style("visibility", function (d) {
                    if (mpTopLevelTerms && mpTopLevelTerms.length > 0) {
                        if (mpTopLevelTerms.indexOf(labels[d.source.index].name) < 0 && mpTopLevelTerms.indexOf(labels[d.target.index].name) < 0) {
                            return "hidden";
                        } else {
                            return "visible";
                        }
                    } else {
                        return "visible";
                    }
                })
                .append("title").text(function (d) {
                return d.source.value + " genes present " + labels[d.source.index].name + " and " + labels[d.target.index].name + ", " + mpTopLevelTerms.join(", ");
            });

            // Returns an array of tick angles and values for a given group and step.
            function groupTicks(d, step) {
                var k = (d.endAngle - d.startAngle) / d.value;
                return d3.range(0, d.value, step).map(function (value) {
                    return {value: value, angle: value * k + d.startAngle, index: d.index};
                });
            }

            // Returns an event handler for fading a given chord group.
            // Display parameter for the group hovering over
            function fade(opacity) {
                return function (d, i) {
                    // hide other chords on mose over
                    svg.selectAll("path.chord")
                        .filter(function (d) {
                            return d.source.index != i && d.target.index != i;
                        })
                        .transition()
                        .style("stroke-opacity", opacity)
                        .style("fill-opacity", opacity);

                };
            }
        }
    }
} ;
