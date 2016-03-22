<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">Some page</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
    
    <jsp:attribute name="header">
                    
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.v3.js"></script>		
		<script type="text/javascript" src="${baseUrl}/js/vendor/d3/d3.layout.js"></script>	
    	
        <link rel="stylesheet" href="${baseUrl}/css/treeStyle.css">
    	                
    </jsp:attribute>
    
    <jsp:attribute name="addToFooter">
        <script type="text/javascript">

        var margin = {top: 140, right: 10, bottom: 140, left: 10},
        width = 240 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

	    var orientations = {
//	      "top-to-bottom": {
/* 	        size: [width, height],
	        x: function(d) { return d.x; },
	        y: function(d) { return d.y; }
	      },
 */	      "right-to-left": {
	        size: [height, width],
	        x: function(d) { return width - d.y; },
	        y: function(d) { return d.x; }
/* 	      },
	      "bottom-to-top": {
	        size: [width, height],
	        x: function(d) { return d.x; },
	        y: function(d) { return height - d.y; }
	        	      },
	      "left-to-right": {
	        size: [height, width],
	        x: function(d) { return d.y; },
	        y: function(d) { return d.x; }
*/	      }
	    };

	    var svg = d3.select("body").selectAll("svg")
	        .data(d3.entries(orientations))
	        .enter().append("svg")
	        .attr("width", width + margin.left + margin.right)
	        .attr("height", height + margin.top + margin.bottom)
	        .append("g")
	        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	
	    svg.append("rect")
	        .attr("width", width)
	        .attr("height", height)
	        .attr("class", "border");
	
	    svg.append("text")
	        .attr("x", 6)
	        .attr("y", 6)
	        .attr("dy", ".71em")
	        .text(function(d) { return d.key; });
	
	   	d3.json("../mpTree/json", function(error, root) {
	    	if (error) throw error;
	        svg.each(function(orientation) {
		        var svg = d3.select(this),
		            o = orientation.value;
		
		        // Compute the layout.
		        var tree = d3.layout.tree().size(o.size),
		            nodes = tree.nodes(root),
		            links = tree.links(nodes);
		
		        // Create the link lines.
		        svg.selectAll(".link")
		            .data(links)
		            .enter().append("path")
		            .attr("class", "link")
		            .attr("d", d3.svg.diagonal().projection(function(d) { return [o.x(d), o.y(d)]; }));
		
		        // Create the node circles.
		        svg.selectAll(".node")
		            .data(nodes)
		            .enter().append("circle")
		            .attr("class", "node")
		            .attr("r", 4.5)
		            .attr("cx", o.x)
		            .attr("cy", o.y);
		      });
	    });
        
 		
    	</script>

    </jsp:attribute>
    
    <jsp:body>
       

    <div id="body">
    </div>
    
        
    </jsp:body>
    
    
    
    </t:genericpage>
