<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>




<script type="text/javascript" src="${baseUrl}/js/charts/parallel/parallel-coordinates.js"></script>

<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.js"></script>
<%-- Consider changing this to underscore-1.8.3.min.js (newer, minimized) --%>
<script type="text/javascript" src="${baseUrl}/js/vendor/underscore.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/backbone.js"></script>

<script src="${baseUrl}/js/vendor/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/charts/parallel/filter.js"></script>

<!-- SlickGrid -->
<script src="${baseUrl}/js/charts/parallel/grid.js"></script>
<!-- script src="${baseUrl}/js/charts/parallel/pie.js"></script-->
<script src="${baseUrl}/js/charts/parallel/options.js"></script>

<div class="container" id="row-fluid">

	<div class="row">
		<div id="pager" class="info"></div>
	</div>

	<div class="row">
		<div id="parallel"></div>
	</div>

	<div class="row">
		<div id="myGrid" style="margin-top: 20px"></div>
	</div>

	<c:if test="${dataJs != null }">
		<script type="text/javascript">
			${dataJs}
		</script>
	</c:if>

</div>


<script type="text/javascript">
	
	var selectedProcedures =  [];
	<c:if test="${selectedProcedures != null}">
    	selectedProcedures= ${selectedProcedures};
	</c:if>
	var significanceMap; // <gene, <booleanTruthValues>>

	$(function() {
				
		$('#parallel-title').html("Gene KO effect comparator ");
		var selectedProceduresIterator = 1; 
		Object.keys(selectedProcedures).forEach(function(key){
			$('#parallel-title').append("<a class=\"bluelink\" href=\"" + selectedProcedures[key] + "\">" + key + "</a>");
			$('#parallel-title').append( (selectedProceduresIterator++ < Object.keys(selectedProcedures).length) ? ", " : "");
		});
		
		/** Add widgets **/
		$('#widgets_pc').html("");
//		$('#widgets_pc').append('<a href="#" id="shadows" class="button right filter_control btn">Shadows</a>');

		// $('#widgets_pc').append('<a href="#" id="export_selected" class="button right filter_control btn" title = "Export raw data in the table">Export</a>');
		// $('#widgets_pc').append('<a href="#" id="remove_filters" class="button right filter_control btn" title = "Remove filters">Clear filters</a>');
        // $('#widgets_pc').append('<textarea onfocus="if(this.value==this.defaultValue)this.value=\'\'" onblur="if(this.value==\'\')this.value=this.defaultValue" id="geneIdsSoft" rows="2" cols="100" style="width:40%">Filter table by gene symbols comma separated.	</textarea>');
        // $('#widgets_pc').append('<a href="#" id="geneSoftFilterButton" class="button btn" title="Filter by gene">Gene filter</a>');
//		$('#widgets_pc').append('<br/>');
//		$('#widgets_pc').append('<div class="right"><input type="range" min="0" max="1" value="0.2" step="0.01"	name="power" list="powers" id="line_opacity"></input>Opacity: <span id="opacity_level">20%</span></div>');
		
		var dimensions = new Filter({
			defaultValues : defaults
		});
		var highlighter = new Selector();
		$('body').addClass("shadows");
		dimensions.set({
			data : foods
		});
		dimensions.set({
			'defaultValues' : defaults
		});

		var columns = _(foods[0]).keys().filter(function(d,i){ return (d != "group" && d!= "significantMask");}); // don't show group column in table
		var axes = _(columns).without('name', 'accession', 'group', 'significantMask');
		var foodgroups = [ "Mutant", "Mean"];
		var colors = {
		    "Mutant" : '#0978A1',
			"No effect" :'#000000',
			"Mean" : '#00b3b3'/*,
			"MRC Harwell" : 'rgb(119, 119, 119)',
			"TCP" : '#16532D',
			"WTSI" : '#602619',
			"JAX" : 'rgb(36, 139, 75)',
			"BCM" : 'rgb(191, 75, 50)',
			"UC Davis" : 'rgb(255, 201, 67)',
			"ICS" : 'rgb(191, 151, 50)',
			"NING" : 'rgb(247, 157, 70)',
			"RBRC" : '#0978A1'*/
		}

//		_(foodgroups).each(function(group) {
//			$('#legend').append("<div class='item'><div class='color' style='background: " + colors[group] + "';></div><div class='key'>" + group + "</div></div>");
//		});

		var pc = parallel(dimensions, colors, defaults, highlighter, axes);
//		var pie = piegroups(foods, foodgroups, colors, 'group');
//		var totals = pietotals([ 'in', 'out' ], [ _(foods).size(), 0 ]);

		var slicky = new grid({
			model : dimensions,
			selector : highlighter,
			width : '100%',
			columns : columns
		});

		// vertical full screen
		var parallel_height = $(window).height() - 64 - 12 - 120 - 320;
		if (parallel_height < 380)
			parallel_height = 380; // min height
		$('#parallel').css({
			height : parallel_height + 'px',
			width : '100%',
			marginBottom: '60px'
		});

		slicky.update();
		pc.render();


        $('#geneSoftFilterButton').click(function(){
            var geneList = $("#geneIdsSoft").val().split(",");
            dimensions.clearfilter();
            console.log(geneList);
            var filtered = dimensions.get('filtered').filter(function(d){ console.log(d.gene); var filterOut = false; geneList.forEach(function(gene){ if(d.gene.includes(gene)){ filterOut = true; }}); return filterOut;});
            dimensions.set({
                'filtered' : filtered
            });
            pc.update(dimensions.get('data'));
            dimensions.trigger('change:filtered');

        });

		dimensions.bind('change:filtered', function() {
			var data = dimensions.get('data');
			var defaultValues = defaults;
			var filtered = dimensions.get('filtered');
			var data_size = _(data).size();
			var filtered_size = _(filtered).size();
///			pie.update(filtered);
//			totals.update([ filtered_size, data_size - filtered_size ]);
			var opacity = _([ 2 / Math.pow(filtered_size, 0.37), 100 ]).min();
			$('#line_opacity').val(opacity).change();
		});

		dimensions.bind('change:removefilter', function() {
			var data = dimensions.get('data');
			var defaultValues = defaults;
			var filtered = dimensions.get('data');
			var data_size = _(data).size();
			var filtered_size = _(filtered).size();
			var opacity = _([ 2 / Math.pow(filtered_size, 0.37), 100 ]).min();
			$('#line_opacity').val(opacity).change();
		});
		
		highlighter.bind('change:selected', function() {
			var highlighted = this.get('selected');
			pc.highlight(highlighted);
		});

		$('#remove_filters').click(function() {
			dimensions.clearfilter();
			pc.update(dimensions.get('data'));
			pc.render();
			dimensions.trigger('change:removefilter');
			return false;
		});
		
		$('#remove_selected').click(function() {
			dimensions.outliers();
			pc.update(dimensions.get('data'));
			pc.render();
			dimensions.trigger('change:filtered');
			return false;
		});

		$('#keep_selected').click(function() {
			dimensions.inliers();
			pc.update(dimensions.get('data'));
			pc.render();
			dimensions.trigger('change:filtered');
			return false;
		});

		$('#shadows').click(function() {
			if ($('body').hasClass("shadows")){
				$('body').removeClass('shadows');
			} else {
				$('body').addClass('shadows');
			}
			return false;
		});		
		
		$('#export_selected').click(function() {
			var data = dimensions.get('filtered');
			var keys = _.keys(data[0]);
			var csv = _(keys).map(function(d) {
				return '"' + addslashes(d) + '"';
			}).join(",");
			_(data).each(function(row) {
				csv += "\n";
				csv += _(keys).map(function(k) {
					var val = row[k];
					if (_.isString(val)) {
						return '"' + addslashes(val) + '"';
					}
					if (_.isNumber(val)) {
						return val;
					}
					if (_.isNull(val)) {
						return "";
					}
				}).join(",");
			});
			
			var link = document.createElement('a');
			link.download = "parallel_coordinates_data.csv";
			link.href = "data:application/octet-stream," + encodeURIComponent(csv);
			document.body.appendChild(link);
			link.click();
			document.body.appendChild(link);
			return false;
		});

		$('#line_opacity').change(function() {
			var val = $(this).val();
			$('#parallel .foreground path').css('stroke-opacity', val.toString());
			$('#opacity_level').html((Math.round(val * 10000) / 100) + "%");
		});

		$('#parallel').resize(function() {
			// vertical full screen
			pc.render();
			var val = $('#line_opacity').val();
			$('#parallel .foreground path').css('stroke-opacity', val.toString());
		});

/*		$('#parallel').resizable({
			handles : 's',
			resize : function() {
				return false;
			}
		});

		$('#myGrid').resizable({
			handles : 's'
		});*/

		function addslashes(str) {
			return (str + '').replace(/\"/g, "\"\"") // escape double quotes
			.replace(/\0/g, "\\0"); // replace nulls with 0
		};

    });

</script>
	
    