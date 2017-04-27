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

		</style>
	</jsp:attribute>

	<jsp:attribute name="addToFooter">
		<div class="region region-pinned"></div>
	</jsp:attribute>




	<jsp:body>

		<script type="text/javascript" src='${baseUrl}/js/vendor/jstree/jstree.min.js'></script>

		<div id="ontotree">Mesh terms</div>
		<div id="tree"></div>
		<div id="tree2"></div>
		<div id="tree3">


		</div>

		<script  type="text/javascript" >


            var clickToOpen = false; // The tree expands too many nodes at load time. This is a hack to only allow it on mouseclick

            var ontologyTree = $('#tree2').jstree({
                core:{
			 data:


                        [{"text":"Animals(<span class='topmesh'>Eukaryota<\/span>)"},{"text":"Humans(<span class='topmesh'>Eukaryota<\/span>)"},{"text":"Mice(<span class='topmesh'>Eukaryota<\/span>)"},{"text":"Obesity(<span class='topmesh'>Physiological Phenomena<\/span>)","children":[{"text":"genetics"}]},{"text":"Thinness(<span class='topmesh'>Physiological Phenomena<\/span>)","children":[{"text":"genetics"}]},{"text":"Nuclear Proteins(<span class='topmesh'>Amino Acids, Peptides, and Proteins<\/span>)","children":[{"text":"genetics"}]},{"text":"Repressor Proteins(<span class='topmesh'>Amino Acids, Peptides, and Proteins<\/span>)","children":[{"text":"genetics"}]},{"text":"Body Mass Index(<span class='topmesh'>Environment and Public Health<\/span>)"},{"text":"Nutrition Surveys(<span class='topmesh'>Environment and Public Health<\/span>)"},{"text":"Epigenesis, Genetic(<span class='topmesh'>Genetic Phenomena<\/span>)"},{"text":"Polymorphism, Genetic(<span class='topmesh'>Genetic Phenomena<\/span>)"},{"text":"Adolescent(<span class='topmesh'>Persons<\/span>)"},{"text":"Child(<span class='topmesh'>Persons<\/span>)"},{"text":"Child, Preschool(<span class='topmesh'>Persons<\/span>)"},{"text":"Haploinsufficiency(<span class='topmesh'>Genetic Phenomena<\/span>)"}]

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

		</script>


	</jsp:body>
</t:genericpage>

