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

        var width = 460,
        height = 200;

	    var cluster = d3.layout.cluster()
	        .size([height, width - 160]);
	
	    var diagonal = d3.svg.diagonal()
	        .projection(function(d) { return [d.y, d.x]; });
	
	    var orientation_left = {
	        size: [height, width],
	        x: function(d) { return d.y; },
	        y: function(d) { return d.x; }
	    };
	    
	    var svg = d3.select("body").append("svg")
	        .attr("width", width)
	        .attr("height", height)
	        .append("g")
	        .attr("transform", "translate(40,0)");
	
	     d3.json("json", function(error, root) {
		     if (error){ 
		    	  console.log(error);
		    	  throw error;
		   	 }
		
		     var nodes = cluster.nodes(root),
		          links = cluster.links(nodes);
		
		     var link = svg.selectAll(".link")
		          .data(links)
		          .enter().append("path")
		          .attr("class", "link")
		          .attr("d", diagonal);
		
		     var node = svg.selectAll(".node")
		          .data(nodes)
		          .enter().append("g")
		          .attr("class", "node")
		          .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })
		
		     node.append("circle")
		          .attr("r", 4.5);
		
		     node.append("text")
		          .attr("dx", function(d) { return d.children ? -8 : 8; })
		          .attr("dy", 3)
		          .style("text-anchor", function(d) { return d.children ? "end" : "start"; })
		          .text(function(d) { return d.name; });
	    });
	
	    d3.select(self.frameElement).style("height", height + "px");
	    
    	</script>

    </jsp:attribute>
    
    <jsp:body>
       

    <div id="body">
      <div id="footer">
      </div>
    </div>
    
        
    </jsp:body>
    
    
    
    </t:genericpage>
