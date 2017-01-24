<%--
  Created by IntelliJ IDEA.
  User: ilinca
  Date: 03/01/2017
  Time: 14:19
  To change this template use File | Settings | File Templates.
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script type="text/javascript" src="${baseUrl}/js/vendor/venn.js?v=${version}"></script>
<script src="${baseUrl}/orthology.jsonp?mpId=MP:0005385"></script>



<div id="${param.divId}">

    <script>
        var chart = venn.VennDiagram()
            .width(500)
            .height(500);
        var div = d3.select("#${param.divId}").datum(${param.currentSet}).call(chart);

        var tooltip = d3.select("body").append("div")
            .attr("class", "venntooltip");

        div.selectAll("path")
            .style("stroke-opacity", 0)
            .style("stroke", "#fff")
            .style("stroke-width", 3)

        div.selectAll("g")
            .on("mouseover", function(d, i) {
                // sort all the areas relative to the current item
                venn.sortAreas(div, d);

                // Display a tooltip with the current size
                tooltip.transition().duration(400).style("opacity", .9);
                tooltip.text(d.size + " genes");

                // highlight the current path
                var selection = d3.select(this).transition("tooltip").duration(400);
                selection.select("path")
                    .style("fill-opacity", d.${param.currentSet}.length == 1 ? .4 : .1)
                    .style("stroke-opacity", 1);
            })

            .on("mousemove", function() {
                tooltip.style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY - 28) + "px");
            })

            .on("mouseout", function(d, i) {
                tooltip.transition().duration(400).style("opacity", 0);
                var selection = d3.select(this).transition("tooltip").duration(400);
                selection.select("path")
                    .style("fill-opacity", d.${param.currentSet}.length == 1 ? .25 : .0)
                    .style("stroke-opacity", 0);
            });

    </script>
</div>



