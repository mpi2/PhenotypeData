(function(undefined) {

  window.grid = Backbone.View.extend({
	  
    initialize: function(options) {
      var self = this;
      for (var k in options) {
        this[k] = options[k];
      }
      this.model.bind('change:filtered', function() { self.update()});
      this.model.bind('change:removefilter', function() { self.clearfilters(); });
      
      this.cols = _(this.columns).map(function(col) {
    	  if (col == 'name'){
	        return {
	          id: col,
	          name: function() { if (self.alias) { return self.alias[col]; } else { return col; } }(), 
	          field: function() { return col;}(),
	          formatter: linkFormatter = function ( row, cell, value, columnDef, dataContext ) {return '<a href="' + baseUrl + '/genes/' + value.split("(")[1].replace(")", "") + '"  target="_blank">'+ value.split("(")[0] + '</a>';},
	          width: function() { if (col == "name") { return 180; } else if (col == "group") { return 60; } else { return 80; }}()
	        }
    	  } else {
    		  return {
    	          id: col,
    	          name: function() { if (self.alias) { return self.alias[col]; } else { return col; } }(), 
    	          field: function() { return col;}(),
    	          width: function() { if (col == "name") { return 180; } else if (col == "group") { return 60; } else { return 80; }}()
    		  }
    	  }
      });
      
      this.options = {
        enableCellNavigation: true,
        enableColumnReorder: true
      };

      this.dataView = new Slick.Data.DataView();
      this.selectedRowIds = [];
      this.grid = new Slick.Grid("#myGrid", this.dataView, this.cols, this.options);
      this.counter = 0;

      var pager = new Slick.Controls.Pager(this.dataView, this.grid, $("#pager"));

      this.dataView.onRowCountChanged.subscribe(function(e,args) {
        self.grid.updateRowCount();
        self.grid.render();
      });

      this.dataView.onRowsChanged.subscribe(function(e,args) {
        self.grid.invalidateRows(args.rows);
        self.grid.render();

        if (self.selectedRowIds.length > 0) {
          // since how the original data maps onto rows has changed,
          // the selected rows in the grid need to be updated
          var selRows = [];
          for (var i = 0; i < self.selectedRowIds.length; i++)
          {
            var idx = self.dataView.getRowById(self.selectedRowIds[i]);
            if (idx != undefined)
              selRows.push(idx);
          }

          self.grid.setSelectedRows(selRows);
        }
      });

      this.dataView.onPagingInfoChanged.subscribe(function(e,pagingInfo) {
        var isLastPage = pagingInfo.pageSize*(pagingInfo.pageNum+1)-1 >= pagingInfo.totalRows;
        var enableAddRow = isLastPage || pagingInfo.pageSize==0;
        var options = self.grid.getOptions();

        if (options.enableAddRow != enableAddRow)
          self.grid.setOptions({enableAddRow:enableAddRow});
      });

      this.grid.onColumnsReordered.subscribe(function(e,args) {
        var columns = _(this.getColumns()).pluck('id');
        self.trigger('columnsReordered', columns);
      });
      
      if (this.selector) {
        var selected = undefined;
        this.grid.onMouseEnter.subscribe(function(e,args) {
          selected = self.grid.getCellFromEvent(e).row;
          self.selector.select(selected);
        });
        this.grid.onMouseLeave.subscribe(function(e,args) {
          selected = undefined;
          setTimeout(function() {
            if (typeof selected == "undefined") {
              self.selector.deselect(); 
            }
          }, 40);
        });
      }
    },
    update: function() {
    	
      var self = this;
      var data = _(this.model.get('filtered')).map(function(obj) {
        obj.id = self.counter++;
        for (var k in obj){
        	if (obj[k] == null){
        		obj[k] = "N/A";
        	}
        }
        return obj;
      });
      this.dataView.beginUpdate();
      this.dataView.setItems(data);
      this.dataView.endUpdate();
    },
    clearfilters: function() {
        var self = this;
        var filtered = _(this.model.get('data')).map(function(obj) {
          obj.id = self.counter++;
          for (var k in obj){
          	if (obj[k] == null){
          		obj[k] = "N/A";
          	}
          }
          return obj;
        });
        this.dataView.beginUpdate();
        this.dataView.setItems(filtered);
        this.dataView.endUpdate();
      }
  });

})();
