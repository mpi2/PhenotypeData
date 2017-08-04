var anatomogram=webpackJsonp_name_([1],{25:/*!***********************!*\
  !*** ./src/Assets.js ***!
  \***********************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0}),t.getDefaultView=t.getAnatomogramViews=void 0;var r=n(/*! ./json/svgsMetadata.json */36),i=function(e){return e&&e.__esModule?e:{default:e}}(r),o=function(e,t,n){return n.indexOf(e)===t},s=function(e){return"string"==typeof e&&""!==e},u=i.default.map(function(e){return e.species}).filter(o).reduce(function(e,t){return e[t]=i.default.filter(function(e){return e.species===t}).map(function(e){return e.view}).filter(s).sort().reverse(),e},{}),a=function(e){var t="string"==typeof e?e.trim().toLowerCase().replace(/ +/,"_"):"";return u[t]||[]},c=function(e){return a(e)[0]};t.getAnatomogramViews=a,t.getDefaultView=c},26:/*!*************************************************!*\
  !*** ./node_modules/css-loader/lib/css-base.js ***!
  \*************************************************/
/*! no static exports found */
/*! all exports used */
function(e,t){function n(e,t){var n=e[1]||"",i=e[3];if(!i)return n;if(t&&"function"==typeof btoa){var o=r(i);return[n].concat(i.sources.map(function(e){return"/*# sourceURL="+i.sourceRoot+e+" */"})).concat([o]).join("\n")}return[n].join("\n")}function r(e){return"/*# sourceMappingURL=data:application/json;charset=utf-8;base64,"+btoa(unescape(encodeURIComponent(JSON.stringify(e))))+" */"}e.exports=function(e){var t=[];return t.toString=function(){return this.map(function(t){var r=n(t,e);return t[2]?"@media "+t[2]+"{"+r+"}":r}).join("")},t.i=function(e,n){"string"==typeof e&&(e=[[null,e,""]]);for(var r={},i=0;i<this.length;i++){var o=this[i][0];"number"==typeof o&&(r[o]=!0)}for(i=0;i<e.length;i++){var s=e[i];"number"==typeof s[0]&&r[s[0]]||(n&&!s[2]?s[2]=n:n&&(s[2]="("+s[2]+") and ("+n+")"),t.push(s))}},t}},27:/*!****************************************************!*\
  !*** ./node_modules/style-loader/lib/addStyles.js ***!
  \****************************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){function r(e,t){for(var n=0;n<e.length;n++){var r=e[n],i=O[r.id];if(i){i.refs++;for(var o=0;o<i.parts.length;o++)i.parts[o](r.parts[o]);for(;o<r.parts.length;o++)i.parts.push(f(r.parts[o],t))}else{for(var s=[],o=0;o<r.parts.length;o++)s.push(f(r.parts[o],t));O[r.id]={id:r.id,refs:1,parts:s}}}}function i(e,t){for(var n=[],r={},i=0;i<e.length;i++){var o=e[i],s=t.base?o[0]+t.base:o[0],u=o[1],a=o[2],c=o[3],f={css:u,media:a,sourceMap:c};r[s]?r[s].parts.push(f):n.push(r[s]={id:s,parts:[f]})}return n}function o(e,t){var n=_(e.insertInto);if(!n)throw new Error("Couldn't find a style target. This probably means that the value for the 'insertInto' parameter is invalid.");var r=E[E.length-1];if("top"===e.insertAt)r?r.nextSibling?n.insertBefore(t,r.nextSibling):n.appendChild(t):n.insertBefore(t,n.firstChild),E.push(t);else{if("bottom"!==e.insertAt)throw new Error("Invalid value for parameter 'insertAt'. Must be 'top' or 'bottom'.");n.appendChild(t)}}function s(e){if(null===e.parentNode)return!1;e.parentNode.removeChild(e);var t=E.indexOf(e);t>=0&&E.splice(t,1)}function u(e){var t=document.createElement("style");return e.attrs.type="text/css",c(t,e.attrs),o(e,t),t}function a(e){var t=document.createElement("link");return e.attrs.type="text/css",e.attrs.rel="stylesheet",c(t,e.attrs),o(e,t),t}function c(e,t){Object.keys(t).forEach(function(n){e.setAttribute(n,t[n])})}function f(e,t){var n,r,i,o;if(t.transform&&e.css){if(!(o=t.transform(e.css)))return function(){};e.css=o}if(t.singleton){var c=R++;n=g||(g=u(t)),r=l.bind(null,n,c,!1),i=l.bind(null,n,c,!0)}else e.sourceMap&&"function"==typeof URL&&"function"==typeof URL.createObjectURL&&"function"==typeof URL.revokeObjectURL&&"function"==typeof Blob&&"function"==typeof btoa?(n=a(t),r=p.bind(null,n,t),i=function(){s(n),n.href&&URL.revokeObjectURL(n.href)}):(n=u(t),r=d.bind(null,n),i=function(){s(n)});return r(e),function(t){if(t){if(t.css===e.css&&t.media===e.media&&t.sourceMap===e.sourceMap)return;r(e=t)}else i()}}function l(e,t,n,r){var i=n?"":r.css;if(e.styleSheet)e.styleSheet.cssText=v(t,i);else{var o=document.createTextNode(i),s=e.childNodes;s[t]&&e.removeChild(s[t]),s.length?e.insertBefore(o,s[t]):e.appendChild(o)}}function d(e,t){var n=t.css,r=t.media;if(r&&e.setAttribute("media",r),e.styleSheet)e.styleSheet.cssText=n;else{for(;e.firstChild;)e.removeChild(e.firstChild);e.appendChild(document.createTextNode(n))}}function p(e,t,n){var r=n.css,i=n.sourceMap,o=void 0===t.convertToAbsoluteUrls&&i;(t.convertToAbsoluteUrls||o)&&(r=m(r)),i&&(r+="\n/*# sourceMappingURL=data:application/json;base64,"+btoa(unescape(encodeURIComponent(JSON.stringify(i))))+" */");var s=new Blob([r],{type:"text/css"}),u=e.href;e.href=URL.createObjectURL(s),u&&URL.revokeObjectURL(u)}var O={},h=function(e){var t;return function(){return void 0===t&&(t=e.apply(this,arguments)),t}}(function(){return window&&document&&document.all&&!window.atob}),_=function(e){var t={};return function(n){return void 0===t[n]&&(t[n]=e.call(this,n)),t[n]}}(function(e){return document.querySelector(e)}),g=null,R=0,E=[],m=n(/*! ./urls */62);e.exports=function(e,t){if("undefined"!=typeof DEBUG&&DEBUG&&"object"!=typeof document)throw new Error("The style-loader cannot be used in a non-browser environment");t=t||{},t.attrs="object"==typeof t.attrs?t.attrs:{},t.singleton||(t.singleton=h()),t.insertInto||(t.insertInto="head"),t.insertAt||(t.insertAt="bottom");var n=i(e,t);return r(n,t),function(e){for(var o=[],s=0;s<n.length;s++){var u=n[s],a=O[u.id];a.refs--,o.push(a)}if(e){r(i(e,t),t)}for(var s=0;s<o.length;s++){var a=o[s];if(0===a.refs){for(var c=0;c<a.parts.length;c++)a.parts[c]();delete O[a.id]}}}};var v=function(){var e=[];return function(t,n){return e[t]=n,e.filter(Boolean).join("\n")}}()},36:/*!************************************!*\
  !*** ./src/json/svgsMetadata.json ***!
  \************************************/
