/**
 * Created by ilinca on 05/12/2016.
 */
/** Initialize var mpTopLevelTerms if you want it filtered **/


/* drawChords("chordDiagramSvgIonChannels", "chordContainerIonChannels", false, [], true, "Ion Channels", true); */

var drawChords = function (svgId, containerId, openNewPage, mpTopLevelTerms, idg, idgClass, clickableChords) {

    console.log("mpTopLevelTerms " + mpTopLevelTerms);
    console.log("containerId " + containerId);
    console.log("openNewPage " + openNewPage);
    console.log("svgId " + svgId);
    var jsonSource = (mpTopLevelTerms && mpTopLevelTerms.length > 0) ? baseUrl + "/chordDiagram.json?phenotype_name=" + mpTopLevelTerms.join("&phenotype_name=") : baseUrl+ "/chordDiagram.json?";
    if (idg != null){
        jsonSource += "&idg=" + idg;
    }
    if (idgClass != null){
        jsonSource += "&idgClass=" + idgClass;
    }
    console.log(jsonSource);

    var url = (mpTopLevelTerms && mpTopLevelTerms.length > 0) ? baseUrl + "/chordDiagram?phenotype_name=" + mpTopLevelTerms.join("&phenotype_name=") : baseUrl+ "/chordDiagram?";
    
    
    console.log(url);
    
    
    queue().defer(d3.json, jsonSource)
        .await(ready);

    console.log("Here")

    function ready(error, json) {

        console.log("in here with json="+json);
        if (error) throw error;

        else {
            var labels = json.labels;
            var labels_json = JSON.parse(labels);

            var matrix = json.matrix;

            // Attach download action
            if (mpTopLevelTerms && mpTopLevelTerms.length > 0) {
                $('#'+containerId).html("<p>" + json.geneCount + " genes with at least one phenotype association in each of the following systems: <b>" + mpTopLevelTerms.join(",") + "</b></p><p><a href='" + url.replace("chordDiagram", "chordDiagram.csv") + "' download='" + "genes with " + mpTopLevelTerms.join(" ") +
                    "genes_by_top_level_phenotype_associations.csv" + "'>Get gene list</a> </p>");
            } else {
                $('#'+containerId).html("<p><b>" + json.geneCount + "</b> genes have phenotypes in more than one biological system.  The chord diagram below shows the pleiotropy between these genes. <a href='" + jsonSource.replace("chordDiagram.json", "chordDiagram.csv") + "' download='" + "genes_phenotype_associations.csv" + "'>Get the genes and associated phenotypes.</a> </p>");
            }

            d3.select("#"+svgId).selectAll("*").remove(); //clear svg for in-place filters
            var svg = d3.select("#"+svgId),
                width = +960,
                height = +960,
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
                    if (clickableChords && mpTopLevelTerms.indexOf(labels_json[d.index].name) < 0) { // top level is not already selected
                        if (openNewPage) {
                            console.log("URL " + url);
                            window.open(url + "&phenotype_name=" + labels_json[d.index].name, "_self");
                        }
                        else {
                            mpTopLevelTerms.push(labels_json[d.index].name);
                            drawChords(svgId, containerId, openNewPage, mpTopLevelTerms, idg, idgClass, clickableChords);
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
                    labels_json = JSON.parse(labels)
                    return labels_json[d.index].name.replace("phenotype", "");
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
                        if (mpTopLevelTerms.indexOf(labels_json[d.source.index].name) < 0 && mpTopLevelTerms.indexOf(labels_json[d.target.index].name) < 0) {
                            return "hidden";
                        } else {
                            return "visible";
                        }
                    } else {
                        return "visible";
                    }
                })
                .append("title").text(function (d) {
                return d.source.value + " genes present " + labels_json[d.source.index].name + " and " + labels_json[d.target.index].name + ", " + ((mpTopLevelTerms && mpTopLevelTerms.length > 0) ? mpTopLevelTerms.join(", ") : "");
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
