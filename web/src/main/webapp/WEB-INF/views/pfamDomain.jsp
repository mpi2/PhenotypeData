<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
		
	
		<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.js"></script>
		
		<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/prototype/1.7.0/prototype.js"></script>
	
		<!-- prototip -->
		<script type='text/javascript' src='http://pfam.xfam.org/shared/javascripts/prototip.js'></script>
		<link rel='stylesheet' href='http://pfam.xfam.org/shared/css/prototip.css' type='text/css' />
		
		<!-- required javascripts -->
		<script type='text/javascript' src='http://pfam.xfam.org/static/javascripts/excanvas.js'></script>
		<script type='text/javascript' src='http://pfam.xfam.org/static/javascripts/domain_graphics.js'></script>
		
		<!-- javascripts applicable to IE -->
		<!--[if IE]>
		<script type='text/javascript' src='http://pfam.xfam.org/static/javascripts/canvas.text.js?reimplement=true&amp;dontUseMoz=true'></script>
		<script type='text/javascript' src='http://pfam.xfam.org/static/javascripts/faces/optimer-bold-normal.js'></script>
		<![endif]-->
		
		<!-- styles applicable to all browsers -->
		<link rel='stylesheet' href='http://pfam.xfam.org/static/css/graphicTools.css' type='text/css' />
					
		<script type="text/javascript">
			  // <![CDATA[
		
			var generator;
			
			jQuery(document).ready(function() {
				
			    this._pg = new PfamGraphic();
			    // get ride of the "no graphic yet" message
			    if ( $("none") ) {
			      $("none").remove();
			    }
	
			    // hide any previous error messages and remove the previous canvas element
			    if ( $("dg").select("canvas").size() > 0 ) {
			      $("dg").select("canvas").first().remove();
			    }
	
			    // see if we can turn the sequence string into an object
			    var sequence;
			    try {
			      eval( "sequence = " + '${pfamJson}' );
			    } catch ( e ) {
				      console.log("Error in pfamDomain.js " + e );
			          return;
			    }
	
			    // set up the PfamGraphic object
			    this._pg.setParent( "dg" );
	
			    this._pg.setImageParams( {
			      xscale: 1.0,
			      yscale: 1.0
			    } );
	
			    // render the sequence
			    try {
			      this._pg.setSequence( sequence );
			      this._pg.render();
			    } catch ( e ) {
			      console.log("Error in pfamDomain.js " + e );
			      return;
			    }
		    					  
			})
					
		 // ]]>
		</script>
                    
		<div id="dg" >
			<span id="none">No graphic yet</span>
		</div>
	
		
				