
//------------------------------------------------------------------------------
//- preamble -------------------------------------------------------------------
//------------------------------------------------------------------------------

// for the benefit of jslint, declare global variables from outside this script
/*global $, $R, $w, $break, Class, console, Element, Hash, Event, document,
  window, G_vmlCanvasManager, Template, Tip */

// spoof a console, if necessary, so that we can run in IE (<8) without having
// to entirely disable debug messages
if ( ! window.console ) {
  window.console     = {};
  window.console.log = function() {};
}  

//------------------------------------------------------------------------------
//- class ----------------------------------------------------------------------
//------------------------------------------------------------------------------

// A javascript object to control the drawing of domain graphics on the 
// domain graphics generator page.
//
// jt6 20110303 WTSI
//
// $Id$
//
// Copyright (c) 2011: Genome Research Ltd.
// 
// Authors: Rob Finn (rdf@sanger.ac.uk), John Tate (jt6@sanger.ac.uk)
// 
// This is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version.
//
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You should have received a copy of the GNU General Public License along with
// this program. If not, see <http://www.gnu.org/licenses/>.

var GraphicGenerator = Class.create( {
  
  //----------------------------------------------------------------------------
  //- class variables ----------------------------------------------------------
  //----------------------------------------------------------------------------

  // these are example sequence strings
  _smallExample: '{ \r\
  "length" : "534",  \r\
  "regions" : [  \r\
    {  \r\
      "type" : "pfama",  \r\
      "text" : "Peptidase_S8",  \r\
      "colour" : "#2dcfff",  \r\
      "display": "true", \r\
      "startStyle" : "curved", \r\
      "endStyle" : "curved", \r\
      "start" : "159", \r\
      "end" : "361",  \r\
      "aliEnd" : "350",  \r\
      "aliStart" : "163"\r\
    }, \r\
    { \r\
      "type" : "pfama", \r\
      "text" : "PA", \r\
      "colour" : "#ff5353", \r\
      "display" : true, \r\
      "startStyle" : "jagged", \r\
      "endStyle" : "curved", \r\
      "start" : "388",\r\
      "end" : "469", \r\
      "aliEnd" : "469", \r\
      "aliStart" : "396"\r\
    } \r\
  ] \r\
}',

  //----------------------------------------------------------------------------
  //- constructor --------------------------------------------------------------
  //----------------------------------------------------------------------------

  initialize: function() {

    this._pg = new PfamGraphic();

    // set up sequence field
    $("seq").focus();
    $("seq").select();

    // add listeners to the various buttons
    $("large").observe( "click", function() {
      $("seq").value = this._largeExample;
    }.bind(this) );
    $("small").observe( "click", function() {
      $("seq").value = this._smallExample;
    }.bind(this) );
    $("submit").observe( "click", this.generate.bind(this) );
    $("clear").observe( "click", this.clear.bind(this) );
  },

  //----------------------------------------------------------------------------
  //- methods ------------------------------------------------------------------
  //----------------------------------------------------------------------------
  
  generate: function() {

    // get ride of the "no graphic yet" message
    if ( $("none") ) {
      $("none").remove();
    }

    // hide any previous error messages and remove the previous canvas element
    $("errors").hide();
    if ( $("dg").select("canvas").size() > 0 ) {
      $("dg").select("canvas").first().remove();
    }

    // see if we can turn the sequence string into an object
    var sequence;
    try {
      eval( "sequence = " + $("seq").getValue() );
    } catch ( e ) {
      $("error").update( e );
      $("errors").show();
      return;
    }

    // set up the PfamGraphic object
    this._pg.setParent( "dg" );

    this._pg.setImageParams( {
      xscale: $F("xscale"),
      yscale: $F("yscale")
    } );

    // render the sequence
    try {
      this._pg.setSequence( sequence );
      this._pg.render();
    } catch ( e ) {
      $("error").update( e );
      $("errors").show();
      return;
    }

  },

} );