/*! no static exports found */
/*! all exports used */
function(e,t){e.exports=[{filename:"mus_musculus.brain.svg",species:"mus_musculus",view:"brain",ids:["UBERON_0001896","UBERON_0000956","UBERON_0000369","UBERON_0001894","UBERON_0000007","UBERON_0002037","UBERON_0002298","UBERON_0001891","UBERON_0001897","UBERON_0001898","UBERON_0000004","UBERON_0002259","EFO_0000530"]},{filename:"mus_musculus.female.svg",species:"mus_musculus",view:"female",ids:["UBERON_0000947","UBERON_0001009","UBERON_0001348","UBERON_0001347","UBERON_0000945","UBERON_0002114","UBERON_0001264","UBERON_0002106","UBERON_0002369","UBERON_0002113","UBERON_0001155","UBERON_0002108","UBERON_0001153","UBERON_0002115","UBERON_0002116","UBERON_0001043","UBERON_0002110","UBERON_0000996","UBERON_0000995","UBERON_0001255","UBERON_0001831","UBERON_0001736","UBERON_0001723","UBERON_0001211","UBERON_0000981","UBERON_0002371","UBERON_0007844","UBERON_0001377","UBERON_0014892","UBERON_0002240","UBERON_0001103","UBERON_0002103","UBERON_0001645","UBERON_0000970","UBERON_0001242","UBERON_0000955","UBERON_0000948","UBERON_0002107","UBERON_0001322","UBERON_0001981","UBERON_0000014","UBERON_0001911","UBERON_0003134","UBERON_0000990","UBERON_0000029","UBERON_0001132","UBERON_0002370","UBERON_0002046","UBERON_0002048","UBERON_0000010","UBERON_0003126"]},{filename:"mus_musculus.male.svg",species:"mus_musculus",view:"male",ids:["UBERON_0000947","UBERON_0001348","UBERON_0001347","UBERON_0000945","UBERON_0002114","UBERON_0001264","UBERON_0002106","UBERON_0002369","UBERON_0002113","UBERON_0001155","UBERON_0002108","UBERON_0001153","UBERON_0002115","UBERON_0002116","UBERON_0001043","UBERON_0002110","UBERON_0001831","UBERON_0001736","UBERON_0000029","UBERON_0000998","UBERON_0000989","UBERON_0000981","UBERON_0002371","UBERON_0007844","UBERON_0001377","UBERON_0002240","UBERON_0002048","UBERON_0001103","UBERON_0003126","UBERON_0002103","UBERON_0001645","UBERON_0001322","UBERON_0001242","UBERON_0002107","UBERON_0000948","UBERON_0000955","UBERON_0014892","UBERON_0001009","UBERON_0001981","UBERON_0000014","UBERON_0001132","UBERON_0001211","UBERON_0002367","UBERON_0001000","UBERON_0001301","UBERON_0000473","UBERON_0001255","UBERON_0002370","UBERON_0000010","UBERON_0000970","UBERON_0001723"]}]},57:/*!**********************!*\
  !*** ./src/index.js ***!
  \**********************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){"use strict";function r(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0}),t.render=t.default=void 0;var i=n(/*! react */7),o=r(i),s=n(/*! react-dom */30),u=r(s),a=n(/*! ./Anatomogram */58),c=r(a),f=function(e,t){u.default.render(o.default.createElement(c.default,e),document.getElementById(t))};t.default=c.default,t.render=f},58:/*!****************************!*\
  !*** ./src/Anatomogram.js ***!
  \****************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){"use strict";function r(e){return e&&e.__esModule?e:{default:e}}function i(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function o(e,t){if(!e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!t||"object"!=typeof t&&"function"!=typeof t?e:t}function s(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function, not "+typeof t);e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,enumerable:!1,writable:!0,configurable:!0}}),t&&(Object.setPrototypeOf?Object.setPrototypeOf(e,t):e.__proto__=t)}Object.defineProperty(t,"__esModule",{value:!0});var u=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e},a=function(){function e(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}return function(t,n,r){return n&&e(t.prototype,n),r&&e(t,r),t}}(),c=n(/*! react */7),f=r(c),l=n(/*! prop-types */12),d=r(l),p=n(/*! ./Switcher */59),O=r(p),h=n(/*! ./AnatomogramSvg */70),_=r(h),g=n(/*! ./Assets */25),R=function(e){function t(e){i(this,t);var n=o(this,(t.__proto__||Object.getPrototypeOf(t)).call(this,e));return n.state={selectedView:(0,g.getDefaultView)(e.species)},n._switchAnatomogramView=n._switchAnatomogramView.bind(n),n}return s(t,e),a(t,[{key:"_switchAnatomogramView",value:function(e){this.setState({selectedView:e})}},{key:"componentWillReceiveProps",value:function(e){e.species!==this.props.species&&this.setState({selectedView:(0,g.getDefaultView)(e.species)})}},{key:"render",value:function(){return f.default.createElement("div",null,f.default.createElement(O.default,{species:this.props.species,selectedView:this.state.selectedView,onChangeView:this._switchAnatomogramView}),f.default.createElement(_.default,u({},this.props,{selectedView:this.state.selectedView})))}}]),t}(f.default.Component);R.propTypes={species:d.default.string.isRequired,showIds:d.default.arrayOf(d.default.string),highlightIds:d.default.arrayOf(d.default.string),selectIds:d.default.arrayOf(d.default.string),showColour:d.default.string,highlightColour:d.default.string,selectColour:d.default.string,onMouseOver:d.default.func,onMouseOut:d.default.func,onClick:d.default.func},R.defaultProps={species:"mus_musculus",showIds:[],highlightIds:[],selectIds:[],showColour:"grey",highlightColour:"red",selectColour:"purple",showOpacity:.4,highlightOpacity:.4,selectOpacity:.4,onMouseOver:function(){},onMouseOut:function(){},onClick:function(){}},t.default=R},59:/*!*************************!*\
  !*** ./src/Switcher.js ***!
  \*************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){"use strict";function r(e){return e&&e.__esModule?e:{default:e}}Object.defineProperty(t,"__esModule",{value:!0});var i=n(/*! react */7),o=r(i),s=n(/*! prop-types */12),u=r(s),a=n(/*! ./Assets */25);n(/*! ./Switcher.css */60);var c=function(e,t){/*! ./img */
return n(63)("./"+e+"."+(e===t?"":"un")+"selected.png")},f=function(e){var t=e.species,n=e.selectedView,r=e.onChangeView;return o.default.createElement("div",{className:"gxa-anatomogram-switcher"},(0,a.getAnatomogramViews)(t).map(function(e){return o.default.createElement("img",{key:e,className:"gxa-anatomogram-switcher-icon",onClick:function(){return r(e)},src:c(e,n)})}))};f.propTypes={species:u.default.string.isRequired,selectedView:u.default.string,onChangeView:u.default.func.isRequired},t.default=f},60:/*!**************************!*\
  !*** ./src/Switcher.css ***!
  \**************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){var r=n(/*! !../node_modules/css-loader!./Switcher.css */61);"string"==typeof r&&(r=[[e.i,r,""]]);var i={};i.transform=void 0;n(/*! ../node_modules/style-loader/lib/addStyles.js */27)(r,i);r.locals&&(e.exports=r.locals)},61:/*!****************************************************!*\
  !*** ./node_modules/css-loader!./src/Switcher.css ***!
  \****************************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){t=e.exports=n(/*! ../node_modules/css-loader/lib/css-base.js */26)(void 0),t.push([e.i,".gxa-anatomogram-switcher{display:inline-block;vertical-align:top;width:10%;max-width:44px}.gxa-anatomogram-switcher-icon{border:1px solid #ccc;border-radius:4px;width:100%;height:auto;padding:2px}.gxa-anatomogram-switcher-icon:hover{border:1px solid orange;background:#fafad2;cursor:pointer}",""])},62:/*!***********************************************!*\
  !*** ./node_modules/style-loader/lib/urls.js ***!
  \***********************************************/
