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
        	var ont_id = '${ontId}';
    	</script>
    	<script type="text/javascript" src="${baseUrl}/js/parentChildTree.js"></script>	

    </jsp:attribute>
    
    <jsp:body>
       

    <div id="body">
		<div class="quarter" id="parentDiv"></div>
		<div class="quarter" id="childDiv"></div>
    </div>
    
        
    </jsp:body>
    
    
    
    </t:genericpage>
