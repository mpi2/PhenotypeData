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
        
        var mp_id = '${mpId}';
        
        var width = 260,
        height = 200;

	    var cluster = d3.layout.cluster()
	        .size([height, width - 160]);
	
	    var diagonal = d3.svg.diagonal()
	        .projection(function(d) { return [d.y, d.x]; });
	

	    var orientation_left = { // right to left
	        size: [height, width],
	        x: function(d) { return width/2 - d.y * 100 / width; },
	        y: function(d) { return d.x; }
	    };
	    
	    var orientation_right = { // left to right
	    	    size: [height, width],
	    	    x: function(d) { return d.y * 100 / width; },
	    	    y: function(d) { return d.x; }
	    	  };
	  
	   
	
	    d3.json("json/" + mp_id + "?type=children", function(error, root) {
	    	  
		    var svg = d3.select("#childDiv").append("svg")
		        .attr("width", width)
		        .attr("height", height)
		        .append("g")
		        .attr("transform", "translate(5,0)");
	    	
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
		          .attr("d", d3.svg.diagonal().projection(function(d) { return [orientation_right.x(d), orientation_right.y(d)]; }));
	
		     var node = svg.selectAll(".node")
		          .data(nodes)
		          .enter().append("g")
		          .attr("class", "node")
		          .attr("transform", function(d) { return "translate(" +  orientation_right.x(d) + "," + orientation_right.y(d) + ")"; })
		          
		     node.append("circle")
		          .attr("r", 4.5);
		
		     node.append("svg:foreignObject")
		          .attr("x", function(d) { return d.children ? -8 : 8; })
		          .attr("y", -20)
		          .attr("class", "treeLabel")
		          .style("text-anchor", function(d) { return d.children ? "end" : "start"; })
		          .text(function(d) { return d.name; })
	   		      .style("width", "150px"); // width of the node labels;
	    });
	     
	     
	    d3.json("json/" + mp_id + "?type=parents", function(error, root) {
	    	 
	    	 var svgP = d3.select("#parentDiv").append("svg")
		        .attr("width", width)
		        .attr("height", height)
		        .append("g")
		        .attr("transform", "translate(95,0)");
	    	 
		     var diagonalP = d3.svg.diagonal()
	        	.projection(function(d) { return [d.y, d.x]; });
	    	
		     if (error){ 
		    	  console.log(error);
		    	  throw error;
		     }
		
		     var nodes = cluster.nodes(root),
		          links = cluster.links(nodes);
		
		     var link = svgP.selectAll(".link")
		          .data(links)
		          .enter().append("path")
		          .attr("class", "link")
		          .attr("d", d3.svg.diagonal().projection(function(d) { return [orientation_left.x(d), orientation_left.y(d)]; }));
		
		     var node = svgP.selectAll(".node")
		          .data(nodes)
		          .enter().append("g")
		          .attr("class", "node")
		          .attr("transform", function(d) { return "translate(" +  orientation_left.x(d) + "," + orientation_left.y(d) + ")"; })
		
		     node.append("circle")
		          .attr("r", 4.5);

		     node.append("svg:foreignObject")
	          .attr("x", function(d) { return d.children ? 8 : -150; })
	          .attr("y", -20)
	          .attr("class", "treeLabel")
	          .style("text-anchor", function(d) { return d.children ? "start" : "end"; })
	          .text(function(d) { return d.name; })
  		      .style("width", "150px"); // width of the node labels;
	    });
	
	    d3.select(self.frameElement).style("height", height + "px");
	    
    	</script>

    </jsp:attribute>
    
    <jsp:body>
       

    <div id="body">
		<div class="quarter" id="parentDiv"></div>
		<div class="quarter" id="childDiv"></div>
    </div>
    
        
    </jsp:body>
    
    
    
    </t:genericpage>
