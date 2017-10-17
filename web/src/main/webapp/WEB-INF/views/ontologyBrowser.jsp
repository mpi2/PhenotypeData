<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/${dataType}?kw=*">${dataTypeLabel}</a>Ontology browser</jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">       
        <link type="text/css" rel="stylesheet" href="${baseUrl}/css/ontologyBrowser.css"/>
        <link type="text/css" rel="stylesheet" href="${baseUrl}/css/vendor/jstree.min.css"/>

		<style>
			/*#ontotree { margin-top: 80px;}*/
			span#gpassoc {
				float: right;
				color: #0978A1;
				font-size: 16px;
			}
			span.gpAssoc {
				color: #0978A1;
			}
		</style>
	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
		<div class="region region-pinned"></div>
	</jsp:attribute>

  	<jsp:body>

 	 <script type="text/javascript" src='${baseUrl}/js/vendor/jstree/jstree.min.js'></script>

	 <div id="ontotree">Browse Mammalian Phenotype Ontology</div>
  	 <div id="tree"></div>

	 <script  type="text/javascript" >

		 var termId = "${termId}";

		 var ontologyLabel = "";
		 if ( termId.indexOf("MP:") != -1){
			 ontologyLabel = "Mammalian Phenotype Ontology (MP) <span id='gpassoc'>Number by a term : significant genotype-phenotype associations</span>";

		 }
		 if ( termId.indexOf("MA:") != -1){
			 ontologyLabel = "Adult: Mouse Adult Gross Anatomy Ontology (MA)";
		 }
		 if ( termId.indexOf("EMAPA:") != -1){
			 ontologyLabel = "Embryo: Mouse gross anatomy and development, timed (EMAPA)";
		 }
		 $('#ontotree').html(ontologyLabel);

		 var clickToOpen = false; // The tree expands too many nodes at load time. This is a hack to only allow it on mouseclick
		 
		 var ontologyTree = $('#tree').jstree({
			  "core" : {
				    "animation" : 0,
				    'data' : {	
				    	'url' : function (node) {
				    	      return node.id === '#' ?
				    	    		"ontologyBrowser2?termId=" + termId + "&node=src" :
				    	    		"ontologyBrowser2?termId=" + termId + "&node=" + node.id;
				    	    },
				      	'data' : function (node) {
				        	return { 'id' : node.id };
				      	}
			  		}
			  },
			  "types" : {
				     "default" : {
				       "icon" : "img/jstree/jstree-node.png"
				     },
				     "selected" : {
				       "icon" : "img/jstree/jstree-node-selected.png"
				     }
			},
			  "plugins" : [
			  	"types"
			  ]
		});



		 $('#tree')
			 // listen for event
			 .on('changed.jstree', function (e, data) {
				 var i, j, r = [];
				 for(i = 0, j = data.selected.length; i < j; i++) {
					 r.push(data.instance.get_node(data.selected[i]).text);
				 }
				 //var nodeHref = data.node.original.href;
                 //var nodeHref = e.target.href;
				 //window.location.href = "${baseUrl}" + nodeHref.replace("/data","");
			 }).jstree();

		
		$("#tree").bind('ready.jstree', function(e, data) {
			var pos = $('#${scrollToNode}').position(); // id of first JSON object with opened: true
			$('body').scrollTop(pos.top);
	     }).delegate("a","click", function(e) {
            if (window.location.host.indexOf("localhost") != -1) {
                window.location.href = e.target.href.replace("/data",baseUrl);
            }
            else {
                window.location.href = e.target.href;
            }
        });
		
		
	 </script>


  	</jsp:body>
</t:genericpage>