/*! no static exports found */
/*! all exports used */
function(e,t){e.exports=function(e){var t="undefined"!=typeof window&&window.location;if(!t)throw new Error("fixUrls requires window.location");if(!e||"string"!=typeof e)return e;var n=t.protocol+"//"+t.host,r=n+t.pathname.replace(/\/[^\/]*$/,"/");return e.replace(/url\s*\(((?:[^)(]|\((?:[^)(]+|\([^)(]*\))*\))*)\)/gi,function(e,t){var i=t.trim().replace(/^"(.*)"$/,function(e,t){return t}).replace(/^'(.*)'$/,function(e,t){return t});if(/^(#|data:|http:\/\/|https:\/\/|file:\/\/\/)/i.test(i))return e;var o;return o=0===i.indexOf("//")?i:0===i.indexOf("/")?n+i:r+i.replace(/^\.\//,""),"url("+JSON.stringify(o)+")"})}},63:/*!***************************************!*\
  !*** ./src/img ^\.\/.*selected\.png$ ***!
  \***************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){function r(e){return n(i(e))}function i(e){var t=o[e];if(!(t+1))throw new Error("Cannot find module '"+e+"'.");return t}var o={"./brain.selected.png":64,"./brain.unselected.png":65,"./female.selected.png":66,"./female.unselected.png":67,"./male.selected.png":68,"./male.unselected.png":69};r.keys=function(){return Object.keys(o)},r.resolve=i,e.exports=r,r.id=63},64:/*!************************************!*\
  !*** ./src/img/brain.selected.png ***!
  \************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"cbe297d1ea7bf5aac3cfcd540c8be570.png"},65:/*!**************************************!*\
  !*** ./src/img/brain.unselected.png ***!
  \**************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"bc6cb140072af5b41e6dc150228f8735.png"},66:/*!*************************************!*\
  !*** ./src/img/female.selected.png ***!
  \*************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"a9cbc6e400cd961706ef5e528563fe6d.png"},67:/*!***************************************!*\
  !*** ./src/img/female.unselected.png ***!
  \***************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"7f876a7270e2c70bc575970c544e758b.png"},68:/*!***********************************!*\
  !*** ./src/img/male.selected.png ***!
  \***********************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"ef28de891e5f4ac45b23ffbb6e6e2194.png"},69:/*!*************************************!*\
  !*** ./src/img/male.unselected.png ***!
  \*************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"e489bf5dc0b662fd5516779442d4c046.png"},70:/*!*******************************!*\
  !*** ./src/AnatomogramSvg.js ***!
  \*******************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){"use strict";function r(e){return e&&e.__esModule?e:{default:e}}function i(e){if(Array.isArray(e)){for(var t=0,n=Array(e.length);t<e.length;t++)n[t]=e[t];return n}return Array.from(e)}Object.defineProperty(t,"__esModule",{value:!0});var o=n(/*! react */7),s=r(o),u=n(/*! prop-types */12),a=r(u),c=n(/*! react-svg */71),f=r(c);n(/*! ./AnatomogramSvg.css */72);var l=function(e,t){return Array.isArray(e)&&Array.isArray(t)?e.filter(function(e){return!t.includes(e)}):e},d=function(e){function t(e){if(n)for(var r=0;r<n.children.length;r++)if(n.children[r].id===e)return n.children[r].attributes["xlink:href"]?t(n.children[r].attributes["xlink:href"].value.substring(1)):n.children[r]}var n=function(e){for(var t=e.getElementsByTagName("g"),n=0;n<t.length;n++)if("LAYER_EFO"===t[n].id)return t[n]}(e);return t},p=function(e,t,n,r){e.forEach(function(e){var i=r(e);i&&(i.style.fill=t,i.style.opacity=n)})},O=function(e,t,n,r,i,o){e.forEach(function(e){var s=o(e);if(s){s.addEventListener("mouseover",function(){s.style.fill=t,s.style.opacity=n,r(e)});var u=s.style.fill,a=s.style.opacity;s.addEventListener("mouseout",function(){s.style.fill=u,s.style.opacity=a,i(e)})}})},h=function(e,t,n,r){e.forEach(function(e){var i=r(e);i&&i.addEventListener(t,function(){n(e)})})},_=function(e,t){var n=t.showIds,r=t.showColour,o=t.showOpacity,s=t.highlightIds,u=t.highlightColour,a=t.highlightOpacity,c=t.selectIds,f=t.selectColour,d=t.selectOpacity,_=t.onMouseOver,g=t.onMouseOut,R=t.onClick,E=l(n,[].concat(i(s),i(c))),m=l(s,c);p(E,r,o,e),p(m,u,a,e),p(c,f,d,e),O(E,u,a,_,g,e),O(m,u,a+.2,_,g,e),O(c,f,d+.2,_,g,e),h([].concat(i(E),i(m),i(c)),"click",R,e)},g=function(e,t){/*! ./svg */
return n(74)("./"+e+(t?"."+t:"")+".svg")},R=function(e){return s.default.createElement("div",{className:"gxa-anatomogram-svg-wrapper"},s.default.createElement(f.default,{path:g(e.species,e.selectedView),callback:function(t){_(d(t),e)},className:"gxa-anatomogram-svg",style:{paddingLeft:e.selectedView?"10px":""}}))};R.propTypes={species:a.default.string.isRequired,selectedView:a.default.string,showIds:a.default.arrayOf(a.default.string).isRequired,highlightIds:a.default.arrayOf(a.default.string).isRequired,selectIds:a.default.arrayOf(a.default.string).isRequired,showColour:a.default.string.isRequired,highlightColour:a.default.string.isRequired,selectColour:a.default.string.isRequired,showOpacity:a.default.number.isRequired,highlightOpacity:a.default.number.isRequired,selectOpacity:a.default.number.isRequired,onMouseOver:a.default.func.isRequired,onMouseOut:a.default.func.isRequired,onClick:a.default.func.isRequired},t.default=R},72:/*!********************************!*\
  !*** ./src/AnatomogramSvg.css ***!
  \********************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){var r=n(/*! !../node_modules/css-loader!./AnatomogramSvg.css */73);"string"==typeof r&&(r=[[e.i,r,""]]);var i={};i.transform=void 0;n(/*! ../node_modules/style-loader/lib/addStyles.js */27)(r,i);r.locals&&(e.exports=r.locals)},73:/*!**********************************************************!*\
  !*** ./node_modules/css-loader!./src/AnatomogramSvg.css ***!
  \**********************************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){t=e.exports=n(/*! ../node_modules/css-loader/lib/css-base.js */26)(void 0),t.push([e.i,".gxa-anatomogram-svg-wrapper{display:inline-block;vertical-align:top;width:90%}.gxa-anatomogram-svg{width:100%;height:auto}",""])},74:/*!*******************************!*\
  !*** ./src/svg ^\.\/.*\.svg$ ***!
  \*******************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){function r(e){return n(i(e))}function i(e){var t=o[e];if(!(t+1))throw new Error("Cannot find module '"+e+"'.");return t}var o={"./mus_musculus.brain.svg":75,"./mus_musculus.female.svg":76,"./mus_musculus.male.svg":77};r.keys=function(){return Object.keys(o)},r.resolve=i,e.exports=r,r.id=74},75:/*!****************************************!*\
  !*** ./src/svg/mus_musculus.brain.svg ***!
  \****************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"bbbcce33c7259be34c77037843606255.svg"},76:/*!*****************************************!*\
  !*** ./src/svg/mus_musculus.female.svg ***!
  \*****************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"f950766239d4c1005b43ec60a19f212a.svg"},77:/*!***************************************!*\
  !*** ./src/svg/mus_musculus.male.svg ***!
  \***************************************/
/*! no static exports found */
/*! all exports used */
function(e,t,n){e.exports=n.p+"236878cf36edd206f852c1c86a98ec2c.svg"}},[57]);