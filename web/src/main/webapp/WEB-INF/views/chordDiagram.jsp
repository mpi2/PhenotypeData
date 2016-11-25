<%--
  Created by IntelliJ IDEA.
  User: ilinca
  Date: 24/11/2016
  Time: 16:51
  To change this template use File | Settings | File Templates.
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Chord diagram</jsp:attribute>

    <jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>



    <jsp:attribute name="header">

    <script src="https://d3js.org/d3.v4.min.js"></script>
    <script type="text/javascript" src="d3/d3.layout.js"></script>

    </jsp:attribute>

    <jsp:body>

        <div class="region region-content">
        <div class="block">
            <div class="content">
                <div class="node node-gene">
                    <h1 class="title" id="top">IMPC Phenotype Diagram</h1>

                    <div class="section">
                        <div class="inner">

                            <svg width="960" height="960"></svg>

                        </div>
                        <!--end of node wrapper should be after all secions  -->
                    </div>
                </div>
            </div>

        </div>

        <script>

            var labels = ["adipose tissue","behavior/neurological","cardiovascular system","craniofacial","digestive/alimentary","embryo","endocrine/exocrine gland","growth/size/body region","hearing/vestibular/ear","hematopoietic system","homeostasis/metabolism","immune system","integument","limbs/digits/tail","liver/biliary system","mortality/aging","muscle","nervous system","normal","pigmentation","renal/urinary system","reproductive system","respiratory system","skeleton","vision/eye"];
            var matrix = [
                [4,107,44,18,2,8,9,171,20,105,145,54,29,42,3,55,4,19,1,8,15,15,6,140,52],
                [107,113,134,32,9,26,36,263,79,269,395,162,113,107,12,213,6,70,2,46,29,56,14,256,173],
                [44,134,35,26,9,40,50,126,35,117,160,73,44,55,15,133,10,50,3,27,22,61,11,102,88],
                [18,32,26,0,7,19,6,74,8,41,49,20,16,24,1,39,1,12,0,7,5,7,0,67,28],
                [2,9,9,7,0,8,9,14,2,16,10,14,6,7,4,12,0,7,3,1,3,2,9,9,11],
                [8,26,40,19,8,0,11,119,12,25,57,17,24,27,7,125,0,47,0,4,4,9,3,18,34],
                [9,36,50,6,9,11,3,30,11,39,48,36,22,17,7,44,1,19,3,4,20,96,10,28,18],
                [171,263,126,74,14,119,30,41,47,223,327,125,80,95,14,206,4,75,2,27,25,43,14,301,126],
                [20,79,35,8,2,12,11,47,24,35,62,26,15,25,1,47,3,26,0,10,9,12,1,43,35],
                [105,269,117,41,16,25,39,223,35,54,392,540,80,79,13,193,5,44,3,41,26,45,20,251,125],
                [145,395,160,49,10,57,48,327,62,392,183,215,107,106,13,289,4,68,2,48,38,76,19,312,158],
                [54,162,73,20,14,17,36,125,26,540,215,1,55,51,12,129,5,27,3,22,23,37,15,163,73],
                [29,113,44,16,6,24,22,80,15,80,107,55,9,32,5,77,1,18,0,66,10,20,8,64,58],
                [42,107,55,24,7,27,17,95,25,79,106,51,32,5,7,82,4,26,1,14,11,22,7,195,54],
                [3,12,15,1,4,7,7,14,1,13,13,12,5,7,0,9,0,7,3,0,2,4,6,9,7],
                [55,213,133,39,12,125,44,206,47,193,289,129,77,82,9,236,3,69,0,35,22,44,7,179,143],
                [4,6,10,1,0,0,1,4,3,5,4,5,1,4,0,3,0,3,0,0,2,1,0,4,3],
                [19,70,50,12,7,47,19,75,26,44,68,27,18,26,7,69,3,5,3,10,12,22,7,45,40],
                [1,2,3,0,3,0,3,2,0,3,2,3,0,1,3,0,0,3,0,0,0,0,3,3,0],
                [8,46,27,7,1,4,4,27,10,41,48,22,66,14,0,35,0,10,0,0,2,8,1,36,86],
                [15,29,22,5,3,4,20,25,9,26,38,23,10,11,2,22,2,12,0,2,5,21,5,25,14],
                [15,56,61,7,2,9,96,43,12,45,76,37,20,22,4,44,1,22,0,8,21,21,3,45,20],
                [6,14,11,0,9,3,10,14,1,20,19,15,8,7,6,7,0,7,3,1,5,3,1,13,7],
                [140,256,102,67,9,18,28,301,43,251,312,163,64,195,9,179,4,45,3,36,25,45,13,54,121],
                [52,173,88,28,11,34,18,126,35,125,158,73,58,54,7,143,3,40,0,86,14,20,7,121,24]
            ];

            var svg = d3.select("svg"),
                    width = +svg.attr("width"),
                    height = +svg.attr("height"),
                    outerRadius = Math.min(width, height) * 0.5 - 80,
                    innerRadius = outerRadius - 30;

            var formatValue = d3.formatPrefix(",.0", 1e3);

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
                    .range(["rgb(239, 123, 11)", "rgb(9, 120, 161)", "rgb(119, 119, 119)", "rgb(238, 238, 180)", "rgb(36, 139, 75)", "rgb(191, 75, 50)", "rgb(255, 201, 67)", "rgb(191, 151, 50)", "rgb(239, 123, 11)", "rgb(247, 157, 70)", "rgb(247, 181, 117)", "rgb(191, 75, 50)", "rgb(151, 51, 51)", "rgb(144, 195, 212)" ]);

            var g = svg.append("g")
                    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")")
                    .datum(chord(matrix));

            var group = g.append("g")
                    .attr("class", "groups")
                    .selectAll("g")
                    .data(function(chords) { return chords.groups; })
                    .enter().append("g")
                    .attr("class", "group")
                    .on("mouseover", fade(.02))
                    .on("mouseout", fade(.80));

            group.append("path")
                    .style("fill", function(d) { return color(d.index); })
                    .style("stroke", function(d) { return d3.rgb(color(d.index)).darker(); })
                    .attr("d", arc);

            var groupTick = group.selectAll(".group-tick")
                    .data(function(d) { return groupTicks(d, 1e3); })
                    .enter().append("g")
                    .attr("class", "group-tick")
                    .attr("transform", function(d) { return "rotate(" + (d.angle * 180 / Math.PI - 90) + ") translate(" + outerRadius + ",0)"; });

            groupTick.append("line")
                    .attr("x2", 6);

            groupTick
                    .filter(function(d) { return d.value % 5e3 === 0; })
                    .append("text")
                    .attr("x", 8)
                    .attr("dy", ".35em")
                    .attr("transform", function(d) { return d.angle > Math.PI ? "rotate(180) translate(-16)" : null; })
                    .style("text-anchor", function(d) { return d.angle > Math.PI ? "end" : null; })
                    .text(function(d) { console.log(d); return labels[d.index]; });

            g.append("g")
                    .attr("class", "ribbons")
                    .selectAll("path")
                    .data(function(chords) { return chords; })
                    .enter().append("path")
                    .attr("d", ribbon)
                    .attr("class", "chord")
                    .style("fill", function(d) { return color(d.target.index); })
                    .style("stroke", function(d) { return d3.rgb(color(d.target.index)).darker(); });

            // Returns an array of tick angles and values for a given group and step.
            function groupTicks(d, step) {
                console.log("groupTicks");
                console.log(d);
                console.log(step);
                var k = (d.endAngle - d.startAngle) / d.value;
                return d3.range(0, d.value, step).map(function(value) {
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

        </script>
    </jsp:body>

</t:genericpage>


