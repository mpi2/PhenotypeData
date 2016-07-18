<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>




<script type="text/javascript" src="${baseUrl}/js/charts/parallel/parallel-coordinates.js"></script>

<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/underscore.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/backbone.js"></script>

<script src="${baseUrl}/js/vendor/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/charts/parallel/filter.js"></script>

<!-- SlickGrid -->
<script src="${baseUrl}/js/charts/parallel/grid.js"></script>
<!-- script src="${baseUrl}/js/charts/parallel/pie.js"></script-->
<script src="${baseUrl}/js/charts/parallel/options.js"></script>

<h3>Procedures displayed: <c:forEach var="procedure" items="${selectedProcedures}" varStatus="loop">
		                    		<a href="/impress/protocol/${procedure.getStableKey()}">${procedure.getName()}</a><c:if test="${!loop.last}">,</c:if>
		                    	</c:forEach>
</h3>	
<!-- div><a href="#" id="inverted" class="right toggle">Dark</a></div-->
<!-- div><a href="#" id="no_ticks" class="right toggle">Hide Ticks</a></div-->
<div id="row-fluid">

	<div id="pager" class="info"></div> <div class="clear"></div>
	<div id="parallel"></div>
	<div id="myGrid"></div>

	<c:if test="${dataJs != null }">
		<script type="text/javascript">
			${dataJs}
		</script>
	</c:if>

</div>


<script type="text/javascript">
	$(function() {
				
		/** Add widgets **/
		$('#widgets_pc').html("");
		$('#widgets_pc').append('<a href="#" id="shadows" class="button right filter_control btn">Shadows</a>');

		$('#widgets_pc').append('<a href="#" id="export_selected" class="button right filter_control btn" title = "Export raw data in the table">Export</a>');
		$('#widgets_pc').append('<a href="#" id="remove_filters" class="button right filter_control btn" title = "Remove filters">Clear filters</a>');
		$('#widgets_pc').append('<br/>');
		$('#widgets_pc').append('<div class="right"><input type="range" min="0" max="1" value="0.2" step="0.01"	name="power" list="powers" id="line_opacity"></input>Opacity: <span id="opacity_level">20%</span></div>');
		
		var dimensions = new Filter({
			defaultValues : defaults
		});
		var highlighter = new Selector();

		dimensions.set({
			data : foods
		});
		dimensions.set({
			'defaultValues' : defaults
		});

		var columns = _(foods[0]).keys();
		var axes = _(columns).without('name', 'accession', 'group');

		var foodgroups = [ "Mutant", "Mean"];
		// "MRC Harwell", "TCP", "JAX", "WTSI", "BCM", "UC Davis", "ICS", "HMGU", "NING", "RBRC" ];

		var colors = {
		    "Mutant" : '#0978A1',
			"Normal" :'#602619',
			"Mean" : '#EF7B0B'/*,
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

		var pc = parallel(dimensions, colors, defaults);
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
		if (parallel_height < 340)
			parallel_height = 340; // min height
		$('#parallel').css({
			height : parallel_height + 'px',
			width : '100%'
		});

		slicky.update();
		pc.render();

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

		$('#parallel').resizable({
			handles : 's',
			resize : function() {
				return false;
			}
		});

		$('#myGrid').resizable({
			handles : 's'
		});

		function addslashes(str) {
			return (str + '').replace(/\"/g, "\"\"") // escape double quotes
			.replace(/\0/g, "\\0"); // replace nulls with 0
		};

	});
</script>
	
    