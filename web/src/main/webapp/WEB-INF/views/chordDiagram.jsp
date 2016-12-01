<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: ilinca
  Date: 24/11/2016
  Time: 16:51
  To change this template use File | Settings | File Templates.
--%>
<t:genericpage>

    <jsp:attribute name="title">Chord diagram</jsp:attribute>

    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search">search</a> &raquo; Chord diagram </jsp:attribute>

    <jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>



    <jsp:attribute name="header">

    <script src="//d3js.org/d3.v4.min.js"></script>
    <script type="text/javascript" src="d3/d3.layout.js"></script>
    <script src="//d3js.org/queue.v1.min.js"></script>

    </jsp:attribute>

    <jsp:body>

        <div class="region region-content">
        <div class="block">
            <div class="content">
                <div class="node node-gene">
                    <h1 class="title" id="top">IMPC Phenotype Diagram</h1>

                    <div class="section">
                        <div class="inner" id="chordContainer">

                            <c:if test="${phenotypeName != null}">
                                <p> Phenotype associations for genes with ${phenotypeName}</p>
                            </c:if>
                            <svg width="960" height="960"></svg>

                        </div>
                        <!--end of node wrapper should be after all secions  -->
                    </div>
                </div>
            </div>

        </div>

        <script>

            var mpTopLevelTerms = ${(phenotypeName != null) ? phenotypeName : []};
            var jsonSource      = (mpTopLevelTerms && mpTopLevelTerms.length > 0) ? "chordDiagram.json?phenotype_name=" + mpTopLevelTerms.join("&phenotype_name=") : "chordDiagram.json";
            var url             = (window.location.href.indexOf("chordDiagram?") >= 0) ? window.location.href : window.location.href.replace("chordDiagram", "chordDiagram?") ;

            // Attach download action
            $('#chordContainer').append("<a href='" + url.replace("chordDiagram", "chordDiagram.csv") + "' download='" + ((mpTopLevelTerms && mpTopLevelTerms.length > 0) ? "genes with " + mpTopLevelTerms.join(" ") : "genes_by_top_level_phenotype_associations.csv")+ "'>Download</a>");

            queue().defer(d3.json, jsonSource)
                    .await(ready);

            function ready(error, json) {

                if (error) throw error;

                else {
                    var labels  = json.labels;
                    var matrix  = json.matrix;
                    var svg     = d3.select("svg"),
                            width = +svg.attr("width"),
                            height = +svg.attr("height"),
                            outerRadius = Math.min(width, height) * 0.5 - 100,
                            innerRadius = outerRadius - 30;

                    var formatValue = d3.formatPrefix(",.0", 1e3);

                    var chord   = d3.chord()
                            .padAngle(0.05)
                            .sortSubgroups(d3.descending);

                    var arc     = d3.arc()
                            .innerRadius(innerRadius)
                            .outerRadius(outerRadius);

                    var ribbon = d3.ribbon()
                            .radius(innerRadius);

                    var color   = d3.scaleOrdinal()
                            .domain(d3.range(4))
                            .range(["rgb(239, 123, 11)", "rgb(9, 120, 161)", "rgb(119, 119, 119)", "rgb(238, 238, 180)", "rgb(36, 139, 75)", "rgb(191, 75, 50)", "rgb(255, 201, 67)", "rgb(191, 151, 50)", "rgb(239, 123, 11)", "rgb(247, 157, 70)", "rgb(247, 181, 117)", "rgb(191, 75, 50)", "rgb(151, 51, 51)", "rgb(144, 195, 212)"]);

                    var g       = svg.append("g")
                            .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")")
                            .datum(chord(matrix));

                    var group   = g.append("g")
                            .attr("class", "groups")
                            .selectAll("g")
                            .data(function (chords) {
                                return chords.groups;
                            })
                            .enter().append("g")
                            .attr("class", "group")
                            .on("mouseover", fade(.02))
                            .on("mouseout", fade(.80))
                            .on("click", function(d){
                                window.open(url + "&phenotype_name=" + labels[d.index].name , "_self" );
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
                            .append("title").text(function (d) {
                        return d.source.value + " genes present " + labels[d.source.index].name + " phenotype and " + labels[d.target.index].name + " phenotype, " + mpTopLevelTerms.join(", ");
                    });

                    // Returns an array of tick angles and values for a given group and step.
                    function groupTicks(d, step) {
                        var k = (d.endAngle - d.startAngle) / d.value;
                        return d3.range(0, d.value, step).map(function (value) {
                            return {value: value, angle: value * k + d.startAngle, index: d.index};
                        });
                    }

                    // Returns an event handler for fading a given chord group.
                    function fade(opacity) {
                        return function (d, i) {
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

        </script>
    </jsp:body>

</t:genericpage>


