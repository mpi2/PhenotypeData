<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
Baseline chart should be here
<div id="baseline-chart-div"></div>
 <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.1/jquery-ui.min.js"></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        	<script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
    
          
<script type="text/javascript">

$(function () {

   ${baselineChart}

});
</script>