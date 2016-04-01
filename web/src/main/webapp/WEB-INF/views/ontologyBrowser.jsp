<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">IMPC Search</jsp:attribute>
  <jsp:attribute name="breadcrumb">&nbsp;&raquo;&nbsp;<a href="${baseUrl}/search/${dataType}?kw=*">${dataTypeLabel}</a> &raquo; ${searchQuery}</jsp:attribute>
  <jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

	<jsp:attribute name="header">
        <%--<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />--%>
        <%--<link type="text/css" rel="stylesheet" href="${baseUrl}/js/vendor/extjs-4.1.3/ jquery-ui-1.7.2.custom.css"/>--%>
        <link type="text/css" rel="stylesheet" href="${baseUrl}/js/vendor/extjs-4.1.3/ext-all-gray.css"/>
        <link type="text/css" rel="stylesheet" href="${baseUrl}/css/ontologyBrowser.css"/>

	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
		<div class="region region-pinned"></div>
	</jsp:attribute>

  <jsp:body>


    <script type="text/javascript" src='${baseUrl}/js/vendor/extjs-4.1.3/ext-all.js'></script>

    <script type="text/javascript">

      $(document).ready(function(){

        var ontologyLabel;
        if ( "${termId}".indexOf("MA:") != -1 ){
          ontologyLabel = 'Mouse Anatomical Entity (MA)';
        }
        else if ( "${termId}".indexOf("MP:") != -1 ){
          ontologyLabel = 'Mammalian Phenotype Ontology (MP)';
        }

        initTree();

        function initTree() {
        Ext.require([
          'Ext.tree.*',
          'Ext.data.*',
          'Ext.tip.*'
        ]);

        //Ext.onReady(function() {
        Ext.QuickTips.init(); // turn qtip on and off

        // define own fields for node.data
        Ext.define('myModel', {
          extend: 'Ext.data.Model',
          proxy: {
            type: 'memory'
          },
          fields: [
            { name: 'expandNodeIds', type: 'array'},
            { name: 'text', type: 'string'}
          ]
        });


        var store = Ext.create('Ext.data.TreeStore', {
            proxy: {
              type: 'ajax',
              url: "${baseUrl}/ontologyBrowser2",
              extraParams: {
                termId: "${termId}"
              },
              actionMethods: {
              create: 'POST',
              destroy: 'DELETE',
              read: 'POST',
              update: 'POST'
            }
          },
          model: 'myModel',
          listeners: {
              //beforeload: function(s, r){}
          },
          root: {
            text: ontologyLabel,
            id: 'src',
            expanded: true
            //nodeType: 'async'
            }

  //          folderSort: true,
  //          sorters: [{
  //              property: 'text',
  //              direction: 'ASC'
  //          }]
          }
        );

        var tree = Ext.create('Ext.tree.Panel', {
            store: store,
            //plugins: new NodeMouseoverPlugin(),
//            viewConfig: {
//              selectedItemCls : "qryTerm"
//              plugins: {
//                ptype: 'treeviewdragdrop'
//              }
//            },
            renderTo: 'tree',
            //columnLines: true,
            //rowLines: true,
            minHeight: 100,
            //width: "80%",
            title: 'IMPC Phenotype Ontology Browser',
            useArrows: false,
            listeners: {
              itemclick: function (s, r) {
                var node = r.data;
                  console.log(node.href + " " + node.hrefTarget);
                // links to term page on new tab
                // just so that click on all area of the div will jump to new page
                window.open(node.href, node.hrefTarget);
              },
              load: function (s, r) {

                //var node = r.data;
                var expandList = s.getRootNode().firstChild.data.expandNodeIds;
                //var expandList = store.getNodeById(1).data.expandNodeIds;

                for (var i = 0; i < expandList.length; i++) {
                  var enode = s.getNodeById(expandList[i]);

                  //console.log(enode.getPath());
                  tree.expandPath(enode.getPath());
                }

                // no bubble up, since its parent
                // will do the same thing (see itemclick above)
                $('div.x-grid-cell-inner a').click(function(e) {
                  e.stopPropagation();
                });
                $('table.x-grid-table tr:nth-child(2)').click(function(){
                    return false; // this one is the ontology root
                })


              }
            }
        });
      }
    });
  </script>

  <h1 id="h1tree">IMPC Ontology browser</h1h1tree>
  <div id="tree"></div>

  </jsp:body>
</t:genericpage>

